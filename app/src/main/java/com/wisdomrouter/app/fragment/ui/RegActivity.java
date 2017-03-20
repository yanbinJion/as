package com.wisdomrouter.app.fragment.ui;

import java.util.ArrayList;
import java.util.List;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.ui.RegActivity;
import com.wisdomrouter.app.fragment.ui.adapter.RegistAdapter;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.R;

public class RegActivity extends FinalActivity {
    private ViewPaperListView listView;

    private List<ArticleListDao> voGlobal = null;
    private List<ArticleListDao> regList = new ArrayList<ArticleListDao>();
    private RegistAdapter regAdapter;

    private int page = 1;
    private int pagesize = 12;
    private String url;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.fragment_reg);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBar();
        initViews();
        setListener();
        initData();
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
        titleText.setText("活动");
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }

    private void initData() {
        gson = new Gson();
        regAdapter = new RegistAdapter(this, regList);
        listView.setAdapter(regAdapter);
        page = 1;
        getRegList();
    }

    private void initViews() {
        url = Const.HTTP_HEADKZ + Const.REGLIST_URL;
        listView = (ViewPaperListView) findViewById(R.id.list_view);
    }

    private void setListener() {
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                // 因为前面有下拉刷新的header和图片的header，所以这里需要减去2
                if (pos - 1 >= 0) {
                    regList.get(pos - 1).setIsRead(true);
                    regAdapter.notifyDataSetChanged();
                    String key = regList.get(pos - 1).getKey();
                    String address = regList.get(pos - 1).getActaddress();
                    String indexpic = regList.get(pos - 1).getIndexpic() == null ? ""
                            : regList.get(pos - 1).getIndexpic();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    bundle.putString("address", address);
                    bundle.putString("indexpic", indexpic);
                    ActivityUtils.to(RegActivity.this,
                            EventDetailActivity.class, bundle);
                }
            }
        });

        listView.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getRegList();
            }
        });

        listView.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getRegList();
            }
        });

    }

    private void getRegList() {
        FinalHttp fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        fh.get(url + "?page=" + page + "&pagesize=" + pagesize
                        + "&user_openid=" + HandApplication.user.getOpenid(),
                new AjaxCallBack<Object>() {
                    @Override
                    public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                        super.onLoading(count, current);
                    }

                    @Override
                    public void onSuccess(Object t) {
                        voGlobal = null;
                        if (page == 1) {
                            listView.onRefreshComplete();
                            regList.clear();
                        } else
                            listView.onLoadMoreComplete();
                        try {
                            voGlobal = gson.fromJson(t.toString(),
                                    new TypeToken<List<ArticleListDao>>() {
                                    }.getType());
                            if (voGlobal != null) {
                                if (voGlobal.size() == 0) {
                                    if (page == 1) {
                                        WarnUtils.toast(RegActivity.this,
                                                "暂时还没有活动哦!");
                                    } else {
                                        WarnUtils.toast(RegActivity.this,
                                                "已经加载完了,没有更多活动了哦!");
                                    }
                                }
                                regList.addAll(voGlobal);
                                page++;
                                regAdapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            WarnUtils.toast(RegActivity.this, "数据获取解析异常,请稍后进入!"
                                    + e.toString());
                            return;
                        }

                    }

                    @Override
                    public void onStart() {
                        // 开始http请求的时候回调
                        if (page == 1)
                            listView.onRefreshStart();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        // 加载失败的时候回调
                        if (page == 1)
                            listView.onRefreshComplete();
                        else
                            listView.onLoadMoreComplete();
                        WarnUtils.toast(RegActivity.this, strMsg + errorNo);
                    }

                });
    }

}
