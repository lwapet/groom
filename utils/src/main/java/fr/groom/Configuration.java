package fr.groom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configuration {
	private static Configuration INSTANCE;
	public String adbPath;

	private Configuration() {
		Properties prop = new Properties();
		InputStream input = Configuration.class.getClassLoader().getResourceAsStream("config.properties");
		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		adbPath = prop.getProperty("adb_path");
	}

	public static Configuration v() {
		if(INSTANCE == null) {
			INSTANCE = new Configuration();
		}
		return INSTANCE;
	}


}
