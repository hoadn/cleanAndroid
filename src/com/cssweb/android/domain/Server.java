package com.cssweb.android.domain;

import java.util.ArrayList;

public class Server {
	private String defaultServerUrl;
	private String currServerUrl;
	private String version;

	private ArrayList<Road> roads;

	public Road getRoad(String roadName) {
		Road road = null;
		for (Road tmpRoad : roads) {
			if (tmpRoad.getName().equals(roadName)) {
				road = tmpRoad;
			}
		}
		return road;
	}

	public String getDefaultServerUrl() {
		return defaultServerUrl;
	}

	public void setDefaultServerUrl(String defaultServerUrl) {
		this.defaultServerUrl = defaultServerUrl;
	}

	public String getCurrServerUrl() {
		return currServerUrl;
	}

	public void setCurrServerUrl(String currServerUrl) {
		this.currServerUrl = currServerUrl;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public ArrayList<Road> getRoads() {
		return roads;
	}

	public void setRoads(ArrayList<Road> roads) {
		this.roads = roads;
	}

}
