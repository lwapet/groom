package fr.groom;

import com.mongodb.client.model.UpdateOptions;
import fr.groom.apk_instrumentation.SootInstrumenter;
import fr.groom.configuration.DatabaseConfiguration;
import fr.groom.configuration.InstrumenterConfiguration;
import fr.groom.apk_instrumentation.FridaInstrumenter;
import fr.groom.apk_instrumentation.InstrumenterUtils;
import fr.groom.models.Application;
import fr.groom.mongo.Database;
import fr.groom.static_analysis.StaticAnalysis;
import org.apache.commons.cli.*;
import org.json.JSONObject;
import soot.PackManager;
import soot.Scene;
import soot.Transform;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlHandler;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.axml.ApkHandler;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static fr.groom.FileUtils.TEMP_DIRECTORY;
import static fr.groom.FileUtils.deleteDir;


public class Main {
	private static final String HELP_CATCH_PHRASE = "ApkInstrumenter [OPTIONS]";
	private static final String OPTION_CONFIG_FILE = "c";
	private static final String OPTION_APK_FILE = "a";
	public static final String APPLICATION_COLLECTION = "application";
	public static final String STATIC_COLLECTION = "static";
	public static final String DYNAMIC_COLLECTION = "dynamic";
	public static final String STATUS_KEY = "status";
	private final Options options = new Options();
	private Storage storage;
	private Application app;


	public Main() {
		initializeCommandLineOptions();
	}

	private void initializeCommandLineOptions() {
		options.addOption("?", "help", false, "Print this help message");

		options.addOption(OPTION_CONFIG_FILE, "configfile", true, "Use the given fr.groom.configuration file");

		options.addOption(OPTION_APK_FILE, "apkfile", true, "Use the given apk file (overrides config file option 'targetApk')");
	}

	public void updateStatus(String status) {
		JSONObject filter = new JSONObject();
		filter.put("sha256", this.app.getSha256());
		JSONObject data = new JSONObject();
		data.put(Main.STATUS_KEY, status);
		this.storage.update(filter, data, Main.STATIC_COLLECTION);
	}

