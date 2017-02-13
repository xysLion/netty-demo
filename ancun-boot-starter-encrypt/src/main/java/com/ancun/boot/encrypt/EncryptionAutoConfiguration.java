package com.ancun.boot.encrypt;

import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import javax.annotation.Resource;

/**
 * 注入加解密工具
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/24
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
@Configuration
@ConditionalOnClass({ TextEncryptor.class })
@EnableConfigurationProperties(KeyProperties.class)
public class EncryptionAutoConfiguration {

    @Resource
    private KeyProperties properties;

    @Bean
    @ConditionalOnProperty("encrypt.enabled")
    public TextEncryptor textEncryptor() {
        return EncryptorFactory.create(properties);
    }

}
