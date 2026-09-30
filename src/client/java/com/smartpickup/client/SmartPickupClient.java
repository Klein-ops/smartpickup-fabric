package com.smartpickup.client;

import net.fabricmc.api.ClientModInitializer;

import com.smartpickup.client.network.ClientNetwork;
import com.smartpickup.client.network.ClientScreenSync;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/**
 * Client-side initializer for Smart Pickup.
 *
 * <p>Registers the client network receiver for pickup toasts and the screen
 * state sync (tells the server whether any GUI is open, so auto-refill is
 * suppressed while the player is in a screen).
 */
public final class SmartPickupClient implements ClientModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        ClientNetwork.register();
        ClientScreenSync.register();

        LOGGER.info("Smart Pickup client initialized!");
    }
}