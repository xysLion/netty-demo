package com.ancun.task.server.monitor;

import com.ancun.task.constant.MsgConstant;
import com.ancun.task.entity.TaskStatusInfo;
import com.ancun.task.server.ServerManager;
import com.ancun.task.service.ScanService;
import com.ancun.task.utils.task.TaskBus;
import com.google.common.base.Joiner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用信息
 *
 * @Created on 2015-09-07
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class ApplicationInfo {

    /** 换行符 */
    private static final String JOINER_STRING = "\n";

    /** 扫描service */
    @Resource
    private ScanService scanService;

    /** 任务总线 */
    @Resource
    private TaskBus taskBus;

    /** 服务管理 */
    @Resource
    private ServerManager manager;

    /** 自定义系统信息 */
    @Resource
    private CustomApplicationInfo customApplicationInfo;

    /**
     * 获取监视信息
     *
     * @return 监视信息
     */
    public String monitor() {

        // 自定义任务信息
        String customInfo = customApplicationInfo.supplyCustomApplicationInfo();

        // 未完成任务信息
        List<TaskStatusInfo> noCompletes = scanService.notCompleteTaskCount();

//        String noCompletesMessage = SpringContextUtil.getMessage(MsgConstant.NOCOMPLETE_TASK_COUNT,
//                new Object[]{ noCompletes.toString() });
        String noCompletesMessage = String.format(MsgConstant.NOCOMPLETE_TASK_COUNT, noCompletes.toString());

        // 未开始任务信息
        List<TaskStatusInfo> noStarts = scanService.notStartTaskCount();
//        String noStartsMessage = SpringContextUtil.getMessage(MsgConstant.NOSTART_TASK_COUNT,
//                new Object[]{noStarts.toString()});
        String noStartsMessage = String.format(MsgConstant.NOSTART_TASK_COUNT, noStarts.toString());

        // 执行中任务信息
        List<TaskStatusInfo> handlings = scanService.handlingTaskCount();
//        String handlingsMessage = SpringContextUtil.getMessage(MsgConstant.NOSTART_TASK_COUNT,
//                new Object[]{handlings.toString()});
        String handlingsMessage = String.format(MsgConstant.HANDLING_TASK_COUNT, handlings);

        return Joiner.on(JOINER_STRING)
                .skipNulls()
                .join(customInfo,
                noCompletesMessage,
                noStartsMessage,
                handlingsMessage,
                manager.toString(),
                taskBus.toString()
        );
    }

}
