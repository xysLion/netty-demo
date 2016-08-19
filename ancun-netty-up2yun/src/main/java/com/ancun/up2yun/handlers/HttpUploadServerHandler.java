package com.ancun.up2yun.handlers;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteSource;
import com.google.common.io.Files;
import com.google.gson.Gson;

import com.ancun.up2yun.cfg.NettyProperties;
import com.ancun.up2yun.constant.BussinessConstant;
import com.ancun.up2yun.constant.MsgConstant;
import com.ancun.up2yun.domain.task.Task;
import com.ancun.up2yun.domain.task.TaskDao;
import com.ancun.up2yun.utils.HostUtil;
import com.ancun.up2yun.utils.NoticeUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.net.Inet4Address;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import javax.annotation.Resource;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.LastHttpContent;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.FileUpload;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder.EndOfDataDecoderException;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.InterfaceHttpData.HttpDataType;
import io.netty.util.CharsetUtil;
import io.netty.util.NetUtil;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * 接收上传到yun对象存储的逻辑处理类
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
@Component(value = "httpUploadServerHandler")
@ChannelHandler.Sharable
@EnableConfigurationProperties({NettyProperties.class})
public class HttpUploadServerHandler extends SimpleChannelInboundHandler<HttpObject> {

	private static final Logger logger = LoggerFactory.getLogger(HttpUploadServerHandler.class);

	/** 监控回馈信息 */
	private static final String MONITOR_RESPONSE = "OK";

	/** 文件路径标记 */
	private static final String FILE_PATH = "file_path";

	private static final HttpDataFactory factory = new DefaultHttpDataFactory(DefaultHttpDataFactory.MINSIZE);

	private static final Inet4Address LOCALHOST4 = NetUtil.LOCALHOST4;

	private HttpRequest request = null;

	private HttpPostRequestDecoder decoder;

    @Resource
    private NettyProperties nettyProperties;

	/** 任务操作Dao */
	@Resource
	private TaskDao taskDao;

	/** 通知组件 */
    @Resource
    private NoticeUtil noticeUtil;

	/** 执行任务所需的参数 */
	private Map<String, Object> taskParams = new HashMap<String, Object>();

	// 开始执行时间
	private long beginTime = 0;

	/**
	 * 上传组件逻辑处理类构造函数初期化spring容器和数据库处理类
	 */
	public HttpUploadServerHandler(){
//		taskDao = (TaskDao) SpringContextUtil.getBean("taskDao");
//		eventBus = SpringContextUtil.getBean(EventBus.class);
	}

    @Override
    public void channelRead0(ChannelHandlerContext ctx, HttpObject msg) throws Exception {

		// 开始接收文件
		beginTime = System.currentTimeMillis();
		logger.info("接收文件请求读取开始：{}", new Object[] { beginTime } );

    	if (msg instanceof HttpRequest) {
    		request = (HttpRequest) msg;

//    		uri = sanitizeUri(request.getUri());

    		// 设置请求uri
//    		String requestUri = request.getProtocolVersion().protocolName() + "://" + request.headers().get("host") + request.getUri();

    		// 设置用户meta信息
    		Map<String, String> metadata = new HashMap<String, String>();
    		for (Entry<String, String> entry : request.headers()) {
    			if (entry.getKey().startsWith(BussinessConstant.USER_META_INFO_PREFIX)) {
    				metadata.put(entry.getKey(), entry.getValue());
    			}
            }
			taskParams.put("user_meta_info", metadata);

       		if (request.getMethod() == HttpMethod.POST) {
    			if (decoder != null) {
					decoder.cleanFiles();
					decoder = null;
				}
    			try {
	              decoder = new HttpPostRequestDecoder(factory, request);
	    		} catch (Exception e) {
	        		e.printStackTrace();
	        		writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, e.toString());
	            	ctx.channel().close();
	        		return;
	    		}
    		}
			// 用于监控接口
			else if (request.getMethod() == HttpMethod.GET) {
				writeResponse(ctx, HttpResponseStatus.OK, MONITOR_RESPONSE);
				ctx.channel().close();
				return;
			}
    	}

    	if (decoder != null && msg instanceof HttpContent) {
        	HttpContent chunk = (HttpContent) msg;

        	try {
        		decoder.offer(chunk);
        	} catch (Exception e) {
				logger.info("请求体解析异常", e);
        		writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, e.toString());
            	ctx.channel().close();
                return;
        	}

        	readHttpDataChunkByChunk();

