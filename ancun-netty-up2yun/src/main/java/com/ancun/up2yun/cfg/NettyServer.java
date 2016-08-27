package com.ancun.up2yun.cfg;

import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;

import com.ancun.netty.httpserver.HttpServer;
import com.ancun.up2yun.handlers.HttpUploadServerHandler;
import com.ancun.up2yun.iplimit.IpFilter;
import com.ancun.up2yun.iplimit.IpLimitProperties;
import com.ancun.up2yun.iplimit.Ipv4FilterRule;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.cert.CertificateException;
import java.util.List;

import javax.annotation.Resource;
import javax.net.ssl.SSLException;

import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

/**
 * 上传组件服务配置信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
@EnableConfigurationProperties({NettyProperties.class, IpLimitProperties.class})
public class NettyServer {

    @Resource
    NettyProperties properties;

    @Resource
    IpLimitProperties ipLimit;

    @Resource(name = "httpUploadServerHandler")
    private HttpUploadServerHandler handler;

    @Bean
    @ConditionalOnProperty("ipLimit.enabled")
    public RuleBasedIpFilter basedIpFilter(){
        List<Ipv4FilterRule> rejects =
                FluentIterable.from(ipLimit.getRejects())
                        .transform(changeIpFilterRule(IpFilterRuleType.REJECT)).toList();
        List<Ipv4FilterRule> accepts =
                FluentIterable.from(ipLimit.getAccepts())
                        .transform(changeIpFilterRule(IpFilterRuleType.ACCEPT)).toList();

        Ipv4FilterRule[] rules = FluentIterable.from(Iterables.concat(rejects, accepts)).toArray(Ipv4FilterRule.class);

        return new IpFilter(rules);
    }

    @Bean(destroyMethod = "shutdown")
    public HttpServer restExpress() throws CertificateException, SSLException {

        HttpServer server = HttpServer.builder()
                .setName(properties.getName())
                .setIoThreadCount(properties.getWorkerThreads())
                .setMaxContentSize((int)properties.getMaxContentSize())
                .setPort(properties.getPort());

        if (ipLimit.isEnabled()) {
            server.addRequestHandler(basedIpFilter());
        }

        server.addRequestHandler(handler);

        return server;
    }

    /**
     * 将ip字符串转换ip过滤规则
     *
     * @param ruleType  规则
     * @return  ip过滤规则
     */
    private Function<String, Ipv4FilterRule> changeIpFilterRule(final IpFilterRuleType ruleType) {
        return new Function<String, Ipv4FilterRule>() {
            @Override
            public Ipv4FilterRule apply(String input) {
                return new Ipv4FilterRule(input, ruleType);
            }
        };
    }

}
