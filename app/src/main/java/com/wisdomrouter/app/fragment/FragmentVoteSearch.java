package com.wisdomrouter.app.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.VoteDetailAdapter;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao;
import com.wisdomrouter.app.fragment.ui.VoteInformationActivity;
import com.wisdomrouter.app.utils.ActivityUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FragmentVoteSearch extends Fragment {

    private View mView;
    private AutoCompleteTextView edtSearch;
    private Button btnSearch;
    private String key;
    private GridView gridView;
    private VoteDetailAdapter voteAdapter;

    /**
     * 搜索内容
     */
    private VoteDetailsDao voVote;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.vote_search, null);
        }
        key = getArguments().getString("votekey");
        voVote = (VoteDetailsDao) getArguments().getSerializable("voVote");
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    @Override
    public void onStart() {
        super.onStart();
        showSearchView();
    }

    private void initView() {
        gridView = (GridView) mView.findViewById(R.id.search_bv);
        btnSearch = (Button) mView.findViewById(R.id.btnSearch);
        edtSearch = (AutoCompleteTextView) mView
                .findViewById(R.id.edtSearch);


    }

    private void showSearchView() {
        btnSearch.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String txt = edtSearch.getText().toString();
                if (txt.isEmpty()) {
                    Toast.makeText(getActivity(), "您输入的结果为空",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                Bundle bundle = new Bundle();
                final List<VoteDetailsDao.Items> data = new ArrayList<>();
                int isCount = 0;

                for (int i = 0; i < voVote.getItems().size(); i++) {
                    if (voVote.getItems().get(i).getTitle()
                            .contains(txt) ||
                            ("000000" + voVote.getItems().get(i).getNo()).contains(txt)) {
                        isCount = isCount + 1;
                        data.add(voVote.getItems().get(i));
                    }
                }
                if (isCount == 0) {
                    Toast.makeText(getActivity(), "没有此选手",
                            Toast.LENGTH_SHORT).show();
                } else if (isCount == 1) {
                    bundle = new Bundle();
                    bundle.putString("votekey", key);
                    bundle.putSerializable("item", voVote.getItems().get(0));
                    bundle.putSerializable("voVote", voVote);
                    bundle.putString("isvoted", voVote.getIsvote() == null ? "" : voVote.getIsvote());
                    ActivityUtils.to(getActivity(), VoteInformationActivity.class,
                            bundle);
                } else if (isCount > 1) {
                    voteAdapter = new VoteDetailAdapter(getActivity(), data);

                    voteAdapter.setOnDetailClickListener(new VoteDetailAdapter.OnDetailClickListener() {
                        @Override
                        public void inDetail(int position) {
                            Bundle bundle = new Bundle();
                            bundle.putString("votekey", key);
                            bundle.putSerializable("item", voVote.getItems().get(position));
                            bundle.putString("isvoted", voVote.getIsvote() == null ? "" : voVote.getIsvote());
                            bundle.putSerializable("voVote", voVote);
                            ActivityUtils.to(getActivity(), VoteInformationActivity.class,
                                    bundle);
                        }
                    });
                }
                gridView.setAdapter(voteAdapter);
            }
        });
    }


}
