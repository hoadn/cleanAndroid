package com.cssweb.android.trade.fund;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.content.Intent;
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
 * 基金份额
 * @author hoho
 *
 */
public class FundPortio extends TradeQueryBase {
		private String DEBUG_TAG="FundPortio";
		private int btnTag = -1;
		private Thread thread = null;
		@Override
		public void onCreate(Bundle paramBundle) {
			super.onCreate(paramBundle);
			
			setTitle("基金份额查询");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金份额查询");
			//初始化工具栏
			String[] toolbarname = {Global.TOOLBAR_DETAIL, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};		
			initToolBar(toolbarname, Global.BAR_TAG);
			super.enabledToolBarfalse();
			colsName = getResources().getStringArray(R.array.fundportio_colsname);
			colsIndex= getResources().getStringArray(R.array.fundportio_colsindex);
			digitColsIndex = new HashSet<Integer>();
			digitColsIndex.add(2);
			digitColsIndex.add(3);
			digitColsIndex.add(4);
			digitColsIndex.add(5);
			digitColsIndex.add(6);
			handlerData();
		}
		
		/**
		 * 请求后台数据
		 */
		protected void init(final int type) {
			this.type = type;
			try{
				r = new Runnable(){
					public void run() {
						if(btnTag == -1 || btnTag == 3){
							if(type == 1){
					    		try {
					    			quoteData= FundService.getFundPosition();
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
		@Override
		protected void handlerData() {
			if(quoteData == null){
				fillNullCurrentPageContent(this);
				hiddenProgressToolBar();
				if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
					Toast.makeText(FundPortio.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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
				}else if (i == 2){
					color = GlobalColor.colorPriceEqual;
					String fundNav = new TradeUtil().getFundCodeNav(jsonObj.getString("FID_JJDM"),FundPortio.this);
					formatJsonObj.put(colsIndex[i], fundNav +"|" +color);
				}else if (i ==7){
					color = GlobalColor.colorPriceEqual;
					formatJsonObj.put(colsIndex[i], TradeUtil.formateShareClass(Integer.parseInt(jsonObj.getString(colsIndex[i])) ) +"|" +color);
				}else if (i ==8){
					color = GlobalColor.colorPriceEqual;
					formatJsonObj.put(colsIndex[i], TradeUtil.formateDividend(Integer.parseInt(jsonObj.getString(colsIndex[i])) ) +"|" +color);
				}
				else {
					color = GlobalColor.colorPriceEqual;
					formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) +"|" +color);
				}
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
			forwardDetails(FundPortio.this);
		}
//		/**
//		 * 基金转换功能
//		 */
//		private void transfer() {
//			if(allRecords.length() ==0){
//				return ;
//			}else{
//				JSONObject jsonObj;
//				try {
//					jsonObj = allRecords.getJSONObject(currentSelectedId);
//					Intent intent = new Intent();
//					intent.setClass(FundPortio.this, FundTransfer.class);
//					String ofcode = jsonObj.getString("FID_JJDM"); ofcode = ofcode.substring(0,ofcode.indexOf("|"));
//					intent.putExtra("ofcode", ofcode);
//					startActivity(intent);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		/**
//		 * 基金赎回功能
//		 */
//		private void sale() {
//			if(allRecords.length() ==0){
//				return ;
//			}else{
//				JSONObject jsonObj;
//				try {
//					jsonObj = allRecords.getJSONObject(currentSelectedId);
//					Intent intent = new Intent();					
//					intent.setClass(FundPortio.this, FundTrading.class);
//					intent.putExtra("type", 2);
//					String ofcode = jsonObj.getString("FID_JJDM"); ofcode = ofcode.substring(0,ofcode.indexOf("|"));
//					intent.putExtra("ofcode", ofcode);
//					startActivity(intent);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
//		
//		/**
//		 * 基金分红设置功能
//		 */
//		private void dividend() {
//			if(allRecords.length() ==0){
//				return ;
//			}else{
//				JSONObject jsonObj;
//				try {
//					jsonObj = allRecords.getJSONObject(currentSelectedId);
//					Intent intent = new Intent();
//					intent.setClass(FundPortio.this, FundMelonSet.class);
//					String ofcode = jsonObj.getString("FID_JJDM"); ofcode = ofcode.substring(0,ofcode.indexOf("|"));
//					intent.putExtra("ofcode", ofcode);
//					startActivity(intent);
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//			}
//		}
		
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
}
