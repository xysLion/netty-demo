package com.ancun.common.task.sms;

import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.model.basicdata.SmsPiple;
import com.ancun.common.model.sms.SmsRecord;
import com.ancun.utils.sms.SmsUtil;
import com.ancun.utils.taskbus.HandleTask;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import org.springframework.stereotype.Component;

/**
 * 创蓝,示远通道发送短信
 *
 * @Created on 2016-1-18
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class Chuanglan extends BaseTask {

    /** 逗号 */
    private static final String COMMA = ",";

    /** 回车符 */
    private static final String ENTER = "\n";

    /**
     * 使用创蓝通道发送短信
     *
     * @param smsRecord 短信信息
     */
    @HandleTask(taskType = Constants.TASK_TYPE_SMS, taskIndex = Constants.SMS_PIPLE_CHUANGLAN)
    public void sendSmsUseChuanglan(SmsRecord smsRecord){

        this.num = Constants.SMS_PIPLE_CHUANGLAN;
        this.desctiption = "创蓝";

        // 发送短信
        sendSms(smsRecord);

    }

    /**
     * 使用示远通道发送短信
     *
     * @param smsRecord 短信信息
     */
    @HandleTask(taskType = Constants.TASK_TYPE_SMS, taskIndex = Constants.SMS_PIPLE_SHIYUAN)
    public void sendSmsUseShiYuan(SmsRecord smsRecord){

        // 设置通道ID
        this.num = Constants.SMS_PIPLE_SHIYUAN;

        this.desctiption = "示远";

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
        return Joiner.on("").join(
                smsPiple.getUrl(),
                "?account=",
                smsPiple.getAccount(),
                "&pswd=",
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
        return SmsUtil.sendSmsForChuanglan( url, mobile, content);
    }

    /**
     * 发送是否成功
     *
     * @param sendResult    发送结果
     * @return              判断结果
     */
    @Override
    protected boolean success(String sendResult){
        // 取得创蓝短信通道响应状态吗
        String code = getCodeForChuanglan(sendResult);

        // 发送结果
        return "0".equals(code);
    }

    /**
     * 发送结果消息code
     *
     * @param sendResult  结果
     * @return  回馈消息码
     */
    @Override
    protected int messageCode(String sendResult){
        // 取得创蓝短信通道响应状态吗
        String code = getCodeForChuanglan(sendResult);

        return ResponseConst.SMS_CHUANGLAN_RETURN_START_CODE + Integer.valueOf(code);
    }

    /**
     * 短信编号
     *
     * @param sendResult    发送结果
     * @return              短信编号
     */
    @Override
    protected String msgId(String sendResult){
        return getMsgidForChuanglan(sendResult);
    }

    /**
     * 取得创蓝响应字段中的状态码
     *
     * @param responseStr   创蓝短信通道响应字段
     * @return              状态码
     */
    private String getCodeForChuanglan(String responseStr){

        // 先按逗号分隔
        Iterable<String> results = Splitter.on(COMMA).omitEmptyStrings().trimResults().split(responseStr);

        // 再按回车符"\n"分割
        Iterable<String> codes = Splitter.on(ENTER).omitEmptyStrings().trimResults().split(Iterables.getLast(results));

        // 取得第一个字段
        return Iterables.getFirst(codes, "");
    }

    /**
     * 取得创蓝响应字段中的状态码
     *
     * @param responseStr   创蓝短信通道响应字段
     * @return              状态码
     */
    private String getMsgidForChuanglan(String responseStr){
        // 按回车符"\n"分割
        Iterable<String> codes = Splitter.on(ENTER).omitEmptyStrings().trimResults().split(responseStr);

        // 取得最后一个字段
        return Iterables.getLast(codes);
    }
}
