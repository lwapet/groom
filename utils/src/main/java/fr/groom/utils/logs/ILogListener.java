package fr.groom.utils.logs;

import fr.groom.utils.logs.models.ILog;

public interface ILogListener {
	public void onNewLog(ILog log);
}
