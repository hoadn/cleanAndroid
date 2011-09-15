package com.cssweb.android.trade.login;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;

import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.AES;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.service.ValidationService;
import com.cssweb.android.service.ZixunService;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.share.StockPreference;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.ui.view.CheckBoxView;
import com.cssweb.android.user.track.LoginInfo;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.web.WebViewDisplay;

public class LoginActivity extends CssBaseActivity {
	//private Activity a = this;
//	private static final String DEBUG_TAG = "LoginActivity";
	private Button button1;
	private Button button2;
	private Button loginbutton;
	private AutoCompleteTextView zrfundid;
	private TextView pwdTextView;
	private EditText zrjypass;
	private ImageView loginFundIdImg;//账号输入框中的箭头
	private ImageView loginPwdDelImg;//账号输入框中的箭头
	private PopupWindow mPop;//点击账号里面的小箭头 弹出的对话框
	private ListView listAccount;
	private Button findpswButton;
	private Button khButton;
	private Button sqtykButton;
//	private String[] myFundInfosTrade;
	private String[] myFundInfosService;
//	private String[] preferredFundInfoTrade;
	private String[] preferredFundInfoService;
	private List<String> fundIdsService = new ArrayList<String>();
	private List<String> fundIdPwdService = new ArrayList<String>();
//	private List<String> fundIdsTrade = new ArrayList<String>();
	private AccountListAdapter adapterAccountListService;
//	private AccountListAdapter adapterAccountListTrade;
	private JSONObject jA = null;
	private String market = null ;
	//是否保存账号
	private boolean isSaved = false;
	//是否保存密码
	private boolean isSavedPwd = false;
	//保存账号按钮
	private CheckBoxView cbx0;
	//保存密码
	private CheckBoxView cbx1;
	private TextView savePwdLbl;
	private int isActivity;
	private Boolean isChangeBtn = false;
	private int position;
	private int pos;
	private String url = "";
	private boolean isServicePassword = false;  // false： 交易密码
	private String loginFundid = "";
	private String tempFundid = "";
	private int flag = 1;
	private String custid = "";
	
	@Override
	public void onCreate(Bundle paramBundle) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(paramBundle);
		Bundle bundle = getIntent().getExtras();
		this.isActivity = bundle.getInt("menu_id");
		this.url = bundle.getString("url");
		isChangeBtn = bundle.getBoolean("isChangeBtn");
		custid = bundle.getString("custid");
		setContentView(R.layout.njzq_trade_login);
		initTitle(R.drawable.njzq_title_left_back, 0, "交易用户登录");
		button1 = (Button) findViewById(R.id.button1);
		button2 = (Button) findViewById(R.id.button2);
		pwdTextView = (TextView)findViewById(R.id.TextView02);
		loginbutton = (Button) findViewById(R.id.surebutton);
		findpswButton = (Button) findViewById(R.id.buttonpwd);
		khButton = (Button) findViewById(R.id.buttonkaihu);
		sqtykButton = (Button) findViewById(R.id.buttonshen);
		zrfundid = (AutoCompleteTextView) findViewById(R.id.fundid);
		//给zrfundid增加获得焦点的响应   
		zrfundid.setOnFocusChangeListener(new OnFocusChangeListener(){   
		    public void onFocusChange(View v, boolean hasFocus){   
		        if(hasFocus){   //获得焦点  
		        	String fundid = zrfundid.getText().toString().trim();
		        	if(fundid == null || "".equals(fundid) || fundid.length() <= 4){
		        		tempFundid = fundid;
		        	}else {
		        		if (tempFundid.indexOf("*")!=-1 && flag==1) {
		        			zrfundid.setText(tempFundid);
						}else {
							zrfundid.setText(loginFundid);
						}
					}
		        } 
		    }   
		});
		zrjypass =(EditText) findViewById(R.id.pwd);
		//给zrjypass增加获得焦点的响应   
		zrjypass.setOnFocusChangeListener(new OnFocusChangeListener(){   
		    public void onFocusChange(View v, boolean hasFocus){   
		        if(hasFocus){   //获得焦点  
		        	flag = 2;
		        	if (zrfundid.getText().toString().trim().indexOf("*")!=-1) {
		        		
					}else {
						setYanMa();
					}
		        } 
		    }   
		}); 
		
