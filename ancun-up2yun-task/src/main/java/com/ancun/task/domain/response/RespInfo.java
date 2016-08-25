package com.ancun.task.domain.response;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 响应体,共通响应信息类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "info")
public class RespInfo {
	private int code;
	private String msg;
	private String logno;
	private String serversion;

	public RespInfo() {
		super();
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getLogno() {
		return logno;
	}
	public void setLogno(String logno) {
		this.logno = logno;
	}
	public String getServersion() {
		return serversion;
	}
	public void setServersion(String serversion) {
		this.serversion = serversion;
	}


}
