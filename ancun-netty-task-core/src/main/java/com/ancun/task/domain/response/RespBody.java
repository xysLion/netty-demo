package com.ancun.task.domain.response;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * 响应体Body类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@XmlRootElement(name="response")
public class RespBody<T> {
	private RespInfo info;
	private T content;
	public RespBody(){
		super();
		this.info = new RespInfo();
	};
	public RespBody(T content) {
		super();
		this.info = new RespInfo();
		this.content = content;
	}
	public RespBody(RespInfo info, T content) {
		super();
		this.info = info;
		this.content = content;
	}
	public RespInfo getInfo() {
		return info;
	}
	public void setInfo(RespInfo info) {
		this.info = info;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}
}



