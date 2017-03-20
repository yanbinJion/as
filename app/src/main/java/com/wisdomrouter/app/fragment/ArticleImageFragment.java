package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.ArimageAdapter;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.ui.ArimagelistActivity;
import com.wisdomrouter.app.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻模块 栏目九宫格形式排列
 */
public class ArticleImageFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View mView;
    private GridView gv;
    private ArimageAdapter adapter;
    private List<AppConfigBean.Module.Channel> data = new ArrayList<>();

    public static ArticleImageFragment getInstance(List<AppConfigBean.Module.Channel> listChannels) {
        ArticleImageFragment sf = new ArticleImageFragment();
        sf.data = listChannels;
        return sf;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_articleimage,container,false);
        return mView;
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        gv = (GridView) mView.findViewById(R.id.gv_country);
        gv.setOnItemClickListener(this);
        adapter = new ArimageAdapter(data,getActivity());
        gv.setAdapter(adapter);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        if (data!=null){
            bundle.putSerializable("id", data.get(position).getKey());
            bundle.putSerializable("urlPart","/article/list");
            bundle.putSerializable("map", data.get(position).getFocus_map());
            bundle.putSerializable("title", data.get(position).getName());
            ActivityUtils.to(getActivity(), ArimagelistActivity.class, bundle);
        }
    }
}
