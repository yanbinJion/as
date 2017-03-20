package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CardsAnimationAdapter;
import com.wisdomrouter.app.fragment.adapter.NewsAdapter;
import com.wisdomrouter.app.fragment.adapter.NewsAutoScrollPagerAdapter;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.FocusDao;
import com.wisdomrouter.app.fragment.bean.NewArticleListDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyUtils.GsonRequestGet;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.TDevice;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.autoscrollviewpager.AutoScrollViewPager;
import com.wisdomrouter.app.view.autoscrollviewpager.CirclePageIndicator;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class ArimagelistActivity extends BaseDetailActivity implements PullRefreshListView.OnRefreshListener,
        PullRefreshListView.OnLoadMoreListener, AdapterView.OnItemClickListener {
    private AutoScrollViewPager vpNews;
    private CirclePageIndicator pageIndicator;

    private RelativeLayout header;
    private RelativeLayout rlNews;
    // 滑动标题
    private TextView tvSlideTitle;
    // 布局设置类

    private ViewPaperListView listView;

    private NewArticleListDao voGlobal;
    private GlobalTools globalTool;
    private int page = 1;
    private int pageSize = 12;
    // 新闻内容
    private List<ArticleListDao> articleList = new ArrayList<>();
    // 轮播
    private List<ArticleListDao> lbList = new ArrayList<>();
    private List<FocusDao> focusList = new ArrayList<>();
    // 新闻适配器
    private NewsAdapter listAdapter;


    private String id = "";
    private String list_api = "";
    private String focus_key = "";
    private String title = "";

    private AsyncTask<Void, Integer, String[]> mRefreshTask;
    List<String> urls = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<String> keys = new ArrayList<>();


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_arimage);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        id = getIntent().getStringExtra("id") == null ? "" : getIntent().getStringExtra("id");
        list_api = getIntent().getStringExtra("urlPart") == null ? "" : getIntent().getStringExtra("urlPart");
        focus_key = getIntent().getStringExtra("map")== null ? "" :getIntent().getStringExtra("map");
        title = getIntent().getStringExtra("title")== null ? "" :getIntent().getStringExtra("title");
        initTitleBar();
        voGlobal = new NewArticleListDao();
        initView();
    }


    private void initView() {
        globalTool = new GlobalTools(this);
        header = (RelativeLayout) LayoutInflater.from(this).inflate(
                R.layout.view_news_auto_scroll_head, null);
        rlNews = (RelativeLayout) header.findViewById(R.id.rl_news);
        vpNews = (AutoScrollViewPager) header.findViewById(R.id.vp_news);
        ViewGroup.LayoutParams layoutParams = vpNews.getLayoutParams();
        float screen = TDevice.getScreenWidth();
        layoutParams.height = (int) (screen * 2 / 3);
        vpNews.setLayoutParams(layoutParams);
        tvSlideTitle = (TextView) header.findViewById(R.id.tvSlideTitle);
        pageIndicator = (CirclePageIndicator) header.findViewById(R.id.pi_news_indicator);
        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (titles != null && titles.size() >= 0) {
                    tvSlideTitle.setText(titles.get(position));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        listView = (ViewPaperListView) findViewById(R.id.mViewPager);

        listAdapter = new NewsAdapter(this, articleList);
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(
                listAdapter);
        animationAdapter.setAbsListView(listView);
        listView.setAdapter(animationAdapter);
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setOnItemClickListener(this);
        listView.onRefreshStart();
        getListData(page, pageSize);
        if (!TextUtils.isEmpty(focus_key))
         getFocusList();
    }

    private void getListData(int page, int pageSize) {
        StrErrListener errorListener = new StrErrorListener(ArimagelistActivity.this, this);

        try {
            String url = Const.HTTP_HEAD + list_api + "?key=" + id + "&page="
                    + page + "&pagesize=" + pageSize + "&newdata=1";
            GsonRequestGet<NewArticleListDao> gsonRequestGet = new GsonRequestGet<>(
                    url, NewArticleListDao.class, new NewsListener(this), errorListener);
            MyVolley.getInstance(this).addToRequestQueue(gsonRequestGet,
                    id);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }


    class NewsListener implements Response.Listener<NewArticleListDao> {

        private final WeakReference<ArimagelistActivity> mFragment;

        public NewsListener(ArimagelistActivity fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onResponse(NewArticleListDao response) {
            if (mFragment.get() != null) {
                voGlobal = response;
                updateNews();
            }
        }
    }

    class StrErrorListener extends StrErrListener {

        private final WeakReference<ArimagelistActivity> mFragment;

        public StrErrorListener(Context mContext, ArimagelistActivity fragment) {
            super(mContext);
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onErrorResponse(VolleyError arg0) {
            if (mFragment.get() != null) {
                super.onErrorResponse(arg0);
                if (page == 1) {
                    listView.onRefreshComplete();
                } else {
                    listView.onLoadMoreComplete();
                }
            }
        }
    }

    private void getFocusList(){
        Response.Listener<List<FocusDao>> listener = new Response.Listener<List<FocusDao>>() {
            @Override
            public void onResponse(List<FocusDao> focusDaos) {
                handleMessage(focusDaos);
            }
        };
        try {
            GlobalTools.getFocusList(this,focus_key,listener,new StrErrListener(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void handleMessage(List<FocusDao> focusDaos){
        if (focusDaos != null && focusDaos.size() > 0) {
            listView.addHeaderView(header);
            rlNews.setVisibility(View.VISIBLE);
            for (FocusDao newsList : focusDaos) {
                urls.add(newsList.getIndexpic());
                titles.add(newsList.getTitle());
                keys.add(newsList.getInfo_key());
            }
            vpNews.setAdapter(new NewsAutoScrollPagerAdapter(urls, this));
            pageIndicator.setViewPager(vpNews);
            pageIndicator.setSnap(true);
            vpNews.startAutoScroll(0);
            if (titles != null && titles.size() >= 0) {
                tvSlideTitle.setText(titles.get(0));
            }
            vpNews.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
                @Override
                public void onPageClick(AutoScrollViewPager pager, int position) {
                    if (keys != null && keys.size() >= 0) {
                        Bundle bundle = new Bundle();
                        bundle.putString("key", keys.get(position));
                        bundle.putString("content_api", "/article/content");
                        String url;
                        url = Const.HTTP_HEADKZ + "/app/multimedia/"+articleList.get(position).getInfo_class()+"?key=" + keys.get(position);
                        bundle.putString("url", url);
                        bundle.putString("title", "详情");
                        bundle.putString("sharetitle", titles.get(position)) ;
                        bundle.putString("indexpic", urls.get(position));
                        bundle.putString("type", articleList.get(position).getInfo_class());
                        ActivityUtils.to(ArimagelistActivity.this, ContentWebviewActivity.class, bundle);
                    }
                }
            });

        } else {
            listView.removeHeaderView(header);
            rlNews.setVisibility(View.GONE);
        }
    }

    /**
     * @description:更新UI
     * @author:baifan
     * @return:void
     */
    private void updateNews() {
        if (voGlobal != null) {
            if (page == 1) {
                if (voGlobal.getOrders() != null
                        && voGlobal.getOrders().size() != 0) {// list置顶
                    HandApplication.ordersize = voGlobal.getOrders().size();
                    articleList.addAll(voGlobal.getOrders());
                }
                if (voGlobal.getNormal() != null) {
                    List<ArticleListDao> noListtemp = new ArrayList<>();
                    noListtemp.addAll(voGlobal.getNormal());
                    articleList.addAll(voGlobal.getNormal());
                    listAdapter.notifyDataSetChanged();
                } else {
                    WarnUtils.toast(this, "该栏目下暂无信息!");
                }
                listView.onRefreshComplete();
            } else {
                if (voGlobal.getNormal() != null) {
                    articleList.addAll(voGlobal.getNormal());
                    listAdapter.notifyDataSetChanged();
                } else {
                    WarnUtils.toast(this, "已经加载完了,没有了哦!");
                }

                listView.onLoadMoreComplete();
            }
        } else {
            if (page == 1) {
                listView.onRefreshComplete();
            } else {
                listView.onLoadMoreComplete();
            }
        }

    }

    @Override
    public void onRefresh() {
        page = 1;
        articleList.clear();
        lbList.clear();
        urls.clear();
        titles.clear();
        keys.clear();
        getListData(page, pageSize);
        getFocusList();

    }

    @Override
    public void onLoadMore() {
        page = page + 1;
        getListData(page, pageSize);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {

        if (listView.getHeaderViewsCount() == 1) {
            if (pos - 1 >= 0 && articleList != null && articleList.get(pos - 1) != null) {
                articleList.get(pos - 1).setIsRead(true);
                listAdapter.notifyDataSetChanged();
                String key = articleList.get(pos - 1).getKey();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                String content_api = "/article/content";
                bundle.putString("content_api", content_api);
                String url;
                url = Const.HTTP_HEADKZ + "/app/multimedia/"+articleList.get(pos-1).getInfo_class()+"?key=" + key;
                bundle.putString("url", url);
                bundle.putString("title", "详情");
                bundle.putString("sharetitle", articleList.get(pos - 1).getTitle());
                bundle.putString("indexpic", articleList.get(pos - 1).getIndexpic());
                bundle.putString("type", articleList.get(pos-1).getInfo_class());
                bundle.putString("useropenid",HandApplication.user.getOpenid());
                ActivityUtils.to(ArimagelistActivity.this, ContentWebviewActivity.class, bundle);
            }
        } else if (listView.getHeaderViewsCount() == 2) {
            if (pos - 2 >= 0 && articleList != null && articleList.get(pos - 2) != null) {
                articleList.get(pos - 2).setIsRead(true);
                listAdapter.notifyDataSetChanged();
                String key = articleList.get(pos - 2).getKey();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                String content_api = "/article/content";
                bundle.putString("content_api", content_api);
                String url;
                url = Const.HTTP_HEADKZ + "/app/multimedia/"+articleList.get(pos-2).getInfo_class()+"?key=" + key;
                bundle.putString("url", url);
                bundle.putString("title", "详情");
                bundle.putString("sharetitle", articleList.get(pos - 2).getTitle());
                bundle.putString("indexpic", articleList.get(pos - 2).getIndexpic());
                bundle.putString("type", articleList.get(pos-2).getInfo_class());
                bundle.putString("useropenid",HandApplication.user.getOpenid());
//                ActivityUtils.to(getActivity(), NewsArticleActivity.class, bundle);
                ActivityUtils.to(ArimagelistActivity.this, ContentWebviewActivity.class, bundle);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        MyVolley.getInstance(this).cancelPendingRequests(id);
        MyVolley.getInstance(this).cancelPendingRequests(Const.FOCUS);
    }



    @Override
    public void onDestroy() {
//        cancelRefreshTask();
        super.onDestroy();
    }
    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
    }
}
