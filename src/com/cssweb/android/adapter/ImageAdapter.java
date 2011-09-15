package com.cssweb.android.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.cssweb.android.common.Global;
import com.cssweb.android.main.R;
import com.cssweb.android.session.TradeUser;

public class ImageAdapter extends BaseAdapter {
	private int height;

	// 定义Context
	private Context mContext;
	
	private int loginType;
	
	private Integer[] mThumbIds;
	
	private ImageView imageview = null;
	
	private Bitmap bmp = null;
	
	// 定义整型数组 即图片源
	private final Integer[] IMAGE_NZBD = { R.drawable.njzq_zx_jxgpc, R.drawable.njzq_zx_tzzh,
			R.drawable.njzq_zx_zqyj, R.drawable.njzq_zx_qhyj, R.drawable.njzq_zx_cfpd, };
	
	private final Integer[] IMAGE_HQBJ = { R.drawable.njzq_hq_hqyj, R.drawable.njzq_hq_zxg, R.drawable.njzq_hq_dpzs,
			R.drawable.njzq_hq_zhpm, R.drawable.njzq_hq_ggcx, R.drawable.njzq_hq_flbj, R.drawable.njzq_hq_cwjj, 
			R.drawable.njzq_hq_qqsc, R.drawable.njzq_hq_qhhq, };//
	
	private final Integer[] IMAGE_ZXHD = { R.drawable.njzq_hd_zjjp, R.drawable.njzq_hd_wdtzgw,
			R.drawable.njzq_hd_wdkhjl, R.drawable.njzq_hd_zxkf, R.drawable.njzq_hd_kfrx, 
			R.drawable.njzq_hd_tsjy, R.drawable.njzq_hd_cjwt, R.drawable.njzq_hd_fzjy,};
	
	private final Integer[] IMAGE_ZSYYT = { R.drawable.njzq_zsyyt_yykh, R.drawable.njzq_zsyyt_yytgg,
			R.drawable.njzq_zsyyt_yywd, R.drawable.njzq_zsyyt_ywzx, R.drawable.njzq_zsyyt_tzzjy, };
	
	private final Integer[] IMAGE_ZSYYT_2 = { R.drawable.njzq_zsyyt_yykh, R.drawable.njzq_zsyyt_yytgg,
			R.drawable.njzq_zsyyt_yywd, R.drawable.njzq_zsyyt_ywzx, R.drawable.njzq_zsyyt_tzzjy,
			R.drawable.njzq_zsyyt_tjkh, };
	
	
	
	private final Integer[] IMAGE_NZFC = { R.drawable.njzq_nzfc_nzdt,
			R.drawable.njzq_nzfc_zjnz, R.drawable.njzq_nzfc_jytd, };
	
	private final Integer[] IMAGE_JFSC = { R.drawable.njzq_zsyyt_yykh, R.drawable.njzq_zsyyt_yytgg,
			R.drawable.njzq_zsyyt_yywd, R.drawable.njzq_zsyyt_ywzx, R.drawable.njzq_zsyyt_tzzjy, };
	
	private final Integer[] IMAGE_ZXLP = { R.drawable.njzq_zx_cpbd, R.drawable.njzq_zx_ywzj,
			R.drawable.njzq_zx_pzhh, R.drawable.njzq_zx_tgnc, R.drawable.njzq_zx_zjlx, R.drawable.njzq_zx_gg, };

	private final Integer[] IMAGE_WTJY_ONE = { R.drawable.njzq_jy_zhgl, R.drawable.njzq_jy_gp_buy, R.drawable.njzq_jy_gp_sail,
			R.drawable.njzq_jy_gp_cd, R.drawable.njzq_jy_yzzz,R.drawable.njzq_jy_gp_drwt,
			R.drawable.njzq_jy_gp_drcj, R.drawable.njzq_jy_gp_cccx ,R.drawable.njzq_jy_gp_zhzc};
	
	private final Integer[] IMAGE_WTJY_TWO = { R.drawable.njzq_jy_gp_zjls, R.drawable.njzq_jy_gp_lscx,
			R.drawable.njzq_hq_cwjj, R.drawable.njzq_jy_gp_cnjj, R.drawable.njzq_jy_szlc, 
			R.drawable.njzq_jy_rzrq,R.drawable.njzq_jy_gp_phcx, R.drawable.njzq_jy_gp_xgmm,R.drawable.njzq_jy_gp_gdzl};
	
