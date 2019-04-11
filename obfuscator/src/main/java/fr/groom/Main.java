package fr.groom;

import fr.groom.configuration.ObfuscatorConfiguration;
import org.apache.commons.cli.*;
import org.xmlpull.v1.XmlPullParserException;
import proguard.ClassPath;
import proguard.ClassPathEntry;
import proguard.ConfigurationParser;
import proguard.ProGuard;
import soot.*;
import soot.jbco.jimpleTransformations.*;
import soot.jbco.jimpleTransformations.ClassRenamer;
import soot.jimple.infoflow.android.axml.AXmlAttribute;
import soot.jimple.infoflow.android.axml.AXmlHandler;
import soot.jimple.infoflow.android.axml.AXmlNode;
import soot.jimple.infoflow.android.axml.ApkHandler;
import soot.jimple.infoflow.android.manifest.ProcessManifest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class Main {
	private static final String HELP_CATCH_PHRASE = "Obfuscator [OPTIONS]";
	private static final String OPTION_APK_FILE = "a";
	private static final String OPTION_CONFIG_FILE = "c";
	public static File TEMP_DIRECTORY = null;
	private final Options options = new Options();

	private void initializeCommandLineOptions() {
		options.addOption("?", "help", false, "Print this help message");

		options.addOption(OPTION_CONFIG_FILE, "configfile", true, "Use the given fr.groom.configuration file");

		options.addOption(OPTION_APK_FILE, "apkfile", true, "Use the given apk file (overrides config file option 'targetApk')");

	}

	private File loadTargetApk(String targetApkfromCmdArgument) {
		File targetApk = null;
		if (targetApkfromCmdArgument != null) {
			File tempFile = new File(targetApkfromCmdArgument);
			if (tempFile.exists()) {
				targetApk = tempFile;
			}
		}
		if (targetApk == null) {
			System.err.println("No apk path given.");
			System.exit(1);
		} else if (!targetApk.exists()) {
			System.err.println("Invalid apk path : " + targetApk.getAbsolutePath());
			System.exit(1);
		} else {
			try {
				File out = new File(Paths.get(Main.TEMP_DIRECTORY.getAbsolutePath(), targetApk.getName()).toUri());
				Files.copy(targetApk.toPath(), out.toPath(), StandardCopyOption.REPLACE_EXISTING);
				return out;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static ProcessManifest getManifest(String pathToApk) {
		try {
			return new ProcessManifest(pathToApk);
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}
//
	private void modifyComponentsNames(File apk, HashMap<String, String> componentMappings) {
		ProcessManifest manifest = getManifest(apk.getAbsolutePath());
		List<AXmlNode> components = new ArrayList<>();
		components.addAll(manifest.getAllActivities());
		components.addAll(manifest.getServices());
		components.addAll(manifest.getReceivers());
		componentMappings.forEach((componentName, newName) -> {
			System.out.println("modifying: " + componentName + " with name: " + newName);
			for(AXmlNode c : components) {
				AXmlAttribute componentNameAttribute = c.getAttribute("name");
				String originalComponentName = (String) componentNameAttribute.getValue();
				if(originalComponentName.equals(componentName)) {
					componentNameAttribute.setValue(newName);
				}
			}
		});
	}

	private void changePackageInManifest(File apk) throws IOException {
		String attr_ns = "http://schemas.android.com/apk/res/android";
		String newPackageName = "a.a.a";
		ProcessManifest manifest = getManifest(apk.getAbsolutePath());
		if (!apk.exists() || manifest == null) {
			System.err.println("Invalid apk path or no manifest");
			System.exit(1);
		}
//
		AXmlHandler aXmlHandler = manifest.getAXml();
		List<AXmlNode> nodeList = aXmlHandler.getNodesWithTag("manifest");
		AXmlNode manifestNode = nodeList.get(0);
		AXmlAttribute packageAttribute = manifestNode.getAttribute("package");
		packageAttribute.setValue("a.a.a");
		String packageName = (String) packageAttribute.getValue();

		List<AXmlNode> components = new ArrayList<>();
		components.addAll(manifest.getAllActivities());
		components.addAll(manifest.getServices());
		components.addAll(manifest.getReceivers());
		for (AXmlNode component : components) {
			AXmlAttribute componentNameAttribute = component.getAttribute("name");
			String componentName = (String) componentNameAttribute.getValue();
			componentNameAttribute.setValue(componentName.replace(packageName, newPackageName));
		}
		byte[] newManifestBytes = aXmlHandler.toByteArray();
		Path newManifestPath = Paths.get(apk.getParentFile().getAbsolutePath(), "AndroidManifest.xml");
		FileOutputStream fileOuputStream = new FileOutputStream(new File(newManifestPath.toUri()));
		fileOuputStream.write(newManifestBytes);
		fileOuputStream.close();
		File newManifest = new File(newManifestPath.toUri());
		try {
			ApkHandler apkH = new ApkHandler(apk);
			apkH.addFilesToApk(Collections.singletonList(newManifest));
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("error when writing new manifest: " + e);
		}
		newManifest.delete();

		AXmlHandler aXmlHandler2 = getManifest(apk.getAbsolutePath()).getAXml();
		List<AXmlNode> nodeList2 = aXmlHandler2.getNodesWithTag("manifest");
	}

	private void run(String[] args) throws ParseException, IOException, proguard.ParseException, InterruptedException {
		initializeCommandLineOptions();
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
				ObfuscatorConfiguration c = JsonConfigurationParser.fromFile(configFile).parse();
				Configuration.setINSTANCE(c);
				System.out.println("Running ApkInstrumenter from json config file : " + configFile);
			} else {
				Configuration.setDefaultINSTANCE();
				System.out.println("Running ApkInstrumenter from default fr.groom.configuration ");
			}
		}

		TEMP_DIRECTORY = new File("temp-" + UUID.randomUUID().toString());
		TEMP_DIRECTORY.mkdirs();
		File apk;
		if (cmd.getOptionValue(OPTION_APK_FILE) != null) {
			apk = loadTargetApk(cmd.getOptionValue(OPTION_APK_FILE)); // Load target apk path from program arguments or config file
		} else {
			throw new MissingArgumentException("No apk given !");
		}

		String pathToDex2Jar = Configuration.v().getDex2jarPath();
		String pathToDx = Configuration.v().getDxPath();
		String pathZip = Configuration.v().getZipCommandPath();

		if (Configuration.v().getApplyProguard()) {
			File apkJar = new File(apk.getAbsolutePath().replace(".apk", ".jar"));
			runProcess(pathToDex2Jar, "--force", "-o", apkJar.getAbsolutePath(), apk.getAbsolutePath());
			File proguardOutputJar = new File(Paths.get(TEMP_DIRECTORY.getAbsolutePath(), "classes.jar").toUri());
//		File proguardOutputApk = new File(Paths.get(TEMP_DIRECTORY.getAbsolutePath(), "output.apk").toUri());
			File proguardOutputDex = new File(Paths.get(TEMP_DIRECTORY.getAbsolutePath(), "classes.dex").toUri());
			proguard.Configuration configuration = new proguard.Configuration();
			File proguardConfigFile = new File(Configuration.v().getProguardConfigPath());
			ConfigurationParser proguardParser = new ConfigurationParser(proguardConfigFile, System.getProperties());
			ClassPath classPath = new ClassPath();
			proguardParser.parse(configuration);
			classPath.add(new ClassPathEntry(apkJar, false));
			classPath.add(new ClassPathEntry(proguardOutputJar, true));
			configuration.programJars = classPath;
			configuration.verbose = true;
			new ProGuard(configuration).execute();
			runProcess(pathToDx, "--dex", "--output=" + proguardOutputDex.getAbsolutePath(), proguardOutputJar.getAbsolutePath());
			runProcess(pathZip, "-d", apk.getAbsolutePath(), "classes.dex");
			runProcess(pathZip, "-uj", apk.getAbsolutePath(), proguardOutputDex.getAbsolutePath());
		}

//		changePackageInManifest(apk);

		SootSetup.initSootInstance(
				new File(apk.getAbsolutePath()),
				TEMP_DIRECTORY.getAbsolutePath(),
				Configuration.v().getAndroidPlatforms()
		);

//		ClassRenamer.v();
		Pack wjtp = PackManager.v().getPack("wjtp");
//		PackageTransformer packageTransformer = new PackageTransformer();
//		wjtp.add(new Transform("wjtp.packageTransformer", packageTransformer));
		ReflectionTransformerV2 rt = new ReflectionTransformerV2();
		wjtp.add(new Transform("wjtp.reflectionTransformer", rt));
		fr.groom.ClassRenamer cr = new fr.groom.ClassRenamer(getManifest(apk.getAbsolutePath()));
//		cr.setRenamePackages(true);
		wjtp.add(new Transform("wjtp.jbco_cr", cr));
		if (Configuration.v().getUseEncryption()) {
			StringEncrypter se = new StringEncrypter();
			wjtp.add(new Transform("wjtp.stringEncrypter", se));
		}
		wjtp.add(new Transform("wjtp.jbco_mr", MethodRenamer.v()));
		FieldRenamer.v().setRenameFields(true);
		wjtp.add(new Transform("wjtp.jbco_fr", FieldRenamer.v()));
//		wjtp.add(new Transform("wjtp.jbco_blbc", new LibraryMethodWrappersBuilder()));
//		wjtp.add(new Transform("wjtp.jbco_bapibm", new BuildIntermediateAppClasses()));
		PackManager.v().runPacks();
//		modifyComponentsNames(apk, cr.componentMappings);
		System.out.println("Recompiling apk.");
		PackManager.v().writeOutput();
		Path sootApkPath = Paths.get(TEMP_DIRECTORY + "/sootOutput", apk.getName());
		Path sootOutputPath = Paths.get(TEMP_DIRECTORY + "/sootOutput");
		File sootOutputDirectory = new File(sootOutputPath.toUri());
		File sootApk = new File(sootApkPath.toUri());

//		runProcess(pathZip, "-d", apk.getAbsolutePath(), "classes.dex");
//		runProcess(pathZip, "-uj", apk.getAbsolutePath(), new File(Paths.get(sootOutputDirectory.getAbsolutePath(), "classes.dex").toUri()).getAbsolutePath());


//		String andResOutDir = "output";
//		File andResOutput = new File(Paths.get(sootOutputDirectory.getAbsolutePath(), andResOutDir).toUri());

//		int andResReturnCode = runProcess(
//				"java",
//				"-jar",
//				Configuration.v().getAndResJarPath(),
//				"-config",
//				"./config.xml",
//				"-out",
//				andResOutput.getAbsolutePath(),
//				sootApk.getAbsolutePath());
//		File andResapk = new File(Paths.get(
//				andResOutput.getAbsolutePath(),
//				sootApk.getName().replace(".apk", "") + "_unsigned.apk"
//		).toUri());

		File signedApk = signApk(sootApk, Configuration.v().getApksignerPath(), Configuration.v().getPathToKeystore(), Configuration.v().getKeyPassword());
//		runProcess(pathToDex2Jar, "--force", "-o", signedApk.getAbsolutePath().replace(".apk", ".jar"), signedApk.getAbsolutePath());
		System.out.println("APPLICATION OBFUSCATED");
	}

	public static void main(String[] args) throws ParseException, proguard.ParseException, InterruptedException, IOException {
		Main main = new Main();
		main.run(args);
	}


	public static int runProcess(String... command) throws InterruptedException, IOException {
		System.out.println("running: " + Arrays.toString(command));
		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process p = pb.start();
		return p.waitFor();
	}

	public static File signApk(File apkToSign, String pathToApksigner, String pathToKeyStore, String keyPassword) throws IOException {
		System.out.println("Signing apk.");
		File signed = new File(apkToSign.getAbsolutePath().replace(".apk", "") + "-signed.apk");
		ProcessBuilder pb = new ProcessBuilder(pathToApksigner,
				"sign",
				"--ks",
				pathToKeyStore,
				"--key-pass",
				"pass:" + keyPassword,
				"--ks-pass",
				"pass:" + keyPassword,
				"--out",
				signed.getAbsolutePath(),
				apkToSign.getAbsolutePath()
		);
		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
		Process p = pb.start();
		try {
			p.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return signed;
	}
}
