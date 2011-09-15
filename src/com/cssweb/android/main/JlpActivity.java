package com.cssweb.android.main;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ViewFlipper;

import com.cssweb.android.base.GridViewActivity;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.custom.MyGrid;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.trade.BankActivity;
import com.cssweb.android.trade.FundActivity;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.user.track.ServiceControl;
import com.cssweb.android.util.ActivityUtil;

/**
 * 金罗盘首页
 * 
 * @author hujun
 * 
 */
public class JlpActivity extends GridViewActivity {

    private Context mContext = JlpActivity.this;

    private ViewFlipper viewFlipper;

    private Animation leftIn;

    private Animation leftOut;

    private Animation rightIn;

    private Animation rightOut;

    private ServiceControl sc = null;//行为分析

    private MyGrid mGrid, mGrid2, mGrid3;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.njzq_gridview);

        initPopupWindow();
        sc = new ServiceControl(this);//行为分析初始化

        preView = (ImageView) findViewById(R.id.previous_screen);

        viewFlipper = (ViewFlipper) this.findViewById(R.id.mainmenu_container);

        leftIn = AnimationUtils.loadAnimation(this, R.anim.push_left_in_layout);
        leftOut = AnimationUtils.loadAnimation(this, R.anim.push_left_out_layout);
        rightIn = AnimationUtils.loadAnimation(this, R.anim.push_right_in_layout);
        rightOut = AnimationUtils.loadAnimation(this, R.anim.push_right_out_layout);

        mGrid = (MyGrid) findViewById(R.id.mainmenu_grid);
        mGrid2 = (MyGrid) findViewById(R.id.mainmenu_grid2);
        mGrid3 = (MyGrid) findViewById(R.id.mainmenu_grid3);

        Bundle bundle = getIntent().getExtras();
        menuId = bundle.getInt("menu_id");
        switch (menuId) { 
        case 0:
            sc.currentSite("nzbd");
            break;
        case 1:
            sc.currentSite("hqbj");
            break;
        case 2:
            sc.currentSite("wtjy");
            break;
        case 3:
            sc.currentSite("zxhd");
            break;
        case 4:
            sc.currentSite("zsyyt");
            break;
        case 5:
            sc.currentSite("nzfc");
            break;
        case 6:
            sc.currentSite("jfly");
            break;
        case 7:
            sc.currentSite("zxlp");
            break;
        default:
            break;

        }

        initMenuName(menuId, -1);
        initTitle(R.drawable.njzq_title_left_back, 0, menuName);
    }

    @Override
    protected void onResume() {
        super.onResume();
        changeBG();
        //initMenuName(menuId, -1);

        String userType = TradeUser.getInstance().getUserType();
        if ("serv".equals(userType))
            initToolBar(Global.BAR_IMAGE_1, Global.BAR_TAG_2);
        else
            initToolBar(Global.BAR_IMAGE_2, Global.BAR_TAG);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initMenuName(int id, int pos) {
        switch (id) {
        case Global.NJZQ_NZBD:
            initGrid(mGrid, Global.NJZQ_ZXLP);
            initGrid(mGrid2, menuId);
            viewFlipper.setDisplayedChild(1);
            preView.setVisibility(View.VISIBLE);
            preView.setImageResource(R.drawable.page_arrow_01);
            menuName = "宁证宝典";
            break;
        case Global.NJZQ_HQBJ:
            initGrid(mGrid, menuId);
            menuName = "行情报价";
            break;
        case Global.NJZQ_WTJY:
            initGrid(mGrid, menuId);
            initGrid(mGrid2, Global.NJZQ_WTJY_TWO);
            initGrid(mGrid3, Global.NJZQ_WTJY_THREE);
            preView.setVisibility(View.VISIBLE);
            preView.setImageResource(R.drawable.page_arrow_11);
            menuName = "委托交易";
            break;
        case Global.NJZQ_ZXHD:
            initGrid(mGrid, menuId);
            menuName = "在线互动";
            break;
        case Global.NJZQ_ZSYYT:
            initGrid(mGrid, menuId);
            menuName = "掌上营业厅";
            break;
        case Global.NJZQ_NZFC:
            initGrid(mGrid, menuId);
            menuName = "宁证风采";
            break;
        case Global.NJZQ_JFSC:
            initGrid(mGrid, menuId);
            menuName = "积分乐园";
            break;
        case Global.NJZQ_ZXLP:
            initGrid(mGrid, menuId);
            initGrid(mGrid2, Global.NJZQ_NZBD);
            preView.setVisibility(View.VISIBLE);
            preView.setImageResource(R.drawable.page_arrow_02);
            menuName = "资讯罗盘";
            break;
        }

        //		if(pos>-1) {
        //			
        //		}
    }

    protected void openChild(int pos, int position) {
        if (pos == Global.NJZQ_WTJY_TWO && position == 2) {
            loadFundData();
        } else if (pos == Global.NJZQ_WTJY && position == 4) {
            loadBanks();
        } else {
            FairyUI.switchToWnd(pos, position, "", "", mContext);
        }
    }

    private void loadFundData() {
        String openFundCompanyDate = ActivityUtil.getPreference(mContext, "openFundCompanyDate", "");
        String openFundInfoDate = ActivityUtil.getPreference(mContext, "openFundInfoDate", "");
        String openFundAccountDate = ActivityUtil.getPreference(mContext, "openFundAccountDate", "");
        if (!DateTool.getToday().equals(openFundCompanyDate) || !DateTool.getToday().equals(openFundInfoDate) || !DateTool.getToday().equals(openFundAccountDate))
            openProgress();
        new AsyncTask<Void, Void, Boolean>() {
            /**
             * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间
             */
            String msg = "";

            @Override
            protected Boolean doInBackground(Void... arg0) {
                boolean flag = true;
                try {
                    String res = TradeUtil.initFundData(mContext);
                    if ("".equals(res)) {
                        flag = Boolean.TRUE;
                    } else {
                        flag = Boolean.FALSE;
                        msg = res;
                    }
                } catch (Exception e) {
                    flag = Boolean.FALSE;
                }
                return flag;
            }

            /**
             * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
             */
            protected void onPostExecute(Boolean result) {
                hiddenProgress();
                if (result != Boolean.TRUE) {
                    toast(msg);
                } else {
                    loadAllFund();
                }
            }
        }.execute();
    }

    private void loadAllFund() {
        Intent localIntent = new Intent();
        localIntent.putExtra("menu_id", Global.NJZQ_WTJY_GP_THREE);
        localIntent.setClass(this, FundActivity.class);
        startActivity(localIntent);
    }

    private void loadBanks() {
        String openBanksDate = ActivityUtil.getPreference(mContext, "openBanksDate", "");
        if (!DateTool.getToday().equals(openBanksDate))
            openProgress();
        new AsyncTask<Void, Void, Boolean>() {
            /**
             * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间
             */
            String msg = "";

            @Override
            protected Boolean doInBackground(Void... arg0) {
                boolean flag = true;
                try {
                    String res = TradeUtil.initBanksData(mContext);
                    if ("".equals(res)) {
                        flag = Boolean.TRUE;
                    } else {
                        flag = Boolean.FALSE;
                        msg = res;
                    }
                } catch (Exception e) {
                    flag = Boolean.FALSE;
                }
                return flag;
            }

            /**
             * 此方法在主线程执行，任务执行的结果作为此方法的参数返回
             */
            protected void onPostExecute(Boolean result) {
                hiddenProgress();
                if (result != Boolean.TRUE) {
                    toast(msg);
                } else {
                    loadAllBank();
                }
            }
        }.execute();
    }

    private void loadAllBank() {
        Intent localIntent = new Intent();
        localIntent.putExtra("menu_id", Global.NJZQ_WTJY_FIVE);
        localIntent.setClass(this, BankActivity.class);
        startActivity(localIntent);
    }

    /**
     * 向左滑动
     */
    protected void moveColLeft() {
        if (viewFlipper.getDisplayedChild() == 0 && menuId == Global.NJZQ_ZXLP) {
            viewFlipper.setInAnimation(leftIn);
            viewFlipper.setOutAnimation(leftOut);
            viewFlipper.showNext();
            setTitleText("宁证宝典");
            preView.setImageResource(R.drawable.page_arrow_01);
        }

        if (viewFlipper.getDisplayedChild() == 0 && menuId == Global.NJZQ_NZBD) {
            viewFlipper.setInAnimation(leftIn);
            viewFlipper.setOutAnimation(leftOut);
            viewFlipper.showNext();
            setTitleText("宁证宝典");
            preView.setImageResource(R.drawable.page_arrow_01);
        }

        if (viewFlipper.getDisplayedChild() == 0 && menuId == Global.NJZQ_WTJY) {
            viewFlipper.setInAnimation(leftIn);
            viewFlipper.setOutAnimation(leftOut);
            viewFlipper.showNext();
            preView.setImageResource(R.drawable.page_arrow_12);
        } else if (viewFlipper.getDisplayedChild() == 1 && menuId == Global.NJZQ_WTJY) {
            viewFlipper.setInAnimation(leftIn);
            viewFlipper.setOutAnimation(leftOut);
            viewFlipper.showNext();
            preView.setImageResource(R.drawable.page_arrow_13);
        }
    }

    /**
     * 向右滑动
     */
    protected void moveColRight() {
        if (viewFlipper.getDisplayedChild() == 1 && menuId == Global.NJZQ_ZXLP) {
            viewFlipper.setInAnimation(rightIn);
            viewFlipper.setOutAnimation(rightOut);
            viewFlipper.showPrevious();
            setTitleText("资讯罗盘");
            preView.setImageResource(R.drawable.page_arrow_02);
        }

        if (viewFlipper.getDisplayedChild() == 1 && menuId == Global.NJZQ_NZBD) {
            viewFlipper.setInAnimation(rightIn);
            viewFlipper.setOutAnimation(rightOut);
            viewFlipper.showPrevious();
            setTitleText("资讯罗盘");
            preView.setImageResource(R.drawable.page_arrow_02);
        }

        if (viewFlipper.getDisplayedChild() == 2 && menuId == Global.NJZQ_WTJY) {
            viewFlipper.setInAnimation(rightIn);
            viewFlipper.setOutAnimation(rightOut);
            viewFlipper.showPrevious();
            preView.setImageResource(R.drawable.page_arrow_12);
        } else if (viewFlipper.getDisplayedChild() == 1 && menuId == Global.NJZQ_WTJY) {
            viewFlipper.setInAnimation(rightIn);
            viewFlipper.setOutAnimation(rightOut);
            viewFlipper.showPrevious();
            preView.setImageResource(R.drawable.page_arrow_11);
        }
    }
}