	private final Integer[] IMAGE_WTJY_THREE = { R.drawable.njzq_jy_gp_xxxg };//, R.drawable.njzq_jy_gzqh
	
	private final Integer[] IMAGE_WTJY_GP_ONE = { R.drawable.njzq_jy_jj_cnrg, R.drawable.njzq_jy_jj_cnsg,
			R.drawable.njzq_jy_jj_cnsh, };
	
	private final Integer[] IMAGE_WTJY_GP_TWO = { R.drawable.njzq_jy_jj_lswt, R.drawable.njzq_jy_jj_lscj,
			R.drawable.njzq_jy_gp_zjls, R.drawable.njzq_jy_gp_phcx, R.drawable.njzq_jy_gp_xgmm,
			R.drawable.njzq_jy_gp_xxxg, R.drawable.njzq_jy_gp_gdzl, };
	
	private final Integer[] IMAGE_WTJY_GP_THREE = { R.drawable.njzq_jy_jj_jjsg, R.drawable.njzq_jy_jj_jjsh, 
			R.drawable.njzq_jy_jj_jjrg, R.drawable.njzq_jy_gp_cd, R.drawable.njzq_jy_jj_drwt, R.drawable.njzq_jy_jj_jjtrans, 
			R.drawable.njzq_jy_jj_lscj, R.drawable.njzq_jy_jj_lswt, R.drawable.njzq_jy_jj_ccjj, };
	
	private final Integer[] IMAGE_WTJY_GP_FOUR = { R.drawable.njzq_jy_jj_fhsz, R.drawable.njzq_jy_jj_jjaccount, 
			R.drawable.njzq_jy_jj_jjkh, R.drawable.njzq_jy_gp_fxcp, 
			};
	
	private final Integer[] IMAGE_WTJY_YZYW = { R.drawable.njzq_yzyw_zjzr, R.drawable.njzq_yzyw_zjzc,
			R.drawable.njzq_yzyw_yhye, R.drawable.njzq_yzyw_zhls,R.drawable.njzq_jy_zjdb };
	
	private final Integer[] IMAGE_WTJY_ZJDB = { R.drawable.njzq_jy_zjcx, R.drawable.njzq_jy_zfzz,
			R.drawable.njzq_jy_fzzz, R.drawable.njzq_jy_dbls, };
	
	private final Integer[] IMAGE_HQBJ_QJSC = { R.drawable.njzq_hq_gghq, R.drawable.njzq_hq_wwsc, R.drawable.njzq_hq_qqhs, };
	private final Integer[] IMAGE_HQBJ_QHHQ = { R.drawable.njzq_hq_zjs, R.drawable.njzq_hq_sqs, R.drawable.njzq_hq_dss, R.drawable.njzq_hq_zss, R.drawable.njzq_hq_qqsp, };

	private final Integer[] IMAGE_HQBJ_GGHQ = { R.drawable.njzq_hq_hszs, R.drawable.njzq_hq_hkzb, R.drawable.njzq_hq_hkcyb, R.drawable.njzq_hq_ggcx, };
	
	private final Integer[] IMAGE_HQBJ_CWJJ = { R.drawable.njzq_hq_cwjj_gp, R.drawable.njzq_hq_cwjj_zqx,
			R.drawable.njzq_hq_cwjj_hbx, R.drawable.njzq_hq_cwjj_hhx, 
			//R.drawable.njzq_hq_cwjj_jjcx, 
			 R.drawable.njzq_hq_cwjj_jzcx, R.drawable.njzq_hq_cwjj_ygsm, };
	
	private final Integer[] IMAGE_ZXHD_FZJY = { R.drawable.njzq_hd_fzjy_ssgg, R.drawable.njzq_hd_fzjy_ckbs,
			R.drawable.njzq_hd_fzjy_phb, R.drawable.njzq_hd_fzjy_cjwt, R.drawable.njzq_hd_fzjy_zc, 
			R.drawable.njzq_hd_fzjy_login, };
	
