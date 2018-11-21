package fr.groom;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class Server {
	private HttpServer server;

	public static final Routing

	private void start() throws IOException {
		server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/test", new EmulatorPoolHandler(pool));
		server.setExecutor(null); // creates a default executor
		server.start();
	}
}
