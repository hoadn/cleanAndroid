/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)ConnectionPool.java 下午02:42:51 2010-9-14
 */
package com.cssweb.android.connect;

import java.io.UnsupportedEncodingException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cssweb.android.common.Base64;
import com.cssweb.android.common.Config;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.util.TradeUtil;

/**
 * 交易部分发送请求
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class ConnPool {
	public static JSONObject sendReq(String funcname, String funcno, String reqbuf) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append(TradeUtil.getGlobalRequest(funcname, funcno));
		sb.append(reqbuf);
		sb.append("&ram="+Math.random());
		String sbStr = sb.toString();
		if("190101".equals(funcno)||"203113".equals(funcno) || "203111".equals(funcno)|| "203526".equals(funcno)|| 
			"203119".equals(funcno)||"202010".equals(funcno)||"202012".equals(funcno)){
			sbStr = sbStr.replace("isSafe=0", "isSafe=1");
		}
		Log.i("请求原文==", sbStr);
		String req="";
		try {
			req = new String(Base64.encode(sbStr.getBytes("gb2312")));
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		req = TradeUtil.getResult(req);

		Log.i(">>>>>url>>>>>>>", getURL() + req);
		return Conn.tradeReq(getURL() + req);
	}
	
	public static JSONObject servicePasswordLogin(String custno , String password){
		StringBuffer sb = new StringBuffer ();
		sb.append("https://jy.njzq.cn/service/login/Login/mobileClientServLogin.do");
		sb.append("?clientNo=");
		sb.append(custno);
		sb.append("&password=");
		sb.append(password); 
		sb.append("&ram=");
		sb.append(Math.random()); 
		Log.i("?>>>>>url>>>>>>>", sb.toString() );
		return Conn.execute(sb.toString());
	}
	
	/**
	 * 修改资金密码
	 * @param funcname 功能名称
	 * @param funcno 功能号
	 * @param oldpass 老密码
	 * @param newpass 新密码
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject modifyFundPass(String funcname, String funcno, String oldpass, String newpass) throws JSONException {
		StringBuffer sb = new StringBuffer();
		
		sb.append(TradeUtil.getGlobalRequest(funcname, funcno));
		sb.append("trdpwd=" + TradeUser.getInstance().getPassword() + TradeUtil.SPLIT);
		sb.append("oldfundpwd=" + new String(Base64.encode(oldpass.getBytes())) + TradeUtil.SPLIT);
		sb.append("newfundpwd=" + new String(Base64.encode(newpass.getBytes())));
		
		String req = new String(Base64.encode(sb.toString().getBytes()));
		req = TradeUtil.getResult(req);

		return Conn.tradeReq(getURL() + req);
	}
	
	/**
	 * 修改交易密码
	 * @param funcname 功能名称
	 * @param funcno 功能号
	 * @param oldpass 老密码
	 * @param newpass 新密码
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject modifyTradePass(String funcname, String funcno, String oldpass, String newpass) throws JSONException {
		StringBuffer sb = new StringBuffer();
		
		sb.append(TradeUtil.getGlobalRequest(funcname, funcno));
		sb.append("trdpwd=" + new String(Base64.encode(oldpass.getBytes())) + TradeUtil.SPLIT);
		sb.append("newpwd=" + new String(Base64.encode(newpass.getBytes())));

		String req = new String(Base64.encode(sb.toString().getBytes()));
		req = TradeUtil.getResult(req);

		return Conn.tradeReq(getURL() + req);
	}
	
	public static String getURL(){
		String url = Config.roadJywgIp;//默认线路一 走https
		if (!"".equals(Config.roadJywgPort) && Config.roadJywgPort!=null) {//线路2 走 鼓楼的http通道
			url = "http://"+ Config.roadJywgIp +":"+ Config.roadJywgPort +"/?";
		}
		return url;
	}
}
