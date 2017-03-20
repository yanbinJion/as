package com.wisdomrouter.app.view;

import com.wisdomrouter.app.interfases.DialogControl;
import com.wisdomrouter.app.utils.TDevice;
import com.wisdomrouter.app.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class ProgressDialog extends Dialog {

	private TextView _messageTv;

	public ProgressDialog(Context context) {
		super(context);
		init(context);
	}

	public ProgressDialog(Context context, int defStyle) {
		super(context, defStyle);
		init(context);
	}
	public ProgressDialog(Context context, int defStyle,String type) {
		super(context, defStyle);
		init(context,type);
	}

	protected ProgressDialog(Context context, boolean cancelable,
			DialogInterface.OnCancelListener listener) {
		super(context, cancelable, listener);
		init(context);
	}

	public static boolean dismiss(ProgressDialog dialog) {
		if (dialog != null) {
			dialog.dismiss();
			return false;
		} else {
			return true;
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.dismiss();
	}

	public static void hide(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).hideProgressDialog();
	}

	public static boolean hide(ProgressDialog dialog) {
		if (dialog != null) {
			dialog.hide();
			return false;
		} else {
			return true;
		}
	}

	private void init(Context context,String type) {
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_progress_vertical, null);
		_messageTv = (TextView) view.findViewById(R.id.waiting_tv);
		setContentView(view);
	}
	private void init(Context context) {
		setCancelable(false);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(context).inflate(
				R.layout.dialog_progress, null);
		_messageTv = (TextView) view.findViewById(R.id.waiting_tv);
		setContentView(view);
	}

	public static void show(Context context) {
		if (context instanceof DialogControl)
			((DialogControl) context).showProgressDialog();
	}

	public static boolean show(ProgressDialog waitdialog) {
		boolean flag;
		if (waitdialog != null) {
			waitdialog.show();
			flag = false;
		} else {
			flag = true;
		}
		return flag;
	}

	@Override
	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		if (TDevice.isTablet()) {
			int i = (int) TDevice.dpToPixel(360F);
			if (i < TDevice.getScreenWidth()) {
				WindowManager.LayoutParams params = getWindow().getAttributes();
				params.width = i;
				getWindow().setAttributes(params);
			}
		}
	}

	public void setMessage(int message) {
		_messageTv.setText(message);
	}

	public void setMessage(String message) {
		_messageTv.setText(message);
	}

	public void hideMessage() {
		_messageTv.setVisibility(View.GONE);
	}
}