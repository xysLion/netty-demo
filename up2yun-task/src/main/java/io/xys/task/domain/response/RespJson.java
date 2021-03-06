package com.ancun.task.domain.response;

/**
 * Json响应体类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class RespJson<T> {

    private RespBody<T> response;

    public RespJson(RespBody<T> response) {
        this.response = response;
    }

    public RespBody<T> getResponse() {
        return response;
    }

    public void setResponse(RespBody<T> response) {
        this.response = response;
    }
}
