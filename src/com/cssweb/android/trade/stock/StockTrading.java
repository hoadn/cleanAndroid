/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)StockTrading.java 下午11:49:54 2010-8-21
 */
package com.cssweb.android.trade.stock;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.custom.ShadowButton;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.view.FinanceMini;
import com.cssweb.android.view.KlineMini;
import com.cssweb.android.view.PriceMini;
import com.cssweb.android.view.TrendView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 股票交易
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class StockTrading extends FlipperActiviy {
	private final String TAG = "StockTrading";
	
	private Spinner stockHolder;
	private ArrayAdapter<String> stockHolderAdapter;
	private Spinner queryMethod;
	private ArrayAdapter<String> queryMethodAdapter;
	
	private ShadowButton btn0;
	
	private EditText stkcode;
	private TextView stkname;
	private EditText price;
	private EditText number;
//	private SeekBar availableNum;
//	private TextView lblMaxNumber;
	
	private ImageView adjustDownPrice;
	private ImageView adjustUpPrice;
	private ImageView adjustDownNumber;
	private ImageView adjustUpNumber;
	private LinearLayout layoutprice0;
	private LinearLayout layoutprice;
	
	//private LinearLayout layoutAvaiNumber;
	//private LinearLayout layoutAvaiAsset;
	private TextView lblAvaiAsset;
	private TextView AvaiAsset;
	private TextView lblPrice;
	private TextView lblPrice1;
	private TextView lblNumber;
	private TextView avaiAsset;
	private TextView lblNumberUnit;
	
	// 声明进度条对话框
	private ProgressDialog myDialog = null;
	
	private List<String> holder;
	private String market;
	private int type;
	private String bsname;
	private String[] quoteMethodVal;
	
	private String exchange;
	private String stockcode;
	private String stockname;
	private String stocktype;
	private JSONObject jsonData;
//	private String zjcj;
	
	private ViewFlipper viewFlipper;
	private ImageView preView;

	private PriceDataHandler priceHandler;
	//避免退出时行情还在刷新
	private boolean nLock = true;
	//根据此类别判断属于何种证券
	private int zqtype = 0;
	private TrendView trendView = null;
	private PriceMini priceView = null;
	private KlineMini klineView = null;
	private FinanceMini financeView = null;
	
	private JSONObject jsonTick;//分时线数据
	private JSONObject jsonKline;//分时线数据
	private int tickFrom = 0;//取分时线数据起始位置
	
	private Animation leftIn;
	private Animation leftOut;
	private Animation rightIn;
	private Animation rightOut;
	
	private int priceFormatNum = 2;//小数位数
	private ServiceControl sc = null;//行为分析
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		sc = new ServiceControl(this);//行为分析初始化
		//行情刷新专用
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		priceHandler = new PriceDataHandler(mHandlerThread.getLooper());

		setContentView(com.cssweb.android.main.R.layout.zr_trade_stock_trade);
		Bundle bundle = getIntent().getExtras();
		type = bundle.getInt("type");
		bsname = bundle.getString("bsname");
		String stockCode = bundle.getString("stkcode");
		
		initTitle(R.drawable.njzq_title_left_back, 0, bsname);
		
		LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.zrtradelayout);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
	    localView2.setOnClickListener(setOnEditClickListener);
		
		viewFlipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
    	preView = (ImageView) findViewById(R.id.previous_screen);
		
		leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in_layout);
		leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out_layout);
		rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in_layout);
		rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out_layout);
		
		priceView = (PriceMini)findViewById(R.id.zrviewprice);
		trendView = (TrendView)findViewById(R.id.zrviewtrend);
		trendView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		klineView = (KlineMini)findViewById(R.id.zrviewkline);
		klineView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		financeView = (FinanceMini)findViewById(R.id.zrviewfinance);
		
		stockHolder = (Spinner) findViewById(R.id.zrtxtaccount);
		queryMethod = (Spinner) findViewById(R.id.zrtxtbsflag);
