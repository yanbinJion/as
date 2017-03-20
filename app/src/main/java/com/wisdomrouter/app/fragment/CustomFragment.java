package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CustomAdapter;
import com.wisdomrouter.app.fragment.adapter.NewsAutoScrollPagerAdapter;
import com.wisdomrouter.app.fragment.bean.CustomDao;
import com.wisdomrouter.app.fragment.ui.ContentWebviewActivity;
import com.wisdomrouter.app.fragment.ui.CountryDetailActivity;
import com.wisdomrouter.app.fragment.ui.CountryListNewsActivity;
import com.wisdomrouter.app.fragment.ui.EventDetailActivity;
import com.wisdomrouter.app.fragment.ui.LiveActivity;
import com.wisdomrouter.app.fragment.ui.ProDetailListActivity;
import com.wisdomrouter.app.fragment.ui.VoteDetailNewActivity;
import com.wisdomrouter.app.fragment.ui.WebviewActivity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.TDevice;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;
import com.wisdomrouter.app.view.autoscrollviewpager.AutoScrollViewPager;
import com.wisdomrouter.app.view.autoscrollviewpager.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义列表
 * Created by Administrator on 2016/3/16.
 */
public class CustomFragment extends Fragment implements PullRefreshListView.OnLoadMoreListener, PullRefreshListView.OnRefreshListener, AdapterView.OnItemClickListener {
    private View mView;
    //header
    private AutoScrollViewPager vpNews;
    private CirclePageIndicator pageIndicator;
    private RelativeLayout header;
    private RelativeLayout rlNews;
    // 滑动标题
    private TextView tvSlideTitle;
    private ViewPaperListView listView;
    private List<CustomDao> listdate = new ArrayList<>();
    // 轮播
    private List<CustomDao> lbList = new ArrayList<>();
    private String list_key;
    private int page = 1;
    private int pagesize = 8;
    private CustomAdapter adapter;
    List<String> urls = new ArrayList<>();
    List<String> titles = new ArrayList<>();
    List<String> keys = new ArrayList<>();

