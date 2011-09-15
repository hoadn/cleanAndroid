package com.cssweb.android.trade;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.fund.FundAccount;
import com.cssweb.android.trade.fund.FundCompany;
import com.cssweb.android.trade.fund.FundMelonSet;
import com.cssweb.android.trade.fund.FundPortio;
import com.cssweb.android.trade.fund.FundTrading;
import com.cssweb.android.trade.fund.FundTransfer;
import com.cssweb.android.trade.fund.TodayTrust;
import com.cssweb.android.util.DateRange;
import com.cssweb.android.web.WebViewDisplay;

public class FundActivity extends GridViewActivity {
	private ViewFlipper viewFlipper;
//	private Context mContext = FundActivity.this;
	private Animation leftIn;
	private Animation leftOut;
	private Animation rightIn;
	private Animation rightOut;
	
	private MyGrid mGrid, mGrid2;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);  
		
		setContentView(R.layout.njzq_gridview);
		
        initPopupWindow();
		
    	preView = (ImageView) findViewById(R.id.previous_screen);
		
    	viewFlipper = (ViewFlipper) this.findViewById(R.id.mainmenu_container);
		
		leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in_layout);
		leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out_layout);
		rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in_layout);
		rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out_layout);
		
		mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
		mGrid2 = (MyGrid) findViewById(R.id.mainmenu_grid2);
		
		Bundle bundle = getIntent().getExtras();
		menuId = bundle.getInt("menu_id");
		menuName = "场外基金";
	   
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
    	initGrid(mGrid2, Global.NJZQ_WTJY_FOUR);
    	preView.setVisibility(View.VISIBLE);
    	preView.setImageResource(R.drawable.page_arrow_02);
		 
		if(pos>-1) {
			
		}
	}

	
	/**
	 * 向左滑动
	 */
	protected void moveColLeft() {
		if(viewFlipper.getDisplayedChild() == 0) {
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
	    	preView.setImageResource(R.drawable.page_arrow_01);
		}
	}
	
	/**
	 * 向右滑动
	 */
	protected void moveColRight() {
		if(viewFlipper.getDisplayedChild() == 1) {
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
	    	preView.setImageResource(R.drawable.page_arrow_02);
		}
	}
	
	protected void openChild(int pos, int position) {
		Log.i("@@@@@@@", pos + ">>>>" + position);
		Intent localIntent = new Intent();
		switch(pos) {
		case 220:
			if(position ==0){
				localIntent.putExtra("type", 1);
				localIntent.setClass(this, FundTrading.class);
			}else if (position ==1){
				localIntent.putExtra("type", 2);
				localIntent.setClass(this, FundTrading.class);
			}else if (position ==2){
				localIntent.putExtra("type", 0);
				localIntent.setClass(this, FundTrading.class);
			}else if (position ==3){
				localIntent.putExtra("type", 3);
		    	localIntent.putExtra("menudetail", Global.QUERY_FUND_CD);
		    	localIntent.setClass(this, TodayTrust.class);
			}else if (position ==4){
				localIntent.putExtra("menudetail", Global.QUERY_FUND_DRWT);
				localIntent.setClass(this, TodayTrust.class);
			}else if (position ==5){
				localIntent.setClass(this, FundTransfer.class);
			}else if (position ==6){
				localIntent.putExtra("menuid", Global.OPEN_JJ);
				localIntent.putExtra("menudetail", Global.QUERY_FUND_LSCJ);
				localIntent.setClass(this, DateRange.class);
			}else if (position ==7){
				localIntent.putExtra("menuid", Global.OPEN_JJ);
				localIntent.putExtra("menudetail", Global.QUERY_FUND_LSWT);
				localIntent.setClass(this, DateRange.class);
			}else if (position ==8){
				localIntent.setClass(this, FundPortio.class);
			}
			startActivity(localIntent);
			break;
		case 23:
			if(position ==0){
//				localIntent.putExtra("type", 7);
//				localIntent.putExtra("menuid", Global.OPEN_JJ);
//		    	localIntent.putExtra("menudetail", Global.QUERY_FUND_DRWT);
				localIntent.setClass(this, FundMelonSet.class);
			}else if (position ==1){
				localIntent.putExtra("type", 8);
				localIntent.setClass(this, FundAccount.class);
			}else if(position ==2){
				localIntent.setClass(this, FundCompany.class);
			}else if (position ==3){
				localIntent.putExtra("pos", Global.NJZQ_FUND_RISK_TEST);
	    		localIntent.setClass(this, WebViewDisplay.class);
			}
			startActivity(localIntent);
		}
	}
	
	
}