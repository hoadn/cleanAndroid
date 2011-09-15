package com.cssweb.android.base;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.cssweb.android.adapter.ImageAdapter;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.session.TradeUser;

public class GridViewActivity extends FlipperActiviy {
	private Context mContext = GridViewActivity.this;
	protected ImageView preView;
	protected int menuId;
	protected String menuName;
	private ImageAdapter imageApdater = null;
//	private ButtonTouchListener l;
	
	@Override  
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);
	}
	
	protected void initGrid(MyGrid mGrid, final int _pos) {
		int i = 3, j = 0;
		int height = getWindowManager().getDefaultDisplay().getHeight();
		if(height==480) 
			j = height - 80;
		else 
			j = height - 240;
		int k = j - 50;
        int l = mGrid.getPaddingTop();
        int i1 = k - l;
        int j1 = mGrid.getPaddingBottom();
        int k1 = i1 - j1 - 50;
        float f = i;
        int l1 = (int)Math.ceil(9 / f);
        mGrid.setNumColumns(i);
		int i2 = k1 / l1;
		mGrid.setFocusableInTouchMode(true);
		imageApdater = new ImageAdapter(this, i2, _pos);
		mGrid.setAdapter(imageApdater); 
		mGrid.setOnTouchListener(this);
		mGrid.setListener(new ItemListener(_pos));
	}
	
	protected class ItemListener implements MyGrid.Listener{   
		private int pos;
		
		public ItemListener(int _pos) {
			this.pos = _pos;
		}
		
		public void onClick(int position) {  
			//
			openChild(pos, position);
		}  
	}
	
	protected void openChild(int pos, int position) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i("#############", ">>>>>>>>>>>");
		if(imageApdater!=null)
			imageApdater.recycle();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			backIndex();
			break;
		case 1:
			if(menuId == Global.NJZQ_HQBJ) return;
			FairyUI.switchToWndWithSingle(Global.NJZQ_HQBJ, mContext,true);
			break;
		case 2:
			if(TradeUser.getInstance().getLoginType()==1||TradeUser.getInstance().getLoginType()==2) {//登录
				if(menuId == Global.NJZQ_WTJY) return;
				FairyUI.switchToWndWithSingle(Global.NJZQ_WTJY, mContext,true);
			}else{
				if(menuId == Global.NJZQ_ZXLP) return;
				FairyUI.switchToWndWithSingle(Global.NJZQ_ZXLP, mContext,true);
			}
			break;
		case 3:
			if(TradeUser.getInstance().getLoginType()==1||TradeUser.getInstance().getLoginType()==2) {//登录
				if(menuId == Global.NJZQ_ZXLP) return;
				FairyUI.switchToWndWithSingle(Global.NJZQ_ZXLP, mContext,true);
			}else{
				if(menuId == Global.NJZQ_ZXHD) return;
				FairyUI.switchToWndWithSingle(Global.NJZQ_ZXHD, mContext,true);
			}
			break;
		case 4:
			if(TradeUser.getInstance().getLoginType()==1||TradeUser.getInstance().getLoginType()==2) {//登录
				if(menuId == Global.NJZQ_ZXHD) return;
				FairyUI.switchToWndWithSingle(Global.NJZQ_ZXHD, mContext,true);
			}else{
				FairyUI.switchToWndWithSingle(Global.NJZQ_JLP_YYKHTAG, mContext,true);
			}
			break;
		case 6:
			if(menuId == Global.NJZQ_WTJY) return;
			FairyUI.switchToWndWithSingle(Global.NJZQ_WTJY, mContext,true);
			break;
		}
	}
    
//    private int[] toolBtns = { R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back};
//    private int[] toolBtnsSelect = { R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back, R.drawable.njzq_status_back};
}
