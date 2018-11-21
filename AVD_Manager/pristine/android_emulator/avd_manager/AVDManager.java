package fr.groom.android_emulator.avd_manager;

import fr.groom.commandline_handler.ADB;
import fr.groom.commandline_handler.CommandHandler;
import fr.groom.commandline_handler.NewLineListener;

import java.util.ArrayList;
import java.util.Optional;

public class AVDManager {
	private static ArrayList<AVD> existingAVDs;
	public static ArrayList<AVD> runningAVDs;

	public static ArrayList<AVD> getExistingAVDs() {
		ArrayList<String> avdsNames = new ArrayList<>();
		ArrayList<String> command = new ArrayList<>();
		existingAVDs = new ArrayList<>();

		command.add(AVD.pathToEmulatorBinary);
		command.add("-list-avds");
		NewLineListener lineListener = new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				if (line != null) {
					avdsNames.add(line);
				}
			}

			@Override
			public void handleErrorLine(String line) {
			}
		};
		CommandHandler.runCommand(command, lineListener);
		avdsNames.forEach(avdName -> {
			AVD avd = new AVD(avdName);
			existingAVDs.add(avd);
		});
		return existingAVDs;
	}

	public static ArrayList<AVD> getRunningAVDs() {
		ArrayList<String> command = new ArrayList<>();
		ArrayList<String> runningDevicesNames = new ArrayList<>();
		ArrayList<AVD> runningAVDs = new ArrayList<>();
		command.add("devices");
		NewLineListener lineListener = new NewLineListener() {
			@Override
			public void handleInputLine(String line) {
				if (line.contains("emulator")) {
					runningDevicesNames.add(line);
				}
			}

			@Override
			public void handleErrorLine(String line) {

			}
		};
		ADB.runCommand(command, lineListener);
		runningDevicesNames.forEach(runningDevice -> {
			String avdName = runningDevice.split("\t")[0];
			int port = Integer.parseInt(avdName.replace("emulator-", ""));
			IEmulator emulator = EmulatorFactory.create(port, "PPXrp5UXxqBltUnW");
			emulator.connect();
			AVD avd = new AVD(emulator.getAVDControl().name(), avdName, port);
			emulator.disconnect();
			runningAVDs.add(avd);
		});
		return runningAVDs;
	}

	public static Integer getUnusedPort() {
		if (getUsedPorts().isEmpty()) {
			return 5554;
		} else {
			Integer max = getUsedPorts().stream().reduce(Integer::max).get();
			if (max % 2 == 0) {
				max += 1;
			}
			return max + 1;
		}
	}

	public static ArrayList<Integer> getUsedPorts() {
		ArrayList<AVD> runningAvds = getRunningAVDs();
		ArrayList<Integer> ports = new ArrayList<>();
		for (AVD avd : runningAvds) {
			ports.add(avd.getPort());
		}
		return ports;
	}

	public static AVD getOrStartAVD(String name, IAVDEventListener eventListener) {
		Optional<AVD> optionalAVD = getRunningAVDs().stream().filter(avd -> avd.getName().equals(name)).findFirst();
		if (optionalAVD.isPresent()) {
			System.out.println("avd_manager.AVD " + name + " is already running.");
			AVD avd = optionalAVD.get();
			avd.addListener(eventListener);
			eventListener.onReady(avd);
			return avd;
		} else {
			AVD avd = new AVD(name);
			System.out.println("Starting avd_manager.AVD " + name + ".");
			avd.addListener(eventListener);
			avd.start();
			return avd;
		}
	}


	public AVD createAVD() {
		return null;
	}

	public AVD get(String name) {
		return null;
	}
}
