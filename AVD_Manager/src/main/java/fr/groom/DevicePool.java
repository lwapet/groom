package fr.groom;

import com.android.ddmlib.*;

import java.util.ArrayList;

public class DevicePool implements AndroidDebugBridge.IDeviceChangeListener, AndroidDebugBridge.IClientChangeListener {
	ArrayList<IWorker> workers;

	public DevicePool() {
		this.workers = new ArrayList<>();
	}

	@Override
	public void deviceConnected(IDevice device) {
//		ThreadInfo threadInfo = ThreadInfo


		Client[] clients = device.getClients();
		System.out.println(device.hasClients());
		System.out.println("cçc");
	}

	@Override
	public void deviceDisconnected(IDevice device) {

	}

	@Override
	public void deviceChanged(IDevice device, int changeMask) {

	}

	@Override
	public void clientChanged(Client client, int changeMask) {
		System.out.println("ici");
	}
}
