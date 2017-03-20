package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;


import com.flyco.tablayout.listener.CustomTabEntity;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.BaseActivity;

import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ProjectDelListBean;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CommonTabLayoutCustom;
import com.wisdomrouter.app.view.SlidingTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专题表页
 */
public class SingleActivity extends BaseActivity {
    @Bind(R.id.slidetab)
    SlidingTabLayoutCustom slidtab;
    @Bind(R.id.vp_content)
    ViewPager vpContent;
    @Bind(R.id.comtab)
    CommonTabLayoutCustom comtab;
    @Bind(R.id.fl_content)
    FrameLayout flContent;
    private View mImage;
    private Gson gson;
    private String key, title;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] titiles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private Boolean ishowvp = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.fragment_content);
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
            WarnUtils.toast(this, "暂无数据");
            return;
        }
        gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
        getdata();
    }
    /**
     * 获取数据
     */
    private void getdata() {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                voGlobal = gson.fromJson(response, ProjectDelListBean.class);
                if (voGlobal != null) {
                    initTitleBar(voGlobal.getSpecialname(), R.drawable.img_share, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
//                            ShareUtils.shareSdk(SingleActivity.this, voGlobal.getSpecialname(), voGlobal.getDesc(), voGlobal.getPic(), key, Const.SHARE_API.PROJECT,umShareListener);
                        }
                    });

//                    SpannableString styledText = new SpannableString("导语 "+voGlobal.getDesc());
//                    styledText.setSpan(new TextAppearanceSpan(SingleActivity.this, R.style.style0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//                    styledText.setSpan(new TextAppearanceSpan(SingleActivity.this, R.style.style1), 2, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

//                    tvDesc.setText(styledText, TextView.BufferType.SPANNABLE);

//                    initHead(voGlobal.getPic());//初始化头部
                    initViewPager(voGlobal.getDetail());
                } else {
                    WarnUtils.toast(SingleActivity.this, "获取数据失败 voGlobal==null");
                }
            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(SingleActivity.this, "获取数据失败" + error);
            }
        };
        GlobalTools.getProjectContentList(volleyRequest, key);
    }

    private ProjectDelListBean voGlobal;
    private void initViewPager(List<ProjectDelListBean.Detail> nameList) {
        fragmentList.clear();
        mTabEntities.clear();
        int k = 0;
        titiles = new String[nameList.size()];
        for (ProjectDelListBean.Detail name : nameList) {
            if (name != null && !TextUtils.isEmpty(name.getKey())) {
                titiles[k] = name.getName();
//                fragmentList.add(ProjectDelListFragment.getInstance(name.getKey()));
                mTabEntities.add(new TabEntity(name.getName()));
                k++;
            }
        }
        if (ishowvp) {
            slidtab.setViewPager(vpContent, titiles, this, fragmentList);
            slidtab.setVisibility(View.VISIBLE);
            vpContent.setVisibility(View.VISIBLE);
            comtab.setVisibility(View.GONE);
            flContent.setVisibility(View.GONE);
        } else {
            comtab.setTabData(mTabEntities, this, R.id.fl_content, fragmentList);
            comtab.setVisibility(View.VISIBLE);
            flContent.setVisibility(View.VISIBLE);
            slidtab.setVisibility(View.GONE);
            vpContent.setVisibility(View.GONE);
        }

        if (nameList.size() < 2) {
            slidtab.setVisibility(View.GONE);
            comtab.setVisibility(View.GONE);
        }
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
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
