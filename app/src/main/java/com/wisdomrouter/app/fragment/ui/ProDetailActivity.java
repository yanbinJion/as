package com.wisdomrouter.app.fragment.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Response;
import com.squareup.picasso.Picasso;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMSocialService;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.ProjectDetailAdapter;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ColumnHorizontalScrollView;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.RatioImageView;
import com.wisdomrouter.app.view.ViewPaperListView;


import java.util.ArrayList;
import java.util.List;

public class ProDetailActivity extends BaseDetailActivity implements AdapterView.OnItemClickListener, PullRefreshListView.OnLoadMoreListener, PullRefreshListView.OnRefreshListener {
    private ProjectListDao voGlobal;
    private ViewPaperListView listView;
    private ProjectDetailAdapter adapter;
    //头布局
    private LinearLayout headView;
    private ColumnHorizontalScrollView cloumn;
    private RatioImageView ratioImageView;
    private TextView tv_pro;
    // 显示被选中的子列
    private LinearLayout mRadioGroup_content;
    // 当前选中的栏目
    private int columnSelectIndex = -1;
    // 左阴影部分
    private ImageView shade_left;
    // 右阴影部分
    private ImageView shade_right;
    // 屏幕宽度
    private int mScreenWidth = 0;
    // 显示列表项
    private LinearLayout ll_more_columns;
    private LinearLayout rl_column;

    private int page = 1;
    private int pagesize = 8;
    private int txt;

