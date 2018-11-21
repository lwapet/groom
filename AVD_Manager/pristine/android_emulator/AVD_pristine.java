package fr.groom.android_emulator;

import fr.groom.Util;

import java.io.File;

public class AVD_pristine {
	private String avdName;
	private AndroidPlatform osVersion;
	private ScreenDensity screenDensity;
	private ScreenResolution screenResolution;
	private String deviceLocale;
	private String sdCardSize;
	private String targetAbi;
	private String deviceDefinition;
	private boolean wipeData;
	private final boolean showWindow;
	private final boolean useSnapshots;
	private final String commandLineOptions;
	private final String androidSdkHome;
	private final String executable;
	private final String avdNameSuffix;

	private AVD_pristine(String avdName, boolean wipeData, boolean showWindow,
						 boolean useSnapshots, String commandLineOptions, String androidSdkHome, String executable, String
						avdNameSuffix) {
		this.avdName = avdName;
		this.wipeData = wipeData;
		this.showWindow = showWindow;
		this.useSnapshots = useSnapshots;
		this.commandLineOptions = commandLineOptions;
		this.androidSdkHome = androidSdkHome;
		this.executable = executable;
		this.avdNameSuffix = avdNameSuffix;
	}

	private AVD_pristine(String osVersion, String screenDensity, String screenResolution,
						 String deviceLocale, String sdCardSize, boolean wipeData, boolean showWindow,
						 boolean useSnapshots, String commandLineOptions, String targetAbi, String deviceDefinition,
						 String androidSdkHome, String executable, String avdNameSuffix)
			throws IllegalArgumentException {
		if (osVersion == null || screenDensity == null || screenResolution == null) {
			throw new IllegalArgumentException("Valid OS version and screen properties must be supplied.");
		}

		// Normalise incoming variables
		int targetLength = osVersion.length();
		if (targetLength > 2 && osVersion.startsWith("\"") && osVersion.endsWith("\"")) {
			osVersion = osVersion.substring(1, targetLength - 1);
		}
		screenDensity = screenDensity.toLowerCase();
		if (screenResolution.matches("(?i)" + Constants.REGEX_SCREEN_RESOLUTION_ALIAS)) {
			screenResolution = screenResolution.toUpperCase();
		} else if (screenResolution.matches("(?i)" + Constants.REGEX_SCREEN_RESOLUTION)) {
			screenResolution = screenResolution.toLowerCase();
		}
		if (deviceLocale != null && deviceLocale.length() > 4) {
			deviceLocale = deviceLocale.substring(0, 2).toLowerCase() + "_"
					+ deviceLocale.substring(3).toUpperCase();
		}

		this.osVersion = AndroidPlatform.valueOf(osVersion);
		if (this.osVersion == null) {
			throw new IllegalArgumentException(
					"OS version not recognised: " + osVersion);
		}
		this.screenDensity = ScreenDensity.valueOf(screenDensity);
		if (this.screenDensity == null) {
			throw new IllegalArgumentException(
					"Screen density not recognised: " + screenDensity);
		}
		this.screenResolution = ScreenResolution.valueOf(screenResolution);
		if (this.screenResolution == null) {
			throw new IllegalArgumentException(
					"Screen resolution not recognised: " + screenResolution);
		}
		this.deviceLocale = deviceLocale;
		this.sdCardSize = sdCardSize;
		this.wipeData = wipeData;
		this.showWindow = showWindow;
		this.useSnapshots = useSnapshots;
		this.commandLineOptions = commandLineOptions;
		if (targetAbi != null && targetAbi.startsWith("default/")) {
			targetAbi = targetAbi.replace("default/", "");
		}
		this.targetAbi = targetAbi;
		this.deviceDefinition = deviceDefinition;
		this.androidSdkHome = androidSdkHome;
		this.executable = executable;
		this.avdNameSuffix = avdNameSuffix;
	}

	public static final AVD_pristine create(String avdName, String osVersion, String screenDensity,
											String screenResolution, String deviceLocale, String sdCardSize, boolean wipeData,
											boolean showWindow, boolean useSnapshots, String commandLineOptions, String targetAbi,
											String deviceDefinition, String androidSdkHome, String executable, String avdNameSuffix) {
		if (Util.fixEmptyAndTrim(avdName) == null) {
			return new AVD_pristine(osVersion, screenDensity, screenResolution, deviceLocale, sdCardSize, wipeData,
					showWindow, useSnapshots, commandLineOptions, targetAbi, deviceDefinition, androidSdkHome, executable, avdNameSuffix);
		}

		return new AVD_pristine(avdName, wipeData, showWindow, useSnapshots, commandLineOptions, androidSdkHome, executable,
				avdNameSuffix);
	}

	public boolean isNamedEmulator() {
		return avdName != null && osVersion == null;
	}

	public String getAvdName() {
		if (isNamedEmulator()) {
			return avdName;
		}

		return getGeneratedAvdName();
	}

	public AndroidPlatform getOsVersion() {
		return osVersion;
	}

	public ScreenDensity getScreenDensity() {
		return screenDensity;
	}

	public ScreenResolution getScreenResolution() {
		return screenResolution;
	}

	public String getSdCardSize() {
		return sdCardSize;
	}

	public String getTargetAbi() {
		return targetAbi;
	}

	public String getDeviceDefinition() {
		return deviceDefinition;
	}

	public boolean isWipeData() {
		return wipeData;
	}

	public boolean isShowWindow() {
		return showWindow;
	}

	public boolean isUseSnapshots() {
		return useSnapshots;
	}

	public String getCommandLineOptions() {
		return commandLineOptions;
	}

	public String getAndroidSdkHome() {
		return androidSdkHome;
	}

	public String getExecutable() {
		return executable;
	}

	public String getAvdNameSuffix() {
		return avdNameSuffix;
	}

	public String getDeviceLocale() {
		if (deviceLocale == null) {
			return Constants.DEFAULT_LOCALE;
		}
		return deviceLocale;
	}

	private String getGeneratedAvdName() {
		String locale = getDeviceLocale().replace('_', '-');
		String density = screenDensity.toString();
		String resolution = screenResolution.toString();
		String platform = osVersion.getTargetName().replaceAll("[^a-zA-Z0-9._-]", "_");
		String abi = "";
		if (targetAbi != null && osVersion.requiresAbi()) {
			abi = "_" + targetAbi.replaceAll("[^a-zA-Z0-9._-]", "-");
		}
		String deviceDef = "";
		if (deviceDefinition != null && !deviceDefinition.isEmpty()) {
			deviceDef = "_" + deviceDefinition.replaceAll("[^a-zA-Z0-9._-]", "-");
		}
		String suffix = "";
		if (avdNameSuffix != null) {
			suffix = "_" + avdNameSuffix.replaceAll("[^a-zA-Z0-9._-]", "-");
		}

		return String.format("hudson_%s_%s_%s_%s%s%s%s", locale, density, resolution, platform, abi, deviceDef, suffix);
	}

	private File getAvdHome(final File homeDir) {
		return new File(homeDir, ".android/avd/");
	}

	private File getAvdDirectory(final File homeDir) {
		return new File(getAvdHome(homeDir), getAvdName() + ".avd");
	}

}
