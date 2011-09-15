/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)AutoLoadAllStock.java 上午11:30:01 2011-3-28
 */
package com.cssweb.android.service;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.connect.ConnService;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class AutoLoadAllStock extends Service {
	private String jsonObject = null;
	private boolean isStop=false;
	
	public void onCreate(){
		Log.i("TAG","Services onCreate");
		super.onCreate();
	}
	public void onStart(Intent intent,int startId){
		Log.i("TAG","Services onStart");
		super.onStart(intent, startId);
		new Thread(){//新建线程，每隔1秒发送一次广播，同时把i放进intent传出
			public void run(){
				while(!isStop){
					int end = Integer.parseInt(DateTool.getLoadAllStockEndTime());
					int star = Integer.parseInt(DateTool.getLoadAllStockStarTime());
					int now = Integer.parseInt(DateTool.getLocalCurrentDate());
					Log.i("==BroadcastReceiver==", star+">>>>>>"+now+">>>>>>" + end + ">>>>>>>");
					if(now<=end&&now>=star) {
						if(jsonObject==null)
							jsonObject = CssIniFile.loadStockData(AutoLoadAllStock.this, CssIniFile.GetFileName(CssIniFile.UserStockFile));
						if(jsonObject!=null) {
							try {
								JSONObject localData = new JSONObject(jsonObject);
								String locMd5code = localData.getString("md5code");
								JSONObject jMD5 = ConnService.getStockFileMD5();
								if(Utils.isHttpStatus(jMD5)) {
									String serMd5code = jMD5.getJSONObject("data").getString("allstock");
									Log.i("==BroadcastReceiver==", "9:10分之后取MD5码比较:"+locMd5code+">>>>>>" +serMd5code);
									Log.i("==BroadcastReceiver==", "9:10分之后取MD5码比较结果:"+locMd5code.equals(serMd5code));
									if(!locMd5code.equals(serMd5code)) {
									//if(locMd5code.equals(serMd5code)) {//test
										//CssIniFile.DeletFilePath(CssIniFile.UserStockFile);
										JSONObject quoteData = ConnService.getAllStock();
										StockInfo.allStock = quoteData;
										StockInfo.clearData();
										StockInfo.initAllStock(quoteData);
										String tmp = quoteData.toString();
										CssIniFile.saveAllStockData(AutoLoadAllStock.this, CssIniFile.UserStockFile, tmp);
										jsonObject = tmp;
										//ActivityUtil.IS_SAVE_LASTEST_STOCK = true;
									}
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
					try {
						sleep(1000*60);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}.start();
 
	}
	@Override
	public void onDestroy() {
		Log.i("TAG","Services onDestory");
		isStop=true;
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}
}
