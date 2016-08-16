package com.ancun.up2yun.server.monitor;

import com.ancun.task.server.monitor.CustomApplicationInfo;
import com.ancun.task.utils.HostUtil;
import com.ancun.task.utils.SpringContextUtil;
import com.ancun.up2yun.constant.MsgConstant;
import org.apache.commons.io.FileUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.io.File;

/**
 * 上传组件自定义应用信息
 *
 * @Created on 2015-09-09
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Primary
@Component
public class UploadCustomApplicationInfo extends CustomApplicationInfo {

    /**
     * 提供上传组件服务器节点临时文件夹容量信息
     *
     * @return 上传组件服务器节点临时文件夹容量信息
     */
    @Override
    public String supplyCustomApplicationInfo() {

        // 临时文件夹路径
        String tempDir = SpringContextUtil.getProperty("tempdir");
        File file = new File(tempDir);
        // 已用的空间
        long usedSize = FileUtils.sizeOfDirectory(file);
        usedSize = usedSize / (1024 * 124);
        String message = String.format(MsgConstant.USED_DISK_INFO, HostUtil.getIpv4Info().getLocalAddress(), usedSize);

        return message;
    }
}
