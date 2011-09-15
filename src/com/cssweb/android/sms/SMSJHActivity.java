package com.cssweb.android.sms;

import java.util.ArrayList;

import org.json.JSONException;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.service.ValidationService;
import com.cssweb.android.service.ZixunService;
import com.cssweb.android.trade.login.LoginActivity;
import com.cssweb.quote.util.Utils;

public class SMSJHActivity extends GridViewActivity {
	private Button sendJHCodeButton;
	private Button activityJHCodeButton;

	private EditText mobileNumEdit;
	private EditText jhCodeEdit;

	private String mobileNum;
	private String jhCode;

	private String imei;
	private boolean exitFlag = true;
	private int paramId = 1;
	private boolean isChangeBtn = false;
	
	private String url = "";

	private ProgressDialog progressDialog;
	private ProgressDialog progressDialog1;
	
	AlertDialog.Builder recemsgsuccess ;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				sendJHCodeButton.setEnabled(true);
				activityJHCodeButton.setEnabled(true);
				
				progressDialog1.dismiss();
				
				alertDialog("接受短信失败", "请重新发送手机号码" , true);
				
				break;
			case 2:
				Intent loginUIActivity = new Intent(SMSJHActivity.this,
						LoginActivity.class);
				Bundle bundle = new Bundle();
				if(url != null && !"".equals(url)){
					bundle.putString("url", url);
					loginUIActivity.putExtras(bundle);
					SMSJHActivity.this.startActivity(loginUIActivity);
				} else {
					bundle.putInt("menu_id", paramId);
					bundle.putBoolean("isChangeBtn", isChangeBtn);
					loginUIActivity.putExtras(bundle);
					SMSJHActivity.this
							.startActivity(loginUIActivity);
				}
				progressDialog.dismiss();
				SMSJHActivity.this.finish();
				break;//15201713385
			case 3:
				progressDialog.dismiss();
				
				Bundle data = msg.getData();
				String message = data.getString("msg");
				
				alertDialog("激活失败", message , true);
				break;
			case 4:
				SharedPreferences sharedPreferences = getSharedPreferences(
						"mobile", Context.MODE_PRIVATE);
				Editor editor = sharedPreferences.edit();// 获取编辑器
				editor.putString("mobileNum", mobileNum);
				editor.putBoolean("exitFlag", false);
				editor.commit();// 提交修改
				
				progressDialog1.dismiss();
				
