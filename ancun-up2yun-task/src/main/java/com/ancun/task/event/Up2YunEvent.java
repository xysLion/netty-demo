package com.ancun.task.event;

import java.util.HashMap;
import java.util.Map;

/**
 * 上传到云对象存储器事件
 *
 * @Created on 2015-02-19
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class Up2YunEvent {

    /** 上传所需的参数 */
    private Map<String, Object> up2yunParams = new HashMap<String, Object>();

    /**
     * 构造事件
     *
     * @param message
     */
    public Up2YunEvent(Map<String, Object> message) {
        up2yunParams = message;
    }

    /**
     * 取得传递的参数
     *
     * @return
     */
    public Map<String, Object> getTaskParams() {
        return up2yunParams;
    }

}
