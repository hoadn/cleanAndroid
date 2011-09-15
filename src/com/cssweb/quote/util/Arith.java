package com.cssweb.quote.util;

import java.math.BigDecimal;

/**
 * 由于Java的简单类型不能够精确的对浮点数进行运算，这个工具类提供精确的浮点数运算，包括加减乘除和四舍五入。
 */
public class Arith {
	// 默认除法运算精度
	private static final int DEF_DIV_SCALE = 10;

	// 这个类不能实例化
	private Arith() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param v1
	 *            被加数
	 * @param v2
	 *            加数
	 * @return 两个参数的和
	 */
	public static double add(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.add(b2).doubleValue();
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param v1
	 *            被减数
	 * @param v2
	 *            减数
	 * @return 两个参数的差
	 */
	public static double sub(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.subtract(b2).doubleValue();
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param v1
	 *            被乘数
	 * @param v2
	 *            乘数
	 * @return 两个参数的积
	 */
	public static double mul(double v1, double v2) {
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.multiply(b2).doubleValue();
	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}

	/**
	 * 提供精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException(
					"The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	/**
	 * 返回最大值
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static int max(int i1, int i2) {
		return Math.max(i1, i2);
	}
	
	public static int max(int i1, int i2, int i3) {
		return Math.max(max(i1, i2), i3);
	}
	
	public static int max(int i1, int i2, int i3, int i4) {
		return Math.max(max(i1, i2, i3), i4);
	}
	
	public static float max(float i1, float i2) {
		return Math.max(i1, i2);
	}
	
	public static float max(float i1, float i2, float i3) {
		return Math.max(max(i1, i2), i3);
	}
	
	public static float max(float i1, float i2, float i3, float i4) {
		return Math.max(max(i1, i2, i3), i4);
	}
	
	public static double max(double i1, double i2) {
		return Math.max(i1, i2);
	}
	
	public static double max(double i1, double i2, double i3) {
		return Math.max(max(i1, i2), i3);
	}
	
	public static double max(double i1, double i2, double i3, double i4) {
		return Math.max(max(i1, i2, i3), i4);
	}
	
	public static double max(double i1, double i2, double i3, double i4, double i5) {
		return Math.max(max(i1, i2, i3, i4), i5);
	}
	
	public static long max(long i1, long i2) {
		return Math.max(i1, i2);
	}
	
	public static long max(long i1, long i2, long i3) {
		return Math.max(max(i1, i2), i3);
	}
	
	/**
	 * 返回最小值
	 * @param i1
	 * @param i2
	 * @return
	 */
	public static int min(int i1, int i2) {
		return Math.min(i1, i2);
	}
	
	public static int min(int i1, int i2, int i3) {
		return Math.min(min(i1, i2), i3);
	}
	
	public static int min(int i1, int i2, int i3, int i4) {
		return Math.min(min(i1, i2, i3), i3);
	}
	
	public static float min(float i1, float i2) {
		return Math.min(i1, i2);
	}
	
	public static float min(float i1, float i2, float i3) {
		return Math.min(min(i1, i2), i3);
	}
	
	public static float min(float i1, float i2, float i3, float i4) {
		return Math.min(min(i1, i2, i3), i3);
	}
	
	public static double min(double i1, double i2) {
		return Math.min(i1, i2);
	}
	
	public static double min(double i1, double i2, double i3) {
		return Math.min(min(i1, i2), i3);
	}
	
	public static double min(double i1, double i2, double i3, double i4) {
		return Math.min(min(i1, i2, i3), i3);
	}
	
	public static long min(long i1, long i2) {
		return Math.min(i1, i2);
	}
	
	public static long min(long i1, long i2, long i3) {
		return Math.min(min(i1, i2), i3);
	}
}
