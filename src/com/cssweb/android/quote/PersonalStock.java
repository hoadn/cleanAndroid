package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ViewFlipper;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.adapter.UserStockAdapter;
import com.cssweb.android.service.ValidationService;
import com.cssweb.android.share.StockPreference;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.android.view.FinanceMini;
import com.cssweb.android.view.KlineMini;
import com.cssweb.android.view.PriceMini;
import com.cssweb.android.view.TrendView;
import com.cssweb.android.web.OpenPdfDisplay;
import com.cssweb.android.web.WebViewDisplay;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

public class PersonalStock extends FlipperActiviy {
	private final String TAG = "PersonalStock";
	
	private TrendView trendView = null;
	private PriceMini priceView = null;
	private KlineMini klineView = null;
	private FinanceMini financeView = null;
	
	private ViewFlipper viewFlipper;
	private ImageView preView;
	
	private Animation leftIn;
	private Animation leftOut;
	private Animation rightIn;
	private Animation rightOut;
	
	private UserStockAdapter adapter;
	private ListView lv;
	
    private List<CssStock> list = new ArrayList<CssStock>();
	private RequestParams requestParams;
	private int requestType = 1;
	private int currentRow = 0;
	private int pageNum = 4;
	private int from = 0, to = pageNum, maxlen = 0, actualLen = 0;
	private int type = 0;
	private String exchange;
	private String stockcode;
	private String stockname;
	private String stocktype;
	private String curexchange;
	private String curstockcode;
	private JSONObject jsonData;
	private JSONObject jsonTick;//分时线数据
	private JSONObject jsonKline;//分时线数据
	private int tickFrom = 0;//取分时线数据起始位置

	private PriceDataHandler priceHandler;
	private boolean nLock = true;
	private boolean firstComing = true;
	
