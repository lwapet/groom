package fr.groom.mongo;

import com.mongodb.*;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import com.mongodb.client.model.UpdateOptions;
import fr.groom.Storage;
import org.bson.Document;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.util.Arrays;

public class Database implements Storage {
	MongoClient mongoClient;
	MongoDatabase mongoDatabase;
	public static int MONGO_DOCUMENT_SIZE_LIMIT = 16793600;


	public Database(String url, int port, String databaseName, boolean auth, String username, String password, String authSource) {
		MongoCredential mongoCredential = MongoCredential.createCredential(username, authSource, password.toCharArray());
		ServerAddress serverAddress = new ServerAddress(url, port);

		if (auth) {
			this.mongoClient = MongoClients.create(
					MongoClientSettings.builder()
							.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(serverAddress)))
							.credential(mongoCredential)
							.build()
			);
		} else {
			this.mongoClient = MongoClients.create(
					MongoClientSettings.builder()
							.applyToClusterSettings(builder -> builder.hosts(Arrays.asList(serverAddress)))
							.build()
			);
		}

		this.mongoDatabase = this.mongoClient.getDatabase(databaseName);
	}

	public MongoDatabase getDatabase() {
		return mongoDatabase;
	}

	//	public Database(DatabaseConfiguration fr.groom.configuration) {
//		super();
//		boolean auth = fr.groom.configuration.getAuthenticationConfiguration().isPerformAuthentication();
//		databaseConnection.configure(
//				fr.groom.configuration.getUrl(),
//				fr.groom.configuration.getPort(),
//				fr.groom.configuration.getName(),
//				auth ? fr.groom.configuration.getAuthenticationConfiguration().getUsername() : null,
//				auth ? fr.groom.configuration.getAuthenticationConfiguration().getPassword() : null,
//				auth ? fr.groom.configuration.getAuthenticationConfiguration().getAuthSourceDatabaseName() : null
//		);
//		databaseConnection.connection();
//	}

	public void close() {
		this.mongoClient.close();
	}

	@Override
	public void insertData(JSONObject analysisData, String collectionName) {
		this.mongoDatabase.getCollection(collectionName).insertOne(Document.parse(analysisData.toString()));
	}


	@Override
	public void update(JSONObject conditions, JSONObject update, String collectionName) {
		try {
			UpdateOptions options = new UpdateOptions();
			options.upsert(true);
//			JSONObject set = new JSONObject();
//			set.put("$set", update);
			BasicDBObject set = new BasicDBObject();
			set.append("$set", BasicDBObject.parse(update.toString()));
			this.mongoDatabase.getCollection(collectionName).updateOne(Document.parse(conditions.toString()), set, options);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

//	@Override
//	public void replace(JSONObject conditions, JSONObject update, String collectionName) {
//		try {
//			FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
//			options.upsert(true);
//			this.mongoDatabase.getCollection(collectionName).findOneAndReplace(Document.parse(conditions.toString()), Document.parse(update.toString()), options);
//		} catch (MongoException e) {
//			e.printStackTrace();
//		}
//	}
}
