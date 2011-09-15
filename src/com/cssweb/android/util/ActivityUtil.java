/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)ActivityUtil.java 上午08:07:55 2010-10-3
 */
package com.cssweb.android.util;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.method.PasswordTransformationMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.EditText;

import com.cssweb.android.main.SplashScreen;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.share.StockPreference;

/**
 * 公共参数设置类
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class ActivityUtil {
	public static int ALARM_RECORED = -1;
	
	public static int CURRENT_USER_LEVEL = 0;
	
	public static AlertDialog buildMainDialog(Context paramContext) {
		return null;
	}

	public static DisplayMetrics getDisplayInfo(Activity paramActivity) {
		return paramActivity.getResources().getDisplayMetrics();
	}

	public static String getPreference(Context paramContext,
			String paramString1, String paramString2) {
		return paramContext.getSharedPreferences("com.cssweb.lcdt.client", 0)
				.getString(paramString1, paramString2);
	}
	
	/**
	 * 获取服务密码账号
	 * @param paramContext
	 * @param paramString1
	 * @param paramString2
	 * @return
	 */
	public static String getCustNoPreference(Context paramContext,
			String paramString1, String paramString2) {
		return paramContext.getSharedPreferences("com.cssweb.lcdt.clientcustno", 0)
				.getString(paramString1, paramString2);
	}
	/**
	 * 服务密码保存账号
	 * @param paramContext
	 * @param paramString1
	 * @param paramString2
	 */
	public static void saveCustNoPreference(Context paramContext,
			String paramString1, String paramString2) {
		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences("com.cssweb.lcdt.clientcustno", 0).edit();
		localEditor.putString(paramString1, paramString2);
		localEditor.commit();
	}

	public static void savePreference(Context paramContext,
			String paramString1, String paramString2) {
		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences("com.cssweb.lcdt.client", 0).edit();
		localEditor.putString(paramString1, paramString2);
		localEditor.commit();
	}

	public static int getPreference(Context paramContext, String paramString1,
			int paramString2) {
		return paramContext.getSharedPreferences("com.cssweb.lcdt.client", 0)
				.getInt(paramString1, paramString2);
	}

	public static void savePreference(Context paramContext,
			String paramString1, int paramString2) {
		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences("com.cssweb.lcdt.client", 0).edit();
		localEditor.putInt(paramString1, paramString2);
		localEditor.commit();
	}

	public static void clearPreference(Context paramContext) {
		SharedPreferences.Editor localEditor = paramContext
				.getSharedPreferences("com.cssweb.lcdt.client", 0).edit();
		localEditor.clear();
		localEditor.commit();
	}

	public static void setFullscreen(Activity paramActivity) {
		paramActivity.getWindow().setFlags(1024, 1024);
	}

	public static void setNoTitle(Activity paramActivity) {
		paramActivity.requestWindowFeature(1);
	}

	public static int dipTopx(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);

	}

	public static int pxTodip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static boolean isNetworkAvailable(Context context) {
		ConnectivityManager cn = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (cn == null) {
			return false;
		}
		NetworkInfo[] netinfo = cn.getAllNetworkInfo();
		if (netinfo == null) {
			return false;
		}
		for (NetworkInfo net : netinfo) {
			if (net.isConnected()) {
				return true;
			}
		}
		return false;
	}

	public static void setPasswordType(EditText paramEditText,
			boolean paramBoolean) {
		if (!paramBoolean)
			return;
		PasswordTransformationMethod localPasswordTransformationMethod = new PasswordTransformationMethod();
		paramEditText
				.setTransformationMethod(localPasswordTransformationMethod);
	}

	public static int getAlarmRecord(Context context) {
		SharedPreferences sh = context.getSharedPreferences("alarm_record",
				Activity.MODE_PRIVATE);
		return sh.getInt("alarmRecord", -1);
	}

	public static void saveAlarmRecord(Context context) {
		SharedPreferences sh = context.getSharedPreferences("alarm_record",
				Activity.MODE_PRIVATE);
		int i = sh.getInt("alarmRecord", -1);
		Log.i(">>>>>>>>>>>>>", i + ">>>>>>>>");

		sh.edit().putInt("alarmRecord", ++i).commit();
	}

	public static void clearAlarmRecord(Context context) {
		SharedPreferences sh = context.getSharedPreferences("alarm_record",
				Activity.MODE_PRIVATE);
		sh.edit().clear().commit();
	}

	public static void clearTradeRecord() {
		TradeUser.getInstance().setUserLevel(0);
		TradeUser.getInstance().setFundid("");
		TradeUser.getInstance().setCustid("");
		TradeUser.getInstance().setUserType("");
		TradeUser.getInstance().setOrgid("");
		TradeUser.getInstance().setLoginType(0);
	}

	public static void clearPhoneNum(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"mobile", Context.MODE_PRIVATE);
		
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("mobileNum","");
		editor.remove("jhSuccess");
		editor.commit();// 提交修改
	}
	//清除所有账号
	public static void clearAllAccount(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"com.cssweb.lcdt.clientcustno", Context.MODE_PRIVATE);
		Editor editor = sharedPreferences.edit();// 获取编辑器
		editor.putString("myCustNos","");
		editor.commit();// 提交修改
		StockPreference.setPreferredCustNo(context, null);
		
		SharedPreferences sharedPreferences1 = context.getSharedPreferences(
				"com.cssweb.lcdt.client", Context.MODE_PRIVATE);
		Editor editor1 = sharedPreferences1.edit();// 获取编辑器
		editor1.putString("myFundIds","");
		editor1.commit();// 提交修改
		StockPreference.setPreferredFund(context, null);
	}
	//清除服务密码
	public static void clearServicePwd(Context context) {
		ArrayList<String> newAccounts = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String myFundIdsStr = StockPreference.getCustNo(context);
		if(null != myFundIdsStr && myFundIdsStr.indexOf("&")!=-1 ){
			String[] myFundInfosService = myFundIdsStr.split(",");
			for(int i=0, size=myFundInfosService.length; i<size; i++){
				if(myFundInfosService[i].indexOf("&")!=-1){
					String account = myFundInfosService[i].substring(0,myFundInfosService[i].indexOf("&"));
					newAccounts.add(account);
					sb.append(account + ",");
				}
			}
			ActivityUtil.saveCustNoPreference(context, "myCustNos", sb.toString());
		}
	}
	//清除交易相关
	public static void clearTradeOpenTime(Context context) {
		ActivityUtil.savePreference(context, "openFundCompanyDate", "");
		ActivityUtil.savePreference(context, "openFundInfoDate","");
		ActivityUtil.savePreference(context, "openFundAccountDate","");
		ActivityUtil.savePreference(context, "openBanksDate", "");
	}
	public static void restart(Activity mContext, int activityKind) {
		clearTradeRecord();
		ALARM_RECORED = -1;
		//android.os.Process.killProcess(android.os.Process.myPid());
		Intent intent = new Intent();   
		intent.setClass(mContext, SplashScreen.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		intent.putExtra("filetype", activityKind);
		mContext.startActivity(intent);
		mContext.finish();
    }
}
