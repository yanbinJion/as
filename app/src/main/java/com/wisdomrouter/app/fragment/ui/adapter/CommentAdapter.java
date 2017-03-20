package com.wisdomrouter.app.fragment.ui.adapter;

import java.util.List;

import com.wisdomrouter.app.fragment.bean.CommentDao;
import com.wisdomrouter.app.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CommentAdapter extends BaseAdapter{
	private Context mContext;
	private List<CommentDao> artlist;
	private LayoutInflater inflater;

	public CommentAdapter(Context context, List<CommentDao> artlist){
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
		return artlist != null ?artlist.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {		
		CommentDao item = null;
		ViewHolder viewHolde = null;
		if(null == convertView){
			viewHolde = new ViewHolder();
			convertView = inflater.inflate(R.layout.activity_comment_item, null);			
			viewHolde.tvUser = (TextView) convertView.findViewById(R.id.txt_review_user);
			viewHolde.tvTime = (TextView) convertView.findViewById(R.id.txt_review_time);
			viewHolde.tvContent = (TextView) convertView.findViewById(R.id.txt_review_content);
			convertView.setTag(viewHolde);
		}else{
			viewHolde = (ViewHolder) convertView.getTag();
		}

		if(artlist != null && artlist.size() > 0){
			item = artlist.get(position);

			viewHolde.tvUser.setText(item.getLikename());
			viewHolde.tvTime.setText(item.getCreatetime());
			viewHolde.tvContent.setText(item.getContent());
		}
		return convertView;
	}

	class ViewHolder{
		TextView tvUser;
		TextView tvTime;
		TextView tvContent;
	}
}

