package fr.groom;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

public class FileUtils {
	public static final File TEMP_DIRECTORY = new File("temp" + UUID.randomUUID().toString());


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
		File sootOutputDirectory = new File(Configuration.v().getSootConfiguration().getOutputDirectory());
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

		public static File copyFileToDynamicRepository(File file) {
		File sootOutputDirectory = new File(Configuration.v().getDynamicAnalysisRepository());
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

	public static File copyFileToTempDirectory(File file) {
		if (TEMP_DIRECTORY.exists()) {
			deleteDir(TEMP_DIRECTORY);
		}
		TEMP_DIRECTORY.mkdir();

		String newApkPath = TEMP_DIRECTORY.getAbsolutePath() + "/" + file.getName();
		try {
			Files.copy(file.toPath(), (new File(newApkPath)).toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Error while copying Apk to temp directory");
			System.exit(1);
		}
		File newTempFile = new File(newApkPath);
		if (!newTempFile.exists()) {
			System.err.println("Temp apk doesn't exist");
			System.exit(1);
		}
		return newTempFile;
	}
}
