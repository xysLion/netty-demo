package com.ancun.task.utils;

import com.ancun.task.constant.Constant;
import com.ancun.task.domain.request.ReqBody;
import com.ancun.task.domain.request.ReqCommon;
import com.ancun.task.domain.request.ReqJson;
import com.ancun.utils.email.AsynEmailUtil;
import com.ancun.utils.email.EmailBean;
import com.ancun.utils.email.Smtp;
import com.ancun.utils.sms.SmsUtil;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * 发送通知工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class NoticeUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(NoticeUtil.class);

    /** 一天开始时间毫秒数 */
    private static long dayStartTimeMillis = 0;

    /** 当次通知时间毫秒数 */
    private static long currentNoticeTimeMillis = 0;

    /** 通知次数 */
    private static long noticeCount = 0;

    /** 一天时间毫秒数 */
    private static final long MILLIS_FOR_ONE_DAY = 24 * 60 * 60 * 1000;

    /** rest请求组件 */
    @Resource
    private RestClient restClient;

    /** gson对象 */
    private final Gson gson = new Gson();

    /**
     * 发送通知信息
     *
     * @param subject 邮件标题
     * @param message 通知信息
     */
    public void sendNotice(String subject, String message) {

        // 通知服务器地址
        String url = SpringContextUtil.getProperty(Constant.NOTICE_URL);

        // 基础请求体内容
        Map<String, String> content = baseContent(subject, message);

        // 发送短信通知
        sendSMS(url, content);

        // 发送邮件通知
        sendEMAIL(url, content);
    }

    /**
     * 构建基础请求体内容
     *
     * @param subject   标题
     * @param message   通知信息
     * @return          基础请求体内容
     */
    private Map<String, String> baseContent(String subject, String message) {
        Map<String, String> content = Maps.newHashMap();

        content.put("subject", subject);
        content.put("message", message);
        content.put("asyn", "true");

        return content;
    }

    /**
     * 创建一个请求体
     *
     * @param action    接口名称
     * @param content   请求内容
     * @param <T>       泛型
     * @return          请求体
     */
    private <T> ReqJson<T> creatReqJson(String action, T content){
        // 请求体头部
        ReqCommon common = new ReqCommon();
        common.setAction(action);
        common.setReqtime(String.valueOf(System.nanoTime()));

        ReqBody<T> reqBody = new ReqBody<T>(common, content);
        ReqJson<T> reqJson = new ReqJson<T>(reqBody);

        return reqJson;
    }

    /**
     * 发送短信通知
     *
     * @param url       通知服务器地址
     * @param basecontent   请求体基本信息
     */
    private void sendSMS(String url, Map<String, String> basecontent){

        // 设置今天开始时间毫秒数
        if (dayStartTimeMillis == 0) {
            // 取得今天0点0时0分时的毫秒数
            Calendar cal = Calendar.getInstance();
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.MILLISECOND, 0);
            dayStartTimeMillis = cal.getTimeInMillis();
        }

        // 设置当次通知时间
        currentNoticeTimeMillis = System.currentTimeMillis();

        // 如果当次时间-第一次通知时间超过24小时(即一天)
        if ((currentNoticeTimeMillis - dayStartTimeMillis) > MILLIS_FOR_ONE_DAY) {
            // 通知次数清0
            noticeCount = 0;
            // 向后移一天
            dayStartTimeMillis += MILLIS_FOR_ONE_DAY;
        } else {
            // 通知次数+1
            noticeCount++;
        }

        // 一天最大允许短信通知数
        long maxTimes = Long.parseLong(SpringContextUtil.getProperty(Constant.NOTICED_PHONE_ALLOW_TIMES));
        //  如果未达到一天中允许的上限则允许短信通知
        if (noticeCount < maxTimes) {
            String phone = SpringContextUtil.getProperty(Constant.NOTICED_PHONE_NUMBER);
            if (!Strings.isNullOrEmpty(phone)) {

                // 请求体内容
                Map<String, String> smsContent = basecontent;
                smsContent.put("phoneNo", phone);
                ReqJson<Map<String, String>> reqJson = creatReqJson("sms", smsContent);

                // 发送短信通知
                restClient.post(url, gson.toJson(reqJson), new HashMap<String, Object>());
            }
        }

    }

        /**
     * 发送短信通知
     *
     * @param url       通知服务器地址
     * @param basecontent   请求体基本信息
     */
    private void sendEMAIL(String url, Map<String, String> basecontent){

        // 取得邮件通知地址列表
        String toAddress = SpringContextUtil.getProperty(Constant.NOTICED_EMAIL_TO);

        // 请求体内容
        Map<String, String> emailContent = basecontent;
        emailContent.put("emailTo", toAddress);
        ReqJson<Map<String, String>> reqJson = creatReqJson("email", emailContent);

        // 发送短信通知
        restClient.post(url, gson.toJson(reqJson), new HashMap<String, Object>());

    }
}
