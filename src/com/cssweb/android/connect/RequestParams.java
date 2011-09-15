package com.cssweb.android.connect;

import com.cssweb.android.common.Global;

public class RequestParams {
    private String market;
    private String paixu;
    private String desc;
    private String begin;
    private String end;
    private String peroid;
    private String kind;
    private String field;
    
    private String stocks;
    private String code;
    private Integer level;
    private Integer  managerlevel;
    private String aesStr;
    private String fundCompanyId;
    private String jingzhi1;
    private String jingzhi2;
    private String jingzhiAdd1;
    private String jingzhiAdd2;
    public RequestParams() {
    	this.market = "sha";
    	this.paixu = "zqdm";
    	this.desc = "asc";
    	this.field = "zf";
    	this.begin = "1";
    	this.peroid = "day";
    	this.end = String.valueOf(Global.PAGE_SIZE);
    }

	public String getMarket() {
		return market;
	}

	public void setMarket(String market) {
		this.market = market;
	}

	public String getPaixu() {
		return paixu;
	}

	public void setPaixu(String paixu) {
		this.paixu = paixu;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getBegin() {
		return begin;
	}

	public void setBegin(String begin) {
		this.begin = begin;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public String getStocks() {
		return stocks;
	}

	public void setStocks(String stocks) {
		this.stocks = stocks;
	}
	
	
	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public Integer getManagerlevel() {
		return managerlevel;
	}

	public void setManagerlevel(Integer managerlevel) {
		this.managerlevel = managerlevel;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	
	public String getPeroid() {
		return peroid;
	}

	public void setPeroid(String peroid) {
		this.peroid = peroid;
	}

	public String getKind() {
		return kind;
	}

	public void setKind(String kind) {
		this.kind = kind;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public String getAesStr() {
		return aesStr;
	}

	public void setAesStr(String aesStr) {
		this.aesStr = aesStr;
	}

	public String getFundCompanyId() {
		return fundCompanyId;
	}

	public void setFundCompanyId(String fundCompanyId) {
		this.fundCompanyId = fundCompanyId;
	}

	public String getJingzhi1() {
		return jingzhi1;
	}

	public void setJingzhi1(String jingzhi1) {
		this.jingzhi1 = jingzhi1;
	}

	public String getJingzhi2() {
		return jingzhi2;
	}

	public void setJingzhi2(String jingzhi2) {
		this.jingzhi2 = jingzhi2;
	}

	public String getJingzhiAdd1() {
		return jingzhiAdd1;
	}

	public void setJingzhiAdd1(String jingzhiAdd1) {
		this.jingzhiAdd1 = jingzhiAdd1;
	}

	public String getJingzhiAdd2() {
		return jingzhiAdd2;
	}

	public void setJingzhiAdd2(String jingzhiAdd2) {
		this.jingzhiAdd2 = jingzhiAdd2;
	}
	
	
	

}
