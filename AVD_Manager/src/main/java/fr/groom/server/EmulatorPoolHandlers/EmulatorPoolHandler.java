package fr.groom.server.EmulatorPoolHandlers;

import fr.groom.EmulatorPool;
import io.undertow.server.HttpHandler;

public abstract class EmulatorPoolHandler implements HttpHandler {
	EmulatorPool pool;

	public EmulatorPoolHandler(EmulatorPool pool) {
		this.pool = pool;
	}
}
