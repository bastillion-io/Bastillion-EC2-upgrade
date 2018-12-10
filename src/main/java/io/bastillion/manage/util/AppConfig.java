/**
 * Copyright 2017 Sean Kavanagh - sean.p.kavanagh6@gmail.com
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.bastillion.manage.util;

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
			//iterate through map to replace text
			Set<String> keySet = replacementMap.keySet();
			for (String key : keySet) {
				//replace values in string
				String rVal = replacementMap.get(key);
				value = value.replace("${" + key + "}", rVal);
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
			} catch (Exception ex) {
				ex.printStackTrace();
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
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}

	}

	static {
		try {
			prop = new PropertiesConfiguration(DBUtils.DB_PATH + "BastillionConfig.properties");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}
