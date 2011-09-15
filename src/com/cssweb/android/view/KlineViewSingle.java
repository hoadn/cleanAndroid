/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)KlineViewSingle.java 下午12:19:48 2011-5-20
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
import android.widget.Toast;

import com.cssweb.android.common.DateTool;
import com.cssweb.android.main.R;
import com.cssweb.android.view.base.BasicView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.GlobalColor;
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
public class KlineViewSingle extends BasicView {
	private Context mContext = null;

	//画笔,一个画线,一个画数据
	private Paint   paint = null, tPaint = null;
    
	private JSONObject quoteData = null;

	public String period = "day";
	
	private int count = 0;
	// 绘图区域的宽度和高度
	private int width, height;
	// K线的上半区域高度和下半区域高度
	private int klineHeight,volumeHeight;
	// k线宽度
	private double shapeWidth = 9;
	// 间隔宽度
	private double spaceWidth = 4;
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
	private int zbmenuHeight = 20;
	private int klineWidth = 0;
	
	private double highPrice = 0;
	private double lowPrice = 99999.999;
	private double highVolume = 0;
	
	public boolean isTrackStatus = false, isTouched = false, isTouchMoved = false, isSingleMoved = false;
	
	private int actualDataLen = 0; // 取到的总的记录总数
	
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
	
	private int trackLineV;
	private int isTrackNumber = 0;
	
	private String lblIndicatorName = "",lblIndicatorT1 = "",lblIndicatorT2 = "",lblIndicatorT3 = "";
	private String lblmainIndicatorT1 = "",lblmainIndicatorT2 = "",lblmainIndicatorT3 = "",lblmainIndicatorT4 = "";

	/**距离，用来计算两点触控**/
	//private float distanceX0 = 0, distanceY0 = 0, distanceX1 = 0, distanceY1 = 0;
	
	private float startPositionX = 0, currentPositionX = 0, startPositionY = 0, currentPositionY = 0;
	
	public KlineViewSingle(Context context) {
		super(context);
	}

