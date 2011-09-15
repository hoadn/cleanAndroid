/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)Jxgpc.java 下午12:14:23 2011-3-8
 */
package com.cssweb.android.web;
import android.os.Bundle;
import android.util.Log;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;

/**
 * 精选股票池图标界面
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class JxgpcActivity extends GridViewActivity {
	private MyGrid mGrid;
	
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.njzq_gridview);
        
        initPopupWindow();
		
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "精选股票池";
	   
		initMenuName(menuId, -1);

		initTitle(R.drawable.njzq_title_left_back, 0, menuName);
		//hiddenToolBar();
	}
	
	private void initMenuName(int id, int pos) {
		initGrid(mGrid, menuId);
		 
		if(pos>-1) {
			
		}
	}
	
	protected void openChild(int pos, int position) {
		Log.i("@@@@@@@", pos + ">>>>" + position);
		if(position==0) {
//			FairyUI.switchToWnd(Global.NJZQ_NZBD_JXZQC_JCC , Global.NJZQ_NZBD_JXZQC_JCC ,  JxgpcActivity.this);
			FairyUI.switchToWnd(Global.NJZQ_NZBD_JXZQC_JCC,  JxgpcActivity.this);
		}
		else if(position==1) {
//			FairyUI.switchToWnd(Global.NJZQ_NZBD_JXZQC_HXC , Global.NJZQ_NZBD_JXZQC_HXC ,  JxgpcActivity.this);
			FairyUI.switchToWnd(Global.NJZQ_NZBD_JXZQC_HXC ,  JxgpcActivity.this);
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
