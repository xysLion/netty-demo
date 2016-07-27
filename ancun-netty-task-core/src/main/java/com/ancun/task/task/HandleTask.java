package com.ancun.task.task;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

/**
 * 标记一个任务方法.
 *
 * @Created on 2015-09-02
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({METHOD, ANNOTATION_TYPE})
public @interface HandleTask {

    /** 任务类型 */
    int taskHandler() default 0;

    /** 任务说明 */
    String description() default "";
}
