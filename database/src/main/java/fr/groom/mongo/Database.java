package fr.groom.mongo;

import com.mongodb.MongoException;
import com.mongodb.client.model.FindOneAndReplaceOptions;
import com.mongodb.client.model.FindOneAndUpdateOptions;
import fr.groom.Storage;
import org.bson.Document;
import org.json.JSONObject;

import java.io.DataOutputStream;

public class Database implements Storage {
	DatabaseConnection databaseConnection = new DatabaseConnection();
	public static int MONGO_DOCUMENT_SIZE_LIMIT = 16793600;


	public Database(String url, int port, String databaseName, boolean auth, String username, String password, String authSource) {
		databaseConnection.configure(
				url,
				port,
				databaseName,
				auth ? username : null,
				auth ? password : null,
				auth ? authSource : null
		);
		databaseConnection.connection();
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

	public DatabaseConnection getDatabaseConnection() {
		return databaseConnection;
	}

	public void close() {
		databaseConnection.close();
	}

	@Override
	public void insertData(JSONObject analysisData, String collectionName) {
		this.getDatabaseConnection().getDatabase().getCollection(collectionName).insertOne(Document.parse(analysisData.toString()));
	}


	@Override
	public void update(JSONObject conditions, JSONObject update, String collectionName) {
		try {
			FindOneAndUpdateOptions options = new FindOneAndUpdateOptions();
			options.upsert(true);
			databaseConnection.getDatabase().getCollection(collectionName).findOneAndUpdate(Document.parse(conditions.toString()), Document.parse(update.toString()), options);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void replace(JSONObject conditions, JSONObject update, String collectionName) {
		try {
			FindOneAndReplaceOptions options = new FindOneAndReplaceOptions();
			options.upsert(true);
			databaseConnection.getDatabase().getCollection(collectionName).findOneAndReplace(Document.parse(conditions.toString()), Document.parse(update.toString()), options);
		} catch (MongoException e) {
			e.printStackTrace();
		}
	}
}
