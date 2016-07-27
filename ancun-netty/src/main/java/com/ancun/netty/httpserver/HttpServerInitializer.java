/*
 * Copyright 2010, eCollege, Inc.  All rights reserved.
 */
package com.ancun.netty.httpserver;

import com.ancun.netty.common.PipelineInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * netty http server 进出管道初始化
 *
 * @Created on 2016年2月25日
 * @author 摇光
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class HttpServerInitializer extends PipelineInitializer {

	/**
	 * 默认构造函数
	 */
	public HttpServerInitializer()
	{
		super();
	}

	@Override
	protected void addOwnHandlers(ChannelPipeline pipeline) {

		// Inbound handlers
		pipeline.addLast("decoder", new HttpRequestDecoder());
		pipeline.addLast("inflater", new HttpContentDecompressor());

		// Outbound handlers
		pipeline.addLast("encoder", new HttpResponseEncoder());
		pipeline.addLast("chunkWriter", new ChunkedWriteHandler());
		pipeline.addLast("deflater", new HttpContentCompressor());

	}

}
