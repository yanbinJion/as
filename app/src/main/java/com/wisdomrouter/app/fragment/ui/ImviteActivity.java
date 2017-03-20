package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.wisdomrouter.app.BaseDetailActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/29.
 */
public class ImviteActivity extends BaseDetailActivity {
    private String usergroupid, user_openid, client;
    private Handler mHandler = new Handler();
    private Map<String, String> extraHeaders;

    public ValueCallback<Uri> mUploadMessage;
    public static final int FILECHOOSER_RESULTCODE = 0;

    @ViewInject(id = R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;
    @ViewInject(id = R.id.webView1)
    WebView mWebView;
    String url;

    @SuppressLint("JavascriptInterface")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_webview);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
//        initTitleBar();
        if (getIntent().getStringExtra("tag").equals("1")) {
            url = Const.HTTP_HEADKZ + "/plugin/invitation/authentication";
        } else if (getIntent().getStringExtra("tag").equals("2")) {
            url = Const.HTTP_HEADKZ + "/plugin/invitation/inputcode";
        } else {
            url = Const.HTTP_HEADKZ + "/plugin/invitation/share";
        }
        extraHeaders = new HashMap<>();

        usergroupid = getIntent().getStringExtra("usergroupid");
        user_openid = getIntent().getStringExtra("user_openid");

        client = getIntent().getStringExtra("client");
        extraHeaders.put("USERGROUPID", usergroupid);
        extraHeaders.put("USEROPENID", user_openid);
        extraHeaders.put("CLIENTID", client);
        extraHeaders.put("TOKEN", Const.APPTOKEN);
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// JavaScript可用
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieSyncManager.createInstance(this);

        mHandler.post(new Runnable() {
            public void run() {
                CookieSyncManager.getInstance().startSync();
                CookieManager.getInstance().removeSessionCookie();
                mWebView.loadUrl(url, extraHeaders);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {// 进度控制
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                roundProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    roundProgressBar.setVisibility(View.GONE);
                }

            }

            /*****************android中使用WebView来打开本机的文件选择器 *************************/
            //  js上传文件的<input type="file" name="fileField" id="fileField" />事件捕获
            //  Android  > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), FILECHOOSER_RESULTCODE);

            }

            //3.0 + 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), FILECHOOSER_RESULTCODE);
            }

            // Android < 3.0 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "完成操作需要使用"), ImviteActivity.FILECHOOSER_RESULTCODE);

            }
            /**************end***************/

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, extraHeaders); // 在当前的webview中跳转到新的url携带token
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public WebResourceResponse shouldInterceptRequest(WebView view,
                                                              String url) {
                // 非超链接(如Ajax)请求无法直接添加请求头，现拼接到url末尾,这里拼接一个imei作为示例
                return super.shouldInterceptRequest(view, url);

            }
        });
        if (getIntent().getStringExtra("sharetype").equals("OK")) {
            initTitleBar("", R.drawable.img_share, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (HandApplication.user.getOpenid() != null) {
                        ShareUtils
                                .shareSdk(
                                        ImviteActivity.this,
                                        "身边很多的朋友都在用"
                                                + getResources().getString(
                                                R.string.app_name)
                                                + "App,生活阅读,爆料互动,全包含!赶快进入我们吧!点击下载哦!",
                                        getResources().getString(
                                                R.string.app_name)+"由南京市路特软件有限公司倾力打造，立足本地，辐射国内，面向全国各地，集新闻资讯传播、公共事务办理、市民生活服务于一体的城市移动应用云平台。", "", "",
                                        Const.SHARE_API.IMVITE, umShareListener);
                    }
                }
            });
        } else if (getIntent().getStringExtra("sharetype").equals("NO")) {
            initTitleBarForLeft("");
        }

    }

    /* (non-Javadoc)
         * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
         * 处理查看图库的回调函数
         */
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    Context mContext = this;


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            mWebView.goBack();
            return true;
        } else {
            finish();
            return true;
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
}
