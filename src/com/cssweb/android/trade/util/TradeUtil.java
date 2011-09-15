/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)TradeUtil.java 下午02:12:03 2010-9-14
 */
package com.cssweb.android.trade.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssStockFund;

/**
 * 交易部分配置
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TradeUtil{
	public static final String SPLIT = "&";
	public static final String g_pubKey_old = "29dlo*%AO+3i16BaweTw.lc!)61K{9^5";
	public static final String g_pubKey = "29dlo*%AO+3i16BaweTw.lc!)61K{9^5";//30dlo*%AO+3i18BaweTw.lc!)61K{9^5
    /***
     * @author andy
     * 
     */
	public static final String MARKET_SHA = "SH";
    public static final String MARKET_SHB = "HB";
    public static final String MARKET_SZA = "SZ";
    public static final String MARKET_SZB = "SB";
    public static final String MARKET_TA = "TA";
    public static final String MARKET_TU = "TU";
    
	
	
	
	
	/**
	 * 系统固定入参封装
	 * @param csswebtype
	 * @param funcid
	 * @return
	 * @throws JSONException 
	 */

	public static String getGlobalRequest(String csswebtype, String funcid) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("funcid=" + funcid + SPLIT );
		sb.append("cssweb_type="+csswebtype+SPLIT);
		sb.append("isSafe=0"+SPLIT);
		sb.append("clientip=" + TradeUser.getInstance().getClientip() + SPLIT );
		sb.append("cssweb_codekey=1.0" + SPLIT );
		sb.append("cssweb_clienttype=1" + SPLIT );
		//判断客户号是否存在如果不存在则传入空值
		if(TradeUser.getInstance().getCustid()== null){
			sb.append("FID_KHH=" + SPLIT);
		}else {
			sb.append("FID_KHH="+TradeUser.getInstance().getCustid()+SPLIT);
		}
		return sb.toString();
	}		
	public static String getMoneyName(String moneytype) {
		String moneyName = "";
		if(moneytype.equals("RMB")) {
			moneyName = "人民币";
		}
		else if(moneytype.equals("USD")) {
			moneyName = "美元";
		}
		else if(moneytype.equals("HKD")) {
			moneyName = "港币";
		}
		return moneyName;
	}
	public static String getFundsType(String zzhbz) {
		String fundsType = "";
		if("1".equals(zzhbz)) {
			fundsType = "主帐号";
		}else {
			fundsType = "辅帐号";
		}
		return fundsType;
	}
	public static String getResult(String req) {
		return req.replace("\\+", "\\*");
	}
	
	public static String checkResult(JSONObject j) throws JSONException {
		String res = null;
		if(j==null) {
			res = "-1";
		}
		else if("error".equals(j.getString("cssweb_code"))) {
			res = j.getString("cssweb_msg");
		}
		return res;
	}
	
	public static String getTradeName(char n) {
		String name = "";
		switch(n)
		{
		case 'B':name = "证券买入";break;
		case 'a':name = "对方买入";break;
		case 'b':name = "本方买入";break;
		case 'c':name = "即时买入";break;
		case 'd':name = "五档买入";break;
		case 'e':name = "全额买入";break;
		case 'q':name = "转限买入";break;
		case 'S':name = "证券卖出";break;
		case 'f':name = "对方卖出";break;
		case 'g':name = "本方卖出";break;
		case 'h':name = "即时卖出";break;
		case 'i':name = "五档卖出";break;
		case 'j':name = "全额卖出";break;
		case 'r':name = "转限买出";break;
		case 'G':name = "转股";break;
		case 'H':name = "回售";break;
		case '3':name = "基金申购";break;
		case '4':name = "基金赎回";break;
		case '5':name = "基金认购";break;
		case '7':name = "行权";break;
		default :name =  "--";break;
		}
		return name;
	}
	
	/**
     * 基金交易代码
     */
    public static String dealFundTrdid(int trdid) {
        String reStr = "-";
        switch( trdid ){
        case 24001: reStr = "开户"; break;
		case 24002: reStr = "销户"; break;
		case 24003: reStr = "账户信息修改"; break;	

		case 24101: reStr = "开户"; break;	
		case 24103: reStr = "账户信息修改"; break;	
		
		case 24020: reStr = "认购申请"; break;
		case 24022:	reStr = "申购申请"; break;
		case 24024:	reStr = "赎回申请"; break;
		
		case 24029:	reStr = "设置分红方式申请"; break;
		case 24129:	reStr = "设置分红方式"; break;
		
		case 24120: reStr = "认购确认"; break;
		case 24122: reStr = "申购确认"; break;
		case 24124: reStr = "赎回确认"; break;
		
		case 24152: reStr = "撤单"; break;
		case 24153: reStr = "撤预约单"; break;
		case 24052: reStr = "撤单"; break;
		case 24053: reStr = "撤预约单"; break;
		
		case 24046: reStr = "配号"; break;
		case 24146: reStr = "配号"; break;
		case 24043: reStr = "红利发放"; break;
		case 24143: reStr = "红利发放"; break;
		
		case 24036: reStr = "基金转换"; break;
		case 24037: reStr = "基金转换转入"; break;
		case 24038: reStr = "基金转换转出"; break;
		case 24136: reStr = "基金转换"; break;
		case 24137: reStr = "基金转换转入"; break;
		case 24138: reStr = "基金转换转出"; break;
         }
         return reStr;
     }

	/***
	 * 获得证券的买卖类别 按照南京证券的文档进行制作
	 */
	public static String getFlagName(int flag,String cancelFlag){
		String flagName = "-";
		switch(flag)
		{
			 case 1 : 
	         	if("W".equals(cancelFlag)) //撤单标志
	         		flagName = "撤买";
	         	else
	         		flagName = "买入";
	         	break;
	         case 2 :
	         	if("W".equals(cancelFlag)) //撤单标志
	         		flagName = "撤卖";
	         	else
	         		flagName = "卖出";
					break;
	         case 3 : flagName = "缴款"; break;
             case 4 : flagName = "融资";    break;
             case 5 : flagName = "融券";    break;
             case 6 : flagName = "红利";    break;
             case 7 : flagName = "转托转出";    break;
             case 8 : flagName = "配号";    break;
             case 9 : flagName = "指定";    break;
             case 10 : flagName = "撤指";    break;
             case 11 : flagName = "转债转股";    break;
             case 12 : flagName = "转债回售";    break;
             case 13 : flagName = "转债赎回";    break;
             case 14 : flagName = "配售申购"; break;
             case 15 : flagName = "转托转入"; break;
             case 16 : flagName = "送股"; break;
             case 17 : flagName = "兑付"; break;
             case 18 : flagName = "托管转入"; break;
             case 19 : flagName = "托管转出"; break;
             case 20 : flagName = "调帐转入"; break;
             case 21 : flagName = "调帐转出"; break;
             case 22 : flagName = "国债派息"; break;
             case 23 : flagName = "投票"; break;
             case 24 : flagName = "配售放弃"; break;
             case 25 : flagName = "质押冻结"; break;
             case 26 : flagName = "解押解冻"; break;
             case 27 : flagName = "无冻质押"; break;
             case 28 : flagName = "无解解押"; break;
             case 29 : flagName = "ETF申购"; break;
             case 30 : flagName = "ETF赎回"; break;
             case 31 : flagName = "协议转让"; break;
             case 32 : flagName = "现金认购"; break;
             case 33 : flagName = "股票认购"; break;
             case 34 : flagName = "融资购回"; break;
             case 35 : flagName = "融券购回"; break;
             case 37 : flagName = "质押入库"; break;
             case 38 : flagName = "质押出库"; break;
             case 41 : flagName = "基金认购"; break;
             case 42 : flagName = "基金申购"; break;
             case 43 : flagName = "基金赎回"; break;
             case 44 : flagName = "分红设置"; break;
             case 45 : flagName = "基金分红"; break;
             case 46 : flagName = "基金转换"; break;
             case 50 : flagName = "权证行权"; break;
             case 51 : flagName = "权证创设"; break;
             case 52 : flagName = "权证注销"; break;
             case 55 : flagName = "意向买入"; break;
             case 56 : flagName = "意向卖出"; break;
             case 57 : flagName = "定价买入"; break;
             case 58 : flagName = "定价卖出"; break;
             case 59 : flagName = "买入确认"; break;
             case 60 : flagName = "卖出确认"; break;
             case 61 : flagName = "合申还款"; break;
             case 62 : flagName = "合申中签"; break;
             case 63 : flagName = "合申卖出"; break;
             case 66 : flagName = "合配缴款"; break;
             case 67 : flagName = "合配卖出"; break;
             case 76 : flagName = "预受要约"; break;
             case 77 : flagName = "解除预受"; break;
             case 80 : flagName = "缴申购款"; break;
             case 81 : flagName = "还申购款"; break;
             case 82 : flagName = "中签通知"; break;
             case 83 : flagName = "缴中签款"; break;
             case 94 : flagName = "限售纳税"; break;
             case 95 : flagName = "清算冻结"; break;
             case 96 : flagName = "调整可卖"; break;
             case 97 : flagName = "手工冻结"; break;
             case 98 : flagName = "成本调整"; break;
             case 99 : flagName = "费用返还"; break;
		}
		return flagName;
	}
	
    /**
     * 报价方式  南京证券的委托方式
     */
     public static String dealOrderType(int mt){
     	String reStr = "-";
        switch(mt){
	         case 0 : reStr = "限价委托";   break;
	         case 1 : reStr = "最优五档立即成交剩余撤单";   break;
	         case 2 : reStr = "最优五档立即成交剩余转限价";   break;
	         case 101 : reStr = "对方最优价格";   break;
	         case 102 : reStr = "本方最优价格";   break;
	         case 103 : reStr = "立即成交否则撤单";   break;
	         case 104 : reStr = "最优五档立即成交否则撤销";   break;
	         case 105 : reStr = "全部成交否则撤单";   break;
         }
         return reStr;
     }

     /**
      * 存取类型
      */
      public static String dealBankTranType(String mt) {
          
    	  String reStr = "-";
          
    	  int order = 0;
    	  
    	  if(mt==null || mt.trim().equals(""))
    	  {
    		  order = 0;
    	  }else {
    		  order = Integer.parseInt(mt);
    	  }
          switch(order){
              
          	  case 1 : reStr = "银行转证券"; break;
              case 1024 : reStr = "预指定存管银行确认"; break;
              case 128 : reStr = "转帐交易核实"; break;
              case 16 : reStr = "撤销存管银行"; break;
              case 16384 : reStr = "信用账户证转银"; break;
              
              case 2 : reStr = "证券转银行"; break;
              case 2048 : reStr = "客户资料修改"; break;
              case 256 : reStr = "批量银行签约确认"; break;
              
              case 32 : reStr = "冲正银行转证券"; break;
              case 32768 : reStr = "信用账户指定存管银行"; break;
              
              case 4 : reStr = "查询余额"; break;
              case 4096 : reStr = "结息入账"; break;
              
              case 512 : reStr = "变更存管银行帐号";     break;
               
              case 64 : reStr = "冲正证券转银行"; break;
              
              case 8 : reStr = "指定存管银行";     break;
              case 8192 : reStr = "信用账户银转证";     break;
          }
          
          return reStr;
      } 
      
      /**
       * 银行列表
       * @return 银行列表的数组 
       * 
       */
      public static String getBankName(String bankCode) {
//      	String[] bankList = ;
    	  String bankName = "";
    	  String st = "ZHCG:中国银行,GHCG:工商银行,ZSCG:招商银行,GDCG:光大银行,JHCG:建设银行,XYCG:兴业银行,NHCG:农行银行,JTCG:交通银行,NJYH:南京银行,PFYH:浦发银行,SFZH:深发展,SKZH:深圳中行,SKZS:深圳招行,GZZH:广州中行,FZZH:福州中行,ZHWB:中行外币,NJZS:南京招商银行,SHCG:上海银行,MSCG:民生存管,HXCG:华夏存管,GFYH:广发银行,ZXYH,:中信银行"; 
//    	  ja.
    	  String[] it = st.split(",");
    	  for(int i=0;i<it.length;i++)
    	  {
    		 String[] keyValue = it[i].split(":");
    		 String key  = keyValue[0];
    		 String value = keyValue[1];
    		 
    		 if(key.equals(bankCode))
    		 {
    			 bankName = value ;
    			 break;
    		 }
    	  }
    	  	return bankName;	
      }
      
  	/**
  	 * 根据柜台返回市场标记显示相应市场名称
  	 * @param mark
  	 * @return markname
  	 * @author hujun
  	 */
  	public static String getMarkName(String mark) {
  		String markname = "";
  		
  		if(mark.equals("SH"))
  		{
  			markname = "沪A";
  		}else if(mark.equals("SZ"))
  		{
  			markname = "深A";
  		}else if(mark.equals("HB"))
  		{
  			markname = "沪B"; 
  		}else if(mark.equals("SB"))
  		{
  			markname = "深B";
  		}else if(mark.equals("TU"))
  		{
  			markname = "转U";
  		}else if(mark.equals("TA"))
  		{
  			markname = "转A";
  		}
//  		switch(mark) {
//  			case '1':markname = "上海A股";break;
//  			case '0':markname = "深圳A股";break;
//  			case '6':markname = "三板A";break;
//  			case '7':markname = "三板B";break;
//  			case '9':markname = "特别转让";break;
//  			case 'A':markname = "非交易所债券";break;
//  			case '3':markname = "上海B股";break;
//  			case '2':markname = "深圳B股";break;
//  			case 'J':markname = "开放式基金";break;
//  		}
  		return markname;
  	}
  	
    /**
	 * 资金查询
	 */
    public static String getFundavl() throws JSONException {
    	StringBuffer sb = new StringBuffer();
		sb.append("FID_BZ=" + TradeUtil.SPLIT);
		sb.append("FID_EXFLG=1");
		JSONObject quoteData = ConnPool.sendReq("GET_FUNDS", "303002", sb.toString());
		String resStr = TradeUtil.checkResult(quoteData);
		if(resStr == null){
			JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
			for(int i=0;i<jArr.length()-1;i++){
				JSONObject  jsonobj = jArr.getJSONObject(i);
				if ("1".equals(jsonobj.getString("FID_ZZHBZ"))){
					String moneytype = jsonobj.getString("FID_BZ");
					if("RMB".equals(moneytype)){
						TradeUser.getInstance().setFundavlRMB(TradeUtil.formatNum(jsonobj.getString("FID_ZHYE"), 2));
						TradeUser.getInstance().setEnablefundavlRMB(TradeUtil.formatNum(jsonobj.getString("FID_KYZJ"), 2));
					}else if("USD".equals(moneytype)){
						TradeUser.getInstance().setFundavlUS(TradeUtil.formatNum(jsonobj.getString("FID_ZHYE"), 3));
						TradeUser.getInstance().setEnablefundavlUS(TradeUtil.formatNum(jsonobj.getString("FID_KYZJ"), 3));
					}else if("HKD".equals(moneytype)){
						TradeUser.getInstance().setFundavlHK(TradeUtil.formatNum(jsonobj.getString("FID_ZHYE"), 3));
						TradeUser.getInstance().setEnablefundavlHK(TradeUtil.formatNum(jsonobj.getString("FID_KYZJ"), 3));
					}
				}
			}
		}
		return TradeUser.getInstance().getEnablefundavlRMB();
	}
