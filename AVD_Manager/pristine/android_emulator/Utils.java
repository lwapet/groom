package fr.groom.android_emulator;

import fr.groom.Util;
import fr.groom.android_emulator.sdk.AndroidSdk;
import fr.groom.android_emulator.sdk.Tool;
import fr.groom.android_emulator.sdk.cli.SdkCliCommand;
import fr.groom.core.ArgumentListBuilder;
import fr.groom.utils.VersionNumber;

import java.util.logging.Logger;

public class Utils {
	private static final Logger LOGGER = Logger.getLogger(Utils.class.getName());

	public static boolean isVersionOlderThan(final String strVersion, final String strVersionToCompare) {
		final VersionNumber version = new VersionNumber(Util.fixNull(strVersion));
		final VersionNumber versiontoCompare = new VersionNumber(Util.fixNull(strVersionToCompare));
		return version.isOlderThan(versiontoCompare);
	}

	public static boolean equalsVersion(final String strVersionA, final String strVersionB, final int partsToCompare) {
		String versionA = Util.fixNull(strVersionA);
		String versionB = Util.fixNull(strVersionB);

		if (partsToCompare <= 0) {
			return (versionA.equals(versionB));
		}

		final String[] splitA = versionA.split("\\.");
		final String[] splitB = versionB.split("\\.");

		for (int idx = 0; idx < partsToCompare; idx++) {
			final String a = (idx < splitA.length) ? splitA[idx] : "";
			final String b = (idx < splitB.length) ? splitB[idx] : "";
			if (!a.equals(b)) {
				return false;
			}
		}
		return true;
	}

	public static ArgumentListBuilder getToolCommand(AndroidSdk androidSdk, boolean isUnix, final SdkCliCommand sdkCmd) {
		// Determine the path to the desired tool
		final Tool tool = sdkCmd.getTool();
		final String executable;
		if (androidSdk.hasKnownRoot()) {
			executable = androidSdk.getSdkRoot() + "/" + tool.getPathInSdk(androidSdk, isUnix);
		} else {
			LOGGER.warning("SDK root not found. Assuming command is on the PATH");
			executable = tool.getExecutable(isUnix);
		}

		// Build tool command
		final ArgumentListBuilder builder = new ArgumentListBuilder(executable);
		final String args = sdkCmd.getArgs();
		if (args != null) {
			builder.add(Util.tokenize(args));
		}

		return builder;
	}

	public static boolean isProcessAlive(final Process process) {
		boolean exited = false;
		try {
			process.exitValue();
			exited = true;
		} catch (IllegalThreadStateException ex) {
		}
		return !exited;
	}

}
