package com.wisdomrouter.app.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.utils.MD5Util;
import com.wisdomrouter.app.utils.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.text.format.DateFormat;
import android.util.Base64;
import android.util.Log;

public class Utils {
	private static String fullFmt = "yyyyMMddkkmmss";
	private static String shortFmt = "yyyy-MM-dd";
	private static String fmt1 = "yyyy-MM-dd kk:mm:ss";

	public static boolean checkFlash(Context ctx) {
		PackageManager pm = ctx.getPackageManager();
		List<PackageInfo> infoList = pm
				.getInstalledPackages(PackageManager.GET_SERVICES);
		for (PackageInfo info : infoList) {
			if ("com.adobe.flashplayer".equals(info.packageName)) {
				return true;
			}
		}
		return false;
	}

	public static Bitmap roate(Bitmap source, int angle) {
		int w = source.getWidth();
		int h = source.getHeight();

		float px = w / 2;
		float py = 0f;

		Matrix matrix = new Matrix();
		matrix.setRotate(angle, px, py);
		Bitmap resizedBitmap = Bitmap.createBitmap(source, 0, 0, (int) (w),
				(int) (h), matrix, true);

		return resizedBitmap;
	}

	public static String nowNormal() {
		return format(System.currentTimeMillis(), fmt1);
	}

	public static String now() {
		return format(System.currentTimeMillis());
	}

	@SuppressLint("SimpleDateFormat")
	public static String parse(String fmt, String dateStr) {
		SimpleDateFormat df = new SimpleDateFormat(fmt);
		try {
			Date d = df.parse(dateStr);
			return "" + DateFormat.format(shortFmt, d);
		} catch (ParseException e) {
		}

		return dateStr;
	}

	public static String format(long time) {
		return format(time, fullFmt);
	}

	public static String format(long time, String fmt) {
		return (String) DateFormat.format(fmt, time);
	}

	public static String shortDate(long time) {
		return (String) DateFormat.format(shortFmt, time);
	}

	public static boolean isEmpty(String s) {
		if (s == null || s.equals("") || s.trim().length() == 0) {
			return true;
		}

		return false;
	}

