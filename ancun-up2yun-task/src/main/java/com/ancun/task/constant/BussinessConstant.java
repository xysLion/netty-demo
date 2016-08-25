package com.ancun.task.constant;

/**
 * 业务常量类
 *
 * @Created on 2015-04-27
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class BussinessConstant {

    /** 上传OSS时显示文件名 */
    public static final String FILE_KEY = "fileKey";

    /** 文件网路路径 */
    public static final String FILE_URL = "file_url";

    /**  回调参数名 */
    public static final String CALLBACK_URI = "callbackUri";

    /**  文件md5参数名 */
    public static final String FILE_MD5 = "fileMd5";

    /** 上传云类型参数名 */
    public static final String YUN_TYPE = "yunType";

    /**  时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 将文件上传到云 */
    public static final int UPTOYUN = 0;

    /** 将上传结果通知回调方 */
    public static final int CALLBACK = 1;

    /** 成功 */
    public static final int SUCCESS = 100000;

    /** 系统异常 */
    public static final int SYSTEM_EXCEPTION = 100001;

}
