package com.cssweb.android.quote;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import com.cssweb.android.base.DialogActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.android.view.KlineView;

/**
 * 行情K线实现部分
 * @author HUJUN
 */
public class KLineActivity extends DialogActivity {
	private Context context = KLineActivity.this;
	
	private KlineView klineView = null;
	
	private String[] toolbarname;
	
	/**市场**/
	private String exchange;
	
	/**代码**/
	private String stockcode;
	
	/**名称**/
	private String stockname;
	
	/**周期**/
	private String peroid = "day";
	
	private String peroidN = "(日线)";
	
	/**K线上半区域的指标**/
	private String mainIndicatorType = "ma";
	
	/**K线下班区域的指标**/
	private String indicatorType = "volume";
	
	private JSONObject joZB;
	
	private int msg = -1;
	private int ktype = 1;//1表示指标切换，2表示周期切换
	private boolean nLock = true;
	private boolean firstComing = true;
	
	private AlertDialog myDialog1 = null, myDialog2 = null;
	private OnClickListener listener1 = null, listener2 = null;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());

		this.activityKind = Global.QUOTE_KLINE;
		
		Bundle bundle = getIntent().getExtras();
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
		this.stockname = bundle.getString("stockname");
		CssSystem.exchange = bundle.getString("exchange");
		CssSystem.stockcode = bundle.getString("stockcode");
		CssSystem.stockname = bundle.getString("stockname");
		
		cssStock = new CssStock();
		cssStock.setMarket(this.exchange);
		cssStock.setStkcode(this.stockcode);
		cssStock.setStkname(this.stockname);
		
		setContentView(R.layout.zr_kline_view);
		
