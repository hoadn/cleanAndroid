package com.cssweb.android.user.track;

import android.app.Application;

/**
 * 用于获取程序context等内容 manifest配置Application
 *
 * @author oyqx
 */
public class CssApplication extends Application
{
    private static CssApplication instance;

    /**
     * @return context
     */
    public static CssApplication getInstance()
    {
        return instance;
    }

    @Override
    public void onCreate()
    {
        super.onCreate();
        instance = this;
    }
}