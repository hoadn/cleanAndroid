/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)TickUtil.java 上午10:45:02 2010-5-26
 */
package com.cssweb.quote.util;

import org.json.JSONArray;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TickUtil {
	
	public static double gethighPrice(JSONArray item, int len) {
		double temp = 0;
		try {
			for (int i = 0; i < len; i++) {
				if (temp < ((JSONArray) item.get(i)).getDouble(0)) {
					temp = ((JSONArray) item.get(i)).getDouble(0);
				}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return temp;
	}
	
	public static double getlowPrice(JSONArray item, int len) {
		double temp = 99999;
		try {
			for (int i = 1; i < len; i++) {
				if (temp > ((JSONArray) item.get(i)).getDouble(0) && ((JSONArray) item.get(i)).getDouble(0)>0) {
					temp = ((JSONArray) item.get(i)).getDouble(0);
				}
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}
		return temp;
	}

	public static double gethighVolume(JSONArray item) {
		double temp = 0;
		try {
			for (int i = 1; i < item.length(); i++) {
				if (temp < (item.getJSONArray(i).getDouble(1) - item.getJSONArray(i - 1).getDouble(1))) {
					temp = item.getJSONArray(i).getDouble(1) - item.getJSONArray(i - 1).getDouble(1);
				}
			}
			if (item.length() > 0
					&& temp < item.getJSONArray(0).getDouble(1)) {
				temp = item.getJSONArray(0).getDouble(1);
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return temp;
	}

	public static double gethighAmount(JSONArray item) {
		double temp = 0;
		try {
			for (int i = 1; i < item.length(); i++) {
				if (temp < (item.getJSONArray(i).getDouble(2) - item.getJSONArray(i - 1).getDouble(2))) {
					temp = (item.getJSONArray(i).getDouble(2) - item.getJSONArray(i - 1).getDouble(2));
				}
			}
			if (item.length() > 0
					&& temp < item.getJSONArray(0).getDouble(2)) {
				temp = item.getJSONArray(0).getDouble(2);
			}
		} catch (Exception e) { 
			e.printStackTrace();
		}

		return temp;
	}
	
	/**
	 * 根据时间的字符串形式，计算 当前理论显示的笔数
	 * @param time 时间字符串 如："2009-09-25 14:01:30"
	 * @return 理论显示的笔数
	 */
	public static int calcLenByTime(String time){
		int len = 0;
		int hour = Integer.parseInt(time.substring(11,13));
		int minutes = Integer.parseInt(time.substring(14, 16));  
		if (hour==9){
			len = Math.max(minutes,30) - 30 + 1;
		}
		if (hour==10){
			len = minutes + 30 + 1;
		}
		if (hour==11){
			len = Math.min(minutes, 30) + 90 + 1;
		}
		if (hour==13){
			len = minutes + 121;
		}
		if (hour==14){
			len = minutes + 121 + 60;
		}
		if (hour==15){
			len = 121 + 120;
		}
		return len;
	}
	
	/**
	 * 根据 当前理论显示的笔数，计算时间的字符串形式
	 * @param len 当前理论显示的笔数
	 * @return	时间字符串 如: "2009-09-25 14:01:30"
	 */
	public static String calcTimeBylen(int len){
		String time = "";
		if (len < 31){
			time = "09:" + formatSS(len+29) + ":00";
		}
		if (len > 30 && len < 91){
			time = "10:" + formatSS(len-31) + ":00";
		}
		if (len > 90 && len <= 121){
			time = "11:" + formatSS(len-91) + ":00";
		}
		if (len > 121 && len < 181){
			time = "13:" + formatSS(len-121) + ":00";
		}
		if (len > 180 && len < 241){
			time = "14:" + formatSS(len-181) + ":00";
		}
		if (len == 241){
			time = "15:00:00";
		}
		return time;
	}
	
	/**
	 * 格式化字符串为两位
	 * @param len
	 * @return
	 */
	public static String formatSS(int len){
		if(len<10){
			return "0"+len;
		}else{
			return String.valueOf(len);
		}
	}
}
