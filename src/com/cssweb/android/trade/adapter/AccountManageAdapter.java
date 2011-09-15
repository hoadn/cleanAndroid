package com.cssweb.android.trade.adapter;

import com.cssweb.android.main.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;


/**
 * 用于账户管理的适配器
 * 
 * @author chengfei
 *
 */
public class AccountManageAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private int mResource;
	private String[] values;
	
	public AccountManageAdapter(Context context, String[] values, int paramInt) {
		this.mInflater = LayoutInflater.from(context);
		this.values = values;
		this.mResource = paramInt;
	}
	
	public int getCount() {
		return values.length;
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
			holder.account = (TextView)convertView.findViewById(R.id.account);
			
			convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }
        
        //当焦点移动到实际行数之外时，将TextView的值置为空
        //防止出现没有数据的地方显示View模板缓存的数据
        if(position >= values.length){
        	holder.account.setText("");
        	
        	return convertView;
        }
        
        bindView(holder, position);
        holder.account.setTag(position);
        return convertView;
	}
	
	
	
	private void bindView(ViewHolder holder, int position) {
    	setRol(holder.account, values[position]);
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
		TextView account;
	}
}
