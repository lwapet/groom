package fr.groom.static_analysis.modules;

import fr.groom.Configuration;
import fr.groom.Main;
import fr.groom.mongo.DatabaseConnection;
import fr.groom.static_analysis.StaticAnalysis;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import fr.groom.configuration.DatabaseConfiguration;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.JSONObject;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;

import java.util.ArrayList;
import java.util.List;

public class TrackerDetectorModule extends Module<List<ObjectId>> implements IModule {
	private String storageField = "trackers";
	private DatabaseConnection databaseConnection;

	public TrackerDetectorModule(StaticAnalysis staticAnalysis) {
		super(new ArrayList<>(), ModuleType.SOOTMETHODLEVEL, staticAnalysis);
		DatabaseConfiguration dbConfig = Configuration.v().getDatabaseConfiguration();
		boolean auth = dbConfig.getAuthenticationConfiguration().isPerformAuthentication();
		this.databaseConnection = new DatabaseConnection();
		databaseConnection.configure(
				dbConfig.getUrl(),
				dbConfig.getPort(),
				"excavator_data",
				auth ? dbConfig.getAuthenticationConfiguration().getUsername() : null,
				auth ? dbConfig.getAuthenticationConfiguration().getPassword() : null,
				auth ? dbConfig.getAuthenticationConfiguration().getAuthSourceDatabaseName() : null
		);
		databaseConnection.connection();
	}

	@Override
	public void executeModule(SootClass sootClass, SootMethod sootMethod, Unit unit) {
		if (!sootClass.getName().contains("android.support") && !sootClass.getName().startsWith("java.")) {
			this.resultHandler(sootClass.getName());
		}
	}

	@Override
	public void processResults() {

	}

	@Override
	public void saveResults() {
		JSONObject field = new JSONObject();
		JSONObject dataUpdate = new JSONObject();
		field.put(this.storageField, this.data);
		dataUpdate.put("$set", field);
		JSONObject condition = new JSONObject();
		condition.put("sha256", this.staticAnalysis.getApp().getSha256());
		this.storage.update(condition, dataUpdate, Main.STATIC_COLLECTION);
	}

	@Override
	public void resultHandler(Object result) {
		String className = (String) result;
		MongoCollection trackerCollection = databaseConnection.getDatabase().getCollection("trackers");
		FindIterable trackers = trackerCollection.find();
		MongoCursor cursor = trackers.iterator();
		while (cursor.hasNext()) {
			Document tracker = (Document) cursor.next();
			List<String> detectionRule = (ArrayList) tracker.get("code_signature");
			for (String rule : detectionRule) {
				if (className.contains(rule)) {
					ObjectId trackerId = (ObjectId) tracker.get("_id");
					this.data.add(trackerId);
				}
			}
		}
	}

	@Override
	public void onFinish() {
		databaseConnection.close();
	}

}
