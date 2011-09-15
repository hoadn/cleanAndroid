/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)FairyUI.java 上午10:06:51 2010-12-10
 */
package com.cssweb.android.common;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;

import com.cssweb.android.connect.ConnService;
import com.cssweb.android.fzjy.VistualTrade;
import com.cssweb.android.main.JlpActivity;
import com.cssweb.android.main.MainActivity;
import com.cssweb.android.main.R;
import com.cssweb.android.quote.DaPan;
import com.cssweb.android.quote.FLineActivity;
import com.cssweb.android.quote.FenLei;
import com.cssweb.android.quote.GGHQActivity;
import com.cssweb.android.quote.GlobalMarket;
import com.cssweb.android.quote.GlobalMarketActivity;
import com.cssweb.android.quote.HKMainboard;
import com.cssweb.android.quote.HSZS;
import com.cssweb.android.quote.KLine2Activity;
import com.cssweb.android.quote.KLineActivity;
import com.cssweb.android.quote.OTCFundActivity;
import com.cssweb.android.quote.PaiMing;
import com.cssweb.android.quote.PersonalStock;
import com.cssweb.android.quote.QHHQActivity;
import com.cssweb.android.quote.QHSCBaseActivity;
import com.cssweb.android.quote.QueryStock;
import com.cssweb.android.quote.QuoteDetail;
import com.cssweb.android.quote.QuotePrice;
import com.cssweb.android.quote.QuoteSet;
import com.cssweb.android.quote.QuoteWarning;
import com.cssweb.android.quote.StockTypeFund;
import com.cssweb.android.quote.SunPrivate;
import com.cssweb.android.quote.TrendActivity;
import com.cssweb.android.quote.ZJS;
import com.cssweb.android.session.TradeUser;
import com.cssweb.android.sms.SMSJHActivity;
import com.cssweb.android.sz.Setting;
import com.cssweb.android.trade.BankActivity;
import com.cssweb.android.trade.CnjjActivity;
import com.cssweb.android.trade.FundActivity;
import com.cssweb.android.trade.fund.HistoryConclusion;
import com.cssweb.android.trade.fund.HistoryTrust;
import com.cssweb.android.trade.login.LoginActivity;
import com.cssweb.android.trade.stock.AssetQuery;
import com.cssweb.android.trade.stock.Bill;
import com.cssweb.android.trade.stock.GetPosition;
import com.cssweb.android.trade.stock.HistoryDeal;
import com.cssweb.android.trade.stock.HistoryEntrust;
import com.cssweb.android.trade.stock.ModifyContactInfo;
import com.cssweb.android.trade.stock.ModifyPassword;
import com.cssweb.android.trade.stock.NewStockMatch;
import com.cssweb.android.trade.stock.ShareholderList;
import com.cssweb.android.trade.stock.StockCancel;
import com.cssweb.android.trade.stock.StockTrading;
import com.cssweb.android.trade.stock.TodayDeal;
import com.cssweb.android.trade.stock.TodayEntrust;
import com.cssweb.android.trade.user.AccountManage;
import com.cssweb.android.trade.util.TradeUtil;
import com.cssweb.android.tyyh.ExperienceUsers;
import com.cssweb.android.util.CssSystem;
import com.cssweb.android.util.DateRange;
import com.cssweb.android.web.CfpdActivity;
import com.cssweb.android.web.JxgpcActivity;
import com.cssweb.android.web.WebViewDisplay;
import com.cssweb.quote.util.StockInfo;
import com.cssweb.quote.util.Utils;

/**
 * 界面跳转控制
 * 
 * @author hujun
 * @version 1.0
 * @see
 * @since 1.0
 */
public class FairyUI {
	public static Context mCtx;

