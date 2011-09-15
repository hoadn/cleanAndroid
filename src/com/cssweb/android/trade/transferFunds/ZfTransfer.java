package com.cssweb.android.trade.transferFunds;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.AES;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
/**
 * 主辅转账/辅主转账
 * @author chengfei
 *
 */
public class ZfTransfer extends CssKeyboardBase{
	private Spinner convertOutSpinner;
	private Spinner convertInSpinner;
	private Spinner currency;
	private EditText convertPwd;
	private EditText convertAmount;
	private ArrayAdapter<String> convertOutSpinnerAdapter;
	private ArrayAdapter<String> convertInSpinnerAdapter;
	private ArrayAdapter<String> currencyAdapter;
	private String[] currencyLabels;
	private String[] currencyVal;
	private List<String> zAccount = new ArrayList<String>();
	private List<String> fAccounts = new ArrayList<String>();
	private List<String> outAccount = new ArrayList<String>();
	private List<String> inAccount = new ArrayList<String>();
	String outId = "";
	String inId = "";
	private int type;
	private String titleName = "";
	private Thread thread = null;
	private TextView convertoutaccounttext ;
	private ServiceControl sc = null;//行为分析
	@Override
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		setContentView(R.layout.zr_trade_fundstransfer);
		sc = new ServiceControl(this);//行为分析初始化
		Bundle bundle = getIntent().getExtras();
		type = bundle.getInt("type");
		if (type == 1) {
			titleName = "主辅转账";
			sc.currentSite("wtjy_yzzz_zjdb_zfzz");
		}else if (type == 2) {
			titleName = "辅主转账";
			sc.currentSite("wtjy_yzzz_zjdb_fzzz");
		}
		initTitle(R.drawable.njzq_title_left_back, 0, titleName);
		//初始化工具栏
		String[] toolbarname = new String[]{Global.TOOLBAR_QUEDING, "","","","", Global.TOOLBAR_BACK};
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.fundstransferLinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		convertOutSpinner = (Spinner) findViewById(R.id.convertoutaccountid);
		convertInSpinner =(Spinner) findViewById(R.id.convertinaccountid);
		currency = (Spinner) findViewById(R.id.Currency);
		convertPwd = (EditText) findViewById(R.id.convertpwd);
		convertAmount = (EditText) findViewById(R.id.convertamount);
		convertoutaccounttext = (TextView) findViewById(R.id.convertoutaccounttext);
		convertPwd.setInputType(InputType.TYPE_NULL);
		convertPwd.setFocusable(true);
		convertPwd.setTag("");
		convertPwd.setOnClickListener(setOnEditClickListener);
		convertPwd.setOnFocusChangeListener(setOnEditFocusListener);
		
		convertAmount.setInputType(InputType.TYPE_NULL);
		convertAmount.setFocusable(true);
		convertAmount.setTag("");
		convertAmount.setOnClickListener(setOnEditClickListener);
		convertAmount.setOnFocusChangeListener(setOnEditFocusListener);
		
		currencyLabels = new String[1];//getResources().getStringArray(R.array.bank_transfer2_moneytype);
		currencyLabels[0]="人民币";
		currencyVal = new String[1];//getResources().getStringArray(R.array.bank_transfer2_moneytype_val)
		currencyVal[0]="RMB";
		
		currencyAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, currencyLabels);
		currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		currency.setAdapter(currencyAdapter);
		
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) convertOutSpinner.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		convertOutSpinner.setLayoutParams(linearParams1);
		
		LinearLayout.LayoutParams linearParams = (LinearLayout.LayoutParams) convertInSpinner.getLayoutParams();//获取queryMethod控件的当前布局
		linearParams.height=spinnerheight;//对该控件的布局参数做修改
		convertInSpinner.setLayoutParams(linearParams);
		
		LinearLayout.LayoutParams linearParams2 = (LinearLayout.LayoutParams) currency.getLayoutParams();//获取queryMethod控件的当前布局
		linearParams2.height=spinnerheight;//对该控件的布局参数做修改
		convertInSpinner.setLayoutParams(linearParams2);
		
		convertoutaccounttext.setFocusable(true);
		convertoutaccounttext.setFocusableInTouchMode(true);
		setToolBar();
		
	}
	protected void init(int type) {
		r = new Runnable() {
			public void run() {
				try {
					StringBuffer sb = new StringBuffer();
					sb.append("FID_EXFLG=1&");
					sb.append("FID_BZ=RMB");
					quoteData = ConnPool.sendReq("GET_FUNDLIST","303002",sb.toString());
					String res = TradeUtil.checkResult(quoteData);
					if(res==null){
						JSONArray jsonArr = (JSONArray)quoteData.getJSONArray("item");
						for(int i=0,size=jsonArr.length()-1; i<size; i++){
							JSONObject jsonObj = (JSONObject)jsonArr.get(i);
							String zjzh = jsonObj.getString("FID_ZJZH");
							if ("1".equals(jsonObj.getString("FID_ZZHBZ"))) {
								zAccount.add(zjzh);
							}else {
								fAccounts.add(zjzh);
							}
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
	/**
	 * 接收消息,更新视图
	 */
	@Override
	protected void handlerData() {
		try{
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
			if (type == 1) {
				outAccount = zAccount;
				inAccount = fAccounts;
			}else if(type == 2){
				outAccount = fAccounts;
				inAccount = zAccount;
			}	
			convertOutSpinnerAdapter = new ArrayAdapter<String>(ZfTransfer.this , android.R.layout.simple_spinner_item,(String [])outAccount.toArray(new String[outAccount.size()]));  
			convertOutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
			convertOutSpinner.setAdapter(convertOutSpinnerAdapter);
			convertInSpinnerAdapter = new ArrayAdapter<String>(ZfTransfer.this , android.R.layout.simple_spinner_item ,(String [])inAccount.toArray(new String[inAccount.size()]));
			convertInSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			convertInSpinner.setAdapter(convertInSpinnerAdapter);
		}catch(Exception e ){
			e.printStackTrace();
		}
		hiddenProgressToolBar();
	}
	
	/**
	 * 返回事件
	 */
	private void cancel() {
		finish();
	}
	
	/**
	 * 提交事件
	 */
	private void submit() {
		String pwd = convertPwd.getText().toString().trim();
		if("".equals(pwd)) {
			toast("请输入转出密码!");
			return;
		}
		String strAmount = convertAmount.getText().toString().trim();
		if(strAmount.equals("")) {
			toast("请输入转账金额!");
			return;
		}else if(!TradeUtil.checkNumber(strAmount, false)){
			toast("请输入正确的转账金额!");
			return;
		}
		outId = outAccount.get(convertOutSpinner.getSelectedItemPosition());
		inId = inAccount.get(convertInSpinner.getSelectedItemPosition());
		String message  = "委托类别:" + titleName  + "\n";
		message += "转出账号:" + outId  + "\n";
		message +="转入账号:" + inId + "\n";
		message +="转帐金额:" + strAmount + " 元\n";
		new AlertDialog.Builder(ZfTransfer.this)
		.setTitle("委托提示")
		.setMessage(message)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				CharSequence title = "正在与服务器通讯握手...";
				CharSequence message = "正在往服务器提交数据...";
				ProgressDialog mDialog =ProgressDialog.show(ZfTransfer.this, title, message ,true);
				try{
					StringBuffer sb = new StringBuffer();
					sb.append("FID_ZJZH_ZR="+inId+TradeUtil.SPLIT );
					sb.append("FID_ZJZH="+outId+TradeUtil.SPLIT );
					sb.append("FID_BZ="+currencyVal[currency.getSelectedItemPosition()]+TradeUtil.SPLIT );
					sb.append("FID_ZY="+titleName+TradeUtil.SPLIT );
					sb.append("FID_ZJMM="+AES.encrypt(convertPwd.getText().toString().trim(),TradeUtil.g_pubKey)+TradeUtil.SPLIT );
					sb.append("FID_FSJE="+convertAmount.getText().toString().trim());
					
					JSONObject dataObject = ConnPool.sendReq("TRANSFER_FUNDS","203526",sb.toString());
					String result  = TradeUtil.checkResult(dataObject);
					if (result != null) {
						if (result.equals("-1"))
							Toast.makeText(ZfTransfer.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
						else
							toast(result);
						mDialog.dismiss();
						return;
					}else{
						JSONArray jarr = (JSONArray) dataObject.getJSONArray("item");
						toast(jarr.getJSONObject(0).getString("FID_MESSAGE"));
					}
					
				}catch(Exception e ){
					e.printStackTrace();
				}
				mDialog.dismiss();
				reset();
			}
		})
		.setNegativeButton("取消", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				
			}
		}).show();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	private void reset(){
		convertPwd.setText("");
		convertAmount.setText("");
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
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
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

