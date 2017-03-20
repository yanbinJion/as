package com.wisdomrouter.app.fragment.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.TextView;

import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.BaoliaoImageAdapter;
import com.wisdomrouter.app.fragment.adapter.BaoliaoReplyAdapter;
import com.wisdomrouter.app.fragment.bean.DisCloseListDao;
import com.wisdomrouter.app.fragment.bean.DisCloseListDao.Replyinfo;
import com.wisdomrouter.app.view.BaseGridView;
import com.wisdomrouter.app.view.BaseListView;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BaoliaodetailActivity extends BaseDetailActivity {
	DisCloseListDao mData;
	ArrayList<String> data = new ArrayList<>();
	List<Replyinfo> info = new ArrayList<Replyinfo>();
	@ViewInject(id = R.id.broke_title)
	TextView broke_title;
	@ViewInject(id = R.id.broke_time)
	TextView broke_time;
	@ViewInject(id = R.id.broke_content)
	TextView broke_content;
	@ViewInject(id = R.id.reply_time)
	TextView reply_time;
	@ViewInject(id = R.id.reply_content)
	TextView reply_content;
	@ViewInject(id = R.id.broke_img)
	BaseGridView broke_img;
	@ViewInject(id = R.id.broke_reply)
	BaseListView broke_reply;
	String[] res;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_baoliaodetail);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.activity_title_common);
		initTitleBarForLeft("爆料详情");
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (getIntent() != null) {
			mData = (DisCloseListDao) getIntent().getSerializableExtra("key");
		}
		res = mData.getImages();
		data.clear();
		data.addAll(Arrays.asList(res));
		BaoliaoImageAdapter adapter = new BaoliaoImageAdapter(data, this);
		broke_img.setAdapter(adapter);
		broke_img.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				showImage(position,data);

			}
		});
		
		info.addAll(mData.getReplyinfo());
		BaoliaoReplyAdapter replyAdapter = new BaoliaoReplyAdapter(info, this);
		broke_reply.setAdapter(replyAdapter);
		replyAdapter.notifyDataSetChanged();
		initData();
	}


	private void initData() {
		if (mData != null) {
			broke_title.setText(mData.getTitle() == null ? "" : mData
					.getTitle());
			broke_time.setText(mData.getCreatetime() == null ? "" : mData
					.getCreatetime());
			broke_content.setText(mData.getContent() == null ? "" : mData
					.getContent());
		}
	}
	private void showImage(int position,ArrayList<String> url){
				Intent intent = new Intent(this,ImagePagerActivity.class);
		// 图片url,为了演示这里使用常量，一般从数据库中或网络中获取
		intent.putStringArrayListExtra(ImagePagerActivity.EXTRA_IMAGE_URLS, url);
		intent.putExtra(ImagePagerActivity.EXTRA_IMAGE_INDEX, position);
		startActivity(intent);
	}
}
