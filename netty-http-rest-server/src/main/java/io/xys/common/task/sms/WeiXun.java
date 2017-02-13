package com.ancun.common.task.sms;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;

import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.model.basicdata.SmsPiple;
import com.ancun.common.model.sms.SmsRecord;
import com.ancun.netty.httpclient.HttpClient;
import com.ancun.utils.sms.SmsUtil;
import com.ancun.utils.taskbus.HandleTask;

import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;

import io.netty.util.CharsetUtil;

import static com.ancun.common.constant.Constants.COLON;
import static com.ancun.common.constant.Constants.SEMICOLON;

/**
 * 微讯通道发送短信
 *
 * @Created on 2016-3-15
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class WeiXun extends BaseTask {

    /**
     * 使用微讯通道发送短信
     *
     * @param smsRecord 短信信息
     */
    @HandleTask(taskType = Constants.TASK_TYPE_SMS, taskIndex = Constants.SMS_PIPLE_WEIXUN)
    public void sendSmsUseChuanglan(SmsRecord smsRecord){

        this.num = Constants.SMS_PIPLE_WEIXUN;
        this.desctiption = "微讯";

        // 发送短信
        sendSms(smsRecord);

    }

    /**
     * 创建短信连接url
     *
     * @param smsPiple  通道信息
     * @return 接口地址信息
     */
    @Override
    protected String createUrl(SmsPiple smsPiple) {
        return Joiner.on("").join(smsPiple.getUrl(),
                "?loginname=",
                smsPiple.getAccount(),
                "&password=",
                SmsUtil.encode(smsPiple.getPassword(), "UTF-8")
        );
    }

    /**
     * 发送短信
     *
     * @param url       短信地址
     * @param mobile    手机号
     * @param content   短信内容
     * @return          发送结果
     */
    @Override
    protected String send(String url, String mobile, String content) {

        // 取得字符编码方式
        Charset charset = Charset.forName(Constants.CHARSETNAME_DEFAULT);

        // 短信内容url加密
        String msg = null;
        try {
            msg = URLEncoder.encode(content.replaceAll("<br/>", " "), Constants.CHARSETNAME_DEFAULT);
        } catch (UnsupportedEncodingException e) {
            logger.info("加密文件时出错！", e);
        }

        url = Joiner.on("").join(
                url,
                "&needstatus=true",
                "&mobile=",
                mobile,
                "&content=",
                msg,
                "&extNo=01"
        );

        return HttpClient.post(url, new byte[0], CharsetUtil.UTF_8);
    }

    /**
     * 发送是否成功
     *
     * @param sendResult    发送结果
     * @return              判断结果
     */
    @Override
    protected boolean success(String sendResult) {

        String successStr = getValueFromResult(sendResult, "success");

        int successNum =  Strings.isNullOrEmpty(successStr) ? 0 : Integer.parseInt(successStr);

        return successNum > 0;
    }

    /**
     * 发送结果消息code
     *
     * @param sendResult  结果
     * @return  回馈消息码
     */
    @Override
    protected int messageCode(String sendResult) {

        Integer code = ResponseConst.SMS_STATUS.get(sendResult);

        if (code == null) {
            code = ResponseConst.SUCCESS;
        }

        return code;
    }

    /**
     * 短信编号
     *
     * @param sendResult    发送结果
     * @return              短信编号
     */
    @Override
    protected String msgId(String sendResult){
        return getValueFromResult(sendResult, "smsid");
    }

    /**
     * 从请求结果中取得相应key的值
     *
     * @param result    请求结果
     * @param key       指定key
     * @return          指定key的值
     */
    private String getValueFromResult(String result, String key){

        String ret = "0";

        // 包含分号和引号时
        if (result.contains(SEMICOLON) && result.contains(COLON)) {
            // 取得参数Map
            Map<String, String> responseStr = Splitter.on(SEMICOLON).withKeyValueSeparator(COLON).split(result);

            ret = responseStr.get(key);
        }

        return ret;
    }

    public static void main(String[] args) {
//        String url = "http://121.40.60.163:8081/message/sendMsg?loginname=279&password=YNTmyvQG3q";
//        String msg = "你的验证码为601162请勿告诉他人，15分钟内有效!";
//        String mobile = "13646829663";
//        WeiXun weiXun = new WeiXun();
//        System.out.println(weiXun.send(url, mobile, msg));
//        System.out.println(CharMatcher.JAVA_DIGIT.matchesAllOf("-123456"));
//        System.out.println(CharMatcher.DIGIT.matchesAllOf("12345A"));
        String result = "smsid:56930264;total:1;success:1;lose:0";
        Map<String, String> responseStr = Splitter.on(SEMICOLON).withKeyValueSeparator(COLON).split(result);
        System.out.println(responseStr);
    }
}
