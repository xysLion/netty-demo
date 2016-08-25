package com.ancun.task.util;

import org.junit.Test;

/**
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/22
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class CommonTest {

    @Test
    public void testPattern() {

        String cipher = "{cipher}";
        String password = "{cipher}FKSAJDFGYOS8F7GLHAKERGFHLSAJ";

        if (password.startsWith(cipher)) {
            System.out.println(password.substring(cipher.length()));
        }

    }

}