//    public static String getFundavlAll() throws JSONException {
//    	double zhyeRMB = 0l;
//    	double zhyeHKD = 0l;
//    	double zhyeUSD = 0l;
//    	double kyzjRMB = 0l;
//    	double kyzjHKD = 0l;
//    	double kyzjUSD = 0l;
//    	StringBuffer sb = new StringBuffer();
//		sb.append("FID_BZ=" + TradeUtil.SPLIT);
//		sb.append("FID_EXFLG=1");
//		JSONObject quoteData = ConnPool.sendReq("GET_FUNDS", "303002", sb.toString());
//		String resStr = TradeUtil.checkResult(quoteData);
//		if(resStr == null){
//			JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
//			for(int i=0;i<jArr.length()-1;i++){
//				JSONObject  jsonobj = jArr.getJSONObject(i);
//				String bz = jsonobj.getString("FID_BZ");
//				double ye = Double.parseDouble(jsonobj.getString("FID_ZHYE"));
//				double ky = Double.parseDouble(jsonobj.getString("FID_KYZJ"));
//				if("RMB".equals(bz)){
//					zhyeRMB += ye;
//					kyzjRMB += ky;
//				}else if("HKD".equals(bz)){
//					zhyeHKD += ye;
//					kyzjHKD += ky;
//				}else if("USD".equals(bz)){
//					zhyeUSD += ye;
//					kyzjUSD += ky;
//				}
//				TradeUser.getInstance().setFundavlRMB(TradeUtil.formatNum(zhyeRMB+"", 2));
//				TradeUser.getInstance().setEnablefundavlRMB(TradeUtil.formatNum(kyzjRMB+"", 2));
//				TradeUser.getInstance().setFundavlHK(TradeUtil.formatNum(zhyeHKD+"", 3));
//				TradeUser.getInstance().setEnablefundavlHK(TradeUtil.formatNum(kyzjHKD+"", 3));
//				TradeUser.getInstance().setFundavlUS(TradeUtil.formatNum(zhyeUSD+"", 3));
//				TradeUser.getInstance().setEnablefundavlUS(TradeUtil.formatNum(kyzjUSD+"", 3));
//			}
//		}
//		return TradeUser.getInstance().getEnablefundavlRMB();
//	}
    //加载基金相关信息
    public static String initFundData(Context context){
//		new Thread() {
//			public void run() {
    	String msg = "";
				String openFundCompanyDate = ActivityUtil.getPreference(context,"openFundCompanyDate", "");
				String openFundInfoDate = ActivityUtil.getPreference(context,"openFundInfoDate", "");
				String openFundAccountDate = ActivityUtil.getPreference(context,"openFundAccountDate", "");
				Log.i(">>>>>>>>加载基金相关数据", "");
				Log.i(">>>>>>>>openFundCompanyDate", openFundCompanyDate);
				Log.i(">>>>>>>>openFundInfoDate", openFundInfoDate);
				Log.i(">>>>>>>>openFundAccountDate", openFundAccountDate);
				String res = "";
				try{
					// *****取基金公司******//
					if (!DateTool.getToday().equals(openFundCompanyDate)) {
						JSONObject fundCompanyData = FundService.getFundCompany();
						res = TradeUtil.checkResult(fundCompanyData);
						if (res != null) {
							if (res.equals("-1")){
								msg = "当前网络中断，请重试！";
							}
							else {
								msg = res;
							}
							return msg;
						}else{
							CssIniFile.saveIni(context, 3, "fundCompany", fundCompanyData.toString());
							ActivityUtil.savePreference(context, "openFundCompanyDate", DateTool.getToday());
						}
					}
					//*****取基金信息******//
					if(!DateTool.getToday().equals(openFundInfoDate)){
						JSONObject fundInfoData = FundService.getFundInfo();
						res = TradeUtil.checkResult(fundInfoData);
						if (res != null) {
							if (res.equals("-1")){
								msg = "当前网络中断，请重试！";
							}
							else {
								msg = res;
							}
							return msg;
						}else {
							//整理基金代码信息取需要字段放入dat文件
							JSONObject tempFundData =  new JSONObject();
							JSONArray tempJsonArray = new JSONArray();
							JSONArray jarr = (JSONArray) fundInfoData.getJSONArray("item");
							for (int i =0 ; i< jarr.length()-1 ; i++ ){
								JSONObject tempObject = new JSONObject();
								JSONObject  jsonobj = jarr.getJSONObject(i);
								tempObject.put("FID_JJDM", jsonobj.getString("FID_JJDM"));
								tempObject.put("FID_TADM", jsonobj.getString("FID_TADM"));
								tempObject.put("FID_JJJC", jsonobj.getString("FID_JJJC"));
								tempObject.put("FID_JJQC", jsonobj.getString("FID_JJQC"));
								tempObject.put("FID_JJJZ", jsonobj.getString("FID_JJJZ"));
								tempObject.put("FID_FHFS", jsonobj.getString("FID_FHFS"));
								tempObject.put("FID_BZ", jsonobj.getString("FID_BZ"));
								tempObject.put("FID_SFFS", jsonobj.getString("FID_SFFS"));
								tempObject.put("FID_JYZT", jsonobj.getString("FID_JYZT"));
								tempObject.put("FID_JJFXDJ", jsonobj.getString("FID_JJFXDJ"));
								tempJsonArray.put(tempObject);
							}
							JSONObject temp = new JSONObject();
							temp.put("cssweb_test : ", "0");
							tempJsonArray.put(temp);
							tempFundData.put("cssweb_code", "success");
							tempFundData.put("cssweb_type", "GET_FUND_INFO");
							tempFundData.put("item", tempJsonArray);
							CssIniFile.saveIni(context, 4, "fundInfo", tempFundData.toString());
							ActivityUtil.savePreference(context, "openFundInfoDate",DateTool.getToday());
						}
					}
					// *****取基金账号******//
					if (!DateTool.getToday().equals(openFundAccountDate)) {
						JSONObject fundAccountInfoData = FundService.getFundAccountInfo();
						res = TradeUtil.checkResult(fundAccountInfoData);
						if (res != null) {
							if (res.equals("-1")){
								msg = "当前网络中断，请重试！";
							}
							else {
								msg = res;
							}
							return msg;
						}else{
							CssIniFile.saveIni(context, 5, "fundAccount", fundAccountInfoData.toString());
							ActivityUtil.savePreference(context, "openFundAccountDate",DateTool.getToday());
						}
					}
				}catch(JSONException e){
					e.printStackTrace();
				}
				return msg;
//			}
//		}.start();
	}
  //加载银行列表
    public static String initBanksData(Context context){
    	String msg = "";
    	String openBanksDate = ActivityUtil.getPreference(context,"openBanksDate", "");
		Log.i(">>>>>>>>加载银行列表", "");
		Log.i(">>>>>>>>openBanksDate", openBanksDate);
		String res = "";
		try{
			// *****取银行列表******//
			if (!DateTool.getToday().equals(openBanksDate)) {
				JSONObject banksData = TradeService.getBanks();
				res = TradeUtil.checkResult(banksData);
				if (res != null) {
					if (res.equals("-1")){
						msg = "当前网络中断，请重试！";
					}
					else {
						msg = res;
					}
					return msg;
				}else{
					CssIniFile.saveIni(context, 8, "banks", banksData.toString());
					ActivityUtil.savePreference(context, "openBanksDate", DateTool.getToday());
				}
			}
		}catch(JSONException e){
			e.printStackTrace();
		}
		return msg;
	}
    public String getZjzhByYhdm(String yhdm,Context context) throws JSONException {
		String zjzh = "";
		JSONObject banksData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openBanksDate", "");
		if(!(filedate).equals(DateTool.getToday()) ){ //如果时间不匹配，重新到柜台获获取
			banksData = TradeService.getBanks();
		}else{
			String jsonObject = CssIniFile.loadIni(context, 8, "banks");
			if(null !=jsonObject && ! jsonObject.equals("")){
				banksData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = banksData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( yhdm.equals(jsonobj.getString("FID_YHDM"))){;
				zjzh = jsonobj.getString("FID_ZJZH");
				break;
			}
		}
		return zjzh;
	}
	public String getFundAccount(String tacode,Context context) throws JSONException {
		String jjzh = "";
		JSONObject fundAccountData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openFundAccountDate", "");
		if(!(filedate).equals(DateTool.getToday()) ){ //如果时间不匹配，重新到柜台获获取
			fundAccountData = FundService.getFundAccountInfo();
		}else{
			String jsonObject = CssIniFile.loadIni(context, 5, "fundAccount");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundAccountData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundAccountData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( tacode.equals(jsonobj.getString("FID_TADM"))){;
				jjzh = jsonobj.getString("FID_JJZH");
				break;
			}
		}
		return jjzh;
	}
	public String getFundCodeName(String ofcode,Context context) throws JSONException {
		String jjjc = "";
		JSONObject fundInfoData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openFundInfoDate", "");
		if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
			fundInfoData = FundService.getFundInfo();
		}else{
			String jsonObject = CssIniFile.loadIni(context, 4, "fundInfo");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundInfoData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundInfoData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( ofcode.equals(jsonobj.getString("FID_JJDM"))){;
				jjjc = jsonobj.getString("FID_JJJC");
				break;
			}
		}
		return jjjc;
	}
	public String getFundCodeNav(String ofcode,Context context) throws JSONException {
		String fundNav = "";
		JSONObject fundInfoData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openFundInfoDate", "");
		if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
			fundInfoData = FundService.getFundInfo();
			
		}else{
			String jsonObject = CssIniFile.loadIni(context, 4, "fundInfo");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundInfoData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundInfoData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( ofcode.equals(jsonobj.getString("FID_JJDM"))){;
				fundNav = jsonobj.getString("FID_JJJZ");
				break;
			}
		}
		return fundNav;
	}
	public String getFundStatus(String ofcode,Context context) throws JSONException {
		String jyzt = "";
		JSONObject fundInfoData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openFundInfoDate", "");
		if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
			fundInfoData = FundService.getFundInfo();
			
		}else{
			String jsonObject = CssIniFile.loadIni(context, 4, "fundInfo");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundInfoData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundInfoData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( ofcode.equals(jsonobj.getString("FID_JJDM"))){;
				jyzt = jsonobj.getString("FID_JYZT");
				break;
			}
		}
		return jyzt;
	}
	
	public String getFundCompanyName(String tacode,Context context) throws JSONException {
		String fundcompanyName = "";
		JSONObject fundCompanyData = new JSONObject();
		String filedate = ActivityUtil.getPreference(context,"openFundCompanyDate", "");
		if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
			fundCompanyData = FundService.getFundCompany();
		}else{
			String jsonObject = CssIniFile.loadIni(context, 3, "fundCompany");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundCompanyData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundCompanyData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			if( tacode.equals(jsonobj.getString("FID_TADM"))){;
				fundcompanyName = jsonobj.getString("FID_JGJC");//FID_JGMC
				break;
			}
		}
		return fundcompanyName;
	}
	public JSONObject getFundByCompany(String tacode,Context context) throws JSONException {
		JSONObject fundInfoData = null;
		JSONObject convertInData = new JSONObject();
		JSONArray convertInarr = new JSONArray();
		String filedate = ActivityUtil.getPreference(context,"openFundInfoDate", "");
		if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
			fundInfoData = FundService.getFundInfo();
		}else{
			String jsonObject = CssIniFile.loadIni(context, 4, "fundInfo");
			if(null !=jsonObject && ! jsonObject.equals("")){
				fundInfoData = new JSONObject(jsonObject);
			}
		}
		JSONArray jarr = fundInfoData.getJSONArray("item");
		for (int i =0 ; i< jarr.length()-1 ; i++ ){
			JSONObject  jsonobj = jarr.getJSONObject(i);
			
			if(!"0".equals(tacode)){
				if( tacode.equals(jsonobj.getString("FID_TADM"))){
					convertInarr.put(jsonobj);
				}
			}else {//全部
				convertInarr.put(jsonobj);
			}
		}
		convertInData.put("item", convertInarr);
		return convertInData;
	}
	public static List<CssStock> fillListToNull(int begin, int end, List<CssStock> list) {
		for(int i=begin; i<end; i++) {
			CssStock cssStock = new CssStock();
			cssStock.setStkname(null);
			cssStock.setStkcode(null);
			cssStock.setZf(0);
			cssStock.setZjcj(0);
			cssStock.setZrsp(0);
			cssStock.setMarket(null);
			cssStock.setExchange(0);
			list.add(cssStock);
		}
		return list;
	}

	public static List<CssStock> fillListToNull(int begin, int end) {
		List<CssStock> list = new LinkedList<CssStock>();
		for(int i=begin; i<end; i++) {
			CssStock cssStock = new CssStock();
			cssStock.setStkname("");
			cssStock.setStkcode("");
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
			cssStock.setMarket("sh");
			cssStock.setExchange(0);
			list.add(cssStock);
		}
		return list;
	}
	
	public static List<CssStock> fillHSZSListToNull(int begin, int end) {
		List<CssStock> list = new LinkedList<CssStock>();
		for(int i=begin; i<end; i++) {
			CssStock cssStock = new CssStock();
			cssStock.setStkname("");
			cssStock.setStkcode("");
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
			cssStock.setMarket("hk");
			cssStock.setExchange(0);
			list.add(cssStock);
		}
		return list;
	}
	
	public static List<CssStockFund> fillListToNull2(int begin , int end){
		List<CssStockFund> list = new ArrayList<CssStockFund>();
		for (int i =begin ; i<end ;i++){
			CssStockFund cssStockFund = new CssStockFund();
			cssStockFund.setStkfunname("");
			cssStockFund.setMarket("sh");
			cssStockFund.setStkcode("");
			cssStockFund.setJjfnpj(0);
			cssStockFund.setJjtnpj(0);
			cssStockFund.setJz(0);
			cssStockFund.setLjjz(0);
			list.add(cssStockFund);
		}
		return list ;
	}
	public static List<String []> fillListToNull3(int begin , int end){
		List<String []> list = new ArrayList<String[]>();
		for (int i = begin ; i <end ;i++){
			String  [] arr = new String [2];
			arr[0]="";
			arr[1]="";
			list.add(arr);
		}
		return list;
	}
	public static List<String []> fillListToNull4(int begin , int end){
		List<String []> list = new ArrayList<String[]>();
		for (int i = begin ; i <end ;i++){
			String  [] arr = new String [9];
			arr[0]="";
			arr[1]="0";
			arr[2]="0";
			arr[3]="0";
			arr[4]="0";
			arr[5]="0";
			arr[6]="0";
			arr[7]="0";
			arr[8]="0";
			list.add(arr);
		}
		return list;
	}
	
	public static List<String []> fillListToNull5(int begin , int end){
		List<String []> list = new ArrayList<String[]>();
		for (int i = begin ; i <end ;i++){
			String  [] arr = new String [7];
			arr[0]="";
			arr[1]="0";
			arr[2]="0";
			arr[3]="0";
			arr[4]="0";
			arr[5]="0";
			arr[6]="0";
			list.add(arr);
		}
		return list;
	}
	
	public static List<String []> fillListToNull6(int begin , int end , String stocks ,String stocksname){
		List<String []> list = new ArrayList<String[]>();
		String[] temp1 =null;
		String[] temp2 =null;
		int t = 0;
		if (null !=stocks){
			temp1 = stocks.split(",");
			if(null !=temp1){
				t = temp1.length;
			}
		}
		if (null !=stocksname){
			temp2 = stocksname.split(",");
		}
		
		for (int i = begin ; i <end ;i++){
			String  [] arr = new String [24];
			if (i > t-1){
				arr[0]="";
				arr[1]="";
			}else{
				arr[0]=temp1[i].substring(2);
				arr[1]=temp2[i];
			}
			
			arr[2]="0";
			arr[3]="0";
			arr[4]="0";
			arr[5]="0";
			arr[6]="0";
			arr[7]="0";
			arr[8]="0";
			arr[9]="0";
			arr[10]="0";
			arr[11]="0";
			arr[12]="0";
			arr[13]="0";
			arr[14]="0";
			arr[15]="0";
			arr[16]="0";
			arr[17]="0";
			arr[18]="0";
			arr[19]="0";
			arr[20]="0";
			arr[21]="3";
			arr[22]="0";
			arr[23]="0";
			list.add(arr);
		}
		return list;
	}
	public static String formateStatus(char status) {
		String dealStatus = "";
		switch(status) {
		case '0':dealStatus = "未报";break;
		case '1':dealStatus = "正报";break;
		case '2':dealStatus = "已报";break;
		case '3':dealStatus = "已报待撤";break;
		case '4':dealStatus = "部成待撤";break;
		case '5':dealStatus = "部撤";break;
		case '6':dealStatus = "已撤";break;
		case '7':dealStatus = "部成";break;
		case '8':dealStatus = "已成";break;
		case '9':dealStatus = "废单";break;
		case 'A':dealStatus = "待报";break;
		case 'B':dealStatus = "正报";break;
		}
		return dealStatus;
	}
	
	public static String dealFundSBJG(int status) {
		String dealStatus = "";
		switch(status) {
			case -1:dealStatus = "内部撤销";break;
			case 0:dealStatus = "未申报";break;
			case 1:dealStatus = "已申报";break;
			case 2:dealStatus = "已申报";break;
		}
		return dealStatus;
	}
	
	

	public static String dealFundStatus(int n) {
		String dealStatus = "";
		switch (n ) {
			case 0: dealStatus = "正常用户"; break;
			case 1: dealStatus = "冻结"; break;
			case 2: dealStatus = "挂失"; break;
			case 4: dealStatus = "销户"; break;
			case 8: dealStatus = "开户申请中"; break;
			case 16: dealStatus = "销户申请中"; break;
			case 64: dealStatus = "内部冻结"; break;
			default: dealStatus = "其他"; break;
		}
		return dealStatus;
	}
	
	
	
	public static String formateBankStatus(char status) {
		String dealStatus = "";
		switch(status) {
		case '0':dealStatus = "未报";break;
		case '1':dealStatus = "已报";break;
		case '2':dealStatus = "成功";break;
		case '3':dealStatus = "失败";break;
		case '4':dealStatus = "超时";break;
		case '5':dealStatus = "待冲正";break;
		case '6':dealStatus = "已冲正";break;
		case '7':dealStatus = "调整为成功";break;
		case '8':dealStatus = "调整为失败";break;
		}
		return dealStatus;
	}
	
	/**
	 * 格式化银证转帐业务类型
	 * @param banktranId
	 * @return
	 */
	public static String formatBankTranName(char banktranId){
		String dealName = "";
		switch (banktranId) {
		case '1':dealName = "银行转证券";break;
		case '2':dealName = "证券转银行";break;
		}
		return dealName;
	}
	
	public static String formateMatchType(char status) {
		String dealStatus = "";
		switch(status) {
		case '0':dealStatus = "普通成交";break;
		case '1':dealStatus = "撤单成交";break;
		case '2':dealStatus = "废单";break;
		case '3':dealStatus = "内部撤单";break;
		case '4':dealStatus = "撤单废单";break;
		}
		return dealStatus;
	}
	
	
	
	/**
	 * 格式化收费方式
	 * @param status
	 * @return
	 */
	public static String formateShareClass(int status) {
		String dealStatus = "";
		switch(status) {
		case 1: dealStatus = "前端收费"; break;
		case 2: dealStatus = "后端收费"; break;
		default:dealStatus = "其他";break;
		}
		return dealStatus;
	}
	
	
	/**
	 * 格式化分红方式名称
	 * @param status
	 * @return
	 */
	public static String formateDividend(int status) {
		String dealStatus = "";
		switch(status) {
		case 1:dealStatus = "红利转投";break;
		case 2:dealStatus = "现金分红";break;
		default:dealStatus = "--";break;
		}
		return dealStatus;
	}
	
	
	/**
	 * 根据柜台返回市场标记显示相应市场名称
	 * @param mark
	 * @return markname
	 * @author hujun
	 */
	public static String getMarkName(char mark) {
		String markname = "";
		switch(mark) {
		case '1':markname = "上海A股";break;
		case '0':markname = "深圳A股";break;
		case '6':markname = "三板A";break;
		case '7':markname = "三板B";break;
		case '9':markname = "特别转让";break;
		case 'A':markname = "非交易所债券";break;
		case '3':markname = "上海B股";break;
		case '2':markname = "深圳B股";break;
		case 'J':markname = "开放式基金";break;
		}
		return markname;
	}
	//根据传入的市场 获取 市场的类别
	public static String getMarket(char market) {
		String value = "";
		switch(market)
		{
			//根据传入的市场转换成南京证券的市场代码
			case '1':
				value = "SH";
				break;
		}
		return value;
	}
	
	
	/**
	 * 根据柜台返回市场标记显示相应市场名称
	 * @param risk
	 * @return riskname
	 * @author hujun
	 */
	public static String getRiskName(int risk) {
		String riskname = "";
		switch(risk) {
		case 1:riskname = "低风险";break;
		case 2:riskname = "中风险-AAA";break;
		case 3:riskname = "中风险-AA";break;
		case 4:riskname = "中风险-A";break;
		case 5:riskname = "中风险";break;
		case 6:riskname = "高风险-AAA";break;
		case 7:riskname = "高风险-AA";break;
		case 8:riskname = "高风险-A";break;
		case 9:riskname = "高风险";break;
		default: riskname = "风险未知";break;
		}
		return riskname;
	}
	/**
     * 业务科目
     * @param mt 业务科目编码
     * @return 
     * 
     */
    public static String dealBusinessAction(int mt){
        String reStr = "-";
        switch( mt ){
        	case 10101 : reStr = "收入"; break;//现金存
        	case 10103 : reStr = "收入"; break;//花旗银行存
        	case 10112 : reStr = "收入"; break;//内部转账转入
        	case 10113 : reStr = "收入"; break;//银证转账存
        	case 10116 : reStr = "收入"; break;//信用账户银转证
        	case 10201 : reStr = "支出"; break;//现金取
        	case 10203 : reStr = "支出"; break;//花旗银行取
        	case 10212 : reStr = "支出"; break;//内部转账转出
        	case 10213 : reStr = "支出"; break;//银证转账取
        	case 10501 : reStr = "收入"; break;//销户利息入本金
        	case 10502 : reStr = "收入"; break;//利差返还入本金
        	case 10503 : reStr = "收入"; break;//季度利息如本金
        	case 13001 : reStr = "支出"; break; //买入成交清算资金
        	case 13011 : reStr = "支出"; break;//配股缴款
        	case 13015 : reStr = "支出"; break;//行权资金划出
        	case 13031 : reStr = "支出"; break;//新股申购成本划出
        	case 13032 : reStr = "支出"; break;//新股中签成本划出
        	case 13101 : reStr = "收入"; break; //卖出成交清算资金
        	case 13115 : reStr = "支出"; break;//行权资金划入
        	case 13131 : reStr = "收入"; break;//新股申购成本归还
        	case 13201 : reStr = "收入"; break;//股票红利划入
        	case 13202 : reStr = "收入"; break;//债券利息划入
        	case 13203 : reStr = "支出"; break;//红利所得税
        	case 14001 : reStr = "支出"; break;//认购资金划出(委托)
        	case 14002 : reStr = "认购资金划出(确认)"; break;//
        	case 14003 : reStr = "支出"; break;//申购资金划出(委托)
        	case 14004 : reStr = "申购资金划出(确认)"; break;//
        	case 14101 : reStr = "收入"; break;//认购失败资金返还
        	case 14102 : reStr = "收入"; break;//申购失败资金返还
        	case 14103 : reStr = "收入"; break;//赎回资金划入
        	case 14201 : reStr = "收入"; break;//开放式基金现金分红
        	case 14202 : reStr = "收入"; break;//开放式基金手续费返还
        }
        return reStr;
    }
    public static String dealFundRiskType(String fxdj){
        String reStr = "";
        if ("0".equals(fxdj))
    	{
    		reStr = "未定义";
    	}
    	else if	("1".equals(fxdj))
    	{
    		reStr = "保守";
    	}
    	else if	("2".equals(fxdj))
    	{
    		reStr = "稳健";
    	}
    	else if	("3".equals(fxdj))
    	{
    		reStr = "积极";
    	}
    	else if	("4".equals(fxdj))
    	{
    		reStr = "激进";
    	}

        return reStr;
    }
	/**
	 * 格式化小数
	 */
	public static String formatNum(String str, int len){
	 	if (str==null) return "";
	 	int flag = 1;
	 	if(str.charAt(0)== '-' && str.charAt(1)=='.'){
	 		str = str.replace("-.","-0.");
	 		flag = -1;
	 	}
	 	if (str.charAt(0)=='.') str = "0" + str; 
	 	if(len<0) len = 0;
	 	BigDecimal small = new BigDecimal("0");
	 	BigDecimal big = new BigDecimal("0");
	 	if (("0").equals(str) && len==0) return "0";
	 	if(str.indexOf(".")!=-1){
	 		String[] ary = str.split("\\.");
	 		big = BigDecimal.valueOf(Double.parseDouble(ary[0]));
	 		small = BigDecimal.valueOf(Double.parseDouble("0."+ary[1])); 
	 		small = small.setScale(len>0 ? len:1, BigDecimal.ROUND_HALF_UP);
	 		if (str.charAt(0) == '-') {
	 			return big.subtract(small).multiply(new BigDecimal(flag)).setScale(len, BigDecimal.ROUND_HALF_UP).toString();
	 		}
	 		return big.add(small).multiply(new BigDecimal(flag)).setScale(len, BigDecimal.ROUND_HALF_UP).toString();
	 	}else{
	 		return new BigDecimal(str).multiply(new BigDecimal(flag)).setScale(len, BigDecimal.ROUND_HALF_UP).toString();
	 	}  
	}
	
	/**
	 * 字符串转时间
	 */
	public static String StringConvertTime(String str){
		try{
			String result =null;
			if(null !=str){
				if(str.length()==8){
					result = str.substring(0, 2)+":"+str.substring(2, 4)+":"+str.substring(4,6);
				}else if(str.length()==7){
					result ="0"+str.substring(0,1)+":"+str.substring(1, 3)+":"+str.substring(3, 5);
				}
				return result ;
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
		return null;
	}
	

	
	public static boolean checkUserLogin() {
		int loginType = TradeUser.getInstance().getLoginType();
		if(loginType == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * 检查字符是否为正数
	 * @param value
	 * @param falg 是否为整数, true是,false不是
	 * @return true是,false不是
	 */
	public static boolean checkNumber(String value, boolean falg){
		try {
			if(falg){
				int intvalue = Integer.parseInt(value);
				if(intvalue<=0){
					return false;
				}
				return true;
			}else{
				double doublevalue = Double.parseDouble(value);
				if(doublevalue<=0){
					return false;
				}
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}
	//电话号码检查
	public static boolean checkPhone(String phone) {
		//String regex = "^\\(?\\d{3,4}[-\\)]?\\d{7,8}$";
		String regex = "((\\d{11})|^((\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})|(\\d{4}|\\d{3})-(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1})|(\\d{7,8})-(\\d{4}|\\d{3}|\\d{2}|\\d{1}))$)";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(phone);
		return matcher.find();
	}
	// 在正则表达式中表示开始，$:表示结束：[^@]:表示不能存在@；一定注意加开头和结束标志
	public static boolean checkEmail(String email) {
		//String regex = "^\\w+@\\w+\\.(com\\.cn)|\\w+@\\w+\\.(com|cn)$";
		//struts2源代码的验证
		String regex = "\\b(^['_A-Za-z0-9-]+(\\.['_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\\.[A-Za-z0-9-]+)*((\\.[A-Za-z0-9]{2,})|(\\.[A-Za-z0-9]{2,}\\.[A-Za-z0-9]{2,}))$)\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(email);
		return matcher.find();
	}
	
}
