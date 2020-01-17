package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import panes.BrowsePane;
import panes.ProcessPane;

@SuppressWarnings("restriction")
public class App extends Application {
	private Stage stage;
	private BrowsePane browsePane;
	private ProcessPane processPane;
	private VBox layout;
	private MyMenu menu;

	public static void main(String[] args) {
		launch(args);
	}

	@Override
	public void start(Stage stage) {
		this.stage = stage;
		this.stage.setTitle("Application");

		browsePane = new BrowsePane();
		processPane = new ProcessPane(this);
		menu = new MyMenu(this);
		layout = new VBox();
		changeToBrowseScene();

		Scene scene = new Scene(layout, 820, 700);
		this.stage.setScene(scene);
		this.stage.show();
	}

	public void changeToBrowseScene() {
		this.layout.getChildren().clear();
		this.layout.getChildren().addAll(menu, browsePane);
	}

	public void changeToProcessScene() {
		this.layout.getChildren().clear();
		this.layout.getChildren().addAll(menu, processPane);
	}

	public void exit() {
		this.stage.close();
	}

	public BrowsePane getBrowsePane() {
		return browsePane;
	}

	public ProcessPane getProcessPane() {
		return processPane;
	}
}