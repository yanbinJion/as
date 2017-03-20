package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class ArimageAdapter extends BaseAdapter {
    private List<Channel> list;
    private Context mContext;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public ArimageAdapter(List<Channel> list, Context mContext) {
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
        return list == null ? null : list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder _viewHold = null;
        if (convertView == null) {
            _viewHold = new ViewHolder();
            convertView = inflater.inflate(R.layout.fragment_arimage_item,
                    null);
            _viewHold.tv = (RatioImageView) convertView.findViewById(R.id.town_img);
            _viewHold.tv.setAspectRatio(1.0f);
            _viewHold.name = (TextView) convertView.findViewById(R.id.town_name);
            convertView.setTag(_viewHold);
        } else {
            _viewHold = (ViewHolder) convertView.getTag();
        }
        Channel listDao = list.get(position);
        if (listDao != null) {
            imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(listDao.getIndexpic(), "100", "100"), _viewHold.tv);
            _viewHold.name.setText(listDao.getName() == null ? "" : listDao.getName());
        }

        return convertView;
    }

    class ViewHolder {
        RatioImageView tv;
        TextView name;
    }

}
