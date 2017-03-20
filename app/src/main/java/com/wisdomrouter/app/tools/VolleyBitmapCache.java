package com.wisdomrouter.app.tools;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.util.LruCache;
import com.android.volley.toolbox.ImageLoader.ImageCache;

@SuppressLint("NewApi")
public class VolleyBitmapCache implements ImageCache{

	//ʹ��LruCache ʵ��ͼƬ���� ��
	//ʹ�õ�ַ��


	private LruCache<String,Bitmap> cache;
	//�������� �ߴ�ֵ
	
	public VolleyBitmapCache() {
		//���췽�� ʵ�� LruCache ���� ͼƬ
		
		int maxSize=10*1024*1024;
		cache=new LruCache<String,Bitmap>(maxSize){
			@Override
			protected int sizeOf(String key, Bitmap value) {
				return value.getRowBytes()*value.getHeight();
			}
		};
		
	}
	
	@Override
	public Bitmap getBitmap(String url) {
		// �õ�
		return cache.get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		// ����
		cache.put(url, bitmap);
	}

}
