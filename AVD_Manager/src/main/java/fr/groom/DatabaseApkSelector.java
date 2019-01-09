package fr.groom;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import org.bson.Document;

import java.util.ArrayList;

public class DatabaseApkSelector implements ApkSelector {
	private MongoCollection<Document> dynamicAnalysis;
	private MongoCollection<Document> applicationCollection;

	public DatabaseApkSelector(MongoDatabase mongoDatabase) {
		this.applicationCollection = mongoDatabase.getCollection("application");
		this.dynamicAnalysis = mongoDatabase.getCollection("dynamic");
	}

	private App queryBySha(String sha256) {
		Document filter = new Document("sha256", sha256);
		Document app = applicationCollection.find(filter).first();
		return new App(
				app.getString("file_name"),
				app.getString("package_name"),
				app.getString("main_activity")
		);
	}

	private ArrayList<App> queryUnanalyzedApplications() {
		ArrayList<String> alreadyAnalyzedSha = new ArrayList<>();
		for (String sha256 : dynamicAnalysis.distinct("sha256", String.class)) {
			alreadyAnalyzedSha.add(sha256);
		}
		System.out.println(alreadyAnalyzedSha.size() + " have already been analyzed dynamically.");

		Document filter = new Document("$nin", alreadyAnalyzedSha);
		Document query = new Document("sha256", filter);
		long count = applicationCollection.count(query);
		System.out.println(count + " applications are stored and not analyzed dynamically yet.");
		FindIterable<Document> iterable = applicationCollection.find(query).limit(AVDConfiguration.apk_quantity);
		ArrayList<App> apps = new ArrayList<>();
		for (Document appData : iterable) {
			if (appData.getString("file_name") != null) {
				App app = new App(
						appData.getString("file_name"),
						appData.getString("package_name"),
						appData.getString("main_activity")
				);
				apps.add(app);
			}
		}
		return apps;
	}

	@Override
	public ArrayList<App> selectApplications() {
		ArrayList apps = new ArrayList();
		apps.add(queryBySha("3A84E25CCD1F5B4087305ED6FD69C163DE4E393DD8D8F0512BCF0E128BDD01D7"));
		return apps;
	}
}
