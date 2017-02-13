package com.ancun.common.model.regionarea;

/**
 * 手机归属地数据库表实体
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemMobileAreaInfo {

	/** 手机号码 */
	private int smaiMobile;
	
	/** 归属地号码 */
	private String smaiAreaCode;

	public int getSmaiMobile() {
		return smaiMobile;
	}

	public void setSmaiMobile(int smaiMobile) {
		this.smaiMobile = smaiMobile;
	}

	public String getSmaiAreaCode() {
		return smaiAreaCode;
	}

	public void setSmaiAreaCode(String smaiAreaCode) {
		this.smaiAreaCode = smaiAreaCode;
	}
}
