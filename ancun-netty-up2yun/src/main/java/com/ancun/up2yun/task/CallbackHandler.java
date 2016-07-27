package com.ancun.up2yun.task;

import com.ancun.task.dao.TaskDao;
import com.ancun.task.domain.request.ReqBody;
import com.ancun.task.domain.request.ReqCommon;
import com.ancun.task.domain.response.RespJson;
import com.ancun.task.entity.Task;
import com.ancun.task.task.HandleTask;
import com.ancun.task.task.TaskBus;
import com.ancun.task.utils.*;
import com.ancun.up2yun.constant.BussinessConstant;
import com.ancun.up2yun.constant.ResponseConst;
import com.ancun.utils.DESUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * 将文件上传到云的结果通知客户端
 *
 * @Created on 2015-09-07
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class CallbackHandler {

    private static final Logger logger = LoggerFactory.getLogger(CallbackHandler.class);

    private final Gson gson = new Gson();

    /** 默认编码方式 */
    private static final String CHARSETNAME_DEFAULT = "UTF-8";

    /** http头部 */
    private Map<String, Object> header = new HashMap<String, Object>();

    /** 任务持久化类Dao */
    private final TaskDao taskDao;

    /** http请求组件 */
    private final RestClient restClient;

    /** 通知组件 */
    private final NoticeUtil noticeUtil;

    @Autowired
    public CallbackHandler(TaskBus taskBus, TaskDao taskDao, RestClient restClient, NoticeUtil noticeUtil) {
        taskBus.register(this);
        this.taskDao = taskDao;
        this.restClient = restClient;
        this.noticeUtil = noticeUtil;
    }

    /**
     * 将上传结果通知回调方
     *
     * @param task 任务内容
     */
    @HandleTask(taskHandler = BussinessConstant.CALLBACK, description = "将上传结果通知回调方")
    public void doCallback(Task task){

        boolean retryFlg = false;
        // 取得执行参数
        Map<String,Object> taskParams = task.getParamsMap();
        // 重试原因
        String reason = "";

        // 开始执行回调任务
        long beginTime = System.currentTimeMillis();

        // 执行post方法
        String uri = TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.CALLBACK_URI));
        // uri解密
        uri = DESUtils.decrypt(uri, null);

        try {

            // 发送请求
            String response = this.sendCallback(uri, taskParams, taskParams.get("accesskey"));
            logger.info(response);
            // 接收文件请求结束
            long endPostTime = System.currentTimeMillis();
            logger.info("文件 ：[{}] 发送回调请求总共花费时间：{}ms",
                    TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                    (endPostTime - beginTime));

            // 响应相应不成功
            Type respJsonType = new TypeToken<RespJson<String>>() {}.getType();
            RespJson<String> respJson = gson.fromJson(response, respJsonType);
//			int statusCode = response.getStatusLine().getStatusCode();
            int statusCode = respJson.getResponse().getInfo().getCode();
            if(statusCode != ResponseConst.SUCCESS){
                retryFlg = true;
                reason = SpringContextUtil.getMessage("server.callback.retry.reason_1",
                        new Object[]{ uri, getCallbackServerInfo(taskParams), response });
            } else {
                // 回调成功
                taskDao.success(task);
                logger.info(SpringContextUtil.getMessage("server.callback.success",
                        new Object[]{TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                                HostUtil.getIpv4Info().getLocalAddress(), uri}));
            }

        } catch (Exception e) {
            logger.info(SpringContextUtil.getMessage("server.callback.retry.reason_2",
                    new Object[]{ uri, getCallbackServerInfo(taskParams)}), e);
            e.printStackTrace();
            retryFlg = true;
            reason = SpringContextUtil.getMessage("server.callback.retry.reason_2",
                    new Object[]{ uri, getCallbackServerInfo(taskParams) }) + e.getMessage();
        }

        // 如果需要重试
        if (retryFlg) {

            // 失败重试
            int defaultRetryTimes = Integer.valueOf(SpringContextUtil.getProperty(BussinessConstant.RETRY_TIMES));
            int retryTimes = task.getRetryCount();
            // 已经重试了3次，还是失败并且需要发送邮件或者短信通知
            if (retryTimes > defaultRetryTimes) {

                // 失败
                taskDao.fail(task);

                String message = SpringContextUtil.getMessage("server.callback.failure",
                        new Object[]{TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                                HostUtil.getIpv4Info().getLocalAddress(),
                                uri,
                                getCallbackServerInfo(taskParams),
                                reason
                        });

                logger.info(message);

                // 发送通知
                noticeUtil.sendNotice(SpringContextUtil.getMessage("callback.exception"), message);

            } else {

                task.setRetryReason(reason);
                // 重试
                taskDao.retry(task);

                logger.info(SpringContextUtil.getMessage("server.callback.retry",
                        new Object[]{TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                                HostUtil.getIpv4Info().getLocalAddress(), uri, getCallbackServerInfo(taskParams) }));
            }
        }

        // 回调任务执行结束
        long endTime = System.currentTimeMillis();
        logger.info("文件 ：[{}] 回调队列中回调任务执行结束：{}",
                TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                endTime);
        logger.info("文件 ：[{}] 回调任务执行总共花费时间：{}ms",
                TaskUtil.getValue(taskParams, SpringContextUtil.getProperty(BussinessConstant.FILE_KEY)),
                ( endTime - beginTime ));

    }

    /**
     * 发送回调请求
     *
     * @param requestUri
     * @param content
     * @param accessKey
     * @return
     */
    private <T> String sendCallback(String requestUri, T content, Object accessKey) throws Exception {

        ReqBody<T> obj = new ReqBody<T>();
        obj.setContent(content);
        ReqCommon common  = new ReqCommon();
        common.setAction(requestUri.substring(requestUri.lastIndexOf("/") + 1));
        obj.setCommon(common);

        String requestJson = "";

        header.put("format", "json");
        requestJson = gson.toJson(obj);
        requestJson = "{\"request\":"+requestJson+"}";
        logger.info("回调服务器地址信息：" + requestUri);
        try{
            if (accessKey != null && !"".equals(accessKey.toString())) {
                String sign = HmacSha1Util.signToString(MD5Util.md5(requestJson).toLowerCase(),
                        accessKey.toString(), CHARSETNAME_DEFAULT);
                header.put("sign", URLEncoder.encode(sign, CHARSETNAME_DEFAULT));
            }
            header.put("reqlength", requestJson.length());
        }catch(Exception e){
            throw new RuntimeException(e);
        }

        // post发送json
        return restClient.post(requestUri, requestJson, header);
    }

    /**
     * 取得回调服务器信息
     *
     * @param taskParams 上传用参数列表
     * @return 回调服务器信息
     */
    private String getCallbackServerInfo(Map<String,Object> taskParams) {

        String returnMsg = " PROVINCECODE ： " + taskParams.get("provinceCode")
                + ", ACCESSID : " + taskParams.get("accessid")
                + ", ACCESSKEY : " + taskParams.get("accesskey");
        return returnMsg;
    }
}