package com.cssweb.android.adapter;

import java.util.HashMap;

import com.cssweb.android.common.Config;
import com.cssweb.android.main.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MenuAdapter extends BaseAdapter {
	private Context context;
	private int len ;

	public MenuAdapter(Context context , int len) {
		this.context = context;
		this.len = len;
	}

	public int getCount() {
		return len;
	}

	public Object getItem(int position) {
		try {
			HashMap<String, Object> tmp = (HashMap<String, Object>) Config.mapBitmap.get(position);
			return tmp.get("itemname");
		} catch (Exception e) {
			return null;
		} 
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		LinearLayout layout;
		Holder holder;
		if (convertView != null) {
			layout = (LinearLayout) convertView;
			holder = (Holder) layout.getTag();
		} else {
			LayoutInflater inflater = LayoutInflater.from(context);
			layout = (LinearLayout) inflater.inflate(
					R.layout.menu_listview_item, null);
			holder = new Holder();
			holder.imageView = (ImageView) layout.getChildAt(0);
			layout.setTag(holder);
		}
		if(Config.mapBitmap != null && Config.mapBitmap.size() == 0){
			holder.imageView.setImageBitmap(null);
		}else{
			try {
				if ((Bitmap) (Config.mapBitmap.get(position).get(String.valueOf(1))) != null) {
					holder.imageView.setImageBitmap((Bitmap) (Config.mapBitmap.get(position).get(String.valueOf(1))));
				}else{
					holder.imageView.setImageBitmap(null);
				}
			} catch (Exception e) {
				holder.imageView.setImageBitmap(null);
			}
		}
		
		
		return layout;
	}

	protected class Holder {
		public ImageView imageView;
	}
}
