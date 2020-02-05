package images;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import org.opencv.core.Core;

import main.App;

public class ImageManager {
	private File root = new File("../Photos/");
	private ArrayList<Collection> collections = new ArrayList<Collection>();
	private App app;

	public ImageManager(App app) {
		// Load the native library.
		this.app = app;
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		initialize();
	}

	public void convertTif() {
		for (Collection collection : collections) {
			for (CollectionImage image : collection.images) {
				File file = image.getRoot();
				if (file.getAbsolutePath().contains(".tif")) {
					try {
						BufferedImage tifImage = null;
						tifImage = ImageIO.read(file);
						String newName = file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 4)
								+ ".bmp";
						ImageIO.write(tifImage, "bmp", new File(newName));
						file.delete();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		System.out.println("Done converting");
	}

	public void initialize() {
		ThreadsManager manager = new ThreadsManager(root.listFiles(), collections, app);
		ArrayList<Thread> threads = new ArrayList<Thread>();
		for (int i = 0; i < 20; i++) {
			Thread thread = new Thread(new WorkerThread(manager));
			thread.start();
			threads.add(thread);
		}

		waitThreads(threads);
	}

	public void update() {
		ArrayList<Thread> threads = new ArrayList<Thread>();
		int count = 0, totalCount = 0;

		for (final File fileEntry : root.listFiles()) {
			try {
				count++;
				Thread thread = new Thread(() -> {
					Collection collection = getByName(fileEntry.getName());
					if (collection == null) {
						collection = new Collection(fileEntry.getName());
						collection.initialize();
						collections.add(collection);
					} else {
						collection.update();
					}
				});
				thread.start();
				threads.add(thread);

				if (count >= 15) {
					waitThreads(threads);
					totalCount += count;
					count = 0;
					System.out.println("First " + totalCount + " are updated.");
				}
			} catch (Exception e) {

			}
		}
		waitThreads(threads);
	}

	public void delete() {
		for (Collection collection : collections) {
			collection.delete();
		}
		System.out.println("Done deleting");
	}

	public Collection getByName(String name) {
		for (Collection collection : collections) {
			if (collection.getName().equals(name)) {
				return collection;
			}
		}
		return null;
	}

	private void waitThreads(ArrayList<Thread> threads) {
		for (Thread t : threads) {
			try {
				t.join();
				System.out.print(".");
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		threads.clear();
	}

	public ArrayList<Collection> getCollections() {
		return collections;
	}
}
