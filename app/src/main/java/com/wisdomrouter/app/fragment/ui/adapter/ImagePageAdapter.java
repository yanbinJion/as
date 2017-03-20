package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ImageContentDao.Gallery;
import com.wisdomrouter.app.view.Phone.PhotoView;

import java.util.List;

public class ImagePageAdapter extends PagerAdapter {
	private List<Gallery> mPaths;	
	private Context mContext;
	private LayoutInflater inflater;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public ImagePageAdapter(Context cx, List<Gallery> mPaths) {
		this.mContext = cx;;
		this.mPaths = mPaths;
		inflater = LayoutInflater.from(mContext);
	}
	
	@Override
	public int getCount() {
		return mPaths == null ? 0 : mPaths.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}
	
	@Override
	public int getItemPosition(Object object) {
		return super.getItemPosition(object);
	}
	
	private PhotoView full_image;
	private TextView desc_img;
	private TextView progress_text;
	private ProgressBar progress;
	private TextView retry;
	
	@Override
	public Object instantiateItem(View container, int position) {
		View view =inflater.inflate(R.layout.activity_image_item, null);
		full_image = (PhotoView)view.findViewById(R.id.imageview);
		
		Gallery image = null;
		if(mPaths != null && mPaths.size() > 0){
			image = mPaths.get(position);
			
			imageLoader.displayImage(image.getUrl(), full_image);
			};
		

		((ViewPager)container).addView(view);
		return view;
	}
	
	@Override
	public void destroyItem(View container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}
}


