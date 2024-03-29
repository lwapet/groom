package fr.groom.models;

import com.android.sdklib.devices.Abi;
import fr.groom.AVDConfiguration;

import java.io.File;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class App {
	private static String pathToRepo = AVDConfiguration.pathToInstrumentedApkDirectory;
	private File apk;
	private String legacyFilename;
	private String packageName;
	private String mainActivity;
	private String sha256;
	private List<String> abis;

	public App(String legacyFilename, String packageName, String mainActivity, String sha256, List<String> abis) {
		this.legacyFilename = legacyFilename;
		this.packageName = packageName;
		this.mainActivity = mainActivity;
		this.sha256 = sha256;
		this.abis = abis;
	}

	public String getLegacyFilename() {
		return legacyFilename;
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

	public void setApk(File apk) {
		this.apk = apk;
	}

	public boolean isEmulatorCompatible() {
		if (abis.size() == 0)
			return true;

		for (String abi : abis) {
			if (Abi.getEnum(abi).equals(Abi.X86) || Abi.getEnum(abi).equals(Abi.X86_64)) {
				return true;
			}
		}
		return false;
	}
}
