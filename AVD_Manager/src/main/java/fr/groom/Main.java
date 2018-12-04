package fr.groom;

import com.android.prefs.AndroidLocation;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.utils.StdLogger;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import fr.groom.mongo.Database;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException, AndroidLocation.AndroidLocationException {

		Database database = new Database(
				"localhost",
				27017,
				"dynamic",
				false,
				null,
				null,
				null
		);

		MongoDatabase mongoDatabase = database.getDatabaseConnection().getDatabase();
		MongoCollection applicationCollection = mongoDatabase.getCollection("application");
		ArrayList<Document> apps = (ArrayList<Document>) applicationCollection.find().limit(5).into(new ArrayList<Document>());

		File sdkRoot = new File(System.getenv("ANDROID_SDK_HOME"));
		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));
		String deviceName = "Nexus_5X_API_27";
		AvdInfo avdInfo = Arrays.stream(avdManager.getAllAvds()).filter(a -> a.getName().equals(deviceName)).findFirst().orElse(null);
		EmulatorPool pool = EmulatorPool.create(avdInfo, 1);
		DynamicAnalysisManager dam = new DynamicAnalysisManager(pool);
		pool.addEmulatorPoolEventListener(dam);
		pool.startPool();
//		Server server = new Server(pool);
//		server.start();



		apps.forEach(appData -> {
			App app = new App(
					appData.getString("file_name"),
					appData.getString("package_name"),
					appData.getString("main_activity")
			);
			dam.addApp(app);
		});
	}
}
