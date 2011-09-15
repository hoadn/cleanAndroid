/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)GetF10Content.java 下午10:27:48 2010-11-10
 */
package com.cssweb.android.quote;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;

/**
 * F10详细内容
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class GetF10Content extends CssBaseActivity {
	
	private TextView f10Text;
	
	private String exchange;
	private String stockcode;
//	private String stockname;
	private String desc;
	private String descname;
	
	private String f10content;
	
	private String paramType =null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		this.activityKind = 9;
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
//		this.stockname = bundle.getString("stockname");
		this.desc = String.valueOf(bundle.getInt("desc"));
		this.descname = bundle.getString("descname");
		this.paramType = bundle.getString("paramType");
		
		//Log.i("!!!!!!!!!!", desc+"@@");
		setContentView(R.layout.zr_info_content);

		initTitle(R.drawable.njzq_title_left_back, 0, "Ｆ１０内容");

		setTitleText(descname);
		
		f10Text = (TextView) findViewById(R.id.zr_ic_text);
		f10Text.setText("数据加载中...");
        
        init(1);
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	protected void init(final int type) {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				if(null !=paramType && paramType.equals("stockfund")){
					f10content = ConnService.getStockFundF10(exchange, stockcode, desc);
				}else{
					f10content = ConnService.getF10(exchange, stockcode, desc);
				}
				if(f10content==null||f10content.equals(""))
					return Boolean.FALSE;
				return Boolean.TRUE;
			}
    		
			protected void onPostExecute(Boolean result) {
				if (result != Boolean.TRUE) {
					f10Text.setText("网络异常");
	            }
				else {
					Log.i("########f10########", f10content+">>>>");
					f10Text.setText(f10content);
				}
			}
    	}.execute();
	}
	
	@Override
	protected void onDestroy() {
		mHandler.removeCallbacks(r);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
        initPopupWindow();
	}
	
    @Override
    protected void onStop() {
        super.onStop();
    }
}