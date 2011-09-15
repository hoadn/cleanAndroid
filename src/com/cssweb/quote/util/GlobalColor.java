/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Global.java 下午02:00:55 2010-8-23
 */
package com.cssweb.quote.util;

import android.graphics.Color;

/**
 * 全局变量
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public interface GlobalColor {
	
	public static final int colorpriceUp    = Color.RED;// price up
	public static final int colorPriceDown  = Color.GREEN;// price down
	public static final int colorPriceDn    = Color.BLUE;// price down
	public static final int colorPriceEqual = Color.WHITE;// price equal
	public static final int colorStockName  = Color.YELLOW;
	public static final int colorLabelName  = Color.WHITE;
	public static final int colorTimeName   = Color.WHITE;
	public static final int colorLine	    = Color.WHITE;
	public static final int clrLine  		= Color.RED;
	public static final int clrGrayLine  	= Color.LTGRAY;
	public static final int colorSky  		= Color.BLUE;
	public static final int clearSCREEN  	= Color.BLACK;
	public static final int colorKlinePopub = Color.LTGRAY;
	
	
	/**
	 * 分时 分钟价格线颜色
	 */
	public static int colorFZLine  = Color.WHITE; // tick price line//0xFFFFFF
	
	/**
	 * 分时 分钟价格平均线颜色
	 */
	public static int colorFZAvePriceLine  = Color.YELLOW; // tick price average line
	
	/**
	 * 分时 成交量颜色
	 */
	public static int colorVolumeLine  = Color.YELLOW; // tick volume line
	
	/**
	 * 分时 成交量线颜色
	 */
	public static int clr_tick_volume  = Color.YELLOW; // tick volume line
	
	/**
	 * 分时 成交金额线颜色
	 */
	public static int colorAmountLine  = 0xFFFF00; // tick amount line
	public static int colorTicklabel = Color.RED; // tick label
	
	public static int colorM5  = Color.WHITE;
	public static int colorM10 = Color.YELLOW;
	public static int colorM20 = Color.BLUE;
	public static int colorM60 = Color.GREEN;
	public static int colorKdown  = Color.RED;
	public static int colorKup    = 0x000000;
	public static int colorKping  = 0xffffff;
	public static int colorKlabel = Color.RED;
}
