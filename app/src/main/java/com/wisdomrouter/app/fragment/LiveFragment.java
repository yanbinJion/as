
package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CardsAnimationAdapter;
import com.wisdomrouter.app.fragment.adapter.LiveAdapter;
import com.wisdomrouter.app.fragment.bean.LiveListDao;
import com.wisdomrouter.app.fragment.ui.ContentWebviewActivity;
import com.wisdomrouter.app.fragment.ui.LiveActivity;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.tools.VolleyHttpRequest;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.ViewPaperListView;


import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LiveFragment extends Fragment implements OnRefreshListener,
        OnLoadMoreListener, OnItemClickListener {
    private View mNews;
    private ViewPaperListView listView;

    private int page = 1;
    private int pageSize = 8;
    // 新闻内容
    private List<LiveListDao> articleList = new ArrayList<>();
    // 新闻适配器
    private LiveAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mNews) {
            mNews = inflater.inflate(R.layout.fragment_live, container, false);
        }
        return mNews;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        getNews(page, pageSize);
    }

    private void initView() {
        listView = (ViewPaperListView) mNews.findViewById(R.id.mViewPager);
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setOnItemClickListener(this);
        listAdapter = new LiveAdapter(getActivity(), articleList);
        AnimationAdapter animationAdapter = new CardsAnimationAdapter(
                listAdapter);
        animationAdapter.setAbsListView(listView);
        listView.setAdapter(animationAdapter);
        articleList.clear();

    }


    private void getNews(int page, int pageSize) {
        String url = Const.HTTP_HEADKZ + "/plugin/weilive-api/list?page=" + page + "&pagesize=" + pageSize;
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                Type type = new TypeToken<List<LiveListDao>>() {
                }.getType();
                List<LiveListDao> list = new Gson().fromJson(response, type);
                handleMessage(list);
            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                listView.onRefreshComplete();
                listView.onLoadMoreComplete();
                WarnUtils.toast(getActivity(), "获取数据失败" + error);
            }
        };

        VolleyHttpRequest.String_request(url, volleyRequest);

    }

    private void handleMessage(List<LiveListDao> list) {
        if (page == 1) {
            listView.onRefreshComplete();
            if (list != null && list.size() > 0) {
                articleList.addAll(list);
            }
            listAdapter.notifyDataSetChanged();
        } else {
            listView.onLoadMoreComplete();
            if (list != null) {
                if (list.size() == 0) {
                    WarnUtils.toast(getActivity(), "加载完成！");
                }
                articleList.addAll(list);
            }
            listAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        articleList.clear();
        getNews(page, pageSize);
    }

    @Override
    public void onLoadMore() {
        page++;
        getNews(page, pageSize);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        if (pos - 1 >= 0) {
//            articleList.get(pos - 1).setIsRead(true);
//            listAdapter.notifyDataSetChanged();
//            String key = articleList.get(pos - 1).getKey();
//            Bundle bundle = new Bundle();
//            bundle.putString("key", key);
//            bundle.putString("pic", articleList.get(pos - 1).getIndexpic());
//            bundle.putString("title", "直播内容");
//            bundle.putString("url", Const.HTTP_HEADKZ+ Const.LIVECONTENT_URL);
//            ActivityUtils.to(getActivity(), LiveActivity.class, bundle);
            Bundle bundle = new Bundle();
            bundle.putString("key", articleList.get(pos - 1).getKey());
            bundle.putString("content_api", "/article/content");
            String url;
            if (HandApplication.user != null && !TextUtils.isEmpty(HandApplication.user.getOpenid()))
                url = Const.HTTP_HEADKZ + "/plugin/weilive?key=" + articleList.get(pos - 1).getKey() + "&user_openid=" + HandApplication.user.getOpenid();
            else
                url = Const.HTTP_HEADKZ + "/plugin/weilive?key=" + articleList.get(pos - 1).getKey();

            bundle.putString("url", url);
            bundle.putString("title", "详情");
            bundle.putString("sharetitle", articleList.get(pos - 1).getTitle());
            bundle.putString("sharedesc", articleList.get(pos - 1).getContent());
            bundle.putString("indexpic", articleList.get(pos - 1).getBg_image());
            bundle.putString("type", "welive");
            ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
        }

    }


}
