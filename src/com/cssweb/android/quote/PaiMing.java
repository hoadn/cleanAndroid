package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.Config;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.adapter.TableAdapter;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

public class PaiMing extends FlipperActiviy {
	private TableAdapter adapter;
	private ListView lv;
	private TextView title4;
    private List<CssStock> list = new ArrayList<CssStock>();
	private RequestParams requestParams;
	private int requestType = 0;
	//0表示按照涨幅,1表示按照跌幅,2表示按照量比
	private int currentRow = 0;
	private int allStockNums = 0;
	private int pageNum = 10;
	private int actualLen = 0;
	
	private String title1, title2, title3;
	
	private boolean nLock = true;
	private boolean firstComing = true;

	private int preClick = 0;	
	
	private AlertDialog myDialog1 = null;
	private OnClickListener listener1 = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		HandlerThread mHandlerThread = new HandlerThread("CSSWEB_THREAD");
		mHandlerThread.start();
		mHandler = new MessageHandler(mHandlerThread.getLooper());
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null)
			this.requestType = bundle.getInt("requestType");
		this.activityKind = Global.QUOTE_PAIMING;
		requestParams = new RequestParams();
		requestParams.setDesc("desc");

	    zhouqi = getResources().getStringArray(R.array.time_menu);
	    pingzh = getResources().getStringArray(R.array.stock_paiming_menu);
	    xuanx = getResources().getStringArray(R.array.select_menu);
	    
	    title1 = pingzh[0];
	    title2 = zhouqi[0];
	    title3 = xuanx[0];
	    
		setContentView(R.layout.zr_table_stock_list);

		initTitle(R.drawable.njzq_title_left_back, 0, "综合排名");
		
		String[] toolbarname = new String[]{ 
			Global.TOOLBAR_MENU, Global.TOOLBAR_PINGZHONG, Global.TOOLBAR_XUANX, 
			Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_REFRESH };
		initToolBar(toolbarname, Global.BAR_TAG);
	
		lv = (ListView) findViewById(R.id.zr_rt_listview);
	    title4 = (TextView) findViewById(R.id.zr_rt_title4);
	    lv.setOnTouchListener(this);
		lv.setOnItemLongClickListener(new OnItemLongClickListener() {

			public boolean onItemLongClick(AdapterView<?> parent,
					View view, int position, long id) {
				if(position<actualLen) {
					FairyUI.switchToWnd(Global.QUOTE_FENSHI, list.get(position).getMarket(), list.get(position).getStkcode(), list.get(position).getStkname(), PaiMing.this);
				}
				return false;
			}
		});

		lv.setOnItemClickListener(new OnItemClickListener() {
			
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if(position<actualLen) {
					if(list.get(position).getStkcode()!=null&&!list.get(position).getStkcode().equals("")) {
						//RefreshTitle(list.get(i));
						currentRow = position;
						cssStock = list.get(currentRow);
						arg0.getChildAt(position).setBackgroundResource(R.drawable.zr_table_xkz);
						if(list.get(position).getZf()<0) {
							arg0.getChildAt(position).findViewById(R.id.zr_rt_col4).setBackgroundResource(R.drawable.zrreportdownbg1);
						}
						else {
							arg0.getChildAt(position).findViewById(R.id.zr_rt_col4).setBackgroundResource(R.drawable.zrreportupbg1);
						}
						if(preClick!=position) {
							arg0.getChildAt(preClick).setBackgroundResource(R.drawable.zr_table_xk);
							if(list.get(preClick).getZf()<0) {
								arg0.getChildAt(preClick).findViewById(R.id.zr_rt_col4).setBackgroundResource(R.drawable.zrreportdownbg);
							}
							else {
								arg0.getChildAt(preClick).findViewById(R.id.zr_rt_col4).setBackgroundResource(R.drawable.zrreportupbg);
							}
							preClick = position;
						}
						else {
							FairyUI.switchToWnd(Global.QUOTE_FENSHI, list.get(position).getMarket(), list.get(position).getStkcode(), list.get(position).getStkname(), PaiMing.this);
						}
					}
				}
			}
			
		});
		//根据不同分辨率获得可显示行数
		pageNum = CssSystem.getTablePageSize(PaiMing.this);
		
		openOption();
		
		openPopup();
		
		//初始化翻页和上下页按钮是否可点
		requestParams.setBegin("0");
		requestParams.setEnd(String.valueOf(pageNum));
		setToolBar(3, false, R.color.zr_newlightgray);
		setToolBar(4, true, R.color.zr_white);
		
		handlerData();
	}
	
    protected void initTitle(int resid1, int resid2, String str) {
    	super.initTitle(resid1, resid2, str);
    	changeTitleBg();
    	midText.setText(title1 + " " + title2 + " " + title3);
    }
	
	protected void init(final int type) {
    	super.init(type);
		mHandler.removeCallbacks(r);
    	this.mLock = true;
    	r = new Runnable() {
			public void run() {
				Log.i("#########paiming mLock##########", mLock+">>>>>>>>>>");
				timetips = DateTool.getLongTime();
				if(mLock&&nLock) {
					try {
						quoteData = ConnService.execute(requestParams, requestType);
						if(Utils.isHttpStatus(quoteData)) {
							list.clear();
							allStockNums = quoteData.getInt("totalrecnum");
							JSONArray jArr = (JSONArray)quoteData.getJSONArray("data");
							actualLen = jArr.length();
							for (int i = 0; i < actualLen; i++) {
								JSONArray jA = (JSONArray)jArr.get(i);
								CssStock cssStock = new CssStock();
								cssStock.setStkname(jA.getString(1));
								cssStock.setStkcode(jA.getString(0));
								cssStock.setZf(jA.getDouble(3));
								//cssStock.setLb(jA.getDouble(4));
								cssStock.setZrsp(jA.getDouble(5));
								cssStock.setZjcj(jA.getDouble(2));
								cssStock.setMarket(NameRule.getExchange(jA.getString(4)));
								list.add(cssStock);
							}

							if(actualLen<pageNum) {
								list.addAll(TradeUtil.fillListToNull(actualLen, pageNum));
							}
							isNetworkError = 0;
						}
						else {
							isNetworkError = -1;
						}
					} catch (JSONException e) {
						e.printStackTrace();
						isNetworkError = -2;
					}
				}
				mLock = isRefreshTime();
                mHandler.sendEmptyMessage(0);
        		mHandler.postDelayed(r, Config.zongherefresh);
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
					actualLen = 0;
					list.addAll(TradeUtil.fillListToNull(0, pageNum));
				}
				if(allStockNums!=0&&pageNum>allStockNums) {
					//避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
					setToolBar(4, false, R.color.zr_newlightgray);
				}
				if(actualLen>0&&currentRow==0)
					cssStock = list.get(0);
				setTitleText(title1 + " " + title2 + " " + title3);
				title4.setText(title2 + " " + title3);
				
				adapter = new TableAdapter(PaiMing.this, list,
						R.layout.zr_table_stock_list_items, requestParams.getField(), currentRow, pageNum);
				lv.setAdapter(adapter);
				//进度条消失
				hiddenProgressToolBar();
			}
		};
		runOnUiThread(r);
	}
	
	protected void openOption() {
	    listener1 = new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which)
			{
				switch ( which )
				{
					case 0: requestParams.setMarket("sha"); break;
					case 1: requestParams.setMarket("shb"); break;
					case 2: requestParams.setMarket("sza"); break;
					case 3: requestParams.setMarket("szb"); break;	
					case 4: requestParams.setMarket("shbond"); break;		
					case 5: requestParams.setMarket("szbond"); break;		
					case 6: requestParams.setMarket("shsza"); break;		
					case 7: requestParams.setMarket("shszb"); break;		
					case 8: requestParams.setMarket("zxb"); break;		
					case 9: requestParams.setMarket("shszwarrant"); break;		
					case 10: requestParams.setMarket("szcyb"); break;							
				}
				title1 = pingzh[which];
				
				requestParams.setBegin("1");
				requestParams.setEnd(String.valueOf(pageNum));
				setToolBar(3, false, R.color.zr_newlightgray);
				setToolBar(4, true, R.color.zr_white);
				setToolBar();
			}
	    };
	    myDialog1 = new AlertDialog.Builder(PaiMing.this)
        .setTitle("请选择证券品种")
        .setItems(pingzh, listener1)
        .create();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacks(r);
	}

	@Override
	protected void onPause() {
		super.onPause();
		nLock = false;
		mHandler.removeCallbacks(r);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		nLock = true;
        initPopupWindow();
		setToolBar();
	}
	
	protected void onPageUp() {
		int i1 = Integer.parseInt(requestParams.getBegin());
		int i2 = 0;
		if(i1 <= 1) {
			setToolBar(3, false, R.color.zr_newlightgray);
		}
		else {
			i1 -= pageNum;
			i2 = Integer.parseInt(requestParams.getEnd()) - pageNum;
			if(i1 <= 1) {
				setToolBar(3, false, R.color.zr_newlightgray);
				setToolBar(4, true, R.color.zr_white);
			}
			else {
				setToolBar(3, true, R.color.zr_white);
				setToolBar(4, true, R.color.zr_white);
			}
		}
		i1 = (i1<=1)?1:i1;
		//i2 = (i2<pageNum)?pageNum:i2;
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	
	protected void onPageDn() {
		int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum;
		int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum;
		if(i2>=allStockNums) {
			//i2 = allStockNums;
			setToolBar(3, true, R.color.zr_white);
			setToolBar(4, false, R.color.zr_newlightgray);
		}
		else {
			setToolBar(3, true, R.color.zr_white);
			setToolBar(4, true, R.color.zr_white);
		}
		String begin = String.valueOf(i1);
		String end = String.valueOf(i2);
		requestParams.setBegin(begin);
		requestParams.setEnd(end);
		setToolBar();
	}
	
	protected void toolBarClick(int tag, View v) {
		switch(tag) {
		case 0:
			onOption();
			break;
		case 1:
		    if(!myDialog1.isShowing())
		    	myDialog1.show();
			break;
		case 2:
		    if(!dlg.isShowing())
		    	dlg.show();
			break;
		case 3:
			onPageUp();
			break;
		case 4:
			onPageDn();
			break;
		case 5:
			firstComing = true;
			setToolBar();
			break;
		default:
			cancelThread();
			break;
		}
	}
	
	@Override
	protected void handleClick(boolean flag) {
		if(flag){
			//Log.i("tag", "timeSpinner : " + timeSpinner.getSelectedItemPosition() + " : stateSpinner : " +stateSpinner.getSelectedItemPosition());
			int which = timeSpinner.getSelectedItemPosition();
			switch ( which )
			{
				case 0: requestParams.setPeroid("day"); break;
				case 1: requestParams.setPeroid("week"); break;
				case 2: requestParams.setPeroid("month"); break;						
			}
			title2 = zhouqi[which];
			
			which = stateSpinner.getSelectedItemPosition();
			switch ( which )
			{
				case 0: requestParams.setField("zf");requestParams.setDesc("desc"); break;
				case 1: requestParams.setField("zf");requestParams.setDesc("asc"); break;
				case 2: requestParams.setField("zd");requestParams.setDesc("desc"); break;	
				case 3: requestParams.setField("hs");requestParams.setDesc("desc"); break;							
			}
			title3 = xuanx[which];
			
			requestParams.setBegin("1");
			requestParams.setEnd(String.valueOf(pageNum));
			setToolBar(3, false, R.color.zr_newlightgray);
			setToolBar(4, true, R.color.zr_white);
			setToolBar();
			
		}else {
			
		}
	}
	
	protected void cancelThread() {
		mHandler.removeCallbacks(r);
		hiddenProgressToolBar();
	}
	
	/**
	 * 向下滑动 下一页
	 */
	@Override
	protected void moveColBottom() {
		int end = Integer.parseInt(requestParams.getEnd()) ;
		if(end>=allStockNums) {
			return ;
		}
		onPageDn();
	}
	
	/**
	 * 向上滑动 上一页
	 */
	@Override
	protected void moveColTop() {
		int begin = Integer.parseInt(requestParams.getBegin());
		if(begin <= 1) {
			return ;
		}
		onPageUp();
	}
}
