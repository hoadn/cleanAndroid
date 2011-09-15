package com.cssweb.android.util;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.domain.Menu;
import com.cssweb.android.domain.Menus;
import com.cssweb.android.service.MenuService;

public class MenuUtils {
	private static Menus menus ;
	private static Menu menu;
	
    private static Menus getMenus(Context context) throws UnsupportedEncodingException{
		try {
			InputStream inputStream=context.getAssets().open("menu.xml");
			menus = MenuService.getServer(inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return menus;
	}
    
    private static Menu getMenu(String menuName){
    	Menu menu = null;
    	for(Menu tmp : menus.getMenuList()){
    		if(menuName.equalsIgnoreCase(tmp.getMenuName())){
    			menu = tmp;
    		}
    	}
    	return menu;
    }

	public static void toMenu(Context context , String menuName) {
		if(null == menuName || "".equals(menuName)){
			return;
		}
		try {
			if(null == menus){
				menus = getMenus(context);
			}
			//防止无限启动当前activity
			ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
            ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
            if(menu != null && menuName.equals(menu.getMenuName()) && null != cn.getClassName() && cn.getClassName().equals(menu.getMenuClass())){
            
                return ;
            }
			menu = getMenu(menuName);
			if(menu != null ){
			   
				if(menu.isNeedLogin()){
					FairyUI.switchToWnd(menu.getPos(),menu.getPos(), context);
				}else {
					FairyUI.switchToWnd(menu.getPos(),menu.getPosition(),null ,null, context);
				}
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
}
