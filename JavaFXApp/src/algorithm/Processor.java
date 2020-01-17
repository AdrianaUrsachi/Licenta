package algorithm;

import java.io.File;
import java.lang.Math.*;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Size;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.HOGDescriptor;
import org.opencv.core.MatOfFloat;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.core.Scalar;

public class Processor {
	
	private Mat detectFace(String image) {

	      // Reading the Image from the file and storing it in to a Matrix object
	      Mat src = Imgcodecs.imread(image);
	      
		  // Check if image is loaded fine
		  if( src.empty() ) {
		    	System.out.println("Error opening image: " + image);
		        System.exit(-1);
		       }	     

	      // Instantiating the CascadeClassifier
	      String xmlFile = "C:\\opencv\\build\\etc\\lbpcascades\\lbpcascade_frontalface.xml";
	      CascadeClassifier classifier = new CascadeClassifier(xmlFile);

	      // Detecting the face in the snap
	      MatOfRect faceDetections = new MatOfRect();
	      classifier.detectMultiScale(src, faceDetections);
	      System.out.println(String.format("Detected %s faces", faceDetections.toArray().length));

	      Mat faceImage = new Mat();
	      Mat resizeimage = new Mat();
	      for (Rect rect : faceDetections.toArray())
	        {
	            faceImage = src.submat(rect);
	            Size sz = new Size(64, 128);
	            Imgproc.resize(faceImage, resizeimage, sz);
	            String window_name = "detected face";
	  	        //HighGui.imshow( window_name,  resizeimage);
	  	        //HighGui.waitKey(0);
	        }

	      return resizeimage;
	}
	
	private void printProp(Mat mag, Mat angle) {
		for (int i = 0; i<mag.rows();i++) { 	
	    	for(int j=0;j<mag.cols();j++) {
	    		System.out.print(mag.get(i,j)[0] + " ");
	    	}
	    	System.out.println();
		}
		System.out.println("#########################################");
		for (int i = 0; i<angle.rows();i++) { 	
	    	for(int j=0;j<angle.cols();j++) {
	    		System.out.print(angle.get(i,j)[0] + " ");
	    	}
	    	System.out.println();
		}
	}
	
	private double computeOrientation(double angle) {
		double lastElement = 2.0 * Math.PI;
		double intervalIncrease = lastElement/9.0;
		double a1 = intervalIncrease;
		double a2 = a1 + intervalIncrease;
		double a3 = a2 + intervalIncrease;
		double a4 = a3 + intervalIncrease;
		double a5 = a4 + intervalIncrease;
		double a6 = a5 + intervalIncrease;
		double a7 = a6 + intervalIncrease;
		double a8 = a7 + intervalIncrease;
		double a9 = a8 + intervalIncrease;
		double orientation = -1.0;
		
		if (angle >= 0.0 && angle <= a1)
			orientation = 1.0;
		if (angle > a1 && angle <= a2)
			orientation =  2.0;
		if (angle > a2 && angle <= a3)
			orientation =  3.0;
		if (angle > a3 && angle <= a4)
			orientation =  4.0;
		if (angle > a4 && angle <= a5)
			orientation =  5.0;
		if (angle > a5 && angle <= a6)
			orientation =  6.0;
		if (angle > a6 && angle <= a7)
			orientation =  7.0;
		if (angle > a7 && angle <= a8)
			orientation =  8.0;
		if (angle > a8 && angle <= a9)
			orientation = 9.0;
		
		return orientation;	
	}
	
