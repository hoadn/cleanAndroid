/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Global.java 下午12:29:04 2010-10-3
 */
package com.cssweb.android.common;

import com.cssweb.android.main.R;



/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public interface Global {

	public static final int FLING_MIN_DISTANCE = 50;   
	public static final int FLING_MIN_VELOCITY = 100;

	public static final int PAGE_SIZE 		   = 9;  
	public static final int MAX_PAGE_SIZE      = 100;
	public static final int P_STOCK_NUMS       = 1000;
	public static final int A_STOCK_NUMS       = 10;
	
	public static final int LINE_SIZE          = 20;
	public static final int DATA_SIZE          = 20;
	
	public static final int QUOTE_LOOP         = 100;//ms
	
	public static final int AUTO_REFRSH_TIME   = 3000;//ms
	public static final int CLICK_RESPONSE_TIME   = 500;//ms
	public static final int DETAIL_DIS_NUMS    = 30;//ms

	
	public static final int[] BAR_IMAGE_1 = { R.drawable.njzq_status_back, R.drawable.njzq_status_hq, R.drawable.njzq_status_jy, R.drawable.njzq_status_zx, R.drawable.njzq_status_fw };
	public static final int[] BAR_IMAGE_2 = { R.drawable.njzq_status_back, R.drawable.njzq_status_hq, R.drawable.njzq_status_zx, R.drawable.njzq_status_fw, R.drawable.njzq_status_yykh };
	
	
	public static final String[] BAR_HQ_ZHPM = {"11", "22", "33", "44", "55", "66"};
	public static final int[] BAR_TAG            = { 0, 1, 2, 3, 4, 5 };
	public static final int[] BAR_TAG_2           = { 0, 1, 6, 3, 4, 5 };
	
	/** 以下资源ID常量不能有重复**/
	//交易部分资源ID常量
	public static final int    NJZQ_NZBD             = 0;
	public static final int    NJZQ_HQBJ    	     = 1;
	public static final int    NJZQ_WTJY             = 2;
	public static final int    NJZQ_ZXHD             = 3;
	public static final int    NJZQ_ZSYYT            = 4;
	public static final int    NJZQ_NZFC       		 = 5;
	public static final int    NJZQ_JFSC        	 = 6;
	public static final int    NJZQ_ZXLP         	 = 7;
	public static final int    NJZQ_HQBJ_HQYJ    	 = 10;
	public static final int    NJZQ_HQBJ_FIVE    	 = 15;
	public static final int    NJZQ_HQBJ_GGHQ    	 = 16;
	public static final int    NJZQ_HQBJ_EGHT    	 = 18;
	public static final int    NJZQ_HQBJ_QHHQ    	 = 19;
	public static final int    NJZQ_WTJY_ONE         = 20;
	public static final int    NJZQ_WTJY_TWO         = 21;
	public static final int    NJZQ_WTJY_THREE       = 22;
	public static final int    NJZQ_WTJY_FOUR        = 23;
	public static final int    NJZQ_WTJY_FIVE        = 24;
	public static final int    NJZQ_TYYH_LOGIN       = 25;
	public static final int    NJZQ_ZXHD_EGHT        = 28;
	
	public static final int    NJZQ_ZXHD_ZJJP        = 31;
	public static final int    NJZQ_ZXHD_TZGW        = 32;
	public static final int    NJZQ_ZXHD_KHJL        = 33;
	public static final int    NJZQ_ZXHD_ZXKF        = 34;
	public static final int    NJZQ_ZXHD_KFRX        = 35;
	public static final int    NJZQ_ZXHD_GNLY        = 36;
	public static final int    NJZQ_ZXHD_CJWT        = 37;
	
	public static final int    NJZQ_ZXLP_CPBD         	 = 41;
	public static final int    NJZQ_ZXLP_YWZJ         	 = 42;
	public static final int    NJZQ_ZXLP_PMHH         	 = 43;
	public static final int    NJZQ_ZXLP_TGLC         	 = 44;
	public static final int    NJZQ_ZXLP_ZJLX         	 = 45;
	public static final int    NJZQ_ZXLP_GG         	 = 46;
	
	public static final int    NJZQ_NZBD_JXZQC_JCC       = 50;
	
	public static final int    NJZQ_ZSYYT_YYKH        	 = 51;
	public static final int    NJZQ_ZSYYT_YYTGG        	 = 52;
	public static final int    NJZQ_ZSYYT_YYWD        	 = 53;
	public static final int    NJZQ_ZSYYT_YWZX        	 = 54;
	public static final int    NJZQ_ZSYYT_TZZJY        	 = 55;
	public static final int    NJZQ_ZSYYT_TJKH        	 = 56;
	
	public static final int    NJZQ_NZBD_JXZQC_HXC       = 57;
	
	
	public static final int    NJZQ_WEBVIEW_LOGIN        = 60;
	
	public static final int    OPENHREF     		 = 70;
	
	public static final int    NJZQ_NZFC_NZDT     		 = 71;
	public static final int    NJZQ_NZFC_NZFCSP          = 72;
	public static final int    NJZQ_NZFC_ZJNZ     		 = 73;
	public static final int    NJZQ_NZFC_JYH     		 = 74;
	public static final int    NJZQ_ZXG_GONGGAO          = 80;
	public static final int    NJZQ_ZXG_DIANPIN          = 81;
	public static final int    NJZQ_ZXG_ZHENDUAN         = 82;
	public static final int    NJZQ_ZXG_QINGBAO          = 83;
	public static final int    NJZQ_ZXG_JJ_ZHENDUAN      = 84	;
	
	
	public static final int     NJZQ_FIND_PASSWORD             = 90;
	public static final int     NJZQ_SQTYK                     = 91;
	public static final int     NJZQ_RESET_SERVIR_PASSWORD     = 92;
	public static final int     NJZQ_TYYH_FIND_PASSWORD        = 93;
	
	
	/** 用来定义ACTIVITY标签，用于返回键处理和页面跳转 **/
	public static final int QUOTE_VIEW               = 100;
	public static final int QUOTE_F10                = 101;
	public static final int QUOTE_FENSHI             = 102;
	public static final int QUOTE_KLINE              = 103;
	public static final int QUOTE_BAOJIA             = 104;
	public static final int QUOTE_MINGXI             = 105;
	public static final int QUOTE_FLINE              = 106; //基金走势图
	public static final int QUOTE_USERSTK            = 107;
	public static final int QUOTE_WARN               = 108;
	public static final int QUOTE_PAIMING            = 109;
	public static final int QUOTE_FENLEI             = 110;
	
	public static final int QUOTE_WARNING            = 111;  //行情预警
	public static final int QUOTE_STOCK              = 112;  //股票型基金
	public static final int QUOTE_BOND               = 113;  //债券型基金
	public static final int QUOTE_MONETARY           = 114;  //货币型基金
	public static final int QUOTE_MIX                = 115;  //混合型基金
	public static final int SUN_PRIVATE              = 116;  //阳光私募
	public static final int QUOTE_DAPAN              = 117;  //大盘指数
	public static final int QUOTE_HKSTOCK            = 118;  //大盘指数
	public static final int HK_MAINBOARD             =119;   //香港主板
	public static final int HK_CYB					 =120;   //香港创业板
	
	public static final int ZJS					 =121;   //中金所
	public static final int SDZ					 =122;   //上期所,大商所,郑商所
	public static final int QUOTE_HSZS              = 123;  //恒生指数
	
	public static final int    NJZQ_HQBJ_QQSC_WHSC   = 151;
	public static final int    NJZQ_WTJY_GP_ONE      = 200;
	public static final int    NJZQ_WTJY_GP_TWO      = 210;
	public static final int    NJZQ_WTJY_GP_THREE    = 220;
	
	public static final int    NJZQ_NZBD_JXZQC       = 901;
	public static final int    NJZQ_NZBD_TZZH        = 902;
	public static final int    NJZQ_NZBD_ZQYJ        = 903;
	public static final int    NJZQ_NZBD_QHYJ        = 904;
	public static final int    NJZQ_NZBD_CFPD        = 905;
	
	public static final int    NJZQ_NZBD_CFPD_MRJP        = 906;
	public static final int    NJZQ_NZBD_CFPD_TZLT        = 907;
	public static final int    NJZQ_NZBD_CFPD_JTPX        = 908;
	
	public static final int    NJZQ_JLP_LLYH         = 1000;
	public static final int    NJZQ_JLP_JYYH         = 1001;
	public static final int    NJZQ_JLP_TYYH         = 1002;
	public static final int    NJZQ_JLP_SZ           = 1003;
	public static final int    NJZQ_JLP_YYKH         = 1004;

	
	public static final int    NJZQ_JLP_WDZQTAG        = 1005;
	public static final int    NJZQ_JLP_YYKHTAG        = 1006;
	public static final int    NJZQ_JLP_TYKTAG         = 1007;
	
	public static final int    NJZQ_FUND_RISK_TEST     = 1008;
	public static final int    NJZQ_WTJY_SZLC     = 1009;
	public static final int    NJZQ_WTJY_RZRQ     = 1010;
	public static final int    NJZQ_WTJY_ZJDB        = 1011;
	
	/** 以上资源ID常量不能有重复**/
	
	public static final String TOOLBAR_PINGZHONG = "品种";//0
	public static final String TOOLBAR_ZHANGFU   = "涨幅";//1
	public static final String TOOLBAR_DIEFU     = "跌幅";//2
	public static final String TOOLBAR_LIANGBI   = "量比";//3
	public static final String TOOLBAR_ZIXUAN    = "自选";//4
	public static final String TOOLBAR_JIAOYI    = "交易";//5
	
	public static final String TOOLBAR_MANGER    = "管理";//6
	public static final String TOOLBAR_TIANJIA   = "加入";//6
	public static final String TOOLBAR_SHANCHU   = "移出";//7
	public static final String TOOLBAR_SHANGYE   = "上页";//8
	public static final String TOOLBAR_XIAYIYE   = "下页";//9
	public static final String TOOLBAR_REFRESH   = "刷新";//10
	public static final String TOOLBAR_PAIXU     = "排序";//11
	public static final String TOOLBAR_PAIMING   = "排名";//12

	public static final String TOOLBAR_KLINE     = "Ｋ线";//13
	public static final String TOOLBAR_XIADAN    = "下单";//14
	public static final String TOOLBAR_CAIWU     = "财务";//15
	public static final String TOOLBAR_CANCEL    = "撤单";//16
	public static final String TOOLBAR_BACK      = "返回";//17
	public static final String TOOLBAR_FENSHI    = "分时";//18
	public static final String TOOLBAR_ZHIBIAO   = "指标";//19
	public static final String TOOLBAR_ZHOUQI    = "周期";//20
	public static final String TOOLBAR_F10   	 = "Ｆ10";//21
	public static final String TOOLBAR_XUANX   	 = "选项";//22
	public static final String TOOLBAR_BUYSALE 	 = "报价";//23
	public static final String TOOLBAR_MINGIX 	 = "明细";//24
	public static final String TOOLBAR_ZOOMOUT 	 = "放大";//25
	public static final String TOOLBAR_ZOOMIN 	 = "缩小";//26
	public static final String TOOLBAR_UPLOAD 	 = "上传";//40
	public static final String TOOLBAR_SH 	     = "沪市";//41
	public static final String TOOLBAR_SZ 	     = "深市";//42
	public static final String TOOLBAR_MENU  	 = "菜单";//43
	public static final String TOOLBAR_GONGGAO  	 = "公告";//43
	public static final String TOOLBAR_DIANPING  	 = "点评";//43
	public static final String TOOLBAR_QINGBAO  	 = "情报";//43
	public static final String TOOLBAR_ZHENDUAN  	 = "诊断";//43
	public static final String TOOLBAR_CLEAR  	 = "清空";//44
	public static final String TOOLBAR_UPDATE  	 = "修改";//45

	
	public static final String TOOLBAR_QUEDING   = "确定";//27
	public static final String TOOLBAR_QUXIAO    = "取消";//28 
	public static final String TOOLBAR_RENMINBI  = "人民币";//29
	public static final String TOOLBAR_MEIYUAN   = "美元";//30
	public static final String TOOLBAR_GANGBI    = "港币";//31
	public static final String TOOLBAR_LAST      = "上条";//32
	public static final String TOOLBAR_NEXT      = "下条";//33
	public static final String TOOLBAR_DETAIL    = "详细";//51
	public static final String TOOLBAR_BUY       = "买入";//52
	public static final String TOOLBAR_SELL      = "卖出";//53
	public static final String TOOLBAR_DELETE    = "删除";//54
	public static final String TOOLBAR_PREFERRED = "首选";//55
	public static final String TOOLBAR_CLEAN_ALL = "清空";//56
	
	
	public static final String TOOLBAR_TRANSFER  ="转换"; //57
	public static final String TOOLBAR_SALE      = "赎回" ; //58
	public static final String TOOLBAR_DIVIDEND  ="分红" ;  //59
	
	public static final String OPENACCOUNT  ="开户" ;  //60
	public static final String TOOLBAR_ALL_CANCEL = "全撤";//61
	public static final String TOOLBAR_CHAXUN ="查询";
	
	public static final String TOOLBAR_ADD       ="增加" ; //62
	public static final String TOOLBAR_REMOVE    ="删除" ; //63
	
	
	public static final int PAGE_SIZE_INCLUDE_TITLE = 9;
	public static final int[]  BARTAG_DETAILS    = {32, 33};
	public static final int[]  BARTAG_STOCK_CANCEL = {51,8,9,16,61,10};
	public static final int[]  BARTAG_BIZHONG    = {29, 30, 31};
	//当日委托
	public static final String QUERY_STOCK_DRWT = "stock_drwt";
	public static final int[]  BARTAG_CHAXUN     = {51, 10, 8, 9};
	//当日成交
	public static final String QUERY_STOCK_DRCJ = "stock_drcj";
	public static final int[]  BARTAG_PROMPT     = {27, 28};
	public static final int    OPEN_JJ         = R.array.fund_list_menu;
	//基金开户
	public static final int[]  OPENACCOUNT_BAR     = {60,8,9,28};
	//基金历史成交
	public static final String QUERY_FUND_LSCJ= "fund_lscj";
	public static final int    OPEN_GP         = R.array.stock_list_menu;
	
	//历史成交
	public static final String QUERY_STOCK_LSCJ = "stock_lscj";
	//历史委托
	public static final String QUERY_STOCK_LSWT = "stock_lswt";
	//对账单查询
	public static final String QUERY_STOCK_DZD = "stock_dzd";
	//新股配号查询
	public static final String QUERY_NEW_STOCK_MATCH = "new_stock_match";
	public static final int    TRADE_STOCK_MORE    = 1;
	public static final int    TRADE_STOCK         = 0;
	public static final int    TRADE_FUND          = 2;
	public static final int    TRADE_FUND_MORE     = 3;
	public static final int    TRADE_YZZZ          = 4;
	public static final int    TRADE_ACCOUNT       = 5;
	
	//基金撤单
	public static final String QUERY_FUND_CD= "fund_cd";
	//基金当日委托
	public static final String QUERY_FUND_DRWT= "fund_drwt";
	
	public static final int[] BARTAG_PORTIO = {51 ,58 ,57 ,59 , 8 , 9 };
	//基金历史委托
	public static final String QUERY_FUND_LSWT= "fund_lswt"; 
	
	public static final String HTTP = "http://vtrade.njzq.cn";
	
	
}
