package images;

import java.io.File;
import java.util.ArrayList;

import main.App;

public class ThreadsManager {
	private ArrayList<Collection> collections = new ArrayList<Collection>();
	private File[] files;
	private App app;
	int count = 0;

	public ThreadsManager(File[] files, ArrayList<Collection> collections, App app) {
		this.files = files;
		this.collections = collections;
		this.app = app;
	}

	synchronized public void addCollection(Collection collection) {
		collections.add(collection);
		System.out.println(collections.size());
	}

	synchronized public File getFile() {
		if (count < files.length) {
			count++;
			app.getBrowsePane().setProgress((double) count / (double) files.length);
			return files[count - 1];
		}
		return null;
	}
}
