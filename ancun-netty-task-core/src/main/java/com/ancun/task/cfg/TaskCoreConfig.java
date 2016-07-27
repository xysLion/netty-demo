package com.ancun.task.cfg;

import com.ancun.task.listener.TaskListener;
import com.ancun.task.task.TaskBus;
import com.google.common.collect.Lists;
import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import javax.annotation.Resource;
import java.util.List;

/**
 * task-core共通配置类
 *
 * @Created on 2015-02-21
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
@ComponentScan("com.ancun.task")
@PropertySource({"file:${up2yun.home}/config/task.properties"})
@Import({DataSourceConfig.class})
public class TaskCoreConfig {

    private static final Logger logger = LoggerFactory.getLogger(TaskCoreConfig.class);

    /** 业务调度线程池大小 */
    @Value("${business.thread.count}")
    private int businessCount;

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
        return new TaskBus(businessCount);
    }

    /**
     * 注入任务监听器
     *
     * @return 任务监听器
     */
    @Bean
    public TaskListener taskListener() {
        return new TaskListener(eventBus(), taskBus());
    }

    /** 默认消息文件数组 */
    @Bean(name = "messageFiles")
    public List<String> messageFiles(){
        return Lists.newArrayList();
    }

    /** 消息文件列表 */
    @Resource(name = "messageFiles")
    private List<String> messageFiles;

    /**
     * 注入消息处理器
     *
     * @return 消息处理器
     */
    @Bean(name = "messageSource")
    public ReloadableResourceBundleMessageSource getMessageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageFiles.add("message/task_messages");

        messageSource.setBasenames(messageFiles.toArray(new String[messageFiles.size()]));
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        messageSource.setCacheSeconds(5);
        return messageSource;
    }

    /**
     * Necessary to make the Value annotations work.
     *
     * @return
     */
    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        return new PropertySourcesPlaceholderConfigurer();
    }
}
