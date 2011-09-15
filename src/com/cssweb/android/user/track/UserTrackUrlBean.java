package com.cssweb.android.user.track;

public class UserTrackUrlBean
{
    /**
     * 客户代码
     */
    private String custID = "";// 客户代码

    /**
     * 当前访问的栏目代码
     */
    private String urlID = "";// 当前访问的栏目代码

    /**
     * 营业部编码
     */
    private String orgID = "";// 营业部编码

    /**
     * 营业部名称
     */
    private String orgDesc = "";// 营业部名称

    /**
     * 用户类型
     */
    private String userType = "";// 用户类型

    /**
     * 用户等级
     */
    private String userLevel = "";// 用户等级

    /**
     * 真实姓名
     */
    private String realName;// 真实姓名

    /**
     * 进行分析的系统编码
     */
    private String systemCode;// 进行分析的系统编码

    /**
     * 进行分析的系统版本
     */
    private String systemVer;// 进行分析的系统版本

    /**
     * 此次回话的ID
     */
    private String sessionId;// 此次回话的ID

    /**
     * 页面点击数
     */
    private String hits;// 页面点击数

    /**
     * 此次访问的操作类型
     */
    private String opera = "";// 此次访问的操作类型

    /**
     * 访问终端类型
     */
    private String terminaltype = "";// 访问终端类型

    /**
     * 标示浏览用户身份的ID
     */
    private String visitorID = "";// 标示浏览用户身份的ID

    /**
     * 操作系统及版本
     */
    private String oS = "";// 操作系统及版本

    /**
     * 网络运营商
     */
    private String iSP = "";// 网络运营商

    /**
     * 联网方式
     */
    private String networkType = "";// 联网方式

    /**
     * 客户端分辨率
     */
    private String resolu = "";// 客户端分辨率

    /**
     * 客户端语言
     */
    private String oSCharacter = "";// 客户端语言

    /**
     * APK获取来源;
     */
    private String channel = "";// APK获取来源;

    private static UserTrackUrlBean INSTANCE;

    public static UserTrackUrlBean getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new UserTrackUrlBean();
        }
        return INSTANCE;
    }

    /*
     * userLevel realName systemCode systemVer sessionId hits opera key
     * terminaltype visitorID oS iSP networkType resolu
     */
    // +"&"+""+
    @Override
    public String toString()
    {
        return "custID=" + this.custID + "&" + "urlID=" + this.urlID
            + "&" + "orgID=" + this.orgID + "&" + "orgDesc=" + this.orgDesc
            + "&" + "userType=" + this.userType + "&" + "userLevel="
            + this.userLevel + "&" + "realName=" + this.realName + "&"
            + "systemCode=" + this.systemCode + "&" + "systemVer="
            + this.systemVer + "&" + "sessionID=" + this.sessionId + "&"
            + "hits=" + this.hits + "&" + "opera=" + this.opera + "&" + "&"
            + "terminaltype=" + this.terminaltype + "&" + "visitorID="
            + this.visitorID + "&" + "oS=" + this.oS + "&" + "ISP="
            + this.iSP + "&" + "networkType=" + this.networkType + "&"
            + "resolu=" + this.resolu + "&" + "oSCharacter="
            + this.oSCharacter + "&" + "channel=" + this.channel;
    }

    public String getCustID()
    {
        return custID;
    }

    public void setCustID(String custID)
    {
        this.custID = custID;
    }

    public String getUrlID()
    {
        return urlID;
    }

    public void setUrlID(String urlID)
    {
        this.urlID = urlID;
    }

    public String getOrgID()
    {
        return orgID;
    }

    public void setOrgID(String orgID)
    {
        this.orgID = orgID;
    }

    public String getOrgDesc()
    {
        return orgDesc;
    }

    public void setOrgDesc(String orgDesc)
    {
        this.orgDesc = orgDesc;
    }

    public String getUserType()
    {
        return userType;
    }

    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getUserLevel()
    {
        return userLevel;
    }

    public void setUserLevel(String userLevel)
    {
        this.userLevel = userLevel;
    }

    public String getRealName()
    {
        return realName;
    }

    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getSystemCode()
    {
        return systemCode;
    }

    public void setSystemCode(String systemCode)
    {
        this.systemCode = systemCode;
    }

    public String getSystemVer()
    {
        return systemVer;
    }

    public void setSystemVer(String systemVer)
    {
        this.systemVer = systemVer;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public void setSessionId(String sessionId)
    {
        this.sessionId = sessionId;
    }

    public String getHits()
    {
        return hits;
    }

    public void setHits(String hits)
    {
        this.hits = hits;
    }

    public String getOpera()
    {
        return opera;
    }

    public void setOpera(String opera)
    {
        this.opera = opera;
    }

    public String getTerminaltype()
    {
        return terminaltype;
    }

    public void setTerminaltype(String terminaltype)
    {
        this.terminaltype = terminaltype;
    }

    public String getVisitorID()
    {
        return visitorID;
    }

    public void setVisitorID(String visitorID)
    {
        this.visitorID = visitorID;
    }

    public String getoS()
    {
        return oS;
    }

    public void setoS(String oS)
    {
        this.oS = oS;
    }

    public String getiSP()
    {
        return iSP;
    }

    public void setiSP(String iSP)
    {
        this.iSP = iSP;
    }

    public String getNetworkType()
    {
        return networkType;
    }

    public void setNetworkType(String networkType)
    {
        this.networkType = networkType;
    }

    public String getResolu()
    {
        return resolu;
    }

    public void setResolu(String resolu)
    {
        this.resolu = resolu;
    }

    public String getOSCharacter()
    {
        return oSCharacter;
    }

    public void setOSCharacter(String oSCharacter)
    {
        this.oSCharacter = oSCharacter;
    }

    public String getChannel()
    {
        return channel;
    }

    public void setChannel(String channel)
    {
        this.channel = channel;
    }
}
