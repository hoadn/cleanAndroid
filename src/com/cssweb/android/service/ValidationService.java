package com.cssweb.android.service;

import com.cssweb.android.common.AESAgloism;
import com.cssweb.android.common.Config;
import com.cssweb.android.connect.Conn;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.util.TradeUtil;

public class ValidationService {

	/**
	 * 取得服务器时间
	 * @return
	 */
	public static String getServiceTime() {
		int loginType = TradeUser.getInstance().getLoginType();
		String url = Config.roadValidation + "login/getDbTime.jsp?ram=";
		String sTime = Conn.getData(url);
		StringBuffer sb = new StringBuffer();
		if(loginType==0) {
			sb.append("null");
			sb.append("$");
			sb.append("null");
			sb.append("$");
			sb.append(sTime);
		}
		else {
			sb.append(TradeUser.getInstance().getLoginType());
			sb.append("$");
			sb.append(TradeUser.getInstance().getCustid());
			sb.append("$");
			sb.append(sTime);
		}
		
		try {
			String str = new String(sb.toString().getBytes("UTF-8"),"GBK");
			return AESAgloism.encrypt(str.getBytes(), TradeUtil.g_pubKey_old.getBytes());
			//兼容新的密钥
			//return AESAgloism.encrypt(str.getBytes()) + "&cssweb_codekey=1.1";
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String getSMSServiceTime() {
		int loginType = TradeUser.getInstance().getLoginType();
		String url = Config.roadValidation + "login/getDbTime.jsp?ram=";
		String sTime = Conn.getData(url);
		StringBuffer sb = new StringBuffer();
		if(loginType!=1&&loginType!=2) {
			sb.append(sTime);
		}
		try {
			String str = new String(sb.toString().getBytes("UTF-8"),"GBK");
			return AESAgloism.encrypt(str.getBytes(), TradeUtil.g_pubKey_old.getBytes());
			//兼容新的密钥
			//return AESAgloism.encrypt(str.getBytes()) + "&cssweb_codekey=1.1";
		} catch (Exception e) {
			return null;
		}
	}
}
