/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)BroadcastReceiver.java 上午10:55:51 2011-3-26
 */
package com.cssweb.android.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cssweb.android.util.ActivityUtil;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class AutoReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//ActivityUtil.saveAlarmRecord(context);
		ActivityUtil.ALARM_RECORED++;
	}
}
