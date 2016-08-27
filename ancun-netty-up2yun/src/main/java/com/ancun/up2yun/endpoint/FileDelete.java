package com.ancun.up2yun.endpoint;

import com.ancun.up2yun.cfg.NettyProperties;
import com.ancun.up2yun.domain.common.HandleResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;

import static com.ancun.up2yun.utils.NettyResult.errorHandleResult;
import static io.netty.handler.codec.http.HttpResponseStatus.FORBIDDEN;
import static io.netty.handler.codec.http.HttpResponseStatus.NOT_FOUND;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;

/**
 * 文件删除端点
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/22
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
@Component
@EnableConfigurationProperties({NettyProperties.class})
public class FileDelete extends FileBase {

    @Autowired
    public FileDelete(NettyProperties properties) {
        super(properties.getTempDir());
    }

    /**
     * 根据请求信息删除文件
     *
     * @param ctx   netty通道
     * @param request   接收到信息
     */
    public HandleResult deleteFile(ChannelHandlerContext ctx, HttpRequest request) throws IOException {

        final String uri = request.getUri();
        final String path = sanitizeUri(uri);

        // 路径为空
        if (path == null) {
            return errorHandleResult(FORBIDDEN, "该端点必须要有文件名作为路径.");
        }

        // 文件不存在
        File file = new File(path);
        if (file.isHidden() || !file.exists()) {
            return errorHandleResult(NOT_FOUND, "文件不存在.");
        }

        // 不为文件
        if (!file.isFile()) {
            return errorHandleResult(FORBIDDEN, "只允许取得文件.");
        }

        // 删除文件
        file.delete();

        return new HandleResult<String>(OK, "删除成功.");

    }

}