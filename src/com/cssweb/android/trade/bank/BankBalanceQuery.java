package com.cssweb.android.trade.bank;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
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
 * 银行余额查询
 * @author ZhongZeng
 * 2011/3/15
 */
public class BankBalanceQuery extends CssKeyboardBase{
	protected static final String request = null;

	private final String DEBUG_TAG = "BankBalanceQuery";
	
	private Spinner bank;
	private EditText bankpwd;
	private ArrayAdapter<String> bankAdapter;
	private String[] bankLabels;
	private String[] bankVal;
	private String[] bankAccount;
	private String[] currencyLabels;
	private String[] currencyVal;
	private Thread thread = null;
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setContentView(R.layout.zr_yhcx);
		
		setTitle("银行余额");
		initTitle(R.drawable.njzq_title_left_back, 0, "银行余额" );
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_QUEDING,"","","","",Global.TOOLBAR_BACK };
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.zryzzzlayout);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);

	    bank = (Spinner) findViewById(R.id.Bank);
	    bankpwd = (EditText)findViewById(R.id.BankPwd);
		
	    bankpwd.setInputType(InputType.TYPE_NULL);
	    bankpwd.setFocusable(true);
	    bankpwd.setTag("");
	    bankpwd.setOnClickListener(setOnEditClickListener);
	    bankpwd.setOnFocusChangeListener(setOnEditFocusListener);
		
	    int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) bank.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		bank.setLayoutParams(linearParams1);
		
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
					String filedate = ActivityUtil.getPreference(BankBalanceQuery.this, "openBanksDate", "");
					if (!(filedate).equals(DateTool.getToday())) { // 如果时间不匹配，重新到柜台获获取
						quoteData = TradeService.getBanks();
					} else {
						String jsonObject = CssIniFile.loadIni(BankBalanceQuery.this, 8, "banks");
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
							currencyLabels = new String [jarr.length() - 1];
							currencyVal = new String [jarr.length() - 1];
							for (int i = 0,size = jarr.length() -1 ; i<size ;i++){
								JSONObject jsonobject = (JSONObject) jarr.get(i);
								String bankCode = jsonobject.getString("FID_YHDM");
								String bankName = TradeUtil.getBankName(bankCode);
								String bankyhzh = jsonobject.getString("FID_YHZH");
								String bankbz = jsonobject.getString("FID_BZ");
								String bankbzName = TradeUtil.getMoneyName(bankbz);
								bankLabels[i] = bankName;
								bankVal[i] = bankCode;
								bankAccount[i] = bankyhzh;
								currencyLabels[i] = bankbzName;
								currencyVal[i] = bankbz;
							}
						}else {
							bankLabels  = new String [0];
							bankVal = new String [0];
							bankAccount = new String [0];
							currencyLabels = new String [0];
							currencyVal = new String [0];
						}
					}
				} catch (JSONException e) {
					e.printStackTrace();
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
			bankAdapter = new ArrayAdapter<String>(BankBalanceQuery.this, android.R.layout.simple_spinner_item, bankLabels);
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
		try {
			bankpassword = AES.encrypt(bankpassword,TradeUtil.g_pubKey);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		CharSequence title = "正在与服务器通讯握手...";
		CharSequence message = "正在往服务器提交数据...";
		
		ProgressDialog myDialog = ProgressDialog.show(BankBalanceQuery.this, title, message, true);
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("FID_YHDM=" + bankVal[bank.getSelectedItemPosition()]  + TradeUtil.SPLIT);//银行代码
			sb.append("FID_ZJZH="+new TradeUtil().getZjzhByYhdm(bankVal[bank.getSelectedItemPosition()], BankBalanceQuery.this)+TradeUtil.SPLIT);//资金账号
			sb.append("FID_YHZH=" + bankAccount[bank.getSelectedItemPosition()]  + TradeUtil.SPLIT);//银行账号
			sb.append("FID_BZ=" + currencyVal[bank.getSelectedItemPosition()]  + TradeUtil.SPLIT);//币种
			sb.append("FID_WBZHMM=" +bankpassword);//银行密码
			quoteData = ConnPool.sendReq("BankBalanceQuery", "203119", sb.toString());
    		String res = TradeUtil.checkResult(quoteData);
    		if (res != null) {
				if (res.equals("-1")){
					toast("网络连接失败！");
					setToolBar(0, false, R.color.zr_dimgray);
				}
				else {
					toast(res);
				}
				myDialog.dismiss();
				return;
			}
    		else {
	    		JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
	    		JSONObject j = (JSONObject)jArr.get(0);
				toast("请在转帐查询中查看银行余额，银行流水号为：" + j.getString("FID_SQH"));
    		}
    		clear();
		} catch (JSONException e) {
			CssLog.e(DEBUG_TAG, e.toString());
		}

		myDialog.dismiss();
	}
	
	private void clear(){
		bankpwd.setText("");
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
