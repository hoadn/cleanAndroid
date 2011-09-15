package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

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

public class HSZS extends QuoteGridActivity {
	private static final int stocktype = 100;

	private RequestParams requestParams;
	private String[] cols;
	private String title1;
	private int requestType;
	@SuppressWarnings("unused")
	private int allStockNums = 0;

	private String stocks, stocksname;
	private int pageNum = 10, grid = 0;

	private boolean firstComing = true;
	private boolean nLock = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());

		requestParams = new RequestParams();
		requestParams.setMarket("SHA");
		requestParams.setPaixu("zqdm");
		setContentView(R.layout.zr_table);

		this.activityKind = Global.QUOTE_HSZS;

		zhouqi = getResources().getStringArray(R.array.time_menu);
		paiming = getResources().getStringArray(R.array.zqpm_menu);

		String[] toolbarname = new String[] { Global.TOOLBAR_MENU,
				Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,
				Global.TOOLBAR_REFRESH };

		title1 = "恒生指数";
		initTitle(R.drawable.njzq_title_left_back, 0, title1);
		initToolBar(toolbarname, Global.BAR_TAG);

		cols = getResources().getStringArray(R.array.hszs_cols);

		// 根据不同分辨率获得可显示行数
		allStockNums = StockInfo.getStockInfoSize(stocktype);
		pageNum = CssSystem.getTablePageSize(HSZS.this);
		rowHeight = CssSystem.getTableRowHeight(HSZS.this);
		init(2);
		
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(1, false, R.color.zr_newlightgray);
		setToolBar(2, false, R.color.zr_newlightgray);
	}

	protected void initTitle(int resid1, int resid2, String str) {
		super.initTitle(resid1, resid2, str);
		changeTitleBg();
	}

	protected void init(final int type) {
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				// Looper.prepare();
				Log.i("##################", mLock + ">>>>>>>>");
				if (mLock &&nLock && type == 1) {
					timetips = DateTool.getLongTime();
					if (grid == 0) {// 为0时按照缓存中的代码去取数据
						stocks = StockInfo.getStockInfo(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocktype);
						stocksname = StockInfo.getStockName(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocktype);
						quoteData = ConnService.getGridData(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocks);
					} else
						quoteData = ConnService.execute(requestParams,
								requestType);

					try {

						if (Utils.isHttpStatus(quoteData)) {

							if (grid == 0) {// 为0时按照缓存中的代码去取数据
								allStockNums = StockInfo
										.getStockInfoSize(stocktype);
							} else {
								allStockNums = quoteData.getInt("totalrecnum");
							}

							list.clear();
							JSONArray jArr = (JSONArray) quoteData
									.getJSONArray("data");
							len = jArr.length();
							for (int i = 0; i < jArr.length(); i++) {
								JSONArray jA = (JSONArray) jArr.get(i);
								CssStock cssStock = new CssStock();
								cssStock.setZjcj(jA.getDouble(1));
								cssStock.setZf(jA.getDouble(12));
								cssStock.setZd(jA.getDouble(14));
								cssStock.setZje(jA.getDouble(0));

								cssStock.setJrkp(jA.getDouble(5));
								cssStock.setZrsp(jA.getDouble(6));
								cssStock.setZgcj(jA.getDouble(8));
								cssStock.setZdcj(jA.getDouble(9));

								cssStock.setAmp(jA.getDouble(13));
								cssStock.setMarket(jA.getString(20));
								cssStock.setStkname(jA.getString(19));
								cssStock.setStkcode(jA.getString(18));

								list.add(cssStock);
							}
							if (len < pageNum) {
								list.addAll(TradeUtil.fillHSZSListToNull(len,
										pageNum));
							}
							isNetworkError = 0;
						} else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						isNetworkError = -2;
						Log.e("tag", e.toString());
					}
				} else {
					stocks = StockInfo.getStockInfo(Integer
							.parseInt(requestParams.getBegin()), Integer
							.parseInt(requestParams.getEnd()), stocktype);
					stocksname = StockInfo.getStockName(Integer
							.parseInt(requestParams.getBegin()), Integer
							.parseInt(requestParams.getEnd()), stocktype);
				}

				mLock = isRefreshTime();
				mHandler.sendEmptyMessage(0);
				mHandler.postDelayed(r, Config.hkquoterefresh);
			}
		};
		mHandler.post(r);
	}

	protected void handlerData() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					if (isNetworkError < 0 && firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					if (list.isEmpty()) {
						if (grid == 0) {// 为0时按照缓存中的代码去取数据
							list.addAll(StockInfo.fillListToNull(0, pageNum,
									stocks, stocksname));
						} else {
							list.addAll(TradeUtil.fillListToNull(0, pageNum));
						}
					}

					refreshHSZSUI(list, cols);
					
					if(selectTag>=0) {
						setSelectRow(selectTag);
					}
				} catch (JSONException e) {

				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					// 进度条消失
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
		mLock = false;
		this.nLock = false;
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mLock = true;
		this.nLock = true;
		initPopupWindow();
		setToolBar();
	}

	protected void toolBarClick(int tag, View v) {
		switch (tag) {
		case 0:
			onOption();
			break;
		case 3:
			this.nLock = true;
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
}
