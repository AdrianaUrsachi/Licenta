package panes;

import java.io.File;

import algorithm.Processor;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import main.App;

@SuppressWarnings("restriction")
public class ProcessPane extends VBox {

	private VBox content;
	private HBox controls;
	private HBox imagePane;
	private HBox resultPane;
	private ImageView selectedImage;
	private File selectedImageFile;
	private App app;
	private Processor processor;

	public ProcessPane(App app) {
		this.app = app;
		processor = new Processor();

		content = new VBox();
		content.setPadding(new Insets(50, 50, 50, 50));
		content.setSpacing(20);
		content.setPrefHeight(750);
		content.setAlignment(Pos.TOP_CENTER);
		imagePane = new HBox();
		imagePane.setAlignment(Pos.TOP_CENTER);
		resultPane = new HBox();
		resultPane.setAlignment(Pos.TOP_CENTER);
		resultPane.setSpacing(10);
		content.getChildren().addAll(imagePane, resultPane);

		controls = new HBox();
		controls.setPadding(new Insets(20, 20, 20, 20));
		controls.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		controls.setAlignment(Pos.CENTER_LEFT);
		controls.setMaxHeight(50);
		controls.setMinHeight(50);
		controls.setSpacing(5);

		Button selectPhotoButton = new Button("Select Photo");
		selectPhotoButton.setOnAction((event) -> selectPhoto());
		Button processButton = new Button("Process photo");
		processButton.setOnAction((event) -> process());

		controls.getChildren().addAll(selectPhotoButton, processButton);
		this.getChildren().addAll(controls, content);
	}

	public void selectPhoto() {
		imagePane.getChildren().clear();
		resultPane.getChildren().clear();
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Files", "*.jpg", "*.png", "*.jpeg", "*.bmp"));
		selectedImageFile = chooser.showOpenDialog(null);

		selectedImage = new ImageView(new Image(selectedImageFile.toURI().toString()));
		selectedImage.setFitHeight(450);

		imagePane.getChildren().add(selectedImage);
	}

	public void process() {
		resultPane.getChildren().clear();

		new Thread(() -> {
			String response = processor.process(selectedImageFile.toString());
			Platform.runLater(new Thread(() -> {
				File directory = new File("../Photos/" + response.trim());
				Label label = new Label("The result is: " + response.replace("_", " "));
				Button viewPhotos = new Button("View Photos");

				viewPhotos.setOnAction((event) -> {
					this.app.getBrowsePane().loadPhotos(directory);
					this.app.changeToBrowseScene();
				});

				this.resultPane.getChildren().addAll(label, viewPhotos);
			}));
		}).start();
	}
}
