package com.smartpickup;

import org.slf4j.Logger;
import com.mojang.logging.LogUtils;

/**
 * Central constants for the Smart Pickup mod.
 */
public final class ModConstants {
    public static final String MODID = "smartpickup";
    public static final String CHANNEL_VERSION = "1";

    /** Shared logger for the mod. */
    public static final Logger LOGGER = LogUtils.getLogger();

    private ModConstants() {
    }
}