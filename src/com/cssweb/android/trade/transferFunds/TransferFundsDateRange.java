package com.cssweb.android.trade.transferFunds;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
//import android.widget.TextView;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;
/**
 * 转帐查询　银行，日期，币种选择
 * @author chengfei
 * @version 1.0
 * @since 1.0
 */
public class TransferFundsDateRange extends CssBaseActivity {

	private final String DEBUG_TAG = "TransferFundsDateRange";
	private EditText endDate;
	private EditText startDate;
//	private TextView lblBZ;
	private Spinner currency;
	private ArrayAdapter<String> currencyAdapter;
	private String[] currencyLabels;
//	private String[] currencyVal;
	
//	private TextView lblbank;
	private Spinner bank;
	private ArrayAdapter<String> bankAdapter;
	private String[] bankLabels;
	private String[] bankVal;
	private String[] bankzh;
	
	//起始日期年、月、日
	private int startYear;
	private int startMonth;
	private int startDay;
	
	//终止日期年、月、日
	private int endYear;
	private int endMonth;
	private int endDay;
	
	private int fillContentId = -1;
	
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
//	        lblBZ = (TextView)findViewById(R.id.lblBZ);
	        currency = (Spinner) findViewById(R.id.CurrencyDate);
//	        lblbank = (TextView)findViewById(R.id.lblbank);
	        bank = (Spinner) findViewById(R.id.bankDate);
	        
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
	        
	        endYear = c.get(Calendar.YEAR);
	        endMonth = c.get(Calendar.MONTH);
	        endDay = c.get(Calendar.DAY_OF_MONTH);
	        
	        updateDisplay();   
	        
	        currencyLabels = getResources().getStringArray(R.array.bank_transfer_moneytype);
//			currencyVal = getResources().getStringArray(R.array.bank_transfer_moneytype_val);
			
			currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyLabels);
			currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			currency.setAdapter(currencyAdapter);
			
//        	lblBZ.setVisibility(View.VISIBLE);
//        	currency.setVisibility(View.VISIBLE);
//        	lblbank.setVisibility(View.VISIBLE);
//        	bank.setVisibility(View.VISIBLE);
	        initTitle(R.drawable.njzq_title_left_back, 0, "转帐查询条件选择");
	        
	        //showProgress();
	    }
	 @Override
		protected void init(int type) {
			r = new Runnable() {
				public void run() {
					try {
						String filedate = ActivityUtil.getPreference(TransferFundsDateRange.this, "openBanksDate", "");
						if (!(filedate).equals(DateTool.getToday())) { // 如果时间不匹配，重新到柜台获获取
							quoteData = TradeService.getBanks();
						} else {
							String jsonObject = CssIniFile.loadIni(TransferFundsDateRange.this, 8, "banks");
							if (null != jsonObject && !jsonObject.equals("")) {
								quoteData = new JSONObject(jsonObject);
							}
						}
						String res = TradeUtil.checkResult(quoteData);
						if(res==null){
							JSONArray jarr  = quoteData.getJSONArray("item");
							if(jarr.length() >=1){
								bankLabels = new String [jarr.length() - 1 ];
								bankVal = new String [jarr.length() - 1];
								bankzh = new String [jarr.length() - 1];
								for (int i = 0,size = jarr.length() -1 ; i<size ;i++){
									JSONObject jsonobject = (JSONObject) jarr.get(i);
									String bankCode = jsonobject.getString("FID_YHDM");
									String bankzhStr = jsonobject.getString("FID_YHZH");
									String bankName = TradeUtil.getBankName(bankCode);
									bankLabels[i] = bankName;
									bankVal[i] = bankCode;
									bankzh[i] = bankzhStr;
								}
							}else {
								bankLabels  = new String [0];
								bankVal = new String [0];
								bankzh = new String [0];
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
						CssLog.e(DEBUG_TAG, e.toString());
					}
					mHandler.sendEmptyMessage(0);
				}
			};
			new Thread(r).start();
		}
		
		@Override
		protected void handlerData() {
			try {
				String res = TradeUtil.checkResult(quoteData);
				if (res != null) {
					if (res.equals("-1")){
						toast("网络连接失败！");
						hiddenProgress();
						TransferFundsDateRange.this.finish();
					}
					else {
						toast(res);
					}
					hiddenProgress();
					return;
				}
				bankAdapter = new ArrayAdapter<String>(TransferFundsDateRange.this, android.R.layout.simple_spinner_item, bankLabels);
				bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				bank.setAdapter(bankAdapter);
			} catch (JSONException e) {
				e.printStackTrace();
				CssLog.e(DEBUG_TAG, e.toString());
			}
			hiddenProgress();
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
    		Intent localIntent = new Intent();
    		localIntent.putExtra("strdate", strDateStr);
	    	localIntent.putExtra("enddate", endDateStr);
//	    	localIntent.putExtra("moneyType", currencyVal[currency.getSelectedItemPosition()]);
//	    	localIntent.putExtra("bankType", bankVal[bank.getSelectedItemPosition()]);
//	    	localIntent.putExtra("bankZh", bankzh[bank.getSelectedItemPosition()]);
	    	localIntent.setClass(TransferFundsDateRange.this, TransferFundsQuery.class);
	    	TransferFundsDateRange.this.startActivity(localIntent);
	    }
	    
	    private void cancel() {
	    	finish();
//	    	if(menuid == Global.OPEN_GP) {
//	    		//FairyUI.switchToWnd(2, 1, "", "", DateRange.this);
//	    		
//			}else if(menuid == Global.OPEN_JJ){
//				//FairyUI.switchToWnd(6, 1, "", "", DateRange.this);
//				
//			}
	    }
	    
		protected Dialog onCreateDialog(int paramInt) {
			DateRangePicker d = new DateRangePicker();
			this.fillContentId = paramInt;
			if(fillContentId == 0)
				return new DatePickerDialog(TransferFundsDateRange.this, d, startYear, startMonth, startDay);
			else
				return new DatePickerDialog(TransferFundsDateRange.this, d, endYear, endMonth, endDay);
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
