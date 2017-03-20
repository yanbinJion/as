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
import com.wisdomrouter.app.fragment.bean.CountryMoreNewsDao;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.Utils;

import java.util.List;

public class MoreAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater inflater;
	private List<CountryMoreNewsDao> mData;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public MoreAdapter(Context mContext, List<CountryMoreNewsDao> mData) {
		this.mContext = mContext;
		this.mData = mData;
		inflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mData != null ? mData.size() : 0;
	}

	@Override
	public Object getItem(int position) {
		return mData != null ? mData.get(position) : null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolde _holder;
		CountryMoreNewsDao more = mData.get(position);
		if (convertView == null) {
			_holder = new ViewHolde();
			convertView = inflater.inflate(R.layout.fragment_country_item,
					parent, false);
			// 标题+简介
			_holder.intro_view = (LinearLayout) convertView
					.findViewById(R.id.intro_view);
			_holder.intro_title = (TextView) convertView
					.findViewById(R.id.intro_title);
//			_holder.intro_content = (TextView) convertView
//					.findViewById(R.id.intro_content);
			_holder.intro_time = (TextView) convertView
					.findViewById(R.id.intro_time);
			// 标题+简介+右图
			_holder.pic_view = (RelativeLayout) convertView
					.findViewById(R.id.pic_view);
			_holder.pic_title = (TextView) convertView
					.findViewById(R.id.pic_title);
//			_holder.pic_content = (TextView) convertView
//					.findViewById(R.id.pic_content);
			_holder.pic_img = (ImageView) convertView
					.findViewById(R.id.pic_img);
			_holder.pic_time = (TextView) convertView
					.findViewById(R.id.pic_time);
			convertView.setTag(_holder);
		} else {
			_holder = (ViewHolde) convertView.getTag();
		}

		if (more != null) {
			if (StringUtil.isEmpty(more.getNewspic())
					|| !Utils.patternUrl(more.getNewspic())) {
				_holder.intro_view.setVisibility(View.VISIBLE);
				_holder.pic_view.setVisibility(View.GONE);
				_holder.intro_title.setText(more.getNewstitle() == null ? ""
						: more.getNewstitle());
//				_holder.intro_content.setText(more.getNwscontent() == null ? ""
//						: more.getNwscontent());
				_holder.intro_time.setText(more.getNwstime() == null ? ""
						: more.getNwstime());
			} else {
				_holder.intro_view.setVisibility(View.GONE);
				_holder.pic_view.setVisibility(View.VISIBLE);
				_holder.pic_title.setText(more.getNewstitle() == null ? ""
						: more.getNewstitle());
//				_holder.pic_content.setText(more.getNwscontent() == null ? ""
//						: more.getNwscontent());
				_holder.pic_img.setTag(more.getNewspic() == null ? "" : more
						.getNewspic());
				_holder.pic_time.setText(more.getNwstime() == null ? "" : more
						.getNwstime());
				imageLoader.displayImage(more.getNewspic(), _holder.pic_img);
			}
		}

		return convertView;
	}

	static class ViewHolde {
		// 标题+简介
		LinearLayout intro_view;
		TextView intro_title;
//		TextView intro_content;
		TextView intro_time;
		// 标题+简介+左图
		RelativeLayout pic_view;
		TextView pic_title;
//		TextView pic_content;
		ImageView pic_img;
		TextView pic_time;
	}
}
