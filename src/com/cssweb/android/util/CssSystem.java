/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CssSystem.java 下午07:28:15 2010-10-3
 */
package com.cssweb.android.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.DisplayMetrics;

/**
 * 公共参数设置动态
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class CssSystem {
	
	public static String exchange;
	public static String stockcode;
	public static String stockname;
	
	/* 屏幕的宽高 */
	public static int	SCREENW;
	public static int	SCREENH;
	
	//自选股类型,默认是0
	public static int   MYSTOCK_TYPE = 0;
	
	//定义堆大小
	public final static int CWJ_HEAP_SIZE = 6* 1024* 1024 ;
    
    //增强程序堆内存的处理效率
	public final static float TARGET_HEAP_UTILIZATION = 0.75f;
	
	
	private static void initCssSystem(Context m_context) {
	    DisplayMetrics d = m_context.getApplicationContext().getResources().getDisplayMetrics();
	    SCREENW = d.widthPixels;
	    SCREENH = d.heightPixels;
	}
	
	/**
	 * 自选股在三种分辨率下面显示行数
	 * @param m_context
	 * @return
	 */
	public static int getMyStockPageSize(Context m_context) {
		initCssSystem(m_context);
		int pageSize = 6;
		if(SCREENH==480) 
			pageSize = 4;
		else if(SCREENH==800) 
			pageSize = 5;
		else if(SCREENH==854) 
			pageSize = 6;
		return pageSize;
	}
	
	/**
	 * 类似于综合排名、自选股这种情况在三种分辨率下的表格行数
	 * @param m_context
	 * @return
	 */
	public static int getTablePageSize(Context m_context) {
		initCssSystem(m_context);
		int pageSize = 10;
		if(SCREENH==480) 
			pageSize = 8;
		else if(SCREENH==800) 
			pageSize = 9;
		else if(SCREENH==854) 
			pageSize = 10;
		return pageSize;
	}
	
	/**
	 * 由于图片原因，不能刚好显示，因此根据三种分辨率增加了点高度
	 * @param m_context
	 * @return
	 */
	public static int getTableRowHeight(Context m_context) {
		initCssSystem(m_context);
		int pageSize = 2;
		if(SCREENH==480) 
			pageSize = 0;
		else if(SCREENH==800) 
			pageSize = 2;
		else if(SCREENH==854) 
			pageSize = 2;
		return pageSize;
	}
	
	/**
	 * 由于图片原因，不能刚好显示，因此根据三种分辨率增加了点高度
	 * @param m_context
	 * @return
	 */
	public static int getTableTitleHeight(Context m_context) {
		initCssSystem(m_context);
		int pageSize = 3;
		if(SCREENH==480) 
			pageSize = 3;
		else if(SCREENH==800) 
			pageSize = 3;
		else if(SCREENH==854) 
			pageSize = 0;
		return pageSize;
	}
	
	public static boolean getHardPixes(Context m_context) {
		initCssSystem(m_context);
//		if(SCREENW==240&&SCREENH==320) 
//			return true;
		if(SCREENW==320&&SCREENH==480) 
			return true;
		else if(SCREENW==480&&SCREENH==800) 
			return true;
		else if(SCREENW==480&&SCREENH==854) 
			return true;
		return false;
	}
	
    public static int getSDKVersionNumber() {
 	   int sdkVersion;
 	   try {
 	     sdkVersion = Integer.valueOf(android.os.Build.VERSION.SDK);
 	   } catch (NumberFormatException e) {
 	     sdkVersion = 0;
 	   }
 	   return sdkVersion;
 	}
    
    public static String getSoftVersionName(Context m_context) {
    	PackageManager manager = m_context.getPackageManager();
    	try { 
    	   PackageInfo info = manager.getPackageInfo(m_context.getPackageName(), 0);
    	   //String packageName = info.packageName;
    	   //int versionCode = info.versionCode;
    	   return info.versionName; 
    	} catch (NameNotFoundException e) {
    		return null;
    	}
    }
}
