/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuoteDialogActivity.java 上午11:13:15 2010-11-25
 */
package com.cssweb.android.base;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.StockRadar;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.share.StockPreference;
import com.cssweb.android.sms.SMSJHActivity;
import com.cssweb.android.trade.login.LoginActivity;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.web.WebViewDisplay;
import com.cssweb.quote.util.NameRule;

/**
 * 用来处理menu菜单
 *
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public abstract class DialogActivity extends CssKeyboardBase {

	protected static final int DIALOG_YES_MESSAGE = 0;
	protected static final int DIALOG_YES_NO_MESSAGE = 1;
	protected static final int DIALOG_CLEAR_STOCK = 2;
//	protected static final int DIALOG_LIST = 3;
//	protected static final int DIALOG_PROGRESS = 4;
//	protected static final int DIALOG_SINGLE_CHOICE = 5;
//	protected static final int DIALOG_MULTIPLE_CHOICE = 6;
//	protected static final int DIALOG_TEXT_ENTRY = 7;
//	protected static final int DIALOG_MULTIPLE_CHOICE_CURSOR = 8;
	
	protected int msg_ask = R.string.network_error;
	protected String msg_info = null;
	
	protected String[] zhouqi;
	protected String[] pingzh;
	protected String[] xuanx;
	protected String[] paiming;
	protected String[] desc;
	
	protected Spinner timeSpinner;
	protected Spinner stateSpinner;
	
	private TextView timeTextView;
	private TextView stateTextView;
	protected AlertDialog dlg ;
	private MenuInflater inflater = null;
	protected static final int DIALOG1_KEY = 9;
	private ProgressDialog chichangDialog =null ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onOption() {
		super.onOption();
		mMenu.performIdentifierAction(R.id.zr_hq_menu, 0);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    Log.i("当前方法", this.getClass().getName() + "."+ new Exception().getStackTrace()[0].getMethodName()+ "()");

		if(activityKind==Global.QUOTE_USERSTK || activityKind==Global.QUOTE_WARNING ||
			activityKind==Global.QUOTE_DAPAN || activityKind==Global.QUOTE_FENLEI ||
			activityKind==Global.QUOTE_PAIMING || activityKind==Global.QUOTE_STOCK ||
			activityKind==Global.QUOTE_BOND || activityKind==Global.QUOTE_MONETARY ||
			activityKind==Global.QUOTE_MIX || activityKind==Global.SUN_PRIVATE ||
			activityKind==Global.QUOTE_FENSHI || activityKind==Global.QUOTE_KLINE ||
			activityKind==Global.HK_MAINBOARD || activityKind==Global.HK_CYB ||
			activityKind==Global.ZJS || activityKind==Global.SDZ || activityKind == Global.QUOTE_HSZS) {
	        inflater = getMenuInflater();
	        inflater.inflate(R.menu.hqtop_menu, menu);
		}
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
			case R.id.zr_hq_mystock:
				if(!TradeUtil.checkUserLogin()) {
					FairyUI.switchToWnd(Global.QUOTE_USERSTK, Global.QUOTE_USERSTK, DialogActivity.this);
				}
				else {
					FairyUI.switchToWnd(Global.QUOTE_USERSTK, null, null, null, DialogActivity.this);
				}
				break;
			case R.id.zr_hq_addstock:
				if(cssStock==null) {
					msg_info = "请选择证券！";
					openDialog(msg_info);
				}
				else {
					if(!TradeUtil.checkUserLogin()) {
						FairyUI.switchToWnd(activityKind, activityKind, DialogActivity.this);
					}
					else {
						msg_info = StockPreference.shareStock(NameRule.getExchange(cssStock.getMarket()), cssStock.getStkcode(), cssStock.getStkname(), DialogActivity.this);
						openDialog(msg_info);
					}
				}
				break;
			case R.id.zr_hq_quickbuy:
				buyStock();
				break;
			case R.id.zr_hq_quicksale:
				saleStock();
				break;
			case R.id.zr_hq_revocation:
				cancelStock();
				break;
			case R.id.zr_hq_gugle://打开股歌链接传股票代码不包含市场
				if(null==cssStock){
					toast("请选择证券！");
					return false;
				}
				Intent localIntent = new Intent();
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_GG);
				localIntent.putExtra("stockcode", cssStock.getStkcode());
    			localIntent.setClass(DialogActivity.this, WebViewDisplay.class);
    			startActivity(localIntent);
				break;
			case R.id.zr_hq_fundtrade:
				if(TradeUser.getInstance().getLoginType()==1&&TradeUtil.checkUserLogin()) {
					FairyUI.switchToWnd(Global.NJZQ_WTJY_TWO, 2, null, null, DialogActivity.this);
				}
				else {
					gotoLogin(Global.NJZQ_WTJY_TWO, Global.NJZQ_WTJY_TWO);
				}
				break;
			case R.id.zr_index:
				backIndex();
				break;
			case R.id.zr_hq_mystock_set:
				initStockBar();
				break;
			case R.id.zr_hq_remove:
				delMenuStock();
				break;
			case R.id.zr_hq_queryfund: //基金查询
				queryFund();
				break;
			
			case R.id.zr_hq_chicang://增加持仓到自选股
				 if(TradeUser.getInstance().getLoginType()==1&&TradeUtil.checkUserLogin()) {
					 new AlertDialog.Builder(DialogActivity.this) 
			            .setTitle(getResources().getString(R.string.alert_tip)) 
			            .setMessage(getResources().getString(R.string.importdata)) 
			            .setPositiveButton(getResources().getString(R.string.alert_dialog_ok) , 
			                    new DialogInterface.OnClickListener() { 
			                        public void onClick(DialogInterface dialog,  int whichButton) { 
			        					 addChiCang();
			                        } 
			             })
			             .setNegativeButton(getResources().getString(R.string.alert_dialog_cancel), 
			                    new DialogInterface.OnClickListener() { 
			                        public void onClick(DialogInterface dialog, int whichButton) { 
			                        	
			                        } 
			             }) 
			             .show();
					 
					
				 }
				 else {
					 gotoLogin(activityKind, activityKind);
				 }
				 break;
            case R.id.zr_hq_boom://信息地雷
                System.out.println("****" + "信息地雷");
                if(null==cssStock){
					toast("请选择证券！");
					return false;
				}
                Intent intent2StockRadar = new Intent(DialogActivity.this, StockRadar.class);
                Bundle StockRadarextras = new Bundle();
                StockRadarextras.putString("stockcode", cssStock.getStkcode());
                StockRadarextras.putString("stockname", cssStock.getStkname());
//                StockRadarextras.putInt("exchange", cssStock.getExchange());
                StockRadarextras.putString("exchange", cssStock.getMarket());
                intent2StockRadar.putExtras(StockRadarextras);
                startActivity(intent2StockRadar);
                break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		if(activityKind == Global.QUOTE_USERSTK) {
			MenuItem localMenuItem1 = menu.findItem(R.id.zr_hq_mystock);
			MenuItem localMenuItem2 = menu.findItem(R.id.zr_hq_addstock);
			MenuItem localMenuItem3 = menu.findItem(R.id.zr_hq_fundtrade);
			MenuItem localMenuItem4 = menu.findItem(R.id.zr_hq_gugle);
			MenuItem localMenuItem5 = menu.findItem(R.id.zr_hq_chicang);
			if(localMenuItem1!=null)
				localMenuItem1.setVisible(false);
			if(localMenuItem2!=null)
				localMenuItem2.setVisible(false);
			if(localMenuItem3!=null)
				localMenuItem3.setVisible(false);
			if(localMenuItem4!=null)
				localMenuItem4.setVisible(false);
			if(localMenuItem5!=null)
				localMenuItem5.setVisible(true);
		}
		else if(activityKind==Global.QUOTE_STOCK || activityKind==Global.QUOTE_BOND || 
				activityKind==Global.QUOTE_MONETARY || activityKind==Global.QUOTE_MIX) {
			MenuItem localMenuItem1 = menu.findItem(R.id.zr_hq_mystock_set);
			MenuItem localMenuItem2 = menu.findItem(R.id.zr_hq_remove);
			MenuItem localMenuItem3 = menu.findItem(R.id.zr_hq_quickbuy);
			MenuItem localMenuItem4 = menu.findItem(R.id.zr_hq_quicksale);
			MenuItem localMenuItem5 = menu.findItem(R.id.zr_hq_revocation);
			MenuItem localMenuItem6 = menu.findItem(R.id.zr_hq_remove);
			
			MenuItem localMenuItem7 = menu.findItem(R.id.zr_hq_queryfund);
			
			if(localMenuItem1!=null)
				localMenuItem1.setVisible(false);
			if(localMenuItem2!=null)
				localMenuItem2.setVisible(false);
			if(localMenuItem3!=null)
				localMenuItem3.setVisible(false);
			if(localMenuItem4!=null)
				localMenuItem4.setVisible(false);
			if(localMenuItem5!=null)
				localMenuItem5.setVisible(false);
			if(localMenuItem6!=null)
				localMenuItem6.setVisible(false);
			if(localMenuItem7!=null){
				localMenuItem7.setVisible(true);
			}
				
		}
		else if(activityKind==Global.SUN_PRIVATE) {
			MenuItem localMenuItem1 = menu.findItem(R.id.zr_hq_mystock_set);
			MenuItem localMenuItem2 = menu.findItem(R.id.zr_hq_remove);
			MenuItem localMenuItem3 = menu.findItem(R.id.zr_hq_quickbuy);
			MenuItem localMenuItem4 = menu.findItem(R.id.zr_hq_quicksale);
			MenuItem localMenuItem5 = menu.findItem(R.id.zr_hq_revocation);
			MenuItem localMenuItem6 = menu.findItem(R.id.zr_hq_remove);
			MenuItem localMenuItem7 = menu.findItem(R.id.zr_hq_addstock);
			MenuItem localMenuItem8 = menu.findItem(R.id.zr_hq_gugle);
			
			MenuItem localMenuItem9 = menu.findItem(R.id.zr_hq_queryfund);
			
			if(localMenuItem1!=null)
				localMenuItem1.setVisible(false);
			if(localMenuItem2!=null)
				localMenuItem2.setVisible(false);
			if(localMenuItem3!=null)
				localMenuItem3.setVisible(false);
			if(localMenuItem4!=null)
				localMenuItem4.setVisible(false);
			if(localMenuItem5!=null)
				localMenuItem5.setVisible(false);
			if(localMenuItem6!=null)
				localMenuItem6.setVisible(false);
			if(localMenuItem7!=null)
				localMenuItem7.setVisible(false);
			if(localMenuItem8!=null)
				localMenuItem8.setVisible(false);
			if(localMenuItem9!=null)
				localMenuItem9.setVisible(true);
		}
		else if(activityKind==Global.QUOTE_DAPAN || activityKind==Global.QUOTE_HSZS ||
				activityKind==Global.HK_CYB || activityKind==Global.HK_MAINBOARD ||
				activityKind==Global.ZJS || activityKind==Global.SDZ) {
			MenuItem localMenuItem1 = menu.findItem(R.id.zr_hq_mystock_set);
			MenuItem localMenuItem2 = menu.findItem(R.id.zr_hq_remove);
			MenuItem localMenuItem3 = menu.findItem(R.id.zr_hq_fundtrade);
			MenuItem localMenuItem4 = menu.findItem(R.id.zr_hq_gugle);
			MenuItem localMenuItem5 = menu.findItem(R.id.zr_hq_quickbuy);
			MenuItem localMenuItem6 = menu.findItem(R.id.zr_hq_quicksale);
			MenuItem localMenuItem7 = menu.findItem(R.id.zr_hq_revocation);
			if(localMenuItem1!=null)
				localMenuItem1.setVisible(false);
			if(localMenuItem2!=null)
				localMenuItem2.setVisible(false);
			if(localMenuItem3!=null)
				localMenuItem3.setVisible(false);
			if(localMenuItem4!=null)
				localMenuItem4.setVisible(false);
			if(localMenuItem5!=null)
				localMenuItem5.setVisible(false);
			if(localMenuItem6!=null)
				localMenuItem6.setVisible(false);
			if(localMenuItem7!=null)
				localMenuItem7.setVisible(false);
		}
		else {
			MenuItem localMenuItem1 = menu.findItem(R.id.zr_hq_mystock_set);
			MenuItem localMenuItem2 = menu.findItem(R.id.zr_hq_remove);
			MenuItem localMenuItem3 = menu.findItem(R.id.zr_hq_fundtrade);
			//MenuItem localMenuItem4 = menu.findItem(R.id.zr_hq_gugle);
			if(localMenuItem1!=null)
				localMenuItem1.setVisible(false);
			if(localMenuItem2!=null)
				localMenuItem2.setVisible(false);
			if(localMenuItem3!=null)
				localMenuItem3.setVisible(false);
			//if(localMenuItem4!=null)
			//	localMenuItem4.setVisible(false);
		}
		return super.onPrepareOptionsMenu(menu);
	}
    
    protected void initStockBar() {
    	hiddenOrDisplayStockBar(View.VISIBLE);
    	
    	TextView stkBar1 = (TextView) findViewById(R.id.njzq_userstockbtn1);
    	TextView stkBar2 = (TextView) findViewById(R.id.njzq_userstockbtn2);
    	TextView stkBar3 = (TextView) findViewById(R.id.njzq_userstockbtn3);
    	TextView stkBar4 = (TextView) findViewById(R.id.njzq_userstockbtn4);
    	TextView stkBar5 = (TextView) findViewById(R.id.njzq_userstockbtn5);
    	TextView stkBar6 = (TextView) findViewById(R.id.njzq_userstockbtn6);
		
    	stkBar1.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar1.setOnClickListener(toolbarClick);
    	stkBar1.setVisibility(View.VISIBLE);
    	stkBar1.setTag(11);
    	
    	stkBar2.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar2.setOnClickListener(toolbarClick);
    	stkBar2.setVisibility(View.VISIBLE);
    	stkBar2.setTag(12);
    	
    	stkBar3.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar3.setOnClickListener(toolbarClick);
    	stkBar3.setVisibility(View.VISIBLE);
    	stkBar3.setTag(13);
    	
    	stkBar4.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar4.setOnClickListener(toolbarClick);
    	stkBar4.setVisibility(View.VISIBLE);
    	stkBar4.setTag(14);
    	
    	stkBar5.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar5.setOnClickListener(toolbarClick);
    	stkBar5.setVisibility(View.VISIBLE);
    	stkBar5.setTag(15);
    	
    	stkBar6.setTextColor(getResources().getColor(R.color.zr_white));
    	stkBar6.setOnClickListener(toolbarClick);
    	stkBar6.setVisibility(View.VISIBLE);
    	stkBar6.setTag(16);
    }
    
    protected void hiddenOrDisplayStockBar(int k) {
    	LinearLayout l = (LinearLayout)findViewById(R.id.zr_userstkbarlayout);
    	l.setVisibility(k);
    }
	
    protected void clearStock() {
		String res = StockPreference.clearStock(DialogActivity.this);
		if(res!=null) {
			openDialog(res);
			setToolBar();
		}
	}
    
    protected void delMenuStock() {
    	
    }
    
    protected void openDialog() {
    	
    }
    /**
     * 基金查询
     */
    protected void queryFund(){
    	
    }
    
    protected void openDialog(String msg) {
    	new AlertDialog.Builder(DialogActivity.this)
        .setIcon(android.R.drawable.ic_dialog_alert)
        .setTitle(R.string.alert_dialog_about)
		.setMessage(msg)
        .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	openDialog();
            }
        })
        .show();
    }

	@Override
	protected Dialog onCreateDialog(int id) {
        switch (id) {
        case DIALOG_YES_MESSAGE:
            return new AlertDialog.Builder(DialogActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.alert_dialog_about)
        		.setMessage((msg_info==null)?textToInt(msg_ask):msg_info)
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                })
                .create();
        case DIALOG_YES_NO_MESSAGE:
            return new AlertDialog.Builder(DialogActivity.this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle(R.string.alert_dialog_about)
        		.setMessage(textToInt(msg_ask))
                .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    	// 点击“确定”转向登陆框
						System.exit(0);
                    }
                })
                .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {

                    }
                })
                .create();
        case DIALOG_CLEAR_STOCK:
            return new AlertDialog.Builder(DialogActivity.this)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle(R.string.alert_dialog_about)
    		.setMessage(R.string.zr_clear_stock_message)
            .setPositiveButton(R.string.alert_dialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	clearStock();
                }
            })
            .setNegativeButton(R.string.alert_dialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {

                }
            })
            .create();
        case DIALOG1_KEY: {
        	chichangDialog = new ProgressDialog(this);
            //dialog.setTitle("Indeterminate");
        	chichangDialog.setMessage(getResources().getString(R.string.loadchicangdata));
        	chichangDialog.setIndeterminate(true);
        	chichangDialog.setCancelable(true);
            return chichangDialog;
          }
        }
        return null;
    }
	/**
	 * 增加持仓数据到自选股
	 */
	protected void addChiCang (){
		showDialog(DIALOG1_KEY);
		try {
			JSONObject  jsonData = TradeService.getStockPosition();
			if (null != jsonData){
				JSONArray jArr = jsonData.getJSONArray("item");
				int len  = jArr.length();
				if(len>0) {
					for (int i=0; i< len-1 ; i++){
						JSONObject object = (JSONObject) jArr.get(i);
						String code = object.getString("FID_ZQDM");						//证券代码
						String exchange  = object.getString("FID_JYS").toLowerCase();   //市场
						String name = object.getString("FID_ZQMC");						//名称
						StockPreference.shareStock(exchange , code ,name ,DialogActivity.this );       //增加方法
					}
					toast(R.string.importcomplete);
					showProgress();
				}
				else {
					toast(R.string.importno);
				}
			}
			else {
				toast(R.string.importerror);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			toast(R.string.importerror);
		} finally{
			if (null !=chichangDialog){
				chichangDialog.dismiss();
			}
		}
	}
	
	
	protected void buyStock() {
		if(cssStock==null) {
			msg_info = "请选择证券！";
			openDialog(msg_info);
		}
		else {
			if(TradeUser.getInstance().getLoginType()==1&&TradeUtil.checkUserLogin()) {
				String ex = cssStock.getMarket().toLowerCase();
				if(NameRule.IsTradeOrNot(ex)) {
					msg_info = "港股期货场外基金不能下单！";
					openDialog(msg_info);
					return;
				}
				int stktype = NameRule.getSecurityType(NameRule.getExchange(cssStock.getMarket()), cssStock.getStkcode());
				if(stktype == NameRule.SH_INDEX || stktype == NameRule.SZ_INDEX || 
						stktype == NameRule.SH_ZZ || stktype == NameRule.SH_KFSJJ || 
						stktype == NameRule.SZ_OPEN_FUND || stktype == NameRule.OTHER_OPEN_FUND ) {
					msg_info = "指数和场外基金不能下单！";
					openDialog(msg_info);
				}
				else {
					FairyUI.switchToWnd(Global.NJZQ_WTJY, 1, cssStock.getStkcode(), null, DialogActivity.this);
				}
			}
			else {
				gotoLogin(activityKind, activityKind);
			}
		}
	}
	
	protected void openPopup() {
		LayoutInflater factory = LayoutInflater.from(DialogActivity.this);
		final LinearLayout sortDialogView = (LinearLayout) factory.inflate(R.layout.sort_dialog, null);

		dlg = new AlertDialog.Builder(DialogActivity.this)
				.setTitle("排序").setView(sortDialogView).setPositiveButton(getResources().getString(R.string.alert_dialog_ok),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								handleClick(true);
							}
						}).setNegativeButton(getResources().getString(R.string.alert_dialog_cancel),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
							}
						}).create();
		///dlg.show();
		
		timeSpinner = (Spinner) sortDialogView.getChildAt(1);
		stateSpinner = (Spinner) sortDialogView.getChildAt(3);
		timeTextView = (TextView) sortDialogView.getChildAt(0);
		stateTextView = (TextView) sortDialogView.getChildAt(2);
			
		if (activityKind == Global.QUOTE_PAIMING) {
			timeTextView.setText("周期");
			stateTextView.setText("排序");
			setAdapter(timeSpinner, zhouqi);
			setAdapter(stateSpinner, xuanx);
		} else if (activityKind == Global.QUOTE_FENLEI || activityKind == Global.HK_MAINBOARD || activityKind == Global.HK_CYB ) {
			timeTextView.setText("类型");
			stateTextView.setText("顺序");
			setAdapter(timeSpinner, paiming);
			setAdapter(stateSpinner, desc);
		}
	}
	
	protected void handleClick(boolean flag){};
	
	private void setAdapter(Spinner timeSpinner, String[] params) {
		ArrayAdapter<String> timeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item);
		timeAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Log.i("=================", ">>>>>>>>>>>>>>>>>>>>>>>>"+params);
		for (String param : params) {
			timeAdapter.add(param);
		}
		timeSpinner.setAdapter(timeAdapter);
	}
	
	
	
	
	protected void saleStock() {
		if(cssStock==null) {
			msg_info = "请选择证券！";
			openDialog(msg_info);
		}
		else {
			if(TradeUser.getInstance().getLoginType()==1&&TradeUtil.checkUserLogin()) {
				String ex = cssStock.getMarket().toLowerCase();
				if(NameRule.IsTradeOrNot(ex)) {
					msg_info = "港股期货场外基金不能下单！";
					openDialog(msg_info);
					return;
				}
				int stktype = NameRule.getSecurityType(NameRule.getExchange(ex), cssStock.getStkcode());
				if(stktype == NameRule.SH_INDEX || stktype == NameRule.SZ_INDEX || 
						stktype == NameRule.SH_ZZ || stktype == NameRule.SH_KFSJJ || 
						stktype == NameRule.SZ_OPEN_FUND || stktype == NameRule.OTHER_OPEN_FUND ) {
					msg_info = "指数不能下单！";
					openDialog(msg_info);
				}
				else {
					FairyUI.switchToWnd(Global.NJZQ_WTJY, 2, cssStock.getStkcode(), null, DialogActivity.this);
				}
			}
			else {
				gotoLogin(activityKind, activityKind);
			}
		}
	}
	
	protected void cancelStock() {
		if(TradeUser.getInstance().getLoginType()==1&&TradeUtil.checkUserLogin()) {
			FairyUI.switchToWnd(Global.NJZQ_WTJY, 3, null, null, DialogActivity.this);
		}
		else {
			gotoLogin(activityKind, activityKind);
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}
	
	/**
	 * 菜单需要登录的时候
	 * @param paramInt
	 * @param paramInt2
	 */
	private void gotoLogin(int paramInt, int paramInt2) {
		Intent localIntent = new Intent();
		if(FairyUI.genIsActiveIntent(paramInt, paramInt2, DialogActivity.this)) {
			localIntent.putExtra("menu_id", paramInt);
			localIntent.putExtra("isChangeBtn", true);
			localIntent.setClass(DialogActivity.this, LoginActivity.class);
			startActivity(localIntent);
		}
		else {
			localIntent.putExtra("menu_id", activityKind);
			localIntent.setClass(DialogActivity.this, SMSJHActivity.class);
			startActivity(localIntent);
		}
	}
}
