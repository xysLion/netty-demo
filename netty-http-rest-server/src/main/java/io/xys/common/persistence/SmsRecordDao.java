package com.ancun.common.persistence;

import com.ancun.common.model.sms.SmsRecord;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.util.List;
import java.util.UUID;

/**
 * 短信记录信息Dao
 *
 * @Created on 2016年01月15日
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Repository
public class SmsRecordDao {

    /** 批量插入用SQL */
    private static final String INSERT_SQL = "INSERT INTO SMS_RECORDE(ID, REQUEST_URL, MOBILE, SMS_PIPELINE, MSG, STATUS, CREATE_TIME)\n" +
            "VALUES(?,?,?,?,?,'SENDING',now())";

    /** 更新语句 */
    private static final String UPDATE_SQL = "UPDATE SMS_RECORDE SET SMS_PIPELINE = ?, MSGID = ?, RESPONSE_TIME = now(), STATUS = ?, REMARK = CONCAT(IFNULL(REMARK, ''), '\\n', ?) WHERE ID IN (?)";

    /** 报告状态语句 */
    private static final String UPDATE_REPORT_SQL = "UPDATE SMS_RECORDE SET REPORT_TIME = now(), STATUS = ?, REMARK = ? WHERE MOBILE = ? AND MSGID = ?";

    /** 操作数据库用template */
    @Resource
    private JdbcTemplate jdbcTemplate;

    /**
     * 批量插入短信记录，该记录中号码字段有多个号码
     *
     * @param smsRecord 短信记录
     * @return          插入记录ID列表
     */
    public Iterable<String> batchInsertSms(SmsRecord smsRecord){
        List<SmsRecord> smsRecords = createSmsRecords(smsRecord);
        return batchInsertSms(smsRecords);
    }

    /**
     * 批量插入短信记录
     *
     * @param smsRecords    短信记录列表
     * @return              插入记录ID集合
     */
    public Iterable<String> batchInsertSms(Iterable<SmsRecord> smsRecords) {

        // 批量插入数据库
        jdbcTemplate.batchUpdate(INSERT_SQL, objectsForInsert(smsRecords));

        return insertIds(smsRecords);
    }

    /**
     * 更新短信记录
     *
     * @param smsRecord 更新内容
     * @param ids       需更新主键ID列表
     */
    public int batchUpdateSms(SmsRecord smsRecord, Iterable<String> ids){
        // 用于IN条件
        String inIds = Joiner.on(",").join(ids).toString();

        // 更新短信记录
        return jdbcTemplate.update(UPDATE_SQL, new Object[]{
                smsRecord.getNum(),
                smsRecord.getMsgid(),
                smsRecord.getStatus(),
                smsRecord.getRemark(),
                inIds
            });
    }

    /**
     * 报告短信状态
     *
     * @param smsRecord 短信状态相关信息
     * @return          变更条数
     */
    public int reportStatus(SmsRecord smsRecord){
        return jdbcTemplate.update(UPDATE_REPORT_SQL, new Object[]{
                smsRecord.getStatus(),
                smsRecord.getRemark(),
                smsRecord.getMobile(),
                smsRecord.getMsgid()
        });
    }

    /**
     * 报告短信状态
     *
     * @param statuses 短信状态列表
     * @return         变更条数
     */
    public int[] reportStatusForWeixun(List<Object[]> statuses){
        return jdbcTemplate.batchUpdate(UPDATE_REPORT_SQL, statuses);
    }

    /**
     * 复制一个短信内容
     *
     * @param smsRecord
     * @return
     */
    private SmsRecord copySms(SmsRecord smsRecord){
        SmsRecord result = new SmsRecord();
        result.setId(UUID.randomUUID().toString());
        result.setRequestUrl(smsRecord.getRequestUrl());
        result.setNum(smsRecord.getNum());
        result.setMessage(smsRecord.getMessage());

        return result;
    }

    /**
     * 创建短信记录列表
     *
     * @param smsRecord 短信实体
     * @return          短信记录列表
     */
    private List<SmsRecord> createSmsRecords(final SmsRecord smsRecord) {

        // 取得号码
        String mobileStr = smsRecord.getMobile();
        Iterable<String> mobiles = Splitter.on(",").trimResults().omitEmptyStrings().split(mobileStr);
        Iterable<SmsRecord> smsRecords = Iterables.transform(mobiles, new Function<String, SmsRecord>() {
            @Nullable
            @Override
            public SmsRecord apply(@Nullable String input) {
                SmsRecord result = copySms(smsRecord);
                result.setMobile(input);
                return result;
            }
        });

        return Lists.newArrayList(smsRecords);
    }

    /**
     * 取得需要插入的字段
     *
     * @param smsRecords    短信列表
     * @return              批量插入用集合
     */
    private List<Object[]> objectsForInsert(Iterable<SmsRecord> smsRecords){
        return FluentIterable.from(smsRecords).transform(new Function<SmsRecord, Object[]>() {
            @Override
            public Object[] apply(SmsRecord input) {
                return new Object[]{input.getId(), input.getRequestUrl(), input.getMobile(),
                        input.getNum(), input.getMessage()};
            }
        }).toList();
    }

    /**
     * 新增记录ID
     *
     * @param smsRecords    短信记录
     * @return              短信记录ID集合
     */
    private Iterable<String> insertIds(Iterable<SmsRecord> smsRecords){
        return FluentIterable.from(smsRecords).transform(new Function<SmsRecord, String>() {
            @Nullable
            @Override
            public String apply(@Nullable SmsRecord input) {
                return input.getId();
            }
        });
    }

    public static void main(String[] args) {
        String mobileStr = "13646829663,13646829663";
        Iterable<String> mobiles = Splitter.on(",").split(mobileStr);
        System.out.println(Joiner.on(",").join(mobiles));
    }

}
