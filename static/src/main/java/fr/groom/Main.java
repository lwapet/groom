package fr.groom;

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
	private final Options options = new Options();


	public Main() {
		initializeCommandLineOptions();
	}

	private void initializeCommandLineOptions() {
		options.addOption("?", "help", false, "Print this help message");

		options.addOption(OPTION_CONFIG_FILE, "configfile", true, "Use the given fr.groom.configuration file");

		options.addOption(OPTION_APK_FILE, "apkfile", true, "Use the given apk file (overrides config file option 'targetApk')");
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
		if (targetApk == null || !targetApk.exists()) {
			System.err.println("Invalid apk path");
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

		Storage storage;
		if (Configuration.v().getDatabaseConfiguration().isConnectToDatabase()) {
			DatabaseConfiguration dbConfig = Configuration.v().getDatabaseConfiguration();
			storage = new Database(
					dbConfig.getUrl(),
					dbConfig.getPort(),
					dbConfig.getName(),
					dbConfig.getAuthenticationConfiguration().isPerformAuthentication(),
					dbConfig.getAuthenticationConfiguration().getUsername(),
					dbConfig.getAuthenticationConfiguration().getPassword(),
					dbConfig.getAuthenticationConfiguration().getAuthSourceDatabaseName()
			);
		} else {
			storage = new Printer();
		}

		Application app = new Application(tempApk); // init Application object
		JSONObject filter = new JSONObject();
		filter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
		storage.replace(filter, app.toJson(), "application");


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
			instrumentationData.put("method_hooked_count", sootInstrumenter.getMethodHookedCount());
			instrumentationData.put("unit_seen_count", sootInstrumenter.getUnitSeenCount());
			set.put("$set", instrumentationData);
			filter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
			storage.update(filter, set, "application");
		}


		if (Configuration.v().getFridaInstrumenterConfiguration().isInstrumentApkWithFrida()) {
			File modifiedApk = FridaInstrumenter.addSoFiles(app.getLastEditedApk());
			File copiedFile = FileUtils.copyFileToOutputDir(modifiedApk);
			app.setFridaInstrumentedApk(copiedFile);
		}
		if (!Configuration.v().isRepackageApk()) {
			File finalApk = FileUtils.copyFileToOutputDir(app.getApk());
			app.setFinalApk(finalApk);
		} else {
			File recompiledApk = recompileApk(app.getLastEditedApk());
			app.setSootInstrumentedApk(recompiledApk);
			File aligned = InstrumenterUtils.alignApk(app.getLastEditedApk(), Configuration.v().getZipalignPath());
			app.setAlignedApk(aligned);
			File signed = InstrumenterUtils.signApk(
					app.getLastEditedApk(),
					Configuration.v().getApksignerPath(),
					Configuration.v().getPathToKeystore(),
					Configuration.v().getKeyPassword()
			);
			app.setSignedApk(signed);
			app.setFinalApk(signed);
		}

		FileUtils.copyFileToDynamicRepository(app.getFinalApk());

		deleteDir(TEMP_DIRECTORY);

		JSONObject updateFilter = new JSONObject();
		updateFilter.put("sha256", app.getSha256());
//		storage.insertData(app.toJson(), "application");
		JSONObject data = new JSONObject();
		JSONObject set = new JSONObject();
		set.put("$set", data);
		data.put("file_name", app.getFinalApk().getName());
		storage.update(updateFilter, set, "application");
		System.out.println("apk_path : " + app.getFinalApk().getAbsolutePath());
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
