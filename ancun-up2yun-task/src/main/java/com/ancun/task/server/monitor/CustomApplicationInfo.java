package com.ancun.task.server.monitor;

import org.springframework.stereotype.Component;

/**
 * 自定义应用信息
 *
 * @Created on 2015-09-09
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class CustomApplicationInfo {

    /**
     * 默认不提供自定义应用信息
     *
     * @return
     */
    public String supplyCustomApplicationInfo() {
        return null;
    }

}
