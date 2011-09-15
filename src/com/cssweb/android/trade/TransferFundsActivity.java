package com.cssweb.android.trade;

import android.content.Intent;
import android.os.Bundle;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.transferFunds.FundsDetails;
import com.cssweb.android.trade.transferFunds.TransferFundsDateRange;
import com.cssweb.android.trade.transferFunds.ZfTransfer;

public class TransferFundsActivity extends GridViewActivity {
	private MyGrid mGrid;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.njzq_gridview);
        
        initPopupWindow();
		
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "资金调拨";
	   
		initMenuName(menuId, -1);

		initTitle(R.drawable.njzq_title_left_back, 0, menuName);
		//hiddenToolBar();
		changeBG();
		String userType = TradeUser.getInstance().getUserType();
		if("serv".equals(userType)) 
			initToolBar(Global.BAR_IMAGE_1, Global.BAR_TAG_2);
		else
			initToolBar(Global.BAR_IMAGE_2, Global.BAR_TAG);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	private void initMenuName(int id, int pos) {
		initGrid(mGrid, menuId);
		 
		if(pos>-1) {
			
		}
	}
	
	protected void openChild(int pos, int position) {
		Intent localIntent = new Intent();
		switch(pos) {
		case Global.NJZQ_WTJY_ZJDB:
			if(position==0){ //资金明细查询
				localIntent.setClass(TransferFundsActivity.this, FundsDetails.class);
			}else if (position==1) {//主辅转账
				localIntent.putExtra("type", 1);
				localIntent.setClass(TransferFundsActivity.this, ZfTransfer.class);
			}else if (position==2) {//辅主转账
				localIntent.putExtra("type", 2);
				localIntent.setClass(TransferFundsActivity.this, ZfTransfer.class);
			}else if (position==3) {
				localIntent.setClass(TransferFundsActivity.this, TransferFundsDateRange.class);
			}
			startActivity(localIntent);
		}
	}
}
