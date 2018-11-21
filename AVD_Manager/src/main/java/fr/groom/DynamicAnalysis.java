package fr.groom;

import com.android.ddmlib.InstallException;
import fr.groom.Emulator;
import fr.groom.EmulatorPool;

import java.io.File;

public class DynamicAnalysis extends EmulatorEventListener {
	DynamicAnalysisManager.ApkData apk;
	Emulator emulator;
	private static int EXECUTION_DURATION = 60 * 1000;

	public DynamicAnalysis(Emulator emulator, DynamicAnalysisManager.ApkData apk) {
		this.emulator = emulator;
		emulator.addEmulatorEventListener(this);
		this.apk = apk;
	}

	public void run() {
		try {
			emulator.installApk(apk.getApk(), true);
		} catch (InstallException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInstallApk(Emulator emulator) {
		try {
			emulator.startApp(apk);
			Thread.sleep(1000 * 20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		emulator.uninstallApk(apk.getPackageName());
	}

	@Override
	public void onUninstallApk(Emulator emulator) {
		emulator.removeEmulatorEventListener(this);
	}
}
