package com.cssweb.android.user.track;

import android.app.Activity;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;

import java.util.Locale;
import java.util.UUID;

/**
 * 处理用户的栏目访问行为分析类, 调用该方法前请先在配置文件中加入名为
 *
 * <pre>
 * &lt;service android:name="com.cssweb.android.user.track.TrackService" /&gt;
 * 的服务,并在配置文件中的application标签中添加属性,&lt;android:name="com.cssweb.android.user.track.CssApplication"/&gt;
 * </pre>
 *
 * @author 欧阳
 *
 *         <pre>
 *                         由于广播无法在activity以外去控制,请在基类activity中手动注册屏幕监听事件
 *                 private BroadcastReceiver lockreceiver, homereceiver;
 *
 *                 private void listenScreen() {
 *                     lockreceiver = new BroadcastReceiver() {
 *
 *                         &#064;Override
 *                         public void onReceive(Context context, Intent intent) {
 *
 *                             ServiceControl.pauseScreenReceiver();
 *                         }
 *                     };
 *                     registerReceiver(lockreceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
 *                     homereceiver = new BroadcastReceiver() {
 *                         &#064;Override
 *                         public void onReceive(Context context, Intent intent) {
 *
 *                             ServiceControl.resumeScreenReceiver();
 *                         }
 *                     };
 *                     registerReceiver(homereceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
 *
 *                 }
 *
 *                 关于当前jar包用户行为分析的一些说明:由于实现机制是根据Activity来判断,而没有具体跟踪到栏目(需对程序源代码进行修改),故暂没有做更为详尽的实现,
 *                 虽然该类提供了currentSite(),和lastSite()方法,但现行版本不需要调用.下一个版本要考虑重新实现机制的问题,必须重写跟踪栏目的方法.
 *                 ---所有webdisplay类相关的webview无法被跟踪,所有共用同一个activity的栏目无法被最跟踪
 *                 ---清单:
 *                      行情报价
 *                          场外基金
 *                              股票型,债券型,货币型,混合型 不能区分
 *                      委托交易:主页. 银行转账中的资金转入,转出无法区别,银行转账-资金调拨栏内的主辅,辅主无法区分
 *                          资金流水,历史查询无法区分
 *                          场外基金
 *                              申购,认购,赎回不能区分
 *                              撤单,当日委托不能区分
 *                              历史成交,历史委托不能区分
 *                          场内基金
 *                              认购,申购,赎回,不能区分
 *                          配号查询 不能区分
 *                      宁证宝典:主页不能识别
 *                          精选
 *                              优选,核心不能识别(web)
 *                          投资组合 不能
 *                      资讯罗盘:全不能
 *                      积分乐园:全不能
 *                      宁证风采:全不能
 *                      掌上乐园:全不能
 *                      在线互动:基本全不能(除模拟炒股主页)
 *                 </pre>
 */
public class ServiceControl extends Application
{
    private Activity thisActivity;

    public ServiceControl()
    {
        super();
    }

    /**
     * @param thisActivity
     *            服务启动时调用的当前Activity
     */
    public ServiceControl(Activity thisActivity)
    {
        super();
        this.thisActivity = thisActivity;
    }

    /**
     * 启动服务;此方法在构造ServiceControl OnCreate()方法中调用
     */
    public void startTrackService()
    {
        // System.out.println(this.getClass().getName() + "."+ new
        // Exception().getStackTrace()[0].getMethodName()+ "()");

        Intent testIntent = new Intent(thisActivity, TrackService.class);
        this.setGlobalParams(thisActivity);
        // Gloable.getInstance().setOpera("0");//把状态更新设置为0(新增)
        thisActivity.startService(testIntent);
    }

    /**
     * 退出服务;在退出系统时调用
     */
    public void stopTrackService()
    {
        thisActivity.stopService(new Intent(thisActivity, TrackService.class));
    }

    /**
     * 当前栏目的代码,(暂时不用管)
     * @param currentSite 目前地址
     */
    public void currentSite(String currentSite)
    {
        Gloable.getInstance().setCurrentSite(currentSite);
    }

