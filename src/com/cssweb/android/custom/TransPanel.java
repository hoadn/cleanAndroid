/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)TransPanel.java 下午08:34:10 2010-11-14
 */
package com.cssweb.android.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * 自定义面板
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TransPanel extends LinearLayout {
	private Paint borderPaint;
	private Paint innerPaint;

	public TransPanel(Context paramContext) {
		super(paramContext);
		init();
	}

	public TransPanel(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		init();
	}

	private void init() {
		Paint localPaint1 = new Paint();
		this.innerPaint = localPaint1;
		this.innerPaint.setARGB(255, 245, 245, 245);
		this.innerPaint.setAntiAlias(true);
		Paint localPaint2 = new Paint();
		this.borderPaint = localPaint2;
		this.borderPaint.setARGB(255, 255, 255, 255);
		this.borderPaint.setAntiAlias(true);
		Paint localPaint3 = this.borderPaint;
		Paint.Style localStyle = Paint.Style.STROKE;
		localPaint3.setStyle(localStyle);
	}

	protected void dispatchDraw(Canvas paramCanvas) {
		RectF localRectF = new RectF();
		float f1 = getMeasuredWidth();
		float f2 = getMeasuredHeight();
		localRectF.set(0, 0, f1, f2);
		Paint localPaint = this.innerPaint;
		paramCanvas.drawRoundRect(localRectF, 0, 0, localPaint);
		super.dispatchDraw(paramCanvas);
	}

	public void setBorderPaint(Paint paramPaint) {
		this.borderPaint = paramPaint;
	}

	public void setInnerPaint(Paint paramPaint) {
		this.innerPaint = paramPaint;
	}
}