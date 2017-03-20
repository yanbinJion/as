package com.wisdomrouter.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.utils.Log;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.TabEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.R.attr.fragment;

public class HomeFragment extends BaseFragment {
    @Bind(R.id.realcontent)
    FrameLayout realcontent;
    @Bind(R.id.left_content)
    FrameLayout leftContent;
    @Bind(R.id.tab)
    CommonTabLayout tab;
    @Bind(R.id.ll_bottom)
    LinearLayout llBottom;
    @Bind(R.id.tab_left)
    CommonTabLayout tabLeft;
    @Bind(R.id.ll_bottom_left)
    LinearLayout llBottomLeft;
    private View mHome;
    private ArrayList<Fragment> mFragments = new ArrayList<>();
    private ArrayList<Fragment> mFragments_left = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private ArrayList<CustomTabEntity> mTabEntities_left = new ArrayList<>();
    private Context mContext;
    private List<AppConfigBean.Module> moduleList = new ArrayList<>();
    private int pos = 0;
    private boolean isExites = false;
    private boolean isfromleft = false;
    private RequestManager requestManager;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public interface OnChangeTitleListener {
        void ChangeTitle(String s);
    }

    private OnChangeTitleListener listener;

    public void setOnChangeTitleListener(OnChangeTitleListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mHome || savedInstanceState == null) {
            mHome = inflater.inflate(R.layout.fragment_home, null);
        }
        ButterKnife.bind(this, mHome);
        mContext = this.getActivity();
        return mHome;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getArguments() != null) {
            pos = getArguments().getInt("pos", 0);
            isExites = getArguments().getBoolean("isExites", false);
            isfromleft = getArguments().getBoolean("isfromleft", false);
        }
        requestManager = Glide.with(getActivity().getApplicationContext());
        moduleList.addAll(HandApplication.getInstance().appConfigBean.getApp_main_btns());
        //后台配置了自定义按钮
        if (moduleList != null && moduleList.size() != 0) {
            refreshUI(moduleList);
        }

    }

    private void refreshUI(List<AppConfigBean.Module> list) {
        mFragments.clear();
        mFragments_left.clear();
        mTabEntities.clear();
        mTabEntities_left.clear();

        for (int i = 0; i < list.size(); i++) {
            mTabEntities.add(new TabEntity(list.get(i).getApp_btn_name() == null ? list.get(i).getTitle() : list.get(i).getApp_btn_name(), R.color.botoom_bar_bg, R.color.botoom_bar_bg));
            mTabEntities_left.add(new TabEntity(list.get(i).getApp_btn_name() == null ? list.get(i).getTitle() : list.get(i).getApp_btn_name(), R.color.botoom_bar_bg, R.color.botoom_bar_bg));
            String classname = list.get(i).getClassname();
            if(i==0){
                Log.e("classname",classname);
            }

            if (i == 0 && classname.equals(Const.HOME_API.ARTICALE)) {

                if (TextUtils.isEmpty(list.get(i).getListmodel()) || !list.get(i).getListmodel().equals("images")){
                    mFragments.add(ArticleFragment.getInstance(list.get(i).getChannel()));
                    mFragments_left.add(ArticleFragment.getInstance(list.get(i).getChannel()));
                }
                else if (!TextUtils.isEmpty(list.get(i).getListmodel()) && list.get(i).getListmodel().equals("images")) {

                    mFragments.add(ArticleImageFragment.getInstance(list.get(i).getChannel()));
                    mFragments_left.add(ArticleImageFragment.getInstance(list.get(i).getChannel()));
                }
            } else if (i != 0 && classname.equals(Const.HOME_API.ARTICALE)) {
                mFragments.add(FragmentArtOnly.getInstance(list.get(i).getChannel()));
                mFragments_left.add(FragmentArtOnly.getInstance(list.get(i).getChannel()));
            } else {
                mFragments.add(getFragment(classname, list.get(i)));
                mFragments_left.add(getFragment(classname, list.get(i)));
            }


        }
        if (mTabEntities.size() > 0) {
            tab.setTabData(mTabEntities, getActivity(), R.id.realcontent, mFragments);

            for (int i = 0; i < mTabEntities.size(); i++) {
//                requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                if (i == 0) {
                    requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tab.getIconView(i));
                }else {
                    requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                }
            }

            if (isfromleft) {//从左侧进入
                if (isExites) {//从左侧点击进入且跟底部按钮重合
                    if (pos != 0) {
                        tab.setCurrentTab(pos);
                        listener.ChangeTitle(moduleList.get(pos).getApp_btn_name());
                    }
                } else {//从左侧点击进入且跟底部按钮不吻合
                    llBottom.setVisibility(View.GONE);
                    realcontent.setVisibility(View.GONE);
                    leftContent.setVisibility(View.VISIBLE);
                    llBottomLeft.setVisibility(View.VISIBLE);
                    tabLeft.setTabData(mTabEntities_left, this.getActivity(), R.id.left_content, mFragments_left);
                    for (int i = 0; i < mTabEntities.size(); i++) {
                        requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tabLeft.getIconView(i));
//                        if (i==0)
//
//                            requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tabLeft.getIconView(i));
//                        else{
//                            requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tabLeft.getIconView(i));
//                        }
                    }
                    getActivity().getSupportFragmentManager().beginTransaction()
                            .add(R.id.left_content,
                                    getFragment(HandApplication.getInstance().appConfigBean.getModules().get(0).getClassname(),
                                            HandApplication.getInstance().appConfigBean.getModules().get(0))).commit();


                }
            }

        }

        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                tab.setCurrentTab(position);
                for (int i = 0; i < mTabEntities.size(); i++) {
//                    requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    if (i == position)

                        requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tab.getIconView(i));
                    else {
                        requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    }
                }
                listener.ChangeTitle(moduleList.get(position).getApp_btn_name());

            }

            @Override
            public void onTabReselect(int position) {
                tab.setCurrentTab(position);
                for (int i = 0; i < mTabEntities.size(); i++) {
                    requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    if (i == position)

                        requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tab.getIconView(i));
                    else {
                        requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    }
                }
                listener.ChangeTitle(moduleList.get(position).getApp_btn_name());
            }
        });
        tabLeft.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                llBottom.setVisibility(View.VISIBLE);
                realcontent.setVisibility(View.VISIBLE);
                leftContent.setVisibility(View.GONE);
                llBottomLeft.setVisibility(View.GONE);
                tab.setCurrentTab(position);
                for (int i = 0; i < mTabEntities.size(); i++) {
//                    requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    if (i == position)

                        requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tab.getIconView(i));
                    else {
                        requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    }
                }
                listener.ChangeTitle(moduleList.get(position).getApp_btn_name());
            }

            @Override
            public void onTabReselect(int position) {
                llBottom.setVisibility(View.VISIBLE);
                realcontent.setVisibility(View.VISIBLE);
                leftContent.setVisibility(View.GONE);
                llBottomLeft.setVisibility(View.GONE);
                tab.setCurrentTab(position);
                listener.ChangeTitle(moduleList.get(pos).getApp_btn_name());
                for (int i = 0; i < mTabEntities.size(); i++) {
//                    requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    if (i == position)

                        requestManager.load(moduleList.get(i).getApp_btn_click_icon()).into(tab.getIconView(i));
                    else {
                        requestManager.load(moduleList.get(i).getApp_btn_icon()).into(tab.getIconView(i));
                    }
                }
                listener.ChangeTitle(moduleList.get(position).getApp_btn_name());
            }
        });

        if (moduleList.size() == 1) {
            tab.setVisibility(View.GONE);
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


}