    /**
     * 上一个栏目的代码(暂时不用管)
     * @param lastSite 上一个地址
     */
    public void lastSite(String lastSite)
    {
        Gloable.getInstance().setLastUid(lastSite);
    }

    /**
     * 在.splash的ONCreate方法处调用
     */
    public static void setSessionId()
    {
        Gloable.getInstance().setSessionid(UUID.randomUUID().toString());
    }

    /**
     * 监听屏幕状态,是否锁频(关闭屏幕)
     */
    public static void startScreenReceiver()
    {

        Gloable.getInstance().setIsLock(false);
    }

    /**
     * 屏幕关闭,暂停发送调用此方法
     */
    public static void pauseScreenReceiver()
    {
        Gloable.getInstance().setIsLock(true);
    }

    /**
     * 屏幕重新打开,调用此方法
     */
    public static void resumeScreenReceiver()
    {
        Gloable.getInstance().setIsLock(false);
    }

    /**
     * 监听是否切换到主页状态,调用此方法
     */
    public static void startHomeReceiver()
    {
        Gloable.getInstance().setIsHome(false);
    }

    /**
     * 切换到主页,暂停发送服务,调用此方法
     */
    public static void pauseHomeReceiver()
    {
        Gloable.getInstance().setIsHome(true);
    }

    /**
     * 在基类Activity onResume()方法处调用,重新启动服务
     */
    public static void resumeHomeReceiver()
    {
        Gloable.getInstance().setIsHome(false);
    }

    /**
     * 注意,Broadcaster的注册需在本类onDestory()方法中动态解绑(必须)
     *
     *
     * @param currentactivity
     *            当前Activity
     * @param lockreceiver
     *            屏幕是否关闭的Broadcast
     * @param homereceiver
     *            是否切换到主页的Broadcast
     */
    public static void stopReceiver(Activity currentactivity, BroadcastReceiver lockreceiver,
        BroadcastReceiver homereceiver)
    {
        currentactivity.unregisterReceiver(lockreceiver);
        currentactivity.unregisterReceiver(homereceiver);
    }

    private void setGlobalParams(Activity thisActivity)
    {
        Gloable gloable = Gloable.getInstance();

        TelephonyManager telmanager =
            (TelephonyManager) thisActivity.getSystemService(Context.TELEPHONY_SERVICE);
        gloable.setIMEI(telmanager.getDeviceId());
        String isp = "";
        String opera = telmanager.getSimOperator();
        if (opera != null)
        {// 网络运营商
            if (opera.equals("46000") || opera.equals("46002"))
            {
                isp = "中国移动";
            }
            else if (opera.equals("46001"))
            {
                isp = "中国联通";
            }
            else if (opera.equals("46003"))
            {
                isp = "中国电信";
            }
        }
        gloable.setIsp(isp);
        gloable.setOs("android" + android.os.Build.VERSION.RELEASE);
        gloable.setOschar(Locale.getDefault().getDisplayName());
        DisplayMetrics dm = new DisplayMetrics();
        thisActivity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        gloable.setReso(dm.heightPixels + "*" + dm.widthPixels);
        gloable.setSysCode("JLP_ANDROID");

        PackageManager manager = thisActivity.getPackageManager();
        PackageInfo info;
        try
        {
            info = manager.getPackageInfo(thisActivity.getPackageName(), 0);

            String versionCode = info.versionName;

            gloable.setSysVer(versionCode);
        }
        catch (NameNotFoundException e)
        {
            e.printStackTrace();
        }
        gloable.setTermianl(android.os.Build.MODEL);
        gloable.setHits(1);
        String nettype = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) thisActivity
            .getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
        NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
        if (activeNetInfo != null)
        {
            if (activeNetInfo.getType() == ConnectivityManager.TYPE_WIFI)
            {
                nettype = "WI-FI";
            }
            else if (activeNetInfo.getType() == ConnectivityManager.TYPE_MOBILE)
            {
                nettype = "EDGE/3G";
            }
            gloable.setNetType(nettype);// 上网方式
        }
        else
        {
            gloable.setNetType("没网络");
        }
    }

    /**
     * 退出时重置用户信息userType(1,交易,2,体验,3浏览,4,注册).在MainActiviy()询问是否退出方法(case 1)后调用
     * 用户登录账号,登录账号类型,用户类型,用户等级,真实姓名,营业部编码,营业部名称,登录状态
     */
    public static void destoryLogin()
    {
        LoginInfo.getInstance().initLoginInfo();
    }
}

