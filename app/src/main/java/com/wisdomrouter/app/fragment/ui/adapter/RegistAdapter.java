package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;

public class RegistAdapter extends BaseAdapter {
	public List<ArticleListDao> list;
	private Context context;
	LayoutInflater inflater;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public RegistAdapter(Context context, List<ArticleListDao> list) {
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
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		ArticleListDao child = list.get(position);

		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.fragment_project_item, null);
			holder.tvTitle = (TextView) convertView
					.findViewById(R.id.tvTitle);
			holder.imgPic = (RatioImageView) convertView.findViewById(R.id.ivHeader);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
//		if (child.getIsRead() != null && child.getIsRead()) {
//			holder.tvTitle.setTextColor(context.getResources().getColor(
//					R.color.dark_gray));
//		} else {
//			holder.tvTitle.setTextColor(context.getResources().getColor(
//					R.color.news_text_title));
//		}
		holder.tvTitle.setText(child.getTitle());

//		holder.tvbrowse.setText((child.getClick() == null ? "0" : child
//				.getClick()) + "人浏览");
//		holder.tvbrowse.setVisibility(View.VISIBLE);
		holder.imgPic.setAspectRatio(1.5f);
		holder.imgPic.setTag(child.getKey());
		imageLoader.displayImage(child.getIndexpic(), holder.imgPic);

		return convertView;
	}

	public final class ViewHolder {
		TextView tvTitle;
		RatioImageView imgPic;
	}
}
