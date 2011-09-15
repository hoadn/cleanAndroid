package com.cssweb.android.quote.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.quote.util.Utils;

public class TableAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	private int mResource;
	private String mCurrent;
	private int mCurrentRow;
	private int mPageNum;
	private List<CssStock> list;
	
	public TableAdapter(Context context, List<CssStock> list, int paramInt, String current, int currentRow, int pageNum) {
		this.mContext = context;
		this.mInflater = LayoutInflater.from(context);
		this.list = list;
		this.mResource = paramInt;
		this.mCurrent = current;
		this.mCurrentRow = currentRow;
		this.mPageNum = pageNum;
	}
	
	public int getCount() {
		return mPageNum;
	}

	public Object getItem(int position) {
		return Integer.valueOf(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v;
        if (convertView == null) {
            v = mInflater.inflate(mResource, null);
        } else {
            v = convertView;
        }
        bindView(v, position);
        return v;
	}
	
	private void bindView(final View v, final int position) {
		int tLength = list.size() - 1;
    	int mDigit = 1;
    	if(position<=tLength) {
    		CssStock css = list.get(position);
    		if(css!=null&&css.getStkcode()!=null&&!css.getStkcode().equals("")) {
    			//先判断小数位数
        		if(css.getStkcode()!=null)
        			mDigit = Utils.getNumFormat(css.getMarket(), css.getStkcode());
                TextView item1 = (TextView) v.findViewById(R.id.zr_rt_col1);
                TextView item2 = (TextView) v.findViewById(R.id.zr_rt_col2);
                TextView item3 = (TextView) v.findViewById(R.id.zr_rt_col3);
                TextView item4 = (TextView) v.findViewById(R.id.zr_rt_col4);
                setRol1(item1, css.getStkcode());
                setRol1(item2, css.getStkname());
                setRol2(item3, css.getZjcj(), css.getZrsp(), mDigit);
                //setRol3(item3, css.getZrsp(), mDigit);
                if("zd".equals(mCurrent))
                	setRol3(item4, css.getZf());
                else 
                	setRol4(item4, css.getZf());
                if(position==mCurrentRow) {
                	v.setBackgroundResource(R.drawable.zr_table_xkz);
                }
                else {
                	v.setBackgroundResource(R.drawable.zr_table_xk);
                }
                item4.setOnClickListener(new View.OnClickListener() {
    	
    				public void onClick(View view) {
    					FairyUI.switchToWnd(Global.QUOTE_FENSHI, list.get(position).getMarket(), list.get(position).getStkcode(), list.get(position).getStkname(), mContext);
    				}
    			});
    		}
    	}
	}

	private void setRol1(TextView paramTextView, String paramString) {
		paramTextView.setTextSize(18);
		paramTextView.setText(paramString);
		paramTextView.setTextColor(Utils.getTextColor(mContext, 1));
	}
	
	private void setRol2(TextView paramTextView, double paramDouble, double paramDouble2, int mDigit) {
		paramTextView.setTextSize(18);
		paramTextView.setText(Utils.dataFormation(paramDouble, mDigit));
		paramTextView.setTextColor(Utils.getTextColor(mContext, paramDouble, paramDouble2));
	}
	
	private void setRol3(TextView paramTextView, double paramDouble) {
		paramTextView.setTextSize(18);
		paramTextView.setText(Utils.dataFormation(paramDouble, 1));
		paramTextView.setTextColor(Utils.getTextColor(mContext, 0));
		if(paramDouble<0) 
			paramTextView.setBackgroundResource(R.drawable.zrreportdownbg);
		else
			paramTextView.setBackgroundResource(R.drawable.zrreportupbg);
	}
	
	private void setRol4(TextView paramTextView, double paramDouble) {
		paramTextView.setTextSize(18);
		paramTextView.setText(Utils.dataFormation(paramDouble*100, 1) + "%");
		paramTextView.setTextColor(Utils.getTextColor(mContext, 0));
		if(paramDouble<0) 
			paramTextView.setBackgroundResource(R.drawable.zrreportdownbg);
		else
			paramTextView.setBackgroundResource(R.drawable.zrreportupbg);
	}
}
