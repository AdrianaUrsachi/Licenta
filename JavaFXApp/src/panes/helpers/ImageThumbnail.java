package panes.helpers;

import java.io.File;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import panes.BrowsePane;

@SuppressWarnings("restriction")
public class ImageThumbnail extends VBox {
	private File directory;
	private File imageFile;
	private BrowsePane parent;

	public ImageThumbnail(File directory, String photo, BrowsePane parent) {
		this.directory = directory;
		this.parent = parent;
		imageFile = new File(photo);
		ImageView image = new ImageView(new Image(imageFile.toURI().toString()));
		image.setFitHeight(230);
		image.setFitWidth(230);

		HBox controls = new HBox();
		controls.setAlignment(Pos.TOP_CENTER);
		Button button = new Button("Delete");
		button.setOnAction((event) -> deletePhoto());
		controls.getChildren().addAll(button);
		controls.setSpacing(15);

		this.getChildren().addAll(image, controls);
	}

	public void deletePhoto() {
		this.imageFile.delete();
		this.parent.loadPhotos(directory);
	}
}
