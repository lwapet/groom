package fr.groom;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileUtils {
	public static final File TEMP_DIRECTORY = new File("temp");
	public static String createSha1(File file) {
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-1");
			InputStream fis = new FileInputStream(file);
			int n = 0;
			byte[] buffer = new byte[8192];
			while (n != -1) {
				n = fis.read(buffer);
				if (n > 0) {
					digest.update(buffer, 0, n);
				}
			}
			byte[] hash = digest.digest();
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String createMd5(File file) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
			InputStream inputStream = new FileInputStream(file);
			DigestInputStream dis = new DigestInputStream(inputStream, md);
			byte[] hash = md.digest();
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}

			return hexString.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String createSha256(File file) {
		byte[] buffer = new byte[8192];
		int count;
		MessageDigest digest = null;
		try {
			digest = MessageDigest.getInstance("SHA-256");
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
			while ((count = bis.read(buffer)) > 0) {
				digest.update(buffer, 0, count);
			}
			bis.close();
			byte[] hash = digest.digest();
			StringBuffer hexString = new StringBuffer();

			for (int i = 0; i < hash.length; i++) {
				String hex = Integer.toHexString(0xff & hash[i]);
				if (hex.length() == 1) hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString().toUpperCase();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

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
