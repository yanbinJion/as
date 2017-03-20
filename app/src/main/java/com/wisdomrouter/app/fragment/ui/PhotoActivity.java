package com.wisdomrouter.app.fragment.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.ImageCompress;

import net.tsz.afinal.annotation.view.ViewInject;

import java.io.IOException;

public class PhotoActivity extends BaseDetailActivity {
	@ViewInject(id = R.id.photo_bt_exit, click = "Exit")
	Button photo_bt_exit;
	@ViewInject(id = R.id.photo_bt_del, click = "Delete")
	Button photo_bt_del;
	@ViewInject(id = R.id.photo_bt_enter, click = "Enter")
	Button photo_bt_enter;
	@ViewInject(id = R.id.photo_relativeLayout)
	RelativeLayout photo_relativeLayout;

	@ViewInject(id = R.id.img)
	ImageView pager;

	private int pos = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_photo);
		Init();

	}

	String url;

	private void Init() {
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_title_common);
		initTitleBarForLeft("已选照片");
		photo_relativeLayout.setBackgroundColor(0x70000000);

		// 获取 ID号
		Intent intent = getIntent();
		url = intent.getStringExtra("url");
		pos = intent.getIntExtra("pos", -1);

		ImageCompress compress = new ImageCompress();
		ImageCompress.CompressOptions options = new ImageCompress.CompressOptions();
		options.maxWidth = 800;
		options.maxHeight = 800;
		Bitmap bitmap = compress.compressFromUri(PhotoActivity.this, options,
				url);
		int exifRotation = readPictureDegree(url);
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(exifRotation);

		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		pager.setImageBitmap(resizedBitmap); // 设置Bitmap

	}

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public void Exit(View v) {
		finish();
	}

	public void Delete(View v) {
		Intent intent = new Intent();
		if (pos != -1) {
			intent.putExtra("position", pos);
		}
		setResult(RESULT_OK, intent);
		finish();
	}

	public void Enter(View v) {

		finish();
	}

}
