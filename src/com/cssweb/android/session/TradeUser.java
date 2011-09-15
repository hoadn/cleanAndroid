/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)TradeUser.java 下午04:42:13 2010-8-20
 */
package com.cssweb.android.session;

import java.util.ArrayList;
import java.util.List;

/**
 * 交易用户
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TradeUser {
	//基本资料
	private String orgid;
	private String orgName;
	private String fundid;
	private String custid;
	private String password;
	private String realName;
	private String bankcode;
	private String custGroup;

	//证券账户
	private String secuidSha;
	private String secuidShb;
	private String secuidSza;
	private String secuidSzb;
	//上次登录
	private String lastIP;
	private String lastTime;
	//本次登录
	private String thisIP;
	private String thisTime;
	//预留信息
	private String resverMsg;
	//风测
	private String riskLevel;
	private String riskLevelName;
	private String riskScore;
	
	private String fundavlRMB; //当前人民币余额
	private String fundavlUS;  //当前美元余额
	private String fundavlHK;  //当前港币余额
	
	private String enablefundavlRMB; //可用人民币余额
	private String enablefundavlUS;  //可用美元余额
	private String enablefundavlHK;  //可用港币余额

	//用户类型：交易用户(trade)，浏览用户(anonymous)，体验用户
	private String userType;
	//1 交易密码登陆  ,2服务密码登陆 ,3体验卡登陆 ,4 注册用户（模拟炒股专用）
	private int loginType;
	private int userLevel;
	
	private String isSafe;
	
	private String clientip;
	private String hardinfo;
	private String mac;
	
	private List<String> holder = new ArrayList<String>();
	
	private static TradeUser INSTANCE=new TradeUser();
	
	//用来备份一份 存放的 股东账号，用来在切换股东账号的时候使用
	/**
	 * @author andy
	 */
	private List<String> holderBak = new ArrayList<String>();

	private TradeUser() {

	}

	public static TradeUser getInstance() {
		return INSTANCE;
	}
	
	public String getOrgid() {
		return orgid;
	}
	public void setOrgid(String orgid) {
		this.orgid = orgid;
	}
	public String getOrgName() {
		return orgName;
	}
	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}
	public String getFundid() {
		return fundid;
	}
	public void setFundid(String fundid) {
		this.fundid = fundid;
	}
	public String getCustid() {
		return custid;
	}
	public void setCustid(String custid) {
		this.custid = custid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getRealName() {
		return realName;
	}
	public void setRealName(String realName) {
		this.realName = realName;
	}
	public String getBankcode() {
		return bankcode;
	}
	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}
	public String getSecuidSha() {
		return secuidSha;
	}
	public void setSecuidSha(String secuidSha) {
		this.secuidSha = secuidSha;
	}
	public String getSecuidShb() {
		return secuidShb;
	}
	public void setSecuidShb(String secuidShb) {
		this.secuidShb = secuidShb;
	}
	public String getSecuidSza() {
		return secuidSza;
	}
	public void setSecuidSza(String secuidSza) {
		this.secuidSza = secuidSza;
	}
	public String getSecuidSzb() {
		return secuidSzb;
	}
	public void setSecuidSzb(String secuidSzb) {
		this.secuidSzb = secuidSzb;
	}
	public String getLastIP() {
		return lastIP;
	}
	public void setLastIP(String lastIP) {
		this.lastIP = lastIP;
	}
	public String getLastTime() {
		return lastTime;
	}
	public void setLastTime(String lastTime) {
		this.lastTime = lastTime;
	}
	public String getThisIP() {
		return thisIP;
	}
	public void setThisIP(String thisIP) {
		this.thisIP = thisIP;
	}
	public String getThisTime() {
		return thisTime;
	}
	public void setThisTime(String thisTime) {
		this.thisTime = thisTime;
	}
	public String getResverMsg() {
		return resverMsg;
	}
	public void setResverMsg(String resverMsg) {
		this.resverMsg = resverMsg;
	}
	public String getRiskLevel() {
		return riskLevel;
	}
	public void setRiskLevel(String riskLevel) {
		this.riskLevel = riskLevel;
	}
	public String getRiskLevelName() {
		return riskLevelName;
	}
	public void setRiskLevelName(String riskLevelName) {
		this.riskLevelName = riskLevelName;
	}
	public String getRiskScore() {
		return riskScore;
	}
	public void setRiskScore(String riskScore) {
		this.riskScore = riskScore;
	}
	public String getFundavlRMB() {
		return fundavlRMB;
	}
	public void setFundavlRMB(String fundavlRMB) {
		this.fundavlRMB = fundavlRMB;
	}
	public String getFundavlUS() {
		return fundavlUS;
	}
	public void setFundavlUS(String fundavlUS) {
		this.fundavlUS = fundavlUS;
	}
	public String getFundavlHK() {
		return fundavlHK;
	}
	public void setFundavlHK(String fundavlHK) {
		this.fundavlHK = fundavlHK;
	}
	public String getUserType() {
		return userType;
	}
	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getClientip() {
		return clientip;
	}

	public void setClientip(String clientip) {
		this.clientip = clientip;
	}

	public String getHardinfo() {
		return hardinfo;
	}

	public void setHardinfo(String hardinfo) {
		this.hardinfo = hardinfo;
	}

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getIsSafe() {
		return isSafe;
	}

	public void setIsSafe(String isSafe) {
		this.isSafe = isSafe;
	}

	public List<String> getHolder() {
		return holder;
	}

	public void setHolder(List<String> holder) {
		this.holder = holder;
	}
	
	public String getEnablefundavlRMB() {
		return enablefundavlRMB;
	}

	public void setEnablefundavlRMB(String enablefundavlRMB) {
		this.enablefundavlRMB = enablefundavlRMB;
	}

	public String getEnablefundavlUS() {
		return enablefundavlUS;
	}

	public void setEnablefundavlUS(String enablefundavlUS) {
		this.enablefundavlUS = enablefundavlUS;
	}

	public String getEnablefundavlHK() {
		return enablefundavlHK;
	}

	public void setEnablefundavlHK(String enablefundavlHK) {
		this.enablefundavlHK = enablefundavlHK;
	}

	public static TradeUser getINSTANCE() {
		return INSTANCE;
	}

	public static void setINSTANCE(TradeUser iNSTANCE) {
		INSTANCE = iNSTANCE;
	}

	public List<String> getHolderBak() {
		return holderBak;
	}

	public void setHolderBak(List<String> holderBak) {
		this.holderBak = holderBak;
	}

	public int getUserLevel() {
		return userLevel;
	}

	public void setUserLevel(int userLevel) {
		this.userLevel = userLevel;
	}

	public int getLoginType() {
		return loginType;
	}

	public void setLoginType(int loginType) {
		this.loginType = loginType;
	}

	public String getCustGroup() {
		return custGroup;
	}

	public void setCustGroup(String custGroup) {
		this.custGroup = custGroup;
	}
}