	private File recompileApk(File apk) {
		System.out.println("Recompiling apk.");
		PackManager.v().writeOutput();
		Path sootApkPath = Paths.get(Configuration.v().getSootConfiguration().getOutputDirectory(), apk.getName());
		File sootApk = new File(sootApkPath.toUri());
		if (!sootApk.exists()) {
			try {
				throw new FileNotFoundException("Recompiled apk does not exist at path: " + sootApk.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		File sootApkRenamed = new File(sootApk.getAbsolutePath().replace(".apk", "") + "-soot.apk");
		sootApk.renameTo(sootApkRenamed);
		if (!sootApkRenamed.exists()) {
			try {
				throw new FileNotFoundException("Failed renaming apk" + sootApkRenamed.getAbsolutePath());
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return sootApkRenamed;
	}

	private void loadTargetApk(String targetApkfromCmdArgument) {
		File targetApk = null;
		if (targetApkfromCmdArgument != null) {
			File tempFile = new File(targetApkfromCmdArgument);
			if (tempFile.exists()) {
				targetApk = tempFile;
			}
		} else if (!Configuration.v().getTargetApk().equals("")) {
			targetApk = new File(Configuration.v().getTargetApk());
		}
		if (targetApk == null) {
			System.err.println("No apk path given.");
			System.exit(1);
		} else if (!targetApk.exists()) {
			System.err.println("Invalid apk path : " + targetApk.getAbsolutePath());
			System.exit(1);
		} else {
			Configuration.v().setTargetApk(targetApk.getAbsolutePath());
		}
	}


	private void run(String[] args) throws ParseException, IOException {
		// We need proper parameters
		final HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		if (cmd.hasOption("?") || cmd.hasOption("help")) {
			formatter.printHelp(HELP_CATCH_PHRASE, options);
			return;
		}
		String configFile = cmd.getOptionValue(OPTION_CONFIG_FILE);


		if (Configuration.v() == null) {
			if (configFile != null && !configFile.isEmpty()) {
				InstrumenterConfiguration configuration = JsonConfigurationParser.fromFile(configFile).parse();
				Configuration.setINSTANCE(configuration);
				System.out.println("Running ApkInstrumenter from json config file : " + configFile);
			} else {
				Configuration.setDefaultINSTANCE();
				System.out.println("Running ApkInstrumenter from default fr.groom.configuration ");
			}
		}

		loadTargetApk(cmd.getOptionValue(OPTION_APK_FILE)); // Load target apk path from program arguments or config file
		File tempApk = FileUtils.copyFileToTempDirectory(new File(Configuration.v().getTargetApk())); // copy file to a temp directory


		SootSetup.initSootInstance(
				new File(tempApk.getAbsolutePath()),
				Configuration.v().getSootConfiguration().getOutputDirectory(),
				Configuration.v().getSootConfiguration().getAndroidPlatforms()
		);

		if (Configuration.v().getDatabaseConfiguration().isConnectToDatabase()) {
			DatabaseConfiguration dbConfig = Configuration.v().getDatabaseConfiguration();
			this.storage = new Database(
					dbConfig.getUrl(),
					dbConfig.getPort(),
					dbConfig.getName(),
					dbConfig.getAuthenticationConfiguration().isPerformAuthentication(),
					dbConfig.getAuthenticationConfiguration().getUsername(),
					dbConfig.getAuthenticationConfiguration().getPassword(),
					dbConfig.getAuthenticationConfiguration().getAuthSourceDatabaseName()
			);
		} else {
			this.storage = new Printer();
		}


		this.app = new Application(tempApk); // init Application object
		JSONObject filter = new JSONObject();
		filter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
		storage.update(filter, app.toJson(), Main.APPLICATION_COLLECTION);
		this.updateStatus("started");


		StaticAnalysis staticAnalysis = null;
		if (Configuration.v().isPerformStaticAnalysis()) {
			staticAnalysis = new StaticAnalysis(app, storage);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.staticAnalysisTransformer", staticAnalysis));
		}


		if (Configuration.v().getFridaInstrumenterConfiguration().isInstrumentApkWithFrida()) {
			FridaInstrumenter fridaInstrumenter = new FridaInstrumenter();
			fridaInstrumenter.injectFridaStatements(app);
		}
		SootInstrumenter sootInstrumenter = null;
		if (Configuration.v().getSootInstrumentationConfiguration().isInstrumentApkWithSoot()) {
			// Prepare constants for instrumentation with Groom
			HookConstant.PACKAGE_NAME.setValue(app.getPackageName());
			HookConstant.SHA_256.setValue(app.getSha256());
			for (HookConstant constant : HookConstant.values()) {
				String field = constant.toString();
				String value = constant.getValue();
				InstrumenterUtils.setGroomConstants(field, value);
			}
			// Clean output directory
			File folder = new File(Configuration.v().getSootConfiguration().getOutputDirectory());
			if (folder.exists()) {
				for (File file : Objects.requireNonNull(folder.listFiles())) {
					if (file.getName().endsWith(".apk")) {
						boolean success = file.delete();
					}
				}
			}
			// Add Soot transformer instrumenter
			sootInstrumenter = new SootInstrumenter(app, staticAnalysis);
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.mainTransformer", sootInstrumenter));
		}

		String attr_ns = "http://schemas.android.com/apk/res/android";
		String internet_permission = "android.permission.INTERNET";
		if (!app.getPermissions().stream().anyMatch(s -> s.equals(internet_permission))) {
			AXmlHandler aXmlHandler = app.getManifest().getAXml();
			List<AXmlNode> nodeList = aXmlHandler.getNodesWithTag("manifest");
			if (!nodeList.isEmpty()) {
				AXmlNode manifestNode = nodeList.get(0);
				AXmlNode internetPermission = new AXmlNode("uses-permission", null, null);
				AXmlAttribute<String> name = new AXmlAttribute<String>("name", internet_permission, attr_ns);
				internetPermission.addAttribute(name);
				manifestNode.addChild(internetPermission);
				byte[] newManifestBytes = aXmlHandler.toByteArray();
				FileOutputStream fileOuputStream = new FileOutputStream(TEMP_DIRECTORY.getAbsolutePath() + File.separatorChar + "AndroidManifest.xml");
				fileOuputStream.write(newManifestBytes);
				fileOuputStream.close();
				File newManifest = new File(TEMP_DIRECTORY.getAbsolutePath() + File.separatorChar + "AndroidManifest.xml");
				try {
					ApkHandler apkH = new ApkHandler(app.getLastEditedApk());
					apkH.addFilesToApk(Collections.singletonList(newManifest));
				} catch (IOException e) {
					e.printStackTrace();
					throw new RuntimeException("error when writing new manifest: " + e);
				}
				newManifest.delete();
				app.setManifest(Application.getManifest(app.getLastEditedApk().getAbsolutePath()));

				AXmlHandler aXmlHandler2 = app.getManifest().getAXml();
				List<AXmlNode> nodeList2 = aXmlHandler2.getNodesWithTag("manifest");
			}
		}

		System.out.println("Running soot packs");
		PackManager.v().runPacks();

		if (sootInstrumenter != null) {
			JSONObject set = new JSONObject();
			JSONObject instrumentationData = new JSONObject();
			instrumentationData.put("statement_hooked_count", sootInstrumenter.getStatementHookedCount());
			instrumentationData.put("injected_units_count", sootInstrumenter.getInjectedUnitsCount());
			instrumentationData.put("unit_seen_count", sootInstrumenter.getUnitSeenCount());
			filter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
			storage.update(filter, instrumentationData, "application");
		}

		if (!Configuration.v().isRepackageApk()) {
			File finalApk = FileUtils.copyFileToOutputDir(app.getApk());
			System.out.println("FINAL APK :" + finalApk.getAbsolutePath());
			app.setFinalApk(finalApk);
		} else {
			this.updateStatus("repackaging");
			File recompiledApk = recompileApk(app.getLastEditedApk());
			this.updateStatus("repackaged");
			app.setSootInstrumentedApk(recompiledApk);
			System.out.println("RECOMPILED : " + recompiledApk.getAbsolutePath());
			if (Configuration.v().getFridaInstrumenterConfiguration().isInstrumentApkWithFrida()) {
				this.updateStatus("adding .so files");
				File modifiedApk = FridaInstrumenter.addSoFiles(app.getLastEditedApk());
				File copiedFile = FileUtils.copyFileToOutputDir(modifiedApk);
				app.setFridaInstrumentedApk(copiedFile);
			}
			this.updateStatus("aligning");
			File aligned = InstrumenterUtils.alignApk(app.getLastEditedApk(), Configuration.v().getZipalignPath());
			this.updateStatus("aligned");
			System.out.println("ALIGNED: " + aligned.getAbsolutePath());
			app.setAlignedApk(aligned);
			this.updateStatus("signing");
			File signed = InstrumenterUtils.signApk(
					app.getLastEditedApk(),
					Configuration.v().getApksignerPath(),
					Configuration.v().getPathToKeystore(),
					Configuration.v().getKeyPassword()
			);
			System.out.println("SIGNED : " + signed.getAbsolutePath());
			app.setSignedApk(signed);
			app.setFinalApk(signed);
		}

		System.out.println(app.getFinalApk().getAbsolutePath());
		File instrumentedApkInDynamicDir = FileUtils.copyFileToInstrumentedApkDirectory(app.getFinalApk());


//		deleteDir(TEMP_DIRECTORY);
//		deleteDir(new File(Configuration.v().getSootConfiguration().getOutputDirectory()));

		if (Configuration.v().isRepackageApk() && Configuration.v().getSootInstrumentationConfiguration().isInstrumentApkWithSoot()) {
			JSONObject updateFilter = new JSONObject();
			updateFilter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
			JSONObject data = new JSONObject();
			data.put("instrumented_filename", app.getFinalApk().getName());
			data.put("recompile_sdk_version", Scene.v().getAndroidAPIVersion());
			storage.update(updateFilter, data, "application");
//			System.out.println("apk_path : " + instrumentedApkInDynamicDir.getAbsolutePath());
		}
		this.updateStatus("ok");
		System.out.println("Intrumentation finished !");
		if (storage instanceof Database) {
			Database s = (Database) storage;
			s.close();
		}

//		DynamicAnalysis dynamicAnalysis = new DynamicAnalysis(app, storage);
//		dynamicAnalysis.run();


//		AVD avd = AVDManager.getOrStartAVD(Configuration.v().getAvdName(), new LogAVDEventListener() {
//			@Override
//			public void onReady(AVD avd) {
//				super.onReady(avd);
//				if (app.getFinalApk().exists()) {
//					avd.installApk(app.getFinalApk(), true);
//				}
//			}
//
//			@Override
//			public void onInstallApk(AVD avd) {
//				super.onInstallApk(avd);
//				logcat.clean();
//				logcat.open(avd.getAdbName());
//				ArrayList<SootClass> launchableActivities = app.getLaunchableActivitySootClasses();
//				String launchableActivityName = launchableActivities.get(0).getName();
//				if (Configuration.v().isRunApk()) {
//					avd.startApk(app.getPackageName(), launchableActivityName, true);
//				}
//			}
//
//			@Override
//			public void onInstallApkFailed(AVD avd, String error) {
//				System.err.println(error);
//			}
//
//			public void onStartApk(AVD avd) {
//				super.onStartApk(avd);
//			}
//		});


//		InterpreterV3 interpreter = new InterpreterV3(app, logcatParser.getLogStorage());
//		Classifier classifier = new Classifier(app, logcatParser.getLogStorage());
//		Controller interpreterController = new Controller(classifier);
//		interpreterController.autoReload();
//		interpreterController.start();

//


	}

	/**
	 * fr.groom.Main program method
	 *
	 * @param args
	 */
	public static void main(String[] args) throws IOException, ParseException {
		Main main = new Main();
		main.run(args);
	}
}
