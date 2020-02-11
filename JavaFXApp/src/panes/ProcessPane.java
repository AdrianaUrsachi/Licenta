package panes;

import java.io.File;

import algorithm.Processor;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
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
import panes.helpers.PaneType;

@SuppressWarnings("restriction")
public class ProcessPane extends VBox {

	private VBox content;
	private HBox controls;
	private HBox imagePane;
	private HBox resultPane;
	private ImageView selectedImage;
	private File selectedImageFile;
	private App app;
	private Button processButton;
	private ProgressIndicator progressIndicator;

	public ProcessPane(App app) {
		this.app = app;
		progressIndicator = new ProgressIndicator();

		content = new VBox();
		content.setPadding(new Insets(50, 50, 50, 50));
		content.setSpacing(20);
		content.setPrefHeight(750);
		content.setAlignment(Pos.TOP_CENTER);
		imagePane = new HBox();
		imagePane.setAlignment(Pos.TOP_CENTER);
		resultPane = new HBox();
		resultPane.setAlignment(Pos.CENTER);
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
		processButton = new Button("Process photo");
		processButton.setOnAction((event) -> process());

		controls.getChildren().addAll(selectPhotoButton);
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
		selectedImage.setFitWidth(450);
		VBox box = new VBox();
		box.setStyle("-fx-border-color: black");
		box.getChildren().add(selectedImage);
		imagePane.getChildren().add(box);
		resultPane.getChildren().add(processButton);
	}

	public void process() {
		resultPane.getChildren().clear();
		resultPane.getChildren().add(progressIndicator);

		new Thread(() -> {
			String response = (String) Processor.process(selectedImageFile.toString())[0];
			Platform.runLater(new Thread(() -> {
				resultPane.getChildren().clear();
				File directory = new File("../Photos/" + response.trim());

				if (!response.equals("")) {
					Label label = new Label("The result is: " + response.replace("_", " "));
					Button viewPhotos = new Button("View Photos");

					viewPhotos.setOnAction((event) -> {
						this.app.getBrowsePane().loadPhotos(directory, PaneType.Process);
						this.app.changeToBrowseScene();
					});

					this.resultPane.getChildren().addAll(label, viewPhotos);
				} else {
					Label label = new Label("No result");

					this.resultPane.getChildren().addAll(label);
				}
			}));
		}).start();
	}
}
