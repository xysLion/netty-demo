package com.ancun.task.domain.request;

import javax.xml.bind.annotation.*;

/**
 * 请求体的Body
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@XmlRootElement(name="request")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ReqBody<T> {
	@XmlElement
	private ReqCommon common;
	@XmlAnyElement(lax=true)
	private T content;

	public ReqBody() {
	}

	public ReqBody(T content) {
		if(null == this.common){
			this.common = new ReqCommon();
		}
		this.content = content;
	}

	public ReqBody(ReqCommon common, T content) {
		this.common = common;
		this.content = content;
	}
	public ReqCommon getCommon() {
		return common;
	}
	public void setCommon(ReqCommon common) {
		this.common = common;
	}
	public T getContent() {
		return content;
	}
	public void setContent(T content) {
		this.content = content;
	}

}
