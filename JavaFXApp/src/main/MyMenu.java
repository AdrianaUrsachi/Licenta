package main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

@SuppressWarnings("restriction")
public class MyMenu extends MenuBar {
	App app;
	private MenuItem processMenuItem;

	public MyMenu(App app) {
		this.app = app;
		Menu menu = new Menu("Menu");
		MenuItem browseMenuItem = new MenuItem("Browse");
		processMenuItem = new MenuItem("Process");
		processMenuItem.setDisable(true);
		Menu imagesMenu = new Menu("Images");
		MenuItem resetMenuItem = new MenuItem("Reset");
		MenuItem updateMenuItem = new MenuItem("Update");
		MenuItem reprocessMenuItem = new MenuItem("Re-process");
		MenuItem convertMenuItem = new MenuItem("Convert");
		MenuItem exitMenuItem = new MenuItem("Exit");

		browseMenuItem.setOnAction((event) -> app.changeToBrowseScene());
		processMenuItem.setOnAction((event) -> app.changeToProcessScene());
		resetMenuItem.setOnAction((event) -> App.imageManager.delete());
		reprocessMenuItem.setOnAction((event) -> App.imageManager.update());
		convertMenuItem.setOnAction((event) -> App.imageManager.convertTif());
		updateMenuItem.setOnAction((event) -> App.imageManager.update());
		exitMenuItem.setOnAction((event) -> app.exit());

		imagesMenu.getItems().addAll(updateMenuItem, resetMenuItem, reprocessMenuItem, convertMenuItem);
		menu.getItems().addAll(browseMenuItem, processMenuItem, imagesMenu, exitMenuItem);
		this.getMenus().add(menu);
	}

	public void enableProcess() {
		processMenuItem.setDisable(false);
	}
}