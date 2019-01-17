package fr.groom;

import fr.groom.server.EmulatorStatus;

public interface IWorkerEventListener {
//	public void onStart(IWorker worker);

//	public void onStartError(IWorker worker, String error);

//	public void onStartFailed(IWorker worker, String error);

//	public void onStop(IWorker worker);

//	public void onStopError(IWorker worker);

//	public void onStopFailed(IWorker worker, String error);

	// TODO add apk reference
	public void onInstallApk(IWorker worker);

	public void onInstallApkFailed(IWorker worker, String error);

	public void onUninstallApk(IWorker worker);

	public void onUninstallApkError(IWorker worker, String error);

	public void onStartApk(IWorker worker);

	public void onStartApkError(IWorker worker, String error);

	public void onStopApk(IWorker worker);

	public void onStopApkError(IWorker worker);

	public void onReady(IWorker worker);

//	public void onStatusChange(IWorker worker, boolean isIdle);

	public void onIdle(IWorker worker);

	public void onBusy(IWorker worker);
}
