package com.cssweb.android.trade.fund;

import java.util.HashMap;
import java.util.Map;

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
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.service.FundService;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;

/**
 * 开放式基金交易（认购/申购/赎回）
 * 
 * @author wangsheng
 * 
 */
public class FundTrading extends CssKeyboardBase {
	private static final String DEBUG_TAG = "FundTrading";
	// '0'－'正常开放，'1'－'认购时期，'2'－'发行成功，'3'－'发行失败，
	// '4'－'基金停止交易', '5'－'停止申购，'6'－'停止赎回，
	// '7'－'权益登记，'8'－'红利发放，'9'－'基金封闭，'a'－'基金终止'

	private static Map<String, String> FUND_STATE = new HashMap<String, String>() {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		{
			put("0", "正常开放");
			put("1", "认购时期");
			put("2", "发行成功");
			put("3", "发行失败");
			put("4", "基金停止交易");
			put("5", "停止申购");
			put("6", "停止赎回");
			put("7", "权益登记");
			put("8", "红利发放");
			put("9", "基金封闭");
			put("a", "基金终止");

		}
	};
	private TextView lblTradeType;
	private EditText fundCode;
	private TextView lblNumber;
	private EditText number;
	private TextView fundName;
	private TextView fundState;
	private TextView fundNav;
	private TextView avaiAsset;

	private LinearLayout layout0;
	private LinearLayout layout1;
	private LinearLayout layout2;
	private LinearLayout layout3;
	private LinearLayout layout4;
	private TextView avaiNumber;
	private Spinner spinnerFlag;

	private int type; // 用于区分是认购还是申购

	private int trdid;
	private String ofcodeRisk;
	private String tacode;
	private String shareclass;
	private String taacc = "";
	private String amt = "";
	
