package com.cssweb.android.trade.stock;

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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.CssLog;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnPool;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.quote.util.NameRule;

/**
 * 场内基金交易（认购/申购/赎回）
 * @author wangsheng
 *
 */
public class ExchangeFund extends CssKeyboardBase {
	public static final String DEBUG_TAG = "ExchangeFund";
	
	private Spinner spinnerOperation;
	private Spinner spinnerHoler;
	
	private EditText fundCode;
	private TextView lblNumber;
	private EditText number;
	private TextView fundNav;
	private TextView fundName;
	private TextView avaiAsset;
	private TextView lblMaxNumber;
	private TextView maxNumber;
	private LinearLayout layoutfundprice;
	private LinearLayout layoutAvaiAsset;
	private LinearLayout layoutMaxNumber;
	
	private List<String> holder;
	
	private ArrayAdapter<String> adapterOperation;
	private ArrayAdapter<String> adapterHolder;
	
	private String market;
	private int bsflag;//基金认购 1，基金申购 42 ，基金赎回 43
	private String price = "0";
	private int type;
	private JSONObject stockPosition = null;
	private Thread thread = null;
//	private int priceFormatNum = 2;//小数位数
	private ServiceControl sc = null;//行为分析
	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		
		setContentView(R.layout.zr_trade_cnjj);
		sc = new ServiceControl(this);//行为分析初始化
		Bundle bundle = getIntent().getExtras();
		type = bundle.getInt("type");
		
		String[] toolbarname = new String[]{Global.TOOLBAR_QUEDING,"","","","", Global.TOOLBAR_BACK};
		initToolBar(toolbarname, Global.BAR_TAG);
		setBtnStatus();
		
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);
		
		//title = (TextView)findViewById(R.id.zr_trade_exchange_fund_label);
		spinnerOperation = (Spinner)findViewById(R.id.Operation);
		fundCode = (EditText)findViewById(R.id.FundCode);
		lblNumber = (TextView)findViewById(R.id.zr_cnjj_label_number);
		number = (EditText)findViewById(R.id.Number);
		fundName = (TextView)findViewById(R.id.FundName);
		avaiAsset = (TextView)findViewById(R.id.AvaiAsset);
		lblMaxNumber = (TextView)findViewById(R.id.zr_cnjj_label_max_number);
		maxNumber = (TextView)findViewById(R.id.MaxNumber);
		fundNav = (TextView)findViewById(R.id.fundNav);
		spinnerHoler = (Spinner)findViewById(R.id.HolderCode);
		layoutfundprice = (LinearLayout)findViewById(R.id.LinearLayout08);
		layoutAvaiAsset = (LinearLayout)findViewById(R.id.LinearLayout06);
		layoutMaxNumber = (LinearLayout)findViewById(R.id.LinearLayout07);
		
		fundCode.setInputType(InputType.TYPE_NULL);
		fundCode.setFocusable(true);
		fundCode.setTag("STOCK");
		fundCode.setOnClickListener(setOnEditClickListener);
		fundCode.setOnFocusChangeListener(setOnEditFocusListener);
		fundCode.addTextChangedListener(new TextWatcher() {
			
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				
			}
			
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				
			}
			
			public void afterTextChanged(Editable s) {
				textChanged(s);
			}

		});
		
		number.setInputType(InputType.TYPE_NULL);
		number.setFocusable(true);
		number.setTag("NUMDOT");
		number.setOnClickListener(setOnEditClickListener);
		number.setOnFocusChangeListener(setOnEditFocusListener);
		
		String[] operation = getResources().getStringArray(R.array.cnjj_list_menu);
		adapterOperation = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operation);
		adapterOperation.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerOperation.setAdapter(adapterOperation);
		spinnerOperation.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				setLabels();
				
			}
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
		
		holder = TradeUser.getInstance().getHolder();
		adapterHolder = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, (String[])holder.toArray(new String[holder.size()]));
		adapterHolder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinnerHoler.setAdapter(adapterHolder);
		
		int h = R.drawable.forminput;
		Resources localResources = getResources();
		Drawable localDrawable = null;
		localDrawable = localResources.getDrawable(h);
		int spinnerheight = localDrawable.getIntrinsicHeight()-4;
