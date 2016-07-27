package com.ancun.up2yun.constant;

/**
 * 业务常量类
 *
 * @Created on 2015-04-27
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class BussinessConstant {

    /** 用户meta信息前缀 */
    public static final String USER_META_INFO_PREFIX = "user_meta_info_prefix";

    /** 上传OSS时显示文件名 */
    public static final String FILE_KEY = "FILE_KEY";

    /** 本上传组件标识 */
    public static final String PROCESS_NUM = "process.num";

    /**  上传文件缓存临时目录 */
    public static final String TEMP_DIR = "tempdir";

    /**  是否启动只扫描本机 0:只扫描本机 1:扫描所有 */
    public static final String LOCATION_ONLY = "location.only";

    /**  回调参数名 */
    public static final String CALLBACK_URI = "CALLBACK_URI";

    /**  重试次数 */
    public static final String RETRY_TIMES = "retry.times";

    /**  上传云的BUCKET */
    public static final String BUCKET_SUFFIX = "BUCKET_SUFFIX";

    /**  上传云的accessId */
    public static final String ACCESSID_SUFFIX = "ACCESSID_SUFFIX";

    /**  上传云的accessKey */
    public static final String ACCESSKEY_SUFFIX = "ACCESSKEY_SUFFIX";

    /**  上传云的类别 */
    public static final String YUN_TYPE = "YUN_TYPE";

    /**  文件md5参数名 */
    public static final String FILE_MD5 = "FILE_MD5";

    /**  时间格式 */
    public static final String DEFAULT_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /** 将文件上传到云 */
    public static final int UPTOYUN = 0;

    /** 将上传结果通知回调方 */
    public static final int CALLBACK = 1;

}
