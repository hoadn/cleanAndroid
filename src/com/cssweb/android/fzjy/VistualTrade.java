package com.cssweb.android.fzjy;

import android.content.Intent;
import android.os.Bundle;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.web.WebViewDisplay;

public class VistualTrade extends GridViewActivity {
	//private Context mContext = VistualTrade.this;
	private MyGrid mGrid;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.njzq_gridview);
        
        initPopupWindow();
		
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "模拟炒股";
	   
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
//		Intent intent = new Intent(VistualTrade.this, TestWebView.class);
//		VistualTrade.this.startActivity(intent);
		
		Intent localIntent = new Intent();	    
		localIntent.putExtra("pos", pos);
		localIntent.putExtra("position", position);
		localIntent.setClass(VistualTrade.this, WebViewDisplay.class);
		VistualTrade.this.startActivity(localIntent);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		initMenuName(menuId, -1);
		changeBG();
		String userType = TradeUser.getInstance().getUserType();
		if("serv".equals(userType)) 
			initToolBar(Global.BAR_IMAGE_1, Global.BAR_TAG_2);
		else
			initToolBar(Global.BAR_IMAGE_2, Global.BAR_TAG);
	}
	
}