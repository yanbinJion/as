
package com.wisdomrouter.app.fragment.ui.adapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.wisdomrouter.app.fragment.bean.ImageInfo;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.view.RatioImageView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class GridViewAdapter extends BaseAdapter {

    protected List<ImageInfo> mList;
    protected Context mContext;

    public GridViewAdapter(Context context, List<ImageInfo> mList) {
        this.mContext = context;
        this.mList = mList;
    }

    @Override
    public int getCount() {
        if (mList != null)
            return mList.size();
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return mList == null ? null : mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // 获取源图片
    public ArrayList<String> getSourceList() {
        ArrayList<String> ret = new ArrayList<String>();
        for (ImageInfo info : mList) {
            if (!info.isAddButton) {
                ret.add(info.getSource_image());
            }
        }
        return ret;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.item_published_grida, parent, false);
            holder = new ViewHolder();
            holder.image = (RatioImageView) convertView
                    .findViewById(R.id.item_grida_image);
            holder.image.setAspectRatio(1f);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ImageInfo info = mList.get(position);
        if (info != null) {

            if (!info.isAddButton()) {
                File imageFile = new File(info.getSource_image());
                Picasso.with(mContext).load(imageFile)
                        .placeholder(R.drawable.camerasdk_pic_loading)
                        .error(R.drawable.camerasdk_pic_loading).resize(70, 70)
                        .centerCrop().into(holder.image);
            } else {

                Picasso.with(mContext).load(R.drawable.icon_addpic_focused)
                        .placeholder(R.drawable.camerasdk_pic_loading)
                        .error(R.drawable.camerasdk_pic_loading).resize(200, 200)
                        .centerCrop().into(holder.image);
            }
        }

        return convertView;
    }

    class ViewHolder {

        public RatioImageView image;

    }


}