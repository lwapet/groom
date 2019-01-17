package fr.groom;

public abstract class WorkerEventListener implements IWorkerEventListener{
	@Override
	public void onInstallApk(IWorker worker) {

	}

	@Override
	public void onInstallApkFailed(IWorker worker, String error) {

	}

	@Override
	public void onUninstallApk(IWorker worker) {

	}

	@Override
	public void onUninstallApkError(IWorker worker, String error) {

	}

	@Override
	public void onStartApk(IWorker worker) {

	}

	@Override
	public void onStartApkError(IWorker worker, String error) {

	}

	@Override
	public void onStopApk(IWorker worker) {

	}

	@Override
	public void onStopApkError(IWorker worker) {

	}

	@Override
	public void onReady(IWorker worker) {

	}

	@Override
	public void onIdle(IWorker worker) {

	}

	@Override
	public void onBusy(IWorker worker) {

	}
}
