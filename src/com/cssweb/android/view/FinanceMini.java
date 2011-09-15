/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FinanceMini.java 下午12:34:58 2010-12-23
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
 * 财务数据
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FinanceMini extends BasicView {
	private final String TAG = "FinanceMini";
	
	private Paint   mPaint = new Paint();
	
	private int type = 0;
	private String stocktype = "0";
	private JSONObject quoteData = null;
	
	protected String exchange;
	protected String stockcode;
	protected String stockname;
	private int stockdigit;
	
	protected int x, y, width, height;
	private int tips = 12;
	
	public FinanceMini(Context context) {
		super(context);
	}

	public FinanceMini(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		//this.context = paramContext;
	}
	
	public void setStockInfo(String exchange, String stockcode, String stockname, int type, String stocktype) {
		this.exchange = exchange;
		this.stockcode = stockcode;
		this.stockname = stockname;
		this.type = type;
		this.stocktype = stocktype;
	}
    
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
    	float rate = (float) w/320;
    	mTextSize = (int)(M_TEXT_SIZE*rate);
    	dTextSize = (int)(D_TEXT_SIZE*rate);
    	DX		  = (int)(DX_W*rate);
    	DY 		  = (int)(DY_H*rate);
		this.x = 0;
		this.y = 0;
		this.width = w;
		this.height = h;
    }
	
	public void onDraw(Canvas canvas) {

        super.onDraw(canvas);
        //初始化参数  
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
    		stockdigit = Utils.getStockDigit(type);
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
	
	public void drawHKPrice(Canvas canvas) {
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setTextSize(mTextSize);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText("总量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总额", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("每手", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("每股盈利", x, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), false), x-tips, y, paint);
				canvas.translate(0, DY);
//				if(jo.getDouble("wb")<0)
//					paint.setColor(GlobalColor.colorPriceDown);
//				else if(jo.getDouble("wb")>0)
//					paint.setColor(GlobalColor.colorpriceUp);
//				else 
//					paint.setColor(GlobalColor.colorPriceEqual);
//				canvas.drawText(Utils.dataFormation(jo.getDouble("wb")*100, 1)+"%", x-tips, y, paint);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjje"), false), x-tips, y, paint);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), false), x-tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("msgs"), true), x-tips, y, paint);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, -DY*3);
				canvas.drawText("量比", x, y, paint);
				canvas.translate(0, DY);
//				canvas.drawText("委差", x, y, paint);
				canvas.drawText("市值", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x, y, paint);
//				canvas.translate(0, DY);
//				canvas.drawText("总额", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("价差", x, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*3);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.dataFormation(jo.getDouble("lb"), 1, jo.getInt("tp")), x-tips, y, paint);
				canvas.translate(0, DY);
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
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.drawText("", x-tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), false), x-tips, y, paint);
//				paint.setColor(GlobalColor.colorStockName);
//				canvas.translate(0, DY);
//				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x-tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void drawPrice(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setTextSize(mTextSize);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText("委比", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("换手", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("净资", x, y, paint);
				canvas.translate(0, DY);
				int syjd = jo.getInt("syjd");
				switch(syjd) {
				case 1:
					canvas.drawText("收益(一)", x, y, paint);
					break;
				case 2:
					canvas.drawText("收益(二)", x, y, paint);
					break;
				case 3:
					canvas.drawText("收益(三)", x, y, paint);
					break;
				case 4:
					canvas.drawText("收益(四)", x, y, paint);
					break;
				default:
					canvas.drawText("收益", x, y, paint);
					break;
				}
				
				if(jo.getDouble("wb")<0)
					paint.setColor(GlobalColor.colorPriceDown);
				else if(jo.getDouble("wb")>0)
					paint.setColor(GlobalColor.colorpriceUp);
				else 
					paint.setColor(GlobalColor.colorPriceEqual);
				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*5);
				if(jo.getInt("tp")==1)
					canvas.drawText("", x-tips, y, paint);
				else
					canvas.drawText(Utils.dataFormation(jo.getDouble("wb")*100, 1)+"%", x-tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), false), x-tips, y, paint);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), false), x-tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("hs")*100, 1)+"%", x-tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jz"), 1), x-tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("mgsy"), 2), x-tips, y, paint);

				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, -DY*5);
				canvas.drawText("委差", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("量比", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("股本", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("流通", x, y, paint);
				canvas.translate(0, DY);
				if(NameRule.isBond(type))
					canvas.drawText("全价", x, y, paint);
				else
					canvas.drawText("PE(动)", x, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*5);
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
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("lb"), 1, jo.getInt("tp")), x-tips, y, paint);
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), false), x-tips, y, paint);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("gb"), true), x-tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("ltsl")*100, true), x-tips, y, paint);
				canvas.translate(0, DY);
				if(NameRule.isBond(type)) {
					canvas.drawText(Utils.dataFormation(jo.getDouble("fullprice"), 1), x-tips, y, paint);
				}
				else {
					canvas.drawText(Utils.dataFormation(jo.getDouble("sy"), 1), x-tips, y, paint);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	public void drawHKIndex(Canvas canvas) {
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setTextSize(mTextSize);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText("上涨家数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("平盘家数", x + tips, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("下跌家数", x + tips, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width, -DY*3);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
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
	
	public void drawIndex(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setTextSize(mTextSize);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText("Ａ股成交", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("Ｂ股成交", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("国债成交", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("基金成交", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("权证成交", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("债券成交", x, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width, -DY*5);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("a"), true), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("b"), true), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("govbond"), true), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("fund"), true), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("warrant"), true), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("bond"), true), x, y, paint);
				
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
	private void drawQihuo(Canvas canvas) {
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);				
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setTextSize(mTextSize);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, DY);
				canvas.drawText("总量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("结算", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("涨停", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("持仓", x, y, paint);
//				canvas.translate(0, DY);
//				canvas.drawText("开仓 ", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("外盘", x, y, paint);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2, -DY*4);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("cjsl"), false), x-tips, y, paint);
				
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrjs"), stockdigit, jo.getInt("tp")), x-tips, y, paint);

				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("zt"), stockdigit, jo.getInt("tp")), x-tips, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrcc"), 0, jo.getInt("tp")), x-tips, y, paint);
				
//				canvas.translate(0, DY);
//				canvas.drawText(Utils.dataFormation(jo.getDouble("jrkc"), 0), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("wp"), false), x, y, paint);
				
				canvas.translate(0, -DY*4);
				
				paint.setTextAlign(Paint.Align.LEFT);
				paint.setColor(GlobalColor.colorLabelName);

				canvas.drawText("总额", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("昨结", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("跌停", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("仓差", x, y, paint);
//				canvas.translate(0, DY);
//				canvas.drawText("平仓 ", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("内盘", x, y, paint);

				canvas.translate(width/2, -DY*5);

				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), false), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorPriceEqual);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("zrjs"), stockdigit, jo.getInt("tp")), x-tips, y, paint);

				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("dt"), stockdigit, jo.getInt("tp")), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("zc"), 0, jo.getInt("tp")), x-tips, y, paint);
				
//				paint.setColor(GlobalColor.colorStockName);
//				canvas.translate(0, DY);
//				canvas.drawText(Utils.dataFormation(jo.getDouble("jrpc"), 0), x-tips, y, paint);	
				
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("np"), false), x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
	}
	
//	public void initData(String stocktype, JSONObject quotedata) {
//		this.stocktype = stocktype;
//		this.quoteData = quotedata;
//	}
	
	public void initData(JSONObject quotedata) {
		this.quoteData = quotedata;
	}
	
	public void reCycle() {
		System.gc();
	}
}


