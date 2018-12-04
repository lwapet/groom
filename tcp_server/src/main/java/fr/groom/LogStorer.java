package fr.groom;

import com.google.gson.JsonObject;
import fr.groom.logs.ILogListener;
import fr.groom.logs.models.ILog;
import fr.groom.mongo.Database;
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
