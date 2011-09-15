/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)ShadowButton.java 下午06:03:35 2010-10-13
 */
package com.cssweb.android.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * 自定义按钮
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class ShadowButton extends Button {

	public ShadowButton(Context paramContext) {
		super(paramContext);
	}

	public ShadowButton(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public ShadowButton(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}
	
	protected void onDraw(Canvas paramCanvas) {
		super.onDraw(paramCanvas);
	}
}
