package com.wisdomrouter.app.fragment.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdomrouter.app.fragment.bean.DisCloseListDao.Replyinfo;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.R;

public class BaoliaoReplyAdapter extends BaseAdapter {
	private List<Replyinfo> list;
	private Context mContext;
	private LayoutInflater inflater;

	public BaoliaoReplyAdapter(List<Replyinfo> list, Context mContext) {
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
		Replyinfo info = list.get(position);
		ViewHolder viewHolder = new ViewHolder();
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.baoliao_reply_item, parent,
					false);
			viewHolder.tv_time = (TextView) convertView
					.findViewById(R.id.reply_time);
			viewHolder.tv_content = (TextView) convertView
					.findViewById(R.id.reply_content);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		if (info != null) {
			viewHolder.tv_time
					.setText(TimeUtil.getStrDate2(info.getReplytime()));
			viewHolder.tv_content.setText(info.getReplycontent() == null ? ""
					: info.getReplycontent());
		}
		return convertView;
	}

	class ViewHolder {
		TextView tv_time;
		TextView tv_content;
	}
}
