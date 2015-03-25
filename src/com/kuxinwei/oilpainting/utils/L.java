/**
 * 创建时间：2015-3-25 上午10:17:19
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting.utils;

import android.util.Log;

public class L {
	private final static String TAG = "OpenCV_Android";

	public static void d(String content) {
		Log.d(TAG, content);
	}

	public static void d(String tag, String content) {
		Log.d(tag, content);
	}

	public static void i(String content) {
		Log.i(TAG, content);
	}
}
