package com.wisdomrouter.app.fragment.ui.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Random;

import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.fragment.bean.VoteDetailDao.Items;
import com.wisdomrouter.app.fragment.ui.adapter.VoteResultAdapter;
import com.wisdomrouter.app.utils.PixelUtils;
import com.wisdomrouter.app.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

public class VoteResultAdapter extends BaseAdapter {
	private Context mContext;
	private List<Items> getMulList;
	private int mScreenWidth;
	private int countSum;
	private Boolean showNum;
	ViewHolder holder = null;
	// 用于记录每个RadioButton的状态，并保证只可选一个
	HashMap<String, Boolean> states = new HashMap<String, Boolean>();

	public VoteResultAdapter(Context context, List<Items> mMulList,
			int screenWidth, int countSum, Boolean showNum) {
		this.mContext = context;
		this.getMulList = mMulList;
		this.countSum = countSum;
		this.showNum = showNum;
		this.mScreenWidth = screenWidth;
		
	}

	@Override
	public int getCount() {
		return getMulList.size();
	}

	@Override
	public Object getItem(int position) {
		return getMulList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
//		final ViewHolder holder = null;
		// if (convertView == null) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.activity_vote_result_item, null);
		holder = new ViewHolder();
		holder.title = (TextView) convertView.findViewById(R.id.resultItem);
		holder.progress = (TextView) convertView
				.findViewById(R.id.resultProcess);
		holder.num = (TextView) convertView.findViewById(R.id.resultNum);
		holder.sum = (LinearLayout) convertView.findViewById(R.id.sum);
		holder.first = (LinearLayout) convertView.findViewById(R.id.first);
		// holder.choose = (RadioButton) convertView.findViewById(R.id.point);
		final RadioButton radio = (RadioButton) convertView
				.findViewById(R.id.point);
		holder.choose = radio;
		convertView.setTag(holder);
		// } else {
		// holder = (ViewHolder) convertView.getTag();
		// }

		try {
			if (getMulList != null) {
				if (showNum) {// 显示投票结果
					holder.sum.setVisibility(View.VISIBLE);
					holder.choose.setVisibility(View.GONE);
					int pos = position + 1;
					holder.title.setText(pos + ". "
							+ getMulList.get(position).getTitle());
					int process = (mScreenWidth / countSum)
							* getMulList.get(position).getCount();
					holder.progress.setWidth(process);
					holder.num
							.setText(((getMulList.get(position).getCount() * 100) / countSum)
									+ "%");
					Random random = new Random();
					int aaa = random.nextInt(16777216) + 1;
					String hex = Integer.toHexString(aaa);
					holder.progress.setBackgroundColor(Color.parseColor("#"
							+ hex));
				} else {
					holder.sum.setVisibility(View.GONE);
					holder.choose.setVisibility(View.VISIBLE);
					holder.title.setText(getMulList.get(position).getTitle());
					holder.choose
							.setOnClickListener(new View.OnClickListener() {

								public void onClick(View v) {
                                HandApplication.voteId=position;
									// 重置，确保最多只有一项被选中
									for (String key : states.keySet()) {
										states.put(key, false);

									}
									states.put(String.valueOf(position),
											radio.isChecked());
//									holder.choose.setChecked(true);
									VoteResultAdapter.this
											.notifyDataSetChanged();
					   
									
								}
							});
					holder.first.setTag(position);	
//					holder.choose.setClickable(false);	
					boolean res = false;
					if (states.get(String.valueOf(position)) == null
							|| states.get(String.valueOf(position)) == false) {
						res = false;
						states.put(String.valueOf(position), false);
					} else
						res = true;

					holder.choose.setChecked(res);
				}
			}
		} catch (Exception e) {
		}

		return convertView;
	}

	class ViewHolder {
		TextView title;
		TextView progress;
		TextView num;
		LinearLayout sum;// 投票结果
		LinearLayout first;// 
		RadioButton choose;// 单选按钮
	}

}
