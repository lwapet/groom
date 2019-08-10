package fr.groom;

import com.android.ddmlib.*;
import com.android.prefs.AndroidLocation;
import com.android.repository.api.LocalPackage;
import com.android.sdklib.build.ApkBuilder;
import com.android.sdklib.build.ApkBuilderMain;
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
import fr.groom.models.App;
import fr.groom.mongo.Database;
import org.bson.Document;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

public class Main {
	private static String CONFIG = "avd-config.properties";
	public static File downloadFile(String filename, String fileUrl) {
		try {
			URL website = new URL(fileUrl);
			ReadableByteChannel rbc = Channels.newChannel(website.openStream());
			FileOutputStream fos = new FileOutputStream(filename);
			fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);

		} catch (IOException e) {
			e.printStackTrace();
		}
		return new File(filename);
	}
	public static File signApk(File apkToSign, String pathToApksigner, String pathToKeyStore, String keyPassword) throws IOException {
		System.out.println("Signing apk.");
		File signed = new File(apkToSign.getAbsolutePath().replace(".apk", "") + "-signed.apk");
		ProcessBuilder pb = new ProcessBuilder(pathToApksigner,
				"sign",
				"--ks",
				pathToKeyStore,
				"--key-pass",
				"pass:" + keyPassword,
				"--ks-pass",
				"pass:" + keyPassword,
				"--out",
				signed.getAbsolutePath(),
				apkToSign.getAbsolutePath()
		);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return signed;
	}

	public static void main(String[] args) throws AndroidLocation.AndroidLocationException, IOException {
		Properties prop = new Properties();
		InputStream input = new FileInputStream(new File("config.properties"));
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
//		for(App app : apps) {
//			String filename = Paths.get("/Users/lgitzing/Development/work/Groom/AVD_Manager/temp", app.getLegacyFilename()).toAbsolutePath().toString();
//			File apk = downloadFile(filename, "http://widecore24:7000/killerdroid/killerdroid_log/" + app.getLegacyFilename());
//			File signedApk = signApk(apk, "/Users/lgitzing/Library/Android/sdk/build-tools/27.0.2/apksigner","/Users/lgitzing/.android/keystore","!L0uL0u!");
//			app.setApk(signedApk);
//		}
//
//		File sdkRoot = new File(AVDConfiguration.androidSdkHome);
//		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
//		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));

//		String deviceName = prop.getProperty("device_name");
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
