package com.wisdomrouter.app.fragment.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;

public class BaoliaoImageAdapter extends BaseAdapter {
    private List<String> list;
    private Context mContext;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public BaoliaoImageAdapter(List<String> list, Context mContext) {
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
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressWarnings("null")
    @SuppressLint("ViewHolder")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder _viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_published_grida, parent,
                    false);
            _viewHolder = new ViewHolder();
            _viewHolder.img = (RatioImageView) convertView
                    .findViewById(R.id.item_grida_image);
            _viewHolder.img.setAspectRatio(1.5f);
            convertView.setTag(_viewHolder);
        } else {
            _viewHolder = (ViewHolder) convertView.getTag();
        }

        imageLoader.displayImage(list.get(position), _viewHolder.img);


        return convertView;
    }

    class ViewHolder {
        RatioImageView img;
    }
}
