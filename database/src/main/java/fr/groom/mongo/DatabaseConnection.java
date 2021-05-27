package fr.groom.mongo;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientURI;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoDatabase;

public class DatabaseConnection {
	boolean authSource;
	String authSourceDatabaseName;
	private String url;
	private MongoClient mongoClient;
	private MongoDatabase database;
	private String name;
	private int port;
	private String user;
	private String password;

	public MongoDatabase getDatabase() {
		return database;
	}

	public void connection() {
		ServerAddress address = new ServerAddress(url, port);
		MongoClientOptions options = MongoClientOptions.builder().serverSelectionTimeout(5000).build();
		if (this.user == null || this.user.isEmpty() || this.password == null || this.password.isEmpty()) {
			System.out.println("try unauthenticated connection");
			mongoClient = new MongoClient(address, options);
		} else {
			StringBuilder mongoUri = new StringBuilder();
			mongoUri.append("mongodb://");
			mongoUri.append(this.user);
			mongoUri.append(":");
			mongoUri.append(this.password);
			mongoUri.append("@");
			mongoUri.append(this.url);
			mongoUri.append(":");
			mongoUri.append(this.port);
			mongoUri.append("/");
			mongoUri.append(this.name);

			if (this.authSource) {
				mongoUri.append("?authSource=");
				mongoUri.append(this.authSourceDatabaseName);
			}

			MongoClientURI uri = new MongoClientURI(mongoUri.toString());

			mongoClient = new MongoClient(uri);
		}
		this.database = mongoClient.getDatabase(this.name);
	}

	public void close() {
		mongoClient.close();
	}


	public void configure(String url, int port, String name, String user, String password, String authSourceDatabaseName) {
		this.url = url;
		this.port = port;
		this.name = name;
		this.user = user;
		this.password = password;
		if (authSourceDatabaseName == null || authSourceDatabaseName.equals("null")) {
			this.authSource = false;
		} else {
			this.authSource = true;
			this.authSourceDatabaseName = authSourceDatabaseName;
		}
	}
}
