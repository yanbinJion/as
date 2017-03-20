package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.volley.Response;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.CountryAdapter;
import com.wisdomrouter.app.fragment.bean.CountryListDao;
import com.wisdomrouter.app.fragment.ui.CountryDetailActivity;
import com.wisdomrouter.app.fragment.ui.CountryListNewsActivity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyUtils.MyVolley;
import com.wisdomrouter.app.tools.VolleyUtils.StrErrListener;
import com.wisdomrouter.app.utils.ActivityUtils;

import java.util.ArrayList;
import java.util.List;

public class CountryFragment extends Fragment implements AdapterView.OnItemClickListener{
    private View mView;
    private GridView gv;
    private CountryAdapter adapter;
    private List<CountryListDao> data = new ArrayList<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_country,container,false);
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
        getData();
        adapter = new CountryAdapter(data,getActivity());
        gv.setAdapter(adapter);
    }

    private void getData() {
        Response.Listener<List<CountryListDao>> listener = new Response.Listener<List<CountryListDao>>() {
            @Override
            public void onResponse(List<CountryListDao> countryListDaos) {
                if (countryListDaos!=null){
                    data.clear();
                    data.addAll(countryListDaos);
                    adapter.notifyDataSetChanged();
                }
            }
        };
        try {
            GlobalTools.getCountry(getActivity(), listener, new StrErrListener(getActivity()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MyVolley.getInstance(getActivity()).cancelPendingRequests(Const.TOWN);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle bundle = new Bundle();
        if (data!=null){
//            bundle.putString("key",data.get(position).getKey());
//            bundle.putString("title",data.get(position).getName());
            bundle.putSerializable("country", data.get(position));
//            ActivityUtils.to(getActivity(), CountryListNewsActivity.class, bundle);
            ActivityUtils.to(getActivity(), CountryDetailActivity.class, bundle);
        }
    }
}
