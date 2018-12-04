package fr.groom.commandline_handler;
import fr.groom.Configuration;

import java.util.ArrayList;

public class ADB {
	public static String pathToAdbBinary = Configuration.v().adbPath;

	private static ArrayList<String> prepareCommand() {
		ArrayList<String> command = new ArrayList<>();
		command.add(pathToAdbBinary);
		return command;
	}

	public static void runDeviceCommand(String deviceName, ArrayList<String> command, NewLineListener newLineListener) {
		ArrayList<String> fullCommand = prepareCommand();
		fullCommand.add("-s");
		fullCommand.add(deviceName);
		fullCommand.addAll(command);

		runAdbCommand(fullCommand, newLineListener);
	}

	public static void runCommand(ArrayList<String> command, NewLineListener newLineListener) {
		ArrayList<String> fullCommand = prepareCommand();
		fullCommand.addAll(command);
		runAdbCommand(fullCommand, newLineListener);
	}

	private static void runAdbCommand(ArrayList<String> command, NewLineListener newLineListener) {
		CommandHandler.runCommand(command, newLineListener);
	}
}
