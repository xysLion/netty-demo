package com.ancun.boot.encrypt;

import org.springframework.security.crypto.encrypt.TextEncryptor;

/**
 *
 *
 * @author 摇光
 * @version 1.0
 * @Created on 2016/8/24
 * @Copyright 杭州安存网络科技有限公司 Copyright (c) 2016
 */
public class FailsafeTextEncryptor implements TextEncryptor {

    @Override
    public String encrypt(String text) {
        throw new UnsupportedOperationException(
                "No encryption for FailsafeTextEncryptor. Did you configure the keystore correctly?");
    }

    @Override
    public String decrypt(String encryptedText) {
        throw new UnsupportedOperationException(
                "No decryption for FailsafeTextEncryptor. Did you configure the keystore correctly?");
    }

}