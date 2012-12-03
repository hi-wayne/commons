package com.hiwayne.commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Config {
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	// map 存储配置信息
	private static Map<String, String> setting = new ConcurrentHashMap<String, String>();
	// 权限map
	private static Map<String, String> authorizationMap = new ConcurrentHashMap<String, String>(
			20);
	// 调用初始化操作
	static {
		iniSetting();
	}

	public Config() {
	}

	/**
	 * 初始化加载配置文件 默认加载配置路径config/config.properties或者config.properties
	 * 
	 * @throws FileNotFoundException
	 */
	public static synchronized void iniSetting() {
		File file;
		file = new File("config.properties");
		if (!file.exists()) {
			iniSetting("config/config.properties");
		} else {
			iniSetting("config.properties");
		}
	}

	/**
	 * 初始化加载配置文件
	 * 
	 * @param path
	 *            加载路径
	 * @throws FileNotFoundException
	 */
	public static synchronized void iniSetting(String path) {
		File file;
		file = new File(path);
		FileInputStream in = null;
		try {
			in = new FileInputStream(file);
			Properties p = new Properties();
			p.load(in);
			// 遍历配置文件加入到Map中进行缓存
			Enumeration<?> item = p.propertyNames();
			while (item.hasMoreElements()) {
				String key = (String) item.nextElement();
				setting.put(key, p.getProperty(key));
			}
			in.close();
		} catch (FileNotFoundException e) {
			logger.error("config file not found at" + file.getAbsolutePath());
			throw new ConfigException("FileNotFoundException", e);
		} catch (IOException e) {
			logger.error("config file not found at" + file.getAbsolutePath());
			throw new ConfigException("IOException", e);
		} catch (Exception e) {
			throw new ConfigException("Exception", e);
		}
	}

	public static void reload() {
		try {
			iniSetting();
		} catch (ConfigException e) {
			throw new ConfigException(e.getMessage(), e);
		}
	}

	public static void reloadAuthorization() {
		Config.reload();
		loadAuthorization();
	}

	public static void loadAuthorization() {
		String line = Config.getSetting("authorization");
		if (line != null) {
			if (line.indexOf("_") != -1) {
				logger.error("权限配置不能包含'_'符号");
			}
			String[] group = line.split(",");
			for (int i = 0, groupLen = group.length; i < groupLen; i++) {
				String[] item = group[i].split("\\|");
				if (item.length == 2) {
					authorizationMap.put(item[0], item[1]);
				}
			}
		}
	}

	public static boolean valid(String appid, String pwd) {
		return pwd.equals(authorizationMap.get(appid));
	}

	/**
	 * 获取配置文件的某个键值的配置信息
	 * 
	 * @param key
	 *            键
	 * @return 值
	 */
	public static String getSetting(String key) {
		return setting.get(key);
	}

	/**
	 * 设置配置文件的数据
	 * 
	 * @param key
	 * @param value
	 */
	public static void setSetting(String key, String value) {
		setting.put(key, value);
	}

	public static int getInt(String key) {
		return Integer.parseInt(setting.get(key));
	}

	public static long getLong(String key) {
		return Long.parseLong(setting.get(key));
	}

	public static boolean getBoolean(String key) {
		Boolean aBoolean = new Boolean(setting.get(key));
		return aBoolean.booleanValue();
	}
}