//		Log.e("<<<<<<<<<<<<<<<<<<<eeeeeeeeeeeeeeeeeeeeeeeeeee>>>>>>>>>>>>>>", String.valueOf(spinnerheight));
		LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) spinnerHoler.getLayoutParams();//获取bank控件的当前布局
		linearParams1.height=spinnerheight;//对该控件的布局参数做修改
		spinnerHoler.setLayoutParams(linearParams1);
		
		setLabels();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	private void textChanged(Editable s) {
		Editable localEditable = this.fundCode.getText();
		if(localEditable == s) {
			String str = this.fundCode.getText().toString().trim();
			if(str.length()==6) {
				onHideKeyBoard();
				setToolBar();
			}
		}
	}
	
	@Override
	protected void init(int type) {
		r = new Runnable() {			
			public void run() {
				try {
					quoteData = ConnService.getTradeHQ(fundCode.getText().toString().trim());
					TradeUtil.getFundavl();
					stockPosition = TradeService.getStockPosition();
				} catch (Exception e) {
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
			if(res!=null) {
				if (res.equals("-1")){
					Toast.makeText(ExchangeFund.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
					//openDialog(R.string.network_error);
				}else if ("stock not found".equals(res)) {
    				toast("证券代码不存在！");
				}else{
    				toast(res);
    			}
				clear();
				hiddenProgressToolBar();
				return;
			}
			JSONArray jsonArr = quoteData.getJSONArray("item");
			JSONObject jsonObj = (JSONObject)jsonArr.get(0);
			
			market = NameRule.getMarketFromZqlb(jsonObj.getString("zqlb"),jsonObj.getString("zqdm"));
			fundName.setText(jsonObj.getString("zqjc"));
			String zqlb = jsonObj.getString("zqlb");
			zqlb = zqlb.substring(0,zqlb.indexOf("."));
			if (Integer.parseInt(zqlb) == 31 || Integer.parseInt(zqlb) == 30 ) {// 中小企业 或者 三板 
//				priceFormatNum = 2;
			}
//			priceFormatNum = NameRule.getStockFormatNum(zqlb);
			//展示用户的可用资金
			if(TradeUtil.MARKET_SHB.equals(market) || TradeUtil.MARKET_TU.equals(market))
				avaiAsset.setText(TradeUser.getInstance().getEnablefundavlUS());
			else if(TradeUtil.MARKET_SZB.equals(market))
				avaiAsset.setText(TradeUser.getInstance().getEnablefundavlHK());
			else 
				avaiAsset.setText(TradeUser.getInstance().getEnablefundavlRMB());
			
			try {
				if(bsflag==1 || bsflag==42){
					price = jsonObj.getString("sjw1");
				}else if(bsflag == 43){
					price = jsonObj.getString("bjw1");
				}
				if(price.equals("")){
					price = jsonObj.getString("zjcj");
				}
				if(price.equals("")){
					price = jsonObj.getString("zrsp");
				}
				fundNav.setText(price);
				if (type == 0) { //认购价格：1元/份
					price = "1";
				}
				
			} catch (Exception e) {
				CssLog.e(DEBUG_TAG, e.toString());
			}
			//切换股东账号
			JSONObject stockAccountData = null;
			JSONArray jsonArr1 = null;
			String filedate = ActivityUtil.getPreference(ExchangeFund.this,"openholdersListDate", "");
			if(!(filedate).equals(DateTool.getToday())){ //如果时间不匹配，重新到柜台获获取
				stockAccountData = ConnPool.sendReq("GET_STOCKACCOUNT", "304001","");
			}else{
				String jsonObject = CssIniFile.loadIni(ExchangeFund.this, 9, "ShareholdersList");
				if(null !=jsonObject && ! jsonObject.equals("")){
					stockAccountData = new JSONObject(jsonObject);
				}
			}
			res = TradeUtil.checkResult(stockAccountData);
			if(res==null){
				jsonArr1 = (JSONArray)stockAccountData.getJSONArray("item");
			}
			for(int i=0,size=jsonArr1.length()-1; i<size; i++){
				JSONObject jsonObj1 = (JSONObject)jsonArr1.get(i);
				if (jsonObj1.getString("FID_JYS").equals(market)) {
					spinnerHoler.setSelection(i);
				}
			}
    		
    		String res1 = TradeUtil.checkResult(stockPosition);
    		String kmcsl = "0";
    		if (res1 == null) {
    			JSONArray jArr = (JSONArray)stockPosition.getJSONArray("item");
    			for (int i =0 ; i< jArr.length()-1 ; i++ ){
    				JSONObject  jsonobj = jArr.getJSONObject(i);
    				if( fundCode.getText().toString().trim().equals(jsonobj.getString("FID_ZQDM"))){;
    					kmcsl = jsonobj.getString("FID_KMCSL");
    					break;
    				}
    			}
			}
    		maxNumber.setText(kmcsl);
		} catch (JSONException e) {
			CssLog.e(DEBUG_TAG, e.toString());
		}
		hiddenProgressToolBar();
	}
	
	/**
	 * 根据选择的操作类别，设置不同的标签显示
	 */
	private void setLabels() {
		switch (type) {
		case 0:
			//title.setText("场内认购");
			sc.currentSite("wtjy_cnjj_cnrg");
			bsflag = 1;
			lblNumber.setText("认购份额");
			initTitle(R.drawable.njzq_title_left_back, 0, "场内基金认购");
			layoutfundprice.setVisibility(View.VISIBLE);
			break;
		case 1:
			//title.setText("场内申购");
			sc.currentSite("wtjy_cnjj_cnsg");
			bsflag = 42;
			lblNumber.setText("申购金额");
			initTitle(R.drawable.njzq_title_left_back, 0, "场内基金申购");
			layoutfundprice.setVisibility(View.GONE);
			layoutAvaiAsset.setBackgroundDrawable(getResources().getDrawable(R.drawable.zrtablegroupcellfooter));
			break;
		case 2:
			//title.setText("场内赎回");
			sc.currentSite("wtjy_cnjj_cnsh");
			bsflag = 43;
			lblNumber.setText("赎回份额");
			lblMaxNumber.setText("可用份额");
			initTitle(R.drawable.njzq_title_left_back, 0, "场内基金赎回");
			layoutfundprice.setVisibility(View.GONE);
			layoutAvaiAsset.setVisibility(View.GONE);
			layoutMaxNumber.setVisibility(View.VISIBLE);
			layoutMaxNumber.setBackgroundDrawable(getResources().getDrawable(R.drawable.zrtablegroupcellfooter));
			break;
		}
	}

	private void cancel() {
		finish();
	}
	
	private void submit() {
		String jjdm = fundCode.getText().toString();
		String qty = number.getText().toString();
		if(jjdm==null||jjdm.equals("")) {
			toast("请输入基金代码!");
			return;
		}else if (jjdm.length()!= 6||!TradeUtil.checkNumber(jjdm, false)) {
			toast("请输入正确的基金代码!");
			return;
		}
		if(qty==null || qty.equals("")){
			toast("请输入"+lblNumber.getText()+"!");
			return;
		}else if(!TradeUtil.checkNumber(qty, false)){
			toast("请输入正确的"+lblNumber.getText()+"!");
			return;
		}
		
		StringBuffer sb = new StringBuffer();
		String operation = "";
		switch (type) {
		case 0:
			operation = "基金认购";
			break;
		case 1:
			operation = "基金申购";
			break;
		case 2:
			operation = "基金赎回";
			break;
		}
		sb.append("操作类别："+operation+"\n");
		sb.append("基金代码："+fundCode.getText()+"\t"+fundName.getText()+"\n");
		sb.append(lblNumber.getText()+"："+number.getText()+"\n");
		sb.append("股东代码："+holder.get(spinnerHoler.getSelectedItemPosition())+"\n\n");
		sb.append("(如果股东代码有误,请选择正确的股东代码)\n");
		new AlertDialog.Builder(ExchangeFund.this)
		.setTitle("交易确认")
		.setMessage(sb.toString())
		.setPositiveButton("确认", 
				new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					CharSequence title = "正在与服务器通讯握手...";
					CharSequence message = "正在往服务器提交数据...";
					
					ProgressDialog myDialog = ProgressDialog.show(ExchangeFund.this, title, message, true);
					
					StringBuffer sb = new StringBuffer();
		    		
					sb.append("FID_JYS=" + market+ TradeUtil.SPLIT);
		    		sb.append("FID_GDH=" + holder.get(spinnerHoler.getSelectedItemPosition()) + TradeUtil.SPLIT);
		    		sb.append("FID_ZQDM=" + fundCode.getText().toString() + TradeUtil.SPLIT);
		    		sb.append("FID_JYLB=" + bsflag + TradeUtil.SPLIT);
		    		sb.append("FID_WTJG=" + price + TradeUtil.SPLIT);	
		    		sb.append("FID_WTSL=" + number.getText().toString() + TradeUtil.SPLIT);
		    		sb.append("FID_DDLX=" + TradeUtil.SPLIT);
					sb.append("FID_WTPCH=" + TradeUtil.SPLIT);
					
					try {
						JSONObject quoteData = ConnPool.sendReq("STOCK_BS_FUND", "204501", sb.toString());
						String res = TradeUtil.checkResult(quoteData);
			    		if(res!=null) {
			    			if (res.equals("-1")){
								Toast.makeText(ExchangeFund.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								//openDialog(R.string.network_error);
							}else {
								toast("委托失败：" + res);
							}
			    		}
			    		else {
				    		JSONArray jArr = (JSONArray)quoteData.getJSONArray("item");
				    		JSONObject j = (JSONObject)jArr.get(0);
				    		toast(j.getString("FID_MESSAGE")+",委托编号："+j.getString("FID_WTH"));
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
		fundCode.setText("");
		number.setText("");
		fundName.setText("");
		fundNav.setText("");
		maxNumber.setText("");
		avaiAsset.setText("");
		spinnerHoler.setAdapter(adapterHolder);
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
