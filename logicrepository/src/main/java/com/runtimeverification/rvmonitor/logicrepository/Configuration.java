package com.runtimeverification.rvmonitor.logicrepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	private static final String DATABASE_SETTING = "database.properties";
	private static Properties databaseSetting = null;

	public Configuration() {
	}

	public static String polishPath(String path) {
		if (path.indexOf("%20") > 0)
			path = path.replaceAll("%20", " ");

		return path;
	}

	public static String getConfigPath() {
		String configPath;
		try {
			Class<?> testClass = Class.forName("com.runtimeverification.rvmonitor.logicrepository.Configuration");
			ClassLoader loader = testClass.getClassLoader();
			String testClassPath = loader.getResource("com/runtimeverification/rvmonitor/logicrepository/Configuration.class").toString();

			if (testClassPath.endsWith(".jar!/com/runtimeverification/rvmonitor/logicrepository/Configuration.class") && testClassPath.startsWith("jar:")) {
				String jarFilePath;
				jarFilePath = testClassPath.substring("jar:file:".length(), testClassPath.length() - "!/com/runtimeverification/rvmonitor/logicrepository/Configuration.class".length());
				jarFilePath = polishPath(jarFilePath);

				configPath = new File(jarFilePath).getParentFile().getParent() + File.separator + "config";
			} else {
				String packageFilePath;

				packageFilePath = testClassPath.substring("file:".length(), testClassPath.length() - "/Configuration.class".length());
				packageFilePath = polishPath(packageFilePath);

				configPath = new File(packageFilePath).getParentFile().getParent() + File.separator + "config";
			}

			return configPath;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	static public Properties getSettingFile(String filename) {
		Properties ret = new Properties();

		String configPath = getConfigPath();
		File configDir = new File(configPath);

		if (!configDir.exists())
			return null;

		File configFile = new File(configPath + File.separator + filename);

		if (!configFile.exists())
			return null;

		try {
			ret.load(new FileInputStream(configFile.getAbsolutePath()));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return ret;
	}

	static public String getDatabaseSetting(String key) {
		if (databaseSetting == null) {
			databaseSetting = getSettingFile(DATABASE_SETTING);
		}

		if (databaseSetting != null) {
			return databaseSetting.getProperty(key);
		}
		return null;
	}

	static public boolean isStatisticsOn() {
		String ret = getDatabaseSetting("Statistics");

		if (ret != null && ret.toLowerCase().compareTo("on") == 0)
			return true;
		else
			return false;
	}

	static public String getServerName() {
		return getDatabaseSetting("ServerName");
	}

	static public String getDatabaseName() {
		return getDatabaseSetting("DatabaseName");
	}

	static public String getID() {
		return getDatabaseSetting("ID");
	}

	static public String getPassword() {
		return getDatabaseSetting("Password");
	}

}
