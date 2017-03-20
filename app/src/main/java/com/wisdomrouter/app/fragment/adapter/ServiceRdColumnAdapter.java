package com.wisdomrouter.app.fragment.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.view.BaseGridView;

import java.util.List;

public class ServiceRdColumnAdapter extends BaseAdapter {

    List<Channel> list;
    Activity activity;
    int currentIndex;
    ServiceColumnAdapter adapter;
    private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示
    private LayoutInflater mInflater;

    public ServiceRdColumnAdapter(Activity ac, BaseGridView gv,
                                  List<Channel> l, ServiceColumnAdapter at, boolean isShowDelete, int currentIndex) {
        adapter = at;
        activity = ac;
        list = l;
        this.isShowDelete = isShowDelete;
        this.currentIndex = currentIndex;
        mInflater = LayoutInflater.from(activity);
    }

    public void setIsShowDelete(boolean isShowDelete) {
        this.isShowDelete = isShowDelete;
        this.notifyDataSetChanged();
    }


    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        Channel entity = list.get(position);
//        if (convertView == null) {
        convertView = mInflater.inflate(R.layout.column_edit_items, null);

        viewHolder = new ViewHolder();
        viewHolder.tvNewsId = (TextView) convertView.findViewById(R.id.column_tv_id);
        viewHolder.tvNewsTitle = (TextView) convertView.findViewById(R.id.column_tv_newstitle);
        viewHolder.deleteView = (ImageView) convertView
                .findViewById(R.id.delete_markView);
        if (position == 0) {
            viewHolder.deleteView.setVisibility(View.GONE);
            if (position == currentIndex) {
                viewHolder.tvNewsTitle.setTextColor(activity.getResources()
                        .getColor(R.color.app_lighttopbg));
            } else {
                viewHolder.tvNewsTitle.setTextColor(activity.getResources()
                        .getColor(R.color.tv_collect_time));
            }
        } else {
            if (position == currentIndex) {
                viewHolder.tvNewsTitle.setTextColor(activity.getResources()
                        .getColor(R.color.app_lighttopbg));
            }
            viewHolder.deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
        }


        convertView.setTag(viewHolder);

//        } else {
//            viewHolder = (ViewHolder) convertView.getTag();
//        }
        viewHolder.deleteView.setTag(position);
        viewHolder.tvNewsId.setTag(position);
        viewHolder.tvNewsId.setText(entity.getKey());
        if (entity.getName().length() > 3) {
            viewHolder.tvNewsTitle.setTextSize(12);
        }
        viewHolder.tvNewsTitle.setText(entity.getName());
        return convertView;
    }


    public void showDelColumnAnim(View v, int currentIndex) {
        int index = Integer.parseInt(v.getTag().toString());
        if (index == 0) return;
        if (currentIndex > list.size() - 1 || currentIndex == list.size() - 1) {
            currentIndex = list.size() - 2;
        }
        this.currentIndex = currentIndex;
        adapter.addItems(list.get(index));
        this.deleteItem(index);

    }

    public int getCurrentIndex() {
        return currentIndex;
    }

    public void deleteItem(int itemIndex) {
        list.remove(itemIndex);
        HandApplication.getInstance().mSpUtil.saveReadcolumn(new Gson().toJson(list));
        this.notifyDataSetChanged();
    }


    static class ViewHolder {
        public TextView tvNewsId;
        public TextView tvNewsTitle;
        public ImageView deleteView;
    }
}
