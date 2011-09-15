package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ScrollView;
import android.widget.Spinner;

import com.cssweb.android.base.QuoteGridActivity;
import com.cssweb.android.common.ConstData;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.main.R;
import com.cssweb.quote.util.Utils;
/**
 * 开放式基金查询条件
 * @author hoho
 *
 */
public class FundQueryCondition extends QuoteGridActivity {
	@SuppressWarnings("unused")
	private Context context = FundQueryCondition.this;
	private Spinner companySpinner;
	private Spinner jingzhiSpinner;
	private Spinner levelSpinner;
	private Spinner managerLevelSpinner;
	private final String TAG = "FundQueryCondition" ;
	private ArrayAdapter<String> jingzhiAdapter;
	private ArrayAdapter<String> levelAdapter;
	private ArrayAdapter<String> managerLevelAdapter;
	private ArrayAdapter<String> companyAdapter ;
	private static  String [] fundCompany ;
	private static  String [] fundCompanyId;
	ScrollView scrollView;
	private Thread thread = null;
	private boolean firstComing = true;
	private int paramType ;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.zr_quote_fundquerycondition);
		initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.openfundname));
		String [] toolbarname = new String []{
				Global.TOOLBAR_CHAXUN, "" ,"" ,"" ,""
		};
		initToolBar(toolbarname, "");
		setSpinner();
		handlerData();
		Bundle bundle = getIntent().getExtras();
		paramType = bundle.getInt("type");
	}
	/**
	 * 请求后台数据
	 */
	@Override
	protected void init(final int type) {
		r = new Runnable(){
			public void run() {
				if (type ==1){
					quoteData = ConnService.execute(null, 4 );
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
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}
	/**
	 * 更新UI界面
	 * 
	 */
	@Override
	protected void handlerData() {
		try{
			if(isNetworkError<0&&firstComing) {
				//firstComing = false;
				toast(R.string.load_data_error);
			}
			if(quoteData ==null){
				hiddenProgressToolBar();
				return;
			}
			JSONArray jArr = quoteData.getJSONArray("data");
			int len  = jArr.length();
			fundCompany = new String [len+1];
			fundCompanyId = new String [len+1];
			fundCompanyId[0] = "-1" ;
			fundCompany[0]= "       全部";
			for (int i=0; i<jArr.length() ; i++){
				JSONArray jA = (JSONArray)jArr.get(i);
				fundCompanyId[i+1] = jA.getString(0);
				fundCompany[i+1]= jA.getString(1);
			}
			companyAdapter = new ArrayAdapter<String>(this , R.layout.mysimple_spinner_item , fundCompany);
			companyAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item);
			companySpinner.setAdapter(companyAdapter);
			companySpinner.setPrompt(getResources().getString(R.string.fundcompany));
			hiddenProgressToolBar();
		}catch(Exception e){
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}finally {
			//进度条消失
			hiddenProgressToolBar();
		}
	}
	/**
	 * 改变背景色
	 */
	 protected void initTitle(int resid1, int resid2, String str) {
	    	super.initTitle(resid1, resid2, str);
	    	changeTitleBg();
	 }
	
	@Override
	protected void toolBarClick(int tag, View v) {
		switch(tag){
		case 0:
			if(isNetworkError<0&&firstComing) {
				//firstComing = false;
				toast(R.string.load_data_error);
				return ;
			}else {
				Intent intent = new Intent();
				String fundCompanyid2="-1";  //为-1的时候表示查询全部
				if(null!=fundCompanyId){
					fundCompanyid2 = fundCompanyId[companySpinner.getSelectedItemPosition()];
				}
				String jingzhi1 =ConstData.jingzhiId1[jingzhiSpinner.getSelectedItemPosition()];
				String jingzhi2 =ConstData.jingzhiId2[jingzhiSpinner.getSelectedItemPosition()];
				
				Integer level = ConstData.fundLevelId[levelSpinner.getSelectedItemPosition()];
				Integer managerLevel = ConstData.fundMangerLevelId[managerLevelSpinner.getSelectedItemPosition()];
				
				Bundle bundle = new Bundle();
				bundle.putString("fundCompanyId", fundCompanyid2);
				bundle.putString("jingzhi1", jingzhi1);
				bundle.putString("jingzhi2", jingzhi2);
				
				bundle.putString("level", String.valueOf(level));
				bundle.putString("managerLevel", String.valueOf(managerLevel));
				bundle.putString("isQuery", "1");
				bundle.putInt("type", paramType);
				intent.putExtras(bundle);
				
				intent.setClass(this, StockTypeFund.class);   
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //解决从这个界面到第一个界面，会再次压栈，这样就有两个一样的activity。加入这个意思是清除
																 //栈里面的最上面一个
				startActivity(intent);
				FundQueryCondition.this.finish();
			}
			break;
		default:
			cancelThread();
			break;
		}
	}
	@Override
	protected void onResume() {
		super.onResume();
		setToolBar();
		initPopupWindow();
	}
	/**
	 * 设置下拉框
	 */
	private void setSpinner(){
		companySpinner = (Spinner) findViewById(R.id.fundcompany);
		jingzhiSpinner = (Spinner) findViewById(R.id.jingzhi);
		levelSpinner = (Spinner) findViewById(R.id.fundlevel);
		managerLevelSpinner = (Spinner) findViewById(R.id.fundmanagerlevel);
		jingzhiAdapter = new ArrayAdapter<String>(this , R.layout.mysimple_spinner_item, ConstData.jingzhi);
		jingzhiAdapter.setDropDownViewResource(R.layout.mysimple_spinner_dropdown_item);
		jingzhiSpinner.setAdapter(jingzhiAdapter);
		jingzhiSpinner.setPrompt(getResources().getString(R.string.fundtype));
		levelAdapter = new ArrayAdapter<String>(this ,  R.layout.mysimple_spinner_item , ConstData.fundLevel);
		levelAdapter.setDropDownViewResource( R.layout.mysimple_spinner_dropdown_item );
		levelSpinner.setAdapter(levelAdapter);
		levelSpinner.setPrompt(getResources().getString(R.string.fundlevel));
		managerLevelAdapter = new ArrayAdapter<String>(this , R.layout.mysimple_spinner_item ,  ConstData.fundManagerLevel);   
		managerLevelAdapter.setDropDownViewResource( R.layout.mysimple_spinner_dropdown_item );
		managerLevelSpinner.setAdapter(managerLevelAdapter);
		managerLevelSpinner.setPrompt(getResources().getString(R.string.fundmanagerlevel));
	}
	 /**
	  * 取消线程
	  */
	 protected void cancelThread() {
		if(thread!=null) {
			thread.interrupt();
		}
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	 }
		
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mHandler.removeCallbacks(r);
	}
	
}
