package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.Const;

import com.wisdomrouter.app.R;

import com.wisdomrouter.app.fragment.adapter.ServiceListAdapter;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.NewArticleListDao;
import com.wisdomrouter.app.fragment.bean.ServiceItem;
import com.wisdomrouter.app.fragment.ui.EventDetailActivity;
import com.wisdomrouter.app.fragment.ui.RegActivity;
import com.wisdomrouter.app.fragment.ui.VoteListActivity;
import com.wisdomrouter.app.fragment.ui.VoteDetailNewActivity;
import com.wisdomrouter.app.fragment.ui.adapter.RegistViewPagerAdapter;
import com.wisdomrouter.app.fragment.ui.adapter.VoteAdapter;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.TDevice;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.HomeViewPager;
import com.wisdomrouter.app.view.HomeViewPager.OnSingleTouchListener;
import com.wisdomrouter.app.view.ServiceScrollViewPager;
import com.wisdomrouter.app.view.autoscrollviewpager.CirclePageIndicator;


import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;


public class FragmentDiscover extends Fragment implements OnClickListener {
    private View mFind;
    /**
     * 活动轮播
     */
    private TextView txt_acmore;
    private HomeViewPager lv_activity;
    private LinearLayout ac_point;
    private TextView ac_title;
    private TextView txt_pamore;
    private View rlService;

    private ListView lv_piao;
    private ServiceScrollViewPager vpService;
    private List<ArticleListDao> regList = new ArrayList<ArticleListDao>();// 活动list
    private List<ArticleListDao> voteList = new ArrayList<ArticleListDao>();
    private RegistViewPagerAdapter regAdapter;
    private VoteAdapter voteAdapter;
    private LinearLayout screenTag;
    private FragmentActivity  mActivity;