    private List<ArticleListDao> news = new ArrayList<>();
    private List<ProjectListDao.Detail> cloumndata = new ArrayList<>();
    private UMShareListener umsnslitener=new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
        WarnUtils.toast(ProDetailActivity.this,"分享成功");
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_prodetail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        if (getIntent() != null) {
            voGlobal = (ProjectListDao) getIntent().getSerializableExtra("project");
        }
//        initTitleBarForLeft(voGlobal.getName());
        if (voGlobal != null) {
            initTitleBar(voGlobal.getName(), R.drawable.img_share, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareUtils.shareSdk(ProDetailActivity.this, voGlobal.getName(), voGlobal.getDesc(), voGlobal.getPic(), voGlobal.getKey(), Const.SHARE_API.PROJECT,umsnslitener);
                }
            });
            initView();
            initHeadData();
            setAdapter();
            getCloumn(txt);
            initTabColumn();

        }
    }

    private void initView() {
        listView = (ViewPaperListView) findViewById(R.id.list_pro);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);
        LayoutInflater inflater = LayoutInflater.from(this);
        headView = (LinearLayout) inflater.inflate(
                R.layout.activity_project_head, null);
        mRadioGroup_content = (LinearLayout) headView.findViewById(R.id.mRadioGroup_content);
        shade_left = (ImageView) headView.findViewById(R.id.shade_left);
        shade_right = (ImageView) headView.findViewById(R.id.shade_right);
        ll_more_columns = (LinearLayout) headView
                .findViewById(R.id.ll_more_columns);
        ratioImageView = (RatioImageView) headView.findViewById(R.id.pro_pic);
        rl_column = (LinearLayout) headView.findViewById(R.id.rl_column);
        tv_pro = (TextView) headView.findViewById(R.id.tv_pro);
        cloumn = (ColumnHorizontalScrollView) headView.findViewById(R.id.cloumn_country);
        listView.addHeaderView(headView);
    }

    private void initHeadData() {
        if (voGlobal != null) {
            if (voGlobal.getIndexpic() != null) {
                Picasso.with(this).load(voGlobal.getIndexpic())
                        .placeholder(R.drawable.load_default)
                        .error(R.drawable.load_default)
                        .into(ratioImageView);
            }
            ratioImageView.setAspectRatio(2f);
            String title = voGlobal.getDesc();
            SpannableStringBuilder builder = new SpannableStringBuilder("导语\b\b\b" + (title == null ? "" : title));
            ForegroundColorSpan blackspan = new ForegroundColorSpan(Color.BLACK);
            builder.setSpan(blackspan, 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            tv_pro.setText(builder);
        }
    }

    private void setAdapter() {
        adapter = new ProjectDetailAdapter(this, news);
        listView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (position - 2 >= 0) {
            String key = news.get(position - 2).getKey();
            Bundle bundle = new Bundle();
            bundle.putString("key", key);
            String content_api = "/article/content";
            bundle.putString("content_api", content_api);
            String url;

            url = Const.HTTP_HEADKZ + "/app/multimedia/"+news.get(position-2).getInfo_class()+"?key=" + key;

            bundle.putString("url", url);
            bundle.putString("title", "详情");
            bundle.putString("sharetitle", news.get(position - 2).getTitle());
            bundle.putString("indexpic", news.get(position - 2).getIndexpic());
            bundle.putString("type", news.get(position-2).getInfo_class());
            ActivityUtils.to(ProDetailActivity.this, ContentWebviewActivity.class, bundle);
        }
    }

    private void getCloumn(int text) {
        if (voGlobal != null) {
            cloumndata.clear();
            cloumndata.addAll(voGlobal.getDetail());
            Response.Listener<List<ArticleListDao>> listener = new Response.Listener<List<ArticleListDao>>() {
                @Override
                public void onResponse(List<ArticleListDao> details) {
                    getNews(details);
                }
            };
            try {
                GlobalTools.getCountryNews(ProDetailActivity.this, page, pagesize, cloumndata.get(text).getKey(), listener, new StrErrListener(this));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void getNews(List<ArticleListDao> response) {
        if (page == 1) {
            listView.onRefreshComplete();
            if (response != null && response.size() != 0) {
                news.clear();
                news.addAll(response);
            }
            adapter.notifyDataSetChanged();
        } else {
            listView.onLoadMoreComplete();
            if (response != null) {
                if (response.size() == 0) {
//                    WarnUtils.toast(this, "加载完成");
                    WarnUtils.showToast(this,"加载完成",500);
                }
                news.addAll(response);
            }
            adapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化Column栏目项
     */
    private void initTabColumn() {
        int count = cloumndata.size();
        cloumn.setParam(ProDetailActivity.this, mScreenWidth, mRadioGroup_content,
                shade_left, shade_right, ll_more_columns, rl_column);
        for (int i = 0; i < count; i++) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.MATCH_PARENT);
            params.topMargin = 10;
            params.bottomMargin = 10;
            params.leftMargin = 10;
            params.rightMargin = 20;
            TextView columnTextView = new TextView(ProDetailActivity.this);
            columnTextView.setTextAppearance(ProDetailActivity.this,
                    R.style.top_category_scroll_view_item_text);
            columnTextView.setBackgroundResource(R.drawable.radio_buttong_bg);
            columnTextView.setGravity(Gravity.CENTER);
            columnTextView.setPadding(40, 5, 40, 5);
            columnTextView.setId(i);
            columnTextView.setText(cloumndata.get(i).getListname());

            if (columnSelectIndex == i) {
                columnTextView.setSelected(true);
            }
            columnTextView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
                        View localView = mRadioGroup_content.getChildAt(i);
                        if (localView != v) {
                            localView.setSelected(false);
                        } else {
                            localView.setSelected(true);
                            news.clear();
                            page = 1;
                            selectTab(i);
                        }
                    }
                }
            });
            mRadioGroup_content.addView(columnTextView, i, params);
        }
        cloumndata.clear();
        selectTab(0);
    }

    /**
     * 选择的Column里面的Tab
     */
    private void selectTab(int tab_postion) {

        columnSelectIndex = tab_postion;
        for (int i = 0; i < mRadioGroup_content.getChildCount(); i++) {
            View checkView = mRadioGroup_content.getChildAt(tab_postion);
            int k = checkView.getMeasuredWidth();
            int l = checkView.getLeft();
            int i2 = l + k / 2 - mScreenWidth / 2;
            cloumn.smoothScrollTo(i2, 0);
        }
        // 判断是否选中
        for (int j = 0; j < mRadioGroup_content.getChildCount(); j++) {
            View checkView = mRadioGroup_content.getChildAt(j);
            boolean ischeck;
            if (j == tab_postion) {
                txt = tab_postion;
                ischeck = true;
                ((TextView) mRadioGroup_content.getChildAt(j))
                        .setTextColor(getResources().getColor(R.color.white));
                getCloumn(j);
            } else {
                ischeck = false;
                ((TextView) mRadioGroup_content.getChildAt(j))
                        .setTextColor(getResources().getColor(R.color.black));
            }
            checkView.setSelected(ischeck);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyVolley.getInstance(this).cancelPendingRequests(Const.TOWNNEWS);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("MainScreen"); // 统计页面
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("MainScreen");
    }

    @Override
    public void onLoadMore() {
        page++;
        getCloumn(txt);
    }

    @Override
    public void onRefresh() {
        page=1;
        news.clear();
        getCloumn(txt);
    }
}