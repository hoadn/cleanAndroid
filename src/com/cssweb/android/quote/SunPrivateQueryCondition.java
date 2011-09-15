package com.cssweb.android.quote;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.cssweb.android.base.QuoteGridActivity;
import com.cssweb.android.common.ConstData;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;

public class SunPrivateQueryCondition extends QuoteGridActivity {

	private Spinner jingzhiSpinner;
	private Spinner jingzhiZengZhangSpinner;
	private ArrayAdapter<String> jingzhiAdapter;
	private ArrayAdapter<String> jingzhiZengZhangAdapter ;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_quote_fundquerymenu);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.openfundname));
		String [] toolbarname = new String []{
				Global.TOOLBAR_CHAXUN, "" ,"" ,"" ,""
		};
		initToolBar(toolbarname, "");
		setSpinner();
	}
	
	/**
	 * 改变背景色
	 */
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	 }
	 
	 @Override
		protected void toolBarClick(int tag, View v) {
			switch(tag){
			case 0:
				Intent intent = new Intent();
				String jingzhi1 =ConstData.jingzhiId1[jingzhiSpinner.getSelectedItemPosition()];
				String jingzhi2 =ConstData.jingzhiId2[jingzhiSpinner.getSelectedItemPosition()];
				
				String jingzhiAdd1 = ConstData.jingzhiAddId1[jingzhiZengZhangSpinner.getSelectedItemPosition()];
				String jingzhiAdd2 = ConstData.jingzhiAddId2[jingzhiZengZhangSpinner.getSelectedItemPosition()];
				
				intent.putExtra("jingzhi1", jingzhi1);
				intent.putExtra("jingzhi2", jingzhi2);
				
				intent.putExtra("jingzhiAdd1", jingzhiAdd1);
				intent.putExtra("jingzhiAdd2", jingzhiAdd2);
				
				intent.setClass(this, SunPrivate.class);   
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //解决从这个界面到第一个界面，会再次压栈，这样就有两个一样的activity。加入这个意思是清除
				startActivity(intent);
				SunPrivateQueryCondition.this.finish();
				break;
			
			}
		}
	 
	 	/**
		 * 设置下拉框
		 */
		private void setSpinner(){
			jingzhiSpinner = (Spinner) findViewById(R.id.jingzhiid);
			jingzhiZengZhangSpinner = (Spinner) findViewById(R.id.jingzhiaddid);
			
			jingzhiAdapter = new ArrayAdapter<String>(this , R.layout.mysimple_spinner_item, ConstData.jingzhi);
			jingzhiAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item);
			jingzhiSpinner.setAdapter(jingzhiAdapter);
			jingzhiSpinner.setPrompt("净值");
			
			jingzhiZengZhangAdapter = new ArrayAdapter<String>(this ,  R.layout.mysimple_spinner_item2 , ConstData.jingzhiAdd);
			jingzhiZengZhangAdapter.setDropDownViewResource( R.layout.mysimple_spinner_dropdown_item );
			jingzhiZengZhangSpinner.setAdapter(jingzhiZengZhangAdapter);
			jingzhiZengZhangSpinner.setPrompt("本年净值增长率");
	
			
		}
	 
	 @Override
	 protected void onResume() {
			super.onResume();
			initPopupWindow();
	  }
	 
	
}
