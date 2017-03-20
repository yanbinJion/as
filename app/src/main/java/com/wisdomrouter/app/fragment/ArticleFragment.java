package com.wisdomrouter.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.flyco.tablayout.SlidingTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.ui.SelectColumnActivity;
import com.wisdomrouter.app.utils.SharePreferenceUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.SlidingTabLayoutCustom;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 新闻模块fragment
 */
public class ArticleFragment extends BaseFragment {
    @Bind(R.id.tab)
    SlidingTabLayoutCustom tab;
    @Bind(R.id.ll_more_columns)
    LinearLayout llMoreColumns;
    @Bind(R.id.vp_content)
    ViewPager vpContent;
    private View mNews;

    private List<AppConfigBean.Module.Channel> channelListcu2 = new ArrayList<>();
    private List<AppConfigBean.Module.Channel> channelListUnred2 = new ArrayList<>();
    private List<AppConfigBean.Module.Channel> channelListcu = new ArrayList<>();
    private List<AppConfigBean.Module.Channel> channelListcus = new ArrayList<>();
    private List<AppConfigBean.Module.Channel> channelListUnred = new ArrayList<>();
    private List<AppConfigBean.Module.Channel> channelListUnreds = new ArrayList<>();
    Gson gson;

    List<AppConfigBean.Module.Channel> listChannels = new ArrayList<>();

    public static ArticleFragment getInstance(List<AppConfigBean.Module.Channel> listChannels) {
        ArticleFragment sf = new ArticleFragment();
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
            mNews = inflater.inflate(R.layout.fragment_article, null);
        }

        ButterKnife.bind(this, mNews);
        return mNews;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }
    /**
     * 自定义栏目保存到缓存中
     */
    public void saveCustomColum() {
        channelListcu.clear();
        channelListUnred.clear();
        channelListcu2.clear();
        channelListUnred2.clear();
        channelListcu2 = HandApplication.getInstance().mSpUtil
                .getReadcolumn();
        channelListUnred2 = HandApplication.getInstance().mSpUtil
                .getUnReadcolumn();
        List<AppConfigBean.Module.Channel> channeldels = new ArrayList<>();
        channeldels.addAll(channelListcu2);
        channeldels.addAll(channelListUnred2);
        if((channeldels.size())!=listChannels.size()){
            SharePreferenceUtil.cleartitle();
        }
        // 缓存中已读列表为空
        if (HandApplication.getInstance().mSpUtil.getReadcolumn().isEmpty()) {
            if (listChannels.size() < 6) {//默认前五条为已读栏目
                channelListcu.addAll(listChannels);
            } else {
                for (int i = 0; i < listChannels.size(); i++) {
                    if (i < 5) {
                        channelListcu.add(listChannels.get(i));
                    } else {
                        channelListUnred.add(listChannels.get(i));
                    }
                }
            }
            HandApplication.getInstance().mSpUtil.saveReadcolumn(gson.toJson(channelListcu));
            HandApplication.getInstance().mSpUtil.saveUnReadcolumn(gson.toJson(channelListUnred));

        } else {

            channelListcu = HandApplication.getInstance().mSpUtil
                    .getReadcolumn();
            channelListUnred = HandApplication.getInstance().mSpUtil
                    .getUnReadcolumn();
//            if(channelListcus.size()!=channelListcu.size()){
//                if(channelListcus.size()>channelListcu.size()){
//                    int l = channelListcus.size() - channelListcu.size();
//                    for(int m=0;m<l;m++){
//                        channelListcu.add(channelListcus.get(channelListcu.size()+m));
//                    }
//                }
//            }
//            for(int k=0;k<channelListcus.size();k++){
//
//            }
            //判断客户端的已读栏目名字是否统一
            for (int i = 0; i < listChannels.size(); i++) {

                for (int j = 0; j < channelListcu.size(); j++) {
                    if (!channelListcu.get(j).getName().equals(listChannels.get(i).getName()) && channelListcu.get(j).getKey().equals(listChannels.get(i).getKey())) {
                        channelListcu.set(j, listChannels.get(i));
                    }
                }
            }
            List<AppConfigBean.Module.Channel> channeldel = new ArrayList<>();
            //删除后台已经删除过的栏目
            for (int i = 0; i < listChannels.size(); i++) {
                for (int j = 0; j < channelListcu.size(); j++) {
                    if (channelListcu.get(j).getKey().equals(listChannels.get(i).getKey())) {
                        channeldel.add(listChannels.get(i));
                        continue;
                    }
                }
            }
            channelListcu.clear();
            channelListcu.addAll(channeldel);


            //判断客户端的未读栏目名字是否统一
            if (channelListUnred != null && channelListUnred.size() > 0) {
                for (int i = 0; i < listChannels.size(); i++) {
                    for (int j = 0; j < channelListUnred.size(); j++) {
                        if (!channelListUnred.get(j).getName().equals(listChannels.get(i).getName()) && channelListUnred.get(j).getKey().equals(listChannels.get(i).getKey())) {
                            channelListUnred.set(j, listChannels.get(i));
                        }
                    }
                }
                List<AppConfigBean.Module.Channel> channeldel2 = new ArrayList<>();
                //删除后台已经删除过的栏目
                for (int i = 0; i < listChannels.size(); i++) {
                    for (int j = 0; j < channelListUnred.size(); j++) {
                        if (channelListUnred.get(j).getKey().equals(listChannels.get(i).getKey())) {
                            channeldel2.add(listChannels.get(i));
                            continue;
                        }
                    }
                }
                channelListUnred.clear();
                channelListUnred.addAll(channeldel2);
            }

            HandApplication.getInstance().mSpUtil.saveUnReadcolumn(gson.toJson(channelListUnred));
            HandApplication.getInstance().mSpUtil.saveReadcolumn(gson.toJson(channelListcu));

        }
//        titiles = new String[channelListcu.size()];
//        tab.setViewPager(vpContent, titiles, this.getActivity(), fragmentList);


        initPager(channelListcu, 0);

    }

