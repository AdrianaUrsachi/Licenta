package panes;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.Tooltip;
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
import panes.helpers.FolderThumbnail;
import panes.helpers.ImageThumbnail;
import panes.helpers.PaneType;

@SuppressWarnings("restriction")
public class BrowsePane extends VBox {

	private VBox content;
	private HBox controls;
	private HBox footer;
	private HBox directoryControls;
	private HBox photosControls;
	private File currentDirectory;
	private int page = 1, totalItems = 0;
	private PaneType returnPaneType;
	private App app;
	private ProgressBar progressBar;

	public BrowsePane(App app) {
		this.app = app;
		content = new VBox();
		content.setSpacing(25);
		content.setPrefHeight(570);
		content.setPadding(new Insets(20, 20, 20, 20));

		controls = new HBox();
		controls.setPadding(new Insets(20, 20, 20, 20));
		controls.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		controls.setAlignment(Pos.CENTER_LEFT);
		controls.setMaxHeight(50);
		controls.setMinHeight(50);

		directoryControls = new HBox();
		directoryControls.setSpacing(5);
		Button addDirectoryButton = new Button("New Collection");
		addDirectoryButton.setOnAction((event) -> addDirectory());
		TextField searchInput = new TextField();
		Button searchButton = new Button("Search");
		searchButton.setOnAction((event) -> {
			page = 1;
			loadDirectories(searchInput.getText());
		});
		HBox box = new HBox();
		box.setPadding(new Insets(0, 50, 0, 100));
		progressBar = new ProgressBar(0);
		progressBar.setMinSize(200, 27);
		box.getChildren().add(progressBar);
		directoryControls.getChildren().addAll(addDirectoryButton, searchInput, searchButton, box);

		photosControls = new HBox();
		photosControls.setSpacing(5);
		Button goBackButton = new Button("Go Back");
		goBackButton.setOnAction((event) -> goBack());
		Button addPhotoButton = new Button("Add new Photo");
		addPhotoButton.setOnAction((event) -> addPhoto());
		photosControls.getChildren().addAll(goBackButton, addPhotoButton);

		footer = new HBox();
		footer.setPadding(new Insets(20, 20, 20, 20));
		footer.setAlignment(Pos.CENTER);
		footer.setMaxHeight(50);
		footer.setMinHeight(50);

		Button nextPageButton = new Button("");
		nextPageButton.setOnAction((event) -> incrementPage());
		ImageView nextImage = new ImageView(new Image(getClass().getResourceAsStream("/resources/next.png")));
		nextImage.setFitHeight(25);
		nextImage.setFitWidth(25);
		nextPageButton.setTooltip(new Tooltip("Next page"));
		nextPageButton.setGraphic(nextImage);

		Button previousPageButton = new Button("");
		previousPageButton.setOnAction((event) -> decrementPage());
		ImageView previousImage = new ImageView(new Image(getClass().getResourceAsStream("/resources/previous.png")));
		previousImage.setFitHeight(25);
		previousImage.setFitWidth(25);
		previousPageButton.setTooltip(new Tooltip("Next page"));
		previousPageButton.setGraphic(previousImage);

		footer.getChildren().addAll(previousPageButton, nextPageButton);

		this.getChildren().addAll(controls, new ScrollPane(content), footer);
		loadDirectories();
	}

	public void loadPhotos(File directory, PaneType type) {
		returnPaneType = type;
		footer.setVisible(false);
		this.currentDirectory = directory;
		content.getChildren().clear();
		HBox photosLine = new HBox();
		photosLine.setSpacing(35);

		for (final File fileEntry : directory.listFiles()) {
			if (fileEntry.getName().toLowerCase().contains("-mat")) {
				continue;
			}
			if (photosLine.getChildren().size() == 3) {
				this.content.getChildren().add(photosLine);
				photosLine = new HBox();
				photosLine.setSpacing(35);
			}
			photosLine.getChildren().add(new ImageThumbnail(directory, fileEntry.getAbsolutePath(), this));
		}
		if (photosLine.getChildren().size() > 0)
			this.content.getChildren().add(photosLine);

		this.controls.getChildren().clear();
		controls.getChildren().add(photosControls);
	}

	public void loadDirectories() {
		loadDirectories("");
	}

	public void setProgress(double progress) {
		if (progress >= 1) {
			this.progressBar.setVisible(false);
			this.app.enableProcess();
		} else {
			this.progressBar.setProgress(progress);
		}
	}

	private void loadDirectories(String searchKey) {
		footer.setVisible(true);
		content.getChildren().clear();
		File photoDirectory = new File("../Photos/");
		HBox directoriesLine = new HBox();
		directoriesLine.setSpacing(25);
		int index = 0, added = 0;
		totalItems = 0;

		for (final File fileEntry : photoDirectory.listFiles()) {
			totalItems++;
			if (!fileEntry.getName().toLowerCase().contains(searchKey.toLowerCase())) {
				continue;
			}

			if (index <= page * 15 && index >= (page - 1) * 15) {
				if (directoriesLine.getChildren().size() == 5) {
					this.content.getChildren().add(directoriesLine);
					directoriesLine = new HBox();
					directoriesLine.setSpacing(25);
				}
				directoriesLine.getChildren().add(new FolderThumbnail(fileEntry.getAbsolutePath(), this));
				added++;
			}
			index++;
		}
		if (directoriesLine.getChildren().size() > 0 && added < 15)
			this.content.getChildren().add(directoriesLine);

		this.controls.getChildren().clear();
		controls.getChildren().add(directoryControls);
		if (added <= 15) {
			footer.setVisible(false);
		}
	}

	public void addPhoto() {
		FileChooser chooser = new FileChooser();
		chooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Files", "*.jpg", "*.png", "*.jpeg", "*.bmp"));
		File imageSource = chooser.showOpenDialog(null);
		TextInputDialog textInputDialog = new TextInputDialog("New photo");
		textInputDialog.setHeaderText("Enter the photo name:");
		textInputDialog.setContentText("Name:");
		String fileName = textInputDialog.showAndWait().get();

		try {
			FileUtils.copyFile(imageSource, new File(currentDirectory.getAbsolutePath() + "/" + fileName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		loadPhotos(currentDirectory, this.returnPaneType);
	}

	public void addDirectory() {
		TextInputDialog textInputDialog = new TextInputDialog("New collection");
		textInputDialog.setHeaderText("Enter the collection name:");
		textInputDialog.setContentText("Name:");
		String directoryName = textInputDialog.showAndWait().get();
		File newDirectory = new File("../Photos/" + directoryName);
		if (newDirectory.mkdir()) {
			loadDirectories();
		}
	}

	private void goBack() {
		if (this.returnPaneType == PaneType.Browse) {
			this.loadDirectories();
		} else {
			this.loadDirectories();
			this.app.changeToProcessScene();
		}
	}

	private void incrementPage() {
		if (page * 15 < totalItems) {
			page++;
			loadDirectories();
		}
	}

	private void decrementPage() {
		if (page > 1) {
			page--;
			loadDirectories();
		}
	}
}
