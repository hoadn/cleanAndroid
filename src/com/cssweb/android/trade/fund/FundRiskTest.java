package com.cssweb.android.trade.fund;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;

import android.os.Bundle;
//import android.os.Handler;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class FundRiskTest extends CssBaseActivity {

	private WebView mWebView;
//	private Handler mHandler = new Handler();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_fundrisktest);
		setTitle("风险测评");
		initTitle(R.drawable.njzq_title_left_back, 0, "风险测评");
		String[] toolbarname = new String[] { Global.TOOLBAR_QUEDING,
				Global.TOOLBAR_BACK };
		initToolBar(toolbarname, Global.BAR_TAG);

		mWebView = (WebView) findViewById(R.id.riskwebview);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
//		mWebView.addJavascriptInterface(new Object() {
//			public void clickOnAndroid() {
//				mHandler.post(new Runnable() {
//					public void run() {
//						// mWebView.loadUrl("javascript:wave()");
//					}
//				});
//			}
//		}, "demo");
//		mWebView.loadUrl("http://www.qq.com");
		

	}

}
