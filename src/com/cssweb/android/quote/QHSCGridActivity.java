/**
 * Copyright 2010 CssWeb Microsystems, Inc. All rights reserved.
 * CssWeb PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 * @(#)QuoteGridActivity.java 上午09:40:57 2011-3-7
 */
package com.cssweb.android.quote;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cssweb.android.base.FlipperActiviy;
import com.cssweb.android.common.FairyUI;
import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.util.CssStock;
import com.cssweb.android.util.CssSystem;
import com.cssweb.quote.util.NameRule;
import com.cssweb.quote.util.Utils;

/**
 * 期货市场表格实现部分
 * 
 * @author
 * @version 1.0
 * @see
 * @since 1.0
 */
public class QHSCGridActivity extends FlipperActiviy {
	private Context mContext = QHSCGridActivity.this;

	protected List<CssStock> list = new ArrayList<CssStock>();

	private View.OnClickListener mClickListener;
	private View.OnLongClickListener mLongClickListener;
	private int mFontSize = 18;
	private LinearLayout mLinerHScroll;
	private LinearLayout mLinerLock;
	private LinearLayout mLinerLock2;
	private int residCol;
	private int m_nPos = 0;
	private int[] residScrollCol;
	private int residSelColor;
	private int residTitleCol;
	private int[] residTitleScrollCol;

	protected int rowHeight = 0;
	protected int len = 0;

	private boolean nameOrcode = true;
	private boolean nameOrCodeFlag = true;

	private String[] temp1;
	private String[] temp2;

	protected int n1 = 2, n2 = 1;
	private String top = "▽";
	private String low = "△";

	protected int selectTag = -1;

	protected ArrayList<String[]> stringList;

	public QHSCGridActivity() {
		int[] arrayOfInt1 = new int[3];
		this.residTitleScrollCol = arrayOfInt1;
		this.residTitleCol = 0;
		this.residCol = 0;
		int[] arrayOfInt2 = new int[3];
		this.residScrollCol = arrayOfInt2;
		this.residSelColor = Color.DKGRAY;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.residTitleCol = R.drawable.zrfundquery01;
		this.residTitleScrollCol[0] = R.drawable.zrfundqueryh02;
		this.residTitleScrollCol[1] = R.drawable.zrfundqueryh03;
		this.residTitleScrollCol[2] = R.drawable.zrfundqueryh04;
		this.residCol = R.drawable.zrfundquery02;
		this.residScrollCol[0] = R.drawable.zrfundqueryh05;
		this.residScrollCol[1] = R.drawable.zrfundqueryh06;
		this.residScrollCol[2] = R.drawable.zrfundqueryh07;
		mClickListener = new TextClick();
		mLongClickListener = new TextLongClick();
	}

	// 初始化空表格
	protected void initBlankTable(int pageNum, int length,
			boolean nameOrCodeFlag) throws JSONException {
		LinearLayout localLinearLayout1 = (LinearLayout) this
				.findViewById(R.id.zr_htable_lock1);
		LinearLayout localLinearLayout2 = (LinearLayout) this
				.findViewById(R.id.zr_htable_lock2);
		LinearLayout localLinearLayout = (LinearLayout) this
				.findViewById(R.id.zr_htable_linearlayout);

		this.mLinerLock = localLinearLayout1;
		this.mLinerLock2 = localLinearLayout2;
		this.mLinerHScroll = localLinearLayout;
		this.mLinerLock.removeAllViews();
		this.mLinerLock2.removeAllViews();
		this.mLinerHScroll.removeAllViews();

		this.nameOrCodeFlag = nameOrCodeFlag;

		if (nameOrCodeFlag) {// 只显示一行，还是显示二行(代码名称都显示出来)
			localLinearLayout2.setVisibility(View.GONE);
			if (nameOrcode)
				AddViewItem(Utils.getTextColor(mContext, 0), mLinerLock, -100,
						0, 0, true);
			else
				AddViewItem(Utils.getTextColor(mContext, 0), mLinerLock, -100,
						0, 0, true);
		} else {
			AddViewItem(Utils.getTextColor(mContext, 0), mLinerLock, 0, 0, 0,
					true);
			AddViewItem(Utils.getTextColor(mContext, 0), mLinerLock2, -1, 0, 0,
					true);
		}

		LinearLayout l1 = new LinearLayout(this);
		for (int i = 2; i < length; i++) {
			if (i == length - 1)
				AddViewItem(Utils.getTextColor(mContext, 0), l1, -i, 100, 0,
						true);
			else
				AddViewItem(Utils.getTextColor(mContext, 0), l1, -i, i - 1, 0,
						true);
		}
		l1.setTag(0);
		mLinerHScroll.addView(l1);
		for (int i = 1; i <= pageNum; i++) {
			if (nameOrCodeFlag) {
				localLinearLayout2.setVisibility(View.GONE);
				if (nameOrcode)
					AddViewItem(Utils.getTextColor(mContext, 1), mLinerLock, i,
							0, i, true);
				else
					AddViewItem(Utils.getTextColor(mContext, 1), mLinerLock, i,
							0, i, true);
			} else {
				AddViewItem(Utils.getTextColor(mContext, 1), mLinerLock, i, 0,
						i, true);
				AddViewItem(Utils.getTextColor(mContext, 1), mLinerLock2, i, 0,
						i, true);
			}

			l1 = new LinearLayout(this);
			l1.setTag(i);
			for (int j = 1; j <= length - 2; j++) {
				if (j == length - 2)
					AddViewItem(Utils.getTextColor(mContext, 0), l1, i, 100, i,
							true);
				else
					AddViewItem(Utils.getTextColor(mContext, 0), l1, i, j, i,
							true);
			}
			mLinerHScroll.addView(l1);
		}
	}

