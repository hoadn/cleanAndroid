/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CustomButton.java 下午04:02:53 2010-11-7
 */
package com.cssweb.android.custom;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.view.View;

/**
 * 自定义按钮
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class CustomButton extends View {
	public CustomButton(Context paramContext) {
		super(paramContext);
	}

	public CustomButton(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}

	public CustomButton(Context paramContext, AttributeSet paramAttributeSet,
			int paramInt) {
		super(paramContext, paramAttributeSet, paramInt);
	}

	public StateListDrawable setbg(Integer[] mImageIds) {
        StateListDrawable bg = new StateListDrawable();
        Drawable normal = this.getResources().getDrawable(mImageIds[0]);
        Drawable selected = this.getResources().getDrawable(mImageIds[1]);
        Drawable pressed = this.getResources().getDrawable(mImageIds[2]);
        bg.addState(View.PRESSED_ENABLED_STATE_SET, pressed);
        bg.addState(View.ENABLED_FOCUSED_STATE_SET, selected);
        bg.addState(View.ENABLED_STATE_SET, normal);
        bg.addState(View.FOCUSED_STATE_SET, selected);
        bg.addState(View.EMPTY_STATE_SET, normal);
        return bg;
    }
}
