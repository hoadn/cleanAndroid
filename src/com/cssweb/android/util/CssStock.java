package com.cssweb.android.util;

import java.util.Date;

public class CssStock {
	
	private String market;
	
	private String stkcode;
	
	private String stkname;
	
	private String stkpy;
	
	private String stktype;
	
	/** 证券类别 */
	private int zqlb;

	/** 昨日收盘 */
	private double zrsp;

	/** 今日开盘 */
	private double jrkp;

	/** 最近成交 */
	private double zjcj;

	/** 成交数量 */
	private double cjsl;

	/** 成交金额 */
	private double cjje;

	/** 成交笔数 */
	private long cjbs;

	/** 最高成交 */
	private double zgcj;

	/** 最低成交 */
	private double zdcj;

	/** 市盈率1 */
	private double syl1;

	/** 市盈率2 */
	private double syl2;
	
	/** 价格升跌1 */
	private double jsd1;

	/** 价格升跌2 */
	private double jsd2;

	/** 合约持仓量 */
	private long hycc;

	/** 卖价5 */
	private double sjw5;

	/** 卖量5 */
	private long ssl5;

	private double sjw4;

	private long ssl4;

	private double sjw3;

	private long ssl3;

	private double sjw2;

	private long ssl2;

	private double sjw1;

	private long ssl1;

	/** 买价1 */
	private double bjw1;

	/** 买量1 */
	private long bsl1;

	private double bjw2;

	private long bsl2;

	private double bjw3;

	private long bsl3;

	private double bjw4;

	private long bsl4;

	private double bjw5;

	private long bsl5;

	/** 现手 */
	private long xs;
	
	/** 量比 */	
	private double lb;

	/** 换手 */
	private double hs;

	/** 涨幅 */
	private double zf;

	/** 振幅 */
	private double amp;

	/** 涨跌 */
	private double zd;

	/** 委比 */
	private double wb;

	/** 委差 */
	private long wc;

	/** 前5日成交数量之和 */
	private long sumCjsl5;

	/** 流通数量 */
	private long ltsl;

	/** 股本 */
	private double gb;

	/** 市盈 */
	private double sy;

	/** 净资 */
	private double jz;
	
	/** 每股收益 */
	private float mgsy;
	
	/** 收益的季度(1 - 4) */
	private int syjd;

	/** 涨停价 */
	private double zt;

	/** 跌停价 */
	private double dt;
	
	/** 基金净值 */
	private double nav;
	
	/** 系数(将成交量或五档买卖量换算到最小单位需要乘上这个系数,仅供行情后台程序使用) */
	private int coefficient;
	
	/** 当日是否已经收盘(0:未收 1:已收) */
	private int spDay;

	/** 行情时间 */
	private Date quoteTime;
	
	/** 沪深股市标志(沪市：1  深市：0) */
	private int exchange;

	/** 新股标志(1：新股 2：非新股) */
	private int zqzt;

	/** 上破价格 */
	private double spj;

	/** 下破价格 */
	private double xpj;
	
	/** 总量*/
	private double zl;
	
	/** 总金额*/
	private double zje;
	
	private String bsFlag ;   //买卖盘
	
	private int tp;//处理停牌的股票
	
	private String jtsy="--";   //静态市盈
	private String pjgb="--";   //平均股本
	private String zsz="--";    //总市值
	private String zb="--";     //占比%
	private String ybjj="--";   //样本均价
	private String ybsl="--";   //样本数量
	
	

	public String getJtsy() {
		return jtsy;
	}

	public void setJtsy(String jtsy) {
		this.jtsy = jtsy;
	}

	public String getPjgb() {
		return pjgb;
	}

	public void setPjgb(String pjgb) {
		this.pjgb = pjgb;
	}

	public String getZsz() {
		return zsz;
	}

	public void setZsz(String zsz) {
		this.zsz = zsz;
	}

	public String getZb() {
		return zb;
	}

	public void setZb(String zb) {
		this.zb = zb;
	}

	public String getYbjj() {
		return ybjj;
	}

	public void setYbjj(String ybjj) {
		this.ybjj = ybjj;
	}

	public String getYbsl() {
		return ybsl;
	}

	public void setYbsl(String ybsl) {
		this.ybsl = ybsl;
	}

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getStkcode() {
		return stkcode;
	}

	public void setStkcode(String stkcode) {
		this.stkcode = stkcode;
	}

