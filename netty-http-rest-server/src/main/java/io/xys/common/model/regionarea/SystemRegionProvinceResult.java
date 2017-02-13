package com.ancun.common.model.regionarea;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 省份信息表,数据集映射
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class SystemRegionProvinceResult implements
        RowMapper<SystemRegionProvince> {

	@Override
	public SystemRegionProvince mapRow(ResultSet resultSet, int i)
	        throws SQLException {
		SystemRegionProvince province = new SystemRegionProvince();
		province.setSrpPk(resultSet.getInt("SRPPK"));
		province.setSrpCode(resultSet.getString("SRPCODE"));
		province.setSrpName(resultSet.getString("SRPNAME"));
		province.setSrpSName(resultSet.getString("SRPSNAME"));
		province.setSrpNickName(resultSet.getString("SRPNICKNAME"));
		province.setSrpStatus(resultSet.getString("SRPSTATUS"));
		province.setSrpOrder(resultSet.getInt("SRPORDER"));
		province.setSrpRecordNo(resultSet.getString("SRPRECORDNO"));
		province.setSrpOperateLogNo(resultSet.getString("SRPOPERATELOGNO"));
		return province;
	}

}
