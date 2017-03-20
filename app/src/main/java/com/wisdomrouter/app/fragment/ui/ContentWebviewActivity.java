package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.bean.ShareScore;
import com.wisdomrouter.app.fragment.bean.UserDao;
import com.wisdomrouter.app.tools.GlobalTools;
import com.wisdomrouter.app.tools.VolleyHandler;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.StringUtil;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class ContentWebviewActivity extends FinalActivity {

    private String url;
    private String sharetitle;
    private String indexpic, sharedesc;
    private String key;
    private String type;
    private String title;
    private Map<String, String> extraHeaders;
    private String versionName;
    @ViewInject(id = R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;
    @ViewInject(id = R.id.webView1)
    WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_content_webview);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        if (getIntent() != null) {
            url = getIntent().getStringExtra("url");
            title = getIntent().getStringExtra("title");
            sharetitle = getIntent().getStringExtra("sharetitle");
            indexpic = getIntent().getStringExtra("indexpic");
            key = getIntent().getStringExtra("key");
            type = getIntent().getStringExtra("type");
            sharedesc = getIntent().getStringExtra("sharedesc");
        }
        initTitleBar();
        initwebview();
    }

    Context mContext = this;

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:videoPause();");
                        finish();
                    }
                });
                finish();
            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        if (type.equals("welive")) {
            rightButton.setVisibility(View.VISIBLE);
            rightButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ShareUtils.shareSdk(ContentWebviewActivity.this, sharetitle, sharedesc, indexpic, key, Const.SHARE_API.LIVES, umShareListener);

                }
            });
        } else
            rightButton.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (mWebView.canGoBack()) {
                mWebView.goBack();
                return true;
            } else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:videoPause();");
                        finish();
                    }
                });

                return true;
            }
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /**使用SSO授权必须添加如下代码 */
        if (requestCode == 1 && resultCode == RESULT_OK) {
            initwebview();//登录成功后重新加载界面
        }
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    private void initwebview() {
        extraHeaders = new HashMap<>();
//        extraHeaders.put("key",key);
        extraHeaders.put("TOKEN", Const.APPTOKEN);
        if (HandApplication.user != null
                && !TextUtils.isEmpty(HandApplication.user.getOpenid())) {
            extraHeaders.put("USEROPENID", HandApplication.user.getOpenid());
        }

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// JavaScript可用
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(false);
        settings.setSupportZoom(false);
        //不显示webview缩放按钮
        mWebView.loadUrl(url, extraHeaders);
        mWebView.setWebChromeClient(new WebChromeClient() {// 进度控制
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                roundProgressBar.setProgress(newProgress);
                if (newProgress > 80 && roundProgressBar.getVisibility() == roundProgressBar.VISIBLE) {
                    roundProgressBar.setVisibility(View.GONE);
                }
            }
        });
        mWebView.addJavascriptInterface(new JavascriptInter(this, ContentWebviewActivity.this
                , key, type, title, indexpic), "wst");//添加js点击回调
    }
    //获取设备信息
    private String getJson() {
        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        StringBuilder sb = new StringBuilder();
        sb.append("deviceId:" + tm.getDeviceId() + ",");
        sb.append("devicename:" + Build.MANUFACTURER + ",");
        sb.append("os:" + "Android" + ",");
        sb.append("devicesys:" + Build.VERSION.RELEASE + ",");
        sb.append("appversion:" + loadVersion() + ",");
        sb.append("devicemodel:" + Build.MODEL);
        return "{" + sb.toString() + "}";
    }

    /**
     * 获取版本号
     *
     * @return
     */
    private String loadVersion() {
        int localVersionCode = 1000;
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(
                    getPackageName(), 0);
            if (pinfo != null) {
                localVersionCode = pinfo.versionCode;
                versionName = pinfo.versionName;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return String.valueOf(localVersionCode);
    }

    class JavascriptInter {
        private Context context;
        private Activity ac;
        private String title, indexpic, key, type;
//        ShareUtils.shareSdk(ac, title, "", indexpic, key, Const.SHARE_API.ARTICLE, umShareListener);

        public JavascriptInter(Context context, Activity ac, String key, String type, String title, String indexpic) {
            this.context = context;
            this.indexpic = indexpic;
            this.key = key;
            this.title = title;
            this.ac = ac;
            this.type = type;

        }

        @JavascriptInterface
        public void AppCMD(String str, final String callback, String json) {

            if (str != null && str.equals("share")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:videoPause();");
//                        finish();
                    }
                });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShareUtils.shareSdk(ac, title,"", indexpic, key, Const.SHARE_API.ARTICLE, umShareListener);
                    }
                });

            } else if (str != null && str.equals("comments")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:videoPause();");
                    }
                });
                Bundle bundle = new Bundle();
                bundle.putString("id", key);
                bundle.putString("type", type);
                ActivityUtils.to(context, CommentActivity.class, bundle);
            } else if (str != null && str.equals("login")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:videoPause();");
                    }
                });
                if (StringUtil.isEmpty(HandApplication.user.getOpenid())) {
                    WarnUtils.toast(getApplicationContext(), "请先登录！");
                    Intent intent = new Intent();
                    intent.setClass(context, LoginActivity.class);
                    startActivityForResult(intent, 1);
                }
            } else {
            }
            if (callback != null) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mWebView.loadUrl("javascript:utils.viewMethodCb." + callback + "(\"" + getJson() + "\");");
                    }
                });
            }
        }

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
