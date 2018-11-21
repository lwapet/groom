package fr.groom.android_emulator.avd_manager;

public class EmulatorFactory {
	public static Emulator create(int port, String authToken) {
		return new Emulator(port, authToken);
	}
}
