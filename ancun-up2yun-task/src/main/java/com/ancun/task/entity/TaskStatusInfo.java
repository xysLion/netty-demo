package com.ancun.task.entity;

import com.google.common.base.MoreObjects;

/**
 * 任务状态信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class TaskStatusInfo {

    /** 节点编号 */
    private int computeNum;

    /** 任务数 */
    private long taskNum;

    public long getTaskNum() {
        return taskNum;
    }

    public void setTaskNum(long taskNum) {
        this.taskNum = taskNum;
    }

    public int getComputeNum() {
        return computeNum;
    }

    public void setComputeNum(int computeNum) {
        this.computeNum = computeNum;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("computeNum", computeNum)
                .add("taskNum", taskNum)
                .toString();
    }
}
