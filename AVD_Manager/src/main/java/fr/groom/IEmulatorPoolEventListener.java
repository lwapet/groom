package fr.groom;

public interface IEmulatorPoolEventListener {

	public void onNewIdleEmulator(Emulator emulator);

	public void onNewBusyEmulator(Emulator emulator);

	public void onNewEmulator(Emulator emulator);

	public void onRemovedEmulator(Emulator emulator);
}
