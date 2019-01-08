package fr.groom;

import java.util.Properties;

public class AVDConfiguration {
	public static String androidSdkHome;
	public static String adbPath;
	public static String emulatorPath;
	public static String pathToInstrumentedApkDirectory;
	public static int poolCount;
	public static int apk_quantity;
	public static String deviceName;
	public static String databaseUrl;
	public static int databasePort;
	public static String databaseName;
	public static boolean performAuth;
	public static String username;
	public static String password;
	public static String authSourceDatabaseName;


	public static void setConfig(Properties prop) {
		androidSdkHome = prop.getProperty("android_sdk_home");
		adbPath = prop.getProperty("adb_path");
		emulatorPath = prop.getProperty("path_to_emulator");
		pathToInstrumentedApkDirectory = prop.getProperty("path_to_instrumented_apk_directory");
		poolCount = Integer.parseInt(prop.getProperty("pool_count"));
		poolCount = Integer.parseInt(prop.getProperty("apk_quantity"));
		deviceName = prop.getProperty("device_name");
		databaseUrl = prop.getProperty("database_url");
		databasePort = Integer.parseInt(prop.getProperty("port"));
		databaseName = prop.getProperty("database_name");
		performAuth = Boolean.parseBoolean(prop.getProperty("perform_auth"));
		username = prop.getProperty("username");
		password = prop.getProperty("password");
		authSourceDatabaseName = prop.getProperty("auth_source_database_name");
	}
}
