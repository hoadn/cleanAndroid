package com.cssweb.android.user.track;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Stack;
import java.util.Timer;
import java.util.TimerTask;

public class HandleUrl
{
    // URL处理1.获取URL
    // thread1每秒检测是否是新URL//每秒
    // thread2,每秒监听页面是否发生改变//每秒
    // thread3,检测堆栈//每500毫秒
    // thread4,文件生成;//每秒
    // thread5,文件读出,删除;//1秒
    // thread6,堆栈发送,删除;//30秒
    // thread7,处理更新状态,//10秒处理一次
    // thread8,处理程序是否在前台,//每秒

    private static Timer timer1;

    private static Timer timer2;

    private static TimerTask task1;

    private static TimerTask task2;

    public static final String URLFILE = "URLFILE";

    public static final int STACKSIZE = 100;

    public static final Integer timer1Interval = 1000;//监听是否是新url

    public static final Integer timer2Interval = 1000;//页面是否改变

    public static final Integer timer3Interval = 500;//堆栈检测

    public static final Integer timer4Interval = 1000;//写文件间隔

    public static final Integer timer5Interval = 1001;//文件读出到堆栈

    public static final Integer timer6Interval = ParamSetting.getInstance().getInterval();
    //堆栈信息发送到服务器

    //    public static final Integer timer7Interval = 10000;//update同页面所间隔时间

    public static final Integer timer8Interval = 1000;//监听程序是否继续产生url信息

    //    Thread thread1 = new Thread(task1);
    //
    //    Thread thread2 = new Thread(task2);
    //
    //    Thread thread3 = new Thread(task3);
    //
    //    Thread thread4 = new Thread(task4);
    //
    //    Thread thread5 = new Thread(task5);
    //
    //    Thread thread6 = new Thread(task6);
    //
    //    Thread thread8 = new Thread(task8);

    static Thread thread7 = new Thread();

    private static UserTrackUrlBean urlBean;

    private static HashMap<String, String> hashmap;// 存数栏目访问状态

    private static String urlname = "";

    private static String ready2push = "";// 准备存入文件的信息

    private static String urlsourcename;

    private static String urlkey;// 服务器返回key

    private static String ram;// 随机数

    public static String TRACKPATH = "";

    private static String lastUid = "";

    private static String currentUid = "";

    private static Stack<String> uidStack = new Stack<String>();

    private static Boolean isNew = true;

    private static Boolean isChanged = false;

    private static int runOnce = 0;

    private static Stack<String> urlStack;

    private static Boolean can2stack = true;//stack可以存入数据

    //    private static Boolean canGenFile = false;//数据可以写入URLFILE
    //
    //    private static Boolean mushGenfile = false;//必须写文件,程序退出

    //    private static ArrayList<String> string2StackArray = new ArrayList<String>();

    //    private static String needRemoveKeyInFile = "";//文件中必须移除的key

    //    private static TreeMap<String, String> fileMap = new TreeMap<String, String>();
    private static HashMap<String, String> filemap;

    /**
     * 主线程启动
     */
    public static void mainThreadStart()
    {
        urlStack = new Stack<String>();
        // 初始化栏目状态
        initMap();
        // 获取当前url
        getUrlParams();
        // 启动线程服务
        initTask();
    }

