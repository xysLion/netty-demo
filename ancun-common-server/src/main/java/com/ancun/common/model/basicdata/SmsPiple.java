package com.ancun.common.model.basicdata;

import com.google.common.base.MoreObjects;

/**
 * 短信通道
 *
 * @Created on 2016-1-20
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SmsPiple {

    /** 主键ID */
    private int id;

    /** 接口地址 */
    private String url;

    /** 账号名 */
    private String account;

    /** 密码 */
    private String password;

    /** 额外参数 */
    private String params;

    /** 备注 */
    private String remark;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("短信通道[SmsPiple]")
                .add("主键[id]", id)
                .add("接口地址[url]", url)
                .add("账户名称[account]", account)
                .add("账号密码[password]", password)
                .add("额外参数[params]", params)
                .add("备注[remark]", remark)
                .toString();
    }
}
