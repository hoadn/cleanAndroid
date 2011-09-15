package com.cssweb.android.web;

import java.util.Calendar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.JlpActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.sms.SMSJHActivity;
import com.cssweb.android.trade.login.LoginActivity;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.tyyh.ExperienceUsers;
import com.cssweb.android.util.ActivityUtil;
import com.cssweb.android.video.VideoPlayer;

public class JHMethod {
	private Handler mHandler;
	private Activity context;;
	private WebView webview;
	
	AlertDialog dlg ;
	
	protected ProgressDialog m_pDialog;

	public JHMethod(Handler mHandler, Activity context, WebView webview) {
		this.mHandler = mHandler;
		this.context = context;
		this.webview = webview;
	}

	public void showPopup() {
		mHandler.post(new Runnable() {
			public void run() {
				showMenu();
			}
		});
	}

	public void showMenu() {
		mHandler.post(new Runnable() {
			public void run() {
				showMenuImpl();
			}
		});
	}

	public void back() {
		Log.i("tag", "back : ");
		mHandler.post(new Runnable() {
			public void run() {
				context.finish();
			}
		});
	}
	
	
	public void startload(){
		m_pDialog = new ProgressDialog(context);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		//m_pDialog.setTitle(R.string.progress_dialog_about);
		m_pDialog.setMessage("正在加载中...");
		m_pDialog.setIndeterminate(false);
		m_pDialog.show();
		
	}
	
	public void endload(){
		if(m_pDialog!=null)
			m_pDialog.cancel();
	}

	public void open_video(final String url) {
		//Intent openVideoIntent = new Intent(context, CustomMediaPlayer.class);
		Intent openVideoIntent = new Intent(context, VideoPlayer.class);
		Bundle bundle = new Bundle();
		//String url1 = "http://wangjun.easymorse.com/wp-content/video/mp4/tuzi.mp4";
		bundle.putString("url", url);
		openVideoIntent.putExtras(bundle);
		context.startActivity(openVideoIntent);
		/*
		 * mHandler.post(new Runnable() { public void run() { try { Uri uri =
		 * Uri.parse(url); Intent videoIntent = new Intent(Intent.ACTION_VIEW,
		 * uri); videoIntent.setType("video/*");
		 * context.startActivity(videoIntent); } catch (Exception e) {
		 * e.printStackTrace(); } } });
		 */

	}

	public void open_date(final String name) {
		mHandler.post(new Runnable() {
			public void run() {

				final Calendar calendar = Calendar.getInstance();
				final int year = calendar.get(Calendar.YEAR);
				final int month = calendar.get(Calendar.MONTH);
				final int day = calendar.get(Calendar.DAY_OF_MONTH);

				new DatePickerDialog(context,
						new DatePickerDialog.OnDateSetListener() {

							public void onDateSet(DatePicker view, int year,
									int monthOfYear, int dayOfMonth) {

								String time = String.valueOf(year)
										+ (monthOfYear < 9 ? "0"
												+ (monthOfYear + 1) : String
												.valueOf(monthOfYear + 1))
										+ (dayOfMonth < 10 ? "0" + dayOfMonth
												: String.valueOf(dayOfMonth));
								webview.loadUrl("javascript:setdate('" + name
										+ "','" + time + "')");

							}
						}, year, month, day).show();
			}

		});
	}

	public void showMenuImpl() {
		
	}

	public void open_pdf(String url, String title, String ispdfFlag) {
		Log.i("tag","" + ispdfFlag);
		openpdf(url, title, true);
	}
	public void open_pdf(String url, String title) {
		openpdf(url, title, false);
	}
	
	public void openpdf(String url, String title,boolean flag){
		Log.i("tag","  openpdf : " + url);
		Intent openPdfIntent = new Intent(context, OpenPdfDisplay.class);
		Bundle bundle = new Bundle();
		bundle.putString("url", url);
		bundle.putString("title", title);
		bundle.putBoolean("ispdfFlag", flag);
		openPdfIntent.putExtras(bundle);
		context.startActivity(openPdfIntent);
	}

