package com.wisdomrouter.app.fragment.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;
import com.wisdomrouter.app.fragment.bean.FolderInfo;
import com.wisdomrouter.app.R;

/**
 * 文件夹Adapter
 */
public class FolderAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;

	private List<FolderInfo> mFolders = new ArrayList<FolderInfo>();

	int mImageSize;

	int lastSelected = 0;

	public FolderAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mImageSize = mContext.getResources().getDimensionPixelOffset(
				R.dimen.dimen_70);
	}

	/**
	 * 设置数据集
	 * 
	 * @param folderInfos
	 */
	public void setData(List<FolderInfo> folderInfos) {
		if (folderInfos != null && folderInfos.size() > 0) {
			mFolders = folderInfos;
		} else {
			mFolders.clear();
		}
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return mFolders.size() + 1;
	}

	@Override
	public FolderInfo getItem(int i) {
		if (i == 0)
			return null;
		return mFolders.get(i - 1);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}

	@Override
	public View getView(int i, View view, ViewGroup viewGroup) {
		ViewHolder holder;
		if (view == null) {
			view = mInflater.inflate(R.layout.camerasdk_list_item_folder,
					viewGroup, false);
			holder = new ViewHolder(view);
		} else {
			holder = (ViewHolder) view.getTag();
		}
		if (holder != null) {
			if (i == 0) {
				holder.name.setText(mContext.getResources().getString(
						R.string.camerasdk_album_all));
				holder.size.setText("(" + getTotalImageSize() + "张)");
				if (mFolders.size() > 0) {
					FolderInfo f = mFolders.get(0);
					Picasso.with(mContext).load(new File(f.cover.path))
							.error(R.drawable.camerasdk_pic_loading)
							.resize(mImageSize, mImageSize).centerCrop()
							.into(holder.cover);
				}
			} else {
				holder.bindData(getItem(i));
			}
			if (lastSelected == i) {

			} else {

			}
		}
		return view;
	}

	private int getTotalImageSize() {
		int result = 0;
		if (mFolders != null && mFolders.size() > 0) {
			for (FolderInfo f : mFolders) {
				result += f.imageInfos.size();
			}
		}
		return result;
	}

	public void setSelectIndex(int i) {
		if (lastSelected == i)
			return;

		lastSelected = i;
		notifyDataSetChanged();
	}

	public int getSelectIndex() {
		return lastSelected;
	}

	class ViewHolder {
		ImageView cover;
		TextView name;
		TextView size;

		ViewHolder(View view) {
			cover = (ImageView) view.findViewById(R.id.cover);
			name = (TextView) view.findViewById(R.id.name);
			size = (TextView) view.findViewById(R.id.size);
			view.setTag(this);
		}

		void bindData(FolderInfo data) {
			name.setText(data.name);
			size.setText("(" + data.imageInfos.size() + "张)");
			// 显示图片
			Picasso.with(mContext).load(new File(data.cover.path))
					.placeholder(R.drawable.camerasdk_pic_loading)
					.resize(mImageSize, mImageSize).centerCrop().into(cover);
			// TODO 选择标识
		}
	}

}