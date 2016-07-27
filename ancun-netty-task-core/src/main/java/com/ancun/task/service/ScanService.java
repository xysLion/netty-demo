package com.ancun.task.service;

import com.ancun.task.constant.Constant;
import com.ancun.task.dao.TaskDao;
import com.ancun.task.entity.Task;
import com.ancun.task.entity.TaskStatusInfo;
import com.ancun.task.utils.SpringContextUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.util.List;

/**
 * 扫描未完成任务
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Service(value = "scanService")
public class ScanService {

	@Value("${statustask.perion.seconds}")
	private long statusPeriod;
	
    @Resource(name = "taskDao")
    private TaskDao taskDao;

    /**
     * 取得任务类型列表
     *
     * @return 任务类型列表
     */
    public List<Integer> scanTaskHandler(){
        return taskDao.selectTaskHandlers();
    }

    /**
     * 根据条件Sql取得任务列表
     *
     * @param conditionSql
     * @return
     */
    public List<Task> scanTask(String conditionSql, int taskHandler){

        StringBuilder sql = new StringBuilder();
        sql.append(" where task_status = 0 ");
        sql.append(" and task_handler = ");
        sql.append(taskHandler);
        sql.append(" ");
        if (StringUtils.hasText(conditionSql)) {
            sql.append(conditionSql);
        }
        sql.append(" order by gmt_create ");
        sql.append(" limit ");
        sql.append(SpringContextUtil.getProperty(Constant.TASK_MAX_NUM));

        return taskDao.selectTasks(sql.toString());
    }

    /**
     * 根据任务ID开始任务，即将任务的状态变为处理中
     *
     * @param taskId
     * @param gmtHandle
     * @return
     */
    public int startTask(String taskId, Timestamp gmtHandle){
        return taskDao.processing(taskId, gmtHandle);
    }
    
    /**
     * 将过长时间未处理的任务重置
     * 
     * @param firstScanFlg 是不是第一次扫描
     * @param conditionSql
     * @return
     */
    public int resetTask(boolean firstScanFlg, String conditionSql) {
    	return taskDao.resetTask(firstScanFlg, statusPeriod, conditionSql);
    }

    /**
     * 取得未完成任务数
     *
     * @return 未完成任务状态列表
     */
    public List<TaskStatusInfo> notCompleteTaskCount(){
        return taskDao.notCompleteTaskCount();
    }

    /**
     * 取得未开始任务数
     * 
     * @return 未开始任务状态列表
     */
    public List<TaskStatusInfo> notStartTaskCount(){
    	return taskDao.notStartTaskCount();
    }

    /**
     * 取得处理中任务数
     *
     * @return 处理中任务状态列表
     */
    public List<TaskStatusInfo> handlingTaskCount(){
        return taskDao.handlingTaskCount();
    }
}
