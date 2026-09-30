package com.smartpickup;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

/**
 * All configuration for Smart Pickup (Fabric port).
 *
 * <p>NeoForge had {@code ModConfigSpec}; Fabric has no built-in config system, so this
 * is a small self-contained JSON config persisted to {@code config/smartpickup.json}.
 * Every feature has its own independent toggle, plus a master switch.
 * When a toggle is off, vanilla behaviour applies for that feature.
 */
public final class Config {

    /** In-memory blacklisted item ids (keyed by registry name string). */
    public static Set<String> blacklist = new HashSet<>();

    /** In-memory hotbar-priority whitelist item ids. */
    public static Set<String> hotbarPriority = new HashSet<>();

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve(ModConstants.MODID + ".json");

    // ------------------------------------------------------------------
    // Persisted fields (defaults mirror the NeoForge version)
    // ------------------------------------------------------------------
    private static boolean masterEnabled = true;
    private static boolean pickupEnabled = true;
    private static boolean hotbarPriorityEnabled = false;
    private static boolean refillEnabled = true;
    private static boolean refillOnEmpty = true;
    private static boolean refillOnBreak = true;
    private static boolean refillSkipCreative = true;
    private static boolean blacklistEnabled = true;
    private static boolean toastEnabled = true;
    private static double toastDuration = 3.0;
    private static double toastMergeWindow = 1.5;
    private static boolean toastBlacklistRejected = true;

    private static List<String> blacklistItems = new ArrayList<>(List.of("minecraft:stone"));
    private static List<String> hotbarPriorityItems = new ArrayList<>();

    private Config() {
    }

    // ------------------------------------------------------------------
    // Load / save
    // ------------------------------------------------------------------

