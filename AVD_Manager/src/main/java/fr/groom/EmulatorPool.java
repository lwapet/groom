package fr.groom;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.EmulatorConsole;
import com.android.ddmlib.IDevice;
import com.android.sdklib.internal.avd.AvdInfo;
import fr.groom.android_emulator.Constants;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;


public class EmulatorPool {

	private AndroidDebugBridge adb;
	private int size;
	private AvdInfo avd;
	private ArrayList<Emulator> emulators = new ArrayList<>();

	private void initAdb() {
		File sdkRoot = new File(System.getenv(Constants.ENV_VAR_ANDROID_SDK_HOME));
		AndroidDebugBridge.initIfNeeded(false);
		AndroidDebugBridge.addDeviceChangeListener(new DeviceChangeListener(this));
		AndroidDebugBridge.addDebugBridgeChangeListener(new AndroidDebugBridgeListener(this));
		adb = AndroidDebugBridge.createBridge("/Users/lgitzing/Library/Android/sdk/platform-tools/adb", true);
	}

	public EmulatorPool(AvdInfo avdInfo, int size) {
		this.avd = avdInfo;
		this.size = size;
	}

	public static EmulatorPool create(AvdInfo avdInfo, int size) {
		return new EmulatorPool(avdInfo, size);
	}

	public void startPool() {
		initAdb();
	}

	private class DeviceChangeListener implements AndroidDebugBridge.IDeviceChangeListener {
		private EmulatorPool pool;

		public DeviceChangeListener(EmulatorPool pool) {
			this.pool = pool;
		}

		@Override
		public void deviceConnected(IDevice iDevice) {
			emulators.stream()
					.filter(e -> e.getPort() == Emulator.getPortFromSerialNumber(iDevice.getSerialNumber()))
					.findFirst().ifPresent(target -> target.setDevice(iDevice));
		}

		@Override
		public void deviceDisconnected(IDevice iDevice) {
		}

		@Override
		public void deviceChanged(IDevice iDevice, int i) {
		}
	}

	private class AndroidDebugBridgeListener implements AndroidDebugBridge.IDebugBridgeChangeListener {
		EmulatorPool pool;

		public AndroidDebugBridgeListener(EmulatorPool pool) {
			this.pool = pool;
		}

		@Override
		public void bridgeChanged(AndroidDebugBridge androidDebugBridge) {
			if (!androidDebugBridge.isConnected()) {
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			if (!androidDebugBridge.isConnected())
				return;
			Arrays.stream(androidDebugBridge.getDevices()).forEach(d -> {
				Emulator emulator = new Emulator(pool.avd, d);
				if (!emulator.getDevice().getAvdName().equals(avd.getName())) {
					EmulatorConsole.getConsole(emulator.getDevice()).kill();
				} else {
					pool.emulators.add(emulator);
				}
			});
			if (androidDebugBridge.getDevices().length < pool.size) { // need to start new emulators
				while (pool.emulators.size() != pool.size) {
					Emulator emulator = new Emulator(pool.avd);
					emulator.start();
					pool.emulators.add(emulator);
				}
			}
		}
	}

	public AndroidDebugBridge getAdb() {
		return adb;
	}

	public int getSize() {
		return size;
	}

	public AvdInfo getAvd() {
		return avd;
	}

	public ArrayList<Emulator> getEmulators() {
		return emulators;
	}

	public ArrayList<Emulator> getIdleEmulators() {
		return emulators.stream()
				.filter(e -> e.getDevice().isOnline())
				.collect(Collectors.toCollection(ArrayList::new));
	}
}
