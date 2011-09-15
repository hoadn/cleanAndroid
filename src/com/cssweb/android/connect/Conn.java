package com.cssweb.android.connect;

import org.json.JSONObject;

public class Conn {
	public static String getData(String req) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		String j = null;
		if(conFlag)
			j = conn.getData();
		conn.close();
		return j;
	}
	
	public static String getF10Data(String req) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		String j = null;
		if(conFlag)
			j = conn.getF10Data();
		conn.close();
		return j;
	}
	
	/**
	 * 此接口专门用于查用户等级,上面的接口发现在2.3的系统下有时候会不正常
	 * @param req
	 * @return
	 */
	public static String getUserLevel(String req) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		String j = null;
		if(conFlag)
			j = conn.getUserLevel();
		conn.close();
		return j;
	}
	
	public static JSONObject execute(String req) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		JSONObject j = null;
		if(conFlag)
			j = conn.execute();
		conn.close();
		return j;
	}
	
	public static JSONObject execute(String req, boolean flag) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		JSONObject j = null;
		if(conFlag)
			j = conn.execute(flag);
		conn.close();
		return j;
	}
	
	public static JSONObject tradeReq(String req) {
		Connection conn = new Connection(req);
		boolean conFlag = conn.connect();
		JSONObject j = null;
		if(conFlag)
			j = conn.tradeReq();
		conn.close();
		return j;
	}
}
