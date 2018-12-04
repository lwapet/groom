package fr.groom;

import com.android.prefs.AndroidLocation;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.utils.StdLogger;
import com.mongodb.DBCursor;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import fr.groom.mongo.Database;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

public class Main {
	private static String CONFIG = "config.properties";
	public static void main(String[] args) throws AndroidLocation.AndroidLocationException, IOException {
		Properties prop = new Properties();
		InputStream input = Main.class.getClassLoader().getResourceAsStream(CONFIG);
		prop.load(input);
		Configuration.setConfig(prop);

		Database database = new Database(
				Configuration.databaseUrl,
				Configuration.databasePort,
				Configuration.databaseName,
				Configuration.performAuth,
				Configuration.username,
				Configuration.password,
				Configuration.authSourceDatabaseName
		);

		MongoDatabase mongoDatabase = database.getDatabaseConnection().getDatabase();
		MongoCollection<Document> applicationCollection = mongoDatabase.getCollection("application");

		File sdkRoot = new File(Configuration.androidSdkHome);
		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));
		String deviceName = prop.getProperty("device_name");
		AvdInfo avdInfo = Arrays.stream(avdManager.getAllAvds()).filter(a -> a.getName().equals(deviceName)).findFirst().orElse(null);
		EmulatorPool pool = EmulatorPool.create(avdInfo,Integer.valueOf(prop.getProperty("pool_count")));
		DynamicAnalysisManager dam = new DynamicAnalysisManager(pool);
		pool.addEmulatorPoolEventListener(dam);
		pool.startPool();
//		Server server = new Server(pool);
//		server.start();


		for (Document appData : applicationCollection.find().limit(5)) {
			App app = new App(
					appData.getString("file_name"),
					appData.getString("package_name"),
					appData.getString("main_activity")
			);
			dam.addApp(app);
		}
	}
}
