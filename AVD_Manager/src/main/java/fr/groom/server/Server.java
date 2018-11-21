package fr.groom.server;


import fr.groom.EmulatorPool;
import fr.groom.server.EmulatorPoolHandlers.GetDevicesHandler;
import fr.groom.server.EmulatorPoolHandlers.InstallPackageHandler;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.RoutingHandler;

import java.io.IOException;

public class Server {
	EmulatorPool pool;

	public Server(EmulatorPool pool) {
		this.pool = pool;
	}


	public void start() throws IOException {

		Undertow server = Undertow.builder()
				.addHttpListener(8080, "localhost")
				.setHandler(Handlers.path()
						.addPrefixPath("/api", Handlers.routing()
								.get("/devices", new GetDevicesHandler(pool))
								.post("/installpackage", new InstallPackageHandler(pool))
						)
				).build();
		server.start();
	}
}
