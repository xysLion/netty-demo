package com.ancun.task.server.scan;

import com.ancun.task.server.DefaultServerListener;
import org.springframework.stereotype.Component;

/**
 * 扫描服务默认监听器
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class ScanServerListener extends DefaultServerListener {

    /**
     * 初始化扫描服务默认监听器
     */
    public ScanServerListener() {
        this("定时扫描");
    }

    /**
     * 初始化扫描服务默认监听器
     *
     * @param serverName 服务名
     */
    public ScanServerListener(String serverName) {
        super(serverName);
    }
}
