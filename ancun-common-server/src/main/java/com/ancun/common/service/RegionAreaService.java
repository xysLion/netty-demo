package com.ancun.common.service;

import com.google.common.base.Strings;

import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.exception.EduException;
import com.ancun.common.model.regionarea.RegionAreaInfo;
import com.ancun.common.model.regionarea.SystemMobileAreaInfo;
import com.ancun.common.model.regionarea.SystemRegionCity;
import com.ancun.common.model.regionarea.SystemRegionProvince;
import com.ancun.common.persistence.RegionAreaDao;
import com.ancun.common.utils.DispatcherBus;
import com.ancun.common.utils.MobileUtil;
import com.ancun.common.utils.ParamValid;
import com.ancun.utils.taskbus.HandleTask;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

/**
 * 号码归属地信息业务sevice
 *
 * @Created on 2015年3月10日
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Service(value = "regionareaService")
public class RegionAreaService {

    @Resource(name = "regionAreaDao")
    private RegionAreaDao regionAreaDao;

    @Resource
    private MobileUtil mobileUtil;

    // 异步操作线程池
    private final Executor executor = Executors.newFixedThreadPool(3);

    /**
     * 注册进分发总线
     *
     * @param bus   分发总线
     */
    @Autowired
    public RegionAreaService(DispatcherBus bus) {
        bus.register(this);
    }

    /**
     * 根据号码取得号码归属地信息
     *
     * @param phoneNo
     * @return
     */
    private RegionAreaInfo getRegionAreaInfo(String phoneNo) {

        // 手机号码归属地信息
        SystemMobileAreaInfo mobileAreaInfo = null;
        // 号码所属城市信息
        SystemRegionCity regionCity = null;
        // 号码所属省份信息
        SystemRegionProvince regionProvince = null;
        // 号码归属地信息(系统用)
        RegionAreaInfo regionAreaInfo = null;
        // 缺少归属地信息
        Integer lackInfo = null;

        // 取得区号
        String areaCode = mobileUtil.getAreaCodeFromPhone(phoneNo);

        // 如果不是固话，则从手机号码归属地信息表中取得
        if (Strings.isNullOrEmpty(areaCode)) {
            int param = Integer.parseInt(phoneNo.substring(0, 7));
            try {
                mobileAreaInfo = regionAreaDao.getMobileAreaInfo(param);
                areaCode = mobileAreaInfo.getSmaiAreaCode();
            } catch (Exception e) {
                lackInfo = Constants.LACK_INFO_ALL;
            }
        }

        // 如果查询不到归属地信息
        if (!Strings.isNullOrEmpty(areaCode)) {

            try {
                // 取得号码所属城市信息
                regionCity = regionAreaDao.getRegionCity(areaCode).get(0);
            } catch (Exception e) {
                lackInfo = Constants.LACK_INFO_ALL;
            }

            // 如果取不到城市信息
            if (regionCity == null) {
                lackInfo = Constants.LACK_INFO_ALL;
            } else {

                // 构造自定义的归属地信息
                regionAreaInfo = new RegionAreaInfo();
                regionAreaInfo.setPhoneNo(phoneNo);
                regionAreaInfo.setAreaCode(areaCode);
                regionAreaInfo.setCity(regionCity.getSrcName());
                regionAreaInfo.setCityCode(regionCity.getSrcCode());
                regionAreaInfo.setCorp(mobileUtil.getPhoneCrop(phoneNo));

                try {
                    // 取得号码所属省份信息
                    regionProvince = regionAreaDao.getRegionProvince(regionCity.getSrcPCode());
                } catch (Exception e) {
                    lackInfo = Constants.LACK_INFO_PROVINCE;
                }

                // 缺少省份信息
                if (regionProvince == null) {
                    lackInfo = Constants.LACK_INFO_PROVINCE;
                } else {
                    //  省份相关信息
                    regionAreaInfo.setProvince(regionProvince.getSrpName());
                    regionAreaInfo.setProvinceCode(regionProvince.getSrpCode());
                }
            }

        }

        // 记录归属地信息缺失号码
        this.recordPhone(phoneNo, lackInfo);

        // 如果归属地信息为空
        if (regionAreaInfo == null) {
            throw new EduException(ResponseConst.REGION_AREA_INFO_EMPTY);
        }

        return regionAreaInfo;
    }

    /**
     * 记录没有归属地信息的电话号码
     *
     * @param phoneNo   电话号码
     * @param lackInfo  归属地信息缺少部分
     */
    private void recordPhone(final String phoneNo, final Integer lackInfo) {

        executor.execute(new Runnable() {
            @Override
            public void run() {

                // 如果有缺少信息
                if (lackInfo != null
                        // 号码未被记录
                        && !regionAreaDao.existRecord(phoneNo)) {
                    // 记录缺少信息
                    regionAreaDao.recordPhone(phoneNo, lackInfo);
                }

            }
        });

    }

    /**
     * 根据号码取得号码归属地信息
     *
     * @param params
     * @return
     */
    @HandleTask(taskType = "regionarea")
    public RegionAreaInfo doWork(Map<String, String> params) {

        // 取得电话号码
        String phoneNo = params.get("phoneNo").toString().trim();
        ParamValid.checkParam(phoneNo, ResponseConst.PARAMS_PHONENO_NOT_EMPTY);

        // 判断是否为号码
        if (!mobileUtil.isPhone(phoneNo.toString())) {
            // 记录归属地信息缺失号码
            this.recordPhone(phoneNo.toString(), Constants.LACK_INFO_ALL);
            throw new EduException(ResponseConst.PARAMS_PHONENO_IS_ERROR,
                    new Object[]{phoneNo.toString()});
        }

        RegionAreaInfo areaInfo = this.getRegionAreaInfo(phoneNo.toString());
        return areaInfo;
    }
}
