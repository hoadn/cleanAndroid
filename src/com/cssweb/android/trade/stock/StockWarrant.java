package com.cssweb.android.trade.stock;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.util.TradeUtil;

public class StockWarrant extends CssKeyboardBase {
	private static final String DEBUG_TAG = "PreWarrent";
	
	private final char BSFLAG = '7';
	private ArrayAdapter<String> stockHolderAdapter;
	
	private Spinner stockHolder;
	private EditText stockCode;
	private EditText number;
	private TextView stockName;
	private TextView price;
	private TextView avaiCash;
	private TextView avaiNum;
	
	private List<String> holder;
	private int market;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.zr_trade_stock_warrant);
		
		setTitle("权证行权");
		//SetRightTitle("刷新", 2);
		initTitle(R.drawable.njzq_title_left_back, 0, "权证行权");
		String[] toolbarNames = {Global.TOOLBAR_QUEDING, Global.TOOLBAR_BACK};
        initToolBar(toolbarNames, Global.BAR_TAG);
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		stockHolder = (Spinner)findViewById(R.id.StockHolder);
		stockCode = (EditText)findViewById(R.id.PreWarrantCodeText);
		number = (EditText)findViewById(R.id.PreWarrantNumText);
		stockName = (TextView)findViewById(R.id.StockNameText);
		price = (TextView)findViewById(R.id.PriceText);
		avaiCash = (TextView)findViewById(R.id.AvailableFundsText);
		avaiNum = (TextView)findViewById(R.id.AvailableNumText);
		
		stockCode.setInputType(InputType.TYPE_NULL);
		stockCode.setFocusable(true);
		stockCode.setTag("STOCK");
		stockCode.setOnClickListener(setOnEditClickListener);
		stockCode.setOnFocusChangeListener(setOnEditFocusListener);
		
		number.setInputType(InputType.TYPE_NULL);
		number.setFocusable(true);
		number.setTag("");
		number.setOnClickListener(setOnEditClickListener);
		number.setOnFocusChangeListener(setOnEditFocusListener);
		
		holder = TradeUser.getInstance().getHolder();
		stockHolderAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])holder.toArray(new String[holder.size()]));
		stockHolderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		stockHolder.setAdapter(stockHolderAdapter);
		
		stockCode.addTextChangedListener(new TextWatcher() {			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
		});
	}
	
	private void textChanged(Editable paramEditable){
		Editable localEditable  = stockCode.getText();
		if(localEditable == paramEditable){
			String str = stockCode.getText().toString().trim();
			if(str.length() == 6){
				onHideKeyBoard();
				getStockInfo();
			}
		}
	}
	
	private void getStockInfo(){
		showProgress();
		
		r = new Runnable() {			
			public void run() {
				try {
					JSONObject res = (ConnService.getTradeHQ(stockCode.getText().toString()));
					JSONArray jsonArr = res.getJSONArray("item");
					JSONObject jsonObj = jsonArr.getJSONObject(0);
					
					Message msg = Message.obtain();
					msg.obj = jsonObj;
					mHandler.sendMessage(msg);
					
					hiddenProgress();
				} catch (JSONException e) {
					Log.e(DEBUG_TAG, e.toString());
					hiddenProgress();
				}
			}
		};
		new Thread(r).start();
	}

	@Override
	protected void handlerData() {
		try {
			stockName.setText(quoteData.getString("zqjc"));
			price.setText(String.valueOf(quoteData.getDouble("zjcj")));
			market = quoteData.getInt("market");
			if(market == 1)
				stockHolder.setSelection(1);
			else
				stockHolder.setSelection(0);
			avaiCash.setText(TradeUtil.getFundavl());
			StringBuffer sb = new StringBuffer();
			sb.append("market=" + market +TradeUtil.SPLIT);
			sb.append("secuid=" + holder.get(stockHolder.getSelectedItemPosition()) + TradeUtil.SPLIT);
			sb.append("stkcode=" + stockCode.getText() + TradeUtil.SPLIT);
			sb.append("bsflag=" + BSFLAG + TradeUtil.SPLIT);
			sb.append("price=" + price.getText() + TradeUtil.SPLIT);
			sb.append("bankcode=" + TradeUtil.SPLIT);
			JSONObject res = ConnPool.sendReq("GET_MAXQTY", "410410", sb.toString());
			//Log.i(">>>>>>", res+"$$$");
			System.out.println("@"+TradeUtil.getFundavl()+"@");
//			String checkRes = TradeUtil.checkResult(res);
//			if(checkRes!=null){
//				avaiNum.setText("0");
//				if(checkRes.equals("-1"))
//					showTipsDialog((String)getApplicationContext().getResources().getText(R.string.network_error));
//				else
//					toast(checkRes);
//				return;
//			}
			JSONArray jsonArr = res.getJSONArray("item");
			JSONObject jsonObj = jsonArr.getJSONObject(0);
			
			avaiNum.setText(jsonObj.getInt("maxstkqty"));
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
		}
	}
    
    /**
     * 处理点击确定事件    
     */
    private void submit() {
    	String gddm = stockHolder.getSelectedItem().toString();
		String zqdm = stockCode.getText().toString();
		String qty = number.getText().toString();
		String jg = price.getText().toString();
		
		if(zqdm==null||zqdm.equals("")) {
			toast("证券代码不能为空");
			return;
		}
		if(jg==null||jg.equals("")) {
			toast("行权价格不能为空");
			return;
		}
		if(qty==null||qty.equals("")) {
			toast("行权数量不能为空");
			return;
		}
		
		String mess = "股东代码:" + gddm + "\n";
		      mess += "证券代码:" + zqdm + "\n";
			  mess += "行权价格:" + jg + "\n";
			  mess += "行权数量:" + qty + "\n";
		new AlertDialog.Builder(StockWarrant.this)
		.setTitle("委托提示")
		.setMessage(mess)
		.setPositiveButton("确定", 
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CharSequence title = "正在与服务器通讯握手...";
					CharSequence message = "正在往服务器提交数据...";
					
					ProgressDialog myDialog = ProgressDialog.show(StockWarrant.this, title, message, true);
					
					StringBuffer sb = new StringBuffer();
		    		sb.append("market=" + market + TradeUtil.SPLIT);
		    		sb.append("secuid=" + holder.get(stockHolder.getSelectedItemPosition()) + TradeUtil.SPLIT);
		    		sb.append("stkcode=" + stockCode.getText() + TradeUtil.SPLIT);
		    		sb.append("bsflag=" + BSFLAG + TradeUtil.SPLIT);
		    		sb.append("price=" + price.getText() + TradeUtil.SPLIT);
					sb.append("qty=" + number.getText().toString() + TradeUtil.SPLIT);
					sb.append("ordergroup=0" + TradeUtil.SPLIT);
					sb.append("remark=" + TradeUtil.SPLIT);
					sb.append("bankcode=" + TradeUser.getInstance().getBankcode() + TradeUtil.SPLIT);
		    		sb.append("bankpwd=" + TradeUtil.SPLIT);

					try {
						JSONObject quoteData = ConnPool.sendReq("STOCK_WARRANT", "410411", sb.toString());
			    		String res = TradeUtil.checkResult(quoteData);
			    		if(res!=null) {
			    			if(res.equals("-1")){
			    				//showTipsDialog((String)getApplicationContext().getResources().getString(R.string.network_error));
			    			}
			    			else
			    				toast("委托失败：" + res);
			    		}else {
				    		JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
				    		JSONObject j = (JSONObject)jArr.get(0);
							toast("委托成功，委托号：" + j.getString("ordersno"));
			    		}
					} catch (JSONException e) {
						Log.e(DEBUG_TAG, e.toString());
					}
					
					myDialog.dismiss();
				}
		})
		.setNegativeButton("取消",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//
					}
				})
		.show();
    }
    
    private void cancel() {
    	finish();
    }
    
    protected void onPause() {
    	mHandler.removeCallbacks(r);
    	super.onPause();
    }
    @Override
	protected void toolBarClick(int tag, View v) {
		 switch(tag) {
			case 0:
				submit();
				break;
			case 1: 
				cancel();
				break;
		 }
	}
}
