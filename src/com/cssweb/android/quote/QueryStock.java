package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

public class QueryStock extends CssKeyboardBase {
	private final String TAG = "QueryStock";
	private EditText queryCode;

	private TableLayout table;
	
	private List<CssStock> result = new ArrayList<CssStock>();
	
	View.OnClickListener mTableRowClick = null;
	
	private boolean flag = false;
	private long difTime = 1000L;
	private Handler handler;
	private Runnable runnable;
	
	private int activityKind = Global.QUOTE_FENSHI;
	
	public QueryStock() {
		Handler localHandler = new Handler();
		this.handler = localHandler;
		Runnable r = new RunTimes();
		this.runnable = r;
	}
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		Bundle bundle = getIntent().getExtras();
		activityKind = bundle.getInt("menuid");
		
		setContentView(R.layout.zr_querystock);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "个股查询");
    	btnRight.setBackgroundResource(R.drawable.njzq_title_right_load);
    	btnRight.setTag(4);
    	
		initFrame();
		
		if(StockInfo.allStock==null)
			loadAllStock(1);
	}
	
	private void initFrame() {
		RelativeLayout localLinearLayout = (RelativeLayout)findViewById(R.id.zrquerystocklayout);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
	    localView2.setOnClickListener(setOnEditClickListener);
	    
		queryCode = (EditText) findViewById(R.id.zrStkItem);
		queryCode.setInputType(InputType.TYPE_NULL);
		queryCode.setFocusable(true);
		queryCode.setTag("NUMBER");
		queryCode.setOnKeyListener(new EditText.OnKeyListener() {

			public boolean onKey(View v, int keyCode, KeyEvent event) {
				return false;
			}
			
		});
		queryCode.addTextChangedListener(new TextWatcher() {

			public void afterTextChanged(Editable arg0) {
				//queryStock(queryCode.getText());
				OnTextChanged(arg0);
			}

			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {
			}

			public void onTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
			}
			
		});
		queryCode.setOnClickListener(setOnEditClickListener);
		queryCode.setOnFocusChangeListener(setOnEditFocusListener);
		
		table = (TableLayout) findViewById(R.id.zrTableLayout);
		mTableRowClick = new TableRowClick();
		
		initData();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	private void initData() {
		//if(result.size()==0)
		//	result.addAll(TradeUtil.fillListToNull(0, 10, result));
		appendRow(result);
	}
	
	private void appendRow(List<CssStock> result) {
		table.setStretchAllColumns(true);
		table.removeAllViews();
		int size = result.size();
		if(size>Global.PAGE_SIZE) size = Global.PAGE_SIZE;
        for(int i=0; i<size; i++) {
    		TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_querystock_list_items,null);

            TextView stkcode = (TextView) row.findViewById(R.id.zr_querystock_list_col2);
            TextView stkname = (TextView) row.findViewById(R.id.zr_querystock_list_col1);

            stkcode.setText(result.get(i).getStkcode());
            stkname.setText(result.get(i).getStkname());
            stkname.setTag(i);
            
            table.addView(row);
            row.setOnClickListener(mTableRowClick);
        }
        if(size<10){
        	for(int i=0;i<10-size;i++){
        		TableRow row = (TableRow) LayoutInflater.from(this).inflate(R.layout.zr_querystock_list_items,null);
        		TextView stkcode = (TextView) row.findViewById(R.id.zr_querystock_list_col2);
                TextView stkname = (TextView) row.findViewById(R.id.zr_querystock_list_col1);
                stkcode.setText("");
                stkname.setText("");             
                table.addView(row);
        	}
        }
    }
	
	private void OnTextChanged(Editable paramEditable) {
		Editable localEditable = this.queryCode.getText();
		if(localEditable == paramEditable) {
			handler.postDelayed(runnable, difTime);
		}
	}
	
	/**
	 * STKCODE jA.getString(1)
	 * STKNAME jA.getString(2)
	 * STKPY jA.getString(3)
	 * EXCHANGE jA.getInt(0)
	 * TYPE jA.getInt(4)
	 * @param c
	 * 利用二分查找去获得匹配的证券代码和简称
	 */
	private void queryStock() {
		String code = this.queryCode.getText().toString().trim().toUpperCase();
		int len = 0;
		String t1,t2,t3;
		try {
			result.clear();
    		if(StockInfo.allHKStock!=null) {
    			flag = true;
    			len = StockInfo.allHKStock.length();
        		for (int i = 0; i < len; i++) {
        			JSONArray jA = (JSONArray)StockInfo.allHKStock.get(i);
        			t1 = jA.getString(1);
        			t2 = jA.getString(2);
        			t3 = jA.getString(3);
        			if(t1.startsWith(code)||(t3.contains(code) && Pattern.compile("^[A-Za-z]").matcher(code).find())) {
        				CssStock cssStock = new CssStock();
        				cssStock.setMarket(NameRule.getExchange(jA.getString(0)));
        				//cssStock.setExchange(jA.getInt(0));
        				cssStock.setStkcode(t1);
        				cssStock.setStkname(t2);
        				result.add(cssStock);
        				if(result.size()>10) 
        					break;
        			}
        		}
    		}

			if(StockInfo.allStock!=null) {
				flag = true;
				len = StockInfo.allStock.getJSONArray("data").length();
	    		for (int i = 0; i < len; i++) {
	    			JSONArray jA = (JSONArray)StockInfo.allStock.getJSONArray("data").get(i);
	    			t1 = jA.getString(1);
	    			t2 = jA.getString(2);
	    			t3 = jA.getString(3);
	    			if(t1.startsWith(code)||(t3.contains(code) && Pattern.compile("^[A-Za-z]").matcher(code).find())) {
	    				CssStock cssStock = new CssStock();
	    				cssStock.setMarket(NameRule.getExchange(jA.getString(0)));
	    				//cssStock.setExchange(jA.getInt(0));
	    				cssStock.setStkcode(t1);
	    				cssStock.setStkname(t2);
	    				result.add(cssStock);
	    				if(result.size()>10) 
	    					break;
	    			}
	    		}
			}
			appendRow(result);
			
		} catch (JSONException e) {
			CssLog.e(TAG, e.toString());
		}
	}
	
	private class TableRowClick implements View.OnClickListener {

		public void onClick(View v) {
			View localView = ((TableRow) v).getChildAt(0);
			int tag = (Integer) localView.getTag();
			if(tag!=-1&&tag<result.size()&&result.get(tag).getStkcode()!=null) {
				switchActivity(activityKind, result.get(tag).getMarket(), result.get(tag).getStkcode(), result.get(tag).getStkname(), result.get(tag).getStktype());
			}
		}
	}
	
	private final class RunTimes implements Runnable {

		public void run() {
			queryStock();
		}
	}
	
	private void switchActivity(int activityKind, String exchange, String stockcode, String stockname, String stocktype) {
		if("kf".equals(exchange)) {
			//单独处理开放式基金
			if(activityKind==Global.QUOTE_FENSHI||activityKind==Global.QUOTE_KLINE||activityKind==Global.QUOTE_HKSTOCK)
				activityKind = Global.QUOTE_FLINE;
		}
		FairyUI.switchToWnd(activityKind, exchange, stockcode, stockname, QueryStock.this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		handler.removeCallbacks(runnable);
	}

	@Override
	protected void onPause() {
		super.onPause();
		handler.removeCallbacks(runnable);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	protected void reLoadAllStock() {
		showDialog(1);
	}
	
    @Override
    protected Dialog onCreateDialog(int id) {
    	switch(id) {
    	case 1:
    		return  new AlertDialog.Builder(QueryStock.this)
			            .setTitle(R.string.alert_dialog_about)
			            .setMessage(R.string.load_allstock_msg)
			            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
			                public void onClick(DialogInterface dialog, int whichButton) {
			                	showProgress();
			                	loadAllStock(2);
			                }
			            })
		                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
		                    public void onClick(DialogInterface dialog, int whichButton) {

		                    }
		                })
			            .create();
    	}
    	return null;
    }
    
    private void loadAllStock(final int type) {
    	new AsyncTask<Void, Void, Boolean>() {
    		/**
    		 * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间
    		 */
			@Override
			protected Boolean doInBackground(Void... arg0) {
				boolean flag = true;
				String jsonObject = null;
				JSONObject j = null;
				try {	
					if(type==1) {
						jsonObject = CssIniFile.loadStockData(QueryStock.this, CssIniFile.GetFileName(CssIniFile.HKStockFile));
						if(jsonObject!=null) {
		    				j = new JSONObject(jsonObject);
		    				if(StockInfo.HKINDEX.isEmpty())
		        				StockInfo.initAllHKStock(j);
		    				StockInfo.allHKStock = j.getJSONArray("data");
						}
						
						jsonObject = CssIniFile.loadStockData(QueryStock.this, CssIniFile.GetFileName(CssIniFile.UserStockFile));
						if(jsonObject!=null) {
		    				j = new JSONObject(jsonObject);
		    				StockInfo.allStock = j;
						}
					}
					else {
						if(activityKind == Global.QUOTE_HKSTOCK) {
							j = ConnService.getAllHKStock();	
							if(Utils.isHttpStatus(j)) {
			    				if(StockInfo.HKINDEX.isEmpty())
			        				StockInfo.initAllHKStock(j);
			    				StockInfo.allHKStock = j.getJSONArray("data");
							}
						}
						else {
							j = ConnService.getAllStock();	
							if(Utils.isHttpStatus(j)) {
								StockInfo.allStock = j;
							}
						}
					}
				} catch (JSONException e) {
					flag = Boolean.FALSE;
				} catch (Exception e) {
					flag = Boolean.FALSE;
				}
				return flag;
			}

			/**
			 * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
			 */
			protected void onPostExecute(Boolean result) {
				if(result&&flag) {
					queryStock();
				}
				hiddenProgress();
			}
    	}.execute();
	}
}