	public KlineViewSingle(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		this.mContext = paramContext;
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
		if(mainIndicatorType.toLowerCase().equals("ma") && !quoteData.isNull("MA") && quoteData.getJSONArray("MA").getJSONArray(idx)!=null) {
			lblIndicatorName = "5:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(1), stockdigit);
			lblIndicatorT1 = "10:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(2), stockdigit);
			lblIndicatorT2 = "20:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(3), stockdigit);
			lblIndicatorT3 = "60:" + Utils.dataFormation(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(4), stockdigit);
		}
		else if(mainIndicatorType.toLowerCase().equals("boll") && !quoteData.isNull("BOLL") && quoteData.getJSONArray("BOLL").getJSONArray(idx)!=null) {
			lblIndicatorName = "M:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(1), stockdigit);
			lblIndicatorT1 = "U:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(2), stockdigit);
			lblIndicatorT2 = "L:" + Utils.dataFormation(quoteData.getJSONArray("BOLL").getJSONArray(idx).getDouble(3), stockdigit);
			lblIndicatorT3 = "";
		}
		
		if(indicatorType.toUpperCase().equals("VOLUME") && !quoteData.isNull("MA") && quoteData.getJSONArray("MA").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "VOL:" + Utils.getAmountFormat(quoteData.getJSONArray("K").getJSONArray(idx).getDouble(5), true);
			lblmainIndicatorT2 = "5:" + Utils.getAmountFormat(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(5), true);
			lblmainIndicatorT3 = "10:" + Utils.getAmountFormat(quoteData.getJSONArray("MA").getJSONArray(idx).getDouble(6), true);
			lblmainIndicatorT4 = "";
		}
		else if (indicatorType.toUpperCase().equals("MACD") && !quoteData.isNull("MACD") && quoteData.getJSONArray("MACD").getJSONArray(idx)!=null) {
			lblmainIndicatorT2 = "DIF:" + Utils.dataFormation(quoteData.getJSONArray("MACD").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "DEA:" + Utils.dataFormation(quoteData.getJSONArray("MACD").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "MACD:" + Utils.dataFormation(quoteData.getJSONArray("MACD").getJSONArray(idx).getDouble(3), stockdigit);
			lblmainIndicatorT1 = "";
		}
		else if (indicatorType.toUpperCase().equals("CCI") && !quoteData.isNull("CCI") && quoteData.getJSONArray("CCI").getJSONArray(idx)!=null) {
			lblmainIndicatorT2 = "CCI CCI:" + Utils.dataFormation(quoteData.getJSONArray("CCI").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "";
			lblmainIndicatorT4 = "";
			lblmainIndicatorT1 = "";
		}
		else if (indicatorType.toUpperCase().equals("BIAS") && !quoteData.isNull("BIAS") && quoteData.getJSONArray("BIAS").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "BIAS";
			lblmainIndicatorT2 = "6:" + Utils.dataFormation(quoteData.getJSONArray("BIAS").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "12:" + Utils.dataFormation(quoteData.getJSONArray("BIAS").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "24:" + Utils.dataFormation(quoteData.getJSONArray("BIAS").getJSONArray(idx).getDouble(3), stockdigit);
		}
		else if (indicatorType.toUpperCase().equals("KDJ") && !quoteData.isNull("KDJ") && quoteData.getJSONArray("KDJ").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "KDJ";
			lblmainIndicatorT2 = "K:" + Utils.dataFormation(quoteData.getJSONArray("KDJ").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "D:" + Utils.dataFormation(quoteData.getJSONArray("KDJ").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "J:" + Utils.dataFormation(quoteData.getJSONArray("KDJ").getJSONArray(idx).getDouble(3), stockdigit);
		}
		else if (indicatorType.toUpperCase().equals("RSI") && !quoteData.isNull("RSI") && quoteData.getJSONArray("RSI").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "RSI";
			lblmainIndicatorT2 = "6:" + Utils.dataFormation(quoteData.getJSONArray("RSI").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "12:" + Utils.dataFormation(quoteData.getJSONArray("RSI").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "24:" + Utils.dataFormation(quoteData.getJSONArray("RSI").getJSONArray(idx).getDouble(3), stockdigit);
		}
		else if (indicatorType.toUpperCase().equals("OBV") && !quoteData.isNull("OBV") && quoteData.getJSONArray("OBV").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "OBV";
			lblmainIndicatorT2 = "OBV:" + Utils.getAmountFormat(quoteData.getJSONArray("OBV").getJSONArray(idx).getDouble(1), true);
			lblmainIndicatorT3 = "MAOBV:" + Utils.getAmountFormat(quoteData.getJSONArray("OBV").getJSONArray(idx).getDouble(2), true);
			lblmainIndicatorT4 = "";
		}
		else if (indicatorType.toUpperCase().equals("PSY") && !quoteData.isNull("PSY") && quoteData.getJSONArray("PSY").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "PSY";
			lblmainIndicatorT2 = "PSY:" + Utils.dataFormation(quoteData.getJSONArray("PSY").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "PSYMA:" + Utils.dataFormation(quoteData.getJSONArray("PSY").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "";
		}
		else if (indicatorType.toUpperCase().equals("ROC") && !quoteData.isNull("ROC") && quoteData.getJSONArray("ROC").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "ROC";
			lblmainIndicatorT2 = "ROC:" + Utils.dataFormation(quoteData.getJSONArray("ROC").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "MAROC:" + Utils.dataFormation(quoteData.getJSONArray("ROC").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "";
		}
		else if (indicatorType.toUpperCase().equals("VR") && !quoteData.isNull("VR") && quoteData.getJSONArray("VR").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "VR";
			lblmainIndicatorT2 = "VR:" + Utils.dataFormation(quoteData.getJSONArray("VR").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "MAVR:" + Utils.dataFormation(quoteData.getJSONArray("VR").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "";
		}
		else if (indicatorType.toUpperCase().equals("WR") && !quoteData.isNull("WR") && quoteData.getJSONArray("WR").getJSONArray(idx)!=null) {
			lblmainIndicatorT1 = "W%R";
			lblmainIndicatorT2 = "WR1:" + Utils.dataFormation(quoteData.getJSONArray("WR").getJSONArray(idx).getDouble(1), stockdigit);
			lblmainIndicatorT3 = "WR2:" + Utils.dataFormation(quoteData.getJSONArray("WR").getJSONArray(idx).getDouble(2), stockdigit);
			lblmainIndicatorT4 = "";
		}
	}
	
	public void initData(JSONObject quoteData) throws JSONException {
		if(!quoteData.isNull("K")) {
			this.quoteData = quoteData;
			this.actualDataLen = quoteData.getJSONArray("K").length();
			if(quoteData.isNull("joTMP")&&actualDataLen>1) {//temp文件取不到的情况下
				Log.i(">>>>>temp文件取不到的情况下并且K线数据取大于1笔的情况>>>>>>", ">>>>>>>>>>>>>>>>>>>>" + quoteData);
				this.actualDataLen = quoteData.getJSONArray("K").length() -1;
			}
			else {
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
    		
            //文字的高度
            axisLabelHeight = Font.getFontHeight(dTextSize);
            klineX = axisLabelWidth;
            klineY = axisLabelHeight;

			klineHeight = (int) ((height-axisLabelHeight) * 0.6);
			volumeHeight = (int) ((height-axisLabelHeight) * 0.4);
			axisX = klineX;
			
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
			
            //画指标值
			tPaint.setTextAlign(Paint.Align.LEFT);
            tPaint.setColor(GlobalColor.colorM5);
			canvas.drawText(lblIndicatorName,  (float) (klineX + tips), axisLabelHeight - 5, tPaint);

			float size = tPaint.measureText(lblIndicatorName) + tips*2;
			tPaint.setColor(GlobalColor.colorM10);
			canvas.drawText(lblIndicatorT1,  (float) (klineX + size), axisLabelHeight - 5, tPaint);

			size += tPaint.measureText(lblIndicatorT1) + tips;
			if(size<=(klineWidth-tPaint.measureText(lblIndicatorT2))) {
				//tPaint.setColor(GlobalColor.colorM20);
				tPaint.setARGB(255, 255, 0, 255);
				canvas.drawText(lblIndicatorT2,  (float) (klineX + size), axisLabelHeight - 5, tPaint);
			}

			size += tPaint.measureText(lblIndicatorT2) + tips;
			if(size<=(klineWidth-tPaint.measureText(lblIndicatorT3))) {
				tPaint.setColor(GlobalColor.colorM60);
				canvas.drawText(lblIndicatorT3,  (float) (klineX + size), axisLabelHeight - 5, tPaint);
			} 
			
			//下半区域
			tPaint.setColor(GlobalColor.colorLabelName);
			canvas.drawText(lblmainIndicatorT1, (float) (klineX + tips), klineY + klineHeight
					+ axisLabelHeight - 5, tPaint);

			size = tPaint.measureText(lblmainIndicatorT1) + tips*2;

			tPaint.setColor(GlobalColor.colorM5);
			canvas.drawText(lblmainIndicatorT2, (float) (klineX + size), klineY + klineHeight
					+ axisLabelHeight - 5, tPaint);

			size += tPaint.measureText(lblmainIndicatorT2) + tips;
			if (size <= (klineWidth - tPaint.measureText(lblmainIndicatorT3))) {
				tPaint.setColor(GlobalColor.colorM10);
				canvas.drawText(lblmainIndicatorT3, (float) (klineX + size), klineY + klineHeight
						+ axisLabelHeight - 5, tPaint);
			}
			
			size += tPaint.measureText(lblmainIndicatorT3) + tips;
			if (size <= (klineWidth - tPaint.measureText(lblmainIndicatorT4))) {
				tPaint.setARGB(255, 255, 0, 255);
				canvas.drawText(lblmainIndicatorT4, (float) (klineX + size), klineY + klineHeight
						+ axisLabelHeight - 5, tPaint);
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
    				AxisLabelPriceText = Utils.dataFormation(AxisLabelPrice, stockdigit);
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
    			else if (mainIndicatorType.toUpperCase().equals("BOLL"))
    				drawBoll(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);

    			
    			rowHeight = (volumeHeight-axisLabelHeight*2) / rowNum;
    			klineY = klineHeight + axisLabelHeight*2;
    			volumeHeight = rowNum * rowHeight;
    			
    			if(indicatorType.toUpperCase().equals("VOLUME"))
    				drawVOLUME(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("MACD"))
    				drawMACD(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("CCI"))
    				drawCCI(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("BIAS"))
    				drawBIAS(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("KDJ"))
    				drawKDJ(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("RSI"))
    				drawRSI(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("OBV"))
    				drawOBV(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    			else if(indicatorType.toUpperCase().equals("PSY"))
    				drawOther(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen, "PSY");
    			else if(indicatorType.toUpperCase().equals("ROC"))
    				drawROC(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen, "ROC");
    			else if(indicatorType.toUpperCase().equals("VR"))
    				drawOther(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen, "VR");
    			else if(indicatorType.toUpperCase().equals("WR"))
    				drawOther(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen, "WR");
    			
    			drawTimeAix(canvas, quoteData, actualPos, count, shapeWidth, spaceWidth, highPrice, lowPrice, highVolume, actualDataLen);
    		}
    		
    		//绘制外边框线分别是上、左、右、中、下边框线
    		paint.setColor(GlobalColor.clrLine);
    		canvas.drawLine(klineX, 0, width, 0, paint);
    		canvas.drawLine(klineX, 0, klineX, height - axisLabelHeight, paint);
    		canvas.drawLine(width, 0, width, height - axisLabelHeight, paint);
    		//canvas.drawLine(klineX, height - axisLabelHeight, width, height - axisLabelHeight, paint);
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
		canvas.drawText("成交量:", 0, axisLabelHeight * 13, mPaint);
		canvas.drawText("成交额:", 0, axisLabelHeight * 15, mPaint);

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
		canvas.drawText(Utils.dataFormation(zhangdie, stockdigit), klineX, axisLabelHeight * 10, mPaint);
//		if(quoteData.getString("period").equals("min5") || 
//				quoteData.getString("period").equals("min15") ||
//				quoteData.getString("period").equals("min30") ||
//				quoteData.getString("period").equals("min60")){
//			qt = qt.substring(4,6)+'/'+qt.substring(6,8)+' '+qt.substring(8);
//		}else{
//			qt = qt.substring(2,4)+qt.substring(4,6)+qt.substring(6,8);
//		}
		qt = qt.substring(2,4)+qt.substring(4,6)+qt.substring(6,8);
		mPaint.setColor(GlobalColor.colorLabelName);
		canvas.drawText(qt, klineX, axisLabelHeight * 12, mPaint);
		
		mPaint.setColor(GlobalColor.colorStockName);
		canvas.drawText(Utils.getAmountFormat(quoteData.getJSONArray("K").getJSONArray(idx).getDouble(5),false), klineX, axisLabelHeight * 14, mPaint);
		mPaint.setColor(GlobalColor.colorStockName);
		canvas.drawText(Utils.getAmountFormat(quoteData.getJSONArray("K").getJSONArray(idx).getDouble(6),false), klineX, axisLabelHeight * 16, mPaint);
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
			paint.setARGB(255, 84, 255, 255);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y1, 
					axisX + shapeWidth/2, klineY + klineHeight - y3, paint);
			if(y4-y3<0) {
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
			Graphics.drawRect(canvas, axisX, klineY + klineHeight - y4, shapeWidth, (y4-y3)>0?(y4-y3):1, paint);
			Graphics.drawLine(canvas, axisX + shapeWidth/2, klineY + klineHeight - y3, 
					axisX + shapeWidth/2, klineY + klineHeight - y2, paint);
		}
	}
	
	//MA
	private void drawMA(Canvas canvas, JSONObject quoteData, int begin, int count, double shapeWidth, 
			double spaceWidth, double highPrice, double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData == null || quoteData.isNull("MA")) {
			isNetworkErrorDisplay();
			return;
		}
		
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
	
	//draw boll
	private void drawBoll(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice, double lowPrice,
			double highVolume, int actualDataLen) throws JSONException {
		if (quoteData == null || quoteData.isNull("BOLL")) {
			isNetworkErrorDisplay();
			return;
		}
		int startX = klineX;
		int startY = klineY;
		//double axisX = 0;
	
		double upperx = 0;
		double uppery = 0;
		double upperHeight = 0;
		double upper = 0;

		double lowerx = 0;
		double lowery = 0;
		double lowerHeight = 0;
		double lower = 0;

		double midx = 0;
		double midy = 0;
		double midHeight = 0;
		double mid = 0;
		
		int len = quoteData.getJSONArray("BOLL").length();
		for (int i = begin; i < (begin + count) && i< len; i++) {
			//klineX = 0;
			if (quoteData.getJSONArray("BOLL").length() < i-1)
				break;
			upper = quoteData.getJSONArray("BOLL")
					.getJSONArray(i).getDouble(2);
			lower = quoteData.getJSONArray("BOLL")
					.getJSONArray(i).getDouble(3);
			mid = quoteData.getJSONArray("BOLL")
					.getJSONArray(i).getDouble(1);

			if ((i - begin) == 0) {
				upperx = startX + spaceWidth + shapeWidth/2;
				upperHeight = (upper - lowPrice) * scale;
				uppery = startY + klineHeight - upperHeight;
			} else {
				if (quoteData.getJSONArray("BOLL")
						.getJSONArray(i - 1).getDouble(2) == 0) {
					upperx = upperx + spaceWidth + shapeWidth;
					upperHeight = (upper - lowPrice) * scale;
					uppery = startY + klineHeight - upperHeight;
				} else {
					double x1 = upperx;
					double y1 = uppery;
					upperx = upperx + spaceWidth + shapeWidth;
					upperHeight = (upper - lowPrice) * scale;
					uppery = startY + klineHeight - upperHeight;
					paint.setColor(GlobalColor.colorM10);
					canvas.drawLine((float)x1, (float)y1, (float)upperx, (float)uppery, paint);
				}
			}

			if ((i - begin) == 0) {
				lowerx = startX + spaceWidth + shapeWidth/2;
				lowerHeight = (lower - lowPrice) * scale;
				lowery = startY + klineHeight - lowerHeight;

			} else {
				if (quoteData.getJSONArray("BOLL")
						.getJSONArray(i - 1).getDouble(3) == 0) {
					lowerx = lowerx  + spaceWidth + shapeWidth;
					lowerHeight = (lower - lowPrice) * scale;
					lowery = startY + klineHeight - lowerHeight;
				} else {
					double x1 = lowerx;
					double y1 = lowery;
					lowerx = lowerx  + spaceWidth + shapeWidth;
					lowerHeight = (lower - lowPrice) * scale;
					lowery = startY + klineHeight - lowerHeight;
					//paint.setColor(GlobalColor.colorM20);
					paint.setARGB(255, 255, 0, 255);
					canvas.drawLine((float)x1, (float)y1, (float)lowerx, (float)lowery, paint);
				}
			}

			if ((i - begin) == 0) {
				midx = startX + spaceWidth + shapeWidth/2;
				midHeight = (mid - lowPrice) * scale;
				midy = startY + klineHeight - midHeight;
			} else {
				if (quoteData.getJSONArray("BOLL")
						.getJSONArray(i - 1).getDouble(1) == 0) {
					midx = midx  + spaceWidth + shapeWidth;
					midHeight = (mid - lowPrice) * scale;
					midy = startY + klineHeight - midHeight;
				} else {
					double x1 = midx;
					double y1 = midy;
					midx = midx  + spaceWidth + shapeWidth;
					midHeight = (mid - lowPrice) * scale;
					midy = startY + klineHeight - midHeight;
					paint.setColor(GlobalColor.colorM5);
					canvas.drawLine((float)x1, (float)y1, (float)midx, (float)midy, paint);
				}
			}
		}
	}
	
	public void drawVOLUME(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData == null || quoteData.isNull("MA")) {
			isNetworkErrorDisplay();
			return;
		}
		axisX = klineX;
		for (int i = begin; i < (begin + count); i++) {
			highVolume = Arith.max(highVolume, quoteData.getJSONArray("MA").getJSONArray(i)
					.getDouble(5), quoteData.getJSONArray("MA").getJSONArray(i)
					.getDouble(6));
		}
		if (highVolume > 0) {
			scale = volumeHeight / highVolume;
		} else {
			scale = 999999;
		}

		double ratio = highVolume / rowNum;
		ratio = Math.round(ratio);

		String lblvalue = "12345", labelRatio = "";
		String lbhighVolume = String.valueOf(Math.round(highVolume));
		int ratiolen = lbhighVolume.length() - lblvalue.length();
		double scaleVol = 1;
		switch (ratiolen) {
		case 1:
			labelRatio = "x10";
			scaleVol = 10;
			break;
		case 2:
			labelRatio = "x100";
			scaleVol = 100;
			break;
		case 3:
			labelRatio = "x1000";
			scaleVol = 1000;
			break;
		case 4:
			labelRatio = "x1万";
			scaleVol = 10000;
			break;
		case 5:
			labelRatio = "x1百万";
			scaleVol = 1000000;
			break;
		case 6:
			labelRatio = "x1百万";
			scaleVol = 1000000;
			break;
		default:
			labelRatio = "x1";
			scaleVol = 1;
			break;
		}

		double AxisLabelVolume = 0;
		paint.setColor(GlobalColor.clrLine);
		tPaint.setTextAlign(Paint.Align.RIGHT);
		for (int i = 0; i <= rowNum; i++) {
			if (i == rowNum || i == 0) {
				canvas.drawLine(klineX, klineY + rowHeight * i, width, klineY + rowHeight * i, paint);
			} else {
				Graphics.drawDashline(canvas, klineX, klineY + rowHeight * i,
						width, klineY + rowHeight * i, paint);
			}
			if (i != rowNum && isTrackStatus == false) {
				AxisLabelVolume = Math.round(highVolume - ratio * i);
				if (i == 0) {
					tPaint.setColor(GlobalColor.colorTicklabel);
					canvas.drawText(
							Utils.dataFormation(
									Math.round(AxisLabelVolume / scaleVol), 0),
							klineX - tips/4, klineY + rowHeight * i
									+ axisLabelHeight / 2, tPaint);
				} else {
					tPaint.setColor(GlobalColor.colorTicklabel);
					canvas.drawText(
							Utils.dataFormation(
									Math.round(AxisLabelVolume / scaleVol), 0),
							klineX - tips/4, klineY + rowHeight * i
									+ axisLabelHeight / 2, tPaint);
				}
			}
		}

		if(isTrackStatus == false){
			tPaint.setColor(GlobalColor.colorTicklabel);
			canvas.drawText(labelRatio, klineX - tips/4, height - axisLabelHeight, tPaint);
		}

		// 画K线
		if (quoteData != null) {
			for (int i = begin; i < (begin + count); i++) {
				if(i==0) {
					drawVolumeKLine(canvas, i - begin, quoteData.getJSONArray("K")
							.getJSONArray(i).getDouble(1),
							quoteData.getJSONArray("K").getJSONArray(i)
									.getDouble(4), quoteData.getJSONArray("K")
									.getJSONArray(i).getDouble(5));
				}
				else {
					drawVolumeKLine(canvas, i - begin, quoteData.getJSONArray("K")
							.getJSONArray(i).getDouble(1),
							quoteData.getJSONArray("K").getJSONArray(i)
									.getDouble(4), quoteData.getJSONArray("K")
									.getJSONArray(i).getDouble(5));
				}

				if (i == (begin + count - 1)) {
					// Util.drawString(g2, labelVolume, "VOLUME:", css);
				}
			}

			drawVolumeMACD(canvas, quoteData, begin, count, shapeWidth, spaceWidth,
					highPrice, lowPrice, highVolume);
		}
	}
	
	// 画K线
	private void drawVolumeKLine(Canvas canvas, int i, double o, double c, double volume) {

		double y1 = volume * scale;
		if (i == 0)
			axisX = axisX + spaceWidth;
		else
			axisX = axisX + spaceWidth + shapeWidth;

		if (c < o) {
			// paint.setColor(GlobalColor.colorKdown);
			paint.setARGB(255, 84, 255, 255);
			Graphics.fillRect(canvas, axisX,
					klineY + volumeHeight - y1, shapeWidth, y1, paint);
		}
//		if (c == o) {
//			// paint.setColor(GlobalColor.colorPriceEqual);
//			paint.setARGB(255, 84, 255, 255);
//			Graphics.fillRect(canvas, axisX,
//					klineY + volumeHeight - y1, shapeWidth, y1, paint);
//		}
		if (c >= o) {
			paint.setColor(GlobalColor.colorpriceUp);
			Graphics.drawRect(canvas, axisX,
					klineY + volumeHeight - y1, shapeWidth, y1, paint);
		}
	}
	
	// 画移动平均线
	private void drawVolumeMACD(Canvas canvas, JSONObject quoteData, int begin,
			int count, double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume) throws JSONException {

		if (quoteData == null || quoteData.isNull("MA") || highVolume == 0) {
			//isNetworkErrorDisplay();
			return;
		}

		double avgPrice, x1 = 0, x2 = 0;
		int y1 = 0, y2 = 0;
		paint.setColor(GlobalColor.colorM5);
		for (int i = begin; i < begin + count; i++) {
			if (quoteData.getJSONArray("MA").length() < i)
				break;
			avgPrice = quoteData.getJSONArray("MA").getJSONArray(i)
					.getDouble(5);
			y2 = (int) (avgPrice * scale);
			y2 = klineY + volumeHeight - y2;
			if (i < 4) {
				if (x2 == 0) {
					x2 = klineX + spaceWidth + shapeWidth / 2;
					x2 += (spaceWidth + shapeWidth) * (4 - i);
				}
				continue;
			}

			if (i == 4 && begin != 4) {
				x1 = x2;
				y1 = y2;
				continue;
			}

			if (i == begin)
				x2 = klineX + spaceWidth + shapeWidth / 2;
			else
				x2 = spaceWidth + shapeWidth + x2;

			if (i == begin) {
				x1 = x2;
				y1 = y2;
			} else {
				canvas.drawLine((int) Math.round(x1), y1, (int) Math.round(x2),
						y2, paint);
				x1 = x2;
				y1 = y2;
			}

			if (i == (begin + count - 1)) {
				// Util.drawString(g2, labelMaVol5, "MA5:"+avgPrice, cssMa5);
			}
		}

		paint.setColor(GlobalColor.colorM10);
		x2 = 0;
		for (int i = begin; i < begin + count; i++) {
			if (quoteData.getJSONArray("MA").length() < i)
				break;
			avgPrice = quoteData.getJSONArray("MA").getJSONArray(i)
					.getDouble(6);

			y2 = (int) (avgPrice * scale);
			y2 = klineY + volumeHeight - y2;

			if (i < 9) {
				if (x2 == 0) {
					x2 = klineX + spaceWidth + shapeWidth / 2;
					x2 += (spaceWidth + shapeWidth) * (9 - i);
				}
				continue;
			}

			if (i == 9 && begin != 9) {
				x1 = x2;
				y1 = y2;
				continue;
			}

			if (i == begin)
				x2 = klineX + spaceWidth + shapeWidth / 2;
			else
				x2 = spaceWidth + shapeWidth + x2;

			if (i == begin) {
				x1 = x2;
				y1 = y2;
			} else {
				canvas.drawLine((int) Math.round(x1), y1, (int) Math.round(x2),
						y2, paint);
				x1 = x2;
				y1 = y2;
			}
			if (i == (begin + count - 1)) {
				// Util.drawString(g2, labelMaVol10, "MA10:"+avgPrice, cssMa10);
			}

		} // end for
	}
	
	private void drawMACD(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData == null || quoteData.isNull("MACD")) {
			isNetworkErrorDisplay();
			return;
		}
		double max = 0.001;
		double min = -0.001;
		int len = quoteData.getJSONArray("MACD").length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			double dif = quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(1);
			double dea = quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(2);
			double macd =quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(3);
			
			if (dif > max)
				max = dif;
			if (dea > max)
				max = dea;
			if (macd > max)
				max = macd; 
				
			if (dif < 0)
			{
				if (dif < min)
					min = dif;
			}
			if (dea < 0)
			{
				if (dea < min)
					min = dea;
			}
			if (macd < 0)
			{
				if (macd < min)
					min = macd;
			}
		}
		
		double upHeight = 0;
		double downHeight = 0;
		upHeight = volumeHeight*(max/(max+Math.abs(min)));
		downHeight = volumeHeight*(Math.abs(min)/(max+Math.abs(min)));
		
		if(isTrackStatus == false) {
			String axisLabel1="";
			if (zs)
				axisLabel1 = Utils.dataFormation(max, 0);
			else
				axisLabel1 = Utils.dataFormation(max, stockdigit);
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(axisLabel1, klineX - tips/4, klineY, tPaint);

			canvas.drawText(Utils.dataFormation(0, stockdigit), klineX - tips/4, (int)(klineY+upHeight), tPaint);
			
			String axisLabel3="";
			if (zs)
				axisLabel3 = Utils.dataFormation(min, 0);
			else
				axisLabel3 = Utils.dataFormation(min, stockdigit);
			canvas.drawText(axisLabel3, klineX - tips/4, (klineY+volumeHeight), tPaint);
		}
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, klineX, klineY, width, klineY, paint);
		Graphics.drawDashline(canvas, klineX, (int)(klineY + upHeight), width, (int)(klineY + upHeight), paint);
		canvas.drawLine(klineX, height-axisLabelHeight, width, height-axisLabelHeight, paint);
		
		if (quoteData != null)
		{
			double axisX = klineX;

			double dif = 0, dea = 0, macd = 0, difx1 = 0, difx2 = 0, deax1 = 0, deax2 = 0;
			int dify1 = 0, dify2 = 0, deay1 = 0, deay2 = 0;
			for (int i=begin; i<(begin+count) && i<len; i++)
			{
				dif = quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(1);
				dea = quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(2);
				macd =quoteData.getJSONArray("MACD").getJSONArray(i).getDouble(3);
				
				double difScale = 0;
				double deaScale = 0;
				double macdScale = 0;
				
				double difHeight = 0;
				double deaHeight = 0;
				double macdHeight = 0;
				
				if (dif >= 0)
				{
					difScale = upHeight / max;
				}
				else
				{
					difScale = downHeight / Math.abs(min);
				}
				
				if (dea >= 0)
				{
					deaScale = upHeight / max;
				}
				else
				{
					deaScale = downHeight / Math.abs(min);
				}
				
				if (macd >= 0)
				{
					macdScale = upHeight / max;
				}
				else
				{
					macdScale = downHeight / Math.abs(min);
				}	 
				
				if ((i-begin)==0)
					axisX = axisX + spaceWidth;
				else
					axisX = axisX + spaceWidth + shapeWidth;
								
				if ((i-begin)==0)
				{
					difx2 = difx2 + klineX + spaceWidth + shapeWidth/2;
					difHeight = Math.abs(dif) * difScale;
					if (dif >= 0)
					{
						dify2 = (int) (klineY + upHeight - difHeight);
					}
					else
					{
						dify2 = (int) (klineY + upHeight + difHeight);
					}
				}
				else
				{
					difx1 = difx2;
					dify1 = dify2;
					difx2 = difx2 + spaceWidth + shapeWidth;
					difHeight = Math.abs(dif) * difScale;
					if (dif >= 0)
					{
						dify2 = (int) (klineY + upHeight - difHeight);
					}
					else
					{
						dify2 = (int) (klineY + upHeight + difHeight);
					}
					
					paint.setColor(GlobalColor.colorM5);
					canvas.drawLine((float)difx1, (float)dify1, (float)difx2, (float)dify2, paint);
				}									
			
				if ((i-begin)==0)
				{
					deax2 = deax2 + klineX + spaceWidth + shapeWidth/2;
					deaHeight = Math.abs(dea) * deaScale;
					if (dea >= 0)
					{
						deay2 = (int) (klineY + upHeight - deaHeight);
					}
					else
					{
						deay2 = (int) (klineY + upHeight + deaHeight);
					}
				}
				else
				{
					deax1 = deax2;
					deay1 = deay2;
					deax2 = deax2 + spaceWidth + shapeWidth;
					deaHeight = Math.abs(dea) * deaScale;
					if (dea >= 0)
					{
						deay2 = (int) (klineY + upHeight - deaHeight);
					}
					else
					{
						deay2 = (int) (klineY + upHeight + deaHeight);
					}
					
					paint.setColor(GlobalColor.colorM10);
					canvas.drawLine((float)deax1, (float)deay1, (float)deax2, (float)deay2, paint);
				}

				if (macd >= 0)
				{
					macdHeight = Math.abs(macd) * macdScale;
					paint.setColor(GlobalColor.colorKdown);
					canvas.drawLine((int)(axisX + shapeWidth/2), (int)(klineY + upHeight - macdHeight), (int)(axisX + shapeWidth/2), (int)(klineY + upHeight), paint);
				}
				else
				{
					macdHeight = Math.abs(macd) * macdScale;
					paint.setARGB(255, 84, 255, 255);
					canvas.drawLine((int)(axisX + shapeWidth/2), (int)(klineY + upHeight), (int)(axisX + shapeWidth/2), (int)(klineY + upHeight + macdHeight), paint);
				}
				
				if ( i == (begin+count-1))
				{
					//
				}
			} // end for
		} // end if
	}
	
	public void drawCCI(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData.isNull("CCI") || quoteData.getJSONArray("CCI").length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		
		//this.shapeWidth = shapeWidth;
		//this.spaceWidth = spaceWidth;
		
		double max = 0.001;
		double min = -0.001;
		for (int i=begin; i<(begin+count);i++)
		{
			double cci = quoteData.getJSONArray("CCI").getJSONArray(i).getDouble(1);
			max = Math.max(cci,max);
			min = Math.min(cci,min); 
		} 
		
		double scale = this.volumeHeight / (Math.max(100,max) + Math.max(Math.abs(min),100));
		
		int axisY100 = (int) (klineY + volumeHeight - (100 + Math.abs(min)) * scale);
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, klineX, axisY100, width, axisY100, paint);
		
		int axisY0 = (int) (klineY + volumeHeight - (0 + Math.abs(min)) * scale);
		Graphics.drawDashline(canvas, klineX, axisY0, width, axisY0, paint);
		
		int axisYM100 = (int) (klineY + volumeHeight - (Math.abs(min)-100) * scale);
		Graphics.drawDashline(canvas, klineX, axisYM100, width, axisYM100, paint);

		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText("100.00", klineX - tips/4, axisY100 + axisLabelHeight/2, tPaint);
			canvas.drawText("0.00", klineX - tips/4, axisY0 + axisLabelHeight/2, tPaint);
			canvas.drawText("-100.00", klineX - tips/4, axisYM100 + axisLabelHeight/2, tPaint);
		}
		canvas.drawLine(klineX, height - axisLabelHeight, width, height - axisLabelHeight, paint);
		
		if (quoteData != null)
		{
			double axisX = 0;
			double difx = 0;
			
			//double deax = 0;
			//double jx = 0;

			int kHeight = 0;
			int ky = 0;					
			//int dHeight = 0;
			//int dy = 0;					
			//int jHeight = 0;
			//int jy = 0;					

			paint.setColor(GlobalColor.colorM5);
			for (int i=begin; i<(begin+count); i++)
			{
				double cci = quoteData.getJSONArray("CCI").getJSONArray(i).getDouble(1);
				
				//klineX = 0;
				if ((i-begin)==0)
					axisX = axisX + klineX + spaceWidth;
				else
					axisX = axisX + spaceWidth + shapeWidth;
								
				if ((i-begin)==0)
				{
					difx = difx + klineX + spaceWidth + shapeWidth/2;
					kHeight = (int) ((cci + Math.abs(min)) * scale);
					//kHeight = cci * scale;
					ky = klineY + volumeHeight - kHeight;
				}
				else
				{
					double x1= difx;
					int y1 = ky;
					difx = difx + spaceWidth + shapeWidth;
					kHeight = (int) ((cci + Math.abs(min)) * scale);
					ky = klineY + volumeHeight - kHeight;
					
					canvas.drawLine((int)x1, y1, (int)difx, ky, paint);
				}									

				if ( i == (begin+count-1))
				{
					//labelDif.text = "CCI:" + Utils.StockFormat(exchange+stockCode, cci);
				}
			} // end for
		} // end if
	}
	
	public void drawBIAS(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		if (quoteData.isNull("BIAS") || quoteData.getJSONArray("BIAS").length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		double max = 0.001;
		double min = -0.001;
		
		int len = quoteData.getJSONArray("BIAS").length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			double bias1 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(1);
			double bias2 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(2);
			double bias3 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(3);
			
			if (bias1 > max)
				max = bias1;
			if (bias2 > max)
				max = bias2;
			if (bias3 > max)
				max = bias3; 
				
			if (bias1 < 0)
			{
				if (bias1 < min)
					min = bias1;
			}
			if (bias2 < 0)
			{
				if (bias2 < min)
					min = bias2;
			}
			if (bias3 < 0)
			{
				if (bias3 < min)
					min = bias3;
			}
		}

		max = max + (max-min)*0.1;
		min = min - (max-min)*0.1;
		
		//double scale = this.volumeHeight / (max + Math.abs(min)) / 1.2;
		double scale = this.volumeHeight / (max - min);
		double lbl1value = max - (max - min)/3;
		double lbl2value = max - (max - min)*2/3;
		
		int startX = klineX;
		int startY = klineY;
		
		int axisY10 = (int) (startY + volumeHeight - (max - min) * scale);
		
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY10, width, axisY10, paint);
		
		int axisY0 = (int) (startY + volumeHeight - (lbl1value - min) * scale);
		Graphics.drawDashline(canvas, startX, axisY0, width, axisY0, paint);

		int axisYM10 = (int) (startY + volumeHeight - (lbl2value - min) * scale);
		Graphics.drawDashline(canvas, startX, axisYM10, width, axisYM10, paint);

		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(Utils.dataFormation(max, stockdigit), startX - tips/4, axisY10 + axisLabelHeight/2, tPaint);
			canvas.drawText(Utils.dataFormation(lbl1value, stockdigit), startX - tips/4, axisY0 + axisLabelHeight/2, tPaint);
			canvas.drawText(Utils.dataFormation(lbl2value, stockdigit), startX - tips/4, axisYM10 + axisLabelHeight/2, tPaint);
		}
		canvas.drawLine(startX, height - axisLabelHeight, width, height - axisLabelHeight, paint);
		
		if (quoteData != null)
		{
			double axisX = 0;
			
			double bias1x = 0;
			double bias1y = 0;
			
			double bias2x = 0;
			double bias2y = 0;

			double bias3x = 0;
			double bias3y = 0;

			for (int i=begin; i<(begin+count); i++)
			{
				double bias1 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(1);
				double bias2 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(2);
				double bias3 = quoteData.getJSONArray("BIAS").getJSONArray(i).getDouble(3);
				
				double bias1Height = 0;
				double bias2Height = 0;
				double bias3Height = 0;
				
				startX = klineX;
				if ((i-begin)==0)
					axisX = axisX + spaceWidth;
				else
					axisX = axisX + spaceWidth + shapeWidth;
								
				if ((i-begin)==0)
				{
					bias1x = bias1x + startX + spaceWidth + shapeWidth/2;
					bias1Height = (bias1 + Math.abs(min)) * scale;
					bias1y = startY + volumeHeight - bias1Height;
				}
				else
				{
					double x1 = bias1x;
					double y1 = bias1y;
					
					bias1x = bias1x + spaceWidth + shapeWidth;
					bias1Height = (bias1 + Math.abs(min)) * scale;
					bias1y = startY + volumeHeight - bias1Height;

					paint.setColor(GlobalColor.colorM5);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias1x), (int)bias1y, paint);
				}									
			
				if ((i-begin)==0)
				{
					bias2x = bias2x + startX +  spaceWidth + shapeWidth/2;
					bias2Height = (bias2 + Math.abs(min)) * scale;
					bias2y = startY + volumeHeight - bias2Height;
				}
				else
				{
					double x1 = bias2x;
					double y1 = bias2y;
					
					bias2x = bias2x +  spaceWidth + shapeWidth;
					bias2Height = (bias2 + Math.abs(min)) * scale;
					bias2y = startY + volumeHeight - bias2Height;
					
					paint.setColor(GlobalColor.colorM10);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias2x), (int)bias2y, paint);
				}
				
				if ((i-begin)==0)
				{
					bias3x = bias3x + startX +  spaceWidth + shapeWidth/2;
					bias3Height = (bias3 + Math.abs(min)) * scale;
					bias3y = startY + volumeHeight - bias3Height;
				}
				else
				{
					double x1 = bias3x;
					double y1 = bias3y;
					
					bias3x = bias3x +  spaceWidth + shapeWidth;
					
					bias3Height = (bias3 + Math.abs(min)) * scale;
					bias3y = startY + volumeHeight - bias3Height;
					
					//paint.setColor(GlobalColor.colorM20);
					paint.setARGB(255, 255, 0, 255);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias3x), (int)bias3y, paint);
				}
				
				if ( i == (begin+count-1))
				{
					//labelDif.text = "BIAS6:" + Utils.StockFormat(exchange+stockCode, bias1);
					//labelDea.text = "BIAS12:" + Utils.StockFormat(exchange+stockCode, bias2);
					//labelMacd.text = "BIAS24:" + Utils.StockFormat(exchange+stockCode, bias3);
				}
			} // end for
		} // end if
	}
	
	public void drawKDJ(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		
		if (quoteData.isNull("KDJ") || quoteData.getJSONArray("KDJ").length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
			
		//this.shapeWidth = shapeWidth;
		//this.spaceWidth = spaceWidth;
		
		double max = 0.001;
		double min = -0.001;
		
		int len = quoteData.getJSONArray("KDJ").length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			double k = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(1);
			double d = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(2);
			double j = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(3);
			
			if (k > max)
				max = k;
			if (d > max)
				max = d;
			if (j > max)
				max = j; 
				
			if (k < 0)
			{
				if (k < min)
					min = k;
			}
			if (d < 0)
			{
				if (d < min)
					min = d;
			}
			if (j < 0)
			{
				if (j < min)
					min = j;
			}
		}
		max = Math.max(max,100);
		min = Math.min(min, 0.0);
		double scale = this.volumeHeight / (Math.max(max,100) + Math.abs(min));
		
		int startX = klineX;
		int startY = klineY;
		
		int axisY100 = (int) (startY + volumeHeight - (100 + Math.abs(min)) * scale);
		
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY100, width, axisY100, paint);
		  
		String axisLabel1 = "100.00";
	
		int axisY80 = (int) (startY + volumeHeight - (80 + Math.abs(min)) * scale);

		Graphics.drawDashline(canvas, startX, axisY80, width, axisY80, paint);

		String axisLabel2 = "80.00";
	
		int axisY50 = (int) (startY + volumeHeight - (50 + Math.abs(min)) * scale);

		Graphics.drawDashline(canvas, startX, axisY50, width, axisY50, paint);
		
		String axisLabel3 = "50.00";
	
		int axisY20 = (int) (startY + volumeHeight - (20 + Math.abs(min)) * scale);

		Graphics.drawDashline(canvas, startX, axisY20, width, axisY20, paint);

		String axisLabel4 = "20.00";
				
		int axisY0 = (int) (startY + volumeHeight - (0 + Math.abs(min)) * scale);

		Graphics.drawDashline(canvas, startX, axisY0, width, axisY0, paint);
		
		String axisLabel5 = "0.00";
		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(axisLabel1, startX - tips/4, axisY100 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel2, startX - tips/4, axisY80 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel3, startX - tips/4, axisY50 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel4, startX - tips/4, axisY20 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel5, startX - tips/4, axisY0 + axisLabelHeight/2, tPaint);
		}
		canvas.drawLine(startX, height - axisLabelHeight, width, height - axisLabelHeight, paint);	
		
		if (quoteData != null)
		{
			
			double axisX = klineX;
			double difx = klineX;
			
			double deax = klineX;
			double jx = klineX;
	
			int kHeight = 0;
			int ky = 0;					
			int dHeight = 0;
			int dy = 0;					
			int jHeight = 0;
			int jy = 0;					
	
			for (int i=begin; i<(begin+count); i++)
			{
				double k = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(1);
				double d = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(2);
				double j = quoteData.getJSONArray("KDJ").getJSONArray(i).getDouble(3);
				
				//klineX = 0;
				if ((i-begin)==0)
					axisX = axisX +  spaceWidth;
				else
								axisX = axisX +  spaceWidth + shapeWidth;
								
				if ((i-begin)==0)
				{
					difx = difx + spaceWidth + shapeWidth/2;
					kHeight = (int) ((k + Math.abs(min)) * scale);
					ky = startY + volumeHeight - kHeight;
				}
				else
				{
					double x1 = difx;
					double y1 = ky;
					
					difx = difx + spaceWidth + shapeWidth;
					kHeight = (int) ((k + Math.abs(min)) * scale);
					ky = startY + volumeHeight - kHeight;
					
					paint.setColor(GlobalColor.colorM5);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(difx), (int)ky, paint);
				}									
	
				if ((i-begin)==0)
				{
					deax = deax + spaceWidth + shapeWidth/2;
					dHeight = (int) ((d + Math.abs(min)) * scale);
					dy = startY + volumeHeight - dHeight;
				}
				else
				{
					double x1 = deax;
					double y1 = dy;
					
					deax = deax + spaceWidth + shapeWidth;
					dHeight = (int) ((d + Math.abs(min)) * scale);
					dy = startY + volumeHeight - dHeight;
					
					paint.setColor(GlobalColor.colorM10);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(deax), (int)dy, paint);
				}
	
				if ((i-begin)==0)
				{
					jx = jx + spaceWidth + shapeWidth/2;
					jHeight = (int) ((j + Math.abs(min)) * scale);
					jy = startY + volumeHeight - jHeight;
				}
				else
				{
					double x1 = jx;
					double y1 = jy;
					
					jx = jx + spaceWidth + shapeWidth;
					jHeight = (int) ((j + Math.abs(min)) * scale);
					jy = startY + volumeHeight - jHeight;
					
					paint.setARGB(255, 255, 0, 255);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(jx), (int)jy, paint);
				}
	
				if ( i == (begin+count-1))
				{
					//labelDif.text = "K:" + Utils.StockFormat(exchange+stockCode, k);
					//labelDea.text = "D:" + Utils.StockFormat(exchange+stockCode, d);
					//labelMacd.text = "J:" + Utils.StockFormat(exchange+stockCode, j);
				}
			} // end for
		} // end if
	}
	
	public void drawRSI(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
		
		if (quoteData.isNull("RSI") || quoteData.getJSONArray("RSI").length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		
		double max = 100.0;
		double min = 0.0;
		
		int len = quoteData.getJSONArray("RSI").length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			double k = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(1);
			double d = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(2);
			double j = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(3);
			
			if (k > max)
				max = k;
			if (d > max)
				max = d;
			if (j > max)
				max = j; 
				
			if (k < 0)
			{
				if (k < min)
					min = k;
			}
			if (d < 0)
			{
				if (d < min)
					min = d;
			}
			if (j < 0)
			{
				if (j < min)
					min = j;
			}
		}
		double scale = this.volumeHeight / (max + Math.abs(min));
		
		int startX = klineX;
		int startY = klineY;
		
		int axisY100 = (int)(startY + volumeHeight - (100 + Math.abs(min)) * scale); 
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY100, width, axisY100, paint);
		
		String axisLabel1 = "";
		if (zs)
			axisLabel1 = "100.00";
		else
			axisLabel1 = "100.00";

		int axisY80 = (int) (startY + volumeHeight - (80 + Math.abs(min)) * scale);
		Graphics.drawDashline(canvas, startX, axisY80, width, axisY80, paint);
				
		String axisLabel2 = "80.00";

		int axisY50 = (int) (startY + volumeHeight - (50 + Math.abs(min)) * scale);
		Graphics.drawDashline(canvas, startX, axisY50, width, axisY50, paint);
				
		String axisLabel3 = "50.00";

		int axisY20 = (int) (startY + volumeHeight - (20 + Math.abs(min)) * scale);
		Graphics.drawDashline(canvas, startX, axisY20, width, axisY20, paint);
				
		String axisLabel4 = "20.00";
		
		int axisY0 = (int) (startY + volumeHeight - (0 + Math.abs(min)) * scale);
		Graphics.drawDashline(canvas, startX, axisY0, width, axisY0, paint);

		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(axisLabel1, startX - tips/4, (float)(axisY100 + axisLabelHeight/2), tPaint);	
			canvas.drawText(axisLabel2, startX - tips/4, (float)(axisY80 + axisLabelHeight/2), tPaint);	
			canvas.drawText(axisLabel3, startX - tips/4, (float)(axisY50 + axisLabelHeight/2), tPaint);
			canvas.drawText(axisLabel4, startX - tips/4, (float)(axisY20 + axisLabelHeight/2), tPaint);
		}
		canvas.drawLine(klineX, height - axisLabelHeight, width, height - axisLabelHeight, paint);		
		
		if (quoteData != null)
		{
			double axisX = klineX;
			
			double bias1x = klineX;
			double bias1y = 0;
			
			double bias2x = klineX;
			double bias2y = 0;

			double bias3x = klineX;
			double bias3y = 0;

			for (int i=begin; i<(begin+count); i++)
			{
				double rsi1 = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(1);
				double rsi2 = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(2);
				double rsi3 = quoteData.getJSONArray("RSI").getJSONArray(i).getDouble(3);
				
				
				double bias1Height = 0;
				double bias2Height = 0;
				double bias3Height = 0;
				
				bias1Height = scale * rsi1;
				bias2Height = scale * rsi2;
				bias3Height = scale * rsi3;
				 
				//klineX = 0;
				if ((i-begin)==0)
					axisX = axisX  + spaceWidth;
				else
					axisX = axisX  + spaceWidth + shapeWidth;
								
				if ((i-begin)==0)
				{
					bias1x = bias1x  + spaceWidth + shapeWidth/2;
					bias1y = height - axisLabelHeight - bias1Height;
				}
				else
				{
					double x1 = bias1x;
					double y1 = bias1y;
					
					bias1x = bias1x  + spaceWidth + shapeWidth;
					
					bias1y = height - axisLabelHeight - bias1Height;
					
					paint.setColor(GlobalColor.colorM5);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias1x), (int)bias1y, paint);
				}									
			
				if ((i-begin)==0)
				{
					bias2x = bias2x  + spaceWidth + shapeWidth/2;
					bias2y = height - axisLabelHeight - bias2Height;
				}
				else
				{				
					double x1 = bias2x;
					double y1 = bias2y;
					bias2x = bias2x  + spaceWidth + shapeWidth;
					bias2y = height - axisLabelHeight - bias2Height;						
					paint.setColor(GlobalColor.colorM10);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias2x), (int)bias2y, paint);
				}
				
				if ((i-begin)==0)
				{
					bias3x = bias3x  + spaceWidth + shapeWidth/2;
					bias3y = height - axisLabelHeight - bias3Height;					
				}
				else
				{
					double x1 = bias3x;
					double y1 = bias3y;
					bias3x = bias3x  + spaceWidth + shapeWidth;
					bias3y = height - axisLabelHeight - bias3Height;						
					paint.setARGB(255, 255, 0, 255);
					canvas.drawLine((int)Math.round(x1), (int)y1, (int)Math.round(bias3x), (int)bias3y, paint);
				}
				
				if ( i == (begin+count-1))
				{
					//labelDif.text = "RSI6:" + Utils.StockFormat(exchange+stockCode, rsi1);
					//labelDea.text = "RSI12:" + Utils.StockFormat(exchange+stockCode, rsi2);
					//labelMacd.text = "RSI24:" + Utils.StockFormat(exchange+stockCode, rsi3);
				}
			} // end for
		} // end if
	}
	
	public void drawOBV(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen) throws JSONException {
//		this.shapeWidth = shapeWidth;
//		this.spaceWidth = spaceWidth;
		if (quoteData.isNull("OBV") || quoteData.getJSONArray("OBV").length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		long max = 1;
		long min = 99999999999999l;
		long obv,maobv;
		int len = quoteData.getJSONArray("OBV").length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			obv   = quoteData.getJSONArray("OBV").getJSONArray(i).getLong(1);
			maobv = quoteData.getJSONArray("OBV").getJSONArray(i).getLong(2);
			max = Arith.max(obv, maobv, max);
			min = Arith.min(obv, maobv, min); 
		} 
		max = max+(max-min)/10;
		min = min-(max-min)/10;
		double scale = (double)(this.volumeHeight) / (max - min);
		
		int startX = klineX;
		int startY = klineY;
		
		double AxisLabelVolume = max;
		String lblvalue = "12345";
		int ratiolen = String.valueOf(Math.round(AxisLabelVolume)).length() - String.valueOf(lblvalue).length();
		
		String labelRatio = "";
		int scaleVol = 1;
		switch(ratiolen){
			case 1:
				labelRatio = "x10";
				scaleVol = 10;
				break;
			case 2:
				labelRatio = "x100";
				scaleVol = 100;
				break;
			case 3:
				labelRatio = "x1000";
				scaleVol = 1000;
				break;
			case 4:
				labelRatio = "x10000";
				scaleVol = 10000;
				break;
			case 5:
				labelRatio = "x1百万";
				scaleVol = 1000000;
				break;
			case 6:
				labelRatio = "x1百万";
				scaleVol = 1000000;
				break;
			default:
				labelRatio = "x1";
				scaleVol = 1;
				break;
		}
		
		int axisY1 = (int) (startY + volumeHeight - (max-min)* scale);
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY1, width, axisY1, paint);

		String axisLabel1 = String.valueOf((int)max/scaleVol);
		
		int axisY2 = (int) (startY + volumeHeight - (max-min)*3/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY2, width, axisY2, paint);
		
		String axisLabel2 = String.valueOf((int)(min+(max-min)*3/4)/scaleVol); 
		
		int axisY3 = (int) (startY + volumeHeight - (max-min)*2/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY3, width, axisY3, paint);

		String axisLabel3 = String.valueOf((int)(min+(max-min)*2/4)/scaleVol); 
		
		int axisY4 = (int) (startY + volumeHeight - (max-min)/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY4, width, axisY4, paint);

		String axisLabel4 = String.valueOf((int)(min+(max-min)*1/4)/scaleVol); 
		
		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(labelRatio, startX - tips/4, startY + volumeHeight + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel1, startX - tips/4, axisY1 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel2, startX - tips/4, axisY2 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel3, startX - tips/4, axisY3 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel4, startX - tips/4, axisY4 + axisLabelHeight/2, tPaint);
		}
		canvas.drawLine(startX, height - axisLabelHeight, width, height - axisLabelHeight, paint);	
		  
		if (quoteData != null)
		{
			double currX=0,currY1=0,currY2=0;//当前坐标
			double nextX=0,nextY1=0,nextY2=0;//下一个坐标
			
			int kHeight = 0; 			
			
			for (int i=begin; i<(begin+count) && i<len; i++)
			{
				obv = quoteData.getJSONArray("OBV").getJSONArray(i).getLong(1);
				maobv = quoteData.getJSONArray("OBV").getJSONArray(i).getLong(2);
				
				startX = klineX;
				
				if ((i-begin)==0)
				{
					currX = 0;
					currX = nextX = currX + startX + spaceWidth + shapeWidth/2; 
					kHeight = (int) ((obv - min) * scale);
					currY1 = nextY1 = startY + volumeHeight - kHeight;
					kHeight = (int) ((maobv - min) * scale);
					currY2 = nextY2 = startY + volumeHeight - kHeight;
				}else{
					nextX = currX + spaceWidth + shapeWidth; 
				} 
				// obv
				kHeight = (int) ((obv - min) * scale);
				nextY1 = startY + volumeHeight - kHeight;
				paint.setColor(GlobalColor.colorM5);
				canvas.drawLine((int)currX, (int)currY1, (int)nextX, (int)nextY1, paint);
				
				// maobv
				kHeight = (int) ((maobv - min) * scale);
				nextY2 = startY + volumeHeight - kHeight;
				paint.setColor(GlobalColor.colorM10);
				canvas.drawLine((int)currX, (int)currY2, (int)nextX, (int)nextY2, paint);
				
				currX = nextX;
				currY1 = nextY1;
				currY2 = nextY2;
				
				if ( i == (begin+count-1))
				{
					
				}
			} // end for
		} // end if
	}
	
	public void drawROC(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen, String indicator) throws JSONException {
		this.shapeWidth = shapeWidth;
		this.spaceWidth = spaceWidth;
		if (quoteData.isNull(indicator) || quoteData.getJSONArray(indicator).length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		double max = 0.001;
		double min = 9999999999f;
		double wr=0, wr2=0;
		int len = quoteData.getJSONArray(indicator).length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			wr  = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(1);
			wr2 = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(2);
			max = Arith.max(wr, wr2, max);
			min = Arith.min(wr, wr2, min);
		} 
		max = max*1.1;
		min = min - Math.abs(min)*0.1;
		double scale = (double)(this.volumeHeight) / (max - min);
		
		int startX = klineX;
		int startY = klineY;
		
		double AxisLabelVolume = max;
		String lblvalue = "12345";
		int ratiolen = String.valueOf(Math.round(AxisLabelVolume)).length() - String.valueOf(lblvalue).length();
		
		String labelRatio = "";	
		int scaleVol = 1;
		switch(ratiolen){
			case 1:
				labelRatio = "x10";
				scaleVol = 10;
				break;
			case 2:
				labelRatio = "x100";
				scaleVol = 100;
				break;
			case 3:
				labelRatio = "x1000";
				scaleVol = 1000;
				break;
			case 4:
				labelRatio = "x10000";
				scaleVol = 10000;
				break;
			default:
				labelRatio = "";
				scaleVol = 1;
				break;
		}
		
		int axisY1 = (int) (startY + volumeHeight - (max-min)* scale);
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY1, width, axisY1, paint);

		String axisLabel1 = Utils.dataFormation(max/scaleVol, 1);
		
		int axisY2 = (int) (startY + volumeHeight - (max-min)*3/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY2, width, axisY2, paint);
		
		String axisLabel2 = Utils.dataFormation((min+(max-min)*3/4)/scaleVol, 1); 
		
		int axisY3 = (int) (startY + volumeHeight - (max-min)*2/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY3, klineX+klineWidth, axisY3, paint);

		String axisLabel3 = Utils.dataFormation((min+(max-min)*2/4)/scaleVol, 1);
		
		int axisY4 = (int) (startY + volumeHeight - (max-min)/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY4, klineX+klineWidth, axisY4, paint);

		String axisLabel4 = Utils.dataFormation((min+(max-min)*1/4)/scaleVol, 1);

		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(labelRatio, startX - tips/4, startY + volumeHeight + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel1, startX - tips/4, axisY1 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel2, startX - tips/4, axisY2 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel3, startX - tips/4, axisY3 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel4, startX - tips/4, axisY4 + axisLabelHeight/2, tPaint);
		}
		
		canvas.drawLine(startX, height - axisLabelHeight, width, height - axisLabelHeight, paint);
		
		if (quoteData != null)
		{
			double currX=0,currY1=0,currY2=0;//当前坐标
			double nextX=0,nextY1=0,nextY2=0;//下一个坐标
			
			int kHeight = 0;				
			
			for (int i=begin; i<(begin+count) && i<len; i++)
			{
				wr  = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(1);
				wr2 = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(2);
				
				if ((i-begin)==0)
				{
					currX = startX;
					currX = nextX = currX + spaceWidth + shapeWidth/2; 
					kHeight = (int) ((wr - min) * scale);
					currY1 = nextY1 = startY + volumeHeight - kHeight;
					
					kHeight = (int) ((wr2 - min) * scale);
					currY2 = nextY2 = startY + volumeHeight - kHeight;
				}else{
					nextX = currX + spaceWidth + shapeWidth; 
					kHeight = (int) ((wr - min) * scale);
					nextY1 = startY + volumeHeight - kHeight;
					
					kHeight = (int) ((wr2 - min) * scale);
					nextY2 = startY + volumeHeight - kHeight;
				} 
				paint.setColor(GlobalColor.colorM5);
				canvas.drawLine((float)currX, (float)currY1, (float)nextX, (float)nextY1, paint);

				paint.setColor(GlobalColor.colorM10);
				canvas.drawLine((float)currX, (float)currY2, (float)nextX, (float)nextY2, paint);
				
				currX = nextX;
				currY1 = nextY1;
				currY2 = nextY2;
				
				if ( i == (begin+count-1))
				{
					
				}
			} // end for
		} // end if
	}

	public void drawOther(Canvas canvas, JSONObject quoteData, int begin, int count,
			double shapeWidth, double spaceWidth, double highPrice,
			double lowPrice, double highVolume, int actualDataLen, String indicator) throws JSONException {
		this.shapeWidth = shapeWidth;
		this.spaceWidth = spaceWidth;
		if (quoteData.isNull(indicator) || quoteData.getJSONArray(indicator).length() < 1) {
			isNetworkErrorDisplay();
			return;
		}
		double max = 0.001;
		double min = 9999999999f;
		double wr=0, wr2=0;
		int len = quoteData.getJSONArray(indicator).length();
		for (int i=begin; i<(begin+count) && i<len;i++)
		{
			wr  = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(1);
			wr2 = quoteData.getJSONArray(indicator).getJSONArray(i).getDouble(2);
			max = Arith.max(wr, wr2, max);
			min = Arith.min(wr, wr2, min);
		} 
		max = max+(max-min)/10;
		min = min-(max-min)/10;
		double scale = (double)(this.volumeHeight) / (max - min);
		
		int startX = klineX;
		int startY = klineY;
		
		double AxisLabelVolume = max;
		String lblvalue = "12345";
		int ratiolen = String.valueOf(Math.round(AxisLabelVolume)).length() - String.valueOf(lblvalue).length();
		
		String labelRatio = "";	
		int scaleVol = 1;
		switch(ratiolen){
			case 1:
				labelRatio = "x10";
				scaleVol = 10;
				break;
			case 2:
				labelRatio = "x100";
				scaleVol = 100;
				break;
			case 3:
				labelRatio = "x1000";
				scaleVol = 1000;
				break;
			case 4:
				labelRatio = "x10000";
				scaleVol = 10000;
				break;
			default:
				labelRatio = "";
				scaleVol = 1;
				break;
		}
		
		int axisY1 = (int) (startY + volumeHeight - (max-min)* scale);
		paint.setColor(GlobalColor.clrLine);
		Graphics.drawDashline(canvas, startX, axisY1, width, axisY1, paint);

		String axisLabel1 = String.valueOf((int)max/scaleVol);
		
		int axisY2 = (int) (startY + volumeHeight - (max-min)*3/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY2, width, axisY2, paint);
		
		String axisLabel2 = String.valueOf((int)(min+(max-min)*3/4)/scaleVol); 
		
		int axisY3 = (int) (startY + volumeHeight - (max-min)*2/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY3, klineX+klineWidth, axisY3, paint);

		String axisLabel3 = String.valueOf((int)(min+(max-min)*2/4)/scaleVol); 
		
		int axisY4 = (int) (startY + volumeHeight - (max-min)/4 * scale);
		Graphics.drawDashline(canvas, startX, axisY4, klineX+klineWidth, axisY4, paint);

		String axisLabel4 = String.valueOf((int)(min+(max-min)*1/4)/scaleVol); 

		if(isTrackStatus == false) {
			tPaint.setColor(GlobalColor.colorKlabel);
			canvas.drawText(labelRatio, startX - tips/4, startY + volumeHeight + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel1, startX - tips/4, axisY1 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel2, startX - tips/4, axisY2 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel3, startX - tips/4, axisY3 + axisLabelHeight/2, tPaint);
			canvas.drawText(axisLabel4, startX - tips/4, axisY4 + axisLabelHeight/2, tPaint);
		}
		
		canvas.drawLine(startX, height - axisLabelHeight, width, height - axisLabelHeight, paint);
		
		if (quoteData != null)
		{
			double currX=0,currY1=0,currY2=0;//当前坐标
			double nextX=0,nextY1=0,nextY2=0;//下一个坐标
			
			int kHeight = 0;				
			
			for (int i=begin; i<(begin+count) && i<len; i++)
			{
				wr  = quoteData.getJSONArray(indicator).getJSONArray(i).getLong(1);
				wr2 = quoteData.getJSONArray(indicator).getJSONArray(i).getLong(2);
				
				if ((i-begin)==0)
				{
					currX = startX;
					currX = nextX = currX + spaceWidth + shapeWidth/2; 
					kHeight = (int) ((wr - min) * scale);
					currY1 = nextY1 = startY + volumeHeight - kHeight;
					
					kHeight = (int) ((wr2 - min) * scale);
					currY2 = nextY2 = startY + volumeHeight - kHeight;
				}else{
					nextX = currX + spaceWidth + shapeWidth; 
					kHeight = (int) ((wr - min) * scale);
					nextY1 = startY + volumeHeight - kHeight;
					
					kHeight = (int) ((wr2 - min) * scale);
					nextY2 = startY + volumeHeight - kHeight;
				} 
				paint.setColor(GlobalColor.colorM5);
				canvas.drawLine((float)currX, (float)currY1, (float)nextX, (float)nextY1, paint);

				paint.setColor(GlobalColor.colorM10);
				canvas.drawLine((float)currX, (float)currY2, (float)nextX, (float)nextY2, paint);
				
				currX = nextX;
				currY1 = nextY1;
				currY2 = nextY2;
				
				if ( i == (begin+count-1))
				{
					
				}
			} // end for
		} // end if
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
		highPrice = 0;
		lowPrice = 99999.999;
		highVolume = 0;

		for (int i = begin; i < begin + count; i++) {
			// 跟 日k线数据比较大小
			double tempHigh = quoteData.getJSONArray("K").getJSONArray(i).getDouble(2);
			if (tempHigh > highPrice)
				highPrice = tempHigh;
			double tempLow = quoteData.getJSONArray("K").getJSONArray(i).getDouble(3);
			if (tempLow < lowPrice)
				lowPrice = tempLow;

			// 跟 ma 线比较
			if (quoteData.has("MA") && quoteData.getJSONArray("MA").length() > i) {
				double ma5 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(1);
				if (ma5 > highPrice)
					highPrice = ma5;
				if (ma5 < lowPrice && ma5 > 0)
					lowPrice = ma5;

				double ma10 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(2);
				if (ma10 > highPrice)
					highPrice = ma10;
				if (ma10 < lowPrice && ma10 > 0)
					lowPrice = ma10;

				double ma20 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(3);
				if (ma20 > highPrice)
					highPrice = ma20;
				if (ma20 < lowPrice && ma20 > 0)
					lowPrice = ma20;

				double ma60 = quoteData.getJSONArray("MA").getJSONArray(i).getDouble(4);
				if (ma60 > highPrice)
					highPrice = ma60;
				if (ma60 < lowPrice && ma60 > 0)
					lowPrice = ma60;

			}
			// 跟boll线比较
			if (quoteData.has("BOLL") && quoteData.getJSONArray("BOLL").length() > i) {
				double mid = quoteData.getJSONArray("BOLL").getJSONArray(i).getDouble(1);
				if (mid > highPrice)
					highPrice = mid;
				if (mid < lowPrice)
					lowPrice = mid;
//				if (mid < lowPrice && mid > 0)
//					lowPrice = mid;

				double upper = quoteData.getJSONArray("BOLL").getJSONArray(i).getDouble(2);
				if (upper > highPrice)
					highPrice = upper;
				if (upper < lowPrice)
					lowPrice = upper;
//				if (upper < lowPrice && upper > 0)
//					lowPrice = upper;

				double lower = quoteData.getJSONArray("BOLL").getJSONArray(i).getDouble(3);
				if (lower > highPrice)
					highPrice = lower;
				if (lower < lowPrice)
					lowPrice = lower;
//				if (lower < lowPrice && lower > 0)
//					lowPrice = lower;
			}
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
		int l = quoteData.getJSONArray("K").length();
		if(quoteData.isNull("joTMP")) {//temp文件取不到的情况下
			newStockhandler();
			return;
		}
		if(!quoteData.getBoolean("tradeFlag")){
			//停牌、已经收盘直接取文件不走下面的方法
			//另外一种就是集合竞价收盘价为0暂时未处理，等服务器那边通知
			return;
		}
		JSONObject tempvalue = quoteData.getJSONObject("joTMP");
		double zrsp = quoteData.getDouble("zrsp");
		period = quoteData.getString("period");
		if(period.equals("week")
				|| period.equals("month")
				|| period.equals("year")){ 
			if(tempvalue.getString(period)!=null) {
				if(tempvalue.isNull("ma")||quoteData.isNull("MA")||quoteData.isNull("K")) {
					makeTmpData();
					return;
				}
				int tp = quoteData.getInt("tp");
				if(tp==1) {
					String date = tempvalue.getJSONObject(period).getString("date");
					if(DateTool.isSameWeekMonthYear(date, period)) {
						quoteData.getJSONArray("K").getJSONArray(l-1).put(1, tempvalue.getJSONObject(period).getDouble("open")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(2, tempvalue.getJSONObject(period).getDouble("high")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(3, tempvalue.getJSONObject(period).getDouble("low")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(4, zrsp) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(5, tempvalue.getJSONObject(period).getDouble("cjsl")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(6, tempvalue.getJSONObject(period).getDouble("cjje")) ;
						//quoteData.getJSONArray("K").getJSONArray(l-1).put(0, ) ;
						zrsp = tempvalue.getJSONObject(period).getDouble("close");
					}
				}
				else {
					int spayday = quoteData.getInt("spday");
					double cjsl = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(5);
					double cjje = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(6);
					if(spayday==0) {//如果为0表示没收盘需要计算，如果收盘了则直接取temp文件里面的成交金额和成交数量
						quoteData.getJSONArray("K").getJSONArray(l-1).put(2, 
								Math.max(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(2), tempvalue.getJSONObject(period).getDouble("high"))) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(3,
								Math.min(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(3), tempvalue.getJSONObject(period).getDouble("low"))) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(5, cjsl + tempvalue.getJSONObject(period).getDouble("cjsl")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(6, cjje + tempvalue.getJSONObject(period).getDouble("cjje")) ;
					}
					else {
						quoteData.getJSONArray("K").getJSONArray(l-1).put(2, 
								Math.max(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(2), tempvalue.getJSONObject(period).getDouble("high"))) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(3,
								Math.min(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(3), tempvalue.getJSONObject(period).getDouble("low"))) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(5, tempvalue.getJSONObject(period).getDouble("cjsl")) ;
						quoteData.getJSONArray("K").getJSONArray(l-1).put(6, tempvalue.getJSONObject(period).getDouble("cjje")) ;
					}
					double jrkp = tempvalue.getJSONObject(period).getDouble("open");
					if(jrkp!=0){
						quoteData.getJSONArray("K").getJSONArray(l-1).put(1,jrkp) ;
					}
					zrsp = tempvalue.getJSONObject(period).getDouble("close");
				}
			}
		}
		
		String qt = quoteData.getJSONArray("K").getJSONArray(l-1).getString(0);
		double high = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(2);
		double low = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(3);
		double zjcj = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(4);
		double cjsl = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(5);
		
		double summa4 = tempvalue.getJSONObject("ma").getDouble("sumMa4");
		double summa9 = tempvalue.getJSONObject("ma").getDouble("sumMa9");
		double summa19 = tempvalue.getJSONObject("ma").getDouble("sumMa19");
		double summa59 = tempvalue.getJSONObject("ma").getDouble("sumMa59");
		double sumvolma4 = tempvalue.getJSONObject("ma").getDouble("sumMavol4");
		double sumvolma9 = tempvalue.getJSONObject("ma").getDouble("sumMavol9");
		
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
		if (indicatorType.equals("BIAS"))
		{
			quoteData.getJSONArray("BIAS").put(new JSONArray());
			double sum5 = tempvalue.getJSONObject("bias").getDouble("sum5");
			double sum11 = tempvalue.getJSONObject("bias").getDouble("sum11");
			double sum23 = tempvalue.getJSONObject("bias").getDouble("sum23");

			double bias1 = l>=6 ? (zjcj - (sum5 + zjcj) / 6)/ ((sum5 + zjcj) / 6) * 100 : 0;
			double bias2 = l>=12 ? (zjcj - (sum11 + zjcj) / 12)/ ((sum11 + zjcj) / 12) * 100 : 0;
			double bias3 = l>=24? (zjcj - (sum23 + zjcj) / 24)/ ((sum23 + zjcj) / 24) * 100 : 0;
			quoteData.getJSONArray("BIAS").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("BIAS").getJSONArray(l-1).put(1, bias1);
			quoteData.getJSONArray("BIAS").getJSONArray(l-1).put(2, bias2);
			quoteData.getJSONArray("BIAS").getJSONArray(l-1).put(3, bias3);
		}
		if (indicatorType.equals("RSI"))
		{
			quoteData.getJSONArray("RSI").put(new JSONArray());

			double rsi1 = 0;
			double rsi2 = 0;
			double rsi3 = 0;

			if(l>1&&tempvalue.has("rsi")){
				double smaMax1 = tempvalue.getJSONObject("rsi").getDouble("smaMax1");
				double smaMax2 = tempvalue.getJSONObject("rsi").getDouble("smaMax2");
				double smaMax3 = tempvalue.getJSONObject("rsi").getDouble("smaMax3");
				double smaAbs1 = tempvalue.getJSONObject("rsi").getDouble("smaAbs1");
				double smaAbs2 = tempvalue.getJSONObject("rsi").getDouble("smaAbs2");
				double smaAbs3 = tempvalue.getJSONObject("rsi").getDouble("smaAbs3");
				
				double rsiMax1 = (Math.max(zjcj - zrsp, 0.0) * 1 + smaMax1
						* (6 - 1)) / 6;
				double rsiAbs1 = (Math.abs(zjcj - zrsp) * 1 + smaAbs1
						* (6 - 1)) / 6;
				if(rsiAbs1 == 0)
					rsi1 = 0;
				else
					rsi1 = rsiMax1 / rsiAbs1 * 100;

				double rsiMax2 = (Math.max(zjcj - zrsp, 0.0) * 1 + smaMax2
						* (12 - 1)) / 12;
				double rsiAbs2 = (Math.abs(zjcj - zrsp) * 1 + smaAbs2
						* (12 - 1)) / 12;
				if(rsiAbs2 == 0)
					rsi2 = 0;
				else
					rsi2 = rsiMax2 / rsiAbs2 * 100;

				double rsiMax3 = (Math.max(zjcj - zrsp, 0.0) * 1 + smaMax3
						* (24 - 1)) / 24;
				double rsiAbs3 = (Math.abs(zjcj - zrsp) * 1 + smaAbs3
						* (24 - 1)) / 24;
				if(rsiAbs3 == 0)
					rsi3 = 0;
				else
					rsi3 = rsiMax3 / rsiAbs3 * 100;
			}
			quoteData.getJSONArray("RSI").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("RSI").getJSONArray(l-1).put(1, rsi1);
			quoteData.getJSONArray("RSI").getJSONArray(l-1).put(2, rsi2);
			quoteData.getJSONArray("RSI").getJSONArray(l-1).put(3, rsi3);
		}
		if (indicatorType.equals("KDJ"))
		{
			quoteData.getJSONArray("KDJ").put(new JSONArray());
			double newk = 0;
			double newd = 0;
			double newj = 0;
			
			if(l>1&&tempvalue.has("kdj")){
				double K = quoteData.getJSONArray("KDJ").getJSONArray(l-2).getDouble(1);
				double D = quoteData.getJSONArray("KDJ").getJSONArray(l-2).getDouble(2);
				double HHV = tempvalue.getJSONObject("kdj").getDouble("hhv");
				double LLV = tempvalue.getJSONObject("kdj").getDouble("llv");
				double nowllv = 0.0;

				if (LLV < low) // l是当前最低价
					nowllv = LLV;
				else
					nowllv = low;

				double nowhhv = 0.0;
				if (HHV > high)
					nowhhv = HHV;
				else
					nowhhv = high;
				double rsv;
				if (Math.abs(nowhhv - nowllv)<0.0001){
					rsv = 0;
				}else{
					rsv = (zjcj - nowllv) / (nowhhv - nowllv) * 100;
				}
				newk = (rsv * 1 + K * (3 - 1)) / 3;
				newd = (newk * 1 + D * (3 - 1)) / 3;
				newj = 3 * newk - 2 * newd;
			}
			quoteData.getJSONArray("KDJ").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("KDJ").getJSONArray(l-1).put(1, newk);
			quoteData.getJSONArray("KDJ").getJSONArray(l-1).put(2, newd);
			quoteData.getJSONArray("KDJ").getJSONArray(l-1).put(3, newj);
		}
		if (indicatorType.equals("CCI"))
		{
			quoteData.getJSONArray("CCI").put(new JSONArray());
			double CCI = 0;
			if(l>13&&tempvalue.has("cci")){
				JSONArray typlist = tempvalue.getJSONObject("cci").getJSONArray("typ");
				double sumTyp = 0;
					double TYP = (zjcj + high + low) / 3;
					double rit = 0;
					for (int i = 0; i < typlist.length(); i++) {
						rit = typlist.getDouble(i);
						if (i == 13)
							break;
						sumTyp += rit;
					}
					sumTyp += TYP;
					double ma = sumTyp / 14;

					sumTyp = 0;
					for (int i = 0; i < typlist.length(); i++) {
						rit = typlist.getDouble(i);
						if (i == 13)
							break;
						sumTyp += Math.abs(rit - ma);
					}
					sumTyp += Math.abs(TYP - ma);
					double avedev = sumTyp / 14;
					
					if(avedev == 0)
						CCI = 0;
					else
						CCI = (TYP - ma) / (0.015 * avedev);
			} 
			quoteData.getJSONArray("CCI").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("CCI").getJSONArray(l-1).put(1, CCI);
		}
		if(indicatorType.equals("OBV")){
			quoteData.getJSONArray("OBV").put(new JSONArray());
			double obv=0, maobv=0;
			if(tempvalue.has("obv")) {
				if(zjcj>zrsp){
					obv = tempvalue.getJSONObject("obv").getDouble("obv")+cjsl;
				}
				if(zjcj==zrsp){
					obv = 0;
				}
				if(zjcj<zrsp){
					obv = tempvalue.getJSONObject("obv").getDouble("obv")-cjsl;
				}
				if(l>=29){
					maobv = (tempvalue.getJSONObject("obv").getDouble("sumObv29")+obv)/30;
				}else{
					maobv = 0;
				}
			}
			quoteData.getJSONArray("OBV").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("OBV").getJSONArray(l-1).put(1, obv);
			quoteData.getJSONArray("OBV").getJSONArray(l-1).put(2, maobv);
		}
		if(indicatorType.equals("PSY")){
			quoteData.getJSONArray("PSY").put(new JSONArray());
			double countPsy=0,psy=0,psyma=0;
			if(zjcj>zrsp){
				countPsy = 1;
			}else{
				countPsy = 0;
			} 
			if(l>=11&&tempvalue.has("psy")){
				countPsy += tempvalue.getJSONObject("psy").getDouble("psyCount11");
				psy = countPsy/12*100;
				psyma = (tempvalue.getJSONObject("psy").getDouble("sumPsy")+psy)/6;
			}else{
				psy = 0;
			}
			quoteData.getJSONArray("PSY").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("PSY").getJSONArray(l-1).put(1, psy); 
			quoteData.getJSONArray("PSY").getJSONArray(l-1).put(2, psyma); 
		}
		if(indicatorType.equals("ROC")){
			quoteData.getJSONArray("ROC").put(new JSONArray());
			double roc = 0, rocma = 0;
			if(l>12&&tempvalue.has("roc")){
				double refClose12 = tempvalue.getJSONObject("roc").getDouble("refClose12");
				if(refClose12==0){
					roc = 0;
				}else{
					roc = 100 * (zjcj-refClose12)/refClose12;
				}
				rocma = (tempvalue.getJSONObject("roc").getDouble("sumRoc")+roc)/6;
			}else{
				roc = 0;
			}
			quoteData.getJSONArray("ROC").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("ROC").getJSONArray(l-1).put(1, roc);  
			quoteData.getJSONArray("ROC").getJSONArray(l-1).put(2, rocma); 
		}
		if(indicatorType.equals("WR")){
			quoteData.getJSONArray("WR").put(new JSONArray());
			double wr = 0, wr2 = 0;
			if(l>9&&tempvalue.has("wr")){
				double llv = tempvalue.getJSONObject("wr").getDouble("llv");
				double hhv = tempvalue.getJSONObject("wr").getDouble("hhv");
				hhv = Math.max(hhv,high);
				llv = Math.min(llv,low);
				if(hhv==llv){
					wr = 0;
				}else{
					wr = 100 * (hhv-zjcj)/(hhv-llv);
				}
			}else{
				wr = 0;
			}
			if(l>5&&tempvalue.has("wr")){
				double llv2 = tempvalue.getJSONObject("wr").getDouble("llv2");
				double hhv2 = tempvalue.getJSONObject("wr").getDouble("hhv2");
				
				hhv2 = Math.max(hhv2,high);
				llv2 = Math.min(llv2,low);
				if(hhv2==llv2){
					wr2 = 0;
				}else{
					wr2 = 100 * (hhv2-zjcj)/(hhv2-llv2);
				}
			}else{
				wr2 = 0;
			}
			quoteData.getJSONArray("WR").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("WR").getJSONArray(l-1).put(1, wr);  
			quoteData.getJSONArray("WR").getJSONArray(l-1).put(2, wr2); 				
		}
		if(indicatorType.equals("VR")){
			quoteData.getJSONArray("VR").put(new JSONArray());
			double vr = 0, vrma = 0;
			if(l>=24&&tempvalue.has("vr")){
				double sum1 = tempvalue.getJSONObject("vr").getDouble("sum1");
				double sum2 = tempvalue.getJSONObject("vr").getDouble("sum2");
				if(zjcj>zrsp){
					sum1 += cjsl;
				}else{
					sum2 += cjsl;
				}
				if(sum2==0){
					vr = 0;
				}else{
					vr = 100*sum1/sum2;
				}
			}else{
				vr = 0;
			}
			if(l>=6)
				vrma = (tempvalue.getJSONObject("vr").getDouble("sumVr")+vr)/6;
			quoteData.getJSONArray("VR").getJSONArray(l-1).put(0, qt);
			quoteData.getJSONArray("VR").getJSONArray(l-1).put(1, vr);  
			quoteData.getJSONArray("VR").getJSONArray(l-1).put(2, vrma);  		
		}
	}
	
	private void makeTmpData() throws JSONException {
		JSONObject tempvalue = quoteData.getJSONObject("joTMP");
		int l = quoteData.getJSONArray("K").length();
		double zrsp = quoteData.getDouble("zrsp");
//		quoteData.getJSONArray("K").getJSONArray(l-1).put(1, tempvalue.getJSONObject(period).getDouble("open")) ;
//		quoteData.getJSONArray("K").getJSONArray(l-1).put(2, tempvalue.getJSONObject(period).getDouble("high")) ;
//		quoteData.getJSONArray("K").getJSONArray(l-1).put(3, tempvalue.getJSONObject(period).getDouble("low")) ;
//		//quoteData.getJSONArray("K").getJSONArray(l-1).put(4, zrsp) ;
//		quoteData.getJSONArray("K").getJSONArray(l-1).put(5, tempvalue.getJSONObject(period).getDouble("cjsl")) ;
//		quoteData.getJSONArray("K").getJSONArray(l-1).put(6, tempvalue.getJSONObject(period).getDouble("cjje")) ;
		
		
		int tp = quoteData.getInt("tp");
		if(tp==1) {
			String date = tempvalue.getJSONObject(period).getString("date");
			Log.i("#####date####", date+">>>>>>>>" + DateTool.isSameWeekMonthYear(date, period));
			if(DateTool.isSameWeekMonthYear(date, period)) {
				quoteData.getJSONArray("K").getJSONArray(l-1).put(1, tempvalue.getJSONObject(period).getDouble("open")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(2, tempvalue.getJSONObject(period).getDouble("high")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(3, tempvalue.getJSONObject(period).getDouble("low")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(4, zrsp) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(5, tempvalue.getJSONObject(period).getDouble("cjsl")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(6, tempvalue.getJSONObject(period).getDouble("cjje")) ;
				//quoteData.getJSONArray("K").getJSONArray(l-1).put(0, ) ;
				zrsp = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(0);
			}
		}
		else {
			int spayday = quoteData.getInt("spday");
			double cjsl = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(5);
			double cjje = quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(6);
			if(spayday==0) {//如果为0表示没收盘需要计算，如果收盘了则直接取temp文件里面的成交金额和成交数量
				quoteData.getJSONArray("K").getJSONArray(l-1).put(2, 
						Math.max(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(2), tempvalue.getJSONObject(period).getDouble("high"))) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(3,
						Math.min(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(3), tempvalue.getJSONObject(period).getDouble("low"))) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(5, cjsl + tempvalue.getJSONObject(period).getDouble("cjsl")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(6, cjje + tempvalue.getJSONObject(period).getDouble("cjje")) ;
			}
			else {
				quoteData.getJSONArray("K").getJSONArray(l-1).put(2, 
						Math.max(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(2), tempvalue.getJSONObject(period).getDouble("high"))) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(3,
						Math.min(quoteData.getJSONArray("K").getJSONArray(l-1).getDouble(3), tempvalue.getJSONObject(period).getDouble("low"))) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(5, tempvalue.getJSONObject(period).getDouble("cjsl")) ;
				quoteData.getJSONArray("K").getJSONArray(l-1).put(6, tempvalue.getJSONObject(period).getDouble("cjje")) ;
			}
			double jrkp = tempvalue.getJSONObject(period).getDouble("open");
			if(jrkp!=0){
				quoteData.getJSONArray("K").getJSONArray(l-1).put(1,jrkp) ;
			}
			zrsp = tempvalue.getJSONObject(period).getDouble("close");
		}
		
		if (quoteData.isNull("MA")&&mainIndicatorType.toUpperCase().equals("BOLL"))
		{
			JSONArray jMA = new JSONArray();
			jMA.put(0, 0);
			jMA.put(1, 0);
			jMA.put(2, 0);
			jMA.put(3, 0);
			jMA.put(4, 0);
			jMA.put(5, 0);
			jMA.put(6, 0);
			quoteData.put("MA", new JSONArray().put(jMA));
		}
		
		Log.i("#######MA#########", quoteData.isNull("MA")+">>>>>>>>>>>");
		if(quoteData.isNull("MA")) {
			JSONArray jMA = new JSONArray();
			jMA.put(0, 0);
			jMA.put(1, 0);
			jMA.put(2, 0);
			jMA.put(3, 0);
			jMA.put(4, 0);
			jMA.put(5, 0);
			jMA.put(6, 0);
			quoteData.put(mainIndicatorType.toUpperCase(), new JSONArray().put(jMA));
		}
		else {
			if(quoteData.getJSONArray("MA").length()==0) {
				JSONArray jMA = new JSONArray();
				jMA.put(0, 0);
				jMA.put(1, 0);
				jMA.put(2, 0);
				jMA.put(3, 0);
				jMA.put(4, 0);
				jMA.put(5, 0);
				jMA.put(6, 0);
				quoteData.put("MA", new JSONArray().put(jMA));
			}
		}
		Log.i("#######MA#########" + indicatorType.toUpperCase(), quoteData.isNull(indicatorType.toUpperCase())+">>>>>>>>>>>");
		if(quoteData.isNull(indicatorType.toUpperCase())) {
			JSONArray jIn = new JSONArray();
			jIn.put(0, 0);
			jIn.put(1, 0);
			jIn.put(2, 0);
			jIn.put(3, 0);
			quoteData.put(indicatorType.toUpperCase(), new JSONArray().put(jIn));
		}
		if(tempvalue.isNull("ma")&&quoteData.getJSONArray("K").length()>1) //处理数据异常的情况
			actualDataLen = quoteData.getJSONArray("K").length() - 1;
		else 
			actualDataLen = quoteData.getJSONArray("K").length();
		if (actualDataLen < visualKLineCount){
			count = actualDataLen;
		}else{
			count = visualKLineCount;
		}
		if (this.actualDataLen - this.actualPos -1 <= this.visualKLineCount)
		{
			actualPos = actualDataLen - count; 
		}
		Log.i("#######makeTmpData########" + actualPos, actualDataLen+">>>>>>>>" + count);
	}
	
	private void newStockhandler() throws JSONException {
//		if(!quoteData.getBoolean("tradeFlag")){
//			return;
//		}
		Log.i("#######quoteData########", quoteData.isNull("MA") + ">>>>>>>>>>>>>>>>>" + quoteData);
		if (mainIndicatorType.toUpperCase().equals("BOLL"))
		{
			if(quoteData.isNull(mainIndicatorType.toUpperCase())) {
				JSONArray jMA = new JSONArray();
				jMA.put(0, 0);
				jMA.put(1, 0);
				jMA.put(2, 0);
				jMA.put(3, 0);
				jMA.put(4, 0);
				jMA.put(5, 0);
				jMA.put(6, 0);
				quoteData.put(mainIndicatorType.toUpperCase(), new JSONArray().put(jMA));
			}
		}
		
		if(quoteData.isNull(mainIndicatorType.toUpperCase())) {
			JSONArray jMA = new JSONArray();
			jMA.put(0, 0);
			jMA.put(1, 0);
			jMA.put(2, 0);
			jMA.put(3, 0);
			jMA.put(4, 0);
			jMA.put(5, 0);
			jMA.put(6, 0);
			quoteData.put(mainIndicatorType.toUpperCase(), new JSONArray().put(jMA));
		}

		if(quoteData.isNull(indicatorType.toUpperCase())) {
			JSONArray jIn = new JSONArray();
			jIn.put(0, 0);
			jIn.put(1, 0);
			jIn.put(2, 0);
			jIn.put(3, 0);
			quoteData.put(indicatorType.toUpperCase(), new JSONArray().put(jIn));
		}
		
		actualDataLen = quoteData.getJSONArray("K").length();
		if (actualDataLen < visualKLineCount){
			count = actualDataLen;
		}else{
			count = visualKLineCount;
		}
		if (this.actualDataLen - this.actualPos -1 <= this.visualKLineCount)
		{
			actualPos = actualDataLen - count; 
		}
		Log.i("#######quoteData########" + actualPos, actualDataLen+">>>>>>>>" + count);
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
	
	private void isNetworkErrorDisplay() {
		Toast.makeText(mContext, R.string.load_data_error, Toast.LENGTH_SHORT).show();
	}
}
