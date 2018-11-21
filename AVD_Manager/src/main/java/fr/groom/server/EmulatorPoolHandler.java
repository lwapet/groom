package fr.groom.server;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import org.json.JSONObject;

import javax.ws.rs.core.MediaType;


public class EmulatorPoolHandler implements HttpHandler {
	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
//		String itemId2 = exchange.getQueryParameters().get("itemId").getFirst();
		exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, MediaType.APPLICATION_JSON);
		JSONObject test = new JSONObject();
		test.put("test", "ok");
		exchange.getResponseSender().send(test.toString());
	}
}
