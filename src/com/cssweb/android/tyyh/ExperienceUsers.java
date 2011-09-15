package com.cssweb.android.tyyh;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.cssweb.android.base.CssBaseActivity;
import com.cssweb.android.common.AES;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.share.StockPreference;
import com.cssweb.android.trade.service.TradeService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.LoginInfo;
import com.cssweb.android.util.ActivityUtil;

public class ExperienceUsers extends CssBaseActivity {
	private static final String DEBUG_TAG = "ExperienceUsers";
	private Button loginbutton;
	private AutoCompleteTextView zrtyyhid;
	private EditText zrjypass;
	private ImageView loginTyyhIdImg;// 账号输入框中的箭头
	private ImageView loginPwdDelImg;// 账号输入框中的箭头
	private PopupWindow mPop;// 点击账号里面的小箭头 弹出的对话框
	private ListView listAccount;

	private String[] myTyyhInfos;
	private String[] preferredTyyhInfo;
	private List<String> tyyhIds = new ArrayList<String>();
	private AccountListAdapter adapterAccountList;

	private Button findpswButton;
	private Button khButton;
	private Button sqtykButton;

	// 是否保存账号
	private boolean isSaved = false;
	// 保存账号按钮
	private CheckBox cbx0;

	@Override
	public void onCreate(Bundle paramBundle) {
		super.onCreate(paramBundle);
		setContentView(R.layout.njzq_tyyh_login);

		initTitle(R.drawable.njzq_title_left_back, 0, "体验用户登录");
		loginbutton = (Button) findViewById(R.id.tyyhsurebutton);
		zrtyyhid = (AutoCompleteTextView) findViewById(R.id.tyyhid);
		zrjypass = (EditText) findViewById(R.id.tyyhpwd);

		findpswButton = (Button) findViewById(R.id.buttonpwd);
		khButton = (Button) findViewById(R.id.buttonkaihu);
		sqtykButton = (Button) findViewById(R.id.buttonshen);

		findpswButton.setOnClickListener(listener);
		khButton.setOnClickListener(listener);
		sqtykButton.setOnClickListener(listener);

		zrjypass.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
		});
		loginbutton.setOnClickListener(myShowProgreeBar);
		cbx0 = (CheckBox) findViewById(R.id.tyyhcheckbox);
		cbx0.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isSaved) {
					isSaved = false;
				} else {
					isSaved = true;
				}
			}
		});
		loginTyyhIdImg = (ImageView) findViewById(R.id.tyyhIdImg);
		initPopMenu();
		// 如果点击账号后面的图片，弹出提示框，用来列出 保存的所有账号信息
		loginTyyhIdImg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				if (mPop.isShowing()) {
					mPop.dismiss();
				} else {
					if (v instanceof ImageButton) {
						ImageButton img = (ImageButton) findViewById(R.id.tyyhIdImg);
						mPop.showAsDropDown(img);
					}
					mPop.showAsDropDown(v, -100, -5);
				}
			}
		});
		loginPwdDelImg = (ImageView) findViewById(R.id.tyyhloginPwdDelImg);
		// 点击按钮后面的删除按钮 ，就把当前保存的按钮设置为空
		loginPwdDelImg.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				zrjypass.setText("");
			}
		});

		init();
	}

	private View.OnClickListener listener = new View.OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.buttonpwd:
				FairyUI.switchToWnd(Global.NJZQ_TYYH_FIND_PASSWORD,
						ExperienceUsers.this);
				break;

			case R.id.buttonkaihu:
				FairyUI
						.switchToWnd(Global.NJZQ_JLP_YYKHTAG,
								ExperienceUsers.this);
				break;

			case R.id.buttonshen:
				FairyUI.switchToWnd(Global.NJZQ_SQTYK, ExperienceUsers.this);
				break;
			default:
				break;
			}
		}
	};

	protected void textChanged(Editable s) {
		Editable localDEditable = zrjypass.getText();
		if (localDEditable == s) {
			String str = zrjypass.getText().toString().trim();
			if (str.length() > 0) {
				loginPwdDelImg.setVisibility(View.VISIBLE);
			} else {
				loginPwdDelImg.setVisibility(View.GONE);
			}
		}
	}

	Button.OnClickListener myShowProgreeBar = new Button.OnClickListener() {
		public void onClick(View arg0) {
			String _tyyhid = zrtyyhid.getText().toString().trim();
			if (_tyyhid == null || _tyyhid.equals("")) {
				toast("卡号不能为空！");
				return;
			}

			String _pwd = zrjypass.getText().toString().trim();
			if (_pwd == null || _pwd.equals("")) {
				toast("密码不能为空！");
				return;
			}
			showProgress();
		}
	};

	protected void init(final int type) {
		new Thread() {
			public void run() {
				try {
					TradeUser.getInstance().setIsSafe("0");
					String tyyhId = AES.encrypt(zrtyyhid.getText().toString()
							.trim(), TradeUtil.g_pubKey);
					String tyyhPwd = AES.encrypt(zrjypass.getText().toString()
							.trim(), TradeUtil.g_pubKey);

					quoteData = TradeService.tyyhLogin(tyyhId, tyyhPwd);

					Log.i(DEBUG_TAG, "体验用户登录返回数据:" + quoteData);

					mHandler.sendEmptyMessage(0);

				} catch (JSONException e) {
					e.printStackTrace();
					hiddenProgress();
				} catch (Exception e) {
					e.printStackTrace();
					hiddenProgress();
				}
			}
		}.start();
	}

	public void handlerData() {
		JSONObject jsonObject = quoteData;
		try {
			String res = TradeUtil.checkResult(jsonObject);
			if (res != null) {
				if (res.equals("-1")) {
					Toast.makeText(this, "网络错误", Toast.LENGTH_LONG).show();
					LoginInfo.getInstance().setLoginErrorDesc("网络错误");
				} else {
					toast(res);
					LoginInfo.getInstance().setLoginErrorDesc(res);
				}
				LoginInfo.getInstance().setLoginState("0");
				hiddenProgress();
				return;
			} else { // 登录成功 保存体验卡卡号
				JSONArray jArr = (JSONArray) jsonObject.getJSONArray("info");
				JSONObject jA = (JSONObject) jArr.get(0);
				LoginInfo.getInstance().setRealName(jA.getString("userName"));
				LoginInfo.getInstance().setOrgDesc(jA.getString("ext1"));
				LoginInfo.getInstance().setOrgID(jA.getString("yybCode"));
				LoginInfo.getInstance().setLoginState("1");
				LoginInfo.getInstance().setSystemCode("JLP_ANDROID");
				LoginInfo.getInstance().setLoginID(zrtyyhid.getText().toString().trim());
				LoginInfo.getInstance().setUserType("2");
				LoginInfo.getInstance().setLoginType("17");
				if (isSaved) {
					StockPreference.saveTyyhId(ExperienceUsers.this, zrtyyhid
							.getText().toString().trim());
				}

				TradeUser.getInstance().setLoginType(3);
				TradeUser.getInstance().setUserType("browse");
				TradeUser.getInstance().setCustid(zrtyyhid.getText().toString().trim());
				TradeUser.getInstance().setFundid(zrtyyhid.getText().toString());
				
				getUserLevel();
			}
			hiddenProgress();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	protected void swinActivity() {
		FairyUI.switchToWnd(-2, ExperienceUsers.this);
		ExperienceUsers.this.finish();
	}
	

	private void init() {
		getSaveTyyhs();
		getPreferred();
		setBranches(null);

	}

	private void setBranches(String areaId) {
		if (preferredTyyhInfo != null) {
			int tyyhPosition = -1;
			for (int i = 0, size = myTyyhInfos.length; i < size; i++) {
				String tyyhInfo = myTyyhInfos[i];
				String[] raw = tyyhInfo.split("\\|");
				if (preferredTyyhInfo[0].equals(raw[0]))
					tyyhPosition = i;
			}
			if (tyyhPosition != -1 && tyyhPosition != 0)
				setTyyhs(tyyhPosition);
			else
				setTyyhs(-1);
		} else {
			setTyyhs(-1);
		}
	}

	private void getSaveTyyhs() {
		// 读取保存的用户账号
		String myTyyhIdsStr = StockPreference.getTyyhId(this);
		if (myTyyhIdsStr != null) {
			isSaved = true;
			cbx0.setChecked(true);
			myTyyhInfos = myTyyhIdsStr.split(",");
			zrtyyhid.setText(myTyyhInfos[myTyyhInfos.length - 1]);
		}
	}

	private void getPreferred() {
		String str = StockPreference.getPreferredTyyh(this);
		if (str != null && !str.equals("")) {
			preferredTyyhInfo = str.split("\\|");
		}
	}

	private void initPopMenu() {
		View popContent = getLayoutInflater().inflate(R.layout.zr_login_popub,
				null, false);
		listAccount = (ListView) popContent.findViewById(R.id.listAccount);
		listAccount.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				TextView txt = (TextView) arg0.getChildAt(position)
						.findViewById(R.id.TextView01);
				zrtyyhid.setText(txt.getText().toString().trim());
				if (mPop != null && mPop.isShowing())
					mPop.dismiss();
			}
		});

		mPop = new PopupWindow(popContent, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		mPop.setOutsideTouchable(true);

		mPop.getContentView().setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (mPop != null)
					mPop.dismiss();
			}
		});
	}

	private void setTyyhs(int position) {
		if (isSaved && myTyyhInfos != null) {
			tyyhIds.clear();
			for (int i = 0; i < myTyyhInfos.length; i++) {
				String tyyhInfo = myTyyhInfos[i];
				String[] raw = tyyhInfo.split("\\|");
				tyyhIds.add(raw[0]);// 目前不过滤各个营业部下的账号
			}
			adapterAccountList = new AccountListAdapter(this,
					R.layout.zr_login_account_list_items, (String[]) tyyhIds
							.toArray(new String[tyyhIds.size()]));
			listAccount.setAdapter(adapterAccountList);
			if (position != -1) {
				zrtyyhid.setDropDownAnchor(position);
				zrtyyhid.setText(tyyhIds.get(zrtyyhid.getDropDownAnchor()));
			} else {
				if (tyyhIds.size() > 0)
					zrtyyhid.setText(tyyhIds.get(0));
				else
					zrtyyhid.setText("");
			}
		}
	}

	class AccountListAdapter extends BaseAdapter {

		private LayoutInflater mInflater;
		private int mResource;
		private String[] account;

		public AccountListAdapter(Context context, int paramInt,
				String[] accounts) {
			this.mInflater = LayoutInflater.from(context);
			this.mResource = paramInt;
			this.account = accounts;
		}

		public int getCount() {
			return account.length;
		}

		public Object getItem(int position) {
			return Integer.valueOf(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(final int position, View convertView,
				ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				convertView = mInflater.inflate(mResource, null);
				holder = new ViewHolder();
				holder.textView = (TextView) convertView
						.findViewById(R.id.TextView01);
				holder.imageView = (ImageView) convertView
						.findViewById(R.id.ImageView01);
				holder.imageView
						.setOnClickListener(new ImageView.OnClickListener() {

							public void onClick(View v) {
								deleteAccount(position);
								if (mPop != null && mPop.isShowing())
									mPop.dismiss();
							}
						});

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.textView.setText(account[position]);

			return convertView;
		}

		/**
		 * 用于存储每行的视图
		 * 
		 * @author Administrator
		 * 
		 */
		class ViewHolder {
			TextView textView;
			ImageView imageView;
		}

		/**
		 * 删除账号处理
		 */
		private void deleteAccount(final int position) {
			new AlertDialog.Builder(ExperienceUsers.this)
					.setTitle("删除账号提示")
					.setMessage("您确定从列表中删除该账户：\n\t\t\t\t\t" + account[position])
					.setPositiveButton(R.string.alert_dialog_ok,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									ArrayList<String> newAccounts = new ArrayList<String>();
									StringBuffer sb = new StringBuffer();
									for (int i = 0, size = myTyyhInfos.length; i < size; i++) {
										if (i == position)
											continue;
										newAccounts.add(myTyyhInfos[i]);
										sb.append(myTyyhInfos[i] + ",");
									}
									ActivityUtil.savePreference(
											ExperienceUsers.this, "myTyyhIds",
											sb.toString());

									String preferredTyyh = StockPreference
											.getPreferredTyyh(ExperienceUsers.this);
									if (preferredTyyh
											.equals(myTyyhInfos[position])) {
										StockPreference.setPreferredTyyh(
												ExperienceUsers.this, "");// 删除登录首选账号
									}
									myTyyhInfos = newAccounts
											.toArray(new String[newAccounts
													.size()]);
									setTyyhs(-1);
									toast("成功删除！");
								}
							}).setNegativeButton("取消", // 设置“取消”按钮
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int whichButton) {
									// 点击"取消"按钮之后退出程序
									// finish();
								}
							}).show();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		changeBG();
		initPopupWindow();
	}
	@Override
	protected void onDestroy() {
		LoginInfo.getInstance().setUserLevel(String.valueOf(TradeUser.getInstance().getUserLevel()));
		super.onDestroy();
	}
}
