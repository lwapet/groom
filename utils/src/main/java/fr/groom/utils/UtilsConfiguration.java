package fr.groom.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class UtilsConfiguration {
	private static UtilsConfiguration INSTANCE;
	public String adbPath;

	private UtilsConfiguration() {
		Properties prop = new Properties();
		InputStream input = UtilsConfiguration.class.getClassLoader().getResourceAsStream("config.properties");
		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		adbPath = prop.getProperty("adb_path");
	}

	public static UtilsConfiguration v() {
		if(INSTANCE == null) {
			INSTANCE = new UtilsConfiguration();
		}
		return INSTANCE;
	}


}
