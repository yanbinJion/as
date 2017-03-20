package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.flyco.tablayout.listener.CustomTabEntity;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.AppConfigBean.Module.Channel;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.view.CommonTabLayoutCustom;
import com.wisdomrouter.app.view.SlidingTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 图片集列表页
 */
public class VideoFragment extends Fragment {
    @Bind(R.id.slidetab1)
    SlidingTabLayoutCustom slidtab;
    @Bind(R.id.vp_content1)
    ViewPager vpContent;
    @Bind(R.id.comtab1)
    CommonTabLayoutCustom comtab;
    @Bind(R.id.fl_content1)
    FrameLayout flContent;
    private View mImage;
    // 用户选择的新闻分类列表
    private List<Channel> channelList = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    private String[] titiles;
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    Boolean ishowvp = false;

    public static VideoFragment getInstance(List<Channel> listChannels) {
        VideoFragment sf = new VideoFragment();
        sf.channelList = listChannels;
        return sf;
    }

    public static VideoFragment getInstance(List<Channel> listChannels, Boolean ishowvp) {
        VideoFragment sf = new VideoFragment();
        sf.channelList = listChannels;
        sf.ishowvp = ishowvp;
        return sf;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mImage) {
            mImage = inflater.inflate(R.layout.fragment_video, null);
        }
        ButterKnife.bind(this, mImage);
        return mImage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (channelList == null || channelList.size() == 0) {
//            WarnUtils.toast(getActivity(), "该模块下暂无信息!");
            slidtab.setVisibility(View.GONE);
            comtab.setVisibility(View.GONE);
            return;
        }
        initData(channelList);
    }

    private void initData(List<Channel> channelItems) {
        fragmentList.clear();
        mTabEntities.clear();
        int k = 0;
        titiles = new String[channelItems.size()];
        for (Channel channel : channelItems) {
            if (channel != null && !TextUtils.isEmpty(channel.getKey())) {
                titiles[k] = channel.getName();
                fragmentList.add(VideoListFragment.getInstance(channel.getKey()));
//                fragmentList.add(ImageListFragment.getInstance(channel.getKey()));
                mTabEntities.add(new TabEntity(channel.getName()));
                k++;
            }
        }
        if (ishowvp) {
            slidtab.setViewPager(vpContent, titiles, this.getActivity(), fragmentList);
            comtab.setVisibility(View.GONE);
            flContent.setVisibility(View.GONE);
        } else {
            comtab.setTabData(mTabEntities, this.getActivity(), R.id.fl_content1, fragmentList);
            slidtab.setVisibility(View.GONE);
            vpContent.setVisibility(View.GONE);
        }
//
        if (channelItems.size() < 2) {
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
