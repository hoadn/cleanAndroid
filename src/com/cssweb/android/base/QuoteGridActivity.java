/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuoteGridActivity.java 上午09:40:57 2011-3-7
 */
package com.cssweb.android.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 行情表格实现部分
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class QuoteGridActivity extends FlipperActiviy {
	private Context mContext = QuoteGridActivity.this;

	protected List<CssStock> list = new ArrayList<CssStock>();

	private Paint mPaint;

	private Drawable localDrawable = null;
	private View.OnClickListener mClickListener;
	private View.OnLongClickListener mLongClickListener;
	private int mFontSize = 18;
	private float textWeight = 0;
	private LinearLayout mLinerHScroll;
	private LinearLayout mLinerLock;
	private int residCol;
	private int m_nPos = 0;
	private int[] residScrollCol;
	private int residSelColor;
	private int residTitleCol;
	private int[] residTitleScrollCol;
	
	protected int rowHeight = 0;
	protected int len = 0;
	protected int selectTag = -1;
	
	private boolean nameOrcode = true;
	private String zqname;
	private String zqcode;
	

	protected int n1 = 2, n2 = 2;
	private String top = "↓";
	private String low = "↑";
	public QuoteGridActivity() {
		Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPaint = localPaint;
		this.mPaint.setTextSize(this.mFontSize);
		textWeight = this.mPaint.measureText("上证指数");
		int[] arrayOfInt1 = new int[3];
		this.residTitleScrollCol = arrayOfInt1;
		this.residTitleCol = 0;
		this.residCol = 0;
		int[] arrayOfInt2 = new int[3];
		this.residScrollCol = arrayOfInt2;
		this.residSelColor = Color.DKGRAY;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
        this.residTitleCol = R.drawable.zrfundquery11;
        this.residTitleScrollCol[0] = R.drawable.zrfundqueryh02;
        this.residTitleScrollCol[1] = R.drawable.zrfundqueryh03;
        this.residTitleScrollCol[2] = R.drawable.zrfundqueryh04;
        this.residCol = R.drawable.zrfundquery02;
        this.residScrollCol[0] = R.drawable.zrfundqueryh05;
        this.residScrollCol[1] = R.drawable.zrfundqueryh06;
        this.residScrollCol[2] = R.drawable.zrfundqueryh07;
        mClickListener = new TextClick();
        mLongClickListener = new TextLongClick();
	}
	
	
	
	//初始化证券名称及代号
	protected void initQuote(int pageNum, String stocks , String stocksname) throws JSONException {
        String[] temp1 = stocks.split(",");
		String[] temp2 = stocksname.split(",");
		try {
			TextView localView = (TextView) this.mLinerLock.getChildAt(0);
			if(nameOrcode){
				localView.setText(zqname);
			}
			else {
				localView.setText(zqcode);
			}
			
			for(int i=1; i<=pageNum; i++) {
				TextView localView6 = (TextView) this.mLinerLock
						.findViewWithTag(i);
				if(nameOrcode){
					localView6.setTextSize(13);
					localView6.setText(temp2[i-1]);
				}
				else {
					localView6.setTextSize(18);
				    localView6.setText(temp1[i-1].substring(2));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
	
	//恒生指数
	protected void refreshHSZSUI(List<CssStock> list, String[] cols) throws JSONException {
        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
        this.mLinerLock = localLinearLayout1;
        this.mLinerHScroll = localLinearLayout2;
        this.mLinerLock.removeAllViews();
        this.mLinerHScroll.removeAllViews();

        if(nameOrcode)
        	AddViewItem(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        else
        	AddViewItem(cols[1], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        
    	LinearLayout l1 = new LinearLayout(this); 
        for(int i=2; i<cols.length; i++) {
        	if(i==cols.length-1)
                AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, 100, 0, true);
        	else
        		AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, i-1, 0, true);
        }
        
        mLinerHScroll.addView(l1);
        
    	int mDigit = 1;
    	double d0 = 0;
        for(int i=1; i<=list.size(); i++) {
        	CssStock cs = list.get(i-1);
        	try {
				d0 = cs.getZrsp();
				//先判断小数位数
				mDigit = Utils.getNumFormat(cs.getMarket(), cs.getStkcode());
				if(nameOrcode)
					AddViewItem(cs.getStkname(), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
				else 
					AddViewItem(cs.getStkcode(), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
				
				l1 = new LinearLayout(this); 
				l1.setTag(i);

				if(cs.getStkcode()==null||cs.getStkcode().equals("") ||
						cs.getStkname()==null||cs.getStkname().equals("")) {
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 1, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 2, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 3, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 4, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 5, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 6, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 7, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 8, i, false);
				    AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 9, i, false);
				    
				}
				else {
				    String str1;
					if(cs.getZf()==0)
						str1 = Utils.dataFormation(cs.getZf()*100, 1);
					else
						str1 = Utils.dataFormation(cs.getZf()*100, 1);
					AddViewItem(Utils.dataFormation(cs.getZjcj(), mDigit), Utils.getTextColor(mContext, cs.getZjcj(), d0), l1, i, 1,  i, true);
					AddViewItem(str1, Utils.getTextColor(mContext, cs.getZf()), l1, i, 2,  i, true);
					AddViewItem(Utils.dataFormation(cs.getZd(), mDigit), Utils.getTextColor(mContext, cs.getZd()), l1, i, 3,  i, true);
					
					AddViewItem(Utils.getAmountFormat(cs.getZje(), false, 1), Utils.getTextColor(mContext, 2), l1, i, 4,  i, true);
					
					AddViewItem(Utils.dataFormation(cs.getJrkp(), mDigit), Utils.getTextColor(mContext, cs.getJrkp(), d0), l1, i, 5,  i, true);
				    AddViewItem(Utils.dataFormation(cs.getZrsp(), mDigit), Utils.getTextColor(mContext, 0), l1, i, 6,  i, true);
					AddViewItem(Utils.dataFormation(cs.getZgcj(), mDigit), Utils.getTextColor(mContext, cs.getZgcj(), d0), l1, i, 7,  i, true);
				    AddViewItem(Utils.dataFormation(cs.getZdcj(), mDigit), Utils.getTextColor(mContext, cs.getZdcj(), d0), l1, i, 8,  i, true);
				    AddViewItem(Utils.dataFormation(cs.getAmp()*100, 1) + "%", Utils.getTextColor(mContext, 5), l1, i, 100,  i, true);
				    
				}
			} catch (Exception e) {
		       for(int j=1; j<=len-2; j++) {
		        	AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, j, i, false);
		        }
				e.printStackTrace();
			}
	        
	        mLinerHScroll.addView(l1);
        }
    }
	
	//分类报价
	protected void refreshUI(List<CssStock> list, String[] cols) throws JSONException {
        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
        this.mLinerLock = localLinearLayout1;
        this.mLinerHScroll = localLinearLayout2;
        this.mLinerLock.removeAllViews();
        this.mLinerHScroll.removeAllViews();

        if(nameOrcode)
        	AddViewItem(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        else
        	AddViewItem(cols[1], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        
    	LinearLayout l1 = new LinearLayout(this); 
        for(int i=2; i<cols.length; i++) {
        	if(i==cols.length-1)
                AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, 100, 0, true);
        	else
        		AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, i-1, 0, true);
        }
        
        mLinerHScroll.addView(l1);
        
    	int mDigit = 1;
    	double d0 = 0;
        for(int i=1; i<=list.size(); i++) {
        	CssStock cs = list.get(i-1);
        	d0 = cs.getZrsp();
			//先判断小数位数
			mDigit = Utils.getNumFormat(cs.getMarket(), cs.getStkcode());

	        //mLinerLock.setTag(i);
			if(nameOrcode)
				AddViewItem(Utils.clearSpace(cs.getStkname()), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
			else 
				AddViewItem(cs.getStkcode(), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
			
	        l1 = new LinearLayout(this); 
	        l1.setTag(i);
            

	        if(cs.getStkcode()==null||cs.getStkcode().equals("") ||
	        		cs.getStkname()==null||cs.getStkname().equals("")) {
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 1, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 2, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 3, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 4, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 5, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 6, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 7, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 8, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 9, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 10, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 12, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 13, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 14, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 15, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 16, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 100, i, true);
	        }
	        else if(cs.getTp()==1) {
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 1, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 2, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 3, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 4, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 5, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 6, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 7, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 8, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 9, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZrsp(), mDigit), Utils.getTextColor(mContext, 0), l1, i, 10, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 12, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 13, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 14, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 15, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 16, i, true);
		        AddViewItem(Utils.TP_TAG, Utils.getTextColor(mContext, 0), l1, i, 100, i, true);
	        }
	        else {
//		        String str1;
//				if(cs.getZf()==0)
//					str1 = Utils.dataFormation(cs.getZf()*100, 1) + "%";
//				else
//					str1 = Utils.dataFormation(cs.getZf()*100, 1) + "%";
				
				AddViewItem(Utils.dataFormation(cs.getZf()*100, 1), Utils.getTextColor(mContext, cs.getZf()), l1, i, 1, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZjcj(), mDigit), Utils.getTextColor(mContext, cs.getZjcj(), d0), l1, i, 2, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZd(), mDigit), Utils.getTextColor(mContext, cs.getZd()), l1, i, 3, i, true);
		        AddViewItem(Utils.dataFormation(cs.getBjw1(), mDigit), Utils.getTextColor(mContext, cs.getBjw1(), d0), l1, i, 4, i, true);
		        AddViewItem(Utils.dataFormation(cs.getSjw1(), mDigit), Utils.getTextColor(mContext, cs.getSjw1(), d0), l1, i, 5, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getZl(), false, 1), Utils.getTextColor(mContext, 1), l1, i, 6, i, true);
		        //AddViewItem(Utils.getAmountFormat(cs.getXs(), false, 1), Utils.getTextColor(mContext, 1), l1, i, 7, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getXs(), false, 1), Utils.getTextColor(mContext, Utils.getColorFromBS(cs.getBsFlag())), l1, i, 7, i, true);
//		        AddViewItem(Utils.dataFormation(cs.getHs()*100, 1) + "%", Utils.getTextColor(mContext, 5), l1, i, 8, i, true);
		        //去掉了%号显示
		        AddViewItem(Utils.dataFormation(cs.getHs()*100, 1), Utils.getTextColor(mContext, 5), l1, i, 8, i, true);
		        AddViewItem(Utils.dataFormation(cs.getJrkp(), mDigit), Utils.getTextColor(mContext, cs.getJrkp(), d0), l1, i, 9, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZrsp(), mDigit), Utils.getTextColor(mContext, 0), l1, i, 10, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZgcj(), mDigit), Utils.getTextColor(mContext, cs.getZgcj(), d0), l1, i, 11, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZdcj(), mDigit), Utils.getTextColor(mContext, cs.getZdcj(), d0), l1, i, 12, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getZje(), false, 1), Utils.getTextColor(mContext, 2), l1, i, 13, i, true);
		        AddViewItem(Utils.dataFormation(cs.getAmp()*100, 1) , Utils.getTextColor(mContext, 5), l1, i, 14, i, true);
		        AddViewItem(Utils.dataFormation(cs.getLb(), 1), Utils.getTextColor(mContext, 1), l1, i, 15, i, true);
