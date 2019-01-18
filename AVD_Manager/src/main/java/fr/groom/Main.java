package fr.groom;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.DdmPreferences;
import com.android.ddmlib.IDevice;
import com.android.prefs.AndroidLocation;
import com.android.sdklib.build.ApkBuilder;
import com.android.sdklib.devices.Abi;
import com.android.sdklib.devices.Device;
import com.android.sdklib.devices.DeviceManager;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.sdklib.repository.PackageParserUtils;
import com.android.sdklib.tool.sdkmanager.SdkManagerCli;
import com.android.utils.ILogger;
import com.android.utils.StdLogger;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.tools.javac.main.Option;
import fr.groom.models.App;
import fr.groom.mongo.Database;
import org.bson.Document;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class Main {
	private static String CONFIG = "avd-config.properties";

	public static void main(String[] args) throws AndroidLocation.AndroidLocationException, IOException {
		Properties prop = new Properties();
		InputStream input = Main.class.getClassLoader().getResourceAsStream(CONFIG);
		prop.load(input);
		AVDConfiguration.setConfig(prop);

		Database database = new Database(
				AVDConfiguration.databaseUrl,
				AVDConfiguration.databasePort,
				AVDConfiguration.databaseName,
				AVDConfiguration.performAuth,
				AVDConfiguration.username,
				AVDConfiguration.password,
				AVDConfiguration.authSourceDatabaseName
		);

		MongoDatabase mongoDatabase = database.getDatabase();
		ApkSelector apkSelector = new DatabaseApkSelector(mongoDatabase);
		ArrayList<App> apps = apkSelector.selectApplications();

		File sdkRoot = new File(AVDConfiguration.androidSdkHome);
		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));


//		String deviceName = prop.getProperty("device_name");
//		AvdInfo avdInfo = Arrays.stream(avdManager.getAllAvds()).filter(a -> a.getName().equals(deviceName)).findFirst().orElse(null);
//		EmulatorPool pool = EmulatorPool.create(avdInfo, Integer.valueOf(prop.getProperty("pool_count")));
//		DynamicAnalysisManager dam = new DynamicAnalysisManager(pool);
//		Server server = new Server(pool);
//		server.start();

//		for(App app : apps) {
//			dam.addApp(app);
//		}

//		DdmPreferences.setDebugPortBase(8701);
		WorkerPool pool = new WorkerPool(apps);
		AndroidDebugBridge.addDeviceChangeListener(pool);
		AndroidDebugBridge.addDebugBridgeChangeListener(pool);
		AndroidDebugBridge.initIfNeeded(true);

		AndroidDebugBridge adb = AndroidDebugBridge.createBridge(AVDConfiguration.adbPath, true);

//		AndroidDebugBridge.addDeviceChangeListener(new EmulatorPool.DeviceChangeListener(this));
//		AndroidDebugBridge.addDebugBridgeChangeListener(new EmulatorPool.AndroidDebugBridgeListener(this));
//		AndroidDebugBridge adb = AndroidDebugBridge.createBridge(AVDConfiguration.adbPath, true);
//		IDevice[] devices = adb.getDevices();
//		for (IDevice device : devices) {
//			System.out.println(device.getState());
//		}
//
//
//		pool.addEmulatorPoolEventListener(dam);
//		pool.startPool();
	}
}
