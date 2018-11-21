package fr.groom.logs;

import fr.groom.logs.models.ILog;

public interface ILogListener {
	public void onNewLog(ILog log);
}
