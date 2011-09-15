package com.cssweb.android.sz;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.cssweb.android.common.Config;
import com.cssweb.android.main.R;

public class LockSettingActivity extends SettingBaseActivity {
	private ListView serverListView;
	
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	private String [] params;
	
	private float density = 1.0f;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.one_listview_setting);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		sharedPreferences = getSharedPreferences(
				"lock", Context.MODE_PRIVATE);
		String tmp = sharedPreferences.getString("locktime", "");
		editor = sharedPreferences.edit();// 获取编辑器
		if("".equals(tmp)){
			editor.putString("locktime","5");
			editor.commit();// 提交修改
		}
		serverListView = (ListView) findViewById(R.id.clear);

		params = getResources().getStringArray(
				R.array.njzq_lock);
		
		setAdapter(serverListView,params ,getPosition(sharedPreferences.getString("locktime", "5")),density);

		initTitle(R.drawable.njzq_title_left_back, 0, "系统锁定时间设置");
	}

	private ArrayList<Integer> getPosition(String itemName){
		ArrayList<Integer> position = new ArrayList<Integer>();
		
		for(int i = 0 ; i < params.length ; i++){
			String item = params[i].substring(0, params[i].indexOf("("));
			if(itemName.equals(item)){
				position.add(i);
			}
		}
		return position;
	}
	

	OnItemClickListener listener = new OnItemClickListener() {
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			
			HashMap<String, Object> adapterMap = (HashMap<String, Object>) parent
			.getItemAtPosition(position);
			String itemName = (String) adapterMap.get("itemName");
			String item = itemName.substring(0, itemName.indexOf("("));
			editor.putString("locktime",item);
			editor.commit();// 提交修改
			
			
			for(int i= 0 ; i <parent.getCount() ; i++){
				LinearLayout layout1 = (LinearLayout) parent.getChildAt(i);
				ImageView imageView1= (ImageView) layout1.getChildAt(2);
				if( i != id){
					imageView1.setVisibility(View.INVISIBLE);
				}else if(i == id){
					imageView1.setVisibility(View.VISIBLE);
					
					//TODO
				}
			}
		}
	};

	@Override
	public void setListener(ListView listView) {
		listView.setOnItemClickListener(listener);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		new Config(LockSettingActivity.this);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        initPopupWindow();
	}
}
