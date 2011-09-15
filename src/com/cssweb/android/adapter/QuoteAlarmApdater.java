/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuoteAlarmApdater.java 下午04:51:47 2010-12-9
 */
package com.cssweb.android.adapter;

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
public class QuoteAlarmApdater extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResource;
	private List<CssStock> list;
	
	public QuoteAlarmApdater(Context context, List<CssStock> list, int paramInt, int beginCol) {
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
	
	private void bindView(View v, int position) {
		int tLength = list.size() - 1;
    	int mDigit = 1;
    	double d0 = 0;
    	if(position<=tLength) {
			CssStock cs = list.get(position);
			d0 = cs.getZrsp();
			if(cs.getStkcode()!=null&&cs.getMarket()!=null) {
				//先判断小数位数
				mDigit = Utils.getNumFormat(cs.getMarket(), cs.getStkcode());
	            TextView item0 = (TextView) v.findViewById(R.id.zr_rt_col0);
	            TextView item1 = (TextView) v.findViewById(R.id.zr_rt_col1);
	            TextView item2 = (TextView) v.findViewById(R.id.zr_rt_col2);
	            TextView item3 = (TextView) v.findViewById(R.id.zr_rt_col3);
	            TextView item4 = (TextView) v.findViewById(R.id.zr_rt_col4);
	            setRol0(item0, cs.getStkcode());
	            setRol1(item1, cs.getStkname());
	            setRol2(item2, cs.getZjcj(), d0, mDigit);
	            setRol3(item3, cs.getSpj() + "\n" + cs.getZf());
	            setRol4(item4, cs.getXpj() + "\n" + cs.getDt());
			}
    	}
	}
	
	private void setRol0(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(12);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol1(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(18);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol2(TextView paramTextView, double paramDouble, double paramDouble2, int mDigit) {
		paramTextView.setTextSize(18);
		//paramTextView.setText(Utils.dataFormation(paramDouble, mDigit));
		paramTextView.setText(Utils.dataFormation(paramDouble, mDigit,0));
		paramTextView.setTextColor(Utils.getTextColor(mContext, paramDouble, paramDouble2));
	}
	
	private void setRol3(TextView paramTextView, String parmStr) {
		paramTextView.setTextSize(18);
		paramTextView.setText(parmStr);
		//paramTextView.setSingleLine(false);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 3));
	}
	
	private void setRol4(TextView paramTextView, String parmStr) {
		paramTextView.setTextSize(18);
		paramTextView.setText(parmStr);
		//paramTextView.setSingleLine(false);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 4));
	}
}