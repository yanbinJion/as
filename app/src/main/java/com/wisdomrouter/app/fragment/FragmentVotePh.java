package com.wisdomrouter.app.fragment;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.VoteDao;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.utils.DialogHelper;
import com.wisdomrouter.app.view.ProgressDialog;
import com.wisdomrouter.app.view.RatioImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class FragmentVotePh extends BaseFragment {

    private View mView;
    private TableLayout table;
    private TextView tvTitle;
    private TextView tvTime;
    private RatioImageView imgHead;
    private TextView tvPeopleCount;
    private TextView tvPiaoCount;
    private TextView tvLookCount;
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private GlobalTools GlobalTools;
    List<VoteDao> list = new ArrayList<>();
    Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    hideProgressDialog();
                    updateView();
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_vote_ph, null);
        }

        return mView;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @SuppressLint("NewApi")
    protected void updateView() {
        LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        imgParam.gravity = Gravity.CENTER;

        for (int i = 0; i < list.size(); i++) {
            TableRow row1 = new TableRow(getActivity());
            RelativeLayout re1 = new RelativeLayout(getActivity());
            View v = new View(getActivity());
            v.setBackgroundColor(Color.parseColor("#e8e8e8"));
            v.setMinimumHeight(10);

            if (i == 0) {
                ImageView iv = new ImageView(getActivity());
                iv.setBackgroundResource(R.drawable.vote_no1);
                iv.setScaleType(ScaleType.CENTER);
                re1.setGravity(Gravity.CENTER);
                re1.setPadding(40, 40, 40, 20);
                re1.addView(iv, imgParam);
                row1.addView(re1);

            } else if (i == 1) {
                ImageView iv = new ImageView(getActivity());
                iv.setBackgroundResource(R.drawable.vote_no2);
                iv.setScaleType(ScaleType.CENTER);
//				re1.setLayoutParams(reParam);
                re1.setGravity(Gravity.CENTER);
                re1.setPadding(40, 20, 40, 20);
                re1.addView(iv, imgParam);
                row1.addView(re1);
            } else if (i == 2) {
                ImageView iv = new ImageView(getActivity());
                iv.setBackgroundResource(R.drawable.vote_no3);
                iv.setScaleType(ScaleType.CENTER);
                re1.setPadding(40, 20, 40, 20);
                re1.setGravity(Gravity.CENTER);
                re1.addView(iv, imgParam);
                row1.addView(re1);
            } else {
                TextView order1 = new TextView(getActivity());
                order1.setText((i + 1) + "");
                order1.setBackgroundResource(R.drawable.shape_circle_blue);
                order1.setGravity(Gravity.CENTER);
                order1.setTextColor(Color.WHITE);
                order1.setPadding(25, 10, 25, 10);
                re1.setPadding(40, 20, 40, 20);
                order1.setTextSize(18);
                re1.setGravity(Gravity.CENTER);
                re1.addView(order1, imgParam);
                row1.addView(re1);
            }

            TextView dictName1 = new TextView(getActivity());
            dictName1.setText(list.get(i).getNo() + "");
            dictName1.setTextSize(18);
            dictName1.setTextColor(getResources().getColor(R.color.votetextcolor));
            dictName1.setGravity(Gravity.CENTER);

            TextView oper1 = new TextView(getActivity());

            String title = list.get(i).getTitle();
            if (title.length() <= 6) {
                oper1.setText(title);
            } else {
                String titled = title.substring(0, 5);
                oper1.setText(titled);
            }
            oper1.setMaxEms(2);
            oper1.setGravity(Gravity.CENTER);
            oper1.setTextSize(18);
            oper1.setTextColor(getResources().getColor(R.color.votetextcolor));

            TextView oper2 = new TextView(getActivity());
            oper2.setText(list.get(i).getCount() + "");
            oper2.setGravity(Gravity.CENTER);
            oper2.setTextSize(18);
            oper2.setTextColor(getResources().getColor(R.color.votetextcolor));
            row1.addView(dictName1);
            row1.addView(oper1);
            row1.addView(oper2);
            row1.addView(v);
            row1.setGravity(Gravity.CENTER);

            table.addView(row1);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getArgumentsData();
        initView();
        initData();
    }

    private void getArgumentsData() {
        key = getArguments().getString("key");
        voVote = (VoteDetailsDao) getArguments().getSerializable("voVote");
    }

    private void initData() {
        showProgressDialog("加载中...", "vertical");
        GlobalTools = new GlobalTools(getActivity());
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    list = GlobalTools.getVotePhList(key);
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    VoteDetailsDao voVote;
    String key;

    private void initView() {

        table = (TableLayout) mView.findViewById(R.id.table);
        tvTitle = (TextView) mView.findViewById(R.id.tv_vote_detail_title);
        tvTime = (TextView) mView.findViewById(R.id.tv_vote_detail_date);
        imgHead = (RatioImageView) mView.findViewById(R.id.img_vote_detail);
        imgHead.setAspectRatio(1.5f);
        tvPeopleCount = (TextView) mView
                .findViewById(R.id.tv_vote_detail_people_count);
        tvPiaoCount = (TextView) mView
                .findViewById(R.id.tv_vote_detail_piao_count);
        tvLookCount = (TextView) mView
                .findViewById(R.id.tv_vote_detail_look_count);

        if (voVote != null) {
            tvTitle.setText(voVote.getTitle() == null ? "" : voVote.getTitle());
            int count = 0;
            for (int i = 0; i < voVote.getItems().size(); i++) {
                count += voVote.getItems().get(i).getCount();
            }
            tvPeopleCount.setText(voVote.getItems().size() + "");// 当前投票人数
            tvPiaoCount.setText(count + "");// 当前总的票数
            tvLookCount.setText(voVote.getClick() == null ? ""
                    : voVote.getClick());
            imageLoader.displayImage(voVote.getVotepic(), imgHead);
        }


    }

}
