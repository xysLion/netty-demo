package com.ancun.task.utils;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * http post发送请求工具类。
 *
 * @Created on 2015-06-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class RestClient {

    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);

    /** spring rest请求模板 */
    private RestTemplate rest;

    /**
     * 构造函数
     */
    public RestClient() {

        this.rest = new RestTemplate(clientHttpRequestFactory());

        // utf-8编码方式
        Charset utf_8 = Charset.forName("UTF-8");
        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter(utf_8);
        List<HttpMessageConverter<?>> messageConverters = Lists.newArrayList();
        messageConverters.add(stringHttpMessageConverter);
        messageConverters.add(new GsonHttpMessageConverter());
        this.rest.setMessageConverters(messageConverters);
    }

    /**
     * 超时设置
     *
     * @return 客户端请求工厂类
     */
    private ClientHttpRequestFactory clientHttpRequestFactory() {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setReadTimeout(10000);
        factory.setConnectTimeout(10000);
        return factory;
    }

    /**
     * 发送rest请求
     *
     * @param uri         请求路径
     * @param content     请求内容体
     * @param headerMap   请求头部
     * @return 应答内容
     */
    public String post(String uri, String content, Map<String, Object> headerMap) {

        // 1. 封装请求header和body
        // 1.1 request header
        HttpHeaders requestHeaders = new HttpHeaders();
        Set<String> keys = headerMap.keySet();
        for(String key : keys){
            requestHeaders.add(key, headerMap.get(key).toString());
        }

        // 设置请求体
        HttpEntity<String> requestEntity = new HttpEntity<String>(content, requestHeaders);
        logger.info( "请求体头部：{}", requestEntity.getHeaders() );
        logger.info( "请求体内容：{}", requestEntity.getBody() );

        // 发送请求
        ResponseEntity<String> responseEntity = rest.exchange( uri, HttpMethod.POST, requestEntity, String.class );
        logger.info( "返回体内容：{}", responseEntity.getBody() );

        // 返回应答内容体
        return responseEntity.getBody();
    }
}
