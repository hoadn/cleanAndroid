package com.cssweb.android.trade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.bank.BankBalanceQuery;
import com.cssweb.android.trade.bank.BankFundTransfer;
import com.cssweb.android.trade.bank.FundBankTransfer;
import com.cssweb.android.trade.bank.TransferDateRange;

public class BankActivity extends GridViewActivity {
//	private Context mContext = BankActivity.this;
	private MyGrid mGrid;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.njzq_gridview);
        
        initPopupWindow();
		
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "银证转账";
	   
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
		Log.i("@@@@@@@", pos + ">>>>" + position);
		Intent localIntent = new Intent();
		switch(pos) {
		case 24:
			if(position==0){ //资金转入
				localIntent.setClass(this, BankFundTransfer.class);
			}else if(position ==1){//资金转出
				localIntent.setClass(this, FundBankTransfer.class);
			}else if(position ==2){//银行余额
				localIntent.setClass(this, BankBalanceQuery.class);
			}else if(position ==3){//转账查询
				//localIntent.setClass(this, TransferQuery.class);
				localIntent.setClass(this, TransferDateRange.class);
			}else if (position ==4) {//资金调拨
				localIntent.putExtra("menu_id", Global.NJZQ_WTJY_ZJDB);
				localIntent.setClass(this, TransferFundsActivity.class);
			}
			startActivity(localIntent);
		}
	}
}
