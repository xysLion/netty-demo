package com.ancun.up2yun.domain.common;

import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * 处理结果
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/19
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class HandleResult<T> {

    /** 处理结果 */
    private HttpResponseStatus status;

    /** 处理结果信息 */
    private T content;

    public HandleResult() {
    }

    public HandleResult(HttpResponseStatus status, T message) {
        this.status = status;
        this.content = message;
    }

    public HttpResponseStatus getStatus() {
        return status;
    }

    public HandleResult setStatus(HttpResponseStatus status) {
        this.status = status;
        return this;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }
}
