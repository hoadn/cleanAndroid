/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)ConstData.java 下午04:40:23 2010-7-16
 */
package com.cssweb.android.common;


/**
 * 下拉框用到的数据
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class ConstData {
	
	public static final String [] fundType = {
		"全部",
		"股票型基金",
		"债券型基金",
		"货币型基金",
		"混合型基金",
		"其他"
	};
	
	public static final String [] fundTypeId ={
		"-1",
		"51",
		"52",
		"54",
		"53",
		"55"
	};
	
	public static final String [] jingzhi = {
		"全部",
		"1元以下",
		"1元－1.5元",
		"2元以上"
	};
	
	/**
	 * 净值 传值是用区间的形式,注意传值规则
	 */
	public static final String [] jingzhiId1 = {
		"-1",
		"-1",
		"1",
		"2"
	};
	public static final String [] jingzhiId2 = {
		"-1",
		"1",
		"1.5",
		"-1"
	};
	
	public static final String [] jingzhiAdd = {
		"全部",
		"-10%以下",
		"-10%--5%",
		"-5%-0",
		"0-5%",
		"5-10%",
		"10%以上"
	};
	
	/**
	 * 本年净值增长率  传值是用区间的形式,注意传值规则
	 */
	public static final String [] jingzhiAddId1 = {
		"-100",
		"-100",
		"-10",
		"-5",
		 "0",
		 "5",
		 "10"
	};
	public static final String [] jingzhiAddId2 = {
		"-100",
		"-10",
		"-5",
		 "0",
		 "5",
		 "10",
		"-100"
	};
	
	public static final String [] fundLevel = {
		"全部",
		"一星",
		"二星",
		"三星",
		"四星",
		"五星"
	};
	public static final int [] fundLevelId = {
		-1,
		1,
		2,
		3,
		4,
		5
	};
	public static final String [] fundManagerLevel = {
		"全部",
		"一星",
		"二星",
		"三星",
		"四星",
		"五星"
	};
	public static final int [] fundMangerLevelId = {
		-1,
		1,
		2,
		3,
		4,
		5
	};
	
}
