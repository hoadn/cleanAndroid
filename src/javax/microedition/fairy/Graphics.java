/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Graphics.java 下午03:24:07 2010-8-26
 */
package javax.microedition.fairy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * 画图
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Graphics {

	public static void fillRect(Canvas canvas, Rect rect, Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
	}

	public static void drawRect(Canvas canvas, Rect rect, Paint paint) {
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(rect.left, rect.top, rect.right, rect.bottom, paint);
	}

	public static void SETAEERECT(Rect rect, int x, int y, int w, int h) {
		rect.left = x;
		rect.top = y;
		rect.right = x + w;
		rect.bottom = y + h;
	}

	public static void fillRect(Canvas canvas, float x, float y, float w, float h,
			Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		canvas.drawRect(x, y, x + w, y + h, paint);
	}

	public static void drawRect(Canvas canvas, float x, float y, float w, float h,
			Paint paint) {
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(x, y, x + w, y + h, paint);
	}
	
	public static void fillRect(Canvas canvas, double x, double y, double w, double h,
			Paint paint) {
		paint.setStyle(Paint.Style.FILL);
		float startX = (float) x;
		float startY = (float) y;
		float stopX = (float) (x + w);
		float stopY = (float) (y + h);
		canvas.drawRect(startX, startY, stopX, stopY, paint);
	}

	public static void drawRect(Canvas canvas, double x, double y, double w, double h,
			Paint paint) {
		paint.setStyle(Paint.Style.STROKE);
		float startX = (float) x;
		float startY = (float) y;
		float stopX = (float) (x + w);
		float stopY = (float) (y + h);
		canvas.drawRect(startX, startY, stopX, stopY, paint);
	}
	
	public static void drawLine(Canvas canvas, double x, double y, double w, double h,
			Paint paint) {
		float startX = (float) x;
		float startY = (float) y;
		float stopX = (float) w;
		float stopY = (float) h;
		canvas.drawLine(startX, startY, stopX, stopY, paint);
	}

	/*------------------------------------
	 * 绘制图片
	 *
	 * @param		x 屏幕上的x坐标	
	 * @param		y 屏幕上的y坐标
	 * @param		w 要绘制的图片的宽度	
	 * @param		h 要绘制的图片的高度
	 * @param		bx图片上的x坐标
	 * @param		by图片上的y坐标
	 *
	 * @return		null
	 ------------------------------------*/
	public static void drawImage(Canvas canvas, Bitmap blt, int x, int y,
			int w, int h, int bx, int by) {
		Rect src = new Rect();// 图片
		Rect dst = new Rect();// 屏幕
		src.left = bx;
		src.top = by;
		src.right = bx + w;
		src.bottom = by + h;
		dst.left = x;
		dst.top = y;
		dst.right = x + w;
		dst.bottom = y + h;
		canvas.drawBitmap(blt, src, dst, null);

		src = null;
		dst = null;
	}

	public static void drawImage(Canvas canvas, Bitmap bitmap, float x, float y) {
		canvas.drawBitmap(bitmap, x, y, null);
	}

	public static void drawString(Canvas canvas, String str, float x, float y,
			Paint paint) {
		canvas.drawText(str, x, y + paint.getTextSize(), paint);
	}

	public static void drawDashline2(Canvas canvas, int param2, int param3, int param4, int param5, Paint paint) {
		int _loc_6;
		int _loc_7;
		int _loc_8;
		int _loc_9;
		_loc_6 = param2;
		_loc_7 = param3;
		
		while (_loc_6 < param4 || _loc_7 < param5) {
			if (_loc_6 > param4) {
				_loc_6 = param4;
			}// end if
			if (_loc_7 > param5) {
				_loc_7 = param5;
			}// end if
			_loc_8 = _loc_6 + 2;
			_loc_9 = _loc_7 + 2;
			if (_loc_8 > param4) {
				_loc_8 = param4;
			}// end if
			if (_loc_9 > param5) {
				_loc_9 = param5;
			}// end if
			canvas.drawLine(_loc_6, _loc_7, _loc_8, _loc_9, paint);
			_loc_6 = _loc_8 + 3;
			_loc_7 = _loc_9 + 3;
		}
	}
	
	public static void drawDashline(Canvas canvas, float param2, float param3, float param4, float param5, Paint paint)
    {                                                                                                                    
		float _loc_6;                                                                                               
		float _loc_7;                                                                                               
		float _loc_8;                                                                                                    
		float _loc_9;                                                                                                    
        _loc_6 = param2;                                                                                                 
        _loc_7 = param3;   
        while (_loc_6 < param4 || _loc_7 < param5)                                                                       
        {                                                                                                                
            // label                                                                                                     
            if (_loc_6 > param4)                                                                                         
            {                                                                                                            
                _loc_6 = param4;                                                                                         
            }// end if                                                                                                   
            if (_loc_7 > param5)                                                                                         
            {                                                                                                            
                _loc_7 = param5;                                                                                         
            }// end if                                                                                              
            _loc_8 = _loc_6 + 2;                                                                                         
            _loc_9 = _loc_7 + 2;                                                                                         
            if (_loc_8 > param4)                                                                                         
            {                                                                                                            
                _loc_8 = param4;                                                                                         
            }// end if                                                                                                   
            if (_loc_9 > param5)                                                                                         
            {                                                                                                            
                _loc_9 = param5;                                                                                         
            }// end if                   
			canvas.drawLine(_loc_6, _loc_7, _loc_8, _loc_9, paint);
            _loc_6 = _loc_8 + 1;                                                                                         
            _loc_7 = _loc_9 + 1;                                                                                         
        }                                                                                                       
    }    
}
