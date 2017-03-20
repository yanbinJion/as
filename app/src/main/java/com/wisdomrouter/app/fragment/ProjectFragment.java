
package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.umeng.analytics.MobclickAgent;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.ProjectAdapter;
import com.wisdomrouter.app.fragment.bean.ProjectListDao;
import com.wisdomrouter.app.fragment.ui.ProDetailActivity;
import com.wisdomrouter.app.fragment.ui.ProDetailListActivity;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.tools.VolleyHttpRequest;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.PullRefreshListView;
import com.wisdomrouter.app.view.ViewPaperListView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ProjectFragment extends Fragment implements OnItemClickListener, PullRefreshListView.OnRefreshListener {
    private View mImage;
    /**
     * 根据cid获取的新闻内容
     */
    private List<ProjectListDao> voGlobal = new ArrayList<>();
    /**
     * 新闻列表
     */
    private ViewPaperListView listView;
    /**
     * 新闻列表适配器
     */
    private ProjectAdapter listAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mImage) {
            mImage = inflater.inflate(R.layout.fragment_project, container, false);
        }
        return mImage;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null != mImage) {
            findViewById();
        }
    }

    /**
     * 初始化控件
     */
    private void findViewById() {
        listView = (ViewPaperListView) mImage.findViewById(R.id.list_view);
        listView.setOnItemClickListener(this);
        listView.setOnRefreshListener(this);
        listAdapter = new ProjectAdapter(getActivity(), voGlobal);
        listView.setAdapter(listAdapter);
        initdata();
    }

    private void initdata() {
        String url = Const.HTTP_HEADKZ + "/plugin/special-api/specialcountnews";
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                listView.onRefreshComplete();
                Type type = new TypeToken<List<ProjectListDao>>() {
                }.getType();
                List<ProjectListDao> list = new Gson().fromJson(response, type);
                voGlobal.clear();
                voGlobal.addAll(list);
                listAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3) {
        if (pos - 1 >= 0) {
            ProjectListDao data = voGlobal.get(pos - 1);
            if (data != null) {
                Bundle bundle = new Bundle();
//                bundle.putString("key",data.getKey());
//                bundle.putString("title",data.getName());
                bundle.putSerializable("project",data);
                ActivityUtils.to(getActivity(), ProDetailActivity.class, bundle);
            }
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
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onRefresh() {
        voGlobal.clear();
        initdata();
    }
}
