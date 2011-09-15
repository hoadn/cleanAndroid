/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CssKeyboardBase.java 下午05:20:43 2010-10-3
 */
package com.cssweb.android.base;

import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.PopupWindow;

import com.cssweb.android.custom.VirtualKeyBoard;
import com.cssweb.android.main.R;

/**
 * 自定义软键盘
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class CssKeyboardBase extends CssBaseActivity {
	private PopupWindow pw;
	
	protected View m_vklayout;
	protected View.OnClickListener setOnEditClickListener;
	protected View.OnFocusChangeListener setOnEditFocusListener;
	
	protected View.OnFocusChangeListener setOnEditFocusListener2;
	private boolean isFirst = true;
	
	public View.OnClickListener adjustIconListener;//加减微调按钮监听器
	
	protected void onCreate(Bundle paramBundle) {
	    super.onCreate(paramBundle);
	    
	    //getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
	    
	    setOnEditClickListener = new EditClickListener();
	    setOnEditFocusListener = new EditFocusListener();
	    setOnEditFocusListener2 = new EditFocusListener2();
	    adjustIconListener = new AdjustIconListener();
	}

	public void showInput(EditText editText, String keyType) {
		LayoutInflater inflater = (LayoutInflater) this
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//editText.setInputType(InputType.TYPE_NULL);
		View PopupView = null;
		et=editText;
		et_type=keyType;
		if ("NUMDOT".equals(keyType) || keyType.equals("")) {
//			PopupView = inflater.inflate(R.layout.zr_keyviewnum, null, false);
			PopupView = inflater.inflate(R.layout.zr_keyviewnew, null, false);
		}else{
			 if(type.equals("shuzi")){
				PopupView = inflater.inflate(R.layout.zr_keyviewnew, null, false);
			}else {
				PopupView = inflater.inflate(R.layout.zr_keyview_en, null, false);
			}
		}
//		else
////			PopupView = inflater.inflate(R.layout.zr_keyviewnew, null, false);
//			PopupView = inflater.inflate(R.layout.zr_keyview_en, null, false);

		cssKeyBoard = new VirtualKeyBoard(PopupView, keyType,type);
		cssKeyBoard.setUp(editText, CssKeyboardBase.this);
//		cssKeyBoard.setHeight(46);
//		cssKeyBoard.setWidth(getWidthPixels());
		//pw = new PopupWindow(PopupView, 600, 1024 * 2 / 3, true);
		pw = new PopupWindow(PopupView,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		pw.setOutsideTouchable(true);
		pw.setFocusable(false);
		pw.showAtLocation(m_vklayout,
				Gravity.BOTTOM, 0, 0);
		if(!isAnim){
			pw.setAnimationStyle(R.style.PopupAnimation_exit);
		}else{
			pw.setAnimationStyle(R.style.PopupAnimation);
		}
		pw.update();
//		pw.getContentView().setOnClickListener(new OnClickListener() {
//			public void onClick(View v) {
//				CssLog.i("@@@@@@@@OnClickListener@@@@@@@@@@@@", ">>>>>>>>>>>>>>>>>>>");
//				onHideKeyBoard();
//			}
//		});
	}

	public void onHideKeyBoard() {
		if (this.pw != null){
			this.pw.dismiss();
		}
	}
	
	protected void onDestroy() {
		onHideKeyBoard();
		super.onDestroy();
	}
	
	protected void onPause() {
		onHideKeyBoard();
		super.onPause();
	}

	protected void onResume() {
		onHideKeyBoard();
		super.onResume();
	}
	
	public class EditClickListener implements View.OnClickListener {

		public void onClick(View v) {
			isFirst =false ;
			String tag = (String)v.getTag();
			if(tag==null) {
				onHideKeyBoard();
			}
			else {
				onHideKeyBoard();
				EditText e = (EditText)v;
				showInput(e, tag);
			}
		}
	}
	
	public class EditFocusListener implements View.OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus) {
			String tag = (String)v.getTag();
			EditText e = (EditText)v;
			onHideKeyBoard();
			if(hasFocus) {
				showInput(e, tag);			
			}
		}
	}
	
	public class EditFocusListener2 implements View.OnFocusChangeListener {

		public void onFocusChange(View v, boolean hasFocus) {
			String tag = (String)v.getTag();
			if (isFirst){
				if (null!=tag && tag.equals("1")){
					isFirst =true ;
				}else if(null!=tag && tag.equals("2")){
					isFirst =false ;
				}
			}
			
			EditText e = (EditText)v;
			onHideKeyBoard();
			if(hasFocus && !isFirst) {
				showInput(e, tag);			
			}
			isFirst =false;
		}
	}
	
	/**
	 * 处理加减微调按钮的方法
	 * @param tag
	 */
	public void adjustDownOrUp(Object tag){
		
	}

	public class AdjustIconListener implements View.OnClickListener{

		public void onClick(View v) {
			adjustDownOrUp(v.getTag());
		}
		
	}
	
	private EditText et;
	private String et_type;
	private String type="shuzi";
	private VirtualKeyBoard cssKeyBoard;
	private boolean isAnim=true;
	public void changeKeyBoard(String stype,boolean isAnim){
		this.isAnim=isAnim;
		if(this.cssKeyBoard!=null) this.cssKeyBoard=null;
		onHideKeyBoard();
		if(stype.equals("shuzi")){
			type="zimu";
			showInput(et,et_type);
		}else if(stype.equals("zimu")){
			type="shuzi";
			showInput(et,et_type);
		}
		this.isAnim=true;
	}
	
//	public int getHeightPixels(){
//		int height=0;
//		DisplayMetrics dm = new DisplayMetrics();   
//		getWindowManager().getDefaultDisplay().getMetrics(dm); 
//		height=dm.heightPixels;
//		
//		return height;
//	}
//	public int getWidthPixels(){
//		int width=0;
////		DisplayMetrics dm = new DisplayMetrics();   
////		getWindowManager().getDefaultDisplay().getMetrics(dm); 
////		width=dm.widthPixels;
////		WindowManager manage = this.getWindowManager(); 
////		Display display = manage.getDefaultDisplay(); 
////		 width = display.getWidth();
////		
////		DisplayMetrics metrics = new DisplayMetrics();
////		WindowManager WM = (WindowManager)this.getSystemService(Context.WINDOW_SERVICE);
////		WM.getDefaultDisplay().getMetrics(metrics);
////		 width=metrics.xdpi;
////		return width;
//		
//		DisplayMetrics dm = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int widthPixels= dm.widthPixels;
//        int heightPixels= dm.heightPixels;
//        float density = dm.density;
////        px= (int) (dip*density+0.5f)
//                int screenWidth = (int) (widthPixels * density) ;
//                int screenHeight = (int) (heightPixels * density) ;
//                return screenWidth;
//	}
}
