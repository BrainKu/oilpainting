package com.kuxinwei.oilpainting;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kuxinwei.oilpainting.utils.ImageUtils;
import com.kuxinwei.oilpainting.utils.L;
import com.kuxinwei.oilpainting.utils.PaintUtils;
import com.kuxinwei.oilpainting.utils.UIUtils;

public class MainActivity extends Activity implements OnClickListener {
	private Button mCaputreBtn;
	private Button mProcessBtn;
	private ImageView mDestImgv;
	private static final String IMAGE_UNSPECIFIED = "image/*";
	private Mat mCurrentImg;

	@Override protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initOpenCV(this);
		mCaputreBtn = (Button) findViewById(R.id.btn_capture);
		mProcessBtn = (Button) findViewById(R.id.btn_process);
		mDestImgv = (ImageView) findViewById(R.id.imgv_dest);
		mCaputreBtn.setOnClickListener(this);
		mProcessBtn.setOnClickListener(this);
	}

	private void initOpenCV(Context context) {
		if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_11,
				context, mOpenCVCallBack)) {
			Toast.makeText(context, "OpenCV init failed", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private Bitmap mCurBitmap = null;
	private String imgFilePath;

	@Override protected void onActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == 233 && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			Cursor cursor = getContentResolver().query(uri,
					new String[] { MediaStore.Images.ImageColumns.DATA }, null,
					null, null);
			cursor.moveToFirst();
			imgFilePath = cursor.getString(0);
			cursor.close();
			if (isOpenCVInit) {

				mCurrentImg = Highgui.imread(imgFilePath);
				// Imgproc.cvtColor(mCurrentImg, mCurrentImg,
				// Imgproc.COLOR_RGB2YUV);
				// Imgproc.cvtColor(mCurrentImg, mCurrentImg,
				// Imgproc.COLOR_YUV2RGB);
				// mCurrentImg = ImageUtils.cvtYIQ2RGB(mCurrentImg);
				if (mCurrentImg != null) {
					mCurBitmap = BitmapFactory.decodeFile(imgFilePath);
					mDestImgv.setImageBitmap(mCurBitmap);
					Toast.makeText(MainActivity.this,
							mCurrentImg.size().toString(), Toast.LENGTH_SHORT)
							.show();
				}
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	// public static final float KER_H[] = { -1, 0, 1, -1, 0, 1, -1, 0, 1 };
	// public static final float KER_V[] = { 1, 1, 1, 0, 0, 0, -1, -1, -1 };
	// public float calDirectional(Mat mat) {
	// Mat hMat = Mat.zeros(mat.size(), CvType.CV_64FC1);
	// Mat vMat = Mat.zeros(mat.size(), CvType.CV_64FC1);
	// int rows = mat.rows();
	// int cols = mat.cols();
	// for (int i = 1; i < rows - 1; i++) {
	// for (int j = 1; j < rows - 2; j++) {
	// double sumh = 0;
	// for (int k = -1; k < 2; k++) {
	// // sumh += mat.ste
	// }
	// }
	// }
	// return 0;
	// }

	@Override public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.btn_capture:
			Intent intent = new Intent(Intent.ACTION_PICK, null);
			intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
					IMAGE_UNSPECIFIED);
			startActivityForResult(intent, 233);
			break;
		case R.id.btn_process:
			if (mCurBitmap != null) {
				new ProcessThread().start();
			}
			break;
		default:
			break;
		}
	}

	class ProcessThread extends Thread {
		@Override public void run() {
			if (mCurrentImg != null)
				PaintUtils.paint(mCurrentImg);
			mHandler.obtainMessage(MSG_SHOW_CONTENT, "Mission finish")
					.sendToTarget();
		}
	}

	public static final int MSG_SHOW_CONTENT = 1001;
	private Handler mHandler = new Handler(new Handler.Callback() {

		@Override public boolean handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SHOW_CONTENT:
				UIUtils.showToast(getApplicationContext(), msg.obj.toString());
				break;

			default:
				break;
			}
			return false;
		}
	});

	private boolean isOpenCVInit = false;

	private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(this) {
		@Override public void onManagerConnected(int status) {
			switch (status) {
			case LoaderCallbackInterface.SUCCESS: {
				isOpenCVInit = true;
				new Thread(new Runnable() {
					@Override public void run() {
//						PatchPool.init(MainActivity.this);
					}
				}).start();
			}
				break;
			default: {
				super.onManagerConnected(status);
			}
				break;
			}
		}
	};
}
