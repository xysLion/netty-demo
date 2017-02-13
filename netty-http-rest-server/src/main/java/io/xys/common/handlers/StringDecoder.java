package com.ancun.common.handlers;

import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.domain.request.ReqBody;
import com.ancun.common.domain.request.ReqCommon;
import com.ancun.common.exception.EduException;
import com.ancun.common.utils.ByteBufToBytes;
import com.ancun.utils.sign.HmacSha1Utils;
import com.ancun.utils.sign.MD5Utils;
import com.google.common.base.*;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Nullable;
import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

/**
 * 把httpRequest转换成String
 *
 * @Created on 2015年5月8日
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "stringDecoder")
@ChannelHandler.Sharable
public class StringDecoder extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(StringDecoder.class);

    /** 请求体头部签名key */
    private static final String HTTP_HEADER_SIGN_KEY = "sign";

    /** 项目前期配置遗留问题接口，此接口已移到请求体内，所以连在url上的该接口无效 */
    private static final String INVALID_ACTION = "regionarea";

    /** 缓存 */
    @Resource
    private BasicDataCache cache;

    /** 临时ByteBuf */
    private volatile ByteBufToBytes reader = null;

    /** 是否get请求 */
    private volatile boolean isGetRequest = false;

    /** 签名 */
    private volatile String sign = "";

    /** get请求接口名 */
    private volatile String actionForUri = "";

    /** get请求时间 */
    private volatile String rqtimeForGet = String.valueOf(System.currentTimeMillis());

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        // 取得请求来源地址
        String remoteAddress = ctx.channel().remoteAddress().toString();
        // IP白名单认证
        doIpLimitValid(remoteAddress);

        /** 请求体中信息 */
        Map<String, String> uriParams = null;

        // 如果是Http请求
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;

            // 取得签名
            sign = HttpHeaders.getHeader(request, HTTP_HEADER_SIGN_KEY);

            uriParams = sanitizeUri(request.getUri());
            logger.info("action on uri：{}", actionForUri);
            isGetRequest = request.getMethod() == HttpMethod.GET;
            reader = new ByteBufToBytes((int) HttpHeaders.getContentLength(request));
        }

        //  如果是请求体内容
        if (msg instanceof HttpContent ) {
            HttpContent content = (HttpContent) msg;

            // 只有POST请求才读取内容
            if (reader != null) {
                // 读取请求体内容
                reader.reading(content.content());
            }
        }

        // 如果是get请求或者已经读完post请求中的所有内容
        if (isReadContentComplete(msg)) {
            ReferenceCountUtil.release(msg);

            String clientMsg = "";

            // 如果是get请求
            if (isGetRequest) {
                if (uriParams.isEmpty()) {
                    ctx.write("OK");
                    return;
                }
                // 校验get请求接口
                doActionValid();
                // 转换为指定类型内容体
                clientMsg = requestJsonForGet(uriParams);
            }
            // 如果是post请求
            else {
                clientMsg = contentForPost(reader);
                doEmptyValid(clientMsg);
                if (Strings.isNullOrEmpty(actionForUri)) {
                    // 签名校验
                    doSignValid(clientMsg);
                }
                // 验证接口是否通过
                else if (doActionValid()){
                    clientMsg = jsonForPost(clientMsg);
                }
            }

            ctx.fireChannelRead(clientMsg);
            return;
        }
    }

    /**
     * 从请求路径中取出请求体common部分
     *
     * @param uri   请求路径
     * @return      请求体common部分
     */
    private void setActionForUri(String uri) throws URISyntaxException {

        URI uriTemp = new URI(uri);

        actionForUri = uriTemp.getPath();

        // 去掉接口前的/
        if (actionForUri.contains("/")) {
            actionForUri = actionForUri.substring(1);
        }

        // 去掉接口后的所有字符串
        if (actionForUri.contains("/")) {
            actionForUri = actionForUri.substring(0, actionForUri.indexOf("/"));
        }

        // 前向兼容操作
        actionForUri = emptyActionForUri(actionForUri);
    }

    /**
     * 前向兼容以前接口用
     *
     * @param actionForUri  连在url上的参数信息
     * @return              接口名
     */
    private String emptyActionForUri(String actionForUri) {

        // 如果取到指定无效接口名，则重置为空
        actionForUri = Objects.equal(INVALID_ACTION, actionForUri) ? "" : actionForUri;

        // 如果是电话号码，则重置为空
        actionForUri = CharMatcher.JAVA_DIGIT.matchesAllOf(actionForUri) ? "" : actionForUri;

        return actionForUri;
    }

    /**
     * 解析url上参数
     *
     * @param uri   需要解析url
     * @return      解析完参数(a=b&c=d形式)
     */
    private Map<String, String> sanitizeUri(String uri) throws URISyntaxException {

        QueryStringDecoder decoder = new QueryStringDecoder(uri);

        // 设置接口名
        setActionForUri(decoder.path());

        // 取得参数
        Map<String, List<String>> paramsMap = decoder.parameters();

        Map<String, String> params = Maps.transformValues(paramsMap, new Function<List<String>, String>() {
            @Nullable
            @Override
            public String apply(@Nullable List<String> input) {
                return input != null ? input.get(0) : "";
            }
        });

        return params;
    }

    /**
     * 判断是否读取完成
     *
     * @param msg   接收到的信息体
     * @return      判断结果 true读取完成/false未读取完成
     */
    private boolean isReadContentComplete(Object msg){

        // get请求时
        if (msg instanceof HttpRequest && isGetRequest) {
            return true;
        }

        // post请求时
        if (msg instanceof HttpContent && reader != null && reader.isEnd()) {
            return true;
        }

        return false;
    }

    /**
     * 取得get请求参数封装成json
     *
     * @param uriParams 请求路径参数列表
     * @return          请求体json
     */
    private String requestJsonForGet(Map<String, String> uriParams) {
        return json(uriParams);
    }

    /**
     * 将POST请求体内容封装成json
     *
     * @param content 请求体内容
     * @return        请求体json
     */
    private String jsonForPost(String content) {

        Map<String, String> paramMap = Maps.newHashMap();
        paramMap.put(Constants.REQ_CONTENT, content);

        return json(paramMap);
    }

    /**
     * 将内容体封装成json
     *
     * @param content   内容体
     * @param <T>       内容体类型
     * @return          json字符串
     */
    private <T> String json(T content) {

        // 生成请求体实例
        ReqCommon common = new ReqCommon();
        common.setAction(actionForUri);
        common.setReqtime(rqtimeForGet);
        ReqBody<T> reqBody = new ReqBody<T>(common, content);

        // 返回请求体json
        String toJson = new Gson().toJson(reqBody);

        //  请求体结果
        String result = Joiner.on("").join(
                "{\"request\": ",
                toJson,
                "}"
        ).toString();

        return result;

    }

    /**
     * 取得POST请求体json
     *
     * @param reader    消息体
     * @return          请求体json
     * @throws UnsupportedEncodingException
     */
    private String contentForPost(ByteBufToBytes reader) throws UnsupportedEncodingException {
        byte[] clientMsg = reader.readFull();
        return new String(clientMsg, Constants.CHARSETNAME_DEFAULT);
    }

    /**
     * IP白名单校验
     *
     * @param remoteAddress 请求来源地址
     */
    private void doIpLimitValid(String remoteAddress) {

        // 启用IP白名单认证时
        if (cache.toBoolenConfig(Constants.IPLIMIT)) {
            // 取得ip地址
            String remoteIp = remoteAddress.substring(remoteAddress.indexOf("/"), remoteAddress.indexOf(":"));

            // ip白名单列表
            String iplimits = cache.config(Constants.IPLIMIT_IPS);

            // 如果列表中不包含该IP
            if (!iplimits.contains(remoteIp)) {
                throw new EduException(ResponseConst.IP_DENIED);
            }
        }
    }

    /**
     * 校验接口是否有效
     *
     * @return  校验通过
     */
    private boolean doActionValid(){

        // get请求列表
        String getUris = cache.config(Constants.GET_URIS);

        // 不是允许的get请求接口
        if (!getUris.contains(actionForUri)) {
            throw new EduException(ResponseConst.INTERFACE_DENIED);
        }

        return true;
    }

    /**
     * 请求体空值判断检验
     *
     * @param content   请求体
     */
    private void doEmptyValid(String content) {

        if (Strings.isNullOrEmpty(content)) {
            throw new EduException(ResponseConst.PARAMS_MSG_NOT_EMPTY);
        }

    }

    /**
     * 签名检验
     *
     * @param content   请求体
     */
    private void doSignValid(String content) {

        // 启用签名校验
        if (cache.toBoolenConfig(Constants.SIGN_VALID)) {
            // 取得签名密钥
            String accessKey = cache.config(Constants.SIGN_KEY);

            // 校验签名
            boolean signValid = HmacSha1Utils.signCheck(
                    MD5Utils.md5(content).toLowerCase(), accessKey, sign, Constants.CHARSETNAME_DEFAULT);

            // 签名检验不通过
            if (!signValid) {
                throw new EduException(ResponseConst.SIGN_NAME_NOT_MATCH);
            }
        }

    }

    public static void main(String[] args) throws Exception {
//        String testmsg = "/?action=sms&time=20160111";
//        testmsg = testmsg.substring(testmsg.indexOf("?") + 1);
//        Map<String, String> testMap = Splitter.on("&").withKeyValueSeparator("=").split(testmsg);
//        System.out.println(testMap);
//        String uri = "/";
//        System.out.println("testMsg : " + new StringDecoder().sanitizeUri(testmsg));
//        System.out.println("uri : " + new StringDecoder().sanitizeUri(uri));

//        Gson gson = new Gson();
//
//        Map<String, String> paramMap = Maps.newHashMap();
//        paramMap.put("common.action", "sms");
//        paramMap.put("common.reqtime", "20160112");
//        paramMap.put("phoneNo", "12345678901");
//
//        ReqCommon common = new ReqCommon();
//        common.setAction(paramMap.get("common.action"));
//        common.setReqtime(paramMap.get("common.reqtime"));
////        ReqBody<Map<String, String>> reqBody = new ReqBody<Map<String, String>>(common, paramMap);
//        ReqBody<?> reqBody = new ReqBody<Map<String, String>>(common, paramMap);
//        String toJson = gson.toJson(reqBody);
//        System.out.println("解析后得到的JSON：" + toJson);
//
//        Type type = new TypeToken<ReqBody<Map<String, String>>>(){}.getType();
//        ReqBody<Map<String, String>> input = gson.fromJson(toJson, type);
//        System.out.println("解析结束。");

//        String urls = "192.168.01,192.168.0.2";
//        String url = "192.168.0.1";
//        System.out.println(urls.contains(url));
//        String content = "{\n" +
//                "  \"request\": {\n" +
//                "    \"common\": {\n" +
//                "      \"action\": \"regionarea\",\n" +
//                "      \"reqtime\": \"2016-01-08 09:52:03\",\n" +
//                "      \"asyn\": \"false\"\n" +
//                "    },\n" +
//                "    \"content\": {\n" +
//                "      \"phoneNo\": \"13646829663\"\n" +
//                "    }\n" +
//                "  }\n" +
//                "}";
//        String key = "COMMON-SERVER";
//
//        String sign = HmacSha1Utils.signToString(MD5Utils.md5(content).toLowerCase(), key, Constants.CHARSETNAME_DEFAULT);
//
//        System.out.println(sign);
//        String uri = "/receivSmsStatus";
        String uri = "/a";
        String action = "receivSmsStatus";
//        System.out.println(uri.length());
//        System.out.println(action.length());
//        System.out.println(uri.substring(action.length() + 2));
        String uriFilter = Splitter.on("?").omitEmptyStrings().trimResults().limit(1).splitToList(uri).get(0);
        String actionForUri = Splitter.on("/").omitEmptyStrings().trimResults().limit(2).splitToList(uriFilter).get(0);
        System.out.println(uriFilter);
        System.out.println(actionForUri);
    }
}