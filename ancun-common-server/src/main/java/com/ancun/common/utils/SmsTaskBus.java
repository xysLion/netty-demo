package com.ancun.common.utils;

import com.ancun.utils.taskbus.IndexTask;
import com.ancun.utils.taskbus.TaskBus;
import com.google.common.base.Function;
import com.google.common.base.Throwables;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 短信发送总线
 *
 * @Created on 2016-1-18
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SmsTaskBus extends TaskBus {

    private static final Logger logger = LoggerFactory.getLogger(SmsTaskBus.class);

    /** 任务表操作锁 */
    private final ReadWriteLock taskTableByTypeLock = new ReentrantReadWriteLock();

    /** 发送失败使用其他通道发送开关 */
    private final boolean sendFailUseOtherChannel;

    /**
     * 默认构造函数
     * 默认开启发送失败使用其他通道发送
     */
    public SmsTaskBus() {
        this(true);
    }

    /**
     * 构造函数
     *
     * @param sendFailUseOtherChannel   发送失败使用其他通道发送开关
     */
    public SmsTaskBus(boolean sendFailUseOtherChannel) {
        this.sendFailUseOtherChannel = sendFailUseOtherChannel;
    }

    /**
     * 执行相应的任务
     *
     * @param type          任务类型
     * @param taskParams    任务参数
     */
    public Object startTask(String type, Object taskParams, int defaultIndex){

        taskTableByTypeLock.readLock().lock();
        try {
            List<IndexTask> wrappers = flattenHierarchy(type);

            // 任务序号不为0时才要排序
            if (defaultIndex > 0) {
                wrappers = FluentIterable.from(wrappers).toSortedList(sortedList(defaultIndex));
            }

            if (!wrappers.isEmpty()) {
                for (IndexTask wrapper : wrappers) {
                    enqueueTask(taskParams, wrapper);
                }
            }
        } finally {
            taskTableByTypeLock.readLock().unlock();
        }
        return dispatchQueuedTasks();
    }

    /**
     * 将指定index放到第一位
     *
     * @param specialIndex  指定index
     * @return              排序方法
     */
    private Ordering<IndexTask> sortedList(final int specialIndex){
        return Ordering.from(specialFirst(specialIndex)).onResultOf(new Function<IndexTask, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable IndexTask input) {
                return input.getIndex();
            }
        });
    }

    /**
     * 将指定index放到第一位
     *
     * @param specialIndex  指定index
     * @return              排序方法
     */
    private Ordering<Integer> specialFirst(final Integer specialIndex) {
        return new Ordering<Integer>() {
            @Override
            public int compare(@Nullable Integer left, @Nullable Integer right) {
                if (left == specialIndex) {
                    return -1;
                } else if (right == specialIndex){
                    return 1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Drain the queue of events to be dispatched. As the queue is being drained,
     * new events may be posted to the end of the queue.
     */
    protected Object dispatchQueuedTasks() {
        // don't dispatch if we're already dispatching, that would allow reentrancy
        // and out-of-order events. Instead, leave the events to be dispatched
        // after the in-progress dispatch is complete.
        if (isDispatching.get()) {
            return null;
        }

        isDispatching.set(true);
        Queue<TaskWithHandler> tasks = tasksToDispatch.get();
        try {
            Exception handleTaskEx = null;
            do {
                try {
                    return runTaskWithHandler(tasks);
                } catch (Exception ex) {
                    handleTaskEx = ex;
                }
            }while (!(handleTaskEx instanceof FinalTaskException) && sendFailUseOtherChannel);
            throw Throwables.propagate(handleTaskEx);
        } finally {
            isDispatching.remove();
            tasksToDispatch.remove();
        }
    }

    /**
     * 一次执行任务队列中的任务
     *
     * @param tasks 任务队列
     * @return      如果执行成功则返回值
     * @throws FinalTaskException   最后任务异常
     */
    private Object runTaskWithHandler(Queue<TaskWithHandler> tasks) throws FinalTaskException {
        TaskWithHandler taskWithHandler = tasks.poll();
        if (taskWithHandler != null) {
            return dispatch(taskWithHandler.taskParam, taskWithHandler.handler);
        } else {
            throw new FinalTaskException();
        }
    }

    /**
     * 最后一个任务异常
     */
    class FinalTaskException extends Exception {

    }

    public static void main(String[] args) {
        SmsTaskBus smsTaskBus = new SmsTaskBus();
        List<Integer> indexList = Lists.newArrayList(4, 6, 3, 1, 5, 2);
        List<Integer> sordList = FluentIterable.from(indexList).toSortedList(Ordering.natural().nullsFirst());
//        Collections.sort(sordList, );
        List<Integer> sordList1 = FluentIterable.from(sordList).toSortedList(smsTaskBus.specialFirst(6));
        System.out.println(sordList);
        System.out.println(sordList1);
    }
}
