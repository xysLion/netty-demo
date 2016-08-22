package com.ancun.task.cfg;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import com.ancun.task.listener.TaskListener;
import com.ancun.task.utils.task.TaskBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * task-core共通配置类
 *
 * @Created on 2015-02-21
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
@EnableConfigurationProperties({TaskProperties.class})
public class TaskCoreConfig {

    private static final Logger logger = LoggerFactory.getLogger(TaskCoreConfig.class);

    /** 任务相关配置 */
    @Resource
    private TaskProperties properties;

    /**
     * 注入guava事件总线
     *
     * @return
     */
    @Bean
    public EventBus eventBus(){

        EventBus eventBus = new EventBus();

        // 将非法事件监听器注册进事件总线
        eventBus.register(new Object() {

            @Subscribe
            public void lister(DeadEvent event) {
                logger.error("[{}]接收到非法事件[{}]!", event.getSource().getClass(), event.getEvent());
            }

        });

        return eventBus;
    }

    /**
     * 注入任务总线类
     *
     * @return 任务总线实体
     */
    @Bean
    public TaskBus taskBus(){
        return new TaskBus(properties.getWorkerThreads());
    }

    /**
     * 注入任务监听器
     *
     * @return 任务监听器
     */
    @Bean
    public TaskListener taskListener() {
        return new TaskListener(eventBus(), taskBus(), properties.getDuration());
    }

    /**
     * rest请求客户端
     *
     * @return rest请求客户端
     */
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }

}
