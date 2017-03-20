package com.wisdomrouter.app.fragment.adapter;

import java.util.List;

import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.MainActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.wisdomrouter.app.view.PagedDragDropGrid;
import com.wisdomrouter.app.view.PagedDragDropGridAdapter;

public class DragDropGridAdapter implements PagedDragDropGridAdapter {
	PagedDragDropGrid gridview;
	List<Channel> list;
	Activity activity;
	ServiceColumnAdapter adapter;
	private boolean isShowDelete;// 根据这个变量来判断是否显示删除图标，true是显示，false是不显示
	private LayoutInflater mInflater;

	public DragDropGridAdapter(Activity ac, PagedDragDropGrid gv,
			List<Channel> l, ServiceColumnAdapter at) {
		adapter = at;
		activity = ac;
		gridview = gv;
		list = l;
		mInflater = LayoutInflater.from(activity);
	}

	@Override
	public int pageCount() {
		return 1;
	}

	@Override
	public int itemCountInPage(int page) {
		return list.size();
	}

	public void setIsShowDelete(boolean isShowDelete) {
		this.isShowDelete = isShowDelete;
		gridview.notifyDataSetChanged();
	}

	@Override
	public View view(int page, int index) {

		View convertView = mInflater.inflate(R.layout.column_edit_items, null);
		TextView tvTitle = (TextView) convertView
				.findViewById(R.id.column_tv_newstitle);
		TextView tvId = (TextView) convertView.findViewById(R.id.column_tv_id);
		ImageView deleteView = (ImageView) convertView
				.findViewById(R.id.delete_markView);
		tvTitle.setText(list.get(index).getName());
		tvId.setText(list.get(index).getKey());
		if (index == 0) {
			tvTitle.setTextColor(activity.getResources()
					.getColor(R.color.app_topbg));
			deleteView.setVisibility(View.GONE);
		}else {
			deleteView.setVisibility(isShowDelete ? View.VISIBLE : View.GONE);// 设置删除按钮是否显示
		}


		convertView.setTag(index);
		deleteView.setTag(index);
		deleteView.setOnClickListener(onClickListenerdel);
		convertView.setOnClickListener(onClickListener);
		convertView.setOnLongClickListener(onLongClickListener);
		return convertView;
	}

	private OnClickListener onClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
//			Toast.makeText(activity, "view" + v.getTag(), Toast.LENGTH_SHORT)
//					.show();
			Intent intent = new Intent(activity,
					MainActivity.class);
			intent.putExtra("state", (v.getTag()+""));

			activity.setResult(1, intent);
			activity.finish();
//			showDelColumnAnim(v);

		}
	};
	private OnClickListener onClickListenerdel= new OnClickListener() {

		@Override
		public void onClick(View v) {
			showDelColumnAnim(v);

		}
	};

	private OnLongClickListener onLongClickListener = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			if (isShowDelete) {
				isShowDelete = false;
			} else {
				isShowDelete = true;
			}
			setIsShowDelete(isShowDelete);
//			 return gridview.onLongClick(v);
//			if (isShowDelete) {
//				isShowDelete = false;
//			} else {
//				isShowDelete = true;
//			}
//			setIsShowDelete(isShowDelete);
			return true;

		}
	};

	private void showDelColumnAnim(View v) {
		int index = Integer.parseInt(v.getTag().toString());
		 if(index==0)return ;

		adapter.addItems(list.get(index));
		this.deleteItem(0, index);

	}

	@Override
	public int rowCount() {
		// TODO Auto-generated method stub
		return AUTOMATIC;
	}

	@Override
	public int columnCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public void printLayout() {
		// TODO Auto-generated method stub

	}

	@Override
	public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
		// TODO Auto-generated method stub

		// B璧峰浣嶇疆
		// A鐩爣浣嶇疆
		Toast.makeText(activity,
				"select" + itemIndexA + " ---select " + itemIndexB,
				Toast.LENGTH_SHORT).show();
	}

	@Override
	public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void moveItemToNextPage(int pageIndex, int itemIndex) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deleteItem(int pageIndex, int itemIndex) {
		list.remove(itemIndex);
		StringBuffer sbread = new StringBuffer();
		sbread.append("[");
		for (int i = 0; i < list.size(); i++) {
			if (i == list.size() - 1)
				sbread.append("{key:" + list.get(i).getKey() + ",name:" + list.get(i).getName() + "}");
			else
				sbread.append("{key:" + list.get(i).getKey() + ",name:" + list.get(i).getName() + "},");

		}
		sbread.append("]");
		HandApplication.getInstance().mSpUtil.saveReadcolumn(sbread.toString());
		gridview.notifyDataSetChanged();
	}

	@Override
	public int deleteDropZoneLocation() {
		// TODO Auto-generated method stub
		return BOTTOM;
	}

	@Override
	public boolean showRemoveDropZone() {
		// TODO Auto-generated method stub
		return false;
	}

}
