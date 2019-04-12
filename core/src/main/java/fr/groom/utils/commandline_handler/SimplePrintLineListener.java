package fr.groom.utils.commandline_handler;

public class SimplePrintLineListener implements NewLineListener{
	@Override
	public void handleInputLine(String line) {
		System.out.println(line);
	}

	@Override
	public void handleErrorLine(String line) {
		System.err.println(line);
	}
}
