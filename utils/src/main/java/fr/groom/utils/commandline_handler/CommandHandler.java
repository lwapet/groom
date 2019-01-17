package fr.groom.utils.commandline_handler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class CommandHandler {

	public static void runCommand(ArrayList<String> command, NewLineListener newLineListener) {
		ProcessBuilder processBuilder = new ProcessBuilder(command);
		try {
			Process process = processBuilder.start();
			readProcessInputStream(process, newLineListener);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static void readProcessInputStream(Process process, NewLineListener newLineListener) {
		try {

			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				newLineListener.handleInputLine(line);
			}
			bufferedReader.close();

			BufferedReader bufferedReaderError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
			String errorLine;
			while ((errorLine = bufferedReaderError.readLine()) != null) {
				newLineListener.handleErrorLine(errorLine);
			}
			bufferedReaderError.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

