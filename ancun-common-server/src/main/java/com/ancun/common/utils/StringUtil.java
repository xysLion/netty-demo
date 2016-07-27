package com.ancun.common.utils;

/**
 * 字符串操作工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class StringUtil {

    public static String nullToStr(String str) {

        if (str == null) {
            str = "";
        }

        return str;
    }

    public static String nullToStrTrim(String str) {
        return nullToStr( str ).trim();
    }

    public static boolean isHalfAngle(String str) {

        str = nullToStrTrim(str);
        return str.length() == getWordLength(str);
    }

    public static int getWordLength(String str) {

        str = nullToStr(str);
        return str.replaceAll("[^\\x00-\\xff]","**").length();
    }
}
