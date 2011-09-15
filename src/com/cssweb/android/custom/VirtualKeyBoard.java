/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)VirtualKeyBoard.java 上午09:31:42 2010-12-28
 */
package com.cssweb.android.custom;

import java.util.Date;

import android.content.res.Resources;
import android.text.Editable;
import android.text.Selection;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.main.R;

/**
 * 虚拟键盘
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class VirtualKeyBoard {

	private static int inputTime = 1000;
	private long stexttime = 0L;
	private int state;
	
	private View m_context = null;
	private EditText edittext;
	private CssKeyboardBase activity;
	private Resources localResources = null;
	private Button button;
	private Button[] btns;
//	private int[] btnid1 = {R.id.zr_keybtn11, R.id.zr_keybtn12, R.id.zr_keybtn13,
//							R.id.zr_keybtn21, R.id.zr_keybtn22, R.id.zr_keybtn23,
//							R.id.zr_keybtn31, R.id.zr_keybtn32, R.id.zr_keybtn33,
//							R.id.zr_keybtn41, R.id.zr_keybtn42};
	private int[] btnid2;
	
	private int[] btnid3 = {R.id.zr_keybtn11, R.id.zr_keybtn12, R.id.zr_keybtn13, R.id.zr_keybtn14,
							R.id.zr_keybtn21, R.id.zr_keybtn22, R.id.zr_keybtn23, R.id.zr_keybtn24,
							R.id.zr_keybtn31, R.id.zr_keybtn32, R.id.zr_keybtn33, R.id.zr_keybtn34,
							R.id.zr_keybtn41, R.id.zr_keybtn42, R.id.zr_keybtn43, R.id.zr_keybtn44,
							R.id.zr_keybtn51, R.id.zr_keybtn52, R.id.zr_keybtn54};
	private int[] btnid4 = {R.id.zr_keybtn11, R.id.zr_keybtn12, R.id.zr_keybtn13, R.id.zr_keybtn14,R.id.zr_keybtn15,R.id.zr_keybtn16,R.id.zr_keybtn17,R.id.zr_keybtn18,
				R.id.zr_keybtn21, R.id.zr_keybtn22, R.id.zr_keybtn23, R.id.zr_keybtn24,R.id.zr_keybtn25,R.id.zr_keybtn26,R.id.zr_keybtn27,R.id.zr_keybtn28,
				R.id.zr_keybtn31, R.id.zr_keybtn32, R.id.zr_keybtn33, R.id.zr_keybtn34, R.id.zr_keybtn35, R.id.zr_keybtn36, R.id.zr_keybtn37, R.id.zr_keybtn38,
				R.id.zr_keybtn41, R.id.zr_keybtn42, R.id.zr_keybtn43, R.id.zr_keybtn44,R.id.zr_keybtn45,
				R.id.zr_keybtn51, R.id.zr_keybtn52, R.id.zr_keybtn54};
	
	private String kbtype="shuzi"; 
	
	private String[] btntitle;
	private String tempStr;
	private String keyBoardType;

	public VirtualKeyBoard(View paramView, String paramStr,String type) {
		this.m_context = paramView;
		this.localResources = paramView.getResources();
		kbtype=type;
		initData(paramStr);
		
	}

	
	private void initData(String paramStr){
		this.keyBoardType = paramStr;
		Log.i("keyBoardType", paramStr);
		Log.i("kbtype",kbtype);
			if ("STOCK".equals(paramStr) || "NUMBER".equals(paramStr)||"SMALL".equals(paramStr) || "BIGGER".equals(paramStr)) {
				if(kbtype.equals("shuzi")){
					btnid2=btnid3;
					tempStr = localResources.getText(R.string.csskeyboardstock, "").toString();
				}else{
					btnid2=btnid4;
					tempStr = localResources.getText(R.string.csskeyboardbig,"").toString();
				}
				btntitle = tempStr.split("\\|");
				initButtons(btntitle.length, btnid2);
			} 
			else {
				tempStr = localResources.getText(R.string.csskeyboardnum2, "")
						.toString();
				btntitle = tempStr.split("\\|");
				btnid2=btnid3;
				kbtype="shuzi";
				initButtons(btntitle.length, btnid2);
			}
	}


//	private void initData(String paramStr) {
//		this.keyBoardType = paramStr;
//		Log.i("keyBoardType", paramStr);
//		if(kbtype.equals("shuzi")){
//			if ("NUMBER".equals(paramStr)) {
//				tempStr = localResources.getText(R.string.csskeyboardquery, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
//				initButtons(btntitle.length, btnid2);
//			} else if ("SMALL".equals(paramStr)) {
////				tempStr = localResources.getText(R.string.csskeyboardsmall, "")
//				tempStr = localResources.getText(R.string.csskeyboardbig, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
//				initButtons(btntitle.length, btnid2);
//			} else if ("BIGGER".equals(paramStr)) {
//				tempStr = localResources.getText(R.string.csskeyboardbig, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
//				initButtons(btntitle.length, btnid2);
//			} else if ("STOCK".equals(paramStr)) {
//					
//				tempStr = localResources.getText(R.string.csskeyboardstock, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
//				initButtons(btntitle.length, btnid2);
//			} else if ("NUMDOT".equals(paramStr)) {
////				tempStr = localResources.getText(R.string.csskeyboardnumdot, "")
//				tempStr = localResources.getText(R.string.csskeyboardnum2, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
////				initButtons(11, btnid1);
//				initButtons(btntitle.length, btnid2);
//			} else {
////				tempStr = localResources.getText(R.string.csskeyboardnum, "")
//				tempStr = localResources.getText(R.string.csskeyboardnum2, "")
//						.toString();
//				btntitle = tempStr.split("\\|");
////				initButtons(11, btnid1);
//				initButtons(btntitle.length, btnid2);
//			}
//		}else{
//			tempStr = localResources.getText(R.string.csskeyboardbig,"").toString();
//			btntitle = tempStr.split("\\|");
//			initButtons(btntitle.length, btnid2);
//		}
//	}

	private void addButText(Button btn, String str, int tag) {
		if(btn!=null) {
			this.button = btn;
			this.button.setTag(tag);
			this.button.setText(str);
			this.button.setOnClickListener(myBar);
//			Log.i("width..........",width+"");
//			initButtonSize(this.button,tag);
		}
	}	
	
//	private int height=46;
//	private int width=480;
//	public void setHeight(int h){
//		this.height=h;
//	}
//	public void setWidth(int w){
//		this.width=w;
//	}
//	private void initButtonSize(Button btn,  int tag){
//		
//		if(kbtype.equals("shuzi")){
//			switch(tag){
//			case 17:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/2,height));
//				break;
//			default:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//			break;
//			}
//		}else{
//			switch(tag){
//			//26 27 28 29 30 31
//			case 26:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//				break;
//			case 27:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//				break;
//			case 28:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//				break;
//			case 29:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//				break;
//			case 30:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/2,height));
//				break;
//			case 31:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/4,height));
//				break;
//			default:
//				btn.setLayoutParams(new  LinearLayout.LayoutParams(width/8,height));
//				break;
//			}
//		}
//	}
	
	private void initButtons(int keyNums, int[] btn) {
		btns = new Button[keyNums];
		for(int i=0; i<btns.length; i++) {
			btns[i] = (Button) this.m_context.findViewById(btn[i]);
			addButText(btns[i], btntitle[i], i);
		}
	    ImageButton localImageButton = (ImageButton)this.m_context.findViewById(R.id.zr_keyimagebtn43);
	    if (localImageButton == null)
	      return;
	    localImageButton.setOnClickListener(new Button.OnClickListener() {

			public void onClick(View v) {
				delEditText();
			}
	    	
	    });
	}

	Button.OnClickListener myBar = new Button.OnClickListener() {
		public void onClick(View id) {
			int tag = (Integer) id.getTag();
			if(kbtype.equals("shuzi")){
				switch (tag) {
	//				case 15:
	//					if ("SMALL".equals(keyBoardType)||"BIGGER".equals(keyBoardType)) {
	//						initData("NUMBER");
	//					}
	//					else
	//						updateEditText(btntitle[15], 15);
	//					break;
					case 15:
						delEditText();
						break;
//					case 16:
//						if ("NUMBER".equals(keyBoardType))
//							initData("SMALL");
//						else if ("SMALL".equals(keyBoardType))
//							initData("BIGGER");
//						else if ("BIGGER".equals(keyBoardType))
//							initData("SMALL");
//						else if ("STOCK".equals(keyBoardType))
//							updateEditText(btntitle[16], 16);
//						break;
					case 17:
						activity.onHideKeyBoard();
						break;
					case 18:
	//					delEditText();
						if("STOCK".equals(keyBoardType) || "NUMBER".equals(keyBoardType)||"SMALL".equals(keyBoardType) || "BIGGER".equals(keyBoardType))
//						{}else
							activity.changeKeyBoard(kbtype,false);
						break;
					default:
						updateEditText(btntitle[tag], tag);
						break;
				}
			}else{
				switch (tag) {
				case 26:
					updateEditText("", 26);
					break;
				case 28:
					delEditText();
					break;
				case 29:
					updateEditText("",29);
					break;
				case 30:
					activity.onHideKeyBoard();
					break;
				case 31:
					activity.changeKeyBoard(kbtype,false);
					break;
				default:
					updateEditText(btntitle[tag],tag);
					break;
				}
			}
		}
	};

	private void updateEditText(String paramString, int paramInt) {
		Editable etext = null;
		StringBuffer sb = new StringBuffer();
		if("".equals(keyBoardType)) {
//			if(paramInt==9) {
//				activity.onHideKeyBoard();
//				return;
//			}
//			else {
				sb.append(edittext.getText());
				int selectLen = edittext.getSelectionStart();
				sb.insert(selectLen, paramString);
				//sb.append(paramString);
				edittext.setText(sb.toString());
				etext = edittext.getText(); 
				int newposition = selectLen+paramString.length() >= edittext.length() ? edittext.length() : selectLen+paramString.length() ;
				Selection.setSelection(etext, newposition );
//			}
		}
		else if ("SMALL".equals(keyBoardType)||"BIGGER".equals(keyBoardType)) {
			long l1 = new Date().getTime();
			if(l1-this.stexttime<=inputTime) {
				if(this.state == paramInt) {
					String str = edittext.getText().toString();
					sb.append(str.subSequence(0, str.length()-1));
					sb.append(paramString.substring(2));
					edittext.setText(sb.toString());
					etext = edittext.getText(); 
					Selection.setSelection(etext, edittext.length());
				}
				else {
					sb.append(edittext.getText());
					int selectLen = edittext.getSelectionStart();
					sb.insert(selectLen, paramString.substring(0,1) );
					//sb.append(paramString.substring(0,1));
					edittext.setText(sb.toString());
					etext = edittext.getText(); 
					int newposition = selectLen + paramString.substring(0,1).length() >= edittext.length() ? edittext.length() : paramString.substring(0,1).length() ;
					Selection.setSelection(etext, newposition );
				}
			}
			else {
				sb.append(edittext.getText());
				int selectLen = edittext.getSelectionStart();
				sb.insert(selectLen, paramString.substring(0,1) );
				//sb.append(paramString.substring(0,1));
				edittext.setText(sb.toString());
				etext = edittext.getText(); 
				int newposition = selectLen + paramString.substring(0,1).length() >= edittext.length() ? edittext.length() : paramString.substring(0,1).length() ;
				Selection.setSelection(etext, newposition );
			}
				
			this.stexttime = l1;
			this.state = paramInt;
		}
		else if ("NUMDOT".equals(keyBoardType)) {
			sb.append(edittext.getText());
			int selectLen = edittext.getSelectionStart();
			sb.insert(selectLen, paramString);
			//sb.append(paramString);
			edittext.setText(sb.toString());
			etext = edittext.getText(); 
			int newposition = selectLen+paramString.length() >= edittext.length() ? edittext.length() : selectLen+paramString.length() ;
			Selection.setSelection(etext, newposition );
		}
		else {
			sb.append(edittext.getText());
			int selectLen = edittext.getSelectionStart();
			//sb.append(paramString);
			sb.insert(selectLen, paramString);
			edittext.setText(sb.toString());
			etext = edittext.getText(); 
			int newposition = selectLen+paramString.length() >= edittext.length() ? edittext.length() : selectLen+paramString.length() ;
			Selection.setSelection(etext, newposition );
		}
		
		//Selection.setSelection(etext, edittext.length());
	}
	/**
	 * 获取光标位置开始删除
	 */
	private void delEditText() {
		Editable etext = edittext.getText();
		int selectLen = edittext.getSelectionStart();
		if (selectLen >0){
			etext.delete(selectLen - 1 , selectLen );
		}
		//int len = edittext.length();
		//if(len>0) {
		//	etext.delete(edittext.length()-1, edittext.length());
		//}
	}

	public void setUp(EditText paramEditText, CssKeyboardBase activity) {
		this.edittext = paramEditText;
		this.activity = activity;
	}
	

}