				alertDialog("手机号码已验证成功", "请输入收到的激活码，激活手机" , true);
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.njzq_smsjh);
		recemsgsuccess = new AlertDialog.Builder(
				SMSJHActivity.this);
		
		Bundle bundle = getIntent().getExtras();
		this.url = bundle.getString("url");
		paramId = getIntent().getIntExtra("menu_id", 1);
		
		isChangeBtn = getIntent().getBooleanExtra("isChangeBtn", false);

		sendJHCodeButton = (Button) findViewById(R.id.sendJHCode);
		activityJHCodeButton = (Button) findViewById(R.id.activityJHCode);
		mobileNumEdit = (EditText) findViewById(R.id.mobileNum);
		jhCodeEdit = (EditText) findViewById(R.id.jhCode);

		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();

		sendJHCodeButton.setOnClickListener(listener);
		activityJHCodeButton.setOnClickListener(listener);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences sharedPreferences = getSharedPreferences("mobile",
				Context.MODE_PRIVATE);
		mobileNum = sharedPreferences.getString("mobileNum", "");

		if (!"".equalsIgnoreCase(mobileNum)) {
			mobileNumEdit.setText(mobileNum);

			exitFlag = sharedPreferences.getBoolean("exitFlag", true);
			if (!exitFlag) {
				sendJHCodeButton.setEnabled(false);
			}
		}else {
//			activityJHCodeButton.setEnabled(false);
		}
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.sendJHCode:
				mobileNum = mobileNumEdit.getText().toString();
				if (mobileNum != null && !"".equalsIgnoreCase(mobileNum)) {
					
				  //if(!Utils.isNumber(mobileNum) || (mobileNum.length() != 11 && mobileNum.length() != 12)){
					if( (!Utils.isNumber(mobileNum) ) || ( (mobileNum.length() != 11) ||  ( (!mobileNum.startsWith("13")) && (!mobileNum.startsWith("15")) && (!mobileNum.startsWith("18"))  )   ) ){
						Toast.makeText(SMSJHActivity.this, "手机号码格式不正确，请重新输入", Toast.LENGTH_SHORT).show();
						return;
					}
					
					((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(SMSJHActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
					// 显示ProgressDialog
					progressDialog1 = ProgressDialog.show(SMSJHActivity.this,
							"手机号码已发送..", "正在接受激活码中，请稍等...", true, true);

					new Thread(new Runnable() {
						public void run() {
							String result;
							try {
								result = ZixunService.mobileMsgValidate(
										mobileNum, imei, ValidationService
												.getSMSServiceTime());
								
								Log.i("tag", "result : "+result);
								
								if (result != null && "success".equals(result)) {
									SharedPreferences sharedPreferences = getSharedPreferences(
											"mobile", Context.MODE_PRIVATE);
									Editor editor = sharedPreferences.edit();// 获取编辑器
									editor.putString("mobileNum",mobileNum);
									editor.commit();// 提交修改
									sendMsg(4);
								} else {
									sendMsg(1);
								}
							} catch (JSONException e) {
								e.printStackTrace();
								sendMsg(1);
							}
						}
					}).start();
					sendJHCodeButton.setEnabled(false);
				} else {
					Log.i("tag", "mobileNum is null ");
				}
				break;
			case R.id.activityJHCode:
				jhCode = jhCodeEdit.getText().toString();
				if (jhCode != null && !"".equalsIgnoreCase(jhCode)) {
					// 显示ProgressDialog
					progressDialog = ProgressDialog.show(SMSJHActivity.this,
							"激活...", "正在激活中，请稍等...", true, false);
					// 新建线程
					new Thread() {
						@Override
						public void run() {
							try {
								SharedPreferences sharedPreferences = getSharedPreferences(
										"mobile", Context.MODE_PRIVATE);
								mobileNum = sharedPreferences.getString(
										"mobileNum", "");
								ArrayList<String> result = ZixunService.validateCode(
										mobileNum, imei, ValidationService
												.getSMSServiceTime(), jhCode);
								if (result != null) {
									if("success".equals(result.get(0))){
										Editor editor = sharedPreferences.edit();// 获取编辑器
										editor.putBoolean("jhSuccess", true);
										editor.commit();// 提交修改
										sendMsg(2);
									} else{
										sendMsgWithParam(3,result.get(1));
									}
								}else {
									sendMsg(3);	
								}
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								sendMsg(3);
							}
						}
					}.start();
				} else {
					Toast.makeText(SMSJHActivity.this, "激活码为空，请输入激活码.", Toast.LENGTH_SHORT).show();
					
					Log.i("tag", "jhCode is null ");
				}
				break;
			default:
				break;
			}
		}
	};

	private void sendMsg(int what){
		Message msg = new Message();
		msg.what = what;// 
		mHandler.sendMessage(msg);
	}
	private void sendMsgWithParam(int what, String message) {
		Message msg = new Message();
		msg.what = what;// 
		Bundle data = new Bundle();
		data.putString("msg", message);
		msg.setData(data);
		mHandler.sendMessage(msg);
		
	}
	
	private void alertDialog(String title, String msg ,final boolean activityJHCodeFlag ){
			try {
				recemsgsuccess.setTitle(title);
				recemsgsuccess.setMessage(msg);
				recemsgsuccess.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								activityJHCodeButton.setEnabled(activityJHCodeFlag);
							}
						});
				recemsgsuccess.setCancelable(false);
				recemsgsuccess.create();
				recemsgsuccess.show();
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
}
