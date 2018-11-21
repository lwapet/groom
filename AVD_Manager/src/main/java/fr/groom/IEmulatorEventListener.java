package fr.groom;

public interface IEmulatorEventListener {
	public void onStart(Emulator emulator);

	public void onStartError(Emulator emulator, String error);

	public void onStartFailed(Emulator emulator, String error);

	public void onStop(Emulator emulator);

	public void onStopError(Emulator emulator);

	public void onStopFailed(Emulator emulator, String error);

	// TODO add apk reference
	public void onInstallApk(Emulator emulator);

	public void onInstallApkFailed(Emulator emulator, String error);

	public void onUninstallApk(Emulator emulator);

	public void onUninstallApkError(Emulator emulator, String error);

	public void onStartApk(Emulator emulator);

	public void onStartApkError(Emulator emulator, String error);

	public void onStopApk(Emulator emulator);

	public void onStopApkError(Emulator emulator);

	public void onReady(Emulator emulator);
}
