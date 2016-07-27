package com.ancun.task.server.taskstatus;

import com.ancun.task.server.DefaultServerListener;
import org.springframework.stereotype.Component;

/**
 * 任务状态重置服务默认监听器
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class TaskStatusResetServerListener extends DefaultServerListener {

    /**
     * 任务状态重置服务监听器
     */
    public TaskStatusResetServerListener() {
        this("任务状态重置");
    }

    /**
     * 任务状态重置服务监听器
     *
     * @param serverName 服务名
     */
    public TaskStatusResetServerListener(String serverName) {
        super(serverName);
    }
}
