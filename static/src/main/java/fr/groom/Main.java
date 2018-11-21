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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

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
		if (Configuration.v().getSootInstrumentationConfiguration().isInstrumentApkWithSoot()) {
			// Prepare constants for instrumentation with Groom
			HookConstant.PACKAGE_NAME.setValue(app.getPackageName());
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
			PackManager.v().getPack("wjtp").add(new Transform("wjtp.mainTransformer", new SootInstrumenter(app, staticAnalysis)));
		}
		System.out.println("Running soot packs");
		PackManager.v().runPacks();

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


		deleteDir(TEMP_DIRECTORY);

		FileUtils.copyFileToDynamicRepository(app.getFinalApk());

		System.out.println("apk_path : " + app.getFinalApk().getAbsolutePath());
		System.out.println("Intrumentation finished !");

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
