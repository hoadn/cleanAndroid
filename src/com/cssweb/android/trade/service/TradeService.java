package com.cssweb.android.trade.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.cssweb.android.common.Config;
import com.cssweb.android.connect.Conn;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.util.TradeUtil;

public class TradeService {
	/**
	 * 最大可买查询
	 */
	public static JSONObject getMaxQtyBuy(String market,String secuid,String stkcode,String bsflag,String price,String orderType) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_JYS="+ market + TradeUtil.SPLIT);
		sb.append("FID_GDH="+ secuid+ TradeUtil.SPLIT);
		sb.append("FID_ZQDM="+ stkcode + TradeUtil.SPLIT);
		sb.append("FID_WTLB="+ bsflag+ TradeUtil.SPLIT);
		sb.append("FID_WTJG="+ price + TradeUtil.SPLIT);
		sb.append("FID_DDLX="+ orderType);
		return ConnPool.sendReq("GET_MAXQTY_B", "204503", sb.toString());
	}
	/**
	 * 持仓查询
	 */
	public static JSONObject getStockPosition() throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_EXFLG=1");
		return ConnPool.sendReq("GET_STOCK_POSITION", "304101", sb.toString());
	}
	/**
	 * 修改交易密码
	 */
	public static JSONObject modifyTradePass(String oldpwd,String newpwd) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_MM="+ oldpwd + TradeUtil.SPLIT);
		sb.append("FID_NEWMM="+ newpwd+ TradeUtil.SPLIT);
		sb.append("FID_MMLB=1"+ TradeUtil.SPLIT);
		sb.append("FID_KHYYB="+ TradeUser.getInstance().getOrgid()+ TradeUtil.SPLIT);
		sb.append("FID_KHQZ="+ TradeUser.getInstance().getCustGroup());
		return ConnPool.sendReq("CUST_MODIFY_TRADE_PWD", "202010", sb.toString());
	}
	/**
	 * 修改资金密码
	 */
	public static JSONObject modifyFundPass(String oldpwd,String newpwd) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_MM="+ oldpwd + TradeUtil.SPLIT);
		sb.append("FID_NEWMM="+ newpwd+ TradeUtil.SPLIT);
		sb.append("FID_KHYYB="+ TradeUser.getInstance().getOrgid()+ TradeUtil.SPLIT);
		sb.append("FID_KHQZ="+ TradeUser.getInstance().getCustGroup());
		return ConnPool.sendReq("MODIFY_FUND_PASSWORD", "202012", sb.toString());
	}
	/**
	 * 银行列表查询
	 */
	public static JSONObject getBanks() throws JSONException {
		return ConnPool.sendReq("GET_OPEN_BANK", "303103", "");
	}
	/**
	 * 体验用户登录
	 */
	public static JSONObject tyyhLogin(String tyyhId,String tyyhPwd) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(Config.roadZixun);
		sb.append("iphone/browseexperuserinfo/BrowseExperUserInfo/experLogin.do?");
		sb.append("&clientNo=");
		sb.append(tyyhId);
		sb.append("&password=");
		sb.append(tyyhPwd);
		String s = Conn.getData(sb.toString());
		if(null !=s ){
			JSONObject j = new JSONObject(s);
			return j;
		}
		return null;
	}
}
