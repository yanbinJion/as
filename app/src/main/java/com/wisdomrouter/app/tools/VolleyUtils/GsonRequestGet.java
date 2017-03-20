package com.wisdomrouter.app.tools.VolleyUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.Listener;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.wisdomrouter.app.Const;

public class GsonRequestGet<T> extends Request<T> {

	private Listener<T> mListener;

	private Gson mGson;

	private Class<T> mClass;

	private Type mTpye;

	public GsonRequestGet(int method, String url, Class<T> clazz,
			Listener<T> listener, StrErrListener errorListener) {
		super(method, url, errorListener);
		mGson = new Gson();
		mClass = clazz;
		mListener = listener;
	}

	public GsonRequestGet(int method, String url, Type tpye,
			Listener<T> listener, StrErrListener errorListener) {
		super(method, url, errorListener);
		mGson = new Gson();
		mTpye = tpye;
		mListener = listener;
	}

	public GsonRequestGet(String url, Class<T> clazz, Listener<T> listener,
			StrErrListener errorListener) {

		this(Method.GET, url, clazz, listener, errorListener);
	}

	public GsonRequestGet(String url, Type tpye, Listener<T> listener,
			StrErrListener errorListener) {

		this(Method.GET, url, tpye, listener, errorListener);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			if (mClass != null) {
				
				return  (Response<T>)Response.success(mGson.fromJson(jsonString, mClass),
						HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return (Response<T>) Response.success(
						mGson.fromJson(jsonString, mTpye),
						HttpHeaderParser.parseCacheHeaders(response));
			}

		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError(e));
		}
	}

	// ���ͷ��
	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("token", Const.APPTOKEN);
		return headers;
	}

	// ���ó�ʱʱ��
	@Override
	public RetryPolicy getRetryPolicy() {
		RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
				DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
				DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
		return retryPolicy;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

}