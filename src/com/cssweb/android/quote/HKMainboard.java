package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.os.HandlerThread;
import android.view.View;

import com.cssweb.android.base.QuoteFundGridActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;
/**
 * 香港主板、香港创业板
 * @author hoho
 *
 */
public class HKMainboard extends QuoteFundGridActivity{
	private int pageNum = 10;
	private RequestParams requestParams;
	private String [] cols ;
	private Thread thread = null;
	private String stocks =null ;
	private String stocksname =null ;
	private int stocktype=-1, grid = 0;//默认为沪深A股
	private int allStockNums = 0;
	private String[] menu;
	@SuppressWarnings("unused")
	private String title1;
	private int flag ;
	private boolean nLock = true;
	private boolean firstComing = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		setContentView(R.layout.zr_hk_table);
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_MENU, 
				Global.TOOLBAR_PAIXU, Global.TOOLBAR_SHANGYE, 
				Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_REFRESH};
		//this.activityKind = Global.QUOTE_FENLEI;
		Bundle bundle = getIntent().getExtras();
		if (null !=bundle){
			stocktype = bundle.getInt("stocktype");
			flag = bundle.getInt("flag");
		}
		requestParams = new RequestParams();
		requestParams.setPaixu("zf");   //根据涨幅排序
		initToolBar(toolbarname, Global.BAR_TAG);
		setTitle();
		cols =getResources().getStringArray(R.array.hkmainboard_colsname);
		init(2); 
		menu = getResources().getStringArray(R.array.stock_type_menu);
		paiming = getResources().getStringArray(R.array.zqpm_menu2);
		desc = getResources().getStringArray(R.array.stock_desc);
		title1 = menu[0];
		openPopup();    //初始化弹出窗口
		
		requestParams.setBegin("1");
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(HKMainboard.this);
		rowHeight = CssSystem.getTableRowHeight(HKMainboard.this);
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(2, false, R.color.zr_newlightgray);
		
	}
	
	private void setTitle(){
		if (flag ==1){
			initTitle(R.drawable.njzq_title_left_back, 0,"香港主板");
			requestParams.setMarket("hkmain"); //主板
			this.activityKind = Global.HK_MAINBOARD;
		}else if (flag ==2){
			initTitle(R.drawable.njzq_title_left_back, 0,"香港创业板");
			requestParams.setMarket("hkcyb"); //创业板
			this.activityKind = Global.HK_CYB;
		}
	}
	@Override
	protected void init(final int type) {
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable(){
			public void run() {
				try{
					if (mLock&&nLock&&type ==1){
						if(grid==0) {//为0时按照缓存中的代码去取数据
							stocks = StockInfo.getStockInfo(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
							stocksname = StockInfo.getStockName(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
							quoteData = ConnService.getGridData(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocks);
						}else{
							quoteData = ConnService.execute(requestParams, 2);
						}
						try {
							if (Utils.isHttpStatus(quoteData)){
								if(grid==0) {//为0时按照缓存中的代码去取数据
									allStockNums = StockInfo.getStockInfoSize(stocktype);
								}
								else {
									allStockNums = quoteData.getInt("totalrecnum");
								}
								JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
								int len = jArr.length();
								if (len >0){
									listqueryfund.clear();
								}
								for (int i = 0; i < jArr.length(); i++) {
									JSONArray jA = (JSONArray)jArr.get(i);
									String  [] arr = new String [24];
									arr[0] = jA.getString(18);     //代码
									arr[1] = jA.getString(19);     //名称
									arr[2] = jA.getString(1);      //现价  --最新价
									arr[3] = jA.getString(12);     //涨幅
									arr[4] = jA.getString(14);     //涨跌
									arr[5] = jA.getString(0);      //总额  -- 成交金额
									arr[6] = jA.getString(4);      //总量 -- 成交量
									arr[7] = jA.getString(10);     //现量  --现手
									arr[8] = jA.getString(17);     //换手率  --换手
									arr[9] = jA.getString(7);      //市盈率
									arr[10] = jA.getString(23);    //市净率  
									arr[11] = jA.getString(2);      //买价
									arr[12] = jA.getString(3);      //卖价
									arr[13] = jA.getString(6);      //昨收
									arr[14] = jA.getString(5);      //今开
									arr[15] = jA.getString(8);      //最高
									arr[16] = jA.getString(9);      //最低
									arr[17] = jA.getString(15);     //委比
									arr[18] = jA.getString(16);     //委差
									arr[19] = jA.getString(11);     //量比
									arr[20] = jA.getString(22);     //每手股数
									arr[21] = jA.getString(20);     //市场
									arr[22] = jA.getString(24);     //买卖盘
									arr[23] = jA.getString(21);     //停牌
									listqueryfund.add(arr);
									
								}
								if(len<pageNum) {
									listqueryfund.addAll(TradeUtil.fillListToNull6(len, pageNum , stocks ,stocksname ));
								}
								isNetworkError = 0;
							}else {
								if(grid==0) {//为0时按照缓存中的代码去取数据
									listqueryfund.clear();
									listqueryfund.addAll(TradeUtil.fillListToNull6(0, pageNum , stocks ,stocksname));
								}else{
									listqueryfund.clear();
									listqueryfund.addAll(TradeUtil.fillListToNull6(0, pageNum , null ,null));
								}
								isNetworkError = -1;
							}
						} catch (JSONException e) {
							isNetworkError = -2;
							e.printStackTrace();
						}
					}else {
						stocks = StockInfo.getStockInfo(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
						stocksname = StockInfo.getStockName(Integer.parseInt(requestParams.getBegin()), Integer.parseInt(requestParams.getEnd()), stocktype);
					}
				} catch(Exception e){
					e.printStackTrace();
				}
				mLock = isRefreshTime();
				mHandler.sendEmptyMessage(0);
				mHandler.postDelayed(r, Config.hkquoterefresh);
			  }
		};
		mHandler.post(r);
	}
	
	protected void handlerData() {
		Runnable r = new Runnable(){
			public void run() {
				try{
					if(isNetworkError<0&&firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					if(quoteData ==null || quoteData.equals("")){
						listqueryfund.clear();
						listqueryfund.addAll(TradeUtil.fillListToNull6(0, pageNum , stocks ,stocksname));
						refreshHKUI(listqueryfund, cols );
						hiddenProgressToolBar();
						return;
					}
					JSONArray  jArr2 = (JSONArray)quoteData.getJSONArray("data");
					if (null !=jArr2 && jArr2.length() >0){
						refreshHKUI(listqueryfund, cols );
					}
				}catch(Exception e){
					e.printStackTrace();
				}finally {
					//进度条消失
					hiddenProgressToolBar();
				}
			}
		};
		runOnUiThread(r);
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		this.nLock = true;
		
		initPopupWindow();
		setToolBar();
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
	protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
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
				setToolBar(2, false, R.color.zr_newlightgray);
				setToolBar(3, true, R.color.zr_white);
			}
			else {
				setToolBar(2, true, R.color.zr_white);
				setToolBar(3, true, R.color.zr_white);
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
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(2, true, R.color.zr_white);
			setToolBar(3, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(2, true, R.color.zr_white);
			setToolBar(3, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
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
		switch ( t1 ){
			case 0: requestParams.setPaixu("zjcj"); n2=-3; break;   
			case 1: requestParams.setPaixu("zf"); n2=-4; break;    
			case 2: requestParams.setPaixu("zd"); n2=-5; break;
			case 3: requestParams.setPaixu("cjsl"); n2=-7; break;
			case 4: requestParams.setPaixu("cjje"); n2=-6; break;
			case 5: requestParams.setPaixu("wb"); n2=-18; break;
		}
		switch ( t2 ){
			case 0: requestParams.setDesc("desc"); n1=1; break;
			case 1: requestParams.setDesc("asc"); n1=0; break;						
		}
		grid = 1;
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(2, false, R.color.zr_newlightgray);
		setToolBar(3, true, R.color.zr_white);
		setToolBar();
	}
	
	protected void zqlbDesc2(int t1, int t2) {
		switch ( t1 ){
			//case -1: requestParams.setPaixu("zqdm"); break;    //代码
			//case -2: requestParams.setPaixu(""); break;        //名称
			
			case -3: requestParams.setPaixu("zjcj"); break;    //现价
			case -4: requestParams.setPaixu("zf"); break;      //涨幅
			case -5: requestParams.setPaixu("zd"); break;      //涨跌
			case -6: requestParams.setPaixu("cjje"); break;   //总额
			case -7: requestParams.setPaixu("cjsl"); break;	   //总量
			case -8: requestParams.setPaixu("xs"); break;     //现量
			case -9: requestParams.setPaixu("hs"); break;	   //换手率
			case -10: requestParams.setPaixu("syl1"); break;   //市盈率 
			case -11: requestParams.setPaixu("sjl"); break;   //市净率
			case -12: requestParams.setPaixu("bjw1"); break;    //买价
			case -13: requestParams.setPaixu("sjw1"); break;    //卖价
			case -14: requestParams.setPaixu("zrsp"); break;   //昨收
			case -15: requestParams.setPaixu("jrkp"); break;   //今开
			case -16: requestParams.setPaixu("zgcj"); break;   //最高
			case -17: requestParams.setPaixu("zdcj"); break;   //最低
			case -18: requestParams.setPaixu("wb"); break;	   //委比
			case -19: requestParams.setPaixu("wc"); break;     //委差
			case -20: requestParams.setPaixu("lb"); break;	   //量比
			case -21: requestParams.setPaixu("coefficient"); break;     //每手股数
		}
		switch ( t2 ){
			case 0: requestParams.setDesc("asc"); break;
			case 1: requestParams.setDesc("desc"); break;						
		}
		grid = 1;
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(2, false, R.color.zr_newlightgray);
		setToolBar(3, true, R.color.zr_white);
		setToolBar();
	}
	
	
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
			 if(!dlg.isShowing())
				 dlg.show();
			//openPopup();
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
	
	/**
	 * 改变背景色
	 */
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
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