    /** Loads the config from disk (or keeps defaults if absent). Thread-safe enough for load-once. */
    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            save();
            return;
        }
        try (Reader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8)) {
            Data data = GSON.fromJson(reader, Data.class);
            if (data != null) {
                masterEnabled = data.masterEnabled;
                pickupEnabled = data.pickupEnabled;
                hotbarPriorityEnabled = data.hotbarPriorityEnabled;
                refillEnabled = data.refillEnabled;
                refillOnEmpty = data.refillOnEmpty;
                refillOnBreak = data.refillOnBreak;
                refillSkipCreative = data.refillSkipCreative;
                blacklistEnabled = data.blacklistEnabled;
                toastEnabled = data.toastEnabled;
                toastDuration = data.toastDuration;
                toastMergeWindow = data.toastMergeWindow;
                toastBlacklistRejected = data.toastBlacklistRejected;
                blacklistItems = data.blacklistItems == null ? new ArrayList<>() : new ArrayList<>(data.blacklistItems);
                hotbarPriorityItems = data.hotbarPriorityItems == null ? new ArrayList<>() : new ArrayList<>(data.hotbarPriorityItems);
            }
        } catch (IOException e) {
            ModConstants.LOGGER.error("Failed to load Smart Pickup config", e);
        }
        // Refresh in-memory sets
        blacklist = new HashSet<>(blacklistItems);
        hotbarPriority = new HashSet<>(hotbarPriorityItems);
    }

    /** Persists the current configuration to disk. */
    public static void save() {
        Data data = new Data();
        data.masterEnabled = masterEnabled;
        data.pickupEnabled = pickupEnabled;
        data.hotbarPriorityEnabled = hotbarPriorityEnabled;
        data.refillEnabled = refillEnabled;
        data.refillOnEmpty = refillOnEmpty;
        data.refillOnBreak = refillOnBreak;
        data.refillSkipCreative = refillSkipCreative;
        data.blacklistEnabled = blacklistEnabled;
        data.toastEnabled = toastEnabled;
        data.toastDuration = toastDuration;
        data.toastMergeWindow = toastMergeWindow;
        data.toastBlacklistRejected = toastBlacklistRejected;
        data.blacklistItems = new ArrayList<>(blacklistItems);
        data.hotbarPriorityItems = new ArrayList<>(hotbarPriorityItems);

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8)) {
                GSON.toJson(data, writer);
            }
        } catch (IOException e) {
            ModConstants.LOGGER.error("Failed to save Smart Pickup config", e);
        }
    }

    /** JSON data holder. */
    private static final class Data {
        boolean masterEnabled = true;
        boolean pickupEnabled = true;
        boolean hotbarPriorityEnabled = false;
        boolean refillEnabled = true;
        boolean refillOnEmpty = true;
        boolean refillOnBreak = true;
        boolean refillSkipCreative = true;
        boolean blacklistEnabled = true;
        boolean toastEnabled = true;
        double toastDuration = 3.0;
        double toastMergeWindow = 1.5;
        boolean toastBlacklistRejected = true;
        List<String> blacklistItems = new ArrayList<>(List.of("minecraft:stone"));
        List<String> hotbarPriorityItems = new ArrayList<>();
    }

    // ------------------------------------------------------------------
    // Getters (used by handlers and GUI)
    // ------------------------------------------------------------------

    public static boolean masterEnabled() { return masterEnabled; }
    public static boolean pickupEnabled() { return pickupEnabled; }
    public static boolean hotbarPriorityEnabled() { return hotbarPriorityEnabled; }
    public static boolean refillEnabled() { return refillEnabled; }
    public static boolean refillOnEmpty() { return refillOnEmpty; }
    public static boolean refillOnBreak() { return refillOnBreak; }
    public static boolean refillSkipCreative() { return refillSkipCreative; }
    public static boolean blacklistEnabled() { return blacklistEnabled; }
    public static boolean toastEnabled() { return toastEnabled; }
    public static double toastDuration() { return toastDuration; }
    public static double toastMergeWindow() { return toastMergeWindow; }
    public static boolean toastBlacklistRejected() { return toastBlacklistRejected; }

    // ------------------------------------------------------------------
    // Setters (persist on change)
    // ------------------------------------------------------------------

    public static void setMasterEnabled(boolean v) { masterEnabled = v; save(); }
    public static void setPickupEnabled(boolean v) { pickupEnabled = v; save(); }
    public static void setHotbarPriorityEnabled(boolean v) { hotbarPriorityEnabled = v; save(); }
    public static void setRefillEnabled(boolean v) { refillEnabled = v; save(); }
    public static void setRefillOnEmpty(boolean v) { refillOnEmpty = v; save(); }
    public static void setRefillOnBreak(boolean v) { refillOnBreak = v; save(); }
    public static void setRefillSkipCreative(boolean v) { refillSkipCreative = v; save(); }
    public static void setBlacklistEnabled(boolean v) { blacklistEnabled = v; save(); }
    public static void setToastEnabled(boolean v) { toastEnabled = v; save(); }
    public static void setToastBlacklistRejected(boolean v) { toastBlacklistRejected = v; save(); }

    // ------------------------------------------------------------------
    // Blacklist helpers
    // ------------------------------------------------------------------

    /** Returns whether the given item id (resource location string) is blacklisted. */
    public static boolean isBlacklisted(String itemId) {
        return blacklistEnabled && blacklist.contains(itemId);
    }

    /** Adds an item id to the blacklist (persisted). */
    public static void addBlacklist(String itemId) {
        if (!blacklistItems.contains(itemId)) {
            blacklistItems.add(itemId);
            blacklist.add(itemId);
            save();
        }
    }

    /** Removes an item id from the blacklist (persisted). */
    public static void removeBlacklist(String itemId) {
        if (blacklistItems.remove(itemId)) {
            blacklist.remove(itemId);
            save();
        }
    }

    // ------------------------------------------------------------------
    // Hotbar-priority whitelist helpers
    // ------------------------------------------------------------------

    /** Returns whether the given item id is in the hotbar-priority whitelist. */
    public static boolean isHotbarPriority(String itemId) {
        return hotbarPriorityEnabled && hotbarPriority.contains(itemId);
    }

    /** Adds an item id to the hotbar-priority whitelist (persisted). */
    public static void addHotbarPriority(String itemId) {
        if (!hotbarPriorityItems.contains(itemId)) {
            hotbarPriorityItems.add(itemId);
            hotbarPriority.add(itemId);
            save();
        }
    }

    /** Removes an item id from the hotbar-priority whitelist (persisted). */
    public static void removeHotbarPriority(String itemId) {
        if (hotbarPriorityItems.remove(itemId)) {
            hotbarPriority.remove(itemId);
            save();
        }
    }

    // ------------------------------------------------------------------
    // UI helpers
    // ------------------------------------------------------------------

    static boolean validateItemId(String id) {
        return id != null && BuiltInRegistries.ITEM.containsKey(ResourceLocation.tryParse(id));
    }
}