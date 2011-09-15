package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.HandlerThread;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssweb.android.base.CssKeyboardBase;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.service.QuoteWarnService;
import com.cssweb.android.service.ValidationService;
import com.cssweb.quote.util.Arith;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;
/**
 * 行情预警 添加、修改
 * @author hoho
 *
 */
public class QuoteSet extends CssKeyboardBase {
	private ImageView search;  			   //搜索
	private TextView zqcode ,zjcjText;     //证券代码、最近成交
	private EditText spjEdit , xpjEdit;    //上破价、下破价
	private ImageButton downSPJ,upSPJ;     //向下、向上
	private TextView zfxdText ,dfxdText;   //涨幅限定、跌幅限定
	private ImageButton downXPJ ,upXPJ;    //向下、向上
	private Button sureButton ,cancelButton ;   //确定 、取消
	private int requestType;
	private String exchange ,stockcode ,stockname ;
	private RequestParams requestParams;
	private String zjcjParam , spjParam ,xpjParam, exchangeParam ,stockFundCode ;
	private String flag =null , message = null ;
	AlertDialog cancelDialog=null ,okDialog=null;
	private boolean firstComing = true ,isAddUpdate = false ;
	private int successOrFail = 1 ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		setContentView(R.layout.zr_quote_set);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.quotewarn_title));
		initView();
		LinearLayout localLinearLayout = (LinearLayout)findViewById(R.id.quotesetlinearyout);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
	    localView2.setOnClickListener(setOnEditClickListener);
	    setDownOrUp();
		search.setOnClickListener(onClickListener);
		this.activityKind = Global.QUOTE_WARN;
		requestParams = new RequestParams();
		Bundle bundle = getIntent().getExtras();
		if(null !=bundle){
			exchange  = bundle.getString("exchange");	//从个股查询带过来的参数
			stockcode = bundle.getString("stockcode");
			stockname = bundle.getString("stockname");
			requestType = bundle.getInt("requestType");
			zqcode.setText(stockname);
			flag = bundle.getString("flag");	//标志是不是从QuoteWarning 过来
			if(null!=flag && flag.equals("1")){
				zjcjParam = bundle.getString("zjcjParam");  // 从QuoteWarning 带过来的参数
				spjParam = bundle.getString("spjParam");
				xpjParam = bundle.getString("xpjParam");
				exchangeParam = bundle.getString("exchangeParam");
				stockFundCode = bundle.getString("stockFundCode");
				
				int tempDigit = getDigit();
				if (tempDigit ==2){
					if (isDigit(zjcjParam) ){
						zjcjText.setText( Utils.dataFormation( Double.parseDouble(zjcjParam) , 2 , 0) );
					}
					if (isDigit(spjParam) ){
						spjEdit.setText( Utils.dataFormation( Double.parseDouble(spjParam) , 2 , 0));
					}
					if (isDigit(xpjParam) ){
						xpjEdit.setText( Utils.dataFormation( Double.parseDouble(xpjParam) , 2 , 0));
					}
				}else{
					if (isDigit(zjcjParam) ){
						zjcjText.setText( Utils.dataFormation( Double.parseDouble(zjcjParam) ,1 ,0) );
					}
					if (isDigit(spjParam) ){
						spjEdit.setText( Utils.dataFormation( Double.parseDouble(spjParam) , 1 , 0));
					}
					if (isDigit(xpjParam) ){
						xpjEdit.setText( Utils.dataFormation( Double.parseDouble(xpjParam) ,1 ,0 ));
					}
				}
				getZFPercentage();
				getDFPercentage();
				String isAdd = bundle.getString("isAdd");
				if (isAdd.equals("2")){
					search.setVisibility(View.INVISIBLE);
				}
			}
		}
		sureButton.setOnClickListener(clickSureListener);
		cancelButton.setOnClickListener(clickCancelListener);
		
		zqcode.setFocusable(true);			 // 把焦点放在证券代码上, 这样上破价 设置 spjEdit.setFocusable(true); 也不会有光标闪烁，点击下，再会出现光标
		zqcode.setFocusableInTouchMode(true);
		okTip("");			//初始化提示框
		cancelTip("");		//初始化提示框
	}
	
	/**
	 * 确定按钮
	 */
	private OnClickListener clickSureListener = new OnClickListener (){
		public void onClick(View v) {
			try{
				hiddenProgress();
				if(null!=flag && flag.equals("1")){   //从行情预警列表过来
				}else{                                //从个股查询过来
					if (NameRule.getFilterMarket(exchange)){
						cancelDialog.setMessage("不能添加此类证券");
						cancelDialog.show();
						return ;
					}
				}
				String spj = spjEdit.getText().toString();
				String xpj = xpjEdit.getText().toString();
				String zjcj = zjcjText.getText().toString() ;
				zjcj = zjcj.equals("") ? "0" : zjcj ;
				if (null ==zjcj || zjcj.equals("0") || zjcj.equals("0.0")|| zjcj.equals("0.00")|| zjcj.equals("0.000") ){
					cancelDialog.setMessage(getResources().getString(R.string.zjcj_message));
					cancelDialog.show();
					return ;
				}
				
				if (! isDigit(spj) ){
					cancelDialog.setMessage(getResources().getString(R.string.spj_message));
					cancelDialog.show();
					return;
				}else if( Double.parseDouble (spj) <= Double.parseDouble(zjcj) ){
					cancelDialog.setMessage(getResources().getString(R.string.spj_message2));
					cancelDialog.show();
					return ;
				}else if (Double.parseDouble (spj) > Double.parseDouble( Utils.dataFormation( (Double.parseDouble(zjcj) ) * 5 ,1 ,0  ) ) ){
					cancelDialog.setMessage(getResources().getString(R.string.spj_message3));
					cancelDialog.show();
					return ;
				}
				
				if (! isDigit(xpj) ){
					cancelDialog.setMessage(getResources().getString(R.string.xpj_message));
					cancelDialog.show();
					return ;
				}else if ( Double.parseDouble (xpj)==0){
					cancelDialog.setMessage(getResources().getString(R.string.xpj_message));
					cancelDialog.show();
					return ;
				}else if (Double.parseDouble (xpj) >= Double.parseDouble (zjcj) && !zjcj.equals("0")){
					cancelDialog.setMessage(getResources().getString(R.string.xpj_message2));
					cancelDialog.show();
					return ;
				}else if (Double.parseDouble(Utils.dataFormation((Double.parseDouble(xpj)) * 5 , 1 ,0) )  <  Double.parseDouble (zjcj) ){
					cancelDialog.setMessage(getResources().getString(R.string.xpj_message3));
					cancelDialog.show();
					return ;
				}
				
				if(null !=flag && flag.equals("1")){
					openProgress();
					init(2);   //线程处理
				}else{
					openProgress();
					init(3);   //线程处理
				}
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
	};
	/**
	 * 取消按钮
	 */
	private OnClickListener clickCancelListener  = new OnClickListener (){
		public void onClick(View v) {		
//			Intent intent = new Intent();
//			intent.setClass(QuoteSet.this, QuoteWarning.class);
//			startActivity(intent);	//这段代码又会增加一个activity 到栈中的，不用了
			QuoteSet.this.finish();
		}
	};
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(final int type) {
		mHandler.removeCallbacks(r);
		try{
			r = new Runnable(){
				public void run() {
					if (type ==1){    //根据代码请求数据填充界面数据
						isAddUpdate = false ;
						if (null !=exchange && null !=stockcode){
							requestParams.setStocks(exchange+stockcode)	;
							quoteData = ConnService.execute(requestParams, requestType);
							try {
								if (Utils.isHttpStatus(quoteData)){
									isNetworkError = 0;
								}else {
									isNetworkError = -1;
								}
							} catch (JSONException e) {
								isNetworkError = -2;
								e.printStackTrace();
							}
						}
					}else if ( type ==2){   //修改操作
						isAddUpdate = true ;
						quoteData =	QuoteWarnService.addQuoteWarn(stockFundCode, Integer.parseInt(exchangeParam), spjEdit.getText().toString(), 
								    xpjEdit.getText().toString(), ValidationService.getServiceTime());
						
						if(null!=quoteData){
							try {
								String result = (String) quoteData.get("cssweb_code");
								if (null!=result && result.equals("success")){
									successOrFail = 1;
								}else {
									successOrFail = 2;
								}
								message = (String) quoteData.get("cssweb_msg");
								if(null!=message && message.equals("")){
									message="修改成功";
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else{
							successOrFail = 2;
							message="网络异常"; 
						}

						
					}else if (type ==3){	//添加操作
						isAddUpdate = true ;
						quoteData =	QuoteWarnService.addQuoteWarn(stockcode, NameRule.getMarket(exchange), spjEdit.getText().toString(), 
								    xpjEdit.getText().toString(), ValidationService.getServiceTime());
						
						if(null !=quoteData){
							try {
								String result = (String) quoteData.get("cssweb_code");
								if (null!=result && result.equals("success")){
									successOrFail = 1;
								}else {
									successOrFail = 2;
								}
								message = (String) quoteData.get("cssweb_msg");
								if(null==message ||  message.equals("")){
									message="添加成功";
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}else {
							successOrFail = 2;
							message="网络异常"; 
						}
					}
					mHandler.sendEmptyMessage(0);
				}
			};
			mHandler.post(r);
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
	/**
	 * 更新UI界面
	 */
	@Override
	protected void handlerData() {
		Runnable r = new Runnable(){
			public void run() {
				try{
					if (isAddUpdate){  //添加、修改 操作
						hiddenProgress();
						if (successOrFail ==1){
							if(!okDialog.isShowing())		//防止重复显示
								okDialog.setMessage(message);
								okDialog.show();
						}else{
							if(!cancelDialog.isShowing())	//防止重复显示
								cancelDialog.setMessage(message);
								cancelDialog.show();
						}
						
					}else {           //根据代码请求数据填充界面数据
						if(isNetworkError<0&&firstComing) {
							firstComing = false;
							toast(R.string.load_data_error);
						}
						if(quoteData ==null){
							//Toast.makeText(QuoteSet.this, getResources().getString(R.string.network_error), Toast.LENGTH_SHORT).show();
							return ;
						}
						JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
						JSONArray object  = (JSONArray) jArr.get(0);
						Double zjjc =  object.getDouble(1);
						int tempDigit = getDigit();
						if (tempDigit ==2){
							zjcjText.setText( Utils.dataFormation( zjjc,2 , 0 ) );
							spjEdit.setText( Utils.dataFormation( zjjc,2 , 0));
							xpjEdit.setText( Utils.dataFormation( zjjc,2  ,0 ));
						}else{
							zjcjText.setText( Utils.dataFormation( zjjc , 1,0 ) );
							spjEdit.setText( Utils.dataFormation( zjjc ,1 ,0));
							xpjEdit.setText( Utils.dataFormation( zjjc ,1,0));
						}
						if(zjjc > 0){
							zfxdText.setText("0.00%");
							dfxdText.setText("0.00%");
						}
					}
				}catch(Exception e ){
					e.printStackTrace();
				}finally{
					hiddenProgress();
				}
			}
		};
		runOnUiThread(r);
	}
	/**
	 * 跳转
	 */
	private OnClickListener onClickListener = new OnClickListener (){
		public void onClick(View v) {
			Intent localIntent = new Intent();
			localIntent.setClass(QuoteSet.this, QueryStock.class);
			localIntent.putExtra("menuid", activityKind);
			startActivity(localIntent);
			//QuoteSet.this.finish();
		}
	};
	/**
	 * 上下微调初始化
	 */
	private void setDownOrUp(){
		spjEdit.setInputType(InputType.TYPE_NULL);
		spjEdit.setTag("STOCK");
		spjEdit.setOnClickListener(setOnEditClickListener);
		spjEdit.setFocusable(true);
		spjEdit.setOnFocusChangeListener(setOnEditFocusListener);
		
		xpjEdit.setInputType(InputType.TYPE_NULL);
		xpjEdit.setTag("STOCK");
		xpjEdit.setOnClickListener(setOnEditClickListener);
		xpjEdit.setFocusable(true);
		xpjEdit.setOnFocusChangeListener(setOnEditFocusListener);
		
		downSPJ.setTag(0);
		downSPJ.setOnClickListener(adjustIconListener);
		upSPJ.setTag(1);
		upSPJ.setOnClickListener(adjustIconListener);
		downXPJ.setTag(2);
		downXPJ.setOnClickListener(adjustIconListener);
		upXPJ.setTag(3);
		upXPJ.setOnClickListener(adjustIconListener);
	}
	/**
	 * 初始化组件
	 */
	private void initView(){
		search = (ImageView) findViewById(R.id.imagesearch);
		zqcode = (TextView) findViewById(R.id.codeedit);
		zjcjText = (TextView) findViewById(R.id.zjcjEdit);
		spjEdit = (EditText) findViewById(R.id.spjEdit);
		spjEdit.setTag("1");
		downSPJ = (ImageButton) findViewById(R.id.downimage);
		upSPJ = (ImageButton) findViewById(R.id.upimage);
		zfxdText = (TextView) findViewById(R.id.zfxdEdit);
		xpjEdit = (EditText) findViewById(R.id.xpjEdit);
		xpjEdit.setTag("2");
		downXPJ = (ImageButton) findViewById(R.id.downimage2);
		upXPJ = (ImageButton) findViewById(R.id.upimage2);
		dfxdText = (TextView) findViewById(R.id.dfxdEdit);
		sureButton = (Button) findViewById(R.id.surebutton);
		cancelButton = (Button) findViewById(R.id.cancelbutton);
	}
	
	/**
	 * 上下微调事件
	 */
	@Override
	public void adjustDownOrUp(Object tag) {
		String name = zqcode.getText().toString();
		String str = "";
		switch ((Integer)tag) {
		case 0:
			try{
				str = spjEdit.getText().toString().trim();
				if(str.length() == 0){
					spjEdit.setText("0.01");
				}else if(str.length() > 0){
					if (! isDigit(str) ){
						
					}else{
						Double amount = Double.valueOf(str);
						if(amount == 0.01 || amount == 0)
							spjEdit.setText("0.00");
						else{
							int tempDigit = getDigit();
							if (tempDigit ==2 ){
								spjEdit.setText( Utils.dataFormation(Arith.round(amount -0.001, 3) ,2  ,0) );
							}else{
								spjEdit.setText( Utils.dataFormation(Arith.round(amount -0.01, 2) ,1 ,0) );
							}
						}
					}
				}
				// 计算涨幅  （上破价 - 现价） / 现价
				if(   null !=name && !name.equals("")  ){
					String zjcjValue = zjcjText.getText().toString();
					if (! isDigit(zjcjValue) ){
						
					}else {
						if (Double.parseDouble(zjcjValue) > 0){
							getZFPercentage();
						}
					}
				}
				break;
			}catch(Exception e ){
				e.printStackTrace();
			}
		case 1:
			try{
				str = spjEdit.getText().toString().trim();
				if(str.length() == 0){
					spjEdit.setText("0.01");
				}else if(str.length() > 0){
					if (! isDigit(str) ){
						
					}else{
						Double amount = Double.valueOf(str);
						int tempDigit = getDigit();
						if (tempDigit ==2){
							spjEdit.setText( Utils.dataFormation(Arith.round(amount + 0.001, 3) ,2 ,0) );
						}else{
							spjEdit.setText( Utils.dataFormation(Arith.round(amount +0.01, 2) ,1, 0) );
						}
					}
				}
				// 计算涨幅  （上破价 - 现价） / 现价
				if(  null !=name && !name.equals("") ){
					String zjcjValue = (String)zjcjText.getText();
					if (!isDigit(zjcjValue)){
						
					}else{
						if (Double.parseDouble(zjcjValue) > 0){
							getZFPercentage();
						}
					}
				}
				break;
			}catch(Exception e ){
				e.printStackTrace();
			}
		case 2:
			try{
				str = xpjEdit.getText().toString().trim();
				if(str.length() == 0){
					xpjEdit.setText("0.01");
				}else if(str.length() > 0){
					if (!isDigit(str)){
						
					}else{
						Double amount = Double.valueOf(str);
						if(amount == 0.01 || amount == 0)
							xpjEdit.setText("0.00");
						else{
							int tempDigit = getDigit();
							if (tempDigit ==2){
								xpjEdit.setText( Utils.dataFormation(Arith.round(amount -0.001, 3) ,2 ,0 ) );
							}else{
								xpjEdit.setText( Utils.dataFormation(Arith.round(amount -0.01, 2) ,1,0) );
							}
						}
					}
				}
				//计算跌幅
				if(  null !=name && !name.equals("") ){
					String zjcjValue = zjcjText.getText().toString();
					if (! isDigit(zjcjValue) ){
						
					}else {
						if (Double.parseDouble(zjcjValue) > 0){
							getDFPercentage();
						}
					}
				}
				break;
			}catch(Exception e ){
				e.printStackTrace();
			}
		case 3:
			try{
				str = xpjEdit.getText().toString().trim();
				if(str.length() == 0){
					xpjEdit.setText("0.01");
				}else if(str.length() > 0){
					if (! isDigit(str)){
						
					}else{
						Double amount = Double.valueOf(str);
						int tempDigit = getDigit();
						if (tempDigit == 2 ){
							xpjEdit.setText( Utils.dataFormation(Arith.round(amount + 0.001, 3) ,2 ,0) );
						}else {
							xpjEdit.setText( Utils.dataFormation(Arith.round(amount+0.01, 2) ,1, 0) );
						}
					}
				}
				//计算跌幅
				if(  null !=name && !name.equals("") ){
					String zjcjValue = zjcjText.getText().toString();
					if (! isDigit(zjcjValue) ){
						
					}else {
						if (Double.parseDouble(zjcjValue) > 0){
							getDFPercentage();
						}
					}
				}
				break;
			}catch(Exception e ){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 计算涨幅百分比
	 */
	private void getZFPercentage(){
		try{
			String shangPoJia = spjEdit.getText().toString();
			String xianJia = zjcjText.getText().toString();
			if (! isDigit(shangPoJia) ){
				
			}else if (xianJia.equals("")){
				
			}else if (Double.parseDouble(xianJia) == 0){     //处理现价为0的情况
				zfxdText.setText(Utils.dataFormation(0, 6 ,0)  );
			}
			else {
				double increase = (Double.parseDouble(shangPoJia)- Double.parseDouble(xianJia) ) / Double.parseDouble(xianJia) ;
				zfxdText.setText(Utils.dataFormation(increase, 6 ,0)  );
			}
		}catch(Exception e ){
			e.printStackTrace();
		}
		
	}
	/**
	 * 计算跌幅百分比
	 */
	private void getDFPercentage(){
		try{
			String xiaPoJia = xpjEdit.getText().toString();
			String xianJia = zjcjText.getText().toString();
			if (! isDigit(xiaPoJia)){
				
			}else if ( xianJia.equals("")){
				
			}else if (Double.parseDouble(xianJia) == 0){   //处理现价为0的情况
				dfxdText.setText(Utils.dataFormation(0, 6, 0)  );
			}
			else{
				double increase = ( Double.parseDouble(xiaPoJia)- Double.parseDouble(xianJia) ) / Double.parseDouble(xianJia) ;
				dfxdText.setText(Utils.dataFormation(increase, 6, 0)  );
			}
		}catch(Exception  e){
			e.printStackTrace();
		}
		
	}
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	 }
	@Override
	protected void onResume() {
		super.onResume();
		init(1);
		initPopupWindow();
		spjEdit.clearFocus();
	}
	
	/**
	 * 取消消息提示
	 * @param message
	 */
	private void cancelTip(String message){
		cancelDialog = new AlertDialog.Builder(QuoteSet.this) 
        .setTitle(getResources().getString(R.string.alert_tip)) 
        .setMessage(message) 
        .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel), 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog,  int whichButton) { 
                    	//QuoteSet.this.finish();
                    } 
         //}).show();
        }).create();
	}
	/**
	 * 确定消息提示
	 * @param message
	 */
	private void okTip(String message){
		okDialog = new AlertDialog.Builder(QuoteSet.this) 
        .setTitle(getResources().getString(R.string.alert_tip)) 
        .setMessage(message) 
        .setPositiveButton(getResources().getString(R.string.alert_dialog_ok), 
                new DialogInterface.OnClickListener() { 
                    public void onClick(DialogInterface dialog,  int whichButton) { 
//                    	Intent intent = new Intent();
//						intent.setClass(QuoteSet.this, QuoteWarning.class);
//						startActivity(intent);   //这段代码又会增加一个activity 到栈中的，不用了
						QuoteSet.this.finish();
                    } 
         //}).show();
        }).create();
	}
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//防止泄露窗口（activity 关闭,可能dialog没有关闭）
		if(okDialog!=null)
			okDialog.dismiss();
		if (cancelDialog!=null)
			cancelDialog.dismiss();
		mHandler.removeCallbacks(r);
	}
	/**
	 * 判断是否为数字
	 * @param str
	 * @return
	 */
	private boolean isDigit(String str){
		if ( null == str || str.equals("") || str.startsWith(".")){
			return false;
		}else {
			return true;
		}
	}
	
	private int getDigit(){
		try{
			int mDigit =0 ;
			if(null!=flag && flag.equals("1")){   //从行情预警列表过来
				if (exchangeParam ==null || exchangeParam.equals("")){
					return 1;
				}
				mDigit = Utils.getNumFormat(NameRule.getExchange(exchangeParam), stockFundCode );   //判断小数位是 2位 还是3 位
			}else{                                //从个股查询过来
				mDigit = Utils.getNumFormat(NameRule.getExchange(exchange), stockcode );   //判断小数位是 2位 还是3 位
			}
			return mDigit ;
		}catch(Exception e ){
			e.printStackTrace();
			return 2;
		}
	}
	
	

}
