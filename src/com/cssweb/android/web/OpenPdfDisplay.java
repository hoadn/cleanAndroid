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
import android.util.Log;
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
public class OpenPdfDisplay extends GridViewActivity {
	private WebView webview;
	private String url = "";
	private String title = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.njzq_open_pdf);

		Bundle bundle = getIntent().getExtras();
		url = bundle.getString("url");
		title = bundle.getString("title");
		initView(url,title);

		initTitle(R.drawable.njzq_title_left_back, 0, title);
	}

	protected void initView( String url , String title) {
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
		webview.loadUrl(/*"http://iweb.njzq.cn/iphone/DocumentViewBean.do?docId=561621"*/url);
		Log.i("tag", url);
		
		webview.setWebChromeClient(new WebChromeClient() {

			// 设置网页加载的进度条
			public void onProgressChanged(WebView view, int newProgress) {
				
				if(newProgress == 50){
				}
				
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
	
	@Override
	protected void initTitle(int resid1, int resid2, String str) {
		// TODO Auto-generated method stub
		super.initTitle(resid1, resid2, str);
		
		changeTitleBg();
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
