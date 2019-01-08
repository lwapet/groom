package fr.groom;

import com.android.ddmlib.*;
import com.android.ddmlib.logcat.LogCatReceiverTask;
//import com.android.instantapp.utils.LogcatService;
//import com.android.sdklib.devices.Device;
import com.android.sdklib.internal.avd.AvdInfo;
import fr.groom.commandline_handler.CommandHandler;
import fr.groom.commandline_handler.NewLineListener;
import fr.groom.models.App;
import fr.groom.server.EmulatorStatus;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Emulator {
	private Set<IEmulatorEventListener> listeners;
	private AvdInfo avd;
	private IDevice device;
	private int port;
	private EmulatorStatus status = EmulatorStatus.BUSY;

	public Emulator(AvdInfo avd) {
		this.avd = avd;
		listeners = new HashSet<>();
	}

	public void setNewStatus(EmulatorStatus status) {
		this.status = status;
		System.out.println(device.getName() + " " + status + " - Thread: " + Thread.currentThread().getId());
		listeners.forEach(l -> l.onStatusChange(this, status));
	}

	public Emulator(AvdInfo avd, IDevice device) {
		this(avd);
		this.device = device;
		this.port = getPortFromSerialNumber(device.getSerialNumber());
		logConnection();
		LogCatReceiverTask logCatReceiverTask = new LogCatReceiverTask(device);
//		logCatReceiverTask.addLogCatListener(list -> {
//			for(LogCatMessage logCatMessage : list) {
//				if(logCatMessage.getMessage().contains("(success)")) {
//					System.out.println(logCatMessage.getMessage());
//				}
//			}
//		});
//		logCatReceiverTask.run();
	}

	private void logConnection() {
		if (device.isOnline()) {
			System.out.println("Connected to emulator : " + device.getName());
		} else {
			System.out.println("Not connected to emulator : " + device.getName());
		}
	}

	public void installApk(File apk, boolean forceInstall)  {
		setNewStatus(EmulatorStatus.BUSY);
		InstallReceiver installReceiver = new InstallReceiver();
		System.out.println("Trying to install apk with the following path:" + apk.getAbsolutePath());
		try {
			device.installPackage(apk.getAbsolutePath(), forceInstall, installReceiver);
		} catch (InstallException e) {
			e.printStackTrace();
			System.out.println("failed install for apk: " + apk.getAbsolutePath());
		}
		HashSet<IEmulatorEventListener> clone = new HashSet<>(listeners);
		if (!installReceiver.isSuccessfullyCompleted()) {
			clone.forEach(l -> l.onInstallApkFailed(this, installReceiver.getErrorMessage()));
		} else {
			clone.forEach(l -> l.onInstallApk(this));
		}
	}

	public void uninstallApk(String packageName) {
		HashSet<IEmulatorEventListener> clone = new HashSet<>(listeners);
		try {
			String result = device.uninstallPackage(packageName);
			if (result == null) {
				clone.forEach(l -> l.onUninstallApk(this));
				setNewStatus(EmulatorStatus.IDLE);
			} else {
				clone.forEach(l -> l.onUninstallApkError(this, result));
			}
		} catch (InstallException e) {
			e.printStackTrace();
			clone.forEach(l -> l.onUninstallApkError(this, e.getMessage()));
		}
	}

	public void stop() {
		EmulatorConsole.getConsole(device).kill();
	}

	public void startApp(App app) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("am ");
		stringBuilder.append("start ");
		stringBuilder.append("-n ");
		stringBuilder.append(app.getPackageName());
		stringBuilder.append("/");
		stringBuilder.append(app.getMainActivity());
		stringBuilder.append(" -a ");
		stringBuilder.append("android.intent.action.MAIN ");
		stringBuilder.append("-c ");
		stringBuilder.append("android.intent.category.LAUNCHER");
		try {
			this.getDevice().executeShellCommand(stringBuilder.toString(), new MultiLineReceiver() {
				@Override
				public void processNewLines(String[] strings) {
					for (String string : strings) {
						System.out.println(string);
					}
				}

				@Override
				public boolean isCancelled() {
					return false;
				}
			});
		} catch (TimeoutException | AdbCommandRejectedException | IOException | ShellCommandUnresponsiveException e) {
			e.printStackTrace();
		}

	}

	public void start() {
		Emulator current = this;
		Runnable startAvd = () -> {
			ArrayList<String> command = new ArrayList<>();
			command.add(AVDConfiguration.emulatorPath);
			command.add("-avd");
			command.add(avd.getName());
			command.add("-no-boot-anim");
			command.add("-read-only");
//			command.add("-port");
//			command.add(String.valueOf(this.port));
			command.add("-no-snapshot-load");
			command.add("-no-snapshot-save");
			command.add("-wipe-data");
			command.add("-verbose");
			CommandHandler.runCommand(command, new NewLineListener() {
				@Override
				public void handleInputLine(String line) {
					if (line.contains("emulator: control console listening on")) {
						Pattern p = Pattern.compile("port (.*?),");
						Matcher m = p.matcher(line);
						if (m.find()) {
							current.port = Integer.valueOf(m.group(1));
						}
					}
					if (line.contains("INFO: boot completed")) {
						logConnection();
						setNewStatus(EmulatorStatus.IDLE);
						listeners.forEach(l -> l.onStart(current));
						listeners.forEach(l -> l.onReady(current));
					}
					if (line.contains("ERROR")) {
						listeners.forEach(l -> l.onStartError(current, line));
					}
				}

				@Override
				public void handleErrorLine(String line) {
					listeners.forEach(l -> l.onStartError(current, line));
				}
			});
//			AVDManager.getRunningAVDs().removeIf(avd -> avd.getName().equals(currentAVD.name));
//			listeners.forEach(l -> l.onStop(currentAVD));
		};
		Thread startThread = new Thread(startAvd);
		startThread.start();
	}

	public static int getPortFromSerialNumber(String serialNumber) {
		String[] split = serialNumber.split("-");
		return Integer.valueOf(split[1]);
	}

	public void addEmulatorEventListener(IEmulatorEventListener listener) {
		listeners.add(listener);
	}

	public void removeEmulatorEventListener(IEmulatorEventListener listener) {
		listeners.remove(listener);
	}

	public AvdInfo getAvd() {
		return avd;
	}

	public IDevice getDevice() {
		return device;
	}

	public int getPort() {
		return port;
	}

	public void setDevice(IDevice device) {
		this.device = device;
	}

	public boolean isIdle() {
		return status.equals(EmulatorStatus.IDLE);
	}
}
