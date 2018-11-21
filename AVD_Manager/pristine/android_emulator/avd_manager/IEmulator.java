package fr.groom.android_emulator.avd_manager;

public interface IEmulator {
	public int getPort();

	public void connect();

	public void disconnect();

	public AVDControl getAVDControl();
}
