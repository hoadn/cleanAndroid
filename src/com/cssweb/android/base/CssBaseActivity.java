package com.cssweb.android.base;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.Spannable;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.common.DateTool;
import com.cssweb.android.main.MainActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.QueryStock;
import com.cssweb.android.service.ZixunService;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.util.CssStock;

public abstract class CssBaseActivity extends BaseActivity {
	
	protected TextView[] btxt;
	
	protected int activityKind;
	protected String activityTtile;
	protected CssStock cssStock;
	
	private final int[] barid = {R.id.zr_tool_btn1, R.id.zr_tool_btn2, R.id.zr_tool_btn3, R.id.zr_tool_btn4, R.id.zr_tool_btn5, R.id.zr_tool_btn6 };
	
	protected JSONObject quoteData = null;
	
	protected Handler mHandler = new MessageHandler();
	protected Runnable r;
	protected ProgressDialog m_pDialog;
	protected ProgressBar proBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//requestWindowFeature(1);
	}
	
	protected void onOption() {
		openOptionsMenu();
	}
	
	protected void initToolBar(String[] bars, int[] bartag) {
		int tlen = bars.length;
		btxt = new TextView[tlen];
		for(int i=0; i<tlen; i++) {
			btxt[i] = (TextView) findViewById(barid[i]);
			btxt[i].setText(bars[i]);
			if (null==bars[i] || bars[i].equals("")){
				btxt[i].setEnabled(false);
			}
			//btxt[i].setEnabled(true);
			btxt[i].setTextColor(getResources().getColor(R.color.zr_white));
			btxt[i].setOnClickListener(toolbarClick);
			btxt[i].setVisibility(View.VISIBLE);
			btxt[i].setTag(bartag[i]);
			
		}
	}
	
	protected void initToolBar(int[] bars, int[] bartag) {
		int tlen = bars.length;
		Button localButton = null;
		for(int i=0; i<tlen; i++) {
			localButton = (Button) findViewById(barid[i]);
			localButton.setEnabled(true);
			localButton.setOnClickListener(toolbarClick);
			localButton.setVisibility(View.VISIBLE);
			localButton.setBackgroundResource(bars[i]);
			localButton.setTag(bartag[i]);
		}
	}
	
	public void initToolBar(String barname) {
		TextView localTextView = (TextView) findViewById(R.id.zr_tool_btn1);
		localTextView.setText(barname);
		localTextView.setEnabled(true);
		localTextView.setTextColor(getResources().getColor(R.color.zr_white));
		localTextView.setVisibility(1);
	}
	protected void initToolBar(String[] bars, String position) {
		int tlen = bars.length;
		btxt = new TextView[tlen];
		for(int i=0; i<tlen; i++) {
			if(i ==0){
				btxt[i] = (TextView) findViewById(barid[i]);
				btxt[i].setText(bars[i]);
				//btxt[i].setEnabled(true);
				btxt[i].setTextColor(getResources().getColor(R.color.zr_white));
				btxt[i].setOnClickListener(toolbarClick);
				btxt[i].setVisibility(View.VISIBLE);
				btxt[i].setTag(0);
			}else {
				btxt[i] = (TextView) findViewById(barid[i]);
				btxt[i].setText(bars[i]);
				btxt[i].setEnabled(false);
				btxt[i].setTextColor(getResources().getColor(R.color.zr_white));
				btxt[i].setOnClickListener(toolbarClick);
				btxt[i].setVisibility(View.VISIBLE);
			}
			
		}
	}
	
	protected void setToolBar() {
		//避免快速点击造成多次请求
//		if(timetips!=0&&DateTool.getLongTime()-timetips<Global.CLICK_RESPONSE_TIME) {
//			return;
//		}
		LinearLayout l = (LinearLayout) findViewById(R.id.zr_toolbarlayout2);
		l.setVisibility(View.GONE);
		/*int count  = l.getChildCount();
		for (int i=0; i< count ; i++){			//禁用点击事件
			TextView textView = (TextView) l.getChildAt(i);
			textView.setEnabled(false);
		}*/
		
		LinearLayout r = (LinearLayout) findViewById(R.id.zr_progressbar);
		r.setVisibility(View.VISIBLE);
		
		proBar = (ProgressBar) findViewById(android.R.id.progress);
		proBar.setVisibility(View.VISIBLE);
		
		TextView btn1 = (TextView) findViewById(R.id.zr_tool_btn7);
		btn1.setVisibility(View.VISIBLE);
		btn1.setTextColor(getResources().getColor(R.color.zr_white));
		btn1.setText(textToInt(R.string.progress_ask));
		
		TextView btn2 = (TextView) findViewById(R.id.zr_tool_btn8);
		btn2.setVisibility(View.VISIBLE);
		btn2.setTextColor(getResources().getColor(R.color.zr_white));
		btn2.setText(textToInt(R.string.progress_cancel));
		btn2.setTag(-1);
		btn2.setOnClickListener(toolbarClick);
		init(1);
	}
	
	protected boolean isRefreshTime() {
		if(DateTool.isRefreshTime())
			return true;
		else 
			return false;
	}
	
	protected void hiddenProgressToolBar() {
		if(proBar!=null)
			proBar.setVisibility(View.GONE);
		LinearLayout l = (LinearLayout) findViewById(R.id.zr_toolbarlayout2);
		l.setVisibility(View.VISIBLE);
		
		LinearLayout r = (LinearLayout) findViewById(R.id.zr_progressbar);
		r.setVisibility(View.GONE);
	}
    
    protected void setToolBar(int b1, boolean flag1, int color) {
    	btxt[b1].setEnabled(flag1);
    	btxt[b1].setTextColor(getResources().getColor(color));
    }
	
	protected void hiddenToolBar() {
		LinearLayout l = (LinearLayout) findViewById(R.id.zr_toolBar);
		l.setVisibility(View.GONE);
	}
	
	public boolean onKeyDownOkPress(View paramView) {
		return false;
	}
	
	public void RefreshTitle(CssStock cssStock) {
		if(cssStock!=null) {
			this.cssStock = cssStock;
			RefreshTitle(cssStock.getStkcode(), cssStock.getStkname());
		}
	}
	
	public void RefreshTitle(final String title1, final String title2) {
		Runnable r = new Runnable() {
			public void run() {
				setTitleText(title1, title2);
			}
		};
		runOnUiThread(r);
	}
	
	protected void setTitleText(final String title) {
		this.midText.setText(title);
		this.midText.setTextColor(getResources().getColor(R.color.zr_qianhuan));
		if(title.length()>10)
			this.midText.setTextSize(15);
	}
	
	private void setTitleText(String title1, String title2) {
		StringBuffer sb = new StringBuffer();
		sb.append(title2);
		sb.append(" ");
		sb.append(title1);
		this.midText.setText(sb.toString(), TextView.BufferType.SPANNABLE);
		this.midText.setTextSize(18);
		String str1 = title2;
		int i1 = str1.length();
		if(i1>0) {
			Spannable localSpannable1 = (Spannable) this.midText.getText();
			int i2 = getResources().getColor(R.color.zr_yellow);
			ForegroundColorSpan localForegroundColorSpan = new ForegroundColorSpan(i2);
			int i3 = localSpannable1.length();
			localSpannable1.setSpan(localForegroundColorSpan, i1, i3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		}
	}
	
	protected void setFullScreen() {
		requestWindowFeature(1);
		getWindow().setFlags(1024,1024);
	}
	
	protected void openProgress() {
		m_pDialog = new ProgressDialog(this);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//m_pDialog.setTitle(R.string.progress_dialog_about);
		m_pDialog.setMessage(getResources().getString(R.string.zr_rt_dialog_message));
		m_pDialog.setIndeterminate(false);
		m_pDialog.show();
	}
	
	protected void showProgress() {
		openProgress();
		init(1);
	}
	
	protected void showProgress(int tradeFuncType) {
		openProgress();
		init(tradeFuncType);
	}
	
	protected void hiddenProgress() {
		if(m_pDialog!=null)
			m_pDialog.cancel();
	}
	
	protected void RefreshUI() {
		
	}
	
	protected void searchStock() {
		Intent localIntent = new Intent();
		localIntent.setClass(this, QueryStock.class);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
		localIntent.putExtra("menuid", activityKind);
		startActivity(localIntent);
	}
	
	protected void init(final int type) {
		//this.mLock = true;
	}
	
	protected void cancelThread() {
		this.mLock = false;
	}
	
	/**
	 * 处理数据
	 */
	protected void handlerData() {
		
	}
	
	protected void backIndex() {
		Intent intent = new Intent();   
		intent.setClass(CssBaseActivity.this, MainActivity.class);   
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);  
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.mMenu = menu;
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return super.onOptionsItemSelected(item);
	}
	
	@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		//防止泄露窗口（activity 关闭,可能dialog没有关闭）
		if(m_pDialog!=null)
			m_pDialog.dismiss();
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}
	
	/**
	 * 信息提示
	 * @param msg
	 */
	protected void toast(String msg) {
		Toast.makeText(CssBaseActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	protected void toast(int msg) {
		Toast.makeText(CssBaseActivity.this, msg, Toast.LENGTH_SHORT).show();
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_MENU && event.getRepeatCount() == 0)
			onOption();
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 统一处理JSONOBJECT格式的数据更新UI
	 * @author HUJUN
	 *
	 */
	protected class MessageHandler extends Handler {
		
        public MessageHandler() {
        	
		}
        
        public MessageHandler(Looper looper) {
        	super(looper);
		}

		@Override
        public void handleMessage(Message msg) {
        	 switch (msg.what) {
	        	 case 0:
	             	 handlerData();
	                 break;
	        	 case 8:
        			Runnable runable = new Runnable() {
        				public void run() {
        					hiddenProgressToolBar();
        				}
        			};
        			runOnUiThread(runable);
	        		 break;
	             default:
	                 super.handleMessage(msg);
        	 }
        }
    }
	
	protected void getUserLevel() {
		new AsyncTask<Void, Void, Boolean>() {
			@Override
			protected Boolean doInBackground(Void... arg0) {
				try {
					int level = ZixunService.getUserLevel(TradeUser.getInstance().getUserType(), TradeUser.getInstance().getCustid());
					if(level!=0) {
						TradeUser.getInstance().setUserLevel(level);
					}
					else {
						return Boolean.FALSE;
					}
				} catch (JSONException e) {
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
			protected void onPostExecute(Boolean result) {
				if (result != Boolean.TRUE) {
					ActivityUtil.clearTradeRecord();
					toast("获取用户等级失败,请重试!");
					hiddenProgress();
					return;
				}
				swinActivity();
			}
		}.execute();
	}
	
	protected void swinActivity() {
		
	}
}
