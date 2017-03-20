package com.wisdomrouter.app.fragment.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.CommError;
import com.wisdomrouter.app.fragment.bean.ShareScore;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.fragment.bean.VoteDetailsDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.image.ImageShowActivity;

import net.tsz.afinal.FinalHttp;
import net.tsz.afinal.annotation.view.ViewInject;
import net.tsz.afinal.http.AjaxCallBack;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class VoteInformationActivity extends BaseActivity implements
        OnClickListener {
    @Bind(R.id.vote_info_img)
    ImageView ivVote;
    @Bind(R.id.vote_info_num)
    TextView tvItem;
    @Bind(R.id.vote_info_name)
    TextView tvName;
    @Bind(R.id.vote_info_count)
    TextView tvCount;
    @Bind(R.id.vote_info_detail)
    WebView wvContent;
    @Bind(R.id.vote_info_voted)
    Button btnvoted;
    @Bind(R.id.vote_info_canvassing)
    Button btnCanvass;


    private CommError commError;
    private Gson gson;

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    private VoteDetailsDao.Items item;
    private String votekey, isvoted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_vote_infomation);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        initTitleBar("投票项详情", 0, null);
        if (getIntent() != null) {
            votekey = getIntent().getStringExtra("votekey");
            isvoted = getIntent().getStringExtra("isvoted");
            item = (VoteDetailsDao.Items) getIntent().getSerializableExtra("item");
            voVote = (VoteDetailsDao) getIntent().getSerializableExtra("voVote");
        }
        initView();

        gson = new Gson();
    }

    private void initView() {
        initWebView();
        tvName.setText("参赛名称：" + (item.getTitle() == null ? "" : item.getTitle()));
        tvItem.setText("参赛编号：" + (item.getNo() == null ? "" : item.getNo()));
        wvContent.loadDataWithBaseURL(null, wrapHtml(item.getContent()), "text/html",
                "utf-8", null);
        tvCount.setText(String.valueOf(item.getCount()));
        imageLoader.displayImage(item.getIndexpic(), ivVote);
        btnvoted.setOnClickListener(this);
        btnCanvass.setOnClickListener(this);
    }

    /**
     * @description: 投票
     * @author:wangfanghui
     * @return:void
     */
    private void sendVote() {
        if (HandApplication.user == null
                || HandApplication.user.getOpenid() == null
                || "".equals(HandApplication.user.getOpenid())) {
            hideProgressDialog();
            WarnUtils.toast(VoteInformationActivity.this, "请先登录!");
            ActivityUtils.to(this, LoginActivity.class);
            return;
        } else if (isvoted != null && isvoted.equals("1")) {
            hideProgressDialog();
            WarnUtils.toast(VoteInformationActivity.this, "您已经投过票了!");
            return;
        }
        if (votekey == null || votekey.equals("")) {
            return;
        }
        if (attempSendVote()) {


            FinalHttp fh = new FinalHttp();
            fh.addHeader("TOKEN", Const.APPTOKEN);
            fh.get(Const.HTTP_HEADKZ
                            + "/plugin/vote2-api/post?key=" + votekey + "&itemkey=" + item.getItemkey()
                            + "&user_openid=" + HandApplication.user.getOpenid(),
                    new AjaxCallBack<Object>() {
                        @Override
                        public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
                            super.onLoading(count, current);
                        }

                        @Override
                        public void onSuccess(Object t) {
                            try {
                                hideProgressDialog();
                                commError = gson.fromJson(t.toString(),
                                        CommError.class);
                                if (commError != null) {
                                    if (commError.getState() == 0) {

                                        if (commError.getErrors() != null) {
                                            if (commError.getErrors().getPlatform() != null
                                                    && commError.getErrors()
                                                    .getPlatform()[0] != null) {
                                                WarnUtils
                                                        .toast(VoteInformationActivity.this,
                                                                commError
                                                                        .getErrors()
                                                                        .getPlatform()[0]);
                                            } else if (commError.getErrors()
                                                    .getUser_openid() != null
                                                    && commError.getErrors()
                                                    .getUser_openid()[0] != null) {
                                                WarnUtils
                                                        .toast(VoteInformationActivity.this,
                                                                commError
                                                                        .getErrors()
                                                                        .getUser_openid()[0]);
                                            } else {
                                                WarnUtils
                                                        .toast(VoteInformationActivity.this,
                                                                commError
                                                                        .getMessage() == null ? "数据异常,提交失败!"
                                                                        : commError
                                                                        .getMessage());

                                            }
                                        } else {
                                            WarnUtils
                                                    .toast(VoteInformationActivity.this,
                                                            commError.getMessage() == null ? "数据异常,提交失败!"
                                                                    : commError
                                                                    .getMessage());
                                        }

                                    } else if (commError.getState() == 1) {
                                        WarnUtils
                                                .toast(VoteInformationActivity.this,
                                                        commError.getMessage() == null ? "投票成功!"
                                                                : commError
                                                                .getMessage());
                                        if (commError.getScore() != 0)
                                            mHandler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    WarnUtils.toast(mContext, "投票成功", commError.getScore());
                                                }
                                            }, 1000);

                                        tvCount.setText((item.getCount() + 1) + "");
                                    }
                                } else {
                                    WarnUtils.toast(VoteInformationActivity.this,
                                            "数据获取解析异常,请稍后进入!");
                                }
                            } catch (Exception e) {
                                WarnUtils.toast(VoteInformationActivity.this,
                                        "数据获取解析异常,请稍后进入!");
                                return;
                            }

                        }

                        @Override
                        public void onStart() {
                            // 开始http请求的时候回调
                            // showDialog();
                        }

                        @Override
                        public void onFailure(Throwable t, int errorNo,
                                              String strMsg) {
                            hideProgressDialog();
                            // 加载失败的时候回调
                            WarnUtils.toast(VoteInformationActivity.this,
                                    "数据获取异常,请稍后进入" + strMsg);
                        }
                    });
        }
    }

    Handler mHandler = new Handler();

    // js通信接口
    public class JavascriptInterface {

        private Context context;

        public JavascriptInterface(Context context) {
            this.context = context;
        }

        @android.webkit.JavascriptInterface
        public void openImage(String img, final int k) {
            String[] imgs = img.split(",");
            ArrayList<String> imgsUrl = new ArrayList<String>();
            for (String s : imgs) {
                imgsUrl.add(s);
            }
            Intent intent = new Intent();
            intent.putStringArrayListExtra("infos", imgsUrl);
            intent.putExtra("pos", k);
            intent.setClass(context, ImageShowActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }

    private void initWebView() {
        wvContent.getSettings().setSupportZoom(true); // 可以缩放
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setLoadWithOverviewMode(true);
        wvContent.addJavascriptInterface(new JavascriptInterface(this),
                "imagelistner");
        wvContent.getSettings().setLayoutAlgorithm(
                LayoutAlgorithm.SINGLE_COLUMN);
        // 进行渲染
        wvContent.getSettings().setBlockNetworkImage(true);
        wvContent.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 缓存模式
        wvContent.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_info_voted:
                sendVote();
                break;
            case R.id.vote_info_canvassing:
                ShareUtils.shareSdk(VoteInformationActivity.this,
                        item.getTitle() == null ? "" : item.getTitle(),"", item.getIndexpic(), votekey, Const.SHARE_API.VOTES,umShareListener);
                break;

            default:
                break;
        }

    }

    private VoteDetailsDao voVote;
    Context mContext = this;

    private Boolean attempSendVote() {
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
                WarnUtils.toast(mContext, "投票时间未开始,或者已经结束!");
                return false;
            }
        }
        if (HandApplication.user == null || TextUtils.isEmpty(HandApplication.user.getOpenid())) {
            WarnUtils.toast(mContext, "请先登录!");
            ActivityUtils.to(mContext, LoginActivity.class);
            return false;
        }

        if (!TextUtils.isEmpty(voVote.getIsvote()) && voVote.getIsvote().equals("1")) {
            WarnUtils.toast(mContext, "您已经投过票了!");
            return false;
        }

        return true;
    }
    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Log.d("plat", "platform" + platform);
            WarnUtils.toast(mContext, "分享成功");
            getShareScode(Const.SHARE_API.ACTIVITY_BEHAVIOR);

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
}
