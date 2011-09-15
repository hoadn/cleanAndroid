package com.cssweb.android.service;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import com.cssweb.android.domain.HQFWQ;
import com.cssweb.android.domain.JYWG;
import com.cssweb.android.domain.Road;
import com.cssweb.android.domain.Server;

import android.content.Context;
import android.util.Xml;

public class XMLService {
	
	public static void saveAsXMLFile(HashMap<String, Boolean> map, Context context) {
		try {
//			File xmlFile = new File("/sdcard/itemNames.xml");
//			FileOutputStream outStream = new FileOutputStream(xmlFile);
//			FileOutputStream outStream = context.openFileOutput("menu.xml", Context.MODE_PRIVATE);
			File file = new File("/data/data/com.cssweb.android.main/files","menu.xml");
			FileOutputStream outStream = new FileOutputStream(file);
			OutputStreamWriter outStreamWriter = new OutputStreamWriter(
					outStream, "UTF-8");
			BufferedWriter writer = new BufferedWriter(outStreamWriter);
			writeXML(map, writer);
			writer.flush();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String writeXML(HashMap<String, Boolean> map, Writer writer) {
		XmlSerializer serializer = Xml.newSerializer();
		try {
			serializer.setOutput(writer);
			serializer.startDocument("UTF-8", true);
			//
			serializer.startTag("", "Item");
			for (String itemName : map.keySet()) {

				writexmlitem(serializer, "itemName", itemName);

			}
			serializer.endTag("", "Item");
			serializer.endDocument();

			return writer.toString();
		} catch (Exception e) {

		}
		return null;
	}

	private static void writexmlitem(XmlSerializer serializer, String name,
			String value) throws Exception {

		serializer.startTag("", name);
		serializer.text(value);
		serializer.endTag("", name);

	}
	
	
	public static Server getServer(InputStream inputStream) throws Exception {

		XmlPullParser xmlpull = Xml.newPullParser();
		xmlpull.setInput(inputStream, "utf-8");

		Server server = null;
		ArrayList<Road> roads = null;
		Road road = null;
		JYWG jywg = null;
		HQFWQ hqfwq = null;
		
		int eventCode = xmlpull.getEventType();
		while (eventCode != XmlPullParser.END_DOCUMENT) {
			switch (eventCode) {
			case XmlPullParser.START_DOCUMENT: {// 
				server = new Server();
				
				break;
			}
			case XmlPullParser.START_TAG: {
				if ("defaultServerUrl".equals(xmlpull.getName()) && server != null) {
					String defaultServerUrl = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(defaultServerUrl)) {
						server.setDefaultServerUrl(defaultServerUrl);
					}
				} else if ("currServerUrl".equals(xmlpull.getName() )
						&& server != null) {
					String currServerUrl = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(currServerUrl)) {
						server.setCurrServerUrl(currServerUrl);
					}
				} else if("version".equals(xmlpull.getName()) && server != null){
					String version = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(version)) {
						server.setVersion(version);
					}
				} else if("roads".equals(xmlpull.getName()) && server != null){
					roads = new ArrayList<Road>();
				}else if("dict".equals(xmlpull.getName()) && server != null && roads != null){
					road = new Road();
				}else if("name".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String name = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(name)) {
						road.setName(name);
					}
				}else if("zixun".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String zixun = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(zixun)) {
						road.setZixun(zixun);
					}
				}else if("wsyyt".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String wsyyt = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(wsyyt)) {
						road.setWsyyt(wsyyt);
					}
				}else if("webcall".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String webcall = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(webcall)) {
						road.setWebcall(webcall);
					}
				}else if("mncg".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String mncg = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(mncg)) {
						road.setMncg(mncg);
					}
				}else if("hqyj".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String hqyj = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(hqyj)) {
						road.setHqyj(hqyj);
					}
				}else if("validation".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					String validation = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(validation)) {
						road.setValidation(validation);
					}
				}else if("jywg".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					jywg = new JYWG();
				}else if("ip".equals(xmlpull.getName()) && server != null && roads != null && road != null && jywg != null){
					String ip = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(ip)) {
						jywg.setIp(ip);
					}
				}else if("port".equals(xmlpull.getName()) && server != null && roads != null && road != null && jywg != null){
					String port = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(port)) {
						jywg.setPort(port);
					}
				}else if("hqfwq".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					hqfwq = new HQFWQ();
				}else if("ip".equals(xmlpull.getName()) && server != null && roads != null && road != null && hqfwq != null){
					String ip = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(ip)) {
						hqfwq.setIp(ip);
					}
				}else if("port".equals(xmlpull.getName()) && server != null && roads != null && road != null && hqfwq != null){
					String port = xmlpull.nextText().trim();
					if (!"".equalsIgnoreCase(port)) {
						hqfwq.setPort(port);
					}
				}
				
				break;
			}
			case XmlPullParser.END_TAG: {
				if("jywg".equals(xmlpull.getName()) && server != null && roads != null && road != null && jywg != null){
					road.setJywg(jywg);
					jywg = null;
				}else if("hqfwq".equals(xmlpull.getName()) && server != null && roads != null && road != null && hqfwq != null){
					road.setHqfwq(hqfwq);
					hqfwq = null;
				}else if("dict".equals(xmlpull.getName()) && server != null && roads != null && road != null){
					roads.add(road);
					road = null;
				}else if("roads".equals(xmlpull.getName()) && server != null && roads != null){
					server.setRoads(roads);
//					roads = null;
				}
				break;
			}
			}
			eventCode = xmlpull.next();
		}
		return server;
	}
}
