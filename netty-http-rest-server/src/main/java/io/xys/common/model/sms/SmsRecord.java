package com.ancun.common.model.sms;

import com.google.common.base.MoreObjects;

import java.util.Date;

/**
 * 短信记录信息
 *
 * @Created on 2016年01月14日
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SmsRecord {

    /** 记录ID主键KEY */
    private String id;

    /** 请求URL */
    private String requestUrl;

    /** 手机号码 */
    private String mobile;

    /** 短信通道 */
    private String num;

    /** 短信内容 */
    private String message;

    /** 第三方平台返回短信ID */
    private String msgid;

    /** 短信状态报告时间 */
    private String reportTime;

    /**
     * 短信状态[SENDING(短消息发送中),DELIVRDING(短消息已到第三方短信发送平台),DELIVRD(短消息转发成功),
     * EXPIRED(短消息超过有效期),UNDELIV(短消息是不可达的),UNKNOWN(未知短消息状态),REJECTD(短消息被短信中心拒绝),
     * DTBLACK(目的号码是黑名单号码),ERR:104(系统忙),REJECT(审核驳回),OTHERS(网关内部状态)]
     */
    private String status;

    /** 创建时间 */
    private Date createTime;

    /** 备注 */
    private String remark;

    /** 接收状态报告验证的用户名（不是账户名），是按照用户要求配置的名称，可以为空 */
    private String receiver;

    /** 接收状态报告验证的密码，可以为空 */
    private String pswd;

    /** 新增加的主键ID列表 */
    private Iterable<String> insertIds;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRequestUrl() {
        return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMsgid() {
        return msgid;
    }

    public void setMsgid(String msgid) {
        this.msgid = msgid;
    }

    public String getReportTime() {
        return reportTime;
    }

    public void setReportTime(String reportTime) {
        this.reportTime = reportTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getPswd() {
        return pswd;
    }

    public void setPswd(String pswd) {
        this.pswd = pswd;
    }

    public Iterable<String> getInsertIds() {
        return insertIds;
    }

    public void setInsertIds(Iterable<String> insertIds) {
        this.insertIds = insertIds;
    }

    @Override
    public String toString() {

        return MoreObjects.toStringHelper("短信记录信息[SmsRecord]")
                .add("记录主键[ID]", id)
                .add("请求URL[requestUrl]", requestUrl)
                .add("手机号码[mobile]", mobile)
                .add("短信通道[num]", num)
                .add("短信内容[message]", message)
                .add("第三方平台返回短信ID[msgid]", msgid)
                .add("短信状态报告时间[reportTime]", reportTime)
                .add("短信状态[status]", status)
                .add("创建时间[createTime]", createTime)
                .add("备注[remark]", remark)
                .add("接收状态报告验证的用户名[receiver]", receiver)
                .add("接收状态报告验证的密码[pswd]", pswd)
                .toString();

    }
}
