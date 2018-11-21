package fr.groom.android_emulator.avd_manager;

import sun.net.TelnetInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.charset.Charset;

public class ControlChannel {
	private static int NO_MORE_DATA_TIMEOUT = 8000;
	private int port;
	private String authToken;
	private InputStream inputChannel;
	private OutputStream outputChannel;
	private Socket communicationClient;
	private boolean open = false;

	public ControlChannel(int port, String authToken) {
		this.port = port;
		this.authToken = authToken;
	}

	public void open() {
		if (open) throw new IllegalStateException("Channel is already open");
		try {
			openChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close() {
		if (!open) return;
		closeChannel();
		open = false;
	}

	private void closeChannel() {
		write("quit");
		try {
			this.communicationClient.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void openChannel() throws IOException {
		this.communicationClient = new Socket(InetAddress.getLoopbackAddress(), this.port);
		communicationClient.setSoTimeout(NO_MORE_DATA_TIMEOUT);
		inputChannel = new TelnetInputStream(this.communicationClient.getInputStream(), true);
		outputChannel = this.communicationClient.getOutputStream();

		consumeInitialData();
	}

	public void write(String data) {
		try {
			this.outputChannel.write(data.getBytes(Charset.forName("US-ASCII")));
			this.outputChannel.flush();
			this.outputChannel.write(0x0D);
			this.outputChannel.write(0x0A);
			this.outputChannel.flush();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public String read() {
		char character;
		StringBuilder stringBuilder = new StringBuilder();
		while (true) {
			try {
				character = (char) this.inputChannel.read();
				if (character == -1) {
					break;
				}
				if (character == 0x0D) {
					this.inputChannel.read();
					break;
				}
				stringBuilder.append(character);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return stringBuilder.toString().trim();
	}

	private void consumeInitialData() {
		boolean mustAuth = false;
		while (true) {
			String line = read();
			if (line.matches(".* Authentication required")) {
				mustAuth = true;
			} else if (line.equals("OK")) {
				break;
			}
		}

		if (mustAuth) {
			doAuth();
		}
	}

	private void doAuth() {
		this.authToken = "PPXrp5UXxqBltUnW";
		write("auth " + this.authToken);
		boolean authOk = false;
		String offendingLine = null;
		while (true) {
			String line = read();
			if (line.matches("NOK.*")) {
				offendingLine = line;
				break;
			} else if (line.equals("OK")) {
				authOk = true;
				break;
			}
		}
		if (!authOk) {
			try {
				throw new IOException("Failed to authenticate: " + offendingLine);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}


}
