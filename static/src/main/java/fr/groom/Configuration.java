package fr.groom;

import fr.groom.configuration.FridaInstrumenterConfiguration;
import fr.groom.configuration.InstrumenterConfiguration;
import fr.groom.configuration.SootConfiguration;

public class Configuration {
	private static InstrumenterConfiguration INSTANCE;

	public static InstrumenterConfiguration v() {
		return INSTANCE;
	}

	public static void setINSTANCE(InstrumenterConfiguration INSTANCE) {
		Configuration.INSTANCE = INSTANCE;
	}

	public static InstrumenterConfiguration setDefaultINSTANCE() {
		INSTANCE = new InstrumenterConfiguration();
		INSTANCE.setSootConfiguration(new SootConfiguration());
		INSTANCE.setFridaInstrumenterConfiguration(new FridaInstrumenterConfiguration());
		return INSTANCE;
	}
}
