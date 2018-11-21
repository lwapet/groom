package fr.groom.core;

import fr.groom.utils.VersionNumber;

import java.io.File;
import java.util.Locale;

public enum Platform {
	WINDOWS(';'),
	UNIX(':');

	public final char pathSeparator;

	private Platform(char pathSeparator) {
		this.pathSeparator = pathSeparator;
	}

	public static Platform current() {
		return File.pathSeparatorChar == ':' ? UNIX : WINDOWS;
	}

	public static boolean isDarwin() {
		return System.getProperty("os.name").toLowerCase(Locale.ENGLISH).startsWith("mac");
	}

	public static boolean isSnowLeopardOrLater() {
		try {
			return isDarwin() && (new VersionNumber(System.getProperty("os.version"))).compareTo(new VersionNumber("10.6")) >= 0;
		} catch (IllegalArgumentException var1) {
			return false;
		}
	}
}
