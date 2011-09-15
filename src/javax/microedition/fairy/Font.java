/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Font.java 下午01:10:45 2010-10-19
 */
package javax.microedition.fairy;

import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;

/**
 * 字体定义
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class Font {
	public static final int mFontSize = 16;
	
	public static int getFontHeight(float fontSize) {
		Paint paint = new Paint();
		paint.setTextSize(fontSize);
		FontMetrics fm = paint.getFontMetrics();
		return (int) Math.ceil(fm.descent - fm.ascent);
	}
}
