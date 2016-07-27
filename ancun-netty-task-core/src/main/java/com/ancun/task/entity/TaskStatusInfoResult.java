package com.ancun.task.entity;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 任务状态信息数据映射
 *
 * @Created on 2015-09-09
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class TaskStatusInfoResult implements RowMapper<TaskStatusInfo> {
    @Override
    public TaskStatusInfo mapRow(ResultSet rs, int rowNum) throws SQLException {

        TaskStatusInfo statusInfo = new TaskStatusInfo();

        statusInfo.setComputeNum(rs.getInt("compute_num"));

        statusInfo.setTaskNum(rs.getLong("task_num"));

        return statusInfo;
    }
}
