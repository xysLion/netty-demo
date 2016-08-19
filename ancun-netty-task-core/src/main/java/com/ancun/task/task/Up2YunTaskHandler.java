package com.ancun.task.task;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;

import com.ancun.task.constant.BussinessConstant;
import com.ancun.task.constant.Constant;
import com.ancun.task.constant.ProcessEnum;
import com.ancun.task.constant.StatusEnum;
import com.ancun.task.constant.TaskHandleTimeEnum;
import com.ancun.task.dao.TaskDao;
import com.ancun.task.entity.Task;
import com.ancun.task.event.Up2YunEvent;
import com.ancun.task.listener.Up2YunListener;
import com.ancun.task.utils.HostUtil;
import com.ancun.task.utils.NoticeUtil;
import com.ancun.task.utils.StringUtil;
import com.ancun.task.utils.TaskUtil;
import com.ancun.task.utils.task.HandleTask;
import com.ancun.task.utils.task.TaskBus;
import com.ancun.utils.DESUtils;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

/**
 * 将文件上传到云上任务包裹类
 *
 * @Created on 2015-09-07
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class Up2YunTaskHandler {

    private static final Logger logger = LoggerFactory.getLogger(Up2YunTaskHandler.class);

    /** 任务持久化类Dao */
    private final TaskDao taskDao;

    /** 事件总线 */
    private final EventBus eventBus;

    /** 上传到云监听器 */
    private final Up2YunListener up2YunListener;

    /** 通知组件 */
    private final NoticeUtil noticeUtil;

    /** 默认重试次数 */
    @Value("${retry.times:3}")
    private int defaultRetryTimes;

    /** 机子唯一编码代号 */
    @Value("${process.num}")
    private int processNum;

    /**
     * 创建实例，并将自己注册到任务总线{@link TaskBus}
     *
     * @param taskBus 任务总线
     */
    @Autowired
    public Up2YunTaskHandler(TaskBus taskBus, TaskDao taskDao, EventBus eventBus, Up2YunListener up2YunListener, NoticeUtil noticeUtil) {
        taskBus.register(this);
        this.taskDao = taskDao;
        this.eventBus = eventBus;
        this.up2YunListener = up2YunListener;
        this.noticeUtil = noticeUtil;
    }

    /**
     * 将文件上传到云
     *
     * @param task 任务信息实体
     */
    @HandleTask(taskHandler = BussinessConstant.UPTOYUN, description = "将文件上传到云" )
    public void upFileToYun(Task task){

        // 默认不重试
        boolean retryFlg = false;
        // 默认不发送回调请求
        boolean callbackFlg = false;
        // 回调信息
        String callbackResult = "";
        int callbackCode = 0;

        // 取得执行参数
        Map<String,Object> taskParams = task.getParamsMap();
        // 重试原因
        String reason = "";

        // 开始执行上传任务
        long beginTime = System.currentTimeMillis();

        try {

            // 发送上传到云事件
            eventBus.post(new Up2YunEvent(taskParams));

            // 接收文件请求结束
            long endPostTime = System.currentTimeMillis();
            logger.info("文件 ：[{}] 发送上传到云事件总共花费时间：{}ms",
                    TaskUtil.getValue(taskParams, BussinessConstant.FILE_KEY),
                    (endPostTime - beginTime));

            // 如果MD5值一致则上传成功
            if (up2YunListener.up2yunResult()) {

                // 上传成功信息
                callbackResult = StatusEnum.SUCCESS.getText();
                callbackCode = StatusEnum.SUCCESS.getNum();
                // 上传OSS成功时间
                SimpleDateFormat df = new SimpleDateFormat(BussinessConstant.DEFAULT_TIME_FORMAT);//设置日期格式
                taskParams.put("uploadTime", df.format(new Date()));

                // 上传成功发送回复信息
                String callbackUri = TaskUtil.getValue(taskParams, BussinessConstant.CALLBACK_URI);
                if (callbackUri != null && !"".equals(callbackUri)) {
                    callbackFlg = true;
                }

                // 上传成功，回调成功，删除记录
                taskDao.success(task);
                // 删除临时文件
                File file = new File(TaskUtil.getValue(taskParams, "file_path"));
                FileUtils.deleteQuietly(file);

                // 如果不需要回调，则直接提示成功
                logger.info("文件[{0}]在服务器节点[{1}]上{2}！",
                        new Object[]{TaskUtil.getValue(taskParams,
                                BussinessConstant.FILE_KEY),
                                HostUtil.getIpv4Info().getLocalAddress(), callbackResult});
            }
            // 否则重试
            else {
                retryFlg = true;
//				reason = SpringContextUtil.getMessage("file.upload.retry.reason_2");
                reason = up2YunListener.getExceptionMsg();
                taskParams.put(up2YunListener.RETRY_UP_YUN_TYPE_KEY, up2YunListener.getRetryUpYunType());
                task.setTaskParams(new Gson().toJson(taskParams));
            }

        } catch (Exception e) {
            reason = "上传到云对象存储时出现异常。" + getYunInfo(taskParams);
            logger.info(reason, e);
            retryFlg = true;
        }

        // 是否重试
        if (retryFlg) {

            // 失败重试
            int retryTimes = task.getRetryCount();
            // 上传成功发送回复信息
            String callbackUri = TaskUtil.getValue(taskParams, BussinessConstant.CALLBACK_URI);
            // 已经重试了3次，还是失败并且需要发送回调信息
            if(retryTimes > defaultRetryTimes && (callbackUri != null && !"".equals(callbackUri))){
                callbackFlg = true;
                callbackResult = StatusEnum.FAIL.getText();
                callbackCode = StatusEnum.FAIL.getNum();
                // 上传失败
                taskDao.fail(task);

                String message = String.format(Constant.FILE_UPLOAD_FAILURE,
                        new Object[]{TaskUtil.getValue(taskParams,
                                BussinessConstant.FILE_KEY),
                                HostUtil.getIpv4Info().getLocalAddress(),
                                callbackResult,
                                getYunInfo(taskParams),
                                reason
                        });
                noticeUtil.sendNotice(Constant.UPLOAD_EXCEPTION_NOTICE_TITLE, message);
                logger.info(message);
            } else {

                task.setRetryReason(reason);
                // 转变重试状态
                taskDao.retry(task);

                logger.info("文件[{}]在服务器节点[{}]上{}，上传到云的信息[{}]，已添加重试上传任务队列。",
                        new Object[]{TaskUtil.getValue(taskParams,
                                BussinessConstant.FILE_KEY),
                                HostUtil.getIpv4Info().getLocalAddress(),
                                callbackResult,
                                getYunInfo(taskParams)
                        });
            }
        }

        // 发送回调请求
        if (callbackFlg) {

            // 插入回调任务
            task.setTaskId(String.valueOf(UUID.randomUUID()));
            task.setReqUrl(HostUtil.getIpv4Info().getLocalAddress());
            // 接收方uri
            String uri = TaskUtil.getValue(taskParams, BussinessConstant.CALLBACK_URI);
            // uri解密
            uri = StringUtil.isBlank(uri)?uri: DESUtils.decrypt(uri, null);
            task.setRevUrl(uri);
            taskParams.put("callbackResult", callbackResult);
            taskParams.put("callbackCode", callbackCode);
            task.setTaskParams(new Gson().toJson(taskParams));
            task.setParamsMap(taskParams);
            task.setComputeNum(processNum);
            task.setTaskHandler(BussinessConstant.CALLBACK);
            task.setTaskStatus(ProcessEnum.PROCESSING.getNum());
            task.setGmtCreate(new Timestamp(System.currentTimeMillis()));
            task.setGmtHandle(new Timestamp(System.currentTimeMillis()));
            task.setHandleTimeEnum(TaskHandleTimeEnum.IMMEDIATELY);
            taskDao.addTask(task);

            logger.info("文件[{}]在服务器节点[{}]上添加进发送回调信息任务队列。",
                    new Object[]{TaskUtil.getValue(taskParams,
                            BussinessConstant.FILE_KEY),
                            HostUtil.getIpv4Info().getLocalAddress()});
        }

        // 接收文件请求结束
        long endTime = System.currentTimeMillis();
        logger.info("文件 ：[{}] 上传队列中上传任务执行结束：{}",
                TaskUtil.getValue(taskParams, BussinessConstant.FILE_KEY),
                endTime);
        logger.info("文件 ：[{}] 上传任务执行总共花费时间：{}ms",
                TaskUtil.getValue(taskParams, BussinessConstant.FILE_KEY),
                ( endTime - beginTime ));
    }

    /**
     * 取得云相关信息
     *
     * @param taskParams 上传用参数列表
     * @return
     */
    private String getYunInfo(Map<String,Object> taskParams){

        String returnMsg = "";

        // 遍历
        for (Map.Entry<String, Object> entry : taskParams.entrySet()) {
            // 记录bucket信息
            if (entry.getKey().contains("_bucket")) {
                returnMsg += ", BUCKET : " + entry.getValue().toString();
            }
            // 记录accessid
            else if (entry.getKey().contains("_accessid")) {
                returnMsg += ", ACCESSID : " + entry.getValue().toString();
            }
            // 记录accesskey
            else if (entry.getKey().contains("_accesskey")) {
                returnMsg += ", ACCESSKEY : " + entry.getValue().toString();
            }
        }

        returnMsg = returnMsg.substring(2, returnMsg.length());

        return returnMsg;
    }
}
