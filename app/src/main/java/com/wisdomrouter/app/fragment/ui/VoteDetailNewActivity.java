package com.wisdomrouter.app.fragment.ui;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.umeng.analytics.MobclickAgent;
import com.umeng.analytics.social.UMSocialService;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.FragmentVoteDel;
import com.wisdomrouter.app.fragment.FragmentVotePh;
import com.wisdomrouter.app.fragment.FragmentVoteSearch;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.ShareScore;
import com.wisdomrouter.app.fragment.bean.TabEntity;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.http.AjaxCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoteDetailNewActivity extends BaseActivity {

    @Bind(R.id.tab)
    CommonTabLayout tab;
    private String key = "";
    private VoteDetailsDao voVote;
    Context mContext = this;
    ImageView right_btn;
    TextView right_text;
    String behavior;
//    final static UMSocialService mController = UMServiceFactory
//            .getUMSocialService("com.umeng.share");
//    SocializeListeners.SnsPostListener mSnsPostListener = null;
    private ArrayList<CustomTabEntity> customTabEntityList = new ArrayList<>();
    private ArrayList<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 设置标题
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_votedetailnew);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        key = getIntent().getStringExtra("key");
        initTitleBar(getIntent().getStringExtra("title"), 0, null);
        right_btn = (ImageView) findViewById(R.id.right_btn);
        right_text = (TextView) findViewById(R.id.tv_right);
        right_btn.setVisibility(View.GONE);
        right_text.setVisibility(View.VISIBLE);
        right_text.setText("投票(0)");
        right_text.setBackgroundResource(R.drawable.bg_vote_right);
        initView();
    }
    ShareScore shareScore;
    private void getShareScode(String behavior) {
        UserDao.Userinfo userinfo = HandApplication.getInstance().mSpUtil
                .getAccount();
        if (userinfo != null && !TextUtils.isEmpty(userinfo.getOpenid())) {
            VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

                @Override
                public void reqSuccess(String response) {
                    // 成功的回调 : 可以操作返回的数据
                    try {
                        shareScore = new Gson().fromJson(response, ShareScore.class);
                        if (shareScore.getState() != null && shareScore.getState().equals("1") && shareScore.getScore() != 0)
                            WarnUtils.toast(mContext, shareScore.getMessage(), shareScore.getScore());
                    } catch (Exception e) {
                        WarnUtils.toast(mContext, "解析数据积分失败" + e.getMessage());
                    }

                }

                @Override
                public void reqError(String error) {
                    // 失败的回调 ：失败的提醒
                    WarnUtils.toast(mContext, "获取数据积分失败" + error);
                }
            };
            Map<String, String> map = new HashMap<>();
            map.put("user_openid", userinfo.getOpenid());
            map.put("behavior", behavior);
            GlobalTools.getShareScore(volleyRequest, map);
        }
    }

    private void initView() {
        customTabEntityList.clear();
        fragmentList.clear();
        customTabEntityList.add(new TabEntity("主页", R.drawable.vote_home, R.drawable.vote_home));
        customTabEntityList.add(new TabEntity("排行榜", R.drawable.vote_ph, R.drawable.vote_ph));
        customTabEntityList.add(new TabEntity("搜索", R.drawable.vote_search, R.drawable.vote_search));
        customTabEntityList.add(new TabEntity("拉票", R.drawable.vote_share, R.drawable.vote_share));
        right_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVote(checkList);
            }
        });

        getVoteDetail(false);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                if (position == 3)
                    ShareUtils.shareSdk(VoteDetailNewActivity.this,
                            voVote.getTitle() == null ? "" : voVote.getTitle(), voVote.getTitle(), voVote.getVotepic(), voVote.getKey(), Const.SHARE_API.VOTES,umShareListener);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
    }

    Gson gson;
    List<VoteDetailsDao.Items> checkList = new ArrayList<>();

    private void getVoteDetail(final boolean refresh) {
        gson = new Gson();
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    voVote = gson.fromJson(response, VoteDetailsDao.class);
                    if (voVote != null) {
                        fragmentList.clear();
//                        customTabEntityList.clear();
                        FragmentVoteDel fragmentdel = new FragmentVoteDel();
                        Bundle bundle = new Bundle();
                        bundle.putString("key", key);
                        bundle.putSerializable("voVote", voVote);
                        fragmentdel.setArguments(bundle);
                        fragmentdel.setOnCountClickListener(new FragmentVoteDel.OnCountClickListener() {
                            @Override
                            public void onCount(String key, int number, List<VoteDetailsDao.Items> checkedList) {
                                right_text.setText("投票(" + number + ")");
                                checkList.clear();
                                checkList.addAll(checkedList);
                            }
                        });
                        fragmentList.add(fragmentdel);
                        FragmentVotePh fragmentVotePh = new FragmentVotePh();
                        fragmentVotePh.setArguments(bundle);
                        FragmentVoteSearch fragmentVoteSearch = new FragmentVoteSearch();
                        fragmentVoteSearch.setArguments(bundle);
                        fragmentList.add(fragmentVotePh);
                        fragmentList.add(fragmentVoteSearch);
                        fragmentList.add(new Fragment());
                        if (!refresh)
                            tab.setTabData(customTabEntityList, VoteDetailNewActivity.this, R.id.realcontent, fragmentList);
                        else {
                            tab.notifyDataSetChanged();
                        }
                    }

                } catch (Exception e) {
                    WarnUtils.toast(mContext, "数据获取解析异常,请稍后进入!");
                }
            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        String useropenid = HandApplication.user == null ? "" : HandApplication.user.getOpenid();
        map.put("key", key);
        if (!TextUtils.isEmpty(useropenid))
            map.put("user_openid", useropenid);
        GlobalTools.getVoteDetail(volleyRequest, map);
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
    int minVote;
    CommError commError;
    String itemkey = "";

    /**
     * @description: 投票
     * @author:wangfanghui
     * @return:void
     */
    private void sendVote(List<VoteDetailsDao.Items> checkedList) {
        if (attempSendVote(checkedList)) {

            for (int i = 0; i < checkedList.size(); i++) {
                itemkey += checkedList.get(i).getItemkey() + ",";
            }
            itemkey = itemkey.substring(0, itemkey.length() - 1);
            VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

                @Override
                public void reqSuccess(String response) {
                    // 成功的回调 : 可以操作返回的数据
                    try {
                        commError = gson.fromJson(response,
                                CommError.class);
                        if (commError.getState() == 0) {//投票失败
                            WarnUtils.toast(mContext, commError.getMessage());
                        } else if (commError.getState() == 1) {//投票成功
                            WarnUtils.toast(mContext, commError.getMessage());
                            if (commError.getScore()!=0)
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        WarnUtils.toast(mContext,"投票成功",commError.getScore());
                                    }
                                },1000);

                            getVoteDetail(true);
                        }
                    } catch (Exception e) {
                        WarnUtils.toast(mContext, "获取解析异常" + e.getMessage());
                    }
                }

                @Override
                public void reqError(String error) {
                    // 失败的回调 ：失败的提醒
                    WarnUtils.toast(mContext, "获取数据失败" + error);
                }
            };
            Map<String, String> map = new HashMap<>();
            map.put("key", key);
            map.put("itemkey", itemkey);
            map.put("user_openid", HandApplication.user.getOpenid());
            GlobalTools.sendVote(volleyRequest, map);
        }

    }
