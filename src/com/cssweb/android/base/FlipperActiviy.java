/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FlipperActiviy.java 下午10:41:35 2011-3-20
 */
package com.cssweb.android.base;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

import com.cssweb.android.common.Global;

/**
 * 滑动界面继承
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FlipperActiviy extends DialogActivity implements OnTouchListener, GestureDetector.OnGestureListener {

	protected GestureDetector gestureDetector;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    gestureDetector = new GestureDetector(this);
	}
	
	public boolean onTouchEvent(MotionEvent motionEvent) {
		return gestureDetector.onTouchEvent(motionEvent);
	}

	public boolean onDown(MotionEvent e) {
		return false;
	}

	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
			float velocityY) {
		if(e1==null||e2==null) return false;
		if (e1.getX() - e2.getX() > Global.FLING_MIN_DISTANCE   
                && Math.abs(velocityX) > Global.FLING_MIN_VELOCITY) {   
            // Fling left   
			moveColLeft();
        } else if (e2.getX() - e1.getX() > Global.FLING_MIN_DISTANCE   
                && Math.abs(velocityX) > Global.FLING_MIN_VELOCITY) {   
            // Fling right   
        	moveColRight();
        } else if (e1.getY() - e2.getY() > Global.FLING_MIN_DISTANCE   
                && Math.abs(velocityY) > Global.FLING_MIN_VELOCITY) {   
            // Fling bottom to top
        	moveColBottom();
        } else if (e2.getY() - e1.getY() > Global.FLING_MIN_DISTANCE   
                && Math.abs(velocityY) > Global.FLING_MIN_VELOCITY) {   
            // Fling top to bottom
        	moveColTop();
        }    
		return false;
	}

	public void onLongPress(MotionEvent e) {
		
	}

	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
			float distanceY) {
		return false;
	}

	public void onShowPress(MotionEvent e) {
		
	}

	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	public boolean onTouch(View v, MotionEvent event) {
		return gestureDetector.onTouchEvent(event);
	}

	/**
	 * 向上滑动
	 */
	protected void moveColTop() {
		
	}
	
	/**
	 * 向下滑动
	 */
	protected void moveColBottom() {
		
	}
	
	/**
	 * 向左滑动
	 */
	protected void moveColLeft() {
		
	}
	
	/**
	 * 向右滑动
	 */
	protected void moveColRight() {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
}
