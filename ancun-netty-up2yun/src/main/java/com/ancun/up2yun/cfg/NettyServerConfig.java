package com.ancun.up2yun.cfg;

import com.ancun.netty.httpserver.HttpServer;
import com.ancun.up2yun.handlers.HttpUploadServerHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import javax.net.ssl.SSLException;
import java.security.cert.CertificateException;

/**
 * 上传组件服务配置信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
public class NettyServerConfig {

//    @Value("${boss.thread.count}")
//    private int bossCount;

    /** netty io操作线程池大小 */
    @Value("${worker.thread.count}")
    private int workerCount;

    /** netty服务器监听端口 */
    @Value("${tcp.port}")
    private int tcpPort;

    @Value("${upload.file.size}")
    private long maxContentSize;

    @Value("${so.keepalive}")
    private boolean keepAlive;

    @Value("${so.backlog}")
    private int backlog;

    @Resource(name = "httpUploadServerHandler")
    private HttpUploadServerHandler handler;

    @Bean(destroyMethod = "shutdown")
    public HttpServer restExpress() throws CertificateException, SSLException {

        return HttpServer.builder()
                .setName("UP2YUN-SERVER")
                .setIoThreadCount(workerCount)
                .setMaxContentSize(maxContentSize)
                .setPort(tcpPort)
                .addRequestHandler(handler);
    }
}
