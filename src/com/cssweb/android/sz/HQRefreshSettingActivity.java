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

public class HQRefreshSettingActivity extends SettingBaseActivity {
	private ListView fenleiRefreshListView;
	private ListView zongHerefreshListView;

	private ListView fenshiRefreshListView;
	private ListView kxRefreshListView;
	private ListView yujingRefreshListView;
	
	private SharedPreferences sharedPreferences;
	private Editor editor;
	
	private String [] params;
	
	private float density = 1.0f;
	
//	private LinearLayout layout = null;
//	private ImageView imageView= null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.hqrefresh_settring);
		
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		sharedPreferences = getSharedPreferences(
				"hqrefresh", Context.MODE_PRIVATE);
		
		Boolean saveFlag = sharedPreferences.getBoolean("saveFlag", false);
		editor = sharedPreferences.edit();// 获取编辑器
		if (!saveFlag) {
			editor.putString("fenleirefresh","3");
			editor.putString("zongherefresh","3");
			editor.putString("fenshirefresh","3");
			editor.putString("kxrefresh","3");
			editor.putString("yujingrefresh","3");
			editor.commit();// 提交修改
		}

		fenleiRefreshListView = (ListView) findViewById(R.id.fenleirefresh);
		zongHerefreshListView = (ListView) findViewById(R.id.zongherefresh);
		fenshiRefreshListView = (ListView) findViewById(R.id.fenshirefresh);
		kxRefreshListView = (ListView) findViewById(R.id.kxrefresh);
		yujingRefreshListView = (ListView) findViewById(R.id.yujingrefresh);

		params = getResources().getStringArray(
				R.array.njzq_fenleirefresh);
		setAdapter(fenleiRefreshListView,params ,getPosition(sharedPreferences.getString("fenleirefresh", "3")),density);
		
		params = getResources().getStringArray(
				R.array.njzq_zongherefresh);
		setAdapter(zongHerefreshListView,params ,getPosition(sharedPreferences.getString("zongherefresh", "3")),density);
		
		params = getResources().getStringArray(
				R.array.njzq_fenshirefresh);
		setAdapter(fenshiRefreshListView, params , getPosition(sharedPreferences.getString("fenshirefresh", "3")),density );
		
		params = getResources().getStringArray(
				R.array.njzq_kxrefresh);
		setAdapter(kxRefreshListView, params, getPosition(sharedPreferences.getString("kxrefresh", "3")),density );

		params = getResources().getStringArray(
				R.array.njzq_yujingrefresh);
		setAdapter(yujingRefreshListView, params , getPosition(sharedPreferences.getString("yujingrefresh", "3")),density );

		
		initTitle(R.drawable.njzq_title_left_back, 0, "行情刷新时间");
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
			
			editor.putBoolean("saveFlag", true);
			editor.commit();// 提交修改
			
			String item = itemName.substring(0, itemName.indexOf("("));
			switch (parent.getId()) {
			case R.id.fenleirefresh:
				editor.putString("fenleirefresh",item);
				editor.commit();// 提交修改
				break;
			case R.id.zongherefresh:
				editor.putString("zongherefresh",item);
				editor.commit();// 提交修改
				break;
			case R.id.fenshirefresh:
				editor.putString("fenshirefresh",item);
				editor.commit();// 提交修改
				break;
			case R.id.kxrefresh:
				editor.putString("kxrefresh",item);
				editor.commit();// 提交修改
				break;
			case R.id.yujingrefresh:
				editor.putString("yujingrefresh",item);
				editor.commit();// 提交修改
				break;
			default:
				break;
			}
			
			
			for(int i= 0 ; i <parent.getCount() ; i++){
				LinearLayout layout1 = (LinearLayout) parent.getChildAt(i);
				ImageView imageView1= (ImageView) layout1.getChildAt(2);
				if( i != id){
					imageView1.setVisibility(View.INVISIBLE);
				}else if(i == id){
					imageView1.setVisibility(View.VISIBLE);
				}
			}
		}
	};

	@Override
	public void setListener(ListView listView) {
		// TODO Auto-generated method stub
		listView.setOnItemClickListener(listener);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		new Config(HQRefreshSettingActivity.this);
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        initPopupWindow();
	}
}
