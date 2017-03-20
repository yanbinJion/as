package com.wisdomrouter.app.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.wisdomrouter.app.Const;
import com.wisdomrouter.app.HandApplication;
import com.wisdomrouter.app.R;
import com.wisdomrouter.app.utils.WarnUtils;
import com.wisdomrouter.app.view.RoundProgressBar;

import java.util.HashMap;
import java.util.Map;

public class OnepageFragment extends Fragment {

	private View onPageView;
	private String url;
	private String title;
	private Handler mHandler = new Handler();
	private Map<String, String> extraHeaders;

	RoundProgressBar roundProgressBar;
	WebView mWebView;
	Bundle bundle;

	public interface OnkeyBackListener {
		void onkeyBack(WebView v);
	}

	private OnkeyBackListener listener;

	public void setOnkeybackListener(OnkeyBackListener listener) {
		this.listener = listener;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == onPageView) {
			onPageView = inflater.inflate(R.layout.activity_webview, null);
		}
		Bundle bundle = getArguments();
		url=bundle.getString("url");
		title=bundle.getString("title");
		return onPageView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mWebView = (WebView) onPageView.findViewById(R.id.webView1);
		if (listener!=null)
		listener.onkeyBack(mWebView);
		roundProgressBar = (RoundProgressBar) onPageView
				.findViewById(R.id.roundProgressBar);
		
		if (url == null || "".equals(url)) {
			WarnUtils.toast(getActivity(), "未能获取链接 url==null");
			roundProgressBar.setVisibility(View.INVISIBLE);
			return;
		}
		extraHeaders = new HashMap<String, String>();
//		if (title.equals("政务厅")) {
			extraHeaders.put("TOKEN", Const.APPTOKEN);
			if (HandApplication.user != null
					&& HandApplication.user.getOpenid() != null
					&& !"".equals(HandApplication.user.getOpenid())) {
				extraHeaders
						.put("USEROPENID", HandApplication.user.getOpenid());
			}
//		}
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);// JavaScript可用
		settings.setAppCacheEnabled(true);
		settings.setBuiltInZoomControls(true);
//		mWebView.setInitialScale(100);// 初始显示比例100%
		settings.setSupportZoom(true);
		settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		CookieSyncManager.createInstance(getActivity());
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
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (mWebView.canGoBack() && event.getKeyCode() == KeyEvent.KEYCODE_BACK
//				&& event.getRepeatCount() == 0) {
//			mWebView.goBack();
//			return true;
//		}
//
//		return false;
//	}


}
