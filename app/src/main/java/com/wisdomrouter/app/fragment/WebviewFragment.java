package com.wisdomrouter.app.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.wisdomrouter.app.BaseFragment;
import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.fragment.ui.WebviewActivity;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/13 0013.
 */
public class WebviewFragment extends BaseFragment {
    @Bind(R.id.webView)
    WebView mWebView;
    @Bind(R.id.roundProgressBar)
    RoundProgressBar roundProgressBar;
    private View mView;
    private String url;
    private String title;
    private String user_openid;
    private String client;
    private Map<String, String> extraHeaders;
    private Handler mHandler = new Handler();
    int FILECHOOSER_RESULTCODE=1;
    ValueCallback<Uri> mUploadMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_webview, container, false);
        }
        ButterKnife.bind(this, mView);
        return mView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (this.getArguments() == null) {
            WarnUtils.toast(getActivity(), "暂无数据 getIntent() == null");
            roundProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        Bundle bundle = this.getArguments();
        url = bundle.getString("url");
        title = bundle.getString("title");
        user_openid = HandApplication.user.getOpenid();
        client = HandApplication.getInstance().mSpUtil.getAppConfigBean().getId();
        initData();

    }

    private void initData() {
        if (url == null || "".equals(url)) {
//            WarnUtils.toast(getActivity(), "未能获取链接 url==null");
            roundProgressBar.setVisibility(View.INVISIBLE);
            return;
        }
        extraHeaders = new HashMap<>();
        extraHeaders.put("TOKEN", Const.APPTOKEN);
        if (!TextUtils.isEmpty(user_openid))
            extraHeaders.put("USEROPENID", HandApplication.user.getOpenid());
        extraHeaders.put("CLIENTID", client);
        initWebSet();
    }

//    Handler handler=new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            switch (msg.what){
//                case 0:
//                    roundProgressBar.setProgress(progress);
//                    if (progress == 100) {
//                        roundProgressBar.setVisibility(View.GONE);
//                    }
//                    break;
//            }
//        }
//    };
private int progress;
    private void initWebSet() {
        WebSettings settings = mWebView.getSettings();
        settings.setJavaScriptEnabled(true);// JavaScript可用
        settings.setAppCacheEnabled(true);
        settings.setBuiltInZoomControls(true);
        settings.setSupportZoom(true);
//        settings.setJavaScriptCanOpenWindowsAutomatically(true);
//        settings.setUseWideViewPort(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        CookieSyncManager.createInstance(getActivity());
        mHandler.post(new Runnable() {
            public void run() {
                CookieSyncManager.getInstance().startSync();
                CookieManager.getInstance().removeSessionCookie();
                mWebView.loadUrl(url, extraHeaders);
            }
        });
    // TODO 要修改的地方，进度条
        mWebView.setWebChromeClient(new WebChromeClient() {// 进度控制
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    super.onProgressChanged(view, newProgress);
                    progress=newProgress;
                    roundProgressBar.setProgress(progress);
                    if (progress == 100) {
                        roundProgressBar.setVisibility(View.GONE);
                    }

//                    handler.sendEmptyMessage(0);

                }
            // Android > 4.1.1 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                        String acceptType, String capture) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                getActivity().startActivityForResult(
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
                getActivity().startActivityForResult(
                        Intent.createChooser(intent, "完成操作需要使用"),
                        FILECHOOSER_RESULTCODE);
            }

            // Android < 3.0 调用这个方法
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                mUploadMessage = uploadMsg;
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                getActivity().startActivityForResult(
                        Intent.createChooser(intent, "完成操作需要使用"),
                        FILECHOOSER_RESULTCODE);

            }

        });
        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url, extraHeaders); // 在当前的webview中跳转到新的url携带token
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
            }
        });

        mWebView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK && mWebView.canGoBack()) {
                        //表示按返回键 时的操作
                        mWebView.goBack(); //后退
                        return true; //已处理
                    }
                }
                return false;
            }
        });
    }

//    @Override
//    public void onPause() {
//        super.onPause();
//        mWebView.clearFormData();
//        mWebView.canGoBack();
//        mWebView.destroy();
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);

    }

}