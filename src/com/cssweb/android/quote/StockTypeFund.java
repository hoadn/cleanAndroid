package com.cssweb.android.quote;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.cssweb.android.base.QuoteFundGridActivity;
import com.cssweb.android.common.Global;
import com.cssweb.android.connect.ConnService;
import com.cssweb.android.connect.RequestParams;
import com.cssweb.android.main.R;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.CssStockFund;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 股票型、债券型、货币型、混合型基金
 * 
 * @author hoho
 * 
 */
public class StockTypeFund extends QuoteFundGridActivity {
    private Context context = StockTypeFund.this;

    private final String TAG = "stockTypeFund";

    @SuppressWarnings("unused")
    private String[] menu;

    private RequestParams requestParams;

    private String[] cols;

    private int allStockNums = 0;

    private int paramType;

    private String flag = null;

    private int pageNum = 10;

    private Thread thread = null;

    private boolean firstComing = true;

    // 没有选择查询条件 全部传-1
    private String fundCompanyId = "-1";

    private String jingzhi1 = "-1";

    private String jingzhi2 = "-1";

    private String level = "-1";

    private String managerLevel = "-1";

    private ServiceControl sc = null;//行为分析

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zr_table);
        sc = new ServiceControl(this);//行为分析初始化
        Bundle bundle = getIntent().getExtras();
        paramType = bundle.getInt("type");
        requestParams = new RequestParams();
        requestParams.setPaixu("secucode");
        setTitle();
        String[] toolbarname = new String[] { Global.TOOLBAR_MENU, Global.TOOLBAR_SHANGYE, Global.TOOLBAR_XIAYIYE, Global.TOOLBAR_F10, Global.TOOLBAR_REFRESH };
        initToolBar(toolbarname, Global.BAR_TAG);
        setToolBar(1, false, R.color.zr_newlightgray);
        menu = getResources().getStringArray(R.array.stock_type_menu);


        String isQuery = bundle.getString("isQuery");
        if (null != isQuery && isQuery.equals("1")) { //标示是从查询条件过来
            fundCompanyId = bundle.getString("fundCompanyId");
            jingzhi1 = bundle.getString("jingzhi1");
            jingzhi2 = bundle.getString("jingzhi2");
            level = bundle.getString("level");
            managerLevel = bundle.getString("managerLevel");
        }
        requestParams.setLevel(Integer.valueOf(level));
        requestParams.setManagerlevel(Integer.valueOf(managerLevel));
        requestParams.setFundCompanyId(fundCompanyId);
        requestParams.setJingzhi1(jingzhi1);
        requestParams.setJingzhi2(jingzhi2);
        //根据不同分辨率获得可显示行数
        pageNum = CssSystem.getTablePageSize(StockTypeFund.this);
        rowHeight = CssSystem.getTableRowHeight(StockTypeFund.this);
        requestParams.setBegin("1");
        requestParams.setEnd(String.valueOf(pageNum));
        setToolBar(1, false, R.color.zr_newlightgray);
        handlerData();
    }

    //	   public static final int QUOTE_WARNING            = 111;  //行情预警
    //	    public static final int QUOTE_STOCK              = 112;  //股票型基金
    //	    public static final int QUOTE_BOND               = 113;  //债券型基金
    //	    public static final int QUOTE_MONETARY           = 114;  //货币型基金
    //	    public static final int QUOTE_MIX                = 115;  //混合型基金
    //	    public static final int SUN_PRIVATE              = 116;  //阳光私募
    //	    public static final int QUOTE_DAPAN              = 117;  //大盘指数
    //	    public static final int QUOTE_HKSTOCK            = 118;  //大盘指数
    //	    public static final int HK_MAINBOARD             =119;   //香港主板
    //	    public static final int HK_CYB                   =120;   //香港创业板
    /**
     * 设置标题
     */
    private void setTitle() {
        if (paramType == 0) {
            initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.kfstock));
            cols = getResources().getStringArray(R.array.stocktypefund_colsname);
            requestParams.setMarket("kfstock");
            this.activityKind = Global.QUOTE_STOCK;
            sc.currentSite("hqbj_cwjj_gpxjj");
        } else if (paramType == 1) {
            requestParams.setMarket("kfbond");
            initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.kfbond));
            cols = getResources().getStringArray(R.array.stocktypefund_colsname);
            this.activityKind = Global.QUOTE_BOND;
            sc.currentSite("hqbj_cwjj_zqxjj");
        } else if (paramType == 2) {
            requestParams.setMarket("kfmonetary");
            initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.kfmonetary));
            cols = getResources().getStringArray(R.array.monetary_colsname);
            flag = "monetary";
            this.activityKind = Global.QUOTE_MONETARY;
            sc.currentSite("hqbj_cwjj_hbxjj");
        } else if (paramType == 3) {
            requestParams.setMarket("kfmix");
            initTitle(R.drawable.njzq_title_left_back, 0, getResources().getString(R.string.kfmix));
            cols = getResources().getStringArray(R.array.stocktypefund_colsname);
            this.activityKind = Global.QUOTE_MIX;
            sc.currentSite("hqbj_cwjj_hhxjj");
        }
    }

    /**
     * 请求后台数据
     */
    @Override
    protected void init(final int type) {
        try {
            r = new Runnable() {
                public void run() {
                    if (type == 1) {
                        quoteData = ConnService.execute(requestParams, 3);
                        try {
                            if (Utils.isHttpStatus(quoteData)) {
                                isNetworkError = 0;
                            } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新UI界面
     * 
     */
    @Override
    protected void handlerData() {
        try {
            if (isNetworkError < 0 && firstComing) {
                firstComing = false;
                toast(R.string.load_data_error);
            }
            if (quoteData == null || quoteData.equals("")) {
                //if(Utils.isHttpStatus(quoteData)){
                listfund.clear();
                listfund.addAll(TradeUtil.fillListToNull2(0, pageNum));
                refreshFundUI(listfund, cols, flag);
                hiddenProgressToolBar();
                return;
            }
            JSONArray jArr = (JSONArray) quoteData.getJSONArray("data");
            int len = jArr.length();
            listfund.clear();
            allStockNums = quoteData.getInt("totalrecnum");

            if (pageNum >= allStockNums) {
                //避免选择品种的时候不足一页显示的时候下页的按钮还是可点的
                setToolBar(2, false, R.color.zr_newlightgray);
            }

            for (int i = 0; i < jArr.length(); i++) {
                JSONArray jA = (JSONArray) jArr.get(i);
                CssStockFund cssStockFund = new CssStockFund();
                cssStockFund.setStkcode(jA.getString(0));
                cssStockFund.setStkfunname(jA.getString(1));
                if (paramType == 2) {
                    cssStockFund.setJz(jA.getDouble(5));
                    cssStockFund.setLjjz(jA.getDouble(6));
                } else {
                    cssStockFund.setJz(jA.getDouble(2));
                    cssStockFund.setLjjz(jA.getDouble(3));
                }
                cssStockFund.setJjtnpj(jA.getDouble(8));
                cssStockFund.setJjfnpj(jA.getDouble(9));
                cssStockFund.setMarket(jA.getString(10));
                listfund.add(cssStockFund);
            }
            if (len < pageNum) {
                listfund.addAll(TradeUtil.fillListToNull2(len, pageNum));
            }
            refreshFundUI(listfund, cols, flag);
            hiddenProgressToolBar();
        } catch (Exception e) {
            hiddenProgressToolBar();
            Log.e(TAG, e.toString());
            e.printStackTrace();
        } finally {
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

    /**
     * 上一页
     */
    protected void onPageUp() {
        int i1 = Integer.parseInt(requestParams.getBegin());
        int i2 = 0;
        if (i1 <= 1) {
            setToolBar(1, false, R.color.zr_newlightgray);
        } else {
            i1 -= pageNum;
            i2 = Integer.parseInt(requestParams.getEnd()) - pageNum;
            if (i1 <= 1) {
                setToolBar(1, false, R.color.zr_newlightgray);
                setToolBar(2, true, R.color.zr_white);
            } else {
                setToolBar(1, true, R.color.zr_white);
                setToolBar(2, true, R.color.zr_white);
            }
        }
        i1 = (i1 <= 1) ? 1 : i1;
        //i2 = (i2<10)?10:i2;
        String begin = String.valueOf(i1);
        String end = String.valueOf(i2);
        requestParams.setBegin(begin);
        requestParams.setEnd(end);
        setToolBar();
    }

    /**
     * 下一页
     */
    protected void onPageDn() {
        int i1 = Integer.parseInt(requestParams.getBegin()) + pageNum;
        int i2 = Integer.parseInt(requestParams.getEnd()) + pageNum;
        if (i2 >= allStockNums) {
            //i2 = allStockNums;
            setToolBar(1, true, R.color.zr_white);
            setToolBar(2, false, R.color.zr_newlightgray);
        } else {
            setToolBar(1, true, R.color.zr_white);
            setToolBar(2, true, R.color.zr_white);
        }
        String begin = String.valueOf(i1);
        String end = String.valueOf(i2);
        requestParams.setBegin(begin);
        requestParams.setEnd(end);
        setToolBar();
    }

    @Override
    protected void toolBarClick(int tag, View v) {
        switch (tag) {
        case 0:
            onOption();
            break;
        case 1:
            onPageUp();
            break;
        case 2:
            onPageDn();
            break;
        case 3:
            if (null == stockFundName && null == stockFundCode) {
                Toast.makeText(context, "请选择", Toast.LENGTH_SHORT).show();
                return;
            }
            String exchange = NameRule.getExchange("2");
            Intent localIntent = new Intent();
            localIntent.setClass(this, GetF10List.class);
            localIntent.putExtra("exchange", exchange);
            localIntent.putExtra("stockcode", stockFundCode);
            localIntent.putExtra("stockname", stockFundName);
            localIntent.putExtra("paramtype", "stockfund");
            startActivity(localIntent);
            //FairyUI.switchToWnd(Global.QUOTE_F10, exchange, stockFundCode, stockFundName, context);
            break;
        case 4:
            firstComing = true;
            setToolBar();
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
        stockFundName = null;
        stockFundCode = null;

    }

    /**
     * 取消线程
     */
    protected void cancelThread() {
        if (thread != null) {
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

    protected void queryFund() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("type", paramType);
        intent.putExtras(bundle);
        intent.setClass(this, FundQueryCondition.class);
        startActivity(intent);

    }

    /**
     * 向下滑动 下一页
     */
    @Override
    protected void moveColBottom() {
        int end = Integer.parseInt(requestParams.getEnd());
        if (end >= allStockNums) {
            return;
        }
        onPageDn();
    }

    /**
     * 向上滑动 上一页
     */
    @Override
    protected void moveColTop() {
        int begin = Integer.parseInt(requestParams.getBegin());
        if (begin <= 1) {
            return;
        }
        onPageUp();
    }

}
