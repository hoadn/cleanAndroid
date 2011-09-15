/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuotePrice.java 上午11:51:25 2010-12-18
 */
package com.cssweb.android.quote;

import org.json.JSONException;

import android.os.Bundle;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.view.PriceView;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 行情报价（大盘+股票）
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class QuotePrice extends CssBaseActivity {
	private PriceView priceView = null;
	
	private String exchange;
	private String stockcode;
	private String stockname;
	
	private int numtype = 0;
    private int stockdigit = 0;
	private String stocktype = "0";

	private Thread thread = null;
	private boolean nLock = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        
		setContentView(R.layout.zr_price_view);

		initTitle(R.drawable.njzq_title_left_back, 0, "详细报价");
		
		Bundle bundle = getIntent().getExtras();
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
		this.stockname = bundle.getString("stockname");
		
		setTitleText(stockname);

		priceView = (PriceView)findViewById(R.id.zrviewprice);
		//priceView.setBackgroundResource(R.drawable.njzq_hqbj_fivequote_bg);
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(priceView!=null) {
			priceView.reCycle();
		}
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		nLock = false;
		if(priceView!=null) {
			priceView.reCycle();
		}
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
        initPopupWindow();
		showProgress();
	}
	
	protected void init(final int type) {
    	super.init(type);
		this.numtype = NameRule.getSecurityType(exchange, stockcode);
		this.stocktype = NameRule.getStockType(numtype);
		this.stockdigit = Utils.getStockDigit(numtype);
		priceView.setStockInfo(exchange, stockcode, stockname, stockdigit, stocktype, numtype);
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				while (!Thread.currentThread().isInterrupted()) {
					if(nLock&&mLock) {
						quoteData = ConnService.getDish("GET_PRICE_VOLUMEJSON", exchange, stockcode, stocktype);
					}
					mLock = isRefreshTime();
	                mHandler.sendEmptyMessage(0);
	                try {
						Thread.sleep(Config.fenshirefresh);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	

	protected void handlerData() {
		try {
			if(Utils.isHttpStatus(quoteData)) {
				priceView.initData(quoteData);
				priceView.invalidate();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		hiddenProgress();
	}
}
