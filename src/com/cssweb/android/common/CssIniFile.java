/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)CssIniFile.java 上午10:11:42 2010-11-26
 */
package com.cssweb.android.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

/**
 * 文件系统
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class CssIniFile {

	public static final int SysSettingPath_ = 1;

	public static final int UserStockFile = 2;
	
	public static final int Fund_Company_File = 3;
	
	public static final int Fund_Info_File = 4;
	
	public static final int Fund_Account_File = 5;
	
	public static final int StockInfoFile = 6;
	
	public static final int HKStockFile = 7;
	
	public static final int Banks_File = 8;
	
	public static final int Shareholders_File = 9;

	File file = null;

	public static String GetFileName(int paramInt) {
		String str1 = "system.cfg";
		switch (paramInt) {
		case SysSettingPath_:
			str1 = "system.cfg";
			return str1;
		case UserStockFile:
			str1 = "userstock";
			return str1;
		case Fund_Company_File:
			str1 = "fundCompany.dat";
			return str1;
		case Fund_Info_File:
			str1 = "fundInfo.dat";
			return str1;
		case Fund_Account_File:
			str1 = "fundAccount.dat";
			return str1;
		case Banks_File:
			str1 = "banks.dat";
			return str1;
		case Shareholders_File:
			str1 = "ShareholdersList.dat";
			return str1;
		case StockInfoFile:
			str1 = "stockinfo.dat";
			return str1;
		case HKStockFile:
			str1 = "hkstock";
			return str1;
		}
		return str1;
	}

	public static String GetFileName(String paramStr) {
		String str = paramStr + ".dat";
		return str;
	}

	public static String loadIni(Context context, int parmInt, String key) {
		String str = null;
		Properties properties = new Properties();
		try {
			FileInputStream stream = context
					.openFileInput(GetFileName(parmInt));
			properties.load(stream);
			Object obj = properties.get(key);
			if(obj!=null)
				str = obj.toString();
			stream.close();
		} catch (FileNotFoundException e) {

		} catch (IOException e) {

		}
		return str;
	}

	public static void saveIni(Context context, int parmInt, String key,
			String value) {
		FileOutputStream fileOut = null;
		Properties properties = new Properties();
		properties.put(key, value);
		try {
			fileOut = context.openFileOutput(
					GetFileName(parmInt), Context.MODE_PRIVATE);//
			properties.store(fileOut, "");
		} catch (FileNotFoundException e) {
			
		} catch (IOException e) {
			
		} finally {
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static boolean saveIniWithAPPEND(Context context, int parmInt, String key,
			String value) {
		FileOutputStream fileOut = null;
		Properties properties = new Properties();
		properties.put(key, value);
		try {
			fileOut = context.openFileOutput(
					GetFileName(parmInt), Context.MODE_APPEND);//
			properties.store(fileOut, "");
			fileOut.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean delIniWithAPPEND(Context context, int parmInt, String key) {
		Properties properties = new Properties();
		try {
			FileOutputStream stream = context.openFileOutput(
					GetFileName(parmInt), Context.MODE_APPEND);//
			properties.remove(key);
			stream.close();
		} catch (FileNotFoundException e) {
			return false;
		} catch (IOException e) {
			return false;
		}
		return true;
	}
	
	public static boolean DeletFilePath(Context context) {
		File file = context.getFilesDir();
		boolean flag = delDir(file);
		return flag;
	}
	
	private static boolean delDir(File dir) {  
        if (dir == null || !dir.exists() || dir.isFile()) {  
            return false;  
        }  
        for (File file : dir.listFiles()) {  
            if (file.isFile()) {  
                file.delete();  
            } else if (file.isDirectory()) {  
                delDir(file);// 递归  
            }  
        }  
        dir.delete();  
        return true;  
    }
	
	/**
	 * 以下代码用来处理K线数据
	 * @throws JSONException 
	 * 
	 **/
	public static JSONObject getStockInfoByKlineURL(Context context, String url) throws JSONException {
		String jsonstr = loadIni(context, StockInfoFile, url);
		if(jsonstr!=null&&!"".equals(jsonstr)) {
			JSONObject j = new JSONObject(jsonstr);
			return j;
		}
		return null;
	}
	
	public static boolean saveStockData(Context context, String filename, String filecontent) {
		FileOutputStream fileOut = null;
		try {
        	fileOut = context.openFileOutput(filename + ".dat",Context.MODE_PRIVATE);
        	fileOut.write(filecontent.getBytes());
        	fileOut.close();
        } catch (FileNotFoundException e) {
            return false;
        }
        catch (IOException e){
            return false;
        }
        return true;
    }
	
	public static String loadStockData(Context context, String filename) {
		FileInputStream fileIn = null;
		InputStreamReader in = null;
		BufferedReader buffer = null;
		StringBuffer text = new StringBuffer();
		String res = null;
		try {
	    	fileIn = context.openFileInput(filename + ".dat");
	    	in = new InputStreamReader(fileIn);
	    	buffer = new BufferedReader(in);
	    	while((res = buffer.readLine())!=null){
				text.append(res);
			}
	    	res = text.toString();
	    } catch (FileNotFoundException e) {
	        //e.printStackTrace();
	    	res = null;
	    } catch (IOException e){
	    	//e.printStackTrace();
	    	res = null;
	    } finally {
			try { if(fileIn!=null) fileIn.close(); } catch (IOException e) { }
			try { if(in!=null) in.close(); } catch (IOException e) { }
			try { if(buffer!=null) buffer.close(); } catch (IOException e) { }
		}
	    return res;
	}
	
	public static void saveAllStockData(Context context, int file, String obj) throws JSONException {
		JSONObject q = new JSONObject(); 
		q.put("key", CssIniFile.GetFileName(file));
		q.put("url", "");
		q.put("time", DateTool.getDateStringByPattern());
		q.put("sp", "");
		boolean flag = CssIniFile.saveStockData(context, GetFileName(file), obj);
		if(flag)
			CssIniFile.saveIniWithAPPEND(context, CssIniFile.StockInfoFile, "loadallstock", q.toString());
	}
	
	public static void saveAllHKStockData(Context context, int file, String obj) throws JSONException {
		JSONObject q = new JSONObject(); 
		q.put("key", CssIniFile.GetFileName(file));
		q.put("url", "");
		q.put("time", DateTool.getDateStringByPattern());
		q.put("sp", "");
		boolean flag = CssIniFile.saveStockData(context, GetFileName(file), obj);
		if(flag)
			CssIniFile.saveIniWithAPPEND(context, CssIniFile.StockInfoFile, "loadallhkstock", q.toString());
	}
	
	public static boolean isNetworkErrorGoInActivity(Context paramContext) {
		JSONObject jsonstr;
		try {
			jsonstr = CssIniFile.getStockInfoByKlineURL(paramContext, "loadallstock");
			if(jsonstr!=null) {
				return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 判断今天有无更新股票列表
	 * @param paramContext
	 * @return
	 */
	public static boolean toDayIsWriteStockListOrNot(Context paramContext) {
		JSONObject jsonstr;
		try {
			jsonstr = CssIniFile.getStockInfoByKlineURL(paramContext, "loadallstock");
			if(jsonstr!=null) {
				String time = jsonstr.getString("time").substring(0, 8);
				String current = DateTool.getDateStringByPattern().substring(0, 8);
				if(time.equals(current)) 
					return true;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return false;
	}
}
