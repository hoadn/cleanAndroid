package com.cssweb.android.user.track;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

public class UrlParams
{
    static Context context;

    static
    {
        context = CssApplication.getInstance();
    }

    /**
     * 客户代码。交易用户，传值：客户号（不支持客户号，传资金帐号）  体验用户，传值：体验卡号  浏览用户，传值：唯一值  注册用户，传值：用户ID或其它唯一标识符（比如：昵称） 
     * 内部用户，传值：员工号
     *
     * 注： 浏览用户客户代码：  移动终端使用设备编号； //1 交易密码登陆 ,2服务密码登陆 ,3体验卡登陆 ,4 注册用户（模拟炒股专用）
     *
     * @return 客户代码
     */
    public static String setCustID()
    {
        // System.out.println(new Exception().getStackTrace()[0].getMethodName()
        // + "()");
        String cusid = "";
        // 1 交易密码登陆 ,2服务密码登陆 ,3体验卡登陆 ,4 注册用户（模拟炒股专用）
        // Tradeuser中 private int loginType;
        // logininfo 中的用户类型,1：交易用户 2：体验用户 3：浏览用户 4：注册用户 UserType
        // int loginType = TradeUser.getInstance().getLoginType();
        String userType = LoginInfo.getInstance().getUserType();

        if (userType == "3")
        {// 浏览3或者没有用户ID
            // System.out.println("浏览用户=====0");
            cusid = "BU"+Gloable.getInstance().getIMEI();
        }
        else if (userType == "1")
        {// 交易1
            // System.out.println("交易用户=====1");
            // cusid = TradeUser.getInstance().getCustid();
            cusid = LoginInfo.getInstance().getLoginID(); // 体验卡号
        }
        else if (userType == "2")
        {// 体验2
            // System.out.println(" 体验用户====3");
            // cusid = TradeUser.getInstance().getCustid(); // 体验卡号
            cusid = LoginInfo.getInstance().getLoginID(); // 体验卡号
        }
        else if (userType == "4")
        {// 注册4
            // System.out.println("注册用户=====4");
            // cusid = TradeUser.getInstance().getCustid();
            cusid = LoginInfo.getInstance().getLoginID();
        }
        if (cusid == null)
        {
            cusid = "";
        }
        return cusid + "";
    }

    /**
     * 根据包名不同获得Activity
     *
     * @return 当前访问的栏目代码
     */
    public static String setUrlID()
    {
        String classname = "";
        context = CssApplication.getInstance();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;
        classname = cn.getShortClassName();
        return matchUID(classname) + "";
    }

    /**
     * 用户登陆后可能有的数据(交易用户)
     *
     * @return 营业部编码
     */
    public static String setOrgID()
    {
        String orgID = "";
        // 判断账户类型18,服务,19 交易
        String loginType = LoginInfo.getInstance().getLoginType();
        // System.out.println(loginType);
        if (loginType == "18" || loginType == "19")
        {// 交易服务密码登录有
            orgID = LoginInfo.getInstance().getOrgID();
        }
        else
        {
            orgID = "";
        }
        if (orgID == null)
        {
            orgID = "";
        }
        return orgID + "";
    }

    /**
     * @return 营业部名称
     */
    public static String setOrgDesc()
    {
        String orgDesc = "";
        // 判断账户类型18,服务,19 交易
        String loginType = LoginInfo.getInstance().getLoginType();
        // System.out.println(loginType);
        if (loginType == "18" || loginType == "19")
        {// 交易服务密码登录有
            orgDesc = LoginInfo.getInstance().getOrgDesc();
        }
        else
        {
            orgDesc = "";
        }
        orgDesc = "";// /////////写死
        return orgDesc + "";
    }

