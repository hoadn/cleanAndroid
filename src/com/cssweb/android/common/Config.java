/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Config.java 下午12:23:31 2011-3-24
 */
package com.cssweb.android.common;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;

import com.cssweb.android.domain.Road;
import com.cssweb.android.domain.Server;
import com.cssweb.android.service.XMLService;

/**
 * 系统配置
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Config {
	public static String version = "版本号：1.0 beta 1(0304)";
	public static String defaultServerUrl = "0";
	public static String currServerUrl = "0";
	public static String roadHqdlqIp = "hq.njzq.cn";
	
	public static String roadName = "线路1";
	public static String roadZixun = "http://iweb.njzq.cn/";
	public static String roadWsyyt = "https://jy.njzq.cn/";
	public static String roadWebcall = "http://webcall.njzq.cn/";
	public static String roadMncg = "http://vtrade.njzq.cn/";
	
	public static String roadHqyj = "http://122.96.60.47/";
	public static String roadValidation = "http://122.96.60.48/";
	public static String roadJywgIp = "njzqhx.njzq.cn";
	public static String roadJywgPort = "8000";
	public static String roadHqfwqIp = "njzqhx.njzq.cn";
	public static String roadHqfwqPort = "9000";
	
	public static String roadf10 = "http://hq.njzq.cn/";
	
	
	public static ArrayList<HashMap<String, Object>> mapBitmap = new ArrayList<HashMap<String,Object>>();

	
	public static int lockTime = 5;

	public static int fenleirefresh = 3 * 1000;
	public static int zongherefresh = 3 * 1000;
	public static int fenshirefresh = 3 * 1000;
	public static int kxrefresh = 3 * 1000;
	public static int yujingrefresh = 3 * 1000;
	public static int hkquoterefresh = 3 * 1000 ;
	
	public Config(Context context) {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				"server", Context.MODE_PRIVATE);
    	String serverRoad = sharedPreferences.getString("road", "线路1");
    	try {
			InputStream inputStream = context.getAssets().open("server.xml");
			Server server = XMLService.getServer(inputStream);
			Road road = server.getRoad(serverRoad);
			
			version = server.getVersion();
			defaultServerUrl = server.getDefaultServerUrl();
			currServerUrl = server.getCurrServerUrl();
			roadName = road.getName();
			roadZixun = road.getZixun();
			roadWsyyt = road.getWsyyt();
			roadWebcall = road.getWebcall();
			roadMncg = road.getMncg();
			roadHqyj = road.getHqyj();
			roadValidation = road.getValidation();
			roadJywgIp = road.getJywg().getIp();
			roadJywgPort = road.getJywg().getPort();
			roadHqfwqIp = road.getHqfwq().getIp();
			roadHqfwqPort = road.getHqfwq().getPort();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		sharedPreferences = context.getSharedPreferences(
				"lock", Context.MODE_PRIVATE);

		lockTime = Integer.parseInt(sharedPreferences.getString("locktime", "5"));
	
		sharedPreferences = context.getSharedPreferences(
				"hqrefresh", Context.MODE_PRIVATE);
		fenleirefresh = Integer.parseInt(sharedPreferences.getString("fenleirefresh", "3"))*1000;
		zongherefresh = Integer.parseInt(sharedPreferences.getString("zongherefresh", "3"))*1000;
		fenshirefresh = Integer.parseInt(sharedPreferences.getString("fenshirefresh", "3"))*1000;
		kxrefresh = Integer.parseInt(sharedPreferences.getString("kxrefresh", "3"))*1000;
		yujingrefresh = Integer.parseInt(sharedPreferences.getString("yujingrefresh", "3"))*1000;
		
	}
}
