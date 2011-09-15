/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuoteDetail.java 下午04:47:51 2010-11-9
 */
package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.custom.CustomScrollView;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 行情明细
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class QuoteDetail extends FlipperActiviy  {
	private final String TAG = "QuoteDetail";
	private Context mContext = QuoteDetail.this;
	private TableLayout table_1;
	private String exchange;
	private String stockcode;
	private String stocktype;
	private int type;
	private int from = Global.DETAIL_DIS_NUMS;
	private int to = 0;
	private LinearLayout loadingLayout ;
	private CustomScrollView customScrollView ;
	private boolean nLock = true;
	private boolean firstComing = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		setContentView(R.layout.zr_quote_price);
		initTitle(R.drawable.njzq_title_left_back, 0, "");
		Bundle bundle = getIntent().getExtras();
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
		this.stocktype = bundle.getString("stocktype");
		this.type = NameRule.getSecurityType(exchange, stockcode);
		if(stocktype==null||stocktype.equals(""))
			stocktype = NameRule.getStockType(type);
		table_1 = (TableLayout) findViewById(R.id.zr_rt_tableview_1);
		setTitleText(getResources().getString(R.string.cjmx_title));
		customScrollView = (CustomScrollView) findViewById(R.id.zr_htable_vscroll);
		customScrollView.setOnTouchListener(this);
		customScrollView.setGestureDetector(gestureDetector);
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
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				if(mLock&&nLock&&type==1) {
					quoteData = ConnService.getTimeShare("GET_TICK_DETAILNORMAL", exchange, stockcode, String.valueOf(from), String.valueOf(to));
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
				}
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.postDelayed(r, Config.fenshirefresh);
			}
		};
		mHandler.post(r);
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
        showProgress();
	}
	
    @Override
    protected void onStop() {
        super.onStop();
    }
	
    /**
     * 更新UI界面
     */
	protected void handlerData() {
		Runnable r = new Runnable() {
			public void run() {
				try {
					if(isNetworkError<0&&firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					String res = TradeUtil.checkResult(quoteData);
					if(res!=null) {
						table_1.removeView(loadingLayout);    //移除顶部加载
					}
					else {
						String[] temp = new String[4];
						temp[0] = "time";
						temp[1] = "zjcj";
						temp[2] = "cjsl";
						temp[3] = "bsflag";
						JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
						double zrsp = quoteData.getDouble("zrsp");
						appendRow(jArr, table_1, zrsp);
						//进度条消失
						table_1.removeView(loadingLayout);      //移除顶部加载
					}
				} catch (JSONException e) {
					Log.e(TAG, e.toString());
				}
				hiddenProgress();
			}
		};
		runOnUiThread(r);
	}
	/**
	 * 填充表格
	 * @param jArr
	 * @param table
	 * @param zrsp
	 * @throws JSONException
	 */
	private void appendRow(JSONArray jArr, TableLayout table, double zrsp) throws JSONException {
		table.setStretchAllColumns(true);
		table.removeAllViews();
		
		if ("cf".equals(exchange.toLowerCase()) || "dc".equals(exchange.toLowerCase()) || 
			"sf".equals(exchange.toLowerCase()) || "cz".equals(exchange.toLowerCase())){
			int len = jArr.length();
			int color = Utils.getTextColor(mContext, 0);
			for (int i = 0; i < len; i++) {
				LinearLayout linearLayout = (LinearLayout) findViewById(R.id.title23);
				linearLayout.setVisibility(View.VISIBLE);
				
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
		}else {
			if(stocktype.charAt(0)=='0') {//普通股票
				//for (int i = jArr.length()-1; i >= 0; i--) {
				for (int i = 0; i <= jArr.length()-1; i++) {
					JSONArray jA = (JSONArray)jArr.get(i);
					TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
		            TextView t1 = (TextView) row.findViewById(R.id.zr_table_col1);
		            TextView t2 = (TextView) row.findViewById(R.id.zr_table_col2);
		            TextView t3 = (TextView) row.findViewById(R.id.zr_table_col3);
		            TextView t4 = (TextView) row.findViewById(R.id.zr_table_col4);
		            t1.setText(jA.getString(4));
		            t2.setText(Utils.dataFormation(jA.getDouble(0), Utils.getStockDigit(type)));
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
				//未填满，初始化填满
				int num  = jArr.length() ;
				if (num <16){
					while ( num <16){
						TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
						table.addView(row);
						num ++ ;
					}
				}
			}
			else if(stocktype.charAt(0)=='1'||stocktype.charAt(0)=='2') {//指数
				//for (int i = jArr.length()-1; i >= 0; i--) {
				for (int i = 0; i <= jArr.length()-1; i++) {
					JSONArray jA = (JSONArray)jArr.get(i);
					TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
		            TextView t1 = (TextView) row.findViewById(R.id.zr_table_col1);
		            TextView t2 = (TextView) row.findViewById(R.id.zr_table_col2);
		            TextView t3 = (TextView) row.findViewById(R.id.zr_table_col4);
		            t1.setText(jA.getString(4));
		            t2.setText(Utils.dataFormation(jA.getDouble(0), Utils.getStockDigit(type)));
		            t2.setTextColor(Utils.getTextColor(mContext, jA.getDouble(0), zrsp));
		            t3.setText(Utils.getAmountFormat(jA.getDouble(5), true));
		            t3.setTextColor(Utils.getTextColor(mContext, 1));
		            table.addView(row);
				}
				//未填满，初始化填满
				int num  = jArr.length() ;
				if (num <16){
					while ( num <16){
						TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_quote_price_item,null);
						table.addView(row);
						num ++ ;
					}
				}
			}
		}
		
	}

	
	/**
	 * 手指从上而下滑动
	 */
	protected void moveColBottom() {
		if(customScrollView.getScrollY() == 0) {    	//顶端
			table_1.removeView(loadingLayout);      //移除顶部加载
			initLoading();		
			init(1);
		} 
		
	}
	/**
	 * 初始化提示加载标题
	 */
	private void initLoading(){
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.HORIZONTAL);  
		ProgressBar progressBar = new ProgressBar(this);  
		progressBar.setPadding(0, 0, 15, 0);  
		LayoutParams layoutParams =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);  
		LayoutParams layoutParams2 =new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT); 
		layout.addView(progressBar, layoutParams);  
		TextView textView = new TextView(this);  
        textView.setText(getResources().getText(R.string.cjmx_loading));  
        textView.setGravity(Gravity.CENTER_VERTICAL); 
        layout.addView(textView, layoutParams2);  
        layout.setGravity(Gravity.CENTER); 
        loadingLayout = new LinearLayout(this);  
        loadingLayout.addView(layout, layoutParams);  
        loadingLayout.setGravity(Gravity.CENTER);  
        table_1.addView(loadingLayout,0);
	}
	
}
