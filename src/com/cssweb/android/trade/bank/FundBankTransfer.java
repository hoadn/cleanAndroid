package com.cssweb.android.trade.bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.AES;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;

/**
 * 银证转账(证券转银行)
 * @author FredZhong
 * 2011/3/17
 */
public class FundBankTransfer extends CssKeyboardBase {
	protected static final String request = null;

	private final String DEBUG_TAG = "FundBankTransfer";
	
	private Spinner currency;
	private Spinner bank;
	private EditText amount;
	private EditText fundpwd;
	private EditText bankpwd;
	private ArrayAdapter<String> typeAdapter;
	private ArrayAdapter<String> currencyAdapter;
	private ArrayAdapter<String> bankAdapter;
	private String[] transferTypeLabels;
	private String[] bankLabels;
	private String[] bankVal;
	private String[] bankAccount;
	private String[] currencyLabels;
	private String[] currencyVal;
	private Thread thread = null;
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.zr_bankfundtransfer);
		setTitle("资金转入");
		initTitle(R.drawable.njzq_title_left_back, 0, "资金转出" );
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_QUEDING,"","","","", Global.TOOLBAR_BACK };
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.zryzzzlayout);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);

	    currency = (Spinner) findViewById(R.id.Currency);
	    bank = (Spinner) findViewById(R.id.Bank);
	    amount = (EditText) findViewById(R.id.Amount);
	    fundpwd = (EditText)findViewById(R.id.FundPwd);
		
		fundpwd.setInputType(InputType.TYPE_NULL);
		fundpwd.setFocusable(true);
		fundpwd.setTag("");
		fundpwd.setOnClickListener(setOnEditClickListener);
		fundpwd.setOnFocusChangeListener(setOnEditFocusListener);
		
		bankpwd = (EditText)findViewById(R.id.BankPwd);
		bankpwd.setInputType(InputType.TYPE_NULL);
		bankpwd.setFocusable(true);
		bankpwd.setTag("");
		bankpwd.setOnClickListener(setOnEditClickListener);
		bankpwd.setOnFocusChangeListener(setOnEditFocusListener);

		amount.setInputType(InputType.TYPE_NULL);
		amount.setFocusable(true);
		amount.setTag("NUMDOT");
		amount.setOnClickListener(setOnEditClickListener);
		amount.setOnFocusChangeListener(setOnEditFocusListener);
		
		transferTypeLabels = getResources().getStringArray(R.array.zr_trade_yzzz_type);
		typeAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, transferTypeLabels);
		typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		currencyLabels = getResources().getStringArray(R.array.bank_transfer2_moneytype);
		currencyVal = getResources().getStringArray(R.array.bank_transfer2_moneytype_val);
		
		currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyLabels);
		currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currency.setAdapter(currencyAdapter);
		
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) bank.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		bank.setLayoutParams(linearParams1);
		
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) currency.getLayoutParams();//获取queryMethod控件的当前布局
		linearParams.height=spinnerheight;//对该控件的布局参数做修改
		currency.setLayoutParams(linearParams);
		
		setToolBar();
	}

    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	
	@Override
	protected void init(int type) {
		r = new Runnable() {
			public void run() {
				try {
					String filedate = ActivityUtil.getPreference(FundBankTransfer.this, "openBanksDate", "");
					if (!(filedate).equals(DateTool.getToday())) { // 如果时间不匹配，重新到柜台获获取
						quoteData = TradeService.getBanks();
					} else {
						String jsonObject = CssIniFile.loadIni(FundBankTransfer.this, 8, "banks");
						if (null != jsonObject && !jsonObject.equals("")) {
							quoteData = new JSONObject(jsonObject);
						}
					}
					String res = TradeUtil.checkResult(quoteData);
					if(res==null){
						JSONArray jarr  = quoteData.getJSONArray("item");
						if(jarr.length() >=1){
							bankLabels = new String [jarr.length() - 1 ];
							bankVal = new String [jarr.length() - 1];
							bankAccount = new String [jarr.length() - 1];
							for (int i = 0,size = jarr.length() -1 ; i<size ;i++){
								JSONObject jsonobject = (JSONObject) jarr.get(i);
								String bankCode = jsonobject.getString("FID_YHDM");
								String bankName = TradeUtil.getBankName(bankCode);
								String bankyhzh = jsonobject.getString("FID_YHZH");
								bankLabels[i] = bankName;
								bankVal[i] = bankCode;
								bankAccount[i] = bankyhzh;
							}
						}else {
							bankLabels  = new String [0];
							bankVal = new String [0];
							bankAccount = new String [0];
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
					CssLog.e(DEBUG_TAG, e.toString());
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	
	@Override
	protected void handlerData() {
		try {
			String res = TradeUtil.checkResult(quoteData);
			if (res != null) {
				if (res.equals("-1")){
					toast("网络连接失败！");
					setToolBar(0, false, R.color.zr_dimgray);
				}
				else {
					toast(res);
				}
				hiddenProgressToolBar();
				return;
			}
			setToolBar(0, true, R.color.zr_white);
			bankAdapter = new ArrayAdapter<String>(FundBankTransfer.this, android.R.layout.simple_spinner_item, bankLabels);
			bankAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			bank.setAdapter(bankAdapter);
		} catch (JSONException e) {
			e.printStackTrace();
			CssLog.e(DEBUG_TAG, e.toString());
			hiddenProgressToolBar();
		}
		hiddenProgressToolBar();
	}
	
	private void submit() {
		String bankpassword = bankpwd.getText().toString().trim();
		if("".equals(bankpassword)) {
			toast("请输入银行密码!");
			return;
		}
		String fundpassword = fundpwd.getText().toString().trim();
		if("".equals(fundpassword)) {
			toast("请输入资金密码!");
			return;
		}
		String strAmount = amount.getText().toString().trim();
		if(strAmount.equals("")) {
			toast("请输入转账金额!");
			return;
		}
		StringBuffer tipMsg = new StringBuffer();
		
		tipMsg.append("操作类别：银证转账\n");
		tipMsg.append("转账方式：银行转证券(转出)\n");
		tipMsg.append("转账银行：" + bankLabels[bank.getSelectedItemPosition()] + "\n");
		tipMsg.append("转账币种：" + currencyLabels[currency.getSelectedItemPosition()] + "\n");
		tipMsg.append("转帐金额：" + strAmount + "\n");
		
		new AlertDialog.Builder(FundBankTransfer.this)
		.setTitle("转换确认")
		.setMessage(tipMsg.toString())
		.setPositiveButton("确定", 
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CharSequence title = "正在与服务器通讯握手...";
					CharSequence message = "正在往服务器提交数据...";
					ProgressDialog myDialog = ProgressDialog.show(FundBankTransfer.this, title, message, true);
					StringBuffer sb = new StringBuffer();
					String aes_fundpwd = "";
					try {
						aes_fundpwd = AES.encrypt(fundpwd.getText().toString().trim(),TradeUtil.g_pubKey);
					} catch (Exception e1) {
						e1.printStackTrace();
					}
					try {
						sb.append("FID_YHZH=" +bankAccount[bank.getSelectedItemPosition()]+ TradeUtil.SPLIT);
						sb.append("FID_BZ=" + currencyVal[currency.getSelectedItemPosition()]+ TradeUtil.SPLIT);
						sb.append("FID_ZZJE=" + amount.getText().toString().trim() + TradeUtil.SPLIT);
						sb.append("FID_ZJZH="+new TradeUtil().getZjzhByYhdm(bankVal[bank.getSelectedItemPosition()], FundBankTransfer.this)+TradeUtil.SPLIT);//资金账号
						sb.append("FID_YHDM=" + bankVal[bank.getSelectedItemPosition()]  + TradeUtil.SPLIT);
						sb.append("FID_ZJMM=" + aes_fundpwd);
						JSONObject quoteData = ConnPool.sendReq("FundBankTransfer", "203113", sb.toString());
						String res = TradeUtil.checkResult(quoteData);
			    		if (res != null) {
							if (res.equals("-1")){
								toast("网络连接失败！");
							}
							else {
								toast(res);
							}
							myDialog.dismiss();
							return;
						}else {
				    		JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
				    		JSONObject j = (JSONObject)jArr.get(0);
							toast("转账请求已提交，请在转账流水中查看转账结果!流水号：" + j.getString("FID_SQH"));
			    		}
			    		clear();
					} catch (JSONException e) {
						CssLog.e(DEBUG_TAG, e.toString());
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
	
	private void clear(){
		bankpwd.setText("");
		fundpwd.setText("");
		amount.setText("");
		currency.setAdapter(currencyAdapter);
		bank.setAdapter(bankAdapter);
	}
	
	private void cancel() {
		finish();
	}
	@Override
	protected void toolBarClick(int tag, View v) {
		 switch(tag) {
			case 0:
				submit();
				break;
			case 5: 
				cancel();
				break;
			default:
				cancelThread();
				break;
		 }
	}
	protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		initPopupWindow();
	}
	private void setBtnStatus(){
		setToolBar(1, false, R.color.zr_dimgray);
		setToolBar(2, false, R.color.zr_dimgray);
		setToolBar(3, false, R.color.zr_dimgray);
		setToolBar(4, false, R.color.zr_dimgray);
	}
}
