package com.cssweb.android.connect;

import com.cssweb.android.common.Config;


public class URLDataSource {

	/**
	 * 获取Kline数据的URL路径
	 */
	private String klineURL = "http://"+Config.roadHqfwqIp+":"+Config.roadHqfwqPort+"/kline";
	
	private String exchangeStr;
	private String stockCode;
	private String period;
	
	public URLDataSource() {
		
	}
	
	/**
	 * 要获取指标数据，必须先使用此方法进行初始化相关参数，用于构建获取指标数据的访问路径（文件地址或者URL）
	 * @param exchangeStr 交易所拼音简称
	 * @param stockCode 正确代码
	 * @param period K线周期
	 */
	public URLDataSource(String exchangeStr, String stockCode,
			String period){
		
		this.exchangeStr = exchangeStr;
		this.stockCode = stockCode;
		this.period = period;
	}

	public String buildSource(String indicator) {
		if(indicator==null||indicator.equals(""))
			return klineURL + "/" + exchangeStr + "/"
				+ period + "/" + stockCode + "/" + stockCode;
		else
			return klineURL + "/" + exchangeStr + "/"
				+ period + "/" + stockCode + "/" + stockCode + "_" + indicator;
	}
}
