package com.cssweb.android.domain;

import java.util.ArrayList;

public class MenuImages {
	private String serverUrl;
	private ArrayList<MenuImageUrl> imageUrls;
	
	
	
	public String getServerUrl() {
		return serverUrl;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public ArrayList<MenuImageUrl> getImageUrls() {
		return imageUrls;
	}
	public void setImageUrls(ArrayList<MenuImageUrl> imageUrls) {
		this.imageUrls = imageUrls;
	}
}
