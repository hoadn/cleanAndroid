package com.cssweb.android.service;

import org.json.JSONObject;

import android.util.Log;

import com.cssweb.android.common.Config;
import com.cssweb.android.connect.Conn;

/**
 * 行情预警服务类
 * @author hoho
 *
 */
public class QuoteWarnService {

	/**
	 * 查询行情预警数据
	 * @param aesStr
	 * @return
	 */
	public static JSONObject getQuoteWarnInfo (String aesStr){
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadHqyj);
		sb.append("hqyj/work/login.action");   //测试服务器
		sb.append("?serviceTime=");
		sb.append(aesStr);
		Log.i("getQuoteWarnInfo>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	/**
	 * 增加行情预警数据
	 * @param zqdm
	 * @param exchange
	 * @param zgcj
	 * @param zdcj
	 * @param serviceTime
	 * @return
	 */
	public static JSONObject addQuoteWarn (String zqdm , int exchange , String zgcj ,String zdcj ,String serviceTime){
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadHqyj);
		sb.append("hqyj/work/modify.action"); 
		sb.append("?zqdm="+zqdm);
		sb.append("&exchange="+exchange);
		sb.append("&zgcj="+zgcj);
		sb.append("&zdcj="+zdcj);
		sb.append("&serviceTime="+serviceTime);
		Log.i("addQuoteWarn1111111111111111:>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	/**
	 * 删除行情预警数据
	 * @param zqdm
	 * @param exchange
	 * @param serviceTime
	 * @return
	 */
	public static JSONObject delQuoteWarn(String zqdm , int exchange ,String serviceTime ){
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadHqyj);
		sb.append("hqyj/work/deleteMarketWarning.action");
		sb.append("?zqdm="+zqdm);
		sb.append("&exchange="+exchange);
		sb.append("&serviceTime="+serviceTime);
		Log.i("delQuoteWarn222222222222222222:>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
}
