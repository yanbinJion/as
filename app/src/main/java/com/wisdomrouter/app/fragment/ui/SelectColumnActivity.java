package com.wisdomrouter.app.fragment.ui;

import java.util.ArrayList;
import java.util.List;

import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.fragment.adapter.ServiceColumnAdapter;
import com.wisdomrouter.app.fragment.adapter.ServiceRdColumnAdapter;

import com.google.gson.Gson;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.view.BaseGridView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;

public class SelectColumnActivity extends BaseDetailActivity {
    BaseGridView gridView;
    List<Channel> listEntity1;
    List<Channel> listEntity2;
    BaseGridView dgvColumn;
    TextView txt_edit;
    ServiceRdColumnAdapter adapter1;
    ServiceColumnAdapter adapter2;
    private Gson gson;
    Context mContext = this;
    final int ADAPTERREAD = 1;
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case ADAPTERREAD://刷新适配器1
                    adapter1.setIsShowDelete(isShowDelete);
                    break;
            }
            return false;
        }
    });
    int currentIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.popwindow_channel_news);
        if (getIntent() != null) {
            currentIndex = getIntent().getIntExtra("currentIndex", 0);
        }
        initViews();
    }


    private void initViews() {
        gson = new Gson();
        listEntity1 = new ArrayList<>();
        listEntity2 = new ArrayList<>();
        // 缓存中存在已读未读列表
        listEntity1 = HandApplication.getInstance().mSpUtil
                .getReadcolumn();
        listEntity2 = HandApplication.getInstance().mSpUtil
                .getUnReadcolumn();

        dgvColumn = (BaseGridView) findViewById(R.id.column_edit_grid_gv);
        dgvColumn.setSelector(R.color.transparent);
        txt_edit = (TextView) findViewById(R.id.txt_edit);
        gridView = (BaseGridView) findViewById(R.id.column_edit_gv_gvcolumn);
        gridView.setSelector(R.color.transparent);
        adapter2 = new ServiceColumnAdapter(this, mContext, listEntity2);
        gridView.setAdapter(adapter2);
        gridView.setOnItemClickListener(onItemClickListener);
        dgvColumn.setOnItemClickListener(onItemClickListenerrd);

        adapter1 = new ServiceRdColumnAdapter(this, dgvColumn, listEntity1,
                adapter2, isShowDelete, currentIndex);

        dgvColumn.setAdapter(adapter1);

        txt_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_edit.getText().toString().equals("编辑")) {
                    txt_edit.setText("完成");
                    isShowDelete = true;
                    mHandler.sendEmptyMessage(ADAPTERREAD);

                } else {
                    txt_edit.setText("编辑");
                    isShowDelete = false;
                    mHandler.sendEmptyMessage(ADAPTERREAD);
                }

            }
        });
    }


    //已读栏目点击添加
    private OnItemClickListener onItemClickListenerrd = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

            if (isShowDelete) {//删除按钮显示 点击进行删除delete_markView
                ImageView iv = (ImageView) arg1.findViewById(R.id.delete_markView);
                adapter1.showDelColumnAnim(iv, currentIndex);
            } else {
                int index = Integer.parseInt(arg1.findViewById(R.id.column_tv_id)
                        .getTag().toString());
                Intent intent = new Intent();
                intent.putExtra("currentIndex", index);
                setResult(RESULT_OK, intent);
                finish();
            }


        }
    };

    //未读栏目点击
    private OnItemClickListener onItemClickListener = new OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {
            int index = Integer.parseInt(arg1.findViewById(R.id.column_tv_id)
                    .getTag().toString());
            listEntity1.add(listEntity2.get(index));
            listEntity2.remove(index);
            saveCustomColum();
            adapter2.notifyDataSetChanged();
            mHandler.sendEmptyMessage(ADAPTERREAD);

        }
    };
    boolean isShowDelete = false;

    public void close(View v) {
        Intent intent = new Intent();
        intent.putExtra("currentIndex", adapter1.getCurrentIndex());
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent intent = new Intent();
            intent.putExtra("currentIndex", adapter1.getCurrentIndex());
            setResult(RESULT_OK, intent);
            finish();
        }
        return true;
    }

    //自定义栏目保存到缓存中
    public void saveCustomColum() {
        HandApplication.getInstance().mSpUtil.saveReadcolumn(gson.toJson(listEntity1));
        HandApplication.getInstance().mSpUtil.saveUnReadcolumn(gson.toJson(listEntity2));
    }


}
