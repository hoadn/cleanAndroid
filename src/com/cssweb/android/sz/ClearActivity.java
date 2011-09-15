package com.cssweb.android.sz;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.main.R;
import com.cssweb.android.util.ActivityUtil;

public class ClearActivity extends SettingBaseActivity {
	private ListView clearListView;
	
	private String [] params;
	
	private float density = 1.0f;
	
	private ArrayList<String> clearList = null;
	private Button clearButton;
	
	private boolean rebootflag;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.one_listview_setting);
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		density = dm.density;
		
		clearList = new ArrayList<String>();
		clearListView = (ListView) findViewById(R.id.clear);
		clearButton = (Button) findViewById(R.id.clearButton);
		clearButton.setVisibility(View.VISIBLE);
		params = getResources().getStringArray(
				R.array.njzq_clear_data); 
		for(String param : params){
			clearList.add(param);
		}
		
		clearButton.setOnClickListener(new View.OnClickListener() {
			
			public void onClick(View v) {
				if(clearList.size() > 0){
					new AlertDialog.Builder(ClearActivity.this)
					.setTitle("清空数据提示")
					.setMessage("您确定要清空上述勾选的数据吗？")
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int which) {
									for(String clearItemName : clearList){
										if("账户信息".equals(clearItemName)){
											ActivityUtil.clearAllAccount(ClearActivity.this);//清除账户
										}else if("服务密码".equals(clearItemName)){
											ActivityUtil.clearServicePwd(ClearActivity.this);
										}else if("手机号码".equals(clearItemName)){
											ActivityUtil.clearPhoneNum(ClearActivity.this);//清除手机号码
											//并且需要重新激活
											rebootflag = true;
										}else if("缓存数据".equals(clearItemName)){
											CssIniFile.DeletFilePath(ClearActivity.this);
											ActivityUtil.clearTradeOpenTime(ClearActivity.this);
											rebootflag = true;
										}
									}
									
									if(rebootflag){//重启
										ActivityUtil.restart(ClearActivity.this, 1);
										ClearActivity.this.finish();
									}
								}
							})
			        .setNegativeButton("取消", //设置“取消”按钮
			                new DialogInterface.OnClickListener() 
			                {
			                    public void onClick(DialogInterface dialog, int whichButton)
			                    {
			                    	
			                    }
			                })
					.show(); 
				}else {
					Toast.makeText(ClearActivity.this , "请选择数据后再点清除按钮", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		setAdapter(clearListView,params,getPosition(),density);
		initTitle(R.drawable.njzq_title_left_back, 0, "清除数据");
	}

	private ArrayList<Integer> getPosition(){
		ArrayList<Integer> position = new ArrayList<Integer>();
		
		for(int i = 0 ; i < params.length ; i++){
			position.add(i);
		}
		
		return position;
	}
	
	OnItemClickListener listener = new OnItemClickListener() {
		@SuppressWarnings("unchecked")
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			HashMap<String, Object> adapterMap = (HashMap<String, Object>) parent
				.getItemAtPosition(position);
			
			LinearLayout layout = (LinearLayout) view;
			ImageView imageView = (ImageView) layout.getChildAt(2);
			String itemName = (String) adapterMap.get("itemName");

			contains(clearList, itemName, imageView);
		}
	};
	
	private void contains(ArrayList<String> list, String itemName,
			ImageView imageView) {
		if (list.contains(itemName)) {
			list.remove(itemName);
			imageView.setVisibility(View.INVISIBLE);
		} else {
			list.add(itemName);
			imageView.setVisibility(View.VISIBLE);
		}
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
        rebootflag = false;
	}

	@Override
	public void setListener(ListView listView) {
		listView.setOnItemClickListener(listener);
	}
}
