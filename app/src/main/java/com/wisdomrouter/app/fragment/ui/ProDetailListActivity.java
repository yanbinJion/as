package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.ProjectDelListFragment;
import com.wisdomrouter.app.fragment.bean.ProjectDelListBean;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ChangeImgUrlUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CommonTabLayoutCustom;
import com.wisdomrouter.app.view.HomeViewPager;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ProDetailListActivity extends BaseActivity {

    @Bind(R.id.tab)
    CommonTabLayoutCustom tab;
    @Bind(R.id.vp_content)
    HomeViewPager vpContent;
    @Bind(R.id.rl_lb)
    RelativeLayout rlLb;
    @Bind(R.id.iv_project)
    RatioImageView ivProject;
    @Bind(R.id.scrollView)
    ScrollView scrollView;
    @Bind(R.id.tv_desc)
    TextView tvDesc;
    private ProjectDelListBean voGlobal;
    private String key, title;
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] titiles;
    private Context mContext = this;
    private Gson gson;
    String behavior;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_projectdel_list);
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
//        scrollView.setOnScrollChangeListener(new View.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                if(scrollX-oldScrollX==vpContent.getHeight()){
//
//                }
//            }
//        });
//        mSnsPostListener = new SocializeListeners.SnsPostListener() {
//            @Override
//            public void onStart() {
//                Toast.makeText(ProDetailListActivity.this, "开始分享",
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void onComplete(SHARE_MEDIA platform, int eCode,
//                                   SocializeEntity entity) {
//                if (eCode == 200) {
//                    //TODO 编写相应的对接接口
//                    String openid=null;
//                    Toast.makeText(ProDetailListActivity.this, platform+"分享成功",
//                            Toast.LENGTH_SHORT).show();
//                    UserDao.Userinfo userinfo= HandApplication.getInstance().mSpUtil
//                            .getAccount();
//                    if(userinfo!=null||!userinfo.equals("")){
//                        openid = userinfo.getOpenid();
//                        behavior=ShareUtils.behavior;
//
//                    }
////                    getShareScode(openid,behavior);
//
//                }else{
//                    Toast.makeText(ProDetailListActivity.this, platform+"分享失败",
//                            Toast.LENGTH_SHORT).show();
//                }
//            }
//        };
//
//        mController.registerListener(mSnsPostListener);
    }



    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();

    private void initPager(List<ProjectDelListBean.Detail> nameList) {
        fragmentList.clear();
        int k = 0;
        titiles = new String[nameList.size()];
        if (nameList.size() > 0) {
            for (ProjectDelListBean.Detail name : nameList) {
                if (name != null && !TextUtils.isEmpty(name.getKey())) {
                    titiles[k] = name.getListname();
                    mTabEntities.add(new TabEntity(name.getListname()));
                    fragmentList.add(ProjectDelListFragment.getInstance(name.getKey(),scrollView));
                    k++;
                }
            }
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
                Log.e("response",response);
                voGlobal = gson.fromJson(response, ProjectDelListBean.class);

                if (voGlobal != null) {
                    initTitleBar(voGlobal.getSpecialname(), R.drawable.img_share, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ShareUtils.shareSdk(ProDetailListActivity.this, voGlobal.getSpecialname(), voGlobal.getDesc(), voGlobal.getPic(), key, Const.SHARE_API.PROJECT,umShareListener);
                        }
                    });

                    SpannableString styledText = new SpannableString("导语 "+voGlobal.getDesc());
                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.style0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    styledText.setSpan(new TextAppearanceSpan(mContext, R.style.style1), 2, styledText.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    tvDesc.setText(styledText, TextView.BufferType.SPANNABLE);

                    initHead(voGlobal.getPic());//初始化头部
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
        GlobalTools.getProjectContentList(volleyRequest, key);
    }


    private void initHead(String topPic) {
        if (TextUtils.isEmpty(topPic)) {
            rlLb.setVisibility(View.GONE);
        } else {
            ivProject.setAspectRatio(1.5f);
            Glide.with(mContext).load(ChangeImgUrlUtils.nativetoslt(topPic, "600", "400")).placeholder(R.drawable.load_default).error(R.drawable.load_default).into(ivProject);
        }

    }


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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);

    }
}
