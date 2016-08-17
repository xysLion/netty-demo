package com.ancun.up2yun.server.netty;

import com.google.common.util.concurrent.AbstractExecutionThreadService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.common.util.concurrent.Service;

import com.ancun.netty.httpserver.HttpServer;
import com.ancun.task.constant.Constant;
import com.ancun.task.server.ServerManager;
import com.ancun.task.utils.HostUtil;
import com.ancun.task.utils.NoticeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 上传组件接收客户端请求netty服务
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class UpToYunServer {

    private static final Logger logger = LoggerFactory.getLogger(UpToYunServer.class);

    /** 服务名 */
    private final String serverName = "上传组件接收客户端请求netty";

    /** nettyServer */
    @Resource
    private HttpServer httpServer;

    /** 通知组件 */
    @Resource
    private NoticeUtil noticeUtil;

    /**
     * 构建上传组件接收客户端请求netty服务实例
     * 1.为该服务添加监听
     * 2.将该服务添加到服务管理
     *
     * @param serverManager 服务管理
     */
    @Autowired
    public UpToYunServer(ServerManager serverManager) {
        // 创建服务实例
        UpToYunService service = new UpToYunService();

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

                // 构建信息
                String message = String.format(Constant.SERVER_STOP_INFO,
                        HostUtil.getIpv4Info().getLocalAddress(),
                        serverName
                );

                // 通知管理员
                noticeUtil.sendNotice(Constant.SERVER_EXCEPTION, message);

                logger.info(message);
            }

            @Override
            public void failed(Service.State from, Throwable failure) {

                // 构建信息
                String message = String.format(Constant.SERVER_EXCEPTION_INFO,
                        HostUtil.getIpv4Info().getLocalAddress(),
                        serverName,
                        failure.getCause()
                );

                // 通知管理员
                noticeUtil.sendNotice(Constant.SERVER_EXCEPTION, message);

                logger.info(message);
            }
        }, MoreExecutors.directExecutor());

        // 将服务注册到服务管理集中
        serverManager.register(service);
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
