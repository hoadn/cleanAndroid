package com.cssweb.android.user.track;

/**
 * <pre>
 * 该类用于设定用户行为分析的一些相关参数
 * 1:间隔发送消息的时间       interval (默认10秒)
 * 2:用户登录行为的基本地址,logTrackPath,LOGINTRACK 默认"http://172.16.1.24:8080/user_login?loginfo="
 * 3:用户栏目访问的基本地址,trackPath,   TRACKPATH  默认"http://172.16.1.24:8080/user_track?URL="
 * 4:是否持续发送,         isEnternal 默认"true"
 *  以上类别参数请在调用启动画面时调用一次.如.SplashActivity 的onCreate()
 * </pre>
 *
 * @author 欧阳
 */
@SuppressWarnings({"ALL"}) public class ParamSetting
{
    private String logTrackPath = "http://172.16.1.24:8080/user_login?loginfo=";

    private String trackPath = "http://172.16.1.24:8080/user_track?URL=";

    private Boolean isEnternal = true;

    private Integer interval = 10000;

    private static ParamSetting INSTANCE = null;

    /**
     * 获取参数设置单一实例
     *
     * @return paramsetting
     */
    public static ParamSetting getInstance()
    {
        if (INSTANCE == null)
        {
            INSTANCE = new ParamSetting();
        }
        return INSTANCE;
    }

    public String getLogTrackPath()
    {
        return logTrackPath;
    }

    /**
     * 用户登录行为的基本地址,logTrackPath,LOGINTRACK 默认"http://172.16.1.24:8080/user_login?loginfo="
     *
     * @param logTrackPath
     */
    public void setLogTrackPath(String logTrackPath)
    {
        this.logTrackPath = logTrackPath;
    }

    public String getTrackPath()
    {
        return trackPath;
    }

    /**
     * 用户栏目访问的基本地址,trackPath,   TRACKPATH  默认"http://172.16.1.24:8080/user_track?URL="
     *
     * @param trackPath
     */
    public void setTrackPath(String trackPath)
    {
        this.trackPath = trackPath;
    }

    public Boolean getIsEnternal()
    {
        return isEnternal;
    }

    /**
     * 是否持续发送,         isEnternal 默认"true"
     *
     * @param isEnternal
     */
    public void setIsEnternal(Boolean isEnternal)
    {
        this.isEnternal = isEnternal;
    }

    public Integer getInterval()
    {
        return interval;
    }

    /**
     * 间隔发送消息的时间       interval (默认10000毫秒)
     *
     * @param interval
     */
    public void setInterval(Integer interval)
    {
        this.interval = interval;
    }
}
