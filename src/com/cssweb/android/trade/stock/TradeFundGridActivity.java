package com.cssweb.android.trade.stock;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssweb.android.base.DialogActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.Utils;

public class TradeFundGridActivity extends DialogActivity{
	private Context mContext = TradeFundGridActivity.this;
	protected List<String []> listquery = new ArrayList<String []>();
	protected int currentSelectedId = 0;//当前选中的行Tag号，默认选中第一行
	protected int currentPageId = 0;//当前页索引，默认显示第一页
	protected int beginRowId;//当前页的起始行
	protected int endRowId;//当前页的结束行
	private int mFontSize = 18;
	private LinearLayout mLinerHScroll;
	private int[] residScrollCol;
	private int[] residTitleScrollCol;
	private View.OnClickListener mClickListener2;
	protected int pageNum = 9;
	protected int rowHeight = 0;
	protected int m_nPos = 0;
	private int residSelColor;
	
	protected String tacode =null;
	protected String taname =null;
	
	protected int begin =1;
	protected int end =9 ;
	
	public TradeFundGridActivity() {
		int[] arrayOfInt1 = new int[3];
		this.residTitleScrollCol = arrayOfInt1;
		int[] arrayOfInt2 = new int[3];
		this.residScrollCol = arrayOfInt2;
		this.residSelColor = Color.DKGRAY;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.residTitleScrollCol[0] = R.drawable.zrfundqueryh02;
        this.residTitleScrollCol[1] = R.drawable.zrfundqueryh03;
        this.residTitleScrollCol[2] = R.drawable.zrfundqueryh04;
        this.residScrollCol[0] = R.drawable.zrfundqueryh05;
        this.residScrollCol[1] = R.drawable.zrfundqueryh06;
        this.residScrollCol[2] = R.drawable.zrfundqueryh07;
        mClickListener2 = new TextClick2();
	}
	
	/**
	 * 基金公司单击
	 * @author hoho
	 *
	 */
	protected class TextClick2 implements View.OnClickListener{
		public void onClick(View v) {
			try{
				int tag = (Integer)v.getTag();
				TextView textView = (TextView) v;
				String name= (String)textView.getText();
				if (null !=name  &&  !name.equals("")){
					setSelectRow2(tag);
				}
				
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 基金公司选中
	 * @author hoho
	 *
	 */
	public void setSelectRow2(int paramInt) throws Exception {
		if(listquery.size() == 0)
			return;
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
			    	//if(i==13)
			    	if(i==1)
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
			LinearLayout localLinearLayout6 = (LinearLayout) localLinearLayout5.findViewWithTag(localInteger4);
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
			
			if ( listquery.size()>0 ){
				String [] data = listquery.get(paramInt);
				tacode = data[0];
				taname = data[1];
			}
			
			
	}
	
	/**
	 * 基金公司查询表格
	 * @param list
	 * @param cols
	 * @param flag
	 * @throws JSONException
	 */
	 protected void refreshFundQueryUI(List<String [] > list, String[] cols , String flag) throws JSONException {
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(TradeFundGridActivity.this);
		rowHeight = CssSystem.getTableRowHeight(TradeFundGridActivity.this);
//		Log.i("<<<<<<<<<<<<<refreshFundQueryUI pageNum>>>>>>>>>>>>>", String.valueOf(pageNum));
//		Log.i("<<<<<<<<<<<<<refreshFundQueryUI rowHeight>>>>>>>>>>>>>", String.valueOf(rowHeight));
		currentSelectedId = currentPageId*pageNum;
		beginRowId = currentPageId*pageNum;
		endRowId = beginRowId + pageNum - 1;
		 LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.tablelayout);
		 this.mLinerHScroll = localLinearLayout2;
		 this.mLinerHScroll.removeAllViews();
		 LinearLayout l1 = new LinearLayout(this);
		 l1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
		 initTitle(l1 ,cols ,Utils.getTextColor(mContext, 0));
		 mLinerHScroll.addView(l1);
		 initContent(mLinerHScroll  ,list ,cols , Utils.getTextColor(mContext, 1) );
		 try {
			setSelectRow2(currentSelectedId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }
	
	 /**
	  * 基金公司查询初始化表头
	  * @param paramLinearLayout
	  * @param cols
	  * @param color
	  */
	private void  initTitle(LinearLayout paramLinearLayout ,String [] cols ,int color){
		for (int i=0; i<cols.length; i++){
			TextView localTextView = new TextView(this);
			float f = this.mFontSize;
			localTextView.setTextSize(f);
		    localTextView.setText(cols[i]);
		    localTextView.setEnabled(false);
		    localTextView.setTextColor(color);
		    localTextView.setGravity(Gravity.CENTER);
		    Resources localResources = getResources();
		    Drawable localDrawable = null;
			localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
			localTextView.setBackgroundDrawable(localDrawable);
			if (i==0){
				localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1.5f)); 
			}else {
				localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
			}
			
			paramLinearLayout.addView(localTextView);
		}
	 }
	/**
	 *  基金公司查询填充表格内容
	 * @param paramLinearLayout
	 * @param list
	 * @param cols
	 * @param color
	 */
	private void initContent(LinearLayout paramLinearLayout , List<String[]> list ,String cols [] , int color ){
		for(int i=beginRowId; i<=endRowId; i++){
			LinearLayout l1 = new LinearLayout(this);
			String [] data = (String[]) list.get(i);
			for (int j =0 ; j<cols.length ; j++){
				TextView localTextView = new TextView(this);
				float f = this.mFontSize;
				localTextView.setTextSize(f);
			    localTextView.setText(data[j]);
			    localTextView.setEnabled(true);
			    if (j==0){
			    	localTextView.setTextColor(color);
			    }else {
			    	localTextView.setTextColor(Utils.getTextColor(mContext, 0));
			    }
			    localTextView.setGravity(Gravity.CENTER);
			    View.OnClickListener localOnClickListener = this.mClickListener2;
			    localTextView.setOnClickListener(localOnClickListener);
			    l1.setTag(i);
			    localTextView.setTag(i);
			    Resources localResources = getResources();
			    Drawable localDrawable = null;
				localDrawable = localResources.getDrawable(this.residScrollCol[0]);
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + rowHeight );
				localTextView.setBackgroundDrawable(localDrawable);
				if (j ==0){
					localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1.5f)); 
				}else {
					localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
				}
				
				l1.addView(localTextView);
			}
			paramLinearLayout.addView(l1);
		}
	}
	/**
	 * 下页处理
	 */
	protected void onPageDown() {
		if(endRowId >= listquery.size()-1){
			toast("已是最后一页！");
		}else{
			currentPageId ++;
//			previousSelectedId = -1;
			setToolBar();
		}
	}
	
	/**
	 * 上页处理
	 */
	protected void onPageUp() {
		if(currentPageId == 0){
			toast("已是第一页！");
		}else{
			currentPageId --;
//			previousSelectedId = -1;
			setToolBar();
		}
	}
}
