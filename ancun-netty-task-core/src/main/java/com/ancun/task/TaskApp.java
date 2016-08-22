package com.ancun.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 任务模块启动
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/19
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
@SpringBootApplication
public class TaskApp {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(TaskApp.class);
        app.setWebEnvironment(false);
        app.run(args);
    }

}
