package com.ancun.task.event;

import com.ancun.task.entity.Task;

/**
 * 任务事件
 *
 * @Created on 2015-06-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class TaskEvent {

    /** 异常实体 */
    private final Task task;

    /**
     * 构造函数
     *
     * @param task
     */
    public TaskEvent(Task task)
    {
        this.task = task;
    }

    /**
     * 取得任务实体
     *
     * @return 取得任务实体
     */
    public Task getTask(){
        return this.task;
    }

}
