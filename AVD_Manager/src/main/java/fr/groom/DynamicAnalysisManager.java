package fr.groom;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jdk.nashorn.internal.parser.JSONParser;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;

public class DynamicAnalysisManager extends EmulatorPoolEventListener {
	private static String pathToRepo = "/Users/lgitzing/Development/work/Groom/instrumented_apks";
	Queue<ApkData> apks;

	public DynamicAnalysisManager() {
		String apkFile = "/Users/lgitzing/Development/work/Groom/AVD_Manager/src/main/resources/groom/android_emulator/apks.json";
		try {
			FileReader reader = new FileReader(apkFile);
			JsonParser parser = new JsonParser();
			JsonArray apkArray = parser.parse(reader).getAsJsonArray();
			this.apks = new PriorityQueue<ApkData>();

			apkArray.forEach(j -> {
				JsonObject o = j.getAsJsonObject();
				ApkData data = new ApkData(
						o.get("file_name").getAsString(),
						o.get("package_name").getAsString(),
						o.get("main_activity").getAsString()
				);
				apks.add(data);
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void addApkToProcess(ApkData apk) {
		apks.add(apk);
	}

	@Override
	public void onNewIdleEmulator(Emulator emulator) {
		System.out.println(emulator.getDevice().getName());
		ApkData apk = apks.poll();
		if (apk != null) {
			DynamicAnalysis analysis = new DynamicAnalysis(emulator, apk);
			analysis.run();
		}
	}

	public class ApkData implements Comparable{

		private String fileName;
		private String packageName;
		private String mainActivity;
		private File apk;

		public ApkData(String fileName, String packageName, String mainActivity) {
			this.fileName = fileName;
			this.apk = Paths.get(pathToRepo, fileName).toFile();
			this.packageName = packageName;
			this.mainActivity = mainActivity;
		}

		public String getPackageName() {
			return packageName;
		}

		public String getMainActivity() {
			return mainActivity;
		}

		public File getApk() {
			return apk;
		}

		@Override
		public int compareTo(@NotNull Object o) {
			return 0;
		}
	}
}