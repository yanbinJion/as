package com.wisdomrouter.app.fragment.adapter;

import java.util.List;

import com.google.gson.Gson;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ServiceColumnAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private List<AppConfigBean.Module.Channel> list;
	private Context context;
	private Activity activity;

	public ServiceColumnAdapter(Activity ac, Context c, List<Channel> l) {
		mInflater = LayoutInflater.from(c);
		context = c;
		activity = ac;
		list = l;
		
	}
	
	public void addItems(AppConfigBean.Module.Channel entity)
	{
		list.add(entity);

		HandApplication.getInstance().mSpUtil.saveUnReadcolumn(new Gson().toJson(list));



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
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		AppConfigBean.Module.Channel entity = list.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.column_edit_top, null);

			viewHolder = new ViewHolder();
			viewHolder.tvNewsId = (TextView) convertView.findViewById(R.id.column_tv_id);
			viewHolder.tvNewsTitle = (TextView) convertView.findViewById(R.id.column_tv_newstitle);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		viewHolder.tvNewsId.setTag(position);
		viewHolder.tvNewsId.setText(entity.getKey());
		if (entity.getName().length()>3){
			viewHolder.tvNewsTitle.setTextSize(12);
		}
		viewHolder.tvNewsTitle.setText(entity.getName());

		return convertView;
	}

	static class ViewHolder {
		public TextView tvNewsId;
		public TextView tvNewsTitle;
	}
}
