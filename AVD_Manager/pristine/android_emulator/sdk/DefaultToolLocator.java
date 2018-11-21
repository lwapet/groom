package fr.groom.android_emulator.sdk;

public class DefaultToolLocator implements ToolLocator {
    @Override
    public String findInSdk(final boolean useLegacySdkStructure) {
        return TOOLS_DIR;
    }
}