    /**
     * 1：交易用户 2：体验用户 3：浏览用户 4：注册用户
     *
     * @return 用户类型
     */
    public static String setUserType()
    {
        String userType = LoginInfo.getInstance().getUserType();
        // if (LoginInfo.getInstance().getUserType() != null) {
        // userType = LoginInfo.getInstance().getUserType();
        // }
        if (userType == null)
        {// 浏览,没有经历过登录
            userType = "3";
        }
        else if (userType == "1")
        {// 交易
            userType = "1";
        }
        else if (userType == "2")
        {// 体验
            userType = "2";
        }
        else if (userType == "4")
        {// 注册
            userType = "4";
        }
        return userType + "";
    }

    /**
     * @return 用户等级
     */
    public static String setUserLevel()
    {
        String userLevel = "";
        String loginType = LoginInfo.getInstance().getLoginType();
        if (loginType == "17" || loginType == "18" || loginType == "19")
        {// 交易,服务,体验存在
            userLevel = LoginInfo.getInstance().getUserLevel() + "";
        }
        return userLevel + "";
    }

    /**
     * @return 真实姓名
     */
    public static String setRealName()
    {
        String realName = "";
        String loginType = LoginInfo.getInstance().getLoginType();
        if (loginType == "18" || loginType == "19")
        {// 交易,服务
            realName = LoginInfo.getInstance().getRealName() + "";
        }
        else
        {
            realName = "";
        }

        return realName + "";
    }

    /**
     * JLP_ANDROID 金罗盘Android版
     *
     * @return 进行分析的系统编码
     */
    public static String setSystemCode()
    {
        return Gloable.getInstance().getSysCode() + "";
    }

    /**
     * 1.0
     *
     * @return 进行分析的系统版本
     */
    public static String setSystemVer()
    {
        // 1.0
        return Gloable.getInstance().getSysVer() + "";
    }

    /**
     * 32+4位长度的唯一值
     *
     * @return 此次回话的ID
     */
    public static String setSessionId()
    {
        return Gloable.getInstance().getSessionid() + "";
    }

    /**
     * 非Web应用传值：1
     *
     * @return 页面点击数
     */
    public static String setHits()
    {
        return Gloable.getInstance().getHits() + "";
    }

    /**
     * 此次访问的操作类型。 1: 更新状态 0: 新增
     *
     * @return 此次访问的操作类型
     */
    public static String setOpera()
    {
        return Gloable.getInstance().getOpera() + "";
    }

    /**
     * HTC Desire 等等
     *
     * @return 访问终端类型
     */
    public static String setTerminaltype()
    {
        return Gloable.getInstance().getTermianl() + "";
    }

    /**
     * 同浏览用户的custID值。
     *
     * @return 标示浏览用户身份的ID
     */
    public static String setVisitorID()
    {
        return Gloable.getInstance().getIMEI() + "";
    }

    /**
     * 当前操作系统版本
     *
     * @return 操作系统及版本
     */
    public static String setOS()
    {
        // android2.3.3
        return Gloable.getInstance().getOs() + "";
    }

    /**
     * 当手机使用非wifi方式上网时，可获取网络运营商。如： 中国移动、中国联通、中国电信等
     *
     * @return 网络运营商
     */
    public static String setISP()
    {
        return Gloable.getInstance().getIsp() + "";
    }

    /**
     * WI-FI EDGE/3G
     *
     * @return 联网方式
     */
    public static String setNetworkType()
    {
        return Gloable.getInstance().getNetType() + "";
    }

    /**
     * 形如：1440*900
     *
     * @return 客户端分辨率
     */
    public static String setResolu()
    {

        return Gloable.getInstance().getReso() + "";
    }

    /**
     * @return 客户端语言
     */
    public static String setOSCharacter()
    {

        return Gloable.getInstance().getOschar() + "";
    }

    /**
     * @return 服务器端唯一标识
     */
    public static String setKey()
    {
        String key = "";
        if (Gloable.getInstance().getKey() != null)
        {
            key = Gloable.getInstance().getKey();
        }

        return key + "";
    }

    /**
     * 应用发布来源
     *
     * @return APK获取来源;
     */
    public static String setChannel()
    {

        return "Google Market";
    }

    public static String setLastUID()
    {
        return matchUID(Gloable.getInstance().getLastUid()) + "";
    }

