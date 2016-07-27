package com.ancun.task.cfg;

import com.ancun.task.utils.SpringContextUtil;
import com.ancun.utils.DESUtils;
import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据库访问配置信息
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Configuration
@PropertySource({"file:${up2yun.home}/config/jdbc.properties"})
public class DataSourceConfig {

    /** 数据库驱动 */
    @Value("${jdbc.driver}")
    private String driver;

    /** 数据库连接地址 */
    @Value("${jdbc.url}")
    private String url;

    /** 数据库连接用户名 */
    @Value("${jdbc.user}")
    private String user;

    /** 数据库连接密码 */
    @Value("${jdbc.password}")
    private String password;

    /** 数据库连接池大小 */
    @Value("${jdbc.initialSize}")
    private int initialSize;

    /** 最大工作线程数 */
    @Value("${jdbc.maxActive}")
    private int maxActive;

    /** 最大空闲线程数 */
    @Value("${jdbc.maxIdle}")
    private int maxIdle;

    /** 最小空闲线程数 */
    @Value("${jdbc.minIdle}")
    private int minIdle;

    /** 最大等待时间 */
    @Value("${jdbc.maxWait}")
    private int maxWait;

    /** 超过removeAbandonedTimeout时间后，是否进行没用连接 */
    @Value("${jdbc.removeAbandoned}")
    private boolean removeAbandoned;

    /** 超过时间限制，回收没有用(废弃)的连接 */
    @Value("${jdbc.removeAbandonedTimeout}")
    private int removeAbandonedTimeout;

    /** 打开检查,用异步线程evict进行检查 */
    @Value("${jdbc.testWhileIdle}")
    private boolean testWhileIdle;

    /** 在进行borrowObject进行处理时，对拿到的connection进行validateObject校验 */
    @Value("${jdbc.testOnBorrow}")
    private boolean testOnBorrow;

    /** 在进行returnObject对返回的connection进行validateObject校验 */
    @Value("${jdbc.testOnReturn}")
    private boolean testOnReturn;

    /** 检查的sql */
    @Value("${jdbc.validationQuery}")
    private String validationQuery;

    /** 检查超时设置 */
    @Value("${jdbc.validationQueryTimeout}")
    private int validationQueryTimeout;

    /** 设置的Evict线程的时间，单位ms，大于0才会开启evict检查线程 */
    @Value("${jdbc.timeBetweenEvictionRunsMillis}")
    private long timeBetweenEvictionRunsMillis;

    /** 每次检查链接的数量，建议设置和maxActive一样大，这样每次可以有效检查所有的链接 */
    @Value("${jdbc.numTestsPerEvictionRun}")
    private int numTestsPerEvictionRun;

    /** 连接的超时时间 */
    @Value("${jdbc.minEvictableIdleTimeMillis}")
    private long minEvictableIdleTimeMillis;

    /**
     * 数据源设定
     *
     * @return
     */
    @Bean(autowire= Autowire.BY_TYPE, destroyMethod = "close")
    public DataSource dataSource() {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName(driver);
        dataSource.setUrl(url);
        dataSource.setUsername(user);
        // 密码解密
        dataSource.setPassword(DESUtils.decrypt(password, null));
        // 连接池启动时创建的初始化连接数量
        dataSource.setInitialSize(initialSize);
        // 连接池中可同时连接的最大的连接数
        dataSource.setMaxActive(maxActive);
        /* 连接池中最大的空闲的连接数，超过的空闲连接将被释放，
        如果设置为负数表示不限制（maxIdle不能设置太小，
        因为假如在高负载的情况下，连接的打开时间比关闭的时间快，
        会引起连接池中idle的个数 上升超过maxIdle，
        而造成频繁的连接销毁和创建，类似于jvm参数中的Xmx设置) */
        dataSource.setMaxIdle(maxIdle);
        /* 连接池中最小的空闲的连接数，低于这个数量会被创建新的连接
        （该参数越接近maxIdle，性能越好，因为连接的创建和销毁，
         都是需要消耗资源的；但是不能太大，因为在机器很空闲的时候，
         也会创建低于minidle个数的连接，类似于jvm参数中的Xmn设置）*/
        dataSource.setMinIdle(minIdle);
        /* 最大等待时间，当没有可用连接时，连接池等待连接释放的最大时间，
        超过该时间限制会抛出异常，如果设置-1表示无限等待
        （避免因线程池不够用，而导致请求被无限制挂起） */
        dataSource.setMaxWait(maxWait);
        // 超过removeAbandonedTimeout时间后，是否进 行没用连接（废弃）的回收
        dataSource.setRemoveAbandoned(removeAbandoned);
        // 超过时间限制，回收没有用(废弃)的连接
        dataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);

        // 断线重连设置
        /* GenericObjectPool中针对pool管理，起了一个Evict的TimerTask定时线程进行控制
        (可通过设置参数timeBetweenEvictionRunsMillis>0),定时对线程池中的链接进行validateObject校验，
        对无效的链接进行关闭后，会调用ensureMinIdle，适当建立链接保证最小的minIdle连接数。 */
//        dataSource.setTestWhileIdle(testWhileIdle);
//        // 在进行borrowObject进行处理时，对拿到的connection进行validateObject校验
//        dataSource.setTestOnBorrow(testOnBorrow);
//        // 在进行returnObject对返回的connection进行validateObject校验
//        dataSource.setTestOnReturn(testOnReturn);
//        // 检查的sql
////        dataSource.setValidationQuery(validationQuery);
//        // 在执行检查时，通过statement设置，statement.setQueryTimeout(validationQueryTimeout)
//        dataSource.setValidationQueryTimeout(validationQueryTimeout);
        // 设置的Evict线程的时间，单位ms，大于0才会开启evict检查线程
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
//        // 每次检查链接的数量，建议设置和maxActive一样大，这样每次可以有效检查所有的链接
//        dataSource.setNumTestsPerEvictionRun(numTestsPerEvictionRun);
        // 连接的超时时间
//        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        return dataSource;
    }

    /**
     * spring jdbc操作类
     *
     * @return
     */
    @Bean(name = "jdbcTemplate")
    public JdbcTemplate jdbcTemplate() {
        return new JdbcTemplate(dataSource());
    }

    /**
     * spring容器工具类注入
     *
     * @return
     */
    @Bean
    @Lazy(false)
    public SpringContextUtil springContextUtil(){
        return new SpringContextUtil();
    }
}