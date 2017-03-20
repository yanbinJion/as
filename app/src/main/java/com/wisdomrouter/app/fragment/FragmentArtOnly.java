package com.wisdomrouter.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.flyco.tablayout.SlidingTabLayout;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.LifeTypeAdapter;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.SlidingTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentArtOnly extends Fragment {
    private View mNews;
    @Bind(R.id.tab)
    SlidingTabLayoutCustom tab;
    @Bind(R.id.ll_more_columns)
    LinearLayout llMoreColumns;
    @Bind(R.id.vp_content)
    ViewPager vpContent;
    List<Channel> listChannels = new ArrayList<>();
    Gson gson;

    public static FragmentArtOnly getInstance(List<AppConfigBean.Module.Channel> listChannels) {
        FragmentArtOnly sf = new FragmentArtOnly();
        sf.listChannels = listChannels;
        return sf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mNews) {
            mNews = inflater.inflate(R.layout.fragment_articleonly, null);
            ButterKnife.bind(this, mNews);
        }
        return mNews;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] titiles;

    private void initPager(List<Channel> channelItems) {
        fragmentList.clear();
        int k = 0;
        titiles = new String[channelItems.size()];
        for (AppConfigBean.Module.Channel channel : channelItems) {
            if (channel != null && !TextUtils.isEmpty(channel.getKey())) {
                titiles[k] = channel.getName();
                fragmentList.add(ArticleListFragment.getInstance(channel.getFocus_map(), channel.getKey()));
                k++;
            }
        }
        tab.setViewPager(vpContent, titiles, this.getActivity(), fragmentList);
    }

    private void initView() {
        if (listChannels == null || listChannels.size() == 0) {
            WarnUtils.toast(getActivity(), "该模块下暂无栏目信息!listChannels==null");
            return;
        }
        if(listChannels.size()>=4){
           llMoreColumns.setVisibility(View.VISIBLE);
            llMoreColumns.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    initChannelPop();
                }
            });

        }
        initPager(listChannels);
    }

    private PopupWindow popupwindow = null;
    private LifeTypeAdapter townAdapter = null;

    /**
     * 初始化PopupWindow
     */
    public void initChannelPop() {
        if (popupwindow == null) {

            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(getActivity().LAYOUT_INFLATER_SERVICE);
            LinearLayout view = (LinearLayout) inflater.inflate(
                    R.layout.popwindow_channel, null);
            /** 在这里可以实现自定义视图的功能 */
            GridView gvType = (GridView) view.findViewById(R.id.gvType);
            int count = listChannels.size();
            townAdapter = new LifeTypeAdapter(getActivity(), listChannels);
            gvType.setAdapter(townAdapter);
            gvType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int pos, long arg3) {
                    tab.setCurrentTab(pos);
                    popupwindow.dismiss();
                }
            });

            // 创建PopupWindow实例,200,150分别是宽度和高度
            popupwindow = new PopupWindow(view,
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.MATCH_PARENT);
            // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
            popupwindow.setAnimationStyle(R.style.AnimationFade);
            popupwindow.showAsDropDown(llMoreColumns);
            popupwindow.setFocusable(true);
            popupwindow.setTouchable(true);
            popupwindow.setOutsideTouchable(true);
            popupwindow.update();
            // 自定义view添加触摸事件
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (popupwindow != null && popupwindow.isShowing()) {
                        popupwindow.dismiss();
                        popupwindow = null;
                    }
                    return false;
                }
            });
        } else {
            popupwindow.showAsDropDown(llMoreColumns);
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
