package com.cssweb.android.trade.fund;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.stock.TradeFundGridActivity;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.util.CssSystem;

public class FundCompany extends TradeFundGridActivity {
	private String DEBUG_TAG ="FundCompany";
	private int btnTag = -1;
	private String [] cols ;
	private Boolean flag = true;
	private Thread thread = null;
	private int type = 0;
	private int len;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_fund_static_table);
		initTitle(R.drawable.njzq_title_left_back, 0, "基金开户");
		String[] toolbarNames = {Global.OPENACCOUNT, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE,Global.TOOLBAR_REFRESH};
		initToolBar(toolbarNames, Global.BAR_TAG);
		setToolBar(0, false, R.color.zr_dimgray);
		setToolBar(1, false, R.color.zr_dimgray);
		setToolBar(2, false, R.color.zr_dimgray);
		cols = getResources().getStringArray(R.array.fundcompany_colsname);
//		colsName = getResources().getStringArray(R.array.fundcompany_colsname);
//		colsIndex= getResources().getStringArray(R.array.fundcompany_colsindex);
		handlerData();
	}
	/**
	 * 	请求后台数据
	 */
	protected void init (final int i){
		type = i;
		try{
			r = new Runnable(){
				public void run() {
					if(btnTag == -1 || btnTag == 3){
						if(type == 1){
							flag = true;
							try {
				    			String filedate = ActivityUtil.getPreference(FundCompany.this,"openFundCompanyDate", "");
								if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
									quoteData = FundService.getFundCompany();
								}else{
									String jsonObject = CssIniFile.loadIni(FundCompany.this, 3, "fundCompany");
									if(null !=jsonObject && ! jsonObject.equals("")){
										quoteData = new JSONObject(jsonObject);
									}
								}
							} catch (JSONException e) {
								Log.e(DEBUG_TAG, e.toString());
							}
						}
					}
					mHandler.sendEmptyMessage(0);
				}
			};
			thread = new Thread(r);
			thread.start();
		}catch(Exception e ){
			Log.e(DEBUG_TAG, e.toString());
		}
	}
	/**
	 * 接收消息,更新视图
	 */
	@Override
	protected void handlerData() {
		try{
			if(null == quoteData ){
				listquery.clear();
				listquery.addAll(TradeUtil.fillListToNull3(0, pageNum ));
				refreshFundQueryUI(listquery, cols, null);
				hiddenProgressToolBar();
				if (type == 1 || btnTag == 3) {// 进去页面请求 或 刷新
					Toast.makeText(FundCompany.this, "读取数据失败！请刷新或者重新设置网络。。", Toast.LENGTH_LONG).show();
					setToolBar(0, false, R.color.zr_dimgray);
					setToolBar(1, false, R.color.zr_dimgray);
					setToolBar(2, false, R.color.zr_dimgray);
				}
				return;
			}else if(flag){
				JSONArray jarr = quoteData.getJSONArray("item");
				len = jarr.length()-1;
				listquery.clear();
				for (int i=0 ; i< jarr.length()-1; i++){
					JSONObject  jsonobj = jarr.getJSONObject(i);
					String [] jArr = new String [2];
					jArr[0] = jsonobj.getString("FID_TADM");
					jArr[1] = jsonobj.getString("FID_JGJC");
					listquery.add(jArr);
				}
				flag = false;
			}
			pageNum = CssSystem.getTablePageSize(FundCompany.this);
			if (len < (currentPageId+1)*pageNum){
				listquery.addAll(TradeUtil.fillListToNull3(len, (currentPageId+1)*pageNum ));
			}
			refreshFundQueryUI(listquery, cols, null);
			setBtnStatus();
			hiddenProgressToolBar();
		}catch(Exception e ){
			Log.e(DEBUG_TAG, e.toString());
			hiddenProgressToolBar();
		}
	}
	/**
	 * 开户事件
	 */
	private void openAccount() {
		if(listquery.size()>0){
			Context context = FundCompany.this;
			Intent intent = new Intent();
			intent.setClass(context, FundAccountForm.class);
			intent.putExtra("tacode", tacode);
			intent.putExtra("taname", taname);
			context.startActivity(intent);
		}else {
			return;
		}
	}
	
	protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		setToolBar();
		initPopupWindow();
	}
	@Override
	protected void toolBarClick(int tag, View v) {
		btnTag = tag;
		 switch(tag) {
			case 0:
				openAccount();
				break;
			case 1: 
				super.onPageUp();
				break;
			case 2:
				super.onPageDown();
				break;
			case 3: 
				setToolBar();
				break;
		 }
	}
	private void setBtnStatus(){
		if (listquery.size() == 0) {
			setToolBar(0, false, R.color.zr_dimgray);
			setToolBar(1, false, R.color.zr_dimgray);
			setToolBar(2, false, R.color.zr_dimgray);
		}else{
			setToolBar(0, true, R.color.zr_white);
			if(endRowId >= len-1){
				setToolBar(2, false, R.color.zr_dimgray);
			}else {
				setToolBar(2, true, R.color.zr_white);
			}
			if(currentPageId == 0){
				setToolBar(1, false, R.color.zr_dimgray);
			}else {
				setToolBar(1, true, R.color.zr_white);
			}
		}
	}
}
