package com.cssweb.android.trade.fund;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.quote.util.GlobalColor;
/**
 * 基金当日委托、基金撤单
 */
public class TodayTrust extends TradeQueryBase {
	private String DEBUG_TAG ="todayTrust";
	//private String [] colsName  =null ;  //列名
	//private String [] colsIndex = null;  //索引
	private String bundletype;
	private int btnTag = -1;
	private Thread thread = null;
	private ServiceControl sc = null;//行为分析
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sc = new ServiceControl(this);//行为分析初始化
        Bundle bundle = getIntent().getExtras();
        bundletype=bundle.getString("menudetail");
        setTitle();
		colsName  = getResources().getStringArray(R.array.todaytrust_colsname);
		colsIndex = getResources().getStringArray(R.array.todaytrust_colsindex);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		digitColsIndex.add(8);
		
	    handlerData();
	}
	/**
	 * 请求后台数据
	 */
	protected void init(final int type){
		this.type = type;
		try{
			r = new Runnable(){
				public void run() {
					if(btnTag == -1 || btnTag == 4){
						if(type == 1){
							try {
								quoteData = FundService.getTodayEntrust();
								String res = TradeUtil.checkResult(quoteData);
								if(res == null){
									allRecords =new JSONArray();
									JSONArray jarr = (JSONArray) quoteData.getJSONArray("item");
										if(bundletype.equals(Global.QUERY_FUND_DRWT)){
											for (int i=0,size=jarr.length()-1; i<size ;i++){
												JSONObject jsonObj  =(JSONObject) jarr.get(i);
												allRecords.put(formatJSONObject(jsonObj));
												//new FormatJsonTask().execute(jsonObj);
											}
										}
										if (bundletype.equals(Global.QUERY_FUND_CD)){
											for (int i=0,size=jarr.length()-1; i<size ;i++){
												JSONObject jsonObj  =(JSONObject) jarr.get(i);
													if(!"-1".equals(jsonObj.get("FID_SBJG"))){
														allRecords.put(formatJSONObject(jsonObj));
														//new FormatJsonTask().execute(jsonObj);
													}
											}
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
		}catch(Exception e ){
			Log.e(DEBUG_TAG, e.toString());
		}
	}
	/**
	 * 接收消息,更新视图
	 */
	
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 4) {// 进去页面请求 或 刷新
				Toast.makeText(TodayTrust.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
				setToolBar(3, false, R.color.zr_dimgray);
			}
			return;
		}
		try{
			String res = TradeUtil.checkResult(quoteData);
			if(res!=null){
				if(!("-1").equals(res))
					toast(res);
				hiddenProgressToolBar();
				return;
			}
		}catch(Exception e ){
			Log.e(DEBUG_TAG, e.toString());
			hiddenProgressToolBar();
		}
		//填充当前页的内容
		fillCurrentPageContent(this);
		hiddenProgressToolBar();
		setBtnStatus();
	}
	/**
	 * 初始化工具栏和标题栏
	 */
	private void setTitle() {
		if(bundletype.equals(Global.QUERY_FUND_DRWT)){
			sc.currentSite("wtjy_cwjj_drwt");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金当日委托");
			setTitle("当日委托");
			String[] toolbarNames = {
					Global.TOOLBAR_DETAIL, 
					Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,
					Global.TOOLBAR_CANCEL,
					Global.TOOLBAR_REFRESH
			};
			initToolBar(toolbarNames, Global.BAR_TAG);
			//setToolBar(1, false, R.color.zr_newlightgray);
		}else if (bundletype.equals(Global.QUERY_FUND_CD)){
			sc.currentSite("wtjy_cwjj_cd");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金撤单");
			setTitle("基金撤单");
			String[] toolbarNames = {Global.TOOLBAR_CANCEL,
					Global.TOOLBAR_DETAIL, 
					Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,
					Global.TOOLBAR_REFRESH
			};
			initToolBar(toolbarNames, Global.BAR_TAG);
			//setToolBar(2, false, R.color.zr_newlightgray);
		}
		super.enabledToolBarfalse();
	}
	
	/**
	 * 格式化数据
	 */
	@Override
	protected JSONObject formatJSONObject(JSONObject jsonObj) throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		for (int i =0 ,size=colsIndex.length ; i<size ;i++){
			if (i == 0){
				color =GlobalColor.colorStockName;
				String ofname = new TradeUtil().getFundCodeName(jsonObj.getString("FID_JJDM"), TodayTrust.this);
				formatJsonObj.put(colsIndex[i], ofname +"|" +color);
			}else if (i == 5){
				color = GlobalColor.colorPriceEqual;
				String fundcompanyName = new TradeUtil().getFundCompanyName(jsonObj.getString(colsIndex[i-1]), TodayTrust.this);
				formatJsonObj.put(colsIndex[i], fundcompanyName +"|" +color);
			}else if (i ==9){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.dealFundTrdid(Integer.parseInt("24"+jsonObj.getString(colsIndex[i])) ) +"|" +color);
			}else if (i ==10){
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], TradeUtil.dealFundSBJG(Integer.parseInt(jsonObj.getString(colsIndex[i])) ) +"|" +color);
			}else{
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) +"|" +color);
			}
		}
		formatJsonObj.put("SBJG", jsonObj.getString("FID_SBJG"));
		return formatJsonObj ;
    	
	}
	@Override
	protected void setSelectedRow(int tag) {
		super.setSelectedRow(tag);
		try {
			JSONObject obj = allRecords.getJSONObject(currentSelectedId);
			if(bundletype.equals(Global.QUERY_FUND_DRWT)){
				if("-1".equals(obj.get("SBJG"))) {
					setToolBar(3, false, R.color.zr_dimgray);
				}else{
					setToolBar(3, true, R.color.zr_white);
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
		}
	}
	/**
	 * 详细事件
	 */
	protected void displayDetails() {
		if(allRecords.length() ==0){
			return ;
		}
		forwardDetails(TodayTrust.this);
	}
	/**
	 * 撤单操作
	 * @throws JSONException 
	 */
	protected void withdraw()  {
		if(allRecords.length()>0){
			try{
				final JSONObject jsonObj = allRecords.getJSONObject(currentSelectedId);
				StringBuffer msg = new StringBuffer();
				String mmfx = jsonObj.getString("FID_YWDM"); mmfx = mmfx.substring(0,mmfx.indexOf("|"));
				String jjdm = jsonObj.getString("FID_JJDM"); jjdm = jjdm.substring(0,jjdm.indexOf("|"));
				String jjmc = jsonObj.getString("fundcode_name"); jjmc = jjmc.substring(0,jjmc.indexOf("|"));
				String wtbh = jsonObj.getString("FID_WTH"); wtbh = wtbh.substring(0,wtbh.indexOf("|"));
				msg.append("操作类别:" + "基金撤单" +"\n");
				msg.append("买卖方向:" + mmfx + "\n");
				msg.append("基金代码:" + jjdm +"\n");
				msg.append("基金名称:" + jjmc +"\n");
				msg.append("委托编号:" + wtbh +"\n");
				new AlertDialog.Builder(TodayTrust.this)
				.setTitle("委托提示")
				.setMessage(msg)
				.setPositiveButton("确定", 
						new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						CharSequence title = "正在与服务器通讯握手...";
						CharSequence message = "正在往服务器提交数据...";
						ProgressDialog progressDialog = ProgressDialog.show(TodayTrust.this, title, message ,true);
						try{
							JSONObject jsonObj = allRecords.getJSONObject(currentSelectedId);
							String sno = jsonObj.getString("FID_WTH");
							JSONObject chedanData=FundService.fundCancel(sno);
							String result =TradeUtil.checkResult(chedanData);
							if (result != null) {
								if (result.equals("-1"))
									Toast.makeText(TodayTrust.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								else
									toast(result);
								progressDialog.dismiss();
								return;
							}else{
								JSONArray jarr = (JSONArray) chedanData.getJSONArray("item");
								toast(jarr.getJSONObject(0).getString("FID_MESSAGE")+",流水号："+jarr.getJSONObject(0).getString("FID_LSH"));
								btnTag=4;
								init(1);
							}
						}catch(Exception e ){
							e.printStackTrace();
							progressDialog.dismiss();
						}
						progressDialog.dismiss();
					}
				}).setNegativeButton("取消", new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
				}).show();
				
			}catch(Exception  e ){
				e.printStackTrace();
			}
		}
		
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
    @Override
	protected void toolBarClick(int tag, View v) {
		btnTag = tag;
		if(bundletype.equals(Global.QUERY_FUND_DRWT)){
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
				withdraw();
				break ;
			case 4:
				setToolBar();
				break;
			default:
				cancelThread();
				break;
		 }
		}else if (bundletype.equals(Global.QUERY_FUND_CD)){
			switch(tag) {
			case 0:
				withdraw();
				break;
			case 1: 
				displayDetails();
				break;
			case 2:
				super.onPageUp();
				break;
			case 3:
				super.onPageDown();
				break ;
			case 4:
				setToolBar();
				break;
			default:
				cancelThread();
				break;
		 }
		}
		 
	}
    protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		setToolBar();
	}
	protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	private void setBtnStatus(){
		//Log.i("<<<<<<<<<<<<<<totalRecordCount>>>>>>>>>>>>>", String.valueOf(totalRecordCount));
		if (totalRecordCount == 0) {
			setToolBar(0, false, R.color.zr_dimgray);
			setToolBar(1, false, R.color.zr_dimgray);
			setToolBar(2, false, R.color.zr_dimgray);
			setToolBar(3, false, R.color.zr_dimgray);
		}else {
			if(bundletype.equals(Global.QUERY_FUND_DRWT)){
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
			}else if (bundletype.equals(Global.QUERY_FUND_CD)){
				setToolBar(0, true, R.color.zr_white);
				setToolBar(1, true, R.color.zr_white);
				if(endRowId >= totalRecordCount-1){
					setToolBar(3, false, R.color.zr_dimgray);
				}else {
					setToolBar(3, true, R.color.zr_white);
				}
				if(currentPageId == 0){
					setToolBar(2, false, R.color.zr_dimgray);
				}else {
					setToolBar(2, true, R.color.zr_white);
				}
			}
		}
	}
    public class FormatJsonTask extends AsyncTask<JSONObject, Void, Void> {
        @Override
    	protected Void doInBackground(JSONObject... jsonObj) {
			try {
				JSONObject formatJsonObj = new JSONObject();
				int color = GlobalColor.colorStockName;
				for (int i =0 ,size=colsIndex.length ; i<size ;i++){
					if (i == 0){
						color =GlobalColor.colorStockName;
						formatJsonObj.put(colsIndex[i], jsonObj[0].getString(colsIndex[i]) + "|" +color);
					}else if (i ==1){
						color = GlobalColor.colorPriceEqual;
						formatJsonObj.put(colsIndex[i], jsonObj[0].getString(colsIndex[i]) +"|" +color);
					}else if (i ==3){
						color = GlobalColor.colorPriceEqual;
						String fundcompanyName = new TradeUtil().getFundCompanyName(jsonObj[0].getString(colsIndex[i-1]), TodayTrust.this);
						formatJsonObj.put(colsIndex[i], fundcompanyName +"|" +color);
					}else if (i == 5){
						color = GlobalColor.colorPriceEqual;
						String ofname = new TradeUtil().getFundCodeName(jsonObj[0].getString(colsIndex[i-1]), TodayTrust.this);
						formatJsonObj.put(colsIndex[i], ofname +"|" +color);
					}else if (i ==9){
						color = GlobalColor.colorPriceEqual;
						formatJsonObj.put(colsIndex[i], TradeUtil.dealFundTrdid(Integer.parseInt("24"+jsonObj[0].getString(colsIndex[i])) ) +"|" +color);
					}else if (i ==10){
						color = GlobalColor.colorPriceEqual;
						formatJsonObj.put(colsIndex[i], TradeUtil.dealFundSBJG(Integer.parseInt(jsonObj[0].getString(colsIndex[i])) ) +"|" +color);
					}else{
						color = GlobalColor.colorPriceEqual;
						formatJsonObj.put(colsIndex[i], jsonObj[0].getString(colsIndex[i]) +"|" +color);
					}
				}
				allRecords.put(formatJsonObj);
			} catch (Exception e) {
				Log.e(DEBUG_TAG, e.toString());
				hiddenProgressToolBar();
			}
			return null;
    	}
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
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
