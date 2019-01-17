package fr.groom;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import org.bson.Document;

import java.io.File;
import java.nio.file.Paths;
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
		File apk = Paths.get(AVDConfiguration.pathToInstrumentedApkDirectory, app.getString("file_name")).toFile();
		return new App(
				apk,
				app.getString("package_name"),
				app.getString("main_activity"),
				app.getString("sha256")
		);
	}

//	private ArrayList<App>

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
				File apk = Paths.get(AVDConfiguration.pathToInstrumentedApkDirectory, appData.getString("file_name")).toFile();
				App app = new App(
						apk,
						appData.getString("package_name"),
						appData.getString("main_activity"),
						appData.getString("sha256")
				);
				apps.add(app);
			}
		}
		return apps;
	}

	private ArrayList<App> queryAppFile() {
		ArrayList<App> apps = new ArrayList<>();
		String filePath = "";
		App app = new App(
				new File("/Users/lgitzing/Development/work/webview_unit_tests/webview_getcontacts/app/build/outputs/apk/debug/app-debug.apk"),
		"com.asap.inria.webview_getcontacts",
		".MainActivity",
		""
		);
		apps.add(app);
		return apps;
	}

	@Override
	public ArrayList<App> selectApplications() {
		ArrayList apps = new ArrayList();
//		apps.add(queryBySha("99A6F10977132C65F3BCCF946444663960C652AF1B3D47E9158ECE277320AEDE"));
//		apps.add(queryBySha("2AE2156C2CD91A43243C367F473D835B4EBD598040BA1E05EB9B2862F61DFE74"));
//		apps.addAll(queryAppFile());
//		return apps;
		return queryUnanalyzedApplications();
	}
}
