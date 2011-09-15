package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.Menu;
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
 * 净值增长查询
 * @author hoho
 *
 */
public class JingZhiQuery extends QuoteFundGridActivity {
	@SuppressWarnings("unused")
	private String[] menu;
	@SuppressWarnings("unused")
	private final String TAG ="JingZhiQuery";
	@SuppressWarnings("unused")
	private Context context =JingZhiQuery.this;
	private String [] cols ;
	private RequestParams requestParams;
	private int allStockNums = 0;
	private int pageNum = 10;
	protected Menu mMenu;
	private Thread thread = null;
	private String titlexuanze[] ;
	private boolean firstComing = true;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_table);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.jingzhititle));
		String [] toolbarname = new String []{
				Global.TOOLBAR_XUANX, Global.TOOLBAR_SHANGYE ,
				Global.TOOLBAR_XIAYIYE ,
				Global.TOOLBAR_REFRESH
		};
		initToolBar(toolbarname, Global.BAR_TAG);
		setToolBar(1, false, R.color.zr_newlightgray);
		menu = getResources().getStringArray(R.array.stock_type_menu);
		cols =getResources().getStringArray(R.array.jingzhi_colsname);
		titlexuanze =getResources().getStringArray(R.array.jingzhi_xuanzename);
		
		requestParams = new RequestParams();
		requestParams.setPaixu("ChangeRateW");
		
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(JingZhiQuery.this);
		rowHeight = CssSystem.getTableRowHeight(JingZhiQuery.this);
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
		r =new Runnable(){
			public void run() {
				if (type ==1){
					quoteData = ConnService.execute(requestParams, 7);
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
				//创建一个Message对象
                Message message = Message.obtain();
                message.obj = quoteData;
                //通过Handler发布携带消息
                mHandler.sendMessage(message);
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
			if( null == quoteData ){
				listqueryfund.clear();
				listqueryfund.addAll(TradeUtil.fillListToNull4(0, pageNum ));
				refreshFundUI3(listqueryfund, cols , "0" );
				hiddenProgressToolBar();
				return;
			}
			JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
			int len = jArr.length();
			listqueryfund.clear();
			allStockNums = quoteData.getInt("totalrecnum");
			for (int i=0 ; i <jArr.length() ; i++){
				JSONArray jA = jArr.getJSONArray(i);
				String [] jarr = new String [9];
				jarr [0] = jA.getString(1); //名称
				
				jarr [1] = jA.getString(2); //最新净值
				jarr [2] = jA.getString(4); //近一周净值
				jarr [3] = jA.getString(3); //累计净值
				jarr [4] = jA.getString(0); //代码
				
				jarr [5] ="";
				jarr [6] ="";
				jarr [7] ="";
				jarr [8] ="";
				listqueryfund.add(jarr);
			}
			if (len < pageNum ){
				listqueryfund.addAll(TradeUtil.fillListToNull4(len, pageNum ));
			}
			refreshFundUI3(listqueryfund, cols, null);
			hiddenProgressToolBar();
		}catch(Exception e){
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
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum;
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
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum;
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
				onXuanZe();
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
	 
	 /**
	  * 选择查询条件
	  */
	 private void onXuanZe (){
		 int checkedItemId = getSelectedItem(requestParams.getPaixu());
		  new AlertDialog.Builder(JingZhiQuery.this)
         .setTitle("请选择")
         .setSingleChoiceItems(R.array.jingzhi_xuanzename, checkedItemId , new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int whichButton) {
            	 if( whichButton ==0 ){
            		 requestParams.setPaixu("ChangeRateW");
            		 cols[3]=titlexuanze[0];
            	 }else if(whichButton ==1){
            		 requestParams.setPaixu("ChangeRateM");
            		 cols[3]=titlexuanze[1];
            	 }else if (whichButton ==2){
            		 requestParams.setPaixu("ChangeRate3M");
            		 cols[3]=titlexuanze[2];
            	 }else if (whichButton ==3){
            		 requestParams.setPaixu("ChangeRate6M");
            		 cols[3]=titlexuanze[3];
            	 }else if (whichButton ==4){
            		 requestParams.setPaixu("ChangeRateY");
            		 cols[3]=titlexuanze[4];
            	 }
				 requestParams.setBegin("1");
				 requestParams.setEnd(String.valueOf(pageNum));
             }
         })
         .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {    //确定
             public void onClick(DialogInterface dialog, int whichButton) {
				 setToolBar(1, false, R.color.zr_newlightgray);
				 setToolBar(2, true, R.color.zr_white);
            	 setToolBar();
             }
         })
         .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {  //取消
             public void onClick(DialogInterface dialog, int whichButton) {

             }
         })
        .show();
	 }
	 
	 @Override
		protected void onResume() {
			super.onResume();

			initPopupWindow();
			setToolBar();
		}
	 
	private int getSelectedItem(String paixu){
		if (paixu.equals("ChangeRateW")){
			return 0;
		}else if (paixu.equals("ChangeRateM")){
			return 1;
		}else if (paixu.equals("ChangeRate3M")){
			return 2;
		}else if (paixu.equals("ChangeRate6M")){
			return 3;
		}else if (paixu.equals("ChangeRateY")){
			return 4;
		}
		return 0;
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
