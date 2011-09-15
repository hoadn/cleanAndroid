package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Intent;
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
 * 阳光私募
 * @author hoho
 *
 */
public class SunPrivate extends QuoteFundGridActivity {
	private String [] cols ;
	@SuppressWarnings("unused")
	private String[] menu;
	private RequestParams requestParams;
	private int allStockNums = 0;
	private int pageNum = 10;
	private Thread thread = null;
	private boolean firstComing = true;
	// 没有选择查询条件 全部传-1
	private String jingzhi1 = "-1" ;
	private String jingzhi2 = "-1";
	private String jingzhiAdd1 ="-100";
	private String jingzhiAdd2 ="-100";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_table);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.sunprivatetitle));
		String [] toolbarname = new String []{
				Global.TOOLBAR_MENU, Global.TOOLBAR_SHANGYE ,
				Global.TOOLBAR_XIAYIYE ,
				Global.TOOLBAR_REFRESH
		};
		initToolBar(toolbarname, Global.BAR_TAG);
		setToolBar(1, false, R.color.zr_newlightgray);
		menu = getResources().getStringArray(R.array.stock_type_menu);
		requestParams = new RequestParams();
		requestParams.setPaixu("nw");
		cols =getResources().getStringArray(R.array.sunprivate_colsname);
		this.activityKind = Global.SUN_PRIVATE;
		
		Bundle bundle = getIntent().getExtras();
		if (null !=bundle){		//从查询条件带过来
			jingzhi1 = (String) bundle.get("jingzhi1");
			jingzhi2 = (String) bundle.get("jingzhi2");
			jingzhiAdd1 = bundle.getString("jingzhiAdd1");
			jingzhiAdd2 = bundle.getString("jingzhiAdd2");
		}
		requestParams.setJingzhiAdd1(jingzhiAdd1);
		requestParams.setJingzhiAdd2(jingzhiAdd2);
		requestParams.setJingzhi1(jingzhi1);
		requestParams.setJingzhi2(jingzhi2);
		
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(SunPrivate.this);
		rowHeight = CssSystem.getTableRowHeight(SunPrivate.this);
		requestParams.setBegin("1");
		requestParams.setEnd(String.valueOf(pageNum));
		handlerData();
		setToolBar(1, false, R.color.zr_newlightgray);
	}
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(final int type) {
		r = new Runnable(){
			public void run() {
				if (type ==1){
					quoteData = ConnService.execute(requestParams, 6);
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
			if( null == quoteData ){
				listqueryfund.clear();
				listqueryfund.addAll(TradeUtil.fillListToNull4(0, pageNum ));
				refreshFundUI2(listqueryfund, cols , "0" );
				hiddenProgressToolBar();
				return;
			}
			JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
			int len = jArr.length();
			listqueryfund.clear();
			allStockNums = quoteData.getInt("totalrecnum");
			if(pageNum>=allStockNums) {
				//避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
				setToolBar(2, false, R.color.zr_newlightgray);
			}
			
			for (int i=0 ; i< jArr.length() ; i++){
				JSONArray jA = jArr.getJSONArray(i);
				String [] jarr = new String [9];
				jarr [0] = jA.getString(1); //基金名称
				jarr [1] = jA.getString(2); //净值
				jarr [2] = jA.getString(3); //净值增长率
				jarr [3] = jA.getString(4); //本年净值增长率
				jarr [4] = jA.getString(5); //累计净值
				jarr [5] = jA.getString(6); //累计净值增长率
				jarr [6] = jA.getString(7); //年化收益率
				jarr [7] = jA.getString(0); //基金代码
				jarr [8] = jA.getString(8); //交易所市场
				listqueryfund.add(jarr);
			}
			if (len < pageNum ){
				listqueryfund.addAll(TradeUtil.fillListToNull4(len, pageNum ));
			}
			refreshFundUI2(listqueryfund, cols, null);
			hiddenProgressToolBar();
		}catch(Exception e ){
			e.printStackTrace();
		}finally {
			//进度条消失
			hiddenProgressToolBar();
		}
	}
	
	/**
	 * 改变背景色
	 */
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	 }
	 /**
	  * 上一页
	  */
	 protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(1, false, R.color.zr_newlightgray);
			return;
		}
		else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum ;
			if(i1 <= 1) {
				setToolBar(1, false, R.color.zr_newlightgray);
				setToolBar(2, true, R.color.zr_white);
			}
			else {
				setToolBar(1, true, R.color.zr_white);
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
			setToolBar(1, true, R.color.zr_white);
			setToolBar(2, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(1, true, R.color.zr_white);
			setToolBar(2, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	 
	@Override
	protected void toolBarClick(int tag, View v) {
		switch(tag){
		case 0:
			onOption();
			break;
		case 1:
			onPageUp();
			break;
		case 2:
			onPageDn();
			break;
		case 3:
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
		initPopupWindow();
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
	
	protected void queryFund(){
		Intent intent  =  new Intent();
		intent.setClass(this, SunPrivateQueryCondition.class);
		startActivity(intent);

	}
	


	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		int end = Integer.parseInt(requestParams.getEnd()) ;
		if(end>=allStockNums) {
			return ;
		}
		onPageDn();
	}
	
	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		int begin = Integer.parseInt(requestParams.getBegin());
		if(begin <= 1) {
			return ;
		}
		onPageUp();
	}
	
	
	
	
	
}
