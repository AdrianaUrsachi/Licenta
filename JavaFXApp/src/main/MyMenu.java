package main;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

@SuppressWarnings("restriction")
public class MyMenu extends MenuBar {
	App app;

	public MyMenu(App app) {
		this.app = app;
		Menu menu = new Menu("Menu");
		MenuItem browseMenuItem = new MenuItem("Browse");
		MenuItem processMenuItem = new MenuItem("Process");
		MenuItem exitMenuItem = new MenuItem("Exit");

		browseMenuItem.setOnAction((event) -> app.changeToBrowseScene());
		processMenuItem.setOnAction((event) -> app.changeToProcessScene());
		exitMenuItem.setOnAction((event) -> app.exit());

		menu.getItems().addAll(browseMenuItem, processMenuItem, exitMenuItem);
		this.getMenus().add(menu);
	}
}