package com.ancun.task.listener;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.common.math.LongMath;

import com.ancun.task.event.Up2YunEvent;
import com.ancun.task.utils.MD5Util;
import com.ancun.task.utils.StringUtil;
import com.ancun.task.utils.TaskUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.math.RoundingMode;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

/**
 * 上传到云对象存储器事件监听
 *
 * @Created on 2015-02-19
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component
public class Up2YunListener implements InitializingBean {

    private static final Logger logger = LoggerFactory.getLogger(Up2YunListener.class);

    // 分块上传默认设置每块为 20M
	private long partSize = 1024 * 1024 * 20;

    /** bucket后缀 */
    @Value("${BUCKET_SUFFIX}")
    private String BUCKET_SUFFIX;

    /** accessid后缀 */
    @Value("${ACCESSID_SUFFIX}")
    private String ACCESSID_SUFFIX;

    /** accesskey后缀 */
    @Value("${ACCESSKEY_SUFFIX}")
    private String ACCESSKEY_SUFFIX;

    /** yuntype的健 */
    @Value("${YUN_TYPE}")
    private String YUNTYPE_KEY;

    /** file_path的健 */
    private String FILE_PATH_KEY = "file_path";

    /** user_meta_info的健 */
    private String USER_META_INFO_KEY = "user_meta_info";

    /** FILE_MD5的健 */
    @Value("${FILE_MD5}")
    private String FILE_MD5_KEY;

    /** FILE_KEY的健 */
    @Value("${FILE_KEY}")
    private String FILE_KEY_KEY;

    /**  重试上传云类型的健 */
    public String RETRY_UP_YUN_TYPE_KEY = "retry_up_yun_type";

    /** 默认云类型为阿里云 */
    private final String DEFAULT_YUN = "oss";

    /** 百度云 */
    private final String YUN_FOR_BOS = "bos";

    /** 上传结果 */
    private List<Boolean> result = Lists.newArrayList();

    /** 异常信息 */
    private String exceptionMsg = "";

    /** 重试上传云参数 */
    private String retryUpYunType = "";

    @Resource
    private EventBus eventBus;

    /**
     * 上传到OSS
     *
     * @return
     * @throws Exception
     */
    @Subscribe
    public void upload2OSS(Up2YunEvent up2YunEvent) {

        // 取得相关参数
        Map<String,Object> taskParams = up2YunEvent.getTaskParams();

        try {

            // 默认上传到阿里云OSS（0：阿里云OSS）
            String yunType = TaskUtil.getValue(taskParams, YUNTYPE_KEY);
            yunType = Strings.isNullOrEmpty(yunType) ? DEFAULT_YUN : yunType;

            // 重试上传到云
            String retryUp2YunType = TaskUtil.getValue(taskParams, RETRY_UP_YUN_TYPE_KEY);
            yunType = Strings.isNullOrEmpty(retryUp2YunType) ? yunType : retryUp2YunType;

            // 如果是上传到阿里云
            if (yunType.contains(DEFAULT_YUN)) {

                // 取得accessId
                String accessId = TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSID_SUFFIX);

                // 取得accessKey
                String accessKey = TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSKEY_SUFFIX);

                // 取得要传入的bucket名称
                String bucket = TaskUtil.getValue(taskParams, DEFAULT_YUN + BUCKET_SUFFIX);

                // 开始上传时间
                long beginTime = System.currentTimeMillis();
//                logger.info("文件 ：[" + TaskUtil.getValue(taskParams, FILE_KEY_KEY) + "]上传到OSS开始：" + beginTime );
                logger.info("文件 ：[{}]上传到OSS开始：{}", TaskUtil.getValue(taskParams, FILE_KEY_KEY), beginTime );

                // 新建阿里云实例
                AliyunOSS oss = new AliyunOSS(accessId, accessKey);

                // 上传到阿里云
                @SuppressWarnings("unchecked")
				Response responseResult = oss.putObject(bucket, TaskUtil.getValue(taskParams, FILE_KEY_KEY),
                        TaskUtil.getValue(taskParams, FILE_PATH_KEY), (Map<String, String>)taskParams.get(USER_META_INFO_KEY));

                // 如果成功则返回上传到阿里云后云上生成的MD5
                String getedMd5 = Strings.nullToEmpty(responseResult.getEtag());

                // 判断是否上传成功
                boolean resultFlg = fileMd5(taskParams).equals(getedMd5.toUpperCase());
                // 如果MD5验证不同过
                if (!resultFlg) {
                    exceptionMsg += "上传阿里云后得到的MD5与产品平台提供的MD5不一致";
                }

                // 上传到云结束时间
                long endTime = System.currentTimeMillis();

                logger.info("文件 ：[{}] 上传OSS结束：{}", TaskUtil.getValue(taskParams, FILE_KEY_KEY), endTime );
                logger.info("文件 ：[{}] 上传OSS花费时间：{}ms", TaskUtil.getValue(taskParams, FILE_KEY_KEY), ( endTime - beginTime ));

                result.add(resultFlg);
            }
        } catch (Exception e) {
            String pattern = "上传阿里云OSS时出现异常, 阿里云OSS信息[BUCKET : {}, ACCESSID : {}, ACCESSKEY : {}] 具体异常信息 ：{}\n";
            logger.info(pattern,
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + BUCKET_SUFFIX),
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSID_SUFFIX),
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSKEY_SUFFIX),
                    e);
            result.add(false);

            exceptionMsg = MessageFormat.format(pattern,
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + BUCKET_SUFFIX),
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSID_SUFFIX),
                    TaskUtil.getValue(taskParams, DEFAULT_YUN + ACCESSKEY_SUFFIX),
                    e.getMessage()
                    );
            // 设置重试上传云类型参数
            setRetryParams(up2YunEvent.getTaskParams(), DEFAULT_YUN);
        }
    }

    /**
     * 上传到百度云BOS
     *
     * @return
     * @throws Exception
     */
    @Subscribe
    public void upload2BOS(Up2YunEvent up2YunEvent) {

        // 取得相关参数
        Map<String,Object> taskParams = up2YunEvent.getTaskParams();

        try {

            // 默认上传到阿里云OSS（0：阿里云OSS）
            String yunType = TaskUtil.getValue(taskParams, YUNTYPE_KEY);
            yunType = StringUtil.isBlank(yunType) ? DEFAULT_YUN : yunType;

            // 重试上传到云
            String retryUp2YunType = TaskUtil.getValue(taskParams, RETRY_UP_YUN_TYPE_KEY);
            yunType = Strings.isNullOrEmpty(retryUp2YunType) ? yunType : retryUp2YunType;

            // 如果是上传到阿里云
            if (yunType.contains(YUN_FOR_BOS)) {

                // 取得accessId
                String accessId = TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSID_SUFFIX);

                // 取得accessKey
                String accessKey = TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSKEY_SUFFIX);

                // 取得要传入的bucket名称
                String bucket = TaskUtil.getValue(taskParams, YUN_FOR_BOS + BUCKET_SUFFIX);

                // 新建百度云BOSclient实例
                BaiduyunBOS oss = new BaiduyunBOS(accessId, accessKey);

                File file = new File(TaskUtil.getValue(taskParams, FILE_PATH_KEY));

                // 开始上传时间
                long beginTime = System.currentTimeMillis();
//                logger.info("文件 ：[" + TaskUtil.getValue(taskParams, FILE_KEY_KEY) + "]上传到BOS开始：" + beginTime );
                logger.info("文件 ：[{}]上传到BOS开始：{}", TaskUtil.getValue(taskParams, FILE_KEY_KEY), beginTime );

                // 上传到阿里云
                @SuppressWarnings("unchecked")
				Response responseResult = oss.putObject(bucket, TaskUtil.getValue(taskParams, FILE_KEY_KEY),
                        file, (Map<String, String>) taskParams.get(USER_META_INFO_KEY));

                // 如果成功则返回上传到阿里云后云上生成的MD5
                String getedMd5 = Strings.nullToEmpty(responseResult.getEtag());

                // 判断是否上传成功
                boolean resultFlg = TaskUtil.getValue(taskParams, FILE_MD5_KEY).toUpperCase().equals(getedMd5.toUpperCase());
                // 如果MD5验证不同过
                if (!resultFlg) {
                    exceptionMsg += "上传百度云后得到的MD5与产品平台提供的MD5不一致";
                }

                // 上传到云结束时间
                long endTime = System.currentTimeMillis();
                logger.info("文件 ：[{}] 上传BOS结束：{}", TaskUtil.getValue(taskParams, FILE_KEY_KEY), endTime );
                logger.info("文件 ：[{}] 上传BOS花费时间：{}ms", TaskUtil.getValue(taskParams, FILE_KEY_KEY), ( endTime - beginTime ));

                result.add(resultFlg);
            }
        } catch (Exception e) {
            String pattern = "上传百度云BOS时出现异常, 百度云BOS信息[BUCKET : {}, ACCESSID : {}, ACCESSKEY : {}] 具体异常信息 ：{}\n";
            logger.info(pattern,
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + BUCKET_SUFFIX),
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSID_SUFFIX),
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSKEY_SUFFIX),
                    e);
            result.add(false);
            exceptionMsg = MessageFormat.format(pattern,
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + BUCKET_SUFFIX),
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSID_SUFFIX),
                    TaskUtil.getValue(taskParams, YUN_FOR_BOS + ACCESSKEY_SUFFIX),
                    e.getMessage());
            // 设置重试上传云类型参数
            setRetryParams(up2YunEvent.getTaskParams(), YUN_FOR_BOS);
        }
    }

    /**
     * 取得上传到云结果
     *
     * @return 结果
     */
    public boolean up2yunResult(){

        // 结果
        boolean flg = true;

        // 遍历列表求出结果
        for (Boolean b : result) {
            flg = flg && b;
        }

        // 如果有结果则清空，否则结果为false
        if (result.size() > 0) {
            // 清空结果
            result.clear();
        } else {
            flg = false;
        }

        return flg;
    }

    /**
     * 取得异常信息
     *
     * @return 异常信息
     */
    public String getExceptionMsg(){

        String returnMsg = exceptionMsg;

        exceptionMsg = null;

        return returnMsg;
    }

    /**
     * 取得重试上传参数
     *
     * @return 重试上传参数
     */
    public String getRetryUpYunType(){

        String returnType = retryUpYunType;

        retryUpYunType = null;

        return returnType;
    }

    /**
     * 设置重试上传云参数
     *
     * @param taskParams
     * @param yunType
     */
    private void setRetryParams(Map<String, Object> taskParams, String yunType){
        retryUpYunType = TaskUtil.getValue(taskParams, RETRY_UP_YUN_TYPE_KEY);
        retryUpYunType = retryUpYunType.contains(yunType) ? retryUpYunType : retryUpYunType + "," + yunType;
    }

    /**
     * 计算接收文件的MD5值
     *
     * @param taskParams    参数
     * @return              接收文件的MD5
     */
    private String fileMd5(Map<String, Object> taskParams) throws Exception {
        // 从客户端接收md5
        String md5 = TaskUtil.getValue(taskParams, FILE_MD5_KEY).toUpperCase();

        // 文件路径
        String filePath = TaskUtil.getValue(taskParams, FILE_PATH_KEY);
        File file = new File(filePath);
        long fileLength = file.length();
        // 大于20M分块上传
        if (fileLength > partSize) {
            // 将md5置为空
            md5 = "";
            // 取得分块数
            long partNum = LongMath.divide(fileLength, partSize, RoundingMode.UP);
            // 取得文件源
            ByteSource byteSource = Files.asByteSource(file);
            for (int i = 0; i < partNum; i++) {
                long offset = i * partSize;
				long length = partSize;
				if ((offset + length) > fileLength ) {
					length = fileLength - offset;
				}
				ByteSource byteSourceTemp = byteSource.slice(offset, length);
				md5 += MD5Util.md5(byteSourceTemp.openStream()).toUpperCase();
            }
            md5 = MD5Util.md5(md5).toUpperCase() + "-" + partNum;
        }

        return md5;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        eventBus.register(this);
    }
}