//		        AddViewItem(Utils.dataFormation(cs.getWb(), 1), Utils.getTextColor(mContext, 5), l1, i, 16, i, true);
		        //颜色换成了涨红跌绿，原本是灰白色
		        AddViewItem(Utils.dataFormation(cs.getWb()*100, 1), Utils.getTextColor(mContext, cs.getWb()), l1, i, 16, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getWc(), false), Utils.getTextColor(mContext, cs.getWc()), l1, i, 100, i, true);
	        }
	        
	        mLinerHScroll.addView(l1);
        }
    }
	
	//大盘指数
	protected void refreshIndexUI(List<CssStock> list, String[] cols) throws JSONException {
        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
        this.mLinerLock = localLinearLayout1;
        this.mLinerHScroll = localLinearLayout2;
        this.mLinerLock.removeAllViews();
        this.mLinerHScroll.removeAllViews();

        if(nameOrcode)
        	AddViewItem(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        else
        	AddViewItem(cols[1], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
        
    	LinearLayout l1 = new LinearLayout(this); 
        for(int i=2; i<cols.length; i++) {
        	if(i==cols.length-1)
                AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, 100, 0, true);
        	else
        		AddViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -i, i-1, 0, true);
        }
        
        mLinerHScroll.addView(l1);
        
    	int mDigit = 1;
    	double d0 = 0;
        for(int i=1; i<=list.size(); i++) {
        	CssStock cs = list.get(i-1);
        	d0 = cs.getZrsp();
			//先判断小数位数
			mDigit = Utils.getNumFormat(cs.getMarket(), cs.getStkcode());

	        //mLinerLock.setTag(i);
			if(nameOrcode)
				AddViewItem(Utils.clearSpace(cs.getStkname()), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
			else 
				AddViewItem(cs.getStkcode(), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
			
	        l1 = new LinearLayout(this); 
	        l1.setTag(i);
	        
	        if(cs.getStkcode()==null||cs.getStkcode().equals("") ||
	        		cs.getStkname()==null||cs.getStkname().equals("")) {
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 1, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 2, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 3, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 4, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 5, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 6, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 7, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 8, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 9, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 10, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 11, i, true);
		        
		        
		        AddViewItem("", Utils.getTextColor(mContext, 0), l1, i, 100, i, true);
	        }
	        else {
//		        String str1;
//				if(cs.getZf()==0)
//					str1 = Utils.dataFormation(cs.getZf()*100, 1) + "%";
//				else
//					str1 = Utils.dataFormation(cs.getZf()*100, 1) + "%";
				
		        AddViewItem(Utils.dataFormation(cs.getZjcj(), mDigit), Utils.getTextColor(mContext, cs.getZjcj(), d0), l1, i, 1, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZf()*100, 1), Utils.getTextColor(mContext, cs.getZf()), l1, i, 2, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getZje(), false, 1), Utils.getTextColor(mContext, 2), l1, i, 3, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZd(), mDigit), Utils.getTextColor(mContext, cs.getZd()), l1, i, 4, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getZl(), false, 1), Utils.getTextColor(mContext, 1), l1, i, 5, i, true);
		        AddViewItem(Utils.getAmountFormat(cs.getXs(), false, 1), Utils.getTextColor(mContext, 1), l1, i, 6, i, true);
		        AddViewItem(Utils.dataFormation(cs.getJrkp(), mDigit), Utils.getTextColor(mContext, cs.getJrkp(), d0), l1, i, 7, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZrsp(), mDigit), Utils.getTextColor(mContext, 0), l1, i, 8, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZgcj(), mDigit), Utils.getTextColor(mContext, cs.getZgcj(), d0), l1, i, 9, i, true);
		        AddViewItem(Utils.dataFormation(cs.getZdcj(), mDigit), Utils.getTextColor(mContext, cs.getZdcj(), d0), l1, i, 10, i, true);
		        AddViewItem(Utils.dataFormation(cs.getAmp()*100, 1), Utils.getTextColor(mContext, 5), l1, i, 11, i, true);
		        //AddViewItem(Utils.dataFormation(cs.getLb(), 1), Utils.getTextColor(mContext, 1), l1, i, 100, i, true);
		        AddViewItem(Utils.dataFormation(cs.getLb(), 1), Utils.getTextColor(mContext, 1), l1, i, 12, i, true);
		        
		        AddViewItem(cs.getJtsy(), Utils.getTextColor(mContext, 0), l1, i, 13, i, true);
		        AddViewItem(cs.getPjgb(), Utils.getTextColor(mContext, 0), l1, i, 14, i, true);
		        AddViewItem(cs.getZsz(), Utils.getTextColor(mContext, 0), l1, i, 15, i, true);
		        AddViewItem(cs.getZb(), Utils.getTextColor(mContext, 0), l1, i, 16, i, true);
		        AddViewItem(cs.getYbjj(), Utils.getTextColor(mContext, 0), l1, i, 17, i, true);
		        AddViewItem(cs.getYbsl(), Utils.getTextColor(mContext, 0), l1, i, 100, i, true);
		        
		        
	        }
	        mLinerHScroll.addView(l1);
        }
    }
	
	private void AddViewItem(String paramString, int paramInt1,
			LinearLayout paramLinearLayout, int paramInt2, int paramInt3,
			int paramInt4, boolean paramBoolean) {
	    TextView localTextView = new TextView(this);
	    float f = this.mFontSize;
	    
	    localTextView.setTextSize(f);
	    //显示名称的时候并且长度是大于4的那就把字体设置的小点
	    if(paramInt2 == paramInt4 && paramInt3 == 0 && nameOrcode){
	    	
	    	if(this.mPaint.measureText(paramString)>textWeight)
	    		localTextView.setTextSize(13);
	    }
	    
	    if(n2 == paramInt2) {
	    	String str = (n1==0)?paramString+low:(n1==1)?paramString+top:paramString;
	    	localTextView.setText(str);
	    }
	    else {
	    	localTextView.setText(paramString);
	    }
	    localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL) ;
	    localTextView.setFocusable(paramBoolean);
	    localTextView.setOnClickListener(mClickListener);
	    localTextView.setOnLongClickListener(mLongClickListener);
	    localTextView.setOnTouchListener(this);    //手势上下翻页touch事件，在这里增加
	    localTextView.setTag(paramInt2);
	    localTextView.setEnabled(paramBoolean);
	    localTextView.setSingleLine(false);
		if(paramInt4==0&&paramInt3>=0) {//表示是标题
			localTextView.setGravity(Gravity.CENTER);
			int i1 = this.residTitleCol;
			int i8 = 0;
			localTextView.setTextColor(paramInt1);
			if(paramInt3==0) {
				localDrawable = getResources().getDrawable(i1);
				i8 = localDrawable.getIntrinsicWidth();
			}
			else if(paramInt3==100) {
			    localDrawable = getResources().getDrawable(this.residTitleScrollCol[2]);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			else if(paramInt3==13) {
			    localDrawable = getResources().getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();
				i8+=20;
			}
			else if(paramInt3==8) {
			    localDrawable = getResources().getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();
				i8+=10;
			}
			else if(paramInt3%2==0) {
			    localDrawable = getResources().getDrawable(this.residTitleScrollCol[1]);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			else {
			    localDrawable = getResources().getDrawable(this.residTitleScrollCol[0]);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			localTextView.setBackgroundDrawable(localDrawable);
			int i6 = localDrawable.getIntrinsicHeight();
		    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
			//int i9 = (int) Math.max(i8, mPaint.measureText(paramString));
			localTextView.setWidth(i8);
			paramLinearLayout.addView(localTextView);
			return;
		}
		if(paramInt4!=0&&paramInt3>=0) {
			int i8 = 0;
	        localTextView.setTextColor(paramInt1);
			if(paramInt3==0) {
				localDrawable = getResources().getDrawable(this.residCol);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			else if(paramInt3==100) {
				localDrawable = getResources().getDrawable(this.residScrollCol[2]);
			    localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			else if(paramInt3==13) {
			    localDrawable = getResources().getDrawable(this.residScrollCol[0]);
			    localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);		
				i8 = localDrawable.getIntrinsicWidth();
				i8+=20;
				//localTextView.setWidth(i8+20);
			}
			else if(paramInt3==8) {
			    localDrawable = getResources().getDrawable(this.residScrollCol[0]);
			    localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);		
				i8 = localDrawable.getIntrinsicWidth();
				i8+=10;
				//localTextView.setWidth(i8+20);
			}
			else if(paramInt3%2==0) {
			    localDrawable = getResources().getDrawable(this.residScrollCol[1]);
			    localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			else {
			    localDrawable = getResources().getDrawable(this.residScrollCol[0]);
			    localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			    i8 = localDrawable.getIntrinsicWidth();
			}
			localTextView.setBackgroundDrawable(localDrawable);	
			int i6 = localDrawable.getIntrinsicHeight();
		    localTextView.setHeight(i6+rowHeight);
			//int i9 = (int) Math.max(i8, mPaint.measureText(paramString));
			localTextView.setWidth(i8);
			paramLinearLayout.addView(localTextView);
			return;
		}
//		if ((paramInt3 == j) && (paramInt4 == l)) {
//			int i13 = this.residTitleScrollCol[l];
//			localDrawable = localResources.getDrawable(i13);
//			localTextView.setTextColor(paramInt1);
//			localTextView.setBackgroundDrawable(localDrawable);
//			paramLinearLayout.addView(localTextView);
//			return;
//		}
	}
	
	public void setSelectRow(int paramInt) throws Exception {
		LinearLayout localLinearLayout1 = this.mLinerLock;
		Integer localInteger1 = Integer.valueOf(this.m_nPos);
		View localView1 = localLinearLayout1.findViewWithTag(localInteger1);
		if (localView1 != null) {
			int l = this.residCol;
//			int l = this.residSelColor;
			localView1.setBackgroundResource(l);
		}
		LinearLayout localLinearLayout2 = this.mLinerLock;
		Integer localInteger2 = Integer.valueOf(paramInt);
		View localView2 = localLinearLayout2.findViewWithTag(localInteger2);
		if (localView2 != null) {
			int i1 = this.residSelColor;
			localView2.setBackgroundColor(i1);
		}

	    LinearLayout localLinearLayout3 = this.mLinerHScroll;
	    Integer localInteger3 = Integer.valueOf(this.m_nPos);
	    LinearLayout localLinearLayout4 = (LinearLayout)localLinearLayout3.findViewWithTag(localInteger3);
	    
	    if (localLinearLayout4 != null) {
		    int i3 = localLinearLayout4.getChildCount();
		    for(int i=0; i<i3; i++) {
		    	View localView3 = localLinearLayout4.getChildAt(i);
		    	int i0 = 0;
		    	if(i==i3-1)
		    		i0 = this.residScrollCol[2];
		    	else if(i==13)
		    		i0 = this.residScrollCol[1];
		    	else if(i%2==0) 
		    		i0 = this.residScrollCol[0];
		    	else 
		    		i0 = this.residScrollCol[1];
		        localView3.setBackgroundResource(i0);
		    }
	    }

		LinearLayout localLinearLayout5 = this.mLinerHScroll;
		Integer localInteger4 = Integer.valueOf(paramInt);
		LinearLayout localLinearLayout6 = (LinearLayout) localLinearLayout5
				.findViewWithTag(localInteger4);
		if (localLinearLayout6 != null) {
			int i4 = this.residSelColor;
			localLinearLayout6.setBackgroundColor(i4);
		}
	    int i6 = localLinearLayout6.getChildCount();
	    for(int i=0; i<i6; i++) {
	    	View localView6 = localLinearLayout6.getChildAt(i);
			int i10 = this.residSelColor;
			localView6.setBackgroundColor(i10);
	    }

		this.m_nPos = paramInt;
		this.cssStock = list.get(paramInt - 1);
	}
	
	protected class TextClick implements View.OnClickListener {
		public void onClick(View arg0) {
			int tag = (Integer)arg0.getTag();
//			isClickNotFirstLoad =true;
			try {
				if(tag>=0) {
					if(selectTag==tag) {
						if(list.get(m_nPos - 1).getMarket()!=null&&!"".equals(list.get(m_nPos - 1).getStkcode()))
							FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(list.get(m_nPos - 1).getMarket()), list.get(m_nPos - 1).getStkcode(), list.get(m_nPos - 1).getStkname(), mContext);
					}
					else {
						selectTag = tag;
						setSelectRow(tag);
					}
				}
				else if(tag==-1){
					nameOrcode = !nameOrcode;
					init(2);
					
				}else if(tag==-100){
					nameOrcode = !nameOrcode;
					initQuote();
				}
				else {
					if(activityKind==Global.QUOTE_FENLEI) {
						if(n2!=tag) 
							n1 = 2;
						n1 = (n1 == 1) ? 0 : 1;
						n2 = tag;
						zqlbDesc2(tag, n1);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	protected void initQuote() {
		
	}
	
	protected class TextLongClick implements View.OnLongClickListener {
		public boolean onLongClick(View v) {
			int tag = (Integer)v.getTag();
			try {
				if(tag>=0) {
					setSelectRow(tag);
					if(m_nPos-1<len) {
						if(true)
							FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(list.get(m_nPos - 1).getMarket()), list.get(m_nPos - 1).getStkcode(), list.get(m_nPos - 1).getStkname(), mContext);
						
					}
				}
			} catch (Exception e) {
			}
			return false;
		}
	}
	
	protected void zqlbDesc2(int t1, int t2) {
		
	}
}
