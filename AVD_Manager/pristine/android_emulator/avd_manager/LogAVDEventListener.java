package fr.groom.android_emulator.avd_manager;

public abstract class LogAVDEventListener implements IAVDEventListener{
	@Override
	public void onCreate(AVD avd) {
		System.out.println("AVD" + avd.getName() + " created");
	}

	@Override
	public void onCreateFailed(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " creation failed");
	}

	@Override
	public void onStart(AVD avd, int port) {
		System.out.println("AVD" + avd.getName() + " started");
	}

	@Override
	public void onStartError(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " start error");
	}

	@Override
	public void onStartFailed(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " start failed");
	}

	@Override
	public void onStop(AVD avd) {
		System.out.println("AVD" + avd.getName() + " stopped");
	}

	@Override
	public void onStopError(AVD avd) {
		System.err.println("AVD" + avd.getName() + " stop error");
	}

	@Override
	public void onStopFailed(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " stop failed");
	}

	@Override
	public void onInstallApk(AVD avd) {
		System.out.println("AVD" + avd.getName() + " apk installed");
	}

	@Override
	public void onInstallApkFailed(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " apk installation failed");
	}

	@Override
	public void onUninstallApk(AVD avd) {
		System.out.println("AVD" + avd.getName() + " apk uninstalled");
	}

	@Override
	public void onUninstallApkError(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " apk uninstall error");
	}

	@Override
	public void onStartApk(AVD avd) {
		System.out.println("AVD" + avd.getName() + " apk launched");
	}

	@Override
	public void onStartApkError(AVD avd, String error) {
		System.err.println("AVD" + avd.getName() + " apk launch error");
	}

	@Override
	public void onStopApk(AVD avd) {
		System.out.println("AVD" + avd.getName() + " apk stopped");
	}

	@Override
	public void onStopApkError(AVD avd) {
		System.err.println("AVD" + avd.getName() + " apk stop error");
	}

	@Override
	public void onReady(AVD avd) {
		System.out.println("AVD" + avd.getName() + " ready");
	}
}
