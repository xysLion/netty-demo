package com.ancun.up2yun.iplimit;

import com.ancun.up2yun.domain.common.Error;
import com.ancun.up2yun.domain.common.HandleResult;
import com.ancun.up2yun.utils.NettyResult;

import java.net.InetSocketAddress;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;

/**
 * Ip 过滤
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/26
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class IpFilter extends RuleBasedIpFilter {

    public IpFilter(IpFilterRule... rules) {
        super(rules);
    }

    /**
     * 返回请求拒绝信息
     *
     * @return A {@link ChannelFuture} if you perform I/O operations, so that
     *         the {@link Channel} can be closed once it completes. Null otherwise.
     */
    @SuppressWarnings("UnusedParameters")
    protected ChannelFuture channelRejected(ChannelHandlerContext ctx, InetSocketAddress remoteAddress) {

        Error error = new Error();
        error.setError(HttpResponseStatus.FORBIDDEN.reasonPhrase());
        error.setMessage("[" + remoteAddress.getAddress().getHostAddress() + "] 不在允许访问的ip列表");
        error.setStatus(HttpResponseStatus.FORBIDDEN.code());

        return NettyResult.writeResponseAndClose(ctx,
                new HandleResult<Error>(HttpResponseStatus.FORBIDDEN, error));
    }
}