	public void open_call(final String phoneNum) {
		mHandler.post(new Runnable() {
			public void run() {
				Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
						+ phoneNum));
				context.startActivity(intent);
			}
		});
	}

	public void open_map(final String name, final String address,
			final String lat, final String lng) {
//		Intent mapIntent = new Intent(context,MapViewActivity.class);
//		Bundle bundle = new Bundle();
//		bundle.putDouble("latitude", Double.parseDouble(lat));
//		bundle.putDouble("longitude", Double.parseDouble(lng));
//		bundle.putString("title", name);
//		mapIntent.putExtras(bundle);
//		context.startActivity(mapIntent);
		
		
		
		mHandler.post(new Runnable() {
			public void run() {
				// TODO
				try {
//					Uri uri = Uri.parse("geo:" + lat + "," + lng);
					Uri uri = Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q="+lat+","+lng);
					
//					Uri uri = Uri.parse("http://ditu.google.cn/maps?hl=zh&mrt=loc&q=" + name);
					Intent mapIntent = new Intent(Intent.ACTION_VIEW, uri);
					context.startActivity(mapIntent);
					
				} catch (Exception e) {
					Toast.makeText(context, "打开地 图出错", Toast.LENGTH_SHORT)
							.show();
					e.printStackTrace();
				}
			}
		});
	}

	// public abstract void open_keyboard(final String name);
	public void open_keyboard(final String name) {
		Log.i("tag", "open_keyboard : " + name);

		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(context.getCurrentFocus(),
				InputMethodManager.SHOW_IMPLICIT);
		// View view = context.getCurrentFocus();
		// if (view != null){
		// imm.showSoftInput(view, 0); //显示软键盘
		// }

		// imm.showSoftInputFromInputMethod(context.getWindow().getCurrentFocus().getWindowToken(),
		// InputMethodManager.SHOW_IMPLICIT);

		// imm.toggleSoftInputFromWindow(context.getWindow().getCurrentFocus().getWindowToken(),
		// InputMethodManager.SHOW_FORCED, 0);
		/*
		 * mHandler.post(new Runnable() { public void run() { // TODO
		 * InputMethodManager imm =
		 * (InputMethodManager)context.getSystemService(
		 * Context.INPUT_METHOD_SERVICE); // if (imm.isActive()) { //
		 * imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,
		 * InputMethodManager.HIDE_IMPLICIT_ONLY); //
		 * imm.showSoftInputFromInputMethod
		 * (context.getCurrentFocus().getWindowToken(), flags); // }else { // //
		 * } imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
		 * 
		 * // imm.showSoftInput(context.getCurrentFocus(),
		 * InputMethodManager.SHOW_FORCED); } });
		 */
	}

	public void open_zxg() {// 由网页跳转到自选股界面
		mHandler.post(new Runnable() {
			public void run() {
				FairyUI.switchToWnd(Global.QUOTE_USERSTK, null, null, null,
						context);
			}
		});
	}

	public void open_position() {// 由网页跳转到持仓界面
		mHandler.post(new Runnable() {
			public void run() {
				FairyUI.switchToWnd(Global.NJZQ_WTJY, 7, "", "", context);
			}
		});
	}

	public void open_zsyyt() {// 由网页跳转到 掌上营业厅
		mHandler.post(new Runnable() {
			public void run() {
				// TODO
				Intent intent = new Intent(context, JlpActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("menu_id", Global.NJZQ_ZSYYT);
				intent.putExtras(bundle);
				context.startActivity(intent);
			}
		});
	}

	public void mncglogin(final String name, final String type) {

		mHandler.post(new Runnable() {
			public void run() {
				if ("1".equals(type) || "2".equals(type) || "4".equals(type)) {
					TradeUser.getInstance()
							.setLoginType(Integer.parseInt(type));
				}
			}
		});
	}

	public void back_out() {
		Log.i("tag", "back_out : ");
		mHandler.post(new Runnable() {
			public void run() {
				ActivityUtil.clearTradeRecord();
				context.finish();
			}
		});
	}

	public void loginURL(final int loginType, final String url) {
		Log.i("tag", "loginURL : " + loginType + "  :   " + url);
		mHandler.post(new Runnable() {
			public void run() {
				if (loginType == 1 || loginType == 2 || loginType == 0) {// 
					SharedPreferences sharedPreferences = context
							.getSharedPreferences("mobile",
									Context.MODE_PRIVATE);
					boolean flag = sharedPreferences.getBoolean("jhSuccess",
							false);
					Bundle bundle = new Bundle();
					if (flag) {
						if (loginType == 1) {
							Intent jyloginUIActivity = new Intent(context,
									LoginActivity.class);

							bundle.putInt("loginType", loginType);
							bundle.putString("url", url);
							jyloginUIActivity.putExtras(bundle);
							context.startActivity(jyloginUIActivity);
						} else if (loginType == 2 || loginType == 0) {
							Intent serverLoginUIActivity = new Intent(context,
									LoginActivity.class);

							bundle.putInt("loginType", loginType);
							bundle.putString("url", url);
							serverLoginUIActivity.putExtras(bundle);
							context.startActivity(serverLoginUIActivity);
						}
					} else {
						Intent jhIntent = new Intent();
						jhIntent.putExtra("url", url);
						jhIntent.setClass(context, SMSJHActivity.class);
						context.startActivity(jhIntent);
					}

				} else if (loginType == 3) {
					Intent tyLoginUIActivity = new Intent(context,
							ExperienceUsers.class);
					tyLoginUIActivity.putExtra("url", url);
					context.startActivity(tyLoginUIActivity);
				} else {
					Intent loginUIActivity = new Intent(context,
							LoginActivity.class);
					loginUIActivity.putExtra("menu_id",
							Global.NJZQ_WEBVIEW_LOGIN);
					context.startActivity(loginUIActivity);
				}
				context.finish();
			}
		});
	}

	public void open_href(final String url) {
		Log.i("tag", "open_href : ");
		mHandler.post(new Runnable() {
			public void run() {
				Intent openHrefIntent = new Intent(context,
						WebViewDisplay.class);
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				bundle.putInt("pos", Global.OPENHREF);
				bundle.putInt("position", 0);
				openHrefIntent.putExtras(bundle);
				context.startActivity(openHrefIntent);
			}
		});
	}

	public void open_href_blank(final String url) {
		Log.i("tag", "open_href_blank : ");
		mHandler.post(new Runnable() {
			public void run() {
				Intent openHrefIntent = new Intent(context,
						OpenHrefDisplay.class);
				Bundle bundle = new Bundle();
				bundle.putString("url", url);
				openHrefIntent.putExtras(bundle);
				context.startActivity(openHrefIntent);
			}
		});
	}

	public void get_location() {
		mHandler.post(new Runnable() {
			public void run() {
				LocationManager lm = (LocationManager) context
						.getSystemService(Context.LOCATION_SERVICE);

				Location loc = lm
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER); // 获取location信息
				if (loc == null) {
					loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				}
				if (loc != null) {
					webview.loadUrl("javascript:setlocationfromiphone('"
							+ loc.getLatitude() + "','" + loc.getLongitude()
							+ "')");
				} else {
					Toast.makeText(context, "无法定位当前位置 ", Toast.LENGTH_SHORT)
							.show();
				}
			}
		});
	}

	public void back_login(final int loginType) {// 1 交易密码；2 服务密码；3 体验用户
		mHandler.post(new Runnable() {
			public void run() { // 1 交易密码；2 服务密码；3 体验用户
				if (loginType == 1 || loginType == 2) {// 页面未登陆, 返回到客户端的登陆页
					if (loginType == 1) {
						Intent jyloginUIActivity = new Intent(context,
								LoginActivity.class);
						jyloginUIActivity.putExtra("menu_id",
								Global.NJZQ_ZXHD_EGHT);
						context.startActivity(jyloginUIActivity);
					} else if (loginType == 2) {
						Intent serverLoginUIActivity = new Intent(context,
								LoginActivity.class);
						serverLoginUIActivity.putExtra("menu_id",
								Global.NJZQ_ZXHD_EGHT);
						context.startActivity(serverLoginUIActivity);
					}
				} else if (loginType == 3) {
					Intent tyLoginUIActivity = new Intent(context,
							ExperienceUsers.class);
					context.startActivity(tyLoginUIActivity);
				}
			}
		});
	}

	public void back_index_page(final String loginType) {// 回退 需跳到九宫格界面
		int tmp = 1;
		try {
			tmp = Integer.parseInt(loginType);
		} catch (NumberFormatException e) {
			tmp = 1;
			e.printStackTrace();
		}
		Log.i("tag", "back_index_page : tmp :  " + tmp);
		 // 1 交易密码；2 服务密码；3 体验用户
				if(tmp == 1 || tmp == 2){
					Intent loginUIActivity = new Intent(context,
							LoginActivity.class);
					Bundle bundle = new Bundle();
					bundle.putInt("loginType", tmp);
					bundle.putBoolean("isChangeBtn", true);
					loginUIActivity.putExtras(bundle);
					context.startActivity(loginUIActivity);
					
					context.finish();
				}else {
					Intent tyLoginUIActivity = new Intent(context,
							ExperienceUsers.class);
					context.startActivity(tyLoginUIActivity);
				}
			
	}
	
	public void fxpc(final String msg, final String type, final String operation) {// 风险测评
		mHandler.post(new Runnable() {
			public void run() {
				String zy = "";
				if ("success".equals(type)) {
					if ("0".equals(TradeUser.getInstance().getRiskLevel())) {
						zy = "修改客户基本信息,未定义 修改为 "+TradeUtil.dealFundRiskType(msg)+" 型";
					}else {
						zy = "修改客户基本信息,"+TradeUtil.dealFundRiskType(TradeUser.getInstance().getRiskLevel())+" 型修改为 "+TradeUtil.dealFundRiskType(msg)+" 型";
					}
					TradeUser.getInstance().setRiskLevel(msg);
				}else {
					zy = "风险等级设置失败！";
				}
				setFundRiskLevel(msg,zy);
			}
		});

	}
	
	public void setFundRiskLevel(String risk_level,String zy){};

	public void confirm(final String title, final String content,
			final String key) {
		mHandler.post(new Runnable() {
			public void run() {
				AlertDialog.Builder b = new AlertDialog.Builder(context);
				b.setTitle(title);
				b.setMessage(content);
				b.setPositiveButton(android.R.string.ok,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								webview.loadUrl("javascript:confirmTrue('"
										+ key + "')");
							}
						});
				b.setNegativeButton(android.R.string.cancel,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								webview.loadUrl("javascript:confirmFalse('"
										+ key + "')");
							}
						});
				b.setCancelable(false);
				b.create();
				b.show();
			}
		});
	}

	public void alert(final String title, final String content,
			final String buttonTitle) {
		mHandler.post(new Runnable() {
			public void run() {
				AlertDialog.Builder b = new AlertDialog.Builder(context);
				b.setTitle(title);
				b.setMessage(content);
				b.setPositiveButton(buttonTitle,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				b.setCancelable(false);
				b.create();
				b.show();

			}
		});
	}
	
	public void showDiv(final String string1, final String string2,
			final String string3) {
		Log.i("tag", "showDiv :  showDiv");
		mHandler.post(new Runnable() {
			public void run() {
				dlg = new AlertDialog.Builder(context).create();
				dlg.show();
				LayoutInflater factory = LayoutInflater.from(context);
				View view = factory.inflate(R.layout.custom_dialog_with_three_button, null);
				TextView textView1 = (TextView) view.findViewById(R.id.string1);
				TextView textView2 = (TextView) view.findViewById(R.id.string2);
				TextView textView3 = (TextView) view.findViewById(R.id.string3);
				dlg.getWindow().setContentView(view);
				
				textView1.setText("  1个月     ");
				textView2.setText("  3个月     ");
				textView3.setText("  6个月     ");

				textView1.setOnClickListener(listener);
				textView2.setOnClickListener(listener);
				textView3.setOnClickListener(listener);
			}
		});
	}
	
	
	private View.OnClickListener listener = new View.OnClickListener() {
		public void onClick(View v) {//exchange(int)
			switch (v.getId()) {
			case R.id.string1:
				if(dlg!= null && dlg.isShowing()){
					dlg.dismiss();
					webview.loadUrl("javascript:exchange('"
							+ 1 + "')");
				}
				break;
			case R.id.string2:
				if(dlg!= null && dlg.isShowing()){
					dlg.dismiss();
					webview.loadUrl("javascript:exchange('"
							+ 3 + "')");
				}
				break;
			case R.id.string3:
				if(dlg!= null && dlg.isShowing()){
					dlg.dismiss();
					webview.loadUrl("javascript:exchange('"
							+ 6 + "')");
				}
				break;
			default:
				break;
			}
		}
	};

	public void alert1(final String title, final String content,
			final String buttonTitle) {
		mHandler.post(new Runnable() {
			public void run() {
				AlertDialog.Builder b = new AlertDialog.Builder(context);
				b.setTitle(title);
				b.setMessage(content);
				b.setPositiveButton(buttonTitle,
						new AlertDialog.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						});
				b.setCancelable(false);
				b.create();
				b.show();

			}
		});
	}

	public void alert2(String title) {
		AlertDialog.Builder b = new AlertDialog.Builder(context);
		b.setTitle("alert2");
		b.setMessage(title);
		b.setPositiveButton(android.R.string.ok,
				new AlertDialog.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					}
				});
		b.setCancelable(false);
		b.create();
		b.show();
	}

	public boolean isAndroid() {
		return true;
	}
	
	public int returnAlarmRecorde(){
		return ActivityUtil.ALARM_RECORED;
	}
}
