package fr.groom;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FileUtils {
//	public static final File TEMP_DIRECTORY = new File("/tmp/temp" + UUID.randomUUID().toString());


	static void deleteDir(File file) {
		File[] contents = file.listFiles();
		if (contents != null) {
			for (File f : contents) {
				if (!Files.isSymbolicLink(f.toPath())) {
					deleteDir(f);
				}
			}
		}
		file.delete();
	}

	public static File copyFileToOutputDir(File file) {
		File sootOutputDirectory = new File(Main.TEMP_DIRECTORY + "/sootOutput");
		if (!sootOutputDirectory.exists()) {
			sootOutputDirectory.mkdir();
		}

		String newFilePath = sootOutputDirectory.getAbsolutePath() + "/" + file.getName();
		try {
			Files.copy(file.toPath(), (new File(newFilePath)).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while copying file to output directory");
			System.exit(1);
		}
		File newTempFile = new File(newFilePath);
		if (!newTempFile.exists()) {
			System.err.println("Temp file doesn't exist");
			System.exit(1);
		}
		return newTempFile;
	}

	public static File copyFileToInstrumentedApkDirectory(File file) {
		File sootOutputDirectory = new File(Configuration.v().getInstrumentedApkDirectory());
		if (!sootOutputDirectory.exists()) {
			sootOutputDirectory.mkdir();
		}

		String newFilePath = sootOutputDirectory.getAbsolutePath() + "/" + file.getName();
		try {
			Files.copy(file.toPath(), (new File(newFilePath)).toPath(), StandardCopyOption.REPLACE_EXISTING);
			System.out.println("File copied to:" + newFilePath);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while copying file to output directory");
			System.exit(1);
		}
		File newTempFile = new File(newFilePath);
		if (!newTempFile.exists()) {
			System.err.println("Temp file doesn't exist");
			System.exit(1);
		}
		return newTempFile;
	}

	public static HashMap<String, Object> getJsonAsHashMap(String path) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.readValue(new File(
					path), new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

//	public static File copyFileToTempDirectory(File file) {
//
//		String newApkPath = "./" + file.getName();
//		try {
//			Files.copy(file.toPath(), (new File(newApkPath)).toPath(), StandardCopyOption.REPLACE_EXISTING);
//		} catch (IOException e) {
//			e.printStackTrace();
//			System.err.println("Error while copying Apk to temp directory");
//			System.exit(1);
//		}
//		File newTempFile = new File(newApkPath);
//		if (!newTempFile.exists()) {
//			System.err.println("Temp apk doesn't exist");
//			System.exit(1);
//		}
//		return newTempFile;
//	}
}
