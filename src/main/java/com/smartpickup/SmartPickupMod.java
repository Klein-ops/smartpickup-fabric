package com.smartpickup;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

import com.smartpickup.network.NetworkHandler;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/**
 * Smart Pickup - intelligent item pickup (Fabric port).
 *
 * <p>Features (each independently toggleable, plus a master switch):
 * <ol>
 *   <li>Picked-up items fill the main inventory before free hotbar slots.</li>
 *   <li>Auto-refill empty or broken held stacks from the inventory.</li>
 *   <li>Configurable pickup blacklist with an item-picker UI.</li>
 *   <li>Small action-bar toast when items are picked up.</li>
 * </ol>
 */
public final class SmartPickupMod implements ModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        // Self-contained JSON config (config/smartpickup.json).
        Config.load();

        // Register custom payload types (both directions) and the server-side
        // receiver that tracks each player's screen state.
        NetworkHandler.registerCommon();

        // Server tick: drive the auto-refill "used up" detection.
        ServerTickEvents.END_SERVER_TICK.register(server ->
                server.getPlayerList().getPlayers().forEach(RefillHandler::tick));

        // Clear per-player state on disconnect.
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            var player = handler.getPlayer();
            if (player != null) {
                ScreenStateTracker.clear(player.getUUID());
                RefillHandler.onLogout(player);
            }
        });

        LOGGER.info("Smart Pickup loaded!");
    }
}