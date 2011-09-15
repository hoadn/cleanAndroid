/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)UserStockAdapter.java 下午02:59:34 2011-3-16
 */
package com.cssweb.android.quote.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class UserStockAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResource;
	private int mCurrentRow;
	private int mPageNum;
	private List<CssStock> list;
	
	public UserStockAdapter(Context context, List<CssStock> list, int paramInt, int currentRow, int pageNum) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.mResource = paramInt;
		this.mCurrentRow = currentRow;
		this.mPageNum = pageNum;
	}
	
	public int getCount() {
		return mPageNum;
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
    		if(list.get(position).getStkcode()!=null&&!list.get(position).getStkcode().equals("")) {
    			//先判断小数位数
        		if(css.getStkcode()!=null)
        			mDigit = Utils.getNumFormat(NameRule.getExchange(css.getMarket()), css.getStkcode());
                TextView item1 = (TextView) v.findViewById(R.id.zr_rt_col1);
                TextView item2 = (TextView) v.findViewById(R.id.zr_rt_col2);
                TextView item3 = (TextView) v.findViewById(R.id.zr_rt_col3);
                TextView item4 = (TextView) v.findViewById(R.id.zr_rt_col4);
                TextView item5 = (TextView) v.findViewById(R.id.zr_rt_col5);
                setRol1(item1, css.getStkcode());
                setRol3(item2, css.getStkname());
                setRol2(item3, css.getZjcj(), css.getZrsp(), mDigit);
                setRol4(item4, css.getZd(), mDigit);
                setRol5(item5, css.getZf());
                if(position==mCurrentRow) {
                	v.setBackgroundResource(R.drawable.zr_table_xkz);
                }
                else {
                	v.setBackgroundResource(R.drawable.zr_table_xk);
                }
                item5.setOnClickListener(new View.OnClickListener() {
    	
    				public void onClick(View view) {
    					FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(list.get(position).getMarket()), list.get(position).getStkcode(), list.get(position).getStkname(), mContext);
    				}
    			});
    		}
    	}
	}

	private void setRol1(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(15);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol3(TextView paramTextView, String paramString) {
		if(paramString!=null&&paramString.trim().length()>4)
			paramTextView.setTextSize(12);
		else 
			paramTextView.setTextSize(15);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol2(TextView paramTextView, double paramDouble, double paramDouble2, int mDigit) {
		paramTextView.setTextSize(13);
		if(paramDouble==0)
			paramTextView.setText(Utils.dataFormation(paramDouble2, mDigit));
		else
			paramTextView.setText(Utils.dataFormation(paramDouble, mDigit));
		paramTextView.setTextColor(Utils.getTextColor(mContext, paramDouble, paramDouble2));
	}
	
	private void setRol4(TextView paramTextView, double paramDouble, int mDigit) {
		paramTextView.setTextSize(15);
		paramTextView.setText(Utils.dataFormation(paramDouble, mDigit));
		paramTextView.setTextColor(Utils.getTextColor(mContext, paramDouble));
	}
	
	private void setRol5(TextView paramTextView, double paramDouble) {
		paramTextView.setTextSize(15);
		paramTextView.setText(Utils.dataFormation(paramDouble*100, 1) + "%");
		paramTextView.setTextColor(Utils.getTextColor(mContext, 0));
		if(paramDouble<0) 
			paramTextView.setBackgroundResource(R.drawable.zrreportdownbg);
		else
			paramTextView.setBackgroundResource(R.drawable.zrreportupbg);
	}
}
