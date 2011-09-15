package com.cssweb.android.base;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssStockFund;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;
/**
 * 场外基金、行情预警 表格基类
 * @author hoho
 *
 */
public class QuoteFundGridActivity extends FlipperActiviy    {
	private Context mContext = QuoteFundGridActivity.this;

	protected List<CssStock> list = new ArrayList<CssStock>();
	protected List<CssStockFund> listfund = new ArrayList<CssStockFund>();
	protected List<String []> listqueryfund = new ArrayList<String []>();
	private View.OnClickListener mClickListener;   //场外基金(股票型、债券型、货币性、混合型基金)
	private View.OnClickListener mClickListener2;  //开放式基金查询 , 此功能已取消
	private View.OnClickListener mClickListener3;  //行情预警单击
	private View.OnClickListener mClickListener4;  //外围市场
	private View.OnClickListener mClickListener5;  //香港主板
	private View.OnClickListener mClickListener6;  //净值增长排行、阳光私募
	
	private View.OnClickListener mSortClickListener;  //排序事件
	private int mFontSize = 18;
	private int mFundFontSize =16;
	private LinearLayout mLinerHScroll;
	private LinearLayout mLinerLock;
	private LinearLayout mLinerMuti;
	
	private Paint mPaint;
	@SuppressWarnings("unused")
	private int residCol;
	protected int m_nPos = 0;
	private int[] residScrollCol;
	private int residSelColor;
	@SuppressWarnings("unused")
	private int residTitleCol;
	private int[] residTitleScrollCol;
	
	protected String stockFundName =null;
	protected String stockFundCode =null;
	protected String market =null;
	
	protected String spjParam = null;
	protected String xpjParam = null;
	protected String zjcjParam = null;
	protected String exchangeParam =null;
	protected int rowHeight = 0;
	
	protected int n1 = 2, n2 = 2;
	private String top = "↓";
	private String low = "↑";
	private boolean selectRowFlag = false;
	protected int selectTag = -1;
	public QuoteFundGridActivity() {
		Paint localPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.mPaint = localPaint;
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
        this.residTitleCol = R.drawable.zrfundquery01;
        this.residTitleScrollCol[0] = R.drawable.zrfundqueryh02;
        this.residTitleScrollCol[1] = R.drawable.zrfundqueryh03;
        this.residTitleScrollCol[2] = R.drawable.zrfundqueryh04;
        this.residCol = R.drawable.zrfundquery02;
        this.residScrollCol[0] = R.drawable.zrfundqueryh05;
        this.residScrollCol[1] = R.drawable.zrfundqueryh06;
        this.residScrollCol[2] = R.drawable.zrfundqueryh07;
        mClickListener = new TextClick();
        mClickListener2 = new TextClick2();  // 此功能已取消
        mClickListener3 = new TextClick3();
        mClickListener4 = new TextClick4();
        mClickListener5 = new TextClick5();
        mClickListener6 = new TextClick6();
        
        mSortClickListener = new TextClickSort();
	}
	
	/**
	 * 场外基金(股票型、债券型、货币性、混合型基金)选中
	 * @param paramInt
	 * @throws Exception
	 */
	public void setSelectRow(int paramInt) throws Exception {
		selectRowFlag = true ;
		LinearLayout localLinearLayout1 = this.mLinerLock;
		View localView1 = localLinearLayout1.findViewWithTag(this.m_nPos);
		if (localView1 != null) {
			localView1.setBackgroundResource(this.residScrollCol[0]);
		}
		LinearLayout localLinearLayout2 = this.mLinerLock;
		View localView2 = localLinearLayout2.findViewWithTag(paramInt);
		if (localView2 != null) {
			localView2.setBackgroundColor(this.residSelColor);
		}
	    LinearLayout localLinearLayout3 = this.mLinerHScroll;
	    LinearLayout localLinearLayout4 = (LinearLayout)localLinearLayout3.findViewWithTag(this.m_nPos);
	    if (localLinearLayout4 != null) {
		    int i3 = localLinearLayout4.getChildCount();
		    for(int i=0; i<i3; i++) {
		    	View localView3 = localLinearLayout4.getChildAt(i);
		    	int i0 = 0;
		    	if(i==i3-1)
		    		i0 = this.residScrollCol[2];
		    	if(i==13)
		    		i0 = this.residScrollCol[1];
		    	else if(i%2==0) 
		    		i0 = this.residScrollCol[0];
		    	else 
		    		i0 = this.residScrollCol[1];
		        localView3.setBackgroundResource(i0);
		    }
	    }
		LinearLayout localLinearLayout5 = this.mLinerHScroll;
		LinearLayout localLinearLayout6 = (LinearLayout) localLinearLayout5.findViewWithTag(paramInt);
		if (localLinearLayout6 != null) {
			localLinearLayout6.setBackgroundColor(this.residSelColor);
		}
	    int i6 = localLinearLayout6.getChildCount();
	    for(int i=0; i<i6; i++) {
	    	View localView6 = localLinearLayout6.getChildAt(i);
			localView6.setBackgroundColor(this.residSelColor);
	    }
		this.m_nPos = paramInt;
		if ( listfund.size()>0 ){
			CssStockFund cssStockFund = listfund.get(paramInt -1);
			stockFundName = cssStockFund.getStkfunname();   //单击取得值
			stockFundCode = cssStockFund.getStkcode();
			market = cssStockFund.getMarket();
			cssStock  = new CssStock();
			cssStock.setStkcode(stockFundCode);    //加入自选
			cssStock.setStkname(stockFundName);
			cssStock.setMarket(market);
		}
		if ( listqueryfund.size()>0 ){			//阳光私募
			String [] data = listqueryfund.get(paramInt -1);
			stockFundName = data[0];
			stockFundCode = data[7];
			market = data[8];
			cssStock  = new CssStock();
			cssStock.setStkcode(stockFundCode);    
			cssStock.setStkname(stockFundName);
			cssStock.setMarket(market);
		}
	
	}
	
