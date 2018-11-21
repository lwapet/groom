package fr.groom.android_emulator.sdk;

public class SdkToolLocator implements ToolLocator {
    @Override
    public String findInSdk(final boolean useLegacySdkStructure) {
        return TOOLS_BIN_DIR;
    }
}
