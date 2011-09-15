/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CustomScrollView.java 下午02:17:07 2011-3-9
 */
package com.cssweb.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ScrollView;
 
public class CustomScrollView extends ScrollView {
 
	private GestureDetector gestureDetector;
 
    public CustomScrollView(Context context) {
		super(context);
	}
 
	public CustomScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
 
	public CustomScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
 
	public void setGestureDetector(GestureDetector gestureDetector) {
		this.gestureDetector = gestureDetector;
	}
 
	@Override
	public boolean onTouchEvent(MotionEvent event) {
	    super.onTouchEvent(event);
	    return gestureDetector.onTouchEvent(event);
	}
 
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev){
	    gestureDetector.onTouchEvent(ev);
	    super.dispatchTouchEvent(ev);
	    return true;
	} 
 
}
