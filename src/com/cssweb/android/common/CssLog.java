/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CssLog.java 上午09:40:18 2010-10-29
 */
package com.cssweb.android.common;

import android.util.Log;

/**
 * Log日志处理
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class CssLog {

	public static void d(String tag, String msg) {
		Log.d(tag, msg);
	}

	public static void e(String tag, String msg) {
		Log.e(tag, msg);
	}

	public static void i(String tag, String msg) {
		Log.i(tag, msg);
	}

	public static void v(String tag, String msg) {
		Log.v(tag, msg);
	}

	public static void w(String tag, String msg) {
		Log.w(tag, msg);
	}
}
