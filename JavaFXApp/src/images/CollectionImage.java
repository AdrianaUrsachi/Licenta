package images;

import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import algorithm.Processor;

public class CollectionImage {
	private String name;
	private File root;
	private Mat[] mats;
	private boolean valid = true;

	public CollectionImage(String name, String collectionName) {
		this.name = name;
		root = new File("../Photos/" + collectionName + "/" + name);
	}

	public void initialize() {
		try {
			mats = Processor.processPhoto(root.getAbsolutePath());
			valid = true;
		} catch (Exception e) {
			mats = null;
			valid = false;
		}
	}

	public Mat[] getMats() {
		return valid ? mats : null;
	}

	public void setMats(Mat[] mats) {
		this.mats = mats;
	}

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}

	public void delete() {
		String matFile = removeExtension(root.getAbsolutePath()) + "-mat";
		mats = null;
		if (new File(matFile + "1.jpg").exists()) {
			for (int i = 1; i < 10; i++) {
				new File(matFile + String.valueOf(i) + ".jpg").delete();
			}
		}
	}

	public File getRoot() {
		return root;
	}

	public void setRoot(File root) {
		this.root = root;
	}

	public void update() {
		if (mats == null) {
			initialize();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public static String removeExtension(String name) {
		return name.replaceFirst("[.][^.]+$", "");
	}

	public static void serialize(String path, Mat[] mats) {
		int i = 1;
		for (Mat mat : mats) {
			Imgcodecs.imwrite(path + String.valueOf(i) + ".jpg", mat);
			i++;
		}
	}

	public static Mat[] deserialize(String path) {
		ArrayList<Mat> mats = new ArrayList<Mat>();
		for (int i = 1; i < 10; i++) {
			mats.add(Imgcodecs.imread(path + String.valueOf(i) + ".jpg"));
		}
		Mat[] result = { mats.get(0), mats.get(1), mats.get(2), mats.get(3), mats.get(4), mats.get(5), mats.get(6),
				mats.get(7), mats.get(8) };
		return result;
	}
}
