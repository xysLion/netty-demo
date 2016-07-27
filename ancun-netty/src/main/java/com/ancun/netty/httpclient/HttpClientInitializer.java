package com.ancun.netty.httpclient;

import com.ancun.netty.common.PipelineInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.util.concurrent.TimeUnit;

/**
 * netty http client进出管道初始化
 *
 * @Created on 2016年3月18日
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class HttpClientInitializer extends PipelineInitializer {

    // 读取超时设置
    private int readTimeout = -1;

    public HttpClientInitializer() {
        super();
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public HttpClientInitializer setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
        return this;
    }

    @Override
    protected void addOwnHandlers(ChannelPipeline pipeline) {

        pipeline.addLast(new HttpClientCodec());

        if (readTimeout > 0) {
            pipeline.addLast(new ReadTimeoutHandler(readTimeout,
                    TimeUnit.MILLISECONDS));
        }

    }
}