	protected void initTitle(int pageNum, String[] cols, boolean nameOrCodeFlag) {
		TextView localView6 = (TextView) this.mLinerLock.getChildAt(0);
		TextView localView7 = (TextView) this.mLinerLock2.getChildAt(0);

		if (n2 == 0) {
			String str = (n1 == 0) ? cols[0] + low : (n1 == 1) ? cols[0] + top
					: cols[0];
			localView6.setText(str);
		} else {
			localView6.setText(cols[0]);
		}

		localView7.setText(cols[1]);

		LinearLayout localLinearLayout = (LinearLayout) this.mLinerHScroll
				.findViewWithTag(0);
		int i6 = localLinearLayout.getChildCount();
		for (int j = 0; j < i6; j++) {
			TextView localView8 = (TextView) localLinearLayout.getChildAt(j);
			if (n2 == -(j + 2)) {
				String str = (n1 == 0) ? cols[j + 2] + low
						: (n1 == 1) ? cols[j + 2] + top : cols[j + 2];
				localView8.setText(str);
			} else {
				localView8.setText(cols[j + 2]);
			}
		}
	}

	// 初始化证券名称及代号
	protected void initQuote(int pageNum, String stocks, String stocksname)
			throws JSONException {
		this.temp1 = stocks.split(",");
		this.temp2 = stocksname.split(",");
		try {
			if (null != temp1 && null != temp2 && !"".equals(temp1)
					&& !"".equals(temp2)) {
				for (int i = 1; i <= temp1.length; i++) {
					TextView localView6 = (TextView) this.mLinerLock
							.findViewWithTag(i);
					TextView localView7 = (TextView) this.mLinerLock2
							.findViewWithTag(i);
					if (localView7 == null || localView6 == null) {
						Log.i("tag", "localView7 is " + localView7);
						Log.i("tag", "localView6 is " + localView6);
					}
					if (nameOrCodeFlag) {// 只显示一行，还是显示二行(代码名称都显示出来)
						if (nameOrcode)
							localView6.setText(temp2[i - 1]);
						else {
							if (temp1[i - 1].length() > 2) {
								localView6.setText(temp1[i - 1].substring(2));
							}
						}
					} else {
						if (temp2[i - 1].length() > 5) {
							localView7.setTextSize(16);
						}
						if (temp1[i - 1].length() > 2) {
							if (temp1[i - 1].substring(2).length() > 5) {
								localView6.setTextSize(16);
							}

							localView6.setText(temp1[i - 1].substring(2));
							localView7.setText(temp2[i - 1]);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void clearData(int scrollNum) {
		for (int i = 1; i <= scrollNum; i++) {
			TextView localView6 = (TextView) this.mLinerLock.findViewWithTag(i);
			TextView localView7 = (TextView) this.mLinerLock2
					.findViewWithTag(i);
			localView6.setText("");
			localView7.setText("");

			LinearLayout localLinearLayout = (LinearLayout) this.mLinerHScroll
					.findViewWithTag(i);
			int i6 = localLinearLayout.getChildCount();
			for (int j = 0; j < i6; j++) {
				TextView localView5 = (TextView) localLinearLayout
						.getChildAt(j);
				localView5.setText("");
			}
		}
	}

	protected void initQuote() {
	};

	// 刷新表格
	protected void refreshTable(List<String[]> list, int scrollNum,
			String exchange) throws JSONException {
		double d0 = 0;
		clearData(scrollNum);

		if (null != list) {
			initQuote();

			for (int i = 1; i <= list.size(); i++) {
				try {
					String[] cs = list.get(i - 1);
					d0 = Double.parseDouble(cs[cs.length - 1]);
					// 先判断小数位数
					int digit = Utils.getStockDigit(NameRule.getSecurityType(
							exchange, cs[cs.length - 3]));

					LinearLayout localLinearLayout = (LinearLayout) this.mLinerHScroll
							.findViewWithTag(i);
					int i6 = localLinearLayout.getChildCount();
					for (int j = 0; j < i6; j++) {
						TextView localView6 = (TextView) localLinearLayout
								.getChildAt(j);
						// Log.i("tag", "cs[j]"+cs[j]);
						double zf = Double.parseDouble(cs[j]);

						if (j == 0) {// 现价
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf, d0));
							localView6.setText(Utils
									.dataFormation(zf, digit, 0));
						} else if (j == 2) {// 涨跌
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf));
							localView6.setText(Utils
									.dataFormation(zf, digit));
						} else if (j == 1) {// 涨幅
							String str1;
							if (zf == 0)
								str1 = Utils.dataFormation(zf * 100, 1);
							else
								str1 = Utils.dataFormation(zf * 100, 1);
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf));
							localView6.setText(str1);

						} else if (j == 3 || j == 4) {// 买价，卖价
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf, d0));
							localView6.setText(Utils
									.dataFormation(zf, digit, 0));
						} else if (j == 5 || j == 6) {// 买量，卖量
							localView6.setTextColor(Utils.getTextColor(
									mContext, 1));
							localView6.setText(Utils.getAmountFormat(zf, true,
									2));
						} else if (j == 7) {// 现量
							localView6.setTextColor(Utils.getTextColor(
									mContext, Utils.getColorFromBS(cs[cs.length - 4]))); 
							localView6.setText(Utils.getAmountFormat(zf, true,
									2));
						} else if (j == 8) {// 总量
							localView6.setTextColor(Utils.getTextColor(
									mContext, 1));
							localView6.setText(Utils.getAmountFormat(zf, true,
									2));
						} else if (j == 9 || j == 10 || j == 11) {// 今天,最低，最高
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf, d0));
							localView6.setText(Utils
									.dataFormation(zf, digit));
						} else if (j == 17) {// 昨收
							localView6.setTextColor(Utils.getTextColor(
									mContext, 0));
							localView6.setText(Utils
									.dataFormation(zf, digit, 0));
						} else if (j == 12) {// 结算
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf, d0));
							localView6.setText(Utils
									.dataFormation(zf, digit, 0));
						} else if (j == 13) {// 昨结
							localView6.setTextColor(Utils.getTextColor(
									mContext, zf, d0));
							localView6.setText(Utils
									.dataFormation(zf, digit, 0));
						} else if (j == 14 || j == 15) {// 持仓，增仓
							localView6.setTextColor(Utils.getTextColor(
									mContext, 1));
							localView6.setText(Utils
									.dataFormation(zf, digit));
						} else if (j == 16) {// 总额
							localView6.setTextColor(Utils.getTextColor(
									mContext, 2));
							localView6.setText(Utils.getAmountFormat(zf, false,
									2));
						}

						// 量，总量，都是根据t值决定显示何种颜色 传1
						// 昨收，根据t值决定显示何种颜色 传0
						// //现价，买价，卖价,今开 ，最高，最低，根据昨日收盘 d0比较
						// 涨幅，涨跌, 和0比较
						// Utils.getAmountFormat(zf, false)
						// Utils.dataFormation(zf, mDigit)

						// Utils.getTextColor(mContext, zf, d0) 昨日收盘价比较
						// Utils.getTextColor(mContext, zf) 和0比较
						// Utils.getTextColor(mContext, 1) 根据t值决定显示何种颜色

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void AddViewItem(int paramInt1, LinearLayout paramLinearLayout,
			int paramInt2, int paramInt3, int paramInt4, boolean paramBoolean) {
		TextView localTextView = new TextView(this);
		float f = this.mFontSize;
		localTextView.setTextSize(f);

		localTextView.setGravity(Gravity.CENTER);
		localTextView.setFocusable(paramBoolean);
		localTextView.setOnClickListener(mClickListener);
		localTextView.setOnLongClickListener(mLongClickListener);
		localTextView.setOnTouchListener(this); // 手势上下翻页touch事件，在这里增加
		localTextView.setTag(paramInt2);
		localTextView.setEnabled(paramBoolean);
		localTextView.setSingleLine(true);
		Resources localResources = getResources();
		Drawable localDrawable = null;
		if (paramInt4 == 0 && paramInt3 >= 0) {// 表示是标题
			int i1 = this.residTitleCol;
			int i8 = 0;
			localTextView.setTextColor(paramInt1);
			if (paramInt3 == 0) {
				localDrawable = localResources.getDrawable(i1);
				i8 = localDrawable.getIntrinsicWidth();
			} else if (paramInt3 == 100) {
				localDrawable = localResources
						.getDrawable(this.residTitleScrollCol[2]);
				i8 = localDrawable.getIntrinsicWidth();
				i8 += 20;
			} else if (paramInt3 == 13) {
				localDrawable = localResources
						.getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();

			} else if (paramInt3 % 2 == 0) {
				localDrawable = localResources
						.getDrawable(this.residTitleScrollCol[1]);
				i8 = localDrawable.getIntrinsicWidth();
			} else {
				localDrawable = localResources
						.getDrawable(this.residTitleScrollCol[0]);
				i8 = localDrawable.getIntrinsicWidth();
			}
			localTextView.setBackgroundDrawable(localDrawable);
			int i6 = localDrawable.getIntrinsicHeight();
			localTextView.setHeight(i6 + CssSystem.getTableTitleHeight(this));
			localTextView.setWidth(i8);
			paramLinearLayout.addView(localTextView);
			return;
		}
		if (paramInt4 != 0 && paramInt3 >= 0) {
			int i8 = 0;
			localTextView.setTextColor(paramInt1);
			if (paramInt3 == 0) {
				localDrawable = localResources.getDrawable(this.residCol);
				i8 = localDrawable.getIntrinsicWidth();
			} else if (paramInt3 == 100) {
				localDrawable = localResources
						.getDrawable(this.residScrollCol[2]);
				localTextView.setGravity(Gravity.RIGHT
						| Gravity.CENTER_VERTICAL);
				i8 = localDrawable.getIntrinsicWidth();
				i8 += 20;
			} else if (paramInt3 == 13) {
				localDrawable = localResources
						.getDrawable(this.residScrollCol[0]);
				localTextView.setGravity(Gravity.RIGHT
						| Gravity.CENTER_VERTICAL);
				i8 = localDrawable.getIntrinsicWidth();

			} else if (paramInt3 % 2 == 0) {
				localDrawable = localResources
						.getDrawable(this.residScrollCol[1]);
				localTextView.setGravity(Gravity.RIGHT
						| Gravity.CENTER_VERTICAL);
				i8 = localDrawable.getIntrinsicWidth();
			} else {
				localDrawable = localResources
						.getDrawable(this.residScrollCol[0]);
				localTextView.setGravity(Gravity.RIGHT
						| Gravity.CENTER_VERTICAL);
				i8 = localDrawable.getIntrinsicWidth();
			}
			localTextView.setBackgroundDrawable(localDrawable);
			int i6 = localDrawable.getIntrinsicHeight();
			localTextView.setHeight(i6 + rowHeight);
			localTextView.setWidth(i8);
			paramLinearLayout.addView(localTextView);
			return;
		}
	}

	public void setSelectRow(int paramInt) throws Exception {
		LinearLayout localLinearLayout1 = this.mLinerLock;
		Integer localInteger1 = Integer.valueOf(this.m_nPos);
		View localView1 = localLinearLayout1.findViewWithTag(localInteger1);
		if (localView1 != null) {
			int l = this.residCol;
			localView1.setBackgroundResource(l);
		}
		LinearLayout localLinearLayout2 = this.mLinerLock;
		Integer localInteger2 = Integer.valueOf(paramInt);
		View localView2 = localLinearLayout2.findViewWithTag(localInteger2);
		if (localView2 != null) {
			int i1 = this.residSelColor;
			localView2.setBackgroundColor(i1);
		}

		if (true) {
			LinearLayout localLinerLock = this.mLinerLock2;
			Integer localLockInteger = Integer.valueOf(this.m_nPos);
			View localLockView = localLinerLock
					.findViewWithTag(localLockInteger);
			if (localLockView != null) {
				int l = this.residCol;
				localLockView.setBackgroundResource(l);
			}
			LinearLayout localLinerLock2 = this.mLinerLock2;
			Integer localLockInteger2 = Integer.valueOf(paramInt);
			View localLockView2 = localLinerLock2
					.findViewWithTag(localLockInteger2);
			if (localLockView2 != null) {
				int i1 = this.residSelColor;
				localLockView2.setBackgroundColor(i1);
			}
		}

		LinearLayout localLinearLayout3 = this.mLinerHScroll;
		Integer localInteger3 = Integer.valueOf(this.m_nPos);
		LinearLayout localLinearLayout4 = (LinearLayout) localLinearLayout3
				.findViewWithTag(localInteger3);

		if (localLinearLayout4 != null) {
			int i3 = localLinearLayout4.getChildCount();
			for (int i = 0; i < i3; i++) {
				View localView3 = localLinearLayout4.getChildAt(i);
				int i0 = 0;
				if (i == i3 - 1)
					i0 = this.residScrollCol[2];
				else if (i == 13)
					i0 = this.residScrollCol[1];
				else if (i % 2 == 0)
					i0 = this.residScrollCol[0];
				else
					i0 = this.residScrollCol[1];
				localView3.setBackgroundResource(i0);
			}
		}

		LinearLayout localLinearLayout5 = this.mLinerHScroll;
		Integer localInteger4 = Integer.valueOf(paramInt);
		LinearLayout localLinearLayout6 = (LinearLayout) localLinearLayout5
				.findViewWithTag(localInteger4);
		if (localLinearLayout6 != null) {
			int i4 = this.residSelColor;
			localLinearLayout6.setBackgroundColor(i4);
		}
		int i6 = localLinearLayout6.getChildCount();
		for (int i = 0; i < i6; i++) {
			View localView6 = localLinearLayout6.getChildAt(i);
			int i10 = this.residSelColor;
			localView6.setBackgroundColor(i10);
		}
		this.m_nPos = paramInt;

		if (null != stringList && stringList.size() > paramInt
				&& null != temp2[m_nPos - 1] && !"".equals(temp2[m_nPos - 1])
				&& null != temp1[m_nPos - 1] && !"".equals(temp1[m_nPos - 1])
				&& temp1[m_nPos - 1].length() > 2) {

			Log.i("tag", "stringList.length" + stringList.size()
					+ " : paramInt : " + paramInt);

			String[] data = stringList.get(paramInt - 1);
			String market = data[data.length - 2];
			cssStock = new CssStock();
			cssStock.setStkcode(temp1[m_nPos - 1].substring(2)); // 加入自选
			cssStock.setStkname(temp2[m_nPos - 1]);
			cssStock.setMarket(market);
		}
		// this.cssStock = list.get(paramInt - 1);
	}

	protected class TextClick implements View.OnClickListener {
		public void onClick(View arg0) {
			int tag = (Integer) arg0.getTag();
			try {
				if (tag > 0) {
					if (tag - 1 >= len){
						return;
					}
					if (selectTag == tag) {
						if (null != temp2[m_nPos - 1]
								&& !"".equals(temp2[m_nPos - 1])
								&& null != temp1[m_nPos - 1]
								&& !"".equals(temp1[m_nPos - 1])
								&& temp1[m_nPos - 1].length() > 2)
							FairyUI.switchToWnd(Global.QUOTE_FENSHI, NameRule
									.getExchange(temp1[m_nPos - 1].substring(0,
											2)),
									temp1[m_nPos - 1].substring(2),
									temp2[m_nPos - 1], mContext);
					} else {
						selectTag = tag;
						setSelectRow(tag);
					}
				} else if (tag != -1 && tag != 0) {
//					if (n2 != tag)
//						n1 = 2;
//					n1 = (n1 == 1) ? 0 : 1;
//					n2 = tag;
//					zqlbDesc2(tag, n1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	protected class TextLongClick implements View.OnLongClickListener {
		public boolean onLongClick(View v) {
			int tag = (Integer) v.getTag();
			try {
				if (tag > 0) {
					setSelectRow(tag);
					if (m_nPos - 1 <= len)
						if (temp2.length > 0 && temp1.length > 0) {
							if (null != temp2[m_nPos - 1]
									&& !"".equals(temp2[m_nPos - 1])
									&& null != temp1[m_nPos - 1]
									&& !"".equals(temp1[m_nPos - 1])
									&& temp1[m_nPos - 1].length() > 2) {
								FairyUI.switchToWnd(Global.QUOTE_FENSHI,
										NameRule.getExchange(temp1[m_nPos - 1]
												.substring(0, 2)),
										temp1[m_nPos - 1].substring(2),
										temp2[m_nPos - 1], mContext);
							}
						}

				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

	protected void zqlbDesc2(int t1, int t2) {

	}
}