    public static CustomFragment getInstance(String list_key) {
        CustomFragment sf = new CustomFragment();
        sf.list_key = list_key;
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_custom, container, false);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (list_key == null || list_key.equals("")) {
            WarnUtils.toast(getActivity(), "暂未配置数据 listkey==null");
            return;
        }
        initView();
    }

    private void initView() {
        listView = (ViewPaperListView) mView.findViewById(R.id.coustom_lv);
        header = (RelativeLayout) LayoutInflater.from(getActivity()).inflate(
                R.layout.view_news_auto_scroll_head, null);
        rlNews = (RelativeLayout) header.findViewById(R.id.rl_news);
        vpNews = (AutoScrollViewPager) header.findViewById(R.id.vp_news);
        ViewGroup.LayoutParams layoutParams = vpNews.getLayoutParams();
        float screen = TDevice.getScreenWidth();
        layoutParams.height = (int) (screen * 2 / 3);
        vpNews.setLayoutParams(layoutParams);
        tvSlideTitle = (TextView) header.findViewById(R.id.tvSlideTitle);
        pageIndicator = (CirclePageIndicator) header.findViewById(R.id.pi_news_indicator);
        listView.setOnLoadMoreListener(this);
        listView.setOnRefreshListener(this);
        listView.setOnItemClickListener(this);
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
        listView.addHeaderView(header);
        adapter = new CustomAdapter(listdate, getActivity());
        listView.setAdapter(adapter);
        getNews();
    }

    private void getNews() {
        Response.Listener<List<CustomDao>> listener = new Response.Listener<List<CustomDao>>() {
            @Override
            public void onResponse(List<CustomDao> customDaos) {
                handleMessage(customDaos);
            }
        };
        try {
            GlobalTools.getCustomList(getActivity(), list_key, page, pagesize, listener, new StrErrListener(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleMessage(List<CustomDao> data) {
        if (page == 1) {
            listView.onRefreshComplete();
            if (data != null && data.size() != 0) {
                listdate.clear();
                listdate.addAll(data);
                int size = 0;
                for (int i = 0; i < listdate.size(); i++) {
                    if (!StringUtil.isEmpty(listdate.get(i).getIndexpic())) {
                        lbList.add(listdate.get(i));
                        size++;
                        if (size == 4) {
                            break;
                        }
                    }
                }
                updateLb();
                listdate.removeAll(lbList);
            }
            adapter.notifyDataSetChanged();
        } else {
            listView.onLoadMoreComplete();
            if (data != null) {
                if (data.size() == 0) {
                    WarnUtils.toast(getActivity(), "已无更多");
                }
                listdate.addAll(data);
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void updateLb() {
        if (lbList != null && lbList.size() > 0) {
            rlNews.setVisibility(View.VISIBLE);
            for (CustomDao newsList : lbList) {

                urls.add(newsList.getIndexpic());
                titles.add(newsList.getTitle());
                keys.add(newsList.getInfo_key());
            }
            vpNews.setAdapter(new NewsAutoScrollPagerAdapter(urls, getActivity()));
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
                        CustomDao dao = lbList.get(position);
                        Bundle bundle = new Bundle();
                        bundle.putString("key", keys.get(position));
                        String url= Const.HTTP_HEADKZ + "/app/multimedia/"+dao.getInfo_class()+"?key=" + keys.get(position);
                        if (dao.getInfo_class().equals("article")) {
                            bundle.putString("content_api", "/article/content");
                            setBundle(bundle,url,urls,titles,position,dao);
                        } else if (dao.getInfo_class().equals("images")) {
                            bundle.putString("content_api", "/images/content");
                            setBundle(bundle,url,urls,titles,position,dao);
                        } else if (dao.getInfo_class().equals("video")) {
                            bundle.putString("content_api", "/video/content");
                            setBundle(bundle,url,urls,titles,position,dao);
                        } else if (dao.getInfo_class().equals("activity")) {
                            ActivityUtils.to(getActivity(), EventDetailActivity.class, bundle);
                        } else if (dao.getInfo_class().equals("weilive")) {
                            bundle.putString("pic", dao.getIndexpic());
                            bundle.putString("title", "直播内容");
                            bundle.putString("url", Const.HTTP_HEADKZ
                                    + Const.LIVECONTENT_URL);
                            ActivityUtils.to(getActivity(), LiveActivity.class, bundle);
                        } else if (dao.getInfo_class().equals("newvote")) {
                            ActivityUtils.to(getActivity(), VoteDetailNewActivity.class, bundle);
                        }else if (dao.getInfo_class().equals("township")) {//镇区内容列表页
                            bundle.putString("key", dao.getLink());
                            bundle.putString("title", dao.getTitle());
//                            bundle.putSerializable("country", data.get(position));
                            ActivityUtils.to(getActivity(),CountryDetailActivity.class, bundle);
                        }else if (dao.getInfo_class().equals("special")) {//专题内容列表页
                            bundle.putString("key", dao.getLink());
                            bundle.putString("title", dao.getTitle());
                            ActivityUtils.to(getActivity(),ProDetailListActivity.class, bundle);
                        }else if (dao.getInfo_class().equals("link")) {//网页
                            bundle.putString("url", dao.getLink());
                            ActivityUtils.to(getActivity(),WebviewActivity.class, bundle);
                        }
                    }
                }
            });
        } else {
            rlNews.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoadMore() {
        page++;
        getNews();
    }

    @Override
    public void onRefresh() {
        page = 1;
        listdate.clear();
        lbList.clear();
        urls.clear();
        titles.clear();
        keys.clear();
        getNews();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CustomDao dao = listdate.get(position - 2);
        String key = listdate.get(position - 2).getInfo_key();
        Bundle bundle = new Bundle();
        bundle.putString("key", key);
        handleIntent(dao,bundle,key);
    }
    private void handleIntent(CustomDao dao,Bundle bundle,String key){
        String url= Const.HTTP_HEADKZ + "/app/multimedia/"+dao.getInfo_class()+"?key=" + key;
        if (dao.getInfo_class().equals("article")) {
            bundle.putString("content_api", "/article/content");
            setBundle(bundle,url,dao);
        } else if (dao.getInfo_class().equals("images")) {
            bundle.putString("content_api", "/images/content");
            setBundle(bundle,url,dao);
        } else if (dao.getInfo_class().equals("video")) {
            bundle.putString("content_api", "/video/content");
            setBundle(bundle,url,dao);
        } else if (dao.getInfo_class().equals("activity")) {
            ActivityUtils.to(getActivity(), EventDetailActivity.class, bundle);
        } else if (dao.getInfo_class().equals("weilive")) {
            bundle.putString("url", Const.HTTP_HEADKZ
                    + Const.LIVECONTENT_URL);
            bundle.putString("pic", dao.getIndexpic());
            bundle.putString("title", "直播内容");
            ActivityUtils.to(getActivity(), LiveActivity.class, bundle);
        } else if (dao.getInfo_class().equals("newvote")) {
            ActivityUtils.to(getActivity(), VoteDetailNewActivity.class, bundle);
        }else if (dao.getInfo_class().equals("township")) {//镇区内容列表页
            bundle.putString("key", dao.getLink());
            bundle.putString("title", dao.getTitle());
            ActivityUtils.to(getActivity(),CountryListNewsActivity.class, bundle);
        }else if (dao.getInfo_class().equals("special")) {//专题内容列表页
            bundle.putString("key", dao.getLink());
            bundle.putString("title", dao.getTitle());
            ActivityUtils.to(getActivity(),ProDetailListActivity.class, bundle);
        }else if (dao.getInfo_class().equals("link")) {//网页
            bundle.putString("url", dao.getLink());
            ActivityUtils.to(getActivity(),WebviewActivity.class, bundle);
        }
    }
    private void setBundle(Bundle bundle,String url,CustomDao dao){
        bundle.putString("url", url);
        bundle.putString("title", "详情");
        bundle.putString("sharetitle", dao.getTitle());
        bundle.putString("indexpic", dao.getIndexpic());
        bundle.putString("type", dao.getInfo_class());
        ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
    }

    private void setBundle(Bundle bundle,String url,List<String> urls,List<String> titles,int position,CustomDao dao){
        bundle.putString("url", url);
        bundle.putString("title", "详情");
        bundle.putString("sharetitle", titles.get(position));
        bundle.putString("indexpic", urls.get(position));
        bundle.putString("type", dao.getInfo_class());
        ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyVolley.getInstance(getActivity()).cancelPendingRequests(Const.CUSTOM);
    }


}
