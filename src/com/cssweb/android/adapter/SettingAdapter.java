package com.cssweb.android.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import com.cssweb.android.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingAdapter extends BaseAdapter {
	private ArrayList<HashMap<String, Object>> data;
	private Context context;
	private ArrayList<Integer> positionList;

	public SettingAdapter(ArrayList<HashMap<String, Object>> data,
			Context context , ArrayList<Integer> positionList) {
		this.data = data;
		this.context = context;
		this.positionList = positionList;
	}

	public int getCount() {
		return data.size();
	}

	public Object getItem(int position) {
		return data.get(position);
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
			layout = (LinearLayout) inflater.inflate(R.layout.list_item,null);
			holder = new Holder();
			holder.imageView = (ImageView) layout.getChildAt(2);
			holder.textView = (TextView) (TextView) layout.getChildAt(0);
			layout.setTag(holder);
		}
		Integer resId = (Integer) data.get(position).get("itemselectedImage");// Integer
		holder.imageView.setImageResource(resId);
		holder.textView.setText((String) data.get(position).get("itemName"));

		if(positionList.contains(position)){
			holder.imageView.setVisibility(View.VISIBLE);
		}
		
		return layout;
	}

	protected class Holder {
		public ImageView imageView;
		public TextView textView;
	}

	  
		/*LayoutInflater inflater = LayoutInflater.from(context);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.list_item, null);
		ImageView imageView = (ImageView) layout.getChildAt(2);
		TextView textView = (TextView) layout.getChildAt(0);
		Integer resId = (Integer) data.get(position).get("itemselectedImage");// Integer
		imageView.setImageResource(resId);
		textView.setText((String) data.get(position).get("itemName"));

		if(positionList.contains(position)){
			imageView.setVisibility(View.VISIBLE);
		}
		return layout;
	  }*/
	 
}
