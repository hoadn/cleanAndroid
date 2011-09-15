/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)BasicView.java 上午10:22:03 2010-10-29
 */
package com.cssweb.android.view.base;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

/**
 * 画图基类
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class BasicView extends View implements IBasicView {
	//320分辨率下的高宽
	protected int M_TEXT_SIZE = 16;
	protected int D_TEXT_SIZE = 15;
	protected int S_TEXT_SIZE = 13;
	protected int DX_W = 20;
	protected int DY_H = 26;

	//小字体
	protected int sTextSize;
	protected int mTextSize;
	protected int dTextSize;
	protected int DX;
	protected int DY;
	
	public BasicView(Context context) {
		super(context);
	}

    public BasicView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BasicView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
	
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
    
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    }
}
