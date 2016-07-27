package com.ancun.common.persistence;

import com.ancun.common.model.basicdata.BasicData;
import com.ancun.common.model.basicdata.SmsPiple;
import com.google.common.base.Strings;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 基础数据Dao
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Repository
public class BasicDataDao {

    /** 操作数据库用template */
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据模块取得基础数据
     *
     * @param module    模块名
     * @return          基础数据列表
     */
    public List<BasicData> getBasicData(String module){

        // 查询SQL语句
        String selectSQL = "SELECT * FROM SYSTEM_BASIC_DATA ";

        // 查询SQL参数
        Object[] params = null;

        // 如果有指定模块
        if (!Strings.isNullOrEmpty(module)) {
            selectSQL += " WHERE MODULE = ?";
            params = new Object[]{ module };
        }

        // 查询数据
        List<BasicData> basicDatas = jdbcTemplate.query(selectSQL, params, new RowMapper<BasicData>() {
            @Override
            public BasicData mapRow(ResultSet rs, int rowNum) throws SQLException {

                BasicData basicData = new BasicData();
                basicData.setId(rs.getLong("ID"));
                basicData.setMoudle(rs.getString("MODULE"));
                basicData.setConfKey(rs.getString("CONF_KEY"));
                basicData.setConfValue(rs.getString("CONF_VALUE"));
                basicData.setDescription(rs.getString("DESCRIPTION"));

                return basicData;
            }
        });

        return basicDatas;
    }

    /**
     * 根据ID取得短信通道信息
     *
     * @param id    通道ID
     * @return      短信通道信息
     */
    public SmsPiple getSmsPiple(int id){

        // 查询语句
        String selectSQL = "SELECT * FROM SMS_PIPLE WHERE ID = ?";

        // 查询SQL参数
        Object[] params = new Object[]{ id };

        // 取得通道列表
        SmsPiple smsPiple = jdbcTemplate.queryForObject(selectSQL, params, new RowMapper<SmsPiple>() {
            @Override
            public SmsPiple mapRow(ResultSet rs, int rowNum) throws SQLException {

                SmsPiple smsPiple = new SmsPiple();
                smsPiple.setId(rs.getInt("ID"));
                smsPiple.setUrl(rs.getString("URL"));
                smsPiple.setAccount(rs.getString("ACCOUNT"));
                smsPiple.setPassword(rs.getString("PASSWORD"));
                smsPiple.setParams(rs.getString("PARAMS"));
                smsPiple.setRemark(rs.getString("REMARK"));

                return smsPiple;
            }
        });

        return smsPiple;
    }

}
