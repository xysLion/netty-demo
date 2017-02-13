package com.ancun.common.constant;

import com.google.common.collect.Maps;

import java.util.Map;

/**
 * 返回信息编号
 *
 * @Created on 2015年3月17日
 * @author tom.tang
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ResponseConst {

	/** 成功 */
	public static final int SUCCESS = 100000;

	/** 系统异常 */
	public static final int SYSTEM_EXCEPTION = 100001;

	/** 短信发送失败 */
	public static final int SMS_SEND_FAILURE = 100002;

	/** 邮件发送失败 */
	public static final int EMAIL_SEND_FAILURE = 100003;

	/** 归属地信息为空 */
	public static final int REGION_AREA_INFO_EMPTY = 100004;

	/**　访问IP被拒绝　*/
	public static final int IP_DENIED = 100005;

	/**　签名不匹配　*/
	public static final int SIGN_NAME_NOT_MATCH = 100006;

	/**　访问接口被拒绝　*/
	public static final int INTERFACE_DENIED = 100007;

	/** 功能不能为空 */
	public static final int ACTION_NOT_NULL = 200001;

	/** 号码不能为空 */
	public static final int PARAMS_PHONENO_NOT_EMPTY = 310000;

	/** 内容不能为空 */
	public static final int PARAMS_MSG_NOT_EMPTY = 310001;

	/** 接收人邮件地址不能为空 */
	public static final int PARAMS_EMAILTO_NOT_EMPTY = 310002;

	/** 号码之间分隔符必须为英文逗号! */
	public static final int PARAMS_PHONENO_FILTER_NOT_COMMA = 310003;

	/** 号码格式不正! */
	public static final int PARAMS_PHONENO_IS_ERROR = 310004;

	/** 邮件格式不正! */
	public static final int PARAMS_EMAIL_IS_ERROR = 310005;

	/** 碟信或者至臻通道回馈信息开始code */
	public static final int SMS_DXZZ_RETURN_START_CODE = 311000;

	/** 创蓝通道回馈信息开始code */
	public static final int SMS_CHUANGLAN_RETURN_START_CODE = 312000;

	/** 短信状态报告对应 */
	public static final Map<String, Integer> SMS_STATUS = Maps.newHashMap();
	static {
		SMS_STATUS.put("DELIVRD", 313000);
		SMS_STATUS.put("EXPIRED", 313001);
		SMS_STATUS.put("UNDELIV", 313002);
		SMS_STATUS.put("UNKNOWN", 313003);
		SMS_STATUS.put("REJECTD", 313004);
		SMS_STATUS.put("DTBLACK", 313005);
		SMS_STATUS.put("ERR:104", 313006);
		SMS_STATUS.put("REJECT", 313007);
		SMS_STATUS.put("其他", 313008);
		SMS_STATUS.put("发送对象不能为空", 314000);
		SMS_STATUS.put("发送对象格式不正确，只能为数字和','，号码之间用','隔开", 314001);
		SMS_STATUS.put("扩展号格式不正确，只能为数字且不超过五位数", 314002);
		SMS_STATUS.put("用户名密码不符合条件", 314003);
		SMS_STATUS.put("字数超出最大限制", 314004);
		SMS_STATUS.put("短信内容有敏感词，禁止发送", 314005);
		SMS_STATUS.put("剩余可发短信条数不足", 314006);
		SMS_STATUS.put("网关流量不足，请联系相关管理人员", 314007);
		SMS_STATUS.put("请等待审核", 314008);
	}
}