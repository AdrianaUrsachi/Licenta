package images;

import java.io.File;

public class WorkerThread implements Runnable {
	public ThreadsManager manager;

	public WorkerThread(ThreadsManager manager) {
		this.manager = manager;
	}

	@Override
	public void run() {
		while (true) {
			File file = manager.getFile();
			if (file == null) {
				return;
			}
			Collection collection = new Collection(file.getName());
			collection.initialize();
			manager.addCollection(collection);
		}
	}
}
