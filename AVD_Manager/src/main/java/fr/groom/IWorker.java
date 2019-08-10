package fr.groom;

import com.android.ddmlib.IDevice;
import com.sun.xml.bind.v2.model.core.ID;
import fr.groom.models.App;

import java.io.File;

public interface IWorker {
	public void setBusy();

	public void setIdle();

	public void installApk(File apk, boolean forceInstall);

	public void uninstallApk(String packageName);

	public void startApp(App app);

	public boolean isIdle();

	public IDevice getDevice();

	public void setDynamicAnalysis(DynamicAnalysis dynamicAnalysis);

	public void cleanLogcat();

//	public void addWorkerEventListener(IWorkerEventListener listener);

//	public void removeEmulatorEventListener(IWorkerEventListener listener);
}