	private double[][][] gradientComputation(Mat image) {
		Mat src = image;
		Mat srcGray = new Mat();
	    Mat gradientImage = new Mat();
	     
	    Mat gradX = new Mat();
	    Mat gradY = new Mat();
	    
	    Imgproc.cvtColor( src, srcGray, Imgproc.COLOR_RGB2GRAY );
	    // Gradient X && Gradient Y
	    Imgproc.Sobel(srcGray, gradX, CvType.CV_32F, 1, 0, 1);
	    Imgproc.Sobel(srcGray, gradY, CvType.CV_32F, 0, 1, 1);
	     
	    // Total Gradient (approximate)
	    Mat absGradX = new Mat(); 
	    Mat absGradY = new Mat();
	    Core.convertScaleAbs( gradX, absGradX );
	    Core.convertScaleAbs( gradY, absGradY );
	    Core.addWeighted( absGradX, 0.5, absGradY, 0.5, 0, gradientImage);
	     
        //String window_name = "gradient image";
	    //HighGui.imshow( window_name,  gradientImage);
	    //HighGui.waitKey(0);
	    
	    //Calculate gradient magnitude and direction (in degrees)
	    Mat mag = new Mat();
	    Mat angle = new Mat(); 
	    Core.cartToPolar(gradX, gradY, mag, angle);

	    //printProp(mag,angle);
	    
	    double[][][] result = new double[mag.rows()][mag.cols()][2];
	    for (int i = 0; i<mag.rows();i++) {
	    	
	    	for(int j=0;j<mag.cols();j++)
	    	{
	    		double[] pixelProp = new double[2];
	    		pixelProp[0] = mag.get(i,j)[0];
	    		pixelProp[1] = computeOrientation(angle.get(i, j)[0]);//discretised orientation
	    		//System.out.println(angle.get(i, j)[0]+"   " + pixelProp[1]);
	    		result[i][j] = pixelProp;
	    	}
	    }

	    return result;
	}
	
	private void printVector(double[] vec) {
		for(int i=0;i<vec.length;i++) {
			System.out.print(vec[i]+" ");
		}
	}
	
	private double[][][] computeHOG(double[][][] pixelProp) {

		double[][][] prop = new double[129][65][2];
		double[][][] histograms = new double[128][64][9];

		// create border
		for (int i = 0; i < 129; i++) {
			for (int j = 0; j < 65; j++) {
				if (i == 0) {
					double[] aux = new double[2];
					aux[0] = 0.0;
					aux[1] = 0.0;
					prop[i][j] = aux;
				} else if (j == 0) {
					double[] aux = new double[2];
					aux[0] = 0.0;
					aux[1] = 0.0;
					prop[i][j] = aux;
				} else if (i == 128) {
					double[] aux = new double[2];
					aux[0] = 0.0;
					aux[1] = 0.0;
					prop[i][j] = aux;
				} else if (j == 64) {
					double[] aux = new double[2];
					aux[0] = 0.0;
					aux[1] = 0.0;
					prop[i][j] = aux;
				} else {
					double[] aux = new double[2];
					aux[0] = pixelProp[i - 1][j - 1][0];
					aux[1] = pixelProp[i - 1][j - 1][1];
					prop[i][j] = aux;
				}
			}
		}


		for (int i = 1; i < 128; i++) {

			for (int j = 1; j < 64; j++) {
				double[] hist = new double[9];
				for(int k=0; k<9;k++)
					hist[k] = 0.0;
				
				double o1 = prop[i - 1][j - 1][1];
				double m1 = prop[i - 1][j - 1][0];
				if(o1!=0.0)
					hist[(int) o1 - 1] += m1;

				double o2 = prop[i - 1][j][1];
				double m2 = prop[i - 1][j][0];
				if(o2!=0.0)
					hist[(int) o2 - 1] += m2;

				double o3 = prop[i - 1][j + 1][1];
				double m3 = prop[i - 1][j + 1][0];
				if(o3!=0.0)
					hist[(int) o3 - 1] += m3;

				double o4 = prop[i][j + 1][1];
				double m4 = prop[i][j + 1][0];
				if(o4!=0.0)
					hist[(int) o4 - 1] += m4;

				double o5 = prop[i + 1][j + 1][1];
				double m5 = prop[i + 1][j + 1][0];
				if(o5!=0.0)
					hist[(int) o5 - 1] += m5;

				double o6 = prop[i + 1][j][1];
				double m6 = prop[i + 1][j][0];
				if(o6!=0.0)
					hist[(int) o6 - 1] += m6;

				double o7 = prop[i + 1][j - 1][1];
				double m7 = prop[i + 1][j - 1][0];
				if(o7!=0.0)
					hist[(int) o7 - 1] += m7;

				double o8 = prop[i][j - 1][1];
				double m8 = prop[i][j - 1][0];
				if(o8!=0.0)
					hist[(int) o8 - 1] += m8;

				histograms[i-1][j-1] = hist;
			}
		}
		
		/*for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 64; j++) {
				printVector(histograms[i][j]);
				System.out.println();
			}
		}*/
		
		return histograms;

	}
	
