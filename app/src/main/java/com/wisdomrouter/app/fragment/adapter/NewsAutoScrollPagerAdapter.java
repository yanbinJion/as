package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.NetworkImageView;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;

import java.util.List;

public class NewsAutoScrollPagerAdapter extends PagerAdapter {

    private List<String> imageUrls;
    private Context mContext;

    public NewsAutoScrollPagerAdapter(List<String> imageUrls, Context context) {
        this.mContext = context;
        this.imageUrls = imageUrls;
    }

    @Override
    public int getCount() {
        if (imageUrls != null) {
            return imageUrls.size();
        }
        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        NetworkImageView networkImageView = new NetworkImageView(mContext);
        networkImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        networkImageView.setImageUrl(imageUrls.get(position),
                MyVolley.getInstance(mContext).getImageLoader());
        container.addView(networkImageView);
        return networkImageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

}
