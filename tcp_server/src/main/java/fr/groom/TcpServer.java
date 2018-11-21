package fr.groom;

import fr.groom.commandline_handler.NewLineListener;
import fr.groom.commandline_handler.SimplePrintLineListener;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpServer implements Runnable {
	private NewLineListener reader;
	static final String SERVER_TAG = "TCP_SERVER";
	private static final int PORT = 1993;
	ServerSocket serverSocket;
	Socket socket;

	public TcpServer() {
		this.reader = new SimplePrintLineListener();
	}

	public TcpServer(NewLineListener reader) {
		this.reader = reader;
	}

	private void createServerSocket() {
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void accept() {
		while(true) {
			System.out.println("Accepting connections");
			try {
				socket = serverSocket.accept();
			} catch (IOException e) {
				e.printStackTrace();
			}
			new SocketThread(socket, reader).start();
		}
	}

	@Override
	public void run() {
		createServerSocket();
		accept();
	}


}
