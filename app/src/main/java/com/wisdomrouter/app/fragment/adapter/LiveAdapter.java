package com.wisdomrouter.app.fragment.adapter;

import java.util.List;

import android.R.color;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.wisdomrouter.app.fragment.bean.LiveListDao;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.R;

public class LiveAdapter extends BaseAdapter {
	private Context mContext;
	private List<LiveListDao> artlist;
	private LayoutInflater inflater;

	public LiveAdapter(Context context, List<LiveListDao> artlist) {
		this.mContext = context;
		this.artlist = artlist;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return artlist != null ? artlist.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return artlist != null ? artlist.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@SuppressLint("ResourceAsColor") @Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolde _holder;
		LiveListDao art = artlist.get(position);
		if (convertView == null) {
			_holder = new ViewHolde();
			convertView = inflater.inflate(R.layout.fragment_live_item, null);
			// 标题+简介+左图
			_holder.pic_title = (TextView) convertView
					.findViewById(R.id.pic_title);
			_holder.pic_content = (TextView) convertView
					.findViewById(R.id.pic_content);
			_holder.txt_day = (TextView) convertView.findViewById(R.id.txt_day);
			_holder.txt_month = (TextView) convertView
					.findViewById(R.id.txt_month);
			_holder.txt_zb = (TextView) convertView
					.findViewById(R.id.txt_zb);
			_holder.vv =(View)convertView
					.findViewById(R.id.vv);
			convertView.setTag(_holder);
		} else {
			_holder = (ViewHolde) convertView.getTag();
		}
		if (art != null) {
			if (art.getLivestate()==0) {
				_holder.txt_zb.setText("直播中");
				_holder.txt_zb.setBackgroundResource(R.drawable.shape_rectangle_lanmu_yellow);
				_holder.txt_zb.setTextColor(Color.parseColor("#ffffff"));
				_holder.vv.setBackgroundResource(R.color.tv_living_list_view);
			}
			else {
				_holder.txt_zb.setText("已结束");
				_holder.txt_zb.setBackgroundResource(R.drawable.bg_live_end);
				_holder.txt_zb.setTextColor(Color.parseColor("#696161"));
				_holder.vv.setBackgroundResource(R.color.tv_living_list_un_view);
			}
			_holder.pic_title.setText(art.getTitle());
			_holder.pic_content.setText(art.getContent());
			_holder.txt_day.setText(TimeUtil.getDay2(art.getCreatetime()));
			_holder.txt_month.setText(TimeUtil.getmonth(art.getCreatetime()));
			if (art.getIsRead() != null && art.getIsRead()) {
				_holder.pic_title.setTextColor(mContext.getResources()
						.getColor(R.color.dark_gray));
			}
		}

		return convertView;
	}

	class ViewHolde {

		// 标题+简介+时间
		TextView pic_title;
		TextView pic_content;
		TextView txt_month;
		TextView txt_day;
		TextView txt_zb;
		View vv;
	}
}
