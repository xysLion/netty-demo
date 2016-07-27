package com.ancun.common.cfg;

import com.ancun.common.handlers.BusinessHandler;
import com.ancun.common.handlers.StringDecoder;
import com.ancun.common.handlers.StringEncoder;
import com.ancun.netty.httpserver.HttpServer;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@ConfigurationProperties(prefix = "netty")
public class NettyServerConfig {

    /** 是否使用https */
    private boolean ssl = false;

    /** 工作线程数 */
    private int workerCount;

    /** 监听端口 */
    private int port;

    /** 最大请求体长度 */
    private int maxContentSize;

    @Resource(name = "businessHandler")
    private BusinessHandler businessHandler;

    @Resource(name = "stringDecoder")
    private StringDecoder stringDecoder;

    @Resource(name = "stringEncoder")
    private StringEncoder stringEncoder;

    @Bean(destroyMethod = "shutdown")
    public HttpServer restExpress() throws CertificateException, SSLException {

        return HttpServer.builder()
                .setName("COMMON-SERVER")
                .setIoThreadCount(workerCount)
                .setPort(port)
                .setMaxContentSize(maxContentSize)
                .addRequestHandler(stringEncoder)
                .addRequestHandler(stringDecoder)
                .addRequestHandler(businessHandler)
                .setSSLContext(ssl);
    }

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getWorkerCount() {
        return workerCount;
    }

    public void setWorkerCount(int workerCount) {
        this.workerCount = workerCount;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getMaxContentSize() {
        return maxContentSize;
    }

    public void setMaxContentSize(int maxContentSize) {
        this.maxContentSize = maxContentSize;
    }
}
