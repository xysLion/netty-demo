package com.ancun.task.utils.task;

import java.util.Map;

/**
 * 查找任务用于{@link TaskBus}.
 *
 * @Created on 2015-09-02
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public interface TaskFindingStrategy {

    /**
     * 将任务{@code task}添加到表
     *
     * @param source  拥有需要执行的任务方法类.
     * @return  依据{@link HandleTask}中的taskHandler属性
     *          为每一个被{@link HandleTask}标记过的方法组建{@link IndexTask}对象
     */
    Map<Integer, IndexTask> findAllTasks(Object source);

}