	private final Integer[] IMAGE_ZXHD_FZJY_2 = { R.drawable.njzq_hd_fzjy_ssgg, R.drawable.njzq_hd_fzjy_ckbs,
			R.drawable.njzq_hd_fzjy_phb, R.drawable.njzq_hd_fzjy_cjwt, R.drawable.njzq_hd_fzjy_wdbs, 
			R.drawable.njzq_hd_fzjy_qxsz, };
	
	private final Integer[] IMAGE_ZXHD_FZJY_3 = { R.drawable.njzq_hd_fzjy_ssgg, R.drawable.njzq_hd_fzjy_ckbs,
			R.drawable.njzq_hd_fzjy_phb, R.drawable.njzq_hd_fzjy_cjwt, R.drawable.njzq_hd_fzjy_wdbs, 
			R.drawable.njzq_hd_fzjy_xgmm, };
	
	private final Integer[] IMAGE_NZBD_JXZQC = { R.drawable.njzq_nzbd_jcc, R.drawable.njzq_nzbd_hxc, };
	
	private final Integer[] IMAGE_NZBD_CFPD = { R.drawable.njzq_nzbd_mrjp, R.drawable.njzq_nzbd_tzlt, R.drawable.njzq_nzbd_ztpx,};
	
	public ImageAdapter(Context context) {
		mContext = context;
	}

	public ImageAdapter(Context context, int i, int j) {
		mContext = context;
		height = i;
		switch(j) {
			case Global.NJZQ_NZBD:
				mThumbIds = new Integer[IMAGE_NZBD.length];
				mThumbIds = IMAGE_NZBD;
				break;
			case Global.NJZQ_HQBJ:
				mThumbIds = new Integer[IMAGE_HQBJ.length];
				mThumbIds = IMAGE_HQBJ;
				break;
			case Global.NJZQ_WTJY:
				mThumbIds = new Integer[IMAGE_WTJY_ONE.length];
				mThumbIds = IMAGE_WTJY_ONE;
				break;
			case Global.NJZQ_ZXHD:
				mThumbIds = new Integer[IMAGE_ZXHD.length];
				mThumbIds = IMAGE_ZXHD;
				break;
			case Global.NJZQ_ZSYYT:
				loginType = TradeUser.getInstance().getLoginType();
				if(loginType==1||loginType==2) {
					mThumbIds = new Integer[IMAGE_ZSYYT_2.length];
					mThumbIds = IMAGE_ZSYYT_2;
				}else {
					mThumbIds = new Integer[IMAGE_ZSYYT.length];
					mThumbIds = IMAGE_ZSYYT;
				}
				break;
			case Global.NJZQ_NZFC:
				mThumbIds = new Integer[IMAGE_NZFC.length];
				mThumbIds = IMAGE_NZFC;
				break;
			case Global.NJZQ_JFSC:
				mThumbIds = new Integer[IMAGE_JFSC.length];
				mThumbIds = IMAGE_JFSC;
				break;
			case Global.NJZQ_ZXLP:
				mThumbIds = new Integer[IMAGE_ZXLP.length];
				mThumbIds = IMAGE_ZXLP;
				break;
			case Global.NJZQ_WTJY_GP_ONE:
				mThumbIds = new Integer[IMAGE_WTJY_GP_ONE.length];
				mThumbIds = IMAGE_WTJY_GP_ONE;
				break;
			case Global.NJZQ_WTJY_GP_TWO:
				mThumbIds = new Integer[IMAGE_WTJY_GP_TWO.length];
				mThumbIds = IMAGE_WTJY_GP_TWO;
				break;
			case Global.NJZQ_WTJY_GP_THREE:
				mThumbIds = new Integer[IMAGE_WTJY_GP_THREE.length];
				mThumbIds = IMAGE_WTJY_GP_THREE;
				break;
			case Global.NJZQ_WTJY_FOUR:
				mThumbIds = new Integer[IMAGE_WTJY_GP_FOUR.length];
				mThumbIds = IMAGE_WTJY_GP_FOUR;
				break;
			case Global.NJZQ_WTJY_FIVE:
				mThumbIds = new Integer[IMAGE_WTJY_YZYW.length];
				mThumbIds = IMAGE_WTJY_YZYW;
				break;
			case Global.NJZQ_WTJY_ZJDB:
				mThumbIds = new Integer[IMAGE_WTJY_ZJDB.length];
				mThumbIds = IMAGE_WTJY_ZJDB;
				break;
			case Global.NJZQ_HQBJ_FIVE:
				mThumbIds = new Integer[IMAGE_HQBJ_QJSC.length];
				mThumbIds = IMAGE_HQBJ_QJSC;
				break;
			case Global.NJZQ_HQBJ_EGHT:
				mThumbIds = new Integer[IMAGE_HQBJ_CWJJ.length];
				mThumbIds = IMAGE_HQBJ_CWJJ;
				break;
			case Global.NJZQ_HQBJ_QHHQ:
				mThumbIds = new Integer[IMAGE_HQBJ_QHHQ.length];
				mThumbIds = IMAGE_HQBJ_QHHQ;
				break;
			case Global.NJZQ_HQBJ_GGHQ:
				mThumbIds = new Integer[IMAGE_HQBJ_GGHQ.length];
				mThumbIds = IMAGE_HQBJ_GGHQ;
				break;
			case Global.NJZQ_ZXHD_EGHT:
				loginType = TradeUser.getInstance().getLoginType();
				if(loginType==1||loginType==2||loginType==3) {
					mThumbIds = new Integer[IMAGE_ZXHD_FZJY_2.length];
					mThumbIds = IMAGE_ZXHD_FZJY_2;
				}
				else if(loginType==4) {
					mThumbIds = new Integer[IMAGE_ZXHD_FZJY_3.length];
					mThumbIds = IMAGE_ZXHD_FZJY_3;
				}
				else {
					mThumbIds = new Integer[IMAGE_ZXHD_FZJY.length];
					mThumbIds = IMAGE_ZXHD_FZJY;
				}
				break;
			case Global.NJZQ_WTJY_ONE:
				mThumbIds = new Integer[IMAGE_WTJY_ONE.length];
				mThumbIds = IMAGE_WTJY_ONE;
				break;
			case Global.NJZQ_WTJY_TWO:
				mThumbIds = new Integer[IMAGE_WTJY_TWO.length];
				mThumbIds = IMAGE_WTJY_TWO;
				break;
			case Global.NJZQ_WTJY_THREE:
				mThumbIds = new Integer[IMAGE_WTJY_THREE.length];
				mThumbIds = IMAGE_WTJY_THREE;
				break;
			case Global.NJZQ_NZBD_JXZQC:
				mThumbIds = new Integer[IMAGE_NZBD_JXZQC.length];
				mThumbIds = IMAGE_NZBD_JXZQC;
				break;
			case Global.NJZQ_NZBD_CFPD:
				mThumbIds = new Integer[IMAGE_NZBD_CFPD.length];
				mThumbIds = IMAGE_NZBD_CFPD;
				break;
				
		}
	}

