package fr.groom;

import com.android.ddmlib.*;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.ddmlib.logcat.LogCatReceiverTask;
import com.mongodb.Mongo;
import com.mongodb.client.MongoDatabase;
import fr.groom.models.App;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class LogWorkerEventListener extends DynamicAnalysis {
	Thread logcatThread;
	boolean alreadyUninstalled = false;
	MongoDatabase mongoDatabase;

	public LogWorkerEventListener(IWorker worker, App app, MongoDatabase mongoDatabase) {
		super(worker, app);
		this.mongoDatabase = mongoDatabase;
		Runnable logcat = () -> {
			LogCatReceiverTask logCatReceiverTask = new LogCatReceiverTask(worker.getDevice());
			logCatReceiverTask.addLogCatListener(msgList -> {
				for (LogCatMessage message : msgList) {
					if (message.getHeader().getTag().equals("KILLERDROID")) {
						if (message.getMessage().equals("MALWARE TRIGGERED")) {
							Document update = new Document();
							Document updateFields = new Document();
							updateFields.append("execution", true);
							update.append("$set", updateFields);
							Document filter = new Document();
							filter.append("sha256", app.getSha256());
							mongoDatabase.getCollection("killerdroid_log").updateOne(filter, update);
//								Writer output = new BufferedWriter(new FileWriter("./results.txt", true));
//								output.append(app.getSha256() + "," + "true\n");
//								output.close();
							logCatReceiverTask.stop();
							alreadyUninstalled = true;
							worker.uninstallApk(app.getPackageName());
						}
					}
				}
			});
			logCatReceiverTask.run();
		};
		logcatThread = new Thread(logcat);
	}

	@Override
	public void onInstallApk(IWorker worker) {
		worker.cleanLogcat();
		Document update = new Document();
		Document updateFields = new Document();
		updateFields.append("installed", true);
		update.append("$set", updateFields);
		Document filter = new Document();
		filter.append("sha256", app.getSha256());
		mongoDatabase.getCollection("killerdroid_log").updateOne(filter, update);
		this.logcatThread.start();
		worker.startApp(app);
	}

	@Override
	public void onStartApk(IWorker worker) {
		try {
			Thread.sleep(DynamicAnalysis.EXECUTION_DURATION);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		if (!alreadyUninstalled) {
			try {
				Writer output = new BufferedWriter(new FileWriter("./results.txt", true));
				output.append(app.getSha256() + "," + "false\n");
				output.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			worker.uninstallApk(app.getPackageName());
		}
	}
}
