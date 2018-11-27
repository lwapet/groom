package fr.groom;

import com.android.ddmlib.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Main2 {
	static AndroidDebugBridge adb;
	static List<DeviceHandler> deviceHandlers;

	static class MyHandler implements HttpHandler {
		@Override
		public void handle(HttpExchange t) throws IOException {
			String response = "This is the response";
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class StartAvdHandler implements HttpHandler {
		String encoding = "UTF-8";

		@Override
		public void handle(HttpExchange httpExchange) {
			try {
				Headers headers = httpExchange.getRequestHeaders();
				List<String> type = headers.get("Content-Type");
				String bodyString;
				InputStream in = httpExchange.getRequestBody();
				try {
					ByteArrayOutputStream out = new ByteArrayOutputStream();
					byte buf[] = new byte[4096];
					for (int n = in.read(buf); n > 0; n = in.read(buf)) {
						out.write(buf, 0, n);
					}
					bodyString = new String(out.toByteArray(), encoding);
				} finally {
					in.close();
				}
				JsonParser parser = new JsonParser();
				JsonObject body = parser.parse(bodyString).getAsJsonObject();
				String response = "null";

				Iterator<DeviceHandler> dhi = deviceHandlers.iterator();
				DeviceHandler first = null;
				while(first == null && dhi.hasNext()) {
					DeviceHandler deviceHandler = dhi.next();
					if(!deviceHandler.isBusy()) {
						first = deviceHandler;
					}
				}

				if(first == null) {
					response = "all devices busy";
					httpExchange.sendResponseHeaders(200, response.length());
					OutputStream os = httpExchange.getResponseBody();
					os.write(response.getBytes());
					os.close();
					return;
				}



				first.setBusy(true);
				first.getDevice().installPackage(body.get("apk_path").getAsString(), true);
				StringBuilder stringBuilder = new StringBuilder();
				stringBuilder.append("am ");
				stringBuilder.append("start ");
				stringBuilder.append("-n ");
				stringBuilder.append(body.get("package_name").getAsString());
				stringBuilder.append("/");
				stringBuilder.append(body.get("main_activity").getAsString());
				stringBuilder.append(" -a ");
				stringBuilder.append("android.intent.action.MAIN ");
				stringBuilder.append("-c ");
				stringBuilder.append("android.intent.category.LAUNCHER");
				first.getDevice().executeShellCommand(stringBuilder.toString(), new MultiLineReceiver() {
					@Override
					public void processNewLines(String[] strings) {
						for (String string : strings) {
							System.out.println(string);
						}
					}

					@Override
					public boolean isCancelled() {
						return false;
					}
				});
				final boolean[] apkRunning = {true};
				while (apkRunning[0]) {
					String appRunning = "pidof " + body.get("package_name");
					System.out.println(appRunning);
					first.getDevice().executeShellCommand(appRunning, new MultiLineReceiver() {
						@Override
						public void processNewLines(String[] strings) {
							boolean gotPid = false;
							for(String s : strings) {
								if(!s.equals("")) {
									gotPid = true;
								}
							}
							if(!gotPid) apkRunning[0] = false;
						}

						@Override
						public boolean isCancelled() {
							return false;
						}
					});
					Thread.sleep(5000);
				}
				first.setBusy(false);

				System.out.println("===================");

				httpExchange.sendResponseHeaders(200, response.length());
				OutputStream os = httpExchange.getResponseBody();
				os.write(response.getBytes());
				os.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static void init() {

		deviceHandlers = new ArrayList<>();
		File sdkRoot = new File(System.getenv("ANDROID_SDK_HOME"));
		AndroidDebugBridge.initIfNeeded(false);
		adb = AndroidDebugBridge.createBridge("/Users/lgitzing/Library/Android/sdk/platform-tools/adb", true);
		while (!adb.isConnected()) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for(IDevice device : adb.getDevices()) {
			System.out.println(device.getName());
			DeviceHandler deviceHandler = new DeviceHandler(device);
			deviceHandlers.add(deviceHandler);
		}

//		Runnable runnable = () -> {
//			LogCatReceiverTask logCatReceiverTask = new LogCatReceiverTask(first);
//			logCatReceiverTask.addLogCatListener(new LogCatListener() {
//				@Override
//				public void log(List<LogCatMessage> list) {
//					for (LogCatMessage logCatMessage : list) {
//						LogCatHeader header = logCatMessage.getHeader();
//						System.out.println(header.getAppName());
//					}
//				}
//			});
//			logCatReceiverTask.run();
//		};
//		new Thread(runnable).start();
	}

	public static void main(String[] args) throws IOException {
		init();
		adb.addDeviceChangeListener(new AndroidDebugBridge.IDeviceChangeListener() {
			@Override
			public void deviceConnected(IDevice iDevice) {
				System.out.println("cic");
			}

			@Override
			public void deviceDisconnected(IDevice iDevice) {
				System.out.println("cç");
			}

			@Override
			public void deviceChanged(IDevice iDevice, int i) {
				System.out.println("cic");
			}
		});
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/test", new MyHandler());
		server.createContext("/startavd", new StartAvdHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}
}

//	AndroidSdk androidSdk = new AndroidSdk(System.getenv(Constants.ENV_VAR_ANDROID_SDK_HOME), System.getenv(Constants.ENV_VAR_SYSTEM_HOME));
//		AVD avd = AVD.create("Nexus_5X_API_27", "Nexus 5X", "8.1.0", "google_apis_playstore/x86", "100M");
//		avd.createAVD(androidSdk);
//		File sdkRoot = new File(System.getenv(Constants.ENV_VAR_ANDROID_SDK_HOME));
//		AndroidSdkHandler androidSdkHandler = AndroidSdkHandler.getInstance(sdkRoot);
//		AvdManager avdManager = AvdManager.getInstance(androidSdkHandler, new StdLogger(StdLogger.Level.INFO));

//		DeviceManager deviceManager = DeviceManager.createInstance(sdkRoot,new StdLogger(StdLogger.Level.INFO));
//		AndroidDebugBridge.initIfNeeded(false);
//		AndroidDebugBridge adb =AndroidDebugBridge.createBridge("/Users/lgitzing/Library/Android/sdk/platform-tools/adb", true);
//		Arrays.stream(adb.getDevices()).forEach(d -> System.out.println(d.getName()));
//		AndroidDebugBridge.terminate();
//		System.out.println(adb.isConnected());
//		IDevice[] devices =
//		adb.getBridge().getDevices();
//		Arrays.stream(devices).forEach(s -> s.getName());
//		Collection<Device> devices = deviceManager.getDevices(DeviceManager.ALL_DEVICES);
//		Device Nexus5X = null;