//		toolbarname = new String[]{ 
//				Global.TOOLBAR_MENU, Global.TOOLBAR_ZHIBIAO, 
//				Global.TOOLBAR_ZHOUQI, Global.TOOLBAR_F10, 
//				Global.TOOLBAR_ZOOMOUT, Global.TOOLBAR_ZOOMIN };
		toolbarname = new String[]{ 
				Global.TOOLBAR_MENU, Global.TOOLBAR_FENSHI,  
				Global.TOOLBAR_ZHOUQI, Global.TOOLBAR_ZHIBIAO, 
				Global.TOOLBAR_F10, Global.TOOLBAR_REFRESH };

		if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
			setToolbarByScreen(2);
		} else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
			setToolbarByScreen(1);
		}
		
		initTitle(R.drawable.njzq_title_left_back, 0, "个股K线");

		setTitleText(stockname + " " + peroidN);
		
		initKlineView();
		
		getZhibiao();
		getZhouqi();
	}
	
    @Override
	public void setRequestedOrientation(int requestedOrientation) {
		super.setRequestedOrientation(requestedOrientation);
	}

	protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	btnRight.setBackgroundResource(R.drawable.njzq_title_right_select);
    	btnRight.setTag(3);
    	changeTitleBg();
    }
	
	protected void init(final int type) {
		super.init(type);
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				try {
					//Log.i("########mnlock#########", mLock+">>>>>>"+nLock);
	                if(mLock&&nLock) {//mLock控制定时器、nLock避免Activity已经退出了，但是线程还在后台运行
	                	mHandler.removeMessages(0);
	                	switch(ktype) {
	                	case 1:
	                		quoteData = ConnService.getKlineData(context, exchange, stockcode, peroid, mainIndicatorType, indicatorType);
	                	case 2:
	                		if(quoteData!=null&&quoteData.has("loadFromServer")) {
		                		msg = 0;
		                		if(!indicatorType.toLowerCase().equals("volume")){
									if(quoteData.isNull(indicatorType)) {
										joZB = ConnService.getKlineIndicator(context, indicatorType.toLowerCase(), true, exchange, stockcode, peroid, quoteData.getBoolean("loadFromServer"));
										Log.i("@@@@@joZB@@@@@", joZB+">>>>>>>");
										if(joZB!=null&&!joZB.isNull("data")) {
											quoteData.put(indicatorType, joZB.getJSONArray("data"));
											msg = 2;
										}
										else {
											if(!quoteData.isNull(indicatorType))
												quoteData.remove(indicatorType);
											msg = -2;
										}
									}
								}
								
								if(mainIndicatorType.toLowerCase().equals("boll")){
									if(quoteData.isNull(mainIndicatorType)) {
										joZB = ConnService.getKlineIndicator(context, mainIndicatorType.toLowerCase(), true, exchange, stockcode, peroid, quoteData.getBoolean("loadFromServer"));
										if(joZB!=null&&!joZB.isNull("data")) {
											quoteData.put(mainIndicatorType, joZB.getJSONArray("data"));
											msg = 2;
										}
										else {
											if(!quoteData.isNull(mainIndicatorType))
												quoteData.remove(mainIndicatorType);
											msg = -3;
										}
									}
								}
		                	}
		                	else {
		                		msg = -1;
		                	}
	                	}
	                }
	                else {
	                	msg = -4;
	                }
				} catch (JSONException e) {
					e.printStackTrace();
					msg = -1;
				} 
                //通过Handler发布携带消息
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
				mHandler.postDelayed(r, Config.kxrefresh);
			}
		};
		mHandler.post(r);
	}
	
	/**
	 * msg=-1表示网络连接失败，弹出提示框
	 * msg=-2||-3需要判断是否是因为服务器没有文件还是真的网络异常，这种情况一般出现在日周月年线的时候处理
	 */
	protected void handlerData() {
		Runnable runable = new Runnable() {
			public void run() {
				try {
					if(msg == -1 && firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					else if (msg == -2 || msg == -3){
						if(quoteData.isNull("joTMP")) {//temp文件取不到的情况下
							Log.i(">>>>>temp文件取不到的情况下>>>>>>", ">>>>>>>>>>>>>>>>>>>>" + quoteData);
							refreshKline();
							return;
						}
						if(!quoteData.getBoolean("tradeFlag")){
							Log.i("@@@@@@@@@tradeFlag为true的时候@@@@@@@@@@@", quoteData+">>>>>>>>>>>");
							refreshKline();
							return;
						}
						JSONObject tempvalue = quoteData.getJSONObject("joTMP");
						if(peroid.equals("week")
								|| peroid.equals("month")
								|| peroid.equals("year")){ 
							Log.i("#####period11111111####", tempvalue.getString(peroid)+">>>>>>>>" + quoteData.getInt("tp"));
							if(tempvalue.getString(peroid)!=null) {
								if(tempvalue.isNull("ma")||quoteData.isNull("MA")||quoteData.isNull("K")) {
									Log.i("######1111111111日周月年没有历史数据的情况#####", ">>>>>>>>>>>>>>>>>");
									refreshKline();
									return;
								}
							}
						}

						Log.i("###########", "此种情况不刷新UI,避免切换的时候出现黑屏的现象" + mainIndicatorType);
						toast(R.string.load_data_error);
					}
					else if(msg == -4) {
						Log.i("###########", "此种情况不刷新UI,因为已经过刷新时间了" + mainIndicatorType);
					}
					else {
						refreshKline();
						return;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} finally {
					ktype = 1;
					hiddenProgress();
				}
			}
		};
		runOnUiThread(runable);
	}
	
	private void refreshKline() throws JSONException {
		if(quoteData!=null) {
			resetKline();
			klineView.initData(quoteData);
			klineView.refresh();
		}
		ktype = 1;
		hiddenProgress();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeHandler();
		if(klineView!=null) {
			klineView.reCycle();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		nLock = false;
		msg = -1;
		joZB = null;
		quoteData = null;
		removeHandler();
		if(klineView!=null) {
			klineView.reCycle();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
		showProgress();
	}
	
	private void initKlineView() {
		klineView = (KlineView)findViewById(R.id.zrviewkline);
		klineView.setFocusable(true);
		klineView.requestFocus();
		klineView.setStockInfo(exchange, stockcode, stockname);
		resetKline();
	}
	
	protected void resetKline() {
		setTitleText(stockname + peroidN);
		klineView.setPeriod(peroid);
		klineView.setIndicatorType(indicatorType.toUpperCase());
		klineView.setMainIndicatorType(mainIndicatorType.toUpperCase());
	}
	
	protected void getZhibiao() {
		listener1 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				myDialog1.dismiss();
				switch ( which )
				{
					case 0: 
						indicatorType = "VOLUME";
						break;
					case 1: 
						indicatorType = "MACD";
						break;
					case 2: 
						indicatorType = "KDJ";
						break;
					case 3: 
						indicatorType = "RSI";
						break;
					case 4: 
						mainIndicatorType = "BOLL";
						break;		
					case 5: 
						indicatorType = "BIAS";
						break;	
					case 6: 
						indicatorType = "OBV";
						break;		
					case 7: 
						indicatorType = "WR";
						break;			
					case 8: 
						indicatorType = "VR";
						break;		
					case 9: 
						indicatorType = "CCI";
						break;		
					case 10:
						indicatorType = "PSY";
						break;		
					case 11: 
						indicatorType = "ROC";
						break;		
					case 12: 
						mainIndicatorType = "MA";
						break;							
				}
				mLock = true;
				ktype = 2;
				//切换指标的时候重置状态
				klineView.resetStatus();
				showProgress();
			}
	    };
	    String[] menu = getResources().getStringArray(R.array.indicator_menu);
	    myDialog1 = new AlertDialog.Builder(KLineActivity.this)
        .setTitle("请选择指标")
        .setItems(menu, listener1)
        .create();
	}
	
	protected void getZhouqi() {
		String[] menu = getResources().getStringArray(R.array.klinezq_menu);
		listener2 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				myDialog1.dismiss();
				switch ( which )
				{
					case 0: 
						peroid = "day";
						break;		
					case 1: 
						peroid = "week";
						break;		
					case 2: 
						peroid = "month";
						break;		
					case 3: 
						peroid = "year";
						break;							
				}
				mLock = true;
				ktype = 1;
				//切换指标的时候重置状态
				klineView.resetStatus();
				setTimePeriod(peroid);
				showProgress();
			}
	    };
	    myDialog2 = new AlertDialog.Builder(KLineActivity.this)
        .setTitle("请选择周期")
        .setItems(menu, listener2)
        .create();
	}
	
	protected void zoomOut() {
		klineView.upHandler();
	}
	
	protected void zoomIn() {
		klineView.downHandler();
	}
	
	@Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if(config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	setToolbarByScreen(2);
        }
        else {
        	setToolbarByScreen(1);
        }
        //klineView.refresh();
    }
	
	private void setToolbarByScreen(int orientation) {
        if(orientation == 2) {
        	RelativeLayout l = (RelativeLayout) findViewById(R.id.zrtoolbar);
    		l.setVisibility(View.GONE);
        }
        else {
        	RelativeLayout l = (RelativeLayout) findViewById(R.id.zrtoolbar);
    		l.setVisibility(View.VISIBLE);
    		initToolBar(toolbarname, Global.BAR_TAG);
        }
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
			FairyUI.switchToWnd(Global.QUOTE_FENSHI, exchange, stockcode, stockname, KLineActivity.this);
			break;
		case 2:
		    if(!myDialog2.isShowing())
		    	myDialog2.show();
			break;
		case 3:
		    if(!myDialog1.isShowing())
		    	myDialog1.show();
			break;
		case 4:
			FairyUI.switchToWnd(Global.QUOTE_F10, exchange, stockcode, stockname, KLineActivity.this);
			break;
		case 5:
			this.ktype = 1;
			this.mLock = true;
			this.nLock = true;
			showProgress();
			break;
//		case 4:
//			zoomOut();
//			break;
//		case 5:
//			zoomIn();
//			break;
		}
	}
	
	public void removeHandler() {
		if(mHandler!=null)
			mHandler.removeCallbacks(r);
	}
	
	public void setTimePeriod(String p) {
		if(p.equals("day")) {
			peroidN = "(日线)";
		}
		else if(p.equals("week")) {
			peroidN = "(周线)";
		}
		else if(p.equals("month")) {
			peroidN = "(月线)";
		}
		else if(p.equals("year")) {
			peroidN = "(年线)";
		}
		else if(p.equals("min5")) {
			peroidN = "(5分钟)";
		}
		else if(p.equals("min15")) {
			peroidN = "(15分钟)";
		}
		else if(p.equals("min30")) {
			peroidN = "(30分钟)";
		}
		else if(p.equals("min60")) {
			peroidN = "(60分钟)";
		}
	}
}
