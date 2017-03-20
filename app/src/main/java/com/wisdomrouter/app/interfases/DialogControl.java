package com.wisdomrouter.app.interfases;

import com.wisdomrouter.app.view.ProgressDialog;


public interface DialogControl {

	public abstract void hideProgressDialog();

	public abstract ProgressDialog showProgressDialog();

	public abstract ProgressDialog showProgressDialog(int resid);

	public abstract ProgressDialog showProgressDialog(String text);
	public abstract ProgressDialog showProgressDialog(String text,String type);
}
