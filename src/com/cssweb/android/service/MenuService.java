package com.cssweb.android.service;

import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.cssweb.android.domain.Menu;
import com.cssweb.android.domain.MenuImageUrl;
import com.cssweb.android.domain.MenuImages;
import com.cssweb.android.domain.Menus;

public class MenuService {
	public static MenuImages getMenuImageUrl(InputStream inputStream) throws Exception {
		XmlPullParser xmlpull = Xml.newPullParser();
		xmlpull.setInput(inputStream, "utf-8");

		MenuImages menuImages = null;
		MenuImageUrl imageUrl = null;
		ArrayList<MenuImageUrl> imageUrls = null;
		
		int eventCode = xmlpull.getEventType();
		while (eventCode != XmlPullParser.END_DOCUMENT) {
			switch (eventCode) {
			case XmlPullParser.START_DOCUMENT: {// 
				menuImages = new MenuImages();
				imageUrls = new ArrayList<MenuImageUrl>();
				break;
			}
			case XmlPullParser.START_TAG: {
				if ("serverUrl".equals(xmlpull.getName()) && menuImages != null) {
					String serverUrl = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(serverUrl)) {
						menuImages.setServerUrl(serverUrl);
					}
				}
				if ("image".equals(xmlpull.getName()) && menuImages != null && imageUrls != null) {
					imageUrl = new MenuImageUrl();
				} else if ("imagename".equals(xmlpull.getName() )
						&& menuImages != null && imageUrls != null && imageUrl != null) {
					String imagename = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(imagename)) {
						imageUrl.setImageName(imagename);
					}
				} else if("dpiext".equals(xmlpull.getName())
						&& menuImages != null && imageUrls != null && imageUrl != null){
					String dpiext = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(dpiext)) {
						imageUrl.setDpiExt(dpiext);
					}
				}  else if("imageitem".equals(xmlpull.getName())
						&& menuImages != null && imageUrls != null && imageUrl != null){
					String imageitem = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(imageitem)) {
						imageUrl.setItemName(imageitem);
					}
				}
				break;
			}
			case XmlPullParser.END_TAG: {
				if("image".equals(xmlpull.getName()) && menuImages != null && imageUrls != null && imageUrl != null){
					imageUrls.add(imageUrl);
					imageUrl = null;
				}else if("images".equals(xmlpull.getName()) && imageUrls != null && menuImages != null){
					menuImages.setImageUrls(imageUrls);
				}
				break;
			}
			}
			eventCode = xmlpull.next();
		}
		return menuImages;
	}
	
	
	public static Menus getServer(InputStream inputStream) throws Exception {
		XmlPullParser xmlpull = Xml.newPullParser();
		xmlpull.setInput(inputStream, "utf-8");

		Menus menus = null;
		Menu menu = null;
		ArrayList<Menu> menuList = null;
		
		int eventCode = xmlpull.getEventType();
		while (eventCode != XmlPullParser.END_DOCUMENT) {
			switch (eventCode) {
			case XmlPullParser.START_DOCUMENT: {// 
				menus = new Menus();
				menuList = new ArrayList<Menu>();
				break;
			}
			case XmlPullParser.START_TAG: {
				if ("menu".equals(xmlpull.getName()) && menuList != null && menus != null) {
					menu = new Menu();
				} else if ("menuName".equals(xmlpull.getName() )
						&& menuList != null && menus != null && menu != null) {
					String menuNmae = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(menuNmae)) {
						menu.setMenuName(menuNmae);
					}
				} else if("menuClass".equals(xmlpull.getName())
						&& menuList != null && menus != null && menu != null){
					String menuClass = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(menuClass)) {
						menu.setMenuClass(menuClass);
					}
				}  else if("pos".equals(xmlpull.getName())
						&& menuList != null && menus != null && menu != null){
					String pos = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(pos)) {
						menu.setPos(Integer.parseInt(pos));
					}
				} else if("position".equals(xmlpull.getName())
						&& menuList != null && menus != null && menu != null){
					String position = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(position)) {
						menu.setPosition(Integer.parseInt(position));
					}
				} else if("menu_id".equals(xmlpull.getName())
						&& menuList != null && menus != null && menu != null){
					String menu_id = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(menu_id)) {
						menu.setMenu_id(Integer.parseInt(menu_id));
					}
				}else if("isneedlogin".equals(xmlpull.getName())
						&& menuList != null && menus != null && menu != null){
					String isneedlogin = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(isneedlogin)) {
						menu.setNeedLogin(Boolean.parseBoolean(isneedlogin));
					}
				}
				
				break;
			}
			case XmlPullParser.END_TAG: {
				if("menu".equals(xmlpull.getName()) && menuList != null && menus != null && menu != null){
					menuList.add(menu);
					menu = null;
				}else if("menus".equals(xmlpull.getName()) && menuList != null && menus != null){
					menus.setMenuList(menuList);
				}
				break;
			}
			}
			eventCode = xmlpull.next();
		}
		return menus;
	}
}
