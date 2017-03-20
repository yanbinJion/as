package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMSocialService;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.ProjectDelListFragment;
import com.wisdomrouter.app.fragment.adapter.NewsAutoScrollPagerAdapter;
import com.wisdomrouter.app.fragment.bean.CountryDelListBean;
import com.wisdomrouter.app.fragment.bean.ShareScore;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.TDevice;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CommonTabLayoutCustom;
import com.wisdomrouter.app.view.autoscrollviewpager.AutoScrollViewPager;
import com.wisdomrouter.app.view.autoscrollviewpager.CirclePageIndicator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 镇区内容列表页
 */
public class CountryListNewsActivity extends BaseActivity {

    @Bind(R.id.tab)
    CommonTabLayoutCustom tab;
    @Bind(R.id.rl_lb)
    RelativeLayout rlLb;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.vp_news)
    AutoScrollViewPager vpNews;
    @Bind(R.id.tvSlideTitle)
    TextView tvSlideTitle;
    @Bind(R.id.pi_news_indicator)
    CirclePageIndicator pageIndicator;
    @Bind(R.id.town_ll)
    LinearLayout townLl;
    @Bind(R.id.rl_news)
    RelativeLayout rlNews;
    private CountryDelListBean voGlobal;
    private String key, title;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] titiles;
    private Context mContext = this;
    private Gson gson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_countrylist_new);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        ButterKnife.bind(this);
        if (getIntent() != null) {
            key = getIntent().getStringExtra("key");
            title = getIntent().getStringExtra("title");
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
        } else {
            WarnUtils.toast(mContext, "暂无数据");
            return;
        }

        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        getdata();
    }




    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private void initPager(List<CountryDelListBean.Detail> nameList) {
        fragmentList.clear();
        int k = 0;
        titiles = new String[nameList.size()];
        if (nameList.size() > 0) {
            for (CountryDelListBean.Detail name : nameList) {
                if (name != null && !TextUtils.isEmpty(name.getKey())) {
                    titiles[k] = name.getListname();
                    mTabEntities.add(new TabEntity(name.getListname()));
                    fragmentList.add(ProjectDelListFragment.getInstance(name.getKey(),scrollView));
                    k++;
                }
            }
//            tab.setTabData(titiles,this,R.id.fl_content,fragmentList);
//            tab.setViewPager(vpContent, titiles, this, fragmentList);
            tab.setTabData(mTabEntities, this, R.id.fl_content, fragmentList);

        }
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 0);
                    }
                });
            }

            @Override
            public void onTabReselect(int position) {
                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
                        scrollView.scrollTo(0, 0);
                    }
                });
            }
        });

    }

    /**
     * 获取数据
     */
    private void getdata() {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                if(response.equals("")||TextUtils.isEmpty(response)||response.equals("[]")){
                        return;
                }
                voGlobal = gson.fromJson(response, CountryDelListBean.class);
                if (voGlobal != null) {
                    initTitleBar(voGlobal.getTownname(), R.drawable.img_share, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareUtils.shareSdk(CountryListNewsActivity.this, voGlobal.getTownname(), voGlobal.getTowndesc(), voGlobal.getTownpic().get(0).getPic(), key, Const.SHARE_API.COUNTRY,umShareListener);
                        }
                    });

                    initHead(voGlobal.getTownpic());//初始化头部
                    initPager(voGlobal.getDetail());
                } else {
                    WarnUtils.toast(mContext, "获取数据失败 voGlobal==null");
                }
            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        GlobalTools.getCountryContentList(volleyRequest, key);
    }


    private void initHead(List<CountryDelListBean.Townpic> townpicList) {
        ViewGroup.LayoutParams layoutParams = vpNews.getLayoutParams();
        float screen = TDevice.getScreenWidth();
        layoutParams.height = (int) (screen * 2 / 3);
        vpNews.setLayoutParams(layoutParams);
        pageIndicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
//                if (titles != null && titles.size() >= 0) {
//                    tvSlideTitle.setText(titles.get(position));
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (townpicList != null && townpicList.size() > 0) {
            rlNews.setVisibility(View.VISIBLE);
            for (CountryDelListBean.Townpic newsList : townpicList) {
                urls.add(newsList.getPic());
            }
            vpNews.setAdapter(new NewsAutoScrollPagerAdapter(urls, mContext));
            pageIndicator.setViewPager(vpNews);
            pageIndicator.setSnap(true);
            vpNews.setOnPageClickListener(new AutoScrollViewPager.OnPageClickListener() {
                @Override
                public void onPageClick(AutoScrollViewPager pager, int position) {

                        Bundle bundle = new Bundle();
                        bundle.putString("img", urls.get(position));
                        bundle.putString("content",voGlobal.getTownpic().get(position).getDesc());
                        ActivityUtils.to(mContext, CountryImageActivity.class, bundle);
                }
            });

        } else {
            rlNews.setVisibility(View.GONE);
        }
    }
    List<String> urls=new ArrayList<>();

    @Override
    public void onDestroy() {
        super.onDestroy();
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);


    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
            WarnUtils.toast(mContext, "分享成功");
//            getShareScode(Const.SHARE_API.AVTIVITY);

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            WarnUtils.toast(mContext, "分享失败");
//            if (t != null) {
//                Log.d("throw", "throw:" + t.getMessage());
//            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            WarnUtils.toast(mContext, "分享取消了");
        }
    };
    ShareScore shareScore;

    private void getShareScode(String behavior) {
        UserDao.Userinfo userinfo = HandApplication.getInstance().mSpUtil
                .getAccount();
        if (userinfo != null && !TextUtils.isEmpty(userinfo.getOpenid())) {
            VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

                @Override
                public void reqSuccess(String response) {
                    // 成功的回调 : 可以操作返回的数据
                    try {
                        shareScore = new Gson().fromJson(response, ShareScore.class);
                        if (shareScore.getState() != null && shareScore.getState().equals("1") && shareScore.getScore() != 0)
                            WarnUtils.toast(mContext, shareScore.getMessage(), shareScore.getScore());
                    } catch (Exception e) {
                        WarnUtils.toast(mContext, "解析数据积分失败" + e.getMessage());
                    }

                }

                @Override
                public void reqError(String error) {
                    // 失败的回调 ：失败的提醒
                    WarnUtils.toast(mContext, "获取数据积分失败" + error);
                }
            };
            Map<String, String> map = new HashMap<>();
            map.put("user_openid", userinfo.getOpenid());
            map.put("behavior", behavior);
            GlobalTools.getShareScore(volleyRequest, map);
        }
    }
}
