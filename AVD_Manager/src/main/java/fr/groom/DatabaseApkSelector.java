package fr.groom;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import org.bson.Document;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
				app.getString("sha256"),
				(List<String>) app.get("abis")
		);
	}

//	private ArrayList<App>

	private ArrayList<App> queryUnanalyzedApplications() {
		ArrayList<String> alreadyAnalyzedSha = new ArrayList<>();
		for (String sha256 : dynamicAnalysis.distinct("sha256", String.class)) {
			alreadyAnalyzedSha.add(sha256);
		}

		Document filter = new Document("$nin", alreadyAnalyzedSha);
		Document query = new Document("sha256", filter);
		query.append("is_malicious", false);
		Document exists = new Document("$exists", true);
		query.append("instrumented_filename", exists);
		long count = applicationCollection.count(query);
		System.out.println(count + " applications correspond to the query and not analyzed dynamically yet.");
		FindIterable<Document> iterable = applicationCollection.find(query).limit(AVDConfiguration.apk_quantity);
		ArrayList<App> apps = new ArrayList<>();
		for (Document appData : iterable) {
			if (appData.getString("instrumented_filename") != null) {
				File apk = Paths.get(AVDConfiguration.pathToInstrumentedApkDirectory, appData.getString("instrumented_filename")).toFile();
				App app = new App(
						apk,
						appData.getString("package_name"),
						appData.getString("main_activity"),
						appData.getString("sha256"),
						(List<String>) appData.get("abis")
				);
				apps.add(app);
			}
		}
		System.out.println("Analyzing " + apps.size() + " applications.");
		return apps;
	}

	private ArrayList<App> queryAppFile() {
		ArrayList<App> apps = new ArrayList<>();
		String filePath = "";
		List<String> abis = new ArrayList<>();
		App app = new App(
				new File("/Users/lgitzing/Development/work/Groom/static/instrumented/app-debug-soot-aligned-signed.apk"),
				"com.asap.inria.webview_getcontacts",
				".MainActivity",
				"",
				abis
		);
		apps.add(app);
		return apps;
	}

	private ArrayList<App> queryMultipleApplications() {
		String[] sha256Array = {
				"00039936C1FCF64F0D6A4B5E95A3A8571254256B0C65CF4156EAE2F4109C1B4D",
				"0003BF3FFCE8128677DA3C057C68AA4667FFA26E8647D318AED1D8A5A476293D",
				"0003EC8BDA35BA7B377C25524A05C6EB9D1275DDE3D50DBE93C77B3F89548ABD",
				"00060E80E3D1719C5075C519DACCA3D062FBB7FA4A4D64B9E99B7AE06849E0C9",
				"000878101A9CF570105D5C43DEF5DD52D62BA383F5606E72B889C15EC62C9982",
				"000898F72BF6DE3972DE2B84C053D8B34C28E9911F84C3E6DC89DB2DF8D2B35A",
				"0009835ED17951F9978B8105726F894F4C8F1B94CD1AD0D4EF22D66BEA5D7F61",
				"000A4121124BCBA257AC7FCCED17F32399F2C65825B5A22E666A51B13697B60C",
				"000C06F8AC50DA364FDE5761DE4925FB57627273130DDC0A35F622A958E0299B",
				"000D29C81FB8DC30347C91C9AE142D785EFC0DAB9776BEFA4DA65F5D2E34455B",
				"2AE2156C2CD91A43243C367F473D835B4EBD598040BA1E05EB9B2862F61DFE74",
				"4636513D1C7D9690307618AE9AA1CAB28040E9F3DC4698FE3D9EEE03C8F2EA4A",
				"B9660162A9F9BDF0E3754702BD36966865FAB23DA6768E6BA1683C2EB3532F33"
		};
		Document filter = new Document("$in", Arrays.asList(sha256Array));
		Document query = new Document("sha256", filter);
		FindIterable<Document> iterable = applicationCollection.find(query).limit(AVDConfiguration.apk_quantity);
		ArrayList<App> apps = new ArrayList<>();
		for (Document appData : iterable) {
			if (appData.getString("file_name") != null) {
				File apk = Paths.get(AVDConfiguration.pathToInstrumentedApkDirectory, appData.getString("file_name")).toFile();
				App app = new App(
						apk,
						appData.getString("package_name"),
						appData.getString("main_activity"),
						appData.getString("sha256"),
						(List<String>) appData.get("abis")
				);
				apps.add(app);
			}
		}
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
//		return queryMultipleApplications();
	}
}
