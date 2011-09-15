package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.view.View;

import com.cssweb.android.base.QuoteFundGridActivity;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.service.QuoteWarnService;
import com.cssweb.android.service.ValidationService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;
/**
 * 行情预警表格
 * @author hoho
 *
 */
public class QuoteWarning  extends QuoteFundGridActivity {
	private String [] cols ;
	private String aesStr = null;
	private int pageNum = 10;
	private RequestParams requestParams;
	private Thread thread = null;
	private boolean firstComing = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread  = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		setContentView(R.layout.zr_static_table2);
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_MENU, Global.TOOLBAR_ADD, 
				Global.TOOLBAR_UPDATE ,Global.TOOLBAR_REMOVE, 
				Global.TOOLBAR_FENSHI ,Global.TOOLBAR_REFRESH };
		initToolBar(toolbarname, Global.BAR_TAG);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.quotewarn_title));
		cols = getResources().getStringArray(R.array.quotealarm_cols);
		requestParams = new RequestParams();
		this.activityKind = Global.QUOTE_WARNING;
	}
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(final int type) {
		mHandler.removeCallbacks(r);
		r = new Runnable(){
			public void run() {
				if (type ==1){
					if (mLock){
						try{
							aesStr = ValidationService.getServiceTime(); 
						}catch(Exception e){
							e.printStackTrace();
						}
						quoteData = QuoteWarnService.getQuoteWarnInfo(aesStr);
					}
					try{
						if( Utils.isHttpStatus(quoteData) ){
							JSONArray jArr = quoteData.getJSONArray("item");
							int len = jArr.length();
							listqueryfund.clear();
							for (int i=0; i< jArr.length() ; i++){
								JSONObject object = (JSONObject) jArr.get(i);
								String [] jarr = new String [7];
								jarr[0]= object.getString("ZQJC");   //名称
								jarr[1]= object.getString("ZJCJ");   //现价  ZJCJ
								String high = object.getString("ZGCJ");     //上破价ZGCJ
								String lower = object.getString("ZDCJ");	//下破价ZDCJ
								jarr[2]= high ;
								jarr[3]= lower;
								jarr[4]=object.getString("ZRSP");           //昨日收盘
								jarr[5]=object.getString("EXCHANGE");
								jarr[6]=object.getString("ZQDM");  
								listqueryfund.add(jarr);								 	
							}
							if (len < pageNum ){
								listqueryfund.addAll(TradeUtil.fillListToNull5(len, pageNum ));
							}
							isNetworkError = 0;
						}else {
							isNetworkError = -1;
						}
					}catch(Exception e){
						isNetworkError = -2;
						e.printStackTrace();
					}
				  
				}
				mLock = isRefreshTime();
				mHandler.sendEmptyMessage(0);
                mHandler.postDelayed(r, Config.yujingrefresh);

			}
		};
		mHandler.post(r);
	}
	/**
	 * 更新UI界面
	 * 
	 */
	@Override
	protected void handlerData() {
		Runnable r = new Runnable(){
			public void run() {
				try{
					if(isNetworkError<0&&firstComing) {
						firstComing = false;
						toast(R.string.load_data_error);
					}
					if(null ==quoteData ){
						listqueryfund.clear();
						listqueryfund.addAll(TradeUtil.fillListToNull5(0, pageNum ));
						refreshWarnQueryUI(listqueryfund, cols, null);
						hiddenProgressToolBar();
						return;
					}
					refreshWarnQueryUI(listqueryfund, cols, null);
					hiddenProgressToolBar();
				}catch(Exception  e){
					e.printStackTrace();
				}finally {
					//进度条消失
					hiddenProgressToolBar();
				}
			}
		};
		runOnUiThread(r);
		
	}
	
	
	/**
	 * 改变背景色
	 */
	protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		mLock = true;
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(QuoteWarning.this);
		rowHeight = CssSystem.getTableRowHeight(QuoteWarning.this);
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		handlerData();
		setToolBar();
		initPopupWindow();
		
	}
	
	@Override
	protected void toolBarClick(int tag, View v) {
		switch(tag){
		case 0:
			onOption();
			break;
		case 1:		//增加
			Intent intent = new Intent();
			intent.setClass(this, QuoteSet.class);
			intent.putExtra("isAdd", "1");
			intent.putExtra("flag", "1");
			startActivity(intent);
			//this.finish();
			break;
		case 2:   //修改
			if(  (null !=stockFundName && !stockFundName.equals("")) && (null!=stockFundCode && !stockFundCode.equals(""))  )  {
				Intent intent2  = new Intent();
				intent2.putExtra("stockname", stockFundName);
				intent2.putExtra("zjcjParam", zjcjParam);
				intent2.putExtra("spjParam", spjParam);
				intent2.putExtra("xpjParam", xpjParam);
				intent2.putExtra("exchangeParam", exchangeParam);
				intent2.putExtra("flag", "1");
				intent2.putExtra("stockFundCode", stockFundCode);
				intent2.setClass(this,  QuoteSet.class);
				intent2.putExtra("isAdd", "2");
				startActivity(intent2);
				//this.finish();
			}
			break;
		case 3:	  //删除
			if(  (null !=stockFundName && !stockFundName.equals("")) && (null!=stockFundCode && !stockFundCode.equals(""))  )  {
				new AlertDialog.Builder(QuoteWarning.this) 
	            .setTitle(getResources().getString(R.string.alert_tip)) 
	            .setMessage(getResources().getString(R.string.alert_delete)) 
	            .setPositiveButton(getResources().getString(R.string.alert_dialog_ok), 
	                    new DialogInterface.OnClickListener() { 
	                        public void onClick(DialogInterface dialog,  int whichButton) { 
	                        	QuoteWarnService.delQuoteWarn(stockFundCode,new Integer( exchangeParam ), ValidationService.getServiceTime() );   
	                        	m_nPos =0;
	                        	mLock = true;
	                        	setToolBar();
	                        } 
	             })
	             .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel), 
	                    new DialogInterface.OnClickListener() { 
	                        public void onClick(DialogInterface dialog, int whichButton) { 
	                        	
	                        } 
	             }) 
	             .show();
			}
			break;
		case 4:	  //分时
			if(  (null !=stockFundName && !stockFundName.equals("")) && (null!=stockFundCode && !stockFundCode.equals(""))  )  {
				FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(exchangeParam), stockFundCode, stockFundName, QuoteWarning.this);
			}
			break;
		case 5:   //刷新
			mLock = true;
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
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLock = false;
		mHandler.removeCallbacks(r);
	}
	
}
