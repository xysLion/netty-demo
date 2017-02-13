package com.ancun.common.cfg;

import com.ancun.common.utils.DispatcherBus;
import com.ancun.common.utils.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

/**
 * spring配置信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
public class SpringConfig {

    /** 请求分发总线 */
    @Bean
    public DispatcherBus bus(){
        return new DispatcherBus();
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

    @Bean
    @Lazy(false)
    public SpringContextUtil springContextUtil(){
        return new SpringContextUtil();
    }
}
