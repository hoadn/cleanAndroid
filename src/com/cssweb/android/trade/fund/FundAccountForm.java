package com.cssweb.android.trade.fund;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;

import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.stock.TradeQueryBase;
import com.cssweb.android.trade.util.TradeUtil;

public class FundAccountForm extends TradeQueryBase {
	private static final String DEBUG_TAG = "FundAccountForm";
	
	private String tacode;
	
	private Spinner spinner;
	private EditText fundTacode;
	private EditText fundTaacc;
	private EditText fundPostId;
	private EditText fundTel;
	private EditText fundMobile;
	private EditText fundEmail;
	private EditText fundAddr;
	
	String [] arrayOfString = null;
	
	private Thread thread = null;
	
	private ArrayAdapter<String> spinnerAdapter;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_trade_fundaccountform);
		initTitle(R.drawable.njzq_title_left_back, 0, "基金开户");
		//初始化工具栏
		String[] toolbarname = new String[]{Global.TOOLBAR_QUEDING,"","","","", Global.TOOLBAR_BACK};
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		fundTacode = (EditText) findViewById(R.id.accountform_fundtacode);
		fundTaacc = (EditText) findViewById(R.id.accountform_fundtaacc);
		fundPostId = (EditText) findViewById(R.id.accountform_fundPostId);
		fundTel = (EditText) findViewById(R.id.accountform_fundTel);
		fundMobile = (EditText) findViewById(R.id.accountform_fundMobile);
		fundEmail = (EditText) findViewById(R.id.accountform_fundEmail);
		fundAddr = (EditText) findViewById(R.id.accountform_fundAddr);
		Bundle bundle = getIntent().getExtras();
		tacode = bundle.getString("tacode");
		fundTacode.setText(bundle.getString("taname"));
		
		arrayOfString = getResources().getStringArray(R.array.fundopenaccount_colsname);
		spinner = (Spinner) findViewById(R.id.accountform_fundAccType);
		spinnerAdapter = new ArrayAdapter<String>(FundAccountForm.this , android.R.layout.simple_spinner_item ,arrayOfString );
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener (){
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				if(position == 0){
					fundTaacc.setText("无需输入");
					fundTaacc.setTextColor(R.color.zr_dimgray);
					fundTaacc.setGravity(Gravity.CENTER);
					fundTaacc.setFocusable(false);
					fundTaacc.setCursorVisible(false);
					fundTaacc.setFocusableInTouchMode(false);
				}else {
					fundTaacc.setText("");
					fundTaacc.setTextColor(getResources().getColor(R.color.zr_black));
					fundTaacc.setGravity(Gravity.LEFT|Gravity.CENTER);
					fundTaacc.setFocusable(true);
					fundTaacc.setCursorVisible(true);
					fundTaacc.setFocusableInTouchMode(true);
				}
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) spinner.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		spinner.setLayoutParams(linearParams1);
		setToolBar();
	}

	@Override
	protected void init(int type) {
		r = new Runnable() {
			public void run() {
				try {
					quoteData = FundService.getCustInfo();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	
	/**
	 * 接收消息,更新视图
	 */
	@Override
	protected void handlerData() {
		JSONObject jsonObject = quoteData;
		try {
			String res = TradeUtil.checkResult(jsonObject);
			if (res != null) {
				if (res.equals("-1"))
					toast("网络连接失败！");
				else
					toast(res);
				hiddenProgressToolBar();
				return;
			} else {
				JSONArray jarr = (JSONArray) jsonObject.getJSONArray("item");
				JSONObject  jsonobj = jarr.getJSONObject(0);
				fundEmail.setText(jsonobj.getString("FID_EMAIL"));
				fundAddr.setText(jsonobj.getString("FID_DZ"));
				fundPostId.setText(jsonobj.getString("FID_YZBM"));
				fundTel.setText(jsonobj.getString("FID_DH"));
				fundMobile.setText(jsonobj.getString("FID_MOBILE"));
			}
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
			showDialog(0);
			hiddenProgressToolBar();
		}
		hiddenProgressToolBar();
	}
	
	/**
	 * 提交事件
	 */
	private void submit() {
		String postId = fundPostId.getText().toString();
		if(postId == null || postId.equals("")){
			toast("邮政编码不能为空");
			return ;
		}else if(postId.length()!=6){
			toast("请输入正确的邮政编码!");
			return;
		}
		
		String txtmoblie = fundMobile.getText().toString().trim();
		if("".equals(txtmoblie)) {
			toast("请输入手机号码!");
			return;
		}else if(txtmoblie.length()!=11){
			toast("请输入正确的手机号码!");
			return;
		}
		
		String tel = fundTel.getText().toString();
		if(tel == null || tel.equals("")){
			toast("电话号码不能为空");
			return ;
		}else if(!TradeUtil.checkPhone(tel)){
			toast("请输入正确的电话号码!");
			return;
		}
		
		String emailtxt = fundEmail.getText().toString().trim();
		if(!TradeUtil.checkEmail(emailtxt)){
			toast("请输入正确的电子邮件!");
			return;
		}
		if(emailtxt.length()>50){
			toast("电子邮件不能超过50位!");
			return;
		}
		
		String addr = fundAddr.getText().toString();
		if(addr == null || addr.equals("")){
			toast("联系地址不能为空");
			return ;
		}
		try {
			Log.i("addresstxt.length()=",new String(addr.getBytes("GBK"), "ISO8859_1").length()+"");
			if(new String(addr.getBytes("GBK"), "ISO8859_1").length()>80){
				toast("通迅地址不能超过80位!");
				return;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		String taacc = "";
		if(!"无需输入".equals(fundTaacc.getText().toString().trim()))
			taacc = fundTaacc.getText().toString().trim();
		CharSequence title = "正在与服务器通讯握手...";
		CharSequence message = "正在往服务器提交数据...";
		ProgressDialog myDialog = ProgressDialog.show(FundAccountForm.this, title, message ,true);
		try{
			JSONObject resultJson = FundService.newFundAccount(tel, fundMobile.getText().toString().trim(), postId, addr, fundEmail.getText().toString().trim(), tacode, taacc);
			String result = TradeUtil.checkResult(resultJson);
			if (result != null) {
				if (result.equals("-1"))
					Toast.makeText(FundAccountForm.this, "当前网络不可用！", Toast.LENGTH_LONG).show();
				else
					toast(result);
				myDialog.dismiss();
				return;
			}else{
				JSONArray jarr = (JSONArray) resultJson.getJSONArray("item");
				toast(jarr.getJSONObject(0).getString("FID_MESSAGE")+",委托编号："+jarr.getJSONObject(0).getString("FID_WTH"));
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		myDialog.dismiss();
	}
	/**
	 * 返回事件
	 */
	private void cancel() {
		finish();
	}
	
	protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
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
		//initView("", "", "");
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
