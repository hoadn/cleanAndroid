/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)DateRange.java 上午10:50:06 2010-9-17
 */
package com.cssweb.android.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.stock.Bill;

/**
 * 历史查询日期选择
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class DateRange extends CssBaseActivity {
	private EditText endDate;
	private EditText startDate;
	private TextView lblBZ;
	private Spinner currency;
	private ArrayAdapter<String> currencyAdapter;
	private String[] currencyLabels;
	private String[] currencyVal;
	
	//起始日期年、月、日
	private int startYear;
	private int startMonth;
	private int startDay;
	
	//终止日期年、月、日
	private int endYear;
	private int endMonth;
	private int endDay;
	
	private int fillContentId = -1;
//	private int menuid;
	private String menudetail = "";
	  
    /** Called when the activity is first created. */
    @SuppressWarnings("static-access")
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zr_trade_daterange);
        
        String[] toolbarNames = {Global.TOOLBAR_QUEDING,"","","","", Global.TOOLBAR_BACK};
        initToolBar(toolbarNames, Global.BAR_TAG);
        setBtnStatus();
        
        startDate = (EditText)findViewById(R.id.StartDate);
        endDate = (EditText)findViewById(R.id.EndDate);
        lblBZ = (TextView)findViewById(R.id.lblBZ);
        currency = (Spinner) findViewById(R.id.CurrencyDate);
        startDate.setInputType(InputType.TYPE_NULL);
        startDate.setFocusable(false);
        endDate.setInputType(InputType.TYPE_NULL);
        endDate.setFocusable(false);
        startDate.setOnClickListener(new EditText.OnClickListener() {			
			public void onClick(View v) {
				onCreateDialog(0).show();
			}
		});
        
        endDate.setOnClickListener(new EditText.OnClickListener() {			
			public void onClick(View v) {
				onCreateDialog(1).show();
			}
		});
        
        Calendar c = Calendar.getInstance();
        SimpleDateFormat tempDate = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c1 = Calendar.getInstance();
		c1.add(c1.DATE, -30);
		Date temp_date = c1.getTime();
		String startDateStr = tempDate.format(temp_date);
		startYear = Integer.valueOf(startDateStr.substring(0,startDateStr.indexOf("-")));
		startMonth = Integer.valueOf(startDateStr.substring(startDateStr.indexOf("-")+1,startDateStr.lastIndexOf("-")))-1;
		startDay = Integer.valueOf(startDateStr.substring(startDateStr.lastIndexOf("-")+1,startDateStr.length()));
//        startYear = c.get(Calendar.YEAR);
//        if(c.get(Calendar.MONTH) == 0){
//        	startYear = startYear - 1;
//        	startMonth = 11;
//        }else{
//        	startMonth = c.get(Calendar.MONTH) - 1;
//        }
//        startDay = c.get(Calendar.DAY_OF_MONTH);
        
        endYear = c.get(Calendar.YEAR);
        endMonth = c.get(Calendar.MONTH);
        endDay = c.get(Calendar.DAY_OF_MONTH);
        
        updateDisplay();   
        
        currencyLabels = getResources().getStringArray(R.array.bank_transfer_moneytype);
		currencyVal = getResources().getStringArray(R.array.bank_transfer_moneytype_val);
		
		currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyLabels);
		currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currency.setAdapter(currencyAdapter);
		
		Bundle bundle = getIntent().getExtras();
//        menuid = bundle.getInt("menuid");
        menudetail = bundle.getString("menudetail");
        String titleName = "";
        if(Global.QUERY_STOCK_DZD.equals(menudetail)){
        	titleName = "资金流水查询";
        	lblBZ.setVisibility(View.VISIBLE);
        	currency.setVisibility(View.VISIBLE);
        }else {
        	titleName = "选择查询日期";
        	lblBZ.setVisibility(View.GONE);
        	currency.setVisibility(View.GONE);
		}
        initTitle(R.drawable.njzq_title_left_back, 0, titleName);
    }
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
    
    private void submit() {
    	//时间选择验证
    	String datemsg = DateTool.checkDate(startDate.getText().toString(),endDate.getText().toString());
    	if(datemsg != null || "".equals(datemsg)){
    		toast(datemsg);
    		return;
    	}
    	String strDateStr = startDate.getText().toString().replaceAll("-", "");
    	String endDateStr = endDate.getText().toString().replaceAll("-", "");
    	if(Global.QUERY_STOCK_DZD.equals(menudetail)){
    		Intent localIntent = new Intent();
    		localIntent.putExtra("strdate", strDateStr);
	    	localIntent.putExtra("enddate", endDateStr);
	    	localIntent.putExtra("moneyType", currencyVal[currency.getSelectedItemPosition()]);
	    	localIntent.setClass(DateRange.this, Bill.class);
	    	DateRange.this.startActivity(localIntent);
		}else{
			FairyUI.switchToWnd(menudetail, 1,strDateStr, endDateStr, DateRange.this);
		}
    }
    
    private void cancel() {
    	finish();
//    	if(menuid == Global.OPEN_GP) {
//    		//FairyUI.switchToWnd(2, 1, "", "", DateRange.this);
//    		
//		}else if(menuid == Global.OPEN_JJ){
//			//FairyUI.switchToWnd(6, 1, "", "", DateRange.this);
//			
//		}
    }
    
	protected Dialog onCreateDialog(int paramInt) {
		DateRangePicker d = new DateRangePicker();
		this.fillContentId = paramInt;
		if(fillContentId == 0)
			return new DatePickerDialog(DateRange.this, d, startYear, startMonth, startDay);
		else
			return new DatePickerDialog(DateRange.this, d, endYear, endMonth, endDay);
	}
    
	class DateRangePicker implements DatePickerDialog.OnDateSetListener {
		public void onDateSet(DatePicker paramDatePicker, int year,
				int monthOfYear, int dayOfMonth) {
			if(fillContentId == 0){
				startYear = year;
				startMonth = monthOfYear;
				startDay = dayOfMonth;
			}else if(fillContentId == 1){
				endYear = year;
				endMonth = monthOfYear;
				endDay = dayOfMonth;
			}
			updateDisplay();
		}
	}
	
	/* 设置显示日期时间的方法 */
	private void updateDisplay() {
		switch(fillContentId) {
			case 0:
				startDate.setText(new StringBuilder().append(startYear).append("-")
						.append(format(startMonth + 1)).append("-").append(format(startDay)));
				break;
			case 1:
				endDate.setText(new StringBuilder().append(endYear).append("-")
						.append(format(endMonth + 1)).append("-").append(format(endDay)));
				break;
			default:
				startDate.setText(new StringBuilder().append(startYear).append("-")
					.append(format(startMonth + 1)).append("-").append(format(startDay)));
				endDate.setText(new StringBuilder().append(endYear).append("-")
					.append(format(endMonth + 1)).append("-").append(format(endDay)));
		}
	}
	
	/* 日期时间显示两位数的方法 */
	private String format(int x) {
		String s = "" + x;
		if (s.length() == 1)
			s = "0" + s;
		return s;
	}
	@Override
	protected void toolBarClick(int tag, View v) {
		 switch(tag) {
			case 0:
				submit();
				break;
			case 5: 
				cancel();
				break;
		 }
	}
	@Override
	protected void onResume() {
		//initView("", "", "");
		super.onResume();
		initPopupWindow();
	}
	private void setBtnStatus(){
		setToolBar(1, false, R.color.zr_dimgray);
		setToolBar(2, false, R.color.zr_dimgray);
		setToolBar(3, false, R.color.zr_dimgray);
		setToolBar(4, false, R.color.zr_dimgray);
	}
}
