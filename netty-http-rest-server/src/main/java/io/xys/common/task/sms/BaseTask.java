package com.ancun.common.task.sms;

import com.ancun.common.cfg.Config;
import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.Constants;
import com.ancun.common.persistence.BasicDataDao;
import com.ancun.common.persistence.SmsRecordDao;
import com.ancun.common.exception.EduException;
import com.ancun.common.model.basicdata.SmsPiple;
import com.ancun.common.model.sms.SmsRecord;
import com.ancun.common.service.SmsService;
import com.google.common.base.Strings;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import javax.annotation.Resource;
import java.text.MessageFormat;

/**
 * 短信发送任务共通操作
 *
 * @Created on 2016-1-18
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public abstract class BaseTask implements InitializingBean {

    protected final Logger logger;

    /** 备注格式 */
    protected static final String REMARK_FORMATTER = "使用【{0}】通道,结果【{1}】";

    /** 通道编号(默认为碟信通道) */
    protected volatile int num = Constants.SMS_PIPLE_DIEXIN;

    /** 通道描述 */
    protected volatile String desctiption = "碟信";

    /** 短信任务转发方 */
    @Resource
    protected SmsService smsService;

    /** 缓存 */
    @Resource
    protected BasicDataCache cache;

    /** 短信记录信息Dao */
    @Resource
    protected SmsRecordDao smsRecordDao;

    /** 基础数据DAO */
    @Resource
    protected BasicDataDao dataDao;

    /** 共通配置项 */
    @Resource
    protected Config config;

    /**
     * 初始化logger
     */
    public BaseTask() {
        logger = LoggerFactory.getLogger(this.getClass());
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        // 将自己注册到短信发送总线中
        smsService.taskBus.register(this);
    }

    /**
     * 创建短信发送到第三方平台基础记录
     *
     * @param num       通道
     * @param msgid     第三方短信返回ID
     * @param status    短信状态
     * @param remark    备注
     * @return          短信记录
     */
    protected SmsRecord createUpdateSms(String num, String msgid, String status, String remark){
        SmsRecord smsRecord = new SmsRecord();
        smsRecord.setNum(num);
        smsRecord.setMsgid(msgid);
        smsRecord.setStatus(status);
        smsRecord.setRemark(remark);
        return smsRecord;
    }

    /**
     * 发送短信
     *
     * @param smsRecord 短信信息实体
     */
    protected final void sendSms(SmsRecord smsRecord) {
        // 通道地址
        String url = getUrl();

        // 手机号码
        String mobile = smsRecord.getMobile();

        // 短信内容
        String msg = smsRecord.getMessage();

        // 发送
        String result = send(url, mobile, msg);
        logger.info("调用{}短信通道发送短信结果：{}", this.desctiption, result);

        // 判断是否发送成功
        boolean success = success(result);

        // 成功时要设置短信编号
        if (success) {
            // 设置短信编号
            smsRecord.setMsgid(msgId(result));
        }

        // 发送后操作
        sendSmsAfter(success, messageCode(result), smsRecord);
    }

    /**
     * 创建短信连接url
     *
     * @param smsPiple  通道信息
     * @return 接口地址信息
     */
    protected abstract String createUrl(SmsPiple smsPiple);

    /**
     * 发送短信
     *
     * @param url       短信地址
     * @param mobile    手机号
     * @param content   短信内容
     * @return          发送结果
     */
    protected abstract String send(String url, String mobile, String content);

    /**
     * 发送是否成功
     *
     * @param sendResult    发送结果
     * @return              判断结果
     */
    protected abstract boolean success(String sendResult);

    /**
     * 发送结果消息code
     *
     * @param sendResult  结果
     * @return  回馈消息码
     */
    protected abstract int messageCode(String sendResult);

    /**
     * 短信编号
     *
     * @param sendResult    发送结果
     * @return              短信编号
     */
    protected abstract String msgId(String sendResult);

    /**
     * 短信发送后操作
     *
     * @param sendResult    发送结果
     * @param messageCode   回馈消息码
     * @param smsRecord     短信发送实体
     */
    private void sendSmsAfter(boolean sendResult, int messageCode, SmsRecord smsRecord ) {

        // 更新用短信记录实体
        String status = "";
        String message = cache.message(messageCode);
        String remark = MessageFormat.format(REMARK_FORMATTER, desctiption, message);

        try {
            if(sendResult){
                // 转发到第三方短信平台
                status = Constants.SMS_DELIVRDING;
                logger.info( "{},phone:{},message{}", remark, smsRecord.getMobile(), smsRecord.getMessage());
            } else {
                // 转发失败
                status = Constants.SMS_OTHERS;
                logger.info("短信发送失败:【{}】,原因:{}", smsRecord.getMessage(), remark);
                throw new EduException(messageCode);
            }
        } finally {
            // 更新用记录实体
            SmsRecord updateSms = createUpdateSms(String.valueOf(this.num), smsRecord.getMsgid(), status, remark);
            // 更新短信记录状态
            smsRecordDao.batchUpdateSms(updateSms, smsRecord.getInsertIds());
        }

    }

    /**
     * 根据通道编号组建接口地址信息
     *
     * @return 接口地址信息
     */
    private String getUrl(){

        // 从缓存中取得接口信息地址
        String url = cache.smsConfig(this.num);

        // 如果取得失败则
        if (Strings.isNullOrEmpty(url)) {
            // 取得通道信息
            SmsPiple smsPiple = dataDao.getSmsPiple(num);

            // 取得密码
            String password = config.getRealPassword(smsPiple.getPassword());
            smsPiple.setPassword(password);

            // 创建连接url
            url = createUrl(smsPiple);

            // 将连接url添加到缓存中
            cache.addDataCache(BasicDataCache.CacheMoulde.SMSCONFIG, String.valueOf(this.num), url);
        }

        return url;
    }

    public static void main(String[] args) {
        Table<String, String, String> testTb = HashBasedTable.create();
        testTb.put("1", "2", "3");
        System.out.println(testTb.get("1", "3"));
    }
}
