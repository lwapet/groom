package fr.groom;

import fr.groom.commandline_handler.NewLineListener;
import fr.groom.logs.HookConstant;
import fr.groom.logs.ILogListener;
import fr.groom.logs.models.Log;

import java.util.ArrayList;

public class TcpParser implements NewLineListener {
	private ArrayList<Log> logStorage;
	private ArrayList<ILogListener> logListeners;


	public TcpParser() {
		logStorage = new ArrayList<>();
		logListeners = new ArrayList<>();
	}

	public ArrayList<Log> getLogStorage() {
		return logStorage;
	}

	@Override
	public void handleInputLine(String line) {
		if (line.contains(HookConstant.MAIN_PATTERN.getValue())) {
			Log log = new Log(line);
			log.parse();
			logListeners.forEach(l -> l.onNewLog(log));
//			logStorage.add(log);
		}
	}

	public void addLogListener(ILogListener logListener) {
		logListeners.add(logListener);
	}


	@Override
	public void handleErrorLine(String line) {

	}
}
