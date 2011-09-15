package com.cssweb.android.trade.fund;

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
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;
/**
 * 基金历史委托
 * @author hoho
 *
 */
public class HistoryTrust extends TradeQueryBase {
	private String startdate ;
	private String enddate ;
	private String DEBUG_TAG="HistoryTrust";
	private int btnTag = -1;
	private Thread thread = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initTitle(R.drawable.njzq_title_left_back, 0, "历史委托");
		//初始化工具栏
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		Bundle bundle = getIntent().getExtras();
		startdate = bundle.getString("startdate");
		enddate = bundle.getString("enddate");
		colsName = getResources().getStringArray(R.array.historyTrust_colsname);
		colsIndex= getResources().getStringArray(R.array.historyTrust_colsindex);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(2);
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		digitColsIndex.add(8);
		handlerData();
	}
	
	/**
	 * 请求后台数据
	 */
	protected void init (final int type){
		this.type = type;
		r= new Runnable(){
			public void run() {
				if(btnTag == -1 || btnTag == 3){
					if(type == 1){
						try {
							quoteData = FundService.getHistoryEntrust(startdate, enddate);
							String res = TradeUtil.checkResult(quoteData);
							if(res==null){
								allRecords = new JSONArray();
								JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
								for(int i=0,size=jsonArr.length()-1; i<size; i++){
									JSONObject jsonObj = (JSONObject)jsonArr.get(i);
									allRecords.put(formatJSONObject(jsonObj));
								}
								sort(allRecords);
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
	/**
	 * 接收消息,更新视图
	 */
	@Override
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
				Toast.makeText(HistoryTrust.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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
	
	
	/**
	 * 格式化数据
	 */
	@Override
	protected JSONObject formatJSONObject(JSONObject jsonObj)
			throws JSONException {
		JSONObject formatJsonObj  = new JSONObject();
		int color = GlobalColor.colorStockName;
		for (int i=0 , size=colsIndex.length; i<size ; i++ ){
			if(i ==0 ){
				color  = GlobalColor.colorStockName;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color );
			}else if (i == 3){
				color = GlobalColor.colorPriceEqual;
				String fundcompanyName = new TradeUtil().getFundCompanyName(jsonObj.getString(colsIndex[i-1]),HistoryTrust.this);
				formatJsonObj.put(colsIndex[i], fundcompanyName +"|" +color);
			}else if (i == 7){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3)+"|"+color);
			}else if (i ==9){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.dealFundTrdid(Integer.parseInt("24"+jsonObj.getString(colsIndex[i])) ) +"|" +color);
			}else if (i ==10){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.dealFundSBJG(Integer.parseInt(jsonObj.getString(colsIndex[i])) ) +"|" +color);
			}else {
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) +"|" +color);
			}
		}
		return formatJsonObj;
	}
	protected JSONObject formatNullJSONObject()throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		for(int i=0,size=colsIndex.length; i<size; i++){
			formatJsonObj.put(colsIndex[i],"|" + color);
		}
		return formatJsonObj;
	}
	
	/**
	 * 详细事件
	 */
	protected void displayDetails() {
		if(allRecords.length() ==0){
			return ;
		}
		forwardDetails(HistoryTrust.this);
	}
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
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
    public void sort(JSONArray values){
		//日期
		for(int i=0 ; i < values.length() ; ++i){
			for(int j=0; j <values.length() - i - 1; ++j){
				JSONObject jsonObj;
				JSONObject jsonObj1;
				try {
					jsonObj = (JSONObject)values.get(j);
					jsonObj1 = (JSONObject)values.get(j + 1);
					String rqStr = jsonObj.getString("FID_WTRQ");
					rqStr = rqStr.substring(0,rqStr.indexOf("|"));
					int rq = Integer.parseInt(rqStr);
					String rq1Str = jsonObj1.getString("FID_WTRQ");
					rq1Str = rq1Str.substring(0,rq1Str.indexOf("|"));
					int rq1 = Integer.parseInt(rq1Str);
					if(rq < rq1){
						values.put(j, jsonObj1);
						values.put(j+1, jsonObj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}	
		}
		//时间
		for(int i=0 ; i < values.length() ; ++i){
			for(int j=0; j <values.length() - i - 1; ++j){
				JSONObject jsonObj;
				JSONObject jsonObj1;
				try {
					jsonObj = (JSONObject)values.get(j);
					jsonObj1 = (JSONObject)values.get(j + 1);
					String rqStr = jsonObj.getString("FID_WTRQ");
					rqStr = rqStr.substring(0,rqStr.indexOf("|"));
					String sjStr = jsonObj.getString("FID_WTSJ");
					sjStr = sjStr.substring(0,sjStr.indexOf("|"));
					sjStr = sjStr.replaceAll(":", "");
					int rq = Integer.parseInt(rqStr);
					int sj = Integer.parseInt(sjStr);
					String rq1Str = jsonObj1.getString("FID_WTRQ");
					rq1Str = rq1Str.substring(0,rq1Str.indexOf("|"));
					String sj1Str = jsonObj1.getString("FID_WTSJ");
					sj1Str = sj1Str.substring(0,sj1Str.indexOf("|"));
					sj1Str = sj1Str.replaceAll(":", "");
					int rq1 = Integer.parseInt(rq1Str);
					int sj1 = Integer.parseInt(sj1Str);
					if(rq == rq1 && sj < sj1){
						values.put(j, jsonObj1);
						values.put(j+1, jsonObj);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}	
		}
	}
}
