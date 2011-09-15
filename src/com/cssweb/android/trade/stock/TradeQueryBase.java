package com.cssweb.android.trade.stock;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cssweb.android.base.DialogActivity;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.GlobalColor;

/**
 * 交易部分查询列表显示的基类
 * 
 * @author wangsheng
 *
 */
public class TradeQueryBase extends DialogActivity {
	private static final String DEBUG_TAG = "TradeQueryBase";
	
	protected String[] colsName = null; //列名数组
	protected String[] colsIndex = null;//列索引数组
	protected Set<Integer> digitColsIndex = null;//数字列的索引
	protected int type;
	
	private Context subClassContext;//保存子类的context对象
	private static final float TEXT_SIZE = 18; //单元格字体大小设置
	
	protected LinearLayout fixedLayout;//存放最左边固定列的容器
	protected LinearLayout scrollLayout;//用于放置右边可横向滚动表格的容器
	protected JSONArray allRecords;//存储从服务端一次获得的所有数据
	protected JSONArray allRecordsTemp;
	protected int totalRecordCount;//获取的记录总数
	protected int beginRowId;//当前页的起始行
	protected int endRowId;//当前页的结束行
	protected int previousSelectedId = -1;//上次选中的行Tag号
	protected int currentSelectedId = 0;//当前选中的行Tag号，默认选中第一行
	protected int currentPageId = 0;//当前页索引，默认显示第一页
	protected TableLayout table;//内容表格
	private static final int TITLE_FIXED_COLUMN = 0;//标题固定列
	private static final int TITLE_SCROLL_COLUMN = 1;//标题滚动列
	private static final int CONTENT_FIXED_COLUMN = 2;//内容固定列
	private static final int CONTENT_SCROLL_COLUMN = 3;//内容滚动列
	
