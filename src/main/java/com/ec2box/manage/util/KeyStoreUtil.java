/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ec2box.manage.util;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStore.PasswordProtection;
import java.security.KeyStore.SecretKeyEntry;
import javax.crypto.spec.SecretKeySpec;

public class KeyStoreUtil {
    private static KeyStore keyStore = null;
    private static final String keyStoreFile;
    private static final char[] KEYSTORE_PASS;
    private static final byte[] key;
    public static final String ENCRYPTION_KEY_ALIAS = "EC2BOX-ENCRYPTION_KEY";

    public KeyStoreUtil() {
    }

    public static byte[] getSecretBytes(String alias) {
        byte[] value = null;

        try {
            SecretKeyEntry entry = (SecretKeyEntry)keyStore.getEntry(alias, new PasswordProtection(KEYSTORE_PASS));
            value = entry.getSecretKey().getEncoded();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    public static String getSecretString(String alias) {
        String value = null;

        try {
            SecretKeyEntry entry = (SecretKeyEntry)keyStore.getEntry(alias, new PasswordProtection(KEYSTORE_PASS));
            value = new String(entry.getSecretKey().getEncoded());
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }

    public static void setSecret(String alias, byte[] secret) {
        PasswordProtection protectionParameter = new PasswordProtection(KEYSTORE_PASS);

        try {
            SecretKeySpec secretKey = new SecretKeySpec(secret, 0, secret.length, "AES");
            SecretKeyEntry secretKeyEntry = new SecretKeyEntry(secretKey);
            keyStore.setEntry(alias, secretKeyEntry, protectionParameter);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public static void setSecret(String alias, String secret) {
        setSecret(alias, secret.getBytes());
    }

    public static void initializeKeyStore() {
        try {
            keyStore = KeyStore.getInstance("JCEKS");
            keyStore.load((InputStream)null, KEYSTORE_PASS);
            setSecret("EC2BOX-ENCRYPTION_KEY", key);
            FileOutputStream fos = new FileOutputStream(keyStoreFile);
            keyStore.store(fos, KEYSTORE_PASS);
            fos.close();
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }

    static {
        keyStoreFile = DBUtils.DB_PATH + "/../ec2box.jceks";
        KEYSTORE_PASS = new char[]{'G', '~', 'r', 'x', 'Z', 'E', 'w', 'f', 'a', '[', '!', 'f', 'Z', 'd', '*', 'L', '8', 'm', 'h', 'u', '#', 'j', '9', ':', '~', ';', 'U', '>', 'O', 'i', '8', 'r', 'C', '}', 'f', 't', '%', '[', 'H', 'h', 'M', '&', 'K', ':', 'l', '5', 'c', 'H', '6', 'r', 'A', 'E', '.', 'F', 'Y', 'W', '}', '{', '*', '8', 'd', 'E', 'C', 'A', '6', 'F', 'm', 'j', 'u', 'A', 'Q', '%', '{', '/', '@', 'm', '&', '5', 'S', 'q', '4', 'Q', '+', 'Y', '|', 'X', 'W', 'z', '8', '<', 'j', 'd', 'a', '}', '`', '0', 'N', 'B', '3', 'i', 'v', '5', 'U', ' ', '2', 'd', 'd', '(', '&', 'J', '_', '9', 'o', '(', '2', 'I', '`', ';', '>', '#', '$', 'X', 'j', '&', '&', '%', '>', '#', '7', 'q', '>', ')', 'L', 'A', 'v', 'h', 'j', 'i', '8', '~', ')', 'a', '~', 'W', '/', 'l', 'H', 'L', 'R', '+', '\\', 'i', 'R', '_', '+', 'y', 's', '0', 'n', '\'', '=', '{', 'B', ':', 'l', '1', '%', '^', 'd', 'n', 'H', 'X', 'B', '$', 'f', '"', '#', ')', '{', 'L', '/', 'q', '\'', 'O', '%', 's', 'M', 'Q', ']', 'D', 'v', ';', 'L', 'C', 'd', '?', 'D', 'l', 'h', 'd', 'i', 'N', '4', 'R', '>', 'O', ';', '$', '(', '4', '-', '0', '^', 'Y', ')', '5', 'V', 'M', '7', 'S', 'a', 'c', 'D', 'C', 'w', 'A', 'o', 'n', 's', 'r', '*', 'G', '[', 'l', 'h', '$', 'U', 's', '_', 'D', 'f', 'X', '~', '.', '7', 'B', 'A', 'E', '(', '#', ']', ':', '`', ',', 'k', 'y'};
        key = new byte[]{'t', '3', '2', 'm', 'p', 'd', 'M', 'O', 'i', '8', 'x', 'z', 'a', 'P', 'o', 'd'};
    }
}
