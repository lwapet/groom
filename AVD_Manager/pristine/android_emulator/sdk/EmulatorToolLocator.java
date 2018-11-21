package fr.groom.android_emulator.sdk;

public class EmulatorToolLocator implements ToolLocator {
    @Override
    public String findInSdk(final boolean useLegacySdkStructure) {
        if (!useLegacySdkStructure) {
            return EMULATOR_DIR;
        } else {
            return TOOLS_DIR;
        }
    }
}
