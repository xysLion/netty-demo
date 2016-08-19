package com.ancun.up2yun.server;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;

import com.ancun.netty.httpserver.HttpServer;
import com.ancun.up2yun.constant.MsgConstant;
import com.ancun.up2yun.utils.HostUtil;
import com.ancun.up2yun.utils.NoticeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * 上传组件接收客户端请求netty服务
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
@Order(Integer.MAX_VALUE)
public class UpToYunServer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(UpToYunServer.class);

    /** 服务名 */
    private final String serverName = "上传组件接收客户端请求netty";

    /** nettyServer */
    private final HttpServer httpServer;

    /** 上传到云服务 */
    private final UpToYunService service;

    /**
     * 构建上传组件接收客户端请求netty服务实例
     * 1.为该服务添加监听
     * 2.将该服务添加到服务管理
     */
    @Autowired
    public UpToYunServer(HttpServer httpServer, final NoticeUtil noticeUtil) {

        // netty SERVER
        this.httpServer = httpServer;

        // 创建服务实例
        service = new UpToYunService();

        // 添加监听
        service.addListener(new Service.Listener() {
            @Override
            public void starting() {
                logger.info("{}服务开始启动.....", serverName);
            }

            @Override
            public void running() {
                logger.info("{}服务开始运行", serverName);
            }

            @Override
            public void stopping(Service.State from) {
                logger.info("{}服务关闭中.....", serverName);
            }

            @Override
            public void terminated(Service.State from) {

//                // 构建信息
//                String message = String.format(MsgConstant.SERVER_STOP_INFO,
//                        HostUtil.getHostInfo().getAddress(),
//                        serverName
//                );
//
//                // 通知管理员
//                noticeUtil.sendNotice(MsgConstant.SERVER_EXCEPTION, message);
//
//                logger.info(message);
            }

            @Override
            public void failed(Service.State from, Throwable failure) {

                // 构建信息
                String message = String.format(MsgConstant.SERVER_EXCEPTION_INFO,
                        HostUtil.getHostInfo().getAddress(),
                        serverName,
                        failure.getCause()
                );

                // 通知管理员
                noticeUtil.sendNotice(MsgConstant.SERVER_EXCEPTION, message);

                logger.info(message);
            }
        }, MoreExecutors.directExecutor());
    }

    /**
     * Bean加载完全后启动
     *
     * @param strings        参数
     * @throws Exception    异常
     */
    @Override
    public void run(String... strings) throws Exception {
        this.service.startAsync().awaitTerminated();
    }

    /**
     * 上传组件接收客户端请求netty服务线程
     */
    private class UpToYunService extends AbstractExecutionThreadService {

        @Override
        protected void run() throws Exception {
            // 启动netty服务
            httpServer.bind();
        }
    }

}
