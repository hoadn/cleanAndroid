/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FLineActivity.java 下午04:54:10 2010-12-24
 */
package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.util.Log;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.view.FTrendView;
import com.cssweb.quote.util.NameRule;

/**
 * 基金走势图
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FLineActivity extends CssBaseActivity {

	private FTrendView ftrendView = null;
	
	private String exchange;
	private String stockcode;
	private String stockname;
    private JSONArray arrayData = null;
	private boolean firstComing = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
		this.stockname = bundle.getString("stockname");
		setContentView(R.layout.zr_fundline_view);
		initTitle(R.drawable.njzq_title_left_back, 0, "基金走势图");
		//setTitle("基金走势图");
		ftrendView = (FTrendView)findViewById(R.id.zrviewftrend);
		ftrendView.setFocusable(true);
		ftrendView.requestFocus();
		showProgress();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	protected void init(final int type) {
		r = new Runnable() {
			public void run() {
				if(type==1) {
					try {
						String str = ConnService.getFundLine(exchange, stockcode, "day");
						Log.i("#######ftrend line#######", str+">>>>>>>>>");
						if(str!=null&&!str.equals("")&&ftrendView!=null) {
							arrayData = new JSONArray("["+str+"]");
							ftrendView.initData(arrayData);
							isNetworkError = 0;
						}
						else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						isNetworkError = -1;
					}
				}
                //通过Handler发布携带消息
                mHandler.sendEmptyMessage(0);
			}
		};
		new Thread(r).start();
	}
	
	protected void handlerData() {
		if(isNetworkError<0&&firstComing) {
			firstComing = false;
			toast(R.string.load_data_error);
		}
		if(arrayData!=null)
			ftrendView.invalidate();
		hiddenProgress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if( null!=ftrendView){
			ftrendView.reCycle();
			ftrendView = null;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initView(exchange, stockcode, stockname);
		initPopupWindow();
	}
	
	protected void initView(String exchange, String stockcode, String stockname) {
		CssStock css = new CssStock();
		css.setMarket(exchange);
		css.setExchange(NameRule.getMarket(exchange));
		css.setStkcode(stockcode);
		css.setStkname(stockname);
		RefreshTitle(css);
	}
}
