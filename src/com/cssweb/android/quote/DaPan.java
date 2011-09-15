/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)DaPan.java 下午08:22:35 2010-10-17
 */
package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import com.cssweb.android.base.QuoteGridActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

/**
 * 大盘指数
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class DaPan extends QuoteGridActivity {
	private final String TAG = "DaPan";

	private Context mContext = DaPan.this;
	
	private String[] cols;
	private int allStockNums = 0;
	private int begin = 1, end = 10;
	private int init = 0;
	
	private String stocks, stocksname;
	private int pageNum = 10;

	private boolean nLock = true;
	private boolean firstComing = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		this.activityKind = Global.QUOTE_DAPAN;
		
		setContentView(R.layout.zr_table);
		
		String[] toolbarname = new String[]{ 
			Global.TOOLBAR_MENU, Global.TOOLBAR_SHANGYE, 
			Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_REFRESH };
		
		initTitle(R.drawable.njzq_title_left_back, 0, "大盘指数");
		initToolBar(toolbarname, Global.BAR_TAG);

		cols = getResources().getStringArray(R.array.index_cols);
		
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(mContext);
		rowHeight = CssSystem.getTableRowHeight(mContext);

		begin = 1;
		end = pageNum;
		allStockNums = StockInfo.getStockInfoSize(20);
		setToolBar(1, false, R.color.zr_newlightgray);
		init(2);
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    	setTitleText("大盘指数");
    }
	
	protected void init(final int type) {
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				Log.i("#########dapan mLock##########", mLock+">>>>>>>>>>");
				stocks = StockInfo.getStockInfo(begin, end, 20);
				stocksname = StockInfo.getStockName(begin, end, 20);
			    if(mLock&&nLock&&type==1) {
					timetips = DateTool.getLongTime();
			    	quoteData = ConnService.getGridData(begin, end, stocks);
			    	
			    	try {
						if(Utils.isHttpStatus(quoteData)) {
							list.clear();
							JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
							len = jArr.length();
							for (int i = 0; i < jArr.length(); i++) {
								JSONArray jA = (JSONArray)jArr.get(i);
								CssStock cssStock = new CssStock();
								cssStock.setZrsp(jA.getDouble(6));
								cssStock.setStkname(jA.getString(19));
								cssStock.setStkcode(jA.getString(18));
								cssStock.setZjcj(jA.getDouble(1));
								cssStock.setZf(jA.getDouble(12));
								cssStock.setZd(jA.getDouble(14));
								cssStock.setBjw1(jA.getDouble(2));
								cssStock.setSjw1(jA.getDouble(3));
								cssStock.setXs(jA.getLong(10));
								cssStock.setZl(jA.getDouble(4));
								cssStock.setHs(jA.getDouble(17));
								cssStock.setJrkp(jA.getDouble(5));
								cssStock.setZgcj(jA.getDouble(8));
								cssStock.setZdcj(jA.getDouble(9));
								cssStock.setZje(jA.getDouble(0));
								cssStock.setAmp(jA.getDouble(13));
								cssStock.setLb(jA.getDouble(11));
								cssStock.setMarket(jA.getString(20));
								
								String jtsy = jA.getString(28);
								if (Double.valueOf(jtsy) ==0 ){
									jtsy="--";
								}
								String pjgb = jA.getString(25);
								if (Double.valueOf(pjgb) ==0 ){
									pjgb="--";
								}
								String zsz = jA.getString(26);
								if (Double.valueOf(zsz) ==0 ){
									zsz="--";
								}
								String zb = jA.getString(27);
								if (Double.valueOf(zb) ==0 ){
									zb="--";
								}
								String ybjj = jA.getString(24);
								if (Double.valueOf(ybjj) == 0){
									ybjj="--";
								}
								String ybsl = jA.getString(23);
								if (Double.valueOf(ybsl) == 0 ){
									ybsl="--";
								}
								cssStock.setJtsy(jtsy );
								cssStock.setPjgb(pjgb);
								cssStock.setZsz(zsz);
								cssStock.setZb(zb);
								cssStock.setYbjj(ybjj);
								cssStock.setYbsl(ybsl);
								list.add(cssStock);
							}
							if(len<pageNum) {
								list.addAll(TradeUtil.fillListToNull(len, pageNum));
							}
							isNetworkError = 0;
						}
						else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						Log.e(TAG, e.toString());
						isNetworkError = -2;
					} 
				}
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.postDelayed(r, Config.fenleirefresh);
			}
		};
		mHandler.post(r);
	}
	
	protected void handlerData() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					if(isNetworkError<0&&firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					if(list.isEmpty()) {
						list.addAll(StockInfo.fillListToNull(0, pageNum, stocks, stocksname));
					}
					refreshIndexUI(list, cols);
					if(selectTag>=0) {
						setSelectRow(selectTag);
					}
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				} finally {
					//进度条消失
					hiddenProgressToolBar();
				}
			}
		};
		runOnUiThread(r);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		this.nLock = false;
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.nLock = true;
        initPopupWindow();
		setToolBar();
	}
	
	protected void onPageUp() {
		int i1 = begin;
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(1, false, R.color.zr_newlightgray);
		}
		else {
			i1 -= pageNum;
			i2 = end - pageNum;
			if(i1 <= 1) {
				setToolBar(1, false, R.color.zr_newlightgray);
				setToolBar(2, true, R.color.zr_white);
			}
			else {
				setToolBar(1, true, R.color.zr_white);
				setToolBar(2, true, R.color.zr_white);
			}
		}
		i1 = (i1 <= 1)?1:i1;
		//i2 = (i2<pageNum)?pageNum:i2;
		begin = i1;
		end = i2;
		setToolBar();
	}
	
	protected void onPageDn() {
		if(init == 0) {
	    	this.init = 1;
	    	
		}
		int i1 = begin + pageNum;
		int i2 = end + pageNum;
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(1, true, R.color.zr_white);
			setToolBar(2, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(1, true, R.color.zr_white);
			setToolBar(2, true, R.color.zr_white);
		}
		begin = i1;
		end = i2;
		setToolBar();
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
			onPageUp();
			break;
		case 2:
			onPageDn();
			break;
		case 3:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	protected void cancelThread() {
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	
	

	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
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
		if(begin <= 1) {
			return ;
		}
		onPageUp();
	}
}
