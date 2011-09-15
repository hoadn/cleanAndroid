/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FenLei.java 下午08:31:13 2010-10-17
 */
package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import com.cssweb.android.base.QuoteGridActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

/**
 * 分类
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FenLei extends QuoteGridActivity  {
	private final String TAG = "FenLei";
	
	private RequestParams requestParams;	
	private String[] cols;
	private String[] menu;
	private String title1;
	private int requestType;
	private int allStockNums = 0;

	private String stocks, stocksname;
	private int pageNum = 10;
	private int stocktype=-1, grid = 0;//默认为沪深A股

	private boolean nLock = true;
	private boolean firstComing = true;
	
	private AlertDialog myDialog1 = null;
	private OnClickListener listener1 = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		Bundle bundle = getIntent().getExtras();
		this.requestType = bundle.getInt("requestType");
		this.activityKind = Global.QUOTE_FENLEI;
		requestParams = new RequestParams();
		requestParams.setMarket("SHSZA");
		requestParams.setPaixu("zqdm");
		setContentView(R.layout.zr_table);
		
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_MENU, Global.TOOLBAR_PINGZHONG, 
				Global.TOOLBAR_PAIXU, Global.TOOLBAR_SHANGYE, 
				Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_REFRESH};
		
		initTitle(R.drawable.njzq_title_left_back, 0, "分类报价");
		initToolBar(toolbarname, Global.BAR_TAG);
		
		cols = getResources().getStringArray(R.array.stock_cols);

		menu = getResources().getStringArray(R.array.stock_type_menu);
		paiming = getResources().getStringArray(R.array.zqpm_menu);
		desc = getResources().getStringArray(R.array.stock_desc);
		//默认显示沪深A股
		title1 = menu[6];
		//title2 = paiming[0];

		//根据不同分辨率获得可显示行数
		allStockNums = StockInfo.getStockInfoSize(stocktype);
		pageNum = CssSystem.getTablePageSize(FenLei.this);
		rowHeight = CssSystem.getTableRowHeight(FenLei.this);
		openOption();

		openPopup();

		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(3, false, R.color.zr_newlightgray);
		
		init(2); 
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
    
    protected void init(final int type) {
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				//Looper.prepare();
				Log.i("#########fenlei mLock##########", mLock+">>>>>>>>>>");
				if(mLock&&nLock&&type==1) {
					timetips = DateTool.getLongTime();
					if(grid==0) {//为0时按照缓存中的代码去取数据
						stocks = StockInfo.getStockInfo(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
						stocksname = StockInfo.getStockName(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
						quoteData = ConnService.getGridData(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocks);
					}
					else
						quoteData = ConnService.execute(requestParams, requestType);
					try {
						if(Utils.isHttpStatus(quoteData)) {
							if(grid==0) {//为0时按照缓存中的代码去取数据
								allStockNums = StockInfo.getStockInfoSize(stocktype);
							}
							else {
								allStockNums = quoteData.getInt("totalrecnum");
							}
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
								cssStock.setWb(jA.getDouble(15));
								cssStock.setWc(jA.getLong(16));
								cssStock.setTp(jA.getInt(21));//停牌标记
								cssStock.setBsFlag(jA.getString(22));   //买卖盘
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
						isNetworkError = -2;
						Log.e(TAG, e.toString());
					}
				}
				else {
					stocks = StockInfo.getStockInfo(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
					stocksname = StockInfo.getStockName(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
				}
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.sendEmptyMessageDelayed(8, 3000);
        		mHandler.postDelayed(r, Config.fenleirefresh);
			}
		};
		mHandler.post(r);
	}
	
	protected void openOption() {
	    listener1 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				requestParams.setBegin("1");
				requestParams.setEnd(String.valueOf(pageNum));
				stocktype = which;
				grid = 0;
				switch ( which )
				{
					case 0: requestParams.setMarket("sha"); break;
					case 1: requestParams.setMarket("shb"); break;
					case 2: requestParams.setMarket("sza"); break;
					case 3: requestParams.setMarket("szb"); break;	
					case 4: requestParams.setMarket("shbond"); break;		
					case 5: requestParams.setMarket("szbond"); break;		
					case 6: requestParams.setMarket("shsza"); break;		
					case 7: requestParams.setMarket("shszb"); break;		
					case 8: requestParams.setMarket("SHSZBOND"); break;		
					case 9: requestParams.setMarket("SHSZFUND"); break;		
					case 10: requestParams.setMarket("shetf"); break;	
					case 11: requestParams.setMarket("szetf"); break;	
					case 12: requestParams.setMarket("szlof"); break;		
					case 13: requestParams.setMarket("SHSZWARRANT"); break;		
					case 14: requestParams.setMarket("SZZXB"); break;			
					case 15: requestParams.setMarket("SZCYB"); break;			
					case 16: requestParams.setMarket("SZTHREEBOARD"); break;							
				}
				title1=menu[which];
				n1 = 2;//默认不显示向上向下的箭头
				requestParams.setBegin("1");
				requestParams.setEnd(String.valueOf(pageNum));
				requestParams.setPaixu("zqdm");
				requestParams.setDesc("desc");
				setToolBar(3, false, R.color.zr_newlightgray);
				setToolBar(4, true, R.color.zr_white);
				setToolBar();
			}
	    };
	    myDialog1 = new AlertDialog.Builder(FenLei.this)
        .setTitle("请选择证券品种")
        .setItems(menu, listener1)
        .create();
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
						if(grid==0) {//为0时按照缓存中的代码去取数据
							list.addAll(StockInfo.fillListToNull(0, pageNum, stocks, stocksname));
						}
						else {
							list.addAll(TradeUtil.fillListToNull(0, pageNum));
						}
					}
					if(pageNum>allStockNums) {
						//避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
						setToolBar(4, false, R.color.zr_newlightgray);
					}
					setTitleText(title1);
					refreshUI(list, cols);
					if(selectTag>=0) {
						setSelectRow(selectTag);
					}
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					//进度条消失
					//hiddenProgressToolBar();
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
		nLock = false;
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
//    	initPopupWindowFromBottom();
        initPopupWindow();
		setToolBar();
	}
	
	protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(3, false, R.color.zr_newlightgray);
		}
		else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum;
			if(i1 <= 1) {
				setToolBar(3, false, R.color.zr_newlightgray);
				setToolBar(4, true, R.color.zr_white);
			}
			else {
				setToolBar(3, true, R.color.zr_white);
				setToolBar(4, true, R.color.zr_white);
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
	
	protected void onPageDn() {
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum;
		//Log.i("===============", i2+">>>>>>>>>>>>>>>>>"+allStockNums);
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(3, true, R.color.zr_white);
			setToolBar(4, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(3, true, R.color.zr_white);
			setToolBar(4, true, R.color.zr_white);
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
			onOption();
			break;
		case 1:
		    if(!myDialog1.isShowing())
		    	myDialog1.show();
			break;
		case 2:
		    if(!dlg.isShowing())
		    	dlg.show();
			break;
		case 3:
			onPageUp();
			break;
		case 4:
			onPageDn();
			break;
		case 5:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	@Override
	protected void handleClick(boolean flag) {
		if(flag){
			int t1 = timeSpinner.getSelectedItemPosition();
			int t2 = stateSpinner.getSelectedItemPosition();
			zqlbDesc(t1, t2);
		}else {
			
		}
	}
	
	
	protected void zqlbDesc(int t1, int t2) {
		switch ( t1 )
		{
			case 0: requestParams.setPaixu("zjcj"); n2=-3; break;
			case 1: requestParams.setPaixu("zf"); n2=-2; break;
			case 2: requestParams.setPaixu("zd"); n2=-4; break;
			case 3: requestParams.setPaixu("cjsl"); n2=-7; break;
			case 4: requestParams.setPaixu("cjje"); n2=-14; break;
			case 5: requestParams.setPaixu("wb"); n2=-17; break;
			case 6: requestParams.setPaixu("amp"); n2=-15; break;
			case 7: requestParams.setPaixu("zqdm"); n2=-3; break;							
		}
		//title2=paiming[t1];
		
		switch ( t2 )
		{
			case 0: requestParams.setDesc("desc"); n1=1; break;
			case 1: requestParams.setDesc("asc"); n1=0; break;						
		}

		grid = 1;
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(3, false, R.color.zr_newlightgray);
		setToolBar(4, true, R.color.zr_white);
		setToolBar();
	}
	
	protected void zqlbDesc2(int t1, int t2) {
		switch ( t1 )
		{
			case -2: requestParams.setPaixu("zf"); break;
			case -3: requestParams.setPaixu("zjcj"); break;
			case -4: requestParams.setPaixu("zd"); break;
			case -5: requestParams.setPaixu("bjw1"); break;
			case -6: requestParams.setPaixu("sjw1"); break;
			case -7: requestParams.setPaixu("cjsl"); break;
			case -8: requestParams.setPaixu("xs"); break;
			case -9: requestParams.setPaixu("hs"); break;	
			case -10: requestParams.setPaixu("jrkp"); break;	
			case -11: requestParams.setPaixu("zrsp"); break;	
			case -12: requestParams.setPaixu("zgcj"); break;	
			case -13: requestParams.setPaixu("zdcj"); break;	
			case -14: requestParams.setPaixu("cjje"); break;
			case -15: requestParams.setPaixu("amp"); break;	
			case -16: requestParams.setPaixu("lb"); break;	
			case -17: requestParams.setPaixu("wb"); break;	
			case -18: requestParams.setPaixu("wc"); break;							
		}
		//title2=cols[-t1];
		
		switch ( t2 )
		{
			case 0: requestParams.setDesc("asc"); break;
			case 1: requestParams.setDesc("desc"); break;						
		}

		grid = 1;
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(3, false, R.color.zr_newlightgray);
		setToolBar(4, true, R.color.zr_white);
		setToolBar();
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
