package fr.groom;

import org.apache.commons.cli.*;
import soot.PackManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

public class Repackager {
	private static final String HELP_CATCH_PHRASE = "Repackager [OPTIONS]";
	private static final String OPTION_CLASSES = "c";
	private static final String OPTION_ORIGINAL_APK = "a";
	public static File TEMP_DIRECTORY = null;
	private final Options options = new Options();

	private void initializeCommandLineOptions() {
		options.addOption("?", "help", false, "Print this help message");

		options.addOption(OPTION_CLASSES, "classDir", true, "class directory");
		options.addOption(OPTION_ORIGINAL_APK, "apkFile", true, "Use the given apk file (overrides config file option 'targetApk')");

	}

	private File loadClasses(String classDirectoryPath) {
		File classDirectory = null;
		if (classDirectoryPath != null) {
			File tempFile = new File(classDirectoryPath);
			if (tempFile.exists()) {
				classDirectory = tempFile;
			}
		}
		if (classDirectoryPath == null) {
			System.err.println("No directory given");
			System.exit(1);
		} else if (!classDirectory.exists()) {
			System.err.println("Invalid class directory path : " + classDirectory.getAbsolutePath());
			System.exit(1);
		} else {
			try {
				File out = new File(Repackager.TEMP_DIRECTORY.getAbsolutePath() + "/" + classDirectory.getName());
				Files.copy(classDirectory.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return classDirectory;
	}

	private void run(String[] args) throws ParseException {
		initializeCommandLineOptions();
		final HelpFormatter formatter = new HelpFormatter();

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options, args);
		if (cmd.hasOption("?") || cmd.hasOption("help")) {
			formatter.printHelp(HELP_CATCH_PHRASE, options);
			return;
		}

		TEMP_DIRECTORY = new File("./class-temp-" + UUID.randomUUID().toString());
		TEMP_DIRECTORY.mkdirs();
		File classes = null;
		if (cmd.getOptionValue(OPTION_CLASSES) != null) {
			classes = loadClasses(cmd.getOptionValue(OPTION_CLASSES)); // Load target apk path from program arguments or config file
		} else {
			throw new MissingArgumentException("No class dir given !");
		}
		SootSetupRepackager.initSootInstance(
				new File(classes.getAbsolutePath()),
				TEMP_DIRECTORY.getAbsolutePath(),
				"/Users/lgitzing/Library/Android/sdk/platforms"
		);
		PackManager.v().runPacks();
		System.out.println("Recompiling apk.");
		PackManager.v().writeOutput();
		Path dexFilePath = Paths.get(TEMP_DIRECTORY + "/sootOutput/classes.dex");
//		File dexFile = new File(dexFilePath.toUri());

		Path apkPath = Paths.get(cmd.getOptionValue(OPTION_ORIGINAL_APK));
		try( FileSystem fs = FileSystems.newFileSystem(apkPath, null) ){
			Path fileInsideZipPath = fs.getPath("/classes.dex");
			Files.copy(dexFilePath, fileInsideZipPath, StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws ParseException {
		Repackager repackager = new Repackager();
		repackager.run(args);
	}
}
