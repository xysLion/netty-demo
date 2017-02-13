package com.ancun.up2yun.domain.task;

import com.google.common.base.MoreObjects;

import java.sql.Timestamp;
import java.util.Map;

/**
 * 任务持久化类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class Task {

    /** PK，唯一标识，默认是UUID */
    private String taskId;

    /** 请求方url */
    private String reqUrl;

    /** 接收方url */
    private String revUrl;

    /** 创建日期 */
    private Timestamp gmtCreate;

    /** 任务待执行日期 */
    private Timestamp gmtHandle;

    /** 任务类型 */
    private int taskHandler;

    /** 任务处理状态；0:未处理 1:处理中 */
    private int taskStatus;

    /** 执行任务的机器编码 */
    private int computeNum;

    /** 执行任务所需的参数 */
    private String taskParams;

    /** 重试次数 */
    private int retryCount;

    /** 重试原因，即上次失败原因，便于排错 */
    private String retryReason;

    /** 执行任务所需的参数（Map 类型） */
    private Map<String, Object> paramsMap;

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getReqUrl() {
        return reqUrl;
    }

    public String getRevUrl() {
        return revUrl;
    }

    public void setRevUrl(String revUrl) {
        this.revUrl = revUrl;
    }

    public void setReqUrl(String reqUrl) {
        this.reqUrl = reqUrl;
    }

    public Timestamp getGmtCreate() {
        return gmtCreate;
    }

    public void setGmtCreate(Timestamp gmtCreate) {
        this.gmtCreate = gmtCreate;
    }

    public Timestamp getGmtHandle() {
        return gmtHandle;
    }

    public void setGmtHandle(Timestamp gmtHandle) {
        this.gmtHandle = gmtHandle;
    }

    public int getTaskHandler() {
        return taskHandler;
    }

    public void setTaskHandler(int taskHandler) {
        this.taskHandler = taskHandler;
    }

    public int getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(int taskStatus) {
        this.taskStatus = taskStatus;
    }

    public int getComputeNum() {
        return computeNum;
    }

    public void setComputeNum(int computeNum) {
        this.computeNum = computeNum;
    }

    public String getTaskParams() {
        return taskParams;
    }

    public void setTaskParams(String taskParams) {
        this.taskParams = taskParams;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }

    public String getRetryReason() {
        return retryReason;
    }

    public void setRetryReason(String retryReason) {
        this.retryReason = retryReason;
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    public void setParamsMap(Map<String, Object> paramsMap) {
        this.paramsMap = paramsMap;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("taskId", taskId)
                .add("reqUrl", reqUrl)
                .add("revUrl", revUrl)
                .add("gmtCreate", gmtCreate)
                .add("gmtHandle", gmtHandle)
                .add("taskHandler", taskHandler)
                .add("taskStatus", taskStatus)
                .add("computeNum", computeNum)
                .add("taskParams", taskParams)
                .add("retryCount", retryCount)
                .add("retryReason", retryReason)
                .add("paramsMap", paramsMap)
                .toString();
    }
}
