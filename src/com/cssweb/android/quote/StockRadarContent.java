package com.cssweb.android.quote;

import android.os.Bundle;
import android.os.HandlerThread;
import android.widget.TextView;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
/**
 * 信息地雷 内容
 * @author hoho
 *
 */
public class StockRadarContent extends CssBaseActivity{
	private String stockcode ;
    private String exchange;
    private String datecontent;
    private String row;
    private TextView textViewContent ;
    private String content ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
	    mHandlerThread.start();
	    mHandler = new MessageHandler(mHandlerThread.getLooper());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_hotnews_content);
		Bundle bundle = getIntent().getExtras();
		stockcode = bundle.getString("stockcode");
        exchange = bundle.getString("exchange").toLowerCase();
        datecontent = bundle.getString("datecontent");
        row = bundle.getString("row");
        textViewContent = (TextView) findViewById(R.id.dilei_content);
        initTitle(R.drawable.njzq_title_left_back, 0, "文章阅读");
        showProgress();
	}
	

    protected void init(final int type) {
    	mHandler.removeCallbacks(r);
        r = new Runnable() {
            public void run() {
            	 content  = ConnService.getRedarContentNew(exchange, stockcode , datecontent , Integer.valueOf(row) );
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
 					textViewContent.setText(content);
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
	
    @Override
	protected void onResume() {
		super.onResume();
		initPopupWindow();
	}

}
