package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.view.View;

import com.cssweb.android.base.QuoteFundGridActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.Utils;
/**
 * 基金查询
 * @author hoho
 *
 */
public class FundQuery extends QuoteFundGridActivity{
	private String fundCompanyId;
	private String fundType;
	private Integer level;
	private Integer managerLevel;
	private String [] cols ;
	private RequestParams requestParams;
	private int pageNum = 10;
	private Thread thread = null;
	private boolean firstComing = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_static_table);
		Bundle bundle = getIntent().getExtras();
		fundCompanyId = bundle.getString("fundCompanyId");
		fundType = bundle.getString("fundType");
		level = bundle.getInt("level");
		managerLevel = bundle.getInt("managerLevel");
		cols = getResources().getStringArray(R.array.fundquery_colsname);
		requestParams = new RequestParams();
		requestParams.setMarket(fundType);
		requestParams.setCode(fundCompanyId);
		requestParams.setLevel(level);
		requestParams.setManagerlevel(managerLevel);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.fundquery));
		String [] toolbarname = new String []{
				Global.TOOLBAR_REFRESH, "" ,"", "" ,""
		};
		initToolBar(toolbarname, "");
	}
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(final int type) {
		r = new Runnable(){
			public void run() {
				if(type ==1){
					quoteData = ConnService.execute(requestParams, 5);
					try {
						if (Utils.isHttpStatus(quoteData)){
							isNetworkError = 0;
						}else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						isNetworkError = -2;
						e.printStackTrace();
					}
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
		
	}
	/**
	 * 更新UI界面
	 * 
	 */
	@Override
	protected void handlerData() {
		try{
			if(isNetworkError<0&&firstComing) {
				firstComing = false;
				toast(R.string.load_data_error);
			}
			if(null ==quoteData ){
				listqueryfund.clear();
				listqueryfund.addAll(TradeUtil.fillListToNull3(0, pageNum ));
				refreshFundQueryUI(listqueryfund, cols, null);
				hiddenProgressToolBar();
				return;
			}
			JSONArray jArr = quoteData.getJSONArray("data");
			int len = jArr.length();
			listqueryfund.clear();
			for (int i=0 ; i< jArr.length(); i++){
				JSONArray jA = (JSONArray)jArr.get(i);
				String [] jarr = new String [2];
				jarr[0] = jA.getString(0);
				jarr[1] = jA.getString(1);
				listqueryfund.add(jarr);
			}
			if (len < pageNum ){
				listqueryfund.addAll(TradeUtil.fillListToNull3(len, pageNum ));
			}
			refreshFundQueryUI(listqueryfund, cols, null);
				
			hiddenProgressToolBar();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			//进度条消失
			hiddenProgressToolBar();
		}
	}
	
	@Override
	protected void toolBarClick(int tag, View v) {
		switch(tag){
		case 0:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	 @Override
	protected void onResume() {
		super.onResume();
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(FundQuery.this);
		rowHeight = CssSystem.getTableRowHeight(FundQuery.this);
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		handlerData();
		setToolBar();
		initPopupWindow();
	}
	/**
	 * 改变背景色
	 */
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	 }
	 /**
	  * 取消线程
	  */
	 protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	 }
			
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(r);
	}
	 
}
