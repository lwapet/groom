package fr.groom;

import fr.groom.mongo.Database;
import fr.groom.utils.logs.ILogListener;
import fr.groom.utils.logs.models.ILog;
import org.json.JSONObject;

public class LogStorer implements ILogListener {
	Storage storage;

	public LogStorer() {
//		this.storage = new Printer();
		this.storage = new Database(
				TcpServerConfiguration.v().databaseUrl,
				TcpServerConfiguration.v().databasePort,
				TcpServerConfiguration.v().databaseName,
				TcpServerConfiguration.v().performAuth,
				TcpServerConfiguration.v().username,
				TcpServerConfiguration.v().password,
				TcpServerConfiguration.v().authSourceDatabaseName
		);
	}

	@Override
	public void onNewLog(ILog log) {
		storage.insertData(new JSONObject(log.getData().toString()), "dynamic");
	}
}