	public static Intent genIntent(String paramString1, int paramInt,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = new Intent();
		if (paramString1.equals(Global.QUERY_STOCK_LSWT)) {
			localIntent.putExtra("strdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, HistoryEntrust.class);
		} else if (paramString1.equals(Global.QUERY_STOCK_LSCJ)) {
			localIntent.putExtra("strdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, HistoryDeal.class);
		} else if (paramString1.equals(Global.QUERY_STOCK_DZD)) {
			localIntent.putExtra("strdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, Bill.class);
		} else if (paramString1.equals(Global.QUERY_NEW_STOCK_MATCH)) {
			localIntent.putExtra("strdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, NewStockMatch.class);
		} else if (paramString1.equals(Global.QUERY_FUND_LSCJ)) {
			localIntent.putExtra("startdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, HistoryConclusion.class);
		} else if (paramString1.equals(Global.QUERY_FUND_LSWT)) {
			localIntent.putExtra("startdate", paramString2);
			localIntent.putExtra("enddate", paramString3);
			localIntent.setClass(paramContext, HistoryTrust.class);
		}
		return localIntent;
	}

	public static void switchToWnd(String paramString1, int paramInt,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = genIntent(paramString1, paramInt, paramString2,
				paramString3, paramContext);
		if (localIntent != null)
			paramContext.startActivity(localIntent);
	}

	public static Intent genIntent(int paramInt1, String paramString1,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = new Intent();
		// localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		localIntent.putExtra("exchange", paramString1);
		localIntent.putExtra("stockcode", paramString2);
		localIntent.putExtra("stockname", paramString3);
		switch (paramInt1) {
		case Global.QUOTE_KLINE:
			localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			if (CssSystem.getSDKVersionNumber() == 4)
				localIntent.setClass(paramContext, KLine2Activity.class);
			else
				localIntent.setClass(paramContext, KLineActivity.class);
			break;
		case Global.QUOTE_F10:
			if ("hk".equals(paramString1)) {
				StringBuffer sb = new StringBuffer();
				sb.append(Config.roadf10);
				sb.append("hkf10/mobile/index.jsp?stockcode=");
				sb.append(paramString1);
				sb.append(paramString2);
				localIntent.putExtra("url", sb.toString());
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if ("cf".equals(paramString1) || "dc".equals(paramString1)
					|| "sf".equals(paramString1) || "cz".equals(paramString1)) {
				StringBuffer sb = new StringBuffer();
				sb.append(Config.roadf10);
				sb.append("hkf10/front/tbobject3511/indexM.jsp?stockcode=");
				sb.append(paramString1);
				sb.append(paramString2);
				localIntent.putExtra("url", sb.toString());
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else {
				StringBuffer sb = new StringBuffer();
				sb.append(Config.roadZixun);
				sb.append("iphone/f10/index_forword.jsp?code=");
				sb.append(paramString1);
				sb.append(paramString2);
				localIntent.putExtra("url", sb.toString());
				localIntent.setClass(paramContext, WebViewDisplay.class);
				//localIntent.setClass(paramContext, GetF10List.class);
			}
			break;
		case Global.QUOTE_FENSHI:
			localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			localIntent.setClass(paramContext, TrendActivity.class);
			break;
		case Global.QUOTE_BAOJIA:
			localIntent.setClass(paramContext, QuotePrice.class);
			break;
		case Global.QUOTE_MINGXI:
			localIntent.setClass(paramContext, QuoteDetail.class);
			break;
		case Global.QUOTE_FLINE:
			localIntent.setClass(paramContext, FLineActivity.class);
			break;
		case Global.QUOTE_USERSTK:
			localIntent.putExtra("requestType", 1);
			localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			localIntent.setClass(paramContext, PersonalStock.class);
			break;
		case Global.QUOTE_WARN:
			localIntent.putExtra("requestType", 1);
			localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			localIntent.setClass(paramContext, QuoteSet.class);
			break;
		default:
			localIntent.setClass(paramContext, TrendActivity.class);
			break;

		}
		return localIntent;
	}

	public static void switchToWnd(int paramInt1, String paramString1,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = genIntent(paramInt1, paramString1, paramString2,
				paramString3, paramContext);
		if (localIntent != null)
			paramContext.startActivity(localIntent);
	}

	public static Intent genIntent(int paramInt1, int paramInt2,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = new Intent();
		if (paramInt1 == Global.NJZQ_WTJY) {
			localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// 委托交易第一屏
			switch (paramInt2) {
			case 0:
				localIntent.setClass(paramContext, AccountManage.class);
				break;
			case 1:
				localIntent.putExtra("type", 0);
				localIntent.putExtra("bsname", "买入");
				localIntent.putExtra("stkcode", paramString2);
				localIntent.setClass(paramContext, StockTrading.class);
				break;
			case 2:
				localIntent.putExtra("type", 1);
				localIntent.putExtra("bsname", "卖出");
				localIntent.putExtra("stkcode", paramString2);
				localIntent.setClass(paramContext, StockTrading.class);
				break;
			case 3:
				localIntent.setClass(paramContext, StockCancel.class);
				break;
			case 4:
				localIntent.putExtra("menu_id", Global.NJZQ_WTJY_FIVE);
				localIntent.setClass(paramContext, BankActivity.class);
				break;
			case 5:
				localIntent.putExtra("type", Global.QUERY_STOCK_DRWT);
				localIntent.setClass(paramContext, TodayEntrust.class);
				break;
			case 6:
				localIntent.putExtra("type", Global.QUERY_STOCK_DRCJ);
				localIntent.setClass(paramContext, TodayDeal.class);
				break;
			case 7:
				localIntent.setClass(paramContext, GetPosition.class);
				break;
			case 8:
				localIntent.setClass(paramContext, AssetQuery.class);
				break;
			}
		} else if (paramInt1 == Global.NJZQ_WTJY_TWO) {
			localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			// 委托交易第二屏幕
			switch (paramInt2) {
			case 0:
				localIntent.putExtra("menuid", Global.OPEN_GP);
				localIntent.putExtra("menudetail", Global.QUERY_STOCK_DZD);
				localIntent.setClass(paramContext, DateRange.class);
				break;
			case 1:
				localIntent.putExtra("menuid", Global.OPEN_GP);
				localIntent.putExtra("menudetail", Global.QUERY_STOCK_LSWT);
				localIntent.setClass(paramContext, DateRange.class);
				break;
			case 2:
				localIntent.putExtra("menu_id", Global.NJZQ_WTJY_GP_THREE);
				localIntent.setClass(paramContext, FundActivity.class);
				break;
			case 3:
				localIntent.putExtra("menu_id", Global.NJZQ_WTJY_GP_ONE);
				localIntent.setClass(paramContext, CnjjActivity.class);
				break;
			case 4:
				localIntent.putExtra("pos", Global.NJZQ_WTJY_SZLC);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 5:
				localIntent.putExtra("pos", Global.NJZQ_WTJY_RZRQ);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 6:
				localIntent.putExtra("menuid", Global.OPEN_GP);
				localIntent
						.putExtra("menudetail", Global.QUERY_NEW_STOCK_MATCH);
				localIntent.setClass(paramContext, DateRange.class);
				break;
			case 7:
				localIntent.setClass(paramContext, ModifyPassword.class);
				break;
			case 8:
				localIntent.setClass(paramContext, ShareholderList.class);
				break;
			}
		} else if (paramInt1 == Global.NJZQ_WTJY_THREE) {
			localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
			switch (paramInt2) {
			case 0:
				localIntent.setClass(paramContext, ModifyContactInfo.class);
				break;
			}
		} else if (paramInt1 == Global.NJZQ_HQBJ) {
			if (!isNetworkErrorGoInActivity(paramContext)) {
//				localIntent.putExtra("restart", 2);
//				localIntent.setClass(paramContext, RestartDialog.class);
				loadAllStock(paramInt1, paramInt2, paramString2, paramString3, paramContext);
				localIntent = null;
			} else {
				localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				switch (paramInt2) {
				case 0:
					localIntent = genIsLoginIntent(Global.NJZQ_HQBJ_HQYJ,
							Global.NJZQ_HQBJ_HQYJ, paramContext);
					break;
				case 1:
					// 为方便测试添加
					// localIntent.putExtra("requestType", 1);
					// localIntent.setClass(paramContext, PersonalStock.class);
					if (TradeUser.getInstance().getLoginType() == 3) {
						localIntent.putExtra("requestType", 1);
						localIntent.setClass(paramContext, PersonalStock.class);
					} else
						localIntent = genIsLoginIntent(Global.QUOTE_USERSTK,
								Global.QUOTE_USERSTK, paramContext);
					break;
				case 2:
					localIntent.putExtra("requestType", 2);
					localIntent.setClass(paramContext, DaPan.class);
					break;
				case 3:
					localIntent.putExtra("requestType", 0);
					localIntent.setClass(paramContext, PaiMing.class);
					break;
				case 4:
					localIntent.putExtra("menuid", Global.QUOTE_FENSHI);
					localIntent.setClass(paramContext, QueryStock.class);
					break;
				case 5:
					localIntent.putExtra("requestType", 2);
					localIntent.setClass(paramContext, FenLei.class);
					break;
				case 6:
					localIntent.putExtra("menu_id", Global.NJZQ_HQBJ_EGHT);
					localIntent.setClass(paramContext, OTCFundActivity.class);
					break;
				case 7:
					localIntent.putExtra("menu_id", Global.NJZQ_HQBJ_FIVE);
					localIntent.setClass(paramContext,
							GlobalMarketActivity.class);
					break;
				case 8:
					localIntent.putExtra("menu_id", Global.NJZQ_HQBJ_QHHQ);
					localIntent.setClass(paramContext, QHHQActivity.class);
					break;
				}
			}
		} else if (paramInt1 == Global.NJZQ_ZXHD) {
			switch (paramInt2) {
			case 0:// 专家解盘
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.putExtra("pos", Global.NJZQ_ZXHD_ZJJP);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 1://投资顾问
	    		localIntent = genIsLoginIntent(Global.NJZQ_ZXHD_TZGW, Global.NJZQ_ZXHD_TZGW, paramContext);
	    		break;
	    	case 2://客户经理
	    		if(TradeUser.getInstance().getLoginType()==3) {
		    		localIntent.putExtra("pos", Global.NJZQ_ZXHD_KHJL);
					localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    		localIntent.setClass(paramContext, WebViewDisplay.class);
				}
	    		else 
	    			localIntent = genIsLoginIntent(Global.NJZQ_ZXHD_KHJL, Global.NJZQ_ZXHD_KHJL, paramContext);
	    		break;
			case 3:// 在线客服
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.putExtra("pos", Global.NJZQ_ZXHD_ZXKF);// 在线客服
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 4:// 客服热线
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.putExtra("pos", Global.NJZQ_ZXHD_KFRX);// 客服热线
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 5:// 广纳良言
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.putExtra("pos", Global.NJZQ_ZXHD_GNLY);// 广纳良言
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 6:// 常见问题
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.putExtra("pos", Global.NJZQ_ZXHD_CJWT);// 常见问题
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 7:
				localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				localIntent.putExtra("menu_id", Global.NJZQ_ZXHD_EGHT);// 模拟交易
				localIntent.setClass(paramContext, VistualTrade.class);
				break;
			default:
				localIntent = null;
				break;
			}
		} else if (paramInt1 == Global.NJZQ_NZFC) {// 宁证风采
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (paramInt2) {
			case 0:
				localIntent.putExtra("pos", Global.NJZQ_NZFC_NZDT);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 1:
				localIntent.putExtra("pos", Global.NJZQ_NZFC_ZJNZ);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 2:
				localIntent.putExtra("pos", Global.NJZQ_NZFC_JYH);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			default:
				localIntent = null;
				break;
			}
		} else if (paramInt1 == Global.NJZQ_NZBD) {// 宁证宝典
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (paramInt2) {
			case 0:// 精选证券池
				localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				localIntent.putExtra("menu_id", Global.NJZQ_NZBD_JXZQC);
				localIntent.setClass(paramContext, JxgpcActivity.class);
				break;
			case 1:
				localIntent.putExtra("pos", Global.NJZQ_NZBD_TZZH);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 2:
				localIntent.putExtra("pos", Global.NJZQ_NZBD_ZQYJ);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 3:
				localIntent.putExtra("pos", Global.NJZQ_NZBD_QHYJ);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 4:// 财富频道
				localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				localIntent.putExtra("menu_id", Global.NJZQ_NZBD_CFPD);
				localIntent.setClass(paramContext, CfpdActivity.class);
				break;
			default:
				localIntent = null;
				break;
			}
		} else if (paramInt1 == Global.NJZQ_ZXLP) {// 资讯罗盘
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (paramInt2) {
			case 0:
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_CPBD);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 1:
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_YWZJ);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 2:
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_PMHH);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 3:
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_TGLC);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 4:
				// localIntent.putExtra("pos", Global.NJZQ_ZXLP_ZJLX);
				// localIntent.setClass(paramContext, WebViewDisplay.class);
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_ZJLX);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 5:
				localIntent.putExtra("pos", Global.NJZQ_ZXLP_GG);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			default:
				localIntent = null;
				break;
			}
		} else if (paramInt1 == Global.NJZQ_JFSC) {// 积分乐园
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// localIntent = genIsLoginIntent(Global.NJZQ_JFSC,
			// Global.NJZQ_JFSC, paramContext);
			localIntent.putExtra("pos", Global.NJZQ_JFSC);
			localIntent.setClass(paramContext, WebViewDisplay.class);
		} else if (paramInt1 == Global.NJZQ_ZSYYT) {// 掌上营业厅，
			localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			switch (paramInt2) {
			case 0://
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_YYKH);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 1:
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_YYTGG);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 2:
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_YYWD);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 3:
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_YWZX);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 4:
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_TZZJY);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			case 5:
				localIntent.putExtra("pos", Global.NJZQ_ZSYYT_TJKH);
				localIntent.setClass(paramContext, WebViewDisplay.class);
				break;
			default:
				localIntent = null;
				break;
			}
		}
		return localIntent;
	}

	public static void switchToWnd(int paramInt1, int paramInt2,
			String paramString2, String paramString3, Context paramContext) {
		Intent localIntent = genIntent(paramInt1, paramInt2, paramString2,
				paramString3, paramContext);
		if (localIntent != null) {
			paramContext.startActivity(localIntent);
		}

	}

	public static Intent genIntent(int paramInt, Context paramContext) {
		Intent localIntent = new Intent();
		if (paramInt >= 0) {
			if (paramInt == Global.NJZQ_WTJY) {
				localIntent = genIsLoginIntent(paramInt, paramInt, paramContext);
				if (null != localIntent) {
					localIntent.putExtra("menu_id", Global.NJZQ_WTJY);
					localIntent.putExtra("isChangeBtn", true);
				}
			} else if (paramInt == Global.NJZQ_JLP_SZ) {
				localIntent.setClass(paramContext, Setting.class);
			} else if (paramInt == Global.NJZQ_JLP_JYYH) {
				localIntent = genIsLoginIntent(paramInt, paramInt, paramContext);
				if (null != localIntent) {
					localIntent.putExtra("menu_id", Global.NJZQ_JLP_JYYH);
				}
			} else if (paramInt == Global.NJZQ_JLP_TYYH) {
				localIntent.setClass(paramContext, ExperienceUsers.class);
			} else if (paramInt == Global.NJZQ_JLP_LLYH) {

			} else if (paramInt == Global.NJZQ_JLP_YYKH) {
				localIntent.setClass(paramContext, Setting.class);
			} else if (paramInt == Global.NJZQ_HQBJ_QQSC_WHSC) {
				localIntent.setClass(paramContext, GlobalMarket.class);
			} else if (paramInt == Global.NJZQ_HQBJ_GGHQ) {
				localIntent.putExtra("menu_id", Global.NJZQ_HQBJ_GGHQ);
				localIntent.setClass(paramContext, GGHQActivity.class);
			} else if (paramInt == Global.NJZQ_JFSC) {
				localIntent.putExtra("pos", Global.NJZQ_JFSC);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			}

			else if (paramInt == Global.NJZQ_JLP_WDZQTAG) {//
				localIntent.putExtra("pos", Global.NJZQ_JLP_WDZQTAG);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_JLP_YYKHTAG) {// 预约开户
				localIntent.putExtra("pos", Global.NJZQ_JLP_YYKHTAG);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_JLP_TYKTAG) {
				localIntent.putExtra("pos", Global.NJZQ_JLP_TYKTAG);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			}

			else if (paramInt == Global.NJZQ_FIND_PASSWORD) {// 找回交易密码
				localIntent.putExtra("pos", Global.NJZQ_FIND_PASSWORD);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_SQTYK) {// 申请体验卡
				localIntent.putExtra("pos", Global.NJZQ_SQTYK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_RESET_SERVIR_PASSWORD) {// 重置服务密码
				localIntent.putExtra("pos", Global.NJZQ_RESET_SERVIR_PASSWORD);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_TYYH_FIND_PASSWORD) {
				localIntent.putExtra("pos", Global.NJZQ_TYYH_FIND_PASSWORD);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			}
			
			
			else if (paramInt == Global.NJZQ_NZBD_JXZQC_JCC) {
				localIntent.putExtra("pos", Global.NJZQ_NZBD_JXZQC);
				localIntent.putExtra("position", 0);
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_NZBD_JXZQC_HXC) {
				localIntent.putExtra("pos", Global.NJZQ_NZBD_JXZQC);
				localIntent.putExtra("position", 1);
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_NZBD_CFPD_JTPX) {
				localIntent.putExtra("pos", Global.NJZQ_NZBD_CFPD);
				localIntent.putExtra("position", 2);
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_NZBD_CFPD_MRJP) {
				localIntent.putExtra("pos", Global.NJZQ_NZBD_CFPD);
				localIntent.putExtra("position", 0);
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			} else if (paramInt == Global.NJZQ_NZBD_CFPD_TZLT) {
				localIntent.putExtra("pos", Global.NJZQ_NZBD_CFPD);
				localIntent.putExtra("position", 1);
				localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				localIntent.setClass(paramContext, WebViewDisplay.class);
			}
		
			else {
				localIntent.putExtra("menu_id", paramInt);
				localIntent.setClass(paramContext, JlpActivity.class);
			}
		} else if (paramInt == -1) {
			localIntent.putExtra("menu_id", paramInt);
			localIntent.setClass(paramContext, LoginActivity.class);
		} else {
			localIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			localIntent.setClass(paramContext, MainActivity.class);
		}
		return localIntent;
	}

	/**
	 * 金罗盘界面跳转
	 * 
	 * @param paramInt
	 * @param paramContext
	 */
	public static void switchToWnd(int paramInt, Context paramContext) {
		Intent localIntent = genIntent(paramInt, paramContext);
		if (localIntent != null)
			paramContext.startActivity(localIntent);
	}

	// 频繁点击toolbar，每个按键最多只生成一个实例，且回退时就返因金罗盘首页
	public static void switchToWndWithSingle(int paramInt,
			Context paramContext, boolean flag) {
		Intent localIntent = genIntent(paramInt, paramContext);
		localIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		if (localIntent != null)
			paramContext.startActivity(localIntent);
	}

	/**
	 * 判断是否已经激活\登录
	 * 
	 * @param paramInt
	 * @param paramContext
	 * @return
	 */
	public static Intent genIsLoginIntent(int paramInt, int paramInt2,
			Context paramContext) {
		Intent localIntent = new Intent();
		if (genIsActiveIntent(paramInt, paramInt2, paramContext)) {
			if (!TradeUtil.checkUserLogin()) {// 没有登录则跳转到登录界面
				localIntent.putExtra("menu_id", paramInt);
				localIntent.setClass(paramContext, LoginActivity.class);
			} else {
				localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				String orgid = TradeUser.getInstance().getOrgid();
				switch (paramInt2) {
				case Global.NJZQ_JLP_JYYH:
					localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					localIntent.putExtra("menu_id", paramInt2);
					if (1 == TradeUser.getInstance().getLoginType()) {
						localIntent = null;
					} else {
						localIntent.setClass(paramContext, LoginActivity.class);
					}
					break;
				case Global.NJZQ_WTJY:
					localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					localIntent.putExtra("menu_id", paramInt2);
					if (orgid == null || "".equals(orgid)) {
						localIntent.setClass(paramContext, LoginActivity.class);
					} else {
						localIntent.setClass(paramContext, JlpActivity.class);
					}
					break;
				case Global.NJZQ_WTJY_TWO:
					localIntent.putExtra("menu_id", Global.NJZQ_WTJY_GP_THREE);
					localIntent.setClass(paramContext, FundActivity.class);
					break;
				case Global.QUOTE_USERSTK:
					localIntent.putExtra("requestType", 1);
					localIntent.setClass(paramContext, PersonalStock.class);
					break;
				case Global.QUOTE_WARNING:
				case Global.NJZQ_HQBJ_HQYJ:
					if (TradeUser.getInstance().getLoginType() == 3) {
						localIntent.putExtra("menu_id", paramInt);
						localIntent.setClass(paramContext, LoginActivity.class);
					} else
						localIntent.setClass(paramContext, QuoteWarning.class);
					break;
				case Global.NJZQ_ZXHD_EGHT:
					localIntent.putExtra("menu_id", Global.NJZQ_ZXHD_EGHT);
					localIntent.setClass(paramContext, VistualTrade.class);
					break;
				case Global.QUOTE_PAIMING:
					localIntent.putExtra("requestType", 0);
					localIntent.setClass(paramContext, PaiMing.class);
					break;
				case Global.QUOTE_DAPAN:
					localIntent.putExtra("requestType", 2);
					localIntent.setClass(paramContext, DaPan.class);
					break;
				case Global.NJZQ_HQBJ_QQSC_WHSC:
					localIntent.putExtra("menu_id", Global.NJZQ_HQBJ_QQSC_WHSC);
					localIntent.setClass(paramContext, GlobalMarket.class);
					break;
				case Global.QUOTE_FENLEI:
					localIntent.putExtra("requestType", 2);
					localIntent.setClass(paramContext, FenLei.class);
					break;
				case Global.QUOTE_STOCK:
					localIntent.putExtra("type", 0);
					localIntent.setClass(paramContext, StockTypeFund.class);
					break;
				case Global.QUOTE_BOND:
					localIntent.putExtra("type", 1);
					localIntent.setClass(paramContext, StockTypeFund.class);
					break;
				case Global.QUOTE_MONETARY:
					localIntent.putExtra("type", 2);
					localIntent.setClass(paramContext, StockTypeFund.class);
					break;
				case Global.QUOTE_MIX:
					localIntent.putExtra("type", 3);
					localIntent.setClass(paramContext, StockTypeFund.class);
					break;
				case Global.SUN_PRIVATE:
					localIntent.putExtra("type", 4);
					localIntent.setClass(paramContext, SunPrivate.class);
					break;
				case Global.HK_MAINBOARD: // 香港主板
					Bundle bundle = new Bundle();
					bundle.putInt("stocktype", 101);
					bundle.putInt("flag", 1);
					localIntent.putExtras(bundle);
					localIntent.setClass(paramContext, HKMainboard.class);
					break;
				case Global.HK_CYB: // 香港创业板
					Bundle bundle2 = new Bundle();
					bundle2.putInt("stocktype", 102);
					bundle2.putInt("flag", 2);
					localIntent.putExtras(bundle2);
					localIntent.setClass(paramContext, HKMainboard.class);
					break;

				case Global.ZJS:// 中金所
					localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					localIntent.setClass(paramContext, ZJS.class);

					Bundle extras = new Bundle();
					extras.putString("market", "cffex");
					extras.putString("exchange", "cf");
					extras.putString("title", "股指期货");
					localIntent.putExtras(extras);
					break;
				case Global.SDZ:// 上期所,大商所,郑商所
					localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					localIntent.setClass(paramContext, QHSCBaseActivity.class);
					break;
				case Global.QUOTE_HSZS:
					localIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
					localIntent.setClass(paramContext, HSZS.class);
					break;

				case Global.QUOTE_KLINE:
					localIntent.setClass(paramContext, KLineActivity.class);
					break;
				case Global.QUOTE_FENSHI:
					localIntent.putExtra("exchange", CssSystem.exchange);
					localIntent.putExtra("stockcode", CssSystem.stockcode);
					localIntent.putExtra("stockname", CssSystem.stockname);
					localIntent.setClass(paramContext, TrendActivity.class);
					break;
					
				case Global.NJZQ_ZXHD_KHJL:
		    		localIntent.putExtra("pos", Global.NJZQ_ZXHD_KHJL);
					localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		    		localIntent.setClass(paramContext, WebViewDisplay.class);
		    		break;
		    	case Global.NJZQ_ZXHD_TZGW:
		    		if(TradeUser.getInstance().getLoginType()==3) {
						localIntent.putExtra("menu_id", paramInt);
						localIntent.setClass(paramContext, LoginActivity.class);
					}
					else {
			    		localIntent.putExtra("pos", Global.NJZQ_ZXHD_TZGW);
						localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			    		localIntent.setClass(paramContext, WebViewDisplay.class);
					}
		    		break;
				}
			}
		} else {
			localIntent.putExtra("menu_id", paramInt);
			if (paramInt == Global.NJZQ_WTJY) {
				localIntent.putExtra("isChangeBtn", true);
			}
			localIntent.setClass(paramContext, SMSJHActivity.class);
		}
		return localIntent;
	}

	/**
	 * 判断是否激活
	 * 
	 * @param paramInt
	 * @param paramInt2
	 * @param paramContext
	 * @return
	 */
	public static boolean genIsActiveIntent(int paramInt, int paramInt2,
			Context paramContext) {
		SharedPreferences sharedPreferences = paramContext
				.getSharedPreferences("mobile", Context.MODE_PRIVATE);
		boolean flag = sharedPreferences.getBoolean("jhSuccess", false);
		return flag;
	}

	public static void switchToWnd(int paramInt, int paramInt2,
			Context paramContext) {
		Intent localIntent = genIsLoginIntent(paramInt, paramInt2, paramContext);
		if (localIntent != null)
			paramContext.startActivity(localIntent);
	}

	/**
	 * 在网络不可用或者没有取到数据的情况下，进入了系统， 行情和交易是不允许点击的，故在此根据行情ALLSTOCK文件写入时间判断
	 */
	public static boolean isNetworkErrorGoInActivity(Context paramContext) {
		return CssIniFile.isNetworkErrorGoInActivity(paramContext);
	}
	

	protected static ProgressDialog m_pDialog;
	
	private static void loadAllStock(final int paramInt1, final int paramInt2,
			final String paramString2, final String paramString3, final Context paramContext) {
		m_pDialog = new ProgressDialog(paramContext);
		m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_pDialog.setMessage(paramContext.getResources().getString(R.string.system_update_content));
		m_pDialog.setIndeterminate(false);
		m_pDialog.show();
		new AsyncTask<Void, Void, Boolean>() {
    		/**
    		 * 此方法在后台线程执行，完成任务的主要工作，通常需要较长的时间
    		 */
			@Override
			protected Boolean doInBackground(Void... arg0) {
				boolean flag = true;
				JSONObject quoteData = null;
				try {
					quoteData = ConnService.getAllStock();	
					if(Utils.isHttpStatus(quoteData)) {
						CssIniFile.saveAllStockData(paramContext, CssIniFile.UserStockFile, quoteData.toString());
						StockInfo.initAllStock(quoteData);
						StockInfo.allStock = quoteData;
					}
					else {
						flag = Boolean.FALSE;
					}
				} catch (JSONException e) {
					flag = Boolean.FALSE;
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
					Toast.makeText(paramContext, R.string.load_data_error, Toast.LENGTH_SHORT).show();
	            }
				else {
					switchToWnd(paramInt1, paramInt2, paramString2, paramString3, paramContext);
				}
			}
    	}.execute();
	}
	
	protected static void hiddenProgress() {
		if(m_pDialog!=null)
			m_pDialog.cancel();
	}
}
