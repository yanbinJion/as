package com.wisdomrouter.app.tools.VolleyUtils;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;
import com.wisdomrouter.app.Const;

public class GsonRequestPost<T> extends Request<T> {

	private Listener<T> mListener;

	private Gson mGson;

	private Class<T> mClass;
	private static Map<String, String> param = new HashMap<String, String>();

	public GsonRequestPost(int method, String url, Class<T> clazz,
			Listener<T> listener, StrErrListener errorListener,
			Map<String, String> params) {
		super(method, url, errorListener);
		mGson = new Gson();
//		����Gson����û��@Exposeע�͵����Խ����ᱻ���л�
//		mGson =new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
		mClass = clazz;
		mListener = listener;
		param = params;
	}

	public GsonRequestPost(String url, Class<T> clazz, Listener<T> listener,
			StrErrListener errorListener, Map<String, String> params) {
		this(Method.POST, url, clazz, listener, errorListener, params);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String jsonString = new String(response.data,
					HttpHeaderParser.parseCharset(response.headers));
			// �滻ͷβ����[],��ֹ����jsonArray
			if (jsonString.endsWith("\\]")) {
				String s1 = jsonString.substring(0,
						jsonString.lastIndexOf("\\]"));
				jsonString = s1 + "\\}";
			}
			if (jsonString.startsWith("\\[", 0)) {
				jsonString = jsonString.replaceAll("\\[", "\\{");
			}
			return Response.success(mGson.fromJson(jsonString, mClass),
					HttpHeaderParser.parseCacheHeaders(response));
 
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
	protected Map<String, String> getParams() {
		Map<String, String> params = new HashMap<String, String>();
		if (param != null) {
			params = param;
		}
		return params;
	}

	@Override
	protected void deliverResponse(T response) {
		mListener.onResponse(response);
	}

}