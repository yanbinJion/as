package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.MyFavouriteDao;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.view.CircleImageView;

import java.util.List;


public class UserFavouriteAdapter extends BaseAdapter {

	public List<MyFavouriteDao> list;
	private Context context;
	LayoutInflater inflater;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public UserFavouriteAdapter(Context context, List<MyFavouriteDao> list) {
		super();
		this.list = list;
		this.context = context;
		inflater = LayoutInflater.from(this.context);
	}

	@Override
	public int getCount() {
		return list == null ? 0 : list.size();
	}

	@Override
	public Object getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		MyFavouriteDao comment = list.get(position);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(
					R.layout.activity_user_favourite_item, null);
			holder.tvTime = (TextView) convertView.findViewById(R.id.time);
			holder.tvContent = (TextView) convertView
					.findViewById(R.id.content);
			holder.txt_name = (TextView) convertView
					.findViewById(R.id.txt_name);
			holder.iv_tu = (ImageView) convertView.findViewById(R.id.iv_tu);
			holder.imageView1 = (CircleImageView) convertView.findViewById(R.id.imageView1);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tvContent.setText(comment.getTitle());
		if (TextUtils.isEmpty(comment.getIndexpic())) {
			imageLoader.displayImage(comment.getIndexpic(), holder.iv_tu);
			holder.iv_tu.setVisibility(View.VISIBLE);
		} else {
			holder.iv_tu.setVisibility(View.GONE);
		}
		if (TextUtils.isEmpty(HandApplication.user.getFacepic())) {
			imageLoader.displayImage(HandApplication.user.getFacepic(), holder.imageView1);
		}
		holder.txt_name.setText(HandApplication.user.getLikename());
		holder.tvTime.setText(comment.getCreatetime());

		return convertView;
	}

	public final class ViewHolder {
		private TextView txt_name;
		private TextView tvTime;
		private TextView tvContent;
		private ImageView iv_tu,imageView1; // 图片
	}

}
