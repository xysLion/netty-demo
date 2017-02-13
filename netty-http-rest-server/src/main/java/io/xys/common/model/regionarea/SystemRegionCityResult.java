package com.ancun.common.model.regionarea;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

/**
 * 省份，城市，区号的对应表,数据集映射
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemRegionCityResult implements RowMapper<SystemRegionCity> {

	@Override
	public SystemRegionCity mapRow(ResultSet resultSet, int i)
	        throws SQLException {
		SystemRegionCity city = new SystemRegionCity();
		city.setSrcPk(resultSet.getInt("SRCPK"));
		city.setSrcPCode(resultSet.getString("SRCPCODE"));
		city.setSrcCode(resultSet.getString("SRCCODE"));
		city.setSrcName(resultSet.getString("SRCNAME"));
		city.setSrcSName(resultSet.getString("SRCSNAME"));
		city.setSrcNickName(resultSet.getString("SRCNICKNAME"));
		city.setSrcAreaCode(resultSet.getString("SRCAREACODE"));
		city.setSrcStatus(resultSet.getString("SRCSTATUS"));
		city.setSrcOrder(resultSet.getInt("SRCORDER"));
		city.setSrcRecordNo(resultSet.getString("SRCRECORDNO"));
		city.setSrcOperateLogNo(resultSet.getString("SRCOPERATELOGNO"));
		return city;
	}

}
