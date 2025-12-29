package com.daytonjwatson.framework.autocrop;

public class AutoCropSettings {
    private final boolean blockImmatureBreaks;
    private final boolean breakProtectionEnabled;
    private final int breakProtectionSeconds;

    public AutoCropSettings(boolean blockImmatureBreaks, boolean breakProtectionEnabled, int breakProtectionSeconds) {
        this.blockImmatureBreaks = blockImmatureBreaks;
        this.breakProtectionEnabled = breakProtectionEnabled;
        this.breakProtectionSeconds = Math.max(0, breakProtectionSeconds);
    }

    public boolean isBlockImmatureBreaks() {
        return blockImmatureBreaks;
    }

    public boolean isBreakProtectionToggle() {
        return breakProtectionEnabled;
    }

    public boolean isBreakProtectionEnabled() {
        return breakProtectionEnabled && breakProtectionSeconds > 0;
    }

    public int getBreakProtectionSeconds() {
        return breakProtectionSeconds;
    }

    public AutoCropSettings withBlockImmatureBreaks(boolean value) {
        return new AutoCropSettings(value, breakProtectionEnabled, breakProtectionSeconds);
    }

    public AutoCropSettings withBreakProtectionEnabled(boolean value) {
        return new AutoCropSettings(blockImmatureBreaks, value, breakProtectionSeconds);
    }

    public AutoCropSettings withBreakProtectionSeconds(int seconds) {
        return new AutoCropSettings(blockImmatureBreaks, breakProtectionEnabled, seconds);
    }
}
