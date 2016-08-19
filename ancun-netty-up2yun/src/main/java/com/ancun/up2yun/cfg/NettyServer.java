package com.ancun.up2yun.cfg;

import com.ancun.netty.httpserver.HttpServer;
import com.ancun.up2yun.cfg.NettyProperties;
import com.ancun.up2yun.handlers.HttpUploadServerHandler;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.security.cert.CertificateException;

import javax.annotation.Resource;
import javax.net.ssl.SSLException;

/**
 * 上传组件服务配置信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
@EnableConfigurationProperties({NettyProperties.class})
public class NettyServer {

    @Resource
    NettyProperties properties;

    @Resource(name = "httpUploadServerHandler")
    private HttpUploadServerHandler handler;

    @Bean(destroyMethod = "shutdown")
    public HttpServer restExpress() throws CertificateException, SSLException {

        return HttpServer.builder()
                .setName(properties.getName())
                .setIoThreadCount(properties.getWorkerThreads())
                .setMaxContentSize((int)properties.getMaxContentSize())
                .setPort(properties.getPort())
                .addRequestHandler(handler);
    }

}
