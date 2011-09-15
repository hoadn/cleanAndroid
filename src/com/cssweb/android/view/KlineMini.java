/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)KlineMini.java 上午11:10:52 2010-11-23
 */
package com.cssweb.android.view;

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

import com.cssweb.android.view.base.BasicView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.GlobalColor;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 只显示主图不显示附图，给自选股、股票买入界面使用
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class KlineMini extends BasicView {
	
	//画笔,一个画线,一个画数据
	private Paint   paint = null, tPaint = null;
	
	private JSONObject quoteData = null;

	public String period = "day";
	
	private int count = 0;
	// 绘图区域的宽度和高度
	private int width, height;
	// K线的上半区域高度和下半区域高度
	private int klineHeight;
	// k线宽度
	private int shapeWidth = 9;
	// 间隔宽度
	private int spaceWidth = 4;
	// 文本距离边框线之间的距离
	private int tips = 10;
	
	private int klineX = 0;
	private int klineY = 0;
	private int rowNum = 3;
	private int rowHeight = 10;
	
	private double axisX = 0;
	
	private double scale = 0;
	
	private int axisLabelWidth = 60;
	private int axisLabelHeight = 20;
	private int klineWidth = 0;
	
	private double highPrice = 0;
	private double lowPrice = 99999.999;
	private double highVolume = 0;
	
	private int actualDataLen = 0; // 取到的总的记录总数

	private int zbmenuHeight = 20;
	
	/**
	 * 当前绘画k线的起始位置.
	 */
	private int actualPos = 0;
	/**
	 * k线可显示的记录数.
	 */
	private int visualKLineCount = 0; // 可视化k线数
	/**
	 * 十字光标当前选中的位置.
	 */
	private int visualPos = 0; 	
	/**
	 * 手指拖动时当前记录起始位置
	 */
	private int fingerPos = 0;
	
	private String indicatorType = "MACD";
	private String mainIndicatorType = "ma";
	
	private String exchange;
	private int stockdigit;
	private boolean zs = false;
	
	private int isTrackNumber = 0;
	private int trackLineV;
	private String lblIndicatorName = "",lblIndicatorT1 = "",lblIndicatorT2 = "",lblIndicatorT3 = "";
	
	public boolean isTrackStatus = false, isTouched = false, isTouchMoved = false, isSingleMoved = false;
	
	/**距离，用来计算两点触控**/
	//private float distanceX0 = 0, distanceY0 = 0, distanceX1 = 0, distanceY1 = 0;
	
	private float startPositionX = 0, currentPositionX = 0, startPositionY = 0, currentPositionY = 0;
	
	public KlineMini(Context context) {
		super(context);
	}

	public KlineMini(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}
	
	/**
	 * 初始化证券信息如市场(sh,sz,kf)
	 * 证券代码、证券名称
	 */
	public void setStockInfo(String exchange, String stockcode, String stockname) {
		this.exchange = exchange;
		int secType = NameRule.getSecurityType(exchange, stockcode);
		if (secType == NameRule.SH_INDEX || secType == NameRule.SZ_INDEX || secType == NameRule.HK_INDEX) {
			zs = true;
		}
		else
			zs = false;
		stockdigit = Utils.getStockDigit(secType);
	}
	
	/**
	 * 初始化指标
	 * @param indicatorType
	 */
	public void setIndicatorType(String indicatorType) {
		this.indicatorType = indicatorType.toUpperCase();
	}

	/**
	 * 初始化指标
	 * @param indicatorType
	 */
	public void setMainIndicatorType(String mainIndicatorType) {
		this.mainIndicatorType = mainIndicatorType.toUpperCase();
	}
	
	public void setPeriod(String period) {
		this.period = period;
	}
	
	private void moveQuote(int idx) throws JSONException {
		if(mainIndicatorType.toLowerCase().equals("ma") && quoteData.getJSONArray("MA").getJSONArray(idx)!=null) {
			lblIndicatorName = "5:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(1), stockdigit);
			lblIndicatorT1 = "10:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(2), stockdigit);
			lblIndicatorT2 = "20:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(3), stockdigit);
			lblIndicatorT3 = "60:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(4), stockdigit);
		}
		else if(mainIndicatorType.toLowerCase().equals("boll") && quoteData.getJSONArray("BOLL").getJSONArray(idx)!=null) {
			lblIndicatorName = "MID:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(1), stockdigit);
			lblIndicatorT1 = "UPPER:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(2), stockdigit);
			lblIndicatorT2 = "LOWER:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(3), stockdigit);
			lblIndicatorT3 = "";
		}
	}
	
	public void initData(JSONObject quoteData) throws JSONException {
		if(!quoteData.isNull("K")) {
			this.quoteData = quoteData;
			this.actualDataLen = quoteData.getJSONArray("K").length();
			if(quoteData.isNull("joTMP")&&actualDataLen>1) {//temp文件取不到的情况下
				Log.i(">>>>>temp文件取不到的情况下并且K线数据取大于1笔的情况>>>>>>", ">>>>>>>>>>>>>>>>>>>>" + quoteData);
				this.actualDataLen = quoteData.getJSONArray("K").length() -1;
				//isTrackNumber = this.actualDataLen - 1;
			}
			else {
				//isTrackNumber = this.actualDataLen - 1;
				makeTodayData();
			}
		}
	}
	
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		if(w>h) {
			int _h = 320;
			if(h<300) 
				_h= 320;
			else if(h>300&&h<460) 
				_h = 480;
	    	float rate = (float) _h/320;
	    	mTextSize = (int)(M_TEXT_SIZE*rate);
	    	dTextSize = (int)(D_TEXT_SIZE*rate);
	    	sTextSize = (int)(S_TEXT_SIZE*rate); 
	    	DX		  = (int)(DX_W*rate);
	    	DY 		  = (int)(DY_H*rate);
			this.width = w;
			this.height = h;
		}
		else { 
	    	float rate = (float) w/320;
	    	mTextSize = (int)(M_TEXT_SIZE*rate);
	    	dTextSize = (int)(D_TEXT_SIZE*rate);
	    	sTextSize = (int)(S_TEXT_SIZE*rate); 
	    	DX		  = (int)(DX_W*rate);
	    	DY 		  = (int)(DY_H*rate);
			this.width = w;
			this.height = h;
		}
    }
	
	public void onDraw(Canvas canvas) {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
		
        tPaint = new Paint();
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setTypeface(Typeface.DEFAULT_BOLD);
		tPaint.setAntiAlias(true);
		tPaint.setTextSize(dTextSize);
		//线和文本之间的间隙
		tips = (int) tPaint.measureText("0");
		
        try {
        	if(actualDataLen==0) {
        		return;
        	}
        	if(zs) 
    			axisLabelWidth = (int) Math.max(tPaint.measureText("99999.99"), tPaint.measureText("11000万"));
        	else
        		axisLabelWidth = (int) tPaint.measureText("11000万");
        	klineWidth = width - axisLabelWidth;
        	//减去spaceWidth确保右边也有一个小空格
    		visualKLineCount = (int) ((klineWidth - spaceWidth) / (spaceWidth + shapeWidth));

            if(isSingleMoved==false&&isTrackStatus==false) {
                if (actualDataLen > visualKLineCount) {
        			actualPos = actualDataLen - visualKLineCount;
        			count = visualKLineCount;
        		} else {
        			actualPos = 0;
        			count = actualDataLen;
        		}
            }
                
            calcMaxMin(actualPos);
            
            if(!isTrackStatus){
    			moveQuote(actualDataLen-1);
    		}else{
    			if(trackLineV==0) {
    				trackLineV = (int) (visualPos * (spaceWidth+shapeWidth) - shapeWidth/2);
    				isTrackNumber = actualPos + visualPos - 1;
    			}    			
    			if(isTrackNumber<0) {
    				isTrackNumber=0;
    			}
    			else if(isTrackNumber>actualDataLen-1) {
    				isTrackNumber=actualDataLen-1;
    			}
    			paint.setColor(GlobalColor.clrGrayLine);
    			canvas.drawLine(klineX + trackLineV, axisLabelHeight, klineX + trackLineV, height - axisLabelHeight, paint);
    			moveQuote(isTrackNumber);
    			drawQuoteWin(canvas, quoteData, isTrackNumber);
    		}
    		
            //文字的高度
            axisLabelHeight = Font.getFontHeight(dTextSize);
            klineX = axisLabelWidth;
            klineY = axisLabelHeight;

			klineHeight = height-axisLabelHeight*2;
			axisX = klineX;
			
            //画指标值
			tPaint.setTextAlign(Paint.Align.LEFT);
            tPaint.setColor(GlobalColor.colorM5);
			canvas.drawText(lblIndicatorName,  (float) (klineX + shapeWidth), axisLabelHeight - 5, tPaint);

			float size = tPaint.measureText(lblIndicatorName) + tips;
			tPaint.setColor(GlobalColor.colorM10);
			canvas.drawText(lblIndicatorT1,  (float) (klineX + shapeWidth + size), axisLabelHeight - 5, tPaint);

			size += tPaint.measureText(lblIndicatorT1) + tips;
			if(size<=(klineWidth-tPaint.measureText(lblIndicatorT2))) {
				//tPaint.setColor(GlobalColor.colorM20);
				tPaint.setARGB(255, 255, 0, 255);
				canvas.drawText(lblIndicatorT2,  (float) (klineX + shapeWidth + size), axisLabelHeight - 5, tPaint);
			}

			size += tPaint.measureText(lblIndicatorT2) + tips;
			if(size<=(klineWidth-tPaint.measureText(lblIndicatorT3))) {
				tPaint.setColor(GlobalColor.colorM60);
				canvas.drawText(lblIndicatorT3,  (float) (klineX + shapeWidth + size), axisLabelHeight - 5, tPaint);
			}
			
			//绘制边框线和左边的价格以及成交量
			rowHeight = klineHeight / rowNum;
    		scale = klineHeight / (highPrice - lowPrice);
    		
    		double ratio = (highPrice - lowPrice) / rowNum;
    		paint.setColor(GlobalColor.clrLine);
			tPaint.setColor(GlobalColor.colorTicklabel);
			tPaint.setTextAlign(Paint.Align.RIGHT);
    		for (int i = 0; i <= rowNum; i++) {
    			if (i == rowNum || i == 0) {
    				canvas.drawLine(klineX, klineY + rowHeight * i, width, klineY + rowHeight * i, paint);
    			}
    			else {
    				Graphics.drawDashline(canvas, klineX, klineY + rowHeight * i, width, klineY + rowHeight * i, paint);
    			}
    			if (i != rowNum && isTrackStatus == false) {
    				double AxisLabelPrice = highPrice - ratio * i;
    				String AxisLabelPriceText;
    				if (zs) {
    					AxisLabelPriceText = Utils.dataFormation(Math.round(AxisLabelPrice), 0);
    				}
    				else {
    					AxisLabelPriceText = Utils.dataFormation(AxisLabelPrice, stockdigit);
    				}
    				canvas.drawText(AxisLabelPriceText, klineX - tips/4, klineY + rowHeight * i + axisLabelHeight/2, tPaint);
    			}
    		}
    		
    		
    		//画线
    		if (quoteData != null) {
    			//axisX = 0;
    			for (int i = actualPos; i < (actualPos + count); i++) {
    				if(i==0)
    					drawKLine(canvas, i - actualPos, 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(1),
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(2), 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(3), 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(4), 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(4));
    				else
    					drawKLine(canvas, i - actualPos, 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(1),
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(2), 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(3), 
    							quoteData.getJSONArray("K").getJSONArray(i).getDouble(4), 
    							quoteData.getJSONArray("K").getJSONArray(i-1).getDouble(4));
    			}
    			if(mainIndicatorType.toUpperCase().equals("MA"))
    				drawMA(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			

    			drawTimeAix(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    		}
    		
    		//绘制外边框线分别是上、左、右、中、下边框线
    		paint.setColor(GlobalColor.clrLine);
    		canvas.drawLine(klineX, 0, width, 0, paint);
    		canvas.drawLine(klineX, 0, klineX, height - axisLabelHeight, paint);
    		canvas.drawLine(width, 0, width, height - axisLabelHeight, paint);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public void drawQuoteWin(Canvas canvas, JSONObject quoteData, int idx) throws JSONException {
		Paint mPaint = new Paint();
		mPaint.setTextAlign(Paint.Align.LEFT);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setTypeface(Typeface.DEFAULT_BOLD);
		mPaint.setAntiAlias(true);
		
		mPaint.setColor(GlobalColor.colorKlinePopub);
		mPaint.setTextSize(sTextSize);
		canvas.drawText("开:", 0, axisLabelHeight, mPaint);
		canvas.drawText("高:", 0, axisLabelHeight * 3, mPaint);
		canvas.drawText("低:", 0, axisLabelHeight * 5, mPaint);
		canvas.drawText("收:", 0, axisLabelHeight * 7, mPaint);
		canvas.drawText("涨跌:", 0, axisLabelHeight * 9, mPaint);
		canvas.drawText("日期:", 0, axisLabelHeight * 11, mPaint);

		mPaint.setTextAlign(Paint.Align.RIGHT);
		String qt = quoteData.getJSONArray("K").getJSONArray(idx).getString(0);
		double jrkp = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(1);
		double zg   = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(2);
		double zd   = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(3);
		double sp   = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(4);
		double preclose;
		if("cf".equals(exchange)||"dc".equals(exchange)||"sf".equals(exchange)||"cz".equals(exchange)) {
			switch(idx) {
				case 0:
					preclose = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(7);
					break;
				default:
					preclose = quoteData.getJSONArray("K").getJSONArray(idx-1).getDouble(7);
					break;
			}
			if(quoteData.getJSONArray("K").length()==1){
				preclose = quoteData.getJSONArray("K").getJSONArray(0).getDouble(7);
			}
		}
		else {
			switch(idx) {
				case 0:
					preclose = quoteData.getJSONArray("K").getJSONArray(idx).getDouble(4);
					break;
				default:
					preclose = quoteData.getJSONArray("K").getJSONArray(idx-1).getDouble(4);
					break;
			}
			if(quoteData.getJSONArray("K").length()==1){
				preclose = quoteData.getDouble("zrsp");//quoteData.getJSONArray("K").getJSONArray(0).getDouble(4);
			}
		}
//		if(quoteData.getJSONArray("K").length()==1 && quoteData.getJSONObject("todayData")!=null){
//			preclose = quoteData.getJSONObject("todayData").getDouble("zrsp");
//		}
		mPaint.setColor(getcolor(jrkp, preclose));
		canvas.drawText(Utils.dataFormation(jrkp, stockdigit), klineX, axisLabelHeight * 2, mPaint);
		mPaint.setColor(getcolor(zg, preclose));
		canvas.drawText(Utils.dataFormation(zg, stockdigit), klineX, axisLabelHeight * 4, mPaint);
		mPaint.setColor(getcolor(zd, preclose));
		canvas.drawText(Utils.dataFormation(zd, stockdigit), klineX, axisLabelHeight * 6, mPaint);
		mPaint.setColor(getcolor(sp, preclose));
		canvas.drawText(Utils.dataFormation(sp, stockdigit), klineX, axisLabelHeight * 8, mPaint);
		double zhangdie = sp-preclose;
		if(zhangdie>0) 
			mPaint.setColor(GlobalColor.colorpriceUp);
		else if(zhangdie<0) 
			mPaint.setColor(GlobalColor.colorPriceDown);
		else 
			mPaint.setColor(GlobalColor.colorPriceEqual);
		canvas.drawText(Utils.dataFormation(zhangdie, 1), klineX, axisLabelHeight * 10, mPaint);
		if(quoteData.getString("period").equals("min5") || 
				quoteData.getString("period").equals("min15") ||
				quoteData.getString("period").equals("min30") ||
				quoteData.getString("period").equals("min60")){
			qt = qt.substring(4,6)+'/'+qt.substring(6,8)+' '+qt.substring(8);
		}else{
			qt = qt.substring(2,4)+qt.substring(4,6)+qt.substring(6,8);
		}
		mPaint.setColor(GlobalColor.colorLabelName);
		canvas.drawText(qt, klineX, axisLabelHeight * 12, mPaint);
	}
	
	
	/*
	 * 绘画k线
	 */
	private void drawKLine(Canvas canvas, int i, double curropen, double currhigh, double currlow, double jrsp, double zrsp) {
		double y1 = (currhigh - lowPrice) * scale;//最高价
		double y2 = (currlow - lowPrice) * scale;//最低价
		double y3 = (curropen - lowPrice) * scale;//开盘价
		double y4 = (jrsp - lowPrice) * scale;//收盘价
		if (i == 0)
			axisX = axisX + spaceWidth;
		else
			axisX = axisX + spaceWidth + shapeWidth;
		
		// 今日收盘价 小于 今日开盘价 ,阴线
		if (jrsp < curropen) {
			//paint.setColor(GlobalColor.colorKdown); 
			paint.setARGB(255, 84, 255, 255);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y1, 
					axisX + shapeWidth/2, klineY + klineHeight - y3, paint);			
			if(y4-y3<0) {//1.6版本下面的方法不支持
				Graphics.fillRect(canvas, axisX, klineY + klineHeight - y3, shapeWidth, y3-y4, paint);
			}
			else 
				Graphics.fillRect(canvas, axisX, klineY + klineHeight - y4, shapeWidth, y4-y3, paint);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y4, 
					axisX + shapeWidth/2, klineY + klineHeight - y2, paint);
		}
		// 今日收盘价 等于 今日开盘价 
		if (jrsp == curropen) {
			paint.setColor(GlobalColor.colorPriceEqual); 
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y1,
					axisX + shapeWidth/2, klineY + klineHeight - y4, paint);
			Graphics.drawLine(canvas, axisX, klineY + klineHeight - y4, 
					axisX + shapeWidth, klineY + klineHeight - y4, paint);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y3, 
					axisX + shapeWidth/2, klineY + klineHeight - y2, paint);
		}
		// 今日收盘价 大于 今日开盘价 
		if (jrsp > curropen) {
			paint.setColor(GlobalColor.colorpriceUp); 
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y1, 
					axisX + shapeWidth/2, klineY + klineHeight - y4, paint);
			Graphics.drawRect(canvas, axisX, klineY + klineHeight - y4, shapeWidth, y4-y3, paint);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y3, 
					axisX + shapeWidth/2, klineY + klineHeight - y2, paint);
		}
	}
	
	//MA
	private void drawMA(Canvas canvas, JSONObject quoteData, int begin, int count, double shapeWidth, 
			double spaceWidth, double highPrice, double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData == null)
			return;
		
		int startX = klineX;
		int startY = klineY;
		double ma5Height = 0;
		double ma5x = 0;
		double ma5y = 0;
		double ma5 = 0;
		
		paint.setColor(GlobalColor.colorM5);
		for (int i=begin; i<(begin+count); i++)
		{
			//klineX = 0;
	
			ma5 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(1);
			
			if ((i-begin)==0)
			{
				ma5x = startX + spaceWidth + shapeWidth/2;
				ma5Height = (int)((ma5 - lowPrice) * scale);
				ma5y = axisLabelHeight + klineHeight - ma5Height;
			}
			else
			{
			    if (quoteData.getJSONArray("MA").getJSONArray(i-1).getDouble(1) == 0)
				{
					ma5x = ma5x + spaceWidth + shapeWidth;
					ma5Height = (ma5 - lowPrice) * scale;
					ma5y = startY + klineHeight - ma5Height;
				}
				else
				{
					canvas.drawLine((float)ma5x, (float)ma5y, (float)(ma5x + spaceWidth + shapeWidth), 
							(float) (axisLabelHeight + klineHeight - (ma5 - lowPrice) * scale), paint);
					ma5x = ma5x  + spaceWidth + shapeWidth;
					ma5Height = (ma5 - lowPrice) * scale;
					ma5y = startY + klineHeight - ma5Height;
				}
			}
	
		} // end for			 	
		
		double ma10x = 0;
		double ma10y = 0;
		double ma10Height = 0;
		double ma10 = 0;
	
		paint.setColor(GlobalColor.colorM10);
		
		for (int i=begin; i<(begin+count); i++)
		{
			//klineX = 0;
	
			ma10 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(2);
			
			if ((i-begin)==0)
			{
				ma10x = startX + spaceWidth + shapeWidth/2;
				ma10Height = (ma10 - lowPrice) * scale;
				ma10y = startY + klineHeight - ma10Height;
			}
			else
			{
					if (quoteData.getJSONArray("MA").getJSONArray(i-1).getDouble(2) == 0)
					{
						ma10x = ma10x  + spaceWidth + shapeWidth;
						ma10Height = (ma10 - lowPrice) * scale;
						ma10y = startY + klineHeight - ma10Height;
					}
					else
					{ 
						canvas.drawLine((float)ma10x, (float)ma10y, (float)(ma10x + spaceWidth + shapeWidth), 
								(float) (axisLabelHeight + klineHeight - (ma10 - lowPrice) * scale), paint);
						ma10x = ma10x  + spaceWidth + shapeWidth;
						ma10Height = (ma10 - lowPrice) * scale;
						ma10y = startY + klineHeight - ma10Height;
					}
			}
		} // end for		 	
					
		double ma20x = 0;
		double ma20y = 0;
		double ma20Height = 0;
		double ma20 = 0;
					
		//paint.setColor(GlobalColor.colorM20);
		paint.setARGB(255, 255, 0, 255);
		for (int i=begin; i<(begin+count); i++)
		{
			//klineX = 0;
	
			ma20 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(3);
			
			if ((i-begin)==0)
			{
				ma20x = startX + spaceWidth + shapeWidth/2;
				ma20Height = (ma20 - lowPrice) * scale;
				ma20y = startY + klineHeight - ma20Height;
			}
			else
			{
					if (quoteData.getJSONArray("MA").getJSONArray(i-1).getDouble(3) == 0)
					{
						ma20x = ma20x  + spaceWidth + shapeWidth;
						ma20Height = (ma20 - lowPrice) * scale;
						ma20y = startY + klineHeight - ma20Height;
					}
					else
					{ 
						canvas.drawLine((float)ma20x, (float)ma20y, (float)(ma20x + spaceWidth + shapeWidth), 
								(float) (axisLabelHeight + klineHeight - (ma20 - lowPrice) * scale), paint);
						ma20x = ma20x  + spaceWidth + shapeWidth;
						ma20Height = (ma20 - lowPrice) * scale;
						ma20y = startY + klineHeight - ma20Height;
					}
			}	
		} // end for			 								
	
		double ma60x = 0;
		double ma60y = 0;
		double ma60Height = 0;
		double ma60 = 0;
	
		paint.setColor(GlobalColor.colorM60);
		for (int i=begin; i<(begin+count); i++)
		{
			//klineX = 0;
	
			ma60 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(4);
			
			if ((i-begin)==0)
			{
				ma60x = startX + spaceWidth + shapeWidth/2;
				ma60Height = (ma60 - lowPrice) * scale;
				ma60y = startY + klineHeight - ma60Height;
			}
			else
			{
					if (quoteData.getJSONArray("MA").getJSONArray(i-1).getDouble(4) == 0)
					{
						ma60x = ma60x  + spaceWidth + shapeWidth;
						ma60Height = (ma60 - lowPrice) * scale;
						ma60y = startY + klineHeight - ma60Height;
					}
					else
					{ 
						canvas.drawLine((float)ma60x, (float)ma60y, (float)(ma60x + spaceWidth + shapeWidth), 
								(float) (axisLabelHeight + klineHeight - (ma60 - lowPrice) * scale), paint);
						ma60x = ma60x  + spaceWidth + shapeWidth;
						ma60Height = (ma60 - lowPrice) * scale;
						ma60y = startY + klineHeight - ma60Height;
					}
			}	
		} // end for			 
	}
	
	//画时间轴
	private void drawTimeAix(Canvas canvas, JSONObject quoteData, int begin,
			int count, double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen)
			throws JSONException {
		String quoteTime = "";
		double apX = klineX;

		int n = 0;
		tPaint.setColor(GlobalColor.colorLabelName);
		tPaint.setTextAlign(Paint.Align.LEFT);
		for (int i = begin; i < (begin + count); i++, n++) {
			quoteTime = quoteData.getJSONArray("K").getJSONArray(i)
					.getString(0);
			if (i == begin) {// 第一个坐标
				canvas.drawText(quoteTime, (float) apX, height
						- axisLabelHeight/4, tPaint);
			} else { // 最后一个坐标
				if (i == begin + count - 1)
					canvas.drawText(quoteTime,
							(float) (width - tPaint.measureText(quoteTime)),
							height - axisLabelHeight/4, tPaint);
			}
		}
	}

	/**
	 * 计算最大最小值
	 * @throws Exception
	 */
	private void calcMaxMin(int begin) throws JSONException {

		highPrice = 0.004;
		lowPrice = 99999999; // 要考虑指数的情况
		highVolume = 0;
		for (int i = begin; i < begin + count; i++) {
			// 跟 日k线数据比较大小
			if(quoteData.getJSONArray("K").getJSONArray(i)==null) continue;
			
			double tempHigh = quoteData.getJSONArray("K").getJSONArray(i).getDouble(2);
			double tempLow = quoteData.getJSONArray("K").getJSONArray(i).getDouble(3);
			highPrice = Math.max(highPrice,tempHigh);
			lowPrice = Math.max(Math.min(lowPrice,tempLow),0);

			// 跟 ma 线比较
			if (mainIndicatorType.toLowerCase().equals("ma")) {
				double ma5 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(1);
				double ma10 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(2);
				double ma20 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(3);
				double ma60 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(4);
				highPrice = Arith.max(highPrice,ma5,ma10,ma20,ma60);
				if(lowPrice==0) lowPrice = ma5;
				if(ma5>0) lowPrice = Math.max(Math.min(lowPrice,ma5),0);	
				if(ma10>0) lowPrice = Math.max(Math.min(lowPrice,ma10),0);	
				if(ma20>0) lowPrice = Math.max(Math.min(lowPrice,ma20),0);	
				if(ma60>0) lowPrice = Math.max(Math.min(lowPrice,ma60),0);	

			}
			// 跟boll线比较
			if (mainIndicatorType.toLowerCase().equals("boll") && quoteData.getJSONArray("BOLL").getJSONArray(i)!=null) {
				double upper = quoteData.getJSONArray("BOLL").getJSONArray(i).getDouble(2);
				double lower = quoteData.getJSONArray("BOLL").getJSONArray(i).getDouble(3);
				highPrice = Math.max(highPrice,upper);
				if(lower>0) lowPrice = Math.max(Math.min(lowPrice,lower),0);
			}
			// 跟成交量线比较
//			double tempVol = quoteData.getJSONArray("K").getJSONArray(i).getDouble(5);
//			double mavol5  =  quoteData.getJSONArray("MA").getJSONArray(i).getDouble(5);
//			double mavol10 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(6);
//			highVolume = Arith.max(highVolume,tempVol,mavol5,mavol10);

			// 跟成交量线比较
			double tempVol = quoteData.getJSONArray("K").getJSONArray(i).getDouble(5);
			if (tempVol > highVolume)
				highVolume = tempVol;
		}
		double tem = highPrice-lowPrice;
		highPrice = highPrice + tem*0.05;
		lowPrice = lowPrice - tem*0.05; //使得k线和 成交量图留点间隙，用于显示 权息资料
	}
	
	/**
	 * 处理今天的数据
	 * @throws JSONException 
	 */
	private void makeTodayData() throws JSONException{ 
		if(quoteData.isNull("joTMP")) {//temp文件取不到的情况下
			Log.i(">>>>>temp文件取不到的情况下>>>>>>", ">>>>>>>>>>>>>>>>>>>>" + quoteData);
			newStockhandler();
			return;
		}
		if(!quoteData.getBoolean("tradeFlag")){
			//停牌、已经收盘直接取文件不走下面的方法
			//另外一种就是集合竞价收盘价为0暂时未处理，等服务器那边通知
			return;
		}
		JSONObject tempvalue = quoteData.getJSONObject("joTMP");
		int l = quoteData.getJSONArray("K").length();
		
		String qt = quoteData.getJSONArray("K").getJSONArray(l-1).getString(0);
		double zjcj = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(4);
		double cjsl = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(5);
		
		double summa4 = tempvalue.getJSONObject("ma").getDouble("sumMa4");
		double summa9 = tempvalue.getJSONObject("ma").getDouble("sumMa9");
		double summa19 = tempvalue.getJSONObject("ma").getDouble("sumMa19");
		double summa59 = tempvalue.getJSONObject("ma").getDouble("sumMa59");
		double sumvolma4 = tempvalue.getJSONObject("ma").getDouble("sumMavol4");
		double sumvolma9 = tempvalue.getJSONObject("ma").getDouble("sumMavol9");
		
		//判断是否已经收盘,如果收盘了,则不能再用tmp文件计算当日指标值. 0403更新,非常重要
		int sp =  quoteData.getInt("sp");
		if(sp==1) {
			return;
		}
		
		if (mainIndicatorType.toUpperCase().equals("MA") || mainIndicatorType.toUpperCase().equals("BOLL"))
		{
			quoteData.getJSONArray("MA").put(new JSONArray());
			quoteData.getJSONArray("MA").getJSONArray(l-1).put(0, qt);
			double ma5 =0;
			if(l>4) ma5 = (summa4 + zjcj)/5;
			quoteData.getJSONArray("MA").getJSONArray(l-1).put(1, ma5);
			double ma10 =0;
			if(l>9) ma10 = (summa9 + zjcj)/10;
			quoteData.getJSONArray("MA").getJSONArray(l-1).put(2, ma10);
			double ma20 =0;
			if(l>19) ma20 = (summa19 + zjcj)/20;
			quoteData.getJSONArray("MA").getJSONArray(l-1).put(3, ma20);
			double ma60 =0;
			if(l>59) ma60 = (summa59 + zjcj)/60;
			quoteData.getJSONArray("MA").getJSONArray(l-1).put(4, ma60);
		}
		double mavol5 = (sumvolma4 + cjsl)/5;
		quoteData.getJSONArray("MA").getJSONArray(l-1).put(5, mavol5); 
		
		double mavol10 = (sumvolma9 + cjsl)/10;
		quoteData.getJSONArray("MA").getJSONArray(l-1).put(6, mavol10);  

		//CssLog.i("=======maaaaaaa========", quoteData.getJSONArray("MA")+">>>>>>>>>>>>>>>>>");
		if (mainIndicatorType.equals("BOLL"))				
		{
			quoteData.getJSONArray("BOLL").put(new JSONArray());
			double mid = 0;
			double upper = 0;
			double lower = 0;
			if(l>25){
				double sumClose = tempvalue.getJSONObject("boll").getDouble("sumClose");
				double sumPowClose = tempvalue.getJSONObject("boll").getDouble("sumPowClose");
				double maPow = 0;
				double temp;
				if(l>25){ 
					mid = (sumClose + zjcj) / 26;
					maPow = (sumPowClose + zjcj* zjcj) / 26;
					temp = (maPow - mid * mid);
					if (temp < 0)
						temp = 0;
					upper = mid + 2 * Math.sqrt((temp * 26) / (26 - 1));
					lower = mid - 2 * Math.sqrt((temp * 26) / (26 - 1));
				}
			}
			 
			quoteData.getJSONArray("BOLL").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("BOLL").getJSONArray(l-1).put(1, mid);
			quoteData.getJSONArray("BOLL").getJSONArray(l-1).put(2, upper);
			quoteData.getJSONArray("BOLL").getJSONArray(l-1).put(3, lower);
		}
		
		if (indicatorType.equals("MACD"))
		{
			quoteData.getJSONArray("MACD").put(new JSONArray());
			double prevemashort = 0;
			double prevemalong = 0;
			double prevdea = 0;
			double dif = 0;
			double dea = 0;
			double macd = 0;
			double emashort = 0;
			double emalong = 0;

			prevemashort = tempvalue.getJSONObject("macd").getDouble("emaShort");
			prevemalong = tempvalue.getJSONObject("macd").getDouble("emaLong");
			prevdea = tempvalue.getJSONObject("macd").getDouble("dea");

			if(l>1){
				emashort = (2*zjcj + (12-1)*prevemashort) / (12+1);
				emalong = (2*zjcj + (26-1)*prevemalong) / (26+1);
				dif = emashort - emalong;
				dea = (2*dif + (9-1)*prevdea) / (9+1);
				macd = (dif - dea)*2;
			}
				
			quoteData.getJSONArray("MACD").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("MACD").getJSONArray(l-1).put(1, dif);
			quoteData.getJSONArray("MACD").getJSONArray(l-1).put(2, dea);
			quoteData.getJSONArray("MACD").getJSONArray(l-1).put(3, macd);
		}
	}
	
	private void newStockhandler() throws JSONException {
		if(!quoteData.getBoolean("tradeFlag")){
			//停牌、已经收盘直接取文件不走下面的方法
			//另外一种就是集合竞价收盘价为0暂时未处理，等服务器那边通知
			return;
		}
		JSONArray jMA = new JSONArray();
		jMA.put(0, 0);
		jMA.put(1, 0);
		jMA.put(2, 0);
		jMA.put(3, 0);
		jMA.put(4, 0);
		jMA.put(5, 0);
		jMA.put(6, 0);
		quoteData.put(mainIndicatorType.toUpperCase(), new JSONArray().put(jMA));
		
		JSONArray jIn = new JSONArray();
		jIn.put(0, 0);
		jIn.put(1, 0);
		jIn.put(2, 0);
		jIn.put(3, 0);
		quoteData.put(indicatorType.toUpperCase(), new JSONArray().put(jIn));
		
		actualDataLen = quoteData.getJSONArray("data").length();
		if (actualDataLen < visualKLineCount){
			count = actualDataLen;
		}else{
			count = visualKLineCount;
		}
		if (this.actualDataLen - this.actualPos -1 <= this.visualKLineCount)
		{
			actualPos = actualDataLen - count; 
		}
	}
	
	
	public void touchesBegan(MotionEvent event) {
		isTouched = true;
		isTouchMoved = false;
		startPositionX = event.getX();
		startPositionY = event.getY();
		//Log.i("========startPositionX==========", startPositionX+">>>>>>>>>>");
		//Log.i("========startPositionY==========", startPositionY+">>>>>>>>>>");
	}

	public void touchesEnded(MotionEvent event) {
		isTouched = false;
		if (isTouchMoved==false) {
			isTouchMoved = false;
			isTrackStatus = !isTrackStatus;
			if(isTrackStatus) {//移动十字架光标
				int mouseX = (int)event.getX();
				int mouseY = (int)event.getY();
				if (mouseX <= klineX
						|| mouseY <=axisLabelHeight
						|| mouseY >= height - axisLabelHeight - zbmenuHeight)
					return; 
				double sep = (shapeWidth + spaceWidth);
				visualPos = (int) ((mouseX - klineX)/ sep + 1);
				if (visualPos >= this.actualDataLen - actualPos) {
					visualPos = this.actualDataLen - actualPos;
					isTrackNumber = actualDataLen-1;
				}
				else {
					isTrackNumber = actualPos + visualPos - 1;
				}
				trackLineV = (int) (visualPos * (spaceWidth+shapeWidth) - shapeWidth/2);
				Log.i("========isTouched222==========", trackLineV+">>>>>"+visualKLineCount+">>>>>" + isTrackNumber);
				Log.i("========isTouchMoved==========", actualDataLen+">>>>"+actualPos+">>>>>>" + visualPos);
			}
			else {
				isSingleMoved = true;
			}
		}
		else {
			
		}
		this.invalidate();
		//Log.i("========isTouched==========", isTouched+">>>>>>>>>>");
		//Log.i("========isTouchMoved==========", isTouchMoved+">>>>>>>>>>");
		//Log.i("========isTrackStatus==========", isTrackStatus+">>>>>>>>>>");
	}

	public void touchesMoved(MotionEvent event) {
		//Log.i("========currentPositionX==========", currentPositionX+">>>>>>>>>>");
		//Log.i("========currentPositionY==========", currentPositionY+">>>>>>>>>>");
		currentPositionX = event.getX();
		currentPositionY = event.getY();
		float deltaX = startPositionX - currentPositionX;
		float deltaY = startPositionY - currentPositionY;
		//Log.i("========Math.abs(delta)==========", Math.abs(deltaX)+">>>>>>>>>>"+Math.abs(deltaY));
		if(Math.abs(deltaX)<8&&Math.abs(deltaY)<8) {
			isTouchMoved = false;
		}
		else {
			isTouchMoved = true;
		}
		if (quoteData==null) return;
		int mouseX = (int)event.getX();
		int mouseY = (int)event.getY();
		if (mouseX <= klineX
				|| mouseY <=axisLabelHeight
				|| mouseY >= height - axisLabelHeight - zbmenuHeight)
			return; 

		if(isTrackStatus) {//移动十字架光标
			isSingleMoved = false;//单手指拖动不可用
			double sep = (shapeWidth + spaceWidth);
			visualPos = (int) ((mouseX - klineX)/ sep + 1);
			if (visualPos >= this.actualDataLen - actualPos) {
				visualPos = this.actualDataLen - actualPos;
			}
			else if(visualPos<1)
				visualPos = 1;
			int idx = actualPos + visualPos - 1;

			if (mouseX > klineX)
			{
				trackLineV= (int) (visualPos * (spaceWidth+shapeWidth) - shapeWidth/2);
			}
				
			if (idx < actualDataLen)
			{
				isTrackNumber = idx;
			}
			else {
				isTrackNumber = this.actualDataLen-1;
			}
			Log.i("@@@@@@@@@@isTrackNumber@@@@@@@@@@", isTrackNumber+">>>>>>>>>");
			Log.i("@@@@@@@@@@idx@@@@@@@@@@", idx+">>>>>>>>>");
			Log.i("@@@@@@@@@@actualPos@@@@@@@@@@", actualPos+">>>>>>>>>");
			Log.i("@@@@@@@@@@visualPos@@@@@@@@@@", visualPos+">>>>>>>>>");
			this.invalidate();
		}
		else {//移动K线显示数据
			isSingleMoved = true;
			if(deltaX<-50&&deltaY<10) {
				moveRight(1);
			}
			else if(deltaX>50&&deltaY<10) {
				moveLeft(1);
			}
		}
	}
	
	public boolean onKeyDown(int paramInt, KeyEvent paramKeyEvent) {
		return false;
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
	public boolean onTouchEvent(MotionEvent motionEvent) {
//		int count = motionEvent.getPointerCount();
//		if(count==2) {//两点触控
//			isTrackStatus = false;
//			if(motionEvent.getAction()==MotionEvent.ACTION_POINTER_1_DOWN||motionEvent.getAction()==MotionEvent.ACTION_POINTER_2_DOWN) {
//				float x0 = motionEvent.getX(0);
//				float x1 = motionEvent.getX(1);
//				float y0 = motionEvent.getY(0);
//				float y1 = motionEvent.getY(1);
//				distanceY0 = Math.abs(y1-y0);
//				distanceX0 = Math.abs(x1-x0);
//			}
//			if(motionEvent.getAction()==MotionEvent.ACTION_POINTER_1_UP||motionEvent.getAction()==MotionEvent.ACTION_POINTER_2_UP) {
//				float x0 = motionEvent.getX(0);
//				float x1 = motionEvent.getX(1);
//				float y0 = motionEvent.getY(0);
//				float y1 = motionEvent.getY(1);
//				distanceY1 = Math.abs(y1-y0);
//				distanceX1 = Math.abs(x1-x0);
//				if(distanceY1>distanceY0&&distanceX1>distanceX0) {
//					upHandler();
//				}
//				else if(distanceY1<distanceY0&&distanceX1<distanceX0) {
//					downHandler();
//				}
//			}
//			return false;
//		}
//		else if(count==1) {//单点触控分移动和滑动两种情况
//			switch (motionEvent.getAction()) {
//		        case MotionEvent.ACTION_DOWN:
//		        	touchesBegan(motionEvent);
//		            break;
//		            
//		        case MotionEvent.ACTION_MOVE:
//		        	touchesMoved(motionEvent);
//		            break;
//		            
//		        case MotionEvent.ACTION_UP:
//		        	touchesEnded(motionEvent);
//		            break;
//		        
//		    }
//		}
		//为了兼容1.6系统，故把上面的多点触控去掉了
		switch (motionEvent.getAction()) {
	        case MotionEvent.ACTION_DOWN:
	        	touchesBegan(motionEvent);
	            break;
	            
	        case MotionEvent.ACTION_MOVE:
	        	touchesMoved(motionEvent);
	            break;
	            
	        case MotionEvent.ACTION_UP:
	        	touchesEnded(motionEvent);
	            break;
	        
	    }
		return true;
	}

    public void setVisualKLineCount(int visualKLineCount) {
		this.visualKLineCount = visualKLineCount;
	}
    
	public void upHandler()
	{
		if (spaceWidth >= MAX_SPACE_WIDTH)
			return;
		
		if (shapeWidth >= MAX_SHAPE_WIDTH)
			return;
		
		spaceWidth = spaceWidth *3/2 ;
		shapeWidth = shapeWidth *3/2 ;
		
		visualKLineCount = (int) (klineWidth / (spaceWidth + shapeWidth));
		
		if (visualKLineCount >= actualDataLen)
		{
			// 可显示的数据量 大于 实际数据量
			actualPos = 0;
			count = actualDataLen;
		}
		else
		{
			//可显示的数据量 小于 本地现在的数据量
			actualPos = actualDataLen -  visualKLineCount;
			count = visualKLineCount;
		}
		this.invalidate();
	}
	
	public void downHandler()
	{
		// 已经是最小了
		if (shapeWidth <= MIN_SHAPE_WIDTH)
			return;
		
		if (spaceWidth <= MIN_SPACE_WIDTH)
			return;
		
		spaceWidth = spaceWidth * 2/3;
		if (spaceWidth < 1)
			spaceWidth = 1;
			
		shapeWidth = shapeWidth * 2/3;
		if (shapeWidth <= 1)
			shapeWidth = 1;
		
		int tempCount = (int) (klineWidth / (spaceWidth + shapeWidth));
		
		if (tempCount > actualDataLen)
		{
			// 可以显示的数据量大于 现有的数据长度，则程序走到这里  向后台请求数据。 
			visualKLineCount = tempCount;
		}
		else 
		{
			// 可以显示的数据量小于或等于 现有的数据长度，
			visualKLineCount = tempCount;
			actualPos = actualDataLen -  visualKLineCount;
			count = visualKLineCount;
		}
		
		this.invalidate();
	}	
	
	public void moveRight(int nums) {
		fingerPos -= nums;
		actualPos = actualDataLen - visualKLineCount + fingerPos;
		if(actualPos<=0) {
			fingerPos = 0;
			actualPos = 0;
			count = actualDataLen;
		}
		else {
			count = visualKLineCount;
		}
		Log.i(">>>>>>>rrrrrrrrr>>>>>>>", count+">>>>>>>>>>>"+actualPos);
		this.invalidate();
	}
	
	public void moveLeft(int nums) {
		fingerPos += nums;
		actualPos = actualDataLen - visualKLineCount + fingerPos;
		Log.i(">>>>>>>llllllllll>>>>>>>", (actualDataLen - visualKLineCount)+">>>>>>>>>>>"+actualPos);
		if(actualPos<=0) {
			fingerPos = 0;
			actualPos = 0;
			count = actualDataLen;
		}
		else if(actualPos>=actualDataLen - visualKLineCount) {
			fingerPos = 0;
			actualPos = actualDataLen - visualKLineCount;
			count = visualKLineCount;
		}
		else {
			count = visualKLineCount;
		}
		Log.i(">>>>>>>llllllllll>>>>>>>", count+">>>>>>>>>>>"+actualPos);
		this.invalidate();
	}
	
	public void resetStatus() {
		isTrackStatus = false; 
		isTouched = false;
		isTouchMoved = false;
		isSingleMoved = false;
		
		//切换数据的时候重新下面参数
		isTrackNumber = this.actualDataLen - 1;
		
		//避免切换指标或者周期的时候造成拉动的时候还是继续上次的位置
		fingerPos = 0;
	}
	
	private int getcolor(double n1,double zrsp)
	{
		if (n1 > zrsp)
		{
			return GlobalColor.colorpriceUp;
		}
		if (n1 < zrsp)
		{
			return GlobalColor.colorPriceDown;
		}
		if (n1 == zrsp)
		{
			return GlobalColor.colorPriceEqual;
		}
		return GlobalColor.colorPriceEqual;
	}
	
	public void reCycle() {
		paint = null;
		System.gc();
	}
	
	public void refresh() {
		this.invalidate();
	}
	
	public boolean isTrackStatus() {
		return isTrackStatus;
	}
}
