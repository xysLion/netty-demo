package com.ancun.up2yun.domain.task;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.text.MessageFormat;

import javax.annotation.Resource;

/**
 * 任务数据库操作
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Repository(value = "taskDao")
public class TaskDao {

    /** 表名 */
    private static final String TABLE_NAME = "task";

    @Resource
    private JdbcTemplate jdbcTemplate;

    /** 日期只取年月 */
    private final String DATE_FOEMAT = "yyyyMM";

    /** 插入语句 */
    private final String INSERT_SQL = "INSERT INTO {0} VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";

    /**
     * 新增任务
     *
     * @param task
     * @return
     */
    public int addTask(Task task){

        // 插入数据
        Object[] params = new Object[]{
                task.getTaskId(),
                task.getReqUrl(),
                task.getRevUrl(),
                task.getGmtCreate(),
                task.getGmtHandle(),
                task.getTaskHandler(),
                task.getTaskStatus(),
                task.getComputeNum(),
                task.getTaskParams(),
                task.getRetryCount(),
                task.getRetryReason()
        };

        // 将数据插入到成功表中
        int count = jdbcTemplate.update(MessageFormat.format(INSERT_SQL, TABLE_NAME), params);

        return count;
    }

}
