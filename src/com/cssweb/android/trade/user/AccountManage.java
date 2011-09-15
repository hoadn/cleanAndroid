package com.cssweb.android.trade.user;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.share.StockPreference;
import com.cssweb.android.trade.adapter.AccountManageAdapter;
import com.cssweb.android.trade.login.LoginActivity;
import com.cssweb.android.util.ActivityUtil;

/**
 * 账号管理
 * 
 * @author 
 *
 */
public class AccountManage extends CssBaseActivity {
	private List<String> keys = new ArrayList<String>();
	private List<String> values = new ArrayList<String>();
	private int currentRowId = -1;
	
	private String[] myFundInfos;
	private ListView listView;
	private AccountManageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.zr_trade_account_details_list);
		
		initTitle(R.drawable.njzq_title_left_back, 0, "账号管理");
		
		listView = (ListView)findViewById(R.id.zr_rt_listview);
		listView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				arg0.getChildAt(position).setBackgroundResource(R.drawable.zr_trade_table_cell_selected_bg);
				if(currentRowId != -1 && position!=currentRowId){
					arg0.getChildAt(currentRowId).setBackgroundColor(Color.BLACK);
				}
				currentRowId = position;
				if(currentRowId == -1)
					return;
				String custid = myFundInfos[currentRowId];
				if(null != custid && custid.indexOf("&")!=-1 ){
					custid = custid.substring(0,custid.indexOf("&"));
				}
				Intent localIntent = new Intent();	    
				localIntent.putExtra("custid", custid);
				localIntent.putExtra("isChangeBtn", true);
				localIntent.setClass(AccountManage.this, LoginActivity.class);
				AccountManage.this.startActivity(localIntent);
			}
		});
		init(1);
		registerForContextMenu(listView);   

	}
	
	public void onCreateContextMenu(ContextMenu menu, View v,  ContextMenuInfo menuInfo) {   
		  super.onCreateContextMenu(menu, v, menuInfo); 
		  
		  AdapterContextMenuInfo info = (AdapterContextMenuInfo) menuInfo;
		  LinearLayout linearLayout   =  (LinearLayout) info.targetView ; 
		  TextView textView = (TextView) linearLayout.findViewById(R.id.account);
		  String name  = (String) textView.getText() ; 
		  
		  currentRowId =(Integer) textView.getTag();
		// Toast.makeText(AccountManage.this, "222222222222:"+name, Toast.LENGTH_SHORT).show();
		  menu.setHeaderTitle(name);
		  
		  menu.add(0, 0, 0, "删除");   
		  menu.add(0, 1, 0,  "清空");   
		  menu.add(0, 2, 0,  "首选");  
		}   
			    
			public boolean onContextItemSelected(MenuItem item) {   
//			  AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();   
			  switch (item.getItemId()) {   
			  case 0:   
				  delete();
			    return true;   
			  case 1:   
				  cleanAll(); 
			    return true;  
			  case 2:   
				  setPreferred(); 
			    return true;  
			  default:   
			   return super.onContextItemSelected(item);   
			  }   
			}
	
	
	
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    }
	
	@Override
	protected void init(int type) {
		r = new Runnable() {
			@SuppressWarnings({ "rawtypes", "unchecked" })
			public void run() {
				keys.clear();
				values.clear();
				String myFundIdsStr = StockPreference.getCustNo(AccountManage.this);
				if (myFundIdsStr != null){
					myFundInfos = myFundIdsStr.split(",");
					for(int i=0,count=myFundInfos.length; i<count; i++){
						String fundInfo = myFundInfos[i];
						if(null != fundInfo && fundInfo.indexOf("&")!=-1 ){
							fundInfo = fundInfo.substring(0,fundInfo.indexOf("&"));
						}
						//String[] raw = fundInfo.split("\\|");
						keys.add("");
						values.add(fundInfo);
					}
				}
				Set  set   =   new   HashSet(); 
				for(int   i=0;i <values.size();i++) 
				{ 
					set.add(values.get(i)); 
				} 
				values = new ArrayList<String>(set);

				mHandler.sendMessage(mHandler.obtainMessage());
			}
		};
		new Thread(r).start();
	}
	
	@Override
	protected void handlerData() {
		adapter = new AccountManageAdapter(AccountManage.this, (String[])values.toArray(new String[values.size()]), R.layout.zr_trade_account_list_items);
		listView.setAdapter(adapter);
		
		//hiddenProgressToolBar();
		
	}
	
	/**
	 * 删除某一个存储账号
	 */
	private void delete() {
		if(currentRowId == -1)
			return;
		new AlertDialog.Builder(this)
		.setTitle("删除账号提示")
		.setMessage("您确定要删除已选中的账号吗？")
		.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						StringBuffer sb = new StringBuffer();
						for(int i=0, size=myFundInfos.length; i<size; i++){
							if(i == currentRowId)
								continue;
							sb.append(myFundInfos[i] + ",");
						}
						ActivityUtil.saveCustNoPreference(AccountManage.this, "myCustNos", sb.toString());
						
						String preferredFund = StockPreference.getPreferredCustNo(AccountManage.this);
						if(preferredFund.equals(myFundInfos[currentRowId]))
							StockPreference.setPreferredCustNo(AccountManage.this, "");//删除登录首选账号
						init(1);
						currentRowId = -1;
						//setBtnStatus();
					}
				})
        .setNegativeButton("取消", //设置“取消”按钮
                new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    	
                    }
                })
		.show(); 
	}
	
	/**
	 * 设置登录首选账号
	 */
	private void setPreferred() {
		if(currentRowId == -1)
			return;
		String fundInfo = myFundInfos[currentRowId];
		StockPreference.setPreferredCustNo(this, fundInfo);
		toast("成功设置登录首选账号！");
	}
	
	/**
	 * 清空存储账号
	 */
	private void cleanAll() {
		new AlertDialog.Builder(this)
		.setTitle("清空账号提示")
		.setMessage("您确定要清空已保存的账号吗？")
		.setPositiveButton(R.string.alert_dialog_ok,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						ActivityUtil.savePreference(AccountManage.this, "myCustNos", "");//清空账号
						ActivityUtil.savePreference(AccountManage.this, "preferredFund", "");//清空登录首选账号
						init(1);
					}
				})
        .setNegativeButton("取消", //设置“取消”按钮
                new DialogInterface.OnClickListener() 
                {
                    public void onClick(DialogInterface dialog, int whichButton)
                    {
                    	//点击"取消"按钮之后退出程序
                    	finish();
                    }
                })
		.show(); 
	}
	
	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}
	@Override
	protected void onResume() {
		super.onResume();
		initPopupWindow();
	}
}
