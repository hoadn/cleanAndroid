package com.cssweb.android.trade.stock;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.AES;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;

/**
 * 修改密码
 * 包括交易密码和资金密码
 * 
 * @author wangsheng
 *
 */
public class ModifyPassword extends CssKeyboardBase {
	private static final String DEBUG_TAG = "ModifyPassword";

	//private TextView title;
	//private CustomSpinner passwordType;
	private TextView lblCurrentPassword;
	private EditText txtCurrentPassword;
	private TextView lblNewPassword;
	private EditText txtNewPassword;
	private TextView lblConfirmPassword;
	private EditText txtConfirmPassword;
	
	private ArrayAdapter<String> adapter;
	private int type = 0;//默认修改交易密码
	private String title = "交易密码";
	
	private TextView jymmTextView;
	private TextView zjmmTextView;
	private LinearLayout layoutupdate_bg;
	
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setContentView(R.layout.zr_trade_modify_password);
		initTitle(R.drawable.njzq_title_left_back, 0, "修改密码" );
		String[] toolbarNames = {Global.TOOLBAR_QUEDING,"","","","",Global.TOOLBAR_QUXIAO};
		initToolBar(toolbarNames, Global.BAR_TAG);
		setBtnStatus();
		
		initPopupWindow();
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		//title = (TextView)findViewById(R.id.zr_trade_modify_password_label);
		//passwordType = (CustomSpinner)findViewById(R.id.PasswordType);
		lblCurrentPassword = (TextView)findViewById(R.id.label_current_password);
		txtCurrentPassword = (EditText)findViewById(R.id.txt_current_password);
		lblNewPassword = (TextView)findViewById(R.id.label_new_password);
		txtNewPassword = (EditText)findViewById(R.id.txt_new_password);
		lblConfirmPassword = (TextView)findViewById(R.id.label_confirm_password);
		txtConfirmPassword = (EditText)findViewById(R.id.txt_confirm_password);
		jymmTextView = (TextView) findViewById(R.id.jymm);
		zjmmTextView = (TextView) findViewById(R.id.zjmm);
		layoutupdate_bg = (LinearLayout) findViewById(R.id.update_bg);
		jymmTextView.setOnClickListener(listener);
		zjmmTextView.setOnClickListener(listener);
		
		
		txtCurrentPassword.setInputType(InputType.TYPE_NULL);
		txtCurrentPassword.setTag("NUMDOT");
		txtCurrentPassword.setOnClickListener(setOnEditClickListener);
		txtCurrentPassword.setOnFocusChangeListener(setOnEditFocusListener);
		ActivityUtil.setPasswordType(txtCurrentPassword, true);
		
		txtNewPassword.setInputType(InputType.TYPE_NULL);
		txtNewPassword.setTag("NUMDOT");
		txtNewPassword.setOnClickListener(setOnEditClickListener);
		txtNewPassword.setOnFocusChangeListener(setOnEditFocusListener);
		ActivityUtil.setPasswordType(txtNewPassword, true);
		
		txtConfirmPassword.setInputType(InputType.TYPE_NULL);
		txtConfirmPassword.setTag("NUMDOT");
		txtConfirmPassword.setOnClickListener(setOnEditClickListener);
		txtConfirmPassword.setOnFocusChangeListener(setOnEditFocusListener);
		ActivityUtil.setPasswordType(txtConfirmPassword, true);
		
		/*passwordType.setOnItemSelectedListener(new OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				setLabels();
			}

			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
		});*/
		
