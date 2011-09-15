/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Ssjp.java 下午10:11:16 2010-12-30
 */
package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.adapter.StockRadarAdapter;

/**
 * 信息地雷 标题
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class StockRadar extends CssBaseActivity implements ListView.OnScrollListener {
    private ListView listView;
    private StockRadarAdapter simpleItem ;
    private ArrayList<HashMap<String, String>> arrayList = new ArrayList<HashMap<String,String>>();
    private String stockcode ;
    private String exchange;
    private String dileiDate;   //地雷 日期  
    private String dileiTitle;  //地雷标题
    private int currentDateIndex;         //当前是哪一个日期索引
    private boolean isScrolling = false;  //是否滑动滚屏
    private boolean isDiLeiDateFirst =true ;  //读取一次地雷信息
    private int lastItem = 0;
    private int screenNum = 9;			//一屏数据行数
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
    	mHandlerThread.start();
    	mHandler = new MessageHandler(mHandlerThread.getLooper());
        setContentView(R.layout.zr_hotnews_list);
        initTitle(R.drawable.njzq_title_left_back, 0, "信息地雷");
        Bundle bundle = getIntent().getExtras();
        stockcode = bundle.getString("stockcode");
        exchange = bundle.getString("exchange").toLowerCase();
        listView = (ListView) findViewById(R.id.zr_il_listview);
        listView.setOnScrollListener(this);
        showProgress();
        listView.setOnItemClickListener(itemClickListener);
    }
    private OnItemClickListener itemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			RelativeLayout relativeLayout = (RelativeLayout) view ;
			TextView rowView = (TextView) relativeLayout.findViewById(R.id.row);
			TextView dateView = (TextView) relativeLayout.findViewById(R.id.zr_hotjf_date);
			String datecontent = (String) dateView.getText();
			String row = (String) rowView.getText();
			//Log.i("111111111111111111", "2222222222222222222 datecontent: " + datecontent +" row:" + row);
			Bundle bundle = new Bundle();
			bundle.putString("datecontent", datecontent);
			bundle.putString("row", row);
			bundle.putString("stockcode", stockcode);
			bundle.putString("exchange", exchange);
			Intent intent = new Intent();
			intent.putExtras(bundle);
			intent.setClass(StockRadar.this, StockRadarContent.class);
			startActivity(intent);
		}
	};

    protected void init(final int type) {
    	mHandler.removeCallbacks(r);
        r = new Runnable() {
            public void run() {
            	if (isDiLeiDateFirst){			//第一次读取日期数据,异步加载就不再读取此数据
            		isDiLeiDateFirst = false;
            		dileiDate = ConnService.getRedarNew(exchange, stockcode);   //请求日期
                	if (null !=dileiDate){
    					String [] dileiDates = dileiDate.split(",");
    					currentDateIndex = dileiDates.length;
    				}
            	}
            	if (!isScrolling){      //第一次进来,没有滚动的时候
            		while (arrayList.size() < screenNum ){      //控制一屏显示9条,填满屏幕
                		if (null !=dileiDate){			//得到日期和数据条数,然后请求内容
                			if (currentDateIndex > 0){
                				String [] dileiDates = dileiDate.split(","); 
            					String dateAndNum = dileiDates[currentDateIndex - 1];
            					String [] dateAndNums = dateAndNum.split(":");
            					String date = dateAndNums[0];
            					//String num = dateAndNums[1];	
            					String oldDate = date.replaceAll("\"", "").replaceAll(" ", "");
            					dileiTitle  = ConnService.getRedarTitleNew(exchange, stockcode , oldDate  );  //请求标题
            					if (null !=dileiTitle){
            						String [] dileiTitles = dileiTitle.split("\\|");
            						for (int i =0 ; i< dileiTitles.length ; i++){
            							HashMap<String, String> hashMap = new HashMap<String, String>();
            							hashMap.put("title", dileiTitles[i]);
            							hashMap.put("date", oldDate);
            							hashMap.put("row", String.valueOf(i));
            							arrayList.add(hashMap);
            						}
            					}
            					if (currentDateIndex ==1 ){    //控制没有数据跳出循环
            						break;
            					}
            					if (arrayList.size() < screenNum ){
            						currentDateIndex -- ;
            					}
            				}
                		}
                	}
            	}else{				//滚动加载
                		if (null !=dileiDate){			//得到日期和数据条数,然后请求内容
                			Log.i("2222222222222222222222 ", "444444444444444444444444444444444444 currentDateIndex: " + currentDateIndex);
        					String [] dileiDates = dileiDate.split(",");  
        					String dateAndNum = dileiDates[currentDateIndex - 1];
        					String [] dateAndNums = dateAndNum.split(":");
        					String date = dateAndNums[0];
        					//String num = dateAndNums[1];	
        					
        					String oldDate = date.replaceAll("\"", "").replaceAll(" ", "");
        					dileiTitle  = ConnService.getRedarTitleNew(exchange, stockcode , oldDate  );  //请求标题
        					if (null !=dileiTitle){
        						String [] dileiTitles = dileiTitle.split("\\|");
        						for (int i =0 ; i< dileiTitles.length ; i++){
        							HashMap<String, String> hashMap = new HashMap<String, String>();
        							hashMap.put("title", dileiTitles[i]);   //标题
        							hashMap.put("date", oldDate);			//日期
        							hashMap.put("row", String.valueOf(i));					//第几个标题	
        							arrayList.add(hashMap);
        						}
        					}
        				}
            	}
            	
            	
                mHandler.sendEmptyMessage(0);
            }
        };
        mHandler.post(r);
    }
    
    
    @Override
    protected void handlerData() {
         Runnable r = new Runnable(){
 			public void run() {
 				try{
 					simpleItem = new StockRadarAdapter(StockRadar.this, arrayList, R.layout.zr_hotnews_item,
 	 		         		new String[] { "title", "date" , "row" }, new int[] { R.id.zr_hotjf_title, R.id.zr_hotjf_date });
 					listView.setAdapter(simpleItem);
 					listView.setSelection(simpleItem.getCount());
 				}catch(Exception e ){
 					e.printStackTrace();
 				}finally{
 					//进度条消失
 					hiddenProgress();
 				}
 			}
 		};
 		runOnUiThread(r);
    }
    
    
    public void onUpdate() {
    	currentDateIndex--;
    	isScrolling = true;
    	
    	if (currentDateIndex -1 >=0){
    		showProgress();
		}else if (null !=dileiTitle){
			//Toast.makeText(this, "已经是最后一页", Toast.LENGTH_SHORT).show();
			showToast2();
		}
    }
    
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        switch (scrollState) {
        case OnScrollListener.SCROLL_STATE_IDLE:
        	 if (lastItem == simpleItem.getCount()  ) { 
        		 onUpdate();
        	 }
            break;
        case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
            break;
        case OnScrollListener.SCROLL_STATE_FLING:
            break;
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
    	lastItem = firstVisibleItem + visibleItemCount ;
    }
    
    @Override
	protected void onResume() {
		super.onResume();
		initPopupWindow();
	}

}
