package com.cssweb.android.trade.transferFunds;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssLog;
//import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.BankService;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 资金转账流水查询
 * @author chengfei
 *
 */
public class TransferFundsQuery extends TradeQueryBase {
	private static final String DEBUG_TAG = "TransferFundsQuery";
	private int btnTag = -1;
	private String strdate;
	private String enddate;
	private Boolean zflag = true;
	private Boolean fflag = true;
//	private String moneyType;
//	private String bankType;
//	private String bankZh;
	
//	private int todayJsonArrLen = 0;
//	private Boolean flag = false;
	private Thread thread = null;
//	private String seqno = "";//查询起始值，传入seqno 流水号的值
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		strdate = bundle.getString("strdate");
		enddate = bundle.getString("enddate");
//		moneyType = bundle.getString("moneyType");
//		bankType = bundle.getString("bankType");
//		bankZh = bundle.getString("bankZh");
		initTitle(R.drawable.njzq_title_left_back, 0, "调拨流水查询" );
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		colsName = getResources().getStringArray(R.array.zr_trade_query_zjdbyzzz_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_query_zjdbyzzz_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(3);
		digitColsIndex.add(4);
		
		handlerData();
	}

    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	protected void init(final int type) {
		this.type = type;
		r = new Runnable() {			
			public void run() {
				if(btnTag == -1 || btnTag == 3){
//					if (reqPageId<=currentPageId){
						if(type == 1){
							try{
//								if (!checkDate(DateTool.getToday(), strdate)) {
									//查询转账的流水记录
								String startDate = (strdate==null)?"":strdate;
								String endDate = (enddate==null)?"":enddate;
								String zseqno = "";
								while (zflag ) {
									String ywkm = "10112";
									quoteData = BankService.getTransferFundsList(startDate, endDate, ywkm,zseqno);
									String res = TradeUtil.checkResult(quoteData);
									if(res==null){
										allRecords = new JSONArray();
										JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
//										Log.e("<<<<<<<<<<<<<<<<10112  jsonArr.length()-1>>>>>>>>>", jsonArr.length()-1+"");
										for(int i=0,size=jsonArr.length()-1; i<size; i++){
											JSONObject jsonObj = (JSONObject)jsonArr.get(i);
											allRecords.put(formatJSONObject(jsonObj));
										}
										if (jsonArr.length()-1 < 200) {
											zflag = false;
										}else {
											for(int i=0,size=jsonArr.length()-1; i<size; i++){
												JSONObject jsonObj = (JSONObject)jsonArr.get(i);
												if(i == jsonArr.length()-2){
													zseqno = jsonObj.getString("FID_BROWINDEX");
												}
											}
										}
									}
								}
								String fseqno = "";
								while (fflag) {
									String ywkm = "10212";
									quoteData = BankService.getTransferFundsList(startDate, endDate, ywkm,fseqno);
									String res = TradeUtil.checkResult(quoteData);
									if(res==null){
										JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
//										Log.e("<<<<<<<<<<<<<<<<10212  jsonArr.length()-1>>>>>>>>>", jsonArr.length()-1+"");
										for(int i=0,size=jsonArr.length()-1; i<size; i++){
											JSONObject jsonObj = (JSONObject)jsonArr.get(i);
											allRecords.put(formatJSONObject(jsonObj));
										}
										if (jsonArr.length()-1 < 200) {
											fflag = false;
										}else {
											for(int i=0,size=jsonArr.length()-1; i<size; i++){
												JSONObject jsonObj = (JSONObject)jsonArr.get(i);
												if(i == jsonArr.length()-2){
													fseqno = jsonObj.getString("FID_BROWINDEX");
												}
											}
										}
									}
								}
								
								sort(allRecords);
								totalRecordCount = allRecords.length();
//								Log.e("<<<<<<<<<<<<<<<<<<<allRecords>>>>>>>>>>>", allRecords+"");
//								Log.e("<<<<<<<<<<<<<<<<<<<totalRecordCount>>>>>>>>>>>", String.valueOf(totalRecordCount));
//									if(quoteData!=null){
//										JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
//										len = jsonArr.length()-1;
//										Log.e("<<<<<<<<<<<< len  >>>>>>>>>", len+"");
//										int tag = 2;
//										if (len-1<pageNum) {
//											tag = 1;
////											lastPageId = currentPageId;
////											flag = checkDate(DateTool.getToday(), enddate);
//										}
//										for(int i=0,size=jsonArr.length()-tag; i<size; i++){
//											JSONObject jsonObj = (JSONObject)jsonArr.get(i);
//											allRecords.put(formatJSONObject(jsonObj));
//											if(i == jsonArr.length()-tag-1){
//												seqno = jsonObj.getString("FID_BROWINDEX");
//											}
//										}
//										reqPageId++;
//									}
//								}
								
//								//查询当天数据
//								if ((len-1<pageNum && flag) || (checkDate(DateTool.getToday(), strdate) && checkDate(DateTool.getToday(), enddate))) {
//									StringBuffer sb1 = new StringBuffer();
//									sb1.append("FID_ZJZH="+new TradeUtil().getZjzhByYhdm(bankType, TransferFundsQuery.this)+TradeUtil.SPLIT);
//									sb1.append("FID_JGDM="+bankType+TradeUtil.SPLIT );
//									sb1.append("FID_BZ=RMB");
//									quoteData = ConnPool.sendReq("BANK_GET_TRANSFER_TODAY","303111",sb1.toString());
//									String res = TradeUtil.checkResult(quoteData);
//									if(res==null){
//										JSONArray todayJsonArr = (JSONArray)quoteData.getJSONArray("item");
//										todayJsonArrLen = todayJsonArr.length()-1;
//										for(int i=0,size=todayJsonArrLen; i<size; i++){
//											JSONObject jsonObj = (JSONObject)todayJsonArr.get(i);
//											allRecords.put(formatJSONObject(jsonObj));
//										}
//									}
//								}
							} catch (JSONException e) {
								CssLog.e(DEBUG_TAG, e.toString());
							}
						}
//					}
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
				Toast.makeText(TransferFundsQuery.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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
			}else if(i ==3){
				color = GlobalColor.colorPriceEqual;
				String fsje = jsonObj.getString(colsIndex[i]);
				if (fsje == null || "".equals(fsje)) {
					fsje = "0";
				}
				String tranAmout = TradeUtil.formatNum(fsje, 3);
				formatJsonObj.put(colsIndex[i], tranAmout +"|"+color);
			}else if(i ==4){
				color = GlobalColor.colorPriceEqual;
				String tranAmout = TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3);
				formatJsonObj.put(colsIndex[i], tranAmout +"|"+color);
			}else if(i == 6){
				color = GlobalColor.colorPriceEqual;
				//获得币种
				String moneyName =  TradeUtil.getMoneyName(jsonObj.getString(colsIndex[i]));
				formatJsonObj.put(colsIndex[i], moneyName + "|" + color);
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
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(TransferFundsQuery.this);
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
					String rqStr = jsonObj.getString("FID_RQ");
					rqStr = rqStr.substring(0,rqStr.indexOf("|"));
					int rq = Integer.parseInt(rqStr);
					String rq1Str = jsonObj1.getString("FID_RQ");
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
					String rqStr = jsonObj.getString("FID_RQ");
					rqStr = rqStr.substring(0,rqStr.indexOf("|"));
					String sjStr = jsonObj.getString("FID_FSSJ");
					sjStr = sjStr.substring(0,sjStr.indexOf("|"));
					sjStr = sjStr.replaceAll(":", "");
					int rq = Integer.parseInt(rqStr);
					int sj = Integer.parseInt(sjStr);
					String rq1Str = jsonObj1.getString("FID_RQ");
					rq1Str = rq1Str.substring(0,rq1Str.indexOf("|"));
					String sj1Str = jsonObj1.getString("FID_FSSJ");
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

	public static Boolean checkDate(String startDate,String endDate){
	    SimpleDateFormat sf = new SimpleDateFormat("yyyyMMdd");
	    SimpleDateFormat sf1 = new SimpleDateFormat("yyyy-MM-dd");
		Long endlong = null;
		Long startlong = null;
		try {
			endlong = (sf.parse(endDate)).getTime();
			startlong = (sf1.parse(startDate)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long day = (endlong - startlong)/1000/60/60/24;
		if(day.intValue()>=0){
			return true;
		}else {
			return false;
		}
    }
}
