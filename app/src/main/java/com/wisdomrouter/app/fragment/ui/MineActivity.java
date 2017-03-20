package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.MainActivity;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.adapter.StatusAdapter;
import com.wisdomrouter.app.fragment.bean.AppConfigBean;
import com.wisdomrouter.app.fragment.bean.ResultDao;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.services.NetChangeService;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.CustomDialog;
import com.wisdomrouter.app.utils.CustomDialog2;
import com.wisdomrouter.app.utils.ImageUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.CircleImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.wisdomrouter.app.fragment.ui.ImviteActivity.FILECHOOSER_RESULTCODE;


public class MineActivity extends BaseDetailActivity {

    @Bind(R.id.iv_login)
    CircleImageView ivLogin;
    @Bind(R.id.rl_login)
    RelativeLayout rlLogin;
    @Bind(R.id.ll_person)
    LinearLayout llPerson;
    @Bind(R.id.nickname)
    TextView nickname;
    @Bind(R.id.txt_register)
    TextView txtRegister;
    @Bind(R.id.txt_login)
    TextView txtLogin;
    @Bind(R.id.txt_sign)
    TextView txtSign;
    @Bind(R.id.txt_score)
    TextView txtScore;
    @Bind(R.id.ll_login)
    LinearLayout llLogin;
    @Bind(R.id.ll_sign)
    LinearLayout llSign;
    @Bind(R.id.rl_collect)
    RelativeLayout rlCollect;
    @Bind(R.id.rl_comment)
    RelativeLayout rlComment;
    @Bind(R.id.rl_baoliao)
    RelativeLayout rlBaoliao;
    @Bind(R.id.rl_share)
    RelativeLayout rlShare;
    @Bind(R.id.rl_au)
    RelativeLayout rlAu;
    @Bind(R.id.rl_invite)
    RelativeLayout rlInvite;
    @Bind(R.id.rl_score)
    RelativeLayout rlScore;
//    @Bind(R.id.rl_actives)
//    RelativeLayout rlActives;

    int i = 0;
    UserDao.Userinfo userinfo;
    private List<UserDao.Userinfo.UserGroup> info = new ArrayList<>();
    private List<UserDao.Userinfo.UserGroup> status = new ArrayList<>();
    private List<UserDao.Userinfo.UserGroup> statused = new ArrayList<>();
    private StatusAdapter adapter2;
    public String SHARETYPE;
    Context mContext = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_mine);
        initTitleBarForLeft("我的");
        ButterKnife.bind(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
        loadUser();
    }

    // 用户信息
    public static UserDao.Userinfo user;

    private void initViews() {
        user = HandApplication.getInstance().mSpUtil.getAccount();
        if (user != null) {
            if (user.getUsergroup() != null) {
                info.clear();
                info.addAll(user.getUsergroup());
            }
        }
        if (info != null && user != null && !TextUtils.isEmpty(user.getOpenid())) {
            statused.clear();
            status.clear();
            //分别获取认证过和未认证身份
            for (int i = 0; i < info.size(); i++) {
                if (info.get(i).getIscheckd().equals("1")) {
                    statused.add(info.get(i));
                } else if (info.get(i).getIscheckd().equals("0")) {
                    status.add(info.get(i));
                }
            }
            for (UserDao.Userinfo.UserGroup userGroup : statused) {
                if (userGroup.getIdentification().equals("promoter")) {
                    rlInvite.setVisibility(View.GONE);
                }
            }
            //用户认证是够可见
            if (status.size() == 0) {
                rlAu.setVisibility(View.GONE);
//                rlInvite.setVisibility(View.GONE);

            } else {
                rlAu.setVisibility(View.VISIBLE);
            }
        }
        if (user != null && user.getPromoter_complete() != null && user != null && !TextUtils.isEmpty(user.getOpenid())) {
            if (user.getLikename() != null
                    && !"".equals(user.getLikename())) {
                rlAu.setVisibility(View.VISIBLE);
                if (user.getPromoter_complete().equals("0")) {
                    String promoter_complete = user.getPromoter_complete();
                    Log.e("getPromoter",promoter_complete);
                    rlInvite.setVisibility(View.VISIBLE);
                } else {
                    rlInvite.setVisibility(View.GONE);
                }
            } else {
                rlAu.setVisibility(View.GONE);
                rlInvite.setVisibility(View.GONE);
            }
        }
        if (status.size() == 0) {
            rlAu.setVisibility(View.GONE);
        }

        txtLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.to(mContext, LoginActivity.class);
            }
        });
        txtRegister.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                ActivityUtils.to(mContext, RegisterActivity.class);
            }
        });

        rlCollect.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user != null
                        && user.getLikename() != null
                        && !"".equals(user.getLikename()))
                    ActivityUtils.to(mContext, MyFavouriteActivity.class);
                else
                    ActivityUtils.to(mContext, LoginActivity.class);
            }
        });
        rlComment.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user != null
                        && user.getLikename() != null
                        && !"".equals(user.getLikename()))
                    ActivityUtils.to(mContext, MyCommentActivity.class);
                else
                    ActivityUtils.to(mContext, LoginActivity.class);
            }
        });
        rlBaoliao.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user != null
                        && user.getLikename() != null
                        && !"".equals(user.getLikename()))
                    ActivityUtils.to(mContext, MybaoliaoActivity.class);
                else
                    ActivityUtils.to(mContext, LoginActivity.class);
            }
        });
        rlShare.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (user != null
                        && !user.getOpenid().isEmpty()) {
                    if (statused != null && statused.size() > 0) {
                        for (int i = 0; i < statused.size(); i++) {
                            if (statused.get(i).getIdentification().equals("promoter")) {
                                Bundle bundle = new Bundle();
                                String TAG = "3";
                                SHARETYPE = "OK";
                                bundle.putString("usergroupid", statused.get(i).getKey());
                                bundle.putString("user_openid", user.getOpenid());
                                bundle.putString("client", Const.APPSHAREID);
                                bundle.putString("tag", TAG);
                                bundle.putString("sharetype", SHARETYPE);
                                ActivityUtils.to(mContext, ImviteActivity.class, bundle);
                            }
                        }
                    } else {
                        shareApk();
                    }
                } else {
                    rlAu.setVisibility(View.GONE);
//                    rlInvite.setVisibility(View.GONE);
                    shareApk();
                }
            }
        });


        rlAu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                statusList();
            }
        });
        rlInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                String TAG = "2";
                SHARETYPE = "NO";
                bundle.putString("user_openid", user.getOpenid());
                bundle.putString("client", Const.APPSHAREID);
                bundle.putString("tag", TAG);
                bundle.putString("sharetype", SHARETYPE);
                ActivityUtils.to(mContext, ImviteActivity.class, bundle);


            }
        });

        //我的积分
        rlScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user = HandApplication.getInstance().mSpUtil
                        .getAccount();
                if (user == null
                        || user.getOpenid().isEmpty()) {
                    WarnUtils.toast(mContext, "请先登录!");
                    ActivityUtils.to(mContext, LoginActivity.class);
                    return;
                }
                Bundle bundle = new Bundle();
                bundle.putString("url", Const.HTTP_HEADKZ + "/plugin/integral/detail");
                bundle.putString("title", "我的积分");
                ActivityUtils.to(mContext, WebviewActivity.class, bundle);
            }
        });

        //积分抽奖
