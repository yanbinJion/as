package com.wisdomrouter.app.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.ui.EventDetailActivity;
import com.wisdomrouter.app.fragment.ui.RegActivity;
import com.wisdomrouter.app.fragment.ui.adapter.RegistAdapter;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class ActivesFragment extends BaseFragment {
    @Bind(R.id.list_view)
    ViewPaperListView listView;
    private View mView;
    private List<ArticleListDao> voGlobal = null;
    private List<ArticleListDao> regList = new ArrayList<>();
    private RegistAdapter regAdapter;

    private int page = 1;
    private int pagesize = 12;
    private String url;
    private Gson gson;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_reg, null);
        }
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
        setListener();
        initData();
    }

    private void initData() {
        gson = new Gson();
        regAdapter = new RegistAdapter(this.getActivity(), regList);
        listView.setAdapter(regAdapter);
        page = 1;
        getRegList();
    }

    private void initViews() {
        url = Const.HTTP_HEADKZ + Const.REGLIST_URL;
    }

    private void setListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int pos,
                                    long arg3) {
                // 因为前面有下拉刷新的header和图片的header，所以这里需要减去2
                if (pos - 1 >= 0) {
                    regList.get(pos - 1).setIsRead(true);
                    regAdapter.notifyDataSetChanged();
                    String key = regList.get(pos - 1).getKey();
                    String address = regList.get(pos - 1).getActaddress();
                    String indexpic = regList.get(pos - 1).getIndexpic() == null ? ""
                            : regList.get(pos - 1).getIndexpic();
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    bundle.putString("indexpic", indexpic);
                    ActivityUtils.to(getActivity(),
                            EventDetailActivity.class, bundle);
                }
            }
        });

        listView.setOnRefreshListener(new PullRefreshListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                getRegList();
            }
        });

        listView.setOnLoadMoreListener(new PullRefreshListView.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                getRegList();
            }
        });

    }

    private void getRegList() {
        FinalHttp fh = new FinalHttp();
        fh.addHeader("TOKEN", Const.APPTOKEN);
        fh.get(url + "?page=" + page + "&pagesize=" + pagesize
                        + "&user_openid=" + HandApplication.user.getOpenid(),
                new AjaxCallBack<Object>() {
                    @Override
                    public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                        super.onLoading(count, current);
                    }

                    @Override
                    public void onSuccess(Object t) {
                        voGlobal = null;
                        if (page == 1) {
                            listView.onRefreshComplete();
                            regList.clear();
                        } else
                            listView.onLoadMoreComplete();
                        try {
                            voGlobal = gson.fromJson(t.toString(),
                                    new TypeToken<List<ArticleListDao>>() {
                                    }.getType());
                            if (voGlobal != null) {
                                if (voGlobal.size() == 0) {
                                    if (page == 1) {
                                        WarnUtils.toast(getActivity(),
                                                "暂时还没有活动哦!");
                                    } else {
                                        WarnUtils.toast(getActivity(),
                                                "已经加载完了,没有更多活动了哦!");
                                    }
                                }
                                regList.addAll(voGlobal);
                                page++;
                                regAdapter.notifyDataSetChanged();
                            }

                        } catch (Exception e) {
                            WarnUtils.toast(getActivity(), "数据获取解析异常,请稍后进入!"
                                    + e.toString());
                            return;
                        }

                    }

                    @Override
                    public void onStart() {
                        // 开始http请求的时候回调
                        if (page == 1)
                            listView.onRefreshStart();
                    }

                    @Override
                    public void onFailure(Throwable t, int errorNo,
                                          String strMsg) {
                        // 加载失败的时候回调
                        if (page == 1)
                            listView.onRefreshComplete();
                        else
                            listView.onLoadMoreComplete();
                        WarnUtils.toast(getActivity(), strMsg + errorNo);
                    }

                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
