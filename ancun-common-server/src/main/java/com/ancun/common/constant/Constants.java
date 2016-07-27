package com.ancun.common.constant;

/**
 * 程序用常量
 *
 * @Created on 2015年3月17日
 * @author tom.tang
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class Constants {

    //编码方式
    public static final String ENCODING_GBK = "GBK";
    public static final String ENCODING_UTF8 = "UTF-8";
    public static final String ENCODING_ISO88591 = "ISO-8859-1";
    public static final String CHARSETNAME_DEFAULT = ENCODING_UTF8;

    /** 分号 */
    public static final String SEMICOLON = ";";
    /** 逗号 */
    public static final String COMMA = ",";
    /** 冒号 */
    public static final String COLON = ":";

    /** 请求体request标记 */
    public static final String REQUEST = "request";

    /** 请求体Common部分标记 */
    public static final String REQ_COMMON = "common";

    /** 请求体Content部分标记 */
    public static final String REQ_CONTENT = "content";

    // 邮件配置相关
    /** 邮件发送方SMTP */
    public static final String EMAIL_HOST    = "email_host";
    /** 邮件发送方SMTP端口 */
    public static final String EMAIL_PORT    = "email_port";
    /** 是否需要身份验证 */
    public static final String EMAIL_AUTH    = "email_auth";
    /** 发送方账户 */
    public static final String EMAIL_USERNAME    = "email_username";
    /** 发送方密码 */
    public static final String EMAIL_PASSWORD    = "email_password";
    /** 发送方显示 */
    public static final String EMAIL_FROM    = "email_from";

    // 短信配置相关
    /** 默认短信通道 */
    public static final String SMS_DEFAULT    = "sms_default";
    /** 碟信通道 */
    public static final int SMS_PIPLE_DIEXIN    = 0;
    /** 至臻通道 */
    public static final int SMS_PIPLE_ZHIZHEN   = 1;
    /** 创蓝通道 */
    public static final int SMS_PIPLE_CHUANGLAN = 2;
    /** 示远通道 */
    public static final int SMS_PIPLE_SHIYUAN   = 3;
    /** 微讯通道 */
    public static final int SMS_PIPLE_WEIXUN    = 4;
    /** 一个短信通道发送失败时是否使用其他短信通道 */
    public static final String SEND_FAILD_USE_OTHER_PIPLE = "send_fail_use_other_channel";

    /** 短信任务 */
    public static final String TASK_TYPE_SMS = "sms";

    // 短信发送状态
    /** 短消息发送中 */
    public static final String SMS_SENDING = "SENDING";
    /** 短消息已到第三方短信发送平台 */
    public static final String SMS_DELIVRDING = "DELIVRDING";
    /** 短消息转发成功 */
    public static final String SMS_DELIVRD = "DELIVRD";
    /** 短消息超过有效期 */
    public static final String SMS_EXPIRED = "EXPIRED";
    /** 短消息是不可达的 */
    public static final String SMS_UNDELIV = "UNDELIV";
    /** 未知短消息状态 */
    public static final String SMS_UNKNOWN = "UNKNOWN";
    /** 短消息被短信中心拒绝 */
    public static final String SMS_REJECTD = "REJECTD";
    /** 目的号码是黑名单号码 */
    public static final String SMS_DTBLACK = "DTBLACK";
    /** 系统忙 */
    public static final String SMS_ERR = "ERR:104";
    /** 审核驳回 */
    public static final String SMS_REJECT = "REJECT";
    /** 网关内部状态 */
    public static final String SMS_OTHERS = "OTHERS";

    /** 微讯短信状态事件 */
    public static final String EVENT_FOR_SMS_WEIXUN = "MT";

    // IP白名单配置相关
    /** 是否启用ip白名单 */
    public static final String IPLIMIT = "iplimt";
    /** ip白名单列表 */
    public static final String IPLIMIT_IPS = "iplimt_ips";

    // 鉴权配置相关
    /** 是否需要签名校验标识 */
    public static final String SIGN_VALID = "sign_valid";
    /** 签名密钥 */
    public static final String SIGN_KEY = "sign_key";
    /** get请求列表 */
    public static final String GET_URIS = "get_uris";

    /** 缺少省份和城市信息 */
    public static final int LACK_INFO_ALL = 0;

    /** 缺少省份信息 */
    public static final int LACK_INFO_PROVINCE = 1;

    /** 移动号码正则表达式 */
    public static final String CHINA_MOBILE_REGEX = "china_mobile_regex";

    /** 联通号码正则表达式 */
    public static final String CHINA_UNICOM_REGEX = "china_unicom_regex";

    /** 电信号码正则表达式 */
    public static final String CHINA_TELECOM_REGEX = "china_telecom_regex";

    /** 手机号码正则表达式 */
    public static final String MOBILE_REGEX = "mobile_regex";

    /** 固话号码正则表达式 */
    public static final String TELEPHONE_REGEX = "telephone_regex";

    /** 无连接符固话号码正则表达式 */
    public static final String TELEPHONE_NO_HYPHEN_REGEX = "telephone_no_hyphen_regex";

    /** 特殊号码 */
    public static final String SPECIAL_CHINA_TELECOM = "special_china_telecom";
}
