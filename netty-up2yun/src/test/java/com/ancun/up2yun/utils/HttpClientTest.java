package com.ancun.up2yun.utils;

import com.ancun.netty.httpclient.HttpClient;

import org.junit.Test;

import java.net.InetAddress;
import java.net.UnknownHostException;

import io.netty.util.CharsetUtil;

/**
 * 发送请求
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/18
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class HttpClientTest {

    @Test
    public void testPost(){

        String url = "http://192.168.0.200:7080";

        String content = "{\n" +
                "  \"request\": {\n" +
                "    \"common\": {\n" +
                "      \"action\": \"regionarea\",\n" +
                "      \"reqtime\": \"2016-01-08 09:52:03\",\n" +
                "      \"asyn\": \"false\"\n" +
                "    },\n" +
                "    \"content\": {\n" +
                "      \"phoneNo\": \"13646829663\"\n" +
                "    }\n" +
                "  }\n" +
                "}";

        System.out.println(HttpClient.post(url, content.getBytes(), CharsetUtil.UTF_8));

    }

    @Test
    public void testRoot(){
        System.out.println(System.getProperty("user.dir") + "\\temp");
    }

    @Test
    public void testIp() throws UnknownHostException {
//        System.out.println(NetUtil.LOCALHOST6);
//        System.out.println(InetAddresses.getCompatIPv4Address(NetUtil.LOCALHOST6));
        System.out.println(InetAddress.getLocalHost().getHostAddress());
    }

}