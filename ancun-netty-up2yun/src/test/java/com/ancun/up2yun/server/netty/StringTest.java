package com.ancun.up2yun.server.netty;

import com.ancun.task.constant.Constant;

import org.junit.Test;

/**
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/17
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class StringTest {

    @Test
    public void testFormat(){
        System.out.println(String.format(Constant.SERVER_STOP_INFO, "【第一个】", "【第二个】"));
    }

}