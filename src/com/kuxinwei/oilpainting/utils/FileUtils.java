/**
 * 创建时间：2015-3-25 上午10:19:51
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting.utils;

import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

public class FileUtils {

	public static boolean write(String filepath, Mat src) {
		return Highgui.imwrite(filepath, src);
	}
}
