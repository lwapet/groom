package fr.groom;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TcpServerConfiguration {
	private static TcpServerConfiguration INSTANCE;
	public String databaseUrl;
	public int databasePort;
	public String databaseName;
	public boolean performAuth;
	public String username;
	public String password;
	public String authSourceDatabaseName;

	private TcpServerConfiguration() {
		Properties prop = new Properties();
		InputStream input = TcpServerConfiguration.class.getClassLoader().getResourceAsStream("tcp-config.properties");
		try {
			prop.load(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		databaseUrl = prop.getProperty("database_url");
		databasePort = Integer.parseInt(prop.getProperty("port"));
		databaseName = prop.getProperty("database_name");
		performAuth = Boolean.parseBoolean(prop.getProperty("perform_auth"));
		username = prop.getProperty("username");
		password = prop.getProperty("password");
		authSourceDatabaseName = prop.getProperty("auth_source_database_name");
	}

	public static TcpServerConfiguration v() {
		if(INSTANCE == null) {
			INSTANCE = new TcpServerConfiguration();
		}
		return INSTANCE;
	}


}
