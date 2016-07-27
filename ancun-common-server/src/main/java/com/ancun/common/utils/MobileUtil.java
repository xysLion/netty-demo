package com.ancun.common.utils;

import com.ancun.common.component.BasicDataCache;
import com.ancun.common.constant.PhoneCorpEnum;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Resource;

import static com.ancun.common.constant.Constants.CHINA_MOBILE_REGEX;
import static com.ancun.common.constant.Constants.CHINA_TELECOM_REGEX;
import static com.ancun.common.constant.Constants.CHINA_UNICOM_REGEX;
import static com.ancun.common.constant.Constants.MOBILE_REGEX;
import static com.ancun.common.constant.Constants.SPECIAL_CHINA_TELECOM;
import static com.ancun.common.constant.Constants.TELEPHONE_NO_HYPHEN_REGEX;
import static com.ancun.common.constant.Constants.TELEPHONE_REGEX;

/**
 * 手机号码鉴权工具类
 * MobileUtil.java
 * @Description:
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2014
 * @Company:杭州安存网络科技有限公司
 * @Created 2014-11-11 下午3:47:19
 * @author hechuan
 * @version 1.0
 */
@Component
public final class MobileUtil {

	/** 缓存 */
	@Resource
	private BasicDataCache cache;

	/**
	 * 是否为中国移动
	 * 134(1349除外），135，136，137，138，139，147，150，151，152，157，158，159，182，183，184，187，188
	 *
	 * @param mobile 手机号码
	 * @return		true(是)/false(否)
	 */
	public boolean isChinaMobile(String mobile) {

//    	String regex = "^1(3[4-9]|4[7]|5[012789]|8[23478])\\d{8}$";
		String regex = cache.config(CHINA_MOBILE_REGEX);

    	return checkMobile(mobile, regex) && !isSpecialChinaTelecom(mobile);
	}
	
	/**
	 * 是否为中国联通
	 * 130、131、132、155、156、185、186、145
	 *
	 * @param mobile 手机号码
	 * @return		true(是)/false(否)
	 */
	public boolean isChinaUnicom(String mobile) {

//    	String regex = "^1(3[012]|4[5]|5[56]|8[56])\\d{8}$";
		String regex = cache.config(CHINA_UNICOM_REGEX);

    	return checkMobile(mobile, regex);
		
	}
	
	/**
	 * 是否为中国电信
	 * 133、153、177、180、181、189
	 *
	 * @param mobile 手机号码
	 * @return		true(是)/false(否)
	 */
	public boolean isChinaTelecom(String mobile) {

//    	String regex = "^1(3[3]|5[3]|7[7]|8[019])\\d{8}$";
		String regex = cache.config(CHINA_TELECOM_REGEX);

    	return checkMobile(mobile, regex) || isSpecialChinaTelecom(mobile);
		
	}

	/**
	 * 是否是手机号码
	 *
	 * @param mobile 手机号码
	 * @return		true(是)/false(否)
	 */
	public boolean isMobile(String mobile) {

//		String regex = "^((13[0-9])|(177)|(15[^4,\\D])|(18[0-9]))\\d{8}$";
		String regex = cache.config(MOBILE_REGEX);

		return checkMobile(mobile, regex);
	}

	/**
	 * 判断是否为指定正则表达式的手机号码
	 *
	 * @param mobile	手机号码
	 * @param regex		正则表达式
     * @return			true是/false否
     */
	private boolean checkMobile(String mobile, String regex) {
		mobile = StringUtil.nullToStr(mobile);
		if(!StringUtil.isHalfAngle(mobile)) {
			return false;
		}

		if(mobile.length() != 11) {
			return false;
		}

		return mobile.matches(regex);
	}

	/**
	 * 是否是特殊的电信号
	 * @param mobile:1349开头
	 * @return true(是)/false(否)
	 */
	private boolean isSpecialChinaTelecom(String mobile) {
		return mobile.startsWith(cache.config(SPECIAL_CHINA_TELECOM));
	}

	/**
	 * 根据号码取得运营商名称
	 *
	 * @param mobile 手机号码
	 * @return		运营商名称
	 */
	public String getPhoneCrop(String mobile) {
		// 中国移动
		if (isChinaMobile(mobile)) {
			return PhoneCorpEnum.CHINAMOBILE.getText();
		}
		// 中国联通
		else if (isChinaUnicom(mobile)) {
			return PhoneCorpEnum.CHINAUNICOM.getText();
		}

		// 剩下的全部为中国电信
		return PhoneCorpEnum.CHINATELECOM.getText();
	}

	/**
	 * 是否固定电话
	 *
	 * @param phone 电话号码
	 * @return		true(是)/false(否)
	 */
	public boolean isTelephone(String phone) {

//		String regex = "(0\\d{2,3}-?\\d{7,8})$";
		String regex = cache.config(TELEPHONE_REGEX);

		return phone.matches(regex);
	}

	/**
	 * 是否固定电话(不加连接符号)
	 *
	 * @param phone 电话号码
	 * @return		true(是)/false(否)
	 */
	public boolean isTelephoneWithoutHyphen(String phone) {
//		String regex = "(0\\d{10,11})$";
		String regex = cache.config(TELEPHONE_NO_HYPHEN_REGEX);

		return phone.matches(regex);
	}

	/**
	 * 是否电话号码，可能为以下几种
	 * 1.手机号码
	 * 2.固定电话
	 * 3.固定电话(不加连接符号)
	 *
	 * @param phone 电话号码
	 * @return		true(是)/false(否)
	 */
	public boolean isPhone(String phone) {
		return isMobile(phone) || isTelephone(phone);
	}

	/**
	 * 获取固定号码号码中的区号
	 *
	 * @param strNumber 电话号码
	 * @return	区号
	 */
	public String getAreaCodeFromPhone(String strNumber) {
		//用于获取固定电话中的区号
		String regexZipCode = "^(010|02\\d|0[3-9]\\d{2})\\d{6,8}$";
		Pattern  patternZipcode = Pattern.compile(regexZipCode);
		Matcher matcher = patternZipcode.matcher(strNumber);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}
	
	public static void main(String[] args) {

		MobileUtil mobileUtil = new MobileUtil();

		// 13958008396
		// 13067892956
		// 13355817707
		// 13492323123
		// 18523231333
		String mobile = " 13221002236";
		boolean chinaMobile = mobileUtil.isChinaMobile(mobile);
		boolean chinaUnicom = mobileUtil.isChinaUnicom(mobile);
		boolean chinaTelecom = mobileUtil.isChinaTelecom(mobile);
		boolean chinaTelephone = mobileUtil.isTelephone(mobile);
		boolean chinaTelephoneWithout = mobileUtil.isTelephoneWithoutHyphen(mobile);
		boolean chinaPhone = mobileUtil.isPhone(mobile);
		System.out.println("移动：" + chinaMobile +
				" ,联通：" + chinaUnicom +
				" ,电信：" + chinaTelecom +
				" ,固话：" + chinaTelephone +
				" ,固话(无)：" + chinaTelephoneWithout +
				" ,号码：" + chinaPhone
		);
	}
}
