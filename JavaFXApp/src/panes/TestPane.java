package panes;

import java.util.Random;

import algorithm.Processor;
import images.Collection;
import images.CollectionImage;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import main.App;

@SuppressWarnings("restriction")
public class TestPane extends VBox {
	private App app;
	private VBox content;
	private HBox controls;
	private TextField leaveOut;
	private TextField certaintyField;
	private TextField accuracyField;
	private HBox certaintyBox;
	private HBox accuracyBox;
	private ProgressBar progressBar;

	public TestPane(App app) {
		this.app = app;

		this.app = app;
		content = new VBox();
		content.setSpacing(25);
		content.setPrefHeight(570);
		content.setPadding(new Insets(20, 20, 20, 20));

		accuracyBox = new HBox();
		accuracyBox.setAlignment(Pos.CENTER_LEFT);
		accuracyBox.setSpacing(5);
		accuracyBox.setMaxHeight(50);
		accuracyBox.setMinHeight(50);
		Label accuracyLabel = new Label("Accuracy: ");
		accuracyField = new TextField();
		accuracyField.setEditable(false);
		accuracyBox.getChildren().addAll(accuracyLabel, accuracyField);

		certaintyBox = new HBox();
		certaintyBox.setAlignment(Pos.CENTER_LEFT);
		certaintyBox.setSpacing(5);
		certaintyBox.setMaxHeight(50);
		certaintyBox.setMinHeight(50);
		Label certaintyLabel = new Label("Certainty: ");
		certaintyField = new TextField();
		certaintyField.setEditable(false);
		certaintyBox.getChildren().addAll(certaintyLabel, certaintyField);
		content.getChildren().addAll(accuracyBox, certaintyBox);

		controls = new HBox();
		controls.setPadding(new Insets(20, 20, 20, 20));
		controls.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
		controls.setAlignment(Pos.CENTER_LEFT);
		controls.setMaxHeight(50);
		controls.setMinHeight(50);
		this.certaintyBox.setVisible(false);
		this.accuracyBox.setVisible(false);

		HBox box = new HBox();
		box.setAlignment(Pos.CENTER);
		box.setSpacing(5);
		Label label = new Label("Leave out:");
		leaveOut = new TextField();
		Button testButton = new Button("Test");
		testButton.setOnAction((event) -> test());
		HBox progressBox = new HBox();
		progressBox.setPadding(new Insets(0, 50, 0, 100));
		progressBar = new ProgressBar(0);
		progressBar.setMinSize(200, 27);
		progressBar.setVisible(false);
		progressBox.getChildren().add(progressBar);

		box.getChildren().addAll(label, leaveOut, testButton,progressBox);
		controls.getChildren().addAll(box);
		this.getChildren().addAll(controls, content);
	}
	
	public void setProgress(double progress) {
		if (progress >= 1) {
			this.progressBar.setVisible(false);
			this.app.enableProcess();
		} else {
			this.progressBar.setVisible(true);
			this.progressBar.setProgress(progress);
		}
	}

	private void test() {
		Thread thread = new Thread(() -> {
			int leaveOutValue = Integer.parseInt(leaveOut.getText()), totalCount = 0, successes = 0;
			double certainetyPercentage = 0;
			Random random = new Random();

			for (Collection collection : App.imageManager.getCollections()) {
				long count = collection.getImages().stream().filter(i -> i.isValid()).count(); //nr de imagini care sunt valide
				if (count - 2 > leaveOutValue) {
					for (int i = 0; i < leaveOutValue; i++) {
						int index = random.nextInt(collection.getImages().size());
						collection.getImages().get(index).setUsable(false);
					}
				}
			}
			double collectionsCount = App.imageManager.getCollections().size(), count = 1;
			for (Collection collection : App.imageManager.getCollections()) {
				count++;
				for (CollectionImage image : collection.getImages()) {
					if (image.isValid() && image.isUsable()) {
						image.setUsable(false);
						totalCount++;

						Object[] result = Processor.process(image.getMats());
						if (result[0].equals(collection.getName())) {
							successes++;
						}
						certainetyPercentage += (Double) result[1];
					}
				}
				double progress = count/collectionsCount;
				Platform.runLater(new Thread(() -> {
					this.setProgress(progress);
				}));
			}
			int certainty = 100 - ((int) certainetyPercentage / totalCount / 10);
			int accuracy = 100 * successes / totalCount;

			Platform.runLater(new Thread(() -> {
				this.certaintyBox.setVisible(true);
				this.accuracyBox.setVisible(true);
				this.certaintyField.setText(String.valueOf(certainty) + "%");
				this.accuracyField.setText(String.valueOf(accuracy) + "%");

				for (Collection collection : App.imageManager.getCollections()) {
					for (CollectionImage image : collection.getImages()) {
						image.setUsable(true);
					}
				}
			}));
		});
		
		thread.start();
	}

}
