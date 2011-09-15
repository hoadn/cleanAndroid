package com.cssweb.android.trade.stock;

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
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 历史成交查询
 * 
 * @author wangsheng
 *
 */
public class HistoryDeal extends TradeQueryBase {
	private static final String DEBUG_TAG ="HistoryDeal";
	private int btnTag = -1;
	private String strdate;
	private String enddate;
	private Boolean flag = true;
	private Thread thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		strdate = bundle.getString("strdate");
		enddate = bundle.getString("enddate");
		
		initTitle(R.drawable.njzq_title_left_back, 0, "历史成交" );
		
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		
		
		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_lscj_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_lscj_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(5);
		digitColsIndex.add(6);
		digitColsIndex.add(8);
		digitColsIndex.add(9);
		digitColsIndex.add(10);
		
		handlerData();
	}
	
	@Override
	protected void init(final int type) {
		this.type = type;
		r = new Runnable() {			
			public void run() {
				if(btnTag == -1 || btnTag == 3){
					if(type == 1){
						flag = true;
						StringBuffer sb = new StringBuffer();
						
						sb.append("FID_KSRQ=" + ((strdate==null)?"":strdate) + TradeUtil.SPLIT);
						sb.append("FID_JSRQ=" + ((enddate==null)?"":enddate) + TradeUtil.SPLIT);
						sb.append("FID_ROWCOUNT=0" + TradeUtil.SPLIT);
						
						try {
							quoteData = ConnPool.sendReq("GET_HISTORY_DEAL", "404201", sb.toString());
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
				Toast.makeText(HistoryDeal.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
			}
			return;
		}else if(flag){
			allRecords = new JSONArray();
			try {
				String res = TradeUtil.checkResult(quoteData);
				if(res!=null){
					if(!("-1").equals(res))
						toast(res);
					hiddenProgressToolBar();
					return;
				}
				JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
				for(int i=0,size=jsonArr.length()-1; i<size; i++){
					JSONObject jsonObj = (JSONObject)jsonArr.get(i);
					allRecords.put(formatJSONObject(jsonObj));
				}
				totalRecordCount = allRecords.length();
				flag = false;
			}catch (Exception e) {
				CssLog.e(DEBUG_TAG, e.toString());
				hiddenProgressToolBar();
			}
		}
		//填充当前页的内容
		fillCurrentPageContent(this);
		hiddenProgressToolBar();
		setBtnStatus();
	}
	
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(HistoryDeal.this);
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
				formatJsonObj.put(colsIndex[i], TradeUtil.StringConvertTime(jsonObj.getString(colsIndex[i])) + "|" + color);
			}else if(i==4){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.getTradeName(jsonObj.getString(colsIndex[i]).charAt(0)) + "|" + color);
			}else {
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}
		}
		return formatJsonObj; 
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
}
