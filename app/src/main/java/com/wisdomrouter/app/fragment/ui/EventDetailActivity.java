package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.EventDetail;
import com.wisdomrouter.app.fragment.bean.ShareScore;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.TimeUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RatioImageView;
import com.wisdomrouter.app.view.image.ImageShowActivity;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@SuppressLint("ResourceAsColor")
public class EventDetailActivity extends BaseDetailActivity implements
        OnClickListener {
    @ViewInject(id = R.id.event_scro)
    ScrollView event_scro;
    @ViewInject(id = R.id.event_title)
    TextView tv_title;
    @ViewInject(id = R.id.event_time)
    TextView tv_time;
    @ViewInject(id = R.id.event_click)
    TextView tv_click;
    @ViewInject(id = R.id.event_date)
    TextView tv_date;
    @ViewInject(id = R.id.event_starttime)
    TextView tv_starttime;
    @ViewInject(id = R.id.event_endtime)
    TextView tv_endtime;
    @ViewInject(id = R.id.event_address)
    TextView tv_address;
    @ViewInject(id = R.id.event_person)
    TextView tv_person;
    @ViewInject(id = R.id.event_detail)
    WebView wv_detail;
    @ViewInject(id = R.id.event_num)
    TextView tv_num;
    @ViewInject(id = R.id.event_img)
    RatioImageView tv_img;
    @ViewInject(id = R.id.event_ok)
    Button tv_ok;

    private EventDetail eventData;
    Gson gson = new Gson();
    String key;
    protected ImageLoader imageLoader = ImageLoader.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_event_detail);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);
        tv_ok.setOnClickListener(this);
        if (getIntent() != null) {
            key = this.getIntent().getExtras().getString("key");
        }


    }

    Context mContext = this;



    @Override
    protected void onStart() {
        super.onStart();
        initData();
    }

    private void initData() {
        VolleyHandler<String> volleyRequest = new VolleyHandler<String>() {
            @Override
            public void reqSuccess(String response) {
                eventData = gson.fromJson(response, EventDetail.class);
                initView();
            }

            @Override
            public void reqError(String error) {
                WarnUtils.toast(EventDetailActivity.this,
                        "数据获取失败" + error);
            }
        };
        Map<String, String> map = new HashMap<>();
        map.put("key", key);
        if (HandApplication.getInstance().mSpUtil.getAccount() != null && !TextUtils.isEmpty(HandApplication.getInstance().mSpUtil.getAccount().getOpenid()))
            map.put("user_openid", HandApplication.getInstance().mSpUtil.getAccount().getOpenid());
        GlobalTools.getActiveDetail(volleyRequest, map);

    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        initWebView();
        tv_title.setText(eventData.getTitle() == null ? "" : eventData
                .getTitle());
        tv_time.setText(TimeUtil.getStrDate2(eventData.getCreatetime()));
        tv_click.setText(eventData.getClick() == null ? "" : eventData
                .getClick());
        tv_date.setText(TimeUtil.getStrDateG(eventData.getStarttime()) + " ~ "
                + TimeUtil.getStrDateG(eventData.getEndtime()));
        tv_starttime.setText(TimeUtil.getStrDate2(eventData.getActivity_begintime()));
        tv_endtime.setText(TimeUtil.getStrDate2(eventData.getActivity_endtime()));
        tv_address.setText(eventData.getActaddress() == null ? "" : eventData.getActaddress());
        tv_person.setText(eventData.getCount() == null ? "" : eventData
                .getCount());
        wv_detail.loadDataWithBaseURL(null, wrapHtml(eventData.getContent()),
                "text/html", "utf-8", null);
        tv_num.setText("限" + eventData.getNumber() + "人报名");
        tv_img.setAspectRatio(1.5f);
        Glide.with(this).load(eventData.getIndexpic()).into(tv_img);
        initTitleBar("活动", R.drawable.img_share, new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShareUtils.shareSdk(EventDetailActivity.this,
                        eventData.getTitle(), eventData.getDesc(), eventData.getIndexpic(), key,
                        Const.SHARE_API.AVTIVITY, umShareListener);
            }
        });
        long currentTime = new Date().getTime();
        long start = 0L;
        if (eventData.getStarttime() != null) {
            start = Long.parseLong(eventData.getActivity_begintime());
        }
        long end = 0L;
        if (eventData.getEndtime() != null) {
            end = Long.parseLong(eventData.getActivity_endtime());
        }
        long mStart = start * 1000;
        long mEnd = end * 1000;
        if (currentTime < mStart) {
            tv_ok.setText("报名未开始");
            tv_ok.setEnabled(false);
            tv_ok.setBackgroundResource(R.drawable.bg_event_darkgray);
        } else if (currentTime > mEnd) {
            tv_ok.setText("报名已结束");
            tv_ok.setEnabled(false);
            tv_ok.setBackgroundResource(R.drawable.bg_event_darkgray);
        } else if (eventData.getIscomplete() == 1) {
            tv_ok.setText("已报名");
            tv_ok.setEnabled(false);
            tv_ok.setBackgroundResource(R.drawable.bg_event_darkgray);
        } else {
            tv_ok.setText("我要报名");
            tv_ok.setEnabled(true);
            tv_ok.setBackgroundResource(R.drawable.shape_rectangle_lanmu);
        }

    }

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

    private void addImageClickListner() {
        // 这段js函数的功能就是，遍历所有的img几点，并添加onclick函数，在还是执行的时候调用本地接口传递url过去
        wv_detail
                .loadUrl("javascript:(function(){"
                        + "var objs = document.getElementsByTagName(\"img\");"
                        + "var imgurl='';"
                        + "for(var i=0;i<objs.length;i++)  "
                        + "{"
                        + "imgurl+=objs[i].src+',';"
                        + " objs[i].onclick=(function(j){return function(){window.imagelistner.openImage(imgurl,j);}; })(i);  "
                        + "}" + "} " + ")()");
    }

    private void initWebView() {
        wv_detail.getSettings().setSupportZoom(true); // 可以缩放
        wv_detail.getSettings().setJavaScriptEnabled(true);
        wv_detail.getSettings().setLoadWithOverviewMode(true);
        wv_detail.addJavascriptInterface(new JavascriptInterface(this),
                "imagelistner");
        wv_detail.getSettings().setLayoutAlgorithm(
                LayoutAlgorithm.SINGLE_COLUMN);

        wv_detail.setVerticalScrollBarEnabled(false);
        wv_detail.setHorizontalScrollBarEnabled(false);
        // 进行渲染
        wv_detail.getSettings().setBlockNetworkImage(true);
        wv_detail.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);// 缓存模式
        wv_detail.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
            }
        });
        wv_detail.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url); // 在当前的webview中跳转到新的url
                return super.shouldOverrideUrlLoading(view, url);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.event_ok:
                if (HandApplication.user == null
                        || HandApplication.user.getOpenid().isEmpty()) {
                    ActivityUtils.to(this, LoginActivity.class);
                    WarnUtils.toast(EventDetailActivity.this, "请先登录!");
                    return;
                }
                if (eventData.getIscomplete() == 0) {
                    Bundle bundle = new Bundle();
                    bundle.putString("key", key);
                    ActivityUtils.to(this, ApplyActivity.class, bundle);
                    return;
                } else {
                    if (eventData.getIscomplete() == 1) {
                        WarnUtils.toast(EventDetailActivity.this,
                                "您已经参加过报名了,无需再次报名!");
                    }
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    tv_person.setText(eventData.getCount() == null ? "" : eventData
                            .getCount());
                }
                break;
            default:
                break;
        }
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
