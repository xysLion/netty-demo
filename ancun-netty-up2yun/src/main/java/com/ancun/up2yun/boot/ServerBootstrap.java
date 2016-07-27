package com.ancun.up2yun.boot;

import com.ancun.up2yun.cfg.Up2yunConfig;
import com.ancun.up2yun.utils.LogbackLoad;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * netty 任务启动类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ServerBootstrap {

    /**
     * 启动应用
     *
     * @param args
     */
    public static void main(String[] args) throws InterruptedException {

        // 加载logback.xml文件
        LogbackLoad.init();

        // 启动spring
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        // 注册配置类
        ctx.register(Up2yunConfig.class);
        ctx.refresh();

        ctx.registerShutdownHook();
    }

}
