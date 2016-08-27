package com.ancun.up2yun.handlers;

import com.ancun.up2yun.constant.MsgConstant;
import com.ancun.up2yun.domain.common.HandleResult;
import com.ancun.up2yun.endpoint.FileDelete;
import com.ancun.up2yun.endpoint.FileReceive;
import com.ancun.up2yun.endpoint.FileSend;
import com.ancun.up2yun.utils.NettyResult;
import com.ancun.up2yun.utils.NoticeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 接收上传到yun对象存储的逻辑处理类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "httpUploadServerHandler")
@ChannelHandler.Sharable
public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger LOGGER = LoggerFactory.getLogger(HttpUploadServerHandler.class);

	/** 通知组件 */
	@Resource
	private NoticeUtil noticeUtil;

    /** 文件接收端点 */
    @Resource
    private FileReceive fileReceive;

    /** 文件发送端点 */
    @Resource
    private FileSend fileSend;

    /** 文件删除 */
    @Resource
    private FileDelete fileDelete;

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

    	if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            HandleResult result = null;

            // 如果是post请求
            if (request.getMethod() == HttpMethod.POST) {
                // 接收文件
                result = fileReceive.receiveFile(ctx, request);
            } else if (request.getMethod() == HttpMethod.GET) {
                result = fileSend.sendFile(ctx, request);
            } else if (request.getMethod() == HttpMethod.DELETE) {
                result = fileDelete.deleteFile(ctx, request);
            }

            if (result != null) {
                NettyResult.writeResponseAndClose(ctx, result);
            }

    	}
    }

    /**
     * 异常处理
     *
     * @param ctx       netty通道信息
     * @param cause     异常信息
     * @throws Exception    会有异常抛出
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        HttpResponseStatus status = HttpResponseStatus.INTERNAL_SERVER_ERROR;
		// 错误信息
		String message = String.format(MsgConstant.REQUEST_RECEIVE_EXCEPTION,
				ctx.channel().remoteAddress(), cause.getMessage());

		// 如果是文件过大的异常
		if (cause instanceof TooLongFrameException) {
			message += MsgConstant.FILE_RECEIVE_SIZE_OUTMAX;
            status = HttpResponseStatus.REQUEST_ENTITY_TOO_LARGE;
		}

		// 发送邮件通知管理员
		noticeUtil.sendNotice(MsgConstant.RECEIVE_EXCEPTION_NOTICE_TITLE, message);

		// 发送回馈信息给客户端
        NettyResult.writeResponseAndClose(ctx, NettyResult.errorHandleResult(status, message));

		LOGGER.error(message, cause);
    }

}
