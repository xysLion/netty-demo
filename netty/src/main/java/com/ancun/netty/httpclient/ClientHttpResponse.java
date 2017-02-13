package com.ancun.netty.httpclient;

import com.google.common.base.Preconditions;
import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.io.InputStream;

/**
 * 使用netty封装http客户端时的response
 *
 * @Created on 2016年3月21日
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ClientHttpResponse {

    private final ChannelHandlerContext context;

    private final FullHttpResponse nettyResponse;

    private final ByteBufInputStream body;

    private volatile HttpHeaders headers;


    public ClientHttpResponse(ChannelHandlerContext context, FullHttpResponse nettyResponse) {
        this.context = Preconditions.checkNotNull(context, "ChannelHandlerContext must not be null");
        this.nettyResponse = Preconditions.checkNotNull(nettyResponse, "FullHttpResponse must not be null");
        this.body = new ByteBufInputStream(this.nettyResponse.content());
        this.nettyResponse.retain();
    }

    /**
     * Return the HTTP status
     * @return the HTTP status
     */
    public HttpResponseStatus getStatus() {
        return this.nettyResponse.getStatus();
    }

    /**
     * Return the HTTP status code of the response as integer
     * @return the HTTP status as an integer
     */
    public int getRawStatusCode() {
        return this.nettyResponse.getStatus().code();
    }

    /**
     * Return the HTTP status text of the response.
     * @return the HTTP status text
     */
    public String getStatusText() {
        return this.nettyResponse.getStatus().reasonPhrase();
    }

    public HttpHeaders getHeaders() {
        return this.headers;
    }

    public InputStream getBody() {
        return this.body;
    }

    /**
     * Close this response, freeing any resources created.
     */
    public void close() {
        this.nettyResponse.release();
        this.context.close();
    }

}
