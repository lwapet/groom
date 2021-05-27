package fr.groom;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class EmulatorPoolHandler implements HttpHandler {
	EmulatorPool pool;

	public EmulatorPoolHandler(EmulatorPool pool) {
		this.pool = pool;
	}

	@Override
	public void handle(HttpExchange httpExchange) throws IOException {
		String response = "pool size : " + String.valueOf(pool.getIdleEmulators().size());
		httpExchange.sendResponseHeaders(200, response.length());
		OutputStream os = httpExchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}
