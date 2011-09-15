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
 * 股票撤单(	410413)
 * 
 * 委托日期 orderdate
 * 资金账户 fundid
 * 委托序号 ordersno 委托返回的
 * 
 * 返回 msgok 成功消息
 * 
 * @author wangsheng
 *
 */
public class StockCancel extends TradeQueryBase {
	private static final String DEBUG_TAG = "StockCancel";
	private int btnTag = -1;
	private Thread thread = null;
	// 声明进度条对话框
	private ProgressDialog myDialog = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "撤单" );
		
		String[] toolbarNames = {
				Global.TOOLBAR_DETAIL,Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,
				Global.TOOLBAR_CANCEL, Global.TOOLBAR_ALL_CANCEL,Global.TOOLBAR_REFRESH};
		initToolBar(toolbarNames, Global.BAR_TAG);
		super.enabledToolBarfalse();
		
		colsName = getResources().getStringArray(R.array.zr_trade_stock_query_cancel_name);
		colsIndex = getResources().getStringArray(R.array.zr_trade_stock_query_cancel_index);
		digitColsIndex = new HashSet<Integer>();
		digitColsIndex.add(6);
		digitColsIndex.add(7);
		digitColsIndex.add(9);
		digitColsIndex.add(10);
		digitColsIndex.add(11);
		
		handlerData();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	@Override
	protected void init(final int type) {
		mHandler.removeCallbacks(r);
		this.type = type;
		r = new Runnable() {			
			public void run() {
				if(btnTag == -1 || btnTag == 5){
					if(type == 1){
						try {
							//查询今日委托
							StringBuffer sb = new StringBuffer();
							sb.append("FID_SORTTYPE=1");
							quoteData = ConnPool.sendReq("GET_TODAY_ENTRUST", "304103", sb.toString());
							String res = TradeUtil.checkResult(quoteData);
							if(res==null){
								allRecords = new JSONArray();
								JSONArray array = (JSONArray)quoteData.getJSONArray("item");
								array = filterResult(array);
								for(int i=0,size=array.length(); i<size; i++){
									JSONObject jsonObj = (JSONObject)array.get(i);
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
			if (type == 1 || btnTag == 5) {// 进去页面请求 或 刷新
				Toast.makeText(StockCancel.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
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
			
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
			super.enabledToolBarfalse();
			hiddenProgressToolBar();
			return;
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
//				formatJsonObj.put("operator", "操作"+"|"+color);
//				//存入 交易所信息
//				formatJsonObj.put("FID_JYS", jsonObj.getString("FID_JYS"));
			}else if(i==4)
			{
				color = GlobalColor.colorPriceEqual;
				
				formatJsonObj.put(colsIndex[i],TradeUtil.getFlagName(jsonObj.getInt(colsIndex[i]),"")+"|"+color);
			//委托价格
			}else if(i==5)
			{
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3)+"|"+color);
			//成交价格
			}else if(i==7)
			{
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i],TradeUtil.formatNum(jsonObj.getString(colsIndex[i]), 3)+"|"+color);
			}else{
				color = GlobalColor.colorPriceEqual;
				formatJsonObj.put(colsIndex[i], jsonObj.getString(colsIndex[i]) +"|" +color);
			}
		}
		formatJsonObj.put("FID_JYS", jsonObj.getString("FID_JYS"));
		return formatJsonObj;
	}
	
	/**
	 * 根据委托状态过滤结果集
	 * @param array
	 * @return
	 */
	private JSONArray filterResult(JSONArray array) {
		JSONArray result = new JSONArray();
		try {
			for(int i=0,size=array.length()-1; i<size; i++){
				JSONObject obj = array.getJSONObject(i);
				
				if("0".equals(obj.get("FID_SBJG")) || "2".equals(obj.get("FID_SBJG")) || "5".equals(obj.get("FID_SBJG"))) {
					result.put(obj);	
				}
			}
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
		}
		return result;
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
				new AlertDialog.Builder(StockCancel.this)
				.setTitle("委托提示")
				.setMessage(msg.toString())
				.setPositiveButton("确定", 
						new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							CharSequence title = "正在与服务器通讯握手...";
							CharSequence message = "正在往服务器提交数据...";
							myDialog = ProgressDialog.show(StockCancel.this, title, message, true);
							try {
								StringBuffer sb = new StringBuffer();
								
								sb.append("FID_JYS=" + jsonObj.getString("FID_JYS") + TradeUtil.SPLIT);
								sb.append("FID_GDH=" + jsonObj.getString("FID_GDH").substring(0, jsonObj.getString("FID_GDH").indexOf("|")) + TradeUtil.SPLIT);
								sb.append("FID_WTH=" + jsonObj.getString("FID_WTH").substring(0, jsonObj.getString("FID_WTH").indexOf("|")) + TradeUtil.SPLIT);
								
								JSONObject quoteData = ConnPool.sendReq("STOCK_CANCEL", "204502", sb.toString());
								String res = TradeUtil.checkResult(quoteData);
								if(res!=null) {
					    			if (res.equals("-1")){
										Toast.makeText(StockCancel.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
									}else {
										Toast.makeText(StockCancel.this, res, Toast.LENGTH_LONG).show();
									}
					    			myDialog.dismiss();
					    			return;
					    		}else{
					    			toast("撤单委托已提交，是否成功请查看当日委托！");
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
	//全撤 204511
	private void allwithdraw(){
		if(allRecords.length() > 0){
			StringBuffer msg = new StringBuffer();
			msg.append(" 操作类别：全部撤单\n");
			msg.append(" (确认要全部撤单？)\n");
			new AlertDialog.Builder(StockCancel.this)
			.setTitle("委托提示")
			.setMessage(msg.toString())
			.setPositiveButton("确定", 
					new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CharSequence title = "正在与服务器通讯握手...";
						CharSequence message = "正在往服务器提交数据...";
						myDialog = ProgressDialog.show(StockCancel.this, title, message, true);
						try {
							JSONObject quoteData = ConnPool.sendReq("STOCK_ALLCANCEL", "204511","");
							String res = TradeUtil.checkResult(quoteData);
							if(res!=null) {
				    			if (res.equals("-1")){
									Toast.makeText(StockCancel.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								}else {
									Toast.makeText(StockCancel.this, res, Toast.LENGTH_LONG).show();
								}
				    			myDialog.dismiss();
				    			return;
				    		}else{
				    			toast("撤单委托已提交，是否成功请查看当日委托！");
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
		}
	}
	
	private void displayDetails() {
		if(allRecords.length() == 0)
			return;
		forwardDetails(StockCancel.this);
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
			setToolBar(3, true, R.color.zr_white);
			setToolBar(4, true, R.color.zr_white);
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
	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
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
				allwithdraw();
				break;
			case 5:
				setToolBar();
				break;
			default:
				cancelThread();
				break;
		 }
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
}
