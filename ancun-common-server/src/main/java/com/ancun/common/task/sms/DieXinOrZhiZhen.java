package com.ancun.common.task.sms;

import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.model.basicdata.SmsPiple;
import com.ancun.common.model.sms.SmsRecord;
import com.ancun.utils.sms.SmsUtil;
import com.ancun.utils.taskbus.HandleTask;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

/**
 * 碟信或者至臻通道发送短信
 *
 * @Created on 2016-1-18
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class DieXinOrZhiZhen extends BaseTask {

    /**
     * 使用碟信通道发送短信
     *
     * @param smsRecord 短信信息
     */
    @HandleTask(taskType = Constants.TASK_TYPE_SMS, taskIndex = Constants.SMS_PIPLE_DIEXIN)
    public void sendSmsUseDieXin(SmsRecord smsRecord){

        // 设置通道ID
        this.num = Constants.SMS_PIPLE_DIEXIN;

        this.desctiption = "碟信";

        // 发送短信
        sendSms(smsRecord);
    }

    /**
     * 使用至臻通道发送短信
     *
     * @param smsRecord 短信信息
     */
    @HandleTask(taskType = Constants.TASK_TYPE_SMS, taskIndex = Constants.SMS_PIPLE_ZHIZHEN)
    public void sendSmsUseZhiZhen(SmsRecord smsRecord){

        // 设置通道ID
        this.num = Constants.SMS_PIPLE_ZHIZHEN;

        this.desctiption = "至臻";

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
                "?UserName=",
                smsPiple.getAccount(),
                "&UserPass=",
                SmsUtil.encode(smsPiple.getPassword(), "UTF-8"),
                "&Subid=01"
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
        return SmsUtil.sendSms( url, mobile, content);
    }

    /**
     * 发送是否成功
     *
     * @param sendResult    发送结果
     * @return              判断结果
     */
    @Override
    protected boolean success(String sendResult){
        // 发送结果
        return "00".equals(sendResult) || "03".equals(sendResult);
    }

    /**
     * 发送结果消息code
     *
     * @param sendResult  结果
     * @return  回馈消息码
     */
    @Override
    protected int messageCode(String sendResult){
        return ResponseConst.SMS_DXZZ_RETURN_START_CODE + Integer.valueOf(sendResult);
    }

    /**
     * 短信编号
     *
     * @param sendResult    发送结果
     * @return              短信编号
     */
    @Override
    protected String msgId(String sendResult){
        return "";
    }
}
