package com.ancun.common.handlers;

import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.Constants;
import com.ancun.common.constant.ResponseConst;
import com.ancun.common.domain.request.ReqBody;
import com.ancun.common.domain.request.ReqCommon;
import com.ancun.common.domain.response.RespBody;
import com.ancun.common.exception.EduException;
import com.ancun.common.utils.DispatcherBus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.Map;

import javax.annotation.Resource;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;

/**
 * 分发服务操作类
 *
 * @Created on 2015年5月8日
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "businessHandler")
@ChannelHandler.Sharable
public class BusinessHandler extends ChannelInboundHandlerAdapter{

    private static final Logger logger = LoggerFactory.getLogger(BusinessHandler.class);

    /** json 解析工具 */
    private static Gson gson = new Gson();

    /** 创建一个JsonParser */
    private static JsonParser parser = new JsonParser();

    /** 请求分发总线 */
    @Resource
    private DispatcherBus dispatcherBus;

    /** 缓存 */
    @Resource
    protected BasicDataCache cache;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        logger.info("BusinessHandler read msg from client :" + msg);

        try {
            // 将json解析成json元素对象
            JsonElement reqJe = parser.parse(msg.toString());
            JsonObject jsonObject = reqJe.getAsJsonObject();
            // 取得request对象
            jsonObject = jsonObject.getAsJsonObject(Constants.REQUEST);

            // 取得请求体Common部分
            ReqCommon common = gson.fromJson(jsonObject.get(Constants.REQ_COMMON), ReqCommon.class);

            // 如果action为空
            if (common == null
                    || common.getAction() == null
                    || "".equals(common.getAction())) {
                throw new EduException(ResponseConst.ACTION_NOT_NULL);
            }

            // 取得异步标记,在取不到的情况下为同步
            boolean asynFlg = common.isAsyn();

            // 异步返回请求
            if (asynFlg) {
                RespBody<String> respBody = new RespBody<String>(null);
                logger.info("BusinessHandler send msg to client :" + toResponseJson(respBody));
                ctx.write(toResponseJson(respBody));
            }

            logger.info("开始分发请求");
            JsonObject content = jsonObject.getAsJsonObject(Constants.REQ_CONTENT);
            // 增加请求URL
            content.addProperty("requestUrl", ctx.channel().remoteAddress().toString());
            // 如果含有电话号码字段
            if (content.has("phoneNo")) {
                content.add("mobile", content.get("phoneNo"));
            }
            Object result = dispatcherBus.post(common.getAction().trim(), content);
            RespBody<?> respBody = new RespBody<>(result);

            // 同步返回请求
            if (!asynFlg) {
                logger.info("具体业务处理，给客户端响应结果 : {}", toResponseJson(respBody));
                ctx.write(toResponseJson(respBody));
            }
        } finally {
            ReferenceCountUtil.release(msg);
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {

        // 请求响应的格式（xml/json）
        RespBody<String> out = new RespBody<String>(null);
        int code = ResponseConst.SYSTEM_EXCEPTION;
        Object[] params = new Object[]{""};

        // 如果是自定义异常
        Throwable rootCause = Throwables.getRootCause(cause);
        if(null != rootCause && rootCause instanceof EduException){
            code = ((EduException) cause).getCode();
            params = ((EduException) cause).getParams();
        }

        // 取得异常信息
        String msg = cache.message(code);
        if (Strings.isNullOrEmpty(msg)) {
            msg = "系统异常";
        } else {
            msg = MessageFormat.format(msg, params);
        }

        // 打印异常
        logger.error("业务异常,{} : {}", code, msg, rootCause);

        // 设置返回体信息
        out.getInfo().setCode(code);
        out.getInfo().setMsg(msg);

        // 通知客户端
        ctx.write(toResponseJson(out));
    }

    /**
     * 根据json取得相应对象
     *
     * @param msg 需要解析的json字符串
     * @return 解析结果
     */
    private ReqBody<Map<String, String>> parseMsg(String msg) {

        // 创建一个JsonParser
        JsonParser parser = new JsonParser();
        JsonElement jsonEl = parser.parse(msg);
        JsonObject jsonObject = jsonEl.getAsJsonObject();

        // 取得json参数
        Type type = new TypeToken<ReqBody<Map<String, String>>>(){}.getType();
        ReqBody<Map<String, String>> input = gson.fromJson(jsonObject.getAsJsonObject("request"), type);

        return input;
    }

    /**
     * 构造response结果
     *
     * @param object 需要解析成json的对象
     * @return json字符串
     */
    private String toResponseJson(Object object) {
        JsonElement je = gson.toJsonTree(object);
        JsonObject jo = new JsonObject();
        jo.add("response", je);
        return jo.toString();
    }

    public static void main(String[] args) {

//        String msg = "{\n" +
//                "\t\"common\":\n" +
//                "\t{\n" +
//                "\t\t\"action\": \"regionarea\",\n" +
//                "\t\t\"reqtime\": \"20150506101218\"\n" +
//                "\t},\n" +
//                "\t\"content\": \n" +
//                "\t{\n" +
//                "\t\t\"phoneNo\": \"13646829663\"\n" +
//                "\t}\n" +
//                "}";
        String msg = "{\"request\": {\"common\":{\"action\":\"smsStatusForWeixun\",\"reqtime\":\"1458114897727\",\"asyn\":false},\"content\":\"MT57086043,13646829663,DELIVRD;57086315,13646829663,DELIVRD;57086833,13646829663,DELIVRD;57088209,13867851262,DELIVRD;\"}}";

        // 取得json参数
//        Type type = new TypeToken<ReqBody<Map<String, String>>>(){}.getType();
//        ReqBody<Map<String, String>> input = new Gson().fromJson(msg, type);
//        System.out.println(input.getContent());
        Gson gson = new Gson();
        JsonParser parser = new JsonParser();
        JsonElement jsonEl = parser.parse(msg);
        JsonObject jsonObject = jsonEl.getAsJsonObject().getAsJsonObject("request");
        ReqCommon common = gson.fromJson(jsonObject.getAsJsonObject("common"), ReqCommon.class);
        System.out.println(common.getAction() + " : " + common.getReqtime());

//        String phone = "13646829663";
//        System.out.println(phone.length());

//        String str = "BOS;OOS";

//        Iterable<String> strs = Splitter.on(";").trimResults().split(str);
//        List<String> strlist = Lists.newArrayList(strs);
//        System.out.println(strlist.contains("BOS"));
    }
}