	// 获取图片的个数
	public int getCount() {
		return mThumbIds.length;
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		return position;
	}

	public View getView(int position, View view, ViewGroup parent) {
		if (view == null) {
			Context context = mContext;
			imageview = new ImageView(context);
			int j = height;
			android.widget.AbsListView.LayoutParams layoutparams = new android.widget.AbsListView.LayoutParams(
					-1, j);
			imageview.setLayoutParams(layoutparams);
			android.widget.ImageView.ScaleType scaletype = android.widget.ImageView.ScaleType.FIT_CENTER;
			imageview.setScaleType(scaletype);
			BitmapFactory.Options opt = new BitmapFactory.Options();
			opt.inPreferredConfig = Bitmap.Config.RGB_565;   
			opt.inPurgeable = true;
			opt.inInputShareable = true;
			bmp = BitmapFactory.decodeStream(mContext.getResources().openRawResource(mThumbIds[position]), null, opt);
			imageview.setImageBitmap(bmp);
		} else {
			imageview = (ImageView) view;
		}
		
		return imageview;
	}
	
	protected class Holder {
		public ImageView imageView;
	}

	public void recycle() {
//		Log.i("#######image recycle########", bmp.isRecycled()+">>>>>>>>>>>>>>>>>>>>>>");
		if(bmp!=null&&!bmp.isRecycled()) {
			//
			bmp.recycle();
			bmp = null;
			System.gc();
		}
	}
}
