package com.wisdomrouter.app.utils;


import com.wisdomrouter.app.view.ProgressDialog;
import com.wisdomrouter.app.R;

import android.app.Activity;

public class DialogHelper {

	public static ProgressDialog getProgressDialog(Activity activity,
			int message) {
		ProgressDialog dialog = null;
		try {
			dialog = new ProgressDialog(activity, R.style.dialog_progress);
			dialog.setMessage(message);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dialog;
	}

	public static ProgressDialog getProgressDialog(Activity activity,
			String message) {
		ProgressDialog dialog = null;
		try {
			dialog = new ProgressDialog(activity, R.style.dialog_progress);
			dialog.setMessage(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dialog;
	}
	public static ProgressDialog getProgressDialog(Activity activity,
			String message,String type) {
		ProgressDialog dialog = null;
		try {
			dialog = new ProgressDialog(activity, R.style.dialog_progress,type);
			dialog.setMessage(message);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dialog;
	}

	public static ProgressDialog getCancelableProgressDialog(Activity activity,
			String message) {
		ProgressDialog dialog = null;
		try {
			dialog = new ProgressDialog(activity, R.style.dialog_progress);
			dialog.setMessage(message);
			dialog.setCancelable(true);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return dialog;
	}
}
