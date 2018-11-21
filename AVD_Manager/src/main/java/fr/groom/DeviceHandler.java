package fr.groom;

import com.android.ddmlib.IDevice;

public class DeviceHandler {
	IDevice device;
	boolean busy = false;

	public DeviceHandler(IDevice device) {
		this.device = device;
	}

	public IDevice getDevice() {
		return device;
	}

	public boolean isBusy() {
		return busy;
	}

	public void setBusy(boolean busy) {
		this.busy = busy;
	}
}
