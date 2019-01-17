package fr.groom;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.Client;
import com.android.ddmlib.IDevice;
import com.android.sdklib.internal.avd.AvdInfo;
import com.android.sdklib.internal.avd.AvdManager;
import fr.groom.models.App;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class WorkerPool extends WorkerEventListener implements AndroidDebugBridge.IDeviceChangeListener, AndroidDebugBridge.IDebugBridgeChangeListener {
	//	private Set<IWorker> workers;
	private Queue<App> apps = new LinkedBlockingQueue<>();

	WorkerPool(ArrayList<App> applicationCollection) {
		this.apps.addAll(applicationCollection);
//		this.workers = new HashSet<>();
	}

	@Override
	public void deviceConnected(IDevice device) {
		new Worker(device, this);
//		workers.add(new Worker(device));
	}

	@Override
	public void deviceDisconnected(IDevice device) {
//		workers.removeIf(worker -> worker.getDevice().getSerialNumber().equals(device.getSerialNumber()));
	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {

	}

	@Override
	public void onIdle(IWorker worker) {
		App app = apps.poll();
		if (app == null)
			return;
		new DynamicAnalysis(worker, app).run();
	}

	@Override
	public void onBusy(IWorker worker) {
	}

	@Override
	public void bridgeChanged(AndroidDebugBridge bridge) {
	}
}
