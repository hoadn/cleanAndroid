package com.cssweb.android.trade.stock;

import java.io.UnsupportedEncodingException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;

/**
 * 修改资料
 * @author FredZhong
 * 2011/3/17
 */
public class ModifyContactInfo extends CssKeyboardBase {
	private final String DEBUG_TAG = "ModifyContactInfo";
	
	private EditText mobile;
	private EditText phone;
	private EditText post;
	private EditText address;
	private EditText email;
	
	private Spinner education;
	private Spinner job;
	
	private ArrayAdapter<String> jobAdapter;
	private String[] jobLabels;
	private String[] jobVal;
	
	private ArrayAdapter<String> educationAdapter;
	private String[] educationLabels;
	private String[] educationVal;
	private Thread thread = null;
	
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setContentView(R.layout.zr_modifycontactinfo);
		initTitle(R.drawable.njzq_title_left_back, 0, "资料修改" );
		String[] toolbarname = new String[]{ 
				Global.TOOLBAR_UPDATE,"","","","", Global.TOOLBAR_CHAXUN };
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
//
//		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.zryzzzlayout);
//		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
//		this.m_vklayout = localLinearLayout;
//		View localView1 = this.m_vklayout;
//		localView1.setOnFocusChangeListener(setOnEditFocusListener);
//		View localView2 = this.m_vklayout;
//		localView2.setOnClickListener(setOnEditClickListener);

	    //currency = (CustomSpinner) findViewById(R.id.Currency);
	    //bank = (CustomSpinner) findViewById(R.id.Bank);
	    mobile = (EditText) findViewById(R.id.Mobile);
	    phone = (EditText)findViewById(R.id.Phone);
	    post = (EditText)findViewById(R.id.Post);
	    email = (EditText)findViewById(R.id.Email);
	    address = (EditText)findViewById(R.id.Address);
	    education = (Spinner)findViewById(R.id.Education);
	    job = (Spinner)findViewById(R.id.Job);
	    
	    
	    jobLabels = getResources().getStringArray(R.array.mci_job);
	    jobVal = getResources().getStringArray(R.array.mci_job_val);
		
