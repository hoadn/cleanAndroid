/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Utils.java 下午02:00:22 2010-8-23
 */
package com.cssweb.quote.util;

import java.text.DecimalFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.cssweb.android.main.R;

/**
 * 格式化数据
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Utils {
	
	public static final String TP_TAG = "--";
	
	/**
	 * 格式化数据
	 * @param d
	 * @param t
	 * @param allowZero d=0时为1时返回空，2返回-，其他返回原始数据
	 * @return
	 */
	public static String dataFormation(double d, int t, int allowZero) {
		String s = "";
		if(d==0&&(allowZero==0||allowZero==1))
			s = "";
		else if(d==0&&allowZero==2)
			s = "-";
		else {
			s = dataFormation(d, t);
		}
		return s;
	}
	
	/**
	 * 小数点位数
	 * @param d
	 * @param t
	 * @return
	 */
	public static String dataFormation(double d, int t) {
		DecimalFormat df = null;
		switch(t) {
			case 0:
				df = new DecimalFormat("#.##");
				df.setMinimumFractionDigits(0);
				return df.format(d);
			case 1:
				df = new DecimalFormat("#.##");
				df.setMinimumFractionDigits(2);
				return df.format(d);
			case 2:
				df = new DecimalFormat("#.###");
				df.setMinimumFractionDigits(3);
				return df.format(d);
			case 3:
				df = new DecimalFormat("#.####");
				df.setMinimumFractionDigits(4);
				return df.format(d);
			case 4:
				df = new DecimalFormat("#.###%");
				df.setMinimumFractionDigits(3);
				return df.format(d/100);
			case 5:
				return String.valueOf((int)d);
			case 6:
				df = new DecimalFormat("#.##%");
				df.setMinimumFractionDigits(2);
				return df.format(d);
			case 7:
				df = new DecimalFormat("#.#");
				df.setMinimumFractionDigits(1);
				return df.format(d);
			case 8:
				df = new DecimalFormat("#.##%");
				df.setMinimumFractionDigits(2);
				return df.format(d/100);
			default:
				return "0";
				
		}
	}
	
	/**
	 * 得到股票类型市场需要保留的精度 
	 * @param stockCode
	 * @param exchange
	 * @return digit
	 * 
	 */		
	public static int getNumFormat(String exchange, String stockCode) {
		int type = NameRule.getSecurityType(exchange, stockCode);
		return getStockDigit(type);
	}
	
	/**
	 * 2011-04-20增加了港股主板和创业板
	 * @param type
	 * @return
	 */
	public static int getStockDigit(int type) {
		int dig;
		if (type == NameRule.SH_B || type == NameRule.SZ_B) {
			dig = 2;
		}

		else if (type == NameRule.SH_WARRANT || type == NameRule.SZ_WARRANT) {
			dig = 2;
		}

		else if (type == NameRule.SH_FUND || type == NameRule.SZ_FUND) {
			dig = 2;
		}

		else if (type == NameRule.SH_ETF || type == NameRule.SZ_ETF) {
			dig = 2;
		} 

		else if (type == NameRule.SZ_BOND || type == NameRule.SZ_CONV_BOND) {
			dig = 2;
		} 
		
		else if (type == NameRule.SZ_OPEN_FUND || type == NameRule.SH_KFSJJ
				|| type == NameRule.SZ_LOF || type == NameRule.OTHER_OPEN_FUND
				|| type == NameRule.SH_BUY_BACK || type == NameRule.SZ_BUY_BACK) {
			dig = 2;
		} 

		else if (type == NameRule.HK_MAIN || type == NameRule.HK_CYB) {
			dig = 2;
		} 
		
		else if (type == NameRule.QH_IF) {
			dig = 7;
		}
		
		else if (type == NameRule.QH_A || type == NameRule.QH_B || 
				type == NameRule.QH_M || type == NameRule.QH_Y || 
				type == NameRule.QH_C || type == NameRule.QH_P || 
				type == NameRule.QH_L || type == NameRule.QH_V || 
				type == NameRule.QH_J || type == NameRule.QH_CU || 
				type == NameRule.QH_AL || type == NameRule.QH_ZN || 
				type == NameRule.QH_PB || type == NameRule.QH_FU || 
				type == NameRule.QH_RU || type == NameRule.QH_RB || 
				type == NameRule.QH_WR || type == NameRule.QH_RO || 
				type == NameRule.QH_WS || type == NameRule.QH_WT || 
				type == NameRule.QH_CF || type == NameRule.QH_SR || 
				type == NameRule.QH_TA || type == NameRule.QH_ER) {
			dig = 5;
		}
		
		else {
			dig = 1;
		}
		return dig;
	}
	
	/**
	 * 画分时图用到
	 * 2011-04-20增加了港股主板和创业板
	 * @param exchange
	 * @param stockCode
	 * @return
	 */
	public static double getStockDigit(String exchange, String stockCode) {
		int type = NameRule.getSecurityType(exchange, stockCode);
		if (type == NameRule.SH_B || type == NameRule.SZ_B) {
			return 0.001;
		}

		else if (type == NameRule.SH_WARRANT || type == NameRule.SZ_WARRANT) {
			return 0.001;
		}

		else if (type == NameRule.SH_FUND || type == NameRule.SZ_FUND) {
			return 0.001;
		}

		else if (type == NameRule.SH_ETF || type == NameRule.SZ_ETF) {
			return 0.001;
		} 
		
		else if (type == NameRule.SZ_OPEN_FUND || type == NameRule.SH_KFSJJ
				|| type == NameRule.SZ_LOF || type == NameRule.OTHER_OPEN_FUND
				|| type == NameRule.SH_BUY_BACK || type == NameRule.SZ_BUY_BACK) {
			return 0.001;
		} 

		else if (type == NameRule.HK_MAIN || type == NameRule.HK_CYB) {
			return 0.001;
		} 
		
		else if (type == NameRule.QH_IF) {
			return 0.1;
		}
		
		else if (type == NameRule.QH_A || type == NameRule.QH_B || 
				type == NameRule.QH_M || type == NameRule.QH_Y || 
				type == NameRule.QH_C || type == NameRule.QH_P || 
				type == NameRule.QH_L || type == NameRule.QH_V || 
				type == NameRule.QH_J || type == NameRule.QH_CU || 
				type == NameRule.QH_AL || type == NameRule.QH_ZN || 
				type == NameRule.QH_PB || type == NameRule.QH_FU || 
				type == NameRule.QH_RU || type == NameRule.QH_RB || 
				type == NameRule.QH_WR || type == NameRule.QH_RO || 
				type == NameRule.QH_WS || type == NameRule.QH_WT || 
				type == NameRule.QH_CF || type == NameRule.QH_SR || 
				type == NameRule.QH_TA || type == NameRule.QH_ER) {
			return 0;
		}
		
		return 0.01;
	}
	
	public static String getAmountFormat(double amount, boolean alowzero) {
		String str = "";
		if(amount==0) {
			if(alowzero)
				str = "0";
			else
				str = "";
		}
		else if(Math.abs(amount)>=100000000) {
			str = String.valueOf(Arith.div(amount, 100000000, 2) + "亿");
		}
		else if(Math.abs(amount)>=10000) {
			str = String.valueOf(Arith.div(amount, 10000, 2) + "万");
		}
		else 
			str = dataFormation(amount, 1);
			//str = String.valueOf(amount);
		return str;
	}
	
	public static String getAmountFormat(double amount, boolean alowzero,int dig) {
		String str = "";
		if(amount==0) {
			if(alowzero)
				str = "0";
			else
				str = "-";
		}
		else if(Math.abs(amount)>=10000000) {
			str = String.valueOf(Arith.div(amount, 100000000, dig) + "亿");
		}
		else if(Math.abs(amount)>=10000) {
			str = String.valueOf(Arith.div(amount, 10000, dig) + "万");
		}
		else 
			str = String.valueOf((int)amount);
		return str;
	}
	
	public static String getAmountFormat(int amount, boolean alowzero) {
		String str = "";
		if(amount==0) {
			if(alowzero)
				str = "0";
			else
				str = "";
		}
		else if(amount>=100000000) {
			str = String.valueOf(Arith.div(amount, 100000000, 2) + "亿");
		}
		else if(amount>=10000) {
			str = String.valueOf(Arith.div(amount, 10000, 2) + "万");
		}
		else 
			str = String.valueOf(amount);
		return str;
	}
	
	public static String getAmountFormat(long amount, boolean alowzero) {
		String str = "";
		if(amount==0) {
			if(alowzero)
				str = "0";
			else
				str = "";
		}
		else if(amount>=100000000) {
			str = String.valueOf(Arith.div(amount, 100000000, 2) + "亿");
		}
		else if(amount>=10000) {
			str = String.valueOf(Arith.div(amount, 10000, 2) + "万");
		}
		else 
			str = String.valueOf(amount);
		return str;
	}
	
	/**
	 * 判断颜色，拿d1和d0比较分三种情况
	 * @param d1
	 * @param d0
	 * @return
	 */
	public static int getTextColor(Context context, double d1, double d0) {
		int color = context.getResources().getColor(R.color.zr_white);
		if(d1==0) {
			color = context.getResources().getColor(R.color.zr_white);
			return color;
		}
		if(d1>d0) {
			color = context.getResources().getColor(R.color.zr_red);
			return color;
		}
		if(d1<d0) {
			color = context.getResources().getColor(R.color.zr_green);
			return color;
		}
		return color;
	}
	
	/**
	 * 判断颜色，拿d1和0比较分三种情况
	 * @param d1
	 * @return
	 */
	public static int getTextColor(Context context, double d1) {
		int color = context.getResources().getColor(R.color.zr_white);
		if(d1>0) {
			color = context.getResources().getColor(R.color.zr_red);
		}
		else if(d1<0) {
			color = context.getResources().getColor(R.color.zr_green);
		}
		else {
			color = context.getResources().getColor(R.color.zr_white);
		}
		return color;
	}
	
	/**
	 * 判断颜色，根据t值决定显示何种颜色
	 * @param t
	 * @return
	 */
	public static int getTextColor(Context context, int t) {
		int color = context.getResources().getColor(R.color.zr_white);
		switch(t) {
			case 0: color = context.getResources().getColor(R.color.zr_white); break;
			case 1: color = context.getResources().getColor(R.color.zr_yellow); break;
			case 2: color = context.getResources().getColor(R.color.zr_qianlan); break;
			case 3: color = context.getResources().getColor(R.color.zr_red); break;
			case 4: color = context.getResources().getColor(R.color.zr_green); break;
			case 5: color = context.getResources().getColor(R.color.zr_newlightgray); break;
			case 6: color = context.getResources().getColor(R.color.zr_ziluolan); break;
		}
		return color;
	}
	
	public static int getTextColor(Context context, double num1 ,double num2 ,double num3 ) {
		int color = context.getResources().getColor(R.color.zr_white);
		 if (num1 > num2){
			color = context.getResources().getColor(R.color.zr_red);
		}else if (num1 < num3){
			color = context.getResources().getColor(R.color.zr_green);
		}
		return color;
	}
	
	public static int getTextColor2(Context context, double num1 ,double num2  ) {
		int color = context.getResources().getColor(R.color.zr_white);
		 if (num1 > num2){
			color = context.getResources().getColor(R.color.zr_red);
		}
		return color;
	}
	
	public static int getTextColor3(Context context, double num1 ,double num3  ) {
		int color = context.getResources().getColor(R.color.zr_white);
		 if (num1 < num3){
			 color = context.getResources().getColor(R.color.zr_green);
		}
		return color;
	}
	
	/**
	 * 判断颜色，根据t值决定显示何种颜色
	 * @param t
	 * @return
	 */
	public static int getTextColor(Context context, String t) {
		int color = context.getResources().getColor(R.color.zr_white);
		if(t.startsWith("+"))
			color = context.getResources().getColor(R.color.zr_red);
		else if(t.startsWith("-"))
			color = context.getResources().getColor(R.color.zr_green);
		else 
			color = context.getResources().getColor(R.color.zr_white);
		return color;
	}
	
	// 得到 证券品种 的 数量  单位换算系数  
	// 后台传回 证券品种 的 单位，  股票 为 手（100个最小单位）， 基金 为 手（100个最小单位），债券 手（10个最小单位） 
	public static int getCoefficient(String exchange, String stockcode) {
		int type = NameRule.getSecurityType(exchange, stockcode);
		if (type == NameRule.SH_BOND || type == NameRule.SH_CONV_BOND) // 上证债券，上证转债
																		// 10个最小单位
		{
			return 10;
		} else if (type == NameRule.SZ_BOND || type == NameRule.SZ_CONV_BOND) { // 深证债券，深证转债
																				// 10
																				// 个最小单位
			return 10;
		} else if (type == NameRule.SH_BUY_BACK) { // 上证回购
			return 1000;
		} else if (type == NameRule.SZ_BUY_BACK) { // 深证回购
			return 1000;
		} else if (type == NameRule.HK_INDEX || type == NameRule.HK_MAIN || type == NameRule.HK_CYB) {
			return 1;
		}

		return 100;			//其他股票基金
	}
	
	// 格式化 数量。 目前后台返回的数据，单位都是 手
	public static int FormatVolume(int n) {
		return Math.round(n); 
	}
	
	public static String format(Date date) {
		String str = "";
		str = date.getYear()+"-"+formatTwo(date.getMonth()+1)+"-"+
					formatTwo(date.getDate())+" "+formatTwo(date.getHours())+":"+formatTwo(date.getMinutes())+":00";
		return str;
	}
	
	private static String formatTwo(int n){
		String str = String.valueOf(n);
		if (str.length()==1){
			return "0"+str;
		}
		if (str.length()==2){
			return str;
		}
		return "00";
	}
	
	public static String getPeriod(String period) {
		String colname = "dayline";
		if("5min".equals(period)) {
			colname = "fivemin";
		}
		else if("15min".equals(period)) {
			colname = "fifvteenmin";
		}
		else if("30min".equals(period)) {
			colname = "thirtymin";
		}
		else if("60min".equals(period)) {
			colname = "sixtymin";
		}
		else if("day".equals(period)) {
			colname = "dayline";
		}
		else if("week".equals(period)) {
			colname = "weekline";
		}
		else if("month".equals(period)) {
			colname = "monthline";
		}
		else if("year".equals(period)) {
			colname = "yearline";
		}
		return colname;
	}
	
	/**
	 * 判断输入的是否为字母
	 * @param str
	 * @return
	 */
	public static boolean isCharacter(String str) {//判断字母
		return (str==null) ? false : str.matches("[a-zA-Z]+");
	}
	
	/**
	 * 判断输入的是否是数字（不含小数）
	 * @param str
	 * @return
	 */
	public static boolean isFloatNumber(String str) {//判断小数
		return (str==null) ? false : str.matches("[\\d.]+");
	}

	/**
	 * 判断输入的是否是数字（含小数）
	 * @param str
	 * @return
	 */
	public static boolean isNumber(String str) {//判断整数
		return (str==null) ? false : str.matches("[\\d]+");
	}
	
	public static boolean isHttpStatus(JSONObject j) throws JSONException {
		if(j!=null&&"success".equals(j.get("cssweb_code"))) {
			return true;
		}
		else 
			return false;
	}
	
	/**
	 * 去掉字符串里面的空格 
	 * @param str
	 * @return
	 */
	public static String clearSpace(String str) {
		return str==null||str.equals("")?"":str.replaceAll(" ", "");
	}
	
	/**
	 * 根据买卖盘 转换相应的颜色 （现量）
	 * @param bsFlag
	 * @return
	 */
	public static int getColorFromBS (String bsFlag){
		if("B".equals(bsFlag))
			return 3;
		return 4 ;
	}
}
