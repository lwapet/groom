package fr.groom.android_emulator.avd_manager;

import java.util.ArrayList;

public interface IAVDManager {
	public ArrayList<AVD> getExistingAVDs();
	public ArrayList<AVD> getRunningAVDs();
	public AVD createAVD();
	public AVD get(String name);
}
