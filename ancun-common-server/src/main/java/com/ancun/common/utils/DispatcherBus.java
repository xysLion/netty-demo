package com.ancun.common.utils;

import com.ancun.utils.taskbus.AnnotatedTaskFinder;
import com.ancun.utils.taskbus.IndexTask;
import com.ancun.utils.taskbus.TaskBus;
import com.ancun.utils.taskbus.TaskFindingStrategy;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.LinkedListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Ordering;
import com.google.common.util.concurrent.UncheckedExecutionException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 请求分发总线
 *
 * @Created on 2016-1-18
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class DispatcherBus {

    private static final Logger logger = LoggerFactory.getLogger(TaskBus.class);

    /**
     * A thread-safe cache for flattenHierarchy(). The Class class is immutable. This cache is shared
     * across all EventBus instances, which greatly improves performance if multiple such instances
     * are created and objects of the same class are posted on all of them.
     */
    private final LoadingCache<String, List<IndexTask>> flattenHierarchyCache =
            CacheBuilder.newBuilder()
                    .weakKeys()
                    .build(new CacheLoader<String, List<IndexTask>>() {
                        @SuppressWarnings({"unchecked", "rawtypes"}) // safe cast
                        @Override
                        public List<IndexTask> load(String taskType) {

                            List<IndexTask> taskList = taskTable.get(taskType);

                            taskList = FluentIterable.from(taskList).toSortedList(sortTask());

                            return taskList;
                        }
                    });

    /**
     * <p>This ListMultimap is NOT safe for concurrent use; all access should be
     * made after acquiring a read or write lock via {@link #taskTableByTypeLock}.
     */
    private final ListMultimap<String, IndexTask> taskTable = LinkedListMultimap.create();

    /** 任务表操作锁 */
    private final ReadWriteLock taskTableByTypeLock = new ReentrantReadWriteLock();

    /** 从注册的类里找出所有需要处理的任务方法的一种策略。 */
    private final TaskFindingStrategy finder = new AnnotatedTaskFinder();

    /** queues of task for the current thread to dispatch */
    private final ThreadLocal<Queue<TaskWithHandler>> tasksToDispatch =
            new ThreadLocal<Queue<TaskWithHandler>>() {
                @Override protected Queue<TaskWithHandler> initialValue() {
                    return new LinkedList<TaskWithHandler>();
                }
            };

    /** true if the current thread is currently dispatching an event */
    private final ThreadLocal<Boolean> isDispatching =
            new ThreadLocal<Boolean>() {
                @Override protected Boolean initialValue() {
                    return false;
                }
            };

    /** json操作类 */
    private static Gson gson = new Gson();

    /**
     * Registers all HandleTask methods on {@code object} to receive events.
     * HandleTask methods are selected and classified using this EventBus's
     * {@link TaskFindingStrategy}; the default strategy is the
     * {@link AnnotatedTaskFinder}.
     *
     * @param object  object whose HandleTask methods should be registered.
     */
    public void register(Object object) {
        ListMultimap<String, IndexTask> methodsInHandler = finder.findAllTasks(object);
        taskTableByTypeLock.writeLock().lock();
        try {

            taskTable.putAll(methodsInHandler);

        } finally {
            taskTableByTypeLock.writeLock().unlock();
        }
    }

    /**
     * 分发任务
     *
     * @param type          任务类型
     * @param taskParams    任务参数
     */
    public Object post(String type, JsonElement taskParams){

        taskTableByTypeLock.readLock().lock();
        try {
            List<IndexTask> wrappers = flattenHierarchy(type);

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
     * Queue the {@code taskParams} for dispatch during
     * {@link #dispatchQueuedTasks()}. Events are queued in-order of occurrence
     * so they can be dispatched in the same order.
     */
    void enqueueTask(JsonElement taskParams, IndexTask handler) {
        tasksToDispatch.get().offer(new TaskWithHandler(taskParams, handler));
    }

    /**
     * Drain the queue of events to be dispatched. As the queue is being drained,
     * new events may be posted to the end of the queue.
     */
    Object dispatchQueuedTasks() {
        // don't dispatch if we're already dispatching, that would allow reentrancy
        // and out-of-order events. Instead, leave the events to be dispatched
        // after the in-progress dispatch is complete.
        if (isDispatching.get()) {
            return null;
        }

        isDispatching.set(true);
        try {
            Queue<TaskWithHandler> tasks = tasksToDispatch.get();
            TaskWithHandler firstTask = tasks.peek();
            if (firstTask == null) {
                return null;
            }
            return dispatch(firstTask.taskParam, firstTask.handler);
        } finally {
            isDispatching.remove();
            tasksToDispatch.remove();
        }
    }

    /**
     * Dispatches {@code taskParam} to the subscriber in {@code wrapper}.  This method
     * is an appropriate override point for subclasses that wish to make
     * event delivery asynchronous.
     *
     * @param taskParam  task to dispatch.
     * @param wrapper  wrapper that will call the subscriber.
     */
    Object dispatch(Object taskParam, IndexTask wrapper) {
        try {
            return wrapper.handleTask(taskParam);
        } catch (Throwable throwable) {
            Throwable rootE = Throwables.getRootCause(throwable);
            logger.error("用{}作为参数执行{}类的{}方法时出现异常：{}",
                    taskParam.toString(),
                    wrapper.getHandler(),
                    wrapper.getMethod(),
                    rootE.getMessage());

            throw Throwables.propagate(rootE);
        }
    }

    /**
     * 按照任务序号排序
     *
     * @return 排序规则
     */
    Ordering<IndexTask> sortTask(){
        return Ordering.natural().nullsFirst().onResultOf(new Function<IndexTask, Integer>() {
            @Nullable
            @Override
            public Integer apply(@Nullable IndexTask input) {
                return input.getIndex();
            }
        });
    }

    /**
     * Flattens a task's type hierarchy into a List of IndexTask objects.  The List
     * will include all superclasses (transitively), and all interfaces
     * implemented by these superclasses.
     *
     * @param taskType  class whose type hierarchy will be retrieved.
     * @return {@code IndexTask}'s complete type hierarchy, flattened and uniqued.
     */
    List<IndexTask> flattenHierarchy(String taskType) {
        try {
            return flattenHierarchyCache.getUnchecked(taskType);
        } catch (UncheckedExecutionException e) {
            throw Throwables.propagate(e.getCause());
        }
    }

    /** simple struct representing an Param and it's Handler */
    static class TaskWithHandler {
        final Object taskParam;
        final IndexTask handler;
        public TaskWithHandler(JsonElement taskParam, IndexTask handler) {
            this.handler = Preconditions.checkNotNull(handler);
            JsonElement el = Preconditions.checkNotNull(taskParam);
            Class[] types = this.handler.getMethod().getParameterTypes();
            this.taskParam = gson.fromJson(el, types[0]);
        }
    }

}