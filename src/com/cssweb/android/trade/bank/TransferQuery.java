package com.cssweb.android.trade.bank;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.BankService;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.quote.util.GlobalColor;

/**
 * 转账查询
 * @author wangsheng
 *
 */
public class TransferQuery extends TradeQueryBase {
	private static final String DEBUG_TAG = "TransferQuery";
	private int btnTag = -1;
	private String strdate;
	private String enddate; 
	private String moneyType;
	private String bankType;
	
	private int todayJsonArrLen = 0;
	private Boolean jgdm = false;
	private Boolean pageFlag = true;
	private Thread thread = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		strdate = bundle.getString("strdate");
		enddate = bundle.getString("enddate");
		moneyType = bundle.getString("moneyType");
		bankType = bundle.getString("bankType");
		initTitle(R.drawable.njzq_title_left_back, 0, "转帐查询" );
		String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		colsName = getResources().getStringArray(R.array.zr_trade_query_yzzz_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_query_yzzz_index);
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
					if(type == 1){
						try{
							String startDate = (strdate==null)?"":strdate;
							String endDate = (enddate==null)?"":enddate;
							String bz = (moneyType==null)?"":moneyType;
							String yhdm = (bankType==null)?"":bankType;
							String seqno = "";//查询起始值，传入seqno 流水号的值
							if (!checkDate(DateTool.getToday(), strdate)){ //开始日期不是当天
								while (pageFlag){
									quoteData = BankService.getTransferList(startDate, endDate, bz, yhdm, seqno);
									String res = TradeUtil.checkResult(quoteData);
									if(res==null){
										allRecords = new JSONArray();
										JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
										for(int i=0,size=jsonArr.length()-1; i<size; i++){
											JSONObject jsonObj = (JSONObject)jsonArr.get(i);
											allRecords.put(formatJSONObject(jsonObj));
										}
										if (jsonArr.length()-1 < 200) {
											pageFlag = false;
										}else {
											JSONObject jsonObj = (JSONObject)jsonArr.get(jsonArr.length()-2);
											seqno = jsonObj.getString("FID_BROWINDEX");
										}
									}
								}
							}
							//查询当天数据
							if ((!pageFlag && checkDate(DateTool.getToday(), enddate)) || (checkDate(DateTool.getToday(), strdate) && checkDate(DateTool.getToday(), enddate))) {
								StringBuffer sb1 = new StringBuffer();
								sb1.append("FID_JGDM="+bankType+TradeUtil.SPLIT );
								sb1.append("FID_BZ="+moneyType);
								quoteData = ConnPool.sendReq("BANK_GET_TRANSFER_TODAY","303111",sb1.toString());
								String res = TradeUtil.checkResult(quoteData);
								if(res==null){
									JSONArray todayJsonArr = (JSONArray)quoteData.getJSONArray("item");
									todayJsonArrLen = todayJsonArr.length()-1;
									if (todayJsonArrLen > 0) {
										jgdm = true;
									}
									for(int i=0,size=todayJsonArrLen; i<size; i++){
										JSONObject jsonObj = (JSONObject)todayJsonArr.get(i);
										allRecords.put(formatJSONObject(jsonObj));
									}
								}
							}
							pageFlag = true;
							jgdm = false;
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
				Toast.makeText(TransferQuery.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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

	@Override
	protected JSONObject formatJSONObject(JSONObject jsonObj)
			throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		String dm = "";
		if (jgdm) {
			dm = "FID_JGDM";
		}else {
			dm = "FID_YHDM";
		}
		for(int i=0; i<colsIndex.length; i++) {
			if(i==0) {
				color = GlobalColor.colorStockName;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}else if(i == 2 ){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(dm) + "|" + color);
			}else if(i == 3 ){
				color = GlobalColor.colorPriceEqual;
				//获得银行的名称
				String bankName =  TradeUtil.getBankName(jsonObj.getString(dm));
				formatJsonObj.put(colsIndex[i], bankName + "|" + color);
			}else if(i ==4){
				color = GlobalColor.colorPriceEqual;
				String tranAmout = TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3);
				formatJsonObj.put(colsIndex[i], tranAmout +"|"+color);
			}else if(i ==5){
				color = GlobalColor.colorPriceEqual;
				String tranAmout = TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3);
				formatJsonObj.put(colsIndex[i], tranAmout +"|"+color);
			}else if(i == 6)
			{
				color = GlobalColor.colorPriceEqual;
				//获得币种
				String moneyName =  TradeUtil.getMoneyName(jsonObj.getString(colsIndex[i]));
				formatJsonObj.put(colsIndex[i], moneyName + "|" + color);
			}else if(i == 9)
			{
				color = GlobalColor.colorPriceEqual;
				//获得币种
				String bankTranType =  TradeUtil.dealBankTranType(jsonObj.getString("FID_YWLB"));
				formatJsonObj.put(colsIndex[i], bankTranType + "|" + color);
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
		forwardDetailsH(TransferQuery.this);
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
		Log.e("查询间隔时间==", day.intValue()+"");
		if(day.intValue()>=0){
			return true;
		}else {
			return false;
		}
    }
}