	protected int pageNum = 9;
	protected int rowHeight = 0;
	protected int tabRowHeight = 60;
	protected int len = 0;
	protected int reqPageId = 0;
	protected int lastPageId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.zr_table);

		fixedLayout = (LinearLayout)findViewById(R.id.zr_htable_lock);
        scrollLayout = (LinearLayout)findViewById(R.id.zr_htable_linearlayout);
        
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.scroll_items, scrollLayout);
    	table = (TableLayout)view.findViewById(R.id.scroll_content);
    	
    	//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(TradeQueryBase.this);
	}
	
	/**
	 * 每行的点击事件监听器
	 */
	private OnClickListener rowListener = new OnClickListener() {
		public void onClick(View v) {
			int tag = (Integer)v.getTag();
			setSelectedRow(tag);
		}
	};
	
	/**
	 * 填充当前页的内容
	 */
	protected void fillCurrentPageContent(Context context) {
		currentSelectedId = currentPageId*pageNum;//Global.PAGE_SIZE_INCLUDE_TITLE
		beginRowId = currentPageId*pageNum;//Global.PAGE_SIZE_INCLUDE_TITLE
		endRowId = beginRowId + pageNum - 1;//Global.PAGE_SIZE_INCLUDE_TITLE
		int page = totalRecordCount/pageNum; //totalRecordCount-endRowId<=pageNum;
	    if(totalRecordCount%pageNum!=0)
	    	page++;
	    if (page*pageNum!=0) {
	    	if(endRowId>=page*pageNum-1){
	    		currentPageId = page-1;
	    		currentSelectedId = currentPageId*pageNum;
	    		beginRowId = currentPageId*pageNum;
	    		endRowId = beginRowId + pageNum - 1;
	    	}
		}
		fixedLayout.removeAllViews();
		table.removeAllViews();
		initTHead(context, colsName);
		if(allRecords.length() <=0){  //没有记录
			initTipContent();
		}else{
			initContent(colsIndex, digitColsIndex);
			setSelectedRow(currentSelectedId);
		}
	}
	protected void fillNullCurrentPageContent(Context context) {
		allRecords = new JSONArray();
		try {
			allRecords.put(formatNullJSONObject());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		totalRecordCount = allRecords.length();
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(TradeQueryBase.this);
		rowHeight = CssSystem.getTableRowHeight(TradeQueryBase.this);
		if (pageNum == 8) {
			tabRowHeight = 40;
		}
		
		currentSelectedId = currentPageId*pageNum;
		beginRowId = currentPageId*pageNum;
		endRowId = beginRowId + pageNum - 1;
		fixedLayout.removeAllViews();
		table.removeAllViews();
		initTHead(context, colsName);
		initContent(colsIndex, digitColsIndex);
	}
	protected void forwardDetails(Context context) {
		Intent intent = new Intent();
		intent.setClass(context, GetDetails.class);
		intent.putExtra("colsName", colsName);
		intent.putExtra("colsIndex", colsIndex);
		intent.putExtra("currentSelectedId", currentSelectedId);
		intent.putExtra("allRecords", allRecords.toString());
		context.startActivity(intent);
	}
	protected void forwardDetailsH(Context context) {
		String allRecordsStr = allRecords == null ? null : allRecords.toString() ;
		if (allRecordsStr ==null || "".equals(allRecordsStr)) {
			return;
		}
		Intent intent = new Intent();
		intent.setClass(context, GetDetailsH.class);
		intent.putExtra("colsName", colsName);
		intent.putExtra("colsIndex", colsIndex);
		intent.putExtra("currentSelectedId", currentSelectedId);
		intent.putExtra("currentPageId", currentPageId);
		intent.putExtra("allRecords", allRecordsStr);
		context.startActivity(intent);
	}
	
	/**
	 * 设置选中行的状态
	 * @param tag
	 */
	protected void setSelectedRow(int tag){
		if(totalRecordCount == 0)
			return;
		currentSelectedId = tag;
		TextView txt = (TextView)fixedLayout.findViewWithTag(tag);
		TableRow row = (TableRow)table.findViewWithTag(tag);
		if (txt == null) {
			return;
		}
		txt.setBackgroundResource(R.drawable.zr_trade_table_cell_selected_bg);
		int count = row.getChildCount();
		for(int i=0; i<count; i++)
			row.getChildAt(i).setBackgroundResource(R.drawable.zr_trade_table_cell_selected_bg);
		
		if(previousSelectedId != -1 && previousSelectedId != currentSelectedId){
			txt = (TextView)fixedLayout.findViewWithTag(previousSelectedId);
			row = (TableRow)table.findViewWithTag(previousSelectedId);
			
			txt.setBackgroundResource(R.drawable.zrfundqueryh05);
			txt.setPadding(10, 0, 10, 0);
			for(int i=0; i<count; i++){
				row.getChildAt(i).setBackgroundResource(R.drawable.zrfundqueryh05);
				row.getChildAt(i).setPadding(10, 0, 10, 0);
			}
		}
		previousSelectedId = tag;
	}
    
	/**
	 * 动态填充表格的内容
	 * @param colsIndex
	 */
    protected void initContent(String[] colsIndex, Set<Integer> digitCols) {
    	if(allRecords == null)
    		return;
    	for(int i=beginRowId; i<=endRowId; i++){
    		TableRow row = new TableRow(this);
    		row.setTag(i);
    		if(i < totalRecordCount){
    			for(int j=0,cols=colsIndex.length; j<cols; j++){
					try {
						if(j==0){
							TextView txt = getTextView(allRecords.getJSONObject(i).getString(colsIndex[j]), CONTENT_FIXED_COLUMN, false);
							txt.setTag(i);
							txt.setOnClickListener(rowListener);
							fixedLayout.addView(txt);
						}else{
							if(digitCols!=null && digitCols.contains(j))
								row.addView(getTextView(allRecords.getJSONObject(i).getString(colsIndex[j]), CONTENT_SCROLL_COLUMN, true));
							else
								row.addView(getTextView(allRecords.getJSONObject(i).getString(colsIndex[j]), CONTENT_SCROLL_COLUMN, false));
							row.setOnClickListener(rowListener);
							
						}
					} catch (JSONException e) {
						CssLog.e(DEBUG_TAG, e.toString());
					}
    			}
    		}else{
    			for(int j=0; j<colsIndex.length; j++){
    				if(j==0){
    					TextView txt = getTextView("", CONTENT_FIXED_COLUMN, false);
    					txt.setTag(i);
    					fixedLayout.addView(txt);
    				}else{
    					row.addView(getTextView("", CONTENT_SCROLL_COLUMN, false));
    				}
    			}
			}
    		table.addView(row);
    	}
	}
    protected void initContentDown(String[] colsIndex, Set<Integer> digitCols) {
    	if(allRecordsTemp == null)
    		return;
    	for(int i=beginRowId; i<=endRowId; i++){
    		TableRow row = new TableRow(this);
    		row.setTag(i);
    		if(i < totalRecordCount){
    			for(int j=0,cols=colsIndex.length; j<cols; j++){
					try {
						if(j==0){
							TextView txt = getTextView(allRecordsTemp.getJSONObject(i).getString(colsIndex[j]), CONTENT_FIXED_COLUMN, false);
							txt.setTag(i);
							txt.setOnClickListener(rowListener);
							fixedLayout.addView(txt);
						}else{
							if(digitCols!=null && digitCols.contains(j))
								row.addView(getTextView(allRecordsTemp.getJSONObject(i).getString(colsIndex[j]), CONTENT_SCROLL_COLUMN, true));
							else
								row.addView(getTextView(allRecordsTemp.getJSONObject(i).getString(colsIndex[j]), CONTENT_SCROLL_COLUMN, false));
							row.setOnClickListener(rowListener);
							
						}
					} catch (JSONException e) {
						CssLog.e(DEBUG_TAG, e.toString());
					}
    			}
    		}else{
    			for(int j=0; j<colsIndex.length; j++){
    				if(j==0){
    					TextView txt = getTextView("", CONTENT_FIXED_COLUMN, false);
    					txt.setTag(i);
    					fixedLayout.addView(txt);
    				}else{
    					row.addView(getTextView("", CONTENT_SCROLL_COLUMN, false));
    				}
    			}
			}
    		table.addView(row);
    	}
	}
    /**
     * 没有记录，提示一下
     */
    protected void initTipContent(){
    	for(int i=0; i<pageNum; i++){
    		TableRow row = new TableRow(this);
			for(int j=0,cols=colsIndex.length; j<cols; j++){
				try {
					if( i ==0 && j==0){
						int color = GlobalColor.colorpriceUp ;
						TextView txt = getTextView(getResources().getString(R.string.no_data) + "|" + color , CONTENT_FIXED_COLUMN, false);
						fixedLayout.addView(txt);
					}else if (i!=0 && j==0){
						TextView txt = getTextView("" , CONTENT_FIXED_COLUMN, false);
						fixedLayout.addView(txt);
					}else{
						row.addView(getTextView("", CONTENT_SCROLL_COLUMN, false));
					}
				} catch (Exception e) {
					CssLog.e(DEBUG_TAG, e.toString());
				}
    		}
    		table.addView(row);
    	}
    }

    /**
     * 动态填充表格的标题
     * @param context
     * @param colsName
     */
	protected void initTHead(Context context, String[] colsName){
		this.subClassContext = context;
		fixedLayout.addView(getTextView(colsName[0], TITLE_FIXED_COLUMN, false));
    	TableRow row = new TableRow(this);
    	for(int i=1,size=colsName.length; i<size; i++){
    		row.addView(getTextView(colsName[i], TITLE_SCROLL_COLUMN, false));
    	}
    	table.addView(row);	
    }
    
	/**
	 * 根据传入的参数，返回表格中对应单元格的内容
	 * @param str
	 * @param type
	 * @return
	 */
    private TextView getTextView(String str, int type, boolean isDigit){
    	TextView txtView = new TextView(subClassContext);
    	txtView.setSingleLine(false);
    	txtView.setTextSize(TEXT_SIZE);
    	String[] temp = str.split("\\|");
    	if(temp.length == 2){
    		txtView.setText(temp[0]);
    		txtView.setTextColor(Integer.parseInt(temp[1]));
    	}else{
    		txtView.setText(str);
    	}
    	
    	if(type == TITLE_FIXED_COLUMN){
    		int width = 163;
    		if (pageNum == 8) {
    			width = 120;
			}
    		txtView.setWidth(width);
    		txtView.setBackgroundResource(R.drawable.zrfundqueryh02);//R.drawable.zr_trade_table_title_fixed_column
    	}else if(type == TITLE_SCROLL_COLUMN){
    		txtView.setBackgroundResource(R.drawable.zrfundqueryh02);//zr_trade_table_title_scroll_column
    	}else if(type == CONTENT_FIXED_COLUMN){
    		txtView.setBackgroundResource(R.drawable.zrfundqueryh05);//zr_trade_table_content_fixed_column
    	}else if(type == CONTENT_SCROLL_COLUMN){
    		txtView.setBackgroundResource(R.drawable.zrfundqueryh05);//zr_trade_table_content_scroll_column
    	}
    	if(isDigit){
    		txtView.setGravity(Gravity.RIGHT|Gravity.CENTER_VERTICAL);
    	}else{
    		txtView.setGravity(Gravity.CENTER);
    	}
    	txtView.setHeight(tabRowHeight+rowHeight);
    	txtView.setPadding(10, 0, 10, 0);
    	return txtView;
    }
	
	/**
	 * 根据字段的特性，将从服务端获取的JSONObject对象的每个字段格式化成人类可读的格式
	 * 
	 * @param jsonObj 原始的JSONObject
	 * @return 格式化后的JSONObject
	 */
	protected JSONObject formatJSONObject(JSONObject jsonObj) throws JSONException{
		return null;
	}
	protected JSONObject formatNullJSONObject()throws JSONException {
		JSONObject formatJsonObj = new JSONObject();
		int color = GlobalColor.colorStockName;
		for(int i=0,size=colsIndex.length; i<size; i++){
			formatJsonObj.put(colsIndex[i],"|" + color);
		}
		return formatJsonObj;
	}
	/**
	 * 处理刷新事件
	 */
	@Override
	protected void RefreshUI() {
		setToolBar();
	}
	
	/**
	 * 下页处理
	 */
	protected void onPageDown() {
		if(endRowId >= totalRecordCount-1){
			//toast("已是最后一页！");
		}else{
			currentPageId ++;
			previousSelectedId = -1;
			setToolBar();
		}
	}
	protected void onPageDownH() {
		currentPageId ++;
		previousSelectedId = -1;
		setToolBar();
	}
	
	/**
	 * 上页处理
	 */
	protected void onPageUp() {
		if(currentPageId == 0){
			//toast("已是第一页！");
		}else{
			currentPageId --;
			previousSelectedId = -1;
			setToolBar();
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(TradeQueryBase.this);
		rowHeight = CssSystem.getTableRowHeight(TradeQueryBase.this);
		if (pageNum == 8) {
			tabRowHeight = 40;
		}
		initPopupWindow();
	}
	
    protected void enabledToolBarfalse(){
    	LinearLayout l = (LinearLayout) findViewById(R.id.zr_toolbarlayout2);
		l.setVisibility(View.GONE);
		int count  = l.getChildCount();
		for (int i=0; i< count-1 ; i++){
			TextView textView = (TextView) l.getChildAt(i);
			if ("刷新".equals(textView.getText().toString())) {
				continue;
			}
			textView.setEnabled(false);
			textView.setTextColor(getResources().getColor(R.color.zr_dimgray));
		}
    }
    protected void enabledToolBartrue(){
    	LinearLayout l = (LinearLayout) findViewById(R.id.zr_toolbarlayout2);
		l.setVisibility(View.GONE);
		int count  = l.getChildCount();
		for (int i=0; i< count-1 ; i++){
			TextView textView = (TextView) l.getChildAt(i);
			textView.setEnabled(true);
		}
    }
}
