package com.cssweb.android.common;

import java.util.Date;

/**
 * 唯一关键字类，它可以产生在项目中唯一的编号
 */
public class UID {

    private static int gene = 0;    //防止1毫秒内产生多次运算所加的0-9之间的一个数

    public static synchronized String getID() {
        return "" + new Date().getTime() + (gene++ % 100);
    }
}