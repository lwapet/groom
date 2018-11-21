package fr.groom;

import com.android.ddmlib.*;
import com.android.ddmlib.logcat.*;
import com.android.sdklib.internal.avd.AvdInfo;
import fr.groom.commandline_handler.CommandHandler;
import fr.groom.commandline_handler.NewLineListener;

import java.io.File;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Emulator {
	private static String PATH_TO_EMULATOR_BINARY = "/Users/lgitzing/Library/Android/sdk/emulator/emulator";
	private ArrayList<IEmulatorEventListener> listeners;
	private AvdInfo avd;
	private IDevice device;
	private int port;

	public Emulator(AvdInfo avd) {
		this.avd = avd;
		listeners = new ArrayList<>();
	}

	public Emulator(AvdInfo avd, IDevice device) {
		this(avd);
		this.device = device;
		this.port = getPortFromSerialNumber(device.getSerialNumber());
		System.out.println("Connected to emulator : " + device.getName());
		LogCatReceiverTask logCatReceiverTask = new LogCatReceiverTask(device);
		logCatReceiverTask.addLogCatListener(list -> {
			for(LogCatMessage logCatMessage : list) {
				if(logCatMessage.getMessage().contains("(success)")) {
					System.out.println(logCatMessage.getMessage());
				}
			}
		});
		logCatReceiverTask.run();
	}

	public void installApk(File apk, boolean forceInstall) throws InstallException {
		device.installPackage(apk.getAbsolutePath(), forceInstall);

	}

	public void uninstallApk(String packageName) throws InstallException {
		device.uninstallPackage(packageName);
	}




	public void stop() {
		EmulatorConsole.getConsole(device).kill();
	}

	public void start() {
		Emulator current = this;
		Runnable startAvd = () -> {
			ArrayList<String> command = new ArrayList<>();
			command.add(PATH_TO_EMULATOR_BINARY);
			command.add("-avd");
			command.add(avd.getName());
			command.add("-no-boot-anim");
			command.add("-read-only");
//			command.add("-port");
//			command.add(String.valueOf(this.port));
			command.add("-no-snapshot-load");
			command.add("-no-snapshot-save");
			command.add("-verbose");
			CommandHandler.runCommand(command, new NewLineListener() {
				@Override
				public void handleInputLine(String line) {
//					System.out.println(line);
					if(line.contains("emulator: control console listening on")) {
						Pattern p = Pattern.compile("port (.*?),");
						Matcher m = p.matcher(line);
						if(m.find()) {
							current.port = Integer.valueOf(m.group(1));
						}
					}
					if (line.contains("INFO: boot completed")) {
						System.out.println(current.getDevice().isOnline());
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
}