		String[] types = getResources().getStringArray(R.array.zr_trade_modify_password_spinner);
		adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, types);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		//passwordType.setAdapter(adapter);
		
		setLabels();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }

    
    private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {
			if (v.getId() == R.id.jymm){
				type = 0;
				title = "交易密码";
				lblCurrentPassword.setText("当前交易密码");
				lblNewPassword.setText("新的交易密码");
				lblConfirmPassword.setText("确认交易密码");
				layoutupdate_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.a4));
			}else if (v.getId() == R.id.zjmm){
				type = 1;
				title = "资金密码";
				lblCurrentPassword.setText("当前资金密码");
				lblNewPassword.setText("新的资金密码");
				lblConfirmPassword.setText("确认资金密码");
				layoutupdate_bg.setBackgroundDrawable(getResources().getDrawable(R.drawable.a3));
			}
		}
    };
    
	private void setLabels() {
		//int position = passwordType.getSelectedItemPosition();
		//if(position == 0){
		//	this.type = position;
		//	title.setText("交易密码");
			lblCurrentPassword.setText("当前交易密码");
			lblNewPassword.setText("新的交易密码");
			lblConfirmPassword.setText("确认交易密码");
		/*}else if(position == 1){
			this.type = position;
			title.setText("资金密码");
			lblCurrentPassword.setText("当前资金密码");
			lblNewPassword.setText("新的资金密码");
			lblConfirmPassword.setText("确认资金密码");
		}*/
	}
	
	private void cancel() {
		finish();
	}
	
	private void submit() {
		String strCurrentPassword = txtCurrentPassword.getText().toString();
		String strNewPassword = txtNewPassword.getText().toString();
		String strConfirmPassword = txtConfirmPassword.getText().toString();
		if(strCurrentPassword.equals("")){
			toast(lblCurrentPassword.getText() + "不能为空");
			return;
		}
		if(strNewPassword.equals("")){
			toast(lblNewPassword.getText() + "不能为空");
			return;
		}
		if(strConfirmPassword.equals("")){
			toast(lblConfirmPassword.getText() + "不能为空");
			return;
		}
		if(strNewPassword.length()<6 || strConfirmPassword.length()<6){
			toast(lblNewPassword.getText() + "至少6位数字，请重新输入");
			cleanPassword();
			return;
		}
		if(!strNewPassword.equals(strConfirmPassword)){
			toast(lblNewPassword.getText() + "与" + lblConfirmPassword.getText() + "不一致，请重新输入");
			cleanPassword();
			return;
		}
		new AlertDialog.Builder(ModifyPassword.this)
		.setTitle("修改"+title+"提示")
		//.setMessage("确认修改" + title.getText() + "?").setPositiveButton("确定",
		.setMessage("确认修改"  +title+ "?").setPositiveButton("确定",
		    new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int which) {
					CharSequence title = "正在与服务器通讯握手...";
					CharSequence message = "正在往服务器提交数据...";

					ProgressDialog myDialog = ProgressDialog.show(ModifyPassword.this, title, message, true);
					try {
						JSONObject quoteData = null;
						String oldpwd = txtCurrentPassword.getText().toString().trim();
						String newpwd = txtNewPassword.getText().toString().trim();
						try {
							oldpwd = AES.encrypt(oldpwd,TradeUtil.g_pubKey);
							newpwd = AES.encrypt(newpwd,TradeUtil.g_pubKey);
						} catch (Exception e1) {
							e1.printStackTrace();
						}
						if(type == 0){
							quoteData = TradeService.modifyTradePass(oldpwd, newpwd);
						}else if(type == 1){
							quoteData = TradeService.modifyFundPass(oldpwd, newpwd);
						}
						//Log.i(">>>>>>", quoteData.toString());
						String res = TradeUtil.checkResult(quoteData);
						if (res != null) {
							cleanPassword();
							myDialog.dismiss();
							if (res.equals("-1")){
								Toast.makeText(ModifyPassword.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								//openDialog(R.string.network_error);
							}else {
								toast("修改" + title + "失败：" + res);
							}
						} else {
							cleanPassword();
							myDialog.dismiss();
							JSONArray jArr = (JSONArray) quoteData.getJSONArray("item");
							JSONObject j = (JSONObject) jArr.get(0);
							toast(j.getString("FID_MESSAGE"));
						}
					} catch (JSONException e) {
						CssLog.e(DEBUG_TAG, e.toString());
					}
				}
			}
		)
		.setNegativeButton("取消",
			new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					//
			    }
			}
	    ).show();
	}

	private void cleanPassword() {
		txtNewPassword.setText("");
		txtConfirmPassword.setText("");
		txtNewPassword.requestFocus();
	}
	protected void toolBarClick(int tag, View v) {
		switch (tag) {
		case 0:
			submit();
			break;
		case 5:
			cancel();
			break;
		}
	}
	private void setBtnStatus(){
		setToolBar(1, false, R.color.zr_dimgray);
		setToolBar(2, false, R.color.zr_dimgray);
		setToolBar(3, false, R.color.zr_dimgray);
		setToolBar(4, false, R.color.zr_dimgray);
	}
}