	// 检查网络是否可用
	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cm.getActiveNetworkInfo() != null) {
			NetworkInfo info = cm.getActiveNetworkInfo();
			if (info != null) {
				return info.isAvailable();
			}
		}
		return false;
	}

	public static String extendPath(String path) {
		String sd = Environment.getExternalStorageDirectory().getAbsolutePath()
				+ "/";
		File appDir = new File(sd + Const.APP_DIR);
		if (!appDir.exists()) {
			appDir.mkdir();
		}

		return sd + Const.APP_DIR + path;
	}

	public static String appPath() {
		return extendPath("");
	}

	public static Bitmap getBitmapByWidth(String localImagePath, int width,
			int addedScaling) {
		Bitmap temBitmap = null;
		try {
			BitmapFactory.Options outOptions = new BitmapFactory.Options();
			// 设置该属性为true，不加载图片到内存，只返回图片的宽高到options中。
			outOptions.inJustDecodeBounds = true;
			// 加载获取图片的宽高
			BitmapFactory.decodeFile(localImagePath, outOptions);
			int height = outOptions.outHeight;
			if (outOptions.outWidth > width) {
				// 根据宽设置缩放比例
				outOptions.inSampleSize = outOptions.outWidth / width + 1
						+ addedScaling;
				outOptions.outWidth = width;
				// 计算缩放后的高度
				height = outOptions.outHeight / outOptions.inSampleSize;
				outOptions.outHeight = height;
			}

			// 重新设置该属性为false，加载图片返回
			outOptions.inJustDecodeBounds = false;
			outOptions.inPurgeable = true;
			outOptions.inInputShareable = true;
			temBitmap = BitmapFactory.decodeFile(localImagePath, outOptions);
		} catch (Throwable t) {
		}

		return temBitmap;
	}

	public static Bitmap downimg(String src, boolean sample, int w, int h) {
		final String path = MD5Util.md5Encode(src);
		String absPath = Utils.extendPath(path);

		Bitmap bm = null;
		File f = new File(absPath);
		if (f.exists()) {
			bm = BitmapFactory.decodeFile(absPath);// 载入bitmap
		} else if (Utils.downloadImage(src, path)) {
			bm = BitmapFactory.decodeFile(absPath);// 载入bitmap
		}

		if (sample && bm != null) {
			if (h != 0) {
				bm = extractThumbnail(bm, w, h);
			} else {
				int rw = bm.getWidth();
				int rh = bm.getHeight();

				Log.e("Utils", "width : " + rw + ", height : " + rh);

				double s = rh / (double) rw;
				h = (int) (w * s);

				Log.e("Utils", "w : " + w + ", h : " + h);
				bm = extractThumbnail(bm, w, h);
			}
		}

		return bm;
	}

	public static Bitmap downimg(String src) {
		final String path = MD5Util.md5Encode(src);
		String absPath = Utils.extendPath(path);

		File f = new File(absPath);
		if (f.exists()) {
			Bitmap bm = BitmapFactory.decodeFile(absPath);// 载入bitmap
			return bm;
		}
		if (Utils.downloadImage(src, path)) {
			Bitmap bm = BitmapFactory.decodeFile(absPath);// 载入bitmap
			return bm;
		}

		return null;
	}

	/**
	 * 下载文件返回绝对路径
	 * 
	 * @param src
	 * @return
	 */
	public static String downfile(String src) {
		final String path = MD5Util.md5Encode(src);
		String absPath = Utils.extendPath(path);

		File f = new File(absPath);
		if (f.exists()) {
			return absPath;
		}
		if (Utils.downloadFile(src, path)) {
			return absPath;
		}

		return null;
	}

	private static Bitmap readImageLocal(String path) {
		Bitmap bitmap = null;
		try {
			File file = new File(path);
			if (file.exists()) {
				bitmap = BitmapFactory.decodeFile(path);
			}
		} catch (Exception e) {
		}

		return bitmap;
	}

	public static Bitmap readImageLocal(String path, boolean sampleSize, int w,
			int h) {
		if (!sampleSize) {
			return readImageLocal(path);
		}

		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opts);

		opts.inSampleSize = computeSampleSize(opts, -1, w * h);
		// 这里一定要将其设置回false，因为之前我们将其设置成了true
		opts.inJustDecodeBounds = false;
		try {
			Bitmap bmp = BitmapFactory.decodeFile(path, opts);
			return bmp;
		} catch (OutOfMemoryError err) {
			Log.e("Utils", "read image OutOfMemoryError");
		}

		return null;
	}

	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);

		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}

		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public static boolean saveBitmap(Bitmap bitmap, String filename) {
		File imgFile = new File(extendPath(filename));
		try {
			if (!imgFile.exists()) {
				imgFile.createNewFile();
			}
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(imgFile));
			bitmap.compress(CompressFormat.JPEG, 80, bos);
			bos.flush();
			bos.close();

			return true;
		} catch (Exception e) {
		}

		return false;
	}

	public static boolean downloadImage(String imgUrl, String path) {
		return downloadFile(imgUrl, path);
	}

	public static boolean downloadFile(String imgUrl, String path) {
		try {
			URL url = new URL(imgUrl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5 * 1000);
			conn.setRequestMethod("GET");
			if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
				return writeToFile(conn.getInputStream(), path);
			}
		} catch (Exception e) {
			return false;
		}

		return false;
	}

	private static boolean writeToFile(InputStream is, String path) {
		if (is != null) {
			try {
				File f = new File(Utils.extendPath(path));
				if (f.exists()) {
					// 已经存在了该文件了，不再下载
					return true;
				} else {
					f.createNewFile();
				}

				FileOutputStream fos = new FileOutputStream(f);
				byte[] buf = new byte[1024];
				int read = 0;
				while ((read = is.read(buf)) != -1) {
					fos.write(buf, 0, read);
					fos.flush();
				}

				fos.close();
				return true;
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	private static final int OPTIONS_NONE = 0x0;
	private static final int OPTIONS_SCALE_UP = 0x1;
	public static final int OPTIONS_RECYCLE_INPUT = 0x2;

	public static Bitmap extractThumbnail(Bitmap source, int width, int height) {
		return extractThumbnail(source, width, height, OPTIONS_NONE);
	}

	/**
	 * Creates a centered bitmap of the desired size.
	 * 
	 * @param source
	 *            original bitmap source
	 * @param width
	 *            targeted width
	 * @param height
	 *            targeted height
	 * @param options
	 *            options used during thumbnail extraction
	 */
	public static Bitmap extractThumbnail(Bitmap source, int width, int height,
			int options) {
		if (source == null) {
			return null;
		}

		float scale;
		if (source.getWidth() < source.getHeight()) {
			scale = width / (float) source.getWidth();
		} else {
			scale = height / (float) source.getHeight();
		}
		Matrix matrix = new Matrix();
		matrix.setScale(scale, scale);
		Bitmap thumbnail = transform(matrix, source, width, height,
				OPTIONS_SCALE_UP | options);
		return thumbnail;
	}

	/**
	 * Transform source Bitmap to targeted width and height.
	 */
	private static Bitmap transform(Matrix scaler, Bitmap source,
			int targetWidth, int targetHeight, int options) {
		boolean scaleUp = (options & OPTIONS_SCALE_UP) != 0;
		boolean recycle = (options & OPTIONS_RECYCLE_INPUT) != 0;

		int deltaX = source.getWidth() - targetWidth;
		int deltaY = source.getHeight() - targetHeight;
		if (!scaleUp && (deltaX < 0 || deltaY < 0)) {
			/*
			 * In this case the bitmap is smaller, at least in one dimension,
			 * than the target. Transform it by placing as much of the image as
			 * possible into the target and leaving the top/bottom or left/right
			 * (or both) black.
			 */
			Bitmap b2 = Bitmap.createBitmap(targetWidth, targetHeight,
					Bitmap.Config.ARGB_8888);
			Canvas c = new Canvas(b2);

			int deltaXHalf = Math.max(0, deltaX / 2);
			int deltaYHalf = Math.max(0, deltaY / 2);
			Rect src = new Rect(deltaXHalf, deltaYHalf, deltaXHalf
					+ Math.min(targetWidth, source.getWidth()), deltaYHalf
					+ Math.min(targetHeight, source.getHeight()));
			int dstX = (targetWidth - src.width()) / 2;
			int dstY = (targetHeight - src.height()) / 2;
			Rect dst = new Rect(dstX, dstY, targetWidth - dstX, targetHeight
					- dstY);
			c.drawBitmap(source, src, dst, null);
			if (recycle) {
				source.recycle();
			}
			return b2;
		}
		float bitmapWidthF = source.getWidth();
		float bitmapHeightF = source.getHeight();

		float bitmapAspect = bitmapWidthF / bitmapHeightF;
		float viewAspect = (float) targetWidth / targetHeight;

		if (bitmapAspect > viewAspect) {
			float scale = targetHeight / bitmapHeightF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		} else {
			float scale = targetWidth / bitmapWidthF;
			if (scale < .9F || scale > 1F) {
				scaler.setScale(scale, scale);
			} else {
				scaler = null;
			}
		}

		Bitmap b1;
		if (scaler != null) {
			// this is used for minithumb and crop, so we want to filter here.
			b1 = Bitmap.createBitmap(source, 0, 0, source.getWidth(),
					source.getHeight(), scaler, true);
		} else {
			b1 = source;
		}

		if (recycle && b1 != source) {
			source.recycle();
		}

		int dx1 = Math.max(0, b1.getWidth() - targetWidth);
		int dy1 = Math.max(0, b1.getHeight() - targetHeight);

		Bitmap b2 = Bitmap.createBitmap(b1, dx1 / 2, dy1 / 2, targetWidth,
				targetHeight);

		if (b2 != b1) {
			if (recycle || b1 != source) {
				b1.recycle();
			}
		}

		return b2;
	}

	@SuppressWarnings("deprecation")
	public static LayerDrawable getLayerDrawable(Bitmap bm, Drawable d) {
		Drawable[] array = new Drawable[2];
		array[0] = new BitmapDrawable(bm);
		array[1] = d;
		LayerDrawable la = new LayerDrawable(array);
		// 其中第一个参数为层的索引号，后面的四个参数分别为left、top、right和bottom
		la.setLayerInset(0, 0, 0, 0, 0);
		la.setLayerInset(1, 30, 17, 30, 17);

		return la;
	}

	@SuppressWarnings("deprecation")
	public static BitmapDrawable getBitmapDrawable(Bitmap bm) {
		return new BitmapDrawable(bm);
	}

	@SuppressLint("DefaultLocale")
	public static boolean checkID(String idnum) {
		String t = idnum.toUpperCase();
		if (!t.matches("^(\\d{18,18}|\\d{15,15}|\\d{17,17}X)$")) {
			return false;
		}
		@SuppressWarnings("deprecation")
		int curYear = new Date().getYear() + 1900;
		int year = Integer.parseInt(t.substring(6, 10));
		int mon = Integer.parseInt(t.substring(10, 12));
		int day = Integer.parseInt(t.substring(12, 14));
		if (!(year > 1900 && year < curYear - 16)) {
			return false;
		}
		if (!(mon > 0 && mon <= 12)) {
			return false;
		}
		if (!(day >= 1 && day <= 31)) {
			return false;
		}

		return true;
	}

	public static String calBirthdate(String idnum) {
		String year = idnum.substring(6, 10);
		String mon = idnum.substring(10, 12);
		String day = idnum.substring(12, 14);

		return year + "-" + mon + "-" + day;
	}

	public static String wrapHeader(String content) {
		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");
		builder.append("<html><head>");
		builder.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />");
		builder.append("<title></title></head><body>");
		builder.append(content);
		builder.append("</body>");
		return builder.toString();
	}

	public static String base64encode(String text) {
		if (isEmpty(text)) {
			return text;
		}

		String code = Base64.encodeToString(text.getBytes(), Base64.DEFAULT);
		return code;
	}

	public static String base64decode(String code) {
		byte[] b = Base64.decode(code, Base64.DEFAULT);
		return new String(b);
	}

	/* 判断是否是手机号 */
	public static boolean isMobileNO(String mobiles) {

		 Pattern number = Pattern.compile("^[0-9]{11}");

		if (mobiles == null || mobiles.trim().length() == 0)
			return false;
		if (mobiles.length() <= 11) {
			return number.matcher(mobiles).matches();
		}
		return false;

	}

	/* 判断是否是邮箱号 */
	public static boolean patternEmail(String emailStr) {
		String check = "^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(emailStr);
		return matcher.matches();
	}

	/* 判断是否是合法URL */
	public static boolean patternUrl(String urlStr) {
		Pattern p = Pattern
				.compile(
						"^(http|www|ftp|)?(://)?(\\w+(-\\w+)*)(\\.(\\w+(-\\w+)*))*((:\\d+)?)(/(\\w+(-\\w+)*))*(\\.?(\\w)*)(\\?)?(((\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*(\\w*%)*(\\w*\\?)*(\\w*:)*(\\w*\\+)*(\\w*\\.)*(\\w*&)*(\\w*-)*(\\w*=)*)*(\\w*)*)$",
						Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(urlStr);
		return m.matches();
	}

	/**
	 * 对WebView进行样式设置
	 * 
	 * @param content
	 * @return
	 */
	public static String wrapHtml(String content) {

		// 需要做处理，因为pre会保持样式不变
		if (content != null) {
			content = content.replaceAll("<pre>", "<p>");
			content = content.replaceAll("</pre>", "</p>");
		}

		StringBuilder builder = new StringBuilder();
		builder.append("<!DOCTYPE html><html><head><title></title>");
		builder.append("<meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0,width=device-width,user-scalable=no\">");
		builder.append("<style>");

		builder.append("p{font-size:16"
				+ "px;color:#555;line-height:32px;text-indent:2em}");
		builder.append("p{} img{ display: block; margin: 0;padding: 0;width: 100%;}");
		builder.append("</style>");
		builder.append("</head>");
		builder.append("<body><div id=\"app_content\">");
		builder.append(content == null ? "" : content);
		builder.append("</div></body></html>");

		return builder.toString();
	}

	static String regx="!|！|@|◎|#|＃|(\\$)|￥|%|％|(\\^)|……|(\\&)|※|(\\*)|×|(\\()|（|(\\))|）|_|——|(\\+)|＋|(\\|)|§";
	/**
	 * 检查输入的数据中是否有特殊字符
	 * 
	 * @param qString
	 *            要检查的数据
	 * @param regx
	 *            特殊字符正则表达式
	 * @return boolean 如果包含正则表达式<code>regx</code>中定义的特殊字符，返回true； 否则返回false
	 */
	public static boolean hasCrossScriptRisk(String qString) {
		if (qString != null) {
			qString = qString.trim();
			Pattern p = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
			Matcher m = p.matcher(qString);
			return m.find();
		}
		return false;
	}

}