		zrjypass.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
		});
		button1.setOnClickListener(clickListener1);
		button2.setOnClickListener(clickListener2);
		loginbutton.setOnClickListener(myShowProgreeBar);
		findpswButton.setOnClickListener(listener);
		khButton.setOnClickListener(listener);
		sqtykButton.setOnClickListener(listener);
		cbx0 = (CheckBoxView) findViewById(R.id.checkbox);
		cbx1 = (CheckBoxView) findViewById(R.id.savepsd);
		cbx0.setClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (cbx0.getChecked()) {
					isSaved = true;
					cbx1.setEnabled(true);
				}else {
					isSaved = false;
					cbx1.setChecked(false);
					cbx1.setEnabled(false);
				}
			}
		});
		
		savePwdLbl = (TextView)findViewById(R.id.label3);
		cbx1.setClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(cbx1.getChecked()){
					isSavedPwd = true;
				}else{
					isSavedPwd = false;
				}
			}
		});
		
		loginFundIdImg = (ImageView)findViewById(R.id.loginFundIdImg);
		initPopMenu();
		initPopupWindow();
		
		//如果点击账号后面的图片，弹出提示框，用来列出 保存的所有账号信息
		loginFundIdImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				//init();
				if(mPop.isShowing()){
					mPop.dismiss();
				}
				else {
					initPopMenu();
					if(v instanceof ImageButton)
					{
						ImageButton img = (ImageButton) findViewById(R.id.loginFundIdImg);
						mPop.showAsDropDown(img);
					}
					mPop.showAsDropDown(v, -100, -5);
				}
			}
		});
		
		loginPwdDelImg = (ImageView)findViewById(R.id.loginPwdDelImg);
		//点击按钮后面的删除按钮 ，就把当前保存的按钮设置为空
		loginPwdDelImg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				zrjypass.setText("");
			}
		});
		
		//手工调用方法给按钮设置背景颜色
		isServicePassword = true;
		button1.setBackgroundResource(R.drawable.btnlogin1);
		button2.setBackgroundResource(R.drawable.btnlogin4);
		button1.setTextColor(getResources().getColor(R.color.zr_white));
		button2.setTextColor(getResources().getColor(R.color.zr_dimgray));
		zrjypass.setInputType(EditorInfo.TYPE_CLASS_TEXT);
		ActivityUtil.setPasswordType(zrjypass, true);
		savePwdLbl.setVisibility(View.VISIBLE);
		cbx1.setVisibility(View.VISIBLE);
		pwdTextView.setText("服务密码");
		if (null !=bundle){
			if(isChangeBtn){
				isServicePassword = false;
				button1.setBackgroundResource(R.drawable.btnlogin2);
				button2.setBackgroundResource(R.drawable.btnlogin3);
				button1.setTextColor(getResources().getColor(R.color.zr_dimgray));
				button2.setTextColor(getResources().getColor(R.color.zr_white));
				button1.setEnabled(false);
				button2.setEnabled(false);
				zrjypass.setInputType(EditorInfo.TYPE_CLASS_PHONE);
				ActivityUtil.setPasswordType(zrjypass, true);
				savePwdLbl.setVisibility(View.INVISIBLE);
				cbx1.setVisibility(View.INVISIBLE);
				pwdTextView.setText("交易密码");
			}else {
				button1.setEnabled(true);
				button2.setEnabled(true);
			}
		}
		init();
		isChangeBtn = false;
		if(zrfundid.getText().toString().trim().length()>0){
			zrjypass.setFocusable(true);
			zrjypass.requestFocus();
		}
		if (custid!=null && !"".equals(custid)) {
			button1.setEnabled(true);
			button2.setEnabled(true);
			zrfundid.setText(custid);
		}
	}
	
	private void setYanMa(){
		if(zrfundid.getText().toString().trim().indexOf("*")!=-1){
			
		}else{
			loginFundid = zrfundid.getText().toString().trim();
		}
    	if(loginFundid == null || "".equals(loginFundid) || loginFundid.length() <= 4){
    		tempFundid = loginFundid;
    	}else {
    		String startempFundid = loginFundid.substring(0,2);
    		String endtempFundid = loginFundid.substring(loginFundid.length()-2,loginFundid.length());
    		StringBuffer sbBuffer = new StringBuffer();
    		for(int i=0;i<loginFundid.length()-4;i++){
    			sbBuffer.append("*");
    		}
    		tempFundid = startempFundid + sbBuffer.toString() + endtempFundid;
		}
    	zrfundid.setText(tempFundid);
	
	}
	
	private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonpwd:
				if(isServicePassword){
					FairyUI.switchToWnd(Global.NJZQ_RESET_SERVIR_PASSWORD, LoginActivity.this);
				}else{
					FairyUI.switchToWnd(Global.NJZQ_FIND_PASSWORD, LoginActivity.this);
				}
				break;
				
			case R.id.buttonkaihu:
				FairyUI.switchToWnd(Global.NJZQ_JLP_YYKHTAG, LoginActivity.this);
				break;
				
			case R.id.buttonshen:
				FairyUI.switchToWnd(Global.NJZQ_SQTYK, LoginActivity.this);
				break;
			default:
				break;
			}
		}
	};
	
	
	
	protected void textChanged(Editable s) {
		Editable localDEditable = zrjypass.getText();
		if(localDEditable == s){
			String str = zrjypass.getText().toString().trim();
			if(str.length() > 0){
				loginPwdDelImg.setVisibility(View.VISIBLE);
			}else {
				loginPwdDelImg.setVisibility(View.GONE);
			}
		}
	}
    private void init() {
		getSaveFunds();
		setYanMa();
		getPreferred();
		setBranches(null);
	}
    
	private void setBranches(String areaId){
		if(preferredFundInfoService != null){
			int fundPosition = -1;
			if (myFundInfosService!=null) {
				for(int i=0,size=myFundInfosService.length; i<size; i++){
					String fundInfo = myFundInfosService[i];
					String[] raw = fundInfo.split("\\|");
					if (raw[0].indexOf("&")!=-1) {
						raw[0] = raw[0].substring(0,raw[0].indexOf("&"));
					}
					if(preferredFundInfoService[0].equals(raw[0]))
						fundPosition = i;
				}
			}
			if(fundPosition!=-1 && fundPosition !=0)
				setFunds(fundPosition);
			else
				setFunds(-1);
		}else{
			setFunds(-1);
		}
	}
	
    private void setFunds(int position){
    	if(isSaved && myFundInfosService!=null){
			fundIdsService.clear();
			for(int i=0; i<myFundInfosService.length; i++){
				String fundInfo = myFundInfosService[i];
				fundIdPwdService.add(fundInfo);
				String[] raw = fundInfo.split("\\|");
				if (raw[0].indexOf("&") != -1) {
					raw[0] = raw[0].substring(0,raw[0].indexOf("&"));
				}
				fundIdsService.add(raw[0]);
			}
			adapterAccountListService = new AccountListAdapter(this, R.layout.zr_login_account_list_items, (String[])fundIdsService.toArray(new String[fundIdsService.size()]));
			listAccount.setAdapter(adapterAccountListService);
			if(position != -1){
				zrfundid.setDropDownAnchor(position);
				loginFundid = fundIdsService.get(zrfundid.getDropDownAnchor());
				String serviceidpwd = fundIdPwdService.get(zrfundid.getDropDownAnchor());
				String pwd = "";
				if (serviceidpwd.indexOf("&") != -1) {
					pwd = serviceidpwd.substring(serviceidpwd.indexOf("&")+1,serviceidpwd.length());
				}
				String startempFundid = loginFundid.substring(0,2);
        		String endtempFundid = loginFundid.substring(loginFundid.length()-2,loginFundid.length());
        		StringBuffer sbBuffer = new StringBuffer();
        		for(int i=0;i<loginFundid.length()-4;i++){
        			sbBuffer.append("*");
        		}
        		tempFundid = startempFundid + sbBuffer.toString() + endtempFundid;
        		zrfundid.setText(tempFundid);
        		zrjypass.setText(pwd);
			}else{
				if(fundIdsService.size()>0){
					loginFundid = fundIdsService.get(0);
					String serviceidpwd = fundIdPwdService.get(0);
    				String pwd = "";
    				if (serviceidpwd.indexOf("&") !=-1) {
    					pwd = serviceidpwd.substring(serviceidpwd.indexOf("&")+1,serviceidpwd.length());
					}
					String startempFundid = loginFundid.substring(0,2);
	        		String endtempFundid = loginFundid.substring(loginFundid.length()-2,loginFundid.length());
	        		StringBuffer sbBuffer = new StringBuffer();
	        		for(int i=0;i<loginFundid.length()-4;i++){
	        			sbBuffer.append("*");
	        		}
	        		tempFundid = startempFundid + sbBuffer.toString() + endtempFundid;
	        		zrfundid.setText(tempFundid);
	        		zrjypass.setText(pwd);
				}else{
					zrfundid.setText(null);
					zrjypass.setText(null);
				}
			}
		}
    	if (!isServicePassword){
    		zrjypass.setText("");
    	}
	}
    
	private void getSaveFunds(){
		// 读取保存的用户账号
		String myFundIdsStr = StockPreference.getCustNo(this);
		if (myFundIdsStr != null){
			isSaved = true;
			cbx0.setChecked(true);
			myFundInfosService = myFundIdsStr.split(",");
			loginFundid = myFundInfosService[myFundInfosService.length-1];
			String pwd = "";
			if (loginFundid.indexOf("&")!=-1) {
				isSavedPwd = true;
				cbx1.setChecked(true);
				String idpwd = loginFundid;
				loginFundid = loginFundid.substring(0,loginFundid.indexOf("&"));
				pwd = idpwd.substring(idpwd.indexOf("&")+1,idpwd.length());
			}
			String startempFundid = loginFundid.substring(0,2);
    		String endtempFundid = loginFundid.substring(loginFundid.length()-2,loginFundid.length());
    		StringBuffer sbBuffer = new StringBuffer();
    		for(int i=0;i<loginFundid.length()-4;i++){
    			sbBuffer.append("*");
    		}
    		tempFundid = startempFundid + sbBuffer.toString() + endtempFundid;
    		zrfundid.setText(tempFundid);
    		zrjypass.setText(pwd);
		}else{
			zrfundid.setText(null);
			zrjypass.setText(null);
		}
		if (!isServicePassword){
    		zrjypass.setText("");
    	}
	}
	
	private void getPreferred() {
		String str = StockPreference.getPreferredCustNo(this);
		if(str!=null && !str.equals("")){
			preferredFundInfoService = str.split("\\|");
		}
	}	
	
	private void initPopMenu() {
		View popContent = getLayoutInflater().inflate(R.layout.zr_login_popub, null, false);
		listAccount = (ListView)popContent.findViewById(R.id.listAccount);
		listAccount.setAdapter(adapterAccountListService);
		listAccount.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				TextView txt = (TextView)arg0.getChildAt(position).findViewById(R.id.TextView01);
				loginFundid = txt.getText().toString().trim();
				if (isServicePassword) {
					for(int i=0;i<fundIdPwdService.size();i++){
						String idpwd = fundIdPwdService.get(i);
						if(idpwd.indexOf("&")!=-1){
							if (fundIdPwdService.get(i).indexOf(loginFundid)!=-1) {
								loginFundid = idpwd.substring(0,idpwd.indexOf("&"));
								String pwd = idpwd.substring(idpwd.indexOf("&")+1,idpwd.length());
								zrjypass.setText(pwd);
								break;
							}
						}
					}
				}
		    	if(loginFundid == null || "".equals(loginFundid) || loginFundid.length() <= 4){
		    		tempFundid = loginFundid;
		    	}else {
		    		String startempFundid = loginFundid.substring(0,2);
		    		String endtempFundid = loginFundid.substring(loginFundid.length()-2,loginFundid.length());
		    		StringBuffer sbBuffer = new StringBuffer();
		    		for(int i=0;i<loginFundid.length()-4;i++){
		    			sbBuffer.append("*");
		    		}
		    		tempFundid = startempFundid + sbBuffer.toString() + endtempFundid;
				}
		    	zrfundid.setText(tempFundid);
				if(mPop!=null && mPop.isShowing())
					mPop.dismiss();
			}
		});
		
		mPop = new PopupWindow(popContent, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		mPop.setOutsideTouchable(true);
		
		mPop.getContentView().setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if(mPop != null)
					mPop.dismiss();
			}
		});
	}
	
	private OnClickListener clickListener1 = new OnClickListener() {  //服务密码
		public void onClick(View v) {
			button1.setBackgroundResource(R.drawable.btnlogin1);
			button2.setBackgroundResource(R.drawable.btnlogin4);
			button1.setTextColor(getResources().getColor(R.color.zr_white));
			button2.setTextColor(getResources().getColor(R.color.zr_dimgray));
			isServicePassword = true;
			zrjypass.setInputType(EditorInfo.TYPE_CLASS_TEXT);
			ActivityUtil.setPasswordType(zrjypass, true);
			savePwdLbl.setVisibility(View.VISIBLE);
			cbx1.setVisibility(View.VISIBLE);
			pwdTextView.setText("服务密码");
			init();
			setYanMa();
			if (custid!=null && !"".equals(custid)) {
				zrfundid.setText(custid);
				zrjypass.setText("");
			}
		}
	};
	private OnClickListener clickListener2 =new OnClickListener() {   //交易密码
		public void onClick(View v) {
			button1.setBackgroundResource(R.drawable.btnlogin2);
			button2.setBackgroundResource(R.drawable.btnlogin3);
			button1.setTextColor(getResources().getColor(R.color.zr_dimgray));
			button2.setTextColor(getResources().getColor(R.color.zr_white));
			isServicePassword = false;
			zrjypass.setInputType(EditorInfo.TYPE_CLASS_PHONE);
			ActivityUtil.setPasswordType(zrjypass, true);
			savePwdLbl.setVisibility(View.INVISIBLE);
			cbx1.setVisibility(View.INVISIBLE);
			pwdTextView.setText("交易密码");
			init();
			setYanMa();
			if (custid!=null && !"".equals(custid)) {
				zrfundid.setText(custid);
			}
			zrjypass.setText("");
		}
	};
	
	Button.OnClickListener myShowProgreeBar = new Button.OnClickListener() {
		public void onClick(View arg0) {
			String _fundid = zrfundid.getText().toString().trim();
			if (_fundid == null || _fundid.equals("")) {
				toast("账号不能为空！");
				return;
			}

			String _pwd = zrjypass.getText().toString().trim();
			if (_pwd == null || _pwd.equals("")) {
				toast("密码不能为空！");
				return;
			}
			showProgress();
		}
	};
	
	protected void init(final int type) {
		new Thread() {
			public void run() {
				try {
					if (zrfundid.isFocusable()&&zrfundid.getText().toString().trim().indexOf("*")==-1) {
						loginFundid = zrfundid.getText().toString().trim();
					}
					if ( isServicePassword ){
						String custStr = loginFundid;
						if (null !=custStr){
							custStr = custStr.trim();
						}
						String passwordStr = zrjypass.getText().toString();
						if (null !=passwordStr){
							passwordStr = passwordStr.trim();
						}
						String passwordAES = AES.encrypt(passwordStr , TradeUtil.g_pubKey);
						String custAES = AES.encrypt(custStr , TradeUtil.g_pubKey);
//						Log.i("<<<<<<<<<<服务 账号>>>>>>>>>>>>>>>", loginFundid);
//						Log.i("<<<<<<<<<<服务 密码>>>>>>>>>>>>>>>", zrjypass.getText().toString());
						quoteData = ConnPool.servicePasswordLogin(custAES, passwordAES);
					}else {
						TradeUser.getInstance().setIsSafe("0");
						//设置用户的客户号
						TradeUser.getInstance().setCustid(loginFundid);
						TradeUser.getInstance().setPassword(AES.encrypt(zrjypass.getText().toString().trim(),TradeUtil.g_pubKey));// 交易密码通过密文AES加密
						
						SharedPreferences sharedPreferences = getSharedPreferences("mobile",Context.MODE_PRIVATE);
						String mobileNum = sharedPreferences.getString("mobileNum", "");
						TradeUser.getInstance().setClientip("ZRSJ:"+mobileNum);

						StringBuffer sb = new StringBuffer();
						sb.append("FID_JYMM="+TradeUser.getInstance().getPassword()+"&");
						sb.append("iphoneflag=1");
//						Log.i("<<<<<<<<<<交易 账号>>>>>>>>>>>>>>>", loginFundid);
//						Log.i("<<<<<<<<<<交易 密码>>>>>>>>>>>>>>>", zrjypass.getText().toString());
						quoteData = ConnPool.sendReq("TRADE_LOGIN","190101", sb.toString());
					}
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}
		}.start();
	}

	@SuppressWarnings("unused")
	public void handlerData() {
		JSONObject jsonObject = quoteData;
		try {
			String res = TradeUtil.checkResult(jsonObject);
			if (res != null) {
				if (res.equals("-1")){
					String errMsg = "网络连接异常！请检查您的网络是否可用。";
					Toast.makeText(LoginActivity.this, errMsg, Toast.LENGTH_LONG).show();
					LoginInfo.getInstance().setLoginErrorDesc(errMsg);
					//openDialog(R.string.network_error);
				}
				else {
					toast(res);
					LoginInfo.getInstance().setLoginErrorDesc(res);
				}
				LoginInfo.getInstance().setLoginState("0");
				hiddenProgress();
				return;
			}
			else {
				LoginInfo.getInstance().setLoginState("1");
				LoginInfo.getInstance().setSystemCode("JLP_ANDROID");
				LoginInfo.getInstance().setLoginID(loginFundid);
				LoginInfo.getInstance().setUserType("1");
				
				if (isServicePassword){     //服务密码
					LoginInfo.getInstance().setLoginType("18");
					String returnUrl = quoteData.getString("returnUrl");
					String cssweb_code = quoteData.getString("cssweb_code");
					if (null!=cssweb_code && cssweb_code.equals("success")){
						//保存用户的客户号和密码
						if(isSaved && !isSavedPwd){
							String myFundIdsStr = StockPreference.getCustNo(this);
							if(null != myFundIdsStr && myFundIdsStr.indexOf("&")!=-1 ){
								ActivityUtil.clearServicePwd(LoginActivity.this);
							}
							StockPreference.saveCustNo(LoginActivity.this, loginFundid);
							StockPreference.setPreferredCustNo(LoginActivity.this, loginFundid);
						}else if (isSaved && isSavedPwd) {
							delSameId();
							StockPreference.saveCustNo(LoginActivity.this, loginFundid+"&"+zrjypass.getText().toString().trim() );
							StockPreference.setPreferredCustNo(LoginActivity.this, loginFundid);
						}else if(!isSaved){
							SharedPreferences sharedPreferences = LoginActivity.this.getSharedPreferences(
									"com.cssweb.lcdt.clientcustno", Context.MODE_PRIVATE);
							Editor editor = sharedPreferences.edit();// 获取编辑器
							editor.putString("myCustNos","");
							editor.commit();// 提交修改
							StockPreference.setPreferredCustNo(LoginActivity.this, null);
						}
						//设置用户类型
						TradeUser.getInstance().setUserType("serv");
						TradeUser.getInstance().setLoginType(2);
						TradeUser.getInstance().setCustid(loginFundid);
						TradeUser.getInstance().setFundid(loginFundid);
						
						if (null!=returnUrl && returnUrl.equals("1")){   //跳转到jsp
							int level = ZixunService.getUserLevel(TradeUser.getInstance().getUserType(), TradeUser.getInstance().getCustid());
							TradeUser.getInstance().setUserLevel(level);
							
							Intent localIntent = new Intent();	    
							localIntent.putExtra("url", Config.roadZixun + "iphone/main/serv_pwd_first.jsp" + "?serviceTime="+ValidationService.getServiceTime());
							
							Log.i("tag", "first success url: "+url);
							
							localIntent.setClass(LoginActivity.this, WebViewDisplay.class);
							LoginActivity.this.startActivity(localIntent);
							
							LoginActivity.this.finish();
						}else {
							getUserLevel();
						}
					}
				}else {						//交易密码
					LoginInfo.getInstance().setLoginType("19");
					JSONArray jArr = (JSONArray) jsonObject.getJSONArray("item");
					for (int i = 0; i < jArr.length() - 1; i++) {
						jA = (JSONObject) jArr.get(i);
					}
					
					TradeUser.getInstance().setCustid(jA.getString("FID_KHH"));
					TradeUser.getInstance().setFundid(jA.getString("FID_KHH"));
					TradeUser.getInstance().setRealName(jA.getString("FID_KHXM"));
					TradeUser.getInstance().setCustGroup(jA.getString("FID_KHQZ"));
					TradeUser.getInstance().setOrgid(jA.getString("FID_YYB"));
					LoginInfo.getInstance().setRealName(jA.getString("FID_KHXM"));
					LoginInfo.getInstance().setOrgID(jA.getString("FID_YYB"));
					
					String riskLeve = (String) jA.getString("FID_TZZFL");
					///////////////////////////////////////////////////////////
					// 基金风险测评状态查询，将结果保存至session中
					// riskLevel: 0 未设定 1 保守型（默认），2 稳健型，3 积极型,4 激进型
					///////////////////////////////////////////////////////////
					if(riskLeve.equals("0"))
					{
						TradeUser.getInstance().setRiskLevel( "0" );
						TradeUser.getInstance().setRiskLevelName( "未设定" );
						TradeUser.getInstance().setRiskScore( "0" );
					}
					else if(riskLeve.equals("1"))
					{
						TradeUser.getInstance().setRiskLevel( "1" );
						TradeUser.getInstance().setRiskLevelName( "保守型" );
						TradeUser.getInstance().setRiskScore( "1" );				
					}
					else if(riskLeve.equals("2"))
					{
						TradeUser.getInstance().setRiskLevel( "2" );
						TradeUser.getInstance().setRiskLevelName( "稳健型" );
						TradeUser.getInstance().setRiskScore( "2" );	
					}
					else if(riskLeve.equals("3"))
					{
						TradeUser.getInstance().setRiskLevel( "3" );
						TradeUser.getInstance().setRiskLevelName( "积极型" );
						TradeUser.getInstance().setRiskScore( "3" );				
					}
					else if(riskLeve.equals("4"))
					{
						TradeUser.getInstance().setRiskLevel( "4" );
						TradeUser.getInstance().setRiskLevelName( "激进型" );
						TradeUser.getInstance().setRiskScore( "4" );				
					}
					
					//保存用户的客户号
					if(isSaved)
					{
						Boolean sameFlag = true;
						String myFundIdsStr = StockPreference.getCustNo(this);
						if (myFundIdsStr!=null) {
							if(myFundIdsStr.indexOf(loginFundid)==-1) {
								StockPreference.saveCustNo(LoginActivity.this, TradeUser.getInstance().getCustid());
							}
						}else {
							StockPreference.saveCustNo(LoginActivity.this, TradeUser.getInstance().getCustid());
						}
						StockPreference.setPreferredCustNo(LoginActivity.this, TradeUser.getInstance().getCustid());
					}
					//设置用户类型
					TradeUser.getInstance().setUserType("serv");
					TradeUser.getInstance().setLoginType(1);
					
					//getFundList();
					getStockAccountList();
					getUserLevel();
				}
			}
		}catch(Exception e){
			e.printStackTrace();
			hiddenProgress();
		}
	}
//	private void getFundList() {
//		new AsyncTask<Void, Void, Boolean>() {
//			@Override
//			protected Boolean doInBackground(Void... arg0) {
//				try {
//					JSONObject fundInfo = ConnPool.sendReq("GET_FUNDLIST","303002","");
//					String resStr = TradeUtil.checkResult(fundInfo);
//					if(resStr == null){
//						JSONArray fundArray = fundInfo.getJSONArray("item");
//						for(int i=0,size=fundArray.length()-1; i<size; i++){
//							JSONObject jsonObj = (JSONObject)fundArray.get(i);
//							if ("1".equals(jsonObj.getString("FID_ZZHBZ"))) {
//								TradeUser.getInstance().setFundid(jsonObj.getString("FID_ZJZH"));
//							}
//						}
//					}
//					else {
//						return Boolean.FALSE;
//					}
//				} catch (JSONException e) {
//					return Boolean.FALSE;
//				}
//				return Boolean.TRUE;
//			}
//			protected void onPostExecute(Boolean result) {
//				if (result != Boolean.TRUE) {
//					ActivityUtil.clearTradeRecord();
//					toast("获取用户信息失败,请重试!");
//					hiddenProgress();
//					return;
//				}
//			}
//		}.execute();
//	}
	private void getStockAccountList() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					JSONObject stockAccountData = ConnPool.sendReq("GET_STOCKACCOUNT","304001","");
					String resStr = TradeUtil.checkResult(stockAccountData);
					if(resStr == null){
						CssIniFile.saveIni(LoginActivity.this, 9, "ShareholdersList", stockAccountData.toString());
						ActivityUtil.savePreference(LoginActivity.this, "openholdersListDate", DateTool.getToday());
						JSONArray stockAccountArr = stockAccountData.getJSONArray("item");
						List<String> holder = new ArrayList<String>();
						//用来备份一份股东账号
						List<String> holderBak = new ArrayList<String>();
						for (int i = 0; i < stockAccountArr.length() - 1; i++) {
							jA = (JSONObject) stockAccountArr.get(i);
							market = jA.getString("FID_JYS");
							if ("SH".equals(market)) {
								TradeUser.getInstance().setSecuidSha(jA.getString("FID_GDH"));
								holderBak.add("SH-"+jA.getString("FID_GDH"));
								holder.add(jA.getString("FID_GDH"));
							} else if ("SZ".equals(market)) {
								TradeUser.getInstance().setSecuidSza(jA.getString("FID_GDH"));
								holderBak.add("SZ-"+jA.getString("FID_GDH"));
								holder.add(jA.getString("FID_GDH"));
							} else if ("HB".equals(market)) {
								TradeUser.getInstance().setSecuidShb(jA.getString("FID_GDH"));
								holderBak.add("HB-"+jA.getString("FID_GDH"));
								holder.add(jA.getString("FID_GDH"));
							} else if ("ZB".equals(market)) {
								TradeUser.getInstance().setSecuidSzb(jA.getString("FID_GDH"));
								holderBak.add("ZB-"+jA.getString("FID_GDH"));
								holder.add(jA.getString("FID_GDH"));
							}
							TradeUser.getInstance().setHolder(holder);
							TradeUser.getInstance().setHolderBak(holderBak);
						}
					}
					else {
						return Boolean.FALSE;
					}
				} catch (JSONException e) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
			protected void onPostExecute(Boolean result) {
				if (result != Boolean.TRUE) {
					ActivityUtil.clearTradeRecord();
					toast("获取用户信息失败,请重试!");
					hiddenProgress();
					return;
				}
			}
		}.execute();
	}
	protected void swinActivity() {
		hiddenProgress();
		if( url != null && !"".equals(url)){
			Intent localIntent = new Intent();	    
			localIntent.putExtra("url", this.url);
			localIntent.setClass(LoginActivity.this, WebViewDisplay.class);
			LoginActivity.this.startActivity(localIntent);
		}else if (isActivity==Global.NJZQ_WTJY) {
			FairyUI.switchToWnd(Global.NJZQ_WTJY, Global.NJZQ_WTJY, LoginActivity.this);
		}else if(isActivity==Global.NJZQ_JLP_JYYH){
			FairyUI.switchToWnd(-2, LoginActivity.this);
		}else if(isActivity!=0&&isActivity!=Global.NJZQ_WTJY) {
			if(isActivity == Global.NJZQ_WEBVIEW_LOGIN){//网页登录情况
				try {
					Bundle bundle = getIntent().getExtras();
					this.position = bundle.getInt("position");
					this.pos = bundle.getInt("pos");
				} catch (Exception e) {
					e.printStackTrace();
					this.pos = 0;
					this.position = 0;
				}
				Intent localIntent = new Intent();	    
				localIntent.putExtra("pos", pos);
				localIntent.putExtra("position", position);
				localIntent.setClass(LoginActivity.this, WebViewDisplay.class);
				LoginActivity.this.startActivity(localIntent);
			}else if(isActivity == Global.SDZ){
				
			}else{
				FairyUI.switchToWnd(isActivity, isActivity, LoginActivity.this);
			}
		}else {
			FairyUI.switchToWnd(-2, LoginActivity.this);
		}
		LoginActivity.this.finish();
	}
	@Override
	protected void onResume() {
		super.onResume();
		changeBG();
	}
	
	@Override
	protected void onDestroy() {
		LoginInfo.getInstance().setUserLevel(String.valueOf(TradeUser.getInstance().getUserLevel()));
		super.onDestroy();
	}

	class AccountListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int mResource;
		private String[] account;
		
		public AccountListAdapter(Context context, int paramInt, String[] accounts) {
			this.mInflater = LayoutInflater.from(context);
			this.mResource = paramInt;
			this.account = accounts;
		}
		
		public int getCount() {
			return account.length;
		}
		
		public Object getItem(int position) {
			return Integer.valueOf(position);
		}
		
		public long getItemId(int position) {
			return position;
		}
		
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
	        if (convertView == null) {
	            convertView = mInflater.inflate(mResource, null);
	            holder = new ViewHolder();
	            holder.textView = (TextView)convertView.findViewById(R.id.TextView01);
				holder.imageView = (ImageView)convertView.findViewById(R.id.ImageView01);
				holder.imageView.setOnClickListener(new ImageView.OnClickListener() {
					public void onClick(View v) {
						deleteAccount(position);
						if(mPop!=null && mPop.isShowing())
							mPop.dismiss();
					}
				});
				
				convertView.setTag(holder);
	        } else {
	            holder = (ViewHolder)convertView.getTag();
	        }
	        holder.textView.setText(account[position]);
	        
	        return convertView;
		}
		
		/**
	     * 用于存储每行的视图
	     * @author Administrator
	     *
	     */
	    class ViewHolder{
	    	TextView textView;
			ImageView imageView;
		}
	    
	    /**
	     * 删除账号处理
	     */
	    private void deleteAccount(final int position) {
	    	new AlertDialog.Builder(LoginActivity.this)
			.setTitle("删除账号提示")
			.setMessage("您确定从列表中删除该账户：\n\t\t\t\t\t" + account[position])
			.setPositiveButton(R.string.alert_dialog_ok,
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							ArrayList<String> newAccounts = new ArrayList<String>();
							StringBuffer sb = new StringBuffer();
							for(int i=0, size=myFundInfosService.length; i<size; i++){
								if(i == position)
									continue;
								newAccounts.add(myFundInfosService[i]);
								sb.append(myFundInfosService[i] + ",");
							}
							ActivityUtil.saveCustNoPreference(LoginActivity.this, "myCustNos", sb.toString());
							String preferredFund = StockPreference.getPreferredCustNo(LoginActivity.this);
							if(preferredFund.equals(myFundInfosService[position])){
								StockPreference.setPreferredCustNo(LoginActivity.this, "");	 //删除登录首选账号
							}
							myFundInfosService = newAccounts.toArray(new String[newAccounts.size()]);
							setFunds(-1);
							toast("成功删除！");
						}
					})
	        .setNegativeButton("取消",  new DialogInterface.OnClickListener() 
	                {
	                    public void onClick(DialogInterface dialog, int whichButton)
	                    {
	                    	
	                    }
	                })
			.show(); 
		}
	}
	protected void openDialog(int msg) {
//    	if(a.hasWindowFocus()) {
	    	new AlertDialog.Builder(LoginActivity.this)
	        .setIcon(android.R.drawable.ic_dialog_alert)
	        .setTitle(R.string.alert_dialog_about)
			.setMessage(getApplicationContext().getResources().getText(msg))
	        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int whichButton) {
	            	Intent mIntent = new Intent("/");
					ComponentName comp = new ComponentName("com.android.settings",
							"com.android.settings.WirelessSettings");
					mIntent.setComponent(comp);
					mIntent.setAction("android.intent.action.VIEW");
					startActivityForResult(mIntent, 0);
					System.exit(0);
	            }
	        })
	        .show();
//    	}
    }
	private  void delSameId() {
		ArrayList<String> newAccounts = new ArrayList<String>();
		StringBuffer sb = new StringBuffer();
		String myFundIdsStr = StockPreference.getCustNo(this);
		if (myFundIdsStr != null){
			for(int i=0, size=myFundInfosService.length; i<size; i++){
				if(myFundInfosService[i].indexOf(loginFundid)!=-1)
					continue;
				newAccounts.add(myFundInfosService[i]);
				sb.append(myFundInfosService[i] + ",");
			}
			ActivityUtil.saveCustNoPreference(LoginActivity.this, "myCustNos", sb.toString());
			String preferredFund = StockPreference.getPreferredCustNo(LoginActivity.this);
			if(preferredFund.equals(loginFundid)){
				StockPreference.setPreferredCustNo(LoginActivity.this, null);
			}
			myFundInfosService = newAccounts.toArray(new String[newAccounts.size()]);
		}
	}
}
