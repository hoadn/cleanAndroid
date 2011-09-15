/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)MyGrid.java 上午10:03:46 2010-10-3
 */
package com.cssweb.android.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

/**
 * 自定义gridview
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class MyGrid extends GridView implements OnItemClickListener {

	private Listener mListener;

	public MyGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOnItemClickListener(this);
	}

	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		if (mListener != null) {
			mListener.onClick(position);
		}
	}

	public void setListener(Listener l) {
		mListener = l;
	}

	public interface Listener {
		void onClick(int position);
	}
}
