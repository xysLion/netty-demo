package com.ancun.common.model.basicdata;

import com.google.common.base.MoreObjects;

/**
 * 系统基础数据
 *
 * @Created on 2016-1-20
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class BasicData {

    /** 主键ID */
    private long id;

    /** 模块名 */
    private String moudle;

    /** 配置键 */
    private String confKey;

    /** 配置值 */
    private String confValue;

    /** 说明 */
    private String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMoudle() {
        return moudle;
    }

    public void setMoudle(String moudle) {
        this.moudle = moudle;
    }

    public String getConfKey() {
        return confKey;
    }

    public void setConfKey(String confKey) {
        this.confKey = confKey;
    }

    public String getConfValue() {
        return confValue;
    }

    public void setConfValue(String confValue) {
        this.confValue = confValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("系统基础数据[BasicData]")
                .add("主键[id]", id)
                .add("模块名[moudle]", moudle)
                .add("配置键[confKey]", confKey)
                .add("配置值[confValue]", confValue)
                .add("说明[description]", description)
                .toString();
    }
}
