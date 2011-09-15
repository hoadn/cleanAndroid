/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)StockInfo.java 下午08:42:29 2010-10-17
 */
package com.cssweb.quote.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cssweb.android.util.CssStock;

/**
 * 股票列表
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class StockInfo {
	public static HashMap<String, Integer> hashMap = new HashMap<String, Integer>();
	
	//大盘指数靠前显示的代码 
	public static final String indexData = "sh000001,sh000002,sh000003,sz399001,sz399002,sz399003,sh000300,sz399005,sz399006,sh000004";
	public static final String[] indexCode = { "上证指数", "A股指数", "B股指数", "深圳成指", "成分A指", "成分B指", "沪深300", "中小板指", "创业板指", "工业指数" };
	
	/** 上证A */
	public static List<HashMap<String, String>> SHA = new ArrayList<HashMap<String, String>>();
	/** 上证B */
	public static List<HashMap<String, String>> SHB = new ArrayList<HashMap<String, String>>();
	/** 上证权证 */
	public static List<HashMap<String, String>> SHWARRANT = new ArrayList<HashMap<String, String>>();
	/** 上证权证行权 */
	public static List<HashMap<String, String>> SHWARRANTEXERCISE = new ArrayList<HashMap<String, String>>();
	/** 上证基金 */
	public static List<HashMap<String, String>> SHFUND = new ArrayList<HashMap<String, String>>();
	/** 上证开放式基金 */
	public static List<HashMap<String, String>> SHKFSJJ = new ArrayList<HashMap<String, String>>();
	/** 上证ETF */
	public static List<HashMap<String, String>> SHETF = new ArrayList<HashMap<String, String>>();
	/** 上证指数 */
	public static List<HashMap<String, String>> SHINDEX = new ArrayList<HashMap<String, String>>();
	
	/** 上证债券 */
	public static List<HashMap<String, String>> SHBOND = new ArrayList<HashMap<String, String>>();
	/** 上证转债 */
	public static List<HashMap<String, String>> SHCONVBOND = new ArrayList<HashMap<String, String>>();
	/** 上证回购 */
	public static List<HashMap<String, String>> SHBUYBACK = new ArrayList<HashMap<String, String>>();
	/** 深证A */
	public static List<HashMap<String, String>> SZA = new ArrayList<HashMap<String, String>>();
	/** 深证B */
	public static List<HashMap<String, String>> SZB = new ArrayList<HashMap<String, String>>();
	/** 深证权证 */
	public static List<HashMap<String, String>> SZWARRANT = new ArrayList<HashMap<String, String>>();
	/** 深证基金 */
	public static List<HashMap<String, String>> SZFUND = new ArrayList<HashMap<String, String>>();
	/** 深证开放式基金 */
	public static List<HashMap<String, String>> SZOPENFUND = new ArrayList<HashMap<String, String>>();
	/** 深证指数 */
	public static List<HashMap<String, String>> SZINDEX = new ArrayList<HashMap<String, String>>();
	/** 深证ETF */
	public static List<HashMap<String, String>> SZETF = new ArrayList<HashMap<String, String>>();
	/** 深证债券 */
	public static List<HashMap<String, String>> SZBOND = new ArrayList<HashMap<String, String>>();
	/** 深证转债 */
	public static List<HashMap<String, String>> SZCONVBOND = new ArrayList<HashMap<String, String>>();
	/** 深证回购 */
	public static List<HashMap<String, String>> SZBUYBACK = new ArrayList<HashMap<String, String>>();
	/** 深证LOF */
	public static List<HashMap<String, String>> SZLOF = new ArrayList<HashMap<String, String>>();
	/** 三板*/
	public static List<HashMap<String, String>> SZTHREEBOARD = new ArrayList<HashMap<String, String>>();
	/** 中小板 */
	public static List<HashMap<String, String>> SZMIDSMALLCAP = new ArrayList<HashMap<String, String>>();
	/** 中证 */
	public static List<HashMap<String, String>> SHZZ = new ArrayList<HashMap<String, String>>();
	/** 深证创业板*/
	public static List<HashMap<String, String>> SZTRAD = new ArrayList<HashMap<String, String>>();
	/** 上证，深证外 的  ，场外 开放式基金*/
	public static List<HashMap<String, String>> OTHEROPENFUND = new ArrayList<HashMap<String, String>>();

	public static List<HashMap<String, String>> SZTHREEBOARDA = new ArrayList<HashMap<String, String>>();//三板A
    
    public static List<HashMap<String, String>> SZTHREEBOARDB = new ArrayList<HashMap<String, String>>();//三板B   
    
    /** 深证其他 */
	public static List<HashMap<String, String>> SZOTHERXG = new ArrayList<HashMap<String, String>>();	
	/** 上证其他 */
	public static List<HashMap<String, String>> SHOTHERXG = new ArrayList<HashMap<String, String>>();
	
	public static List<HashMap<String, String>> SHSZZS = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZA = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZB = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZBOND = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZFUND = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZOPENFUND = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> SHSZWARRANT = new ArrayList<HashMap<String, String>>();
	

	public static List<HashMap<String, String>> HKINDEX  = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> HKZHUBAN = new ArrayList<HashMap<String, String>>();
	public static List<HashMap<String, String>> HKCYB    = new ArrayList<HashMap<String, String>>();
	
	public static List<HashMap<String, String>> QHMAIN    = new ArrayList<HashMap<String, String>>();
	
	public static JSONObject allStock;
	public static JSONArray allHKStock;
	
	public static void clearData() {
		allStock = null;
		allHKStock = null;
		hashMap.clear();
		SHA.clear();
		SHB.clear();
		SZA.clear();
		SZB.clear();
		SHSZZS.clear();
		SHSZA.clear();
		SHSZB.clear();
		SHBOND.clear();
		SZBOND.clear();
		SZTRAD.clear();
		SHSZBOND.clear();
		SHSZFUND.clear();
		SHSZOPENFUND.clear();
		SHSZWARRANT.clear();
		HKINDEX.clear();
	}
	
	public static void initAllStock(JSONObject quoteData) throws JSONException {
		if(Utils.isHttpStatus(quoteData)) {
			JSONArray jArr = quoteData.getJSONArray("data");
			for (int i = 0; i < jArr.length(); i++) {
    			JSONArray jA = (JSONArray)jArr.get(i);
				hashMap.put(NameRule.getExchange(jA.getString(0)) + jA.getString(1), jA.getInt(4));
				HashMap<String, String> h = new HashMap<String, String>();
				switch(jA.getInt(4)) {
					case NameRule.SH_A:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHA.add(h);
						break;
					case NameRule.SH_B:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHB.add(h);
						break;
					case NameRule.SH_WARRANT:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHWARRANT.add(h);
						break;
					case NameRule.SH_WARRANT_EXERCISE:
						break;
					case NameRule.SH_FUND:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHFUND.add(h);
						break;
					case NameRule.SH_KFSJJ:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHKFSJJ.add(h);
						break;
					case NameRule.SH_ETF:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHETF.add(h);
						break;
					case NameRule.SH_INDEX:
						if(!indexData.contains(jA.getString(1))) {
							h.put("exchange", NameRule.getExchange(jA.getString(0)));
							h.put("stockcode", jA.getString(1));
							h.put("stockname", jA.getString(2));
							SHINDEX.add(h);
						}
						
						break;
					case NameRule.SH_BOND:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SHBOND.add(h);
						break;
					case NameRule.SH_CONV_BOND:
						break;
					case NameRule.SH_BUY_BACK:
						break;
					case NameRule.SZ_A:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZA.add(h);
						break;
					case NameRule.SZ_B:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZB.add(h);
						break;
					case NameRule.SZ_WARRANT:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZWARRANT.add(h);
						break;
					case NameRule.SZ_FUND:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZFUND.add(h);
						break;
					case NameRule.SZ_OPEN_FUND:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZOPENFUND.add(h);
						break;
					case NameRule.SZ_INDEX:
						if(!indexData.contains(jA.getString(1))) {
							h.put("exchange", NameRule.getExchange(jA.getString(0)));
							h.put("stockcode", jA.getString(1));
							h.put("stockname", jA.getString(2));
							SZINDEX.add(h);
						}
						break;
					case NameRule.SZ_ETF:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZETF.add(h);
						break;
					case NameRule.SZ_BOND:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZBOND.add(h);
						break;
					case NameRule.SZ_CONV_BOND:
						break;
					case NameRule.SZ_BUY_BACK:
						break;
					case NameRule.SZ_LOF:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZLOF.add(h);
						break;
					case NameRule.SZ_THREE_BOARD:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZTHREEBOARD.add(h);
						break;
					case NameRule.SZ_MIDSMALLCAP:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZMIDSMALLCAP.add(h);
						break;
					case NameRule.SH_ZZ:
						if(!indexData.contains(jA.getString(1))) {
							h.put("exchange", NameRule.getExchange(jA.getString(0)));
							h.put("stockcode", jA.getString(1));
							h.put("stockname", jA.getString(2));
							SHZZ.add(h);
						}
						break;
					case NameRule.SZ_TRAD:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						SZTRAD.add(h);
						break;
					case NameRule.OTHER_OPEN_FUND:
						break;
					case NameRule.SZ_OTHER_XG:
						break;
					case NameRule.SH_OTHER_XG:
						break;
				}
    		}
			String[] temp = indexData.split(",");
			for(int i=0; i<temp.length; i++) {
				HashMap<String, String> h = new HashMap<String, String>();
				h.put("exchange", temp[i].substring(0, 2));
				h.put("stockcode", temp[i].substring(2, 8));
				h.put("stockname", indexCode[i]);
				SHSZZS.add(h);
			}
			SHSZZS.addAll(SHINDEX);
			SHSZZS.addAll(SZINDEX);
			SHSZZS.addAll(SHZZ);
			
			SHSZA.addAll(SHA);
			SHSZA.addAll(SZA);
			
			SHSZB.addAll(SHB);
			SHSZB.addAll(SZB);
			
			SHSZBOND.addAll(SHBOND);
			SHSZBOND.addAll(SZBOND);
			
			SHSZFUND.addAll(SHFUND);
			SHSZFUND.addAll(SZFUND);
			
			SHSZOPENFUND.addAll(SHKFSJJ);
			SHSZOPENFUND.addAll(SZOPENFUND);
			
			SHSZWARRANT.addAll(SHWARRANT);
			SHSZWARRANT.addAll(SZWARRANT);
		}
	}
	
	public static void initAllHKStock(JSONObject quoteData) throws JSONException {
		if(Utils.isHttpStatus(quoteData)) {
			HKINDEX.clear();
			HKZHUBAN.clear();
			HKCYB.clear();
			QHMAIN.clear();
			JSONArray jArr = quoteData.getJSONArray("data");
			for (int i = 0; i < jArr.length(); i++) {
    			JSONArray jA = (JSONArray)jArr.get(i);
				HashMap<String, String> h = new HashMap<String, String>();
				switch(jA.getInt(4)) {
					case NameRule.HK_INDEX:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						HKINDEX.add(h);
						hashMap.put(NameRule.getExchange(jA.getString(0)) + jA.getString(1), jA.getInt(4));
						break;
					case NameRule.HK_MAIN:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						HKZHUBAN.add(h);
						hashMap.put(NameRule.getExchange(jA.getString(0)) + jA.getString(1), jA.getInt(4));
						break;
					case NameRule.HK_CYB:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						HKCYB.add(h);
						hashMap.put(NameRule.getExchange(jA.getString(0)) + jA.getString(1), jA.getInt(4));
						break;
					default:
						h.put("exchange", NameRule.getExchange(jA.getString(0)));
						h.put("stockcode", jA.getString(1));
						h.put("stockname", jA.getString(2));
						QHMAIN.add(h);
						hashMap.put(NameRule.getExchange(jA.getString(0)) + jA.getString(1), jA.getInt(4));
						break;
				}
    		}
		}
	}
	
	/**
	 * 此函数用来显示大盘指数，前段调用必须判断好起始结束位置，否则报错
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getStockInfo(int begin, int end, int type) {
		StringBuffer sb = new StringBuffer();
		List<HashMap<String, String>> l = getStockListByType(type);
		int t = l.size();
		for(int i=(begin); i<=(end<t?end:t); i++) {
			HashMap<String, String> h = l.get(i-1);
			sb.append(h.get("exchange"));
			sb.append(h.get("stockcode"));
			if(i!=end)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String getStockName(int begin, int end, int type) {
		StringBuffer sb = new StringBuffer();
		List<HashMap<String, String>> l = getStockListByType(type);
		int t = l.size();
		for(int i=(begin); i<=(end<t?end:t); i++) {
			HashMap<String, String> h = l.get(i-1);
			sb.append(h.get("stockname"));
			if(i!=end)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static int getStockInfoSize(int type) {
		List<HashMap<String, String>> l = getStockListByType(type);
		if(l!=null && l.size()>0)
			return l.size();
		return 0;
	}
	
	/**
	 * 此函数用来显示大盘指数，前段调用必须判断好起始结束位置，否则报错
	 * 重构函数为了适应股指期货的取法
	 * @param begin
	 * @param end
	 * @return
	 */
	public static String getStockInfo(int begin, int end, int type, String exchange) {
		StringBuffer sb = new StringBuffer();
		List<HashMap<String, String>> l = getStockListByType(type, exchange);
		int t = l.size();
		for(int i=(begin); i<=(end<t?end:t); i++) {
			HashMap<String, String> h = l.get(i-1);
			sb.append(h.get("exchange"));
			sb.append(h.get("stockcode"));
			if(i!=end)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String getStockCode(int begin, int end, int type, String exchange) {
		StringBuffer sb = new StringBuffer();
		List<HashMap<String, String>> l = getStockListByType(type, exchange);
		int t = l.size();
		for(int i=(begin); i<=(end<t?end:t); i++) {
			HashMap<String, String> h = l.get(i-1);
			sb.append(h.get("stockcode"));
			if(i!=end)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static String getStockName(int begin, int end, int type, String exchange) {
		StringBuffer sb = new StringBuffer();
		List<HashMap<String, String>> l = getStockListByType(type, exchange);
		int t = l.size();
		for(int i=(begin); i<=(end<t?end:t); i++) {
			HashMap<String, String> h = l.get(i-1);
			sb.append(h.get("stockname"));
			if(i!=end)
				sb.append(",");
		}
		return sb.toString();
	}
	
	public static int getStockInfoSize(int type, String exchange) {
		List<HashMap<String, String>> l = getStockListByType(type, exchange);
		if(l!=null && l.size()>0)
			return l.size();
		return 0;
	}
	
	private static List<HashMap<String, String>> getStockListByType(int type, String exchange) {
		List<HashMap<String, String>> l = getStockListByType(type);
		List<HashMap<String, String>> t = new ArrayList<HashMap<String, String>>();
		int tlen = l.size();
		for(int i=0; i<tlen; i++) {
			HashMap<String, String> h = l.get(i);
			if(exchange.equals(h.get("exchange"))) {
				h.put("exchange", h.get("exchange"));
				h.put("stockcode", h.get("stockcode"));
				h.put("stockname", h.get("stockname"));
				t.add(h);
			}
		}
		return t;
	}
	
	private static List<HashMap<String, String>> getStockListByType(int type) {
		switch(type) {
			case 0:
				return SHA; 
			case 1:
				return SHB; 
			case 2:
				return SZA; 
			case 3:
				return SZB; 
			case 4:
				return SHBOND; 
			case 5:
				return SZBOND; 
			case 6:
				return SHSZA; 
			case 7:
				return SHSZB; 
			case 8:
				return SHSZBOND; 
			case 9:
				return SHSZFUND; 
			case 10:
				return SHETF; 
			case 11:
				return SZETF; 
			case 12:
				return SZLOF; 
			case 13:
				return SHSZWARRANT; 
//			case 12:
//				return SHSZZS;
			case 14:
				return SZMIDSMALLCAP;  
			case 15:
				return SZTRAD; 
			case 16:
				return SZTHREEBOARD;
			case 20:
				return SHSZZS;
				
			case 100:
				return HKINDEX;
			case 101:
				return HKZHUBAN;
			case 102:
				return HKCYB;
				
			case 201:
				return QHMAIN;
				
			default:
				return SHSZA;
		}
	}
	
	public static List<CssStock> fillListToNull(int begin, int end, String stocks, String stocksname) {
		List<CssStock> list = new LinkedList<CssStock>();
		String[] temp1 = stocks.split(",");
		String[] temp2 = stocksname.split(",");
//		Log.i("======temp1======", temp1+">>>>>>>>>>>");
//		Log.i("======temp2======", temp2+">>>>>>>>>>>");
		int t = temp1.length;
		for(int i=begin; i<end; i++) {
			CssStock cssStock = new CssStock();
			if(i>t-1) {
				cssStock.setStkname("");
				cssStock.setStkcode("");
				cssStock.setMarket("sh");
			}
			else {
				if(null != temp1[i] && null != temp2[i] && !"".equals(temp1[i]) && !"".equals(temp2[i]) && temp1[i].length() > 2){
					cssStock.setStkname(temp2[i]);
					cssStock.setStkcode(temp1[i].substring(2));
					cssStock.setMarket(temp1[i].substring(0,2));
				}
			}
			cssStock.setZf(0);
			cssStock.setZjcj(0);
			cssStock.setZrsp(0);
			cssStock.setZjcj(0);
			cssStock.setZf(0);
			cssStock.setZd(0);
			cssStock.setBjw1(0);
			cssStock.setSjw1(0);
			cssStock.setXs(0);
			cssStock.setZl(0);
			cssStock.setHs(0);
			cssStock.setJrkp(0);
			cssStock.setZgcj(0);
			cssStock.setZdcj(0);
			cssStock.setZje(0);
			cssStock.setAmp(0);
			cssStock.setLb(0);
			cssStock.setExchange(0);
			list.add(cssStock);
		}
		return list;
	}
	
	
	public static List<CssStock> fillHSZSListToNull(int begin, int end, String stocks, String stocksname) {
		List<CssStock> list = new LinkedList<CssStock>();
		String[] temp1 = stocks.split(",");
		String[] temp2 = stocksname.split(",");
		int t = temp1.length;
		for(int i=begin; i<end; i++) {
			CssStock cssStock = new CssStock();
				if(i>t-1) {
					cssStock.setStkname("");
					cssStock.setStkcode("");
					cssStock.setMarket("hk");
				}
				else if(temp1[i].length() >= 3){
					cssStock.setStkname(temp2[i]);
					cssStock.setStkcode(temp1[i].substring(2));
					cssStock.setMarket(temp1[i].substring(0,2));
				}
				cssStock.setBjw1(0);
				cssStock.setZf(0);
				cssStock.setZd(0);
				cssStock.setZje(0);
				cssStock.setZl(0);
				cssStock.setXs(0);
				cssStock.setJrkp(0);
				cssStock.setZrsp(0);
				cssStock.setZgcj(0);
				cssStock.setZdcj(0);
				cssStock.setAmp(0);
				cssStock.setLb(0);
				cssStock.setExchange(0);
				list.add(cssStock);
			}
		return list;
	}
}
