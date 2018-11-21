//package groom.android_emulator;
//
//import groom.android_emulator.sdk.AndroidSdk;
//import java.io.File;
//
//public class AVD {
//	private String avdName;
//	private String deviceName;
//	private AndroidPlatform osVersion;
//	private String targetAbi;
//	private String sdCardSize;
//
//	private AVD(String avdName, String deviceName, String osVersion, String targetAbi, String sdCardSize) throws IllegalArgumentException {
//
//		if (avdName == null || osVersion == null)
//			throw new IllegalArgumentException("A name and an OS Version must be given");
//
//		int targetLength = osVersion.length();
//		if (targetLength > 2 && osVersion.startsWith("\"") && osVersion.endsWith("\"")) {
//			osVersion = osVersion.substring(1, targetLength - 1);
//		}
//
//		this.osVersion = AndroidPlatform.valueOf(osVersion);
//		if (this.osVersion == null)
//			throw new IllegalArgumentException("OS version not recognised: " + osVersion);
//
//		this.avdName = avdName;
//		this.deviceName = deviceName;
//		this.sdCardSize = sdCardSize;
//		this.targetAbi = targetAbi;
//	}
//
//	public static AVD create(String avdName, String deviceName, String osVersion, String targetAbi, String sdCardSize) {
//		return new AVD(avdName, deviceName, osVersion, targetAbi, sdCardSize);
//	}
//
//	public String getAvdName() {
//		return avdName;
//	}
//
//	private File getAvdHome(final File homeDir) {
//		return new File(homeDir, ".android/avd/");
//	}
//
//	private File getAvdDirectory(final File homeDir) {
//		return new File(getAvdHome(homeDir), getAvdName() + ".avd");
//	}
//
//	private File getAvdConfigFile(File homeDir) {
//		return new File(getAvdDirectory(homeDir), "config.ini");
//	}
//
//	public AVDCreationTask getAVDCreationTask(AndroidSdk androidSdk) {
//		return new AVDCreationTask(androidSdk);
//	}
//
//	public void createAVD(AndroidSdk androidSdk) throws AndroidAVDException {
//		this.getAVDCreationTask(androidSdk).call();
//	}
//
//	private boolean isAvdExists(AndroidSdk androidSdk) {
//		File homeDir = androidSdk.getSdkHomeDirectory();
//		return getAvdConfigFile(homeDir).exists();
//	}
//
//
//	private final class AVDCreationTask {
//		private final AndroidSdk androidSdk;
//		AVDCreationTask(AndroidSdk androidSdk) {
//			this.androidSdk = androidSdk;
//		}
//
//		public boolean call() throws AndroidAVDException{
//			File homeDir = androidSdk.getSdkHomeDirectory();
//			final File avdDirectory = getAvdDirectory(homeDir);
//			if(isAvdExists(androidSdk)) {
//				throw new AVDDiscoveryException("AVD already exists");
//			}
//
//			return true;
//		}
//
//	}
//}
