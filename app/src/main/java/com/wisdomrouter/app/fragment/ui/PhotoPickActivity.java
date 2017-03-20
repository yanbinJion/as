package com.wisdomrouter.app.fragment.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CameraSdkParameterInfo;
import com.wisdomrouter.app.fragment.bean.FolderInfo;
import com.wisdomrouter.app.fragment.bean.ImageInfo;
import com.wisdomrouter.app.fragment.ui.adapter.FolderAdapter;
import com.wisdomrouter.app.fragment.ui.adapter.ImageCameraGridAdapter;
import com.wisdomrouter.app.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 相片列表界面
 * 
 * @author admin
 */
public class PhotoPickActivity extends FragmentActivity {

	private CameraSdkParameterInfo mCameraSdkParameterInfo = new CameraSdkParameterInfo();

	private ArrayList<String> resultList = new ArrayList<>();// 结果数据
	private ArrayList<FolderInfo> mResultFolder = new ArrayList<FolderInfo>();// 文件夹数据

	// 不同loader定义
	private static final int LOADER_ALL = 0;
	private static final int LOADER_CATEGORY = 1;

	private RelativeLayout rlLeft;
	private RelativeLayout rlRight;
	private TextView tvRight;
	private TextView mCategoryText;
	private GridView mGridView;
	private PopupWindow mpopupWindow;
	private ImageCameraGridAdapter mImageAdapter;
	private FolderAdapter mFolderAdapter;
	private boolean hasFolderGened = false;
	private File mTmpFile;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.camerasdk_activity_main);
		initExtra();
		initViews();
		initEvent();
		getSupportLoaderManager().restartLoader(LOADER_ALL, null,
				mLoaderCallback);
	}

	// 获取传过来的参数
	private void initExtra() {

		Intent intent = getIntent();
		try {
			mCameraSdkParameterInfo = (CameraSdkParameterInfo) intent
					.getSerializableExtra(CameraSdkParameterInfo.EXTRA_PARAMETER);
			resultList = mCameraSdkParameterInfo.getImage_list();
		} catch (Exception e) {
		}

	}

	private void initViews() {
		rlLeft = (RelativeLayout) findViewById(R.id.camerasdk_title_rlyt_left);
		rlRight = (RelativeLayout) findViewById(R.id.camerasdk_title_rlyt_right);
		tvRight = (TextView) findViewById(R.id.camerasdk_title_txv_right_text);
		mCategoryText = (TextView) findViewById(R.id.camerasdk_actionbar_title);
		if (resultList.size() > 0) {
			tvRight.setText("完成(" + resultList.size() + "/"
					+ Const.MAX_SELECT_PICS + ")");
			tvRight.setBackgroundResource(R.drawable.bg_select_compete);
		} else {
			tvRight.setText("完成(0/" + Const.MAX_SELECT_PICS + ")");
			tvRight.setBackgroundResource(R.drawable.bg_select_uncompete);
		}

		Drawable drawable = getResources().getDrawable(
				R.drawable.message_popover_arrow);
		drawable.setBounds(10, 0, drawable.getMinimumWidth(),
				drawable.getMinimumHeight());
		mCategoryText.setCompoundDrawables(null, null, drawable, null);
		rlLeft.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mGridView = (GridView) findViewById(R.id.gv_list);
		mImageAdapter = new ImageCameraGridAdapter(this,
				mCameraSdkParameterInfo.isShow_camera());
		mGridView.setAdapter(mImageAdapter);
		mFolderAdapter = new FolderAdapter(this);
	}

	private void initEvent() {

		mCategoryText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				showPopupFolder(view);
			}
		});
		tvRight.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (resultList.size() > 0) {
					selectComplate();
				}
			}
		});

		mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView absListView, int state) {

				final Picasso picasso = Picasso.with(PhotoPickActivity.this);
				if (state == SCROLL_STATE_IDLE
						|| state == SCROLL_STATE_TOUCH_SCROLL) {
					picasso.resumeTag(PhotoPickActivity.this);
				} else {
					picasso.pauseTag(PhotoPickActivity.this);
				}

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		mGridView.getViewTreeObserver().addOnGlobalLayoutListener(
				new ViewTreeObserver.OnGlobalLayoutListener() {
					@Override
					@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
					public void onGlobalLayout() {

						final int width = mGridView.getWidth();
						final int height = mGridView.getHeight();
						// mGridWidth = width;
						// mGridHeight = height;
						final int desireSize = getResources()
								.getDimensionPixelOffset(R.dimen.dimen_120);
						final int numCount = width / desireSize;
						final int columnSpace = getResources()
								.getDimensionPixelOffset(R.dimen.dimen_5);
						int columnWidth = (width - columnSpace * (numCount - 1))
								/ numCount;
						mImageAdapter.setItemSize(columnWidth);

						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
							mGridView.getViewTreeObserver()
									.removeOnGlobalLayoutListener(this);
						} else {
							mGridView.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					}
				});

		mGridView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view,
					int i, long l) {

				if (mImageAdapter.isShowCamera()) {
					if (i == 0) {
						if (Const.MAX_SELECT_PICS == resultList.size()) {
							Toast.makeText(PhotoPickActivity.this,
									R.string.camerasdk_msg_amount_limit,
									Toast.LENGTH_SHORT).show();
						} else {
							showCameraAction();
						}
						return;
					}
				}
				ImageInfo imageInfo = (ImageInfo) adapterView.getAdapter()
						.getItem(i);
				selectImageFromGrid(imageInfo);
			}
		});
	}

	/**
	 * 选择相机
	 */
	private void showCameraAction() {
		// 跳转到系统照相机
		Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		if (cameraIntent.resolveActivity(getPackageManager()) != null) {
			// 设置系统相机拍照后的输出路径
			// 创建临时文件
			mTmpFile = FileUtil.createTmpFile(this);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT,
					Uri.fromFile(mTmpFile));
			startActivityForResult(cameraIntent,
					CameraSdkParameterInfo.TAKE_PICTURE_FROM_CAMERA);
		} else {
			Toast.makeText(this, R.string.camerasdk_msg_no_camera,
					Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 相机拍照完成后，返回图片路径
		if (requestCode == CameraSdkParameterInfo.TAKE_PICTURE_FROM_CAMERA) {
			if (resultCode == Activity.RESULT_OK) {
				if (mTmpFile != null) {
					resultList.add(mTmpFile.getPath());
					selectComplate();
				}
			} else {
				if (mTmpFile != null && mTmpFile.exists()) {
					mTmpFile.delete();
				}
			}
		}
	}

	/**
	 * 选择图片操作
	 * 
	 * @param imageInfo
	 */
	private void selectImageFromGrid(ImageInfo imageInfo) {
		if (imageInfo != null) {
			if (resultList.contains(imageInfo.path)) {
				resultList.remove(imageInfo.path);
			} else {
				// 判断选择数量问题
				if (Const.MAX_SELECT_PICS == resultList.size()) {
					Toast.makeText(this, R.string.camerasdk_msg_amount_limit,
							Toast.LENGTH_SHORT).show();
					return;
				}
				resultList.add(imageInfo.path);

			}
			if (resultList.size() > 0) {
				tvRight.setText("完成(" + resultList.size() + "/"
						+ Const.MAX_SELECT_PICS + ")");
				tvRight.setBackgroundResource(R.drawable.bg_select_compete);
			} else {
				tvRight.setText("完成(0/" + Const.MAX_SELECT_PICS + ")");
				tvRight.setBackgroundResource(R.drawable.bg_select_uncompete);
			}
			mImageAdapter.select(imageInfo);
		}
	}

	private LoaderManager.LoaderCallbacks<Cursor> mLoaderCallback = new LoaderManager.LoaderCallbacks<Cursor>() {

		private final String[] IMAGE_PROJECTION = {
				MediaStore.Images.Media.DATA,
				MediaStore.Images.Media.DISPLAY_NAME,
				MediaStore.Images.Media.DATE_ADDED,
				MediaStore.Images.Media._ID, MediaStore.Images.Media.SIZE };

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			if (id == LOADER_ALL) {
				CursorLoader cursorLoader = new CursorLoader(
						PhotoPickActivity.this,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						IMAGE_PROJECTION, null, null, IMAGE_PROJECTION[2]
								+ " DESC");
				return cursorLoader;
			} else if (id == LOADER_CATEGORY) {
				CursorLoader cursorLoader = new CursorLoader(
						PhotoPickActivity.this,
						MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						IMAGE_PROJECTION, IMAGE_PROJECTION[0] + " like '%"
								+ args.getString("path") + "%'", null,
						IMAGE_PROJECTION[2] + " DESC");
				return cursorLoader;
			}

			return null;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (data != null) {

				List<ImageInfo> imageInfos = new ArrayList<ImageInfo>();
				int count = data.getCount();
				if (count > 0) {
					data.moveToFirst();
					do {

						String path = data.getString(data
								.getColumnIndexOrThrow(IMAGE_PROJECTION[0]));
						String name = data.getString(data
								.getColumnIndexOrThrow(IMAGE_PROJECTION[1]));
						long dateTime = data.getLong(data
								.getColumnIndexOrThrow(IMAGE_PROJECTION[2]));
						int size = data.getInt(data
								.getColumnIndexOrThrow(IMAGE_PROJECTION[4]));
						boolean show_flag = size > 1024 * 10; // 是否大于10K
						if ((name.contains("jpg") || name.contains("png"))
								&& show_flag&&FileUtil.checkFilePathExists(path)&&!path.contains("amap")) {
							ImageInfo imageInfo = new ImageInfo(path, name,
									dateTime);
							imageInfos.add(imageInfo);

							if (!hasFolderGened && show_flag) {
								// 获取文件夹名称
								File imageFile = new File(path);
								File folderFile = imageFile.getParentFile();
								FolderInfo folderInfo = new FolderInfo();
								folderInfo.name = folderFile.getName();
								folderInfo.path = folderFile.getAbsolutePath();
								folderInfo.cover = imageInfo;
								if (!mResultFolder.contains(folderInfo)) {
									List<ImageInfo> imageList = new ArrayList<ImageInfo>();
									imageList.add(imageInfo);
									folderInfo.imageInfos = imageList;
									mResultFolder.add(folderInfo);
								} else {
									// 更新
									FolderInfo f = mResultFolder
											.get(mResultFolder
													.indexOf(folderInfo));
									f.imageInfos.add(imageInfo);
								}
							}
						}

					} while (data.moveToNext());

					mImageAdapter.setData(imageInfos);

					// 设定默认选择
					if (resultList != null && resultList.size() > 0) {
						mImageAdapter.setSelectedList(resultList);
					}

					mFolderAdapter.setData(mResultFolder);
					hasFolderGened = true;

				}
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {

		}
	};

	/**
	 * 创建弹出的文件夹ListView
	 */
	private void showPopupFolder(View v) {

		View view = getLayoutInflater().inflate(
				R.layout.camerasdk_popup_folder, null);
		LinearLayout ll_popup = (LinearLayout) view.findViewById(R.id.ll_popup);
		ll_popup.startAnimation(AnimationUtils.loadAnimation(
				getApplicationContext(), R.anim.camerasdk_push_up_in));

		ListView lsv_folder = (ListView) view.findViewById(R.id.lsv_folder);
		lsv_folder.setAdapter(mFolderAdapter);
		// if(mpopupWindow==null){

		WindowManager manager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		Display display = manager.getDefaultDisplay();
		// int width = display.getWidth();
		// int height = display.getHeight();

		mpopupWindow = new PopupWindow(this);
		mpopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mpopupWindow.setHeight(LayoutParams.MATCH_PARENT);

		mpopupWindow.setFocusable(true);
		mpopupWindow.setOutsideTouchable(true);
		// }
		view.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				mpopupWindow.dismiss();
			}
		});
		lsv_folder.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				mFolderAdapter.setSelectIndex(arg2);
				final int index = arg2;
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						mpopupWindow.dismiss();
						if (index == 0) {
							getSupportLoaderManager().restartLoader(LOADER_ALL,
									null, mLoaderCallback);
							mCategoryText.setText(R.string.camerasdk_album_all);
							mImageAdapter.setShowCamera(mCameraSdkParameterInfo
									.isShow_camera());
						} else {
							FolderInfo folderInfo = (FolderInfo) mFolderAdapter
									.getItem(index);
							if (null != folderInfo) {
								mImageAdapter.setData(folderInfo.imageInfos);
								mCategoryText.setText(folderInfo.name);
								// 设定默认选择
								if (resultList != null && resultList.size() > 0) {
									mImageAdapter.setSelectedList(resultList);
								}
							}
							// mImageAdapter.setShowCamera(false);
						}
						// 滑动到最初始位置
						mGridView.smoothScrollToPosition(0);

					}
				}, 100);
			}
		});
		mpopupWindow.setContentView(view);
		mpopupWindow.setBackgroundDrawable(new ColorDrawable(0xb0000000));
		mpopupWindow.showAsDropDown(findViewById(R.id.layout_actionbar_root));
	}

	// 选择完成实现跳转
	private void selectComplate() {

		mCameraSdkParameterInfo.setImage_list(resultList);
		Bundle b = new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,
				mCameraSdkParameterInfo);

		Intent intent = new Intent();
		intent.putExtras(b);

		setResult(RESULT_OK, intent);
		finish();

	}

	// 返回裁剪后的图片
	public void getForResultComplate(String path) {

		ArrayList<String> list = new ArrayList<String>();
		list.add(path);

		Intent intent = new Intent();
		mCameraSdkParameterInfo.setImage_list(list);
		Bundle b = new Bundle();
		b.putSerializable(CameraSdkParameterInfo.EXTRA_PARAMETER,
				mCameraSdkParameterInfo);
		intent.putExtras(b);
		setResult(RESULT_OK, intent);
		finish();
	}

}
