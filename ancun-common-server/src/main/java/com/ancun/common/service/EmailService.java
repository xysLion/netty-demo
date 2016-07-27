package com.ancun.common.service;

import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.exception.EduException;
import com.ancun.common.utils.DispatcherBus;
import com.ancun.common.utils.ParamValid;
import com.ancun.utils.email.EmailBean;
import com.ancun.utils.email.Smtp;
import com.ancun.utils.taskbus.HandleTask;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.text.MessageFormat;
import java.util.Map;

import static com.ancun.common.constant.Constants.COMMA;
import static com.ancun.common.constant.Constants.SEMICOLON;
import static com.ancun.common.constant.ResponseConst.PARAMS_EMAIL_IS_ERROR;

/**
 * 邮件通知业务sevice
 *
 * @Created on 2015年5月8日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    /** 邮件正则表达式 */
    private static final String EMAIL_REGEX =
            "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

    /** 缓存 */
    @Resource
    private BasicDataCache cache;

    /**
     * 注册进分发总线
     *
     * @param bus   分发总线
     */
    @Autowired
    public EmailService(DispatcherBus bus) {
        bus.register(this);
    }

    /**
     * 发送邮件通知
     *
     * @param params 参数Map
     *     具体参数有：
     *     emailTo:接收人邮件地址
     *     subject:邮件标题
     *     message:邮件内容
     *     num    :通道编码
     * @return 发送成功
     */
    @HandleTask(taskType = "email")
    public Object doWork(Map<String, String> params) {

        // 取得接受人邮件地址
        Object emailTo = params.get("emailTo");
        ParamValid.checkParam(emailTo, ResponseConst.PARAMS_EMAILTO_NOT_EMPTY);

        Object subject = params.get("subject");

        // 取得通知内容
        Object message = params.get("message");
        ParamValid.checkParam(message, ResponseConst.PARAMS_MSG_NOT_EMPTY);

        for (String to : splitEmail(emailTo.toString())) {

            // 发送邮件通知
            EmailBean bean = new EmailBean();
            bean.setTo(to);
            bean.setSubject(subject.toString());
            bean.setContent(message.toString());
            bean.setFrom(cache.config(Constants.EMAIL_FROM));
            logger.info("调用邮件发送工具类发送邮件，接收方地址：{}", to);
            getSmtp().send(bean);
        }

        return null;
    }

    /**
     * 根据通道编号取得邮件发送方实例
     *
     * @return 邮件发送方实例
     */
    private Smtp getSmtp(){

        // 取得发送方地址
        String host = cache.config(Constants.EMAIL_HOST);

        // 取得发送方端口
        int port = Integer.parseInt(cache.config(Constants.EMAIL_PORT));

        // 取得发送方鉴权
        boolean auth = Boolean.valueOf(cache.config(Constants.EMAIL_AUTH));

        // 取得发送方用户名
        String userName = cache.config(Constants.EMAIL_USERNAME);

        // 取得发送方密码
        String password = cache.config(Constants.EMAIL_PASSWORD);

        // 返回邮件发送方实例
        return new Smtp(host, port, auth, userName, password);
    }

    /**
     * 取得email发送列表
     *
     * @param emailTo   邮件发送列表(string)
     * @return      email发送列表
     */
    public Iterable<String> splitEmail(final String emailTo) {
        String emailToTemp = emailTo;
        // 将逗号转换为分号
        emailToTemp = emailToTemp.replaceAll(COMMA, SEMICOLON);
        // 以分号分割emailTo字符串
        Iterable<String> emailToTempIt = Splitter.on(SEMICOLON)
                .omitEmptyStrings().trimResults().split(emailToTemp);

        // 格式不正确email列表
        Iterable<String> invalidTos = FluentIterable.from(emailToTempIt).filter(isNotEmail());
        // 如果有错误列表则抛出异常
        if (invalidTos.iterator().hasNext()) {
            throw new EduException(PARAMS_EMAIL_IS_ERROR, new Object[] {invalidTos.toString()});
        }

        // 返回切割结果
        return Splitter.on(SEMICOLON).omitEmptyStrings().trimResults().split(emailTo);
    }

    /**
     * 不是邮件地址判断
     *
     * @return 判读表达式
     */
    private Predicate<String> isNotEmail() {
        return new Predicate<String>() {
            @Override
            public boolean apply(@Nullable String input) {
                return !isEmailAddress(input);
            }
        };
    }

    /**
     * 是否为邮件地址
     *
     * @param emailTo   邮件地址
     * @return  true(是)/false(否)
     */
    private boolean isEmailAddress(final String emailTo) {
        return emailTo.matches(EMAIL_REGEX);
    }

    public static void main(String[] args) {
        String email = "xieyushi@ancun.com,jianzhiyuan@ancun.com";
        EmailService service = new EmailService(new DispatcherBus());
        String result = service.splitEmail(email).toString();
        System.out.println(result);
        String msg = "{0}邮件格式不正确";
        System.out.println(MessageFormat.format(msg, ""));
    }
}