	    jobAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, jobLabels);
		jobAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item);
		job.setAdapter(jobAdapter);
		
		
		educationLabels = getResources().getStringArray(R.array.mci_education);
		educationVal = getResources().getStringArray(R.array.mci_education_val);
		
		educationAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, educationLabels);
		educationAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item);
		education.setAdapter(educationAdapter);
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
//		Log.e("<<<<<<<<<<<<<<<<<<<eeeeeeeeeeeeeeeeeeeeeeeeeee>>>>>>>>>>>>>>", String.valueOf(spinnerheight));
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) education.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		education.setLayoutParams(linearParams1);
		
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) job.getLayoutParams();//获取queryMethod控件的当前布局
		linearParams.height=spinnerheight;//对该控件的布局参数做修改
		job.setLayoutParams(linearParams);
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
					quoteData = ConnPool.sendReq("GET_ContactInfo", "302001", "FID_EXFLG=1");
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
				}
				else {
					toast(res);
				}
				hiddenProgressToolBar();
				return;
			}
			JSONArray jsonArr = quoteData.getJSONArray("item");
			quoteData = (JSONObject)jsonArr.get(0);
			
			mobile.setText(quoteData.getString("FID_MOBILE"));
			phone.setText(quoteData.getString("FID_DH"));
			post.setText(quoteData.getString("FID_YZBM"));
			email.setText(quoteData.getString("FID_EMAIL"));
			address.setText(quoteData.getString("FID_DZ"));
			int ijob = Integer.parseInt(quoteData.getString("FID_ZYDM"));
			int ieducation = Integer.parseInt(quoteData.getString("FID_XLDM"));
			if(ijob ==99){
				job.setSelection(8);
			}else{
				job.setSelection(ijob-1);
			}
			education.setSelection(ieducation-1);
		} catch (JSONException e) {
			e.printStackTrace();
			CssLog.e(DEBUG_TAG, e.toString());
		}
		hiddenProgressToolBar();
	}
	
	private void submit() {
		String txtmoblie = mobile.getText().toString().trim();
		if("".equals(txtmoblie)) {
			toast("请输入手机号码!");
			return;
		}else if(txtmoblie.length()!=11){
			toast("请输入正确的手机号码!");
			return;
		}
		
		String txtphone = phone.getText().toString().trim();
		if("".equals(txtphone)) {
			toast("请输入联系电话号码!");
			return;
		}else if(!TradeUtil.checkPhone(txtphone)){
			toast("请输入正确的联系电话号码!");
			return;
		}
		String txtpost = post.getText().toString().trim();
		if("".equals(txtpost)) {
			toast("请输入邮政编码!");
			return;
		}else if(txtpost.length()!=6){
			toast("请输入正确的邮政编码!");
			return;
		}
		
		String emailtxt = email.getText().toString().trim();
		if(!TradeUtil.checkEmail(emailtxt)){
			toast("请输入正确的电子邮件!");
			return;
		}
		if(emailtxt.length()>50){
			toast("电子邮件不能超过50位!");
			return;
		}
		String addresstxt = address.getText().toString().trim();
		
		try {
			Log.i("addresstxt.length()=",new String(addresstxt.getBytes("GBK"), "ISO8859_1").length()+"");
			if(new String(addresstxt.getBytes("GBK"), "ISO8859_1").length()>80){
				toast("通迅地址不能超过80位!");
				return;
			}
		} catch (UnsupportedEncodingException e1) {
			e1.printStackTrace();
		}
		StringBuffer tipMsg = new StringBuffer();
		
		tipMsg.append("您确认要修改自己的个人资料吗?\n");
		new AlertDialog.Builder(ModifyContactInfo.this)
		.setTitle("系统提示")
		.setMessage(tipMsg.toString())
		.setPositiveButton("确认", 
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CharSequence title = "正在与服务器通讯握手...";
					CharSequence message = "正在往服务器提交数据...";
					ProgressDialog myDialog = ProgressDialog.show(ModifyContactInfo.this, title, message, true);
					StringBuffer sb = new StringBuffer();
					
					sb.append("FID_ZLLB=99"+ TradeUtil.SPLIT);
					sb.append("FID_DH=" + phone.getText().toString().trim() + TradeUtil.SPLIT);
					sb.append("FID_MOBILE=" +mobile.getText().toString().trim()+ TradeUtil.SPLIT);
					sb.append("FID_YZBM=" + post.getText().toString().trim() + TradeUtil.SPLIT);
					sb.append("FID_EMAIL=" + email.getText().toString().trim() +TradeUtil.SPLIT);
					sb.append("FID_DZ=" + address.getText().toString().trim() + TradeUtil.SPLIT);
					sb.append("FID_XLDM=" + educationVal[education.getSelectedItemPosition()] + TradeUtil.SPLIT);
					sb.append("FID_ZYDM=" + jobVal[job.getSelectedItemPosition()]);
//					sb.append("FID_ZY=金罗盘Android客户端，客户信息修改");
					try {

						JSONObject quoteData = ConnPool.sendReq("ModifyContactInfo", "202002", sb.toString());
				    	String result = TradeUtil.checkResult(quoteData);
				    	if (result != null) {
							if (result.equals("-1"))
								Toast.makeText(ModifyContactInfo.this, "当前网络不可用！", Toast.LENGTH_LONG).show();
							else
								toast(result);
							myDialog.dismiss();
							return;
						}else{
							JSONArray jarr = (JSONArray) quoteData.getJSONArray("item");
							String msg = jarr.getJSONObject(0).getString("FID_MESSAGE");
							msg = msg.substring(0,msg.indexOf("<"));
							toast(msg);
						}
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
	@Override
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			submit();
			break;
		case 5: 
			setToolBar();
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
