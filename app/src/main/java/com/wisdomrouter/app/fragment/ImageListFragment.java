package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.ImgAdapter;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.fragment.ui.ContentWebviewActivity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.tools.VolleyHttpRequest;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.ColumnHorizontalScrollView;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.PullRefreshListView.OnRefreshListener;
import com.wisdomrouter.app.view.PullRefreshListView.OnLoadMoreListener;
import com.wisdomrouter.app.view.ViewPaperListView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class ImageListFragment extends BaseFragment implements PullRefreshListView.OnRefreshListener, PullRefreshListView.OnLoadMoreListener, AdapterView.OnItemClickListener {
    @Bind(R.id.mViewPager)
    ViewPaperListView listView;
    private View mView;
    String key;
    /**
     * 根据cid获取的新闻内容
     */
    private List<ArticleListDao> voGlobal = null;
    /**
     * 新闻列表适配器
     */
    private ImgAdapter listAdapter;
    /**
     * 适配器的数据
     */
    private List<ArticleListDao> imgList = new ArrayList<>();
    /**
     * 获取内容的页码
     */
    private int page = 1;
    /**
     * 获取内容的数量
     */
    private int pageSize = 12;

    public static ImageListFragment getInstance(String key) {
        ImageListFragment sf = new ImageListFragment();
        sf.key = key;
        return sf;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_image_list, container, false);
        }
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        voGlobal = new ArrayList<>();
        initWidgets();
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
        getPics();

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 获取图片
     */
    private void getPics() {
        if (page <= 1) {
            listView.onRefreshStart();
        }
        String url = Const.HTTP_HEAD + "/images/list" + "?key=" + key + "&page="
                + page + "&pagesize=" + pageSize;
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    Type type = new TypeToken<List<ArticleListDao>>() {
                    }.getType();
                    List<ArticleListDao> list = new Gson().fromJson(response, type);
                    voGlobal.clear();
                    voGlobal.addAll(list);
                } catch (Exception e) {
                    WarnUtils.toast(getActivity(), "数据解析异常!");
                }
                updateViews();
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

    /**
     * @description:更新UI
     * @author:baifan
     * @return:void
     */
    private void updateViews() {
        if (page <= 1) {// 首次加载
            if (voGlobal != null && voGlobal.size() > 0) {
                // 初始化信息
                imgList.clear();
                imgList.addAll(voGlobal);
                listAdapter.notifyDataSetChanged();
                page += 1;
            } else {
                Toast.makeText(getActivity(), "该栏目下,暂时没有信息!",
                        Toast.LENGTH_SHORT).show();
            }
            listView.onRefreshComplete();
        } else {// 加载更多
            if (voGlobal != null && voGlobal.size() > 0) {
                imgList.addAll(voGlobal);
                listAdapter.notifyDataSetChanged();
                page++;
            } else {
                Toast.makeText(getActivity(), "已经加载完了,没有了哦!",
                        Toast.LENGTH_SHORT).show();
            }
            listView.onLoadMoreComplete();
        }

    }

    /**
     * 初始化数据
     */
    private void initWidgets() {
        page = 1;
        listAdapter = new ImgAdapter(getActivity(), imgList);
        listView.setAdapter(listAdapter);
        listView.setOnRefreshListener(this);
        listView.setOnLoadMoreListener(this);
        listView.setOnItemClickListener(this);
    }


    @Override
    public void onRefresh() {
        page = 1;
        getPics();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        // 因为前面有下拉刷新的header和图片的header，所以这里需要减去2
        if (pos - 1 >= 0) {
            imgList.get(pos - 1).setIsRead(true);// 标记为已读
            listAdapter.notifyDataSetChanged();
            String key = imgList.get(pos - 1).getKey();
            Bundle bundle = new Bundle();
            bundle.putString("key", key);
            bundle.putString("content_api", "/images/list");
            String url;
            url = Const.HTTP_HEADKZ + "/app/multimedia/images?key=" + key;
            bundle.putString("url", url);
            bundle.putString("title", "详情");
            bundle.putString("sharetitle", imgList.get(pos - 1).getTitle());
            bundle.putString("indexpic", imgList.get(pos - 1).getIndexpic());
            bundle.putString("type", imgList.get(pos - 1).getInfo_class());
            ActivityUtils.to(getActivity(), ContentWebviewActivity.class, bundle);
        }

    }

    @Override
    public void onLoadMore() {
        getPics();

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
