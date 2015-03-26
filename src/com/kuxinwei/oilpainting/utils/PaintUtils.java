/**
 * 创建时间：2015-3-26 上午10:32:02
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import android.graphics.Point;

public class PaintUtils {

	public static final int[] DEFAULT_RADIUS = { 8, 4, 2 };
	public static final float GAUSSIAN_FACTOR = 0.6f;
	public static final float GRID_FACTOR = 0.5f;
	private static final int THRESHOLD = 75;
	public static final boolean DEBUG = false;

	public static Mat paint(Mat srcImg) {
		Mat resultImg = Mat.zeros(srcImg.size(), CvType.CV_16SC3);
		for (int index = 0, size = DEFAULT_RADIUS.length; index < size; index++) {
			int radius = DEFAULT_RADIUS[index];
			float sigma = GAUSSIAN_FACTOR * DEFAULT_RADIUS[index];
			Mat referImg = Mat.zeros(srcImg.size(), srcImg.type());
			Imgproc.GaussianBlur(srcImg, referImg, new Size(radius + 1,
					radius + 1), sigma);
			if (DEBUG)
				FileUtils.write(ImageUtils.getTempFilePatch(
						"/storage/emulated/0/PIC/file.jpg", "_gaussian_"
								+ index), referImg);
			paintLayer(resultImg, referImg, radius);
			if (DEBUG)
				FileUtils.write(ImageUtils.getTempFilePatch(
						"/storage/emulated/0/PIC/file.jpg", "_layer_" + index),
						resultImg);
		}
		if (DEBUG)
			FileUtils.write(ImageUtils.getTempFilePatch(
					"/storage/emulated/0/PIC/file.jpg", "temp_" + "finish"),
					resultImg);
		return resultImg;
	}

	private static void paintLayer(Mat resultImg, Mat referImg, int radius) {
		List<Stroke> strokeList = new ArrayList<Stroke>();
		int width = resultImg.cols();
		int height = resultImg.rows();
		// resultImg.convertTo(resultImg, CvType.CV_16SC3);
		referImg.convertTo(referImg, CvType.CV_16SC3);
		short srcColArr[][] = new short[3][width * height];
		short referColArr[][] = new short[3][width * height];
		int difference[][] = new int[height][width];
		ArrayList<Mat> srcChannels = new ArrayList<Mat>();
		ArrayList<Mat> referChannels = new ArrayList<Mat>();
		Core.split(resultImg, srcChannels);
		Core.split(referImg, referChannels);
		extractPixel(srcChannels, srcColArr);
		extractPixel(referChannels, referColArr);
		calDifference(srcColArr, referColArr, difference, width, height);
		int gridSize = (int) (GRID_FACTOR * radius);
		for (int h = 0; h < height; h += gridSize) {
			for (int w = 0; w < width; w += gridSize) {
				if (h + gridSize >= height || w + gridSize >= width)
					break;
				int sumDiffer = sumDifference(difference, h, w, gridSize);
				int areaError = sumDiffer / (gridSize * gridSize);
				if (areaError > THRESHOLD) {
					Point p = getMaxDifferPoint(difference, h, w, gridSize);
					Stroke s = makeCircleStroke(radius, p.x, p.y, referColArr,
							width);
					strokeList.add(s);
				}
			}
		}
		Collections.shuffle(strokeList);
		for (Stroke s : strokeList) {
			s.apply(resultImg);
		}
	}

	static int count = 0;

	/**
	 * @param radius
	 * @param x
	 * @param y
	 * @param referColArr
	 * @return
	 */
	private static Stroke makeCircleStroke(int radius, int x, int y,
			short[][] referColArr, int width) {
		Stroke stroke = new Stroke();
		int pos = width * y + x;
		stroke.b = referColArr[0][pos];
		stroke.g = referColArr[1][pos];
		stroke.r = referColArr[2][pos];
		stroke.startX = x;
		stroke.startY = y;
		stroke.radius = radius;
		return stroke;
	}

	/**
	 * @param difference
	 * @param h
	 * @param w
	 * @param gridSize
	 * @return
	 */
	private static Point getMaxDifferPoint(int[][] difference, int h, int w,
			int gridSize) {
		Point point = new Point(0, 0);
		int maxValue = 0;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				int value = difference[h + i][w + j];
				if (value > maxValue) {
					point.x = w + j;
					point.y = h + i;
				}
			}
		}
		return point;
	}

	/**
	 * @param difference
	 * @param h
	 * @param w
	 * @param gridSize
	 * @return
	 */
	private static int sumDifference(int[][] difference, int h, int w,
			int gridSize) {
		int result = 0;
		for (int i = 0; i < gridSize; i++) {
			for (int j = 0; j < gridSize; j++) {
				result += difference[h + i][w + j];
			}
		}
		return result;
	}

	/**
	 * 计算两张图片当中不同像素之间的欧式距离并保存到表中
	 * 
	 * @param srcColArr
	 * @param referColArr
	 * @param difference
	 */
	private static void calDifference(short[][] srcColArr,
			short[][] referColArr, int[][] difference, int width, int height) {
		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width; w++) {
				int pos = h * width + w;
				int srcB = srcColArr[0][pos];
				int srcG = srcColArr[1][pos];
				int srcR = srcColArr[2][pos];
				int referB = referColArr[0][pos];
				int referG = referColArr[1][pos];
				int referR = referColArr[2][pos];
				int value = (srcR - referR) * (srcR - referR) + (srcG - referG)
						* (srcG - referG) + (srcB - referB) * (srcB - referB);
				difference[h][w] = (int) Math.abs(Math.sqrt(value));
			}
		}
	}

	private static void extractPixel(ArrayList<Mat> channels,
			short[][] srcColArr) {
		channels.get(0).get(0, 0, srcColArr[0]);
		channels.get(1).get(0, 0, srcColArr[1]);
		channels.get(2).get(0, 0, srcColArr[2]);
	}

	public static class Stroke {
		public int startX;
		public int startY;
		public int r;
		public int g;
		public int b;
		public int radius;

		public void apply(Mat resultMat) {
			Scalar color = new Scalar(b, g, r);
			org.opencv.core.Point point = new org.opencv.core.Point(startX,
					startY);
			Core.circle(resultMat, point, radius, color, -1);
		}
	}
}
