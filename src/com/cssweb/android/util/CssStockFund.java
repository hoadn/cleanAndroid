package com.cssweb.android.util;

public class CssStockFund {
	
	private String market;
	private String stkcode;
	private String stkfunname;  //基金名称
	private double jz;			//净值
	private double jzl;         //净值增长率
	private double bnjzl;       //本年净值增长率
	private double ljjz;		//累计净值
	private double ljjzl;       //累计净值增长率
	private double nhsyl;       //年化收益率
	private double jjtnpj ;		//基金3年评级
	private double jjfnpj ;		//基金5年评级
	
	
	public String getStkfunname() {
		return stkfunname;
	}
	public void setStkfunname(String stkfunname) {
		this.stkfunname = stkfunname;
	}
	
	
	public double getJz() {
		return jz;
	}
	public void setJz(double jz) {
		this.jz = jz;
	}
	public double getLjjz() {
		return ljjz;
	}
	public void setLjjz(double ljjz) {
		this.ljjz = ljjz;
	}
	public double getJjtnpj() {
		return jjtnpj;
	}
	public void setJjtnpj(double jjtnpj) {
		this.jjtnpj = jjtnpj;
	}
	public double getJjfnpj() {
		return jjfnpj;
	}
	public void setJjfnpj(double jjfnpj) {
		this.jjfnpj = jjfnpj;
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
	public double getJzl() {
		return jzl;
	}
	public void setJzl(double jzl) {
		this.jzl = jzl;
	}
	public double getBnjzl() {
		return bnjzl;
	}
	public void setBnjzl(double bnjzl) {
		this.bnjzl = bnjzl;
	}
	public double getLjjzl() {
		return ljjzl;
	}
	public void setLjjzl(double ljjzl) {
		this.ljjzl = ljjzl;
	}
	public double getNhsyl() {
		return nhsyl;
	}
	public void setNhsyl(double nhsyl) {
		this.nhsyl = nhsyl;
	}
	
	
}
