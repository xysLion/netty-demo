package com.ancun.task.utils;

import java.io.UnsupportedEncodingException;

/**
 * 字符串工具类。
 *
 * @Created on 2015-02-11
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class StringUtil {

    public static final String EMPTY_STRING = "";

    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }

        int length = str.length();
        int start = 0;
        int end = length;

        // ɨ���ַ�ͷ��
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }

        // ɨ���ַ�β��
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end) && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end) && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }

        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }

        return str;
    }

    public static String trim(String str, String stripChars) {
        return trim(str, stripChars, 0);
    }

    public static String trimToEmpty(String str, String stripChars) {
        String result = trim(str, stripChars);

        if (result == null) {
            return EMPTY_STRING;
        }

        return result;
    }

    public static String trimToEmpty(String str) {
        return trimToEmpty(str, null);
    }

    public static String byte2hex(byte[] b) {

        String str = "";
        String stmp = "";

        int length = b.length;

        for (int n = 0; n < length; n++) {
            stmp = (Integer.toHexString(b[n] & 0XFF));
            if (stmp.length() == 1) {
                str += "0";
            }
            str += stmp;
        }

        return str.toLowerCase();
    }

    public static boolean isBlank(String str) {
        int length;

        if ((str == null) || ((length = str.length()) == 0)) {
            return true;
        }

        for (int i = 0; i < length; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static int getRealLength(String str, String charsetName) {
        if(isBlank(str)) {
            return 0;
        }
        try {
            return str.getBytes(charsetName).length;
        } catch (UnsupportedEncodingException e) {
            return 0;
        }
    }
}
