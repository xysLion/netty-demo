package com.ancun.up2yun.strategy;

import com.ancun.task.strategy.Strategy;
import com.ancun.up2yun.constant.ScanTypeEnum;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 默认策略，只扫描本机
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "defaultStrategy")
public class DefaultStrategy implements Strategy {

    /** 机子唯一编码代号 */
    @Value("${process.num}")
	private int processNum;

    /** 是否只扫描本地 */
    @Value("${location.only}")
    private int localOnly;

	/**
	 * 取得条件SQL
	 *
	 * @return
	 */
	@Override
	public String getStrategy() {
		StringBuilder sb = new StringBuilder();
		sb.append(" and compute_num = ");
		sb.append(processNum);
		return localOnly == ScanTypeEnum.SCANLOCALONLY.getNum() ? sb.toString() : "";
	}
}
