package com.ancun.task.constant;

/**
 * 处理状态枚举
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public enum ProcessEnum {
    /** 未处理 */
    NOTPROCESS(0, "未处理"),
    /** 处理中 */
    PROCESSING(1, "处理中");

    private int num;

    private String text;

    ProcessEnum(int num, String text) {
        this.num = num;
        this.text = text;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
