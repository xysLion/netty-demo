package com.ancun.up2yun.constant;

/**
 * 消息相关
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/16
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public interface MsgConstant {

    /** 请求异常 */
    String REQUEST_RECEIVE_EXCEPTION = "从节点[s%]接收到文件过程中出现异常：s%。";

    /** 请求体太大 */
    String FILE_RECEIVE_SIZE_OUTMAX = "异常发生原因：文件超过服务器设置的所允许的最大大小！";

    /** 数据接收异常报警标题 */
    String RECEIVE_EXCEPTION_NOTICE_TITLE = "上传组件接收数据发生异常";

    /** 服务器节点信息 */
    String SERVER_NODE_INFO = "服务器节点[s%]";

    /** 文件接收成功信息 */
    String FILE_RECEIVE_SUCCESS = "已从节点[s%]接收到文件[s%],准备上传到云对象存储。";

    /** 已用硬盘空间 */
    String USED_DISK_INFO = "服务器节点[s%]已用掉硬盘空间：s% MB";

    /** 回调异常情况1 */
    String SERVER_CALLBACK_RETRY_REASON_1 = "向服务器[s%]发送回调请求时出现异常，回调服务器基础信息[s%]，响应结果为s%。";

    /** 回调异常情况2 */
    String SERVER_CALLBACK_RETRY_REASON_2 = "向服务器[s%]发送回调请求时出现异常，回调服务器基础信息[s%]。";

    /** 回调成功 */
    String SERVER_CALLBACK_SUCCESS = "文件[s%]在服务器节点[s%]上处理结束，并且向服务器[s%]发送回调请求成功！";

    /** 回调失败 */
    String SERVER_CALLBACK_FAILURE = "文件[s%]在服务器节点[s%]上回调服务器[s%]发送回调请求不成功，回调服务器基础信息[s%]，已重试3次还是失败，失败原因【s%】。";

    /** 回调异常异常报警标题 */
    String CALLBACK_EXCEPTION_NOTICE_TITLE = "上传组件回调功能发生异常";

    /** 文件上传失败 */
    String FILE_UPLOAD_FAILURE = "文件[s%]在服务器节点[s%]上s%，上传到云的信息[s%]，已重试3次还是失败，失败原因【s%】。";

    /** 上传功能异常报警标题 */
    String UPLOAD_EXCEPTION_NOTICE_TITLE = "上传组件上传功能发生异常";

}