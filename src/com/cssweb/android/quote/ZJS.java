package com.cssweb.android.quote;

import com.cssweb.android.common.Global;

import android.view.View;

public class ZJS extends QHSCBaseActivity {
	
	protected int getActivityKind() {
		return Global.ZJS;
	}
	
	@Override
	protected String[] gettoolbarname() {
		return new String[] { Global.TOOLBAR_MENU,"","","","",
				Global.TOOLBAR_REFRESH };
	}
	
	protected void toolBarClick(int tag, View v) {
		switch (tag) {
		case 0:
			onOption();
			break;
		case 5:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
}
