package com.ancun.task.utils;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * 异常工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ExceptionUtil {

    /**
     * 以字符串形式返回异常堆栈信息
     * @param e
     * @return 异常堆栈信息字符串
     */
    public static String getStackTrace(Throwable e)
    {
        StringWriter writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer,true));

        return writer.toString();
    }
}
