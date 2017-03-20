package com.wisdomrouter.app.fragment.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.fragment.bean.CommentDao;
import com.wisdomrouter.app.fragment.ui.MyCommentActivity;
import com.wisdomrouter.app.fragment.ui.adapter.UserCommentAdapter;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.R;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class MyCommentActivity extends FinalActivity implements
        OnRefreshListener {
    /**
     * 新闻列表
     */
    @ViewInject(id = R.id.list_view)
    ViewPaperListView listView;
    @ViewInject(id = R.id.txt_blank)
    TextView txt_blank;
    /**
     * 新闻列表适配器 == ReadAdapter
     */
    private UserCommentAdapter listAdapter;

    /**
     * 获取数据的工具
     */
    private GlobalTools globalTool;
    private List<CommentDao> voGlobal;
    private List<CommentDao> commentList = new ArrayList<CommentDao>();

    private int page = 1;
    private int pageSize = 8;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_my_comment);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);

        initTitleBar();
        setListener();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (voGlobal == null) {
            initData();
        }
    }

    /**
     * 设置点击事件
     */
    private void setListener() {
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                new RefreshTask().execute();
            }
        });

    }

    /**
     * 初始化数据
     */
    private void initData() {
        globalTool = new GlobalTools(this);
        listAdapter = new UserCommentAdapter(MyCommentActivity.this,
                commentList);
        listView.setAdapter(listAdapter);
        page = 1;
        new RefreshTask().execute();
    }

    /**
     * 用于下拉刷新
     */
    class RefreshTask extends AsyncTask<Void, Integer, String[]> {
        private int ERRORCODE = 1;
        private String errorInfo = null;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (page == 1) {
                listView.onRefreshStart();
            }
        }

        @Override
        protected String[] doInBackground(Void... params) {
            try {

                voGlobal = globalTool.getCommentByIdOrinfokey(true,
                        HandApplication.user.getOpenid(), "", page + "",
                        pageSize + "");
            } catch (Exception e) {
                e.printStackTrace();
                errorInfo = e.getMessage();
                publishProgress(ERRORCODE);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            if (ERRORCODE == values[0]) {
                WarnUtils.toast(MyCommentActivity.this, errorInfo);
            }
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(String[] result) {
            if (page == 1) {
                commentList.clear();
                listView.onRefreshComplete();
            } else
                listView.onLoadMoreComplete();

            if (voGlobal != null) {
                if (voGlobal.size() > 0) {
                    txt_blank.setVisibility(View.GONE);
                    page++;
                    commentList.addAll(voGlobal);
                    listAdapter.notifyDataSetChanged();
                } else {
                    listAdapter.notifyDataSetChanged();
                    if (page == 1) {
                        txt_blank.setVisibility(View.VISIBLE);
                        txt_blank.setText("您暂时还没有评论过任何信息!");
                    } else {
                        txt_blank.setVisibility(View.GONE);
                        WarnUtils.toast(MyCommentActivity.this, "已经加载完了,没有了哦!");
                    }

                }
            } else {

                WarnUtils.toast(MyCommentActivity.this,
                        "网络异常,获取数据失败 [voGlobal==null]");
            }

            super.onPostExecute(result);
        }
    }

    /**
     * 设置左右标签,专门设置title_bar
     */
    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText("我的评论");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onRefresh() {
        page = 1;
        new RefreshTask().execute();
    }
}
