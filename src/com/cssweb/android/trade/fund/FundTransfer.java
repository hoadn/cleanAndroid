package com.cssweb.android.trade.fund;

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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.ActivityUtil;
/**
 * 基金转换
 * @author hoho
 *
 */
public class FundTransfer extends CssKeyboardBase{
	private TextView  transfer_label_title ;
	private Spinner convertOutSpinner ;
	private Spinner convertInSpinner;
	//private Spinner redempFlagSpinner;
	private EditText convertnum ;
	private TextView maxhaveconvert;
//	private String [] redempFlag_arrayofString  =null;
//	private String [] redempFlag_arrayofValue= null;
	private String [] convertOut_arrayofString = null;
	private String [] convertOut_arrayofValue=null;
	private String [] convertIn_arrayofString =null;
	private String [] convertIn_arrayofValue = null;
	//private ArrayAdapter<String> redempFlagSpinnerAdapter;
	private ArrayAdapter<String> convertOutSpinnerAdapter;
	private ArrayAdapter<String> convertInSpinnerAdapter ;
	private JSONObject convertOutData = null;
	private JSONObject convertInData = null;
	private Thread thread = null;
	@Override
	public void onCreate(Bundle paramBundle){
		super.onCreate(paramBundle);
		setContentView(R.layout.zr_trade_fundtransfer);
		setTitle("基金转换");
		//SetRightTitle("刷新", 2);
		initTitle(R.drawable.njzq_title_left_back, 0, "基金转换");
		//初始化工具栏
		String[] toolbarname = new String[]{Global.TOOLBAR_QUEDING, "","","","", Global.TOOLBAR_BACK};
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.fundtransferLinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		transfer_label_title= (TextView) findViewById(R.id.zr_transfer_label_title);
		transfer_label_title.setText("基金转换");
		transfer_label_title.setVisibility(View.GONE);
		maxhaveconvert = (TextView) findViewById(R.id.maxhaveconvertid);
		convertOutSpinner =(Spinner) findViewById(R.id.convertoutcodeid);
		convertInSpinner = (Spinner) findViewById(R.id.convertincodeid);
		//redempFlagSpinner = (Spinner) findViewById(R.id.redemp_flagid);
		convertnum = (EditText) findViewById(R.id.convertnumid);
		
//		redempFlag_arrayofString = getResources().getStringArray(R.array.fundtransfer_methods);
//		redempFlag_arrayofValue = getResources().getStringArray(R.array.fundtransfer_methods_val);
//		redempFlagSpinnerAdapter = new ArrayAdapter<String>(FundTransfer.this ,android.R.layout.simple_spinner_item,redempFlag_arrayofString);
//		redempFlagSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		redempFlagSpinner.setAdapter(redempFlagSpinnerAdapter);
		
		convertnum.setInputType(InputType.TYPE_NULL);
		convertnum.setFocusable(true);
		convertnum.setTag("NUMDOT");
		convertnum.setOnClickListener(setOnEditClickListener);
		convertnum.setOnFocusChangeListener(setOnEditFocusListener);
		
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
		
		setToolBar();
		
	}
	protected void init(int type) {
		r = new Runnable() {
			public void run() {
				try {
					convertOutData = FundService.getFundPosition();
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
			String res = TradeUtil.checkResult(convertOutData);
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
			JSONArray jarr  = convertOutData.getJSONArray("item");
			if(jarr.length() >=1){
				convertOut_arrayofValue = new String [jarr.length() - 1 ];
				convertOut_arrayofString = new String [jarr.length() - 1];
				for (int i = 0,size = jarr.length() -1 ; i<size ;i++){
					JSONObject jsonobject = (JSONObject) jarr.get(i);    //数组值
					String ofcode = jsonobject.getString("FID_JJDM");
					String ofname = jsonobject.getString("FID_JJJC");
					convertOut_arrayofValue[i] = ofcode ;
					convertOut_arrayofString[i] = ofcode+"\t"
												+ofname;
//												+"\t"
//												+TradeUtil.formatNum(new TradeUtil().getFundCodeNav(ofcode, FundTransfer.this), 4)+"\t"
//												+TradeUtil.dealFundStatus(new TradeUtil().getFundStatus(ofcode, FundTransfer.this).charAt(0)) ;
				}
			}else{
				convertOut_arrayofValue  = new String [0];
				convertOut_arrayofString = new String [0];
			}
			
			convertOutSpinnerAdapter = new ArrayAdapter<String>(FundTransfer.this , android.R.layout.simple_spinner_item,convertOut_arrayofString);  
			convertOutSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);  
			convertOutSpinner.setAdapter(convertOutSpinnerAdapter);
			convertOutSpinner.setOnItemSelectedListener(new OnItemSelectedListener (){
				public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
					//转入代码
					try {
						JSONArray jarr  = convertOutData.getJSONArray("item");
						String tacode =null;
						for (int i = 0,size = jarr.length() -1 ; i<size ;i++){
							JSONObject jsonobject =  (JSONObject) jarr.get(i);
							String ofcode = jsonobject.getString("FID_JJDM");
							if(null !=ofcode  && ofcode.equals(convertOut_arrayofValue[position] ) ){
								//最大可转
								maxhaveconvert.setText(jsonobject.getString("FID_KYSL"));
								tacode = jsonobject.getString("FID_TADM") ;
								break;
							}
						}
						if("".equals(maxhaveconvert.getText().toString())){
							maxhaveconvert.setText("0");
						}
						convertInData = new TradeUtil().getFundByCompany(tacode, FundTransfer.this);
						//Log.i("<<<<<convertInData>>>>>>", convertInData.toString());
			    		if(null !=convertInData){
			    			JSONArray jarrin  = convertInData.getJSONArray("item");
			    			if(jarrin.length() >=1){
			    				convertIn_arrayofValue = new String [jarrin.length()];
				    			convertIn_arrayofString = new String [jarrin.length()];
				    			for (int i = 0,size = jarrin.length() ; i<size ;i++){
				    				JSONObject jsonobject = (JSONObject) jarrin.get(i);    	//数组值
				    				String ofcode = jsonobject.getString("FID_JJDM");
				    				String ofname = jsonobject.getString("FID_JJJC");
				    				convertIn_arrayofValue[i] =ofcode; 
				    				convertIn_arrayofString[i] = ofcode+"\t"+ofname;
//																+"\t"
//																+TradeUtil.formatNum(new TradeUtil().getFundCodeNav(ofcode, FundTransfer.this), 4)+"\t"
//																+TradeUtil.dealFundStatus(new TradeUtil().getFundStatus(ofcode, FundTransfer.this).charAt(0)) ;
				    			}
			    			}else {
			    				convertIn_arrayofValue = new String [0] ;
				    			convertIn_arrayofString = new String [0];
			    			}
			    		}
			    		//Log.i("debug", "33333333333333333333333334444444444444444convertIn_arrayofValue :"+convertIn_arrayofValue.length);
			    		//Log.i("debug", "4444444444444444444444444444444444444444convertIn_arrayofString:"+convertIn_arrayofString.length);
			    		convertInSpinnerAdapter = new ArrayAdapter<String>(FundTransfer.this , android.R.layout.simple_spinner_item ,convertIn_arrayofString);
			    		convertInSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			    		convertInSpinner.setAdapter(convertInSpinnerAdapter);
					} catch (JSONException e) {
						e.printStackTrace();
					}
					
					
				}
				public void onNothingSelected(AdapterView<?> arg0) {
					
				}
			});
			
			
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
		final String  convertnumValue = convertnum.getText().toString();
		if( null ==convertnumValue || convertnumValue.equals("") ){
			toast("转换份额不能为空");
			return ;
		}
		String convertout =(String) convertOutSpinner.getSelectedItem();
		final String convertoutcode = convertOut_arrayofValue[convertOutSpinner.getSelectedItemPosition()];
		if(null == convertout || convertout.equals("")){
			toast("请选择转出代码");
			return ;
		}
		String convertin = (String) convertInSpinner.getSelectedItem();
		final String convertincode = convertIn_arrayofValue[convertInSpinner.getSelectedItemPosition()];
		if(null ==convertin || convertin.equals("") ){
			toast("请选择转入代码");
			return ;
		}
		//final long redempFlagvalue = redempFlagSpinner.getSelectedItemId();
		
		String message  = "委托类别:" + "基金转换"  + "\n";
		message += "转入代码:" + convertincode  + "\n";
		message +="转出代码:" + convertoutcode + "\n";
		message +="转换份额:" + convertnumValue + "\n";
		new AlertDialog.Builder(FundTransfer.this)
		.setTitle("委托提示")
		.setMessage(message)
		.setPositiveButton("确定", new DialogInterface.OnClickListener(){
			public void onClick(DialogInterface arg0, int arg1) {
				CharSequence title = "正在与服务器通讯握手...";
				CharSequence message = "正在往服务器提交数据...";
				ProgressDialog mDialog =ProgressDialog.show(FundTransfer.this, title, message ,true);
				try{
					String tacode = ""; //基金公司代码
					String shareclass = ""; //收费方式
					String fhfs = ""; //分红方式
					String taacc = ""; //基金账户
					String filedate = ActivityUtil.getPreference(FundTransfer.this,"openFundInfoDate", "");
					if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
						JSONObject fundInfoData = FundService.getFundInfo();
						JSONArray jarr = fundInfoData.getJSONArray("item");
						for (int i =0 ; i< jarr.length()-1 ; i++ ){
							JSONObject  jsonobj = jarr.getJSONObject(i);
							if( convertoutcode.equals(jsonobj.getString("FID_JJDM"))){;
								tacode = jsonobj.getString("FID_TADM");
								shareclass = jsonobj.getString("FID_SFFS");
								fhfs = jsonobj.getString("FID_FHFS");
								break;
							}
						}
					}else{
						String jsonObject = CssIniFile.loadIni(FundTransfer.this, 4, "fundInfo");
						if(null !=jsonObject && ! jsonObject.equals("")){
							JSONObject fundInfoData = new JSONObject(jsonObject);
							JSONArray jarr = fundInfoData.getJSONArray("item");
							for (int i =0 ; i< jarr.length()-1 ; i++ ){
								JSONObject  jsonobj = jarr.getJSONObject(i);
								if( convertoutcode.equals(jsonobj.getString("FID_JJDM"))){;
									tacode = jsonobj.getString("FID_TADM");
									shareclass = jsonobj.getString("FID_SFFS");
									fhfs = jsonobj.getString("FID_FHFS");
									break;
								}
							}
						}
					}
					taacc = new TradeUtil().getFundAccount(tacode, FundTransfer.this);
					JSONObject fundTransferData = new FundService().fundTransfer(convertoutcode, convertincode, convertnumValue,tacode,shareclass,fhfs,taacc);
					//Log.i("333333", "1111111111111111111111222fundTransferData:"+fundTransferData.toString());
					String result  = TradeUtil.checkResult(fundTransferData);
					if (result != null) {
						if (result.equals("-1"))
							Toast.makeText(FundTransfer.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
						else
							toast(result);
						mDialog.dismiss();
						return;
					}else{
						JSONArray jarr = (JSONArray) fundTransferData.getJSONArray("item");
						toast(jarr.getJSONObject(0).getString("FID_MESSAGE")+",委托编号："+jarr.getJSONObject(0).getString("FID_WTH"));
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
		convertnum.setText("");
		maxhaveconvert.setText("");
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
		convertOut_arrayofString = null;
		convertOut_arrayofValue=null;
		convertIn_arrayofString =null;
		convertIn_arrayofValue = null;
		convertOutData = null;
		convertInData = null;
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
