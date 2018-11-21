package fr.groom.android_emulator.sdk;

public class PlatformToolLocator implements ToolLocator {
    @Override
    public String findInSdk(final boolean useLegacySdkStructure) {
        return PLATFORM_TOOLS_DIR;
    }
}
