/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)PriceView.java 上午09:55:05 2010-10-19
 */
package com.cssweb.android.view;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;

import com.cssweb.android.view.base.BasicView;
import com.cssweb.quote.util.GlobalColor;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 文字行情绘图
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class PriceView extends BasicView {
	
	private final String TAG = "PriceView";

	private Paint   mPaint = new Paint(), tPaint = new Paint();

	private int type;
    private int stockdigit = 0;
	private String stocktype = "0";
	private JSONObject quoteData = null;
	
	protected String exchange;
	protected String stockcode;
	protected String stockname;
	
	protected int x, y, width, height;
	private int tips = 12;
	private int startX, endX;
	
	public PriceView(Context context) {
		super(context);
	}

	public PriceView(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
	}
	
	public void setStockInfo(String exchange, String stockcode, String stockname, int stockdigit, String stocktype, int type) {
		this.exchange = exchange;
		this.stockcode = stockcode;
		this.stockname = stockname;
		this.stocktype = stocktype;
		this.stockdigit = stockdigit;
		this.type = type;
	}
    
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	float rate = (float) w/320;
    	mTextSize = (int)(M_TEXT_SIZE*rate);
    	dTextSize = (int)(D_TEXT_SIZE*rate);
    	DX		  = (int)(DX_W*rate);
    	DY 		  = (int)(DY_H*rate)-5;
		this.x = 0;
		this.y = 0;
		this.width = w;
		this.height = h;
    }
	
	public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        //初始化参数
		//initView();
		//canvas.drawColor(GlobalColor.clearSCREEN);
        if("hk".equals(exchange)) {
            switch(stocktype.charAt(0)) {
            case '0': 
            	drawHKPrice(canvas);
            	break;
            case '1': 
            	drawHKIndex(canvas);
            	break;
            }
        }
        else if("cf".equals(exchange)||"dc".equals(exchange)
        		||"sf".equals(exchange)||"cz".equals(exchange)) {
        	drawQihuo(canvas);
        }
        else {
            switch(stocktype.charAt(0)) {
            case '0': 
            	drawPrice(canvas);
            	break;
            case '1': 
            	drawIndex(canvas);
            	break;
            case '2': 
            	drawIndex(canvas);
            	break;
            }
        }
	}
	
	public void drawPrice(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		tPaint.setColor(GlobalColor.clrLine);
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setStrokeWidth(1);
		this.x = 0;
		this.y = 0;
		startX = x + tips/2;
		endX = width - tips/2;
		canvas.drawRect(startX, DY/4, endX, height - DY/4, tPaint);
		canvas.drawLine(startX, DY+DY/4, endX, DY+DY/4, tPaint);
		canvas.drawLine(startX, DY*6+DY/4, endX, DY*6+DY/4, tPaint);
		canvas.drawLine(startX, DY*11+DY/4, endX, DY*11+DY/4, tPaint);
		canvas.drawLine(startX, DY*16+DY/4, endX, DY*16+DY/4, tPaint);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				String str = "";
				double zrsp = jo.getDouble("zrsp");

				canvas.translate(0, DY);

				paint.setTypeface(Typeface.DEFAULT_BOLD);
				paint.setAntiAlias(true);
				paint.setTextSize(mTextSize);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("委比", x+tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, 0);
				if(jo.getDouble("wb")<0)
					paint.setColor(GlobalColor.colorPriceDown);
				else if(jo.getDouble("wb")>0)
					paint.setColor(GlobalColor.colorpriceUp);
				else 
					paint.setColor(GlobalColor.colorPriceEqual);
				if(jo.getInt("tp")==1)
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(Utils.dataFormation(jo.getDouble("wb")*100, 1)+"%", x, y, paint);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("委差", x+tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, 0);
				if(jo.getDouble("wc")<0) {
					paint.setColor(GlobalColor.colorPriceDown);
					canvas.drawText("-" + Utils.getAmountFormat(Math.abs(jo.getDouble("wc")), true), x-tips, y, paint);
				}
				else if(jo.getDouble("wc")>0) {
					paint.setColor(GlobalColor.colorpriceUp);
					canvas.drawText(Utils.getAmountFormat(jo.getDouble("wc"), true), x-tips, y, paint);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
					canvas.drawText(Utils.getAmountFormat(jo.getDouble("wc"), false), x-tips, y, paint);
				}
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("卖五", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖四", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖三", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖二", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖一", x + tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*4);
				for(int i=5; i>=1; i--) {
					double temp2 = jo.getDouble("sjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					//paint.setTextAlign(Paint.Align.CENTER);
					canvas.drawText(str, x, y, paint);
					if(i!=1)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				for(int i=5; i>=1; i--) {
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("ssl" + i), false), x - tips, y, paint);
					if(i!=1)
						canvas.translate(0, DY);
				}
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("买一", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买二", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买三", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买四", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买五", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*4);
				paint.setTextAlign(Paint.Align.RIGHT);
				for(int i=1; i<=5; i++) {
					double temp2 = jo.getDouble("bjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					canvas.drawText(str, x, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				for(int i=1; i<=5; i++) {				
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("bsl" + i), false), x - tips, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("现价", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨跌", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总量", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("换手", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("净资", x + tips, y, paint);
				canvas.translate(0, DY);
				int syjd = jo.getInt("syjd");
				switch(syjd) {
					case 1:
						canvas.drawText("收益(一)", x + tips, y, paint);
						break;
					case 2:
						canvas.drawText("收益(二)", x + tips, y, paint);
						break;
					case 3:
						canvas.drawText("收益(三)", x + tips, y, paint);
						break;
					case 4:
						canvas.drawText("收益(四)", x + tips, y, paint);
						break;
					default:
						canvas.drawText("收益()", x + tips, y, paint);
						break;
				}
				
				canvas.translate(width/2, -DY*7);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double zjcj = jo.getDouble("zjcj");
				setColor(paint, zjcj, zrsp);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit, jo.getInt("tp")), x, y, paint);
				
				canvas.translate(0, DY);
				double zhangd = jo.getDouble("zd");
				if(zhangd<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangd>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				String zhangdie = Utils.dataFormation(zhangd, stockdigit, jo.getInt("tp"));
				if(zhangdie.equals("-"))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangdie, x, y, paint);
				
				double zhangf = jo.getDouble("zf");
				if(zhangf<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangf>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangfu = Utils.dataFormation(zhangf*100, stockdigit, jo.getInt("tp"));
				if(zhangfu.equals("-")||zhangfu.equals(""))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), false), x, y, paint);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), false), x, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("hs")*100, 1)+"%", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jz"), 1), x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("mgsy"), 2), x, y, paint);
				
				canvas.translate(0, -DY*7);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("今开", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("量比", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("股本", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("流通", x + tips, y, paint);
				canvas.translate(0, DY);
				if(NameRule.isBond(type))
					canvas.drawText("全价", x + tips, y, paint);
				else
					canvas.drawText("PE(动)", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*7);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("lb"), 1, jo.getInt("tp")), x - tips, y, paint);
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), false), x - tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("gb"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("ltsl")*100, true), x - tips, y, paint);
				canvas.translate(0, DY);
				if(NameRule.isBond(type)) {
					canvas.drawText(Utils.dataFormation(jo.getDouble("fullprice"), 1), x - tips, y, paint);
				}
				else {
					canvas.drawText(Utils.dataFormation(jo.getDouble("sy"), 1), x - tips, y, paint);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void drawIndex(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		tPaint.setColor(GlobalColor.clrLine);
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setStrokeWidth(1);
		this.x = 0;
		this.y = 0;
		startX = x + tips/2;
		endX = width - tips/2;
		canvas.drawRect(startX, DY+DY/4, endX, height - DY/4, tPaint);
		canvas.drawLine(startX, DY*7+DY/2, endX, DY*7+DY/2, tPaint);
		canvas.drawLine(startX, DY*18+DY/4, endX, DY*18+DY/4, tPaint);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				double zrsp = jo.getDouble("zrsp");
				
				paint.setTextSize(mTextSize);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY*2);
				canvas.drawText("Ａ股成交", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("Ｂ股成交", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("国债成交", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("基金成交", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("权证成交", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("债券成交", x + tips, y, paint);
				
				canvas.translate(0, DY*1.5f);
				canvas.drawText("最新指数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("今日开盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("昨日收盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数涨跌", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数涨幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数振幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交量", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交额", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高指数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低指数", x + tips, y, paint);
				
				canvas.translate(width, -DY*15.5f);
				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("a"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("b"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("govbond"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("fund"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("warrant"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("bond"), true), x - tips, y, paint);
				
				double zjcj = jo.getDouble("zjcj");
				setColor(paint, zjcj, zrsp);
				canvas.translate(0, DY*1.5f);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText(Utils.dataFormation(zrsp, stockdigit), x - tips, y, paint);
				
				double zhangd = jo.getDouble("zd");
				if(zhangd<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangd>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangdie = Utils.dataFormation(zhangd, stockdigit);
				if(zhangdie.equals("-"))
					canvas.drawText("", x - tips, y, paint);
				else
					canvas.drawText(zhangdie, x - tips, y, paint);
				
				double zhangf = jo.getDouble("zf");
				if(zhangf<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangf>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangfu = Utils.dataFormation(zhangf*100, stockdigit);
				if(zhangfu.equals("-"))
					canvas.drawText("", x - tips, y, paint);
				else
					canvas.drawText(zhangfu+"%", x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("amp") * 100, 1) + "%", x - tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjsl"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit), x - tips, y, paint);
				
				canvas.translate(-width, DY*1.7f);
				
				paint.setTextSize(mTextSize);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("涨家数", x + tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(width/2, 0);
				canvas.drawText(String.valueOf(jo.getInt("zj")), x, y, paint);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("跌家数", x + tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(width/2, 0);
				canvas.drawText(String.valueOf(jo.getInt("dj")), x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void drawHKPrice(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		tPaint.setColor(GlobalColor.clrLine);
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setStrokeWidth(1);
		this.x = 0;
		this.y = 0;
		startX = x + tips/2;
		endX = width - tips/2;
		canvas.drawRect(startX, DY/4, endX, height - DY/4, tPaint);
//		canvas.drawLine(startX, DY+DY/4, endX, DY+DY/4, tPaint);
		canvas.drawLine(startX, DY*5+DY/4, endX, DY*5+DY/4, tPaint);
		canvas.drawLine(startX, DY*10+DY/4, endX, DY*10+DY/4, tPaint);
		canvas.drawLine(startX, DY*16+DY/4, endX, DY*16+DY/4, tPaint);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				String str = "";
				double zrsp = jo.getDouble("zrsp");

				canvas.translate(0, DY);

				paint.setTypeface(Typeface.DEFAULT_BOLD);
				paint.setAntiAlias(true);
				paint.setTextSize(mTextSize);
//				paint.setTextAlign(Paint.Align.LEFT);
//				paint.setColor(GlobalColor.colorLabelName);
//				canvas.drawText("委比", x+tips, y, paint);
//
//				paint.setTextAlign(Paint.Align.RIGHT);
//				canvas.translate(width/2, 0);
//				if(jo.getDouble("wb")<0)
//					paint.setColor(GlobalColor.colorPriceDown);
//				else if(jo.getDouble("wb")>0)
//					paint.setColor(GlobalColor.colorpriceUp);
//				else 
//					paint.setColor(GlobalColor.colorPriceEqual);
//				canvas.drawText(Utils.dataFormation(jo.getDouble("wb")*100, 1)+"%", x, y, paint);
//				
//				paint.setTextAlign(Paint.Align.LEFT);
//				paint.setColor(GlobalColor.colorLabelName);
//				canvas.drawText("委差", x+tips, y, paint);
//
//				paint.setTextAlign(Paint.Align.RIGHT);
//				canvas.translate(width/2, 0);
//				if(jo.getDouble("wc")<0) {
//					paint.setColor(GlobalColor.colorPriceDown);
//					canvas.drawText("-" + Utils.getAmountFormat(Math.abs(jo.getDouble("wc")), true), x-tips, y, paint);
//				}
//				else if(jo.getDouble("wc")>0) {
//					paint.setColor(GlobalColor.colorpriceUp);
//					canvas.drawText(Utils.getAmountFormat(jo.getDouble("wc"), true), x-tips, y, paint);
//				}
//				else {
//					paint.setColor(GlobalColor.colorPriceEqual);
//					canvas.drawText(Utils.getAmountFormat(jo.getDouble("wc"), true), x-tips, y, paint);
//				}
//				
//				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("卖五", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖四", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖三", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖二", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖一", x + tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*4);
				for(int i=5; i>=1; i--) {
					double temp2 = jo.getDouble("sjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					canvas.drawText(str, x, y, paint);
					if(i!=1)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				for(int i=5; i>=1; i--) {
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("ssl" + i), false), x - tips, y, paint);
					if(i!=1)
						canvas.translate(0, DY);
				}
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("买一", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买二", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买三", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买四", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买五", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*4);
				paint.setTextAlign(Paint.Align.RIGHT);
				for(int i=1; i<=5; i++) {
					double temp2 = jo.getDouble("bjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					canvas.drawText(str, x, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				for(int i=1; i<=5; i++) {				
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("bsl" + i), false), x - tips, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("现价", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨跌", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总量", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("每手", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("每股盈利", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*6);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double zjcj = jo.getDouble("zjcj");
				setColor(paint, zjcj, zrsp);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit, jo.getInt("tp")), x, y, paint);
				
				canvas.translate(0, DY);
				double zhangd = jo.getDouble("zd");
				if(zhangd<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangd>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				String zhangdie = Utils.dataFormation(zhangd, stockdigit, jo.getInt("tp"));
				if(zhangdie.equals("-")||zhangdie.equals(""))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangdie, x, y, paint);
				
				double zhangf = jo.getDouble("zf");
				if(zhangf<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangf>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangfu = Utils.dataFormation(zhangf*100, 1, jo.getInt("tp"));
				if(zhangfu.equals("-")||zhangfu.equals(""))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), false), x, y, paint);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), false), x, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("msgs"), true), x, y, paint);
				
				canvas.translate(0, -DY*5);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("今开", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总额", x + tips, y, paint);
//				canvas.drawText("量比", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("价差", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*5);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit, jo.getInt("tp")), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
//				canvas.translate(0, DY);
//				canvas.drawText(Utils.dataFormation(jo.getDouble("lb"), 1), x - tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), false), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), false), x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void drawHKIndex(Canvas canvas) {
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		tPaint.setColor(GlobalColor.clrLine);
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setStrokeWidth(1);
		this.x = 0;
		this.y = 0;
		startX = x + tips/2;
		endX = width - tips/2;
		canvas.drawRect(startX, DY+DY/4, endX, height - DY/4, tPaint);
		canvas.drawLine(startX, DY*5+DY/2, endX, DY*5+DY/2, tPaint);
		canvas.drawLine(startX, DY*11+DY/8, endX, DY*11+DY/8, tPaint);
		//canvas.drawLine(startX, DY*16+DY/4, endX, DY*16+DY/4, tPaint);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				double zrsp = jo.getDouble("zrsp");
				
				paint.setTextSize(mTextSize);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY*2);
				canvas.drawText("指数现价", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数涨跌", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数涨幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数振幅", x + tips, y, paint);
				
				canvas.translate(0, DY*1.5f);
				canvas.drawText("昨日收盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("今日开盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高价格", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低价格", x + tips, y, paint);
//				canvas.translate(0, DY);
//				canvas.drawText("总成交量", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交额", x + tips, y, paint);
				
				canvas.translate(0, DY*1.5f);
//				canvas.drawText("指数量比", x + tips, y, paint);
//				canvas.translate(0, DY);
				canvas.drawText("上涨家数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("平盘家数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("下跌家数", x + tips, y, paint);
				
				canvas.translate(width, -DY*13f);
				paint.setTextAlign(Paint.Align.RIGHT);
				
				double zjcj = jo.getDouble("zjcj");
				setColor(paint, zjcj, zrsp);
				
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x - tips, y, paint);
				
				double zhangd = jo.getDouble("zd");
				if(zhangd<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangd>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangdie = Utils.dataFormation(zhangd, stockdigit);
				if(zhangdie.equals("-"))
					canvas.drawText("", x - tips, y, paint);
				else
					canvas.drawText(zhangdie, x - tips, y, paint);
				
				double zhangf = jo.getDouble("zf");
				if(zhangf<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangf>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangfu = Utils.dataFormation(zhangf*100, stockdigit);
				if(zhangfu.equals("-"))
					canvas.drawText("", x - tips, y, paint);
				else
					canvas.drawText(zhangfu+"%", x - tips, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("amp") * 100, 1) + "%", x - tips, y, paint);
				
				canvas.translate(0, DY*1.5f);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText(Utils.dataFormation(zrsp, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				//canvas.translate(0, DY);
				//canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjsl"), true), x - tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x - tips, y, paint);

				canvas.translate(0, DY*1.5f);
				//canvas.drawText(Utils.dataFormation(jo.getDouble("lb"), 1), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorpriceUp);
				//canvas.translate(0, DY);
				canvas.drawText(String.valueOf(jo.getInt("zj")), x - tips, y, paint);

				paint.setColor(GlobalColor.colorPriceEqual);
				canvas.translate(0, DY);
				canvas.drawText(String.valueOf(jo.getInt("pj")), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(String.valueOf(jo.getInt("dj")), x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	private void drawQihuo(Canvas canvas) {
		//stockdigit = 0;
		Paint paint = this.mPaint;
		tPaint.setColor(GlobalColor.clrLine);
		tPaint.setStyle(Paint.Style.STROKE);
		tPaint.setStrokeWidth(1);
		this.x = 0;
		this.y = 0;
		startX = x + tips/2;
		endX = width - tips/2;
		canvas.drawRect(startX, DY/4, endX, height - DY/4, tPaint);
		canvas.drawLine(startX, DY*2+DY/4, endX, DY*2+DY/4, tPaint);
		canvas.drawLine(startX, DY*8+DY/4, endX, DY*8+DY/4, tPaint);
		canvas.drawLine(startX, DY*11+DY/4, endX, DY*11+DY/4, tPaint);
		//canvas.drawLine(startX, DY*16+DY/4, endX, DY*16+DY/4, tPaint);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				String str = "";
				double zrsp = jo.getDouble("zrsp");

				canvas.translate(0, DY);

				paint.setTypeface(Typeface.DEFAULT_BOLD);
				paint.setAntiAlias(true);
				paint.setTextSize(mTextSize);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("卖价", x+tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买价", x+tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY);
				double temp2 = jo.getDouble("sjw1");
				setColor(paint, temp2, zrsp);
				str = Utils.dataFormation(temp2, stockdigit);
				canvas.drawText(str, x, y, paint);

				canvas.translate(0, DY);
				temp2 = jo.getDouble("bjw1");
				setColor(paint, temp2, zrsp);
				str = Utils.dataFormation(temp2, stockdigit);
				canvas.drawText(str, x, y, paint);
				
				canvas.translate(0, -DY);
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText("卖量", x+tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买量", x+tips, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("ssl1"), true), x - tips, y, paint);

				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("bsl1"), true), x - tips, y, paint);
				
				canvas.translate(-width, DY);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("现价", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨跌", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨幅", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("结算", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总量", x + tips, y, paint);

				canvas.translate(width/2, -DY*5);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double zjcj = jo.getDouble("zjcj");
				setColor(paint, zjcj, zrsp);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x, y, paint);
				
				canvas.translate(0, DY);
				double zhangd = jo.getDouble("zd");
				if(zhangd<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangd>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				String zhangdie = Utils.dataFormation(zhangd, stockdigit);
				if(zhangdie.equals("-"))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangdie, x, y, paint);
				
				double zhangf = jo.getDouble("zf");
				if(zhangf<0) {
					paint.setColor(GlobalColor.colorPriceDown);
				}
				else if(zhangf>0) {
					paint.setColor(GlobalColor.colorpriceUp);
				}
				else {
					paint.setColor(GlobalColor.colorPriceEqual);
				}
				canvas.translate(0, DY);
				String zhangfu = Utils.dataFormation(zhangf*100, 1);
				if(zhangfu.equals("-"))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrjs"), stockdigit), x, y, paint);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), true), x, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), true), x, y, paint);
				
				canvas.translate(0, -DY*5);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("今开", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("昨结", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总额", x + tips, y, paint);

				canvas.translate(width/2, -DY*5);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit), x - tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit), x - tips, y, paint);

				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("zrjs"), stockdigit), x - tips, y, paint);				
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), true), x - tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x - tips, y, paint);
				
				canvas.translate(-width, DY);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.drawText("涨停", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("持仓", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("开仓 ", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*2);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double zt = jo.getDouble("zt");
				setColor(paint, zt, zrsp);
				canvas.drawText(Utils.dataFormation(zt, stockdigit), x, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrcc"), 0), x, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrkc"), 0), x, y, paint);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, -DY*2);
				
				canvas.drawText("跌停", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("仓差", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("平仓 ", x + tips, y, paint);
				
				canvas.translate(width/2, -DY*2);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				double dt = jo.getDouble("dt");
				setColor(paint, dt, zrsp);
				canvas.drawText(Utils.dataFormation(dt, stockdigit), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("zc"), 0), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrpc"), 0), x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void initData(JSONObject quotedata) {
		this.quoteData = quotedata;
	}
	
	public void reCycle() {
		System.gc();
	}
	
	private void setColor(Paint paint, double d0, double d1) {
		if(d0==0)
			paint.setColor(GlobalColor.colorPriceEqual);
		else if(d0>d1) 
			paint.setColor(GlobalColor.colorpriceUp);
		else if(d0<d1) 
			paint.setColor(GlobalColor.colorPriceDown);
		else 
			paint.setColor(GlobalColor.colorPriceEqual);
	}
}
