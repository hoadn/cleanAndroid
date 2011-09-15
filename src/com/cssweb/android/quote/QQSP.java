package com.cssweb.android.quote;

import java.util.ArrayList;

import org.json.JSONArray;

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
 * 全球商品
 *
 */
public class QQSP extends QuoteFundGridActivity {
	private String [] cols ;
	private int pageNum = 10;
	private RequestParams requestParams;
	private int allStockNums = 0;
	
	private Thread thread = null;
	private boolean firstComing = true;
	private ArrayList<String[]> stringList;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		stringList = new ArrayList<String[]>();
		
		setContentView(R.layout.zr_static_table3);
		String[] toolbarname = new String[]{ Global.TOOLBAR_SHANGYE,"", Global.TOOLBAR_XIAYIYE, "",Global.TOOLBAR_REFRESH  };
		initToolBar(toolbarname, Global.BAR_TAG);
		initTitle(R.drawable.njzq_title_left_back, 0, "全球商品");
		cols = getResources().getStringArray(R.array.qqsp_cols);
		requestParams = new RequestParams();
		//requestParams.setField("index_code");
		requestParams.setKind("glcomm");
		
		pageNum = CssSystem.getTablePageSize(QQSP.this);
		rowHeight = CssSystem.getTableRowHeight(QQSP.this);
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		handlerData();
		setToolBar(0, false, R.color.zr_newlightgray);
	}
	
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(int type) {
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				quoteData = ConnService.execute(requestParams , 8);
				try{
					if( Utils.isHttpStatus(quoteData) ){
						JSONArray jArr = quoteData.getJSONArray("data");
						int len = jArr.length();
						stringList.clear();
						allStockNums = quoteData.getInt("totalrecnum");
						for (int i = 0; i < jArr.length(); i++) {
							JSONArray jA = (JSONArray)jArr.get(i);
							String [] jarr = new String [4];
							jarr[0] = jA.getString(9);    //名称
							jarr[1] = jA.getString(3);    //现价
							jarr[2] = jA.getString(5);	  //涨幅
							jarr[3] = jA.getString(4);    //涨跌
							stringList.add(jarr);
						}
						if (len < pageNum ){
							stringList.addAll(TradeUtil.fillListToNull5(len, pageNum ));
						}
						isNetworkError = 0;
					}else {
						isNetworkError = -1;
					}
				}catch(Exception e ){
					isNetworkError = -2;
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	
	/**
	 * 更新UI界面
	 */
	@Override
	protected void handlerData() {
		try{
			if(isNetworkError<0&&firstComing) {
				firstComing = false;
				toast(R.string.load_data_error);
			}
			if(quoteData==null) {
				stringList.clear();
				stringList.addAll(TradeUtil.fillListToNull5(0, pageNum ));
				refreshMarketQueryUI(stringList, cols, "2");
				hiddenProgressToolBar();
				return;
			}
			if(allStockNums!=0&&pageNum>allStockNums) {
				//避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
				setToolBar(2, false, R.color.zr_newlightgray);
			}
			refreshMarketQueryUI(stringList, cols, "2");
			hiddenProgressToolBar();
		}catch(Exception e ){
			hiddenProgressToolBar();
			e.printStackTrace();
		}finally {	
			//进度条消失
			hiddenProgressToolBar();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		//根据不同分辨率获得可显示行数
		initPopupWindow();
		mLock = true;
		setToolBar();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mLock = false;
		mHandler.removeCallbacks(r);
	}
	
	
	/**
	 * 改变背景色
	 */
	protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onPageUp();
			break;
		case 2:
			onPageDn();
			break;
		case 4:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	/**
	 * 上一页
	 */
	 protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(0, false, R.color.zr_newlightgray);
			setToolBar(2, true, R.color.zr_white);
			return;
		}
		else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum ;
			if(i1 <= 1) {
				setToolBar(0, false, R.color.zr_newlightgray);
				setToolBar(2, true, R.color.zr_white);
			}
			else {
				setToolBar(0, true, R.color.zr_white);
				setToolBar(2, true, R.color.zr_white);
			}
		}
		i1 = (i1 <= 1)?1:i1;
		//i2 = (i2<pageNum)?pageNum:i2;
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	 /**
	  * 下一页
	  */
	 protected void onPageDn() {
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum ;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum ;
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(0, true, R.color.zr_white);
			setToolBar(2, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(0, true, R.color.zr_white);
			setToolBar(2, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
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
	
	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		int end = Integer.parseInt(requestParams.getEnd());
		if (end >= allStockNums) {
			return;
		}
		onPageDn();
	}

	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		int begin = Integer.parseInt(requestParams.getBegin());
		if (begin <= 1) {
			return;
		}
		onPageUp();
	}
	
}
