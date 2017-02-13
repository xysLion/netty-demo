package com.ancun.task.domain.request;

/**
 * Json请求体类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ReqJson<T> {

    /** 请求体 */
    private ReqBody<T> request;

    public ReqJson(ReqBody<T> request) {
        this.request = request;
    }

    public ReqBody<T> getRequest() {
        return request;
    }

    public void setRequest(ReqBody<T> request) {
        this.request = request;
    }
}
