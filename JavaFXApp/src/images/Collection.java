package images;

import java.io.File;
import java.util.ArrayList;

public class Collection {
	private File root;
	ArrayList<CollectionImage> images = new ArrayList<CollectionImage>();
	String name;

	public Collection(String name) {
		this.name = name;
		root = new File("../Photos/" + name);
	}

	public void initialize() {
		images.clear();
		for (final File fileEntry : root.listFiles()) {
			if (fileEntry.getAbsolutePath().contains("-mat")) {
				continue;
			}
			CollectionImage image = new CollectionImage(fileEntry.getName(), name);
			image.initialize();
			images.add(image);
		}
	}

	public void update() {
		for (final File fileEntry : root.listFiles()) {
			if (fileEntry.getAbsolutePath().contains("-mat")) {
				continue;
			}
			CollectionImage image = getByName(fileEntry.getName());
			if (image == null) {
				image = new CollectionImage(fileEntry.getName(), name);
				image.initialize();
				images.add(image);
			} else {
				image.update();
			}
		}
	}

	public void delete() {
		for (CollectionImage image : images) {
			image.delete();
		}
	}

	public CollectionImage getByName(String name) {
		for (CollectionImage image : images) {
			if (image.getName().equals(name)) {
				return image;
			}
		}
		return null;
	}

	public ArrayList<CollectionImage> getImages() {
		return images;
	}

	public void setImages(ArrayList<CollectionImage> images) {
		this.images = images;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
