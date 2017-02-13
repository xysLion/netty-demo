package com.ancun.common.domain.request;

public class ReqBody<T> {
	private ReqCommon common;
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
