package com.cssweb.android.connect;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.cssweb.android.common.Config;
import com.cssweb.android.common.CssIniFile;
import com.cssweb.android.common.DateTool;
import com.cssweb.android.common.UID;
import com.cssweb.quote.util.Utils;

public class ConnService {

	public static JSONObject execute(RequestParams requestParams, int requestType) {
		JSONObject j = new JSONObject();
		
		switch(requestType) {
			case 0: 
				j = getZHPM(requestParams.getPeroid(), requestParams.getMarket(), requestParams.getField(),
							requestParams.getDesc(), requestParams.getBegin(), requestParams.getEnd());
				break;
			case 1:
				j = getMyStock(requestParams.getStocks());
				break;
			case 2:
				j = getGridData("GET_GRID_QUOTE_SORT", requestParams.getMarket(), 
						requestParams.getPaixu(), requestParams.getDesc(), 
						requestParams.getBegin(), requestParams.getEnd());
				break;
			case 3:
				j = getStockFund("GET_GRID_OPENFUND_SORT" , requestParams.getMarket() ,
						requestParams.getPaixu(), requestParams.getDesc(),
						requestParams.getBegin(), requestParams.getEnd() ,
						requestParams.getFundCompanyId(), requestParams.getLevel(), 
						requestParams.getManagerlevel(), requestParams.getJingzhi1(),
						requestParams.getJingzhi2() );
				break;
			case 4:
				j =getFundName();
				break;
			case 5:
				j =getFundQueryName(requestParams.getCode() , requestParams.getMarket(),
						requestParams.getLevel() , requestParams.getManagerlevel());
				break;
			case 6:
				j =getSunPrivate("GET_GRID_PRIVATEFUND_SORT" , requestParams.getPaixu() ,
						requestParams.getDesc(), requestParams.getBegin(), requestParams.getEnd(), 
						requestParams.getJingzhi1() , requestParams.getJingzhi2() ,
						requestParams.getJingzhiAdd1() , requestParams.getJingzhiAdd2() );
				break;
			case 7:
				j =getJingzhi("GET_OPENFUND_NAV_SORT" ,requestParams.getPaixu() ,
						"desc", requestParams.getBegin(), requestParams.getEnd());
				break;
			
			case 8:
				j = getGlobalHuiShi("GET_GRID_GLOBAL_SORT" , requestParams.getKind(),
						requestParams.getBegin(), requestParams.getEnd()) ;
		}
		
		return j;
	}
	/**
	 * 全球汇市接口数据
	 * @param type
	 * @param kind
	 * @param from
	 * @param to
	 * @return
	 */
	public static JSONObject getGlobalHuiShi (String type ,String kind ,String from ,String to){
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append("?type=");
		sb.append(type);
		sb.append("&kind=");
		sb.append(kind);
		sb.append("&from=");
		sb.append(from);
		sb.append("&to=");
		sb.append(to);
		Log.i("getGlobalHuiShi>>>>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	
	public static JSONObject getJingzhi (String type ,String field, String asc ,String from ,String to){
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append("?type="+type);
		sb.append(field==null?"":"&field="+field);
		sb.append(asc==null?"":"&asc="+asc);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		Log.i("getJingzhi>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getSunPrivate(String type ,String field , String asc ,String from ,String to ,
			String jingzhi1 , String jingzhi2 , String jingzhiAdd1 , String jingzhiAdd2 ){

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append("?type="+type);
		sb.append(field==null?"":"&field="+field);
		sb.append(asc==null?"":"&asc="+asc);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		
		sb.append("&fundNav=" + jingzhi1 );
		sb.append("&fundNav2=" + jingzhi2 );
		sb.append("&yearRise=" + jingzhiAdd1 );
		sb.append("&yearRise2=" + jingzhiAdd2 );
		
		Log.i("getSunPrivate>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getFundQueryName(String code ,String fundType , Integer level ,Integer managerLevel ){

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append("?type=GET_OPENFUND");
		sb.append(code==null?"":"&managementComId="+code);
		sb.append("&fundType="+fundType);
		sb.append("&fundRank="+level);
		sb.append("&fundManagerRank="+managerLevel);
		Log.i("getFundQueryName>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	public static JSONObject getFundName(){

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append("?type=GET_OPENFUND_MANAGEMENT_COM");
		return Conn.execute(sb.toString());
	}
	public static JSONObject getGridData(String type, String kind, 
			String field, String asc, String from, String to) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(kind==null?"":"&kind="+kind.toLowerCase());
		sb.append(field==null?"":"&field="+field);
		sb.append(asc==null?"":"&asc="+asc);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		Log.i(">>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getStockFund(String type , String kind ,
				String field ,String asc ,String from ,String to , String fundCompanyId , int level , int manageLevel, String jingzhi1, String jingzhi2){
		
		StringBuffer sb  = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(kind==null?"":"&kind="+kind.toLowerCase());
		sb.append(field==null?"":"&field="+field);
		sb.append(asc==null?"":"&asc="+asc);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		
		sb.append("&managementComId="+ fundCompanyId);
		sb.append("&fundRank=" + level);
		sb.append("&fundManagerRank="+ manageLevel);
		sb.append("&fundNav=" + jingzhi1);
		sb.append("&fundNav2=" + jingzhi2);
		
		Log.i("stocktypefund>>>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getZHPM(String period, String kind, String field, String desc, String from, String to) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_STAGE_STAT");
		sb.append(period==null?"day":"&period="+period);
		sb.append(kind==null?"sha":"&kind="+kind);
		sb.append(field==null?"":"&field="+field);
		sb.append(desc==null||desc.equals("")?"":"&asc="+desc);
		sb.append(from==null?"0":"&from="+from);
		sb.append(to==null?"10":"&to="+to);
		Log.i(">>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getTick(String type, String exchange, String stkcode, String from) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(exchange==null?"":"&exchange="+exchange);
		sb.append(stkcode==null?"":"&stockcode="+stkcode);
		sb.append(from==null?"":"&from="+from);
		Log.i(">>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getDish(String type, String exchange, String stkcode, String stocktype) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(exchange==null?"":"&exchange="+exchange);
		sb.append(stkcode==null?"":"&stockcode="+stkcode);
		sb.append(stocktype==null?"":"&stocktype="+stocktype);
		Log.i(">>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}

	public static JSONObject getTimeShare(String type, String exchange, String stkcode, String from, String to) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(exchange==null?"":"&exchange="+exchange);
		sb.append(stkcode==null?"":"&stockcode="+stkcode);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		Log.i(">>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getTodayKline(String type, String exchange, String stkcode, String period) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb");
		sb.append(type==null?"":"?type="+type);
		sb.append(exchange==null?"":"&exchange="+exchange);
		sb.append(stkcode==null?"":"&stockcode="+stkcode);
		sb.append(period==null?"":"&period="+period);
		Log.i("KKK", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getKlineIndicator(Context context, String indicator, boolean isWrapped, String exchangeStr, String stockCode, String period, boolean loadFromServer) throws JSONException {
		JSONObject quoteData = null;
		URLDataSource klineDataSource = new URLDataSource(exchangeStr, stockCode, period);
		String dataURL = klineDataSource.buildSource(indicator);
		//需要把冒号和斜杆去掉，不然存进去读不出来
		String dataURLReplace = dataURL.replaceAll("\\:", "").replaceAll("\\/", "");
		if(loadFromServer) {//根据当日数据判断必须取数据时
			JSONObject jsonstr = CssIniFile.getStockInfoByKlineURL(context, dataURLReplace);
			if(jsonstr!=null) {
				String time = jsonstr.getString("time").substring(4, 12);
				String key  = jsonstr.getString("key");
				int     sp  = jsonstr.getInt("sp");
				//Log.i("########K line##########", time + "===sp=1===" + DateTool.getLocalDate2()+">>>>>>"+dataURLReplace);
				if(sp == 1 && (DateTool.isLoadStockTime2()||Integer.parseInt(time) > Integer.parseInt(DateTool.getLocalDate2()))) {//本地有数据，并且时间是当天的时间
					//Log.i("########K line##########", "===sp=1本地有数据，并且时间是当天小于0930或者文件时间大于1500的时间===");
					String jsondata = CssIniFile.loadStockData(context, key);
					quoteData = new JSONObject(jsondata);
				}
				else {
					quoteData =  Conn.execute(dataURL, isWrapped);
					if(quoteData!=null) {
						//Log.i("########K line##########", "===sp=0或者时间不是当天的时间===");
						//把取回来的数据写入文件系统
						saveKlineData(context, key, dataURL, quoteData, dataURLReplace, DateTool.getDateStringByPattern(), 1);
					}
				}
			}
			else {
				//Log.i("########K line##########", "===sp=1并且本地取不到数据的情况===");
				quoteData =  Conn.execute(dataURL, isWrapped);
				if(quoteData!=null) {
					//把取回来的数据写入文件系统
					saveKlineData(context, UID.getID(), dataURL, quoteData, dataURLReplace, DateTool.getDateStringByPattern(), 1);
				}
			}
		}
		else {
			JSONObject jsonstr = CssIniFile.getStockInfoByKlineURL(context, dataURLReplace);
			if(jsonstr!=null) {
				String time = jsonstr.getString("time").substring(0, 8);
				String key  = jsonstr.getString("key");
				int     sp  = jsonstr.getInt("sp");
				if(sp==0 && DateTool.getYMD() == Integer.parseInt(time)) {//本地有数据，并且时间是当天的时间
					//Log.i("########K line##########", "===本地有数据，sp==0并且时间是当天的时间===");
					String jsondata = CssIniFile.loadStockData(context, key);
					quoteData = new JSONObject(jsondata);
				}
				else {//本地有数据，但是时间不是当天的
					//Log.i("########K line##########", "===本地有数据，sp==1或者时间不是当天的===");
					quoteData =  Conn.execute(dataURL, isWrapped);
					if(quoteData!=null) {
						//把取回来的数据写入文件系统
						saveKlineData(context, key, dataURL, quoteData, dataURLReplace, DateTool.getDateStringByPattern(), 0);
					}
				}
			}
			else {
				//Log.i("########K line##########", "===本地取不到数据的情况===");
				//本地取不到数据的情况
				quoteData =  Conn.execute(dataURL, isWrapped);
				if(quoteData!=null) {//把取回来的数据写入文件系统
					saveKlineData(context, UID.getID(), dataURL, quoteData, dataURLReplace, DateTool.getDateStringByPattern(), 0);
				}
			}
		}
		return quoteData;
	}
	
	private static void saveKlineData(Context context, String uid, String dataURL, JSONObject quoteData, String urlR, String lasttime, int sp) throws JSONException {
		JSONObject q = new JSONObject(); 
		q.put("key", uid);
		q.put("url", dataURL);
		q.put("time", lasttime);
		q.put("sp", sp);
		//将数据写入缓存
		boolean flag = CssIniFile.saveStockData(context, uid, quoteData.toString());
		if(flag) {
			//数据写入成功后更新info文件，主要是日期修改
			CssIniFile.saveIniWithAPPEND(context, CssIniFile.StockInfoFile, urlR, q.toString());
		}
	}
	
	/**
	 * 获取K线数据 从文件 和 tt 中
	 * @param exchangeStr 交易所，取值范围：sz|sh
	 * @param stockcode 股票代码
	 * @param period K线周期，取值范围：year|week|month|day|weight（除权除息信息）
	 * @param mainIndicator
	 * @param indicator
	 * @return 绘制K线图所需的JSON格式数据
	 * @throws IOException
	 * @throws JSONException
	 */
	public static JSONObject getKline(Context context, String exchangeStr, String stockCode,
			String period, String mainIndicator, String indicator, boolean loadFromServer) throws JSONException {
		JSONObject quoteData = new JSONObject(); 
		String stockCodeIndex = exchangeStr + stockCode;
		quoteData.put("stockcode", stockCodeIndex);
		quoteData.put("period", period.toLowerCase());
		quoteData.put("mainIndicator", mainIndicator.toUpperCase());
		quoteData.put("indicator", indicator.toUpperCase());
		
		JSONObject joK = getKlineIndicator(context, "", true, exchangeStr, stockCode, period, loadFromServer);
		JSONObject joMA = getKlineIndicator(context, "ma", true, exchangeStr, stockCode, period, loadFromServer);
		JSONObject joTMP = getKlineIndicator(context, "tmp", false, exchangeStr, stockCode, period, loadFromServer);
//		Log.i("===joTMP====", joTMP+">>>>>>>>>>");
//		Log.i("===joK====", joK+">>>>>>>>>>");
//		Log.i("===joMA====", joMA+">>>>>>>>>>");
		if(joK!=null&&!joK.isNull("data"))
			quoteData.put("K", joK.getJSONArray("data"));
		if(joMA!=null&&!joMA.isNull("data"))
			quoteData.put("MA", joMA.getJSONArray("data"));
		if(joTMP!=null)
			quoteData.put("joTMP", joTMP);

		JSONObject joBOLL;
		if(mainIndicator.toLowerCase().equals("boll")){
			joBOLL = getKlineIndicator(context, "boll", true, exchangeStr, stockCode, period, loadFromServer);
			//Log.i("===joBOLL====", joBOLL+">>>>>>>>>>");
			if(joBOLL!=null&&!joBOLL.isNull("data"))
				quoteData.put("BOLL", joBOLL.getJSONArray("data"));
		}
		return quoteData;
	}
	
	/**
	 * 集合文件管理系统读取K线数据
	 * @param exchangeStr
	 * @param stockCode
	 * @param period
	 * @param mainIndicator
	 * @param indicator
	 * @return
	 * @throws JSONException
	 */
	public static JSONObject getKlineData(Context context, String exchangeStr, String stockCode, String period, String mainIndicator, String indicator) throws JSONException {
		JSONObject quoteData = new JSONObject();
		//先取当天的数据
		JSONObject joToday = getTodayKline("REFRESH_KLINE", exchangeStr, stockCode, period);
		//Log.i("@@@@@@@@@@@@@joToday2@@@@@@@@@@@@@@@", "##########"+joToday);
		if(Utils.isHttpStatus(joToday)) {
			JSONArray jaToday = new JSONArray();
			String lasttime = joToday.getJSONArray("data").getJSONArray(0).getString(6);
			String qt = lasttime.substring(0,8);

			int sp = Integer.parseInt(joToday.get("sp").toString());
			int spday = Integer.parseInt(joToday.get("spday").toString());
			int tp = Integer.parseInt(joToday.get("tp").toString());

			boolean callauction = false;
			boolean daySp, bolSp, loadFromServer;
			
			if (tp==1) {
				bolSp = false;
				daySp = false;
			}
			else {
				long localtime = Long.parseLong(DateTool.getLocalDate());
				long currtime = Long.parseLong(lasttime.substring(4,12));
				if(currtime<localtime) {
					callauction = true;
				}
				else 
					callauction = false;

				if (spday==1) {
					daySp = true;
				}else {
					daySp = false;
				}
				if (sp==1) {
					bolSp = true;
				}else if (callauction==true) {
					bolSp = true;
				}else {
					bolSp = false;
				}
			}
			
			
			// 如果服务器清算完成,则需要重取服务器文件.
			if (bolSp || daySp) {
				loadFromServer = true;
			}
			else 
				loadFromServer = false;

			//Log.i("@@@@@@@@loadFromServer@@@@@@@@@", loadFromServer+">>>>>>>" + lasttime);
			quoteData = getKline(context, exchangeStr, stockCode, period, mainIndicator, indicator, loadFromServer);
			if(!quoteData.isNull("K")) {
				quoteData.put("loadFromServer", loadFromServer);
				quoteData.put("lasttime", lasttime);
				quoteData.put("zqlb", joToday.get("zqlb")); 
				quoteData.put("zqjc", joToday.get("zqjc")); 
				quoteData.put("tp", joToday.get("tp"));
				quoteData.put("sp", joToday.get("sp"));
				quoteData.put("spday", joToday.get("spday"));
				quoteData.put("zrsp", joToday.getJSONArray("data").getJSONArray(0).getDouble(7));

				boolean tradeFlag = false;//判断是否需要画当日K线
				if("1".equals(joToday.get("tp"))) {
					if("month".equals(period)||"week".equals(period)||"year".equals(period)) {
						if(!quoteData.isNull("joTMP")&&DateTool.isSameWeekMonthYear(quoteData.getJSONObject("joTMP").getJSONObject(period).getString("date"), period)) {
							tradeFlag = true;
							jaToday.put(quoteData.getJSONObject("joTMP").getJSONObject(period).getString("date"));
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(0)); 
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(1));
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(2));
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(3));
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(4));
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(5));
							//期货增加的字段普通个股和港股当日的有值但是是0
							jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(8));
							quoteData.getJSONArray("K").put(jaToday); 
						}
					}
				}
				else {
					if ("1".equals(joToday.get("sp"))){
						tradeFlag = false;
					}else{
						tradeFlag = true;
						jaToday.put(qt);
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(0)); 
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(1));
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(2));
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(3));
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(4));
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(5));
						jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(8));
						quoteData.getJSONArray("K").put(jaToday); 
					}
				}
				quoteData.put("tradeFlag", tradeFlag);
			}
			else {
				boolean tradeFlag = false;//判断是否需要画当日K线
				if ("1".equals(joToday.get("tp"))||"1".equals(joToday.get("sp"))){
					tradeFlag = false;
				}else{
					tradeFlag = true;
				}
				jaToday.put(qt);
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(0)); 
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(1));
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(2));
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(3));
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(4));
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(5));
				jaToday.put(joToday.getJSONArray("data").getJSONArray(0).getDouble(8));
				quoteData.put("K", new JSONArray().put(jaToday));
				
				quoteData.put("loadFromServer", loadFromServer);
				quoteData.put("lasttime", lasttime);
				quoteData.put("zqlb", joToday.get("zqlb")); 
				quoteData.put("zqjc", joToday.get("zqjc")); 
				quoteData.put("tp", joToday.get("tp"));
				quoteData.put("sp", joToday.get("sp"));
				quoteData.put("spday", joToday.get("spday"));
				quoteData.put("zrsp", joToday.getJSONArray("data").getJSONArray(0).getDouble(7));
				quoteData.put("tradeFlag", tradeFlag);
			}
		}
		else {
			//Log.i("@@@@@@@@loadFromServer@@@@@@@@@", loadFromServer+">>>>>>>" + lasttime);
			quoteData = getKline(context, exchangeStr, stockCode, period, mainIndicator, indicator, true);
			if(!quoteData.isNull("K")) {
				quoteData.put("tradeFlag", false);
			}
		}
		return quoteData;
	}
	
	public static String getFundLine(String exchangeStr, String stockCode, String peroid) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/kline");
		sb.append("/"+exchangeStr);
		sb.append("/"+peroid);
		sb.append("/"+stockCode);
		sb.append("/"+stockCode);
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	
	public static JSONObject getIndex() {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_STATUS_QUOTE&stockcode=sh000001,sz399001");
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getGridData(int begin, int end, String str) {
		if(str!=null) {
	
			StringBuffer sb = new StringBuffer();
			sb.append("http://");
			sb.append(Config.roadHqfwqIp);
			sb.append(":");
			sb.append(Config.roadHqfwqPort);
			sb.append("/cssweb?type=GET_GRID_QUOTE&stockcode="+str);
			Log.i("KKK", sb.toString());
			return Conn.execute(sb.toString());
		}
		return null;
	}
	
	public static JSONObject getMyStock(String stkcode) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_GRID_QUOTE&stockcode="+stkcode);
		Log.i("KKK", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getTradeHQ(String stkcode) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_PRICE_VOLUMEJY&stockcode="+stkcode+"&cssweb_type=abc");
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getAllStock() {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/allstock");
		Log.i("KKK", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	/**
	 * 获取香港市场的股票列表
	 * @return
	 */
	public static JSONObject getAllHKStock() {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/allstockex");
		Log.i("KKK", sb.toString());
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getSsjp() {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_REALTIME_INFO");
		return Conn.execute(sb.toString());
	}
	
	public static JSONObject getStockFileMD5() {
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_MD5");
		return Conn.execute(sb.toString());
	}
	
	public static String getF10(String exchange, String stockcode, String desc) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/f10");
		sb.append("/"+exchange);
		sb.append("/"+stockcode);
		if(desc!=null) 
			sb.append("_"+desc+".txt");
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	public static String getStockFundF10 (String exchange, String stockcode, String desc){

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/f10");
		sb.append("/"+exchange);
		sb.append("/"+stockcode);
		if(desc!=null) {
			sb.append("/"+desc);
		}else{
			sb.append("/list");
		}
			
		Log.i("KKKFFF", sb.toString());
		return Conn.getF10Data(sb.toString());
	}
	
	public static String getRedar(String exchange, String stockcode) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/radar");
		sb.append("/"+exchange);
		sb.append("/kline");
		sb.append("/"+stockcode);
		sb.append("/date");
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	//地雷日期 信息
	public static String getRedarNew(String exchange, String stockcode) {
		//http://hq.njzq.cn/radar/sz/kline/000001/date
		StringBuffer sb = new StringBuffer();
		sb.append("http://");	
		sb.append(Config.roadHqdlqIp);
		sb.append("/radar");
		sb.append("/"+exchange);
		sb.append("/kline");
		sb.append("/"+stockcode);
		sb.append("/date");
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}

	public static String getRedarContent(String exchange, String stockcode, String title, String date, int desc) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/radar");
		sb.append("/"+exchange);
		sb.append("/kline");
		sb.append("/"+stockcode);
		sb.append("/"+title);
		sb.append(".");
		sb.append(date);
		sb.append(".");
		sb.append(desc);
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	
	//地雷内容信息
	public static String getRedarContentNew(String exchange, String stockcode, String date, int desc) {
		//http://hq.njzq.cn/radar/sz/kline/000001/content.20110702.0
		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqdlqIp);
		sb.append("/radar");
		sb.append("/"+exchange);
		sb.append("/kline");
		sb.append("/"+stockcode);
		sb.append("/content");
		sb.append(".");
		sb.append(date);
		sb.append(".");
		sb.append(desc);
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	
	//地雷内容信息
	public static String getRedarTitleNew(String exchange, String stockcode, String date) {
		//http://hq.njzq.cn/radar/sz/kline/000001/title.20110729

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqdlqIp);
		sb.append("/radar");
		sb.append("/"+exchange);
		sb.append("/kline");
		sb.append("/"+stockcode);
		sb.append("/title");
		sb.append(".");
		sb.append(date);
		Log.i("KKK", sb.toString());
		return Conn.getData(sb.toString());
	}
	
	
	/**
	 * field=index_code&asc=asc&from=1&to=5
	 * @return 字符串格式
	 */
	public static JSONObject getWorldMarket(String field, String asc, String from, String to) {

		StringBuffer sb = new StringBuffer();
		sb.append("http://");
		sb.append(Config.roadHqfwqIp);
		sb.append(":");
		sb.append(Config.roadHqfwqPort);
		sb.append("/cssweb?type=GET_GRID_WORLDINDEX_SORT");
		sb.append(field==null?"":"&field="+field);
		sb.append(asc==null?"":"&asc="+asc);
		sb.append(from==null?"":"&from="+from);
		sb.append(to==null?"":"&to="+to);
		Log.i("getWorldMarket>>>>>>>", sb.toString());
		return Conn.execute(sb.toString());
	}
}
