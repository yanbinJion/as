package com.wisdomrouter.app.tools;

import java.io.UnsupportedEncodingException;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.Gson;



 /**
  * �Զ���  Gson ���󷽷�
  * @author yuan
  * http://blog.csdn.net/guolin_blog/article/details/17612763
  * @param <T>
  */
public class GsonRequest<T> extends Request<T> {

	private Listener<T> glistener;
	
	private Gson gson;
	
	private Class<T> gClass;
	
	/**
	 * ���캯�� �������ж����ͬ�����ģ�����Ͳ�����ˣ�
	 * @param method
	 * @param url
	 * @param listener
	 */
	public GsonRequest(int method, String url,Class<T> clazz,Listener<T> listener,ErrorListener errorlistener) {
		super(method, url, errorlistener);
		//��ʼ�� ����
		gson=new Gson();
		gClass=clazz;
		glistener=listener;
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			//���ַ���ת���ַ������������� �ַ����� ��������Ӧ��Ϣ�ı��Ķ�����Ϣ
			String jsonString=new String(response.data,HttpHeaderParser.parseCharset(response.headers));
		
			//������Ϣ ʹ�� gson ֱ��ת ���󣬵ڶ������� ���ñ���
			return Response.success(gson.fromJson(jsonString, gClass),HttpHeaderParser.parseCacheHeaders(response));
			
		
		} catch (UnsupportedEncodingException e) {
			// �����ʱ�򣬽�������Ϣ���µ���
			return Response.error(new ParseError(e));
		}
	}

	@Override
	protected void deliverResponse(T response) {
		// TODO Auto-generated method stub
       glistener.onResponse(response);
	}

}
