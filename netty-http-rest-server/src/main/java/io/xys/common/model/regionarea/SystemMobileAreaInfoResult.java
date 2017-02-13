package com.ancun.common.model.regionarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 手机归属地数据库表实体,数据集映射
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemMobileAreaInfoResult implements RowMapper<SystemMobileAreaInfo> {
	
	@Override
    public SystemMobileAreaInfo mapRow(ResultSet resultSet, int i) throws SQLException {
    	SystemMobileAreaInfo info = new SystemMobileAreaInfo();
    	info.setSmaiMobile(resultSet.getInt("SMAIMOBILE"));
    	info.setSmaiAreaCode(resultSet.getString("SMAIAREACODE"));
        return info;
    }
}
