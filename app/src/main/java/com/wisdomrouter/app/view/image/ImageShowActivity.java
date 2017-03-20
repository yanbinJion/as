package com.wisdomrouter.app.view.image;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.Phone.PhotoView;
import com.wisdomrouter.app.view.image.ImageShowActivity;
import com.wisdomrouter.app.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

/*
 * 图片展示
 */
public class ImageShowActivity extends BaseDetailActivity {
	/** 图片展示 */
	private ViewPager image_pager;
	private TextView page_number;
	/** 图片下载按钮 */
	private ImageView download;
	/** 图片列表 */
	private ArrayList<String> imgsUrl = new ArrayList<String>();
	private int position = 0;
	private final static String ALBUM_PATH = Environment
			.getExternalStorageDirectory() + "/wisdomimg/";
	private static final int GETDOWNPICS = 0;
	private static final int FAIL = 2;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case GETDOWNPICS:
					WarnUtils.toast(ImageShowActivity.this, "成功下载到图库,可以在图库中查看!");
					break;

				case FAIL:
					WarnUtils.toast(ImageShowActivity.this, "网络异常!下载失败");
					break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.details_imageshow);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_title_common_pic);
		initTitleBarForLeft("");
		initView();

	}

	@Override
	protected void onResume() {
		super.onResume();
		initData();
	}

	private void initData() {

		imgsUrl = getIntent().getStringArrayListExtra("infos");
		// mAdapter = new ImagePagerAdapter(this, imgsUrl);
		ViewpagerAdapter mAdapter = new ViewpagerAdapter(this, imgsUrl);
		image_pager.setAdapter(mAdapter);
		if (imgsUrl != null && imgsUrl.size() != 0) {
			position = getIntent().getIntExtra("pos", 0);
			page_number.setText((position + 1) + "" + "/" + imgsUrl.size());
			image_pager.setCurrentItem(position);
			image_pager.setOnPageChangeListener(new OnPageChangeListener() {

				@Override
				public void onPageSelected(int arg0) {
					// position = arg0;
					page_number.setText((arg0 + 1) + "/" + imgsUrl.size());
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {

				}

				@Override
				public void onPageScrollStateChanged(int arg0) {

				}
			});
		}
	}

	private void initView() {
		image_pager = (ViewPager) findViewById(R.id.image_pager);
		image_pager.setPageTransformer(true, new DepthPageTransformer());
		page_number = (TextView) findViewById(R.id.page_number);
		download = (ImageView) findViewById(R.id.download);
		// 下载图片
		download.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// 下载图片
				getDownPic();

			}
		});

	}

	// view内控件
	PhotoView full_image;
	TextView progress_text;
	ProgressBar progress;
	TextView retry;
	ImageLoaderConfiguration config;
	ImageLoader imageLoader = ImageLoader.getInstance();

	public class ViewpagerAdapter extends PagerAdapter {
		Context context;
		ArrayList<String> imgsUrl;
		LayoutInflater inflater = null;

		DisplayImageOptions options;

		public ViewpagerAdapter(Context context, ArrayList<String> imgsUrl) {
			this.context = context;
			this.imgsUrl = imgsUrl;
			inflater = LayoutInflater.from(context);
			options = new DisplayImageOptions.Builder()
					.showStubImage(R.drawable.load_default)
							// 设置图片Uri为空或是错误的时候显示的图片
					.showImageForEmptyUri(R.drawable.load_default)
							// 设置图片加载/解码过程中错误时候显示的图片
					.showImageOnFail(R.drawable.load_default)
					.cacheInMemory(false)
							// 设置下载的图片是否缓存在内存中
					.imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
							// 设置图片以如何的编码方式显示
					.cacheInMemory(false).cacheOnDisc(false)
					.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型
					.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
					.displayer(new FadeInBitmapDisplayer(100))// 淡入
					.build();
		}

		@Override
		public int getCount() {
			return imgsUrl == null ? 0 : imgsUrl.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public Object instantiateItem(View container, int position) {
			View view = inflater.from(context).inflate(R.layout.item_imageshow,
					null);
			full_image = (PhotoView) view.findViewById(R.id.full_image);
			progress_text = (TextView) view.findViewById(R.id.progress_text);
			progress = (ProgressBar) view.findViewById(R.id.progress);
			retry = (TextView) view.findViewById(R.id.retry);// 加载失败
			progress_text.setText(String.valueOf(position + 1));
			imageLoader.displayImage(imgsUrl.get(position), full_image, options
					// , new ImageLoadingListener() {
					//
					// @Override
					// public void onLoadingStarted(String imageUri, View view) {
					// // TODO Auto-generated method stub
					// progress.setVisibility(View.VISIBLE);
					// progress_text.setVisibility(View.VISIBLE);
					// full_image.setVisibility(View.GONE);
					// retry.setVisibility(View.GONE);
					// }
					//
					// @Override
					// public void onLoadingComplete(String imageUri,
					// View view, Bitmap loadedImage) {
					// progress.setVisibility(View.GONE);
					// progress_text.setVisibility(View.GONE);
					// full_image.setVisibility(View.VISIBLE);
					// retry.setVisibility(View.GONE);
					// }
					//
					// @Override
					// public void onLoadingCancelled(String imageUri,
					// View view) {
					// progress.setVisibility(View.GONE);
					// progress_text.setVisibility(View.GONE);
					// full_image.setVisibility(View.GONE);
					// retry.setVisibility(View.VISIBLE);
					// }
					//
					// @Override
					// public void onLoadingFailed(String imageUri, View view,
					// FailReason failReason) {
					// progress.setVisibility(View.GONE);
					// progress_text.setVisibility(View.GONE);
					// full_image.setVisibility(View.GONE);
					// retry.setVisibility(View.VISIBLE);
					//
					// }
					// }
			);
			((ViewPager) container).addView(view);
			return view;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			((ViewPager) container).removeView((View) object);
		}
	}

	/**
	 * Get image from newwork
	 *
	 * @param path
	 *            The path of image
	 * @return byte[]
	 * @throws Exception
	 */
	public byte[] getImage(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		InputStream inStream = conn.getInputStream();
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return readStream(inStream);
		}
		return null;
	}

	/**
	 * Get image from newwork
	 *
	 * @param path
	 *            The path of image
	 * @return InputStream
	 * @throws Exception
	 */
	public InputStream getImageStream(String path) throws Exception {
		URL url = new URL(path);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setConnectTimeout(5 * 1000);
		conn.setRequestMethod("GET");
		if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
			return conn.getInputStream();
		}
		return null;
	}

	/**
	 * Get data from stream
	 *
	 * @param inStream
	 * @return byte[]
	 * @throws Exception
	 */
	public static byte[] readStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		outStream.close();
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 保存文件
	 *
	 * @param bm
	 * @param fileName
	 * @throws IOException
	 */
	public void saveFile(Bitmap bm, String fileName) throws IOException {
		File dirFile = new File(ALBUM_PATH);
		if (!dirFile.exists()) {
			dirFile.mkdir();
		}
		File myCaptureFile = new File(ALBUM_PATH + fileName);
		BufferedOutputStream bos = new BufferedOutputStream(
				new FileOutputStream(myCaptureFile));
		bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
		bos.flush();
		bos.close();
	}

	/**
	 * @description:图片下载
	 * @author:wangfanghui
	 * @return:void
	 */
	Bitmap bitmap;
	String fileName;
	String filePath;

	protected void getDownPic() {
		filePath = imgsUrl.get(position).toString();
		fileName = "wisdom" + new Date().getTime() + ".jpg";
		// 有网络的情况下走线程
		if (NetUtil.NETWORN_NONE != NetUtil.getNetworkState(this)) {
			new Thread() {
				public void run() {
					// 得到图片地址
					try {
						byte[] data = getImage(filePath);
						if (data != null) {
							bitmap = BitmapFactory.decodeByteArray(data, 0,
									data.length);// bitmap
						}
						saveFile(bitmap, fileName);
						mHandler.sendEmptyMessage(GETDOWNPICS);
					} catch (Exception e) {
						e.printStackTrace();
						mHandler.sendEmptyMessage(FAIL);
					}

				};
			}.start();
		} else {
			WarnUtils.toast(ImageShowActivity.this, "您的网络不通,无法下载!");
		}

	}

	// 声明称为静态变量有助于调用
	public static byte[] readImage(String path) throws Exception {
		URL url = new URL(path);
		// 记住使用的是HttpURLConnection类
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		// 如果运行超过5秒会自动失效 这是android规定
		conn.setConnectTimeout(5 * 1000);
		InputStream inStream = conn.getInputStream();
		// 调用readStream方法
		return readStream(inStream);
	}

	public class DepthPageTransformer implements ViewPager.PageTransformer {
		private static final float MIN_SCALE = 0.85f;
		private static final float MIN_ALPHA = 0.5f;

		public void transformPage(View view, float position) {
			int pageWidth = view.getWidth();
			int pageHeight = view.getHeight();

			Log.e("TAG", view + " , " + position + "");

			if (position < -1) { // [-Infinity,-1)
				// This page is way off-screen to the left.
				view.setAlpha(0);

			} else if (position <= 1) // a页滑动至b页 ； a页从 0.0 -1 ；b页从1 ~ 0.0
			{ // [-1,1]
				// Modify the default slide transition to shrink the page as
				// well
				float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
				float vertMargin = pageHeight * (1 - scaleFactor) / 2;
				float horzMargin = pageWidth * (1 - scaleFactor) / 2;
				if (position < 0) {
					view.setTranslationX(horzMargin - vertMargin / 2);
				} else {
					view.setTranslationX(-horzMargin + vertMargin / 2);
				}

				// Scale the page down (between MIN_SCALE and 1)
				view.setScaleX(scaleFactor);
				view.setScaleY(scaleFactor);

				// Fade the page relative to its size.
				view.setAlpha(MIN_ALPHA + (scaleFactor - MIN_SCALE)
						/ (1 - MIN_SCALE) * (1 - MIN_ALPHA));

			} else { // (1,+Infinity]
				// This page is way off-screen to the right.
				view.setAlpha(0);
			}
		}
	}

}
