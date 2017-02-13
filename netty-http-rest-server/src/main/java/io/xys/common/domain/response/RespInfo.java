package com.ancun.common.domain.response;

import com.ancun.common.constant.ResponseConst;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(value = XmlAccessType.FIELD)
@XmlRootElement(name = "info")
public class RespInfo {
	private int code;
	private String msg;
//	private String logno;
//	private String serversion;
	
	public RespInfo() {
		super();
		this.code = ResponseConst.SUCCESS;
		this.msg = "成功";
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
	
}
