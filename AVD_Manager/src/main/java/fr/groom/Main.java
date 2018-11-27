package fr.groom;

import com.android.prefs.AndroidLocation;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.utils.StdLogger;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class Main {

	public static void main(String[] args) throws IOException, AndroidLocation.AndroidLocationException {
		File sdkRoot = new File(System.getenv("ANDROID_SDK_HOME"));
		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));
		String deviceName = "Nexus_5X_API_27";
		AvdInfo avdInfo = Arrays.stream(avdManager.getAllAvds()).filter(a -> a.getName().equals(deviceName)).findFirst().orElse(null);
		DynamicAnalysisManager dam = new DynamicAnalysisManager();
		EmulatorPool pool = EmulatorPool.create(avdInfo, 1);
		pool.addEmulatorPoolEventListener(dam);
		pool.startPool();
//		Server server = new Server(pool);
//		server.start();

	}
}
