package fr.groom;

import com.android.ddmlib.*;
import fr.groom.Emulator;
import fr.groom.EmulatorPool;
import fr.groom.models.App;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class DynamicAnalysis extends EmulatorEventListener {
	App app;
	Emulator emulator;
	private static int EXECUTION_DURATION = 10 * 1000;

	public DynamicAnalysis(Emulator emulator, App app) {
		this.emulator = emulator;
		emulator.addEmulatorEventListener(this);
		this.app = app;
	}

	public void run() {
		try {
			emulator.installApk(app.getApk(), true);
		} catch (InstallException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onInstallApk(Emulator emulator) {
		try {
			emulator.startApp(app);
			Thread.sleep(EXECUTION_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		emulator.uninstallApk(app.getPackageName());
	}

	@Override
	public void onUninstallApk(Emulator emulator) {
		emulator.removeEmulatorEventListener(this);
	}

	@Override
	public void onUninstallApkError(Emulator emulator, String error) {
		if (error.equals("DELETE_FAILED_DEVICE_POLICY_MANAGER")) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("pm ");
			stringBuilder.append("disable-user ");
			stringBuilder.append(app.getPackageName());
			try {
				emulator.getDevice().executeShellCommand(stringBuilder.toString(), new MultiLineReceiver() {
					@Override
					public void processNewLines(String[] lines) {
						emulator.uninstallApk(app.getPackageName());
					}

					@Override
					public boolean isCancelled() {
						return false;
					}
				});
			} catch (TimeoutException | AdbCommandRejectedException | ShellCommandUnresponsiveException | IOException e) {
				e.printStackTrace();
			}
		}
	}
}
