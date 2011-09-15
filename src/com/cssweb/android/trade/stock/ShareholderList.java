package com.cssweb.android.trade.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 股东列表
 * 
 * @author wangsheng
 *
 */
public class ShareholderList extends TradeQueryBase{
	private static final String DEBUG_TAG = "ShareholderList";
	private int btnTag = -1;
	private Thread thread = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "股东列表" );
		String[] toolbarNames = {
				Global.TOOLBAR_DETAIL,Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_REFRESH};
		initToolBar(toolbarNames, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_holder_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_holder_index);
		
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
						try{
							String filedate = ActivityUtil.getPreference(ShareholderList.this,"openholdersListDate", "");
							if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
								quoteData = ConnPool.sendReq("GET_STOCKACCOUNT", "304001","");
							}else{
								String jsonObject = CssIniFile.loadIni(ShareholderList.this, 9, "ShareholdersList");
								if(null !=jsonObject && ! jsonObject.equals("")){
									quoteData = new JSONObject(jsonObject);
								}
							}
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
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
				Toast.makeText(ShareholderList.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		for(int i=0,size=colsIndex.length; i<size; i++){
			if(i==0){
				color = GlobalColor.colorStockName;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}else if(i==1){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}else if(i==2){
				color = GlobalColor.colorpriceUp;
				//String str = jsonObj.getString(colsIndex[i]);
				formatJsonObj.put(colsIndex[i], TradeUtil.getMarkName(jsonObj.getString(colsIndex[i])) + "|" + color);
			}else if(i==3){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}
		}
		return formatJsonObj;
	}
	/**
	 * 处理详细事件
	 */
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(ShareholderList.this);
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
