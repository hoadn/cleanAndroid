/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)ZixunService.java 下午02:03:50 2011-3-14
 */
package com.cssweb.android.service;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cssweb.android.common.Config;
import com.cssweb.android.connect.Conn;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class ZixunService {
	
	/**
	 * 根据客户号和客户类型返回客户级别
	 * @param userType
	 * @param custid
	 * @return 
	 * @throws JSONException 
	 */
	public static int getUserLevel(String userType, String custid) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadZixun);
		sb.append("iphone/login/Login/getTradUserCustLevel.do?");
		sb.append("&userType=");
		sb.append(userType);
		sb.append("&clientNo=");
		sb.append(custid);
		Log.i("KKK", sb.toString()+">>>>>");
		String s = Conn.getUserLevel(sb.toString());
		Log.i("##########", s+">>>>>>>>");
		if(null !=s ){
			JSONObject j = new JSONObject(s);
			if(j!=null&&"success".equals(j.getString("cssweb_code")))
				return j.getInt("custLevel");
		}
		return 0;
	}
	
	public static String mobileMsgValidate(String mobileNum, String ino ,String serviceTime) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadZixun);
		sb.append("iphone/mobilemsgvalidate/MobileMsgValidate/sendMsgValidate.do?");
		sb.append("&mobile=");
		sb.append(mobileNum);
		sb.append("&ino=");
		sb.append(ino);
		sb.append("&serviceTime=");
		sb.append(serviceTime);

		String result = Conn.getData(sb.toString());
		if(null != result ){
			JSONObject j = new JSONObject(result);
			return j.getString("cssweb_code");
		}
		
		return null;
	}
	public static ArrayList<String> validateCode(String mobileNum, String ino ,String serviceTime,String validateCode) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadZixun);
		sb.append("iphone/mobilemsgvalidate/MobileMsgValidate/validateCode.do?");
		sb.append("&mobile=");
		sb.append(mobileNum);
		sb.append("&ino=");
		sb.append(ino);
		sb.append("&serviceTime=");
		sb.append(serviceTime);
		sb.append("&validateCode=");
		sb.append(validateCode);
		
		String result = Conn.getData(sb.toString());
		if(null != result ){
			JSONObject j = new JSONObject(result);
			
			ArrayList<String> list = new ArrayList<String>();
			list.add(j.getString("cssweb_code"));
			list.add(j.getString("cssweb_msg"));
			
			return list;
		}
		
		return null;
	}
	
	/**
	 * 判断当前版本是否是最新
	 * @param versioname
	 * @return 
	 * @throws JSONException 
	 */
	public static String checkVersion() throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadZixun);
		sb.append("iphone/common/versionandroid.txt");
		
		String result = Conn.getData(sb.toString());
		return result;
	}
}
