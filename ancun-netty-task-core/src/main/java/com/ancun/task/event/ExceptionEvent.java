package com.ancun.task.event;

/**
 * eventBus异常事件
 *
 * @Created on 2015-06-05
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ExceptionEvent {

    /** 异常实体 */
    private final Throwable exception;

    /**
     * 构造函数
     *
     * @param exception
     */
    public ExceptionEvent(Throwable exception)
    {
        this.exception = exception;
    }

    /**
     * 取得异常实体
     *
     * @return 取得异常
     */
    public Throwable getException(){
        return this.exception;
    }
}
