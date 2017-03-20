package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.UserDao;

import java.util.List;

/**
 * Created by Administrator on 2016/1/29.
 */
public class StatusAdapter extends BaseAdapter {

    private Context mContext;
    private List<UserDao.Userinfo.UserGroup> data;
    private LayoutInflater inflater;

    public StatusAdapter(Context mContext, List<UserDao.Userinfo.UserGroup> data) {
        this.mContext = mContext;
        this.data = data;
        inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.size();
    }

    @Override
    public Object getItem(int position) {
        return data == null ? null : data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_status_item,parent,false);
            holder = new ViewHolder();
            holder.status = (TextView) convertView.findViewById(R.id.item_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UserDao.Userinfo.UserGroup group = data.get(position);
        if (group != null) {
            holder.status.setText(group.getName() == null ? "" : group.getName());
        }
        return convertView;
    }

    class ViewHolder {
        TextView status;
    }
}
