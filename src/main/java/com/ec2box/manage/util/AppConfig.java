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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang3.StringUtils;

public class AppConfig {
    public static PropertiesConfiguration prop;

    private AppConfig() {
    }

    public static String getProperty(String name) {
        return prop.getString(name);
    }

    public static String getProperty(String name, Map<String, String> replacementMap) {
        String value = prop.getString(name);
        if (StringUtils.isNotEmpty(value)) {
            Set<String> keySet = replacementMap.keySet();

            String key;
            String rVal;
            for(Iterator var4 = keySet.iterator(); var4.hasNext(); value = value.replace("${" + key + "}", rVal)) {
                key = (String)var4.next();
                rVal = (String)replacementMap.get(key);
            }
        }

        return value;
    }

    public static void removeProperty(String name) {
        try {
            prop.clearProperty(name);
            prop.save();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public static void updateProperty(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            try {
                prop.setProperty(name, value);
                prop.save();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    public static boolean isPropertyEncrypted(String name) {
        String property = prop.getString(name);
        return StringUtils.isNotEmpty(property) ? property.matches("^AES\\{.*\\}$") : false;
    }

    public static String decryptProperty(String name) {
        String retVal = prop.getString(name);
        if (StringUtils.isNotEmpty(retVal)) {
            retVal = retVal.replaceAll("^AES\\{", "").replaceAll("\\}$", "");
            retVal = EncryptionUtil.decrypt(retVal);
        }

        return retVal;
    }

    public static void encryptProperty(String name, String value) {
        if (StringUtils.isNotEmpty(value)) {
            try {
                prop.setProperty(name, "AES{" + EncryptionUtil.encrypt(value) + "}");
                prop.save();
            } catch (Exception var3) {
                var3.printStackTrace();
            }
        }

    }

    static {
        try {
            prop = new PropertiesConfiguration(DBUtils.DB_PATH + "/../EC2BoxConfig.properties");
        } catch (Exception var1) {
            var1.printStackTrace();
        }

    }
}
