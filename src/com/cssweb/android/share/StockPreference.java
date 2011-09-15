package com.cssweb.android.share;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.Global;
import com.cssweb.android.util.ActivityUtil;

public class StockPreference {
	public static String shareStock(String exchange, String stkcode, String stkname, Context context) {
		// 取得活动的preferences对象.
		if(exchange==null||exchange.equals("")) 
			return null;
		String mystocks = ActivityUtil.getPreference(context, "mystock", "");
		String newStock = exchange.toLowerCase() + stkcode + ",";
		String res = "";
		String[] temp = mystocks.split(",");
		if(temp.length>=Global.P_STOCK_NUMS) 
			res = "自选股最大个数不能超过【"+Global.P_STOCK_NUMS+"】个！";
		else if(!mystocks.contains(newStock)) {
			if("0".equals(mystocks)) 
				mystocks = "";
			StringBuffer sb = new StringBuffer();
			sb.append(mystocks);
			sb.append(exchange);
			sb.append(stkcode);
			sb.append(",");
			ActivityUtil.savePreference(context, "mystock", sb.toString());
			res = stkcode+stkname+"成功加入自选股中！";
		}
		else
			res = stkcode+stkname+"已经在自选股中！";
		
		return res;
	}
	
	public static String alarmStock(String exchange, String stockcode, String stockname, String str, Context context) {
		// 取得活动的preferences对象.
		if(exchange==null||exchange.equals("")) 
			return null;
		String alarmstocks = ActivityUtil.getPreference(context, "alarmstock", "");
		String newStock = exchange.toLowerCase() + stockcode + ",";
		String res = "";
		String[] temp = alarmstocks.split("\\|");
		if(temp.length>Global.P_STOCK_NUMS) 
			res = "预警股票最大个数不能超过【"+Global.A_STOCK_NUMS+"】个！";
		else if(!alarmstocks.contains(newStock)) {
			StringBuffer sb = new StringBuffer();
			sb.append(alarmstocks);
			sb.append(newStock);
			sb.append(str);
			sb.append("|");
			ActivityUtil.savePreference(context, "alarmstock", sb.toString());
			res = "预警股票【"+stockname+"】保存成功！";
		}
		else {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<temp.length; i++) {
				if(!temp[i].contains(newStock)) {
					sb.append(temp[i]);
					sb.append("|");
				}
			}
			sb.append(newStock);
			sb.append(str);
			sb.append("|");
			ActivityUtil.savePreference(context, "alarmstock", sb.toString());
			res = "预警股票【"+stockname+"】保存成功！";
		}
		return res;
	}
	
	public static String delAlarmStock(String exchange, String stockcode, String stockname, Context context) {
		// 取得活动的preferences对象.
		if(exchange==null||exchange.equals("")) 
			return null;
		String alarmstocks = ActivityUtil.getPreference(context, "alarmstock", "");
		String newStock = exchange.toLowerCase() + stockcode + ",";
		String res = "";
		String[] temp = alarmstocks.split("\\|");
		if(alarmstocks.contains(newStock)) {
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<temp.length; i++) {
				if(!temp[i].contains(newStock)) {
					sb.append(temp[i]);
					sb.append("|");
				}
			}
			ActivityUtil.savePreference(context, "alarmstock", sb.toString());
			res = "预警股票【"+stockname+"】移除成功！";
		}
		return res;
	}
	
	public static String getAlarmStock(Context context) {
		String stkcode = null;
		// 取得活动的preferences对象.

		String alarmstocks = ActivityUtil.getPreference(context, "alarmstock", "");

		if (alarmstocks != null && !alarmstocks.equals(""))
			stkcode = alarmstocks.substring(0, alarmstocks.length() - 1);
		return stkcode;
	}
	
	public static boolean stockIsExist(String exchange, String stkcode, Context context) {
		// 取得活动的preferences对象.
		String mystocks = ActivityUtil.getPreference(context, "mystock", "");
		String newStock = exchange.toLowerCase() + stkcode + ",";
		if(mystocks.contains(newStock)) {
			return true;
		}
		return false;
	}
	
	public static String delStock(String exchange, String stkcode, String stkname, Context context) {
		// 取得活动的preferences对象.
		String mystocks = ActivityUtil.getPreference(context, "mystock", "");
		String delStock = exchange.toLowerCase() + stkcode + ",";
		String res = "";
		if(mystocks.contains(delStock)) {
			mystocks = mystocks.replace(delStock, "");
			ActivityUtil.savePreference(context, "mystock", mystocks);
			String temp = ActivityUtil.getPreference(context, "mystock", "");
			if(temp==null||temp.equals(""))
				ActivityUtil.savePreference(context, "mystock", "0");
			res = stkcode+stkname+" 删除成功！";
		}
		else {
			res = stkcode+stkname+" 不存在！";
		}
		return res;
	}
	
	public static String clearStock(Context context) {
		String res = "清除自选成功！";
		String mystocks = ActivityUtil.getPreference(context, "mystock", "");
		if(mystocks==null||mystocks.equals("")) 
			return null;
		else 
			ActivityUtil.savePreference(context, "mystock", "0");
		return res;
	}
	
	public static String getStock(Context context) {
		String stkcode = null;
		// 取得活动的preferences对象.

		String mystocks = ActivityUtil.getPreference(context, "mystock", "");
		//Log.i("==================", ">>>>>>>>>>>>>>>>>>"+mystocks);
		if (mystocks != null && !mystocks.equals("")) {
			if("0".equals(mystocks))
				stkcode=null;
			else 
				stkcode = mystocks.substring(0, mystocks.length() - 1);
		}
		else {
			shareStock("sh", "000001", null, context);
			shareStock("sz", "399001", null, context);
			stkcode = "sh000001,sz399001";
		}
		return stkcode;
	}
	
	public static JSONObject getAllStock(Context context, int type) {
		JSONObject j = null;
		String jsonObject = null;
		//读数据
		try {
			if(type==Global.QUOTE_HKSTOCK)
				jsonObject = CssIniFile.loadStockData(context, CssIniFile.GetFileName(CssIniFile.HKStockFile));
			else 
				jsonObject = CssIniFile.loadStockData(context, CssIniFile.GetFileName(CssIniFile.UserStockFile));
			j = new JSONObject(jsonObject);
		} catch (JSONException e) {
		}
		return j;
	}
	
	/**
	 * 保存服务密码登陆账号
	 * @param context
	 * @param fundId
	 */
	public static void saveCustNo(Context context, String fundId){
		String myFundIds = ActivityUtil.getCustNoPreference(context, "myCustNos", "");
		String newFundId = fundId + ",";
		if(!myFundIds.contains(newFundId)){
			StringBuffer sb = new StringBuffer();
			sb.append(myFundIds);
			sb.append(newFundId);
			ActivityUtil.saveCustNoPreference(context, "myCustNos", sb.toString());
		}
	}
	
	/**
	 * 获取服务密码用户的账号
	 */
	public static String getCustNo(Context context){
		String fundId = null;
		String myFundIds = ActivityUtil.getCustNoPreference(context, "myCustNos", "");
		if(myFundIds != null && !myFundIds.equals(""))
			fundId = myFundIds.substring(0, myFundIds.length() - 1);
		return fundId;
	}
	
	/**
	 * 保存交易用户登录时的账号
	 */
	public static void saveFundId(Context context, String fundId){
		String myFundIds = ActivityUtil.getPreference(context, "myFundIds", "");
		String newFundId = fundId + ",";
		if(!myFundIds.contains(newFundId)){
			StringBuffer sb = new StringBuffer();
			sb.append(myFundIds);
			sb.append(newFundId);
			ActivityUtil.savePreference(context, "myFundIds", sb.toString());
		}
	}
	
	/**
	 * 获取交易用户的账号
	 */
	public static String getFundId(Context context){
		String fundId = null;
		String myFundIds = ActivityUtil.getPreference(context, "myFundIds", "");
		if(myFundIds != null && !myFundIds.equals(""))
			fundId = myFundIds.substring(0, myFundIds.length() - 1);
		return fundId;
	}
	
	/**
	 * 设置服务密码登录时的首选账号
	 * @param context
	 * @param fund
	 */
	public static void setPreferredCustNo(Context context, String fundInfo){
		ActivityUtil.saveCustNoPreference(context, "preferredFund", fundInfo);
	}
	
	/**
	 * 设置交易登录时的首选账号
	 * @param context
	 * @param fund
	 */
	public static void setPreferredFund(Context context, String fundInfo){
		ActivityUtil.savePreference(context, "preferredFund", fundInfo);
	}
	
	/**
	 * 获得服务密码登录时的首选账号
	 * @param context
	 */
	public static String getPreferredCustNo(Context context){
		return ActivityUtil.getCustNoPreference(context, "preferredFund", "");
	}
	
	/**
	 * 获得交易登录时的首选账号
	 * @param context
	 */
	public static String getPreferredFund(Context context){
		return ActivityUtil.getPreference(context, "preferredFund", "");
	}
	
	/**
	 * 保存体验用户登录时的卡号
	 */
	public static void saveTyyhId(Context context, String tyyhId){
		String myTyyhIds = ActivityUtil.getPreference(context, "myTyyhIds", "");
		String newTyyhId = tyyhId + ",";
		if(!myTyyhIds.contains(newTyyhId)){
			StringBuffer sb = new StringBuffer();
			sb.append(myTyyhIds);
			sb.append(newTyyhId);
			ActivityUtil.savePreference(context, "myTyyhIds", sb.toString());
		}
	}
	/**
	 * 获取体验用户卡号
	 */
	public static String getTyyhId(Context context){
		String tyyhId = null;
		String myTyyhIds = ActivityUtil.getPreference(context, "myTyyhIds", "");
		if(myTyyhIds != null && !myTyyhIds.equals(""))
			tyyhId = myTyyhIds.substring(0, myTyyhIds.length() - 1);
		return tyyhId;
	}
	/**
	 * 设置体验登录时的首选账号
	 * @param context
	 * @param tyyh
	 */
	public static void setPreferredTyyh(Context context, String TyyhInfo){
		ActivityUtil.savePreference(context, "preferredTyyh", TyyhInfo);
	}
	
	/**
	 * 获得体验登录时的首选账号
	 * @param context
	 */
	public static String getPreferredTyyh(Context context){
		return ActivityUtil.getPreference(context, "preferredTyyh", "");
	}
}
