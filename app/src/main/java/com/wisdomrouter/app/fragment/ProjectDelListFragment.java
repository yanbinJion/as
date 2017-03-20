package com.wisdomrouter.app.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CardsAnimationAdapter;
import com.wisdomrouter.app.fragment.adapter.NewsAdapter;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.NewArticleListDao;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.fragment.ui.ContentWebviewActivity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyUtils.GsonRequestGet;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.BaseListView;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.ViewPaperOnmeasureListView;

import java.lang.ref.WeakReference;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ProjectDelListFragment extends BaseFragment implements PullRefreshListView.OnRefreshListener,
        PullRefreshListView.OnLoadMoreListener, AdapterView.OnItemClickListener {
    @Bind(R.id.mViewPager)
    ViewPaperOnmeasureListView listView;
    private View mView;
    private List<ArticleListDao> voGlobal=new ArrayList<>();
    private int page = 1;
    private int pageSize = 5;
    // 新闻内容
    private List<ArticleListDao> articleList = new ArrayList<>();
    // 新闻适配器
    private NewsAdapter listAdapter;
    GlobalTools globalTool;

    private String key = "";
//    private String name = "";
    private ScrollView view;

    public static ProjectDelListFragment getInstance(String key, ScrollView view) {
        ProjectDelListFragment sf = new ProjectDelListFragment();
        sf.view=view;
        sf.key = key;
//        sf.name = name;
        return sf;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_projectdel_list, container, false);
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListData(page, pageSize);
    }

    private void initView() {
        globalTool = new GlobalTools(getActivity());
        listAdapter = new NewsAdapter(getActivity(), articleList);
//        AnimationAdapter animationAdapter = new CardsAnimationAdapter(
//                listAdapter);
//        animationAdapter.setAbsListView(listView);
        listView.setAdapter(listAdapter);
//        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setOnItemClickListener(this);
        listView.onRefreshStart();

    }

    private void getListData(int page, int pageSize) {
        StrErrListener errorListener = new StrErrorListener(getActivity(), this);

        try {
//            String url = Const.HTTP_HEADKZ + "/plugin/juhe-api/listdata" + "?key=" + key +"&name=" + name+"&page="
//                    + page + "&pagesize=" + pageSize ;
            String url = Const.HTTP_HEADKZ + "/plugin/juhe-api/getlist" + "?key=" + key +"&page="
                    + page + "&pagesize=" + pageSize ;
            Type type = new TypeToken<List<ArticleListDao>>() {
            }.getType();

            GsonRequestGet<List<ArticleListDao>> gsonRequestGet = new GsonRequestGet<>(
                    url, type, new NewsListener(this), errorListener);
            MyVolley.getInstance(getActivity()).addToRequestQueue(gsonRequestGet);
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

//    @Override
//    public boolean onTouch(View v, MotionEvent event) {
//
//            if(event.getAction() == MotionEvent.ACTION_UP){
//                view.requestDisallowInterceptTouchEvent(false);
//        }else{
//                view.requestDisallowInterceptTouchEvent(true);
//
//    }
//        return false;
//    }


    class NewsListener implements Response.Listener<List<ArticleListDao>> {

        private final WeakReference<ProjectDelListFragment> mFragment;

        public NewsListener(ProjectDelListFragment fragment) {
            mFragment = new WeakReference<>(fragment);
        }

        @Override
        public void onResponse(List<ArticleListDao> response) {
            if (mFragment.get() != null) {
                voGlobal.clear();
                voGlobal.addAll(response);
                updateNews();
            }
        }
    }

    class StrErrorListener extends StrErrListener {

        private final WeakReference<ProjectDelListFragment> mFragment;

        public StrErrorListener(Context mContext, ProjectDelListFragment fragment) {
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


    /**
     * @description:更新UI
     * @author:baifan
     * @return:void
     */
    private void updateNews() {
        if (voGlobal != null) {
            if (page == 1) {
//                articleList.clear();
                if (voGlobal != null&&voGlobal.size()>0) {
                    articleList.addAll(voGlobal);
                    listAdapter.notifyDataSetChanged();
                } else {
                    WarnUtils.toast(getActivity(), "该栏目下暂无信息!");
                }
                listView.onRefreshComplete();
            } else {
                if (voGlobal != null && voGlobal.size() != 0) {
                    articleList.addAll(voGlobal);
                    listAdapter.notifyDataSetChanged();
                } else {
                    WarnUtils.toast(getActivity(), "已经加载完了,没有了哦!");
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
        getListData(page, pageSize);
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
                listAdapter.recordPosition(pos - 1);
                listAdapter.notifyDataSetChanged();
                String key = articleList.get(pos - 1).getKey();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                String content_api = "/article/content";
                bundle.putString("content_api", content_api);
                String url;
                url = Const.HTTP_HEADKZ + "/app/multimedia/" + articleList.get(pos - 1).getInfo_class() + "?key=" + key;
                bundle.putString("url", url);
                bundle.putString("title", "详情");
                bundle.putString("sharetitle", articleList.get(pos - 1).getTitle());
                bundle.putString("indexpic", articleList.get(pos - 1).getIndexpic());
                bundle.putString("type", articleList.get(pos - 1).getInfo_class());
                ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
            }
        } else if (listView.getHeaderViewsCount() == 2) {
            if (pos - 2 >= 0 && articleList != null && articleList.get(pos - 2) != null) {
                articleList.get(pos - 2).setIsRead(true);
                listAdapter.recordPosition(pos - 2);
                listAdapter.notifyDataSetChanged();
                String key = articleList.get(pos - 2).getKey();
                Bundle bundle = new Bundle();
                bundle.putString("key", key);
                String content_api = "/article/content";
                bundle.putString("content_api", content_api);
                String url;
                url = Const.HTTP_HEADKZ + "/app/multimedia/" + articleList.get(pos - 2).getInfo_class() + "?key=" + key;
                bundle.putString("url", url);
                bundle.putString("title", "详情");
                bundle.putString("sharetitle", articleList.get(pos - 2).getTitle());
                bundle.putString("indexpic", articleList.get(pos - 2).getIndexpic());
                bundle.putString("type", articleList.get(pos - 2).getInfo_class());
                ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
