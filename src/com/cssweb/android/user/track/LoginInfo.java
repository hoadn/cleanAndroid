package com.cssweb.android.user.track;

/**
 * 用户登录信息,在loginActivity中setter
 *
 * @author oyqx
 */
public class LoginInfo
{
    /**
     * 登录账号
     */
    private String loginID = "";// 登录账号

    /**
     * 登录账号类型
     */
    private String loginType = "";// 登录账号类型

    /**
     * 用户类型
     */
    private String userType = "3";// 用户类型

    /**
     * 用户等级
     */
    private String userLevel = "";// 用户等级

    /**
     * 真实姓名
     */
    private String realName = "";// 真实姓名

    /**
     * 营业部编码
     */
    private String orgID = "";// 营业部编码

    /**
     * 营业部名称
     */
    private String orgDesc = "";// 营业部名称

    /**
     * 进行分析的系统编码
     */
    private String systemCode = "";// 进行分析的系统编码

    // private String loginModule;// 登录模块

    /**
     * 登录状态
     */
    private String loginState = "";// 登录状态

    /**
     * 登录错误信息
     */
    private String loginErrorDesc = "";// 登录错误信息

    private static LoginInfo INSTANCE = null;

    /**
     * 获取用户登录信息的唯一实例
     *
     * @return 获取用户登录信息的唯一实例
     */
    public static LoginInfo getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new LoginInfo();
        }
        return INSTANCE;
    }

    public String getLoginID()
    {
        return loginID;
    }

    /**
     * 用户登录账号
     *
     * @param loginID 用户登录账号
     */
    public void setLoginID(String loginID)
    {
        this.loginID = loginID;
    }

    public String getLoginType()
    {
        return loginType;
    }

    /**
     * 17：体验卡号 18：服务密码 19：交易密码
     *
     * @param loginType 用户登录账号类型 17：体验卡号 18：服务密码 19：交易密码
     */
    public void setLoginType(String loginType)
    {
        this.loginType = loginType;
    }

    public String getUserType()
    {
        return userType;
    }

    /**
     * 用户类型, 1：交易用户 2：体验用户 3：浏览用户 4：注册用户 程序启动时请初始化:3,浏览
     *
     * @param userType 用户类型,1：交易用户 2：体验用户 3：浏览用户 4：注册用户
     */
    public void setUserType(String userType)
    {
        this.userType = userType;
    }

    public String getUserLevel()
    {
        return userLevel;
    }

    /**
     * 用户等级,注意,此方法请在ReceiverControl.sendLogininfo()方法在务必ondestory()方法调用
     *
     * @param userLevel
     */
    public void setUserLevel(String userLevel)
    {
        this.userLevel = userLevel;
    }

    public String getRealName()
    {
        return realName;
    }

    /**
     * 用户真实姓名
     *
     * @param realName 用户真实姓名
     */
    public void setRealName(String realName)
    {
        this.realName = realName;
    }

    public String getOrgID()
    {
        return orgID;
    }

    /**
     * 营业厅代码
     *
     * @param orgID 营业厅代码
     */
    public void setOrgID(String orgID)
    {
        this.orgID = orgID;
    }

    public String getOrgDesc()
    {
        return orgDesc;// 你懂的
    }

    /**
     * 营业厅名称
     *
     * @param orgDesc 营业厅名称
     */
    public void setOrgDesc(String orgDesc)
    {
        this.orgDesc = orgDesc;
    }

    public String getSystemCode()
    {
        return systemCode;
    }

    /**
     * 进行分析的系统编码,如JLP_ANDROID 金罗盘Android版
     *
     * @param systemCode 进行分析的系统编码,如JLP_ANDROID 金罗盘Android版
     */
    public void setSystemCode(String systemCode)
    {
        this.systemCode = systemCode;
    }

    // public String getLoginModule() {
    // return loginModule;
    // }
    //
    // public void setLoginModule(String loginModule) {
    // this.loginModule = loginModule;
    // }

    public String getLoginState()
    {
        return loginState;
    }

    /**
     * 登录状态1：登录成功 0：登录失败
     *
     * @param loginState 登录状态1：登录成功 0：登录失败
     */
    public void setLoginState(String loginState)
    {
        this.loginState = loginState;
    }

    public String getLoginErrorDesc()
    {
        return loginErrorDesc;
    }

    /**
     * 登录错误信息;登录不成功时，该值不应该为空
     *
     * @param loginErrorDesc 登录错误信息;登录不成功时，该值不应该为空
     */
    public void setLoginErrorDesc(String loginErrorDesc)
    {
        this.loginErrorDesc = loginErrorDesc;
    }

    /**
     * 退出时重置用户信息userType(1,交易,2,体验,3浏览,4,注册).在MainActiviy()询问是否退出方法(case 1)后调用
     * 用户登录账号,登录账号类型,用户类型,用户等级,真实姓名,营业部编码,营业部名称,登录状态
     */
    void initLoginInfo()
    {
        setLoginID("");// 用户登录账号清空
        setLoginType(""); // 登录账号类型
        setUserType("3"); // 用户类型
        setUserLevel("");// 用户等级
        setRealName("");// 真实姓名
        setOrgID("");// 营业部编码
        setOrgDesc(""); // 营业部名称
        setLoginState(""); // 登录状态
        Gloable.getInstance().setIsLogin(false);// 登录状态设置为未登录
    }
}
