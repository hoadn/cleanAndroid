package com.cssweb.android.trade.adapter;

import com.cssweb.android.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 用于显示明细的适配器
 * 
 * @author wangsheng
 *
 */
public class DetailsAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int mResource;
	private String[] keys;
	private String[] values;
	
	public DetailsAdapter(Context context, String[] keys, String[] values, int paramInt) {
		this.mInflater = LayoutInflater.from(context);
		this.keys = keys;
		this.values = values;
		this.mResource = paramInt;
	}
	
	public int getCount() {
		return keys.length;
	}

	public Object getItem(int position) {
		return Integer.valueOf(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
        if (convertView == null) {
            convertView = mInflater.inflate(mResource, null);
            holder = new ViewHolder();
			holder.column1 = (TextView)convertView.findViewById(R.id.DetailsName);
			holder.column2 = (TextView)convertView.findViewById(R.id.DetailsValue);
			if (keys[position].equals("摘要")){
				if (values[position].length()>10) {
					LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) holder.column1.getLayoutParams();//获取bank控件的当前布局
		    		linearParams1.height=145;//对该控件的布局参数做修改
		        	holder.column1.setLayoutParams(linearParams1);
		        	holder.column2.setLayoutParams(linearParams1);
				}
			}
			
			convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        //当焦点移动到实际行数之外时，将TextView的值置为空
        //防止出现没有数据的地方显示View模板缓存的数据
        if(position >= keys.length){
        	holder.column1.setText("");
        	holder.column2.setText("");
        	
        	return convertView;
        }
        
        bindView(holder, position);
        
        return convertView;
	}
	
	private void bindView(ViewHolder holder, int position) {
    	setRol(holder.column1, keys[position]);
    	setRol(holder.column2, values[position]);
	}

	private void setRol(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(18);
		String[] temp = paramString.split("\\|");
    	if(temp.length == 2){
    		paramString = temp[0];
    	}
		paramTextView.setText(paramString);
	}
	
	/**
     * 用于存储每行的视图
     * @author Administrator
     *
     */
    static class ViewHolder{
		TextView column1;
		TextView column2;
	}
}
