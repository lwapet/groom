package fr.groom;

import com.android.prefs.AndroidLocation;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import com.android.sdklib.repository.AndroidSdkHandler;
import com.android.utils.StdLogger;
import com.sun.net.httpserver.HttpServer;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;

public class Main {
	private static int EMULATOR_POOL_SIZE = 2;

	public static void main(String[] args) throws IOException, AndroidLocation.AndroidLocationException {
		File sdkRoot = new File(System.getenv(Constants.ENV_VAR_ANDROID_SDK_HOME));
		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler,new StdLogger(StdLogger.Level.INFO));
		String deviceName = "Pixel_API_28";
		AvdInfo avdInfo = Arrays.stream(avdManager.getAllAvds()).filter(a -> a.getName().equals(deviceName)).findFirst().orElse(null);
		EmulatorPool pool = EmulatorPool.create(avdInfo, 1);
		pool.startPool();

	}
}
