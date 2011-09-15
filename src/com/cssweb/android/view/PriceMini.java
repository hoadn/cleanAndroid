/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)PriceMini.java 上午11:11:03 2010-11-23
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
import com.cssweb.quote.util.Utils;

/**
 * 
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class PriceMini extends BasicView {
	
	private final String TAG = "PriceMini";
	
	private Paint   mPaint = new Paint();
	
    private int stockdigit = 0;
	private String stocktype = "0";
	private JSONObject quoteData = null;
	
	private String exchange;
//	private String stockcode;
//	private String stockname;
	
	protected int x, y, width, height;
	private int tips = 12;
	
	public PriceMini(Context context) {
		super(context);
	}

	public PriceMini(Context paramContext, AttributeSet paramAttributeSet) {
		super(paramContext, paramAttributeSet);
		//this.context = paramContext;
	}
	
	public void setStockInfo(String exchange, String stockcode, String stockname, String stocktype) {
		this.exchange = exchange;
//		this.stockcode = stockcode;
//		this.stockname = stockname;
		this.stocktype = stocktype;
		this.stockdigit = Utils.getNumFormat(exchange, stockcode);
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
        if("hk".equals(exchange)) {
            switch(stocktype.charAt(0)) {
            case '0': 
            	drawPrice(canvas);
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
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				String str = "";
				double zrsp = jo.getDouble("zrsp");
				
				double zjcj = jo.getDouble("zjcj");
				paint.setTextSize(mTextSize*2);
				setColor(paint, zjcj, zrsp);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(DX, DY*2f);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x, y, paint);
				
				paint.setStyle(Paint.Style.STROKE);
				paint.setTextSize(mTextSize);
				
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
				canvas.translate(-DX/2, DY*0.8f);
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
				canvas.translate(DX*2.5f, 0);
				//String zhangfu = Utils.dataFormation(zhangf*100, stockdigit);
				//原来是根据证券市场判断小数位数的，现在改为固定2位有效数字
				String zhangfu = Utils.dataFormation(zhangf*100, 1, jo.getInt("tp"));
				if(zhangfu.equals("-")||zhangfu.equals(""))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(-DX*3, DY*1.2f);
				canvas.drawText("卖一", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖二", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖三", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖四", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖五", x, y, paint);
				
				canvas.translate(DX*2, -DY*4);
				for(int i=1; i<=5; i++) {
					double temp2 = jo.getDouble("sjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					canvas.drawText(str, x, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2 - DX*2, -DY*4);
				for(int i=1; i<=5; i++) {
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("ssl" + i), false), x-tips, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorLabelName);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(0, -DY*7);
				canvas.drawText("开盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买一", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买二", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买三", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买四", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买五", x, y, paint);
				
				//paint.setTextAlign(Paint.Align.CENTER);
				canvas.translate(DX*2, -DY*7);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit, jo.getInt("tp")), x, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit, jo.getInt("tp")), x, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit, jo.getInt("tp")), x, y, paint);
				
				canvas.translate(0, DY);
				for(int i=1; i<=5; i++) {
					double temp2 = jo.getDouble("bjw" + i);
					setColor(paint, temp2, zrsp);
					str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
					canvas.drawText(str, x, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(width/2-DX*2, -DY*4);
				for(int i=1; i<=5; i++) {				
					paint.setTextAlign(Paint.Align.RIGHT);
					canvas.drawText(Utils.getAmountFormat(jo.getInt("bsl" + i), false), x-tips, y, paint);
					if(i!=5)
						canvas.translate(0, DY);
				}
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}
		else {//处理空数据，避免出现黑屏现象
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(GlobalColor.colorLabelName);
			
			canvas.translate(DX, DY*2f);
			canvas.drawText("-", x, y, paint);
			
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(mTextSize);
			
			canvas.translate(-DX/2, DY*0.8f);
			canvas.drawText("-", x, y, paint);
			
			canvas.translate(DX*2.5f, 0);
			canvas.drawText("-", x, y, paint);
			canvas.translate(-DX*3, DY*1.2f);
			canvas.drawText("卖一", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("卖二", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("卖三", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("卖四", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("卖五", x, y, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.translate(width/2, -DY*7);
			canvas.drawText("开盘", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最高", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最低", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买一", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买二", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买三", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买四", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买五", x, y, paint);
		}
	}
	
	public void drawQihuo(Canvas canvas) {
		//canvas.restore();
		Paint paint = this.mPaint;
		paint.setTypeface(Typeface.DEFAULT_BOLD);
		paint.setAntiAlias(true);
		if(quoteData!=null) {
			try {
				JSONArray jArr = quoteData.getJSONArray("data");
				JSONObject jo = jArr.getJSONObject(0);
				String str = "";
				double zrsp = jo.getDouble("zrsp");
				
				double zjcj = jo.getDouble("zjcj");
				paint.setTextSize(mTextSize*2);
				setColor(paint, zjcj, zrsp);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(DX, DY*2f);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x, y, paint);
				
				paint.setStyle(Paint.Style.STROKE);
				paint.setTextSize(mTextSize);
				
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
				canvas.translate(-DX/2, DY*0.8f);
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
				canvas.translate(DX*2.5f, 0);
				String zhangfu = Utils.dataFormation(zhangf*100, 1);
				if(zhangfu.equals("-")||zhangfu.equals(""))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.translate(-DX*3, DY*1.2f);
				canvas.drawText("卖价", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买价", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("开仓 ", x, y, paint);
				
				canvas.translate(width/2, -DY*2);
				paint.setTextAlign(Paint.Align.RIGHT);
				double temp2 = jo.getDouble("sjw1");
				setColor(paint, temp2, zrsp);
				str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
				canvas.drawText(str, x-tips, y, paint);

				canvas.translate(0, DY);
				temp2 = jo.getDouble("bjw1");
				setColor(paint, temp2, zrsp);
				str = Utils.dataFormation(temp2, stockdigit, jo.getInt("tp"));
				canvas.drawText(str, x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrkc"), 0, jo.getInt("tp")), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(0, -DY*5);
				canvas.drawText("开盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("卖量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("买量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("平仓 ", x, y, paint);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*5);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit, jo.getInt("tp")), x-tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit, jo.getInt("tp")), x-tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit, jo.getInt("tp")), x-tips, y, paint);
				
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorStockName);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("ssl1"), false), x-tips, y, paint);

				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getInt("bsl1"), false), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("jrpc"), 0, jo.getInt("tp")), x-tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}else {//处理空数据，避免出现黑屏现象
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(GlobalColor.colorLabelName);
			
			canvas.translate(DX, DY*2f);
			canvas.drawText("-", x, y, paint);
			
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(mTextSize);
			
			canvas.translate(-DX/2, DY*0.8f);
			canvas.drawText("-", x, y, paint);
			
			canvas.translate(DX*2.5f, 0);
			canvas.drawText("-", x, y, paint);
			canvas.translate(-DX*3, DY*1.2f);
			canvas.drawText("卖价", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买价", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("开仓 ", x, y, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.translate(width/2, -DY*5);
			canvas.drawText("开盘", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最高", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最低", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("卖量", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("买量", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("平仓 ", x, y, paint);
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
				double zrsp = jo.getDouble("zrsp");
				
				double zjcj = jo.getDouble("zjcj");
				paint.setTextSize(mTextSize*2);
				setColor(paint, zjcj, zrsp);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(DX*0.5f, DY*2f);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x, y, paint);
				
				paint.setStyle(Paint.Style.STROKE);
				paint.setTextSize(mTextSize);
				
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
				canvas.translate(0, DY*0.8f);
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
				canvas.translate(DX*3.5f, 0);
				String zhangfu = Utils.dataFormation(zhangf*100, stockdigit);
				if(zhangfu.equals("-"))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				
				canvas.translate(-DX*4f, DY*1.2f);
				canvas.drawText("昨日收盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交额", x, y, paint);
				
				canvas.translate(width/2, -DY*1);
				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.drawText(Utils.dataFormation(zrsp, stockdigit), x - tips, y, paint);

				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x - tips, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(0, -DY*4);
				canvas.drawText("开盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("指数振幅", x, y, paint);
				
				paint.setTextAlign(Paint.Align.RIGHT);
				canvas.translate(width/2, -DY*3);
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit), x-tips, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit), x-tips, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit), x-tips, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.dataFormation(jo.getDouble("amp") * 100, 1) + "%", x - tips, y, paint);
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}else {//处理空数据，避免出现黑屏现象
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(GlobalColor.colorLabelName);
			
			canvas.translate(DX, DY*2f);
			canvas.drawText("-", x, y, paint);
			
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(mTextSize);
			
			canvas.translate(-DX/2, DY*0.8f);
			canvas.drawText("-", x, y, paint);
			
			canvas.translate(DX*2.5f, 0);
			canvas.drawText("-", x, y, paint);
			canvas.translate(-DX*3, DY*1.2f);
			canvas.drawText("昨日收盘", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("总成交额", x, y, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.translate(width/2, -DY*4);
			canvas.drawText("开盘", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最高", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最低", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("指数振幅", x, y, paint);
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
				double zrsp = jo.getDouble("zrsp");
				
				double zjcj = jo.getDouble("zjcj");
				paint.setTextSize(mTextSize*2);
				setColor(paint, zjcj, zrsp);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(DX*0.5f, DY*2f);
				canvas.drawText(Utils.dataFormation(zjcj, stockdigit), x, y, paint);
				

				paint.setStyle(Paint.Style.STROKE);
				paint.setTextSize(mTextSize);
				
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
				canvas.translate(0, DY*0.8f);
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
				canvas.translate(DX*3.5f, 0);
				String zhangfu = Utils.dataFormation(zhangf*100, stockdigit);
				if(zhangfu.equals("-"))
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(zhangfu+"%", x, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(-DX*3.5f, DY*1.2f);
				//canvas.translate(-DX*0.5f, DY*2f);
				canvas.drawText("昨收", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交量", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("总成交额", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("上涨家数", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("下跌家数", x, y, paint);
				
				paint.setColor(GlobalColor.colorLabelName);
				paint.setTextAlign(Paint.Align.LEFT);
				canvas.translate(width/2-DX, -DY*7);
				canvas.drawText("开盘", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最高", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("最低", x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText("振幅", x, y, paint);

				paint.setTextAlign(Paint.Align.RIGHT);
				paint.setColor(GlobalColor.colorLabelName);
				canvas.translate(0, 0);
				canvas.drawText(Utils.dataFormation(zrsp, stockdigit), x, y, paint);
				
				canvas.translate(width/2, -DY*3);
				paint.setTextAlign(Paint.Align.RIGHT);
				
				double jrkp = jo.getDouble("jrkp");
				setColor(paint, jrkp, zrsp);
				canvas.drawText(Utils.dataFormation(jrkp, stockdigit), x, y, paint);
				
				canvas.translate(0, DY);
				double zg = jo.getDouble("zgcj");
				setColor(paint, zg, zrsp);
				canvas.drawText(Utils.dataFormation(zg, stockdigit), x, y, paint);
				
				canvas.translate(0, DY);
				double zd = jo.getDouble("zdcj");
				setColor(paint, zd, zrsp);
				canvas.drawText(Utils.dataFormation(zd, stockdigit), x, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				double amp = jo.getDouble("amp");
				if(amp==0)
					canvas.drawText("", x, y, paint);
				else
					canvas.drawText(Utils.dataFormation(jo.getDouble("amp")*100, 1) + "%", x, y, paint);
				
				paint.setColor(GlobalColor.colorStockName);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjsl"), true), x, y, paint);
				canvas.translate(0, DY);
				canvas.drawText(Utils.getAmountFormat(jo.getDouble("cjje"), true), x, y, paint);
				
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorpriceUp);
				canvas.drawText(String.valueOf(jo.getInt("zj")), x, y, paint);
				canvas.translate(0, DY);
				paint.setColor(GlobalColor.colorPriceDown);
				canvas.drawText(String.valueOf(jo.getInt("dj")), x, y, paint);
				
			} catch (JSONException e) {
				Log.e(TAG, e.toString());
			}
		}else {//处理空数据，避免出现黑屏现象
			paint.setTextAlign(Paint.Align.LEFT);
			paint.setColor(GlobalColor.colorLabelName);
			
			canvas.translate(DX, DY*2f);
			canvas.drawText("-", x, y, paint);
			
			paint.setStyle(Paint.Style.STROKE);
			paint.setTextSize(mTextSize);
			
			canvas.translate(-DX/2, DY*0.8f);
			canvas.drawText("-", x, y, paint);
			
			canvas.translate(DX*2.5f, 0);
			canvas.drawText("-", x, y, paint);
			canvas.translate(-DX*2.5f, DY*1.2f);
			canvas.drawText("昨收", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("总成交量", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("总成交额", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("上涨家数", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("下跌家数", x, y, paint);
			
			paint.setTextAlign(Paint.Align.LEFT);
			canvas.translate(width/2 - DX, -DY*7);
			canvas.drawText("开盘", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最高", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("最低", x, y, paint);
			canvas.translate(0, DY);
			canvas.drawText("振幅", x, y, paint);
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

