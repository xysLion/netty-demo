/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ancun.boot.encrypt;

import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;
import org.springframework.security.rsa.crypto.KeyStoreKeyFactory;
import org.springframework.security.rsa.crypto.RsaSecretEncryptor;
import org.springframework.util.StringUtils;

/**
 * @author Dave Syer
 *
 */
public class EncryptorFactory {

    public static TextEncryptor create(KeyProperties properties) {

        TextEncryptor encryptor;

        KeyProperties.KeyStore keyStore = properties.getKeyStore();

        String data = properties.getKey();

        if (keyStore.getLocation() != null && keyStore.getLocation().exists()) {
            encryptor = new RsaSecretEncryptor(
                    new KeyStoreKeyFactory(keyStore.getLocation(),
                            keyStore.getPassword().toCharArray()).getKeyPair(
                            keyStore.getAlias(),
                            keyStore.getSecret().toCharArray()),
                    properties.getRsa().getAlgorithm(), properties.getRsa().getSalt(),
                    properties.getRsa().isStrong());
        }
        else if (data.contains("RSA PRIVATE KEY")) {

            try {
                encryptor = new RsaSecretEncryptor(data);
            }
            catch (IllegalArgumentException e) {
                throw new KeyFormatException();
            }

        }
        else if (data.startsWith("ssh-rsa") || data.contains("RSA PUBLIC KEY")) {
            throw new KeyFormatException();
        }
        else if (StringUtils.hasText(data)) {
            encryptor = Encryptors.text(data, properties.getSalt());
        }
        else {
            encryptor = new FailsafeTextEncryptor();
        }

        return encryptor;
    }

    /**
     * TextEncryptor that just fails, so that users don't get a false sense of security
     * adding ciphers to config files and not getting them decrypted.
     *
     * @author Dave Syer
     *
     */
    protected static class FailsafeTextEncryptor implements TextEncryptor {

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

}
