package com.cssweb.android.trade.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.trade.util.TradeUtil;

public class BankService {
	/**
	 * 资金调拨流水查询
	 */
	public static JSONObject getTransferFundsList(String startDate,String endDate,String ywkm,String seqno) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_SORTTYPE=1"+TradeUtil.SPLIT);
		sb.append("FID_KSRQ=" + startDate + TradeUtil.SPLIT);
		sb.append("FID_JSRQ=" + endDate + TradeUtil.SPLIT);
		sb.append("FID_YWKM=" + ywkm + TradeUtil.SPLIT);
		sb.append("FID_BROWINDEX=" +seqno+ TradeUtil.SPLIT);
		sb.append("FID_ROWCOUNT=200");
		return ConnPool.sendReq("TRANSFER_BANKS_QUERY", "303010", sb.toString());
	}
	/**
	 * 资金流水查询
	 */
	public static JSONObject getTransferList(String startDate,String endDate,String bz,String yhdm,String seqno) throws JSONException {
		StringBuffer sb = new StringBuffer();
		sb.append("FID_KSRQ=" + startDate + TradeUtil.SPLIT);
		sb.append("FID_JSRQ=" + endDate + TradeUtil.SPLIT);
		sb.append("FID_BZ=" + bz + TradeUtil.SPLIT);
		sb.append("FID_YHDM=" + yhdm + TradeUtil.SPLIT);
		sb.append("FID_SORTTYPE=1" + TradeUtil.SPLIT);
		sb.append("FID_ROWCOUNT=200" + TradeUtil.SPLIT);
		sb.append("FID_BROWINDEX=" +seqno);
		return ConnPool.sendReq("TRANSFER_QUERY", "403204", sb.toString());
	}
}
