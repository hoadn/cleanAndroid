/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)TrendView.java 上午09:58:45 2010-10-19
 */
package com.cssweb.android.view;

import java.util.Date;

import javax.microedition.fairy.Font;
import javax.microedition.fairy.Graphics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;

import com.cssweb.android.common.CssLog;
import com.cssweb.android.view.base.BasicView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.GlobalColor;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.TickUtil;
import com.cssweb.quote.util.Utils;

/**
 * 分时走势绘图
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class TrendView extends BasicView {
	private final String TAG = "TrendView";
	
	private Paint   paint = null, mpaint = null;
    
    private int stocktype = 0;
    private int stockdigit = 0;
	protected String exchange = "sh";
	protected String stockcode = "000001";
	protected String stockname;
	
	protected int x, y, width, height;

	public boolean isTrackStatus = false, isTouched = false, isTouchMoved = false;
	private float startPositionX = 0, currentPositionX = 0, startPositionY = 0, currentPositionY = 0;
	
	protected int trackLineV,trackLineH;
	protected int isTrackNumber = 0;
	
	private JSONObject quoteData = null;
	private JSONArray quoteArray = null;
	
	/**
	 *  收盘价
	 */
	private double close = 0;
	
	/**
	 *  开盘价
	 */
	private double jrkp = 0;
	
	/**
	 *  最高价
	 */
	private double high = 0;
	
	/**
	 *  最低价
	 */
	private double low = 0;
	
	/**
	 * 
	 */
	private double scale = 0;
	
	/**
	 *  最高成交量
	 */
	private double highVolume = 0;
	private double highvolume;
	private double highamount;
	
	/**
	 * 绘图区上限价格
	 */
	//private double upPrice = 0;
	
	/**
	 * 绘图区下限价格
	 */
	//private double downPrice = 0;
	
	/**
	 * 空隙(线之间的间隔)
	 */
	private float SPACE = 1f;
	
	/**
	 * 时间（分时总共240笔数据）
	 */
	private int MINUTES = 240;
	
	/**
	 * 
	 */
	private int DIVIDE_COUNT = 4;
	
	private int topTitleHeight = 0;
	
	// 涨行情
	private int upQuoteX = 0;
	private int upQuoteY = 0;
	private int upQuoteWidth = 0;
	private int upQuoteHeight = 0;
	
	// 跌行情
	private int downQuoteX = 0;
	private int downQuoteY = 0;
	private int downQuoteWidth = 0;
	private int downQuoteHeight = 0;
	
	// 成交量图
	private int volumeX = 0;
	private int volumeY = 0;
	private int volumeWidth = 0;
	private int volumeHeight = 0;
	
    private float graphicsQuoteWidth = MINUTES * SPACE; // 绘图区域宽度
     
	private int graphicsQuoteHeight = 0;
    
    private int price_row_num ; // 价格行数
    private int price_row_height; // 价格行高
	private int volume_row_num ;
	private int volume_row_height;

    // 轴文本
    //private int axisLabelWidth = 0;
    private int axisLabelHeight = 0;
    
	private double AxisLabelPrice = 0;
	//private double AxisLabelPer = 0;
	
	private double AxisLabelVolume = 0;
	
	private String closeLeft;
	private String closeRight;
	private int LSpace = 50;
	private int RSpace = 50;
	
	private int actualDataLen = 0;
	private double max_zf = 0, max_zd=0;

	private String stime = "", etime = "";
	private String title1,title2,title7,title8,title9;
	
	public TrendView(Context context) {
		super(context);
	}

	public TrendView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}
	
	public boolean isZs() {	
		if(this.stocktype==NameRule.SH_INDEX
				|| this.stocktype==NameRule.SZ_INDEX
				|| this.stocktype==NameRule.SH_ZZ
				|| this.stocktype==NameRule.HK_INDEX){
			return true;
		}else{
			return false;
		}
	}
	
	public boolean isOpenFund() {	
		if(this.stocktype==NameRule.SH_KFSJJ
				|| this.stocktype==NameRule.SZ_OPEN_FUND
				|| this.stocktype==NameRule.OTHER_OPEN_FUND){
			return true;
		}else{
			return false;
		}
	}
	
	public void setStockInfo(String exchange, String stockcode, String stockname) {
		this.exchange = exchange;
		this.stockcode = stockcode;
		this.stockname = stockname;
		this.stocktype = NameRule.getSecurityType(exchange, stockcode);
		this.stockdigit = Utils.getStockDigit(stocktype);
		this.isTrackNumber = 0;
		this.quoteData = null;
		this.isTrackStatus = false;
		this.isTouched = false;
		this.isTouchMoved = false;
	}

	public void getQuoteData(JSONObject quoteData) {
		this.quoteData = quoteData;
		this.quoteArray = null;

		try {
			repairData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	if(h<75) return;
    	float rate = (float) w/320;
    	mTextSize = (int)(M_TEXT_SIZE*rate);
    	dTextSize = (int)(D_TEXT_SIZE*rate);
    	DX		  = (int)(DX_W*rate);
    	DY 		  = (int)(DY_H*rate);
		this.x = 0;
		this.y = 0;
		this.width = w;
		this.height = h;
		//this.height = (int) (h*0.7);
		//this.bottomHeight = (int) (h*0.3);
    }
	
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    	if(quoteData==null) {
    		drawWin(canvas);
    		return;
    	}
        try {
	        drawChart(canvas);
		} catch (JSONException e) {
			e.printStackTrace();
			CssLog.e(TAG, e.toString());
		}
    }
    
    private void drawWin(Canvas canvas) {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
		
		mpaint = new Paint();
		mpaint.setTypeface(Typeface.DEFAULT_BOLD);
		mpaint.setAntiAlias(true);
		mpaint.setTextAlign(Paint.Align.LEFT);
		mpaint.setStyle(Paint.Style.STROKE);
		mpaint.setTextSize(dTextSize);
		
		/**
		 * 画图表之前先计算宽度
		 */
		closeLeft = Utils.dataFormation(high, stockdigit);
		closeRight = "00.00%";
		 
		LSpace = (int)(Math.max(mpaint.measureText(closeLeft),mpaint.measureText(closeRight)));
		//暂时屏蔽右边的数据宽度
		//RSpace = (int)(paint.measureText(closeRight) + 10);
		RSpace = 0;
		
		axisLabelHeight = Font.getFontHeight(dTextSize);
		
		graphicsQuoteWidth = width - LSpace - RSpace;
		SPACE = graphicsQuoteWidth / MINUTES;
		topTitleHeight = axisLabelHeight;
		graphicsQuoteHeight = height - axisLabelHeight - topTitleHeight; 
		price_row_num = 2;//(int)graphicsQuoteHeight/3/25;
		volume_row_num = price_row_num;
		price_row_height = graphicsQuoteHeight/price_row_num/3;
		volume_row_height = price_row_height;
		
		calc_zf();
		
		title1 = "分时:";
		title7 = "时间:";
		title8 = "";
		title2 = "";
		
		mpaint.setColor(GlobalColor.colorLabelName);
		
		canvas.drawText(title7+title8, LSpace, axisLabelHeight - 5, mpaint);
		canvas.drawText(title1, LSpace + mpaint.measureText("时间:00:0000"), axisLabelHeight - 5, mpaint);
		canvas.drawText(title2, LSpace + mpaint.measureText("时间:00:0000分时:"), axisLabelHeight - 5, mpaint);
		
		if(isZs()){
			if(close == 0) close = 1000;
		}else{
			if(close == 0) close = 1;
		}
		
		low = Math.min(low, close);
		//upPrice = close * (1+max_zf);
		//downPrice = close * (1-max_zf);
		
		paint.setColor(GlobalColor.clrLine);
		canvas.drawLine(LSpace + graphicsQuoteWidth, topTitleHeight, LSpace + graphicsQuoteWidth, graphicsQuoteHeight + topTitleHeight, paint);
		
		// 画涨的行情
		upQuoteX = LSpace;
		upQuoteY = topTitleHeight;
		upQuoteWidth = (int)graphicsQuoteWidth;
		upQuoteHeight = price_row_num * price_row_height;
		for (int i=0; i<price_row_num; i++)
		{
			if (i==0)
			{
				canvas.drawLine(upQuoteX, upQuoteY + price_row_height * i, upQuoteX + upQuoteWidth, upQuoteY  + price_row_height * i, paint);
			}
			else	
				Graphics.drawDashline(canvas, upQuoteX, upQuoteY + price_row_height
						* i, upQuoteX + upQuoteWidth, upQuoteY
						+ price_row_height * i, paint);
		}
		
		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(upQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, upQuoteY, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, upQuoteY  + upQuoteHeight, paint);
			}
			else 
				Graphics.drawDashline(canvas, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE * i, upQuoteY, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE * i, upQuoteY  + upQuoteHeight, paint);
		}
		
		// 画跌的行情图
		downQuoteX = LSpace;
		downQuoteY = topTitleHeight + upQuoteHeight;
		downQuoteWidth = (int)graphicsQuoteWidth;
		
		downQuoteHeight = upQuoteHeight;
		
		paint.setColor(GlobalColor.clrLine); 
		for (int i=0; i<price_row_num; i++)
		{
			if (i==0)
			{
				canvas.drawLine(downQuoteX, downQuoteY + price_row_height * i, downQuoteX + downQuoteWidth, downQuoteY  + price_row_height * i, paint);
			}
			else
				Graphics.drawDashline(canvas, downQuoteX, downQuoteY + price_row_height * i,downQuoteX + downQuoteWidth,downQuoteY  + price_row_height * i, paint);	
		}

		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY  + downQuoteHeight +1, paint);
			}
			else
				Graphics.drawDashline(canvas, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY  + downQuoteHeight, paint);
		}
		
		// 画成交量图
    	volumeX = LSpace;
    	volumeY = topTitleHeight + upQuoteHeight + downQuoteHeight ;
    	volumeWidth = (int)graphicsQuoteWidth; 
    	volumeHeight = graphicsQuoteHeight - upQuoteHeight - downQuoteHeight;

		volume_row_height = volumeHeight / volume_row_num;

		paint.setColor(GlobalColor.clrLine); 
		for (int i=0; i<=volume_row_num; i++)
		{
			if (i == 0)
			{
				canvas.drawLine(volumeX, volumeY + volume_row_height * i, volumeX + volumeWidth, volumeY  + volume_row_height * i, paint);
			}
			else
			{
				if (i != volume_row_num)
					Graphics.drawDashline(canvas, volumeX, volumeY + volume_row_height * i, volumeX + volumeWidth, volumeY  + volume_row_height * i, paint);
			}
		}
		
		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY  + volumeHeight, paint);
			}	
			else
				Graphics.drawDashline(canvas, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY  + volumeHeight, paint);  
		}
		// 画横线
	  	canvas.drawLine(LSpace, graphicsQuoteHeight + topTitleHeight, LSpace + graphicsQuoteWidth, graphicsQuoteHeight + topTitleHeight, paint);
    }
    
    private void drawChart(Canvas canvas) throws JSONException {
    	Log.i("@@@@@@@@@draw tick@@@@@@@@@@", quoteArray+">>>>>>>>>");
    	//Log.i("@@@@@@@@@@@@@@@@@@@", quoteArray.length()+">>>>>>>>>");
		if(quoteArray==null||quoteArray.isNull(0)) return;
		
    	canvas.drawColor(GlobalColor.clearSCREEN);
    	
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
		
		mpaint = new Paint();
		mpaint.setTypeface(Typeface.DEFAULT_BOLD);
		mpaint.setAntiAlias(true);
		mpaint.setTextAlign(Paint.Align.LEFT);
		mpaint.setStyle(Paint.Style.STROKE);
		mpaint.setTextSize(dTextSize);
		
		/**
		 * 画图表之前先计算宽度
		 */
		closeLeft = Utils.dataFormation(high, stockdigit);
		closeRight = "00.00%";
		LSpace = (int)(Math.max(mpaint.measureText(closeLeft),mpaint.measureText(String.valueOf(Math.round(highvolume)))));
		//暂时屏蔽右边的数据宽度
		//RSpace = (int)(paint.measureText(closeRight) + 10);
		RSpace = 0;
		
		axisLabelHeight = Font.getFontHeight(dTextSize);
		
		graphicsQuoteWidth = width - LSpace - RSpace;
		SPACE = graphicsQuoteWidth / MINUTES;
		topTitleHeight = axisLabelHeight;
		graphicsQuoteHeight = height - axisLabelHeight - topTitleHeight; 
		price_row_num = 2;//(int)graphicsQuoteHeight/3/25;
		volume_row_num = price_row_num;
		price_row_height = graphicsQuoteHeight/price_row_num/3;
		volume_row_height = price_row_height;
		
		calc_zf();
		
		title1 = "分时:";
		title7 = "时间:";
		title8 = quoteArray.getJSONArray(isTrackNumber).getString(3);
		title2 = Utils.dataFormation(quoteArray.getJSONArray(isTrackNumber).getDouble(0), stockdigit);
		
		mpaint.setColor(GlobalColor.colorLabelName);
		
		canvas.drawText(title7+title8.substring(11, 16), LSpace, axisLabelHeight - 5, mpaint);
		canvas.drawText(title1, LSpace + mpaint.measureText("时间:00:0000"), axisLabelHeight - 5, mpaint);
		if(quoteArray.getJSONArray(isTrackNumber).getDouble(0)==0) {
			mpaint.setColor(GlobalColor.colorPriceEqual);
		}
		else if(quoteArray.getJSONArray(isTrackNumber).getDouble(0)>close) {
			mpaint.setColor(GlobalColor.colorpriceUp);
		}
		else if(quoteArray.getJSONArray(isTrackNumber).getDouble(0)<close) {
			mpaint.setColor(GlobalColor.colorPriceDown);
		}
		else {
			mpaint.setColor(GlobalColor.colorPriceEqual);
		}
		canvas.drawText(title2, LSpace + mpaint.measureText("时间:00:0000分时:"), axisLabelHeight - 5, mpaint);
		
		if (!this.isZs()){
			if("cf".equals(exchange) || "dc".equals(exchange) || 
					"sf".equals(exchange) || "cz".equals(exchange) || "hk".equals(exchange) ) {
			}
			else {
				mpaint.setColor(GlobalColor.colorLabelName);
				title9= "均价:";
				canvas.drawText(title9, LSpace + mpaint.measureText("时间:00:000000分时:"+title2), axisLabelHeight-5, mpaint);
				double avePrice=0;
				double cjje = quoteArray.getJSONArray(isTrackNumber).getDouble(2);
				int scaleCount = 1;
				scaleCount = Utils.getCoefficient(exchange, stockcode); 
				double cjsl = quoteArray.getJSONArray(isTrackNumber).getDouble(1) * scaleCount;
				if(cjsl>0){
					avePrice = cjje/cjsl;
				}else{
					avePrice = jrkp;
				}
				if(avePrice==0) {
					mpaint.setColor(GlobalColor.colorPriceEqual);
				}
				else if(avePrice>close) {
					mpaint.setColor(GlobalColor.colorpriceUp);
				}
				else if(avePrice<close) {
					mpaint.setColor(GlobalColor.colorPriceDown);
				}
				else {
					mpaint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.drawText(Utils.dataFormation(avePrice,stockdigit), LSpace + mpaint.measureText("时间:00:000000分时:"+title2+"均价:"), axisLabelHeight-5, mpaint);
			}
		}
		
		if(isZs()){
			if(close == 0) close = 1000;
		}else{
			if(close == 0) close = 1;
		}
		
		low = Math.min(low, close);
		//upPrice = close * (1+max_zf);
		//downPrice = close * (1-max_zf);
		
		paint.setColor(GlobalColor.clrLine);
		canvas.drawLine(LSpace + graphicsQuoteWidth, topTitleHeight, LSpace + graphicsQuoteWidth, graphicsQuoteHeight + topTitleHeight, paint);
		
		// 画涨的行情
		upQuoteX = LSpace;
		upQuoteY = topTitleHeight;
		upQuoteWidth = (int)graphicsQuoteWidth;
		upQuoteHeight = price_row_num * price_row_height;
		for (int i=0; i<price_row_num; i++)
		{
			if (i==0)
			{
				canvas.drawLine(upQuoteX, upQuoteY + price_row_height * i, upQuoteX + upQuoteWidth, upQuoteY  + price_row_height * i, paint);
			}
			else	
				Graphics.drawDashline(canvas, upQuoteX, upQuoteY + price_row_height
						* i, upQuoteX + upQuoteWidth, upQuoteY
						+ price_row_height * i, paint);
		}
		
		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(upQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, upQuoteY, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, upQuoteY  + upQuoteHeight, paint);
			}
			else 
				Graphics.drawDashline(canvas, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE * i, upQuoteY, upQuoteX + MINUTES/DIVIDE_COUNT * SPACE * i, upQuoteY  + upQuoteHeight, paint);
		}
		//scale = close * max_zf / price_row_num;
		scale = Arith.div(close * max_zf, price_row_num, stockdigit+1);

		mpaint.setTextAlign(Paint.Align.RIGHT);
		mpaint.setColor(GlobalColor.colorPriceEqual);
		canvas.drawText(Utils.dataFormation(close, stockdigit), upQuoteX, upQuoteY + upQuoteHeight + axisLabelHeight/2, mpaint);

		mpaint.setColor(GlobalColor.colorpriceUp);
		for (int i=1; i<=price_row_num; i++)
		{
			AxisLabelPrice = close + scale * i;
			canvas.drawText(Utils.dataFormation(AxisLabelPrice, stockdigit), upQuoteX, upQuoteY + upQuoteHeight - price_row_height * i + axisLabelHeight/2, mpaint);
		}
		
		// 画跌的行情图
		downQuoteX = LSpace;
		downQuoteY = topTitleHeight + upQuoteHeight;
		downQuoteWidth = (int)graphicsQuoteWidth;
		
		downQuoteHeight = upQuoteHeight;
		
		paint.setColor(GlobalColor.clrLine); 
		for (int i=0; i<price_row_num; i++)
		{
			if (i==0)
			{
				canvas.drawLine(downQuoteX, downQuoteY + price_row_height * i, downQuoteX + downQuoteWidth, downQuoteY  + price_row_height * i, paint);
			}
			else
				Graphics.drawDashline(canvas, downQuoteX, downQuoteY + price_row_height * i,downQuoteX + downQuoteWidth,downQuoteY  + price_row_height * i, paint);	
		}

		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY  + downQuoteHeight +1, paint);
			}
			else
				Graphics.drawDashline(canvas, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY, downQuoteX + MINUTES/DIVIDE_COUNT * SPACE*i, downQuoteY  + downQuoteHeight, paint);
		}
		
		mpaint.setColor(GlobalColor.colorPriceDown);
		for (int i=1; i<price_row_num; i++)
		{
			AxisLabelPrice = close - scale * i;
			canvas.drawText(Utils.dataFormation(AxisLabelPrice, stockdigit), upQuoteX, upQuoteY + upQuoteHeight + price_row_height * i + axisLabelHeight/2, mpaint);
		}
		
		//画 分时 价格线
		// added by hujun for 20110511判断是否是开放式基金，如果是则不画
		if (actualDataLen > 0 && !this.isOpenFund())
		{
			if("cf".equals(exchange) || "dc".equals(exchange) || 
					"sf".equals(exchange) || "cz".equals(exchange) ) {
				scale = upQuoteHeight / (close * max_zf);
				paint.setColor(GlobalColor.colorFZLine); 
				int quotelen = quoteArray.length();
				double x1 = 0;
				double y1 = 0;
				double x2 = 0;
				double y2 = 0;
				double temp = 0;
				  	
				x1 = upQuoteX;
				double lastnewp = 0;
				double nownewp = 0;
				nownewp = quoteArray.getJSONArray(0).getDouble(0);
				if (nownewp==0) nownewp = close;
				if (nownewp >= close)
				{
					temp = (nownewp - close) * scale;
					y1 = topTitleHeight + upQuoteHeight - temp;
				}
				else
				{
					temp = (close - nownewp) * scale;
					y1 = topTitleHeight + upQuoteHeight + temp;
				}
				lastnewp = nownewp;
				for (int i=1; i<quotelen; i++)
				{
					x2 = upQuoteX + SPACE * i;
					nownewp = quoteArray.getJSONArray(i).getDouble(0);
					if (nownewp==0)  nownewp = lastnewp; 
					if (nownewp >= close)
				  	{
				  		temp = (nownewp - close) * scale;
				  		y2 = topTitleHeight + upQuoteHeight - temp;
				  	}
				  	else
				  	{
				  		temp = (close - nownewp) * scale;
				  		y2 = topTitleHeight + upQuoteHeight + temp;
				  	}
					lastnewp = nownewp;
					
					canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
					
					x1 = x2;
					y1 = y2;
				} // end for
				
				paint.setColor(GlobalColor.colorFZAvePriceLine); 
				x1 = upQuoteX;
				double cjje = quoteArray.getJSONArray(0).getDouble(0);
				double avePrice = cjje;
				double lastavg = 0;
				if (avePrice > high ){
					avePrice = high;
				}
				if (avePrice < low){
					avePrice = low;
				}
				if (avePrice >= close)
				{
					temp = (avePrice - close) * scale;
					y1 = topTitleHeight + upQuoteHeight - temp;
				}
				else
				{
					temp = (close - avePrice) * scale;
					y1 = topTitleHeight + upQuoteHeight + temp;
				}
				lastavg = avePrice;
				double xl = quoteArray.getJSONArray(0).getDouble(1);
				double mathpjj = quoteArray.getJSONArray(0).getDouble(0) * xl;
				for (int i=1; i<quotelen; i++)
				{
					x2 = upQuoteX + SPACE * i;
					mathpjj +=quoteArray.getJSONArray(i).getDouble(0) * (quoteArray.getJSONArray(i).getDouble(1)- quoteArray.getJSONArray(i-1).getDouble(1)); 
					if (mathpjj==0){
						if (lastavg==0 ){
							avePrice = close;
						}else{
							avePrice = lastavg;
						}
					}else{
						avePrice = mathpjj / quoteArray.getJSONArray(i).getDouble(1);
					}
					lastavg = avePrice;
					if (avePrice > high ){
						avePrice = high;
					}
					if (avePrice < low){
						avePrice = low;
					} 
					if (avePrice >= close)
					{
						temp = (avePrice - close) * scale;
						y2 = topTitleHeight + upQuoteHeight - temp;
				 	}
				  	else
				  	{
				  		temp = (close - avePrice) * scale;
				  		y2 = topTitleHeight + upQuoteHeight + temp;
				  	}
					canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
					  	
					x1 = x2;
					y1 = y2;
				} // end for
			}
			else {
				scale = upQuoteHeight / (close * max_zf);
				paint.setColor(GlobalColor.colorFZLine); 
				int quotelen = quoteArray.length();
				double x1 = 0;
				double y1 = 0;
				double x2 = 0;
				double y2 = 0;
				double temp = 0;
				  	
				x1 = upQuoteX;
				double lastnewp = 0;
				double nownewp = 0;
				nownewp = quoteArray.getJSONArray(0).getDouble(0);
				if (nownewp==0) nownewp = close;
				if (nownewp >= close)
				{
					temp = (nownewp - close) * scale;
					y1 = topTitleHeight + upQuoteHeight - temp;
				}
				else
				{
					temp = (close - nownewp) * scale;
					y1 = topTitleHeight + upQuoteHeight + temp;
				}
				lastnewp = nownewp;
				for (int i=1; i<quotelen; i++)
				{
					x2 = upQuoteX + SPACE * i;
					nownewp = quoteArray.getJSONArray(i).getDouble(0);
					if (nownewp==0)  nownewp = lastnewp; 
					if (nownewp >= close)
				  	{
				  		temp = (nownewp - close) * scale;
				  		y2 = topTitleHeight + upQuoteHeight - temp;
				  	}
				  	else
				  	{
				  		temp = (close - nownewp) * scale;
				  		y2 = topTitleHeight + upQuoteHeight + temp;
				  	}
					lastnewp = nownewp;
					
					canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
					
					x1 = x2;
					y1 = y2;
				} // end for
				
				if (this.isZs()) {
					// 画股票价格均线	，指数画的是上证领先或深证领先
					if("sz".equals(exchange)||"sh".equals(exchange)) {
						if(("000001".equals(stockcode)||"399001".equals(stockcode))&&!quoteData.isNull("data2")) {
							paint.setColor(GlobalColor.colorFZAvePriceLine); 
							x1 = upQuoteX;
							double avePrice = quoteData.getJSONArray("data2").getJSONArray(0).getDouble(0);
							if (avePrice > high ){
								avePrice = high;
							}
							if (avePrice < low){
								avePrice = low;
							}
							if (avePrice >= close)
							{
								temp = (avePrice - close) * scale;
								y1 = topTitleHeight + upQuoteHeight - temp;
							}
							else
							{
								temp = (close - avePrice) * scale;
								y1 = topTitleHeight + upQuoteHeight + temp;
							}
							int len = quoteData.getJSONArray("data2").length();
							for (int i=1; i<len; i++)
							{
								if("15:00".equals(quoteData.getJSONArray("data2").getJSONArray(i).getString(1).substring(11, 16))) {
									
								}
								else {
									x2 = upQuoteX + SPACE * i;
									avePrice = quoteData.getJSONArray("data2").getJSONArray(i).getDouble(0);
									if (avePrice > high ){
										avePrice = high;
									}
									if (avePrice < low){
										avePrice = low;
									} 
									if (avePrice >= close)
									{
										temp = (avePrice - close) * scale;
										y2 = topTitleHeight + upQuoteHeight - temp;
								 	}
								  	else
								  	{
								  		temp = (close - avePrice) * scale;
								  		y2 = topTitleHeight + upQuoteHeight + temp;
								  	}
									canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
									  	
									x1 = x2;
									y1 = y2;
								}
							} // end for
						}
						else {
							paint.setColor(GlobalColor.colorFZAvePriceLine); 
							x1 = upQuoteX;
							double cjje = quoteArray.getJSONArray(0).getDouble(0);
							double avePrice = cjje;
							double lastavg = 0;
							if (avePrice > high ){
								avePrice = high;
							}
							if (avePrice < low){
								avePrice = low;
							}
							if (avePrice >= close)
							{
								temp = (avePrice - close) * scale;
								y1 = topTitleHeight + upQuoteHeight - temp;
							}
							else
							{
								temp = (close - avePrice) * scale;
								y1 = topTitleHeight + upQuoteHeight + temp;
							}
							lastavg = avePrice;
							double xl = quoteArray.getJSONArray(0).getDouble(1);
							double mathpjj = quoteArray.getJSONArray(0).getDouble(0) * xl;
							for (int i=1; i<quotelen; i++)
							{
								x2 = upQuoteX + SPACE * i;
								mathpjj +=quoteArray.getJSONArray(i).getDouble(0) * (quoteArray.getJSONArray(i).getDouble(1)- quoteArray.getJSONArray(i-1).getDouble(1)); 
								if (mathpjj==0){
									if (lastavg==0 ){
										avePrice = close;
									}else{
										avePrice = lastavg;
									}
								}else{
									avePrice = mathpjj / quoteArray.getJSONArray(i).getDouble(1);
								}
								lastavg = avePrice;
								if (avePrice > high ){
									avePrice = high;
								}
								if (avePrice < low){
									avePrice = low;
								} 
								if (avePrice >= close)
								{
									temp = (avePrice - close) * scale;
									y2 = topTitleHeight + upQuoteHeight - temp;
							 	}
							  	else
							  	{
							  		temp = (close - avePrice) * scale;
							  		y2 = topTitleHeight + upQuoteHeight + temp;
							  	}
								canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
								  	
								x1 = x2;
								y1 = y2;
							} // end for
						}
					}
				}	
				else {
					paint.setColor(GlobalColor.colorFZAvePriceLine); 
					x1 = upQuoteX;
					double cjje = quoteArray.getJSONArray(0).getDouble(2);
					int scaleCount = 1;
					scaleCount = Utils.getCoefficient(exchange, stockcode); 
					double cjsl = quoteArray.getJSONArray(0).getDouble(1) * scaleCount;
					double avePrice = cjje/cjsl;
					double lastavg = 0;
					if (cjsl==0) avePrice = close;	
					if (avePrice > high ){
						avePrice = high;
					}
					if (avePrice < low){
						avePrice = low;
					}
					if (avePrice >= close)
					{
						temp = (avePrice - close) * scale;
						y1 = topTitleHeight + upQuoteHeight - temp;
					}
					else
					{
						temp = (close - avePrice) * scale;
						y1 = topTitleHeight + upQuoteHeight + temp;
					}
					lastavg = avePrice;
					for (int i=1; i<quotelen; i++)
					{
						x2 = upQuoteX + SPACE * i;
						cjje = quoteArray.getJSONArray(i).getDouble(2);
						cjsl = quoteArray.getJSONArray(i).getDouble(1) * scaleCount; // 因为数据库默认存的是手数
						if (cjsl==0){
							if (lastavg==0 ){
								avePrice = close;
							}else{
								avePrice = lastavg;
							}
						}else{
							avePrice = cjje / cjsl;
						}
						lastavg = avePrice;
						if (avePrice > high ){
							avePrice = high;
						}
						if (avePrice < low){
							avePrice = low;
						} 
						if (avePrice >= close)
						{
							temp = (avePrice - close) * scale;
							y2 = topTitleHeight + upQuoteHeight - temp;
					 	}
					  	else
					  	{
					  		temp = (close - avePrice) * scale;
					  		y2 = topTitleHeight + upQuoteHeight + temp;
					  	}
						canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
						  	
						x1 = x2;
						y1 = y2;
					} // end for
				}
			}
		}
		
		// 画成交量图
    	volumeX = LSpace;
    	volumeY = topTitleHeight + upQuoteHeight + downQuoteHeight ;
    	volumeWidth = (int)graphicsQuoteWidth; 
    	volumeHeight = graphicsQuoteHeight - upQuoteHeight - downQuoteHeight;

		volume_row_height = volumeHeight / volume_row_num;

		paint.setColor(GlobalColor.clrLine); 
		for (int i=0; i<=volume_row_num; i++)
		{
			if (i == 0)
			{
				canvas.drawLine(volumeX, volumeY + volume_row_height * i, volumeX + volumeWidth, volumeY  + volume_row_height * i, paint);
			}
			else
			{
				if (i != volume_row_num)
					Graphics.drawDashline(canvas, volumeX, volumeY + volume_row_height * i, volumeX + volumeWidth, volumeY  + volume_row_height * i, paint);
			}
		}
		
		for (int i=0; i<DIVIDE_COUNT; i++)
		{
			if (i==0)
			{
				canvas.drawLine(volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY  + volumeHeight, paint);
			}	
			else
				Graphics.drawDashline(canvas, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY, volumeX + MINUTES/DIVIDE_COUNT * SPACE * i, volumeY  + volumeHeight, paint);  
		}
		
		if (this.isZs())
		{
			//highVolume =  TickUtil.gethighAmount(quoteData.getJSONArray("data"));
			highVolume =  highamount;
		}
		else
		{
			//highVolume =  TickUtil.gethighVolume(quoteData.getJSONArray("data"));
			highVolume = highvolume;
		}
		
		if(highVolume==0)
		{
			if (this.isZs())
			{
				// 这里需要修改
				highVolume = volume_row_num * 4 * 100; // 默认最小单位为4，如果是8行那么highVolume=32
			}
			else
			{
				highVolume = volume_row_num * 4 * 100; // 默认最小单位为4，如果是8行那么highVolume=32
			}
		}
		if (highVolume < volume_row_num+1) highVolume = volume_row_num + 1;
		scale = highVolume / volume_row_num;
		int volumeLabelY = volumeY + Font.getFontHeight(dTextSize)/2;
		
		mpaint.setColor(GlobalColor.clr_tick_volume);
		for (int i=0; i<=volume_row_num; i++)
		{
	  		if (i != volume_row_num)
	  		{
	  			AxisLabelVolume = highVolume - scale * i;
	  			if (this.isZs())
	  				AxisLabelVolume = Math.round(AxisLabelVolume / 10000);
	  			else
	  				AxisLabelVolume = Math.round(AxisLabelVolume);
	  	  		 
	  			canvas.drawText(String.valueOf((int)AxisLabelVolume), upQuoteX, volumeLabelY + volume_row_height * i, mpaint);
	  		}
		}
	  
	  	// 画成交量图
		if (actualDataLen > 0)
		{
			scale = volumeHeight / highVolume;
			paint.setColor(GlobalColor.colorVolumeLine); 
		  	double prevVol = 0;
		  	double temp = 0;
		  	for (int i=0; i<actualDataLen; i++)
		  	{
		  		if (this.isZs())
					temp =  (quoteArray.getJSONArray(i).getDouble(2) - prevVol)* scale;
			  	else
					temp =  (quoteArray.getJSONArray(i).getDouble(1) - prevVol)* scale;
			  	float x1 = volumeX + SPACE * i;
			  	float y1 = (float)(volumeY + volumeHeight - temp);
	
			  	float x2 = x1;
			  	float y2 = volumeY + volumeHeight;
				canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, paint);
				if (this.isZs())
					prevVol = quoteArray.getJSONArray(i).getDouble(2);
				else
					prevVol = quoteArray.getJSONArray(i).getDouble(1);
			}
		}

		drawTimeX(canvas);
		
		// 画横线
		paint.setColor(GlobalColor.clrLine);
	  	canvas.drawLine(LSpace, graphicsQuoteHeight + topTitleHeight, LSpace + graphicsQuoteWidth, graphicsQuoteHeight + topTitleHeight, paint);
	  	
		if(isTrackStatus) {
		  	canvas.save();
			//显示坐标轴
			paint.setColor(GlobalColor.colorLine);
			canvas.drawLine(trackLineV, topTitleHeight, trackLineV, graphicsQuoteHeight + topTitleHeight, paint);
		  	canvas.restore();
		}
    }
    
    private void drawTimeX(Canvas canvas) {
		mpaint.setColor(GlobalColor.colorLabelName);
		mpaint.setTextAlign(Paint.Align.LEFT);
		if("hk".equals(exchange)) {
			stime = "09:30";
			etime = "16:00";
		}
		else if("cf".equals(exchange)) {
			stime = "09:15";
			etime = "15:15";
		}
		else if("dc".equals(exchange) || "sf".equals(exchange) ||
				"cz".equals(exchange)) {
			stime = "09:00";
			etime = "15:00";
		}
		else {
			stime = "09:30";
			etime = "15:00";
		}
		canvas.drawText(stime, LSpace, graphicsQuoteHeight + topTitleHeight + axisLabelHeight, mpaint);
		//mpaint.setTextAlign(Paint.Align.CENTER);
		//canvas.drawText("11:30", graphicsQuoteWidth/2 + LSpace, graphicsQuoteHeight + topTitleHeight + axisLabelHeight, mpaint);
		mpaint.setTextAlign(Paint.Align.RIGHT);
		canvas.drawText(etime, width, graphicsQuoteHeight + topTitleHeight + axisLabelHeight, mpaint);
    }

	private void repairData() throws JSONException {
		close = quoteData.getDouble("close");
		high = quoteData.getDouble("high");
		low = quoteData.getDouble("low");
		jrkp = quoteData.getDouble("jrkp");
		
		Date dt = new Date();
		int year = dt.getYear();
		int month = dt.getMonth(); 
		int day = dt.getDate();
		int hour = dt.getHours();
		int minute = dt.getMinutes();
		JSONArray list = quoteData.getJSONArray("data");
		if(quoteData.getString("quotetime")!=null&&!quoteData.getString("quotetime").equals("")) {
			year = Integer.parseInt(quoteData.getString("quotetime").substring(0,4));
			month = Integer.parseInt(quoteData.getString("quotetime").substring(5,7)) - 1;
			day = Integer.parseInt(quoteData.getString("quotetime").substring(8,10));
			hour = Integer.parseInt(quoteData.getString("quotetime").substring(11,13));
			minute = Integer.parseInt(quoteData.getString("quotetime").substring(14,16));
			dt = new Date(year,month,day,hour,minute);
		}
		JSONArray jsonArray = new JSONArray();
		if("hk".equals(exchange)) {
			this.MINUTES = 300;
			
			Date dt1 = new Date(year,month,day,9,30);
			Date dt2 = new Date(year,month,day,12,0);
			Date dt3 = new Date(year,month,day,13,31);
			Date dt4 = new Date(year,month,day,16,0);
			
			long hopelen = 0;
			if (dt.getTime()<dt1.getTime()){
				// 0 笔数据
				hopelen = 0;
			}
			if (dt.getTime()>=dt1.getTime() && dt.getTime() < dt2.getTime()){
				hopelen = (dt.getTime() - dt1.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt2.getTime() && dt.getTime()<dt3.getTime()){
				hopelen = 151;
			}
			if (dt.getTime()>=dt3.getTime() && dt.getTime()<dt4.getTime()){
				hopelen = 151 + (dt.getTime()-dt3.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt4.getTime()){
				hopelen = 301;
			}
			//处理早上9.15 到 9.25 没有时间的情况。
			if (quoteData.getString("quotetime")=="null"||quoteData.getString("quotetime").equals("")){
				hopelen = 1;
			} 

			String time = "";
			for (int i=0;i<hopelen;i++) {
				
				if (i<151){
					time = Utils.format(new Date(dt1.getTime()+1000*60*i));
				}
				if (i>=151 && i<=301){
					time = Utils.format(new Date(dt3.getTime()+1000*60*(i-151)));
				}
				Boolean flag = false;

				JSONArray json = new JSONArray();
				for (int j=0;j<list.length();j++){
					if (list.getJSONArray(j).getString(3).equals(time)){	
						json.put(0, list.getJSONArray(j).getDouble(0));
						json.put(1, list.getJSONArray(j).getDouble(1));
						json.put(2, list.getJSONArray(j).getDouble(2));
						json.put(3, list.getJSONArray(j).getString(3));
						json.put(4, 1);//是否是真实的数据
						if (i==0){
							json.put(5, list.getJSONArray(j).getDouble(1));//一分钟的成交总数量
							json.put(6, list.getJSONArray(j).getDouble(2));//一分钟的成交总额
						}else{
							if (jsonArray.getJSONArray(i-1).getInt(4)==1){
								json.put(5, list.getJSONArray(j).getDouble(1) - jsonArray.getJSONArray(i-1).getInt(1));
								json.put(6, list.getJSONArray(j).getDouble(2) - jsonArray.getJSONArray(i-1).getInt(2));
							}else{
								json.put(5, 0);
								json.put(6, 0);
							}
						}
						//json.put(7, (list.getJSONArray(j).getDouble(2)/list.getJSONArray(j).getDouble(1))/100);//均价
						flag = true;
						break;
					}
				}
				//该笔数据丢失，
				if (!flag) {
					if (i==0){					
						json.put(1, 0);
						json.put(2, 0);
						json.put(3, time);
						json.put(0, quoteData.getDouble("close"));
					}else{
						json.put(1, jsonArray.getJSONArray(i-1).getDouble(1));
						json.put(2, jsonArray.getJSONArray(i-1).getDouble(2));
						json.put(3, time);
						json.put(0, ((JSONArray)jsonArray.get(i-1)).getDouble(0));
					}	
					json.put(4, 0);
					json.put(5, 0);
					json.put(6, 0);
					json.put(7, 0);
				}
				jsonArray.put(json);
			}
		}
		else if("cf".equals(exchange)) {
			this.MINUTES = 270;
			
			Date dt1 = new Date(year,month,day,9,15);
			Date dt2 = new Date(year,month,day,11,30);
			Date dt3 = new Date(year,month,day,13,1);
			Date dt4 = new Date(year,month,day,15,15);
			
			long hopelen = 0;
			if (dt.getTime()<dt1.getTime()){
				// 0 笔数据
				hopelen = 0;
			}
			if (dt.getTime()>=dt1.getTime() && dt.getTime() < dt2.getTime()){
				hopelen = (dt.getTime() - dt1.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt2.getTime() && dt.getTime()<dt3.getTime()){
				hopelen = 136;
			}
			if (dt.getTime()>=dt3.getTime() && dt.getTime()<dt4.getTime()){
				hopelen = 136 + (dt.getTime()-dt3.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt4.getTime()){
				hopelen = 271;
			}
			//处理早上9.15 到 9.25 没有时间的情况。
			if (quoteData.getString("quotetime")=="null"){
				hopelen = 0;
			} 
			
			String time = "";
			for (int i=0;i<hopelen;i++) {
				
				if (i<136){
					time = Utils.format(new Date(dt1.getTime()+1000*60*i));
				}
				if (i>=136 && i<=271){
					time = Utils.format(new Date(dt3.getTime()+1000*60*(i-136)));
				}
				Boolean flag = false;

				JSONArray json = new JSONArray();
				for (int j=0;j<list.length();j++){
					if (list.getJSONArray(j).getString(3).equals(time)){	
						json.put(0, list.getJSONArray(j).getDouble(0));
						json.put(1, list.getJSONArray(j).getDouble(1));
						json.put(2, list.getJSONArray(j).getDouble(2));
						json.put(3, list.getJSONArray(j).getString(3));
						json.put(4, 1);//是否是真实的数据
						if (i==0){
							json.put(5, list.getJSONArray(j).getDouble(1));//一分钟的成交总数量
							json.put(6, list.getJSONArray(j).getDouble(2));//一分钟的成交总额
						}else{
							if (jsonArray.getJSONArray(i-1).getInt(4)==1){
								json.put(5, list.getJSONArray(j).getDouble(1) - jsonArray.getJSONArray(i-1).getInt(1));
								json.put(6, list.getJSONArray(j).getDouble(2) - jsonArray.getJSONArray(i-1).getInt(2));
							}else{
								json.put(5, 0);
								json.put(6, 0);
							}
						}
						//json.put(7, (list.getJSONArray(j).getDouble(2)/list.getJSONArray(j).getDouble(1))/100);//均价
						flag = true;
						break;
					}
				}
				//该笔数据丢失，
				if (!flag) {
					if (i==0){					
						json.put(1, 0);
						json.put(2, 0);
						json.put(3, time);
						json.put(0, quoteData.getDouble("jrkp"));
					}else{
						json.put(1, jsonArray.getJSONArray(i-1).getDouble(1));
						json.put(2, jsonArray.getJSONArray(i-1).getDouble(2));
						json.put(3, time);
						json.put(0, ((JSONArray)jsonArray.get(i-1)).getDouble(0));
					}	
					json.put(4, 0);
					json.put(5, 0);
					json.put(6, 0);
					json.put(7, 0);
				}
				jsonArray.put(json);
			}
		}
		else if("dc".equals(exchange) || "sf".equals(exchange) ||
				"cz".equals(exchange)) {
			this.MINUTES = 225;
			
			Date dt1 = new Date(year,month,day,9,0);
			Date dt2 = new Date(year,month,day,10,15);
			Date dt3 = new Date(year,month,day,10,31);
			Date dt4 = new Date(year,month,day,11,30);
			Date dt5 = new Date(year,month,day,13,31);
			Date dt6 = new Date(year,month,day,15,0);
			
			long hopelen = 0;
			if (dt.getTime()<dt1.getTime()){
				// 0 笔数据
				hopelen = 0;
			}
			if (dt.getTime()>=dt1.getTime() && dt.getTime() < dt2.getTime()){
				hopelen = (dt.getTime() - dt1.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt2.getTime() && dt.getTime()<dt3.getTime()){
				hopelen = 76;
			}
			if (dt.getTime()>=dt3.getTime() && dt.getTime()<dt4.getTime()){
				hopelen = 76 + (dt.getTime()-dt3.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt4.getTime() && dt.getTime()<dt5.getTime()){
				hopelen = 136;
			}
			if (dt.getTime()>=dt5.getTime() && dt.getTime()<dt6.getTime()){
				hopelen = 136 + (dt.getTime()-dt5.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt6.getTime()){
				hopelen = 226;
			}
			//处理早上9.15 到 9.25 没有时间的情况。
			if (quoteData.getString("quotetime")=="null"){
				hopelen = 0;
			} 
			
			String time = "";
			for (int i=0;i<hopelen;i++) {
				
				if (i<76){
					time = Utils.format(new Date(dt1.getTime()+1000*60*i));
				}
				if (i>=76 && i<136){
					time = Utils.format(new Date(dt3.getTime()+1000*60*(i-76)));
				}
				if (i>=136 && i<=226){
					time = Utils.format(new Date(dt5.getTime()+1000*60*(i-136)));
				}
				Boolean flag = false;
				JSONArray json = new JSONArray();
				for (int j=0;j<list.length();j++){
					if (list.getJSONArray(j).getString(3).equals(time)){	
						json.put(0, list.getJSONArray(j).getDouble(0));
						json.put(1, list.getJSONArray(j).getDouble(1));
						json.put(2, list.getJSONArray(j).getDouble(2));
						json.put(3, list.getJSONArray(j).getString(3));
						json.put(4, 1);//是否是真实的数据
						if (i==0){
							json.put(5, list.getJSONArray(j).getDouble(1));//一分钟的成交总数量
							json.put(6, list.getJSONArray(j).getDouble(2));//一分钟的成交总额
						}else{
							if (jsonArray.getJSONArray(i-1).getInt(4)==1){
								json.put(5, list.getJSONArray(j).getDouble(1) - jsonArray.getJSONArray(i-1).getInt(1));
								json.put(6, list.getJSONArray(j).getDouble(2) - jsonArray.getJSONArray(i-1).getInt(2));
							}else{
								json.put(5, 0);
								json.put(6, 0);
							}
						}
						//json.put(7, (list.getJSONArray(j).getDouble(2)/list.getJSONArray(j).getDouble(1))/100);//均价
						flag = true;
						break;
					}
				}
				//该笔数据丢失，
				if (!flag) {
					if (i==0){					
						json.put(1, 0);
						json.put(2, 0);
						json.put(3, time);
						json.put(0, quoteData.getDouble("jrkp"));
					}else{
						json.put(1, jsonArray.getJSONArray(i-1).getDouble(1));
						json.put(2, jsonArray.getJSONArray(i-1).getDouble(2));
						json.put(3, time);
						json.put(0, ((JSONArray)jsonArray.get(i-1)).getDouble(0));
					}	
					json.put(4, 0);
					json.put(5, 0);
					json.put(6, 0);
					json.put(7, 0);
				}
				jsonArray.put(json);
			}
		}
		else {
			Date dt1 = new Date(year,month,day,9,30);
			Date dt2 = new Date(year,month,day,11,30);
			Date dt3 = new Date(year,month,day,13,1);
			Date dt4 = new Date(year,month,day,15,0);
			
			long hopelen = 0;
			if (dt.getTime()<dt1.getTime()){
				// 0 笔数据
				hopelen = 0;
			}
			if (dt.getTime()>=dt1.getTime() && dt.getTime() < dt2.getTime()){
				hopelen = (dt.getTime() - dt1.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt2.getTime() && dt.getTime()<dt3.getTime()){
				hopelen = 121;
			}
			if (dt.getTime()>=dt3.getTime() && dt.getTime()<dt4.getTime()){
				hopelen = 121 + (dt.getTime()-dt3.getTime())/1000/60 + 1;
			}
			if (dt.getTime()>=dt4.getTime()){
				hopelen = 241;
			}
			//处理早上9.15 到 9.25 没有时间的情况。
			if (quoteData.getString("quotetime")=="null"){
				hopelen = 0;
			} 
			
			String time = "";
			for (int i=0;i<hopelen;i++) {
				
				if (i<121){
					time = Utils.format(new Date(dt1.getTime()+1000*60*i));
				}
				if (i>=121 && i<=241){
					time = Utils.format(new Date(dt3.getTime()+1000*60*(i-121)));
				}
				Boolean flag = false;

				JSONArray json = new JSONArray();
				for (int j=0;j<list.length();j++){
					if (list.getJSONArray(j).getString(3).equals(time)){	
						json.put(0, list.getJSONArray(j).getDouble(0));
						json.put(1, list.getJSONArray(j).getDouble(1));
						json.put(2, list.getJSONArray(j).getDouble(2));
						json.put(3, list.getJSONArray(j).getString(3));
						json.put(4, 1);//是否是真实的数据
						if (i==0){
							json.put(5, list.getJSONArray(j).getDouble(1));//一分钟的成交总数量
							json.put(6, list.getJSONArray(j).getDouble(2));//一分钟的成交总额
						}else{
							if (jsonArray.getJSONArray(i-1).getInt(4)==1){
								json.put(5, list.getJSONArray(j).getDouble(1) - jsonArray.getJSONArray(i-1).getInt(1));
								json.put(6, list.getJSONArray(j).getDouble(2) - jsonArray.getJSONArray(i-1).getInt(2));
							}else{
								json.put(5, 0);
								json.put(6, 0);
							}
						}
						//json.put(7, (list.getJSONArray(j).getDouble(2)/list.getJSONArray(j).getDouble(1))/100);//均价
						flag = true;
						break;
					}
				}
				//该笔数据丢失，
				if (!flag) {
					if (i==0){					
						json.put(1, 0);
						json.put(2, 0);
						json.put(3, time);
						json.put(0, quoteData.getDouble("close"));
					}else{
						json.put(1, jsonArray.getJSONArray(i-1).getDouble(1));
						json.put(2, jsonArray.getJSONArray(i-1).getDouble(2));
						json.put(3, time);
						json.put(0, ((JSONArray)jsonArray.get(i-1)).getDouble(0));
					}	
					json.put(4, 0);
					json.put(5, 0);
					json.put(6, 0);
					json.put(7, 0);
				}
				jsonArray.put(json);
			}
		}
		//Log.i("#########getSecurityType##########", NameRule.getSecurityType(exchange, stockcode)+">>>>>>>>>>>>>>");
//		if(NameRule.getSecurityType(exchange, stockcode)==15 
//				|| NameRule.getSecurityType(exchange, stockcode)==5
//				|| NameRule.getSecurityType(exchange, stockcode)==35){
//			quoteArray = null; 
//			return;
//		}else{
//			quoteArray = jsonArray; 
//		}
		quoteArray = jsonArray; 
		
		actualDataLen = quoteArray.length();
		if(!isTrackStatus)
			isTrackNumber = actualDataLen - 1;//默认显示最后一笔数据
		
		highvolume  = TickUtil.gethighVolume(quoteArray);
		highamount  = TickUtil.gethighAmount(quoteArray);
		high = Math.max(TickUtil.gethighPrice(quoteArray, quoteArray.length()), close);
		low = Math.min(TickUtil.getlowPrice(quoteArray, quoteArray.length()), close);
		if("sz399001".equals(exchange + stockcode)||"sh000001".equals(exchange + stockcode)) {
			//最后一笔数据目前有问题，等服务器改好后在放开，画线的地方也要改
			int len = quoteData.getJSONArray("data2").length()-1;
			high = Math.max(high, TickUtil.gethighPrice(quoteData.getJSONArray("data2"), len));
			low = Math.min(low, TickUtil.getlowPrice(quoteData.getJSONArray("data2"), len));
		}
	}
	
	// 计算最大振幅
	public void calc_zf() { 
		if (close <= 0) close = 1;
		double scales = Utils.getStockDigit(exchange, stockcode);
		if(scales/close < 0.0001){
			max_zd = close * 0.0001 * price_row_num;
		}else{
			max_zd = Utils.getStockDigit(exchange, stockcode) * price_row_num;
		}

		if(high==0 || low==0 || close==0 || high==low){
			max_zf = max_zd / close;
		}else{
			max_zd = Math.max(high - close,close - low);
			max_zf = Math.max(high - close,close - low) / close;
			double zt;
			if(Utils.getStockDigit(exchange, stockcode)==0.01){
				zt = Arith.div(Math.round(close * 1.1 * 100), 100, 2);
			}else{
				zt = Arith.div(Math.round(close * 1.1 * 1000), 1000, 3);
			} 
			if(max_zf < 0.1 && max_zf*1.05 < 0.1){
				max_zf = max_zf * 1.05;
				max_zd = max_zd * 1.05;
			} else if(max_zf < 0.1 && max_zf*1.05 > 0.1){
				max_zf = (zt - close)/close ;
				max_zd = zt - close;
			}
		}
	}
	
	public void touchesBegan(MotionEvent event) {
		isTouched = true;
		isTouchMoved = false;
		startPositionX = event.getX();
		startPositionY = event.getY();
	}

	public void touchesEnded(MotionEvent event) {
		int x = (int)event.getX();
		int y = (int)event.getY();
		//if (x < LSpace || x > (width - LSpace))
		if (x < LSpace || x > width)
			return;
		if (y < this.topTitleHeight || y > height - this.axisLabelHeight)
			return;
		isTouched = false;
		if (isTouchMoved==false) {
			isTouchMoved = false;
			isTrackStatus = !isTrackStatus;
			if(isTrackStatus) {//移动十字架光标
				//if (x > LSpace && x < (width - LSpace))
				if (x > LSpace && x < width)
					trackLineV = x;
				if (y > axisLabelHeight && y < (height - axisLabelHeight))
					trackLineH = y;
				
				int idx = (int) ((x-LSpace) / SPACE);
				if (idx < actualDataLen && idx >=0 )
				{
					isTrackNumber = idx;
					trackLineV = (int) (LSpace + idx * SPACE);
				}else{
					if (idx < 0) {
						isTrackNumber = 0;
						trackLineV = LSpace ;
					}
					if (idx >= actualDataLen){
						isTrackNumber = actualDataLen -1;
						trackLineV = (int) (LSpace + (actualDataLen -1) * SPACE);
					} 
				}
			}
		}
		this.invalidate();
	}

	public void touchesMoved(MotionEvent event) {
		currentPositionX = event.getX();
		currentPositionY = event.getY();
		float deltaX = startPositionX - currentPositionX;
		float deltaY = startPositionY - currentPositionY;
		if(Math.abs(deltaX)<8&&Math.abs(deltaY)<8) {
			isTouchMoved = false;
		}
		else {
			isTouchMoved = true;
		}
		if(isTrackStatus) {//移动十字架光标
			int x = (int)event.getX();
			int y = (int)event.getY();
			float sep = SPACE;
			int idx = (int)((x-LSpace) / sep);
			if (idx < actualDataLen && idx >=0)
			{
				//if (x >= LSpace && x <= (width - LSpace))
				if (x >= LSpace || x <= width)
				{
					trackLineV = (int) (LSpace + idx * SPACE);
				}
				if (y > axisLabelHeight && y < (height - axisLabelHeight))
					trackLineH = y;
				isTrackNumber = idx;
			}else{
				if (idx < 0) {
					idx = 0;
					isTrackNumber = idx;
					trackLineV = LSpace ;
				}
				if (idx >= actualDataLen){
					isTrackNumber = actualDataLen -1;
					trackLineV = (int) (LSpace + (actualDataLen -1) * SPACE);
				} 
			}
		}
		this.invalidate();
	}
	
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		return false;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		return super.dispatchKeyEvent(event);
	}

	public boolean onKeyMultiple(int paramInt1, int paramInt2,
			KeyEvent paramKeyEvent) {
		return false;
	}

	public boolean onKeyUp(int paramInt, KeyEvent paramKeyEvent) {
		return false;
	}

	/**
	 * 返回true可滑动,为false不可滑动
	 */
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	touchesBegan(event);
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	        	touchesMoved(event);
	            break;
	            
	        case MotionEvent.ACTION_UP:
	        	touchesEnded(event);
	            break;
	            
	        case MotionEvent.ACTION_CANCEL:
	            break;
	    }
		return true;
	}

	public void setTouchDelegate(TouchDelegate paramTouchDelegate) {
		super.setTouchDelegate(paramTouchDelegate);
	}
	
	public void reCycle() {
		paint = null;
		System.gc();
	}

	public boolean isTrackStatus() {
		return isTrackStatus;
	}
}
