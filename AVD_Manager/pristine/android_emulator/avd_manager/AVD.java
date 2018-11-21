package fr.groom.android_emulator.avd_manager;

import fr.groom.commandline_handler.ADB;
import fr.groom.commandline_handler.CommandHandler;
import fr.groom.commandline_handler.NewLineListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

public class AVD implements IAVD {
	public static String pathToAdbBinary = "/Users/lgitzing/Library/Android/sdk/platform-tools/adb";
	public static String pathToEmulatorBinary = "/Users/lgitzing/Library/Android/sdk/emulator/emulator";
	public static String AUTH_TOKEN = "PPXrp5UXxqBltUnW";
	private String name;
	private String adbName;
	private boolean running;
	private int port;
	private ArrayList<IAVDEventListener> listeners;


	AVD(String name) {
		this.name = name;
		this.listeners = new ArrayList<>();
	}

	AVD(String name, String adbName, int port) {
		this(name);
		this.adbName = adbName;
		this.port = port;
		this.running = true;
	}

	@Override
	public void start() {
		if (AVDManager.getRunningAVDs().stream().anyMatch(avd -> avd.getName().equals(this.name))) {
			throw new IllegalStateException("avd_manager.AVD already running");
		}

		AVD currentAVD = this;
		Runnable startAvd = () -> {
			ArrayList<String> command = new ArrayList<>();
			command.add(pathToEmulatorBinary);
			command.add("-avd");
			command.add(this.name);
			command.add("-no-boot-anim");
			command.add("-read-only");
			command.add("-port");
			if (this.port == 0) {
				this.port = AVDManager.getUnusedPort();
			}
			command.add(String.valueOf(this.port));
			command.add("-no-snapshot-load");
			command.add("-no-snapshot-save");
			CommandHandler.runCommand(command, new NewLineListener() {
				@Override
				public void handleInputLine(String line) {
					System.out.println(line);
					if (line.contains("INFO: boot completed")) {
						Optional<AVD> optionalAVD = AVDManager
								.getRunningAVDs()
								.stream()
								.filter(avd -> avd.getName().equals(currentAVD.name))
								.findFirst();
						if (optionalAVD.isPresent()) {
							AVD foundAVD = optionalAVD.get();
							currentAVD.adbName = foundAVD.adbName;
							currentAVD.port = foundAVD.port;
						} else {
							AVDManager.getRunningAVDs().add(currentAVD);
						}
						listeners.forEach(l -> l.onStart(currentAVD, port));
						listeners.forEach(l -> l.onReady(currentAVD));
					}
					if (line.contains("ERROR")) {
						listeners.forEach(l -> l.onStartError(currentAVD, line));
					}
				}

				@Override
				public void handleErrorLine(String line) {
					listeners.forEach(l -> l.onStartError(currentAVD, line));
				}
			});
			AVDManager.getRunningAVDs().removeIf(avd -> avd.getName().equals(currentAVD.name));
			listeners.forEach(l -> l.onStop(currentAVD));
		};
		Thread startThread = new Thread(startAvd);
		startThread.start();
	}

	@Override
	public void stop() {
		AVD currentAVD = this;
		ArrayList<String> command = new ArrayList<>();
		command.add(pathToAdbBinary);
		command.add("emu");
		command.add("kill");
		CommandHandler.runCommand(command, new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
			}

			@Override
			public void handleErrorLine(String line) {
				listeners.forEach(l -> l.onStopError(currentAVD));
			}
		});
	}

	@Override
	public void installApk(File apk, boolean forceInstall) {
		AVD currentAVD = this;
		if (!apk.exists()) {
			System.err.println("Apk file doesn't exists");
			return;
		}
		ArrayList<String> command = new ArrayList<>();
		command.add("install");
		if (forceInstall) {
			command.add("-r");
		}
		command.add(apk.getAbsolutePath());
		System.out.println("Installing apk located at: " + apk.getAbsolutePath());
		ADB.runDeviceCommand(this.adbName, command, new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				System.out.println(line);
				if (line.equals("Success")) {
					System.out.println("Apk correctly installed.");
					listeners.forEach(l -> l.onInstallApk(currentAVD));
				}
			}

			@Override
			public void handleErrorLine(String line) {
				listeners.forEach(l -> l.onInstallApkFailed(currentAVD, line));
			}
		});
	}

	@Override
	public void uninstallApk(String packageName) {
		AVD currentAVD = this;
		ArrayList<String> command = new ArrayList<>();
		command.add("uninstall");
		command.add(packageName);
		ADB.runDeviceCommand(this.adbName, command, new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				if (line.equals("Success")) {
					System.out.println("Apk correctly uninstalled.");
					listeners.forEach(l -> l.onUninstallApk(currentAVD));
				}
				if (line.equals("Failure [DELETE_FAILED_INTERNAL_ERROR]")) {
					listeners.forEach(l -> l.onUninstallApkError(currentAVD, line));
				}
			}

			@Override
			public void handleErrorLine(String line) {
				listeners.forEach(l -> l.onUninstallApkError(currentAVD, line));
			}
		});
	}

	@Override
	public void startApk(String packageName, String mainActivity, boolean fromLauncher) {
		AVD currentAVD = this;
		ArrayList<String> command = new ArrayList<>();
		command.add("shell");
		command.add("am");
		command.add("start");
		command.add("-n");
		command.add(packageName + "/" + mainActivity);
		if (fromLauncher) {
			command.add("-a");
			command.add("android.intent.action.MAIN");
			command.add("-c");
			command.add("android.intent.category.LAUNCHER");
		}

		ADB.runDeviceCommand(this.adbName, command, new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				System.out.println(line);
				listeners.forEach(l -> l.onStartApk(currentAVD));
			}

			@Override
			public void handleErrorLine(String line) {
				System.out.println(line);
				listeners.forEach(l -> l.onStartApkError(currentAVD, line));
			}
		});
	}

	@Override
	public void stopApk(String packageName) {
		AVD currentAVD = this;
		ArrayList<String> command = new ArrayList<>();
		command.add("shell");
		command.add("am");
		command.add("force-stop");
		command.add(packageName);
		ADB.runDeviceCommand(this.adbName, command, new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				// No output here, forced to trigger the listerner after running
				// the command (below)
			}

			@Override
			public void handleErrorLine(String line) {
				System.out.println(line);
				listeners.forEach(l -> l.onStopApkError(currentAVD));
			}
		});
		listeners.forEach(l -> l.onStopApk(currentAVD));
	}

	@Override
	public boolean isRunning() {
		Optional<AVD> runningAVDOptional = AVDManager.getRunningAVDs().stream().filter(avd -> avd.getName().equals(this.name)).findFirst();
		if (runningAVDOptional.isPresent()) {
			AVD runningAVD = runningAVDOptional.get();
			this.port = runningAVD.port;
			this.adbName = runningAVD.adbName;
			this.running = true;
		} else {
			this.running = false;
		}
		return this.running;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getAdbName() {
		if (!this.isRunning()) {
			throw new IllegalStateException("Device not running, cannot get adbName");
		}
		return this.adbName;
	}

	@Override
	public int getPort() {
		if (!this.isRunning()) {
			throw new IllegalStateException("Device not running, cannot get port");
		}
		return 0;
	}

	@Override
	public void create() {

	}

	@Override
	public void addListener(IAVDEventListener listener) {
		listeners.add(listener);
	}

}

