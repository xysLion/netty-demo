package com.ancun.task.utils;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * HmacSHA1加密工具类。
 *
 * @Created on 2015-02-11
 * @author xieyushi
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class HmacSha1Util {

    private static final String ALGORITHM = "HmacSHA1";

    public static byte[] sign(String data, String key, String charsetName)
            throws NoSuchAlgorithmException, InvalidKeyException,
            IllegalStateException, UnsupportedEncodingException {

        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(charsetName),
                ALGORITHM);
        Mac mac = Mac.getInstance(ALGORITHM);
        mac.init(signingKey);

        return mac.doFinal(data.getBytes(charsetName));
    }

    public static String signToString(String data, String key,
                                      String charsetName) throws InvalidKeyException,
            NoSuchAlgorithmException, IllegalStateException,
            UnsupportedEncodingException {

        return StringUtil.trimToEmpty(Base64.encodeBase64String(sign(data, key,
                charsetName)));
    }

}
