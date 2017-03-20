package com.wisdomrouter.app.tools;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request.Method;
import com.android.volley.RetryPolicy;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;


public class VolleyHttpRequest {


    /**
     * 1.
     * StringRequest GET��ʽ
     *
     * @param url           ��ַ
     * @param volleyRequest �ص�����
     */
    public static void String_request(String url, VolleyHandler<String> volleyRequest) {
        Volley_StringRequest(Method.GET, url, null, volleyRequest);
    }

    /**
     * 1.
     * StringRequset POST��ʽ
     *
     * @param url           ��ַ
     * @param map           ����
     * @param volleyRequest �ص�����
     */
    public static void String_request(String url, final Map<String, String> map, VolleyHandler<String> volleyRequest) {
        Volley_StringRequest(Method.POST, url, map, volleyRequest);
    }

    /**
     * 1.
     * ��װ StringRequest ��������
     *
     * @param method        ��ʽ
     * @param url           ��ַ
     * @param params        ����
     * @param volleyRequest �ص�����
     */
    private static void Volley_StringRequest(int method, String url, final Map<String, String> params, VolleyHandler<String> volleyRequest) {
        StringRequest stringrequest = new StringRequest(method, url, volleyRequest.reqLis, volleyRequest.reqErr) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // TODO Auto-generated method stub
                return params;
            }

            //添加头部
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.APPTOKEN);
                return headers;
            }

            //添加超时时间
            @Override
            public RetryPolicy getRetryPolicy() {
                RetryPolicy retryPolicy = new DefaultRetryPolicy(6000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };

        stringrequest.setTag("stringrequest");
        HandApplication.getQueue().add(stringrequest);
    }


    /**
     * 2.
     * JsonObjectRequest GET ����
     *
     * @param url           �����ַ
     * @param volleyRequest �ص���������
     */
    public static void JsonObject_Request(String url, VolleyHandler<JSONObject> volleyRequest) {
        Volley_JsonObjectRequest(Method.GET, url, null, volleyRequest);
    }

    /**
     * 2.
     * JsonObjectRequest POST ����
     *
     * @param url           �����ַ
     * @param jsonObject    �������
     * @param volleyRequest �ص���������
     */
    public static void JsonObject_Request(String url, JSONObject jsonObject, VolleyHandler<JSONObject> volleyRequest) {
        Volley_JsonObjectRequest(Method.POST, url, jsonObject, volleyRequest);
    }

    /**
     * 2.
     * ��װ JsonObjectRequest ���󷽷�
     *
     * @param method        ��ʽ
     * @param url           ��ַ
     * @param jsonObject    ����
     * @param volleyRequest �ص���������
     */
    private static void Volley_JsonObjectRequest(int method, String url, JSONObject jsonObject, VolleyHandler<JSONObject> volleyRequest) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(method, url, jsonObject, volleyRequest.reqLis, volleyRequest.reqErr){
            //添加头部
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.APPTOKEN);
                return headers;
            }

            //添加超时时间
            @Override
            public RetryPolicy getRetryPolicy() {
                RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };
        jsonObjectRequest.setTag("jsonObjectRequest");
        HandApplication.getQueue().add(jsonObjectRequest);
    }


    /**
     * 3.
     * ImageRequest Ĭ�ϴ�С ԭͼ����
     *
     * @param url           ��ַ
     * @param volleyRequest �ص�����
     */
    public static void Image_request(String url, VolleyHandler<Bitmap> volleyRequest) {
        Volley_ImageRequest(url, 0, 0, volleyRequest);
    }

    /**
     * 3.
     * ImageRequest �Զ��������
     *
     * @param url           ��ַ
     * @param maxWidth      �����
     * @param maxHeight     ���߶�
     * @param volleyRequest �ص�����
     */
    public static void Image_request(String url, int maxWidth, int maxHeight, VolleyHandler<Bitmap> volleyRequest) {
        Volley_ImageRequest(url, maxWidth, maxHeight, volleyRequest);
    }


    /**
     * 3.
     * ��װ ImageRequest ���󷽷�
     *
     * @param url           ��ַ
     * @param maxWidth      �����
     * @param maxHeight     ���߶�
     * @param volleyRequest �ص���������
     */
    private static void Volley_ImageRequest(String url, int maxWidth, int maxHeight, VolleyHandler<Bitmap> volleyRequest) {
        ImageRequest imageRequest = new ImageRequest(url, volleyRequest.reqLis, maxWidth, maxHeight, Config.RGB_565, volleyRequest.reqErr){
            //添加头部
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("token", Const.APPTOKEN);
                return headers;
            }

            //添加超时时间
            @Override
            public RetryPolicy getRetryPolicy() {
                RetryPolicy retryPolicy = new DefaultRetryPolicy(5000,
                        DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                        DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
                return retryPolicy;
            }
        };
        imageRequest.setTag("imageRequest");
        HandApplication.getQueue().add(imageRequest);
    }


    /**
     * 4.
     * �Զ���ͼƬ�Ŀ��ֵ
     *
     * @param url
     * @param imageListener
     * @param maxWidth
     * @param maxHidth
     */
    public static void Image_Loader(String url, ImageListener imageListener, int maxWidth, int maxHidth) {
        Volley_ImageLoader(url, imageListener, maxWidth, maxHidth);
    }


    /**
     * 4.
     * Ĭ��ֵ��ԭʼ����
     *
     * @param url           ��ַ
     * @param imageListener ͼƬ����
     */
    public static void Image_Loader(String url, ImageListener imageListener) {
        Volley_ImageLoader(url, imageListener, 0, 0);
    }


    /**
     * 4.
     * ��װ ImageLoader ����
     *
     * @param url           ��ַ
     * @param imageListener ͼƬ����
     * @param maxWidth
     * @param maxHidth
     */
    private static void Volley_ImageLoader(String url, ImageListener imageListener, int maxWidth, int maxHidth) {
        // ���� ͼƬ���� :���� imageLoader������
        // ʹ�� LruBitmap + ImageCache ʵ��
        // ʵ��������
        ImageLoader imageLoader = new ImageLoader(HandApplication.getQueue(),
                new VolleyBitmapCache());
        // ����ͼƬ ͼƬ���� ��Ĭ��ͼƬ������ͼƬ�� �� imageView
        imageLoader.get(url, imageListener, maxWidth, maxHidth);
    }



}
