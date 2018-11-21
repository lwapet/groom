package fr.groom.server.EmulatorPoolHandlers;

import fr.groom.EmulatorPool;
import io.undertow.io.Receiver;
import io.undertow.server.HttpServerExchange;

public class InstallPackageHandler extends EmulatorPoolHandler{
	public InstallPackageHandler(EmulatorPool pool) {
		super(pool);
	}

	@Override
	public void handleRequest(HttpServerExchange exchange) throws Exception {
		exchange.getRequestReceiver().receiveFullBytes((exchange1, message) -> {
			System.out.println(new String(message));
		});
	}
}
