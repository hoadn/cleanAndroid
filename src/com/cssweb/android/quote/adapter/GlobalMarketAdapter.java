/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)GlobalMarketAdapter.java 下午03:59:45 2011-3-12
 */
package com.cssweb.android.quote.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.quote.util.Utils;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class GlobalMarketAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResource;
	private List<CssStock> list;
	
	public GlobalMarketAdapter(Context context, List<CssStock> list, int paramInt) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.mResource = paramInt;
	}
	
	public int getCount() {
		return Global.PAGE_SIZE;
	}

	public Object getItem(int position) {
		return Integer.valueOf(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
        if (convertView == null) {
            v = mInflater.inflate(mResource, null);
        } else {
            v = convertView;
        }
        bindView(v, position);
        return v;
	}
	
	private void bindView(final View v, final int position) {
		int tLength = list.size() - 1;
    	int mDigit = 1;
    	if(position<=tLength) {
    		CssStock css = list.get(position);
    		if(list.get(position).getStkname()!=null&&!list.get(position).getStkname().equals("")) {
                TextView item1 = (TextView) v.findViewById(R.id.zr_rt_col1);
                TextView item2 = (TextView) v.findViewById(R.id.zr_rt_col2);
                TextView item3 = (TextView) v.findViewById(R.id.zr_rt_col3);
                TextView item4 = (TextView) v.findViewById(R.id.zr_rt_col4);
                setRol1(item1, css.getStkname());
                setRol2(item2, css.getZjcj(), 0, mDigit);
                setRol3(item3, css.getZd());
                setRol4(item4, css.getZf());
    		}
    	}
	}

	private void setRol1(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(18);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol2(TextView paramTextView, double paramDouble, double paramDouble2, int mDigit) {
		paramTextView.setTextSize(18);
		paramTextView.setText(Utils.dataFormation(paramDouble, mDigit));
		paramTextView.setTextColor(Utils.getTextColor(mContext, paramDouble, paramDouble2));
	}
	
	private void setRol3(TextView paramTextView, double paramDouble) {
		paramTextView.setTextSize(18);
		paramTextView.setText(Utils.dataFormation(paramDouble, 1));
		if(paramDouble<0) 
			paramTextView.setTextColor(Utils.getTextColor(mContext, 4));
		else
			paramTextView.setTextColor(Utils.getTextColor(mContext, 3));
	}
	
	private void setRol4(TextView paramTextView, double paramDouble) {
		paramTextView.setTextSize(18);
		if(paramDouble==0)
			paramTextView.setText(Utils.dataFormation(paramDouble*100, 1));
		else
			paramTextView.setText(Utils.dataFormation(paramDouble*100, 1) + "%");
		if(paramDouble<0) 
			paramTextView.setTextColor(Utils.getTextColor(mContext, 4));
		else
			paramTextView.setTextColor(Utils.getTextColor(mContext, 3));
	}
}
