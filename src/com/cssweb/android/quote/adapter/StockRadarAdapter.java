package com.cssweb.android.quote.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.cssweb.android.main.R;

public class StockRadarAdapter extends SimpleAdapter{
	
    private LayoutInflater mInflater;
    private int resource;
    private ArrayList<HashMap<String, String>> data;
    public StockRadarAdapter(Context context, ArrayList<HashMap<String, String>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.resource = resource;
        this.data = data;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
  
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
    	ViewHolder holder;
    	if (convertView == null){
    		convertView  = mInflater.inflate(R.layout.zr_hotnews_item, null);
    		holder = new ViewHolder();
    		holder.title =  (TextView) convertView.findViewById(R.id.zr_hotjf_title);
    		holder.date = (TextView) convertView.findViewById(R.id.zr_hotjf_date);
    		holder.row = (TextView) convertView.findViewById(R.id.row);
    		 convertView.setTag(holder);
    	}else {
    		 holder = (ViewHolder) convertView.getTag();
    	}
        
        holder.title.setText(data.get(position).get("title"));
        holder.date.setText(data.get(position).get("date"));
        holder.row.setText(data.get(position).get("row"));
        holder.title.setTag(null);
    

        // return text;
        return super.getView(position, convertView, parent);
    }
    static class ViewHolder {
    	  TextView title;
          TextView date;
          TextView row;
    }
    
   
}
