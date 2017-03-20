package com.wisdomrouter.app.fragment.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.Utils;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.List;

public class VideoAdapter extends BaseAdapter {
	private Context mContext;
	private List<ArticleListDao> artlist;
	private LayoutInflater inflater;
	protected ImageLoader imageLoader = ImageLoader.getInstance();

	public VideoAdapter(Context context, List<ArticleListDao> artlist) {
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolde viewHolde;
		ArticleListDao art = artlist.get(position);
		if (convertView == null) {
			viewHolde = new ViewHolde();
			convertView = inflater.inflate(R.layout.fragment_video_item, null);
			viewHolde.ivHeader = (RatioImageView) convertView
					.findViewById(R.id.ivHeader);
			viewHolde.ivHeader.setAspectRatio(1.5f);
			viewHolde.tvTitle = (TextView) convertView
					.findViewById(R.id.tvTitle);
			viewHolde.tvNum = (TextView) convertView.findViewById(R.id.tvNum);

			convertView.setTag(viewHolde);
		} else {
			viewHolde = (ViewHolde) convertView.getTag();
		}

		viewHolde.tvTitle.setText(art.getTitle());
		viewHolde.tvNum.setText("评论 " + art.getComment());
		// 异步加载图片
		viewHolde.ivHeader.setTag(art.getIndexpic());
		// 合法的URL路径
		if (!StringUtil.isEmpty(art.getIndexpic())
				&& Utils.patternUrl(art.getIndexpic())) {
			imageLoader.displayImage(ChangeImgUrlUtils.nativetoslt(art.getIndexpic(), "600", "900"), viewHolde.ivHeader);
		}
		if (art.getIsRead() != null && art.getIsRead()) {
			viewHolde.tvTitle.setTextColor(mContext.getResources().getColor(
					R.color.dark_gray));
		}
		return convertView;
	}

	class ViewHolde {
		RatioImageView ivHeader;
		TextView tvTitle;
		TextView tvNum;
	}
}
