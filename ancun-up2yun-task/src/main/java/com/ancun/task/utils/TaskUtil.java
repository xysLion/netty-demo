package com.ancun.task.utils;

import com.ancun.task.entity.Task;

import java.util.List;
import java.util.Map;

/**
 * 集合工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class TaskUtil {

    /**
     * 根据任务Id删除list中相应元素
     *
     * @param tasks
     * @param taskId
     */
    public static void removeTask(List<Task> tasks, String taskId) {
        for (Task task : tasks) {
            if (task.getTaskId().equals(taskId)) {
                tasks.remove(task);
            }
        }
    }

    /**
     * 从map中取得值
     *
     * @param objectMap
     * @param key
     * @return
     */
    public static String getValue(Map<String, Object> objectMap, String key){
        String result = "";
        if (objectMap.get(key) != null) {
            result = objectMap.get(key).toString();
        }
        return result;
    }
}
