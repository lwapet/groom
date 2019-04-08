package fr.groom;

import fr.groom.configuration.ObfuscatorConfiguration;

public class Configuration {
	private static ObfuscatorConfiguration INSTANCE;

	public static ObfuscatorConfiguration v() {
		return INSTANCE;
	}

	public static void setINSTANCE(ObfuscatorConfiguration INSTANCE) {
		Configuration.INSTANCE = INSTANCE;
	}

	public static ObfuscatorConfiguration setDefaultINSTANCE() {
		INSTANCE = new ObfuscatorConfiguration();
		return INSTANCE;
	}
}