	private Boolean flag = false;
	private Thread thread = null;
	private ServiceControl sc = null;//行为分析
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.zr_trade_open_fund);
		sc = new ServiceControl(this);//行为分析初始化
		type = getIntent().getExtras().getInt("type");
		//Log.d("type_java", "type_java  " + type);
		String[] toolbarNames = { Global.TOOLBAR_QUEDING,"","","","",Global.TOOLBAR_BACK};
		initToolBar(toolbarNames, Global.BAR_TAG);
		setBtnStatus();
		LinearLayout localLinearLayout = (LinearLayout) findViewById(R.id.LinearLayout01);
		localLinearLayout.setOnFocusChangeListener(setOnEditFocusListener);
		this.m_vklayout = localLinearLayout;
		View localView1 = this.m_vklayout;
		localView1.setOnFocusChangeListener(setOnEditFocusListener);
		View localView2 = this.m_vklayout;
		localView2.setOnClickListener(setOnEditClickListener);

		lblTradeType = (TextView) findViewById(R.id.zr_openjj_label_title);
		lblTradeType.setVisibility(View.GONE);
		fundCode = (EditText) findViewById(R.id.FundCode);
		lblNumber = (TextView) findViewById(R.id.zr_openjj_label_number);
		number = (EditText) findViewById(R.id.Number);
		fundName = (TextView) findViewById(R.id.FundName);
		fundState = (TextView) findViewById(R.id.FundState);
		fundNav = (TextView) findViewById(R.id.FundNav);
		avaiAsset = (TextView) findViewById(R.id.AvaiAsset);
		layout0 = (LinearLayout) findViewById(R.id.LinearLayout07);
		layout1 = (LinearLayout) findViewById(R.id.LinearLayout09);
		layout2 = (LinearLayout) findViewById(R.id.LinearLayout10);
		layout3 = (LinearLayout) findViewById(R.id.LinearLayout06);
		layout4 = (LinearLayout) findViewById(R.id.LinearLayout08);
		
		setTitle();
		
		String ofcode = "";
		Boolean flag = true;
		try {
			ofcode = getIntent().getExtras().getString("ofcode");
			if (ofcode != null && !"".equals(ofcode))
				flag = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
		fundCode.setInputType(InputType.TYPE_NULL);
		fundCode.setFocusable(flag);
		fundCode.setTag("STOCK");
		fundCode.setOnClickListener(setOnEditClickListener);
		fundCode.setOnFocusChangeListener(setOnEditFocusListener);
		fundCode.addTextChangedListener(new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {
				textChanged(s);
			}
		});
		if (ofcode != null || !"".equals(ofcode)) {
			fundCode.setText(ofcode);
		}
		number.setInputType(InputType.TYPE_NULL);
		number.setFocusable(flag);
		number.setTag("NUMDOT");
		number.setOnClickListener(setOnEditClickListener);
		number.setOnFocusChangeListener(setOnEditFocusListener);
	}

	protected void textChanged(Editable s) {
		Editable localDEditable = fundCode.getText();
		if (localDEditable == s) {
			String str = fundCode.getText().toString().trim();
			if (str.length() == 6) {
				onHideKeyBoard();
				setToolBar();
			}
		}
	}

	protected void init(final int type)  {

		r = new Runnable() {
			public void run() {
				try {
					String filedate = ActivityUtil.getPreference(FundTrading.this, "openFundInfoDate", "");
					if (!(filedate).equals(DateTool.getToday())) { // 如果时间不匹配，重新到柜台获获取
						quoteData = FundService.getFundInfo();
					} else {
						String jsonObject = CssIniFile.loadIni(FundTrading.this, 4, "fundInfo");
						if (null != jsonObject && !jsonObject.equals("")) {
							quoteData = new JSONObject(jsonObject);
						}
					}
				} catch (JSONException e) {
					Log.e(DEBUG_TAG, e.toString());
				}
				mHandler.sendEmptyMessage(0);
			}
		};
		thread = new Thread(r);
		thread.start();
	}

	private void setTitle() {
		if (type == 0) {
			sc.currentSite("wtjy_cwjj_jjrg");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金认购");
			setTitle("基金认购");
			trdid = 24020;
			lblTradeType.setText("基金认购");
			lblNumber.setText("认购金额");
		} else if (type == 1) {
			sc.currentSite("wtjy_cwjj_jjsg");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金申购");
			setTitle("基金申购");
			trdid = 24022;
			lblTradeType.setText("基金申购");
			lblNumber.setText("申购金额");
		} else if (type == 2) {
			sc.currentSite("wtjy_cwjj_jjsh");
			initTitle(R.drawable.njzq_title_left_back, 0, "基金赎回");
			setTitle("基金赎回");
			trdid = 24024;
			lblTradeType.setText("基金赎回");
			lblNumber.setText("赎回份额");
			avaiNumber = (TextView) findViewById(R.id.AvaiNumber);
			spinnerFlag = (Spinner) findViewById(R.id.RedempFlag);
			layout0.setVisibility(View.GONE);
			layout1.setVisibility(View.VISIBLE);
			layout2.setVisibility(View.VISIBLE);
			layout3.setVisibility(View.GONE);
			layout4.setBackgroundDrawable(getResources().getDrawable(R.drawable.zrtablegroupcellcenter));
			layout1.setBackgroundDrawable(getResources().getDrawable(R.drawable.zrtablegroupcellcenter));
			String[] flag = { "赎回", "顺延" };
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_item, flag);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spinnerFlag.setAdapter(adapter);
			int h = R.drawable.forminput;
			Resources localResources = getResources();
			Drawable localDrawable = null;
			localDrawable = localResources.getDrawable(h);
			int spinnerheight = localDrawable.getIntrinsicHeight()-4;
//			Log.e("<<<<<<<<<<<<<<<<<<<eeeeeeeeeeeeeeeeeeeeeeeeeee>>>>>>>>>>>>>>", String.valueOf(spinnerheight));
			LinearLayout.LayoutParams linearParams1 = (LinearLayout.LayoutParams) spinnerFlag.getLayoutParams();//获取bank控件的当前布局
			linearParams1.height=spinnerheight;//对该控件的布局参数做修改
			spinnerFlag.setLayoutParams(linearParams1);

		}
	}

	@Override
	protected void handlerData() {
		JSONObject jsonObject = quoteData;
		try {
			String res = TradeUtil.checkResult(jsonObject);
			if (res != null) {
				if (res.equals("-1"))
					Toast.makeText(FundTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
				else
					toast(res);
				hiddenProgressToolBar();
				return;
			} else {
				JSONArray jarr = (JSONArray) jsonObject.getJSONArray("item");
				flag = false;
				for (int i = 0; i < jarr.length() - 1; i++) {
					JSONObject jsonobj = jarr.getJSONObject(i);
					String cacheofcode = jsonobj.getString("FID_JJDM");
					if (fundCode.getText().toString().trim().equals(cacheofcode)) {
						String ofname = jsonobj.getString("FID_JJJC");
						String fundstate = jsonobj.getString("FID_JYZT");
						//
//						Set entries = FUND_STATE.entrySet();
//						if (entries != null) {
//							Iterator iterator = entries.iterator();
//							while (iterator.hasNext()) {
//								Map.Entry entry = (Entry) iterator.next();
//								Object key = entry.getKey();
//								Object value = entry.getValue();
//							}
//						}
						String fundstate1=FUND_STATE.get(fundstate);
					//	Log.d("FID_JYZT_javone", "FID_JYZT_javone" + fundstate);
						fundName.setText(ofname);
						fundState.setText(fundstate1);
						fundNav.setText(TradeUtil.formatNum(
								jsonobj.getString("FID_JJJZ"), 3));

						tacode = jsonobj.getString("FID_TADM");
						shareclass = jsonobj.getString("FID_SFFS");
						ofcodeRisk = jsonobj.getString("FID_JJFXDJ");
						flag = true;
						break;
					}
				}
				if ("".equals(fundName.getText().toString().trim())) {
					toast("基金代码不存在！");
					reset();
					hiddenProgressToolBar();
					return;
				}
				if (type == 2) {
					JSONObject fundPositionJson = FundService.getFundPosition();
					String res1 = TradeUtil.checkResult(fundPositionJson);
					if (res1 != null) {
						if (res1.equals("-1"))
							Toast.makeText(FundTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
						else
							toast(res);
						hiddenProgressToolBar();
						return;
					}
					JSONArray jArr = (JSONArray) fundPositionJson.getJSONArray("item");
					for (int i = 0; i < jArr.length() - 1; i++) {
						JSONObject jsonobj = jArr.getJSONObject(i);
						String cacheofcode = jsonobj.getString("FID_JJDM");
						if (fundCode.getText().toString().trim()
								.equals(cacheofcode)) {
							avaiNumber.setText(TradeUtil.formatNum(
									jsonobj.getString("FID_KYSL"), 2));
							break;
						}
					}
					if ("".equals(avaiNumber.getText().toString())) {
						avaiNumber.setText("0");
					}
				}
				avaiAsset.setText(TradeUtil.getFundavl());
			}
			if (!flag) {
				reset();
			}
			hiddenProgressToolBar();

		} catch (JSONException e) {
			Log.e(DEBUG_TAG, e.toString());
			showDialog(0);
		}
	}

	private void submit() {
		if (fundCode.getText().toString().trim() == null || "".equals(fundCode.getText().toString().trim())) {
			toast("请输入基金代码！");
			return;
		}else if (fundCode.getText().toString().trim().length()!= 6||!TradeUtil.checkNumber(fundCode.getText().toString().trim(), false)) {
			toast("请输入正确的基金代码!");
			return;
		}
		amt = number.getText().toString();
		if(amt==null || amt.equals("")){
			toast("请输入"+lblNumber.getText()+"!");
			return;
		}else if(!TradeUtil.checkNumber(amt, false)){
			toast("请输入正确的"+lblNumber.getText()+"!");
			return;
		}
		String mess = "委托类别: " + lblTradeType.getText() + "\n";
		mess += "基金代码: " + fundCode.getText() + "\n";
		mess += lblNumber.getText() + ": " + amt + "\n";
		mess += "委托方向: " + TradeUtil.dealFundTrdid(trdid) + "\n";

		try {
			taacc = new TradeUtil().getFundAccount(tacode, FundTrading.this);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		if (Integer.parseInt(ofcodeRisk) > Integer.parseInt(TradeUser
				.getInstance().getRiskLevel())) {
			mess += "您所购置的基金品种超出您的风险承受能力，请确认是否继续？\n";
		}
		new AlertDialog.Builder(FundTrading.this)
				.setTitle("委托提示")
				.setMessage(mess)
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						CharSequence title = "正在与服务器通讯握手...";
						CharSequence message = "正在往服务器提交数据...";

						ProgressDialog myDialog = ProgressDialog.show(
								FundTrading.this, title, message, true);
						String ywdm = "";
						String flag = "";
						if (type == 0)
							ywdm = "020";
						if (type == 1)
							ywdm = "022";
						if (type == 2) {
							ywdm = "024";
							flag = String.valueOf(spinnerFlag
									.getSelectedItemPosition());
						}
						try {

							JSONObject quoteData = FundService.getFundBuySale(
									fundCode.getText().toString().trim(),
									tacode, taacc, shareclass, amt, ywdm, flag);
							String res = TradeUtil.checkResult(quoteData);
							if (res != null) {
								if (res.equals("-1"))
									Toast.makeText(FundTrading.this, "网络连接异常！请检查您的网络是否可用。", Toast.LENGTH_LONG).show();
								else
									toast(res);
								myDialog.dismiss();
								return;
							} else {
								JSONArray jArr = (JSONArray) quoteData.getJSONArray("item");
								JSONObject j = (JSONObject) jArr.get(0);
								toast(j.getString("FID_MESSAGE")+",委托编号："+j.getString("FID_WTH"));
							}
						} catch (JSONException e) {
							Log.e(DEBUG_TAG, e.toString());
						}

						myDialog.dismiss();
						reset();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						//
					}
				}).show();
	}

	private void cancel() {
		finish();
	}

	@Override
	protected void onPause() {
		mHandler.removeCallbacks(r);
		super.onPause();
	}

	protected void initTitle(int resid1, int resid2, String str) {
		super.initTitle(resid1, resid2, str);
		changeTitleBg();
	}

	private void reset() {
		fundCode.setText("");
		number.setText("");
		fundName.setText("");
		fundState.setText("");
		fundNav.setText("");
		avaiAsset.setText("");
		if (type == 2)
			avaiNumber.setText("");
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