class Gloable
{
    private Boolean isLogin = false;// 是否登录

    private Boolean isLock = false;// 是否锁屏

    private Boolean isHome = false;// 是否切回主页

    private String IMEI = "";// 移动设备编号

    private String sysCode = "";// 系统编码

    private String sysVer = "";// 系统版本

    private String sessionid = "";// 会话表示

    private int hits = 1;// 点击数

    private String termianl = "";// 终端类型

    private String os = "";// 操作系统版本

    private String oschar = "";// 系统语言

    private String isp = "";// 运营商

    private String reso = "";// 分辨率

    private String netType = "";// 联网方式

    private String currentSite = "";// 当前的栏目编码

    private String lastUid = "jlp_start";// 上一个栏目地址//默认启动项

    private String Opera = "";// * 此次访问的操作类型。 1: 更新状态 0: 新增

    private String key = "";// 服务器端的唯一标识符

    private String jsonString = "";// 服务器返回的jsonString

    private static Gloable INSTANCE = null;

    public static Gloable getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new Gloable();
        }
        return INSTANCE;
    }

    public String getIMEI()
    {
        return IMEI;
    }

    public void setIMEI(String iMEI)
    {

        IMEI = iMEI;
    }

    public String getSysCode()
    {
        return sysCode;
    }

    public void setSysCode(String sysCode)
    {
        this.sysCode = sysCode;
    }

    public String getSysVer()
    {
        return sysVer;
    }

    public void setSysVer(String sysVer)
    {
        this.sysVer = sysVer;
    }

    public String getSessionid()
    {
        return sessionid;
    }

    public void setSessionid(String sessionid)
    {
        this.sessionid = sessionid;
    }

    public int getHits()
    {
        return hits;
    }

    public void setHits(int hits)
    {
        this.hits = hits;
    }

    public String getTermianl()
    {
        return termianl;
    }

    public void setTermianl(String termianl)
    {
        this.termianl = termianl;
    }

    public String getOs()
    {
        return os;
    }

    public void setOs(String os)
    {
        this.os = os;
    }

    public String getOschar()
    {
        return oschar;
    }

    public void setOschar(String oschar)
    {
        this.oschar = oschar;
    }

    public String getNetType()
    {
        return netType;
    }

    public void setNetType(String netType)
    {
        this.netType = netType;
    }

    public String getIsp()
    {
        return isp;
    }

    public void setIsp(String isp)
    {
        this.isp = isp;
    }

    public String getReso()
    {
        return reso;
    }

    public void setReso(String reso)
    {
        this.reso = reso;
    }

    public void setLastUid(String lastUid)
    {
        this.lastUid = lastUid;
    }

    public String getLastUid()
    {
        return lastUid;
    }

    public void setOpera(String opera)
    {
        Opera = opera;
    }

    public String getOpera()
    {
        return Opera;
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public void setJsonString(String jsonString)
    {
        this.jsonString = jsonString;
    }

    public String getJsonString()
    {
        return jsonString;
    }

    public void setIsLock(Boolean isLock)
    {
        this.isLock = isLock;
    }

    public Boolean getIsLock()
    {
        return isLock;
    }

    public void setIsHome(Boolean isHome)
    {
        this.isHome = isHome;
    }

    public Boolean getIsHome()
    {
        return isHome;
    }

    public String getCurrentSite() {
        return currentSite;
    }

    public void setCurrentSite(String currentSite) {
        this.currentSite = currentSite;
    }

    public Boolean getIsLogin() {
        return isLogin;
    }

    public void setIsLogin(Boolean isLogin) {
        this.isLogin = isLogin;
    }

}