    private static void initTask()
    {

        uidStack.push("jlp_start");
        uidStack.push("jlp_sy");
        /********** 7 处理更新状态操作 暂停10秒(定时器执行一次取消) **************/
        thread7 = new Thread(new Runnable()
        {// 单起线程运行一次
            @SuppressWarnings("static-access")
            @Override
            public void run()
            {
                // //////System.out.println("/********** 7 处理更新状态操作 暂停10秒(定时器执行一次取消) **************/");
                try
                {
                    if (checkNetStatus())
                    {
                        System.out
                            .println("thread7thread7thread7&thread7&thread7&thread7&:网络OK更新状态");
                        realtime2Stack();
                    }
                    else
                    {
                        System.out
                            .println("thread7thread7thread7&thread7&thread7&thread7&:网络不通,停止更新状态");
                    }
                    Thread.sleep(13000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        });

        /******************** 1 每秒检测是否是新URL//每秒(转入8线程) ************/
        /******************* 2 监听页面是否发生改变//每秒(转入8线程) ************/
        /******************* 3 检测堆栈//每500毫秒 *****************************/

        TimerTask task3 = new TimerTask()
        {
            @Override
            public void run()
            {
                // System.out.println("/******************* 3 检测堆栈//每500毫秒 ***************************/");
                switch (listenUrlStack())
                {
                    case 1:// 小于STACKSIZE条信息
                        // 可以写入
                        if (checkNetStatus())
                        {
                            if (urlStack.size() < STACKSIZE)
                            {
                                System.out.println(
                                    "listenUrlStack()listenUrlStack()listenUrlStack()||||" +
                                        urlStack.size() + "网络通且空间够");
                                can2stack = true;
                            }
                            else if ((urlStack.size() > STACKSIZE) ||
                                (urlStack.size() == STACKSIZE))
                            {
                                System.out.println(
                                    "listenUrlStack()listenUrlStack()listenUrlStack()||||" +
                                        urlStack.size() + "网络通,空间不够");
                                can2stack = false;
                            }
                        }
                        else
                        {//网络不通
                            System.out.println(
                                "listenUrlStack()listenUrlStack()listenUrlStack()||||" +
                                    urlStack.size() + "网络不通");
                            can2stack = false;
                        }

                        break;
                    case 2:// 大于STACKSIZE条信息
                        // 可以生成file
                        System.out.println("listenUrlStack()listenUrlStack()listenUrlStack()||||" +
                            urlStack.size() + "|||stack大于N,具备输出file条件|||||");
                        can2stack = false;
                        //                    canGenFile = true;
                        break;

                    default:
                        // //////System.out.println("堆栈数据错误");
                        break;
                }
            }
        };
        Timer timer3 = new Timer();
        timer3.schedule(task3, 0, timer3Interval);
        /******************* 4 文件生成;//每秒 *******************************/

        TimerTask task4 = new TimerTask()
        {
            @Override
            public void run()
            {
            }
        };
        Timer timer4 = new Timer();
        timer4.schedule(task4, 0, timer4Interval);
        /******************** 5 文件读出,删除;//1秒 **************************/
        TimerTask task5 = new TimerTask()
        {
            //            int file2stackOnce = 1;

            @Override
            public void run()
            {
                if (can2stack)
                {//堆栈可以写,并且文件key非空

                    System.out.println(
                        "thread5*&can2stack||timer5&can2stack||timer5&can2stack||timer5&can2stack||timer5&can2stack||timer5&can2stack||");
                    readFile2Map(URLFILE, CssApplication.getInstance());
                    System.out.println(
                        "thread5*thread5*thread5*thread5*thread5*thread5*thread5*thread5*filemap" +
                            filemap.toString());
                    removeKeyFromFile(CssApplication.getInstance(), URLFILE, filemap);
                    //                    file2stackOnce++;

                }
                //////System.out.println("堆栈写入条件不满足或者文件没有数据");
                //////System.out.println("*************************************************************************");
            }
        };
        Timer timer5 = new Timer();
        timer5.schedule(task5, 0, timer5Interval);

        /******************** 6 堆栈发送,删除;//30秒 *************************/
        TimerTask task6 = new TimerTask()
        {
            @Override
            public void run()
            {
                if (checkNetStatus())
                {
                    //////System.out.println("||||网络良好,可以执行readStack2Server");
                    readStack2Server();// 发送url地址
                }
                else
                {
                    //////System.out.println("||||||||||||||网络不通,不会发送|||||||执行存入URLFILE操作||");
                    removeStack2File();
                }
            }
        };
        Timer timer6 = new Timer();
        timer6.schedule(task6, 0, timer6Interval);

        /******************** 8 监听程序是否在前台运行 ***********************/
        TimerTask task8 = new TimerTask()
        {
            @Override
            public void run()
            {
                // //////System.out.println("/******************** 8 监听程序是否在前台运行 ***********************/");
                if (runOnce == 0 && checkIsInApp())
                {
                    threadStart();
                    runOnce = 1;
                }
                else if (!checkIsInApp())
                {
                    threadPause();
                    runOnce = 0;
                }
            }
        };
        Timer timer8 = new Timer();
        timer8.schedule(task8, 0, timer8Interval);
    }

    /**
     * 进入程序时获取Url参数
     *
     * @return url path
     */
    private static String getUrlParams()
    {

        String url = "";
        urlBean = UserTrackUrlBean.getInstance();
        urlBean.setCustID(UrlParams.setCustID());// 客户代码
        urlBean.setUrlID(UrlParams.setUrlID());// 当前访问的栏目代码
        urlBean.setOrgID(UrlParams.setOrgID());// 营业部编码
        urlBean.setOrgDesc(UrlParams.setOrgDesc());// 营业部名称
        urlBean.setUserType(UrlParams.setUserType());// 用户类型----------------------
        urlBean.setUserLevel(UrlParams.setUserLevel());// 用户等级
        urlBean.setRealName(UrlParams.setRealName());// 真实姓名
        urlBean.setSystemCode(UrlParams.setSystemCode());// 非空, 进行分析的系统编码
        urlBean.setSystemVer(UrlParams.setSystemVer());// 进行分析的系统版本
        urlBean.setSessionId(UrlParams.setSessionId());// 此次回话的ID
        urlBean.setHits(UrlParams.setHits());// 页面点击数
        urlBean.setOpera(UrlParams.setOpera());// 此次访问的操作类型
        urlBean.setTerminaltype(UrlParams.setTerminaltype());// 访问终端类型----------------
        urlBean.setVisitorID(UrlParams.setVisitorID());// 标示浏览用户身份的ID
        urlBean.setoS(UrlParams.setOS());// 操作系统及版本
        urlBean.setiSP(UrlParams.setISP());// 网络运营商----------------------
        urlBean.setNetworkType(UrlParams.setNetworkType());// 联网方式------------------
        urlBean.setResolu(UrlParams.setResolu());// 客户端分辨率-------------------
        urlBean.setOSCharacter(UrlParams.setOSCharacter());// 客户端语言---------------
        urlBean.setChannel(UrlParams.setChannel());// apk来源----------------------
        return url;
    }

    private static boolean checkIsInApp()
    {
        Boolean inApp = true;// 默认在程序

        Boolean home = false;// 默认非主页
        if (Gloable.getInstance().getCurrentSite().equals(""))
        {
            // //////System.out.println("栏目没有匹配,程序外");
            home = true;
        }

        if ((!Gloable.getInstance().getIsLock()) &&
            (!Gloable.getInstance().getIsHome()))
        {
            // //////System.out.println("--------------------程序转到前台-----------------------------------------------------");
            inApp = true;
        }
        else if (Gloable.getInstance().getIsLock() || home)
        {
            // //////System.out.println("||||||||||||||||||||屏幕关闭/程序后台||||||||||||||||||||||||||||||||||||||||||||||||||||||||");
            inApp = false;
        }
        return inApp;
    }

    /**
     * 暂停线程1,2,7
     */
    private static void threadPause()
    {
        timer1.cancel();
        task1.cancel();
        timer2.cancel();
        task2.cancel();
    }

    /**
     * 恢复1,2,7线程
     */
    private static void threadStart()
    {
        timer1 = new Timer();
        task1 = new TimerTask()
        {
            @Override
            public void run()
            {
                // //////System.out.println("/******************** 1 每秒检测是否是新URL//每秒 *********************/");
                if (newUrl())
                {// 如果是新url,这一次不用存,实时发送
                    // //////System.out.println("新url,先获取KEY");
                    Gloable.getInstance().setOpera("0");
                    Gloable.getInstance().setKey("");
                    hashmap.put(urlname, getKeyOnce());
                }
                else
                {
                    // //////System.out.println("已经有KEY的URL");
                    if (Gloable.getInstance().getOpera().equals("0"))
                    {
                        // //////System.out.println("设置一次opera为0");
                        Gloable.getInstance().setKey(hashmap.get(urlname));
                        Gloable.getInstance().setOpera("1");
                    }
                }
            }
        };
        timer1.schedule(task1, 0, timer1Interval);
        timer2 = new Timer();
        task2 = new TimerTask()
        {
            @Override
            public void run()
            {
                // //////System.out.println("/******************* 2 监听页面是否发生改变//每秒 **********************/");
                String temp = UrlParams.setUrlID();// 临时UID
                if (uidStack.size() == 2)
                {
                    if (!uidStack.peek().equals(temp))
                    {
                        // //////System.out.println("不同*****存入堆栈" + uidStack.get(0) +
                        // "***" + uidStack.get(1));
                        uidStack.remove(0);
                        uidStack.push(temp);
                        isChanged = true;
                    }
                    else
                    {
                        // //////System.out.println("相同*****移走栈顶" + uidStack.get(0) +
                        // "***" + uidStack.get(1));
                        uidStack.remove(1);
                        uidStack.push(temp);
                        isChanged = false;
                    }
                }
                else
                {
                    // //////System.out.println("url前后地址堆栈格式不对");
                }
                lastUid = uidStack.get(0);
                currentUid = uidStack.get(1);
                Gloable.getInstance().setLastUid(lastUid);
                Gloable.getInstance().setCurrentSite(currentUid);
                if (isChanged)
                {// 如果地址改变
                    // 返回主线程执行dourl
                    // //////System.out.println("地址改变,地址存入堆栈");
                    realtime2Stack();
                }
                else
                {// 未改变则更新状态
                    // //////System.out.println("地址未改变,准备执行更新状态操作");
                    if (!Gloable.getInstance().getKey().equals(""))
                    {// 有key则执行update
                        //                        thread7.run();
                        thread7.run();
                    }
                }
            }
        };
        timer2.schedule(task2, 0, timer2Interval);
    }

    /**
     * 判断是否是新地址
     *
     * @return ture:新地址 false:
     */
    private static Boolean newUrl()
    {
        urlname = Gloable.getInstance().getCurrentSite();
        // //////System.out.println("当前urlname是" + urlname);
        if ((hashmap.get(urlname) != null) && !urlname.equals(""))
        {
            isNew = hashmap.get(urlname).equals("");
            Gloable.getInstance().setIsHome(false);
        }
        else
        {// urlname是""则没有匹配的地址
            Gloable.getInstance().setIsHome(true);// 非本app
            // //////System.out.println("hashmap没有匹配项,设置为非本程序状态" +
            // hashmap.get(urlname) + "*urlname=====*" + urlname);
        }

        return isNew;
    }

    /**
     * 从服务器取得key1.取得初始参数.2.向服务器发送请求.3.根据请求获得返回的key
     *
     * @return key
     */
    private static String getKeyOnce()
    {
        // ////////System.out.println("方法:" + "\n" + new
        // Exception().getStackTrace()[0].getMethodName() + "()");
        String tempurl = "";
        getUrlParams();
        if (Gloable.getInstance().getLastUid() == null)
        {
            urlsourcename = "jly_sy";
        }
        else
        {
            urlsourcename = UrlParams.setLastUID();
        }

        urlkey = UrlParams.setKey();
        ram = Math.random() + "";
        TRACKPATH = ParamSetting.getInstance().getTrackPath();
        try
        {
            tempurl = TRACKPATH + TrackBase64Encoder.encode(urlBean.toString(), "gb2312") +
                "&urlSourceName=" + TrackBase64Encoder.encode(urlsourcename, "gb2312")
                + "&key=" + urlkey + "&ram=" + ram;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        String jsobString = TrackConn.execute(tempurl) + "";

        if (!jsobString.equals("null"))
        {
            Gloable.getInstance().setJsonString(jsobString);
        }
        else
        {
            Gloable.getInstance().setJsonString("{\"key\":\"\"}");
        }
        try
        {
            if ((Gloable.getInstance().getJsonString() != null) &&
                (!Gloable.getInstance().getJsonString().equals("null")))
            {
                // ////////System.out.println("Gloable.getInstance().getJsonString()"
                // +
                // Gloable.getInstance().getJsonString());
                JSONObject jb = new JSONObject(Gloable.getInstance().getJsonString());
                if (jb.getString("key") != null)
                {
                    urlkey = jb.getString("key");
                    // //////System.out.println("urlkeyString" + urlkey);
                }
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return urlkey;
    }

    /**
     * 判断用户类型,如果用户类型为登录,存入一次
     */
    private static void checkUserType()
    {
        if (LoginInfo.getInstance().getUserLevel() == null)
        {
        }
        else if (LoginInfo.getInstance().getUserLevel().equals("0"))
        {
        }
        else if (LoginInfo.getInstance().getUserLevel().equals(""))
        {
        }
        else if (Gloable.getInstance().getIsLogin() == null ||
            !Gloable.getInstance().getIsLogin())
        {// 未登录
            LoginInfo instance = LoginInfo.getInstance();
            String logininfo =
                "loginID=" + instance.getLoginID() + "&" + "loginType=" + instance.getLoginType() +
                    "&" + "userType=" + instance.getUserType() + "&"
                    + "userLevel=" + instance.getUserLevel() + "&" + "realName=" +
                    instance.getRealName() + "&" + "orgId=" + instance.getOrgID() + "&" + "orgDesc="
                    + instance.getOrgDesc() + "&" + "systemCode=" + instance.getSystemCode() + "&" +
                    "loginState=" + instance.getLoginState() + "&" + "loginErrorDesc="
                    + instance.getLoginErrorDesc();
            String logTrackPath = ParamSetting.getInstance().getLogTrackPath();
            String jsobString;
            try
            {
                jsobString = TrackConn
                    .execute(logTrackPath + TrackBase64Encoder.encode(logininfo, "gb2312")) + "";
                // //////System.out.println("执行登陆操作,取得一次URL地址" + logTrackPath +
                // logininfo + "\n" + jsobString);
                Gloable.getInstance().setIsLogin(true);// 设置为登录状态
                Gloable.getInstance().setJsonString(jsobString);
                boolean contains = jsobString.contains("success");
                if (contains)
                {
                    System.out.println("登陆成功");
                }
                else
                {
                    System.out.println("登陆失败");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * 实时存入URL堆栈信息
     */
    private static void realtime2Stack()
    {
        System.out.println(
            "realtime2Stackrealtime2Stackrealtime2Stackrealtime2Stackrealtime2Stackrealtime2Stackrealtime2Stackrealtime2Stack");

        checkUserType();// 这个必须执行,以发送longin状态的地址
        if (Gloable.getInstance().getLastUid().equals(""))
        {
            Gloable.getInstance().setLastUid("jlp_start");
        }
        urlsourcename = UrlParams.setLastUID();
        // /////////////以下数据在存入stack时产生//////////////
        urlkey = UrlParams.setKey();
        ram = Math.random() + "";
        TRACKPATH = ParamSetting.getInstance().getTrackPath();
        if (!Gloable.getInstance().getCurrentSite().equals(""))
        {
            try
            {
                ready2push = TRACKPATH + TrackBase64Encoder.encode(urlBean.toString(), "gb2312") +
                    "&urlSourceName=" + TrackBase64Encoder.encode(urlsourcename, "gb2312")
                    + "&key=" + urlkey + "&ram=" + ram;
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            if (can2stack)
            {
                System.out.println("执行push堆栈操作执行push堆栈操作执行push堆栈操作执行push堆栈操作执行push堆栈操作");
                if (ready2push != null)
                {
                    urlStack.push(ready2push);
                }
                else
                {
                    System.out.println(
                        "realtime2Stack()realtime2Stack()realtime2Stack()ready2push is null");
                }
            }
            else
            {
                readStack2File(URLFILE, ready2push, CssApplication.getInstance());
            }
        }
        else
        {
            // //////System.out.println("目前不在程序内,目前不在程序内目前不在程序内目前不在程序内目前不在程序内目前不在程序内目前不在程序内");
        }
    }

    /**
     * 根据返回的整型判断是堆栈状态
     *
     * @return 1:小于50 2:大于或等于50
     */
    private static int listenUrlStack()
    {
        if (urlStack.size() < STACKSIZE)
        {
            return 1;
        }
        else
        {
            return 2;
        }
    }

    /**
     * 从stack读文件写到urlFile(PRIVATE模式),每次操作完文件后必须有个临时map存储状态, 在进行完读出工作后重新写入新的内容到file
     * 存入键值对形式,保证key唯一,取当前系统时间作文件名
     *
     * @param fileName filename
     * @param content content
     * @param context context
     *
     * @return success true,fail false;
     */
    private static Boolean readStack2File(String fileName, String content, Context context)
    {
        System.out.println(
            "readStack2File()readStack2File()readStack2File()readStack2File()readStack2File()readStack2File()readStack2File()");
        Boolean ok;
        try
        {
            FileOutputStream out = context.openFileOutput(fileName, Context.MODE_APPEND);
            Properties properties = new Properties();
            String filekey = new Date().getTime() + "";
            properties.put(filekey + "", content);
            System.out.println("写入文件操作" + filekey + "\n" + "******content" + content);
            properties.store(out, "");
            out.close();
            ok = true;
        }
        catch (IOException e)
        {
            ok = false;
            e.printStackTrace();
        }
        return ok;
    }

    /**
     * 读取文件内容到Stack中
     *
     * @param fileName 读取的文件名
     * @param context context
     */
//    @SuppressWarnings("rawtypes")
    @SuppressWarnings({"rawtypes", "SuspiciousMethodCalls"})
    private static void readFile2Map(String fileName, Context context)
    {
        System.out.println(
            "readFile2Map()readFile2Map()readFile2Map()readFile2Map()readFile2Map()readFile2Map()readFile2Map()");
        Properties pro2remove = new Properties();
        FileInputStream stream;
        filemap = new HashMap<String, String>();

        try
        {
            stream = context.openFileInput(fileName);
            /********************* 测试遍历properties ***key相同的只打印一个,如果remove7,那么7所有的数据都不显示 *********************************/
            pro2remove.load(stream);

            Iterator iter = pro2remove.entrySet().iterator();
            ArrayList<String> array = new ArrayList<String>();
            ArrayList<HashMap<String, String>> arrymap = new ArrayList<HashMap<String, String>>();

            while (iter.hasNext())
            {
                Properties.Entry entry = (Properties.Entry) iter.next();
                Object key = entry.getKey();
                Object val = entry.getValue();
                HashMap<String, String> kvMap = new HashMap<String, String>();
                filemap.put((String) key, (String) val);
                kvMap.put((String) key, (String) val);
                //                array.add(Long.parseLong((String) key));
                arrymap.add(kvMap);
                array.add((String) key);
            }

            System.out.println(
                "readFile2Map()readFile2Map()readFile2Map()readFile2Map()当前文件内容条数是" + array.size());
            if (!array.isEmpty())
            {
                System.out.println(
                    "readFile2Map()readFile2Map()readFile2Map()readFile2Map()" + array.get(0));
            }

            int Map2StackCount;
            Map2StackCount = STACKSIZE - 10;//文件存入堆栈的数量要略小于Stack额定大小
            if (arrymap.size() < Map2StackCount)
            {
                int lens = arrymap.size();
                for (int i = 0; i < lens; i++)
                {
                    //                    needRemoveKeyInFile = arrymap.get(0);
                    HashMap<String, String> tempmap = arrymap.get(0);
                    //                    while (iter2.hasNext())
                    for (Map.Entry<String, String> stringStringEntry : tempmap.entrySet())
                    {
                        Object key = stringStringEntry.getKey();
                        Object val = stringStringEntry.getValue();

                        if (val != null)
                        {
                            urlStack.push((String) val);
                            System.out.println(
                                new StringBuilder()
                                    .append("readFile2Map()readFile2Map()arrymap.size()")
                                    .append(arrymap.size()).append(" <").append(Map2StackCount)
                                    .append(" Map2StackCount条件URLfile中移除").append(filemap.get(key))
                                    .toString());
                            filemap.remove(key);
                        }
                        else
                        {
                            System.out.println(
                                "readFile2Map()readFile2Map()readFile2Map()readFile2Map() VAL to push IS NULL");
                        }
                    }
                    arrymap.remove(0);
                }
            }
            else
            {
                for (int i = 0; i < Map2StackCount; i++)
                {
                    HashMap<String, String> tempmap = arrymap.get(0);
                    for (Map.Entry<String, String> stringStringEntry : tempmap.entrySet())
                    {
                        Object key = stringStringEntry.getKey();
                        Object val = stringStringEntry.getValue();
                        urlStack.push((String) val);
                        System.out.println(
                            "readFile2Map()readFile2Map()arrymap.size()" + arrymap.size() + ">=" +
                                Map2StackCount + " Map2StackCount条件URLfile中移除"
                                + filemap.get(key));
                        filemap.remove(key);
                    }
                    arrymap.remove(0);
                }
            }

            stream.close();
        }
        catch (IOException e)
        {
            try
            {
                FileOutputStream out = context.openFileOutput(URLFILE, Context.MODE_PRIVATE);
//                Properties properties = new Properties();
//                properties.put("" + "", "");
//                properties.store(out, "");
                out.close();
            }
            catch (IOException e1)
            {
                System.out.println(
                    "readfile2mapreadfile2mapreadfile2mapreadfile2mapreadfile2mapreadfile2map,创建文件失败");
            }
        }
    }

    @SuppressWarnings("rawtypes")
    private static void removeKeyFromFile(Context context, String filename,
        HashMap<String, String> hmap)
    {
        System.out.println(
            "removeKeyFromFile(removeKeyFromFile(removeKeyFromFile(removeKeyFromFile(removeKeyFromFile(removeKeyFromFile(");
        FileOutputStream fileOut = null;
        Properties properties = new Properties();
        for (Map.Entry<String, String> stringStringEntry : hmap.entrySet())
        {
            Object key = stringStringEntry.getKey();
            Object val = stringStringEntry.getValue();
            properties.put(key + "", val + "");
        }

        try
        {
            fileOut = context.openFileOutput(filename, Context.MODE_PRIVATE);//
            properties.store(fileOut, "");
        }
        catch (FileNotFoundException e)
        {
            System.out.println("file not found");
        }
        catch (IOException e)
        {
            System.out.println("io exception");
        }
        finally
        {
            try
            {
                assert fileOut != null;
                fileOut.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    private static Boolean checkNetStatus()
    {
        String nettype = "";
        ConnectivityManager connectivityManager = (ConnectivityManager) CssApplication.getInstance()
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
            Gloable.getInstance().setNetType(nettype);// 上网方式
        }
        else
        {
            Gloable.getInstance().setNetType("没网络");
        }
        return !Gloable.getInstance().getNetType().equals("没网络");
    }

    /**
     * 发送堆栈信息
     */
    private static void readStack2Server()
    {
        if (!urlStack.empty())
        {
            System.out.println(urlStack.size() +
                "readStack2Server()readStack2Server()readStack2Server()readStack2Server()readStack2Server()readStack2Server()");
            int tempStackSize = urlStack.size();
            int outLoopTimer = tempStackSize / 10;
            if (tempStackSize % 10 > 0)
            {
                outLoopTimer++;
            }
            for (int j = 0; j < outLoopTimer; j++)
            {
                for (int i = 0; i < 10; i++)
                {//一次只发10次
                    if (!urlStack.empty() && urlStack.get(0) != null)
                    {// urlStack是实时变化的
                        JSONObject S2C = TrackConn.execute(urlStack.get(0));
                        //                        if (S2C != null) {

                        try
                        {
                            System.out.println(
                                "readStack2Server()readStack2Server()readStack2Server()|||||||||服务器返回数据" +
                                    S2C.toString());
                            if (S2C.toString().contains("success"))
                            {
                                //System.out.println("*********************发送成功,清空该位置stack数据*********************" + "i==" + i);
                                urlStack.remove(0);
                            }
                            else
                            {
                                //System.out.println("*********************     发送失败  *********************");
                            }
                        }
                        catch (NullPointerException e)
                        {
                            System.out.println(
                                "readStack2Server()readStack2Server()readStack2Server()||发送有问题服务器返回空" +
                                    urlStack.get(0));//可能网络来不及判断,断开状态下发送了一条数据
                            //**********这里也可以添加上新的值
                        }
                        //                        }else{
                        //                            System.out.println("readStack2Server()readStack2Server()readStack2Server()||发送有问题服务器返回空"+urlStack.get(0));//可能网络来不及判断,断开状态下发送了一条数据
                        //                        }
                    }
                    else
                    {
                        System.out.println(
                            "readStack2Server()readStack2Server()readStack2Server()数据已经被清空");
                    }
                }
                try
                {
                    System.out.println(
                        "readStack2Server()readStack2Server()readStack2Server()执行发送10次,暂停3秒再发送......");
                    Thread.sleep(3000);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
        }
        else
        {
            System.out.println(
                "readStack2Server()readStack2Server()readStack2Server()readStack2Server()readStack2Server()");
        }
    }

    /**
     * 网络断开时清空stack数据并写入URLFILE
     */
    private static void removeStack2File()
    {
        System.out.println(
            "removeStack2File()removeStack2File()removeStack2File()removeStack2File()removeStack2File()removeStack2File()removeStack2File()");
        if (!urlStack.empty())
        {
            int tempStackSize = urlStack.size();
            int outLoopTimer = tempStackSize / 10;
            if (tempStackSize % 10 > 0)
            {
                outLoopTimer++;
            }
            for (int j = 0; j < outLoopTimer; j++)
            {
                for (int i = 0; i < 10; i++)
                {
                    if (!urlStack.empty())
                    {// urlStack是实时变化的
                        //////System.out.println("*********************存入FILE,清空该位置stack数据*********************" + "i==" + i);
                        readStack2File(URLFILE, urlStack.get(0), CssApplication.getInstance());
                        urlStack.remove(0);
                    }
                    else
                    {
                        System.out.println(
                            "removeStack2File()removeStack2File()removeStack2File()断网,stack数据已经被清空到URLFILE");
                    }
                }
            }
        }
        else
        {
        }
        //////System.out.println("stack为空");
    }

    /**
     * 初始化栏目地址状态
     */
    private static void initMap()
    {

        hashmap = new HashMap<String, String>();
        hashmap.put(".MainActivity", "");
        hashmap.put(".JlpActivity", "");
        hashmap.put(".RestartDialog", "");
        hashmap.put(".TranslucentMenuActivity", "");
        hashmap.put("com.cssweb.android.sms.SMSJHActivity", "");
        hashmap.put("com.cssweb.android.quote.SQS", "");
        hashmap.put("com.cssweb.android.quote.DSS", "");
        hashmap.put("com.cssweb.android.quote.ZSS", "");
        hashmap.put("com.cssweb.android.quote.ZJS", "");
        hashmap.put("com.cssweb.android.quote.QQSP", "");
        hashmap.put("com.cssweb.android.quote.QHSCGridActivity", "");
        hashmap.put("com.cssweb.android.quote.QHSCBaseActivity", "");
        hashmap.put("com.cssweb.android.quote.HSZS", "");
        hashmap.put("com.cssweb.android.quote.GlobalMarketActivity", "");
        hashmap.put("com.cssweb.android.quote.OTCFundActivity", "");
        hashmap.put("com.cssweb.android.quote.PaiMing", "");
        hashmap.put("com.cssweb.android.quote.DaPan", "");
        hashmap.put("com.cssweb.android.quote.FenLei", "");
        hashmap.put("com.cssweb.android.quote.QueryStock", "");
        hashmap.put("com.cssweb.android.quote.PersonalStock", "");
        hashmap.put("com.cssweb.android.quote.QuoteAlarm", "");
        hashmap.put("com.cssweb.android.quote.TrendActivity", "");
        hashmap.put("com.cssweb.android.quote.KLineActivity", "");
        hashmap.put("com.cssweb.android.quote.KLine2Activity", "");
        hashmap.put("com.cssweb.android.quote.QuotePrice", "");
        hashmap.put("com.cssweb.android.quote.QuoteDetail", "");
        hashmap.put("com.cssweb.android.quote.GetF10List", "");
        hashmap.put("com.cssweb.android.quote.GetF10Content", "");
        hashmap.put("com.cssweb.android.quote.GlobalMarket", "");
        hashmap.put("com.cssweb.android.quote.FLineActivity", "");
        hashmap.put("com.cssweb.android.quote.StockTypeFund", "");
        hashmap.put("com.cssweb.android.quote.FundQueryCondition", "");
        hashmap.put("com.cssweb.android.quote.FundQuery", "");
        hashmap.put("com.cssweb.android.quote.Sunpublic", "");
        hashmap.put("com.cssweb.android.quote.JingZhiQuery", "");
        hashmap.put("com.cssweb.android.quote.QuoteWarning", "");
        hashmap.put("com.cssweb.android.quote.QuoteSet", "");
        hashmap.put("com.cssweb.android.quote.QHHQActivity", "");
        hashmap.put("com.cssweb.android.quote.GGHQActivity", "");
        hashmap.put("com.cssweb.android.quote.HKMainboard", "");
        hashmap.put("com.cssweb.android.quote.GlobalHuiShi", "");
        hashmap.put("com.cssweb.android.quote.SunpublicQueryCondition", "");
        hashmap.put("com.cssweb.android.web.OpenHrefDisplay", "");
        hashmap.put("com.cssweb.android.web.WebViewDisplay", "");
        hashmap.put("com.cssweb.android.web.JxgpcActivity", "");
        hashmap.put("com.cssweb.android.web.CfpdActivity", "");
        hashmap.put("com.cssweb.android.web.OpenPdfDisplay", "");
        hashmap.put("com.cssweb.android.fzjy.VistualTrade", "");
        hashmap.put("com.cssweb.android.sz.ClearActivity", "");
        hashmap.put("com.cssweb.android.sz.Setting", "");
        hashmap.put("com.cssweb.android.sz.HelpActivity", "");
        hashmap.put("com.cssweb.android.sz.CustomSettingActivity", "");
        hashmap.put("com.cssweb.android.sz.HQRefreshSettingActivity", "");
        hashmap.put("com.cssweb.android.sz.ServerSettingActivity", "");
        hashmap.put("com.cssweb.android.sz.LockSettingActivity", "");
        hashmap.put("com.cssweb.android.tyyh.ExperienceUsers", "");
        hashmap.put("com.cssweb.android.video.CustomMediaPlayer", "");
        hashmap.put("com.cssweb.android.video.VideoPlayer", "");
        hashmap.put("com.cssweb.android.trade.CnjjActivity", "");
        hashmap.put("com.cssweb.android.trade.FundActivity", "");
        hashmap.put("com.cssweb.android.trade.BankActivity", "");
        hashmap.put("com.cssweb.android.trade.TransferFundsActivity", "");
        hashmap.put("com.cssweb.android.trade.user.AccountManage", "");
        hashmap.put("com.cssweb.android.trade.stock.GetDetails", "");
        hashmap.put("com.cssweb.android.trade.stock.GetDetailsH", "");
        hashmap.put("com.cssweb.android.trade.stock.StockTrading", "");
        hashmap.put("com.cssweb.android.trade.stock.GetPosition", "");
        hashmap.put("com.cssweb.android.trade.stock.StockCancel", "");
        hashmap.put("com.cssweb.android.trade.stock.AssetQuery", "");
        hashmap.put("com.cssweb.android.trade.stock.TodayEntrust", "");
        hashmap.put("com.cssweb.android.trade.stock.TodayDeal", "");
        hashmap.put("com.cssweb.android.trade.stock.ExchangeFund", "");
        hashmap.put("com.cssweb.android.trade.stock.StockWarrant", "");
        hashmap.put("com.cssweb.android.trade.stock.StockWarrant", "");
        hashmap.put("com.cssweb.android.util.DateRange", "");
        hashmap.put("com.cssweb.android.trade.stock.HistoryEntrust", "");
        hashmap.put("com.cssweb.android.trade.stock.HistoryDeal", "");
        hashmap.put("com.cssweb.android.trade.stock.Bill", "");
        hashmap.put("com.cssweb.android.trade.stock.NewStockMatch", "");
        hashmap.put("com.cssweb.android.trade.stock.ModifyPassword", "");
        hashmap.put("com.cssweb.android.trade.stock.StockList", "");
        hashmap.put("com.cssweb.android.trade.stock.ShareholderList", "");
        hashmap.put("com.cssweb.android.trade.login.LoginActivity", "");
        hashmap.put("com.cssweb.android.trade.bank.TransferQuery", "");
        hashmap.put("com.cssweb.android.trade.bank.BankBalanceQuery", "");
        hashmap.put("com.cssweb.android.trade.bank.BankFundTransfer", "");
        hashmap.put("com.cssweb.android.trade.bank.FundBankTransfer", "");
        hashmap.put("com.cssweb.android.trade.bank.TransferDateRange", "");
        hashmap.put("com.cssweb.android.trade.transferFunds.FundsDetails", "");
        hashmap.put("com.cssweb.android.trade.transferFunds.ZfTransfer", "");
        hashmap.put("com.cssweb.android.trade.transferFunds.TransferFundsDateRange", "");
        hashmap.put("com.cssweb.android.trade.transferFunds.TransferFundsQuery", "");
        hashmap.put("com.cssweb.android.trade.stock.ModifyContactInfo", "");
        hashmap.put("com.cssweb.android.trade.fund.FundTrading", "");
        hashmap.put("com.cssweb.android.trade.fund.TodayTrust", "");
        hashmap.put("com.cssweb.android.trade.fund.FundTransfer", "");
        hashmap.put("com.cssweb.android.trade.fund.FundMelonSet", "");
        hashmap.put("com.cssweb.android.trade.fund.FundPortio", "");
        hashmap.put("com.cssweb.android.trade.fund.FundAccount", "");
        hashmap.put("com.cssweb.android.trade.fund.FundRiskTest", "");
        hashmap.put("com.cssweb.android.trade.fund.HistoryConclusion", "");
        hashmap.put("com.cssweb.android.trade.fund.HistoryTrust", "");
        hashmap.put("com.cssweb.android.trade.fund.FundCompany", "");
        hashmap.put("com.cssweb.android.trade.fund.FundAccountForm", "");
        /* webview部分 */
        hashmap.put("NJZQ_ZXHD_EGHT_ssgg", "");
        hashmap.put("NJZQ_ZXHD_EGHT_ckbs", "");
        hashmap.put("NJZQ_ZXHD_EGHT_phb", "");
        hashmap.put("NJZQ_ZXHD_EGHT_cjwt", "");
        hashmap.put("NJZQ_ZXHD_EGHT_wdbs", "");
        hashmap.put("NJZQ_ZXHD_EGHT_zc", "");
        hashmap.put("NJZQ_ZXHD_EGHT_gxmm", "");
        hashmap.put("NJZQ_ZXHD_EGHT_qxsz", "");
        hashmap.put("NJZQ_ZXHD_EGHT_login", "");
        hashmap.put("NJZQ_NZBD_JXZQC_yxc", "");
        hashmap.put("NJZQ_NZBD_JXZQC_hxc", "");
        hashmap.put("NJZQ_NZBD_JXZQC_tzzh", "");
        hashmap.put("NJZQ_NZBD_JXZQC_zqyj", "");
        hashmap.put("NJZQ_NZBD_JXZQC_qhyj", "");
        hashmap.put("NJZQ_NZBD_CFPD_mrjp", "");
        hashmap.put("NJZQ_NZBD_CFPD_tzjt", "");
        hashmap.put("NJZQ_NZBD_CFPD_ztpx", "");
        hashmap.put("NJZQ_NZFC_NZDT_nzdt", "");
        hashmap.put("NJZQ_NZFC_NZDT_zjnz", "");
        hashmap.put("NJZQ_NZFC_NZDT_jyh", "");
        hashmap.put("NJZQ_NZFC_NZDT_sp", "");
        hashmap.put("NJZQ_ZXLP_CPBD", "");
        hashmap.put("NJZQ_ZXLP_YWZJ", "");
        hashmap.put("NJZQ_ZXLP_PMHH", "");
        hashmap.put("NJZQ_ZXLP_TGLC", "");
        hashmap.put("NJZQ_ZXLP_ZJLX", "");
        hashmap.put("NJZQ_ZXLP_GG", "");
        hashmap.put("NJZQ_ZSYYT_YYKH", "");
        hashmap.put("NJZQ_ZSYYT_YYTGG", "");
        hashmap.put("NJZQ_ZSYYT_YYWD", "");
        hashmap.put("NJZQ_ZSYYT_YWZX", "");
        hashmap.put("NJZQ_ZSYYT_TZZJY", "");
        hashmap.put("NJZQ_ZSYYT_TJKH", "");
        hashmap.put("NJZQ_JLP_WDZQTAG", "");
        hashmap.put("NJZQ_JLP_YYKHTAG", "");
        hashmap.put("NJZQ_JLP_TYKTAG", "");
        hashmap.put("NJZQ_JFSC", "");
        hashmap.put("NJZQ_ZXG_GONGGAO", "");
        hashmap.put("NJZQ_ZXG_DIANPIN", "");
        hashmap.put("NJZQ_ZXG_QINGBAO", "");
        hashmap.put("NJZQ_ZXG_ZHENDUAN", "");
        hashmap.put("NJZQ_ZXG_JJ_ZHENDUAN", "");
        hashmap.put("NJZQ_FIND_PASSWORD", "");
        hashmap.put("NJZQ_SQTYK", "");
        hashmap.put("NJZQ_RESET_SERVIR_PASSWORD", "");
        hashmap.put("NJZQ_TYYH_FIND_PASSWORD", "");
        hashmap.put("NJZQ_FUND_RISK_TEST", "");
        hashmap.put("NJZQ_WTJY_SZLC", "");
        hashmap.put("NJZQ_WTJY_RZRQ", "");
        hashmap.put("NJZQ_ZXHD_ZJJP", "");
        hashmap.put("NJZQ_ZXHD_TZGW", "");
        hashmap.put("NJZQ_ZXHD_KHJL", "");
        hashmap.put("NJZQ_ZXHD_ZXKF", "");
        hashmap.put("NJZQ_ZXHD_KFRX", "");
        hashmap.put("NJZQ_ZXHD_GNLY", "");
        hashmap.put("NJZQ_ZXHD_CJWT", "");

        /*********************************************************************/
        /*********************************************************************/
        /*********************************************************************/
        hashmap.put("jlp_start", "");// 金罗盘启动

        // 重写
        hashmap.put("jlp_sy", "");// 金罗盘首页
        /*********************************************************************/

        hashmap.put("nzbd", "");// 宁证宝典
        hashmap.put("nzbd_jxzqc", "");// 宁证宝典-精选证券池
        hashmap.put("nzbd_jxzqc_yxc", "");// 宁证宝典-精选证券池-优选池
        hashmap.put("nzbd_jxzqc_hxc", "");// 宁证宝典-精选证券池-核心池
        hashmap.put("nzbd_tzzh", "");// 宁证宝典-投资组合
        hashmap.put("nzbd_zqyj", "");// 宁证宝典-证券研究
        hashmap.put("nzbd_qhyj", "");// 宁证宝典-期货研究
        hashmap.put("nzbd_qhyj", "");// 宁证宝典-财富频道
        hashmap.put("nzbd_cfpd_mrjp", "");// 宁证宝典-财富频道-每日解盘
        hashmap.put("nzbd_cfpd_tzlt", "");// 宁证宝典-财富频道-投资论坛
        hashmap.put("nzbd_cfpd_ztpx", "");// 宁证宝典-财富频道-专题培训
        /*********************************************************************/
        hashmap.put("hqbj", "");// 行情报价
        hashmap.put("hqbj_kline", "");// 行情报价-K线
        hashmap.put("hqbj_hqyj", "");// 行情报价-行情预警
        hashmap.put("hqbj_zxg", "");// 行情报价-自选股
        hashmap.put("hqbj_dpzs", "");// 行情报价-大盘指数
        hashmap.put("hqbj_zhpm", "");// 行情报价-综合排名
        hashmap.put("hqbj_ggcx", "");// 行情报价-个股查询
        hashmap.put("hqbj_flbj", "");// 行情报价-分类报价
        hashmap.put("hqbj_cwjj", "");// 行情报价-场外基金
        hashmap.put("hqbj_cwjj_gpxjj", "");// 行情报价-场外基金-股票型基金
        hashmap.put("hqbj_cwjj_zqxjj", "");// 行情报价-场外基金-债券型基金
        hashmap.put("hqbj_cwjj_hbxjj", "");// 行情报价-场外基金-货币型基金
        hashmap.put("hqbj_cwjj_hhxjj", "");// 行情报价-场外基金-混合型基金
        hashmap.put("hqbj_cwjj_jzzzph", "");// 行情报价-场外基金-净值增长排行
        hashmap.put("hqbj_cwjj_ygsm", "");// 行情报价-场外基金-阳光私募
        hashmap.put("hqbj_qqsc", "");// 行情报价-全球市场
        hashmap.put("hqbj_qqsc_gghq", "");// 行情报价-全球市场-港股行情
        hashmap.put("hqbj_qqsc_gghq_hszs", "");// 行情报价-全球市场-港股行情-恒生指数
        hashmap.put("hqbj_qqsc_gghq_xgzb", "");// 行情报价-全球市场-港股行情-香港主板
        hashmap.put("hqbj_qqsc_gghq_xgcyb", "");// 行情报价-全球市场-港股行情-香港创业板
        hashmap.put("hqbj_qqsc_wwsc", "");// 行情报价-全球市场-外围市场
        hashmap.put("hqbj_qqsc_qqhs", "");// 行情报价-全球市场-全球汇市
        hashmap.put("hqbj_qhhq", "");// 行情报价-期货行情
        hashmap.put("hqbj_qhhq_zjs", "");// 行情报价-期货行情-中金所
        hashmap.put("hqbj_qhhq_sqs", "");// 行情报价-期货行情-上期所
        hashmap.put("hqbj_qhhq_dss", "");// 行情报价-期货行情-大商所
        hashmap.put("hqbj_qhhq_zss", "");// 行情报价-期货行情-郑商所
        hashmap.put("hqbj_qhhq_qqsp", "");// 行情报价-期货行情-全球商品
        /*********************************************************************/

        hashmap.put("wtjy", "");// 委托交易
        hashmap.put("wtjy_zhgl", "");// 委托交易-账户管理
        hashmap.put("wtjy_mr", "");// 委托交易-买入
        hashmap.put("wtjy_mc", "");// 委托交易-卖出
        hashmap.put("wtjy_cd", "");// 委托交易-撤单
        hashmap.put("wtjy_yzzz", "");// 委托交易-银证转帐
        hashmap.put("wtjy_yzzz_zjzr", "");// 委托交易-银证转帐-资金转入
        hashmap.put("wtjy_yzzz_zjzc", "");// 委托交易-银证转帐-资金转出
        hashmap.put("wtjy_yzzz_yhye", "");// 委托交易-银证转帐-银行余额
        hashmap.put("wtjy_yzzz_zzcx", "");// 委托交易-银证转帐-转账查询
        hashmap.put("wtjy_yzzz_zjdb", "");// 委托交易-银证转帐-资金调拨
        hashmap.put("wtjy_yzzz_zjdb_zjcx", "");// 委托交易-银证转帐-资金调拨-资金查询
        hashmap.put("wtjy_yzzz_zjdb_zfzz", "");// 委托交易-银证转帐-资金调拨-主辅转账
        hashmap.put("wtjy_yzzz_zjdb_fzzz", "");// 委托交易-银证转帐-资金调拨-辅主转账
        hashmap.put("wtjy_yzzz_zjdb_dbls", "");// 委托交易-银证转帐-资金调拨-调拨流水
        hashmap.put("wtjy_drwt", "");// 委托交易-当日委托
        hashmap.put("wtjy_drcj", "");// 委托交易-当日成交
        hashmap.put("wtjy_cccx", "");// 委托交易-持仓查询
        hashmap.put("wtjy_zhzc", "");// 委托交易-账户资产
        hashmap.put("wtjy_zjls", "");// 委托交易-资金流水
        hashmap.put("wtjy_lscx", "");// 委托交易-历史查询
        hashmap.put("wtjy_cwjj", "");// 委托交易-场外基金
        hashmap.put("wtjy_cwjj_jjsg", "");// 委托交易-场外基金-基金申购
        hashmap.put("wtjy_cwjj_jjsh", "");// 委托交易-场外基金-基金赎回
        hashmap.put("wtjy_cwjj_jjrg", "");// 委托交易-场外基金-基金认购
        hashmap.put("wtjy_cwjj_cd", "");// 委托交易-场外基金-撤单
        hashmap.put("wtjy_cwjj_drwt", "");// 委托交易-场外基金-当日委托
        hashmap.put("wtjy_cwjj_jjzh", "");// 委托交易-场外基金-基金转换
        hashmap.put("wtjy_cwjj_lscj", "");// 委托交易-场外基金-历史成交
        hashmap.put("wtjy_cwjj_lswt", "");// 委托交易-场外基金-历史委托
        hashmap.put("wtjy_cwjj_ccjj", "");// 委托交易-场外基金-持仓基金
        hashmap.put("wtjy_cwjj_fhsz", "");// 委托交易-场外基金-分红设置
        hashmap.put("wtjy_cwjj_jjzh2", "");// 委托交易-场外基金-基金账户
        hashmap.put("wtjy_cwjj_jjkh", "");// 委托交易-场外基金-基金开户
        hashmap.put("wtjy_cwjj_fxcp", "");// 委托交易-场外基金-风险测评
        hashmap.put("wtjy_cnjj", "");// 委托交易-场内基金
        hashmap.put("wtjy_cnjj_cnrg", "");// 委托交易-场内基金-场内认购
        hashmap.put("wtjy_cnjj_cnsg", "");// 委托交易-场内基金-场内申购
        hashmap.put("wtjy_cnjj_cnsh", "");// 委托交易-场内基金-场内赎回
        hashmap.put("wtjy_szlc", "");// 委托交易-神州理财
        hashmap.put("wtjy_rzrq", "");// 委托交易-融资融券
        hashmap.put("wtjy_phcx", "");// 委托交易-配号查询
        hashmap.put("wtjy_xgmm", "");// 委托交易-修改密码
        hashmap.put("wtjy_gdzl", "");// 委托交易-股东资料
        hashmap.put("wtjy_xxxg", "");// 委托交易-信息修改
        /*********************************************************************/
        hashmap.put("zxhd", "");// 在线互动
        hashmap.put("zxhd_zjjp", "");// 在线互动-专家解盘
        hashmap.put("zxhd_wdtzgw", "");// 在线互动-我的投资顾问
        hashmap.put("zxhd_wdkfjl", "");// 在线互动-我的客户经理
        hashmap.put("zxhd_zxkf", "");// 在线互动-在线客服
        hashmap.put("zxhd_kfrx", "");// 在线互动-客服热线
        hashmap.put("zxhd_gnly", "");// 在线互动-广纳良言
        hashmap.put("zxhd_cjwt", "");// 在线互动-常见问题
        hashmap.put("zxhd_mncg", "");// 在线互动-模拟炒股
        hashmap.put("zxhd_mncg_ssgg", "");// 在线互动-模拟炒股-赛事公告
        hashmap.put("zxhd_mncg_ckbs", "");// 在线互动-模拟炒股-查看比赛
        hashmap.put("zxhd_mncg_phb", "");// 在线互动-模拟炒股-排行榜
        hashmap.put("zxhd_mncg_cjwt", "");// 在线互动-模拟炒股-常见问题
        hashmap.put("zxhd_mncg_wdbs", "");// 在线互动-模拟炒股-我的比赛
        hashmap.put("zxhd_mncg_qxsz", "");// 在线互动-模拟炒股-权限设置

        /*********************************************************************/
        hashmap.put("zsyyt", "");// 掌上营业厅
        hashmap.put("zsyyt_yykh", "");// 掌上营业厅-预约开户
        hashmap.put("zsyyt_yytgg", "");// 掌上营业厅-营业厅公告
        hashmap.put("zsyyt_yywd", "");// 掌上营业厅-营业网点
        hashmap.put("zsyyt_ywzx", "");// 掌上营业厅-业务中心
        hashmap.put("zsyyt_tzzjy", "");// 掌上营业厅-投资者教育
        hashmap.put("zsyyt_tjkh", "");// 掌上营业厅-推荐开户
        /*********************************************************************/
        hashmap.put("nzfc", "");// 宁证风采
        hashmap.put("nzfc_nzdt", "");// 宁证风采-宁证动态
        hashmap.put("nzfc_zjnz", "");// 宁证风采-走进宁证
        hashmap.put("nzfc_jyh", "");// 宁证风采-精英汇
        /*********************************************************************/
        hashmap.put("jfly", "");// 积分乐园
        /*********************************************************************/
        hashmap.put("zxlp", "");// 资讯罗盘
        hashmap.put("zxlp_cpbd", "");// 资讯罗盘-操盘必读
        hashmap.put("zxlp_ywzj", "");// 资讯罗盘-要闻直击
        hashmap.put("zxlp_pmhh", "");// 资讯罗盘-盘面护航
        hashmap.put("zxlp_tgnc", "");// 资讯罗盘-特供内参
        hashmap.put("zxlp_zjlx", "");// 资讯罗盘-资金流向
        hashmap.put("zxlp_gg", "");// 资讯罗盘-股歌gugle
        /*********************************************************************/
        hashmap.put("wdzq", "");// 我的专区
        hashmap.put("wdzq_wdcc", "");// 我的专区_我的持仓
        hashmap.put("wdzq_wdzx", "");// 我的专区_我的自选
        hashmap.put("wdzq_jrrd", "");// 我的专区_今日热点
        hashmap.put("wdzq_wdkhjl", "");// 我的专区_我的客户经理
        /*********************************************************************/
        hashmap.put("szym", "");// 设置页面
        /*********************************************************************/
        hashmap.put("tyyhdly", "");// 体验用户登录页
        hashmap.put("zhmm", "");// 找回密码
        hashmap.put("yykh", "");// 预约开户
        hashmap.put("sqtyk", "");// 申请体验卡
        /*********************************************************************/
        hashmap.put("jyyhdly", "");// 交易用户登录页
        hashmap.put("dxjh", "");// 短信激活
    }
}
