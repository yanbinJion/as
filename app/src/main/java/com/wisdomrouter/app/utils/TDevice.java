package com.wisdomrouter.app.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import com.ta.utdid2.android.utils.StringUtils;
import com.wisdomrouter.app.HandApplication;

import java.io.File;
import java.lang.reflect.Field;
import java.text.NumberFormat;
import java.util.List;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class TDevice {

	// 手机网络类型
	public static final int NET_TYPE_WIFI = 0x01;
	public static final int NET_TYPE_CMWAP = 0x02;
	public static final int NET_TYPE_CMNET = 0x03;

	public static boolean GTE_HC;
	public static boolean GTE_ICS;
	public static boolean PRE_HC;
	private static Boolean _hasBigScreen = null;
	private static Boolean _hasCamera = null;
	private static Boolean _isTablet = null;

	public static float displayDensity = 0.0F;

	static {
		GTE_ICS = Build.VERSION.SDK_INT >= 14;
		GTE_HC = Build.VERSION.SDK_INT >= 11;
		PRE_HC = Build.VERSION.SDK_INT >= 11 ? false : true;
	}

	public TDevice() {
	}

	public static float dpToPixel(float dp) {
		return dp * (getDisplayMetrics().densityDpi / 160F);
	}

	public static float getDensity() {
		if (displayDensity == 0.0)
			displayDensity = getDisplayMetrics().density;
		return displayDensity;
	}

	public static DisplayMetrics getDisplayMetrics() {
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((WindowManager) HandApplication.getInstance().getSystemService(
				Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(
				displaymetrics);
		return displaymetrics;
	}

	/**
	 * 取得设备的屏幕高度
	 * 
	 * @return
	 */
	public static float getScreenHeight() {
		return getDisplayMetrics().heightPixels;
	}

	/**
	 * 取得设备的屏幕宽度
	 * 
	 * @return
	 */
	public static float getScreenWidth() {
		return getDisplayMetrics().widthPixels;
	}

	public static int[] getRealScreenSize(Activity activity) {
		int[] size = new int[2];
		int screenWidth = 0, screenHeight = 0;
		WindowManager w = activity.getWindowManager();
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();
		d.getMetrics(metrics);
		// since SDK_INT = 1;
		screenWidth = metrics.widthPixels;
		screenHeight = metrics.heightPixels;
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17)
			try {
				screenWidth = (Integer) Display.class.getMethod("getRawWidth")
						.invoke(d);
				screenHeight = (Integer) Display.class
						.getMethod("getRawHeight").invoke(d);
			} catch (Exception ignored) {
			}
		// includes window decorations (statusbar bar/menu bar)
		if (Build.VERSION.SDK_INT >= 17)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d,
						realSize);
				screenWidth = realSize.x;
				screenHeight = realSize.y;
			} catch (Exception ignored) {
			}
		size[0] = screenWidth;
		size[1] = screenHeight;
		return size;
	}

	public static int getStatusBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0;
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			return HandApplication.getInstance().getResources()
					.getDimensionPixelSize(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static boolean hasBigScreen() {
		boolean flag = true;
		if (_hasBigScreen == null) {
			boolean flag1;
			if ((0xf & HandApplication.getInstance().getResources()
					.getConfiguration().screenLayout) >= 3)
				flag1 = flag;
			else
				flag1 = false;
			Boolean boolean1 = Boolean.valueOf(flag1);
			_hasBigScreen = boolean1;
			if (!boolean1.booleanValue()) {
				if (getDensity() <= 1.5F)
					flag = false;
				_hasBigScreen = Boolean.valueOf(flag);
			}
		}
		return _hasBigScreen.booleanValue();
	}

	public static final boolean hasCamera() {
		if (_hasCamera == null) {
			PackageManager pckMgr = HandApplication.getInstance()
					.getPackageManager();
			boolean flag = pckMgr
					.hasSystemFeature("android.hardware.camera.front");
			boolean flag1 = pckMgr.hasSystemFeature("android.hardware.camera");
			boolean flag2;
			if (flag || flag1)
				flag2 = true;
			else
				flag2 = false;
			_hasCamera = Boolean.valueOf(flag2);
		}
		return _hasCamera.booleanValue();
	}

	public static boolean hasHardwareMenuKey(Context getInstance) {
		boolean flag = false;
		if (PRE_HC)
			flag = true;
		else if (GTE_ICS) {
			flag = ViewConfiguration.get(getInstance).hasPermanentMenuKey();
		} else
			flag = false;
		return flag;
	}

	public static boolean hasInternet() {
		boolean flag;
		if (((ConnectivityManager) HandApplication.getInstance()
				.getSystemService("connectivity")).getActiveNetworkInfo() != null)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean gotoGoogleMarket(Activity activity, String pck) {
		try {
			Intent intent = new Intent();
			intent.setPackage("com.android.vending");
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("market://details?id=" + pck));
			activity.startActivity(intent);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static boolean isPackageExist(String pckName) {
		try {
			PackageInfo pckInfo = HandApplication.getInstance()
					.getPackageManager().getPackageInfo(pckName, 0);
			if (pckInfo != null)
				return true;
		} catch (NameNotFoundException e) {

		}
		return false;
	}

	public static void hideAnimatedView(View view) {
		if (PRE_HC && view != null)
			view.setPadding(view.getWidth(), 0, 0, 0);
	}

	public static void hideSoftKeyboard(View view) {
		if (view == null)
			return;
		((InputMethodManager) HandApplication.getInstance().getSystemService(
				Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
				view.getWindowToken(), 0);
	}

	public static boolean isLandscape() {
		boolean flag;
		if (HandApplication.getInstance().getResources().getConfiguration().orientation == 2)
			flag = true;
		else
			flag = false;
		return flag;
	}

	public static boolean isPortrait() {
		boolean flag = true;
		if (HandApplication.getInstance().getResources().getConfiguration().orientation != 1)
			flag = false;
		return flag;
	}

	public static boolean isTablet() {
		if (_isTablet == null) {
			boolean flag;
			if ((0xf & HandApplication.getInstance().getResources()
					.getConfiguration().screenLayout) >= 3)
				flag = true;
			else
				flag = false;
			_isTablet = Boolean.valueOf(flag);
		}
		return _isTablet.booleanValue();
	}

	public static float pixelsToDp(float f) {
		return f / (getDisplayMetrics().densityDpi / 160F);
	}

	public static void showAnimatedView(View view) {
		if (PRE_HC && view != null)
			view.setPadding(0, 0, 0, 0);
	}

	public static void showSoftKeyboard(Dialog dialog) {
		dialog.getWindow().setSoftInputMode(4);
	}

	public static void showSoftKeyboard(View view) {
		((InputMethodManager) HandApplication.getInstance().getSystemService(
				Context.INPUT_METHOD_SERVICE)).showSoftInput(view,
				InputMethodManager.SHOW_FORCED);
	}

	public static void toogleSoftKeyboard(View view) {
		((InputMethodManager) HandApplication.getInstance().getSystemService(
				Context.INPUT_METHOD_SERVICE)).toggleSoftInput(0,
				InputMethodManager.HIDE_NOT_ALWAYS);
	}

	public static boolean isSdcardReady() {
		return Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState());
	}

	public static String getCurCountryLan() {
		return HandApplication.getInstance().getResources().getConfiguration().locale
				.getLanguage()
				+ "-"
				+ HandApplication.getInstance().getResources()
						.getConfiguration().locale.getCountry();
	}

	public static boolean isZhCN() {
		String lang = HandApplication.getInstance().getResources()
				.getConfiguration().locale.getCountry();
		if (lang.equalsIgnoreCase("CN")) {
			return true;
		}
		return false;
	}

	public static String percent(double p1, double p2) {
		String str;
		double p3 = p1 / p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(2);
		str = nf.format(p3);
		return str;
	}

	public static String percent2(double p1, double p2) {
		String str;
		double p3 = p1 / p2;
		NumberFormat nf = NumberFormat.getPercentInstance();
		nf.setMinimumFractionDigits(0);
		str = nf.format(p3);
		return str;
	}

	public static void gotoMarket(Context getInstance, String pck) {
		if (!isHaveMarket(getInstance)) {

			return;
		}
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_VIEW);
		intent.setData(Uri.parse("market://details?id=" + pck));
		if (intent.resolveActivity(getInstance.getPackageManager()) != null) {
			getInstance.startActivity(intent);
		}
	}

	public static boolean isHaveMarket(Context getInstance) {
		Intent intent = new Intent();
		intent.setAction("android.intent.action.MAIN");
		intent.addCategory("android.intent.category.APP_MARKET");
		PackageManager pm = getInstance.getPackageManager();
		List<ResolveInfo> infos = pm.queryIntentActivities(intent, 0);
		return infos.size() > 0;
	}

	public static void openAppInMarket(Context getInstance) {
		if (getInstance != null) {
			String pckName = getInstance.getPackageName();
			try {
				gotoMarket(getInstance, pckName);
			} catch (Exception ex) {
				try {
					String otherMarketUri = "http://market.android.com/details?id="
							+ pckName;
					Intent intent = new Intent(Intent.ACTION_VIEW,
							Uri.parse(otherMarketUri));
					getInstance.startActivity(intent);
				} catch (Exception e) {

				}
			}
		}
	}

	public static void setFullScreen(Activity activity) {
		WindowManager.LayoutParams params = activity.getWindow()
				.getAttributes();
		params.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
		activity.getWindow().setAttributes(params);
		activity.getWindow().addFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	public static void cancelFullScreen(Activity activity) {
		WindowManager.LayoutParams params = activity.getWindow()
				.getAttributes();
		params.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
		activity.getWindow().setAttributes(params);
		activity.getWindow().clearFlags(
				WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
	}

	/**
	 * 得到app包信息
	 * 
	 * @param pckName
	 * @return
	 */
	public static PackageInfo getPackageInfo(String pckName) {
		try {
			return HandApplication.getInstance().getPackageManager()
					.getPackageInfo(pckName, 0);
		} catch (NameNotFoundException e) {

		}
		return null;
	}

	/**
	 * 得到app的版本号
	 * 
	 * @return
	 */
	public static int getVersionCode() {
		int versionCode = 0;
		try {
			versionCode = getVersionCode(HandApplication.getInstance()
					.getPackageName());
		} catch (Exception ex) {
			versionCode = 0;
		}
		return versionCode;
	}

	/**
	 * 得到app的版本号
	 * 
	 * @param packageName
	 * @return
	 */
	public static int getVersionCode(String packageName) {
		int versionCode = 0;
		try {
			versionCode = getPackageInfo(packageName).versionCode;
		} catch (Exception ex) {
			versionCode = 0;
		}
		return versionCode;
	}

	/**
	 * 得到app版本名
	 * 
	 * @return
	 */
	public static String getVersionName() {
		String name = "";
		try {
			name = getPackageInfo(HandApplication.getInstance()
					.getPackageName()).versionName;
		} catch (Exception ex) {
			name = "";
		}
		return name;
	}

	public static boolean isScreenOn() {
		PowerManager pm = (PowerManager) HandApplication.getInstance()
				.getSystemService(Context.POWER_SERVICE);
		return pm.isScreenOn();
	}

	/**
	 * 安装apk
	 * 
	 * @param getInstance
	 * @param file
	 */
	public static void installAPK(Context getInstance, File file) {
		if (file == null || !file.exists())
			return;
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		getInstance.startActivity(intent);
	}

	public static Intent getInstallApkIntent(File file) {
		Intent intent = new Intent();
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		return intent;
	}

	public static void openDial(Context getInstance, String number) {
		Uri uri = Uri.parse("tel:" + number);
		Intent it = new Intent(Intent.ACTION_DIAL, uri);
		getInstance.startActivity(it);
	}

	public static void openSMS(Context getInstance, String smsBody, String tel) {
		Uri uri = Uri.parse("smsto:" + tel);
		Intent it = new Intent(Intent.ACTION_SENDTO, uri);
		it.putExtra("sms_body", smsBody);
		getInstance.startActivity(it);
	}

	public static void openDail(Context getInstance) {
		Intent intent = new Intent(Intent.ACTION_DIAL);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getInstance.startActivity(intent);
	}

	public static void openSendMsg(Context getInstance) {
		Uri uri = Uri.parse("smsto:");
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		getInstance.startActivity(intent);
	}

	/**
	 * 调用照相机
	 * 
	 * @param getInstance
	 */
	public static void openCamera(Context getInstance) {
		Intent intent = new Intent();
		intent.setAction("android.media.action.STILL_IMAGE_CAMERA");
		intent.setFlags(0x34c40000);
		getInstance.startActivity(intent);
	}

	public static String getIMEI() {
		TelephonyManager tel = (TelephonyManager) HandApplication.getInstance()
				.getSystemService(Context.TELEPHONY_SERVICE);
		return tel.getDeviceId();
	}

	public static String getPhoneType() {
		return Build.MODEL;
	}

	public static boolean isWifiOpen() {
		boolean isWifiConnect = false;
		ConnectivityManager cm = (ConnectivityManager) HandApplication
				.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
		// check the networkInfos numbers
		NetworkInfo[] networkInfos = cm.getAllNetworkInfo();
		for (int i = 0; i < networkInfos.length; i++) {
			if (networkInfos[i].getState() == NetworkInfo.State.CONNECTED) {
				if (networkInfos[i].getType() == ConnectivityManager.TYPE_MOBILE) {
					isWifiConnect = false;
				}
				if (networkInfos[i].getType() == ConnectivityManager.TYPE_WIFI) {
					isWifiConnect = true;
				}
			}
		}
		return isWifiConnect;
	}

	public static void uninstallApk(Context getInstance, String packageName) {
		if (isPackageExist(packageName)) {
			Uri packageURI = Uri.parse("package:" + packageName);
			Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,
					packageURI);
			getInstance.startActivity(uninstallIntent);
		}
	}

	@SuppressWarnings("deprecation")
	public static void copyTextToBoard(String string) {
		if (TextUtils.isEmpty(string))
			return;
		ClipboardManager clip = (ClipboardManager) HandApplication
				.getInstance().getSystemService(Context.CLIPBOARD_SERVICE);
		clip.setText(string);

	}

	/**
	 * 发送邮件
	 *
	 * @param getInstance
	 * @param subject
	 *            主题
	 * @param content
	 *            内容
	 * @param emails
	 *            邮件地址
	 */
	public static void sendEmail(Context getInstance, String subject,
			String content, String... emails) {
		try {
			Intent intent = new Intent(Intent.ACTION_SEND);
			// 模拟器
			// intent.setType("text/plain");
			intent.setType("message/rfc822"); // 真机
			intent.putExtra(Intent.EXTRA_EMAIL, emails);
			intent.putExtra(Intent.EXTRA_SUBJECT, subject);
			intent.putExtra(Intent.EXTRA_TEXT, content);
			getInstance.startActivity(intent);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static int getStatuBarHeight() {
		Class<?> c = null;
		Object obj = null;
		Field field = null;
		int x = 0, sbar = 38;// 默认为38，貌似大部分是这样的
		try {
			c = Class.forName("com.android.internal.R$dimen");
			obj = c.newInstance();
			field = c.getField("status_bar_height");
			x = Integer.parseInt(field.get(obj).toString());
			sbar = HandApplication.getInstance().getResources()
					.getDimensionPixelSize(x);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return sbar;
	}

	public static int getActionBarHeight(Context getInstance) {
		int actionBarHeight = 0;
		TypedValue tv = new TypedValue();
		if (getInstance.getTheme().resolveAttribute(
				android.R.attr.actionBarSize, tv, true))
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getInstance.getResources().getDisplayMetrics());

		if (actionBarHeight == 0
				&& getInstance.getTheme().resolveAttribute(
						android.R.attr.actionBarSize, tv, true)) {
			actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
					getInstance.getResources().getDisplayMetrics());
		}

		return actionBarHeight;
	}

	public static boolean hasStatusBar(Activity activity) {
		WindowManager.LayoutParams attrs = activity.getWindow().getAttributes();
		if ((attrs.flags & WindowManager.LayoutParams.FLAG_FULLSCREEN) == WindowManager.LayoutParams.FLAG_FULLSCREEN) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 调用系统安装了的应用分享
	 *
	 * @param getInstance
	 * @param title
	 * @param url
	 */
	public static void showSystemShareOption(Activity getInstance,
			final String title, final String url) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT, "分享：" + title);
		intent.putExtra(Intent.EXTRA_TEXT, title + " " + url);
		getInstance.startActivity(Intent.createChooser(intent, "选择分享"));
	}

	/**
	 * 获取当前网络类型
	 *
	 * @return 0：没有网络 1：WIFI网络 2：WAP网络 3：NET网络
	 */
	public static int getNetworkType(Context context) {
		int netType = 0;
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return netType;
		}
		int nType = networkInfo.getType();
		if (nType == ConnectivityManager.TYPE_MOBILE) {
			String extraInfo = networkInfo.getExtraInfo();
			if (!StringUtils.isEmpty(extraInfo)) {
				if (extraInfo.toLowerCase().equals("cmnet")) {
					netType = NET_TYPE_CMNET;
				} else {
					netType = NET_TYPE_CMWAP;
				}
			}
		} else if (nType == ConnectivityManager.TYPE_WIFI) {
			netType = NET_TYPE_WIFI;
		}
		return netType;
	}
}
