package fr.groom;

import fr.groom.models.App;

import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class DynamicAnalysisManager extends EmulatorPoolEventListener {
	EmulatorPool pool;
	Queue<App> apps;

	public DynamicAnalysisManager(EmulatorPool pool) {
		this.pool = pool;
		this.apps = new LinkedBlockingQueue<>();
	}


	@Override
	public void onNewIdleEmulator(Emulator emulator) {
		System.out.println(emulator.getDevice().getName());
		App app = apps.poll();
		if (app != null) {
			DynamicAnalysis analysis = new DynamicAnalysis(emulator, app);
			analysis.run();
		}
	}

	public void addApp(App app) {
		apps.add(app);
		ArrayList<Emulator> idleEmulators = pool.getIdleEmulators();
		if(idleEmulators != null && idleEmulators.size() != 0) {
			new DynamicAnalysis(idleEmulators.get(0), apps.poll()).run();
		}
	}
}
