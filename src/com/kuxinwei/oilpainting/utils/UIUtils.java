/**
 * 创建时间：2015-3-25 下午4:52:22
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting.utils;

import android.content.Context;
import android.widget.Toast;

public class UIUtils {

	public static void showToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_SHORT).show();
	}
}