	private void computeLBP(double[][][] histograms) {
		//create 9 images
		
		double[][] i1 = new double[128][64];
		double[][] i2 = new double[128][64];
		double[][] i3 = new double[128][64];
		double[][] i4 = new double[128][64];
		double[][] i5 = new double[128][64];
		double[][] i6 = new double[128][64];
		double[][] i7 = new double[128][64];
		double[][] i8 = new double[128][64];
		double[][] i9 = new double[128][64];
		
		for(int i=0;i<128;i++) {
			for(int j=0;j<64;j++)
			{
				i1[i][j]=0.0;
				i2[i][j]=0.0;
				i3[i][j]=0.0;
				i4[i][j]=0.0;
				i5[i][j]=0.0;
				i6[i][j]=0.0;
				i7[i][j]=0.0;
				i8[i][j]=0.0;
				i9[i][j]=0.0;
			}
		}
		
		Mat image1 = new Mat(128,64,CvType.CV_32F);
		Mat image2 = new Mat(128,64,CvType.CV_32F);
		Mat image3 = new Mat(128,64,CvType.CV_32F);
		Mat image4 = new Mat(128,64,CvType.CV_32F);
		Mat image5 = new Mat(128,64,CvType.CV_32F);
		Mat image6 = new Mat(128,64,CvType.CV_32F);
		Mat image7 = new Mat(128,64,CvType.CV_32F);
		Mat image8 = new Mat(128,64,CvType.CV_32F);
		Mat image9 = new Mat(128,64,CvType.CV_32F);
		
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 64; j++) {
				i1[i][j] += histograms[i][j][0];
				i2[i][j] += histograms[i][j][1];
				i3[i][j] += histograms[i][j][2];
				i4[i][j] += histograms[i][j][3];
				i5[i][j] += histograms[i][j][4];
				i6[i][j] += histograms[i][j][5];
				i7[i][j] += histograms[i][j][6];
				i8[i][j] += histograms[i][j][7];
				i9[i][j] += histograms[i][j][8];
			}
	    }
		for (int i = 0; i < 128; i++) {
			for (int j = 0; j < 64; j++) {
				image1.put(i,j,i1[i][j]);
				image2.put(i,j,i2[i][j]);
				image3.put(i,j,i3[i][j]);
				image4.put(i,j,i4[i][j]);
				image5.put(i,j,i5[i][j]);
				image6.put(i,j,i6[i][j]);
				image7.put(i,j,i7[i][j]);
				image8.put(i,j,i8[i][j]);
				image9.put(i,j,i9[i][j]);
				}
			}
		System.out.println(image1.get(3,4));
		
		
	}
	
	public String process(String selectedFile) {
		// Load the native library.
	       System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

	       Mat face = detectFace(selectedFile);
	       double[][][] pixelProp = gradientComputation(face);
		   double[][][] histograms = computeHOG(pixelProp);
		   computeLBP(histograms);
	       
		File folder = new File("../Photos/");

		/*for (final File collection : folder.listFiles()) {
			System.out.println(collection.getName() + " cu imaginile: ");
			for (File image : collection.listFiles()) {
				System.out.println(image.getName());
			}
			System.out.println("");
		}*/

		// returnezi numele directorului corect
		return "Adriana_Lima";
	}
}

