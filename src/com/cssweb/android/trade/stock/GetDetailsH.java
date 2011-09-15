package com.cssweb.android.trade.stock;

import org.json.JSONArray;
import org.json.JSONException;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.adapter.DetailsAdapter;

/**
 * 获取详细信息
 * @author wangsheng
 *
 */
public class GetDetailsH extends TradeQueryBase {
	private static final String DEBUG_TAG = "GetDetails";
	
	private String[] names;
	private String[] namesIndex;
	private JSONArray allRecords;
	private int currentRowId;
	private int currentPageId;
	private ListView listView;
	private DetailsAdapter adapter;
	private Thread thread = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.zr_trade_details_list);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "详细信息");
		
		String[] toolbarNames = {Global.TOOLBAR_LAST,"","","","", Global.TOOLBAR_NEXT};
		initToolBar(toolbarNames, Global.BAR_TAG);
		setToolBar(1, false, R.color.zr_newlightgray);
		setBtnStatus();
		
		Bundle bundle = getIntent().getExtras();
		names = (String[])bundle.get("colsName");
		namesIndex = (String[])bundle.get("colsIndex");
		currentRowId = bundle.getInt("currentSelectedId");
		currentPageId = bundle.getInt("currentPageId");
		String strAllRecords = bundle.getString("allRecords");
		try {
			allRecords = new JSONArray(strAllRecords);
		} catch (JSONException e) {
			CssLog.e(DEBUG_TAG, e.toString());
		}
		
		listView = (ListView)findViewById(R.id.zr_rt_listview);
		
		setToolBar();
	}
	
	@Override
	protected void init(int type) {
		r = new Runnable() {
			public void run() {
				try {
					quoteData = allRecords.getJSONObject(currentRowId);
				} catch (JSONException e) {
					CssLog.e(DEBUG_TAG, e.toString());
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	
	@Override
	protected void handlerData() {
		String[] values = new String[namesIndex.length];
		for(int i=0,size=namesIndex.length; i<size; i++){
			try {
				values[i] = quoteData.getString(namesIndex[i]);
			} catch (JSONException e) {
				CssLog.e(DEBUG_TAG, e.toString());
			}
		}
		adapter = new DetailsAdapter(this, names, values, R.layout.zr_trade_details_list_items);
		listView.setAdapter(adapter);
		
		hiddenProgressToolBar();
		if(currentRowId == currentPageId*pageNum){
			setToolBar(0, false, R.color.zr_dimgray);
		}else{
			setToolBar(0, true, R.color.zr_white);
		}
		if (allRecords.length()-1 < currentPageId*pageNum + pageNum - 1) {
			if(currentRowId == allRecords.length()-1){
	        	setToolBar(5, false, R.color.zr_dimgray);
	        }else {
	        	setToolBar(5, true, R.color.zr_white);
			}
		}else {
			if(currentRowId == currentPageId*pageNum + pageNum - 1){
	        	setToolBar(5, false, R.color.zr_dimgray);
	        }else {
	        	setToolBar(5, true, R.color.zr_white);
			}
		}
        
	}
	
	/**
	 * 上一条
	 */
	private void previousItem() {
		if(currentRowId == 0){
			//toast("已是第一条记录！");
		}else{
			currentRowId --;
			setToolBar();
		}
	}
	
	/**
	 * 下一条
	 */
	private void nextItem() {
		if(currentRowId == allRecords.length()-1){
			//toast("已是最后一条记录！");
		}else{
			currentRowId ++;
			setToolBar();
		}
	}
	 @Override
		protected void toolBarClick(int tag, View v) {
			 switch(tag) {
				case 0:
					previousItem();
					break;
				case 5: 
					nextItem();
					break;
				default:
					cancelThread();
					break;
			 }
		}
		protected void cancelThread() {
			if(thread!=null) {
				thread.interrupt();
			}
			mHandler.removeCallbacks(r);
			hiddenProgressToolBar();
		}
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	    }
	 @Override
		protected void onResume() {
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
