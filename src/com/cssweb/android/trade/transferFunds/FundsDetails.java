package com.cssweb.android.trade.transferFunds;

import java.text.DecimalFormat;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 资金明细查询
 * 
 * @author chengfei
 *
 */
public class FundsDetails extends TradeQueryBase {
	private static final String DEBUG_TAG = "FundsDetails";
	private int btnTag = -1;
	private Thread thread = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "资金明细查询");
		
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		colsName = getResources().getStringArray(R.array.zr_trade_funds_details_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_funds_details_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(2);
		digitColsIndex.add(3);
		digitColsIndex.add(4);
		digitColsIndex.add(5);
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		
		handlerData();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	@Override
	protected void init(final int type) {
		this.type = type;
		r = new Runnable() {			
			public void run() {
				if(btnTag == -1 || btnTag == 3){
					if(type == 1){
						try {
							//资金明细查询
							StringBuffer sb = new StringBuffer();
							sb.append("FID_EXFLG=1&");
							sb.append("FID_BZ=RMB");
							quoteData = ConnPool.sendReq("GET_FUNDLIST","303002",sb.toString());
							String res = TradeUtil.checkResult(quoteData);
							if(res==null){
								allRecords = new JSONArray();
								JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
								Double zjye = 0.00,kyye = 0.00,kqzj = 0.00,djzj = 0.00,zzc = 0.00;
								for(int i=0,size=jsonArr.length()-1; i<size; i++){
									JSONObject jsonObj = (JSONObject)jsonArr.get(i);
									if ("".equals(jsonObj.getString("FID_ZHYE"))) {
										zjye +=0;
									}else {
										zjye = zjye + Double.valueOf(jsonObj.getString("FID_ZHYE"));
									}
									if ("".equals(jsonObj.getString("FID_KYZJ"))) {
										kyye +=0;
									}else {
										kyye = kyye + Double.valueOf(jsonObj.getString("FID_KYZJ"));
									}
									if ("".equals(jsonObj.getString("FID_KQZJ"))) {
										kqzj +=0;
									}else {
										kqzj = kqzj + Double.valueOf(jsonObj.getString("FID_KQZJ"));
									}
									if ("".equals(jsonObj.getString("FID_DJJE"))) {
										djzj +=0;
									}else {
										djzj = djzj + Double.valueOf(jsonObj.getString("FID_DJJE"));
									}
									if ("".equals(jsonObj.getString("FID_ZZC"))) {
										zzc +=0;
									}else {
										zzc = zzc + Double.valueOf(jsonObj.getString("FID_ZZC"));
									}
									allRecords.put(formatJSONObject(jsonObj));
								}
								JSONObject summaryObj = new JSONObject();
								int color = GlobalColor.colorStockName;
								for(int i=0; i<colsIndex.length; i++) {
									if(i==0) {
										color = GlobalColor.colorStockName;
										summaryObj.put(colsIndex[i], "汇总|" + color);
									}else if(i==1){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i], "人民币|" + color);
									}else if(i == 2){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(zjye)+"|"+color);
									}else if(i == 3){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(kyye)+"|"+color);
									}else if(i == 4){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(kqzj)+"|"+color);
									}else if(i == 5){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(djzj)+"|"+color);
									}else if(i == 6){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(zzc)+"|"+color);
									}else if(i == 7){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],doubleToStr(kqzj)+"|"+color);
									}else if(i == 8){
										color = GlobalColor.colorPriceEqual;
										summaryObj.put(colsIndex[i],"|"+color);
									}
								}
								allRecords.put(summaryObj);
								totalRecordCount = allRecords.length();
							}
						} catch (JSONException e) {
							Log.e(DEBUG_TAG, e.toString());
						}
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	
	@Override
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
				Toast.makeText(FundsDetails.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
			}
			return;
		}
		try {
			String res = TradeUtil.checkResult(quoteData);
			if(res!=null){
				if(!("-1").equals(res))
					toast(res);
				hiddenProgressToolBar();
				return;
			}
			
		}catch (Exception e) {
			CssLog.e(DEBUG_TAG, e.toString());
			hiddenProgressToolBar();
		}
		//填充当前页的内容
		fillCurrentPageContent(this);
		hiddenProgressToolBar();
		setBtnStatus();
	}
	@Override
	protected JSONObject formatJSONObject(JSONObject jsonObj)
			throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		for(int i=0; i<colsIndex.length; i++) {
			if(i==0) {
				color = GlobalColor.colorStockName;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}else if(i==1){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.getMoneyName(jsonObj.getString(colsIndex[i])) + "|" + color);
			}else if(i == 7){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],jsonObj.getString(colsIndex[4])+"|"+color);
			}else if(i == 8){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.getFundsType(jsonObj.getString(colsIndex[i]))+"|"+color);
			}else {
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}
		}
		return formatJsonObj; 
	}
	/**
	 * 处理详细事件
	 */
	protected void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(FundsDetails.this);
	}
	
	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		setToolBar();
	}
	@Override
	protected void toolBarClick(int tag, View v) {
		btnTag = tag;
		 switch(tag) {
			case 0:
				displayDetails();
				break;
			case 1: 
				super.onPageUp();
				break;
			case 2:
				super.onPageDown();
				break;
			case 3: 
				setToolBar();
				break;
			default:
				cancelThread();
				break;
		 }
	}
	protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	private void setBtnStatus(){
		if (totalRecordCount == 0) {
			setToolBar(0, false, R.color.zr_dimgray);
			setToolBar(1, false, R.color.zr_dimgray);
			setToolBar(2, false, R.color.zr_dimgray);
		}else{
			setToolBar(0, true, R.color.zr_white);
			if(endRowId >= totalRecordCount-1){
				setToolBar(2, false, R.color.zr_dimgray);
			}else {
				setToolBar(2, true, R.color.zr_white);
			}
			if(currentPageId == 0){
				setToolBar(1, false, R.color.zr_dimgray);
			}else {
				setToolBar(1, true, R.color.zr_white);
			}
		}
	}
	public static String doubleToStr(double   d)   { 
        DecimalFormat   format=new   DecimalFormat( "###0.00 "); 
        String   str   =   " "; 
        str   =   format.format(d); 
        return   str; 
    }
}
