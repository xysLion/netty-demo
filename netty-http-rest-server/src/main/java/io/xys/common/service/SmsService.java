package com.ancun.common.service;

import com.google.common.base.Function;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;

import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.model.sms.SmsRecord;
import com.ancun.common.persistence.SmsRecordDao;
import com.ancun.common.utils.DispatcherBus;
import com.ancun.common.utils.MobileUtil;
import com.ancun.common.utils.ParamValid;
import com.ancun.common.utils.SmsTaskBus;
import com.ancun.utils.taskbus.HandleTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.Resource;

/**
 * 短信通知业务sevice
 *
 * @Created on 2015年5月8日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Service
public class SmsService {

    private static final Logger logger = LoggerFactory.getLogger(SmsService.class);

    /** 短信记录信息Dao */
    @Resource
    private SmsRecordDao smsRecordDao;

    /** 缓存 */
    private final BasicDataCache cache;

    @Resource
    private MobileUtil mobileUtil;

    /** 短信发送任务总线 */
    public final SmsTaskBus taskBus;

    /**
     * 注册进分发总线
     *
     * @param bus   分发总线
     */
    @Autowired
    public SmsService(DispatcherBus bus, BasicDataCache cache) {
        bus.register(this);
        this.cache = cache;
        boolean sendFailUseOtherChannel = this.cache.toBoolenConfig(Constants.SEND_FAILD_USE_OTHER_PIPLE);
        taskBus = new SmsTaskBus(sendFailUseOtherChannel);
    }

    /**
     * 发送短信通知
     *
     * @param params 参数Map
     *     具体参数有：
     *     phoneNo:手机号码 支持群发,手机号码之间要用[,]隔开,
     *     message:短信内容
     *     num    :通道编码
     * @return 发送成功
     */
    @HandleTask(taskType = "sms")
    public Object doWork(SmsRecord params) throws Throwable {

        logger.info("发送短信开始。");

        // 去除电话号码前后的空格
        params.setMobile(params.getMobile().trim());
        // 取得要通知的电话号码
        Object phoneNumber = params.getMobile();
        ParamValid.checkParam(phoneNumber, ResponseConst.PARAMS_PHONENO_NOT_EMPTY);
        ParamValid.checkPhone(phoneNumber.toString(), mobileUtil);

        // 取得通知内容
        Object message = params.getMessage();
        ParamValid.checkParam(message, ResponseConst.PARAMS_MSG_NOT_EMPTY);

        // 如果通道编号为空
        if (Strings.isNullOrEmpty(params.getNum())) {
            // 使用默认通道
            params.setNum(cache.config(Constants.SMS_DEFAULT));
        }

        // 转换成数字类型
        int num = Integer.parseInt(params.getNum());

        // 将短信记录插入到数据库并取得ID列表
        Iterable<String> smsRecordIds = smsRecordDao.batchInsertSms(params);
        // 将新增记录ID列表放进参数
        params.setInsertIds(smsRecordIds);

        // 发送短信通知
        logger.info("调用发送短信发送工具(SmsUtil.sendSms)开始！");
        taskBus.startTask(Constants.TASK_TYPE_SMS, params, num);
        logger.info("调用发送短信发送工具(SmsUtil.sendSms)结束！");

        return null;
    }

    /**
     * 创蓝短信通道状态报告推送接收
     *
     * @param input 输入参数
     * @return      操作结果
     */
    @HandleTask(taskType = "smsStatusForChuanglan")
    public String receiveSmsStatus(SmsRecord input){
        int messageCode = ResponseConst.SMS_STATUS.get(input.getStatus());
        String remark = cache.message(messageCode);
        input.setRemark(remark);
        logger.info("接收到短信通道状态报告推送信息：{}", input);
        smsRecordDao.reportStatus(input);
        return null;
    }

    /**
     * 微讯短信通道状态报告推送接收
     *
     * @param input 输入参数
     * @return      操作结果
     */
    @HandleTask(taskType = "smsStatusForWeixun")
    public String smsStatusForWeixun(Map<String, String> input){

        logger.info("接收到短信通道状态报告推送信息：{}", input);

        // 取得状态内容列表字符串
        String statuses = input.get(Constants.REQ_CONTENT).substring(Constants.EVENT_FOR_SMS_WEIXUN.length());

        // 用分号分隔字符串
        Iterable<String> statusIte = Splitter.on(";").omitEmptyStrings().trimResults().split(statuses);

        // 构建用于批量更新的列表
        List<Object[]> statusList = FluentIterable.from(statusIte).transform(new Function<String, Object[]>() {
            @Nullable
            @Override
            public Object[] apply(@Nullable String input) {
                // 将状态信息【短信编号,手机号码,状态】->【状态，状态，手机号，短信编号】
                List<String> status = Splitter.on(",").omitEmptyStrings().trimResults().splitToList(input);
                return new Object[]{status.get(2), status.get(2), status.get(1), status.get(0)};
            }
        }).toList();

        // 批量更新
        smsRecordDao.reportStatusForWeixun(statusList);

        return null;
    }

    public static void main(String[] args) {
        String result = "20110725160412,0\n1234567890100";
        String result1 = "20110725160412,101";
//        int endIndex = result.indexOf("\n")==-1?result.length():result.indexOf("\n");
//        int endIndex1 = result1.indexOf("\n")==-1?result1.length():result1.indexOf("\n");
//        String code1 = result.substring(result.indexOf(",") + 1, endIndex);
//        System.out.println(code1);
//        String code = result1.substring(result1.indexOf(",") + 1, endIndex1);
//        System.out.println(code);
//        String num = "2";
//        System.out.println(Integer.getInteger(num, 0));
//        Iterable<String> strings = Splitter.on(COMMA).omitEmptyStrings().trimResults().split(result);
//        System.out.println(Iterables.getLast(s));
        List<String> indexList = Lists.newArrayList("4", "6", "3", "1", "5", "2");
        List<String> sordList = FluentIterable.from(indexList).toSortedList(Ordering.natural().nullsFirst());
        System.out.println(sordList);
    }
}
