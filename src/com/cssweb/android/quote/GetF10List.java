/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)GetF10.java 下午09:45:45 2010-11-10
 */
package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;

/**
 * F10列表
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class GetF10List extends CssBaseActivity {
	
	private ListView lv;
	
	private String exchange;
	private String stockcode;
	private String stockname;
	
	private String f10list;
	private String[] f10;
	
	private String paramType=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getIntent().getExtras();
		this.exchange = bundle.getString("exchange");
		this.stockcode = bundle.getString("stockcode");
		this.stockname = bundle.getString("stockname");
		this.paramType = bundle.getString("paramtype");
		setContentView(R.layout.zr_info_list);

		initTitle(R.drawable.njzq_title_left_back, 0, "");

		if(null !=paramType && paramType.equals("stockfund")){
			setTitleText("基金资讯");
		}
		else {
			setTitleText("Ｆ１０资讯");
		}
		lv = (ListView) findViewById(R.id.zr_il_listview);
		
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
					f10list = ConnService.getStockFundF10(exchange, stockcode, null);
				}else{
					f10list = ConnService.getF10(exchange, stockcode, null);
				}
				if(f10list==null||f10list.equals(""))
					return Boolean.FALSE;
				return Boolean.TRUE;
			}
    		
			protected void onPostExecute(Boolean result) {
				if (result != Boolean.TRUE) {
					
	            }
				else {
					f10 = f10list.split(";");
		        	ArrayList<HashMap<String, Object>> f10map = new ArrayList<HashMap<String, Object>>();
		        	for (int i = 0; i < f10.length; i++) { 
		        		HashMap<String, Object> f10item = new HashMap<String, Object>(); 
		        		f10item.put("fname", f10[i]); 
		        		f10item.put("img", R.drawable.arrow_tiem); 
		        		f10map.add(f10item);
		        	} 

		        	SimpleAdapter simpleItem = new SimpleAdapter(GetF10List.this, 
		        			f10map,// 数据来源 
		        			R.layout.zr_info_list_item,//每一个user xml 相当ListView的一个组件 
		        			new String[] { "fname", "img" }, 
		        			// 分别对应view 的id 
		        			new int[] { R.id.zr_jyil_item_text, R.id.zr_jyil_item_arrow }); 
		        	lv.setAdapter(simpleItem);
		        	lv.setOnItemClickListener(new OnItemClickListener() {

						public void onItemClick(AdapterView<?> arg0, View arg1,
								int arg2, long arg3) {
							int id = arg2+1;
							Intent localIntent = new Intent();	    
							localIntent.putExtra("exchange", exchange);
							localIntent.putExtra("stockcode", stockcode);
							localIntent.putExtra("stockname", stockname);
							localIntent.putExtra("desc", id);
							if(null !=paramType && paramType.equals("stockfund")){
								localIntent.putExtra("paramType", "stockfund");
							}
							localIntent.putExtra("descname", f10[arg2]);
							localIntent.setClass(GetF10List.this, GetF10Content.class);
							GetF10List.this.startActivity(localIntent);
						}
						
					});
				}
			}
    	}.execute();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
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