//        rlActives.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                user = HandApplication.getInstance().mSpUtil
//                        .getAccount();
//                if (user == null
//                        || user.getOpenid().isEmpty()) {
//                    WarnUtils.toast(mContext, "请先登录!");
//                    ActivityUtils.to(mContext, LoginActivity.class);
//                    return;
//                }
//                Bundle bundle = new Bundle();
//                bundle.putString("url", Const.HTTP_HEADKZ + "/plugin/draw");
//                bundle.putString("title", "积分抽奖");
//                ActivityUtils.to(mContext, WebviewActivity.class, bundle);
//
//            }
//        });

    }

    private void shareApk() {
        ShareUtils
                .shareSdk(
                        MineActivity.this,
                        "身边很多的朋友都在用"
                                + getResources().getString(
                                R.string.app_name)
                                + "App,生活阅读,爆料互动,全包含!赶快进入我们吧!点击下载哦!",
                        getResources().getString(
                                R.string.app_name) + "由南京市路特软件有限公司倾力打造，立足本地，辐射国内，面向全国各地，集新闻资讯传播、公共事务办理、市民生活服务于一体的城市移动应用云平台。", "", "",
                        Const.SHARE_API.FRIEND, umShareListener);
    }

    private PopupWindow popupWindow = null;

    //用户认证
    private void statusList() {

        LayoutInflater inflater = (LayoutInflater)
                getSystemService(LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_status_pop, null);
        TextView tv_ok = (TextView) view.findViewById(R.id.status_ok);
        lv = (ListView) view.findViewById(R.id.status_list);
        popupWindow = new PopupWindow(view, WindowManager.LayoutParams.MATCH_PARENT, 500);
        // 设置动画效果 [R.style.AnimationFade 是自己事先定义好的]
        popupWindow.setAnimationStyle(R.style.AnimationFade);
        popupWindow.showAtLocation(rlLogin, Gravity.BOTTOM, 0, 0);
        popupWindow.setFocusable(true);
        popupWindow.setTouchable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.update();
        tv_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        adapter2 = new StatusAdapter(mContext, status);
        lv.setAdapter(adapter2);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                String TAG = "1";
                SHARETYPE = "NO";
                bundle.putString("usergroupid", status.get(position).getKey());
                bundle.putString("user_openid", user.getOpenid());
                bundle.putString("client", Const.APPSHAREID);
                bundle.putString("tag", TAG);
                bundle.putString("sharetype", SHARETYPE);
                ActivityUtils.to(mContext, ImviteActivity.class, bundle);
                popupWindow.dismiss();
            }
        });
    }

    ListView lv;

    /**
     * 加载用户信息
     */
    private void loadUser() {
        user = HandApplication.getInstance().mSpUtil
                .getAccount();
        if (user != null
                && !user.getOpenid().isEmpty()) {
            rlAu.setVisibility(View.VISIBLE);
            nickname.setText(user.getLikename());
            Glide.with(mContext.getApplicationContext()).load(user.getFacepic()).placeholder(R.drawable.right_menu_person).error(R.drawable.right_menu_person).into(ivLogin);
            nickname.setVisibility(View.VISIBLE);
            llLogin.setVisibility(View.GONE);
            nickname.setText(user.getLikename());
            llPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ActivityUtils.to(mContext, PersonActivity.class);

                }
            });
            llSign.setVisibility(View.VISIBLE);//签到可见
            if (user.getIs_sign() == 1) {
                txtSign.setText("已签到");
                txtSign.setTextColor(Color.parseColor("#ffb3b3"));
                txtSign.setBackgroundResource(R.drawable.shape_rectangle_login_alpha);
            } else {
                txtSign.setText("签 到");
                txtSign.setTextColor(Color.parseColor("#ffffff"));
                txtSign.setBackgroundResource(R.drawable.shape_rectangle_login);
            }
            txtSign.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user.getIs_sign() == 0) {
                        getSign(user.getOpenid());
                    }
                }
            });
            getScore(user.getOpenid());//获取积分

        } else {// 登录按钮可见
            llLogin.setVisibility(View.VISIBLE);
            nickname.setVisibility(View.GONE);
            llSign.setVisibility(View.GONE);
            rlAu.setVisibility(View.GONE);
            Glide.with(mContext.getApplicationContext()).load("").placeholder(R.drawable.right_menu_person).error(R.drawable.right_menu_person).into(ivLogin);
//            ivLogin.setBackgroundResource(R.drawable.right_menu_person);
            llPerson.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user != null
                            && !user.getOpenid().isEmpty()) {
                        ActivityUtils.to(mContext, PersonActivity.class);
                    }
                }
            });
        }

    }

    ResultDao resultDao;
    ResultDao resultSignDao;

    /**
     * 获取积分
     */
    void getScore(String openid) {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    resultDao = new Gson().fromJson(response, ResultDao.class);
                    if (resultDao != null && resultDao.getState().equals("1")) {
                        txtScore.setText((resultDao.getScore() + "分"));
                    } else {
                        txtScore.setText("0");
                    }
                } catch (Exception e) {
                    WarnUtils.toast(mContext, "解析数据失败" + e.getMessage());
                }

            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        GlobalTools.getScore(volleyRequest, openid);
    }

    /**
     * 积分签到
     */
    void getSign(String openid) {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {

            @Override
            public void reqSuccess(String response) {
                // 成功的回调 : 可以操作返回的数据
                try {
                    resultSignDao = new Gson().fromJson(response, ResultDao.class);
                    if (resultSignDao != null && resultSignDao.getState().equals("1")) {
                        //签到成功
//                        WarnUtils.toast(mContext, resultSignDao.getMessage());
                        showdialog();
                        txtSign.setText("已签到");
                        txtSign.setTextColor(Color.parseColor("#ffb3b3"));
                        txtSign.setBackgroundResource(R.drawable.shape_rectangle_login_alpha);
                        user.setIs_sign(1);
                        HandApplication.getInstance().mSpUtil.saveAccount(user);
                        HandApplication.user = HandApplication.getInstance().mSpUtil.getAccount();
                        getScore(user.getOpenid());
                    } else {
                        WarnUtils.toast(mContext, resultSignDao.getMessage());
                    }
                } catch (Exception e) {
                    WarnUtils.toast(mContext, "解析数据失败" + e.getMessage());
                }

            }

            @Override
            public void reqError(String error) {
                // 失败的回调 ：失败的提醒
                WarnUtils.toast(mContext, "获取数据失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("user_openid", openid);
        GlobalTools.getSign(volleyRequest, map);
    }

    void showdialog() {
        CustomDialog2.Builder customDialog = new CustomDialog2.Builder(this);
        customDialog.setMessage("签到成功! " + "获得" + resultSignDao.getScore() + "个积分");
        customDialog.setMessage2("已签到" + resultSignDao.getSum() + "天,坚持签到可获得更多奖励哦!");
        customDialog.setPositiveButton("我知道啦", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();

            }
        });
        customDialog.setNegativeButton(null, null);

        Window window = new CustomDialog(this).getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        // 设置透明度为0.3
        lp.alpha = 0.6f;
        window.setAttributes(lp);
        window.setGravity(Gravity.CENTER);
//        window.setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND,
//                WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        customDialog.create().show();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
            WarnUtils.toast(mContext, "分享成功");
//            getShareScode(Const.SHARE_API.AVTIVITY);

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