	public String getStkname() {
		return stkname;
	}

	public void setStkname(String stkname) {
		this.stkname = stkname;
	}

	public String getStkpy() {
		return stkpy;
	}

	public void setStkpy(String stkpy) {
		this.stkpy = stkpy;
	}

	public int getZqlb() {
		return zqlb;
	}

	public void setZqlb(int zqlb) {
		this.zqlb = zqlb;
	}

	public double getZrsp() {
		return zrsp;
	}

	public void setZrsp(double zrsp) {
		this.zrsp = zrsp;
	}

	public double getJrkp() {
		return jrkp;
	}

	public void setJrkp(double jrkp) {
		this.jrkp = jrkp;
	}

	public double getZjcj() {
		return zjcj;
	}

	public void setZjcj(double zjcj) {
		this.zjcj = zjcj;
	}

	public double getCjsl() {
		return cjsl;
	}

	public void setCjsl(double cjsl) {
		this.cjsl = cjsl;
	}

	public double getCjje() {
		return cjje;
	}

	public void setCjje(double cjje) {
		this.cjje = cjje;
	}

	public long getCjbs() {
		return cjbs;
	}

	public void setCjbs(long cjbs) {
		this.cjbs = cjbs;
	}

	public double getZgcj() {
		return zgcj;
	}

	public void setZgcj(double zgcj) {
		this.zgcj = zgcj;
	}

	public double getZdcj() {
		return zdcj;
	}

	public void setZdcj(double zdcj) {
		this.zdcj = zdcj;
	}

	public double getSyl1() {
		return syl1;
	}

	public void setSyl1(double syl1) {
		this.syl1 = syl1;
	}

	public double getSyl2() {
		return syl2;
	}

	public void setSyl2(double syl2) {
		this.syl2 = syl2;
	}

	public double getJsd1() {
		return jsd1;
	}

	public void setJsd1(double jsd1) {
		this.jsd1 = jsd1;
	}

	public double getJsd2() {
		return jsd2;
	}

	public void setJsd2(double jsd2) {
		this.jsd2 = jsd2;
	}

	public long getHycc() {
		return hycc;
	}

	public void setHycc(long hycc) {
		this.hycc = hycc;
	}

	public double getSjw5() {
		return sjw5;
	}

	public void setSjw5(double sjw5) {
		this.sjw5 = sjw5;
	}

	public long getSsl5() {
		return ssl5;
	}

	public void setSsl5(long ssl5) {
		this.ssl5 = ssl5;
	}

	public double getSjw4() {
		return sjw4;
	}

	public void setSjw4(double sjw4) {
		this.sjw4 = sjw4;
	}

	public long getSsl4() {
		return ssl4;
	}

	public void setSsl4(long ssl4) {
		this.ssl4 = ssl4;
	}

	public double getSjw3() {
		return sjw3;
	}

	public void setSjw3(double sjw3) {
		this.sjw3 = sjw3;
	}

	public long getSsl3() {
		return ssl3;
	}

	public void setSsl3(long ssl3) {
		this.ssl3 = ssl3;
	}

	public double getSjw2() {
		return sjw2;
	}

	public void setSjw2(double sjw2) {
		this.sjw2 = sjw2;
	}

	public long getSsl2() {
		return ssl2;
	}

	public void setSsl2(long ssl2) {
		this.ssl2 = ssl2;
	}

	public double getSjw1() {
		return sjw1;
	}

	public void setSjw1(double sjw1) {
		this.sjw1 = sjw1;
	}

	public long getSsl1() {
		return ssl1;
	}

	public void setSsl1(long ssl1) {
		this.ssl1 = ssl1;
	}

	public double getBjw1() {
		return bjw1;
	}

	public void setBjw1(double bjw1) {
		this.bjw1 = bjw1;
	}

	public long getBsl1() {
		return bsl1;
	}

	public void setBsl1(long bsl1) {
		this.bsl1 = bsl1;
	}

	public double getBjw2() {
		return bjw2;
	}

	public void setBjw2(double bjw2) {
		this.bjw2 = bjw2;
	}

	public long getBsl2() {
		return bsl2;
	}

	public void setBsl2(long bsl2) {
		this.bsl2 = bsl2;
	}

	public double getBjw3() {
		return bjw3;
	}

	public void setBjw3(double bjw3) {
		this.bjw3 = bjw3;
	}

	public long getBsl3() {
		return bsl3;
	}

