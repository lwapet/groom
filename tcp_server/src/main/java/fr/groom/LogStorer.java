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
				"localhost",
				27017,
				"dynamic",
				false,
				null,
				null,
				null
		);
	}

	@Override
	public void onNewLog(ILog log) {
		storage.insertData(new JSONObject(log.getData().toString()),"dynamic");
	}
}
