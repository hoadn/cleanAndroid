/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FundTrend.java 下午04:50:15 2010-12-24
 */
package com.cssweb.android.view;

import javax.microedition.fairy.Font;
import javax.microedition.fairy.Graphics;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.TouchDelegate;

import com.cssweb.android.view.base.BasicView;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.GlobalColor;
import com.cssweb.quote.util.Utils;

/**
 * 基金走势图
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FTrendView extends BasicView {
	
	private Paint   paint = null;   //绘单元格
	private Paint   tPaint = null;  //绘文字
	private Paint   dataPaint = null;  //绘数据

	private JSONArray quoteData = null;
	private int begin = 0, count = 90;  //90笔数据
	private int dataLen = 0;

	private double highPrice = 0;         //最高净值
	private double lowPrice = 99999.999;  //最低净值

	private int width, height;  //可绘图区域宽度、高度
	private int klineX = 0;
	private int klineY = 0;
	private int klineWidth = 0;		//右边图表宽度
	private int klineHeight = 0;    //右边图表高度
	private int axisLabelWidth = 0;  //左边绘文字宽度
	private int axisLabelHeight = 0; //左边绘文字高度
	
	private int offSet =3 ;    //偏移量 （文字与图表之间的偏移）
	private int iSLowerScreen = 0 ;  //是否是320*480 的手机 , 列高度偏移量
	/**
	 * 空隙(线之间的间隔)
	 */
	private double SPACE = 0;   //90笔数据，每笔数据占用的宽度
	private double scale = 0;
	
	private int colNum = 4;      //列数
	private int colWeight = 10;  //列宽度
	private int rowNum = 7;      //行数
	private int rowHeight = 10;  //行高度
	
	private int trackLineV;			//追踪线的x坐标
	private int isTrackNumber = 0;  //追踪数量	
	
	public boolean isTrackStatus = false, isTouched = false, isTouchMoved = false;
	private float startPositionX = 0, currentPositionX = 0, startPositionY = 0, currentPositionY = 0;
	
	public FTrendView(Context context) {
		super(context);
	}

	public FTrendView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		
       
		
	}
	
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	float rate = (float) w/320;
    	mTextSize = (int)(M_TEXT_SIZE*rate);
    	dTextSize = (int)(D_TEXT_SIZE*rate);
    	DX		  = (int)(DX_W*rate);
    	DY 		  = (int)(DY_H*rate);
		this.width = w;
		this.height = h;
		if (w == 320){
			iSLowerScreen = 3 ;
		}
    }
	
	public void onDraw(Canvas canvas) {
        try {

        	if(quoteData==null) {
        		return;
        	}
        	
        	/**
        	 * 计算最大最小值
        	 */
        	calcMaxMin();
        	
	        drawChart(canvas);

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
    
	public void drawChart(Canvas canvas) throws JSONException {
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);  //抗锯齿
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1);
		tPaint = new Paint();
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setTypeface(Typeface.DEFAULT_BOLD);
		tPaint.setAntiAlias(true);  //去锯齿
		dataPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  //抗锯齿
		dataPaint.setStyle(Paint.Style.STROKE);
		dataPaint.setStrokeWidth(3);
		tPaint.setTextSize(dTextSize);
		axisLabelHeight = Font.getFontHeight(dTextSize);
		axisLabelWidth = (int)tPaint.measureText("0.00000") + offSet;
		klineWidth = width - axisLabelWidth ;
		klineHeight = height - axisLabelHeight*2  - iSLowerScreen ;
		klineX = axisLabelWidth;
		klineY = axisLabelHeight;
		
		SPACE = Arith.div(klineWidth, count);		//klineWidth/count;
		scale = klineHeight / (highPrice - lowPrice);       //比例
		double ratio = (highPrice - lowPrice) / rowNum;     //比例
		rowHeight = klineHeight / rowNum;
		colWeight = klineWidth / colNum ;
		tPaint.setColor(GlobalColor.colorLabelName);
		tPaint.setTextAlign(Paint.Align.LEFT);
		
		/**
		 * 最顶部文字
		 */
		canvas.drawText("时间:"+quoteData.getJSONArray(isTrackNumber).getString(0), axisLabelWidth , axisLabelHeight-5, tPaint);
		canvas.drawText("净值:"+quoteData.getJSONArray(isTrackNumber).getDouble(3), axisLabelWidth + width/2, axisLabelHeight-5, tPaint);
		paint.setColor(GlobalColor.clrGrayLine);
		/**
		 * 绘制行数
		 */

		tPaint.setTextAlign(Paint.Align.RIGHT);
		for (int i = 0; i <= rowNum; i++) {
			if (i==0 || i == rowNum) {
				canvas.drawLine(klineX, klineY + rowHeight * i, klineX + klineWidth, klineY + rowHeight * i, paint);
			}
			else {
				Graphics.drawDashline(canvas, klineX, klineY + rowHeight * i, klineX + klineWidth, klineY + rowHeight * i, paint);
			}
			if (i==0) {
				canvas.drawText(Utils.dataFormation(highPrice, 3), klineX, klineY + rowHeight * i + axisLabelHeight/2, tPaint);
			}
			else if (i == rowNum) {
				canvas.drawText(Utils.dataFormation(lowPrice, 3), klineX, klineY + rowHeight * i, tPaint);
			}
			else {
				double AxisLabelPrice = highPrice - ratio * i;
				canvas.drawText(Utils.dataFormation(AxisLabelPrice, 3), klineX, klineY + rowHeight * i + axisLabelHeight/2, tPaint);
			}
		}
		/**
		 * 绘制列数
		 */
		for (int i = 0; i <= colNum; i++) {
			if (i==0) {
				canvas.drawLine(klineX , klineY, klineX  , klineY + klineHeight, paint);
			}
			else if(i == colNum) {
				canvas.drawLine(width-1,klineY, width-1, klineY + klineHeight, paint);
			}
			else {
				Graphics.drawDashline(canvas, klineX + colWeight* i, klineY, klineX + colWeight* i, klineY + klineHeight, paint);
			}
		}
		
		/**
		 * 以下是绘数据
		 */
		double x1 = 0;
		double y1 = 0;
		double x2 = 0;
		double y2 = 0;
		double temp = 0;
		double tHeight = 0;
		dataPaint.setColor(GlobalColor.colorStockName);
		dataPaint.setStrokeWidth(3);
		for(int i=begin; i<(begin+count); i++) {
			temp = quoteData.getJSONArray(i).getDouble(3);
			if(i-begin==0) {
				x1 = klineX + SPACE * (i-begin);
				x2 = klineX + SPACE * (i-begin+1);
				tHeight = (temp - lowPrice) * scale;             // tHeight = (净值 - 最低净值) * 比例 
				y1 = axisLabelHeight + klineHeight - tHeight;    // 最底部文字标签高度 - tHeight 
				y2 = axisLabelHeight + klineHeight - tHeight;
			}
			else {
				canvas.drawLine((int)x1, (int)y1, (int)x2, (int)y2, dataPaint);
				x1 = x2;
				x2 = klineX + SPACE * (i-begin+1);
				tHeight = (temp - lowPrice) * scale;
				y1 = y2;
				y2 = axisLabelHeight + klineHeight - tHeight;
			}
		}
		/**
		 * 最底部文字
		 */
		tPaint.setColor(GlobalColor.colorLabelName);
		tPaint.setTextAlign(Paint.Align.LEFT);
		canvas.drawText(quoteData.getJSONArray(begin).getString(0), axisLabelWidth, klineHeight+axisLabelHeight*2-5, tPaint);
		canvas.drawText(quoteData.getJSONArray(begin+count-1).getString(0), (float) (width-tPaint.measureText(quoteData.getJSONArray(begin+count-1).getString(0))), klineHeight+axisLabelHeight*2-5, tPaint);
		
		/**
		 * 绘制追踪线
		 */
		if(isTrackStatus) {
		  	canvas.save();
			//显示坐标轴
			paint.setColor(GlobalColor.colorLine);
			canvas.drawLine(trackLineV, axisLabelHeight, trackLineV, klineHeight+axisLabelHeight, paint);
		  	canvas.restore();
		}
	}
	/**
	 * 计算净值的最高最低值
	 * @throws JSONException
	 */
	private void calcMaxMin() throws JSONException {
		dataLen = quoteData.length();
		if(dataLen<=count) {
			begin = 0;
			count = dataLen;
		}
		else 
			begin = dataLen-count;
		for(int i=begin; i<(begin+count); i++) {
			highPrice = Math.max(quoteData.getJSONArray(i).getDouble(3), highPrice);
			lowPrice = Math.min(quoteData.getJSONArray(i).getDouble(3), lowPrice);
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
		if (x < axisLabelWidth || x > width)
			return;
		if (y < this.axisLabelHeight || y > height - this.axisLabelHeight)
			return;
		isTouched = false;
		if (isTouchMoved==false) {
			isTouchMoved = false;
			isTrackStatus = !isTrackStatus;
			if(isTrackStatus) {//移动十字架光标
				if (x > axisLabelWidth && x < width)
					trackLineV = x;
				
				int idx = (int) ((x-axisLabelWidth) / SPACE);
				if (idx < dataLen && idx >=0 )
				{
					isTrackNumber = begin + idx;
					trackLineV = (int) (axisLabelWidth + idx * SPACE);
				}else{
					if (idx < 0) {
						isTrackNumber = 0;
						trackLineV = axisLabelWidth ;
					}
					if (idx >= dataLen){
						isTrackNumber = dataLen -1;
						trackLineV = (int) (axisLabelWidth + (dataLen -1) * SPACE);
					} 
				}
			}
		}
		/**
		 * 根据Android SDK api文档说明
		 *	invalidate 方法是用来更新视图（View）的方法
		 */
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
		if (isTrackStatus) {
			double sep = SPACE;
			int idx = (int)((currentPositionX-axisLabelWidth) / sep);
			if (idx < dataLen && idx >=0)
			{
				if (currentPositionX >= axisLabelWidth || currentPositionX <= width)
				{
					trackLineV = (int) (axisLabelWidth + idx * SPACE);
				}
				isTrackNumber = begin + idx;
				if(isTrackNumber>=dataLen){
					isTrackNumber = dataLen -1;
				}
			}else{
				if (idx < 0) {
					idx = 0;
					isTrackNumber = begin + idx;
					trackLineV = axisLabelWidth ;
				}
				if (idx >= dataLen){
					isTrackNumber = dataLen -1;
					trackLineV = (int) (axisLabelWidth + (dataLen -1) * SPACE);
				} 
			}
		}
		/**
		 * 根据Android SDK api文档说明
		 *	invalidate 方法是用来更新视图（View）的方法
		 */
		this.invalidate();
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
	
	public void initData(JSONArray arrayData) throws JSONException {
		this.quoteData = arrayData;
		isTrackNumber = quoteData.length() - 1;
	}
	
	public void reCycle() {
		paint = null;
		System.gc();
	}
}
