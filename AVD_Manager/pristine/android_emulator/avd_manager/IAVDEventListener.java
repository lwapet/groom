package fr.groom.android_emulator.avd_manager;

public interface IAVDEventListener {
	public void onCreate(AVD avd);

	public void onCreateFailed(AVD avd, String error);

	public void onStart(AVD avd, int port);

	public void onStartError(AVD avd, String error);

	public void onStartFailed(AVD avd, String error);

	public void onStop(AVD avd);

	public void onStopError(AVD avd);

	public void onStopFailed(AVD avd, String error);

	// TODO add apk reference
	public void onInstallApk(AVD avd);

	public void onInstallApkFailed(AVD avd, String error);

	public void onUninstallApk(AVD avd);

	public void onUninstallApkError(AVD avd, String error);

	public void onStartApk(AVD avd);

	public void onStartApkError(AVD avd, String error);

	public void onStopApk(AVD avd);

	public void onStopApkError(AVD avd);

	public void onReady(AVD avd);

}
