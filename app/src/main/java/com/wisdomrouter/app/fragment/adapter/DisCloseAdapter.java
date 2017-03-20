package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.DisCloseListDao;

import java.util.List;

public class DisCloseAdapter extends BaseAdapter {
    private List<DisCloseListDao> list;
    private Context mContext;
    private LayoutInflater inflater;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    public DisCloseAdapter(List<DisCloseListDao> list, Context mContext) {
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder _viewHolder;
        DisCloseListDao disDao = list.get(position);
        if (convertView == null) {
            _viewHolder = new ViewHolder();
            convertView = inflater
                    .inflate(R.layout.activity_baoliao_item, null);
            _viewHolder.ll_top = (LinearLayout) convertView
                    .findViewById(R.id.ll_top);
            _viewHolder.tv_time = (TextView) convertView
                    .findViewById(R.id.tv_broketime);
            _viewHolder.tv_title = (TextView) convertView
                    .findViewById(R.id.tv_disclosetitle);
            _viewHolder.img_disclose = (ImageView) convertView
                    .findViewById(R.id.iv_disclose);
            _viewHolder.iv_used = (ImageView) convertView
                    .findViewById(R.id.iv_used);
            _viewHolder.rl_content = (RelativeLayout) convertView
                    .findViewById(R.id.ll_content);
            convertView.setTag(_viewHolder);
        } else {
            _viewHolder = (ViewHolder) convertView.getTag();
        }
        _viewHolder.tv_time.setText(disDao.getCreatetime() == null ? ""
                : disDao.getCreatetime());
        _viewHolder.tv_title.setText(disDao.getTitle() == null ? "" : disDao
                .getTitle());
        if (disDao.getImages() == null || disDao.getImages().length == 0
                || disDao.getImages()[0] == null) {
            _viewHolder.rl_content.setVisibility(View.GONE);
        } else {
            _viewHolder.rl_content.setVisibility(View.VISIBLE);
            imageLoader.displayImage(disDao.getImages()[0],
                    _viewHolder.img_disclose);
        }
        if (disDao.getState() == 1) {
            _viewHolder.ll_top.setVisibility(View.GONE);
            _viewHolder.iv_used.setVisibility(View.VISIBLE);
        } else {
            _viewHolder.ll_top.setVisibility(View.VISIBLE);
            _viewHolder.iv_used.setVisibility(View.GONE);

        }
        return convertView;
    }

    class ViewHolder {
        ImageView img_disclose, iv_used;
        TextView tv_time;
        TextView tv_title;
        LinearLayout ll_top;
        RelativeLayout rl_content;
    }
}
