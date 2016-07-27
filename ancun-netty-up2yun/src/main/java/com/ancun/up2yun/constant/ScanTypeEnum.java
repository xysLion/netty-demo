package com.ancun.up2yun.constant;

public enum ScanTypeEnum {

	/** 只扫描本机 */
    SCANLOCALONLY(0, "只扫描本机"),
    /** 扫描所有机子 */
    SCANALL(1, "扫描所有机子");

    private int num;

    private String text;

    ScanTypeEnum(int num, String text) {
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
