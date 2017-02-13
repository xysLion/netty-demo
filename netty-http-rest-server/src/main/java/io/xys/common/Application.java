package com.ancun.common;

import com.ancun.common.listener.AESEncript;
import com.ancun.netty.httpserver.HttpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * 数据订阅服务启动入口
 *
 * @Created on 2015年12月07日
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@SpringBootApplication
@EnableConfigurationProperties
public class Application {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Application.class);
		app.addListeners(new AESEncript());
		app.run(args);
	}

	@Bean
	public CommandLineRunner demo(final HttpServer httpServer) {

		return new CommandLineRunner() {
			@Override
			public void run(String... strings) throws Exception {
				httpServer.bind();
			}
		};

	}

}
