package com.ancun.task.strategy;

import org.springframework.stereotype.Component;

/**
 * 默认策略
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "defaultStrategy")
public class DefaultStrategy implements Strategy {

	/**
	 * 取得条件SQL
	 *
	 * @return
	 */
	@Override
	public String getStrategy() {
		return "";
	}
}
