package fr.groom;

import com.android.ddmlib.*;
import com.android.ddmlib.logcat.LogCatListener;
import com.android.ddmlib.logcat.LogCatMessage;
import com.android.ddmlib.logcat.LogCatReceiverTask;
import fr.groom.models.App;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class LogWorkerEventListener extends DynamicAnalysis {
	Thread logcatThread;
	boolean alreadyUninstalled = false;

	public LogWorkerEventListener(IWorker worker, App app) {
		super(worker, app);
		Runnable logcat = () -> {
			LogCatReceiverTask logCatReceiverTask = new LogCatReceiverTask(worker.getDevice());
			logCatReceiverTask.addLogCatListener(msgList -> {
				for (LogCatMessage message : msgList) {
					if (message.getHeader().getTag().equals("KILLERDROID")) {
						if (message.getMessage().equals("MALWARE TRIGGERED")) {
							try {
								Writer output = new BufferedWriter(new FileWriter("./results.txt", true));
								output.append(app.getSha256() + "," + "true\n");
								output.close();
								logCatReceiverTask.stop();
								alreadyUninstalled = true;
								worker.uninstallApk(app.getPackageName());
							} catch (IOException e) {
								e.printStackTrace();
							}
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
