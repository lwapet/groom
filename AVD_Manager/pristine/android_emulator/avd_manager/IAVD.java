package fr.groom.android_emulator.avd_manager;

import java.io.File;

public interface IAVD {
	public void start();
	public void stop();
	public void installApk(File apk, boolean forceInstall);
	public void uninstallApk(String packageName);
	public void startApk(String packageName, String mainActivity, boolean fromLauncher);
	public void stopApk(String packageName);
	public boolean isRunning();
	public String getName();
	public String getAdbName();
	public int getPort();
	public void create();
	public void addListener(IAVDEventListener listener);

}
