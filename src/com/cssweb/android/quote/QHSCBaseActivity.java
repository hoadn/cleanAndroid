package com.cssweb.android.quote;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;

import com.cssweb.android.common.Config;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

public class QHSCBaseActivity extends QHSCGridActivity {
	private int stocktype = 201;

	private RequestParams requestParams;
	private String[] cols;
	private String title1 = "股指期货";
	private int allStockNums = 0;

	private String stocks, stocksname, market, exchange;
	StringBuilder code;
	StringBuilder name;
	private int pageNum = 10, grid = 0;

	// private ArrayList<String[]> stringList;

	private AlertDialog myDialog1 = null;
	private OnClickListener listener1 = null;
	private String[] menu = null;

	private boolean nLock = true;
	protected boolean firstComing = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());

		requestParams = new RequestParams();
		requestParams.setPaixu("zqdm");
		setContentView(R.layout.zr_hk_table);

		stringList = new ArrayList<String[]>();
		this.activityKind = getActivityKind();

		zhouqi = getResources().getStringArray(R.array.time_menu);
		paiming = getResources().getStringArray(R.array.zqpm_menu);

		String[] toolbarname = gettoolbarname();
		initToolBar(toolbarname, Global.BAR_TAG);

		Bundle bundle = this.getIntent().getExtras();
		int colsId = -1;
		if (bundle != null) {
			colsId = bundle.getInt("taitou", -1);
			stocktype = bundle.getInt("stocktype");
			market = bundle.getString("market");
			exchange = bundle.getString("exchange");
			title1 = bundle.getString("title");
			int type = bundle.getInt("type", 0);
			if (type != 0) {
				menu = getResources().getStringArray(type);
			}
			requestParams.setMarket(market);
		}
		initTitle(R.drawable.njzq_title_left_back, 0, title1);
		cols = getResources().getStringArray(
				(colsId != -1) ? colsId : R.array.qhsc_cols);

		// 根据不同分辨率获得可显示行数
		allStockNums = StockInfo.getStockInfoSize(stocktype, exchange);
		pageNum = CssSystem.getTablePageSize(QHSCBaseActivity.this);
		requestParams.setEnd(String.valueOf(pageNum));
		rowHeight = CssSystem.getTableRowHeight(QHSCBaseActivity.this);
		initblanktable();

		openOption();
		openPopup();

		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		initToolBar();
	}

	protected int getActivityKind() {
		return Global.SDZ;
	}

	protected String[] gettoolbarname() {
		return new String[] { Global.TOOLBAR_MENU, Global.TOOLBAR_PINGZHONG,
				Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,
				Global.TOOLBAR_REFRESH };
	}

	protected void initTitle(int resid1, int resid2, String str) {
		super.initTitle(resid1, resid2, str);
		changeTitleBg();
	}

	protected void initblanktable() {
		stocks = StockInfo.getStockInfo(Integer.parseInt(requestParams
				.getBegin()), Integer.parseInt(requestParams.getEnd()),
				stocktype, exchange);
		stocksname = StockInfo.getStockName(Integer.parseInt(requestParams
				.getBegin()), Integer.parseInt(requestParams.getEnd()),
				stocktype, exchange);
		try {
			initBlankTable(pageNum, cols.length, false);
			initTitle(pageNum, cols, false);
			initQuote(pageNum, stocks, stocksname);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void init(final int type) {
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				if (mLock && nLock && type == 1) {
					if (grid == 1) {
						quoteData = ConnService.execute(requestParams, 2);
						try {
							if (Utils.isHttpStatus(quoteData)) {
								allStockNums = quoteData.getInt("totalrecnum");
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						stocks = StockInfo.getStockInfo(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocktype,
								exchange);
						stocksname = StockInfo.getStockName(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocktype,
								exchange);
						quoteData = ConnService.getGridData(Integer
								.parseInt(requestParams.getBegin()), Integer
								.parseInt(requestParams.getEnd()), stocks);
					}
					try {
						if (Utils.isHttpStatus(quoteData)) {

							JSONArray jArr = (JSONArray) quoteData
									.getJSONArray("data");
							len = jArr.length();
							stringList = null;
							stringList = new ArrayList<String[]>();

							code = new StringBuilder();
							name = new StringBuilder();
							// 名称、现价、涨幅%、涨跌、买价、卖价、买量、卖量、现量、增仓、总量、今开、最高、最低、昨收、结算、昨结、持仓、仓差、总额。
							for (int i = 0; i < jArr.length(); i++) {
								JSONArray jA = (JSONArray) jArr.get(i);
								String[] tmp = new String[22];
								tmp[0] = String.valueOf(jA.getDouble(1));// 现价
								tmp[1] = String.valueOf(jA.getDouble(12));// 涨幅
								tmp[2] = String.valueOf(jA.getDouble(14));// 涨跌

								tmp[3] = String.valueOf(jA.getDouble(2));// 买价
								tmp[4] = String.valueOf(jA.getDouble(3));// 卖价
								tmp[5] = String.valueOf(jA.getDouble(17));// 买量
								tmp[6] = String.valueOf(jA.getDouble(7));// 卖量
								tmp[7] = String.valueOf(jA.getDouble(10));// 现量

								tmp[8] = String.valueOf(jA.getDouble(4));// 总量
								//
								tmp[9] = String.valueOf(jA.getDouble(5));// 今开
								tmp[10] = String.valueOf(jA.getDouble(8));// 最高
								tmp[11] = String.valueOf(jA.getDouble(9));// 最低

								tmp[12] = String.valueOf(jA.getDouble(11));// 结算
								tmp[13] = String.valueOf(jA.getDouble(15));// 昨结

								tmp[14] = String.valueOf(jA.getDouble(13));// 持仓
								tmp[15] = String.valueOf(jA.getDouble(16));// 仓差
								tmp[16] = String.valueOf(jA.getDouble(0));// 总额
								tmp[17] = String.valueOf(jA.getDouble(6));// 昨收
								
								
								tmp[18] = jA.getString(22);// S/B
								
								tmp[19] = jA.getString(18);// code
								tmp[20] = jA.getString(20);// market
								//tmp[21] = String.valueOf(jA.getDouble(6));// 用于判断颜色
								tmp[21] = String.valueOf(jA.getDouble(15));
								if (grid == 1) {
									code.append(NameRule.getExchange(jA.getString(20))).append(jA.getString(18))
											.append(",");
									name.append(jA.getString(19)).append(",");
								}

								stringList.add(tmp);
							}
							if (grid == 1 && null != code && !"".equals(code)
									&& null != name && !"".equals(name)) {
								stocks = code.toString();
								stocksname = name.toString();
							}
							isNetworkError = 0;
						} else {
							stringList = null;
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						isNetworkError = -2;
						Log.e("tag", e.toString());
					}

					mLock = isRefreshTime();
					mHandler.sendEmptyMessage(0);
					mHandler.postDelayed(r, Config.hkquoterefresh);
				}
			}
		};
		mHandler.post(r);
	}

	protected void handlerData() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					if (pageNum >= allStockNums) {
						// 避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
						setToolBar(3, false, R.color.zr_newlightgray);
					}
					initTitle(pageNum, cols, false);
					if (isNetworkError < 0 && firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
						return;
					}
					// initQuote(pageNum, stocks, stocksname);
					if (isNetworkError >= 0) {
						refreshTable(stringList, pageNum, exchange);
					}
				} catch (JSONException e) {
					Log.e("tag", e.toString());
				} finally {
					// 进度条消失
					hiddenProgressToolBar();
				}
			}
		};
		runOnUiThread(r);
	}

	@Override
	protected void initQuote() {
		try {
			initQuote(pageNum, stocks, stocksname);
		} catch (JSONException e) {
			e.printStackTrace();
		}
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

	protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if (i1 <= 1) {
			setToolBar(2, false, R.color.zr_newlightgray);
		} else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum;
			if (i1 <= 1) {
				setToolBar(2, false, R.color.zr_newlightgray);
				setToolBar(3, true, R.color.zr_white);
			} else {
				setToolBar(2, true, R.color.zr_white);
				setToolBar(3, true, R.color.zr_white);
			}
		}
		i1 = (i1 <= 1) ? 1 : i1;
		// i2 = (i2<pageNum)?pageNum:i2;
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}

	protected void onPageDn() {
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum;

		if (i2 >= allStockNums) {
			// i2 = allStockNums;
			setToolBar(2, true, R.color.zr_white);
			setToolBar(3, false, R.color.zr_newlightgray);
		} else {
			setToolBar(2, true, R.color.zr_white);
			setToolBar(3, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);

		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}

	protected void zqlbDesc2(int t1, int t2) {
		switch (t1) {
		// case 0: requestParams.setPaixu("zqdm"); break; //代码
		case -2:
			requestParams.setPaixu("zjcj");
			break; // 现价

		case -3:
			requestParams.setPaixu("zf");
			break; // 涨幅
		case -4:
			requestParams.setPaixu("zd");
			break; // 涨跌
		case -5:
			requestParams.setPaixu("bjw1");
			break; // 买价
		case -6:
			requestParams.setPaixu("sjw1");
			break; // 卖价
		case -7:
			requestParams.setPaixu("bsl");
			break; // 买量
		case -8:
			requestParams.setPaixu("ssl");
			break; // 总量

		case -9:
			requestParams.setPaixu("xs");
			break; // 现量
		case -10:
			requestParams.setPaixu("cjsl");
			break; // 总量
		case -11:
			requestParams.setPaixu("jrkp");
			break; // 今开
		case -12:
			requestParams.setPaixu("zgcj");
			break; // 最高
		case -13:
			requestParams.setPaixu("zdcj");
			break; // 最低
		case -19:
			requestParams.setPaixu("zrsp");
			break; // 昨收

		case -14:
			requestParams.setPaixu("jrjs");
			break; // 结算
		case -15:
			requestParams.setPaixu("zrjs");
			break; // 昨结
		case -16:
			requestParams.setPaixu("jrcc");
			break; // 持仓
		case -17:
			requestParams.setPaixu("zc");
			break; // 增仓
		case -18:
			requestParams.setPaixu("cjje");
			break; // 总额

		}
		switch (t2) {
		case 0:
			requestParams.setDesc("asc");
			break;
		case 1:
			requestParams.setDesc("desc");
			break;
		}
		grid = 1;
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(2, false, R.color.zr_newlightgray);
		setToolBar(3, true, R.color.zr_white);
		setToolBar();
	}

	protected void openOption() {
		listener1 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				grid = 1;
				onclickMenu(requestParams, which);

				title1 = menu[which];
				n1 = 2;// 默认不显示向上向下的箭头
				requestParams.setBegin("1");
				requestParams.setEnd(String.valueOf(pageNum));

				initToolBar();
				setToolBar();
			}
		};
		if (null != menu) {
			myDialog1 = new AlertDialog.Builder(QHSCBaseActivity.this)
					.setTitle("请选择期货品种").setItems(menu, listener1).create();
		}
	}

	protected void onclickMenu(RequestParams requestParams, int which) {

	}

	private void initToolBar() {
		if (pageNum >= allStockNums) {
			setToolBar(2, false, R.color.zr_newlightgray);
			setToolBar(3, false, R.color.zr_newlightgray);
		} else {
			setToolBar(2, false, R.color.zr_newlightgray);
			setToolBar(3, true, R.color.zr_white);
		}
	}

	protected void toolBarClick(int tag, View v) {
		switch (tag) {
		case 0:
			onOption();
			break;
		case 1:
			if (null != myDialog1 && !myDialog1.isShowing())
				myDialog1.show();
			break;
		case 2:
			onPageUp();
			break;
		case 3:
			onPageDn();
			break;
		case 4:
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

	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		int end = Integer.parseInt(requestParams.getEnd());
		if (end >= allStockNums) {
			return;
		}
		onPageDn();
	}

	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		int begin = Integer.parseInt(requestParams.getBegin());
		if (begin <= 1) {
			return;
		}
		onPageUp();
	}
}
