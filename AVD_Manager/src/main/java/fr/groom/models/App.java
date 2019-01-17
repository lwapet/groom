package fr.groom.models;

import fr.groom.AVDConfiguration;

import java.io.File;
import java.nio.file.Paths;

public class App {
	private static String pathToRepo = AVDConfiguration.pathToInstrumentedApkDirectory;
	private File apk;
	private String packageName;
	private String mainActivity;
	private String sha256;

	public App(File apk, String packageName, String mainActivity, String sha256) {
		this.apk = apk;
		this.packageName = packageName;
		this.mainActivity = mainActivity;
		this.sha256 = sha256;
	}

	public File getApk() {
		return apk;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getMainActivity() {
		return mainActivity;
	}

	public String getSha256() {
		return sha256;
	}
}
