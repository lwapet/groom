package fr.groom;


import fr.groom.utils.commandline_handler.NewLineListener;

import java.io.*;
import java.net.Socket;

public class SocketThread extends Thread {
		protected Socket socket;
	protected NewLineListener newLineListener;

	public SocketThread(Socket socket, NewLineListener newLineListener) {
		this.socket = socket;
		this.newLineListener = newLineListener;
	}


	@Override
	public void run() {
		InputStream inputStream;
		BufferedReader brinput;
		DataOutputStream out = null;
		try {
			inputStream = socket.getInputStream();
			brinput = new BufferedReader(new InputStreamReader(inputStream));
			out = new DataOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			return;
		}
		String line;
		while (true) {
			try {
				line = brinput.readLine();
				if ((line == null) || line.equalsIgnoreCase("QUIT")) {
					socket.close();
					return;
				} else {
					newLineListener.handleInputLine(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
