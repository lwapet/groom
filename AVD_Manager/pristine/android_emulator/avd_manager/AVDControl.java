package fr.groom.android_emulator.avd_manager;

public class AVDControl implements IAVDControl {
	private ControlChannel controlChannel;

	public AVDControl(ControlChannel controlChannel) {
		this.controlChannel = controlChannel;
	}

	@Override
	public boolean stop() {
		controlChannel.write("avd stop");
		return controlChannel.read().equals("OK");
	}

	@Override
	public boolean start() {
		controlChannel.write("avd start");
		return controlChannel.read().equals("OK");
	}

	@Override
	public boolean restart() {
		controlChannel.write("avd start");
		return controlChannel.read().equals("OK");
	}

	@Override
	public boolean status() {
		return false;
	}

	@Override
	public String name() {
		controlChannel.write("avd name");
		String name = controlChannel.read();
		if (name.startsWith("KO:")) {
			return name;
		}
		if(name.equals("OK")) {
			name = controlChannel.read();
		}
		controlChannel.read();
		return name;
	}
}
