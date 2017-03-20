package com.wisdomrouter.app.fragment.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.ActivityUtils;
import com.wisdomrouter.app.utils.ShareUtils;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import net.tsz.afinal.FinalActivity;
import net.tsz.afinal.annotation.view.ViewInject;

import java.util.HashMap;
import java.util.Map;

public class NewsArticleActivity extends FinalActivity {

    private String url;
    private String title, sharetitle, indexpic, key, type;
    private Map<String, String> extraHeaders;
    private ContentWebviewActivity.JavascriptInter javascriptInter;
    @ViewInject(id = R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;
    @ViewInject(id = R.id.webView1)
    WebView mWebView;
    private UMShareListener umShareListener=new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA share_media) {
            Toast.makeText(NewsArticleActivity.this,"分享成功",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(SHARE_MEDIA share_media, Throwable throwable) {

        }

        @Override
        public void onCancel(SHARE_MEDIA share_media) {

        }
    };


    @SuppressLint({"SetJavaScriptEnabled", "JavascriptInterface"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (this.getIntent() == null) {
            WarnUtils.toast(this, "暂无数据  getIntent() == null");
            return;
        }

        url = getIntent().getExtras().getString("url");
        title = getIntent().getExtras().getString("title");
        sharetitle = getIntent().getExtras().getString("sharetitle");
        indexpic = getIntent().getExtras().getString("indexpic");
        key = getIntent().getExtras().getString("key");
        type = getIntent().getExtras().getString("type");

        requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
        setContentView(R.layout.activity_content_webview);
        getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
                R.layout.activity_title_common);
        initTitleBar();
        if (url == null || "".equals(url)) {
            WarnUtils.toast(this, "未能获取链接 url==null");
            roundProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        roundProgressBar.setVisibility(View.VISIBLE);
        extraHeaders = new HashMap<>();           
        extraHeaders.put("TOKEN", Const.APPTOKEN);

        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// JavaScript可用
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
        mWebView.setHapticFeedbackEnabled(false);
        mWebView.loadUrl(url, extraHeaders);



        mWebView.setWebChromeClient(new WebChromeClient() {// 进度控制
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                roundProgressBar.setProgress(newProgress);
                if (newProgress == 100) {
                    roundProgressBar.setVisibility(View.GONE);
                }
            }


        });


        mWebView.setWebViewClient(new WebViewClient() {
                                      @Override
                                      public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                return super.shouldOverrideUrlLoading(view, url);
                                          view.loadUrl(url);
                                          return true;
                                      }

                                      @Override
                                      public void onPageFinished(WebView view, String url) {

                                          view.getSettings().setJavaScriptEnabled(true);

                                          super.onPageFinished(view, url);

                                      }
                                  }

        );

        mWebView.addJavascriptInterface(new JavascriptInter(this, NewsArticleActivity.this
                , key, type, sharetitle, indexpic), "imagelistner");//添加js点击回调
    }

    private void initTitleBar() {
        ImageView leftButton = (ImageView) findViewById(R.id.left_btn);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }
        });
        TextView titleText = (TextView) findViewById(R.id.title);
        titleText.setText(title);
        ImageView rightButton = (ImageView) findViewById(R.id.right_btn);
        rightButton.setVisibility(View.INVISIBLE);
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
    class JavascriptInter {
        private Context context;
        private Activity ac;
        private String title,  indexpic, key, type;



        public JavascriptInter(Context context,Activity ac,String key,String type,String title,String indexpic) {
            this.context = context;
            this.indexpic=indexpic;
            this.key=key;
            this.title=title;
            this.ac=ac;
            this.type=type;
        }
        @JavascriptInterface
        public void AppCMD(String str,String json) {
            if (str != null && str.equals("share")) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ShareUtils.shareSdk(ac, title, "", indexpic, key, Const.SHARE_API.ARTICLE, umShareListener);

                    }
                });

            } else if (str != null && str.equals("comments")) {
                Bundle bundle = new Bundle();
                bundle.putString("id", key);
                bundle.putString("type", type);
                ActivityUtils.to(context, CommentActivity.class, bundle);
            } else {
                Toast.makeText(context, "js未传值", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, intent);

    }

}
