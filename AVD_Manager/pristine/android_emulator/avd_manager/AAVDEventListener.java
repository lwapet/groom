package fr.groom.android_emulator.avd_manager;

public abstract class AAVDEventListener implements IAVDEventListener{
	@Override
	public void onCreate(AVD avd) {

	}

	@Override
	public void onCreateFailed(AVD avd, String error) {

	}

	@Override
	public void onStart(AVD avd, int port) {
	}

	@Override
	public void onStartError(AVD avd, String error) {
		System.err.println(error);
	}

	@Override
	public void onStartFailed(AVD avd, String error) {

	}

	@Override
	public void onStop(AVD avd) {
	}

	@Override
	public void onStopError(AVD avd) {

	}

	@Override
	public void onStopFailed(AVD avd, String error) {

	}

	@Override
	public void onInstallApk(AVD avd) {

	}

	@Override
	public void onInstallApkFailed(AVD avd, String error) {

	}

	@Override
	public void onUninstallApk(AVD avd) {

	}

	@Override
	public void onUninstallApkError(AVD avd, String error) {

	}

	@Override
	public void onStartApk(AVD avd) {

	}

	@Override
	public void onStartApkError(AVD avd, String error) {

	}

	@Override
	public void onStopApk(AVD avd) {

	}

	@Override
	public void onStopApkError(AVD avd) {

	}

	@Override
	public void onReady(AVD avd) {

	}
}
