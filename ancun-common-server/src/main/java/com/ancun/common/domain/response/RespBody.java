package com.ancun.common.domain.response;

import javax.xml.bind.annotation.XmlRootElement;

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



