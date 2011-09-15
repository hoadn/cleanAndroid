package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.custom.CustomScrollView;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.android.view.FinanceMini;
import com.cssweb.android.view.PriceMini;
import com.cssweb.android.view.TrendView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

public class TrendActivity extends FlipperActiviy {
	//private final String TAG = "TrendActivity";
	private Context mContext = TrendActivity.this;
	
	private ViewFlipper viewFlipper;
	
	private Animation leftIn;
	private Animation leftOut;
	private Animation rightIn;
	private Animation rightOut;
	
	private CustomScrollView csView;
	private TrendView trendView = null;
	private PriceMini priceView = null;
	private FinanceMini financeView = null;
	
	private String exchange;
	private String stockcode;
	private String stockname;
	
	private TableLayout table_1;
	private ImageView preView;
	
	private String stocktype;
	private int type;
	private int from = 0;
	private int digit = 0;
	
	private JSONObject json= null, json2 = null, json3 = null;
	private boolean nLock = true;
	private boolean firstComing = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());

		this.activityKind = Global.QUOTE_FENSHI;
		
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
		
		setContentView(R.layout.zr_trend_view);
		
		type = NameRule.getSecurityType(exchange, stockcode);
		digit = Utils.getStockDigit(type);
		stocktype = NameRule.getStockType(type);
		table_1 = (TableLayout) findViewById(R.id.zr_rt_tableview_1);
		
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_MENU, Global.TOOLBAR_KLINE, 
				Global.TOOLBAR_BUYSALE, Global.TOOLBAR_MINGIX, 
				Global.TOOLBAR_F10, Global.TOOLBAR_REFRESH };
		
		initTitle(R.drawable.njzq_title_left_back, 0, "个股分时");
		initToolBar(toolbarname, Global.BAR_TAG);

		setTitleText(stockname + " (分时)");
		
		viewFlipper = (ViewFlipper) this.findViewById(R.id.trend_view_fliper);
    	preView = (ImageView) findViewById(R.id.previous_screen);

		leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in_layout);
		leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out_layout);
		rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in_layout);
		rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out_layout);
		
		csView = (CustomScrollView)findViewById(R.id.zr_htable_vscroll);
		csView.setGestureDetector(gestureDetector);
		csView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});

		trendView = (TrendView)findViewById(R.id.zrviewtrend);
		trendView.setStockInfo(exchange, stockcode, stockname);
		priceView = (PriceMini)findViewById(R.id.zrviewprice);
		financeView = (FinanceMini)findViewById(R.id.zrviewfinance);
		priceView.setStockInfo(exchange, stockcode, stockname, stocktype);
		financeView.setStockInfo(exchange, stockcode, stockname, type, stocktype);
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	btnRight.setBackgroundResource(R.drawable.njzq_title_right_select);
    	btnRight.setTag(3);
    	changeTitleBg();
    }
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
		if(financeView!=null) {
			financeView.reCycle();
		}
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onPause() {
		super.onPause();
		nLock = false;
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
		if(financeView!=null) {
			financeView.reCycle();
		}
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
		showProgress();
	}
	
	protected void initView(String exchange, String stockcode, String stockname) {
		CssStock css = new CssStock();
		css.setMarket(exchange);
		css.setExchange(NameRule.getMarket(exchange));
		css.setStkcode(stockcode);
		css.setStkname(stockname);
		RefreshTitle(css);
	}

	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
			FairyUI.switchToWnd(Global.QUOTE_KLINE, exchange, stockcode, stockname, mContext);
			break;
		case 2:
			FairyUI.switchToWnd(Global.QUOTE_BAOJIA, exchange, stockcode, stockname, mContext);
			break;
		case 3:
			FairyUI.switchToWnd(Global.QUOTE_MINGXI, exchange, stockcode, stockname, mContext);
			break;
		case 4:
			FairyUI.switchToWnd(Global.QUOTE_F10, exchange, stockcode, stockname, mContext);
			break;
		case 5:
			from = 0;
			showProgress();
			break;
		}
	}
	
	/**
	 * 向左滑动
	 */
	protected void moveColLeft() {
		if(viewFlipper.getDisplayedChild() == 0) {
	    	preView.setImageResource(R.drawable.page_arrow_12);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_13);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
	}
	
	/**
	 * 向右滑动
	 */
	protected void moveColRight() {
		if(viewFlipper.getDisplayedChild() == 2) {
	    	preView.setImageResource(R.drawable.page_arrow_12);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_11);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
	}
	
	protected void init(final int type) {
		mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				Log.i("@@@@@@@@@@@@@@@", ">>>>>>>>>>>>>>>>"+mLock);
				if(mLock&&nLock) {
					try {
						quoteData = ConnService.getTick("GET_TICK", exchange, stockcode, String.valueOf(from));
						if(Utils.isHttpStatus(quoteData)) {
							if(from==0) {
								json3 = quoteData;
							}
							else {
								JSONArray list = quoteData.getJSONArray("data");
								Log.i("@@@@@@@@@@length@@@@@@@@", list.length()+">>>>>>>");
								for(int i = 0; i<list.length(); i++) {
									if(json3.getString("quotetime").equals(list.getJSONArray(i).getString(3))) {
										Log.i("#################", from+">>>>>>>>>>>>>>>>");
										json3.getJSONArray("data").put(from-1, quoteData.getJSONArray("data").get(i));
									}
									else {
										json3.getJSONArray("data").put(quoteData.getJSONArray("data").get(i));
									}
								}
								if(("sh000001".equals(exchange + stockcode) || "sz399001".equals(exchange + stockcode))&&
										!quoteData.isNull("data2")) {
									JSONArray list2 = quoteData.getJSONArray("data2");
									Log.i("@@@@@@@@@@data2 length@@@@@@@@", list2.length()+">>>>>>>");
									for(int i = 0; i<list2.length(); i++) {
										if(json3.getString("quotetime").equals(list2.getJSONArray(i).getString(1))) {
											Log.i("########data2#########", from+">>>>>>>>>>>>>>>>" + json3.getJSONArray("data2").length());
											json3.getJSONArray("data2").put(from-1, quoteData.getJSONArray("data2").get(i));
										}
										else {
											json3.getJSONArray("data2").put(quoteData.getJSONArray("data2").get(i));
										}
									}
								}
								json3.remove(json3.getString("quotetime"));
								json3.put("quotetime", quoteData.getString("quotetime"));
							}
							from = json3.getJSONArray("data").length();
						}
						else {
							isNetworkError = -1;
							quoteData = null;
						}
						
						json = ConnService.getTimeShare("GET_TICK_DETAILNORMAL", exchange, stockcode, "10", "0");
						
						json2 = ConnService.getDish("GET_PRICE_VOLUMEJSON", exchange, stockcode, stocktype);
						if(Utils.isHttpStatus(json2)) {
							//isNetworkError = 0;
						}
						else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						isNetworkError = -2;
						e.printStackTrace();
					}
				}

				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.postDelayed(r, Config.fenshirefresh);
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
					if(Utils.isHttpStatus(json3)) {
						Log.i("z########json3#########", json3.getJSONArray("data").length()+">>>>>>>>>>");
						trendView.getQuoteData(json3);
						trendView.invalidate();
					}
					if(Utils.isHttpStatus(json)) {
						JSONArray jArr1 = (JSONArray)json.getJSONArray("data");
						double zrsp = json.getDouble("zrsp");
						appendRow2(jArr1, table_1, zrsp);
					}
					if(Utils.isHttpStatus(json2)) {
						priceView.initData(json2);
						priceView.invalidate();
						financeView.initData(json2);
		        		financeView.invalidate();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				hiddenProgress();
			}
		};
		runOnUiThread(r);
	}
	
	private void appendRow2(JSONArray jArr, TableLayout table, double zrsp) throws JSONException {
		table.setStretchAllColumns(true);
		table.removeAllViews();
		if ("cf".equals(exchange.toLowerCase()) || "dc".equals(exchange.toLowerCase()) || 
				"sf".equals(exchange.toLowerCase()) || "cz".equals(exchange.toLowerCase())){
				int len = jArr.length();
				int color = Utils.getTextColor(mContext, 0);
				for (int i = 0; i < len; i++) {
					JSONArray jA = (JSONArray)jArr.get(i);
					TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
		            TextView t1 = (TextView) row.findViewById(R.id.zr_table_col1);
		            TextView t2 = (TextView) row.findViewById(R.id.zr_table_col2);
		            TextView t3 = (TextView) row.findViewById(R.id.zr_table_col3);
		            TextView t4 = (TextView) row.findViewById(R.id.zr_table_col4);
		            TextView t5 = (TextView) row.findViewById(R.id.zr_table_col5);
		            t1.setText(jA.getString(4));
		            t2.setText(Utils.dataFormation(jA.getDouble(0), Utils.getStockDigit(type)));
		            t2.setTextColor(Utils.getTextColor(mContext, jA.getDouble(0), zrsp));
		            t2.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		            double cjsl = Arith.round(jA.getDouble(1), 0);
		            t3.setText(Utils.dataFormation(cjsl, 0));
		            t3.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		            t4.setText(jA.getString(7));
		            t5.setText(jA.getString(8));
		            table.addView(row);
		            if("B".equals(jA.getString(6))) {
		            	color = Utils.getTextColor(mContext, 3);
			            t3.setTextColor(color);
			            t4.setTextColor(color);
			            t5.setTextColor(color);
		            }
		            else if("S".equals(jA.getString(6))) {
		            	color = Utils.getTextColor(mContext, 4);
			            t3.setTextColor(color);
			            t4.setTextColor(color);
			            t5.setTextColor(color);
		            }
		            else {
			            t3.setTextColor(color);
			            t4.setTextColor(color);
			            t5.setTextColor(color);
		            }
				}
			} else {
			if(stocktype.charAt(0)=='0') {//普通股票
				for (int i = 0; i < jArr.length(); i++) {
					JSONArray jA = (JSONArray)jArr.get(i);
					TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
		            TextView t1 = (TextView) row.findViewById(R.id.zr_table_col1);
		            TextView t2 = (TextView) row.findViewById(R.id.zr_table_col2);
		            TextView t3 = (TextView) row.findViewById(R.id.zr_table_col3);
		            TextView t4 = (TextView) row.findViewById(R.id.zr_table_col4);
		            
		            t1.setText(jA.getString(4));
		            t2.setText(Utils.dataFormation(jA.getDouble(0), digit));
		            t2.setTextColor(Utils.getTextColor(mContext, jA.getDouble(0), zrsp));
		            t2.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		            double cjsl = Arith.round(jA.getDouble(1), 0);
		            t3.setText(Utils.dataFormation(cjsl, 0));
		            if(cjsl>500)
		            	t3.setTextColor(Utils.getTextColor(mContext, 6));
		            else
		            	t3.setTextColor(Utils.getTextColor(mContext, 1));
		            t3.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
		            t4.setText(jA.getString(6));
		            if("B".equals(jA.getString(6)))
		            	t4.setTextColor(Utils.getTextColor(mContext, 3));
		            else 
		            	t4.setTextColor(Utils.getTextColor(mContext, 4));
		            
		            table.addView(row);
				}
			}
			else if(stocktype.charAt(0)=='1'||stocktype.charAt(0)=='2') {//指数
				for (int i = 0; i < jArr.length(); i++) {
					JSONArray jA = (JSONArray)jArr.get(i);
					TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
		            TextView t1 = (TextView) row.findViewById(R.id.zr_table_col1);
		            TextView t2 = (TextView) row.findViewById(R.id.zr_table_col2);
		            TextView t3 = (TextView) row.findViewById(R.id.zr_table_col4);
		            
		            t1.setText(jA.getString(4));
		            t2.setText(Utils.dataFormation(jA.getDouble(0), digit));
		            t2.setTextColor(Utils.getTextColor(mContext, jA.getDouble(0), zrsp));
		            t3.setText(Utils.getAmountFormat(jA.getDouble(5), true));
		            t3.setTextColor(Utils.getTextColor(mContext, 1));
		            
		            table.addView(row);
				}
			}
		}
	}
}
