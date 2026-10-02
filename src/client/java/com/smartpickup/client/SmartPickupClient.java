package com.smartpickup.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;

import net.minecraft.resources.ResourceLocation;

import com.smartpickup.ModConstants;
import com.smartpickup.client.network.ClientNetwork;
import com.smartpickup.client.network.ClientScreenSync;
import com.smartpickup.toast.PickupToastRenderer;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

/**
 * Client-side initializer for Smart Pickup.
 *
 * <p>Registers the client network receiver for pickup toasts, the screen
 * state sync (tells the server whether any GUI is open, so auto-refill is
 * suppressed while the player is in a screen), and the stacked pickup-toast
 * HUD layer just above the hotbar (after the overlay-message layer, so it
 * paints on top of it).
 */
public final class SmartPickupClient implements ClientModInitializer {

    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitializeClient() {
        ClientNetwork.register();
        ClientScreenSync.register();

        // Register the stacked pickup-toast layer above the vanilla action-bar
        // message (equivalent to NeoForge's RegisterGuiLayersEvent.registerAbove).
        HudElementRegistry.attachElementBefore(
                VanillaHudElements.OVERLAY_MESSAGE,
                ResourceLocation.fromNamespaceAndPath(ModConstants.MODID, "pickup_toasts"),
                PickupToastRenderer::render);

        LOGGER.info("Smart Pickup client initialized!");
    }
}