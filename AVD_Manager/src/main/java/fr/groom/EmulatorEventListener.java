package fr.groom;

import fr.groom.Emulator;
import fr.groom.IEmulatorEventListener;
import fr.groom.server.EmulatorStatus;

public abstract class EmulatorEventListener implements IEmulatorEventListener {
	@Override
	public void onStart(Emulator emulator) {

	}

	@Override
	public void onStartError(Emulator emulator, String error) {

	}

	@Override
	public void onStartFailed(Emulator emulator, String error) {

	}

	@Override
	public void onStop(Emulator emulator) {

	}

	@Override
	public void onStopError(Emulator emulator) {

	}

	@Override
	public void onStopFailed(Emulator emulator, String error) {

	}

	@Override
	public void onInstallApk(Emulator emulator) {

	}

	@Override
	public void onInstallApkFailed(Emulator emulator, String error) {

	}

	@Override
	public void onUninstallApk(Emulator emulator) {

	}

	@Override
	public void onUninstallApkError(Emulator emulator, String error) {

	}

	@Override
	public void onStartApk(Emulator emulator) {

	}

	@Override
	public void onStartApkError(Emulator emulator, String error) {

	}

	@Override
	public void onStopApk(Emulator emulator) {

	}

	@Override
	public void onStopApkError(Emulator emulator) {

	}

	@Override
	public void onReady(Emulator emulator) {

	}

	@Override
	public void onStatusChange(Emulator emulator, EmulatorStatus status) {

	}
}
