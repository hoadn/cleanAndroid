package com.cssweb.android.sz;

import android.os.Bundle;
import android.widget.ListView;

import com.cssweb.android.main.R;

public class HelpActivity extends SettingBaseActivity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.njzq_help);

	

		initTitle(R.drawable.njzq_title_left_back, 0, "帮助说明");
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        initPopupWindow();
	}

	@Override
	public void setListener(ListView listView) {
		// TODO Auto-generated method stub
		
	}
}
