package com.cssweb.android.trade.stock;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 持仓查询
 * 
 * @author wangsheng
 *
 */
public class GetPosition extends TradeQueryBase {
	private final String DEBUG_TAG = "GetPosition";
	private int btnTag = -1;
	private Thread thread = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_BUY,Global.TOOLBAR_SELL,
				Global.TOOLBAR_DETAIL,Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,  Global.TOOLBAR_REFRESH};
		initTitle(R.drawable.njzq_title_left_back, 0, "持仓查询");
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		

		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_position_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_position_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(2);
		digitColsIndex.add(3);
		digitColsIndex.add(4);
		digitColsIndex.add(5);
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		digitColsIndex.add(8);
		digitColsIndex.add(9);
		digitColsIndex.add(10);
		
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
				if(btnTag == -1 || btnTag == 5){
					if(type == 1){
						try {
							//查询股票的持仓信息
							quoteData = TradeService.getStockPosition();
							String res = TradeUtil.checkResult(quoteData);
							if(res==null){
								allRecords = new JSONArray();
								JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
								for(int i=0,size=jsonArr.length()-1; i<size; i++){
									JSONObject jsonObj = (JSONObject)jsonArr.get(i);
									allRecords.put(formatJSONObject(jsonObj));
								}
								totalRecordCount = allRecords.length();
							}
						} catch (JSONException e) {
							CssLog.e(DEBUG_TAG, e.toString());
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	
	
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 5) {// 进去页面请求 或 刷新
				Toast.makeText(GetPosition.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
				setToolBar(3, false, R.color.zr_dimgray);
				setToolBar(4, false, R.color.zr_dimgray);
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
		int color = GlobalColor.colorPriceEqual;
		String lastPrice = "0";
		String referencePrice = jsonObj.getString(colsIndex[7]);
		double zxsz = Double.parseDouble(referencePrice);
		double tbfdyk = Double.parseDouble(jsonObj.getString(colsIndex[8]));
		if (tbfdyk>0) {
			color = GlobalColor.colorpriceUp;
		}else {
			color = GlobalColor.colorPriceDown;
		}
		
		for(int i=0,size=colsIndex.length; i<size; i++){
//			if(i==0 ){
//				color = GlobalColor.colorStockName;
//				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
//			}else 
			if(i==5)
			{
				formatJsonObj.put(colsIndex[i], TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3) + "|" + color);
			}else if(i==6)
			{
				formatJsonObj.put(colsIndex[i], TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3) + "|" + color);
			}else if(i==7)
			{
				//color = GlobalColor.colorPriceEqual;
				//referencePrice = jsonObj.getString(colsIndex[i]);
				formatJsonObj.put(colsIndex[i], TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3) + "|" + color);
			}else if(i==8)
			{
				//color = GlobalColor.colorPriceEqual;
				lastPrice = jsonObj.getString(colsIndex[i]);
				formatJsonObj.put(colsIndex[i], TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3) + "|" + color);
			}else if(i==9)
			{
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3) + "|" + color);
			}else if(i==10)
			{
				//color = GlobalColor.colorPriceEqual;
				
				double temp = 0.000;
				double dou = zxsz;
	            if((dou-0) > 0.000000001){
	            	temp = Double.parseDouble(lastPrice)/( dou - Double.parseDouble(lastPrice))*100;
	            }else if (temp==0) {
            		temp = temp-100;
				}
				formatJsonObj.put("perincome", TradeUtil.formatNum(temp+"",3) + "|" + color);
			}else{
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}
		}
		return formatJsonObj;
	}
	protected void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(GetPosition.this);
	}
	/***
	 * @author andy
	 * 卖出股票的相关事件
	 * 
	 */
	private void onSellEvent() {
		try {
			JSONObject jsonObj = allRecords.getJSONObject(currentSelectedId);
			//FairyUI.switchToWnd(Global.TRADE_STOCK, 1, jsonObj.getString("FID_ZQDM"),"", GetPosition.this);
			Intent intent = new Intent();
			intent.setClass(GetPosition.this, StockTrading.class);
			intent.putExtra("type", 1);
			intent.putExtra("bsname", "卖出");
			intent.putExtra("stkcode", jsonObj.getString("FID_ZQDM"));
			intent.putExtra("allRecords", allRecords.toString());
			startActivity(intent);
		} catch (JSONException e) {
			CssLog.e(DEBUG_TAG, e.toString());
		}
	}
	/***
	 * @author andy
	 * 
	 * 买入股票的事件
	 */
	private void onBuyEvent() {
		try {
			JSONObject jsonObj = allRecords.getJSONObject(currentSelectedId);
			//FairyUI.switchToWnd(Global.TRADE_STOCK, 1, jsonObj.getString("FID_ZQDM"),"", GetPosition.this);
			Intent intent = new Intent();
			intent.setClass(GetPosition.this, StockTrading.class);
			intent.putExtra("type", 0);
			intent.putExtra("bsname", "买入");
			intent.putExtra("stkcode", jsonObj.getString("FID_ZQDM"));
			intent.putExtra("allRecords", allRecords.toString());
			startActivity(intent);
		} catch (JSONException e) {
			CssLog.e(DEBUG_TAG, e.toString());
		}
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
				onBuyEvent();
				break;
			case 1:
				onSellEvent();
				break;
			case 2:
				displayDetails();
				break;
			case 3: 
				super.onPageUp();
				break;
			case 4:
				super.onPageDown();
				break;
			case 5: 
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
			setToolBar(3, false, R.color.zr_dimgray);
			setToolBar(4, false, R.color.zr_dimgray);
		}else{
			setToolBar(0, true, R.color.zr_white);
			setToolBar(1, true, R.color.zr_white);
			setToolBar(2, true, R.color.zr_white);
			if(endRowId >= totalRecordCount-1){
				setToolBar(4, false, R.color.zr_dimgray);
			}else {
				setToolBar(4, true, R.color.zr_white);
			}
			if(currentPageId == 0){
				setToolBar(3, false, R.color.zr_dimgray);
			}else {
				setToolBar(3, true, R.color.zr_white);
			}
		}
	}
}