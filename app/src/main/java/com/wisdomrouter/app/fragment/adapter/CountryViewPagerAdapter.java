package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CountryListDao;

import java.util.List;

public class CountryViewPagerAdapter extends PagerAdapter {
    private List<CountryListDao.Townpic> list;
    private LayoutInflater inflater;
    private Context mContext;

    public CountryViewPagerAdapter(List<CountryListDao.Townpic> list, Context mContext) {
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
    public Object instantiateItem(ViewGroup container, int position) {
        View view = inflater.inflate(R.layout.fragment_country_imgitem, null);
        ImageView image = (ImageView) view.findViewById(R.id.town_img);
        if (list != null && list.size() != 0) {
            if (list.get(position).getPic() != null) {
                Glide.with(mContext).load(list.get(position).getPic()).into(image);
            }
        }

        ((ViewPager) container).addView(view);

        return view;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
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
