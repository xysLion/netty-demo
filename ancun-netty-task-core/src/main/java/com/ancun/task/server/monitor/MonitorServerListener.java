package com.ancun.task.server.monitor;

import com.ancun.task.server.DefaultServerListener;
import org.springframework.stereotype.Component;

/**
 * 应用监控服务默认监听器
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class MonitorServerListener extends DefaultServerListener {

    /**
     * 初始化应用监控服务默认监听器
     */
    public MonitorServerListener() {
        this("应用监控");
    }

    /**
     * 初始化应用监控服务默认监听器
     *
     * @param serverName 服务名
     */
    public MonitorServerListener(String serverName) {
        super(serverName);
    }
}
