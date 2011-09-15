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
 * 基金分红设置
 * @author hoho
 *
 */
public class FundMelonSet extends CssKeyboardBase  {
	private static final String DEBUG_TAG = "FundMelonSet";
	
	private Spinner spinner;
	private EditText melon_FundCode;
	private TextView melon_FundName ;
	private TextView melon_FundNav ;
	private TextView melon_label_title;
	
	private String shareclass ;
	private String tacode = "" ;
	private String taacc ;
	
	private ArrayAdapter<String> spinnerAdapter;
	String [] arrayOfString = null;
	String [] arrayOfValue = null;
	private Thread thread = null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_trade_fundmelonset);
		//setTitle("分红设置");
		//SetRightTitle("刷新", 2);
		initTitle(R.drawable.njzq_title_left_back, 0, "分红设置");
		//初始化工具栏
		String[] toolbarname = new String[]{Global.TOOLBAR_QUEDING,"","","","", Global.TOOLBAR_BACK};
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.melonLinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		arrayOfString = getResources().getStringArray(R.array.funddividmethod_colsname);
		arrayOfValue = getResources().getStringArray(R.array.funddividmethod_colsindex);
		spinner=(Spinner) findViewById(R.id.melon_RedempFlag);
		spinnerAdapter = new ArrayAdapter<String>(FundMelonSet.this , android.R.layout.simple_spinner_item ,arrayOfString );
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(spinnerAdapter);
		
		melon_FundCode = (EditText) findViewById(R.id.melon_fundcode);
		melon_FundName = (TextView) findViewById(R.id.melon_FundName);
		melon_FundNav = (TextView) findViewById(R.id.melon_FundNav);
		//melon_AvaiNumber = (TextView) findViewById(R.id.melon_AvaiNumber);
		melon_label_title = (TextView) findViewById(R.id.zr_melon_label_title);
		melon_label_title.setText("分红设置");
		String ofcode  = "";
		Boolean flag = true;
		try {
			ofcode = getIntent().getExtras().getString("ofcode");
			if(ofcode != null && !"".equals(ofcode))
				flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		melon_FundCode.setInputType(InputType.TYPE_NULL);
		melon_FundCode.setFocusable(flag);
		melon_FundCode.setTag("STOCK");
		melon_FundCode.setOnClickListener(setOnEditClickListener);
		melon_FundCode.setOnFocusChangeListener(setOnEditFocusListener);
		melon_FundCode.addTextChangedListener(new TextWatcher(){
			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				
			}
		});
		
		if (ofcode != null || !"".equals(ofcode)) {
			melon_FundCode.setText(ofcode);
		}
		
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
//		Log.e("<<<<<<<<<<<<<<<<<<<eeeeeeeeeeeeeeeeeeeeeeeeeee>>>>>>>>>>>>>>", String.valueOf(spinnerheight));
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) spinner.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		spinner.setLayoutParams(linearParams1);
	}
	
	protected void textChanged(Editable s){
		Editable editable = melon_FundCode.getText();
		if(editable == s){
			String str = melon_FundCode.getText().toString().trim();
			if(str.length() == 6){
				onHideKeyBoard();
				setToolBar();
			}
		}
	}
	/**
	 * 请求后台数据
	 */
	protected void init(final int type){
		r = new Runnable (){
			public void run() {
			    		try {
			    			String filedate = ActivityUtil.getPreference(FundMelonSet.this,"openFundInfoDate", "");
							if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
								quoteData = FundService.getFundInfo();
							}else{
								String jsonObject = CssIniFile.loadIni(FundMelonSet.this, 4, "fundInfo");
								if(null !=jsonObject && ! jsonObject.equals("")){
									quoteData = new JSONObject(jsonObject);
								}
							}
							mHandler.sendEmptyMessage(0);
						} catch (JSONException e) {
							e.printStackTrace();
						}
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
					Toast.makeText(FundMelonSet.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
				else
					toast(res);
				hiddenProgressToolBar();
				return;
			} else {
				JSONArray jarr = (JSONArray) jsonObject.getJSONArray("item");
				Boolean flag = true;
				for (int i =0 ; i< jarr.length()-1 ; i++ ){
					JSONObject  jsonobj = jarr.getJSONObject(i);
					String cacheofcode = jsonobj.getString("FID_JJDM");
					if( melon_FundCode.getText().toString().trim().equals(cacheofcode)){
						String ofname = jsonobj.getString("FID_JJJC");
						melon_FundName.setText(ofname);
						melon_FundNav.setText(TradeUtil.formatNum(jsonobj.getString("FID_JJJZ"), 4));
						
						tacode = jsonobj.getString("FID_TADM");
						shareclass = jsonobj.getString("FID_SFFS");
						flag = false;
						break;
					}
				}
				if (flag) {
					toast("基金代码不存在！");
					reset();
					hiddenProgressToolBar();
					return;
				}
			}

			hiddenProgressToolBar();

			
		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
			hiddenProgressToolBar();
		}
//		//Toast.makeText(FundMelonSet.this, fundmelonsetData.toString(), Toast.LENGTH_LONG).show();
//		try{
//			if(null == fundmelonsetData){
//				//showTipsDialog(getApplicationContext().getResources().getText(R.string.network_error) );
//				hiddenProgress();
//				return ;
//			}
//			JSONArray jsonarray = fundmelonsetData.getJSONArray("item");
//			if(jsonarray.length()<=1){
//				toast("没有基金");
//				hiddenProgress();
//				return ;
//			}
//			JSONObject jsonobject = jsonarray.getJSONObject(0);
//			String ofcode = jsonobject.getString("FID_JJDM");
//			// 取基金名称
//			String filedate =  ActivityUtil.getPreference(FundMelonSet.this, "openFundDate", "");
//			JSONObject fundcompanyData = null;
//			if( !DateTool.getToday().equals(filedate)){  //时间不匹配的话,到后台去请求
//				// 要特意测试下这个方法正确不？？？
//				StringBuffer sb  = new StringBuffer();
//				sb.append("tacode=" +  TradeUtil.SPLIT);
//				sb.append("ofcode=" + ofcode + TradeUtil.SPLIT);
//				sb.append("qryflag=1" + TradeUtil.SPLIT);
//				sb.append("count=" + Global.MAX_PAGE_SIZE + TradeUtil.SPLIT);
//				sb.append("poststr=" + TradeUtil.SPLIT);
//				JSONObject getData = ConnPool.sendReq("BS_FUNDTRANSFER", "410815", sb.toString());
//				String ofname  = getData.getString("ofname");
//				melon_FundName.setText(ofname);
//			}else{
//				String jsonObject  = CssIniFile.loadIni(FundMelonSet.this, 3, "fundCompany");
//				if(null !=jsonObject && ! jsonObject.equals("")){
//					fundcompanyData = new JSONObject(jsonObject);
//					JSONArray jarr = fundcompanyData.getJSONArray("item");
//					for (int i =0 ; i< jarr.length()-1 ; i++ ){
//						JSONObject  jsonobj = jarr.getJSONObject(i);
//						String cacheofcode = jsonobj.getString("ofcode");
//						if( ofcode.equals(cacheofcode)){
//							String ofname = jsonobj.getString("ofname");
//							melon_FundName.setText(ofname);
//							break;
//						}
//					}
//				}
//			}
//			
//			melon_FundNav.setText(jsonobject.getString("nav"));
//			melon_AvaiNumber.setText(jsonobject.getString("ofavl"));
//			shareclass = jsonobject.getString("shareclass");
//			tacode = jsonobject.getString("tacode");
//			
//		}catch(Exception e ){
//			e.printStackTrace();
//		}
//		hiddenProgress();
	}
	
	/**
	 * 提交事件
	 */
	private void submit() {
		String melon_FundCodevalue = melon_FundCode.getText().toString();
		if(melon_FundCodevalue == null || melon_FundCodevalue.equals("")){
			toast("基金代码不能为空");
			return ;
		}else if (melon_FundCodevalue.length()!= 6||!TradeUtil.checkNumber(melon_FundCodevalue, false)) {
			toast("请输入正确的基金代码!");
			return;
		}
		try {
			taacc = new TradeUtil().getFundAccount(tacode, FundMelonSet.this);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		String message  = "操作类别:" + "基金分红设置" + "\n" ;
		message +="基金代码:" + melon_FundCode.getText() + "\n" ;
		message +="基金净值:" + melon_FundNav.getText() + "\n" ;
		message +="分红方式:" + arrayOfString[spinner.getSelectedItemPosition()] + "\n" ; 
		new AlertDialog.Builder(FundMelonSet.this)
		.setTitle("委托提示")
		.setMessage(message)
		.setPositiveButton("确定", 
				 new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
						CharSequence title = "正在与服务器通讯握手...";
						CharSequence message = "正在往服务器提交数据...";
						ProgressDialog myDialog = ProgressDialog.show(FundMelonSet.this, title, message ,true);
						try{
							JSONObject fundMelon = FundService.fundDividend(melon_FundCode.getText().toString().trim(), tacode, taacc, shareclass, String.valueOf(spinner.getSelectedItemPosition()));
							String result = TradeUtil.checkResult(fundMelon);
							if (result != null) {
								if (result.equals("-1"))
									Toast.makeText(FundMelonSet.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								else
									toast(result);
								myDialog.dismiss();
								return;
							}else{
								JSONArray jarr = (JSONArray) fundMelon.getJSONArray("item");
								toast(jarr.getJSONObject(0).getString("FID_MESSAGE")+",委托编号："+jarr.getJSONObject(0).getString("FID_WTH"));
							}
							myDialog.dismiss();
							reset();
						}catch(Exception e ){
							e.printStackTrace();
						}
					}
		})
		.setNegativeButton("取消", 
				new DialogInterface.OnClickListener(){
					public void onClick(DialogInterface arg0, int arg1) {
						
					}
		}).show();
	}
	
	/**
	 * 取消事件
	 */
	private void cancel() {
		finish();
	}
	
//	/**
//	 * 处理刷新事件
//	 */
//	@Override
//	protected void RefreshUI() {
//		String melontext =  melon_FundCode.getText().toString();
//		if(null !=melontext && ! melontext.equals("")){
//			getFundMelonSetDate(1);
//		}
////		else {
////			getFundMelonSetDate(2);
////		}
//		
//	}
	
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	private void reset(){
		melon_FundCode.setText("");
		melon_FundName.setText("");
		melon_FundNav.setText("");
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
