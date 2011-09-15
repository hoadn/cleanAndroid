/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)GlobalMarket.java 下午12:37:48 2011-3-11
 */
package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.view.View;

import com.cssweb.android.base.QuoteFundGridActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.Utils;

/**
 * 外围市场
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class GlobalMarket extends QuoteFundGridActivity {
	private RequestParams requestParams;	
	private String[] cols;
	private int allStockNums = 0;
	private Thread thread = null;
	private int pageNum = 10;
	private boolean firstComing = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestParams = new RequestParams();
		requestParams.setField("index_code");
		activityKind = Global.NJZQ_HQBJ_QQSC_WHSC;
		setContentView(R.layout.zr_static_table3);
		initTitle(R.drawable.njzq_title_left_back, 0, "外围市场");
		String[] toolbarname = new String[]{ Global.TOOLBAR_SHANGYE,"", Global.TOOLBAR_XIAYIYE, "",Global.TOOLBAR_REFRESH  };
		initToolBar(toolbarname, Global.BAR_TAG);
		cols = getResources().getStringArray(R.array.globalmarket_colsname);
		//setToolBar(0, false, R.color.zr_newlightgray);
		
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(GlobalMarket.this);
		rowHeight = CssSystem.getTableRowHeight(GlobalMarket.this);
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(0, false, R.color.zr_newlightgray);
		handlerData();
	}
	
	/**
	 * 改变背景色
	 */
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	/**
	 * 请求后台数据
	 */
	protected void init(final int type) {
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				quoteData = ConnService.getWorldMarket(requestParams.getField(), requestParams.getDesc(), requestParams.getBegin(), requestParams.getEnd());
				try {
					if (Utils.isHttpStatus(quoteData)){
						isNetworkError = 0;
					}else {
						isNetworkError = -1;
					}
				} catch (JSONException e) {
					isNetworkError = -2;
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
    
	/**
	 * 更新UI界面
	 */
    protected void handlerData() {
		try {
			if(isNetworkError<0&&firstComing) {
				firstComing = false;
				toast(R.string.load_data_error);
			}
			if(quoteData==null) {
				listqueryfund.clear();
				listqueryfund.addAll(TradeUtil.fillListToNull5(0, pageNum ));
				refreshMarketQueryUI(listqueryfund, cols, "3");
				hiddenProgressToolBar();
				return;
			}
			JSONArray jArr = quoteData.getJSONArray("data");
			int len = jArr.length();
			listqueryfund.clear();
			allStockNums = quoteData.getInt("totalrecnum");
			for (int i = 0; i < jArr.length(); i++) {
				JSONArray jA = (JSONArray)jArr.get(i);
				String [] jarr = new String [7];
				jarr[0] = jA.getString(0);
				jarr[1] = jA.getString(1);
				jarr[2] = jA.getString(3);
				jarr[3] = jA.getString(2);
				listqueryfund.add(jarr);
			}
			if (len < pageNum ){
				listqueryfund.addAll(TradeUtil.fillListToNull5(len, pageNum ));
			}
			refreshMarketQueryUI(listqueryfund, cols, "3");
			hiddenProgressToolBar();
		} catch (JSONException e) {
			e.printStackTrace();
		} finally {	
			//进度条消失
			hiddenProgressToolBar();
		}
	}

	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(r);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		setToolBar();
        initPopupWindow();
	}
	
	/**
	 * 上一页
	 */
	 protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(0, false, R.color.zr_newlightgray);
			return;
		}
		else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum ;
			if(i1 <= 1) {
				setToolBar(0, false, R.color.zr_newlightgray);
				setToolBar(2, true, R.color.zr_white);
			}
			else {
				setToolBar(0, true, R.color.zr_white);
				setToolBar(2, true, R.color.zr_white);
			}
		}
		i1 = (i1 <= 1)?1:i1;
		//i2 = (i2<pageNum)?pageNum:i2;
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	 /**
	  * 下一页
	  */
	 protected void onPageDn() {
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum ;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum ;
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(0, true, R.color.zr_white);
			setToolBar(2, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(0, true, R.color.zr_white);
			setToolBar(2, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onPageUp();
			break;
		case 2:
			onPageDn();
			break;
		case 4:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	 /**
	  * 取消线程
	  */
	protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	


	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		int end = Integer.parseInt(requestParams.getEnd()) ;
		if(end>=allStockNums) {
			return ;
		}
		onPageDn();
	}
	
	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		int begin = Integer.parseInt(requestParams.getBegin());
		if(begin <= 1) {
			return ;
		}
		onPageUp();
	}
	
	
}