	/**
	 * 开放式基金选中
	 * @author hoho
	 *
	 */
	public void setSelectRow2(int paramInt) throws Exception {
		 	LinearLayout lockLinearLayout = this.mLinerLock;
		    LinearLayout lockLinearLayout4 = (LinearLayout)lockLinearLayout.findViewWithTag(this.m_nPos);
		    if (lockLinearLayout4 != null) {
		    	View localView3 = lockLinearLayout4.getChildAt(0);
		    	localView3.setBackgroundResource(this.residScrollCol[1]);
		    }
		    LinearLayout localLinearLayout3 = this.mLinerHScroll;
		    LinearLayout localLinearLayout4 = (LinearLayout)localLinearLayout3.findViewWithTag(this.m_nPos);
		    if (localLinearLayout4 != null) {
		    	View localView3 = localLinearLayout4.getChildAt(0);
		    	localView3.setBackgroundResource(this.residScrollCol[1]);
		    }
		    LinearLayout lockLinearLayout5 = this.mLinerLock;
			LinearLayout lockLinearLayout6 = (LinearLayout) lockLinearLayout5.findViewWithTag(paramInt);
			if (lockLinearLayout6 != null) {
				lockLinearLayout6.setBackgroundColor(this.residSelColor);
			}
			View localView6 = lockLinearLayout6.getChildAt(0);
			localView6.setBackgroundColor(this.residSelColor);
			LinearLayout localLinearLayout5 = this.mLinerHScroll;
			LinearLayout localLinearLayout6 = (LinearLayout) localLinearLayout5.findViewWithTag(paramInt);
			if (localLinearLayout6 != null) {
				localLinearLayout6.setBackgroundColor(this.residSelColor);
			}
			View scrolllocalView6 = localLinearLayout6.getChildAt(0);
			scrolllocalView6.setBackgroundColor(this.residSelColor);
			this.m_nPos = paramInt;
			
	}
	/**
	 * 场外基金(股票型、债券型、货币性、混合型基金)单击
	 * @author hoho
	 *
	 */
	protected class TextClick implements View.OnClickListener {
		public void onClick(View arg0) {
			int tag = (Integer)arg0.getTag();
			TextView textView = (TextView) arg0 ;
			String value = (String) textView.getText();
			try {
				if(tag>0 ) {
					if(selectTag==tag) {				//双击事件
						CssStockFund data = listfund.get(tag -1);
						String code = data.getStkcode();
						String name = data.getStkfunname();
						if ( ( null !=name && !name.equals("")) && (null !=code && !code.equals(""))  ){
							FairyUI.switchToWnd(Global.QUOTE_FLINE,
									NameRule.getExchange(listfund.get(tag-1).getMarket()),
									listfund.get(tag-1).getStkcode(), 
									listfund.get(tag-1).getStkfunname(), mContext);
						}
					}else {							//选中表示
						if (null!= value && !value.equals("")){
							selectTag = tag;
							setSelectRow(tag);
						}
					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	/**
	 * 净值增长排行、阳光私募  单击
	 * @author hoho
	 *
	 */
	protected class TextClick6 implements View.OnClickListener {
		public void onClick(View v) {
			int tag = (Integer)v.getTag();
			TextView textView = (TextView) v ;
			String value = (String) textView.getText();
			try {
				//选中表示
				if (null!= value && !value.equals("")){
					selectTag = tag;
					setSelectRow(tag);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		
		}
	}
	
	/**
	 * 开放式基金单击 , 此功能已取消
	 * @author hoho
	 *
	 */
	protected class TextClick2 implements View.OnClickListener{
		public void onClick(View v) {
			try{
				int tag = (Integer)v.getTag();
				setSelectRow2(tag);
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
	}
	/**
	 * 验证名称是否为空
	 * @param name
	 * @return
	 */
	private boolean isNotNull (String name){
		if ( null!=name && !name.equals("")){
			return true;
		}else {
			return false;
		}
	}
	
	/**
	 * 场外基金(股票型基金、债券型基金、货币型基金、混合型基金) 绘制表格
	 * @param list
	 * @param cols
	 * @throws JSONException
	 */
	protected void refreshFundUI(List<CssStockFund> list, String[] cols , String flag) throws JSONException {
        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
        this.mLinerLock = localLinearLayout1;
        this.mLinerHScroll = localLinearLayout2;
        this.mLinerLock.removeAllViews();
        this.mLinerHScroll.removeAllViews();
        AddFundViewItem(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, 0, 0, true);
    	LinearLayout l1 = new LinearLayout(this); 
        for(int i=1; i<cols.length; i++) {
        	if(i ==2){
        		AddFundViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -1, 100, 0, false);
        	}
        	else if(i==cols.length-1){
        		AddFundViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -1, 100, 0, true);
        	}
        	else{
        		AddFundViewItem(cols[i], Utils.getTextColor(mContext, 0), l1, -1, i, 0, true);
        	}
        }
        mLinerHScroll.addView(l1);
        for(int i=1; i<=list.size(); i++) {
        	CssStockFund cs = list.get(i-1);
			AddFundViewItem(cs.getStkfunname(), Utils.getTextColor(mContext, 1), mLinerLock, i, 0, i, true);
	        l1 = new LinearLayout(this); 
	        l1.setTag(i);
	        
	        if ( !isNotNull(cs.getStkfunname()) ){
	        	AddFundViewItem(Utils.dataFormation(cs.getJz() , 0 , 1),   Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);   //删除初始化0
	        	AddFundViewItem(Utils.dataFormation(cs.getLjjz() , 0 , 1), Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);   //删除初始化0
	        	AddFundViewItem(Utils.dataFormation(cs.getJjtnpj() ,0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);   //删除初始化0
	        	AddFundViewItem(Utils.dataFormation(cs.getJjfnpj() ,0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);   //删除初始化0
	        }else {
	        	 AddFundViewItem(Utils.dataFormation(cs.getJz(),2 , 0), Utils.getTextColor(mContext, 0, 0), l1, i, 1, i, true);
	        	 if(null !=flag && flag.equals("monetary")){
	        		 AddFundViewItem(Utils.dataFormation(cs.getLjjz(),2, 0 ), Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);
	        	 }else{
	        		 AddFundViewItem(Utils.dataFormation(cs.getLjjz(),2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, 2, i, true);
	        	 }
	        	 AddFundViewItem(Utils.dataFormation(cs.getJjtnpj(), 5, 0), Utils.getTextColor(mContext, 0, 0), l1, i, 3, i, true);
	        	 AddFundViewItem(Utils.dataFormation(cs.getJjfnpj(), 5, 0), Utils.getTextColor(mContext, 0, 0), l1, i, 4, i, true);
	        }
	        mLinerHScroll.addView(l1);
        }
        
        if (selectRowFlag){
        	try {
				setSelectRow(m_nPos);
			} catch (Exception e) {
				e.printStackTrace();
			}
        }
        
        
        /*try{
        	CssStockFund cs2 = list.get(0);
        	String code  = cs2.getStkcode() ;
        	String name  = cs2.getStkfunname();
        	if ( (null !=code && !code.equals("")) && (null!=name && !name.equals("")) ){
        		setSelectRow(1);
        	}
        }catch(Exception e){
        	e.printStackTrace();
        }*/
    }
	
	/**
	 * 场外基金(股票型基金、债券型基金、货币型基金、混合型基金) 绘制表格
	 * @param text
	 * @param color
	 * @param paramLinearLayout
	 * @param tag
	 * @param paramInt3
	 * @param istitle
	 * @param bool
	 */
	 private void AddFundViewItem(String text, int color,LinearLayout paramLinearLayout, int tag, int paramInt3, int istitle, boolean bool) {
			TextView localTextView = new TextView(this);
			float f = this.mFontSize;
			localTextView.setTextSize(f);
		    localTextView.setText(text);
		    localTextView.setFocusable(bool);
		    localTextView.setGravity(Gravity.CENTER);
		    View.OnClickListener localOnClickListener = this.mClickListener;
		    localTextView.setOnClickListener(localOnClickListener);
		    localTextView.setTag(tag);
		    localTextView.setOnLongClickListener(onLongClickListener);
		    localTextView.setOnTouchListener(this);    //手势上下翻页touch事件，在这里增加
		    localTextView.setEnabled(bool);
		    localTextView.setSingleLine(true);
		    Resources localResources = getResources();
		    Drawable localDrawable = null;
		    if(istitle ==0){	//表示是标题
				int i8 = 0;
				localTextView.setTextColor(color);
				localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();
				localTextView.setBackgroundDrawable(localDrawable);
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this) );
				int i9 = (int) Math.max(i8, mPaint.measureText(text)) + 95 ;
				localTextView.setWidth(i9);
				paramLinearLayout.addView(localTextView);
				return;
		    }
		    if(istitle !=0){	//填充内容
				localTextView.setTextSize(this.mFundFontSize);
		    	int i8 = 0;
		    	localTextView.setSingleLine(false);
		        localTextView.setTextColor(color);
		        localDrawable = localResources.getDrawable(this.residScrollCol[0]);
			    i8 = localDrawable.getIntrinsicWidth();
			    localTextView.setBackgroundDrawable(localDrawable);	
				int i6 = localDrawable.getIntrinsicHeight() ;
			    localTextView.setHeight(i6 + rowHeight  );
			    if (paramInt3 ==0){
			    	localTextView.setWidth(120);
			    	 localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
			    }else{
			    	 localTextView.setGravity(Gravity.CENTER);
			    	int i9 = (int) Math.max(i8, mPaint.measureText(text)) + 95 ;
					localTextView.setWidth(i9);
			    }
				paramLinearLayout.addView(localTextView);
				return;
		    }
		}
	
	 /**
	  * 场外基金(股票型基金、债券型基金、货币型基金、混合型基金) 长按事件
	  */
	 private OnLongClickListener onLongClickListener = new OnLongClickListener(){
		public boolean onLongClick(View v) {
			int tag =(Integer) v.getTag();
			CssStockFund data = listfund.get(tag -1);
			String code = data.getStkcode();
			String name = data.getStkfunname();
			if ( ( null !=name && !name.equals("")) && (null !=code && !code.equals(""))  ){
				try {
					setSelectRow(tag);
				} catch (Exception e) {
					e.printStackTrace();
				}
				FairyUI.switchToWnd(Global.QUOTE_FLINE,
						NameRule.getExchange(listfund.get(tag-1).getMarket()),
						listfund.get(tag-1).getStkcode(), 
						listfund.get(tag-1).getStkfunname(), mContext);
			}
			return false;
		}
	 };
	 
	/**
	 * 开放式基金查询表格 , 此功能已取消
	 * @param list
	 * @param cols
	 * @param flag
	 * @throws JSONException
	 */
	 protected void refreshFundQueryUI(List<String [] > list, String[] cols , String flag) throws JSONException {
		 LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.tablelayout);
		 LinearLayout localLinearLayout1 =(LinearLayout) this.findViewById(R.id.tablelayout2);
		 this.mLinerHScroll = localLinearLayout2;
		 this.mLinerLock = localLinearLayout1;
		 this.mLinerHScroll.removeAllViews();
		 this.mLinerLock.removeAllViews();
		 
		 LinearLayout l1 = new LinearLayout(this);
		 l1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
		 initTitle(l1 ,cols ,Utils.getTextColor(mContext, 0) , true);
		 mLinerLock.addView(l1);
		 
		 LinearLayout l2 = new LinearLayout(this);
		 l2.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
		 initTitle(l2 ,cols ,Utils.getTextColor(mContext, 0) , false);
		 mLinerHScroll.addView(l2);
		 
		 initContent(mLinerLock  ,list ,cols , Utils.getTextColor(mContext, 0) ,true);
		 initContent(mLinerHScroll  ,list ,cols , Utils.getTextColor(mContext, 0) ,false );
	 }
	
	 /**
	  * 开放式基金查询初始化表头 , 此功能已取消
	  * @param paramLinearLayout
	  * @param cols
	  * @param color
	  */
	private void  initTitle(LinearLayout paramLinearLayout ,String [] cols ,int color ,boolean isFirstCol ){
		int start =0, end =1;
		if (isFirstCol){
			 start =0;
			 end =1;
		}else {
			 start =1;
			 end =2;
		}
		for (int i=start; i<end; i++){
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
			localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f));
			int i6 = localDrawable.getIntrinsicHeight();
		    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this) );
			paramLinearLayout.addView(localTextView);
		}
	 }
	/**
	 *  开放式基金查询填充表格内容 , 此功能已取消
	 * @param paramLinearLayout
	 * @param list
	 * @param cols
	 * @param color
	 */
	private void initContent(LinearLayout paramLinearLayout , List<String[]> list ,String cols [] , int color ,boolean isFirstCol  ){
		int start =0, end =1;
		if (isFirstCol){
			 start =0;
			 end =1;
		}else {
			 start =1;
			 end =2;
		}
		for (int i= 0 ; i<list.size() ; i++){
			LinearLayout l1 = new LinearLayout(this);
			String [] data = (String[]) list.get(i);
			for (int j =start ; j<end ; j++){
				float fontWidth = mPaint.measureText(data[j].toString());
				TextView localTextView = new TextView(this);
				float f = this.mFontSize;
				localTextView.setTextSize(f);
			    localTextView.setText(data[j]);
			    localTextView.setEnabled(true);
			    localTextView.setTextColor(color);
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
				localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
				localTextView.setSingleLine(false);
				if (fontWidth >96){
					localTextView.setTextSize(14);
				}
				if (!isFirstCol){
					localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				}else{
					localTextView.setGravity(Gravity.CENTER);
				}
				l1.addView(localTextView);
			}
			paramLinearLayout.addView(l1);
		}
	}
	
	/**
	 * 场外基金 阳光私募
	 * @param list
	 * @param cols
	 * @throws JSONException
	 */
	protected void refreshFundUI2(List<String []> list, String[] cols , String flag) throws JSONException {
        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
        this.mLinerLock = localLinearLayout1;
        this.mLinerHScroll = localLinearLayout2;
        this.mLinerLock.removeAllViews();
        this.mLinerHScroll.removeAllViews();
        Resources localResources = getResources();
        Drawable localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
		int i8 = localDrawable.getIntrinsicWidth();
        AddFundViewItem2(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, (int) Math.max(i8, mPaint.measureText(cols[0]))+41  , 0, false ,1);
    	LinearLayout l1 = new LinearLayout(this); 
        for(int i=1; i<cols.length; i++) {
        	if(i ==3 || i ==5){
        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, (int) Math.max(i8, mPaint.measureText(cols[i])) + 69, 0, false ,1);
        	}
        	else if(i==cols.length-1){
        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, (int) Math.max(i8, mPaint.measureText(cols[i])) + 41, 0, false ,1);
        	}
        	else{
        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, (int) Math.max(i8, mPaint.measureText(cols[i])) + 41, 0, false ,1);
        	}
        }
        mLinerHScroll.addView(l1);
        for(int i=1; i<=list.size(); i++) {
        	String data [] = list.get(i-1);
        	Resources localResources2 = getResources();
            Drawable localDrawable2 = localResources2.getDrawable(this.residTitleScrollCol[0]);
       		int i9 = localDrawable2.getIntrinsicWidth();
			//先判断小数位数
       		String name = data[0];
       		if(null!=name && name.equals("0")){
       			name = "";
       		}
			AddFundViewItem2(name, Utils.getTextColor(mContext, 1), mLinerLock, i, 120, i, true , 0);
	        l1 = new LinearLayout(this); 
	        l1.setTag(i);
	        DisplayMetrics d = this.getApplicationContext().getResources().getDisplayMetrics();
		    int   SCREENH = d.heightPixels;
	        
		    if ( !isNotNull(data[0]) ){   //初始化的0  , 空数据
		    	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[1]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[1])) + 41 , i, true ,2);  //删除初始化0
		    	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[2])) + 41 , i, true ,4);  //删除初始化0
		    	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[3]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[3])) + 70 , i, true ,6);  //删除初始化0   
		    	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[4]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[4])) + 41 , i, true ,10); //删除初始化0
		    	 if(SCREENH==480) {
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[5]) ,0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[5])) + 70 , i, true ,1);  //删除初始化0
		    	 }else{
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[5]),0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[5])) + 69 , i, true ,1);   //删除初始化0
		    	 }
		    	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[6]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[6])) + 41 , i, true, 1);     //删除初始化0
		    }else {
		    	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[1]),2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[1])) + 41 , i, true , 1);
		    	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]),4 ,0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[2])) + 41 , i, true ,3);
		    	 if(SCREENH==480) {
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[3]), 4, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[3])) + 70 , i, true ,5);
		    	 }else{
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[3]), 4 ,0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[3])) + 69 , i, true ,7);
		    	 }
		    	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[4]), 2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[4])) + 41 , i, true ,9);
		    	 if(SCREENH==480) {
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[5]), 4 ,0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[5])) + 70 , i, true ,1);
		    	 }else{
		    		 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[5]), 4, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[5])) + 69 , i, true ,1);
		    	 }
		    	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[6]), 4, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[6])) + 41 , i, true ,1);
		    }
	        mLinerHScroll.addView(l1);
        }
    }
	
	/**
	 * 场外基金 阳光私募
	 * @param text
	 * @param color
	 * @param paramLinearLayout
	 * @param tag
	 * @param width
	 * @param istitle
	 * @param bool
	 */
	 private void AddFundViewItem2(String text, int color, LinearLayout paramLinearLayout, int tag, int width, int istitle, boolean bool , int firstCol) {
			TextView localTextView = new TextView(this);
			float f = this.mFontSize;
			localTextView.setTextSize(f);
		    localTextView.setText(text);
		    localTextView.setFocusable(bool);
		    localTextView.setGravity(Gravity.CENTER);
		    //View.OnClickListener localOnClickListener = this.mClickListener;
		    View.OnClickListener localOnClickListener = this.mClickListener6;
		    localTextView.setOnClickListener(localOnClickListener);
		    localTextView.setTag(tag);
		    localTextView.setEnabled(bool);
		    localTextView.setSingleLine(true);
		    localTextView.setOnTouchListener(this);
		    Resources localResources = getResources();
		    Drawable localDrawable = null;
		    if(istitle ==0){	//表示是标题
				int i8 = 0;
				localTextView.setTextColor(color);
				localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();
				localTextView.setBackgroundDrawable(localDrawable);
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
				@SuppressWarnings("unused")
				int i9 = (int) Math.max(i8, mPaint.measureText(text));
				localTextView.setWidth(width);
				paramLinearLayout.addView(localTextView);
				return;
		    }
		    if(istitle !=0){	//填充内容
				localTextView.setTextSize(this.mFundFontSize);
		    	int i8 = 0;
		    	localTextView.setSingleLine(false);
		        localTextView.setTextColor(color);
		        localDrawable = localResources.getDrawable(this.residScrollCol[0]);
			    //localTextView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
			    i8 = localDrawable.getIntrinsicWidth();
			    localTextView.setBackgroundDrawable(localDrawable);	
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + rowHeight );
				@SuppressWarnings("unused")
				int i9 = (int) Math.max(i8, mPaint.measureText(text));
				localTextView.setWidth(width);
				if (firstCol ==0){
					 localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
				}else{
					 localTextView.setGravity(Gravity.CENTER);
				}
				paramLinearLayout.addView(localTextView);
				return;
		    }
		}
	
	 	/**
		 * 场外基金 净值增长排行
		 * @param list
		 * @param cols
		 * @throws JSONException
		 */
		protected void refreshFundUI3(List<String []> list, String[] cols , String flag) throws JSONException {
	        LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock);
	        LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
	        this.mLinerLock = localLinearLayout1;
	        this.mLinerHScroll = localLinearLayout2;
	        this.mLinerLock.removeAllViews();
	        this.mLinerHScroll.removeAllViews();
	        Resources localResources = getResources();
	        Drawable localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
			int i8 = localDrawable.getIntrinsicWidth();
	        AddFundViewItem2(cols[0], Utils.getTextColor(mContext, 0), mLinerLock, -1, (int) Math.max(i8, mPaint.measureText(cols[0])) + 41 , 0, false ,1);
	    	LinearLayout l1 = new LinearLayout(this); 
	        for(int i=1; i<cols.length; i++) {
	        	 if(i==cols.length-1){
	        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, (int) Math.max(i8, mPaint.measureText(cols[i])) + 41 , 0, false ,1);
	        	}else if (i ==2){
	        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, i8 + 105 , 0, false ,1);
	        	}
	        	else{
	        		AddFundViewItem2(cols[i], Utils.getTextColor(mContext, 0), l1, -1, (int) Math.max(i8, mPaint.measureText(cols[i])) + 41 , 0, false ,1);
	        	}
	        }
	        mLinerHScroll.addView(l1);
	        for(int i=1; i<=list.size(); i++) {
	        	String data [] = list.get(i-1);
	        	Resources localResources2 = getResources();
	            Drawable localDrawable2 = localResources2.getDrawable(this.residScrollCol[0]);
	       		int i9 = localDrawable2.getIntrinsicWidth();
				//先判断小数位数
	       		String name = data[0];
	       		if(null!=data[0] && data[0].equals("0")){
	       			name ="";
	       		}
				AddFundViewItem2(name, Utils.getTextColor(mContext, 1), mLinerLock, i, 120 , i, true ,0);   //名称
		        l1 = new LinearLayout(this); 
		        l1.setTag(i);
		        
		        if ( !isNotNull(name) ){
		        	 //最新净值
		        	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]) ,0,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[2])) + 41 , i, true ,1); //删除初始化0
		        	 //近一周净值
		        	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]) ,0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, i8 + 105 , i, true ,1);
		        	 //累计净值
		        	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]) ,0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[4])) + 41 , i, true ,1);
		        	 //代码
		        	 AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[1]) , 0 ,1), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[1])) + 41 , i, true ,1); //删除初始化0 
		        }else {
		        	//最新净值
		        	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[1]),2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[2])) + 41 , i, true ,1);
		        	//近一周净值
		        	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[2]), 2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, i8 + 105 , i, true ,1);
		        	//累计净值
		        	AddFundViewItem2(Utils.dataFormation(Double.parseDouble(data[3]), 2, 0), Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[4])) + 41 , i, true ,1);
		        	//代码
		        	AddFundViewItem2(data[4], Utils.getTextColor(mContext, 0, 0), l1, i, (int) Math.max(i9, mPaint.measureText(data[1])) + 41 , i, true ,1); 
		        }
		        mLinerHScroll.addView(l1);
	        }
	    }
		
		
		/**
		 * 行情预警表格
		 * @param list
		 * @param cols
		 * @param flag
		 * @throws JSONException
		 */
		 protected  void refreshWarnQueryUI(List<String [] > list, String[] cols , String flag) throws JSONException {
			 LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.layoutcolumn1);
			 LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.layoutcolumn2);
			 LinearLayout localLinearLayout0 = (LinearLayout)this.findViewById(R.id.layoutcolumn0);
			 this.mLinerLock = localLinearLayout1;
			 this.mLinerHScroll = localLinearLayout2;
			 this.mLinerMuti = localLinearLayout0;
			 this.mLinerLock.removeAllViews();
			 this.mLinerHScroll.removeAllViews();
			 this.mLinerMuti.removeAllViews();
			 LinearLayout l0 = new LinearLayout(this);
			 l0.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,2f ));
			 initWarnTitle(l0 ,cols ,Utils.getTextColor(mContext, 0), 0);
			 mLinerMuti.addView(l0);
			 LinearLayout l1 = new LinearLayout(this);
			 l1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,2f ));
			 initWarnTitle(l1 ,cols ,Utils.getTextColor(mContext, 0), 1);
			 mLinerLock.addView(l1);
			 LinearLayout l12 = new LinearLayout(this);
			 l12.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f));
			 initWarnTitle(l12 ,cols ,Utils.getTextColor(mContext, 0), 2);
			 mLinerHScroll.addView(l12);
			 initWarnContent2(mLinerMuti  ,list ,cols   , 0 );
			 initWarnContent2(mLinerLock  ,list ,cols   , 1 );
			 initWarnContent2(mLinerHScroll  ,list ,cols   , 2  );
			 try{
				 String [] data  = list.get(m_nPos);
				 String name = data[0];
				 String code = data[6];
				 if ( null!=name && !name.equals("") && ( null !=code && !code.equals(""))  ){  //无记录不要选中
					 setSelectRow3(m_nPos);
				 }
			 }catch(Exception e ){
				 e.printStackTrace();
			 }
			 
		 }
		
		 /**
		  * 行情预警初始化表头
		  * @param paramLinearLayout
		  * @param cols
		  * @param color
		  */
		private void  initWarnTitle(LinearLayout paramLinearLayout ,String [] cols ,int color ,int num){
			int start=0 , end =1;
			if (num==0){
				start=0 ; end=1;
			}else if (num==1){
				start=1 ; end=2;
			}else {
				start=2 ; end=4;
			}
			for (int i=start; i< end ; i++){
				TextView localTextView = new TextView(this);
				float f = this.mFontSize;
				localTextView.setTextSize(f);
			    localTextView.setText(cols[i]);
			    localTextView.setEnabled(false);
			    localTextView.setTextColor(color);
			    if(num==2){
			    	localTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
			    	localTextView.setTextSize(16);
			    }else{
			    	localTextView.setGravity(Gravity.CENTER);
			    }
			    Resources localResources = getResources();
			    Drawable localDrawable = null;
				localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
				localTextView.setBackgroundDrawable(localDrawable);
				localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
				paramLinearLayout.addView(localTextView);
			}
		 }
		
		
		/**
		 *  行情预警填充表格内容
		 * @param paramLinearLayout
		 * @param list
		 * @param cols
		 * @param color
		 */
		private void initWarnContent2(LinearLayout paramLinearLayout , List<String []> list ,String cols []  , int num  ){
			try{
				int start=0 , end =1;
				if (num==0){
					start=0 ; end=1;
				}else if (num==1){
					start=1 ; end=2;
				}else {
					start=2 ; end=4;
				}
				for (int i= 0 ; i<list.size() ; i++){
					LinearLayout l1 = new LinearLayout(this);
					String [] data = (String[]) list.get(i);
					for (int j =start ; j<end ; j++){
						TextView localTextView = new TextView(this);
						localTextView.setSingleLine(false);
						if( null ==data[0] || data[0].equals("") || null ==data[6] || data[6].equals("")  ) {     //没有数据 初始化的0 
							 if (j==1){
								 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[1]) , 0 ,1)); 				  //初始化的0
							 }else if (j==2){
								 double increase = ( Double.parseDouble(data[2])- Double.parseDouble(data[1]) ) / Double.parseDouble(data[1]) ;
								 if( Double.parseDouble(data[1]) !=0){
									 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[2]) ,0 , 0) +"\n" + Utils.dataFormation(increase, 6 ,0) );  //初始化的0
								 }else{
									 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[2]) , 0 ,1) );      		  //初始化的0
								 }
							 }else if (j==3){
								 double decline = ( Double.parseDouble(data[3])- Double.parseDouble(data[1]) ) / Double.parseDouble(data[1]) ;
								 if( Double.parseDouble(data[1]) !=0){
									 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[3]) , 0 ,0) +"\n" + Utils.dataFormation(decline, 6, 0));   //初始化的0
								 }else{
									 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[3]) , 0 ,1));   			  //初始化的0
								 }
							 }
							
						}else{																		//有数据的情况
							float f = this.mFontSize;
							localTextView.setTextSize(f);
							if(j==0){
								localTextView.setText(data[j].toString());
								localTextView.setTextColor(Utils.getTextColor(mContext, 1));
								float fontWidth = mPaint.measureText(data[j].toString());
								if (fontWidth > 48){
									localTextView.setTextSize(15 );
								}else{
									localTextView.setTextSize(mFontSize);
								}
							}else if (j==1){
								int mDigit = Utils.getNumFormat(NameRule.getExchange(data[5]), data[6]);   //判断小数位是 2位 还是3 位
								localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]), mDigit , 0));    //处理为0 的时候
								localTextView.setTextColor(Utils.getTextColor(mContext, Double.parseDouble (data[1]) , Double.parseDouble( data[2]), Double.parseDouble(data[3]) )  );
							}else if (j==2){
								// （上破价 - 现价） / 现价
								double increase = ( Double.parseDouble(data[2])- Double.parseDouble(data[1]) ) / Double.parseDouble(data[1]) ;
								if( new Double(data[1]) !=0){
									int mDigit = Utils.getNumFormat(NameRule.getExchange(data[5]), data[6]);   //判断小数位是 2位 还是3 位
									localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , mDigit ,0 ) +"\n" + Utils.dataFormation(increase, 6 ,0) );  //处理为0 的时候
								}else {
									localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,1 , 0) );		//处理为0 的时候
								}
								localTextView.setTextColor(Utils.getTextColor2(mContext,  Double.parseDouble (data[1]) , Double.parseDouble( data[2]) ));
							}else if(j==3){
								//(下破价 -  现价 ) / 现价
								double decline = ( Double.parseDouble(data[3])- Double.parseDouble(data[1]) ) / Double.parseDouble(data[1]) ;
								if( new Double(data[1]) !=0){
									int mDigit = Utils.getNumFormat(NameRule.getExchange(data[5]), data[6]);   //判断小数位是 2位 还是3 位
									localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , mDigit  ,0 ) +"\n" + Utils.dataFormation(decline, 6 , 0));   //处理为0 的时候
								}else {
									localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 0 ,1)  );   //处理为0 的时候
								}
								localTextView.setTextColor(Utils.getTextColor3(mContext,Double.parseDouble (data[1]) , Double.parseDouble( data[3]) ));
							}
						}
					    if(num==2){
					    	localTextView.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
					    	localTextView.setTextSize(16);
					    }else{
					    	localTextView.setGravity(Gravity.CENTER);
					    }
					    localTextView.setGravity(Gravity.CENTER);
					    View.OnClickListener localOnClickListener = this.mClickListener3;
					    localTextView.setOnClickListener(localOnClickListener);
					    l1.setTag(i);
					    localTextView.setTag(i);
					    localTextView.setOnLongClickListener(warnLongClick);
					    Resources localResources = getResources();
					    Drawable localDrawable = null;
						localDrawable = localResources.getDrawable(this.residScrollCol[0]);
						int i6 = localDrawable.getIntrinsicHeight();
						localTextView.setHeight(i6 + rowHeight  );
						localTextView.setBackgroundDrawable(localDrawable);
						localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
						l1.addView(localTextView);
					}
					paramLinearLayout.addView(l1);
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
		
		/**
		 * 行情预警   长按事件
		 */
		private OnLongClickListener warnLongClick  = new OnLongClickListener() {
			public boolean onLongClick(View v) {
				int tag = (Integer)v.getTag();
				String [] data = listqueryfund.get(tag );
				stockFundName = data[0];
				exchangeParam=data[5];
				stockFundCode=data[6];
				if(null!=stockFundName && !stockFundName.equals("")){
					try {
						setSelectRow3(tag);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(exchangeParam), stockFundCode, stockFundName, QuoteFundGridActivity.this);
				}
				return false;
			}
		};
		
		/**
		 * 行情预警 点击事件
		 * @author hoho
		 *
		 */
		protected class TextClick3 implements View.OnClickListener{
			public void onClick(View v) {
				try{
					int tag = (Integer)v.getTag();
					TextView textview  = (TextView) v;
					String name = textview.getText().toString();
					if(null!=name && !name.equals("")){
						if(selectTag==tag) {		//双击事件
							String [] data = listqueryfund.get(tag );
							stockFundName = data[0];
							exchangeParam=data[5];
							stockFundCode=data[6];
							FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(exchangeParam), stockFundCode, stockFundName, QuoteFundGridActivity.this);
						}
						else {						//选中行
							selectTag = tag;
							setSelectRow3(tag);
						}
					}
				}catch(Exception e ){
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 外围市场 点击事件
		 * @author hoho
		 *
		 */
		protected class TextClick4 implements View.OnClickListener{
			public void onClick(View v) {
				try{
					TextView textview  = (TextView) v;
					String name = textview.getText().toString();
					if(null!=name && !name.equals("")){
						int tag = (Integer)v.getTag();
						setSelectRow4(tag);
					}
				}catch(Exception e ){
					e.printStackTrace();
				}
			}
		}
		
		
		
		/**
		 * 行情预警 选中行
		 * @param paramInt
		 * @throws Exception
		 */
		public void setSelectRow3(int paramInt) throws Exception {
				LinearLayout mutiLinearLayout = this.mLinerMuti;
			    LinearLayout mutiLayout = (LinearLayout)mutiLinearLayout.findViewWithTag(this.m_nPos);
			    if (mutiLayout != null) {
			    	View view = mutiLayout.getChildAt(0);
			    	view.setBackgroundResource(this.residScrollCol[0]);
			    }
			 	LinearLayout lockLinearLayout = this.mLinerLock;
			    LinearLayout lockLayout = (LinearLayout)lockLinearLayout.findViewWithTag(this.m_nPos);
			    if (lockLayout != null) {
			    	View view = lockLayout.getChildAt(0);
			    	view.setBackgroundResource(this.residScrollCol[0]);
			    }
			    LinearLayout scrollLinearLayout3 = this.mLinerHScroll;
			    LinearLayout scrollLayout4 = (LinearLayout)scrollLinearLayout3.findViewWithTag(this.m_nPos);
			    if (scrollLayout4 != null) {
			    	View view1 = scrollLayout4.getChildAt(0);
			    	View view2 = scrollLayout4.getChildAt(1);
			    	view1.setBackgroundResource(this.residScrollCol[0]);
			    	view2.setBackgroundResource(this.residScrollCol[0]);
			    }
			    LinearLayout mutiLinearLayout2 = this.mLinerMuti;
				LinearLayout mutiLayout2 = (LinearLayout) mutiLinearLayout2.findViewWithTag(paramInt);
				if (mutiLayout2 != null) {
					mutiLayout2.setBackgroundColor(this.residSelColor);
				}
				View mutiview = mutiLayout2.getChildAt(0);
				mutiview.setBackgroundColor(this.residSelColor);
			    LinearLayout lockLinearLayout2 = this.mLinerLock;
				LinearLayout lockLayout2 = (LinearLayout) lockLinearLayout2.findViewWithTag(paramInt);
				if (lockLayout2 != null) {
					lockLayout2.setBackgroundColor( this.residSelColor);
				}
				View lockview = lockLayout2.getChildAt(0);
				lockview.setBackgroundColor(this.residSelColor);
				LinearLayout scrollLinearLayout2 = this.mLinerHScroll;
				LinearLayout scrollLayout2 = (LinearLayout) scrollLinearLayout2.findViewWithTag(paramInt);
				if (scrollLayout2 != null) {
					scrollLayout2.setBackgroundColor(this.residSelColor);
				}
				View scrollview1 = scrollLayout2.getChildAt(0);
				View scrollview2 = scrollLayout2.getChildAt(1);
				scrollview1.setBackgroundColor(this.residSelColor);
				scrollview2.setBackgroundColor(this.residSelColor);
				this.m_nPos = paramInt;
				try{
					String [] data = listqueryfund.get(paramInt );
					stockFundName = data[0];
					spjParam = data[2];
					xpjParam = data[3];
					zjcjParam = data[1];
					exchangeParam=data[5];
					stockFundCode=data[6];
					cssStock  = new CssStock();
					cssStock.setStkcode(stockFundCode);    //加入自选
					cssStock.setStkname(stockFundName);
					cssStock.setMarket(exchangeParam);
				}catch(Exception e){
					e.printStackTrace();
				}
		}

		/**
		 * 外围市场 选中行
		 * @param paramInt
		 * @throws Exception
		 */
		public void setSelectRow4(int paramInt) throws Exception {
			 	LinearLayout lockLinearLayout = this.mLinerLock;
			    LinearLayout lockLayout = (LinearLayout)lockLinearLayout.findViewWithTag(this.m_nPos);
			    if (lockLayout != null) {
			    	View view = lockLayout.getChildAt(0);
			    	view.setBackgroundResource(this.residScrollCol[1]);
			    }
			    LinearLayout scrollLinearLayout3 = this.mLinerHScroll;
			    LinearLayout scrollLayout4 = (LinearLayout)scrollLinearLayout3.findViewWithTag(this.m_nPos);
			    if (scrollLayout4 != null) {
				    int i3 = scrollLayout4.getChildCount();
				    for(int i=0; i<i3; i++) {
				    	View view = scrollLayout4.getChildAt(i);
				    	view.setBackgroundResource(this.residScrollCol[1]);
				    }
			    }
				LinearLayout scrollLinearLayout2 = this.mLinerHScroll;
				LinearLayout scrollLayout2 = (LinearLayout) scrollLinearLayout2.findViewWithTag(paramInt);
				if (scrollLayout2 != null) {
					scrollLayout2.setBackgroundColor(this.residSelColor);
				}
			    int i6 = scrollLayout2.getChildCount();
			    for(int i=0; i<i6; i++) {
			    	View view = scrollLayout2.getChildAt(i);
					view.setBackgroundColor(this.residSelColor);
			    }
			    LinearLayout lockLinearLayout2 = this.mLinerLock;
				LinearLayout lockLayout2 = (LinearLayout) lockLinearLayout2.findViewWithTag(paramInt);
				if (lockLayout2 != null) {
					lockLayout2.setBackgroundColor(this.residSelColor);
				}
				View view = lockLayout2.getChildAt(0);
				view.setBackgroundColor(this.residSelColor);
				this.m_nPos = paramInt;
				
		}

		
		
		/**
		 * 外围市场表格
		 * @param list
		 * @param cols
		 * @param flag
		 * @throws JSONException
		 */
		 protected void refreshMarketQueryUI(List<String [] > list, String[] cols , String flag) throws JSONException {
			 LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.layoutcolumn1);
			 LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.layoutcolumn2);
			 this.mLinerLock = localLinearLayout1;
			 this.mLinerHScroll = localLinearLayout2;
			 this.mLinerLock.removeAllViews();
			 this.mLinerHScroll.removeAllViews();
			 
			 LinearLayout l1 = new LinearLayout(this);
			 l1.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
			 initMarketTitle(l1 ,cols ,Utils.getTextColor(mContext, 0), true);
			 mLinerLock.addView(l1);
			 
			 LinearLayout l12 = new LinearLayout(this);
			 l12.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ));
			 initMarketTitle(l12 ,cols ,Utils.getTextColor(mContext, 0), false);
			 mLinerHScroll.addView(l12);
			 
			 initMarketContent(mLinerLock  ,list ,cols   , true     ,flag );
			 initMarketContent(mLinerHScroll  ,list ,cols   , false ,flag );
			 /*try{
				// String [] data  = list.get(m_nPos);
				 setSelectRow4(m_nPos);
			 }catch(Exception e ){
				 e.printStackTrace();
			 }*/
		 }
		
		 /**
		  * 外围市场初始化表头
		  * @param paramLinearLayout
		  * @param cols
		  * @param color
		  */
		private void  initMarketTitle(LinearLayout paramLinearLayout ,String [] cols ,int color , boolean isStart  ){
			int start=0 , end =1;
			if (!isStart){
				start=1 ; end=4;
			}
			for (int i=start; i< end ; i++){
				TextView localTextView = new TextView(this);
				float f = this.mFontSize;
				localTextView.setTextSize(f);
			    localTextView.setText(cols[i]);
			    localTextView.setEnabled(false);
			    localTextView.setOnTouchListener(this);
			    localTextView.setTextColor(color);
			    localTextView.setGravity(Gravity.CENTER);
			    Resources localResources = getResources();
			    Drawable localDrawable = null;
				localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
				localTextView.setBackgroundDrawable(localDrawable);
				if (i == 1){
					localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,0.9f)); 
				}else {
					localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
				}
				//localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
				int i6 = localDrawable.getIntrinsicHeight();
			    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
				paramLinearLayout.addView(localTextView);
			}
		 }
		/**
		 *  外围市场填充表格内容
		 * @param paramLinearLayout
		 * @param list
		 * @param cols
		 * @param color
		 */
		private void initMarketContent(LinearLayout paramLinearLayout , List<String []> list ,String cols []  ,boolean isStart ,String flag ){
			try{
				int start=0 , end =1;
				if (!isStart){
					start=1 ; end=4;
				}
				for (int i= 0 ; i<list.size() ; i++){
					LinearLayout l1 = new LinearLayout(this);
					String [] data = (String[]) list.get(i);
					for (int j =start ; j<end ; j++){
						TextView localTextView = new TextView(this);
						localTextView.setTextSize(14);
						if(j>0){
							try {
								int d = 0;					//用涨跌 根据0比较
								if (Double.parseDouble(data[3])> 0 ){
									d = 3;
								}else if (Double.parseDouble(data[3])< 0  ){
									d = 4;
								}else {
									d = 0;
								}
								
								if ( !isNotNull(data[0]) ){   //数据为空
									 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,0 ,1) );      //初始化的0
								}else {
									if (flag.equals("1")){		//全球汇市
										if (j==2){		//涨幅
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 0 ,0 ));
										}else if(j==3){  //涨跌
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 3 , 0) );
										}
										else{			//现价
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,3 , 0) );
										}
										localTextView.setTextColor(Utils.getTextColor(mContext, d));
									}else if (flag.equals("2")) {	//全球商品			
										if (j==2){
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 1 ,0));
										}else if(j==3){
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,1 ,0));
										}
										else{
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 1, 0));
										}
										localTextView.setTextColor(Utils.getTextColor(mContext, d));
									}else {//外围市场
										if (j==2){
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j])*100 , 1 ,0));
										}else if(j==3){
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,1 ,0));
										}
										else{
											localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) , 1, 0));
										}
										localTextView.setTextColor(Utils.getTextColor(mContext, d));
									}
								}
								
							}catch(Exception e){
								e.printStackTrace();
								 localTextView.setText("");
							}
						}else {						//第一列指数名称
							boolean isSecond =true;												//判断处理换行问题
							float textlength = mPaint.measureText(data[j].toString());
							if (textlength <= 72){
								localTextView.setText(data[j].toString());
							}else{
								StringBuffer result = new StringBuffer();
								StringBuffer tempresult = new StringBuffer();
								for (int num=0; num < data[j].length() ; num++){
									char tempChar = data[j].charAt(num);
									tempresult.append(tempChar);
									float fontWidth = mPaint.measureText(tempresult.toString());
									if (fontWidth >=65 && isSecond){
										tempresult.append("\n");
										result.append(tempresult);
										tempresult = new StringBuffer();
										isSecond=false ;
									}else if (num == data[j].length()-1 ){
										float fontWidth2 = mPaint.measureText(tempresult.toString());
										if (fontWidth2 <=76){
											result.append(tempresult);
										}else{
											while (fontWidth2 >76){
												tempresult = tempresult.deleteCharAt(tempresult.length()-1);
												fontWidth2 = mPaint.measureText(tempresult.toString());
											}
											result.append(tempresult);
										}
									}
								}
								localTextView.setText(result.toString());
							}
							//localTextView.setText(data[j]);
							localTextView.setTextColor(Utils.getTextColor(mContext, 1));
						}
						if (j==0){
							 localTextView.setGravity(Gravity.LEFT |Gravity.CENTER_VERTICAL);
							
						}else{
							 //localTextView.setGravity(Gravity.CENTER);
							localTextView.setGravity(Gravity.RIGHT |Gravity.CENTER_VERTICAL);
						}
					    View.OnClickListener localOnClickListener = this.mClickListener4;
					    localTextView.setOnClickListener(localOnClickListener);
					    l1.setTag(i);
					    localTextView.setTag(i);
					    
					    localTextView.setOnTouchListener(this);
					    
					    Resources localResources = getResources();
					    Drawable localDrawable = null;
						localDrawable = localResources.getDrawable(this.residScrollCol[0]);
						int i6 = localDrawable.getIntrinsicHeight();
						localTextView.setHeight(i6 + rowHeight );
						localTextView.setBackgroundDrawable(localDrawable);
						if (j == 1){
							localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,0.9f)); 
						}else {
							localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
						}
						//localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
						l1.addView(localTextView);
					}
					paramLinearLayout.addView(l1);
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
		
		
		/**
		 * 香港主板、香港创业板  表格
		 * @param list
		 * @param cols
		 * @throws JSONException
		 */
		protected void refreshHKUI(List<String []> list, String[] cols ) throws JSONException {
			 	LinearLayout localLinearLayout1 = (LinearLayout)this.findViewById(R.id.zr_htable_lock1);
			 	LinearLayout localLinearLayout2 = (LinearLayout)this.findViewById(R.id.zr_htable_lock2);
		        LinearLayout localLinearLayout = (LinearLayout)this.findViewById(R.id.zr_htable_linearlayout);
		        this.mLinerLock = localLinearLayout1;
		        this.mLinerMuti = localLinearLayout2;
		        this.mLinerHScroll = localLinearLayout;
		        this.mLinerLock.removeAllViews();
		        this.mLinerHScroll.removeAllViews();
		        this.mLinerMuti.removeAllViews();
		        
		        LinearLayout l1 = new LinearLayout(this);  
		        initHKTitle(l1 , cols  ,0);
		        this.mLinerLock .addView(l1);       //代码列
		        
		        LinearLayout l2 = new LinearLayout(this);
		        initHKTitle(l2 , cols  ,1);
		        this.mLinerMuti.addView(l2);       //名称列
		        
		        LinearLayout l3 = new LinearLayout(this);
		        initHKTitle(l3 , cols  ,2);
		        this.mLinerHScroll.addView(l3);    //滑屏列
		        
		        initHKContent(mLinerLock  , list ,cols , 0);   //代码列
		        initHKContent(mLinerMuti  , list ,cols, 1);    //名称列
		        initHKContent(mLinerHScroll  , list ,cols, 2); //滑屏列
		        
		        if (selectRowFlag){
		        	try {
						setSelectRow5(m_nPos);
					} catch (Exception e) {
						e.printStackTrace();
					}
		        }
		        
		        /*try{
					 String [] data  = list.get(m_nPos);
					 String name = data[1];
					 String code = data[0];
					 if ( null!=name && !name.equals("") && ( null !=code && !code.equals(""))  ){  //无记录不要选中
						 setSelectRow5(m_nPos);
					 }
				 }catch(Exception e ){
					 e.printStackTrace();
				 }*/
		        
		}
		/**
		 * 香港主板、香港创业板  表头初始化
		 * @param locklayout
		 * @param scrolllayout
		 * @param cols
		 */
		private void initHKTitle( LinearLayout locklayout , String [] cols ,int colnum){
				int start =0 ,end =1;	
			    if (colnum ==0){
			    	start =0 ;end =1 ;
				}else if (colnum ==1){
					start =1 ;end =2 ;
				}else if (colnum ==2){
					start =2 ;end = cols.length ;
				}
				for (int i=start; i<end; i++){
					TextView localTextView = new TextView(this);
					float f = this.mFontSize;
					localTextView.setTextSize(f);
				    localTextView.setEnabled(true);
				    if( n2 == 0-i-1 ) {
				    	String str = (n1==0)?cols[i]+low:(n1==1)?cols[i]+top:cols[i];
				    	localTextView.setText(str);
				    }else {
				    	localTextView.setText(cols[i]);
				    }
				    if (i==1){
				    	 localTextView.setWidth(100);
				    }
				    int tag = 0-i-1 ;  //标题都是负数，避免mLinerLock 中有标题和内容冲突
				    localTextView.setTag(tag);
				    if (tag < -2){
				    	 localTextView.setOnClickListener(mSortClickListener);
				    }
				    localTextView.setTextColor(Utils.getTextColor(mContext, 0));
				    localTextView.setGravity(Gravity.CENTER);
				    Resources localResources = getResources();
				    Drawable localDrawable = null;
					localDrawable = localResources.getDrawable(this.residTitleScrollCol[0]);
					localTextView.setBackgroundDrawable(localDrawable);
					//localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
					if (i == 9){
						localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,0.99f)); 
					}else {
						localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
					}
					int i6 = localDrawable.getIntrinsicHeight();
				    localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
					locklayout.addView(localTextView);
				}
		  }
		/**
		 * 香港主板、香港创业板 填充内容
		 * @param locklayout
		 * @param scrolllayout
		 * @param list
		 */
		private void initHKContent( LinearLayout layout , List<String []> list , String [] cols , int  colnum){
			int start =0 ,end =1;	
		    if (colnum ==0){
		    	start =0 ;end =1 ;
			}else if (colnum ==1){
				start =1 ;end =2 ;
			}else if (colnum ==2){
				start =2 ;end = cols.length ;
			}
			 for(int i=0; i<list.size(); i++) {
		        	String data [] = list.get(i);
		        	double d0 = Double.parseDouble (data[13] );  //昨收
		        	LinearLayout l1 = new LinearLayout(this);
		        	for (int j = start; j <end ; j++){
		        			TextView localTextView = new TextView(this);
							float f = this.mFontSize;
							localTextView.setTextSize(f);
						    localTextView.setEnabled(true);
						    l1.setTag(i);
						    localTextView.setTag(i);
						    localTextView.setOnLongClickListener(onLongHKClickListener);
						    //localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT ,1f)); 
						    if (j == 9){
								localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,0.99f)); 
							}else {
								localTextView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT ,1f)); 
							}
						    String name = data[1];
						try{
				        	if (null ==name || name.equals("")){
				        		 localTextView.setText(null);
				        	}else if ( data[23].equals("1") ){    //停牌标志
				        		if (j==0){  //代码
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 1));
							    	 localTextView.setText(data[j]);
							    	 localTextView.setGravity(Gravity.CENTER);
							    }else if (j==1){  //名称
							    	  float textlength = mPaint.measureText(data[j]);
							    	  if (textlength >=60){
										 localTextView.setTextSize(14);
							    	  }
							    	 localTextView.setWidth(100);
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 1));
							    	 localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
							    	 localTextView.setText(data[j]);
							    }else if (j==13) {        		//昨收
				        			localTextView.setTextColor(Utils.getTextColor(mContext, 0) );
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,2 ,0 ));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				        		}else if (j==20){			//每手股数
				        			localTextView.setTextColor(Utils.getTextColor(mContext, 1) );
							    	localTextView.setText(data[j]);
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				        		}else {
				        			localTextView.setText(Utils.TP_TAG);
				        			localTextView.setTextColor(Utils.getTextColor(mContext, 0) );
				        			localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
				        		}
				        	}
				        	else {
				        		if (j==0){  //代码
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 1));
							    	 localTextView.setText(data[j]);
							    	 localTextView.setGravity(Gravity.CENTER);
							    }else if (j==1){  //名称
							    	  float textlength = mPaint.measureText(data[j]);
							    	  if (textlength >=60){
										 localTextView.setTextSize(14);
							    	  }
							    	 localTextView.setWidth(100);
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 1));
							    	 localTextView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
							    	 localTextView.setText(data[j]);
							    }else if (j ==2 || j==11 ||j==12 || j==14 || j==15 || j==16){   // 根据昨收比较   \\现价、买价、卖价、今开、最高、最低
							    	localTextView.setTextColor(Utils.getTextColor(mContext, Double.parseDouble(data[j]), d0));
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,2 ,0 ));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==13){                                              //昨收
							    	localTextView.setTextColor(Utils.getTextColor(mContext, 0) );
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,2 ,0 ));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==17){											 //委比
							    	int d = 0;					//用涨跌 根据0比较
									if (Double.parseDouble(data[j])> 0 ){
										d = 3;
									}else if (Double.parseDouble(data[j])< 0  ){
										d = 4;
									}else {
										d = 0;
									}
									localTextView.setTextColor(Utils.getTextColor(mContext, d));
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j])*100 , 1 , 0));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==18){											//委差
							    	localTextView.setTextColor(Utils.getTextColor(mContext, Double.parseDouble(data[j])) );
							    	localTextView.setText(Utils.getAmountFormat(Double.parseDouble(data[j]) , false));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==19){											//量比
							    	localTextView.setTextColor(Utils.getTextColor(mContext, 1) );
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]), 1 , 0));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==20){											//每手股数
							    	localTextView.setTextColor(Utils.getTextColor(mContext, 1) );
							    	localTextView.setText(data[j]);
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==3){		//涨幅   根据0 比较
							    		String str1;
							    	 	//Log.i("444444444", "ddd6777 data[j]:" + data[j]);
										//if (!Utils.isFloatNumber(data[j])){
										//	data[j] = "0";
										//}
										if(Double.parseDouble(data[j])==0)
											str1 = Utils.dataFormation(Double.parseDouble(data[j])*100, 1 , 0);
										else
											str1 = Utils.dataFormation(Double.parseDouble(data[j])*100, 1 , 0);
							    	
							    	localTextView.setTextColor(Utils.getTextColor(mContext, Double.parseDouble(data[j]) ));
							    	localTextView.setText(str1);
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
							    else if (j==4){   //根据0 比较         //涨跌
							    	localTextView.setTextColor(Utils.getTextColor(mContext, Double.parseDouble(data[j]) ));
							    	localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,2 , 0 ) );
							    	//localTextView.setText(Utils.round(Double.parseDouble(data[j]) , 2 ));
							    	localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
							    else if (j==5){ 				   //总额
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 2));
							    	 localTextView.setText(Utils.getAmountFormat( Double.parseDouble(data[j]), false , 1 ));
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
							    else if (j==6 ){    //总量
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 1));
							    	 localTextView.setText(Utils.getAmountFormat( Double.parseDouble(data[j]), false , 1 ));
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==7 ){    //现量
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, Utils.getColorFromBS(data[22])  ));
							    	 localTextView.setText(Utils.getAmountFormat( Double.parseDouble(data[j]), false , 1 ));
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
							    else if (j==8 ){   //换手率
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 5));
							    	 //localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j])*100, 1) + "%");
							    	 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j])*100, 1 , 0) );
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }else if (j==9 ||j==10){ // 市盈率、市净率
							    	 localTextView.setTextColor(Utils.getTextColor(mContext, 5));
							    	 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]), 1 , 0) );
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
							    else{
							    	 localTextView.setText(Utils.dataFormation(Double.parseDouble(data[j]) ,1 ,0));
							    	 localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
							    }
				        	}
							}catch(Exception e){
			        			localTextView.setText("");
				    			localTextView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
			        			e.printStackTrace();
			        		}
						    View.OnClickListener localOnClickListener = this.mClickListener5;
						    localTextView.setOnClickListener(localOnClickListener);
						    Resources localResources = getResources();
						    Drawable localDrawable = null;
							localDrawable = localResources.getDrawable(this.residScrollCol[0]);
							int i6 = localDrawable.getIntrinsicHeight();
							localTextView.setOnTouchListener(this);
						    localTextView.setHeight(i6 + rowHeight );
							localTextView.setBackgroundDrawable(localDrawable);
							l1.addView(localTextView);
		        	}
		        	layout.addView(l1);
			 }
			 
		}
		
		 /**
		  * 港股主板、香港创业板 长按事件
		  */
		 private OnLongClickListener onLongHKClickListener = new OnLongClickListener(){
			public boolean onLongClick(View v) {
				int tag =(Integer) v.getTag();
				String [] data = listqueryfund.get(tag);
				String market = data[21];
				String code = data[0];
				String name = data[1];
				if ( ( null !=name && !name.equals("")) && (null !=code && !code.equals(""))  ){
					try {
						setSelectRow5(tag);
					} catch (Exception e) {
						e.printStackTrace();
					}
					FairyUI.switchToWnd(Global.QUOTE_FENSHI,NameRule.getExchange(market),code, name, mContext);
				}
				return false;
			}
		 };
		
		/**
		 * 香港主板、香港创业板 排序 点击事件
		 * @author hoho
		 *
		 */
		protected class TextClickSort implements View.OnClickListener{
			public void onClick(View v) {
				int tag = (Integer)v.getTag();
				try {
					if(n2!=tag) 
						n1 = 2;
					n1 = (n1 == 1) ? 0 : 1;
					n2 = tag;
					zqlbDesc2(tag, n1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		protected void zqlbDesc2(int t1, int t2) {
			
		}
		
		/**
		 * 香港主板、香港创业板  点击事件
		 * @author hoho
		 *
		 */
		protected class TextClick5 implements View.OnClickListener{
			public void onClick(View v) {
				try{
					TextView textview  = (TextView) v;
					String name = textview.getText().toString();
					if(null!=name && !name.equals("")){
						int tag = (Integer)v.getTag();
						if (tag >=0){
							if(selectTag==tag) {   //双击事件
								String [] data = listqueryfund.get(tag);
								String market = data[21];
								String code = data[0];
								String name2 = data[1];
								FairyUI.switchToWnd(Global.QUOTE_FENSHI,NameRule.getExchange(market),code, name2, mContext);
							}
							else {					//选中行表示
								selectTag = tag;
								setSelectRow5(tag);
							}
						}
					}
				}catch(Exception e ){
					e.printStackTrace();
				}
			}
		}
		
		/**
		 * 香港主板、香港创业板  选中行
		 * @param paramInt
		 * @throws Exception
		 */
		public void setSelectRow5(int paramInt) throws Exception {
				selectRowFlag = true;
				LinearLayout mutilLinearLayout = this.mLinerMuti;
			    LinearLayout mutiLayout = (LinearLayout)mutilLinearLayout.findViewWithTag(this.m_nPos);
			    if (mutiLayout != null) {
			    	View view = mutiLayout.getChildAt(0);
			    	view.setBackgroundResource(this.residScrollCol[0]);
			    }
			 	LinearLayout lockLinearLayout = this.mLinerLock;
			    LinearLayout lockLayout = (LinearLayout)lockLinearLayout.findViewWithTag(this.m_nPos);
			    if (lockLayout != null) {
			    	View view = lockLayout.getChildAt(0);
			    	view.setBackgroundResource(this.residScrollCol[0]);
			    }
			    LinearLayout scrollLinearLayout3 = this.mLinerHScroll;
			    LinearLayout scrollLayout4 = (LinearLayout)scrollLinearLayout3.findViewWithTag(this.m_nPos);
			    if (scrollLayout4 != null) {
				    int i3 = scrollLayout4.getChildCount();
				    for(int i=0; i<i3; i++) {
				    	View view = scrollLayout4.getChildAt(i);
				    	view.setBackgroundResource(this.residScrollCol[0]);
				    }
			    }
				LinearLayout scrollLinearLayout2 = this.mLinerHScroll;
				LinearLayout scrollLayout2 = (LinearLayout) scrollLinearLayout2.findViewWithTag(paramInt);
				if (scrollLayout2 != null) {
					scrollLayout2.setBackgroundColor(this.residSelColor);
				}
			    int i6 = scrollLayout2.getChildCount();
			    for(int i=0; i<i6; i++) {
			    	View view = scrollLayout2.getChildAt(i);
					view.setBackgroundColor(this.residSelColor);
			    }
			    
			    LinearLayout lockLinearLayout2 = this.mLinerLock;
				LinearLayout lockLayout2 = (LinearLayout) lockLinearLayout2.findViewWithTag(paramInt);
				if (lockLayout2 != null) {
					lockLayout2.setBackgroundColor(this.residSelColor);
				}
				View view = lockLayout2.getChildAt(0);
				view.setBackgroundColor(this.residSelColor);
				
				LinearLayout mutiLinearLayout2 = this.mLinerMuti;
				LinearLayout mutiLayout2 = (LinearLayout) mutiLinearLayout2.findViewWithTag(paramInt);
				if (mutiLayout2 != null) {
					mutiLayout2.setBackgroundColor(this.residSelColor);
				}
				View view2 = mutiLayout2.getChildAt(0);
				view2.setBackgroundColor(this.residSelColor);
				//this.m_nPos = paramInt;
				this.m_nPos = paramInt;
				if ( listqueryfund.size()>0 ){
					String [] data  = listqueryfund.get(paramInt);
					stockFundName = data[1];   //单击取得值
					stockFundCode = data[0];
					market = data[21];
					cssStock  = new CssStock();
					cssStock.setStkcode(stockFundCode);    //加入自选
					cssStock.setStkname(stockFundName);
					cssStock.setMarket(market);
				}
				
		}
		
		
}
