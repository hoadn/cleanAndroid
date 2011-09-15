package com.cssweb.android.trade.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.trade.util.TradeUtil;

public class FundService{
	
	/**
	 * 基金公司查询
	 */
	public static JSONObject getFundCompany() throws JSONException {
		StringBuffer sb = new StringBuffer();
		return ConnPool.sendReq("FUND_GET_COMPANY", "105001", sb.toString());
	}
	/**
	 * 基金代码查询
	 */
	public static JSONObject getFundInfo() throws JSONException {
		StringBuffer sb = new StringBuffer();
		return ConnPool.sendReq("GET_FUND_INFO", "105002", sb.toString());
	}
	/**
	 * 基金账号查询
	 */
	public static JSONObject getFundAccountInfo() throws JSONException {
		StringBuffer sb = new StringBuffer();
		return ConnPool.sendReq("FUND_GET_ACCOUNT_INFO", "305001", sb.toString());
	}
	/**
	 * 基金认购/申购/赎回
	 */
	public static JSONObject getFundBuySale(String ofcode,String tacode,String taacc,String shareclass,String orderamt,String ywdm,String flag) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_FLAG="+ flag + TradeUtil.SPLIT);
		sb.append("FID_JJDM="+ ofcode+ TradeUtil.SPLIT);
		sb.append("FID_WTJE="+ orderamt + TradeUtil.SPLIT);
		sb.append("FID_TADM="+ tacode + TradeUtil.SPLIT);
		sb.append("FID_YWDM="+ ywdm + TradeUtil.SPLIT);
		sb.append("FID_JJZH="+ taacc + TradeUtil.SPLIT);
		sb.append("FID_SFFS="+ shareclass);
		return ConnPool.sendReq("FUND_GET_BUY_SALE", "205021", sb.toString());
	}
	/**
	 * 基金当日委托/撤单查询
	 */
	public static JSONObject getTodayEntrust() throws JSONException {
		StringBuffer sb = new StringBuffer();
		return ConnPool.sendReq("FUND_GET_TODAY_ENTRUST", "305003", sb.toString());
	}
	/**
	 * 基金撤单提交
	 */
	public static JSONObject fundCancel(String sno) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_WTH="+ sno);
		return ConnPool.sendReq("FUND_CANCEL", "205011", sb.toString());
	}
	/**
	 * 基金份额查询
	 */
	public static JSONObject getFundPosition() throws JSONException {
		StringBuffer sb = new StringBuffer();
		return ConnPool.sendReq("FUND_GET_POSITION", "305002", sb.toString());
	}
	/**
	 * 基金转换
	 */
	public JSONObject fundTransfer(String ofcode1,String ofcode2,String qty,String tacode,String shareclass,String fhfs,String taacc) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_JJZH="+taacc+TradeUtil.SPLIT);
		sb.append("FID_SFFS="+shareclass+TradeUtil.SPLIT);
		sb.append("FID_WTFE="+qty+TradeUtil.SPLIT);
		sb.append("FID_DFJJDM="+ofcode2+TradeUtil.SPLIT);
		sb.append("FID_TADM="+tacode+TradeUtil.SPLIT);
		sb.append("FID_JJDM="+ofcode1+TradeUtil.SPLIT);
		sb.append("FID_FHFS="+fhfs+TradeUtil.SPLIT);
		sb.append("FID_YWDM=036"+TradeUtil.SPLIT);
		return ConnPool.sendReq("FUND_TRANSFER", "205025", sb.toString());
	}
	/**
	 * 基金分红
	 */
	public static JSONObject fundDividend(String ofcode,String tacode,String taacc,String shareclass,String dividmethod) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_TADM="+tacode+TradeUtil.SPLIT);
		sb.append("FID_JJZH="+taacc+TradeUtil.SPLIT);
		sb.append("FID_JJDM="+ofcode+TradeUtil.SPLIT);
		sb.append("FID_SFFS="+shareclass+TradeUtil.SPLIT);
		sb.append("FID_FHFS="+dividmethod+TradeUtil.SPLIT);
		return ConnPool.sendReq("FUND_DIVIDEND", "205022", sb.toString());
	}
	/**
	 * 基金历史委托查询
	 */
	public static JSONObject getHistoryEntrust(String startDate,String endDate) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_KSRQ="+startDate+TradeUtil.SPLIT);
		sb.append("FID_JSRQ="+endDate+TradeUtil.SPLIT);
		return ConnPool.sendReq("FUND_GET_HISTORY_ENTRUST", "405001", sb.toString());
	}
	/**
	 * 基金历史成交查询
	 */
	public static JSONObject getHistoryTrade(String startDate,String endDate) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_KSRQ="+startDate+TradeUtil.SPLIT);
		sb.append("FID_JSRQ="+endDate+TradeUtil.SPLIT);
		return ConnPool.sendReq("FUND_GET_HISTORY_TRADE", "405003", sb.toString());
	}
	/**
	 * 客户信息查询
	 */
	public static JSONObject getCustInfo() throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_EXFLG=1"+TradeUtil.SPLIT);
		return ConnPool.sendReq("CUST_GET_CUST_INFO", "302001", sb.toString());
	}
	/**
	 * 基金开户提交
	 */
	public static JSONObject newFundAccount(String telno,String mobileno,String postId,String addr,String email,String tacode,String taacc) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_ZLLB=99"+TradeUtil.SPLIT);
		sb.append("FID_DH="+telno+TradeUtil.SPLIT);
		sb.append("FID_MOBILE="+telno+TradeUtil.SPLIT);
		sb.append("FID_YZBM="+telno+TradeUtil.SPLIT);
		sb.append("FID_EMAIL="+telno+TradeUtil.SPLIT);
		sb.append("FID_DZ="+addr+TradeUtil.SPLIT);
		sb.append("FID_TADM="+tacode+TradeUtil.SPLIT);
		sb.append("FID_JSLX=1"+TradeUtil.SPLIT);
		sb.append("FID_JJZH="+taacc+TradeUtil.SPLIT);
		return ConnPool.sendReq("FUND_NEW_ACCOUNT", "205004", sb.toString());
	}
	/**
	 * 设置用户的风险等级
	 */
	public static JSONObject setFundRiskLevel(String risk_level,String zy) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_ZLLB=99"+TradeUtil.SPLIT);
		sb.append("FID_TZZFL="+risk_level+TradeUtil.SPLIT);
		sb.append("FID_ZY="+zy);
		return ConnPool.sendReq("FUND_SET_USER_RISK_LEVEL", "202002", sb.toString());
	}
}
