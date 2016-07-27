package com.ancun.common.component;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import com.ancun.common.cfg.Config;
import com.ancun.common.model.basicdata.BasicData;
import com.ancun.common.persistence.BasicDataDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 系统基础数据
 *
 * @Created on 2016-1-20
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class BasicDataCache {

    /** 日志输出 */
    private static final Logger logger = LoggerFactory.getLogger(BasicDataCache.class);

    /** 模块枚举 */
    public enum CacheMoulde {
        CONFIG, SMSCONFIG, MESSAGE;
    }

    /** 基础数据表 */
    private final Table<CacheMoulde, String, String> basicData = HashBasedTable.create();

    /** 共通配置项 */
    private final Config config;

    /**
     * 初始化基础数据缓存表
     *
     * @param dao   基础数据DAO
     */
    @Autowired
    public BasicDataCache(BasicDataDao dao, Config config) {

        this.config = config;

        // 取得所有基础数据
        List<BasicData> basicDatas = dao.getBasicData("");

        // 将基础数据加到缓存中
        for (BasicData basicData : basicDatas) {
            this.basicData.put(CacheMoulde.valueOf(basicData.getMoudle().toUpperCase()),
                    basicData.getConfKey().toUpperCase(), basicData.getConfValue());
        }

    }

    /**
     * 添加一个缓存
     *
     * @param moulde    模块
     * @param confKey   配置键
     * @param confValue 配置值
     */
    public void addDataCache(CacheMoulde moulde, String confKey, String confValue){
        this.basicData.put(moulde, confKey, confValue);
    }

    /**
     * 取得config模块的指定键的配置值
     *
     * @param confKey   配置键
     * @return          配置值
     */
    public String config(String confKey){
        return getBasicData(CacheMoulde.CONFIG, confKey);
    }

    /**
     * 取得config模块的指定键的配置值
     *
     * @param confKey   配置键
     * @return          配置值
     */
    public boolean toBoolenConfig(String confKey){
        return Boolean.parseBoolean(config(confKey));
    }

    /**
     * 取得指定短信通道的配置值
     *
     * @param num   通道编号
     * @return
     */
    public String smsConfig(int num){
        return getBasicData(CacheMoulde.SMSCONFIG, String.valueOf(num));
    }

    /**
     * 取得指定消息内容
     *
     * @param confKey   配置键
     * @return          消息内容
     */
    public String message(int confKey){
        return getBasicData(CacheMoulde.MESSAGE, String.valueOf(confKey));
    }

    /**
     * 取得配置值
     *
     * @param moulde    模块名
     * @param confKey   配置键
     * @return          配置值
     */
    private String getBasicData(CacheMoulde moulde, String confKey){
        String result = basicData.get(moulde, confKey.toUpperCase());
        return result;
    }
}
