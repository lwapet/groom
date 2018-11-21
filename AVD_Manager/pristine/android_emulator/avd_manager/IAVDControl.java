package fr.groom.android_emulator.avd_manager;

public interface IAVDControl {
	public boolean stop();
	public boolean start();
	public boolean restart();
	public boolean status();
	public String name();
}
