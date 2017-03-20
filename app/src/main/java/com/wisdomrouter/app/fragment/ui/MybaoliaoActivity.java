package com.wisdomrouter.app.fragment.ui;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.DisCloseAdapter;
import com.wisdomrouter.app.fragment.bean.DisCloseListDao;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

public class MybaoliaoActivity extends BaseDetailActivity {
    @ViewInject(id = R.id.btn_disclose, click = "toBaoliao")
    Button btndisclose;
    @ViewInject(id = R.id.lv_disclose)
    ViewPaperListView listView;
    @ViewInject(id = R.id.re_disclose)
    RelativeLayout redisclose;
    @ViewInject(id = R.id.left_btn)
    ImageView leftBtn;
    @ViewInject(id = R.id.title)
    TextView title;
    @ViewInject(id = R.id.tv_baoliao, click = "toBaoliao")
    TextView tv_baoliao;
    @ViewInject(id = R.id.right_btn)
    Button rightBtn;

    private List<DisCloseListDao> voGlobal = null;
    private List<DisCloseListDao> disList = new ArrayList<DisCloseListDao>();
    private DisCloseAdapter adapter;
    private int page = 1;
    private int pageSize = 12;
    Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_mybaoliao);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBarForLeft("我的爆料");

    }

    @Override
    protected void onStart() {
        super.onStart();
        initView();
    }

    private void initView() {
        adapter = new DisCloseAdapter(disList, MybaoliaoActivity.this);
        listView.setAdapter(adapter);
        page = 1;
        gson = new Gson();
        getData();
        redisclose.setVisibility(View.GONE);
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                if (disList != null && arg2 >= 1) {
                    Bundle bund = new Bundle();
                    bund.putSerializable("key", disList.get(arg2 - 1));
                    ActivityUtils.to(MybaoliaoActivity.this,
                            BaoliaodetailActivity.class, bund);
                }

            }
        });
        listView.setOnRefreshListener(new OnRefreshListener() {

            @Override
            public void onRefresh() {
                page = 1;
                getData();
            }
        });
        listView.setOnLoadMoreListener(new OnLoadMoreListener() {

            @Override
            public void onLoadMore() {
                getData();

            }
        });
    }

    FinalHttp fh;

    private void getData() {
        fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        if (NetUtil.NETWORN_NONE != NetUtil.getNetworkState(this)) {
            fh.get(Const.HTTP_HEADKZ
                    + "/plugin/broke-api/brokelist?openid="
                    + HandApplication.user.getOpenid() + "&page=" + page
                    + "&pagesize=" + pageSize, new AjaxCallBack<Object>() {

                @Override
                public void onLoading(long count, long current) {
                    super.onLoading(count, current);
                }

                @Override
                public void onSuccess(Object t) {
                    try {
                        voGlobal = null;
                        voGlobal = gson.fromJson(t.toString(),
                                new TypeToken<List<DisCloseListDao>>() {
                                }.getType());
                        if (page == 1) {
                            listView.onRefreshComplete();
                        } else {
                            listView.onLoadMoreComplete();
                        }
                        if (page == 1 && voGlobal.size() == 0) {
                            redisclose.setVisibility(View.VISIBLE);
                            listView.setVisibility(View.GONE);
                        }else if (page==1&&voGlobal.size()>0){
                            redisclose.setVisibility(View.GONE);
                            listView.setVisibility(View.VISIBLE);
                        }
                        if (voGlobal != null && voGlobal.size() > 0) {
                            if (page == 1) {
                                disList.clear();
                            }
                            page += 1;
                            disList.addAll(voGlobal);
                            adapter.notifyDataSetChanged();
                        }
                    } catch (Exception e) {
                        WarnUtils.toast(MybaoliaoActivity.this, "获取数据异常,稍后进入"
                                + e.getMessage());
                        listView.onRefreshComplete();
                        return;
                    }
                }

                @Override
                public void onStart() {
                    // 开始http请求的时候回调
                    if (page == 1) {
                        listView.onRefreshStart();
                    }

                }

                @Override
                public void onFailure(Throwable t, int errorNo, String strMsg) {
                    // 加载失败的时候回调
                    // super.onFailure(t, errorNo, strMsg);
                    if (page == 1)
                        listView.onRefreshComplete();
                    else
                        listView.onLoadMoreComplete();
                    WarnUtils.toast(MybaoliaoActivity.this, "数据获取异常,稍后获取!"
                            + strMsg);
                }

            });
        } else {
            WarnUtils.toast(MybaoliaoActivity.this, "您的网络不通!");
        }

    }

    public void toBaoliao(View v) {
        ActivityUtils.to(MybaoliaoActivity.this, BaoliaoActivity.class);
    }
}
