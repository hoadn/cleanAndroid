package com.cssweb.android.sz;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.main.R;

public class Setting extends CssBaseActivity{
	private ListView listView;
	
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.njzq_setting);
		listView = (ListView) findViewById(R.id.setting);
		
		setAdapter(listView, getResources().getStringArray(R.array.njzq_setting));
		
		initTitle(R.drawable.njzq_title_left_back, 0, "设置");
	}
	
	private void setAdapter(ListView listView , String[] params){
		ArrayList<HashMap<String, Object>> data = getData(params);
		BaseAdapter adapter = new SimpleAdapter(this, data,
				R.layout.setting_list_item, new String[] { "itemName",
						"itemselectedImage" }, new int[] { R.id.itemName,
						R.id.itemselectedImage });
		
		listView.setOnItemClickListener(listener);
		
		listView.setAdapter(adapter);
	}
	
	OnItemClickListener listener = new OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int position,
                long id) {
/*
        	Log.i("tag",parent.getItemAtPosition(position).toString());
        	HashMap<String, Object> adapterMap = (HashMap<String, Object>) parent.getItemAtPosition(position);
        	Boolean flag = (Boolean) adapterMap.get("flag");
        	LinearLayout layout = (LinearLayout) view;
        	ImageView imageView = (ImageView) layout.getChildAt(2);
        	String itemName = (String) adapterMap.get("itemName");*/
        	
        	switch (position) {
			case 0:
				Intent clearIntent = new Intent(Setting.this, ClearActivity.class);  
				 startActivity(clearIntent);  
				break;
			case 1:
				   Intent intent = new Intent(Setting.this, HQRefreshSettingActivity.class);  
				   startActivity(intent);  
				                 
				  //添加界面切换效果，注意只有Android的2.0(SdkVersion版本号为5)以后的版本才支持  
				    int version = Integer.valueOf(android.os.Build.VERSION.SDK);     
				    if(version  >= 5) {     
//				         overridePendingTransition(R.anim.push_right_in_layout, R.anim.push_left_out_layout);  //此为自定义的动画效果，下面两个为系统的动画效果  
				      //overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);    
//				        overridePendingTransition(android.R.anim.slide_out_right,android.R.anim.slide_in_left);  
				   }    
				break;
			case 2:
				 Intent serverIntent = new Intent(Setting.this, ServerSettingActivity.class);  
				 startActivity(serverIntent);  
				break;
			case 3:
				Intent customSettingIntent = new Intent(Setting.this, CustomSettingActivity.class);  
				 startActivity(customSettingIntent);  
				break;
			case 4:
				Intent lockIntent = new Intent(Setting.this, LockSettingActivity.class);  
				 startActivity(lockIntent);  
				
				break;
			case 5:
				
				Intent helpIntent = new Intent(Setting.this, HelpActivity.class);  
				 startActivity(helpIntent);  
				
				break;
			default:
				break;
			}
        }
    };
    
	
	private ArrayList<HashMap<String, Object>> getData(String[] params){
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
		
		for(int i= 0 ; i < params.length ; i++){
			HashMap<String, Object> map = new HashMap<String, Object>();
//			if(i == 0 ){
//				map.put("itemselectedImage", R.drawable.back);// 添加图像资源的ID
//			}else{
				map.put("itemselectedImage", R.drawable.arrow_tiem);// 添加图像资源的ID
//			}
			map.put("itemName", params[i]);// 按序号做ItemText
			data.add(map);
		}
		
		return data;
	}
	
	@Override
    protected void onResume() {
        super.onResume();
        initPopupWindow();
	}
}
