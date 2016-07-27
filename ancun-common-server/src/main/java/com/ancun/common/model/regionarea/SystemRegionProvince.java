package com.ancun.common.model.regionarea;

/**
 * 省份信息表
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemRegionProvince {

	/** 主键Key */
	private int srpPk;
	
	/** code */
	private String srpCode;
	
	/** 名称 */
	private String srpName;
	
	/** 简称 */
	private String srpSName;
	
	/** 昵称 */
	private String srpNickName;
	
	/** 状态 */
	private String srpStatus;
	
	/** 排序 */
	private int srpOrder;
	
	private String srpRecordNo;
	
	private String srpOperateLogNo;

	public int getSrpPk() {
		return srpPk;
	}

	public void setSrpPk(int srpPk) {
		this.srpPk = srpPk;
	}

	public String getSrpCode() {
		return srpCode;
	}

	public void setSrpCode(String srpCode) {
		this.srpCode = srpCode;
	}

	public String getSrpName() {
		return srpName;
	}

	public void setSrpName(String srpName) {
		this.srpName = srpName;
	}

	public String getSrpSName() {
		return srpSName;
	}

	public void setSrpSName(String srpSName) {
		this.srpSName = srpSName;
	}

	public String getSrpNickName() {
		return srpNickName;
	}

	public void setSrpNickName(String srpNickName) {
		this.srpNickName = srpNickName;
	}

	public String getSrpStatus() {
		return srpStatus;
	}

	public void setSrpStatus(String srpStatus) {
		this.srpStatus = srpStatus;
	}

	public int getSrpOrder() {
		return srpOrder;
	}

	public void setSrpOrder(int srpOrder) {
		this.srpOrder = srpOrder;
	}

	public String getSrpRecordNo() {
		return srpRecordNo;
	}

	public void setSrpRecordNo(String srpRecordNo) {
		this.srpRecordNo = srpRecordNo;
	}

	public String getSrpOperateLogNo() {
		return srpOperateLogNo;
	}

	public void setSrpOperateLogNo(String srpOperateLogNo) {
		this.srpOperateLogNo = srpOperateLogNo;
	}
	
}