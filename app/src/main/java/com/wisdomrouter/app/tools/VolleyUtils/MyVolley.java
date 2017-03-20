package com.wisdomrouter.app.tools.VolleyUtils;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

public class MyVolley {
	private static MyVolley mInstance;
	private RequestQueue mRequestQueue;
	private ImageLoader mImageLoader;
	private Context mCtx;

	private MyVolley(Context context) {
		mCtx = context.getApplicationContext();
		mRequestQueue = getRequestQueue();

		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
					private LruCache<String, Bitmap> cache = new LruCache<String, Bitmap>(
							8 * 1024 * 1024) {
						protected int sizeOf(String key, Bitmap bitmap) {
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
								return bitmap.getByteCount() / 1024;
							}

							else {
								return (bitmap.getRowBytes() * bitmap
										.getHeight()) / 1024;
							}
						};
					};

					@Override
					public Bitmap getBitmap(String url) {
						return cache.get(url);
					}

					@Override
					public void putBitmap(String url, Bitmap bitmap) {
						if (cache.get(url) == null) {
							cache.put(url, bitmap);
						}
					}
				});
	}

	public static synchronized MyVolley getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new MyVolley(context.getApplicationContext());
		}
		return mInstance;
	}

	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(mCtx);
		}
		return mRequestQueue;
	}

	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public <T> void addToRequestQueue(Request<T> req, String tag) {
		// set the default tag if tag is empty
		req.setTag(tag);
		getRequestQueue().add(req);
	}

	public void cancelPendingRequests(Object tag) {
		getRequestQueue().cancelAll(tag);
	}
}
