package fr.groom.models;

import fr.groom.AVDConfiguration;

import java.io.File;
import java.nio.file.Paths;

public class App {
	private static String pathToRepo = AVDConfiguration.pathToInstrumentedApkDirectory;
	private String fileName;
	private File apk;
	private String packageName;
	private String mainActivity;

	public App(String fileName, String packageName, String mainActivity) {
		this.fileName = fileName;
		this.apk = Paths.get(pathToRepo, fileName).toFile();
		this.packageName = packageName;
		this.mainActivity = mainActivity;
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
}
