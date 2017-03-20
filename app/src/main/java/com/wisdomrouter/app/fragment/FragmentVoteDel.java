package com.wisdomrouter.app.fragment;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.VoteDetailAdapter;
import com.wisdomrouter.app.fragment.bean.ArticleListDao;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao.Items;
import com.wisdomrouter.app.fragment.ui.VoteInformationActivity;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.NetUtil;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.BaseGridView;
import com.wisdomrouter.app.view.RatioImageView;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class FragmentVoteDel extends BaseFragment {

    @Bind(R.id.tv_vote_detail_date)
    TextView tvTime;
    @Bind(R.id.img_vote_detail)
    RatioImageView imgHead;
    @Bind(R.id.tv_vote_detail_people_count)
    TextView tvPeopleCount;
    @Bind(R.id.tv_vote_detail_piao_count)
    TextView tvPiaoCount;
    @Bind(R.id.tv_vote_detail_look_count)
    TextView tvLookCount;
    @Bind(R.id.rl_detail)
    RelativeLayout rl_detail;
    @Bind(R.id.tv_vote_detail_class_start_date)
    TextView tvStarTime;
    @Bind(R.id.tv_vote_detail_class_end_date)
    TextView tvEndTime;
    @Bind(R.id.gv_vote_detail)
    BaseGridView gridView;

    private View mView;
    private VoteDetailsDao voVote;
    private VoteDetailAdapter voteAdapter;
    private List<Items> voteList = new ArrayList<>();

    //已经选择的item数据
    private List<Items> checkedList = new ArrayList<>();
    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private String mainKey = "";
    int maxVote, minVote;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }


    public interface OnCountClickListener {
        void onCount(String key, int number, List<Items> checkedList);
    }

    public OnCountClickListener listener;

    public void setOnCountClickListener(OnCountClickListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.activity_vote_details_list, null);
        }
        voVote = (VoteDetailsDao) getArguments().getSerializable("voVote");
        if (voVote != null) {
            minVote = voVote.getLimititem().split("-").length > 1 ? Integer.parseInt(voVote.getLimititem().split("-")[0]) : 0;
            maxVote = voVote.getLimititem().split("-").length > 1 ? Integer.parseInt(voVote.getLimititem().split("-")[1]) : 0;

        }
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        handle();
    }

    private void initView() {
        imgHead.setAspectRatio(1.5f);
        rl_detail.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                changeWindowAlpha(0.5f);
                PopupWindow = new PopupWindows(getActivity(), rl_detail);

            }
        });
        gridView.setFocusable(false);
    }

    private void changeWindowAlpha(float windowAlpha) {
        WindowManager.LayoutParams lp = getActivity().getWindow()
                .getAttributes();
        lp.alpha = windowAlpha;
        getActivity().getWindow().setAttributes(lp);
    }


    private void handle() {

        if (voVote != null) {
            mainKey = voVote.getKey() == null ? "" : voVote
                    .getKey();
            tvTime.setText(voVote.getCreatetime() == null ? ""
                    : TimeUtil.getStrDate(voVote.getCreatetime()));
            imageLoader.displayImage(voVote.getVotepic(), imgHead);
            int count = 0;
            for (int i = 0; i < voVote.getItems().size(); i++) {
                count += voVote.getItems().get(i).getCount();
            }
            tvPeopleCount.setText(voVote.getItems().size() + "");// 当前投票人数
            tvPiaoCount.setText(count + "");// 当前总的票数
            tvLookCount.setText(voVote.getClick() == null ? ""
                    : voVote.getClick());

            tvStarTime.setText(voVote.getStarttime() == null ? ""
                    : voVote.getStarttime());
            tvEndTime.setText(voVote.getEndtime() == null ? ""
                    : voVote.getEndtime());

            if (voVote.getItems() != null) {
                if (voVote.getItems().size() > 0) {
                    voteList.clear();
                    voteList.addAll(voVote.getItems());
                    initData();
                }


            }
        }
    }

    private void initData() {

        voteAdapter = new VoteDetailAdapter(getActivity(), voteList);
        gridView.setAdapter(voteAdapter);
        voteAdapter.setOnDetailClickListener(new VoteDetailAdapter.OnDetailClickListener() {
            @Override
            public void inDetail(int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("item", voteList.get(position));
                bundle.putString("isvoted", voVote.getIsvote());
                bundle.putString("votekey", mainKey);
                bundle.putSerializable("voVote", voVote);
                ActivityUtils.to(getActivity(), VoteInformationActivity.class,
                        bundle);
            }
        });
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Items info = (Items) voteAdapter.getItem(position);
                if (info != null) {
                    if (checkedList.contains(info)) {
                        checkedList.remove(info);

                    } else {
                        if (checkedList.size() > maxVote) {
                            WarnUtils.toast(getActivity(), "已达到投票数上限");
                            return;
                        } else {
                            checkedList.add(info);
                        }
                    }
                    voteAdapter.select(info);
                    if (checkedList.size() >= 0) {
                        listener.onCount(info.getItemkey(), checkedList.size(), checkedList);
                    }
                }
            }
        });
    }


    PopupWindows PopupWindow;

    /**
     * 活动介绍
     *
     * @param v
     */
    public void openDetail(View v) {
        PopupWindow = new PopupWindows(getActivity(), rl_detail);
    }

    /**
     * 打开写跟贴页面
     *
     * @author Administrator
     */
    public class PopupWindows extends PopupWindow {

        public PopupWindows(Context mContext, View parent) {

            View view = View.inflate(mContext, R.layout.activity_live_detail,
                    null);
            view.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.fade_ins));
            LinearLayout ll_popup = (LinearLayout) view
                    .findViewById(R.id.ll_popup);
            ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                    R.anim.push_bottom_in_2));

            // 设置键盘不挡住Dialog
            setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            setWidth(LayoutParams.WRAP_CONTENT);
            setHeight(LayoutParams.WRAP_CONTENT);
            setBackgroundDrawable(new BitmapDrawable());
            setFocusable(true);
            setOutsideTouchable(true);
            setContentView(view);
            showAtLocation(parent, Gravity.CENTER, 0, 0);
            update();
            setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    changeWindowAlpha(1f);
                }
            });

            final TextView txt_detail2 = (TextView) view
                    .findViewById(R.id.txt_detail);
            txt_detail2.setVisibility(View.GONE);
            final WebView txt_detail = (WebView) view
                    .findViewById(R.id.web_detail);
            txt_detail.setVisibility(View.VISIBLE);
            final ImageView iv_close = (ImageView) view
                    .findViewById(R.id.iv_close);
            iv_close.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });
            txt_detail.loadDataWithBaseURL(null, wrapHtml(voVote.getContent()),
                    "text/html", "utf-8", null);
        }
    }

    public String wrapHtml(String content) {
        // 需要做处理，因为pre会保持样式不变
        if (content != null) {
            content = content.replaceAll("<pre>", "<p>");
            content = content.replaceAll("</pre>", "</p>");
        }

        StringBuilder builder = new StringBuilder();
        builder.append("<!DOCTYPE html><html><head><title></title>");
        builder.append("<meta name=\"viewport\" content=\"initial-scale=1.0, maximum-scale=1.0,width=device-width,user-scalable=no\">");
        builder.append("<style>");

        // 判断字体大小
        String font_size = "18";
        builder.append("p{font-size:" + font_size
                + "px;color:#555;line-height:20px;}");
        builder.append(" img{display:block;margin: 0 auto;max-width: 100%;}");
        builder.append("</style>");
        builder.append("</head>");
        builder.append("<body><div id=\"app_content\" >");// ;width=\"100%\"
        builder.append(content == null ? "" : content);
        builder.append("</div></body></html>");

        return builder.toString();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
}
