package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.CountryDelListBean;
import com.wisdomrouter.app.fragment.ui.adapter.VoteAdapter;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoteListActivity extends BaseActivity {
    @Bind(R.id.list_view)
    ViewPaperListView listView;

    private List<ArticleListDao> voGlobal = null;
    private List<ArticleListDao> regList = new ArrayList<>();
    private VoteAdapter regAdapter;

    private int page = 1;
    private int pagesize = 12;
    private Gson gson;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.fragment_reg);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        initTitleBar("投票",0,null);

        initWidgets();
        setListener();
        getVoteList();
    }

    private void initWidgets() {
        regAdapter = new VoteAdapter(this, regList);
        listView.setAdapter(regAdapter);
        page = 1;
        gson = new Gson();
        listView.onRefreshStart();
    }



    private void getVoteList() {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                if (page == 1) {
                    regList.clear();
                    listView.onRefreshComplete();
                } else
                    listView.onLoadMoreComplete();
                try {
                    voGlobal = gson.fromJson(response,
                            new TypeToken<List<ArticleListDao>>() {
                            }.getType());
                    if (voGlobal != null && voGlobal.size() > 0) {
                        page += 1;
                        regList.addAll(voGlobal);
                        regAdapter.notifyDataSetChanged();
                    } else {
                        if (page == 1) {
                            WarnUtils.toast(mContext, "暂无投票活动");
                        } else {
                            WarnUtils.toast(mContext, "已经加载完了!");
                        }
                    }

                } catch (Exception e) {
                    WarnUtils.toast(mContext, "数据获取解析异常,请稍后进入!");
                }
            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        GlobalTools.getVoteList(volleyRequest,page,pagesize);
    }



    private void setListener() {

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                if (pos - 1 >= 0) {
                    String aid = regList.get(pos - 1).getKey();
                    String title = regList.get(pos - 1).getTitle();
                    String pic = regList.get(pos - 1).getIndexpic() == null ? ""
                            : regList.get(pos - 1).getIndexpic();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", aid);
                    bundle.putString("title", title);
                    bundle.putString("pic", pic);
                    ActivityUtils.to(VoteListActivity.this,
                            VoteDetailNewActivity.class, bundle);
                }
            }
        });

        listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getVoteList();
            }
        });

        listView.setOnLoadMoreListener(new PullRefreshListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getVoteList();
            }
        });


    }

}
