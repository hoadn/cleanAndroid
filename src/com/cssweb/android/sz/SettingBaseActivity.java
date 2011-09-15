package com.cssweb.android.sz;

import java.util.ArrayList;
import java.util.HashMap;

import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.LinearLayout.LayoutParams;

import com.cssweb.android.adapter.SettingAdapter;
import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.main.R;

public abstract class SettingBaseActivity extends CssBaseActivity{

	public void setAdapter(ListView listView, String[] params , ArrayList<Integer> positionList, float density) {
		ArrayList<HashMap<String, Object>> data = getData(params);
		SettingAdapter adapter = new SettingAdapter(data,this,positionList);

		LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(
				LayoutParams.FILL_PARENT,(int)(data.size() * 40 * density + 60 * density));
		listView.setLayoutParams(param);

		setListener(listView);
		listView.setAdapter(adapter);
	
	
	}
	
	public abstract void setListener(ListView listView);
	
	private ArrayList<HashMap<String, Object>> getData(String[] params) {
		ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();

		for (String param : params) {
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("itemselectedImage", R.drawable.listview_selected);// 添加图像资源的ID
			map.put("itemName", param);// 按序号做ItemText
			data.add(map);
		}
		return data;
	}
}
