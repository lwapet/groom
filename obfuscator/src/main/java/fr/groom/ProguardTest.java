//package fr.groom;
//
//import proguard.*;
//
//import java.io.File;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.Arrays;
//import java.util.Enumeration;
//import java.util.List;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipOutputStream;
//
//public class ProguardTest {
//	public static String WORK_DIRECTORY = "/Users/lgitzing/Development/work/Groom/obfuscator/temp-f56c2a66-92c1-4fe1-86da-155ce5a6c47c/sootOutput";
//	public static String APK_FILE_NAME = "locker-fe666e209e094968d3178ecf0cf817164c26d5501ed3cd9a80da786a4a3f3dc4.apk";
//	public static void main(String[] args) throws IOException, ParseException, InterruptedException {
//		Configuration configuration = new Configuration();
//		File proguardConfigFile = new File("test.pro");
//		ConfigurationParser parser = new ConfigurationParser(proguardConfigFile, System.getProperties());
//		ClassPath classPath = new ClassPath();
//		String pathToDex2Jar = "/Users/lgitzing/Development/work/dex-tools/d2j-dex2jar.sh";
//		String pathToDx = "/Users/lgitzing/Library/Android/sdk/build-tools/28.0.2/dx";
//		String pathZip = "/usr/bin/zip";
//		File apk = new File(Paths.get(WORK_DIRECTORY, APK_FILE_NAME).toUri());
//		if(!apk.exists()) {
//			System.err.println("Wrong path");
//			System.exit(1);
//		}
//		File apkJar = new File(Paths.get(WORK_DIRECTORY, APK_FILE_NAME.replace(".apk", ".jar")).toUri());
//		File proguardOutputJar = new File(Paths.get(WORK_DIRECTORY, "classes.jar").toUri());
//		File proguardOutputDex = new File(Paths.get(WORK_DIRECTORY, "classes.dex").toUri());
//		runProcess(pathToDex2Jar, "--force", "-o", apkJar.getAbsolutePath(), apk.getAbsolutePath());
//		parser.parse(configuration);
//		classPath.add(new ClassPathEntry(apkJar, false));
//		classPath.add(new ClassPathEntry(proguardOutputJar, true));
//		configuration.programJars = classPath;
//		new ProGuard(configuration).execute();
//		runProcess(pathToDx, "--dex", "--output="+proguardOutputDex.getAbsolutePath(), proguardOutputJar.getAbsolutePath());
//		runProcess(pathZip, "-d", apk.getAbsolutePath(), "classes.dex");
//		runProcess(pathZip, "-uj", apk.getAbsolutePath(), proguardOutputDex.getAbsolutePath());
//	}
//
//	public static int runProcess(String... command) throws InterruptedException, IOException {
//		System.out.println("running: " + Arrays.toString(command));
//		ProcessBuilder pb = new ProcessBuilder(command);
//		pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
//		pb.redirectError(ProcessBuilder.Redirect.INHERIT);
//		Process p = pb.start();
//		return p.waitFor();
//	}
//}