//    /**
//     * 自定义栏目保存到缓存中
//     */
//    public void saveCustomColum() {
//        channelListcu.clear();
//        channelListcus.clear();
//        channelListUnreds.clear();
//        channelListUnred.clear();
//        // 缓存中已读列表为空
//        if (HandApplication.getInstance().mSpUtil.getReadcolumn().isEmpty()) {
//            if (listChannels.size() < 6) {//默认前五条为已读栏目
//                channelListcu.addAll(listChannels);
//            } else {
//                for (int i = 0; i < listChannels.size(); i++) {
//                    if (i < 5) {
//                        channelListcu.add(listChannels.get(i));
//                    } else {
//                        channelListUnred.add(listChannels.get(i));
//                    }
//                }
//            }
//            HandApplication.getInstance().mSpUtil.saveReadcolumn(gson.toJson(channelListcu));
//            HandApplication.getInstance().mSpUtil.saveUnReadcolumn(gson.toJson(channelListUnred));
//
//        } else {
//            if (listChannels.size() < 6) {//默认前五条为已读栏目
//                channelListcus.addAll(listChannels);
//            } else {
//                for (int i = 0; i < listChannels.size(); i++) {
//                    if (i < 5) {
//                        channelListcus.add(listChannels.get(i));
//                    } else {
//                        channelListUnreds.add(listChannels.get(i));
//                    }
//                }
//            }
//            channelListcu = HandApplication.getInstance().mSpUtil
//                    .getReadcolumn();
//            channelListUnred = HandApplication.getInstance().mSpUtil
//                    .getUnReadcolumn();
//            List<AppConfigBean.Module.Channel> channeldels = new ArrayList<>();
//            channeldels.addAll(channelListcu);
//            channeldels.addAll(channelListUnred);
//            Log.e("channeldes",channeldels.size()+"       "+listChannels.size());
//
//            //判断客户端的已读栏目名字是否统一
//            if(channelListcus.size()>=channelListcu.size()){
//
//
//            for (int i = 0; i < channelListcu.size(); i++) {
////                for (int j = 0; j < channelListcu.size(); j++) {
//                    if (!channelListcu.get(i).getName().equals(channelListcus.get(i).getName()) && channelListcu.get(i).getKey().equals(channelListcus.get(i).getKey())) {
//                        channelListcu.set(i, channelListcus.get(i));
////                    }
//                }
//
//             }
//                int m = channelListcus.size() - channelListcu.size();
//                List<AppConfigBean.Module.Channel> channeldel = new ArrayList<>();
//                for(int l=0;l<m;l++){
//                    channeldel.add(channelListcus.get(channelListcu.size()+l));
//                }
//                channelListcu.addAll(channeldel);
//            }
//            else if((channelListcus.size()<channelListcu.size())&&((channeldels.size())!=listChannels.size())){
//                SharePreferenceUtil.cleartitle();
//                for (int i = 0; i < channelListcus.size(); i++) {
//                for (int j = 0; j < channelListcu.size(); j++) {
//                    if (!channelListcu.get(i).getName().equals(listChannels.get(i).getName()) && channelListcu.get(i).getKey().equals(listChannels.get(i).getKey())) {
//                        channelListcu.set(i, listChannels.get(i));
//                    }
//                    }
//                }
//                int m = channelListcu.size() - channelListcus.size();
//                for(int l=0;l<m;l++){
//                    channelListcu.remove(channelListcus.size()-1+l);
//                }
//
//            }
//
////            List<AppConfigBean.Module.Channel> channeldel4 = new ArrayList<>();
////            //删除后台已经删除过的栏目
////            for (int i = 0; i < channelListcus.size(); i++) {
////                for (int j = 0; j < channelListcu.size(); j++) {
////                    if (channelListcu.get(j).getKey().equals(channelListcus.get(i).getKey())) {
////                        channeldel4.add(channelListcus.get(i));
////                        continue;
////                    }
////                }
////            }
////            channelListcu.clear();
////            channelListcu.addAll(channeldel4);
//
//
//            //判断客户端的未读栏目名字是否统一
//            if (channelListUnred != null && channelListUnreds.size() > 0) {
//                if(channelListUnreds.size()>=channelListUnred.size()){
//                    for(int i=0;i<channelListUnred.size();i++){
//                        if (!channelListUnred.get(i).getName().equals(channelListUnreds.get(i).getName()) && channelListUnred.get(i).getKey().equals(channelListUnreds.get(i).getKey())) {
//                            channelListUnred.set(i, channelListUnreds.get(i));
//                        }
//                    }
//                    int j = channelListUnreds.size() - channelListUnred.size();
//                    List<AppConfigBean.Module.Channel> channeldel2 = new ArrayList<>();
//                    for(int i=0;i<j;i++){
//                        channeldel2.add(channelListUnreds.get(channelListUnred.size()+i));
//                    }
//                    channelListUnred.addAll(channeldel2);
//                }
//                else if((channelListUnreds.size()<channelListUnred.size())&&((channeldels.size())!=listChannels.size())){
//                    for(int i=0;i<channelListUnreds.size();i++){
//                        if (!channelListUnred.get(i).getName().equals(channelListUnreds.get(i).getName()) && channelListUnred.get(i).getKey().equals(channelListUnreds.get(i).getKey())) {
//                            channelListUnred.set(i, channelListUnreds.get(i));
//                        }
//                    }
//                    int j = channelListUnred.size() - channelListUnreds.size();
//
//                    for(int i=0;i<j;i++){
//                        channelListUnred.remove(channelListUnreds.size()+i);
//                    }
//
//                }
////                for (int i = 0; i < channelListUnreds.size(); i++) {
////                    for (int j = 0; j < channelListUnred.size(); j++) {
////                        if (!channelListUnred.get(j).getName().equals(channelListUnreds.get(i).getName()) && channelListUnred.get(j).getKey().equals(listChannels.get(i).getKey())) {
////                            channelListUnred.set(j, channelListUnreds.get(i));
////                        }
////                    }
////                }
//                List<AppConfigBean.Module.Channel> channeldel3 = new ArrayList<>();
////                删除后台已经删除过的栏目
//                for (int i = 0; i < channelListUnreds.size(); i++) {
//                    for (int j = 0; j < channelListUnred.size(); j++) {
//                        if (channelListUnred.get(j).getKey().equals(channelListUnreds.get(i).getKey())) {
//                            channeldel3.add(channelListUnreds.get(i));
//                            continue;
//                        }
//                    }
//                }
//                channelListUnred.clear();
//                channelListUnred.addAll(channeldel3);
////            }
//    }
////
//
//            HandApplication.getInstance().mSpUtil.saveUnReadcolumn(gson.toJson(channelListUnred));
//            HandApplication.getInstance().mSpUtil.saveReadcolumn(gson.toJson(channelListcu));
//
//        }
////        titiles = new String[channelListcu.size()];
////        tab.setViewPager(vpContent, titiles, this.getActivity(), fragmentList);
//
//
//        initPager(channelListcu, 0);
//
//    }

    private ArrayList<Fragment> fragmentList = new ArrayList<>();
    String[] titiles;

    private void initPager(List<AppConfigBean.Module.Channel> channelItems, int currentpos) {
//        mTabEntities.clear();
        fragmentList.clear();
        int k = 0;
        titiles = new String[channelItems.size()];
        for (AppConfigBean.Module.Channel channel : channelItems) {
            if (channel != null && !TextUtils.isEmpty(channel.getKey())) {
//                mTabEntities.add(new TabEntity(channel.getName()));
                titiles[k] = channel.getName();
                fragmentList.add(ArticleListFragment.getInstance(channel.getFocus_map(), channel.getKey()));
                k++;
            }
        }
        tab.setViewPager(vpContent, titiles, this.getActivity(), fragmentList);

    }

    private void initPager2(List<AppConfigBean.Module.Channel> channelItems, final int currentpos) {
//        mTabEntities.clear();
        fragmentList.clear();
        int k = 0;

        titiles = new String[channelItems.size()];
        for (AppConfigBean.Module.Channel channel : channelItems) {
            if (channel != null && !TextUtils.isEmpty(channel.getKey())) {
//                mTabEntities.add(new TabEntity(channel.getName()));
                titiles[k] = channel.getName();

                fragmentList.add(ArticleListFragment.getInstance(channel.getFocus_map(), channel.getKey()));
                k++;
            }

        }
        Log.e("channeldes",titiles.length+" ");
        currentIndex = currentpos;
        tab.setViewPager(vpContent,titiles);
        vpContent.getAdapter().notifyDataSetChanged();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tab.notifyDataSetChanged();
                tab.setCurrentTab(currentIndex);

            }
        }, 1500);

    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message message) {
            switch (message.what) {
                case 1://刷新适配器1
                    break;
            }
            return false;
        }
    });

    private void initView() {
        if (listChannels == null || listChannels.size() == 0) {
//            WarnUtils.toast(getActivity(), "该模块下暂无栏目信息!listChannels==null");
            return;
        }
        saveCustomColum();


        llMoreColumns.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                initChannelPop();
            }
        });

        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                currentIndex = position;
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    int currentIndex = 0;

    /**
     * 初始化PopupWindow
     */
    public void initChannelPop() {
        //系统当前的栏目
        Intent intent = new Intent();
        intent.putExtra("currentIndex", currentIndex);
        intent.setClass(getActivity(), SelectColumnActivity.class);
        startActivityForResult(intent, 1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //点击栏目返回
        if (requestCode == 1 && resultCode == this.getActivity().RESULT_OK) {
            channelListcu = HandApplication.getInstance().mSpUtil.getReadcolumn();
            int currentIndex = intent.getIntExtra("currentIndex", 0);
            initPager2(channelListcu, currentIndex);

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
