package com.cssweb.android.quote;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
/**
 * 场外基金
 * @author hoho
 *
 */
public class OTCFundActivity extends GridViewActivity {
	@SuppressWarnings("unused")
	private Context mContext = OTCFundActivity.this;
	private MyGrid mGrid;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		setContentView(R.layout.njzq_gridview);
        initPopupWindow();
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "场外基金";
		initMenuName(menuId, -1);
		initTitle(R.drawable.njzq_title_left_back, 0, menuName);
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
		case 18:
			if(position ==0){
				localIntent.putExtra("type", position);
				localIntent.setClass(this, StockTypeFund.class);
			}else if(position ==1){
				localIntent.putExtra("type", position);
				localIntent.setClass(this, StockTypeFund.class);
			}else if(position ==2){
				localIntent.putExtra("type", position);
				localIntent.setClass(this, StockTypeFund.class);
			}else if(position ==3){
				localIntent.putExtra("type", position);
				localIntent.setClass(this, StockTypeFund.class);
			}
			/*else if(position ==4){
				localIntent.setClass(this, FundQueryCondition.class);
			}*/
			
			else if(position ==5){
				localIntent.setClass(this, SunPrivate.class);
			}else if (position ==4){
				localIntent.setClass(this, JingZhiQuery.class);
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