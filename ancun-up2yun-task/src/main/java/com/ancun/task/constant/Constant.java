package com.ancun.task.constant;

/**
 * 常量类
 *
 * @Created on 2015-04-27
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class Constant {

    /** 任务执行间隔时间 */
    public static final String DURATION = "duration";

    /** 任务类型前缀 */
    public static final String TASK_TYPE_PREFIX = "task.type.prefix";

    /** 一次取得任务数 */
    public static final String TASK_MAX_NUM = "task.max.num";

    /** 一天允许短信通知最大次数 */
    public static final String NOTICED_PHONE_ALLOW_TIMES = "noticed.phone.allow.times";

    /** 被通知电话号码 */
    public static final String NOTICED_PHONE_NUMBER = "noticed.phone.number";

    /** 被通知邮箱地址 */
    public static final String NOTICED_EMAIL_TO = "noticed.email.to";

    /** 通知服务器地址 */
    public static final String NOTICE_URL = "notice.url";

    /** 未完成任务数 */
    public static final String NOCOMPLETE_TASK_COUNT = "未完成任务信息：%s";

    /** 未开始任务数 */
    public static final String NOSTART_TASK_COUNT = "未开始任务信息：%s";

    /** 执行中任务数 */
    public static final String HANDLING_TASK_COUNT = "执行中任务信息：%s";

    /** 上传组件日常监控 */
    public static final String UPLOAD_MONITOR = "上传组件日常监控";

    /** 上传组件异常 */
    public static final String SERVER_EXCEPTION = "上传组件异常";

    /** 服务停止信息 */
    public static final String SERVER_STOP_INFO = "服务器节点[%s]上,[%s]服务停止";

    /** 服务异常信息 */
    public static final String SERVER_EXCEPTION_INFO = "服务器节点[%s]上,[%s]服务发生异常，cause：%s";

    /** 文件上传失败 */
    public static final String FILE_UPLOAD_FAILURE = "文件[%s]在服务器节点[%s]上%s，上传到云的信息[%s]，已重试3次还是失败，失败原因【%s】。";

    /** 上传功能异常报警标题 */
    public static final String UPLOAD_EXCEPTION_NOTICE_TITLE = "上传组件上传功能发生异常";

    /** 回调异常情况1 */
    public static final String SERVER_CALLBACK_RETRY_REASON_1 = "向服务器[%s]发送回调请求时出现异常，回调服务器基础信息[%s]，响应结果为%s。";

    /** 回调异常情况2 */
    public static final String SERVER_CALLBACK_RETRY_REASON_2 = "向服务器[%s]发送回调请求时出现异常，回调服务器基础信息[%s]。";

    /** 回调成功 */
    public static final String SERVER_CALLBACK_SUCCESS = "文件[%s]在服务器节点[%s]上处理结束，并且向服务器[%s]发送回调请求成功！";

    /** 回调失败 */
    public static final String SERVER_CALLBACK_FAILURE = "文件[%s]在服务器节点[%s]上回调服务器[%s]发送回调请求不成功，回调服务器基础信息[%s]，已重试3次还是失败，失败原因【%s】。";

    /** 回调异常异常报警标题 */
    public static final String CALLBACK_EXCEPTION_NOTICE_TITLE = "上传组件回调功能发生异常";
}
