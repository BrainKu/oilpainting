/**
 * 创建时间：2015-3-25 下午2:44:55
 * @author kuxinwei
 * @since 1.0
 * @version 1.0<br>
 */
package com.kuxinwei.oilpainting;

import java.io.IOException;
import java.util.ArrayList;

import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.BitmapFactory;

import com.kuxinwei.oilpainting.utils.L;

public class PatchPool {

	// private static SparseArray<ArrayList<MAT_DATA> CACHE_POOL = new
	// SparseArray<ArrayList<MAT_DATA>>();
	private static ArrayList<MatData> CACHE_POOL = new ArrayList<MatData>();
	private static final String TYPE_PREFIX = "stotr_";
	private static final String TYPE_POSFIX = ".jpg";
	private static final int TOTAL_COUNT = 4;

	public static boolean init(Context context) {
		AssetManager mAssetManager = context.getAssets();
		for (int count = 0; count < TOTAL_COUNT; count++) {
			MatData matData = new MatData();
			try {
				Utils.bitmapToMat(BitmapFactory.decodeStream(mAssetManager
						.open(TYPE_PREFIX + count + TYPE_POSFIX)), matData.mMat);
				matData.init();
				CACHE_POOL.add(matData);
			} catch (IOException e) {
				L.e("file not exist", e);
			}
		}
		return true;
	}

	// FIXME maybe throw exception
	public static MatData getPatch(int index) {
		MatData matData = CACHE_POOL.get(index);
		return matData;
	}

	public static class MatData {
		public Mat mMat;
		public int mRow;
		public int mCol;
		public int mMeanLumian;
		public byte[] mYarr;
		public double mDirectional; // Not use

		public MatData() {
			mMat = new Mat();
		}

		public void init() {
			mRow = mMat.rows();
			mCol = mMat.cols();
			mYarr = new byte[mRow * mCol];
			mMat.get(0, 0, mYarr);
			mMeanLumian = (int) Core.mean(mMat).val[0];
		}
	}
}
