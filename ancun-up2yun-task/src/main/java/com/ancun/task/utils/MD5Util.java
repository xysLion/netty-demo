package com.ancun.task.utils;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密工具类。
 *
 * @Created on 2015-02-11
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class MD5Util {

    private static final Logger log = LoggerFactory.getLogger(MD5Util.class);
    private static final int BUFFERSIZE = 8196;
    private static final String ALGORITHM = "MD5";
    private static final String CHARSET = "UTF-8";

    private static String md5(String input, String charsetName) {
        try {
            MessageDigest md5 = MessageDigest.getInstance(ALGORITHM);
            byte md5Bytes[] = md5.digest(input.getBytes(charsetName));
            return StringUtil.byte2hex(md5Bytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String md5(String input) {
        return md5(input, CHARSET);
    }

    public static String md5reverse(String md5) {
		return "";
	}

	public static String md5(InputStream inputStream) throws Exception {

		BufferedInputStream bufferedInputStream = null;
		MessageDigest md = null;

		try {
			byte[] buffer = new byte[BUFFERSIZE];
			int i = 0;

			bufferedInputStream = new BufferedInputStream(inputStream, BUFFERSIZE);
			md = MessageDigest.getInstance(ALGORITHM);

			while ((i = bufferedInputStream.read(buffer)) != -1) { // >0
				md.update(buffer, 0, i);
			}

			buffer = null;

			byte[] md5Bytes = md.digest();
			return StringUtil.byte2hex(md5Bytes);
		} catch (Exception e) {
			throw new RuntimeException(e);
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
				bufferedInputStream = null;
			}
		}
	}
	/**
	 * csj 2014-06-18 备案规则
	 */
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5",
		"6", "7", "8", "9", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l",
		"m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"};

	public static String byteArrayToHexString(byte[] b) {
		StringBuffer resultSb = new StringBuffer();
		log.debug(" byteLen:"+ (null == b ? 0 : b.length));
		for (int i = 0; i < b.length; i++) {
			log.debug("byteToHexString(b["+i+"]):"+byteToHexString(b[i]));
			resultSb.append(byteToHexString(b[i]));
		}
		return resultSb.toString();
	}

	public static String encode(String origin) {
		String resultString = null;
		try {
			resultString = new String(origin);
			MessageDigest md = MessageDigest.getInstance("MD5");
			log.debug("resultString.getBytes():"+resultString.getBytes());
			log.debug("md.digest(resultString.getBytes()):"+md.digest(resultString.getBytes()));
			resultString = byteArrayToHexString(md.digest(resultString
					.getBytes()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return resultString;
	}

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 36;
		int d2 = n % 36;
		int d = d1+d2;
		if(d>=36){
			d = d%36;
		}
		log.debug("byteToHexString.d:"+d);
		//return hexDigits[d1] + hexDigits[d2];
		return hexDigits[d];
	}

	public static String generateCommonMD5(String result){
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] by = md5.digest(result.getBytes());
			return  Hex.encodeHexString(by);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return null;
	}
}
