/**
 * 创建时间：2015-3-25 上午10:14:57
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting.utils;

import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;

public class ImageUtils {

	private static ArrayList<Mat> mChannels = new ArrayList<Mat>();
	private static final int DEFAULT_CV_TYPE = CvType.CV_8U;

	public static Mat cvtRGB2YIQ(Mat srcMat) {
		mChannels.clear();
		Core.split(srcMat, mChannels);
		Mat r = mChannels.get(0);
		Mat g = mChannels.get(1);
		Mat b = mChannels.get(2);
		Mat y = new Mat();
		Mat i = new Mat();
		Mat q = new Mat();
		// Mat y = Mat.zeros(r.size(), r.type());
		// Mat i = Mat.zeros(r.size(), r.type());
		// Mat q = Mat.zeros(r.size(), r.type());
		// MAT_RGB2YIQ = new MatOfDouble(0.299, 0.587, 0.114, 0.596, -0.275,
		// -0.321, 0.212, -0.523, 0.311);
		// cal Y
		Core.addWeighted(r, 0.299, g, 0.587, 0.0, y);
		Core.addWeighted(y, 1.0, b, 0.114, 0.0, y);
		// cal I
		Core.addWeighted(r, 0.596, g, -0.275, 0.0, i);
		Core.addWeighted(i, 1.0, b, -0.321, 0.0, i);
		// cal Q
		Core.addWeighted(r, 0.212, g, -0.523, 0.0, q);
		Core.addWeighted(i, 1.0, b, 0.311, 0.0, q);

		// Core.addWeighted(y, 0.0, y, 0.0, 0.0, y);
		Mat resultMat = Mat.zeros(srcMat.size(), CvType.CV_32FC3);
		mChannels.set(0, y);
		mChannels.set(1, i);
		mChannels.set(2, q);
		Core.merge(mChannels, resultMat);
		return resultMat;
	}

	public static Mat cvtYIQ2RGB(Mat srcMat) {
		Mat resultMat = Mat.zeros(srcMat.size(), DEFAULT_CV_TYPE);
		mChannels.clear();
		Core.split(srcMat, mChannels);
		Mat y = mChannels.get(0);
		Mat i = mChannels.get(1);
		Mat q = mChannels.get(2);
		Mat r = Mat.zeros(y.size(), y.type());
		Mat g = Mat.zeros(y.size(), y.type());
		Mat b = Mat.zeros(y.size(), y.type());
		Core.addWeighted(i, 0.956, q, 0.620, 0.0, r);
		Core.add(r, y, r);
		// Core.addWeighted(r, 1.0, y, 1.0, 0.0, r);
		Core.addWeighted(i, -0.272, q, -0.647, 0.0, g);
		Core.add(r, y, r);
		Core.addWeighted(i, -1.108, q, 1.705, 0.0, b);
		Core.add(g, y, r);
		mChannels.set(0, r);
		mChannels.set(1, g);
		mChannels.set(2, b);
		return resultMat;
	}

	public static void retrivePixelValue(Mat srcMat) {
		mChannels.clear();
		Core.split(srcMat, mChannels);
		Mat yMat = mChannels.get(0);
		byte[] pixel = new byte[yMat.rows() * yMat.cols()];
		yMat.get(0, 0, pixel);
		for (int i = 0, size = pixel.length; i < size; i++) {
			pixel[i] = (byte) (pixel[i] + 20);
		}
		yMat.put(0, 0, pixel);
		mChannels.set(0, yMat);
		Core.merge(mChannels, srcMat);
	}

	public static void extractPatch(String imgFilePath) {
		Mat curImg = Highgui.imread(imgFilePath);
		int arr[][] = { { 642, 504 }, { 240, 527 }, { 602, 196 }, { 559, 101 } };
		int EOP = imgFilePath.lastIndexOf(".");
		for (int i = 0, size = arr.length; i < size; i++) {
			String tempFileName = imgFilePath.substring(0, EOP) + "_temp_" + i
					+ imgFilePath.substring(EOP, imgFilePath.length());
			Rect roi = new Rect(arr[i][0], arr[i][1], 64, 64);
			Mat subMat = curImg.submat(roi);
			FileUtils.write(tempFileName, subMat);
		}
	}
}
