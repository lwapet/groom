package fr.groom.android_emulator.sdk.cli;

import fr.groom.android_emulator.sdk.Tool;

import java.util.List;

/**
 * CLI commands using the SDK tools.
 */
public interface SdkToolsCommands {

    SdkCliCommand getSdkInstallAndUpdateCommand(final String proxySettings, final List<String> components);
    SdkCliCommand getListSdkComponentsCommand();
    SdkCliCommand getListExistingTargetsCommand();
    SdkCliCommand getListSystemImagesCommand();
    boolean isImageForPlatformAndABIInstalled(final String listSystemImagesOutput,
											  final String platform, final String abi);
    SdkCliCommand getCreatedAvdCommand(final String avdName, final boolean supportsSnapshots,
									   final String sdCardSize, final String screenResolutionSkinName, final String deviceDefinition,
									   final String androidTarget, final String systemImagePackagePath, final String tag);

    SdkCliCommand getAdbInstallPackageCommand(final String deviceIdentifier, final String packageFileName);
    SdkCliCommand getAdbUninstallPackageCommand(final String deviceIdentifier, final String packageId);

    /**
     * Creates the command ({@code Tool} and arguments to created a sdcard-images.
     *
     * @param absolutePathToSdCard The absolute path where the images should be created
     * @param requestedSdCardSize The requested size of the sdcard-image in bytes (may be suffixed with 'K', 'M', 'G')
     * @return a {@code SdkCommand} which holds the command to use and the arguments
     */
    SdkCliCommand getCreateSdkCardCommand(final String absolutePathToSdCard, final String requestedSdCardSize);

    SdkCliCommand getEmulatorListSnapshotsCommand(final String avdName, final Tool executable);

    SdkCliCommand getAdbStartServerCommand();
    SdkCliCommand getAdbKillServerCommand();

    @Deprecated
    SdkCliCommand getUpdateProjectCommand(final String projectPath);
    @Deprecated
    SdkCliCommand getUpdateTestProjectCommand(final String projectPath, final String testMainClass);
    @Deprecated
    SdkCliCommand getUpdateLibProjectCommand(final String projectPath);
}