    private int pageIndex = 0;
    private static final int GETSUCCESS = 1;
    private static final int GETFAIL = 2;
    private int servicePage = 1;
    private int servicePageSize = 100;
    private List<ServiceItem> serviceItems = new ArrayList<>();
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GETSUCCESS:
                    updateViews();
                    break;
                case GETFAIL:
                    break;
            }
        }

        ;
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mFind) {
            mFind = inflater.inflate(R.layout.fragment_find, null);
        }
        mActivity=getActivity();
        return mFind;
    }

    protected void updateViews() {
        if (newArticleListDao != null) {
            regList.clear();
            voteList.clear();
            if (newArticleListDao.getActivity()!=null&&newArticleListDao.getActivity().size()>0)
            regList.addAll(newArticleListDao.getActivity());
            if (newArticleListDao.getNewvote()!=null&&newArticleListDao.getNewvote().size()>0)
            voteList.addAll(newArticleListDao.getNewvote());
            regAdapter = new RegistViewPagerAdapter(mActivity, regList);
            voteAdapter = new VoteAdapter(mActivity, voteList);
            lv_activity.setAdapter(regAdapter);
            ViewGroup.LayoutParams layoutParams = lv_activity.getLayoutParams();
            float screen = TDevice.getScreenWidth() - TDevice.dpToPixel(10) * 2;
            layoutParams.height = (int) (screen * 2 / 3);
            lv_activity.setLayoutParams(layoutParams);

            lv_activity.setOnSingleTouchListener(new OnSingleTouchListener() {

                @Override
                public void onSingleTouch() {
                    Bundle bundle = new Bundle();
                    ArticleListDao dao = regList.get(pageIndex);
                    if (dao != null) {
                        bundle.putString("key", dao.getKey());
                        bundle.putString("address", dao.getActaddress());
                    }
                    ActivityUtils.to(mActivity, EventDetailActivity.class,
                            bundle);
                }
            });
            lv_piao.setAdapter(voteAdapter);
            lv_piao.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> arg0, View arg1,
                                        int arg2, long arg3) {

                    String aid = voteList.get(arg2).getKey();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", aid);
                    bundle.putString("title", voteList.get(arg2).getTitle());
                    bundle.putString("pic", voteList.get(arg2).getVotepic());
                    bundle.putString("limitItem", voteList.get(arg2).getLimitItem());
                    ActivityUtils.to(mActivity,
                            VoteDetailNewActivity.class, bundle);
                }
            });
            setListViewHeightBasedOnChildren(lv_piao);

            if (regList.size() > 0) {
                buildActvityPoint(0);
                lv_activity.addOnPageChangeListener(new pageChangeListener());
                ac_title.setText(regList.get(0).getTitle());
            }
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews() {
        rlService = mFind.findViewById(R.id.rl_service);
        vpService = (ServiceScrollViewPager) mFind.findViewById(R.id.vp_service);
        screenTag = (LinearLayout) mFind.findViewById(R.id.iv_screen_tag);
        ac_point = (LinearLayout) mFind.findViewById(R.id.ac_point);
        ac_title = (TextView) mFind.findViewById(R.id.regist_title);
        txt_acmore = (TextView) mFind.findViewById(R.id.txt_acmore);
        lv_activity = (HomeViewPager) mFind.findViewById(R.id.regist_lb);
        txt_pamore = (TextView) mFind.findViewById(R.id.txt_pamore);
        lv_piao = (ListView) mFind.findViewById(R.id.lv_piao);
        lv_piao.setFocusable(false);
        txt_acmore.setOnClickListener(this);
        txt_pamore.setOnClickListener(this);
    }

    NewArticleListDao newArticleListDao;

    @Override
    public void onStart() {
        super.onStart();
        pageIndex = 0;
        getJuhe();
        getServiceList();
    }

    private void getServiceList() {
        FinalHttp fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        String url = Const.HTTP_HEADKZ + "/plugin/fwyy-api/list";
        fh.get(url + "?page=" + servicePage + "&pagesize=" + servicePageSize,
                new AjaxCallBack<Object>() {
                    @Override
                    public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                        super.onLoading(count, current);
                    }

                    @Override
                    public void onSuccess(Object t) {
                        Gson gson = new Gson();

                        try {
                            serviceItems = gson.fromJson(t.toString(),
                                    new TypeToken<List<ServiceItem>>() {
                                    }.getType());
                            updateServiceLb();
                        } catch (Exception e) {
                            WarnUtils.toast(mActivity, "数据获取解析异常,请稍后进入!"
                                    + e.toString());

                        }
                    }

                    @Override
                    public void onStart() {
                        // 开始http请求的时候回调

                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        WarnUtils.toast(mActivity, strMsg + errorNo);
                    }

                });
    }

    private void updateServiceLb() {
        if (serviceItems != null && serviceItems.size() > 0) {
            rlService.setVisibility(View.VISIBLE);
//            List<GridView> gridViews = new ArrayList<>();
            int t = serviceItems.size() / 8;
            int f = serviceItems.size() % 8;
            int count = 0;
            if (t == 0) {
                if (f == 0) {
                    count = 0;
                } else {
                    count = 1;

                }
            } else if (t >= 1) {
                if (f == 0) {
                    count = t;

                } else {
                    count = t + 1;
                }
            }
            vpService.setPageAdapter(new ServiceListAdapter(mActivity, serviceItems));
            vpService.startAutoScroll(0);
            buildImagePoint(0, count);

            final int finalCount = count;
            vpService.setOnServicePageListenser(new ServiceScrollViewPager.OnServicePageListenser() {
                @Override
                public void click(int position) {
                    buildImagePoint(position, finalCount);
                }
            });
        } else {
            rlService.setVisibility(View.GONE);
        }
    }

    private void buildImagePoint(int index, int length) {
        if (length == 0 || length == 1) {
            return;
        }
        screenTag.removeAllViews();

        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgParam.gravity = Gravity.CENTER_VERTICAL;
        imgParam.setMargins(4, 5, 4, 5);

        for (int i = 0; i < length; i++) {
            if (i == index) {
                ImageView point = new ImageView(mActivity);
                point.setBackgroundResource(R.drawable.service_redpoint);
                point.setScaleType(ScaleType.FIT_CENTER);
                point.setLayoutParams(imgParam);
                screenTag.addView(point);
            } else {
                ImageView point = new ImageView(mActivity);
                point.setBackgroundResource(R.drawable.service_whitepoint);
                point.setScaleType(ScaleType.FIT_CENTER);
                point.setLayoutParams(imgParam);
                screenTag.addView(point);
            }
        }
    }

    /**
     * 获取聚合活动投票
     */
    private void getJuhe() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                newArticleListDao = new GlobalTools(mActivity)
                        .getFindList();

                mHandler.sendEmptyMessage(GETSUCCESS);
            }
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

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

    private void buildActvityPoint(int index) {
        ac_point.removeAllViews();
        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        imgParam.gravity = Gravity.CENTER_VERTICAL;
        imgParam.setMargins(4, 5, 4, 5);
        for (int i = 0; i < regList.size(); i++) {
            if (i == index) {
                ImageView point = new ImageView(mActivity);
                point.setBackgroundResource(R.drawable.event_point_2);
                point.setScaleType(ScaleType.FIT_CENTER);
                point.setLayoutParams(imgParam);
                ac_point.addView(point);
            } else {
                ImageView point = new ImageView(mActivity);
                point.setBackgroundResource(R.drawable.ecent_point_1);
                point.setScaleType(ScaleType.FIT_CENTER);
                point.setLayoutParams(imgParam);
                ac_point.addView(point);
            }
        }
    }

    /**
     * 动态设置ListView的高度
     *
     * @param listView
     */
    public static void setListViewHeightBasedOnChildren(ListView listView) {
        if (listView == null)
            return;
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight
                + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }

    /**
     * 轮播图
     *
     * @author Administrator
     */
    public class pageChangeListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int arg0) {
            pageIndex = arg0;
            buildActvityPoint(arg0);
            ac_title.setText(regList.get(arg0).getTitle());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_acmore:
                ActivityUtils.to(mActivity, RegActivity.class);
                break;

            case R.id.txt_pamore:
                ActivityUtils.to(mActivity, VoteListActivity.class);
                break;
            default:
                break;
        }

    }
}
