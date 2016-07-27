package com.ancun.up2yun.cfg;

import com.ancun.task.cfg.TaskCoreConfig;
import com.ancun.task.utils.RestClient;
import com.google.common.collect.Lists;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.List;

/**
 * up2yun配置信息
 *
 * @Created on 2015-03-31
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
//@ComponentScan("com.ancun.up2yun")
@ComponentScan(basePackages = { "com.ancun.up2yun" },
excludeFilters = {@ComponentScan.Filter(type = FilterType.REGEX, pattern = "com.ancun.up2yun.utils.*" )})
@PropertySource({"file:${up2yun.home}/config/netty-server.properties",
        "file:${up2yun.home}/config/config.properties"
})
@Import({NettyServerConfig.class, TaskCoreConfig.class})
public class Up2yunConfig {

    @Resource
    private Environment env;

    /** 默认消息文件数组 */
    @Primary
    @Bean(name = "messageFiles")
    public List<String> messageFiles(){

        String messagePath = "file:" + env.getProperty("up2yun.home") + "/message/messages";
        String configPath = "file:" + env.getProperty("up2yun.home") + "/config/config";
        String taskTypePath = "file:" + env.getProperty("up2yun.home") + "/config/task-type";

        return Lists.newArrayList(messagePath, configPath, taskTypePath);
    }
}