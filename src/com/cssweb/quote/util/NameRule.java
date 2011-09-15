/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)NameRule.java 上午10:23:19 2010-8-23
 */
package com.cssweb.quote.util;

import java.util.HashMap;

import com.cssweb.android.trade.util.TradeUtil;

/**
 * 行情数据命名规则
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public abstract class NameRule {
	
	//证券种类
	/** 未知 */
	public static final int UNKNOWN = 0;
	/** 上证A */
	public static final int SH_A = 1;
	/** 上证B */
	public static final int SH_B = 2;
	/** 上证权证 */
	public static final int SH_WARRANT = 3;
	/** 上证权证行权 */
	public static final int SH_WARRANT_EXERCISE = 33;
	/** 上证基金 */
	public static final int SH_FUND = 4;
	/** 上证开放式基金 */
	public static final int SH_KFSJJ = 5;
	/** 上证ETF */
	public static final int SH_ETF = 6;
	/** 上证指数 */
	public static final int SH_INDEX = 7;
	
	/** 上证债券 */
	public static final int SH_BOND = 8;
	/** 上证转债 */
	public static final int SH_CONV_BOND = 9;
	/** 上证回购 */
	public static final int SH_BUY_BACK = 10;
	/** 深证A */
	public static final int SZ_A = 11;
	/** 深证B */
	public static final int SZ_B = 12;
	/** 深证权证 */
	public static final int SZ_WARRANT = 13;
	/** 深证基金 */
	public static final int SZ_FUND = 14;
	/** 深证开放式基金 */
	public static final int SZ_OPEN_FUND = 15;
	/** 深证指数 */
	public static final int SZ_INDEX = 16;
	/** 深证ETF */
	public static final int SZ_ETF = 17;
	/** 深证债券 */
	public static final int SZ_BOND = 18;
	/** 深证转债 */
	public static final int SZ_CONV_BOND = 19;
	/** 深证回购 */
	public static final int SZ_BUY_BACK = 20;
	/** 深证LOF */
	public static final int SZ_LOF = 21;
	/** 三板*/
	public static final int SZ_THREE_BOARD = 30;
	/** 中小板 */
	public static final int SZ_MIDSMALLCAP = 31;
	/** 中证 */
	public static final int SH_ZZ = 32;
	/** 深证创业板*/
	public static final int SZ_TRAD = 34;
	/** 上证，深证外 的  ，场外 开放式基金*/
	public static final int OTHER_OPEN_FUND = 35;

	public static final int SZ_THREEBOARD_A = 301;//三板A
    
    public static final int SZ_THREEBOARD_B = 302;//三板B   
    
    /** 深证其他 */
	public static final int  SZ_OTHER_XG = 22;	
	/** 上证其他 */
	public static  final int SH_OTHER_XG = 40;
	
	/**港股类别-指数*/
	public static final int  HK_INDEX = 100;
	/**港股类别-主板*/
	public static final int  HK_MAIN = 101;
	/**港股类别-窝轮*/
	public static final int  HK_WRNT = 102;
	/**港股类别-债券*/
	public static final int  HK_BOND = 103;
	/**港股类别-信托*/
	public static final int  HK_TRST = 104;
	/**香港创业板**/		
	public static final int  HK_CYB  = 105;
	
	/**期货**/		
	
	public static final int  QH_IF = 501;
	public static final int  QH_A  = 601;
	public static final int  QH_B  = 602;
	public static final int  QH_M  = 603;
	public static final int  QH_Y  = 604;
	public static final int  QH_C  = 605;
	public static final int  QH_P  = 606;
	public static final int  QH_L  = 607;
	public static final int  QH_V  = 608;
	public static final int  QH_J  = 609;
	public static final int  QH_CU = 701;
	public static final int  QH_AL = 702;
	public static final int  QH_ZN = 703;
	public static final int  QH_PB = 704;
	public static final int  QH_AU = 705;
	public static final int  QH_FU = 706;
	public static final int  QH_RU = 707;
	public static final int  QH_RB = 708;
	public static final int  QH_WR = 709;
	public static final int  QH_RO = 801;
	public static final int  QH_WS = 802;
	public static final int  QH_WT = 803;
	public static final int  QH_CF = 804;
	public static final int  QH_SR = 805;
	public static final int  QH_TA = 806;
	public static final int  QH_ER = 807;
	
	public static String getExchange(String market) {
		String exchange = "sh";
		if("0".equals(market)) 
			exchange = "sz";
		else if("1".equals(market))
			exchange = "sh";
		else if("2".equals(market))
			exchange = "kf";
		else if("3".equals(market))
			exchange = "hk";
		else if("5".equals(market))
			exchange = "cf";
		else if("6".equals(market))
			exchange = "dc";
		else if("7".equals(market))
			exchange = "sf";
		else if("8".equals(market))
			exchange = "cz";
		else if("sh".equals(market.toLowerCase()))
			exchange = "sh";
		else if("sz".equals(market.toLowerCase()))
			exchange = "sz";
		else if("kf".equals(market.toLowerCase()))
			exchange = "kf";
		else if("hk".equals(market.toLowerCase()))
			exchange = "hk";
		else if("cf".equals(market.toLowerCase()))
			exchange = "cf";
		else if("dc".equals(market.toLowerCase()))
			exchange = "dc";
		else if("sf".equals(market.toLowerCase()))
			exchange = "sf";
		else if("cz".equals(market.toLowerCase()))
			exchange = "cz";
		return exchange;
	}
	
	public static int getMarket(String exchange) {
		int market = 1;
		String str = exchange.toLowerCase();
		if(str.equals("sh"))
			market = 1;
		else if(str.equals("sz"))
			market = 0;
		else if(str.equals("kf"))
			market = 2;
		else if(str.equals("hk"))
			market = 3;
		else if(str.equals("cf"))
			market = 5;
		else if(str.equals("dc"))
			market = 6;
		else if(str.equals("sf"))
			market = 7;
		else if(str.equals("cz"))
			market = 8;
		return market;
	}
	/**
	 * 行情预警添加过滤证券
	 * @param exchange
	 * @return
	 */
	public static boolean getFilterMarket(String exchange) {
		String str = exchange.toLowerCase();
		if(str.equals("hk"))
			return true;
		else if(str.equals("cf"))
			return true;
		else if(str.equals("dc"))
			return true;
		else if(str.equals("sf"))
			return true;
		else if(str.equals("cz"))
			return true;
		return false;
	}
	
	/**
	 * type为股票分类，五档行情的时候需要根据类别去取不同的数据
	 * 其中0 个股，1 上证中证指数,港股指数，2 深证指数
	 * @param type
	 * @return
	 */
	public static String getStockType(int type) {
		String stocktype = "0";
		if(type==SH_INDEX||type==SH_ZZ) 
			stocktype = "1";
		else if(type==SZ_INDEX)
			stocktype = "2";
		else if(type==HK_INDEX)
			stocktype = "1";
		else if(type==HK_MAIN||type==HK_CYB)
			stocktype = "0";
		else
			stocktype = "0";
		return stocktype;
	}
	
	public static int getSecurityType(String exchange, String stockCode) {
		int type = 0;
		HashMap<String, Integer> map = StockInfo.hashMap;
		if(!map.isEmpty()) {
			String str = exchange.toLowerCase();
			String temp = str.concat(stockCode);
			if(map.containsKey(temp))
				type = map.get(exchange.toLowerCase() + stockCode);
		}
		else {
			//重新加载，暂时保留
		}
		if (type == 0)
			type = UNKNOWN;
		return type;
	}
	
	public static boolean IsTradeOrNot(String ex) {
		if("kf".equals(ex) || "hk".equals(ex) || "cf".equals(ex) || "dc".equals(ex) ||
		"sf".equals(ex) || "cz".equals(ex))
			return true;
		return false;
	}
	
	// 判断 是否 深证的 基金  ，输入参数格式 sz160010
	public static boolean IsSZ_FUND(int type) {
		// 判断 是 深圳开放式基金，etf基金，封闭基金
		if(type==SZ_OPEN_FUND || type== SZ_ETF || type== SZ_FUND){
			return true;
		}else{
			return false;
		} 
	}
	
	//判断是否 上证的基金。  ，输入参数格式 sh160010
	public static boolean IsSH_FUND(int type) {
		// 判断 是 上证etf基金，上证封闭基金
		if(type==SH_FUND || type== SH_ETF ||type==SH_KFSJJ){
			return true;
		}else{
			return false;
		}
	}
	
	public static boolean isFundWithStockCode(int type) {
		if(type==SH_FUND || type== SZ_FUND ||type==SZ_OPEN_FUND){
			return true;
		}else{
			return false;
		}
	}
	
	/**
	 * 判断是否是债券
	 * @param type
	 * @return
	 */
	public static boolean isBond(int type) {
		if(type==SH_BOND || type== SZ_BOND){
			return true;
		}else{
			return false;
		}
	}
	
    /**
     * 
     * 判断股票所在的交易市场
     * @author andy
     * @param zqlb_str 证券类别
     * @param stockCode 证券代码
     * @return 所在的交易市场
     * 
     */
    public static String getMarketFromZqlb(String zqlb_str, String stockCode)
    {
    	String market = "";
    	
    	int zqlb = (int)Double.parseDouble(zqlb_str);
    	
    	if(stockCode!=""){
    		String type = stockCode.substring(0, 3); //三板
            if ( type=="400" || type=="430" ){
                 zqlb = 301;
            }else if(type=="420" ){
            	 zqlb = 302;
            }        	
    	}
        switch(zqlb){
            case NameRule.UNKNOWN:
            case NameRule.SH_A:
            case NameRule.SH_WARRANT:
            case NameRule.SH_FUND:
            case NameRule.SH_KFSJJ:
            case NameRule.SH_ETF:
            case NameRule.SH_INDEX:
            case NameRule.SH_BOND:
            case NameRule.SH_CONV_BOND:
            case NameRule.SH_BUY_BACK:
            case NameRule.SH_WARRANT_EXERCISE:
            case NameRule.SH_OTHER_XG:
                market = TradeUtil.MARKET_SHA; break;                  
            case NameRule.SZ_A:           
            case NameRule.SZ_WARRANT:            
            case NameRule.SZ_FUND:
            case NameRule.SZ_OPEN_FUND:
            case NameRule.SZ_INDEX:
            case NameRule.SZ_ETF:
            case NameRule.SZ_BOND:
            case NameRule.SZ_CONV_BOND:
            case NameRule.SZ_BUY_BACK:
            case NameRule.SZ_LOF:              
            case NameRule.SZ_THREE_BOARD:
            case NameRule.SZ_MIDSMALLCAP:
            case NameRule.SZ_TRAD:
            case NameRule.SZ_OTHER_XG:
                market = TradeUtil.MARKET_SZA; break;
            case NameRule.SH_B:                                              
                market = TradeUtil.MARKET_SHB; break;
            case NameRule.SZ_B:                                                  
                market = TradeUtil.MARKET_SZB; break;  
            case NameRule.SZ_THREEBOARD_A:
                market = TradeUtil.MARKET_TA; break;  
            case NameRule.SZ_THREEBOARD_B:                                                  
                market = TradeUtil.MARKET_TU; break;                                          
                                                      
            default:
                market = TradeUtil.MARKET_SHA; break;                  
        }
        return market;                                                                                                                                                       
    } 	
    /**
     * 获取证券的单位（沪市债券委托单位为：手，深市债券为：张，其他：股）
     * @author chengfei
     * @param zqlb_str 证券类别
     * @return 证券单位
     */
    public static String getStockUnit(String zqlb_str) {
    	String temp = "股";
    	int zqlb = Integer.parseInt(zqlb_str);
    	switch(zqlb){
	        case UNKNOWN:
	        case SH_A:
	        case SH_INDEX:
	        case SH_OTHER_XG:
	        case SH_ZZ:
	        
	        case SZ_A:
	        case SZ_INDEX:
	        case SZ_OTHER_XG:
	        case SZ_MIDSMALLCAP:
	        
	        case SH_B:
	        
	        case SZ_B:
	        
	        case SH_FUND:
	        case SH_ETF:
	        case SH_KFSJJ:
	        
	        case SZ_ETF:
	        case SZ_LOF:
	        case SZ_FUND:
	        case SZ_OPEN_FUND:
	        
	        case SH_WARRANT:
	         
	        case SZ_WARRANT:
	        
	        default:
	            temp = "股"; break;
	        
	        case SH_BUY_BACK:
	        case SH_CONV_BOND:
	        case SH_BOND:
	        	temp = "手"; break;
	        case SZ_BOND:
	        case SZ_CONV_BOND:
	        case SZ_THREE_BOARD:
	        case SZ_BUY_BACK:
	            temp = "张"; break;
    	}
    	return temp;
    }
    /**
     * 获取证券的小数位数（A股和债券：2 其他：3）
     */
    public static int getStockFormatNum(String zqlb_str){
    	int temp = 3;
    	int zqlb = Integer.parseInt(zqlb_str);
	    switch(zqlb){
	        case UNKNOWN:
	        case SH_A:
	        case SZ_A:
	        case SH_BOND:
	        case SH_CONV_BOND:
	        default:
	            temp = 2; break;
	        case SH_INDEX:
	        case SH_OTHER_XG:
	        case SH_ZZ:
	        case SZ_INDEX:
	        case SZ_OTHER_XG:
	        case SZ_MIDSMALLCAP:
	        case SH_B:
	        case SZ_B:
	        case SH_FUND:
	        case SH_ETF:
	        case SH_KFSJJ:
	        case SZ_ETF:
	        case SZ_LOF:
	        case SZ_FUND:
	        case SZ_OPEN_FUND:
	        case SH_WARRANT:
	        case SH_BUY_BACK:
	        case SZ_THREE_BOARD:
	        case SZ_BUY_BACK:
	        case SZ_WARRANT:
	        case SZ_BOND:
	        case SZ_CONV_BOND:
	            temp = 3; break;
	    }
	    return temp;
    }
}
