package panes.helpers;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import panes.BrowsePane;

@SuppressWarnings("restriction")
public class FolderThumbnail extends VBox {
	private File directory;
	private BrowsePane parent;
	private static Image folderImg = new Image("/resources/folder.png");
	private static Image plusImg = new Image("/resources/plus.png");
	private static Image xImg = new Image("/resources/x.png");

	public FolderThumbnail(String path, BrowsePane parent) {
		this.parent = parent;
		directory = new File(path);

		ImageView image = new ImageView(folderImg);
		image.setFitHeight(125);
		image.setFitWidth(125);

		ImageView plusImage = new ImageView(plusImg);
		plusImage.setFitHeight(12);
		plusImage.setFitWidth(12);
		ImageView xImage = new ImageView(xImg);
		xImage.setFitHeight(12);
		xImage.setFitWidth(12);

		HBox controls = new HBox();
		Label label = new Label(directory.getName().split("\\.")[0]);
		label.setMaxWidth(75);
		label.setMinWidth(75);

		Button deleteButton = new Button("");
		deleteButton.setGraphic(xImage);
		deleteButton.setTooltip(new Tooltip("Delete directory"));
		deleteButton.setOnAction((event) -> deleteDirectory());
		Button enterButton = new Button("");
		enterButton.setGraphic(plusImage);
		enterButton.setTooltip(new Tooltip("Enter directory"));
		enterButton.setOnAction((event) -> this.parent.loadPhotos(directory));

		controls.getChildren().addAll(label, deleteButton, enterButton);

		this.getChildren().addAll(image, controls);
	}

	public void deleteDirectory() {
		try {
			FileUtils.deleteDirectory(this.directory);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.parent.loadDirectories();
	}
}