        	if (chunk instanceof LastHttpContent) {

				// 文件完整性校验
				String checkResult = fileIntegrityCheck(ctx, taskParams);
				if (!Strings.isNullOrEmpty(checkResult)) {
					HttpResponseStatus status = new HttpResponseStatus(10001, checkResult);
					writeResponse(ctx, status, checkResult);
					ctx.channel().close();
                	return;
				}

				// 如果上传到OSS的Key为空则用文件名
				if (taskParams.get(BussinessConstant.FILE_KEY) == null
						|| "".equals(taskParams.get(BussinessConstant.FILE_KEY).toString())) {
					taskParams.put(BussinessConstant.FILE_KEY, taskParams.get(BussinessConstant.FILE_NAME).toString());
				}

				// 开始持久化数据(将任务持久化到数据库)
				long beginSaveTaskTime = System.currentTimeMillis();
//				logger.info("持久化数据(将任务持久化到数据库)开始：" + beginSaveTaskTime );

        		// 持久化信息
				Task task = new Task();
				task.setTaskId(String.valueOf(UUID.randomUUID()));
				task.setReqUrl(ctx.channel().remoteAddress().toString());
				task.setRevUrl(HostUtil.getHostInfo().getAddress());
				task.setTaskParams(new Gson().toJson(taskParams));
				task.setParamsMap(taskParams);
				task.setComputeNum(0);
				task.setTaskHandler(BussinessConstant.UPTOYUN);
				task.setTaskStatus(BussinessConstant.UN_PROCESS);
				task.setGmtCreate(new Timestamp(System.currentTimeMillis()));
				task.setGmtHandle(new Timestamp(System.currentTimeMillis()));
//				task.setHandleTimeEnum(TaskHandleTimeEnum.IMMEDIATELY);
				taskDao.addTask(task);

				// 接收文件请求结束
				long endSaveTaskTime = System.currentTimeMillis();
				logger.info("文件 ：[{}] 持久化数据(将任务持久化到数据库)总共花费时间：{}ms", new Object[] {
						taskParams.get(BussinessConstant.FILE_KEY),
						(endSaveTaskTime - beginSaveTaskTime)});

				// 反馈消息给客户端
				writeResponse(ctx, HttpResponseStatus.OK, "");
                reset();

				// 反馈消息给客户端
				long endResponseTime = System.currentTimeMillis();
				logger.info("文件 ：[{}] 反馈消息给客户端总共花费时间：{}ms", new Object[]{
						taskParams.get(BussinessConstant.FILE_KEY),
						(endResponseTime - beginTime)});

                return;
            }
        }
    }

	/**
	 * 重置属性
	 */
    private void reset() {
        request = null;

        //销毁decoder释放所有的资源
        decoder.destroy();

        decoder = null;
    }

    /**
     * 通过chunk读取request，获取chunk数据
     * @throws java.io.IOException
     */
    private void readHttpDataChunkByChunk() throws IOException {
    	try {
	    	while (decoder.hasNext()) {

	            InterfaceHttpData data = decoder.next();
	            if (data != null) {
	                try {
	                    writeHttpData(data);
	                } finally {
	                    data.release();
	                }
	            }
	        }
    	} catch (EndOfDataDecoderException e1) {
			// chunk读取结束正常操作，不做任何处理
//    		System.out.println("end chunk");
    	}
    }

    /**
     * 根据取得chunk数据类型进行相应操作
     * @param data chunk数据
     * @throws java.io.IOException
     */
    private void writeHttpData(InterfaceHttpData data) throws IOException {
        if (data.getHttpDataType() == HttpDataType.FileUpload) {
            FileUpload fileUpload = (FileUpload) data;
            if (fileUpload.isCompleted()) {

				taskParams.put(BussinessConstant.FILE_NAME, fileUpload.getFilename());

				beginTime = System.currentTimeMillis();
//				logger.info("文件 ：[" + fileUpload.getFilename() + "] 接收开始：" + beginTime);
				logger.info("文件 ：[{}] 接收开始：{}", fileUpload.getFilename(), beginTime);

				StringBuffer fileNameBuf = new StringBuffer();
				fileNameBuf.append(nettyProperties.getTempDir());

				// 如果文件夹不存在
				File dir = new File(fileNameBuf.toString());
				if (!dir.exists()){
					dir.mkdirs();
				}

				// 新建文件
				fileNameBuf.append(fileUpload.getFilename())
						.append("_")
						.append(UUID.randomUUID());

				fileUpload.renameTo(new File(fileNameBuf.toString()));
				taskParams.put(FILE_PATH, fileNameBuf.toString());

				// 文件持久化时间
				long endTime = System.currentTimeMillis();
				logger.info("文件 ：[{}] 持久化到本地总共花费时间：{}ms",
						fileUpload.getFilename(), (endTime - beginTime));
            }
        } else if (data.getHttpDataType() == HttpDataType.Attribute) {
        	Attribute attribute = (Attribute) data;
        	// 取得任务参数
			taskParams.put(attribute.getName(), attribute.getValue());
        }
    }

	/**
	 * 将结果写到通道中反馈回去
	 *
	 * @param ctx
	 * @param httpResponseStatus
	 * @param returnMsg
	 */
    private void writeResponse(ChannelHandlerContext ctx, HttpResponseStatus httpResponseStatus, String returnMsg) {

    	StringBuffer resultStr = new StringBuffer();
    	resultStr.append(String.format(MsgConstant.SERVER_NODE_INFO, HostUtil.getHostInfo().getAddress()));
    	if(httpResponseStatus.code() == HttpResponseStatus.OK.code()) {
    		resultStr.append(String.format(MsgConstant.FILE_RECEIVE_SUCCESS, ctx.channel().remoteAddress().toString(),
							taskParams.get(BussinessConstant.FILE_KEY)));
    	} else if(httpResponseStatus.code() == HttpResponseStatus.INTERNAL_SERVER_ERROR.code()) {
    		resultStr.append(String.format(MsgConstant.REQUEST_RECEIVE_EXCEPTION,
					ctx.channel().remoteAddress().toString(), returnMsg ));
    	}
        //将请求响应的内容转换成ChannelBuffer.e
    	ByteBuf buf = Unpooled.copiedBuffer(resultStr.toString(), CharsetUtil.UTF_8);


    	//构建请求响应对象
    	FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, httpResponseStatus);
        response.headers().set(CONTENT_TYPE, "text/html; charset=UTF-8");
        //若该请求响应是最后的响应，则在响应头中没有必要添加'Content-Length'
        response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, buf.readableBytes());

        response.content().writeBytes(buf);
        buf.release();

        // Close the connection as soon as the error message is sent.
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        logger.info(resultStr.toString());
		logger.info("文件来源客户端信息：{}", new Object[]{ ctx.channel().remoteAddress()});
    }

	/**
	 * 文件完整性校验
	 *
	 * @param ctx			通道容器
	 * @param taskParams	参数信息
	 */
	private String fileIntegrityCheck(ChannelHandlerContext ctx, Map<String, Object> taskParams) throws Exception {

		// 返回结果
		String result = null;

		// 取得源文件MD5
		String md5Source = taskParams.get(BussinessConstant.FILE_MD5).toString();
		// 如果MD5不存在
		if (Strings.isNullOrEmpty(md5Source)) {
//			writeResponse(ctx, HttpResponseStatus.PRECONDITION_FAILED, "文件MD5参数不能为空！");
			result = "FILE'S MD5 IS NOT EMPTY!";
			return result;
		}
		// 取得文件的MD5
		String filePath = taskParams.get(FILE_PATH).toString();
		ByteSource byteSource = Files.asByteSource(new File(filePath));
		String md5needCheck = Hashing.md5().hashBytes(byteSource.read()).toString();
		// 如果文件MD5不一致
		if (!Objects.equal(md5Source, md5needCheck)) {
//			writeResponse(ctx, HttpResponseStatus.PRECONDITION_FAILED, "文件完整性检查失败，两次MD5不一致！");
			result = "File integrity check failed, twice MD5 inconsistent!";
			return result;
		}

		return result;
	}

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		// 错误信息
		String message = String.format(MsgConstant.REQUEST_RECEIVE_EXCEPTION,
				ctx.channel().remoteAddress(), cause.getMessage());

		// 如果是文件过大的异常
		if (cause instanceof TooLongFrameException) {
			message += MsgConstant.FILE_RECEIVE_SIZE_OUTMAX;
		}

		// 发送邮件通知管理员
		noticeUtil.sendNotice(MsgConstant.RECEIVE_EXCEPTION_NOTICE_TITLE, message);

		// 发送回馈信息给客户端
    	writeResponse(ctx, HttpResponseStatus.INTERNAL_SERVER_ERROR, message);
		ctx.channel().close();

		logger.error(message, cause);
    }

}
