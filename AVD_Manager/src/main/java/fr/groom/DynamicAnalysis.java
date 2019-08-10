package fr.groom;

import com.android.ddmlib.*;
import fr.groom.models.App;

import java.io.IOException;

public class DynamicAnalysis extends WorkerEventListener {
	App app;
	IWorker worker;
	public static int EXECUTION_DURATION = 10 * 1000;

	public DynamicAnalysis(IWorker worker, App app) {
		this.worker = worker;
		worker.setDynamicAnalysis(this);
		this.app = app;
	}

	public void run() {
		worker.installApk(app.getApk(), true);
	}

	@Override
	public void onInstallApk(IWorker worker) {
		worker.startApp(app);
	}

	@Override
	public void onStartApk(IWorker worker) {
		try {
			Thread.sleep(EXECUTION_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		worker.uninstallApk(app.getPackageName());
	}

	//	@Override
//	public void onInstallApkFailed(IWorker worker, String error) {
//		worker.removeEmulatorEventListener(this);
//	}

//	@Override
//	public void onUninstallApk(IWorker worker) {
//		worker.removeEmulatorEventListener(this);
//	}

	@Override
	public void onUninstallApkError(IWorker worker, String error) {
		if (error.equals("DELETE_FAILED_DEVICE_POLICY_MANAGER")) {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("pm ");
			stringBuilder.append("disable-user ");
			stringBuilder.append(app.getPackageName());
			try {
				worker.getDevice().executeShellCommand(stringBuilder.toString(), new MultiLineReceiver() {
					@Override
					public void processNewLines(String[] lines) {
						worker.uninstallApk(app.getPackageName());
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
