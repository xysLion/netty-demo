package com.ancun.common.model.regionarea;

/**
 * 省份，城市，区号的对应表
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemRegionCity {

	/** 主键Key */
	private int srcPk;
	
	/** 所属省份code */
	private String srcPCode;
	
	/** code */
	private String srcCode;
	
	/** 名称 */
	private String srcName;
	
	/** 简称 */
	private String srcSName;
	
	/** 昵称 */
	private String srcNickName;
	
	/** 手机归属地code */
	private String srcAreaCode;
	
	/** 状态 */
	private String srcStatus;
	
	/** 排序 */
	private int srcOrder;
	
	private String srcRecordNo;
	
	private String srcOperateLogNo;

	public int getSrcPk() {
		return srcPk;
	}

	public void setSrcPk(int srcPk) {
		this.srcPk = srcPk;
	}

	public String getSrcPCode() {
		return srcPCode;
	}

	public void setSrcPCode(String srcPCode) {
		this.srcPCode = srcPCode;
	}

	public String getSrcCode() {
		return srcCode;
	}

	public void setSrcCode(String srcCode) {
		this.srcCode = srcCode;
	}

	public String getSrcName() {
		return srcName;
	}

	public void setSrcName(String srcName) {
		this.srcName = srcName;
	}

	public String getSrcSName() {
		return srcSName;
	}

	public void setSrcSName(String srcSName) {
		this.srcSName = srcSName;
	}

	public String getSrcNickName() {
		return srcNickName;
	}

	public void setSrcNickName(String srcNickName) {
		this.srcNickName = srcNickName;
	}

	public String getSrcAreaCode() {
		return srcAreaCode;
	}

	public void setSrcAreaCode(String srcAreaCode) {
		this.srcAreaCode = srcAreaCode;
	}

	public String getSrcStatus() {
		return srcStatus;
	}

	public void setSrcStatus(String srcStatus) {
		this.srcStatus = srcStatus;
	}

	public int getSrcOrder() {
		return srcOrder;
	}

	public void setSrcOrder(int srcOrder) {
		this.srcOrder = srcOrder;
	}

	public String getSrcRecordNo() {
		return srcRecordNo;
	}

	public void setSrcRecordNo(String srcRecordNo) {
		this.srcRecordNo = srcRecordNo;
	}

	public String getSrcOperateLogNo() {
		return srcOperateLogNo;
	}

	public void setSrcOperateLogNo(String srcOperateLogNo) {
		this.srcOperateLogNo = srcOperateLogNo;
	}
	
}