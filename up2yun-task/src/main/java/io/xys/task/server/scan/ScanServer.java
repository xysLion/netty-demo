package com.ancun.task.server.scan;

import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.AbstractScheduledService;
import com.google.common.util.concurrent.MoreExecutors;

import com.ancun.task.cfg.TaskProperties;
import com.ancun.task.entity.Task;
import com.ancun.task.event.InQueneEvent;
import com.ancun.task.server.ServerManager;
import com.ancun.task.service.ScanService;
import com.ancun.task.strategy.Strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

/**
 * 扫描任务表，并将需要执行的任务添加到执行队列
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
@EnableConfigurationProperties({TaskProperties.class})
public class ScanServer {

    private static final Logger logger = LoggerFactory.getLogger(ScanServer.class);

    /** 任务相关配置 */
    @Resource
    private TaskProperties properties;

    /** 扫描service */
    @Resource
    private ScanService scanService;

    /** 策略类 */
    @Resource
    private Strategy strategy;

    /** 任务总线 */
    @Resource
    private EventBus eventBus;

    /**
     * 构建定时扫描服务实例
     * 1.为该服务添加监听
     * 2.将该服务添加到服务管理
     *
     * @param serverManager 服务管理
     * @param listener      定时扫描服务监听
     */
    @Autowired
    public ScanServer(ServerManager serverManager, ScanServerListener listener){

        // 创建服务实例
        ScanScheduleService service = new ScanScheduleService();

        // 添加监听
        service.addListener(listener, MoreExecutors.directExecutor());

        // 将服务注册到服务管理集中
        serverManager.register(service);
    }

    /**
     * 扫描定时任务
     */
    private class ScanScheduleService extends AbstractScheduledService {

        /**
         * 扫描表中数据并执行任务
         *
         * @throws Exception
         */
        @Override
        protected void runOneIteration() throws Exception {

            logger.debug("正在扫描需要执行任务...");

            // 取得任务类型列表
            List<Integer> taskHandlers = scanService.scanTaskHandler();

            // 取得每种任务类型任务
            for (int taskHandler : taskHandlers) {

                // 取得待执行任务
                List<Task> tasks = scanService.scanTask(strategy.getStrategy(), taskHandler);
                logger.debug("已扫描到{}需要执行任务,即将执行扫描到的任务", tasks.size());

                // 循环执行任务
                for (Task task : tasks) {

                    // 当前执行时间
                    task.setGmtHandle(new Timestamp(System.currentTimeMillis()));

                    // 如果不能开始任务则表示已在处理中
                    if (scanService.startTask(task.getTaskId(), task.getGmtHandle()) != 1){
                        continue;
                    }

                    // 发送添加任务事件
                    InQueneEvent inQueneEvent = new InQueneEvent(task);
                    eventBus.post(inQueneEvent);
                }
            }

            logger.debug("需要执行任务扫描结束。");
        }

        /**
         * 定时器设置
         *
         * @return 定时器
         */
        @Override
        protected Scheduler scheduler() {
            return Scheduler.newFixedRateSchedule(
                    properties.getDelayTime(),
                    properties.getScanTime(),
                    TimeUnit.MILLISECONDS
            );
        }
    }
}
