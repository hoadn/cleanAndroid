/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)BaseActivity.java 下午10:26:59 2011-3-24
 */
package com.cssweb.android.base;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.adapter.MenuAdapter;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.custom.TransPanel;
import com.cssweb.android.domain.MenuImageUrl;
import com.cssweb.android.domain.MenuImages;
import com.cssweb.android.main.R;
import com.cssweb.android.service.AutoReceiver;
import com.cssweb.android.service.MenuService;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.util.MenuUtils;
import com.cssweb.android.util.NetUtils;
import com.cssweb.quote.util.StockInfo;

/**
 * 
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class BaseActivity extends Activity {

	private Animation hideMenu;
	private Animation showMenu;
	private TransPanel popupMenu;

	protected Menu mMenu;
	protected Button btnLeft;
	protected Button btnRight;
	protected TextView midText;
	protected int fromLoginActivityFlag = 1;
	protected boolean mLock = true;
	protected long timetips = 0;
	protected int isNetworkError = 0;

	protected View.OnClickListener toolbarClick;

	private boolean downloadingFlag = false;

	private AlarmManager alarmManager;
	private PendingIntent pendingIntent;

	private MenuImages images;

	private Handler mHandler;
	private MenuAdapter adapter;

	private ListView localListView;
	
	private BroadcastReceiver lockreceiver, homereceiver;
	private Toast toast;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		toolbarClick = new ToolbarClick();

		alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(this, AutoReceiver.class);
		pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
		
//		 ParamSetting.getInstance().setIsEnternal(false);
//	     ParamSetting.getInstance().setInterval(20000);
		ServiceControl sControl=new ServiceControl(this);
		ServiceControl.startScreenReceiver();
		ServiceControl.startHomeReceiver();
	     listenScreen();
	     sControl.startTrackService();

	}

	protected void initTitle(int resid1, int resid2, String str) {
		btnLeft = (Button) findViewById(R.id.zr_backmenu);
		btnRight = (Button) findViewById(R.id.zr_allmenu);
		midText = (TextView) findViewById(R.id.zr_maintitle);
		midText.setText(str);
		if (resid1 != 0) {
			btnLeft.setBackgroundResource(resid1);
		}
		if (resid2 != 0) {
			btnLeft.setBackgroundResource(resid2);
		}

		btnLeft.setTag(0);
		btnRight.setTag(1);

		btnLeft.setOnClickListener(titleBar);
		btnRight.setOnClickListener(titleBar);
	}

	protected void changeTitleBg() {
		RelativeLayout l = (RelativeLayout) findViewById(R.id.zr_headerBar);
		l.setBackgroundResource(R.drawable.njzq_title_red_bg);
	}

	Button.OnClickListener titleBar = new Button.OnClickListener() {
		public void onClick(View v) {
			int tag = (Integer) v.getTag();
			switch (tag) {
			case 0:
				onExit();
				break;
			case 1:
				downloadImage();
				openMenu();
				break;
			case 2:
				RefreshUI();
				break;
			case 3:// 选股按钮
				searchStock();
				break;
			case 4:// 重新加载allstock列表
				reLoadAllStock();
				break;
			}
		}
	};

	protected void RefreshUI() {

	}

	protected void searchStock() {

	}

	protected void reLoadAllStock() {

	}

	public void finish() {
		hidePop();
		super.finish();
	}

	protected void hidePop() {

	}

	protected void onExit() {
		finish();
	}

	/**
	 * 工具栏事件处理
	 * 
	 * @author HUJUN
	 * 
	 */
	protected class ToolbarClick implements View.OnClickListener {

		public void onClick(View v) {
			// 避免快速点击造成多次请求
			// if(timetips!=0&&DateTool.getLongTime()-timetips<Global.CLICK_RESPONSE_TIME)
			// {
			// return;
			// }
			if ((v != null) && (v.getTag() != null)) {
				// timetips = DateTool.getLongTime();
				int tag = (Integer) v.getTag();
				toolBarClick(tag, v);
			}
		}
	}

	protected void toolBarClick(int tag, View v) {

	}

	protected void changeBG() {
		RelativeLayout r = (RelativeLayout) findViewById(R.id.njzq_main_jlp);
		int level = TradeUser.getInstance().getUserLevel();
		switch (level) {
		case 1:
			r.setBackgroundResource(R.drawable.njzq_main_licai_bg);
			break;
		case 2:
			r.setBackgroundResource(R.drawable.njzq_main_licai_bg);
			break;
		case 3:
			r.setBackgroundResource(R.drawable.njzq_main_jin_bg);
			break;
		case 4:
			r.setBackgroundResource(R.drawable.njzq_main_baijin_bg);
			break;
		case 5:
			r.setBackgroundResource(R.drawable.njzq_main_zuanshi_bg);
			break;
		default:
			r.setBackgroundResource(R.drawable.njzq_main_fk_bg);
			break;
		}
	}

	protected CharSequence textToInt(int msg) {
		return getApplicationContext().getResources().getText(msg);
	}

	protected String[] textToArray(int msg) {
		return getApplicationContext().getResources().getStringArray(msg);
	}

	protected void openMenu() {
		int visible = popupMenu.getVisibility();
		if (visible == View.GONE) {
			popupMenu.startAnimation(showMenu);
			popupMenu.setVisibility(View.VISIBLE);
		} else {
			popupMenu.startAnimation(hideMenu);
			popupMenu.setVisibility(View.GONE);
		}
	}

	protected void initPopupWindow() {
		hideMenu = AnimationUtils.loadAnimation(this, R.anim.zr_popup_hide);
		showMenu = AnimationUtils.loadAnimation(this, R.anim.zr_popup_show);
		popupMenu = (TransPanel) findViewById(R.id.zr_popup_window);
		localListView = (ListView) findViewById(R.id.zr_menulist);

		initMenu();
		setAdapter(localListView);
	}

	private void initMenu() {
		try {
			images = MenuService.getMenuImageUrl(BaseActivity.this.getAssets()
					.open("menu_image_url.xml"));
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	private void setAdapter(ListView localListView , boolean flag) {
		int len = initBitmap();
		adapter = new MenuAdapter(this,len);
		localListView.setAdapter(adapter);
		
		localListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String menuName = (String)(parent.getItemAtPosition(position));
				MenuUtils.toMenu(BaseActivity.this, menuName);
				openMenu();
			}
		});
	}
	

	private void setAdapter(ListView localListView) {
		int len = getCustMenus().length;
		if(Config.mapBitmap != null  &&  Config.mapBitmap.size() > 0 ){
		}else {
			initBitmap();
		}
		adapter = new MenuAdapter(this,len);
		localListView.setAdapter(adapter);
		
		localListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String menuName = (String)(parent.getItemAtPosition(position));
				MenuUtils.toMenu(BaseActivity.this, menuName);
				openMenu();
			}
		});
	}
	
	private int initBitmap(){
		String[] menus = getCustMenus();
		try {
			if(images == null){
				initMenu();
			}
			if (images != null) {
				for (String menu : menus) {
					String imagename = getImageNameFromItemName(images, menu);
					Bitmap bitmap = null;
					HashMap<String,Object> map = new HashMap<String, Object>();
					if (imagename != null) {
						File file = new File(imagename);
						if (file.exists()) {
							BitmapFactory.Options options=new BitmapFactory.Options();
				             options.inSampleSize = 2;
							bitmap = BitmapFactory.decodeFile(imagename , options);
							map.put(String.valueOf(1), bitmap); 
							map.put("itemname", menu);
							Config.mapBitmap.add(map);
						}else{
							
						}
					}
					
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return 10;
		}
		return menus.length;
	}


	private String getImageNameFromItemName(MenuImages menuImages, String menu) {
		try {
			for (MenuImageUrl imageUrl : menuImages.getImageUrls()) {
				if (imageUrl.getItemName().equals(menu)) {
					String filename = "/data/data/com.cssweb.android.main/files/"
							+ imageUrl.getImageName() + imageUrl.getDpiExt();
					File file = new File(filename);
					if (file.exists()) {
						return filename;
					}
				}
			}
		} catch (Exception e) {
			return null;
		}
		return null;
	}

	private String[] getCustMenus() {
		String[] menus;
		ArrayList<String> menuList = new ArrayList<String>();

		SharedPreferences sharedPreferences = getSharedPreferences(
				"customsetting", Context.MODE_PRIVATE);

		Boolean saveFlag = sharedPreferences.getBoolean("saveFlag", false);
		if (!saveFlag) {
			String[] defaultSetting = getResources().getStringArray(
					R.array.njzq_default_setting);

			for (String defaultStr : defaultSetting) {
				addMenuItem(menuList, defaultStr);
			}

		} else {
			String baojia = sharedPreferences.getString("baojia", "");
			String hudong = sharedPreferences.getString("hudong", "");
			String yyting = sharedPreferences.getString("yyting", "");
			String fengcai = sharedPreferences.getString("fengcai", "");

			String shangcheng = sharedPreferences.getString("shangcheng", "");
			String luopang = sharedPreferences.getString("luopang", "");
			String baodian = sharedPreferences.getString("baodian", "");

			addMenuItem(menuList, baojia);
			addMenuItem(menuList, hudong);
			addMenuItem(menuList, yyting);
			addMenuItem(menuList, fengcai);

			addMenuItem(menuList, shangcheng);
			addMenuItem(menuList, luopang);
			addMenuItem(menuList, baodian);
		}
		int size = menuList.size();
		if (size <= 10) {
			for (int i = 0; i < (10 - size); i++) {
				menuList.add(" ");
			}
		}
		menus = new String[menuList.size()];
		for (int i = 0; i < menuList.size(); i++) {
			menus[i] = menuList.get(i);
		}

		return menus;
	}

	private void addMenuItem(ArrayList<String> menuList, String itemName) {
		String[] items = itemName.split(",");
		for (String item : items) {
			if (item != null && !"".equals(item)) {
				menuList.add(item);
			}
		}
	}

	@Override
	protected void onDestroy() {
		ServiceControl.stopReceiver(BaseActivity.this, lockreceiver, homereceiver);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// Log.i("==========", "======onPause===");
		super.onPause();
		ActivityUtil.ALARM_RECORED = -1;
		// ActivityUtil.clearAlarmRecord(BaseActivity.this);
		alarmManager
				.setRepeating(AlarmManager.RTC, 0, 60 * 1000, pendingIntent);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	private void downloadImage() {
		// 下载快捷菜单图片
		if (!downloadingFlag) {
			downloadingFlag = true;
			mHandler = new Handler() {
				@Override
				public void handleMessage(Message msg) {
					super.handleMessage(msg);
					switch (msg.what) {
					case 1:
						setAdapter(localListView,true);
						break;
					default:
						break;
					}
				};
			};
			
			if(images == null){
				initMenu();
			}

			new Thread(new Runnable() {
				public void run() {
					try {
						boolean flag = NetUtils.download(BaseActivity.this, images);
						if (flag) {
							Message msg = new Message();
							msg.what = 1;
							mHandler.sendMessage(msg);
						}
					} catch (Exception e) {
					}
				}
			}).start();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmManager.cancel(pendingIntent);
		if (ActivityUtil.ALARM_RECORED >= Config.lockTime) {
			finish();
			this.mLock = false;
			ActivityUtil.restart(this, 0);
		}
		ActivityUtil.ALARM_RECORED = -1;
		HashMap<String, Integer> hashMap = StockInfo.hashMap;
		if (hashMap.isEmpty()) {
			String jsonObject = CssIniFile.loadStockData(BaseActivity.this,
					CssIniFile.GetFileName(CssIniFile.UserStockFile));
			JSONObject quoteData;
			try {
				if (jsonObject != null) {
					quoteData = new JSONObject(jsonObject);
					StockInfo.initAllStock(quoteData);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		ServiceControl.resumeHomeReceiver();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
		// Log.i("==========", "======onStop===");
	}
	 
	 private void listenScreen() {
	     lockreceiver = new BroadcastReceiver() {
	 
	         @Override
	         public void onReceive(Context context, Intent intent) {
	 
	             ServiceControl.pauseScreenReceiver();
	         }
	     };
	     registerReceiver(lockreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
	     homereceiver = new BroadcastReceiver() {
	         @Override
	         public void onReceive(Context context, Intent intent) {
	 
	             ServiceControl.resumeScreenReceiver();
	         }
	     };
	     registerReceiver(homereceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
	 
	 }
	 
	 /**
	  * 自定义toast
	  */
		
		protected void showToast2(){
			 if (null == toast){
	             toast = Toast.makeText(BaseActivity.this, "",Toast.LENGTH_LONG);
	             toast.setDuration(Toast.LENGTH_SHORT);
	         }
			 toast.setText("已经是最后一页");
			toast.show();
		}
	 
}
