/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)GGHQActivity.java 上午10:12:02 2011-4-17
 */
package com.cssweb.android.quote;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;

/**
 * 港股行情九宫格界面
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class GGHQActivity extends GridViewActivity {
	
	private MyGrid mGrid;
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.njzq_gridview);
        initPopupWindow();
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "港股行情";
		initMenuName(menuId, -1);
		initTitle(R.drawable.njzq_title_left_back, 0, menuName);

//		toast(R.string.hongkongstocktimeout);
		
		Toast toast = new Toast(getApplicationContext());
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.zr_custom_toast,null);

		TextView text = (TextView) layout.findViewById(R.id.text);
		text.setText("港股行情内容延迟15分钟");
		toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_LONG);
		toast.setView(layout);
		toast.show();
		
		/*Toast toast = Toast.makeText(getApplicationContext(),
			     "港股行情内容延迟15分钟", Toast.LENGTH_LONG);
		toast.setGravity(Gravity.CENTER, 0, 0);
		View view = toast.getView();
		view.setBackgroundColor(R.color.zr_ziluolan);
		toast.show();*/
			   
			   
		//msg_ask = R.string.hongkongstocktimeout;
		//showDialog(DIALOG_YES_MESSAGE);
	}
	
	private void initMenuName(int id, int pos) {
		initGrid(mGrid, menuId);
		if(pos>-1) {
			
		}
	}
	
	@Override
	protected void openChild(int pos, int position) {
		Log.i("@@@@@@@", pos + ">>>>" + position);
		Intent localIntent = new Intent();
		switch(pos){
			case Global.NJZQ_HQBJ_GGHQ:
				if(position == 0){
					localIntent.setClass(this, HSZS.class);
				}else if (position ==1){
					localIntent.setClass(this, HKMainboard.class);
					Bundle bundle = new Bundle();
					bundle.putInt("stocktype", 101);
					bundle.putInt("flag", 1);
					localIntent.putExtras(bundle);
					
				}else if (position ==2){
					localIntent.setClass(this, HKMainboard.class);
					Bundle bundle = new Bundle();
					bundle.putInt("stocktype", 102);
					bundle.putInt("flag", 2);
					localIntent.putExtras(bundle);
				}
				else if(position==3) {//港股查询
					localIntent.setClass(this, QueryStock.class);
		    		localIntent.putExtra("menuid", Global.QUOTE_HKSTOCK);
					
				}
				startActivity(localIntent);
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		 changeBG();
		 String userType = TradeUser.getInstance().getUserType();
		 if("serv".equals(userType)) 
			initToolBar(Global.BAR_IMAGE_1, Global.BAR_TAG_2);
		 else
			initToolBar(Global.BAR_IMAGE_2, Global.BAR_TAG);
	}
	
}
