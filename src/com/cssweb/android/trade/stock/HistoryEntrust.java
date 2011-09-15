package com.cssweb.android.trade.stock;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 历史委托查询
 */
public class HistoryEntrust extends TradeQueryBase {
	private static final String DEBUG_TAG ="HistoryEntrust";
	private int btnTag = -1;
	private String strdate;
	private String enddate;
	private Thread thread = null;
	private String seqno = "";//查询起始值，传入seqno 流水号的值
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		strdate = bundle.getString("strdate");
		enddate = bundle.getString("enddate");
		
		initTitle(R.drawable.njzq_title_left_back, 0, "历史委托" );
		
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		
		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_lswt_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_lswt_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(5);
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		digitColsIndex.add(8);
		digitColsIndex.add(9);
		
		handlerData();
		allRecords = new JSONArray();
	}
	
	@Override
	protected void init(final int type) {
		this.type = type;
		r = new Runnable() {			
			public void run() {
				if (btnTag != 1 && btnTag != 0) {
					if (reqPageId<=currentPageId) {
						if(type == 1){
							StringBuffer sb = new StringBuffer();
							sb.append("FID_KSRQ=" + ((strdate==null)?"":strdate) + TradeUtil.SPLIT);
							sb.append("FID_JSRQ=" + ((enddate==null)?"":enddate) + TradeUtil.SPLIT);
							sb.append("FID_JYS=" + TradeUtil.SPLIT);
							sb.append("FID_GDH=" + TradeUtil.SPLIT);
							sb.append("FID_WTLB=" + TradeUtil.SPLIT);
							sb.append("FID_ZQDM=" + TradeUtil.SPLIT);
							sb.append("FID_WTFS=" + TradeUtil.SPLIT);
							sb.append("FID_BROWINDEX=" +seqno+ TradeUtil.SPLIT);
							int count = pageNum+1;
							sb.append("FID_ROWCOUNT="+count);
							try {
								quoteData = ConnPool.sendReq("GET_HISTORY_ENTRUST", "404202", sb.toString());
								if(quoteData!=null){
									JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
									len = jsonArr.length()-1;
									int tag = 2;
									if (len-1<pageNum) {
										tag = 1;
									}
									for(int i=0,size=jsonArr.length()-tag; i<size; i++){
										JSONObject jsonObj = (JSONObject)jsonArr.get(i);
										allRecords.put(formatJSONObject(jsonObj));
										if(i == jsonArr.length()-tag-1){
											seqno = jsonObj.getString("FID_BROWINDEX");
										}
									}
									reqPageId++;
								}
							} catch (JSONException e) {
								Log.e(DEBUG_TAG, e.toString());
							}
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
			if (btnTag == 2) {
				Toast.makeText(HistoryEntrust.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				super.onPageUp();
				hiddenProgressToolBar();
				return;
				
			}
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
				Toast.makeText(HistoryEntrust.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
			}
			return;
		}else{
			try {
				String res = TradeUtil.checkResult(quoteData);
				if(res!=null){
					if(!("-1").equals(res))
						toast(res);
					hiddenProgressToolBar();
					return;
				}
				totalRecordCount = allRecords.length();
				fillCurrentPageContent(this);
			}catch (Exception e) {
				hiddenProgressToolBar();
			}
		}
		hiddenProgressToolBar();
		setBtnStatus();
	}
	
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetailsH(HistoryEntrust.this);
	}
	
	@Override
	protected JSONObject formatJSONObject(JSONObject jsonObj)
			throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorPriceEqual;
		String mmbz = TradeUtil.getFlagName(jsonObj.getInt("FID_WTLB"),jsonObj.getString("FID_CXBZ"));
		if ("买入".equals(mmbz)) {
			color = GlobalColor.colorpriceUp;
		}else if ("卖出".equals(mmbz)) {
			color = getResources().getColor(R.color.zr_sail);
		}
		for(int i=0; i<colsIndex.length; i++) {
//			if(i==0) {
//				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
//			}
			if(i==4){
//				color = GlobalColor.colorPriceEqual;
//				String mmbz = TradeUtil.getFlagName(jsonObj.getInt("FID_WTLB"),jsonObj.getString("FID_CXBZ"));
//				if ("买入".equals(mmbz)) {
//					color = GlobalColor.colorpriceUp;
//				}else if ("卖出".equals(mmbz)) {
//					color = GlobalColor.colorPriceDown;
//				}
				formatJsonObj.put(colsIndex[i],mmbz+"|"+color);
			}
			else if(i == 5){
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3)+"|"+color);
			}else if(i == 7){
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3)+"|"+color);
			}else if(i == 11){
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.dealOrderType(jsonObj.getInt(colsIndex[i]))+"|"+color);
			}else {
				//color = GlobalColor.colorPriceEqual;
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
				super.onPageDownH();
				break;
			case 3: 
				seqno = "";
				allRecords = new JSONArray();
				reqPageId = 0;
				currentPageId = 0;
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
	protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	private void setBtnStatus(){
		if (totalRecordCount == 0) {
			setToolBar(0, false, R.color.zr_dimgray);
			setToolBar(1, false, R.color.zr_dimgray);
			setToolBar(2, false, R.color.zr_dimgray);
		}else{
			setToolBar(0, true, R.color.zr_white);
			if (reqPageId-1<=currentPageId){
				if(len-1<pageNum ){
					setToolBar(2, false, R.color.zr_dimgray);
				}else {
					setToolBar(2, true, R.color.zr_white);
				}
			}else {
				if(endRowId >= totalRecordCount-1){
					setToolBar(2, false, R.color.zr_dimgray);
				}else {
					setToolBar(2, true, R.color.zr_white);
				}
			}
			if(currentPageId == 0){
				setToolBar(1, false, R.color.zr_dimgray);
			}else {
				setToolBar(1, true, R.color.zr_white);
			}
		}
	}
}
