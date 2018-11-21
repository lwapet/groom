package fr.groom.android_emulator.avd_manager;

public class Emulator implements IEmulator {
	private int port;
	private String authToken;
	private ControlChannel controlChannel;
	private AVDControl avdControl;
	private static String pathToEmulatorBinary = "/Users/lgitzing/Library/Android/sdk/emulator/emulator";
	private boolean running;


	public Emulator(int port, String authToken) {
		this.port = port;
		this.authToken = authToken;
		this.controlChannel = new ControlChannel(port, authToken);
		this.avdControl = new AVDControl(this.controlChannel);
		this.running = false;
	}



	@Override
	public int getPort() {
		return this.port;
	}

	@Override
	public void connect() {
		this.controlChannel.open();
	}

	@Override
	public void disconnect() {
		this.controlChannel.close();
	}

	@Override
	public AVDControl getAVDControl() {
		return this.avdControl;
	}


}
