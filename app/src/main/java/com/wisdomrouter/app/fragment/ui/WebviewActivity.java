package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.ConsoleMessage;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.GeolocationPermissions;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wisdomrouter.app.BaseActivity;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.wisdomrouter.app.R.id.txtTitle;

public class WebviewActivity extends BaseActivity {

    @Bind(R.id.webView1)
    WebView mWebView;
    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;
    private String url;
    private String title;
    private String user_openid;
    private String client;
    private Handler mHandler = new Handler();
    private Map<String, String> extraHeaders = new HashMap<>();
    Context mContext = this;

    @SuppressLint("JavascriptInterface")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_webview);
        ButterKnife.bind(this);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common_new);

        if (getIntent() == null) {
            WarnUtils.toast(this, "暂无数据  getIntent() == null");
            return;
        }
        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");
        user_openid = HandApplication.getInstance().mSpUtil.getAccount() == null ? "" : HandApplication.getInstance().mSpUtil.getAccount().getOpenid();
        client = Const.APPSHAREID;

        initTitleBar(title == null ? "" : title, 0, null);

        if (TextUtils.isEmpty(url)) {
            WarnUtils.toast(this, "未能获取链接 url==null");
            roundProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        extraHeaders.put("TOKEN", Const.APPTOKEN);
        if (!TextUtils.isEmpty(user_openid)) {
            extraHeaders.put("USEROPENID", user_openid);
            extraHeaders.put("CLIENTID", client);

        }
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// JavaScript可用
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setUseWideViewPort(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setNeedInitialFocus(true);
        mHandler.post(new Runnable() {
            public void run() {
                CookieSyncManager.getInstance().startSync();
                CookieManager.getInstance().removeSessionCookie();
                mWebView.loadUrl(url, extraHeaders);
            }
        });
        mWebView.setWebChromeClient(new WebChromeClient() {// 进度控制
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                initTitleBar(title == null ? "" : title, 0, null);
            }
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                roundProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    roundProgressBar.setVisibility(View.GONE);
                }
            }

            // Android > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                WebviewActivity.this.startActivityForResult(
                        Intent.createChooser(intent, "完成操作需要使用"),
                        FILECHOOSER_RESULTCODE);

            }

            // 3.0 + 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                WebviewActivity.this.startActivityForResult(
                        Intent.createChooser(intent, "完成操作需要使用"),
                        FILECHOOSER_RESULTCODE);
            }

            // Android < 3.0 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                WebviewActivity.this.startActivityForResult(
                        Intent.createChooser(intent, "完成操作需要使用"),
                        FILECHOOSER_RESULTCODE);

            }


        });
        mWebView.setWebViewClient(
                new WebViewClient() {
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                          view.loadUrl(url, extraHeaders); // 在当前的webview中跳转到新的url携带token
                                          return super.shouldOverrideUrlLoading(view, url);
                                      }


                                  }

        );



    }


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

    int FILECHOOSER_RESULTCODE = 1;
    ValueCallback mUploadMessage;


    @Override
    protected void onActivityResult(int requestCode, int resultCode,
                                    Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
//            mUploadMessage = wcci.getmUploadMessage();
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null
                    : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        }
    }
}
