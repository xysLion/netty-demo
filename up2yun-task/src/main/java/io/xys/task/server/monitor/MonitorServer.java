package com.ancun.task.server.monitor;

import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;

import com.ancun.task.cfg.TaskProperties;
import com.ancun.task.constant.MsgConstant;
import com.ancun.task.server.ServerManager;
import com.ancun.task.utils.NoticeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * 应用监控服务
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
@EnableConfigurationProperties({TaskProperties.class})
public class MonitorServer {

    private static final Logger logger = LoggerFactory.getLogger(MonitorServer.class);

    /** 任务相关配置 */
    @Resource
    private TaskProperties properties;

    /** 监控信息获取类 */
    @Resource
    private ApplicationInfo applicationInfo;

    /** 通知组件 */
    @Resource
    private NoticeUtil noticeUtil;

    /**
     * 构建应用监控服务实例
     * 1.为该服务添加监听
     * 2.将该服务添加到服务管理
     *
     * @param serverManager 服务管理
     */
    @Autowired
    public MonitorServer(ServerManager serverManager, MonitorServerListener listener) {
        // 创建服务实例
        MonitorService service = new MonitorService();

        // 添加监听
        service.addListener(listener, MoreExecutors.directExecutor());

        // 将服务注册到服务管理集中
        serverManager.register(service);
    }

    /**
     * 应用监控服务线程
     */
    private class MonitorService extends AbstractScheduledService{

        /**
         * 监控任务操作
         *
         * @throws Exception
         */
        @Override
        protected void runOneIteration() throws Exception {

            logger.debug("监控进程启动中...");

            // 获取监视信息
            String message = applicationInfo.monitor();
            // 通知管理员
            noticeUtil.sendNotice(MsgConstant.UPLOAD_MONITOR, message);

            logger.info(message);

            logger.debug("监控进程结束。");
        }

        /**
         * 定时器
         *
         * @return 定时器
         */
        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedRateSchedule(
                    properties.getDelayTime(),
                    properties.getMonitorTime(),
                    TimeUnit.MILLISECONDS
            );
        }
    }

}
