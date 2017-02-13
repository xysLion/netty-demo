package com.ancun.task.event;

import com.ancun.task.entity.Task;

/**
 * 任务进入队列事件
 *
 * @Created on 2015-02-21
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class InQueneEvent {

    /** 任务实体 */
    private Task task;

    /**
     * 构造函数
     *
     * @param task 任务实体
     */
    public InQueneEvent(Task task){
        this.task = task;
    }

    /**
     * 取得任务实体
     *
     * @return 任务实体
     */
    public Task getTask() {
        return task;
    }
}