Handler mHandler =new Handler();
    private Boolean attempSendVote(List<VoteDetailsDao.Items> checkedList) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long currentTime = new Date().getTime();
        long start = 0L;
        if (voVote.getStarttime() != null) {
            try {
                Date dateStart = format.parse(voVote.getStarttime());
                start = dateStart.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        long end = 0L;
        if (voVote.getEndtime() != null) {
            try {
                Date dateEnd = format.parse(voVote.getEndtime());
                end = dateEnd.getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        if (start != 0L && end != 0L) {

            if (currentTime < start || currentTime > end) {
                WarnUtils.toast(VoteDetailNewActivity.this, "投票时间未开始,或者已经结束!");
                return false;
            }
        }
        if (HandApplication.user == null || TextUtils.isEmpty(HandApplication.user.getOpenid())) {
            WarnUtils.toast(mContext, "请先登录!");
            ActivityUtils.to(mContext, LoginActivity.class);
            return false;
        }
        if (checkedList.size() == 0) {
            WarnUtils.toast(mContext, "您尚未选择投票项!");
            return false;
        }
        if (!TextUtils.isEmpty(voVote.getIsvote()) && voVote.getIsvote().equals("1")) {
            WarnUtils.toast(VoteDetailNewActivity.this, "您已经投过票了!");
            return false;
        }

        if (voVote.getLimititem() != null && voVote.getLimititem().contains("-")) {
            if (voVote.getLimititem().split("-")[0] != null) {
                minVote = Integer.parseInt(voVote.getLimititem().split("-")[0]);
            }
        }
        int selectCount = checkedList.size();
        if (minVote > selectCount) {
            WarnUtils.toast(mContext, "每人至少选" + minVote + "票");
            return false;
        }
        return true;
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
            WarnUtils.toast(mContext, "分享成功");
            getShareScode(Const.SHARE_API.VOTE_BEHAVIOR);

        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            WarnUtils.toast(mContext, "分享失败");
//            if (t != null) {
//                Log.d("throw", "throw:" + t.getMessage());
//            }
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
            WarnUtils.toast(mContext, "分享取消了");
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);

    }
}
