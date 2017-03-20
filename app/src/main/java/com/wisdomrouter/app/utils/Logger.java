package com.wisdomrouter.app.utils;

import android.util.Log;

public class Logger {
	private static final String TAG = "HandGaoChun";

	private static final boolean output_v = true;
	private static final boolean output_d = true;
	private static final boolean output_i = true;
	private static final boolean output_w = true;
	private static final boolean output_e = true;

	public static void v(String message) {
		if (output_v) {
			Log.v(TAG, message);
		}
	}

	public static void d(String message) {
		if (output_d) {
			Log.d(TAG, message);
		}
	}

	public static void i(String message) {
		if (output_i) {
			Log.i(TAG, message);
		}
	}

	public static void w(String message) {
		if (output_w) {
			Log.w(TAG, message);
		}
	}

	public static void e(String message) {
		if (output_e) {
			Log.e(TAG, message);
		}
	}
}
