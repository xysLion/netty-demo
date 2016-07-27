package com.ancun.common.persistence;

import com.ancun.common.model.regionarea.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.List;

/**
 * 号码归属地信息Dao
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Repository(value = "regionAreaDao")
public class RegionAreaDao {

    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 根据号码取得手机归属地信息
     *
     * @param phoneNo
     * @return
     */
    public SystemMobileAreaInfo getMobileAreaInfo(int phoneNo) {
        String sql = "select * from SYSTEM_MOBILEAREAINFO where SMAIMOBILE = ?";

        Object[] params = new Object[]{
          phoneNo
        };

        return jdbcTemplate.queryForObject(sql, params, new SystemMobileAreaInfoResult());
    }

    /**
     * 根据号码归属地Code取得号码所属城市信息
     *
     * @param areaCode
     * @return
     */
    public List<SystemRegionCity> getRegionCity(String areaCode) {
        String sql = "select * from SYSTEM_REGIONCITY where SRCAREACODE = ?";

        Object[] params = new Object[]{
                areaCode
        };

        return jdbcTemplate.query(sql, params, new SystemRegionCityResult());
    }

    /**
     * 根据省份Code取得省份信息
     *
     * @param srpCode
     * @return
     */
    public SystemRegionProvince getRegionProvince(String srpCode){
        String sql = "select * from SYSTEM_REGIONPROVINCE where SRPCODE = ?";

        Object[] params = new Object[]{
                srpCode
        };

        return jdbcTemplate.queryForObject(sql, params, new SystemRegionProvinceResult());
    }

    /**
     * 判断该号码是否已被记录
     *
     * @param phone 手机号码
     * @return  true：已记录/false : 未被记录
     */
    public boolean existRecord(String phone) {
        // 是否存在查询
        String countSQL = "SELECT COUNT(*) FROM PHONEINFO_LACK_RECORD WHERE PHONE = ?";

        // 是否查询到记录
        return jdbcTemplate.queryForObject(countSQL, Integer.class, phone) > 0;
    }

    /**
     * 记录未查询到归属地信息的号码
     *
     * @param phone     号码
     * @param lackInfo  归属地信息
     * @return          成功条数
     */
    public int recordPhone(String phone, int lackInfo) {

        // 插入SQL
        String insertSQL = "INSERT INTO PHONEINFO_LACK_RECORD(PHONE, LACK_INFO) VALUES(?, ?)";

        // 执行参数
        Object[] params = new Object[]{
                phone,
                lackInfo
        };

        // 执行插入语句
        return jdbcTemplate.update(insertSQL, params);
    }
}
