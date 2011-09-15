/*
 * @(#)version 1.0 Mar 21, 2009
 * @author hujun
 * DateTool.java
 * Copyright 2009 CSS WEB Microsystems, Inc. All rights reserved.
 * CSS WEB ROOM PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package com.cssweb.android.common;

import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.util.Log;

public class DateTool {
	
	public static String getDateStringByPattern() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyyMMddHHmmss");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getLocalDate() {
		SimpleDateFormat sf = new SimpleDateFormat("MMdd0930");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getLoadAllStockEndTime() {
		SimpleDateFormat sf = new SimpleDateFormat("MMdd0922");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getLoadAllStockStarTime() {
		SimpleDateFormat sf = new SimpleDateFormat("MMdd0920");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getLocalCurrentDate() {
		SimpleDateFormat sf = new SimpleDateFormat("MMddHHmm");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getLocalDate2() {
		SimpleDateFormat sf = new SimpleDateFormat("MMdd1500");
		String str = sf.format(new Date());
		return str;
	}
	
	public static String getToday() {
		SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		String str = sf.format(new Date());
		return str;
	}
	
    public static int getYMD(){   
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");   
        return Integer.parseInt(simpledateformat.format(new Date()));   
    }
	
    public static String getHHMMSS(){   
        SimpleDateFormat simpledateformat = new SimpleDateFormat("HHmmss");   
        return simpledateformat.format(new Date());   
    }
    
    public static int getWeekDay() {
    	int day;
    	GregorianCalendar g=new GregorianCalendar();
    	day=(int)g.get(Calendar.DAY_OF_WEEK);
        return day;
    }
    
    public static int getHour() {
    	int day;
    	GregorianCalendar g=new GregorianCalendar();
    	day=(int)g.get(Calendar.HOUR_OF_DAY);
        return day;
    }
    
    public static int getCurrentYear() {
    	int day;
    	GregorianCalendar g=new GregorianCalendar();
    	day=(int)g.get(Calendar.YEAR);
        return day;
    }
    
    public static int getCurrentMonth() {
    	int day;
    	GregorianCalendar g=new GregorianCalendar();
    	day=(int)g.get(Calendar.MONTH);
        return day;
    }
    
    public static int getCurrentWeek() {
    	int day;
    	GregorianCalendar g=new GregorianCalendar();
    	day=(int)g.get(Calendar.WEEK_OF_YEAR);
        return day;
    }
    
    /**
     * 早上小于9：40大于9：20的时候
     * @return
     */
    public static boolean isLoadStockTime() {
    	int time = Integer.parseInt(getLocalCurrentDate());
    	if(time<=Integer.parseInt(getLoadAllStockEndTime()) &&
    			time>=Integer.parseInt(getLoadAllStockStarTime())) {//小于9：40大于9：20
    		return true;
    	}
    	return false;
    }
    
    /**
     * 早上小于0920的时候
     * @return
     */
    public static boolean isNotStartLoadStockTime() {
    	int time = Integer.parseInt(getLocalCurrentDate());
    	if(time<Integer.parseInt(getLoadAllStockStarTime())) {//小于9：20
    		return true;
    	}
    	return false;
    }
    
    public static boolean checkTodayIsLoadOrnot(String timestr) {
    	int time = Integer.parseInt(timestr.substring(0, 8));
    	if(time == getYMD())
    		return true;
    	return false;
    }
    
    /**
     * 由于港股延时15分钟，所以规定每天早上9：50开始读取
     * @return
     */
    public static boolean isLoadHKStockTime() {
//    	int hour = Integer.parseInt(getHHMMSS().substring(0, 2));
//    	int minute = Integer.parseInt(getHHMMSS().substring(2, 4));
//    	if(hour==9&&minute>50) {
//    		return true;
//    	}
//    	else if(hour>9) {
//    		return true;
//    	}
    	int time = Integer.parseInt(getLocalCurrentDate());
    	if(time<=Integer.parseInt(getLoadAllStockEndTime())) {//小于9：40
    		return true;
    	}
    	return false;
    }
    
    public static Date getTime(String date){
        SimpleDateFormat simpledateformat = new SimpleDateFormat("yyyyMMdd");   
        Date d = simpledateformat.parse(date,new ParsePosition(0));
        return d;   
    }
    
    public static boolean isSameWeekMonthYear(String date, String period) {
    	Calendar cal = Calendar.getInstance() ;
    	cal.setTime(getTime(date)) ;//放入你的时间对象
    	int week = cal.get(Calendar.WEEK_OF_YEAR);
    	int month = cal.get(Calendar.MONTH);
    	int year = cal.get(Calendar.YEAR);
    	if("week".equals(period)) {
    		if(week==getCurrentWeek()&&
    				year==getCurrentYear())
    			return true;
    	}
    	else if("month".equals(period)) {
    		if(month==getCurrentMonth()&&
    				year==getCurrentYear())
    			return true;
    	}
    	else if("year".equals(period)) {
    		if(year==getCurrentYear())
    			return true;
    	}
    	return false;
    }
    
    public static boolean isLoadStockTime2() {
    	int time1 = Integer.parseInt(getLocalCurrentDate());
    	int time2 = Integer.parseInt(getLocalDate());
    	if(time1<time2) {
    		return true;
    	}
    	return false;
    }
    
    /**
     * 正式发布的时候需要放开
     * @return
     */
    public static boolean isRefreshTime() {
    	int hour = getHour();
    	int day = getWeekDay();
    	if(day==1||day==7) 
    		return false;
    	if(hour<9||hour>15)
    		return false;
    	return true;
    }
    
    public static long getLongTime(){
    	return new Date().getTime();
    }
    
  //时间选择验证
    public static String checkDate(String startDate,String endDate){
	    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
		Long endlong = null;
		Long startlong = null;
		try {
			endlong = (sf.parse(endDate)).getTime();
			startlong = (sf.parse(startDate)).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		Long day = (endlong - startlong)/1000/60/60/24;
		Log.i("查询间隔时间==", day.intValue()+"");
		if(day.intValue()<0){
			return "起始时间　不能超过　终止时间";
		}
		if(!isStartTime()){//判断当前时间　属于开市，还是闭市时间
			if(day.intValue()>360){
				return "闭市时间,查询间隔不能大于360天";
			}
		}else{//开市
			if(day.intValue()>30){
				return "开市时间,查询间隔不能大于30天";
			}
		}
		return null;
    }
  //判断是否是开市时间 上午9：30－11：30； 下午1：00－3：00 
    public static boolean isStartTime() {//int day,int hour,int minute
    	int day = getWeekDay();
    	int hour = getHour();
    	int minute = Integer.parseInt(getHHMMSS().substring(2, 4));
    	Log.i("星期=="+(day-1)+"   小时==", hour+"   分钟=="+minute);
    	System.out.println("星期=="+day+"   时间=="+ hour+"   分钟=="+minute);
    	if(day==2||day==3||day==4||day==5||day==6){
	    	if(hour == 10 || hour == 14 || hour == 13)
	    		return true;
	    	if(hour == 9 && minute  > 30)
	    		return true;
	    	if(hour == 11 && minute  < 30)
	    		return true;
	    	if(hour == 15 && minute  < 1)
	    		return true;
    	}else{
    		return false;
    	}
    	return false;
    }
}
