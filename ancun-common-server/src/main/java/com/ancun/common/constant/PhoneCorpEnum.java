package com.ancun.common.constant;

/**
 * 号码种类
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public enum PhoneCorpEnum {

    CHINAMOBILE(0,"中国移动"),
    CHINAUNICOM(1,"中国联通"),
    CHINATELECOM(2,"中国电信");

    private int num;

    private String text;

    PhoneCorpEnum(int num, String text) {
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
