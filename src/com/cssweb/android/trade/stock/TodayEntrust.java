package com.cssweb.android.trade.stock;

import java.util.HashSet;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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
 * 今日委托查询
 * 
 * @author wangsheng
 *
 */
public class TodayEntrust extends TradeQueryBase {
	private static final String DEBUG_TAG = "TodayEntrust";
	private int btnTag = -1;
	private Thread thread = null;
	// 声明进度条对话框
	private ProgressDialog myDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setTitle("当日委托");
		//SetRightTitle("刷新", 2);
		initTitle(R.drawable.njzq_title_left_back, 0, "当日委托");
		
		String[] toolbarname = {Global.TOOLBAR_DETAIL,Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_CANCEL, Global.TOOLBAR_REFRESH};		
		initToolBar(toolbarname, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_drwt_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_drwt_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(5);
		digitColsIndex.add(6);
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
				if(btnTag == -1 || btnTag == 4){
					if(type == 1){
						try {
							StringBuffer sb = new StringBuffer();
							sb.append("FID_SORTTYPE=1");
							quoteData = ConnPool.sendReq("GET_TODAY_ENTRUST", "304103", sb.toString());
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
	}
	
	@Override
	protected void handlerData() {
		if(quoteData == null){
			fillNullCurrentPageContent(this);
			hiddenProgressToolBar();
			if (type == 1 || btnTag == 4) {// 进去页面请求 或 刷新
				Toast.makeText(TodayEntrust.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
				setToolBar(0, false, R.color.zr_dimgray);
				setToolBar(1, false, R.color.zr_dimgray);
				setToolBar(2, false, R.color.zr_dimgray);
				setToolBar(3, false, R.color.zr_dimgray);
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
		String mmbz = TradeUtil.getFlagName(jsonObj.getInt("FID_WTLB"),jsonObj.getString("FID_CXBZ"));
		if ("买入".equals(mmbz)) {
			color = GlobalColor.colorpriceUp;
		}else if ("卖出".equals(mmbz)) {
			color = getResources().getColor(R.color.zr_sail);
		}
		for(int i=0; i<colsIndex.length; i++) {
//			if(i==0) {
//				color = GlobalColor.colorStockName;
//				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
//				//当日委托 中的 买卖标志
//			}else 
			if(i==4){
//				color = GlobalColor.colorPriceEqual;
//				String mmbz = TradeUtil.getFlagName(jsonObj.getInt("FID_WTLB"),jsonObj.getString("FID_CXBZ"));
//				if ("买入".equals(mmbz)) {
//					color = GlobalColor.colorpriceUp;
//				}else if ("卖出".equals(mmbz)) {
//					color = GlobalColor.colorPriceDown;
//				}
				formatJsonObj.put(colsIndex[i],mmbz+"|"+color);
			//委托价格
			}else if(i == 5){
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3)+"|"+color);
			//成交价格
			}else if(i == 7){
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]),3)+"|"+color);
			}else if(i == 11)
			{
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.dealOrderType(jsonObj.getInt(colsIndex[i]))+"|"+color);
			}else {
				//color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) + "|" + color);
			}
		}
		formatJsonObj.put("FID_SBJG", jsonObj.getString("FID_SBJG"));
		formatJsonObj.put("FID_JYS", jsonObj.getString("FID_JYS"));
		return formatJsonObj; 
	}
	@Override
	protected void setSelectedRow(int tag) {
		super.setSelectedRow(tag);
		try {
			if (totalRecordCount!=0) {
				JSONObject obj = allRecords.getJSONObject(currentSelectedId);
				if("0".equals(obj.get("FID_SBJG")) || "2".equals(obj.get("FID_SBJG")) || "5".equals(obj.get("FID_SBJG"))) {
					setToolBar(3, true, R.color.zr_white);
				}else{
					setToolBar(3, false, R.color.zr_dimgray);
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
		}
	}

	/**
	 * 处理详细事件
	 */
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(TodayEntrust.this);
	}
	//撤单
	public void withdraw() {
		if(allRecords.length() > 0){
			try {
				final JSONObject jsonObj = allRecords.getJSONObject(currentSelectedId);
				StringBuffer msg = new StringBuffer();
				String strTemp = jsonObj.getString("FID_ZQDM");
				msg.append("证券代码：" + strTemp.substring(0, strTemp.indexOf("|")) +"\n");
				strTemp = jsonObj.getString("FID_ZQMC");
				msg.append("证券名称：" + strTemp.substring(0, strTemp.indexOf("|")) +"\n");
				strTemp = jsonObj.getString("FID_WTJG");
				msg.append("委托价格：" + strTemp.substring(0, strTemp.indexOf("|")) +"\n");
				new AlertDialog.Builder(TodayEntrust.this)
				.setTitle("委托提示")
				.setMessage(msg.toString())
				.setPositiveButton("确定", 
						new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							CharSequence title = "正在与服务器通讯握手...";
							CharSequence message = "正在往服务器提交数据...";
							myDialog = ProgressDialog.show(TodayEntrust.this, title, message, true);
							try {
								StringBuffer sb = new StringBuffer();
								
								sb.append("FID_JYS=" + jsonObj.getString("FID_JYS") + TradeUtil.SPLIT);
								sb.append("FID_GDH=" + jsonObj.getString("FID_GDH").substring(0, jsonObj.getString("FID_GDH").indexOf("|")) + TradeUtil.SPLIT);
								sb.append("FID_WTH=" + jsonObj.getString("FID_WTH").substring(0, jsonObj.getString("FID_WTH").indexOf("|")) + TradeUtil.SPLIT);
								
								JSONObject quoteData = ConnPool.sendReq("STOCK_CANCEL", "204502", sb.toString());
								String res = TradeUtil.checkResult(quoteData);
								if (res != null) {
									toast("撤单失败：" + res);
								} else {
//									JSONArray jArr = (JSONArray) quoteData.getJSONArray("item");
//									JSONObject j = (JSONObject) jArr.get(0);
									
									toast("撤单委托已提交，是否成功请查看当日委托！");
									//toast(j.getString("msgok"));
									try {
										Thread.sleep(1000*2);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
									btnTag = 5;
									setToolBar();
								}
							} catch (JSONException e) {
								e.printStackTrace();
								CssLog.e(DEBUG_TAG, e.toString());
							}
							myDialog.dismiss();
						}
					}
				)
				.setNegativeButton("取消",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							//
					    }
					}
			    ).show();
			} catch (JSONException e) {
				Log.e(DEBUG_TAG, e.toString());
			}
			
		}
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
				withdraw();
				break ;
			case 4:
				setToolBar();
				break;
			default:
				cancelThread();
				break;
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
		if (totalRecordCount == 0) {
			setToolBar(0, false, R.color.zr_dimgray);
			setToolBar(1, false, R.color.zr_dimgray);
			setToolBar(2, false, R.color.zr_dimgray);
			setToolBar(3, false, R.color.zr_dimgray);
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