    /*
     * 
     * 行情报价 hqbj
     * 行情报价-行情预警 hqbj_hqyj
     * 行情报价-自选股 hqbj_zxg
     * 行情报价-大盘指数 hqbj_dpzs
     * 行情报价-综合排名 hqbj_zhpm
     * 行情报价-个股查询 hqbj_ggcx
     * 行情报价-分类报价 hqbj_flbj
     * 行情报价-场外基金 hqbj_cwjj
     * 行情报价-场外基金-股票型基金 hqbj_cwjj_gpxjj
     * 行情报价-场外基金-债券型基金 hqbj_cwjj_zqxjj
     * 行情报价-场外基金-货币型基金 hqbj_cwjj_hbxjj
     * 行情报价-场外基金-混合型基金 hqbj_cwjj_hhxjj
     * 行情报价-场外基金-净值增长排行 hqbj_cwjj_jzzzph
     * 行情报价-场外基金-阳光私募 hqbj_cwjj_ygsm
     * 行情报价-全球市场 hqbj_qqsc
     * 行情报价-全球市场-港股行情 hqbj_qqsc_gghq
     * 行情报价-全球市场-港股行情-恒生指数 hqbj_qqsc_gghq_hszs
     * 行情报价-全球市场-港股行情-香港主板 hqbj_qqsc_gghq_xgzb
     * 行情报价-全球市场-港股行情-香港创业板 hqbj_qqsc_gghq_xgcyb
     * 行情报价-全球市场-外围市场 hqbj_qqsc_wwsc
     * 行情报价-全球市场-全球汇市 hqbj_qqsc_qqhs
     * 行情报价-期货行情 hqbj_qhhq
     * 行情报价-期货行情-中金所 hqbj_qhhq_zjs
     * 行情报价-期货行情-上期所 hqbj_qhhq_sqs
     * 行情报价-期货行情-大商所 hqbj_qhhq_dss
     * 行情报价-期货行情-郑商所 hqbj_qhhq_zss
     * 行情报价-期货行情-全球商品 hqbj_qhhq_qqsp
     */
    private static String matchUID(String classname)
    {
        String urlID = "";
        if (classname.equals(".MainActivity"))
        {
            urlID = "jlp_sy";
        }
        else if (classname.equals(".JlpActivity"))
        {
            //            urlID = ".JlpActivity";
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals(".RestartDialog"))
        {
            //            urlID = ".RestartDialog";
            urlID = "njzq_restartDialog";
        }
        else if (classname.equals(".TranslucentMenuActivity"))
        {
            urlID = "njzq_jinluopan";
        }
        else if (classname.equals("com.cssweb.android.sms.SMSJHActivity"))
        {
            urlID = "dxjh";// 手机验证
        }
        else if (classname.equals("com.cssweb.android.quote.SQS"))
        {
            urlID = "hqbj_qhhq_sqs";// 期货-上期所
        }
        else if (classname.equals("com.cssweb.android.quote.DSS"))
        {
            urlID = "hqbj_qhhq_dss";// 期货-大商所
        }
        else if (classname.equals("com.cssweb.android.quote.ZSS"))
        {
            urlID = "hqbj_qhhq_zss";// 期货-郑商所
        }
        else if (classname.equals("com.cssweb.android.quote.ZJS"))
        {
            urlID = "hqbj_qhhq_zjs";// 期货-中金所
        }
        else if (classname.equals("com.cssweb.android.quote.QQSP"))
        {
            urlID = "hqbj_qhhq_qqsp";// 期货-全球商品
        }
        else if (classname.equals("com.cssweb.android.quote.QHSCGridActivity"))
        {
            urlID = "android.quote.QHSCGridActivity";
        }
        else if (classname.equals("com.cssweb.android.quote.QHSCBaseActivity"))
        {
            urlID = "android.quote.QHSCBaseActivity";
        }
        else if (classname.equals("com.cssweb.android.quote.HSZS"))
        {
            urlID = "hqbj_qqsc_gghq_hszs";// 全球-港股-恒生
        }
        else if (classname.equals("com.cssweb.android.quote.GlobalMarketActivity"))
        {
            urlID = "hqbj_qqsc";// 全球市场
        }
        else if (classname.equals("com.cssweb.android.quote.OTCFundActivity"))
        {
            urlID = "hqbj_cwjj";// 场外基金
        }
        else if (classname.equals("com.cssweb.android.quote.PaiMing"))
        {
            urlID = "hqbj_zhpm";// 综合排名
        }
        else if (classname.equals("com.cssweb.android.quote.DaPan"))
        {
            urlID = "hqbj_dpzs";// 大盘指数
        }
        else if (classname.equals("com.cssweb.android.quote.FenLei"))
        {
            urlID = "hqbj_flbj";// 分类报价
        }
        else if (classname.equals("com.cssweb.android.quote.QueryStock"))
        {
            urlID = "hqbj_ggcx";// 个股查询
        }
        else if (classname.equals("com.cssweb.android.quote.PersonalStock"))
        {
            urlID = "hqbj_zxg";// 自选
        }
        else if (classname.equals("com.cssweb.android.quote.QuoteAlarm"))
        {
            urlID = "android.quote.QuoteAlarm";
        }
        else if (classname.equals("com.cssweb.android.quote.TrendActivity"))
        {
            urlID = "hqbj_fsx";// 分时线
        }
        else if (classname.equals("com.cssweb.android.quote.KLineActivity"))
        {
            urlID = "hqbj_kline";// K线
        }
        else if (classname.equals("com.cssweb.android.quote.KLine2Activity"))
        {
            urlID = "njzq_ggkx";//个股K线
        }
        else if (classname.equals("com.cssweb.android.quote.QuotePrice"))
        {
            urlID = "njzq_xxbj";//详细报价
        }
        else if (classname.equals("com.cssweb.android.quote.QuoteDetail"))
        {
            urlID = "njzq_hqmx";//行情明细
        }
        else if (classname.equals("com.cssweb.android.quote.GetF10List"))
        {
            urlID = "hqbj_f10";
        }
        else if (classname.equals("com.cssweb.android.quote.GetF10Content"))
        {
            urlID = "njzq_f10_xx";//F10详细内容
        }
        else if (classname.equals("com.cssweb.android.quote.GlobalMarket"))
        {
            urlID = "hqbj_qqsc_wwsc";// 外围市场
        }
        else if (classname.equals("com.cssweb.android.quote.FLineActivity"))
        {
            urlID = "njzq_jjzst";//基金走势图
        }
        else if (classname.equals("com.cssweb.android.quote.StockTypeFund"))
        {
            //            urlID = "hqbj_cwjj_gp,zq,hb,hh";// 场外-股票型,债券,货币,混合
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.quote.FundQueryCondition"))
        {
            urlID = "njzq_kfjj_quote";//开放式基金查询条件
        }
        else if (classname.equals("com.cssweb.android.quote.FundQuery"))
        {
            urlID = "njzq_jjcx";//基金查询
        }
        else if (classname.equals("com.cssweb.android.quote.SunPrivate"))
        {
            urlID = "hqbj_cwjj_ygsm";// 阳光私募
        }
        else if (classname.equals("com.cssweb.android.quote.JingZhiQuery"))
        {
            urlID = "hqbj_cwjj_jzzzph";// 净增长排行
        }
        else if (classname.equals("com.cssweb.android.quote.QuoteWarning"))
        {
            urlID = "hqbj_hqyj";// 行情预警
        }
        else if (classname.equals("com.cssweb.android.quote.QuoteSet"))
        {
            urlID = "hqbj_xg";//行情预警 添加、修改
        }
        else if (classname.equals("com.cssweb.android.quote.QHHQActivity"))
        {
            urlID = "hqbj_qhhq";// 期货行情
        }
        else if (classname.equals("com.cssweb.android.quote.GGHQActivity"))
        {
            urlID = "hqbj_qqsc_gghq";// 港股行情
        }
        else if (classname.equals("com.cssweb.android.quote.HKMainboard"))
        {
            urlID = "hqbj_qqsc_gghq_xgzb";// 全球-港股-香港主板
        }
        else if (classname.equals("com.cssweb.android.quote.GlobalHuiShi"))
        {
            urlID = "hqbj_qqsc_qqhs";// 全球-全球汇市
        }
        else if (classname.equals("com.cssweb.android.quote.SunpublicQueryCondition"))
        {
            urlID = "android.quote.SunpublicQueryCondition";
        }
        else if (classname.equals("com.cssweb.android.web.OpenHrefDisplay"))
        {
            urlID = "android.web.OpenHrefDisplay";
        }
        else if (classname.equals("com.cssweb.android.web.JxgpcActivity"))
        {
            urlID = "nzbd_jxzqc";//宁证宝典-精选证券池
        }
        else if (classname.equals("com.cssweb.android.web.CfpdActivity"))
        {
            urlID = "nzbd_jxc_cfpd";
        }
        else if (classname.equals("com.cssweb.android.web.OpenPdfDisplay"))
        {
            urlID = "android.web.OpenPdfDisplay";
        }
        else if (classname.equals("com.cssweb.android.fzjy.VistualTrade"))
        {
            urlID = "zxhd_mncg";
        }
        else if (classname.equals("com.cssweb.android.sz.ClearActivity"))
        {
            urlID = "android.sz.ClearActivity";
        }
        else if (classname.equals("com.cssweb.android.sz.Setting"))
        {
            urlID = "szym";//设置
        }
        else if (classname.equals("com.cssweb.android.sz.HelpActivity"))
        {
            urlID = "android.sz.HelpActivity";
        }
        else if (classname.equals("com.cssweb.android.sz.CustomSettingActivity"))
        {
            urlID = "android.sz.CustomSettingActivity";
        }
        else if (classname.equals("com.cssweb.android.sz.HQRefreshSettingActivity"))
        {
            urlID = "android.sz.HQRefreshSettingActivity";
        }
        else if (classname.equals("com.cssweb.android.sz.ServerSettingActivity"))
        {
            urlID = "android.sz.ServerSettingActivity";
        }
        else if (classname.equals("com.cssweb.android.sz.LockSettingActivity"))
        {
            urlID = "android.sz.LockSettingActivity";
        }
        else if (classname.equals("com.cssweb.android.tyyh.ExperienceUsers"))
        {
            urlID = "tyyhdly";
        }
        else if (classname.equals("com.cssweb.android.video.CustomMediaPlayer"))
        {
            urlID = "android.video.CustomMediaPlayer";
        }
        else if (classname.equals("com.cssweb.android.video.VideoPlayer"))
        {
            urlID = "android.video.VideoPlayer";
        }
        else if (classname.equals("com.cssweb.android.trade.CnjjActivity"))
        {
            urlID = "wtjy_cnjj";
        }
        else if (classname.equals("com.cssweb.android.trade.FundActivity"))
        {
            urlID = "wtjy_cwjj";
        }
        else if (classname.equals("com.cssweb.android.trade.BankActivity"))
        {
            urlID = "wtjy_yhzz";
        }
        else if (classname.equals("com.cssweb.android.trade.TransferFundsActivity"))
        {
            urlID = "wtjy_yhzz_zzdb";
        }
        else if (classname.equals("com.cssweb.android.trade.user.AccountManage"))
        {
            urlID = "wtjy_zhgl";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.GetDetails"))
        {
            urlID = "android.trade.stock.GetDetails";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.GetDetailsH"))
        {
            urlID = "android.trade.stock.GetDetailsH";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.StockTrading"))
        {
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.stock.GetPosition"))
        {
            urlID = "wtjy_cccx";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.StockCancel"))
        {
            urlID = "wtjy_cd";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.AssetQuery"))
        {
            urlID = "wtjy_zhzc";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.TodayEntrust"))
        {
            urlID = "wtjy_drwt";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.TodayDeal"))
        {
            urlID = "wtjy_drcj";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.ExchangeFund"))
        {
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.stock.StockWarrant"))
        {
            urlID = "android.trade.stock.StockWarrant";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.StockWarrant"))
        {
            urlID = "android.trade.stock.StockWarrant";
        }
        else if (classname.equals("com.cssweb.android.util.DateRange"))
        {
//            urlID = "wtjy_zjls,lscx,cwjj_lscj,cwjj_lswt,phcx";
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.stock.HistoryEntrust"))
        {
            urlID = "wtjy_lscx";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.HistoryDeal"))
        {
            urlID = "android.trade.stock.HistoryDeal";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.Bill"))
        {
            urlID = "wtjy_zjls";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.NewStockMatch"))
        {
            urlID = "wtjy_phcx";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.ModifyPassword"))
        {
            urlID = "wtjy_xgmm";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.StockList"))
        {
            urlID = "android.trade.stock.StockList";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.ShareholderList"))
        {
            urlID = "wtjy_gdzl";
        }
        else if (classname.equals("com.cssweb.android.trade.login.LoginActivity"))
        {
            urlID = "jyyhdly";
        }
        else if (classname.equals("com.cssweb.android.trade.bank.TransferQuery"))
        {
            urlID = "android.trade.bank.TransferQuery";
        }
        else if (classname.equals("com.cssweb.android.trade.bank.BankBalanceQuery"))
        {
            urlID = "wtjy_yhzz_yhye";
        }
        else if (classname.equals("com.cssweb.android.trade.bank.BankFundTransfer"))
        {
            urlID = "wtjy_yzzz_zjzr";
        }
        else if (classname.equals("com.cssweb.android.trade.bank.FundBankTransfer"))
        {
            urlID = "wtjy_yzzz_zjzc";
        }
        else if (classname.equals("com.cssweb.android.trade.bank.TransferDateRange"))
        {
            urlID = "wtjy_yhzz_zzcx";
        }
        else if (classname.equals("com.cssweb.android.trade.transferFunds.FundsDetails"))
        {
            urlID = "wtjy_yzzz_zjdb_zjcx";
        }
        else if (classname.equals("com.cssweb.android.trade.transferFunds.ZfTransfer"))
        {
//            urlID = "wtjy_yhzz_zzdb_zfzz,fzzz";
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.transferFunds.TransferFundsDateRange"))
        {
            urlID = "wtjy_yhzz_zzdb_dbls";
        }
        else if (classname.equals("com.cssweb.android.trade.transferFunds.TransferFundsQuery"))
        {
            urlID = "android.trade.transferFunds.TransferFundsQuery";
        }
        else if (classname.equals("com.cssweb.android.trade.stock.ModifyContactInfo"))
        {
            urlID = "wtjy_xxxg";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundTrading"))
        {
//            urlID = "wtjy_cwjj_jjsg,jjsh,jjrg";
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.fund.TodayTrust"))
        {
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundTransfer"))
        {
            urlID = "wtjy_cwjj_jjzh2";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundMelonSet"))
        {
            urlID = "wtjy_cwjj_fhsz";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundPortio"))
        {
            urlID = "wtjy_cwjj_ccjj";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundAccount"))
        {
            urlID = "wtjy_cwjj_jjzh";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundRiskTest"))
        {
            urlID = "android.trade.fund.FundRiskTest";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.HistoryConclusion"))
        {
            urlID = "wtjy_cwjj_lscj";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.HistoryTrust"))
        {
            urlID = "wtjy_cwjj_lswt";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundCompany"))
        {
            urlID = "wtjy_cwjj_jjkh";
        }
        else if (classname.equals("com.cssweb.android.trade.fund.FundAccountForm"))
        {
            urlID = "android.trade.fund.FundAccountForm";
        }
        else if (classname.equals("com.cssweb.android.web.WebViewDisplay"))
        {
            String gurl = Gloable.getInstance().getCurrentSite();
            urlID = gurl;
        }

        return urlID + "";
    }
}
