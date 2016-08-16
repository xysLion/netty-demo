package com.ancun.up2yun.server.monitor;

import com.ancun.task.constant.Constant;
import com.ancun.task.server.monitor.MonitorServerListener;
import com.ancun.task.utils.HostUtil;
import com.ancun.task.utils.NoticeUtil;
import com.google.common.util.concurrent.Service;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 上传组件应用监控服务监听器
 *
 * @Created on 2015-09-08
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Primary
@Component
public class UploadMonitorServerListener extends MonitorServerListener {

    /** 通知组件 */
    @Resource
    private NoticeUtil noticeUtil;

    /**
     * 服务终止时
     *
     * @param from 服务状态枚举
     */
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

    /**
     * 服务出现异常时
     *
     * @param from      服务状态枚举
     * @param failure   具体异常
     */
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

}