	private String[] tradeMenu;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		priceHandler = new PriceDataHandler(mHandlerThread.getLooper());
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null) {
			this.requestType = bundle.getInt("requestType");
			this.exchange = bundle.getString("exchange");
			this.stockcode = bundle.getString("stockcode");
			this.stockname = bundle.getString("stockname");
			if(exchange!=null&&stockcode!=null) {
				String res = StockPreference.shareStock(this.exchange, this.stockcode, this.stockname, PersonalStock.this);
				if(res!=null)
					toast(res);
			}
		}
		this.activityKind = Global.QUOTE_USERSTK;
		requestParams = new RequestParams();
		
		tradeMenu = getResources().getStringArray(R.array.trade_selected_menu);
		
		setContentView(R.layout.zr_user_stock_list);
		
		trendView = (TrendView)findViewById(R.id.zrviewtrend);
		trendView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		klineView = (KlineMini)findViewById(R.id.zrviewkline);
		klineView.setOnTouchListener(new View.OnTouchListener() {
			
			public boolean onTouch(View v, MotionEvent event) {
				return gestureDetector.onTouchEvent(event);
			}
		});
		priceView = (PriceMini)findViewById(R.id.zrviewprice);
		financeView = (FinanceMini)findViewById(R.id.zrviewfinance);

		viewFlipper = (ViewFlipper) this.findViewById(R.id.ViewFlipper01);
    	preView = (ImageView) findViewById(R.id.previous_screen);

		leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in_layout);
		leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out_layout);
		rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in_layout);
		rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out_layout);
		
		String[] toolbarname = new String[]{ Global.TOOLBAR_MENU, 
			Global.TOOLBAR_XIADAN, Global.TOOLBAR_GONGGAO,
			Global.TOOLBAR_DIANPING, Global.TOOLBAR_QINGBAO, Global.TOOLBAR_ZHENDUAN};
		
		initTitle(R.drawable.njzq_title_left_back, 0, "自选股");
		initToolBar(toolbarname, Global.BAR_TAG);
		lv = (ListView) findViewById(R.id.zr_rt_listview);
	    lv.setOnTouchListener(this);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				if(position<actualLen) {
					FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule.getExchange(list.get(position).getMarket()), list.get(position).getStkcode(), list.get(position).getStkname(), PersonalStock.this);
				}
				return false;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				if(timetips!=0&&DateTool.getLongTime()-timetips<Global.CLICK_RESPONSE_TIME) return;
				int count = Math.min(arg0.getChildCount(), list.size());
				for(int i=0; i<count; i++) {
					if(list.get(i).getStkcode()!=null&&!list.get(i).getStkcode().equals("")) {
						if(i==arg2) {
							timetips = DateTool.getLongTime();
							currentRow = i;
							cssStock = list.get(currentRow);
							arg0.getChildAt(i).setBackgroundResource(R.drawable.zr_table_xkz);
							if(list.get(i).getZf()<0) {
								arg0.getChildAt(i).findViewById(R.id.zr_rt_col5).setBackgroundResource(R.drawable.zrreportdownbg1);
							}
							else {
								arg0.getChildAt(i).findViewById(R.id.zr_rt_col5).setBackgroundResource(R.drawable.zrreportupbg1);
							}
							initView(NameRule.getExchange(list.get(i).getMarket()), list.get(i).getStkcode(), list.get(i).getStkname());
						}
						else if(i!=arg2) {
							arg0.getChildAt(i).setBackgroundResource(R.drawable.zr_table_xk);
							if(list.get(i).getZf()<0) {
								arg0.getChildAt(i).findViewById(R.id.zr_rt_col5).setBackgroundResource(R.drawable.zrreportdownbg);
							}
							else {
								arg0.getChildAt(i).findViewById(R.id.zr_rt_col5).setBackgroundResource(R.drawable.zrreportupbg);
							}
						}
					}
				}
			}
			
		});

		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getMyStockPageSize(PersonalStock.this);

		currentRow = 0;
		from = 0;
		to = pageNum;
		
		handlerData();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	btnRight.setBackgroundResource(R.drawable.njzq_title_right_select);
    	btnRight.setTag(3);
    	changeTitleBg();
    	setTitleText("自选股");
    }
	
	protected void init(final int type) {
		this.mLock = true;
		mHandler.removeCallbacks(r);
		r = new Runnable() {
			public void run() {
				if(mLock&&nLock&&type==1) {
					String res = StockPreference.getStock(PersonalStock.this);
					if(res!=null&&!res.equals("")) {
						String[] temp = res.split(",");
						maxlen = temp.length;
						StringBuffer sb = new StringBuffer();
						//Log.i("============", from+">>>>>>>>>"+to);
						for(int i = from; i < (maxlen>to?to:maxlen); i++) {
							sb.append(temp[i]+",");
						}
						requestParams.setStocks(sb.substring(0, sb.length()-1));
						quoteData = ConnService.execute(requestParams, requestType);
						try {
							if(Utils.isHttpStatus(quoteData)) {
								list.clear();
								JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
								actualLen = jArr.length();
								for (int i = 0; i < actualLen; i++) {
									JSONArray jA = (JSONArray)jArr.get(i);
									CssStock cssStock = new CssStock();
									cssStock.setStkname(jA.getString(19));
									cssStock.setStkcode(jA.getString(18));
									cssStock.setZf(jA.getDouble(12));
									cssStock.setZjcj(jA.getDouble(1));
									cssStock.setZrsp(jA.getDouble(6));
									//显示的是涨跌值而不是昨日收盘
									cssStock.setZd(jA.getDouble(14));
									cssStock.setMarket(jA.getString(20));
									list.add(cssStock);
								}

								isNetworkError = 0;
							}
							else {
								isNetworkError = -1;
							}
							if(actualLen<pageNum) {
								list.addAll(TradeUtil.fillListToNull(actualLen, pageNum));
							}
						} catch (JSONException e) {
							isNetworkError = -2;
							Log.e(TAG, e.toString());
						}
					}
					else {
						list.clear();
					}
				}
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.postDelayed(r, Config.fenleirefresh);
			}
		};
		mHandler.post(r);
	}
	
	protected void handlerData() {
		Runnable r = new Runnable() {
			public void run() {
				if(isNetworkError<0&&firstComing) {
					firstComing = false;
					toast(R.string.load_data_error);
				}
				if(list.isEmpty()) {
					list.addAll(TradeUtil.fillListToNull(0, pageNum));
				}
				if(list.get(currentRow).getStkcode()!=null&&!list.get(currentRow).getStkcode().equals("")) {
					cssStock = list.get(currentRow);
					initView(NameRule.getExchange(list.get(currentRow).getMarket()), list.get(currentRow).getStkcode(), list.get(currentRow).getStkname());
				}
				adapter = new UserStockAdapter(PersonalStock.this, list,
						R.layout.zr_user_stock_list_item, currentRow, pageNum);
				lv.setAdapter(adapter);
				if(curexchange==null)
					hiddenProgress();
				//进度条消失
				hiddenProgressToolBar();
			}
		};
		runOnUiThread(r);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		removeHandler();
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(klineView!=null) {
			klineView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
		if(financeView!=null) {
			financeView.reCycle();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		removeHandler();
		nLock = false;
		if(trendView!=null) {
			trendView.reCycle();
		}
		if(klineView!=null) {
			klineView.reCycle();
		}
		if(priceView!=null) {
			priceView.reCycle();
		}
		if(financeView!=null) {
			financeView.reCycle();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
		showProgress();
	}
	
	protected void initView(String exchange, String stockcode, String stockname) {
		//避免在同一行上重复点，造成流量浪费
		if(exchange==null||exchange.equals("")||stockcode==null||stockcode.equals("")||exchange.equals(this.curexchange)&&stockcode.equals(this.curstockcode)) {
			hiddenProgress();
			return;
		}
		
		this.jsonData = null;
		this.jsonTick = null;//分时线数据
		this.jsonKline = null;//分时线数据
		this.tickFrom = 0;//取分时线数据起始位置
		
		this.curexchange = exchange;
		this.curstockcode = stockcode;
		
		 
		trendView.setStockInfo(exchange, stockcode, stockname);
		//trendView.init();
		
		klineView.setStockInfo(exchange, stockcode, stockname);
		klineView.setPeriod("day");
		klineView.setIndicatorType("volume");
		klineView.setMainIndicatorType("ma");
		//切换指标的时候重置状态
		klineView.resetStatus();
		//klineView.init(1);

		type = NameRule.getSecurityType(exchange, stockcode);
		stocktype = NameRule.getStockType(type);
		
		priceView.setStockInfo(exchange, stockcode, stockname, stocktype);
		financeView.setStockInfo(exchange, stockcode, stockname, type, stocktype);
		
		init(exchange, stockcode, stockname);
	}
	
	/**
	 * 向左滑动
	 */
	protected void moveColLeft() {
		if(trendView.isTrackStatus())
			return;
		if(viewFlipper.getDisplayedChild() == 0) {
	    	preView.setImageResource(R.drawable.page_arrow_22);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_23);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}
		else if(viewFlipper.getDisplayedChild() == 2) {
	    	preView.setImageResource(R.drawable.page_arrow_24);
			viewFlipper.setInAnimation(leftIn);
			viewFlipper.setOutAnimation(leftOut);
			viewFlipper.showNext();
		}

//		if(m_pDialog.isShowing())
//			openProgress();
	}
	
	/**
	 * 向右滑动
	 */
	protected void moveColRight() {
		if(trendView.isTrackStatus())
			return;
		if(viewFlipper.getDisplayedChild() == 3) {
	    	preView.setImageResource(R.drawable.page_arrow_23);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
		else if(viewFlipper.getDisplayedChild() == 2) {
	    	preView.setImageResource(R.drawable.page_arrow_22);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
		else if(viewFlipper.getDisplayedChild() == 1) {
	    	preView.setImageResource(R.drawable.page_arrow_21);
			viewFlipper.setInAnimation(rightIn);
			viewFlipper.setOutAnimation(rightOut);
			viewFlipper.showPrevious();
		}
//		if(m_pDialog.isShowing())
//			openProgress();
	}
	
	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		pageDn();
	}
	
	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		pageUp();
	}
	
	protected void addMenuStock() {
		FairyUI.switchToWnd(1, 4, "", "", PersonalStock.this);
	}

	protected void delMenuStock() {
		if(currentRow>=actualLen) 
			return;
		String res = StockPreference.delStock(NameRule.getExchange(list.get(currentRow).getMarket()), list.get(currentRow).getStkcode(), list.get(currentRow).getStkname(), PersonalStock.this);
		openDialog(res);
	}
	
	protected void openDialog() {
		this.curexchange = "";
		this.curstockcode = "";
		showProgress();
	}
	
	protected void openTrade() {
	    OnClickListener listener = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				switch ( which )
				{
				case 0:
					buyStock();
					break;
				case 1:
					saleStock();
					break;
				}
			}
	    };
	    new AlertDialog.Builder(PersonalStock.this)
        .setTitle("下单")
        .setItems(tradeMenu, listener)
        .show();
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
			openTrade();
			break;
		case 2:
			//
			if(currentRow>=actualLen) 
				return;
			openZixunActivity(list.get(currentRow).getStkcode(), Global.NJZQ_ZXG_GONGGAO);
			break;
		case 3:
			//
			if(currentRow>=actualLen) 
				return;
			openZixunActivity(list.get(currentRow).getStkcode(), Global.NJZQ_ZXG_DIANPIN);
			break;
		case 4:
			//
			if(currentRow>=actualLen) 
				return;
				openZixunActivity(list.get(currentRow).getStkcode(), Global.NJZQ_ZXG_QINGBAO);

			break;
		case 5:
			if(NameRule.isFundWithStockCode(NameRule.getSecurityType(NameRule.getExchange(list.get(currentRow).getMarket()), list.get(currentRow).getStkcode())))
				openZDActivity(getURL(list.get(currentRow).getStkcode(), "iphone/zxlp/guGe_zdJijin.jsp"), "诊断"/*Global.NJZQ_ZXG_JJ_ZHENDUAN*/);
			else
				openZDActivity(getURL(list.get(currentRow).getStkcode(), "iphone/zxlp/guGe_zdGupiao.jsp"),"诊断"/* Global.NJZQ_ZXG_ZHENDUAN*/);
			break;
		case 11:
			pageUp();
			break;
		case 12:
			pageDn();
			break;
		case 13:
			moveUp();
			break;
		case 14:
			moveDn();
			break;
		case 15:
			showDialog(DIALOG_CLEAR_STOCK);
			break;
		case 16:
			hiddenOrDisplayStockBar(View.GONE);
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	private void moveDn() {
		
		if(currentRow>=(actualLen<pageNum?actualLen-1:pageNum-1)) return;
		currentRow++;
		adapter = new UserStockAdapter(PersonalStock.this, list,
				R.layout.zr_user_stock_list_item, currentRow, pageNum);
		lv.setAdapter(adapter);
		initView(NameRule.getExchange(list.get(currentRow).getMarket()), list.get(currentRow).getStkcode(), list.get(currentRow).getStkname());
	}
	
	private void moveUp() {
		if(currentRow==0) return;
		currentRow--;
		adapter = new UserStockAdapter(PersonalStock.this, list,
				R.layout.zr_user_stock_list_item, currentRow, pageNum);
		lv.setAdapter(adapter);
		initView(NameRule.getExchange(list.get(currentRow).getMarket()), list.get(currentRow).getStkcode(), list.get(currentRow).getStkname());
	}
	
	private void pageDn() {
		if(maxlen==0||to>=maxlen) return;
		from += pageNum;
		to  += pageNum;
		setToolBar();
	}
	
	private void pageUp() {
		if(maxlen==0||from<=0) return;
		from -= pageNum;
		to   -= pageNum;
		setToolBar();
	}

	protected void cancelThread() {
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	
	private void openZixunActivity(String stock, int pos) {
		Intent localIntent = new Intent();
		localIntent.putExtra("pos", pos);
		localIntent.putExtra("stockcode", stock);
		localIntent.setClass(PersonalStock.this, WebViewDisplay.class);
		startActivity(localIntent);
	}
	private void openZDActivity(String url, String title) {
		Intent localIntent = new Intent();
		localIntent.putExtra("url", url);
		localIntent.putExtra("title", title);
		localIntent.setClass(PersonalStock.this, OpenPdfDisplay.class);
		startActivity(localIntent);
	}
	
	private String getURL(String stock ,String url){
		String whereStockcode = "?";
		if (stock != null && !"".equals(stock)) {
			whereStockcode = stock.equals("") ? "?" : "?stock_id="
					+ stock + "&";
		}

		return Config.roadZixun + url
				+ whereStockcode + "serviceTime="
				+ ValidationService.getServiceTime();
	}
	
	
	public void removeHandler() {
		mHandler.removeCallbacks(r);
		priceHandler.removeCallbacks(pricerunable);
	}
	
    private Runnable pricerunable;
    private int msg = -1;
	
	private class PriceDataHandler extends Handler {

        public PriceDataHandler(Looper looper) {
			super(looper);
		}

		@Override
        public void handleMessage(Message msg) {
        	refreshData(msg.what);
        }
    }
	
	protected void refreshData(final int type) {
		Runnable r = new Runnable() {
			public void run() {
				try {
					switch(type) {
			        	case 0:
			        		Log.i("###############", jsonTick+">>>>>>>>>>>");
			        		if(Utils.isHttpStatus(jsonTick)) {
								//tickFrom = jsonTick.getJSONArray("data").length();
				    			trendView.getQuoteData(jsonTick);
				        		trendView.invalidate();
							}
			        		break;
			        	case 1:
			        		klineView.invalidate();
			        		break;
			        	case 2:
			        		priceView.invalidate();
			        		financeView.invalidate();
			        		break;
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
	        	hiddenProgress();
			}
		};
		runOnUiThread(r);
	}
	
	public void init(final String exchange, final String stockcode, final String stockname) {
		//打开加载进度对话框,nLock=TRUE避免退出的时候还在加载
		if(nLock&&!m_pDialog.isShowing())
			openProgress();
		priceHandler.removeCallbacks(pricerunable);
		pricerunable = new Runnable() {
			public void run() {
				priceHandler.removeMessages(msg);
				if(nLock) {
					try {
						Log.i("#########加载FLIPPER##########", ">>>>>>>>>>>>>"+viewFlipper.getDisplayedChild());
						switch(viewFlipper.getDisplayedChild()) {
						case 0:
							/**加载FLIPPER分时图需要的数据**/
							JSONObject j = ConnService.getTick("GET_TICK", exchange, stockcode, String.valueOf(tickFrom));
							if(Utils.isHttpStatus(j)) {
								if(tickFrom==0) {
									jsonTick = j;
								}
								else {
									JSONArray list = j.getJSONArray("data");
									for(int i = 0; i<list.length(); i++) {
										if(!jsonTick.getString("quotetime").equals(list.getJSONArray(i).getString(3))) {
											jsonTick.getJSONArray("data").put(j.getJSONArray("data").get(i));
										}
									}
									Log.i("@@@@@@@@@@data2 length@@@@@@@@", (exchange + stockcode)+">>>>>>>" + j.isNull("data2"));
									if(("sh000001".equals(exchange + stockcode) || "sz399001".equals(exchange + stockcode))&&
											!j.isNull("data2")) {
										JSONArray list2 = j.getJSONArray("data2");
										Log.i("@@@@@@@@@@data2 length@@@@@@@@", list2.length()+">>>>>>>");
										for(int i = 0; i<list2.length(); i++) {
											if(jsonTick.getString("quotetime").equals(list2.getJSONArray(i).getString(1))) {
												Log.i("########data2#########", tickFrom+">>>>>>>>>>>>>>>>" + jsonTick.getJSONArray("data2").length());
												jsonTick.getJSONArray("data2").put(tickFrom-1, j.getJSONArray("data2").get(i));
											}
											else {
												jsonTick.getJSONArray("data2").put(j.getJSONArray("data2").get(i));
											}
										}
									}
									jsonTick.remove(jsonTick.getString("quotetime"));
									jsonTick.put("quotetime", j.getString("quotetime"));
								}
								tickFrom = jsonTick.getJSONArray("data").length();
								msg = 0;
							}
							else {
								msg = -1;
							}
							break;
						case 1:
							/**加载FLIPPERK线图需要的数据**/
							jsonKline = ConnService.getKlineData(PersonalStock.this, exchange, stockcode, "day", "ma", "volume");
							if(jsonKline!=null) {
				    			klineView.initData(jsonKline);
				    			msg = 1;
							}
							else {
								msg = -1;
							}
							break;
						default:
							/**加载FLIPPER后两屏需要的数据**/
							jsonData = ConnService.getDish("GET_PRICE_VOLUMEJSON", exchange, stockcode, stocktype);
							if(Utils.isHttpStatus(jsonData)) {
				    			priceView.initData(jsonData);
				    			financeView.initData(jsonData);
				    			msg = 2;
							}
							else {
								msg = -1;
							}
							break;
						}
						
						
					} catch (JSONException e) {
						e.printStackTrace();
						msg = -1;
					}
				}
                //通过Handler发布携带消息
				priceHandler.sendEmptyMessageDelayed(msg, 50);
				priceHandler.postDelayed(pricerunable, Config.fenshirefresh);
			}
		};
		priceHandler.post(pricerunable);
	}
}
