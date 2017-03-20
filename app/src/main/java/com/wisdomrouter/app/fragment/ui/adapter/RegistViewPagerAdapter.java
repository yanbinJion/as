package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;

import java.util.List;

public class RegistViewPagerAdapter extends PagerAdapter {
	private List<ArticleListDao> list;
	private Context mContext;
	private LayoutInflater inflater;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public RegistViewPagerAdapter(Context mContext, List<ArticleListDao> list) {
		super();
		this.list = list;
		this.mContext = mContext;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {

		View view = inflater.inflate(R.layout.fragment_country_imgitem, null);
		ArticleListDao data = list.get(position);
		ImageView image = (ImageView) view.findViewById(R.id.town_img);

		imageLoader.displayImage(data.getIndexpic(), image);


		((ViewPager) container).addView(view);

		return view;

	}

	@Override
	public int getItemPosition(Object object) {
		return POSITION_NONE;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((View) object);
	}

}
