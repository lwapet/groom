package fr.groom;

import com.android.ddmlib.*;
import com.android.ddmlib.testrunner.InstrumentationResultParser;
import fr.groom.models.App;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;

public class Worker implements IWorker {
	private boolean isIdle;
	private IDevice device;
//	private HashSet<IWorkerEventListener> listeners;
	private WorkerPool workerPool;
	private IWorkerEventListener dynamicAnalysis;


	public Worker(IDevice device, WorkerPool workerPool) {
//		this.listeners = new HashSet<>();
		this.workerPool = workerPool;
//		this.addWorkerEventListener(workerEventListener);
		this.device = device;
		this.setIdle();
	}

//	private void notifyStatusChange(){
//		listeners.forEach(l -> l.onStatusChange(this, this.isIdle));
//	}

	@Override
	public void setBusy() {
		this.isIdle = false;
//		notifyStatusChange();
		workerPool.onBusy(this);
	}

	@Override
	public void setIdle() {
		this.isIdle = true;
//		notifyStatusChange();
		workerPool.onIdle(this);
	}

	@Override
	public void installApk(File apk, boolean forceInstall) {
		this.setBusy();
		InstallReceiver installReceiver = new InstallReceiver();
		System.out.println("[install apk output]: Trying to install apk with the following path:" + apk.getAbsolutePath());
		try {
			device.installPackage(apk.getAbsolutePath(), forceInstall, installReceiver);
		} catch (InstallException e) {
//			e.printStackTrace();
			System.err.println("[install apk output]: install failed for apk: " + apk.getAbsolutePath());
		}
		if (!installReceiver.isSuccessfullyCompleted()) {
			System.err.println("[install apk output]: Installation error: " + installReceiver.getErrorMessage());
			dynamicAnalysis.onInstallApkFailed(this, installReceiver.getErrorMessage());
//			listeners.forEach(l -> l.onInstallApkFailed(this, installReceiver.getErrorMessage()));
			setIdle();
		} else {
			System.out.println("[install apk output]: install succeeded for apk: " + apk.getAbsolutePath());
			dynamicAnalysis.onInstallApk(this);
//			listeners.forEach(l -> l.onInstallApk(this));
		}
	}

	@Override
	public void uninstallApk(String packageName) {
//		HashSet<IWorkerEventListener> clone = new HashSet<>(listeners);
		try {
			String result = device.uninstallPackage(packageName);
			if (result == null) {
				dynamicAnalysis.onUninstallApk(this);
//				listeners.forEach(l -> l.onUninstallApk(this));
				setIdle();
			} else {
				dynamicAnalysis.onUninstallApkError(this, result);
//				listeners.forEach(l -> l.onUninstallApkError(this, result));
			}
		} catch (InstallException e) {
			e.printStackTrace();
			dynamicAnalysis.onUninstallApkError(this, e.getMessage());
//			listeners.forEach(l -> l.onUninstallApkError(this, e.getMessage()));
		}
	}

	@Override
	public void startApp(App app) {
		Worker currentWorker = this;
		StringBuilder stringBuilder = new StringBuilder();
//		stringBuilder.append("am ");
//		stringBuilder.append("start ");
//		stringBuilder.append("-n ");
//		stringBuilder.append(app.getPackageName());
//		stringBuilder.append("/");
//		stringBuilder.append(app.getMainActivity().replace("$", "\\$"));
//		stringBuilder.append(" -a ");
//		stringBuilder.append("android.intent.action.MAIN ");
//		stringBuilder.append("-c ");
//		stringBuilder.append("android.intent.category.LAUNCHER");
//		stringBuilder.append("; echo return code: $?");
		stringBuilder.append("monkey -p ");
		stringBuilder.append(app.getPackageName());
		stringBuilder.append("monkey -c android.intent.category.LAUNCHER 1");
		String test = "monkey -p " + app.getPackageName() + " -c android.intent.category.LAUNCHER 1; echo $?";
		try {
			this.getDevice().executeShellCommand(test, new MultiLineReceiver() {

				@Override
				public void processNewLines(String[] strings) {
					for (String string : strings) {
						System.out.println("[startApp output][" + app.getSha256() + "]: " + string);
					}
				}

				@Override
				public boolean isCancelled() {
					return false;
				}

				@Override
				public void done() {
					dynamicAnalysis.onStartApk(currentWorker);
				}
			});
		} catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isIdle() {
		return this.isIdle;
	}

	@Override
	public IDevice getDevice() {
		return this.device;
	}

//	@Override
//	public void addWorkerEventListener(IWorkerEventListener listener) {
//		listeners.add(listener);
//	}
//
//	@Override
//	public void removeEmulatorEventListener(IWorkerEventListener listener) {
//		listeners.remove(listener);
//	}


	public void setDynamicAnalysis(DynamicAnalysis dynamicAnalysis) {
		this.dynamicAnalysis = dynamicAnalysis;
	}

}
