package com.cssweb.android.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cssweb.android.domain.MenuImageUrl;
import com.cssweb.android.domain.MenuImages;

import android.content.Context;
import android.util.Log;

public class NetUtils {

	// 
	public static byte[] readStream(InputStream inStream) throws Exception {
		byte[] buffer = new byte[1024];
		int len = -1;
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		byte[] data = outStream.toByteArray();
		outStream.close();
		inStream.close();
		return data;
	}

	public static boolean download(Context context, MenuImages menuImages) {
		boolean flag = false;
		String serverUrl = menuImages.getServerUrl();
		for (MenuImageUrl imageUrl : menuImages.getImageUrls()) {
			String fileName = imageUrl.getImageName() + imageUrl.getDpiExt();
			File file = new File("/data/data/com.cssweb.android.main/files/" + fileName);
			if (!file.exists()) {
				download(context, serverUrl + fileName, fileName);
				flag = true;
			}else{
				flag = false;
			}
		}
		return flag;
	}

	public static boolean download(Context context, String serverUrl,
			MenuImageUrl imageUrl) {
		String fileName = imageUrl.getImageName() + imageUrl.getDpiExt();
		File file = new File("/data/data/com.cssweb.android.main/files/" + fileName);
		if (!file.exists()) {
			download(context, serverUrl + fileName, fileName);
			return true;
		}else {
			return false;
		}
	}

	private static void download(Context context, String urlpath, String name) {
		Log.i("tag", "download" + urlpath);
		try {
			URL url = new URL(urlpath);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(5000);
			if (conn.getResponseCode() == 200) {
				// 获取服务器返回的数据
				byte[] bytedata = NetUtils.readStream(conn.getInputStream());
				// String name = urlpath.substring(urlpath.lastIndexOf("/") +1
				// );
				FileOutputStream fos = context.openFileOutput(name,
						Context.MODE_PRIVATE);
				fos.write(bytedata);
				fos.close();
			}
		} catch (Exception e) {
		}
	}
}
