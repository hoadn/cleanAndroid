package com.cssweb.android.domain;

public class Menu {
	private String menuName;
	private String menuClass;
	private int pos;
	private int position;
	private int menu_id;
	
	private boolean needLogin = false;
	
	
	public String getMenuName() {
		return menuName;
	}
	public void setMenuName(String menuName) {
		this.menuName = menuName;
	}
	public String getMenuClass() {
		return menuClass;
	}
	public void setMenuClass(String menuClass) {
		this.menuClass = menuClass;
	}
	public int getPos() {
		return pos;
	}
	public void setPos(int pos) {
		this.pos = pos;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public int getMenu_id() {
		return menu_id;
	}
	public void setMenu_id(int menuId) {
		menu_id = menuId;
	}
	public boolean isNeedLogin() {
		return needLogin;
	}
	public void setNeedLogin(boolean needLogin) {
		this.needLogin = needLogin;
	}
}
