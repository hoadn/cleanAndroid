/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)WebViewDisplay.java 下午01:09:17 2010-9-20
 */
package com.cssweb.android.web;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.main.R;

/**
 * 
 * @version 1.0
 * @see
 * @since 1.0
 */
public class OpenHrefDisplay extends GridViewActivity {
	private WebView webview;
	private String url = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);

		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("url");
		initView(url);

		initTitle(R.drawable.njzq_title_left_back, 0, "文章阅读");
	}

	protected void initView( String url) {
		
		showProgress();

		webview = (WebView) findViewById(R.id.webview);
		WebSettings webSettings = webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webview.addJavascriptInterface(new JHMethodImpl(mHandler, this, webview), "cssweb");

		webview.setWebViewClient(new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				webview.loadUrl(url);
				return true;
			}
		});

		webview.clearCache(true);
		
		webview.setHorizontalScrollBarEnabled(false);
		webview.setVerticalScrollBarEnabled(true); 
		webview.loadUrl(url);
		
		
		webview.setWebChromeClient(new WebChromeClient() {
			// 设置网页加载的进度条
			public void onProgressChanged(WebView view, int newProgress) {
				if (newProgress == 100) {
					hiddenProgress();
				}
				super.onProgressChanged(view, newProgress);
			}

			// 设置应用程序的标题
			public void onReceivedTitle(WebView view, String title) {
				super.onReceivedTitle(view, title);
			}
		});

	}
	
	public class JHMethodImpl  extends JHMethod{

		public JHMethodImpl(Handler mHandler, Activity context, WebView webview) {
			super(mHandler, context, webview);
			
		}

		@Override
		public void showMenuImpl() {
			openMenu();
		}
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        initPopupWindow();
	}
}