	public void setBsl3(long bsl3) {
		this.bsl3 = bsl3;
	}

	public double getBjw4() {
		return bjw4;
	}

	public void setBjw4(double bjw4) {
		this.bjw4 = bjw4;
	}

	public long getBsl4() {
		return bsl4;
	}

	public void setBsl4(long bsl4) {
		this.bsl4 = bsl4;
	}

	public double getBjw5() {
		return bjw5;
	}

	public void setBjw5(double bjw5) {
		this.bjw5 = bjw5;
	}

	public long getBsl5() {
		return bsl5;
	}

	public void setBsl5(long bsl5) {
		this.bsl5 = bsl5;
	}

	public long getXs() {
		return xs;
	}

	public void setXs(long xs) {
		this.xs = xs;
	}

	public double getLb() {
		return lb;
	}

	public void setLb(double lb) {
		this.lb = lb;
	}

	public double getHs() {
		return hs;
	}

	public void setHs(double hs) {
		this.hs = hs;
	}

	public double getZf() {
		return zf;
	}

	public void setZf(double zf) {
		this.zf = zf;
	}

	public double getAmp() {
		return amp;
	}

	public void setAmp(double amp) {
		this.amp = amp;
	}

	public double getZd() {
		return zd;
	}

	public void setZd(double zd) {
		this.zd = zd;
	}

	public double getWb() {
		return wb;
	}

	public void setWb(double wb) {
		this.wb = wb;
	}

	public long getWc() {
		return wc;
	}

	public void setWc(long wc) {
		this.wc = wc;
	}

	public long getSumCjsl5() {
		return sumCjsl5;
	}

	public void setSumCjsl5(long sumCjsl5) {
		this.sumCjsl5 = sumCjsl5;
	}

	public long getLtsl() {
		return ltsl;
	}

	public void setLtsl(long ltsl) {
		this.ltsl = ltsl;
	}

	public double getGb() {
		return gb;
	}

	public void setGb(double gb) {
		this.gb = gb;
	}

	public double getSy() {
		return sy;
	}

	public void setSy(double sy) {
		this.sy = sy;
	}

	public double getJz() {
		return jz;
	}

	public void setJz(double jz) {
		this.jz = jz;
	}

	public float getMgsy() {
		return mgsy;
	}

	public void setMgsy(float mgsy) {
		this.mgsy = mgsy;
	}

	public int getSyjd() {
		return syjd;
	}

	public void setSyjd(int syjd) {
		this.syjd = syjd;
	}

	public double getZt() {
		return zt;
	}

	public void setZt(double zt) {
		this.zt = zt;
	}

	public double getDt() {
		return dt;
	}

	public void setDt(double dt) {
		this.dt = dt;
	}

	public double getNav() {
		return nav;
	}

	public void setNav(double nav) {
		this.nav = nav;
	}

	public int getCoefficient() {
		return coefficient;
	}

	public void setCoefficient(int coefficient) {
		this.coefficient = coefficient;
	}

	public int getSpDay() {
		return spDay;
	}

	public void setSpDay(int spDay) {
		this.spDay = spDay;
	}

	public Date getQuoteTime() {
		return quoteTime;
	}

	public void setQuoteTime(Date quoteTime) {
		this.quoteTime = quoteTime;
	}

	public int getExchange() {
		return exchange;
	}

	public void setExchange(int exchange) {
		this.exchange = exchange;
	}

	public int getZqzt() {
		return zqzt;
	}

	public void setZqzt(int zqzt) {
		this.zqzt = zqzt;
	}

	public double getSpj() {
		return spj;
	}

	public void setSpj(double spj) {
		this.spj = spj;
	}

	public double getXpj() {
		return xpj;
	}

	public void setXpj(double xpj) {
		this.xpj = xpj;
	}

	public double getZl() {
		return zl;
	}

	public void setZl(double zl) {
		this.zl = zl;
	}

	public double getZje() {
		return zje;
	}

	public void setZje(double zje) {
		this.zje = zje;
	}

	public String getStktype() {
		return stktype;
	}

	public void setStktype(String stktype) {
		this.stktype = stktype;
	}

	public String getBsFlag() {
		return bsFlag;
	}

	public void setBsFlag(String bsFlag) {
		this.bsFlag = bsFlag;
	}

	public int getTp() {
		return tp;
	}

	public void setTp(int tp) {
		this.tp = tp;
	}
	
	
}