//		availableNum = (SeekBar) findViewById(R.id.zrcanbs);
//		lblMaxNumber = (TextView)findViewById(R.id.txtMaxNumber);
		stkcode = (EditText) findViewById(R.id.zredtstockcode);
		stkname = (TextView) findViewById(R.id.zrtxtstockname);
		price = (EditText) findViewById(R.id.zredtprice);
		number = (EditText) findViewById(R.id.zredtcount);
		lblNumberUnit = (TextView)findViewById(R.id.lblNumberUnit);
		btn0 = (ShadowButton) findViewById(R.id.zr_surebutton);
		btn0.setOnClickListener(myShowProgreeBar);
		
		layoutprice0 = (LinearLayout)findViewById(R.id.LinearLayout03);
		layoutprice = (LinearLayout)findViewById(R.id.LinearLayout11);
		//layoutAvaiNumber = (LinearLayout)findViewById(R.id.LinearLayout04);
		//layoutAvaiAsset = (LinearLayout)findViewById(R.id.LinearLayout06);
		lblAvaiAsset = (TextView)findViewById(R.id.lblAvaiAsset);
		AvaiAsset = (TextView)findViewById(R.id.AvaiAsset);
		lblPrice = (TextView)findViewById(R.id.lblPrice);
		lblPrice1 = (TextView)findViewById(R.id.lblPrice1);
		lblNumber = (TextView)findViewById(R.id.lblNumber);
		avaiAsset = (TextView)findViewById(R.id.AvaiAsset);
		lblNumberUnit = (TextView)findViewById(R.id.lblNumberUnit);
		
		setDynamic();
		
		adjustDownPrice = (ImageView)findViewById(R.id.AdjustDownPrice);
		adjustDownPrice.setTag(0);
		adjustDownPrice.setOnClickListener(adjustIconListener);
		
		adjustUpPrice = (ImageView)findViewById(R.id.AdjustUpPrice);
		adjustUpPrice.setTag(1);
		adjustUpPrice.setOnClickListener(adjustIconListener);
		
		adjustDownNumber = (ImageView)findViewById(R.id.AdjustDownNumber);
		adjustDownNumber.setTag(2);
		adjustDownNumber.setOnClickListener(adjustIconListener);
		
		adjustUpNumber = (ImageView)findViewById(R.id.AdjustUpNumber);
		adjustUpNumber.setTag(3);
		adjustUpNumber.setOnClickListener(adjustIconListener);
		
		stkcode.setText(stockCode);
		if(stkcode.getText().length()==6) {
			showProgress();
		}

		stkcode.setInputType(InputType.TYPE_NULL);
		stkcode.setFocusable(true);
		stkcode.setTag("STOCK");
		stkcode.setOnClickListener(setOnEditClickListener);
		stkcode.setOnFocusChangeListener(setOnEditFocusListener);
		
		price.setInputType(InputType.TYPE_NULL);
		price.setFocusable(true);
		price.setTag("NUMDOT");
		price.setOnClickListener(setOnEditClickListener);
		price.setOnFocusChangeListener(setOnEditFocusListener);

		number.setInputType(InputType.TYPE_NULL);
		number.setFocusable(true);
		number.setTag("");
		number.setOnClickListener(setOnEditClickListener);
		number.setOnFocusChangeListener(setOnEditFocusListener);

		stkcode.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable arg0) {
				//queryStock(queryCode.getText());
				OnTextChanged(arg0);
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
				
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				
			}
			
		});
		
		holder = TradeUser.getInstance().getHolder();
		
		stockHolderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String [])holder.toArray(new String[holder.size()]));
		stockHolderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		stockHolder.setAdapter(stockHolderAdapter);
		
		String[] arrayOfString = getResources().getStringArray(R.array.other_market_bs_methods);
		for (int i = 0; i < arrayOfString.length; i++) {
			Log.e("arrayOfString", arrayOfString[i]);
		}
		
		
		queryMethodAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arrayOfString);
		queryMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		queryMethod.setAdapter(queryMethodAdapter);
		queryMethod.setOnItemSelectedListener(new OnItemSelectedListener (){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position != 0){
					layoutprice0.setVisibility(View.GONE);
					layoutprice.setVisibility(View.VISIBLE);
				}else {
					layoutprice0.setVisibility(View.VISIBLE);
					layoutprice.setVisibility(View.GONE);
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
//		Log.e("<<<<<<<<<<<<<<<<<<<eeeeeeeeeeeeeeeeeeeeeeeeeee>>>>>>>>>>>>>>", String.valueOf(spinnerheight));
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) stockHolder.getLayoutParams();//获取stockHolder控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		stockHolder.setLayoutParams(linearParams1);
		
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) queryMethod.getLayoutParams();//获取queryMethod控件的当前布局
		linearParams.height=spinnerheight;//对该控件的布局参数做修改
		queryMethod.setLayoutParams(linearParams);
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	private void setDynamic(){
		if(type == 0){
			//layoutAvaiAsset.setVisibility(0);
			sc.currentSite("wtjy_mr");
			lblAvaiAsset.setVisibility(0);
			AvaiAsset.setVisibility(0);
			lblPrice.setText("买入价格");
			lblPrice1.setText("买入价格");
			lblNumber.setText("买入数量");
		}else if(type == 1){
			//layoutAvaiNumber.setVisibility(0);
			sc.currentSite("wtjy_mc");
			lblPrice.setText("卖出价格");
			lblPrice1.setText("卖出价格");
			lblNumber.setText("卖出数量");
		}
	}
	
	@Override
	public void adjustDownOrUp(Object tag) {
		Double temp = 0.01;
		if(priceFormatNum == 3)
			temp = 0.001;
		String str = "";
		switch ((Integer)tag) {
		case 0:
			str = price.getText().toString().trim();
			if(str.length() == 0){
				price.setText("0.00");
				if(priceFormatNum == 3)
					price.setText("0.000");
			}else if(str.length() > 0){
				if (! isDigit(str)){
					
				}else{
					Double amount = Double.valueOf(str);
					if(amount == 0.00 || amount == 0.000 || amount == 0){
						break;
					}
					if(amount == 0.01 || amount == 0.001 || amount == 0){
						price.setText("0.00");
						if(priceFormatNum == 3)
							price.setText("0.000");
					}else
						price.setText( String.valueOf(Arith.round(amount - temp, priceFormatNum)) );
				}
				
			}
			break;
		case 1:
			str = price.getText().toString().trim();
			if(str.length() == 0){
				price.setText("0.01");
				if(priceFormatNum == 3)
					price.setText("0.001");
			}else if(str.length() > 0){
				if (! isDigit(str)){
					
				}else{
					Double amount = Double.valueOf(str);
					price.setText( String.valueOf(Arith.round(amount + temp, priceFormatNum)) );
				}
				
			}
			break;
		case 2:
			str = number.getText().toString().trim();
			if(str.length() == 0){
				number.setText("100");
			}else if(str.length() > 0){
				if (! isDigit(str)){
					
				}else{
					Integer amount = Integer.valueOf(str);
					if (amount == 100) {
						break;
					}
					if(amount == 0){
						number.setText("100");
					}else if(amount < 100){
						number.setText("100");
					}else if(amount%100 == 0){
						number.setText(String.valueOf(amount-100));
					}else {
						Integer res = amount/100;
						number.setText(String.valueOf(res*100));
					}
				}
				
			}
			break;
		case 3:
			str = number.getText().toString().trim();
			if(str.length() == 0){
				number.setText("100");
			}else if(str.length() > 0){
				if (! isDigit(str)){
					
				}else {
					Integer amount = Integer.valueOf(str);
					if(amount < 100){
						number.setText("100");
					}else if(amount%100 == 0){ 
						number.setText(String.valueOf(amount+100));
					}else{
						Integer res = amount/100;
						number.setText(String.valueOf((res+1)*100));
					}
				}
				
			}
			break;
		}
	}
	
	private void buystock() {
		String gddm = stockHolder.getSelectedItem().toString();
		String zqdm = stkcode.getText().toString();
		String qty = number.getText().toString();
		String jg = price.getText().toString();
		
		if(zqdm==null||zqdm.equals("")) {
			toast("请输入证券代码!");
			return;
		}else if (zqdm.length()!= 6||!TradeUtil.checkNumber(zqdm, false)) {
			toast("请输入正确的证券代码!");
			return;
		}
		if(jg==null||jg.equals("")) {
			toast("请输入委托价格!");
			return;
		}else if(!TradeUtil.checkNumber(jg, false)){
			toast("请输入正确的委托价格!");
			return;
		}
		if(qty==null||qty.equals("")) {
			toast("请输入委托数量!");
			return;
		}else if(!TradeUtil.checkNumber(qty, true)){
			toast("请输入正确的委托数量!");
			return;
		}
	    String flag = "";
		if(type == 0)
			flag = "买入确认";
		else if(type == 1)
			flag = "卖出确认";
		String mess = "股票代码:" + zqdm + "\n";
		      mess += "股票名称:" + stkname.getText().toString() + "\n";
		      if (queryMethod.getSelectedItemPosition() == 0) {
		    	  mess += "委托价格:" + jg + "\n";
			}
			  mess += "委托数量:" + qty + "\n";
			  mess += "委托方式:" + queryMethod.getSelectedItem().toString() + "\n";
			  mess += "股东代码:" + gddm + "\n";
			  //mess += "委托方向:" + TradeUtil.getTradeName(quoteMethodVal[queryMethod.getSelectedItemPosition()].charAt(0)) + "\n";
			  new AlertDialog.Builder(StockTrading.this)
				.setTitle(flag)
				.setMessage(mess)
				.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CharSequence title = "正在与服务器通讯握手...";
						CharSequence message = "正在往服务器提交数据...";
						myDialog = ProgressDialog.show(StockTrading.this, title, message, true);
					
					StringBuffer sb = new StringBuffer();
		    		
		    		sb.append("FID_JYS=" + market+ TradeUtil.SPLIT);
		    		sb.append("FID_GDH=" + holder.get(stockHolder.getSelectedItemPosition()) + TradeUtil.SPLIT);
		    		sb.append("FID_ZQDM=" + stkcode.getText() + TradeUtil.SPLIT);
		    		if(queryMethod.getSelectedItemPosition()==0){
		    			sb.append("FID_WTJG=" + price.getText() + TradeUtil.SPLIT);
		    		}else {
		    			sb.append("FID_WTJG=0.0"+ TradeUtil.SPLIT);
					}
		    		if(type == 0)
		    		{
		    			sb.append("FID_JYLB=" + 1 + TradeUtil.SPLIT);//表示买入		    		
		    		}else if(type == 1){
		    			sb.append("FID_JYLB=" + 2 + TradeUtil.SPLIT);//表示卖出
		    		}
					sb.append("FID_WTSL=" + number.getText().toString() + TradeUtil.SPLIT);
					String orderType = "";
					if (queryMethod.getSelectedItemPosition() != 0) {
						orderType = quoteMethodVal[queryMethod.getSelectedItemPosition()];
					}
					sb.append("FID_DDLX=" + orderType + TradeUtil.SPLIT);
					sb.append("FID_WTPCH=" + TradeUtil.SPLIT);

					try {
						JSONObject quoteData = ConnPool.sendReq("BUY_STOCK", "204501", sb.toString());
			    		String res = TradeUtil.checkResult(quoteData);
			    		if(res!=null) {
			    			if (res.equals("-1")){
								Toast.makeText(StockTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								//openDialog(R.string.network_error);
							}else {
								Toast.makeText(StockTrading.this, res, Toast.LENGTH_LONG).show();
							}
			    		}else {
				    		JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
				    		JSONObject j = (JSONObject)jArr.get(0);
							toast("委托成功，委托号：" + j.getString("FID_WTH"));
			    		}
			    		clear();
					} catch (JSONException e) {
						e.printStackTrace();
						CssLog.e(TAG, e.toString());
					}
					
					myDialog.dismiss();
				}
		})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//
					}
				})
		.show();
	}
	
	Button.OnClickListener myShowProgreeBar =
    	new Button.OnClickListener() {
    	public void onClick(View arg0) {
    		buystock();
		}
    };
	
    private void clear(){
    	stkcode.setText("");
    	stkname.setText("");
    	number.setText("");
    	price.setText("");
    	queryMethod.setAdapter(queryMethodAdapter);
    	stockHolder.setAdapter(stockHolderAdapter);
    	
    }
    
	protected void init(final int type) {
		new Thread() {
			public void run() {
				try {
					quoteData = ConnService.getTradeHQ(stkcode.getText().toString().trim());
					//Log.e("<<<<<<<<<<行情>>>>>>>>>>>>>",quoteData.toString());
					mHandler.sendEmptyMessage(0);
				} catch (Exception e) {
					e.printStackTrace();
					CssLog.e(TAG, e.toString());
				}
			}
		}.start();
	}
	
	protected void handlerData() {			
    	try {
    		String res = TradeUtil.checkResult(quoteData);
    		if (res != null) {
    			if (res.equals("-1")){
    				toast("网络连接错误，请重试或者检查您的网络设置！");
    			}else if ("stock not found".equals(res)) {
    				toast("证券代码不存在！");
				}else{
    				toast(res);
    			}
    			clear();
    			hiddenProgress();
    			return;
    		}
    		
    		JSONArray jarr = quoteData.getJSONArray("item");
    		JSONObject jA = (JSONObject)jarr.get(0);
    		if(jA == null){
    			return;
    		}
    		
			stkname.setText(jA.getString("zqjc"));
//			zjcj = jA.getString("zjcj");
			String zqlb = jA.getString("zqlb");
			zqlb = zqlb.substring(0,zqlb.indexOf("."));
			lblNumberUnit.setText(NameRule.getStockUnit(zqlb));
			priceFormatNum = NameRule.getStockFormatNum(zqlb);
			
			market = NameRule.getMarketFromZqlb(jA.getString("zqlb"),jA.getString("zqdm"));
			
			String[] arrayOfString = null;
			
			this.exchange = NameRule.getExchange(String.valueOf(market));
			this.stockcode = jA.getString("zqdm");
			this.stockname = jA.getString("zqjc");
			JSONObject stockAccountData = null;
			JSONArray jsonArr = null;
			String filedate = ActivityUtil.getPreference(StockTrading.this,"openholdersListDate", "");
			if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
				stockAccountData = ConnPool.sendReq("GET_STOCKACCOUNT", "304001","");
			}else{
				String jsonObject = CssIniFile.loadIni(StockTrading.this, 9, "ShareholdersList");
				if(null !=jsonObject && ! jsonObject.equals("")){
					stockAccountData = new JSONObject(jsonObject);
				}
			}
			res = TradeUtil.checkResult(stockAccountData);
			if(res==null){
				jsonArr = (JSONArray)stockAccountData.getJSONArray("item");
			}
			//切换股东账号
			for(int i=0,size=jsonArr.length()-1; i<size; i++){
				JSONObject jsonObj = (JSONObject)jsonArr.get(i);
				if (jsonObj.getString("FID_JYS").equals(market)) {
					stockHolder.setSelection(i);
				}
			}
			if (TradeUtil.MARKET_SHB.equals(market)||TradeUtil.MARKET_SZB.equals(market)) {
				arrayOfString = getResources().getStringArray(R.array.other_market_bs_methods);
				quoteMethodVal = getResources().getStringArray(R.array.other_market_bs_methods_val);
			}else if (TradeUtil.MARKET_SHA.equals(market)) {
				arrayOfString = getResources().getStringArray(R.array.sh_market_bs_methods);
				quoteMethodVal = getResources().getStringArray(R.array.sh_market_buy_methods_val);
			}else if (TradeUtil.MARKET_SZA.equals(market)) {
				arrayOfString = getResources().getStringArray(R.array.sz_market_bs_methods);
				quoteMethodVal = getResources().getStringArray(R.array.sz_market_bs_methods_val);
			}
			queryMethodAdapter = new ArrayAdapter<String>(StockTrading.this, android.R.layout.simple_spinner_item, arrayOfString);
			queryMethodAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			queryMethod.setAdapter(queryMethodAdapter);
			if(type==1) {//卖出
				if(!"0.0".equals(jA.getString("bjw1"))){
					price.setText(TradeUtil.formatNum(jA.getString("bjw1"), priceFormatNum));
				}else if(!"0.0".equals(jA.getString("zjcj"))){
					price.setText(TradeUtil.formatNum(jA.getString("zjcj"), priceFormatNum));
				}else {
					price.setText(TradeUtil.formatNum(jA.getString("zrsp"), priceFormatNum));
				}
				number.setText("0");
				
	    		StringBuffer sb = new StringBuffer();
	    		sb.append("FID_GDH=" + holder.get(stockHolder.getSelectedItemPosition()) + TradeUtil.SPLIT);
	    		sb.append("FID_ZQDM=" + stkcode.getText().toString().trim() + TradeUtil.SPLIT);
	    		sb.append("FID_EXFLG=1" + TradeUtil.SPLIT);
	    		JSONObject tradeData = ConnPool.sendReq("GET_STOCK_POSITION", "304101", sb.toString());
	    		res = TradeUtil.checkResult(tradeData);
	    		if(res!=null) {
	    			if (res.equals("-1")){
						Toast.makeText(StockTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
						//openDialog(R.string.network_error);
					}else {
						Toast.makeText(StockTrading.this, res, Toast.LENGTH_LONG).show();
					}
	    			hiddenProgress();
	    			return;
	    		}else{
	    			JSONArray jArr = (JSONArray)tradeData.getJSONArray("item");
		    		if( jArr.length() > 0)
		    		{
		    			JSONObject j = (JSONObject)jArr.get(0);
		    			number.setText(j.getString("FID_KMCSL"));
		    		}
	    		}
			}else {
				if("1".equals(jA.getString("xgsg")) && !"0.0".equals(jA.getString("zrsp"))){
					price.setText(TradeUtil.formatNum(jA.getString("zrsp"), priceFormatNum));
				}else if(!"0.0".equals(jA.getString("sjw1"))){
					price.setText(TradeUtil.formatNum(jA.getString("sjw1"), priceFormatNum));
				}else if(!"0.0".equals(jA.getString("zjcj"))) {
					price.setText(TradeUtil.formatNum(jA.getString("zjcj"), priceFormatNum));
				}else {
					price.setText(TradeUtil.formatNum(jA.getString("zrsp"), priceFormatNum));
				}
				JSONObject maxQtyJson = null;
				String orderType = "";
				//查询最大可买
				String maxQty = "0";
				if (queryMethod.getSelectedItemPosition() != 0) {
					orderType = quoteMethodVal[queryMethod.getSelectedItemPosition()];
				}
				maxQtyJson = TradeService.getMaxQtyBuy(market, holder.get(stockHolder.getSelectedItemPosition()), stkcode.getText().toString().trim(), "1", price.getText().toString().trim(),orderType);
				String resmaxQtyJson = TradeUtil.checkResult(maxQtyJson);
				if (resmaxQtyJson != null) {
					if (resmaxQtyJson.equals("-1")){
						Toast.makeText(StockTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
					}else {
						toast(resmaxQtyJson);
					}
					number.setText("0");
					hiddenProgress();
					return;
				}
				JSONArray maxQtyjArr = maxQtyJson.getJSONArray("item");
				if( maxQtyjArr.length() > 0){
					JSONObject maxQtyjA = (JSONObject)maxQtyjArr.get(0);
					maxQty = maxQtyjA.getString("FID_WTSL");
				}
				number.setText(maxQty);
				//展示用户的可用资金
				TradeUtil.getFundavl();
				if(TradeUtil.MARKET_SHB.equals(market) || TradeUtil.MARKET_TU.equals(market))
					avaiAsset.setText(TradeUser.getInstance().getEnablefundavlUS());
				else if(TradeUtil.MARKET_SZB.equals(market))
					avaiAsset.setText(TradeUser.getInstance().getEnablefundavlHK());
				else 
					avaiAsset.setText(TradeUser.getInstance().getEnablefundavlRMB());
			}
			
			//切换股东账号信息			
		
			List<String> list = TradeUser.getInstance().getHolderBak();
			
			for(int i=0; i<list.size();i++)
			{
				String st = (String)list.get(i);
				String key = st.split("-")[0];
				if(key.equals(market))
				{
					stockHolder.setSelection(i);
					break;
				}
			}
    		//initFivePrive(jA);

			initView(this.exchange, this.stockcode, this.stockname);
		} catch (JSONException e) {
			e.printStackTrace();
			CssLog.e(TAG, e.toString());
		}
		//hiddenProgress();
	}
	
	private void OnTextChanged(Editable paramEditable) {
		Editable localEditable = this.stkcode.getText();
		if(localEditable == paramEditable) {
			String str = this.stkcode.getText().toString().trim();
			if(str.length()==6) {
				onHideKeyBoard();
				showProgress();
			}
		}
	}
	
	protected void initView(String exchange, String stockcode, String stockname) {
		this.jsonData = null;
		this.jsonTick = null;//分时线数据
		this.jsonKline = null;//分时线数据
		this.tickFrom = 0;//取分时线数据起始位置
		
		trendView.setStockInfo(exchange, stockcode, stockname);
		trendView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		
		klineView.setStockInfo(exchange, stockcode, stockname);
		klineView.setPeriod("day");
		klineView.setIndicatorType("volume");
		klineView.setMainIndicatorType("ma");
		//切换指标的时候重置状态
		klineView.resetStatus();

		zqtype = NameRule.getSecurityType(exchange, stockcode);
		stocktype = NameRule.getStockType(type);
		
		priceView.setStockInfo(exchange, stockcode, stockname, stocktype);
		financeView.setStockInfo(exchange, stockcode, stockname, zqtype, stocktype);
		
		init(exchange, stockcode, stockname);
	}
	
	/**
	 * 向左滑动
	 */
	protected void moveColLeft() {
		if(trendView.isTrackStatus()||klineView.isTrackStatus())
			return;
		if(viewFlipper.getDisplayedChild() == 0) {
	    	preView.setImageResource(R.drawable.page_arrow_22);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_23);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
		else if(viewFlipper.getDisplayedChild() == 2) {
	    	preView.setImageResource(R.drawable.page_arrow_24);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
	}
	
	/**
	 * 向右滑动
	 */
	protected void moveColRight() {
		if(trendView.isTrackStatus()||klineView.isTrackStatus())
			return;
		if(viewFlipper.getDisplayedChild() == 3) {
	    	preView.setImageResource(R.drawable.page_arrow_23);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
		else if(viewFlipper.getDisplayedChild() == 2) {
	    	preView.setImageResource(R.drawable.page_arrow_22);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_21);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		priceHandler.removeCallbacks(pricerunable);
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(klineView!=null) {
			klineView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		nLock = false;
		priceHandler.removeCallbacks(pricerunable);
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(klineView!=null) {
			klineView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
	}
	
	@Override
	protected void onResume() {
		//initView("", "", "");
		super.onResume();
		nLock = true;
		initPopupWindow();
	}
	
	protected void RefreshUI() {
		init(1);
	}
	private boolean isDigit(String str){
		if ( null == str || str.equals("") || str.startsWith(".")){
			return false;
		}else {
			return true;
		}
	}
	
    private Runnable pricerunable;
    private int msg = -1;
	
	private class PriceDataHandler extends Handler {

        public PriceDataHandler(Looper looper) {
			super(looper);
		}

		@Override
        public void handleMessage(Message msg) {
        	refreshData(msg.what);
        }
    }
	
	protected void refreshData(final int type) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					switch(type) {
			        	case 0:
			        		Log.i("###############", jsonTick+">>>>>>>>>>>");
			        		if(Utils.isHttpStatus(jsonTick)) {
								tickFrom = jsonTick.getJSONArray("data").length();
				    			trendView.getQuoteData(jsonTick);
				        		trendView.invalidate();
							}
			        		break;
			        	case 1:
			        		klineView.invalidate();
			        		break;
			        	case 2:
			        		priceView.invalidate();
			        		financeView.invalidate();
			        		break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	hiddenProgress();
			}
		};
		runOnUiThread(r);
	}
	
	public void init(final String exchange, final String stockcode, final String stockname) {
		//打开加载进度对话框
		//openProgress();
		priceHandler.removeCallbacks(pricerunable);
		pricerunable = new Runnable() {
			public void run() {
				priceHandler.removeMessages(msg);
				if(nLock) {
					try {
						Log.i("#########加载FLIPPER##########", ">>>>>>>>>>>>>"+viewFlipper.getDisplayedChild());
						switch(viewFlipper.getDisplayedChild()) {
						case 0:
							/**加载FLIPPER分时图需要的数据**/
							JSONObject j = ConnService.getTick("GET_TICK", exchange, stockcode, String.valueOf(tickFrom));
							if(Utils.isHttpStatus(j)) {
								if(tickFrom==0) {
									jsonTick = j;
								}
								else {
									JSONArray list = j.getJSONArray("data");
									for(int i = 0; i<list.length(); i++) {
										if(!jsonTick.getString("quotetime").equals(list.getJSONArray(i).getString(3))) {
											jsonTick.getJSONArray("data").put(j.getJSONArray("data").get(i));
										}
									}
									jsonTick.remove(jsonTick.getString("quotetime"));
									jsonTick.put("quotetime", j.getString("quotetime"));
								}
								msg = 0;
							}
							else {
								msg = -1;
							}
							break;
						case 1:
							/**加载FLIPPERK线图需要的数据**/
							jsonKline = ConnService.getKlineData(StockTrading.this, exchange, stockcode, "day", "ma", "volume");
							if(jsonKline!=null) {
				    			klineView.initData(jsonKline);
				    			msg = 1;
							}
							else {
								msg = -1;
							}
							break;
						default:
							/**加载FLIPPER后两屏需要的数据**/
							jsonData = ConnService.getDish("GET_PRICE_VOLUMEJSON", exchange, stockcode, stocktype);
							if(Utils.isHttpStatus(jsonData)) {
				    			priceView.initData(jsonData);
				    			financeView.initData(jsonData);
				    			msg = 2;
							}
							else {
								msg = -1;
							}
							break;
						}
						
						
					} catch (JSONException e) {
						e.printStackTrace();
						msg = -1;
					}
				}
                //通过Handler发布携带消息
				priceHandler.sendEmptyMessageDelayed(msg, 50);
				priceHandler.postDelayed(pricerunable, Config.fenshirefresh);
			}
		};
		priceHandler.post(pricerunable);
	}
}
