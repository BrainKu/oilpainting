/**
 * 创建时间：2015-3-25 下午3:23:33
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting;

import com.kuxinwei.oilpainting.utils.L;

import android.app.Application;

public class PaintApplication extends Application {

	@Override public void onCreate() {
		super.onCreate();
		L.d("Application start");
	}
}
