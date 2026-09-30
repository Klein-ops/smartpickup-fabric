package com.smartpickup.client.network;

import com.smartpickup.network.ScreenStatePayload;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.client.Minecraft;

/**
 * Client-side: tells the server whether a GUI screen is currently open, so that
 * auto-refill only runs while nothing is open.
 *
 * <p>The state is only sent to the server when it actually changes (open -> closed or
 * closed -> open), so this costs nothing in steady state.
 */
public final class ClientScreenSync {

    private static boolean lastOpen = false;

    private ClientScreenSync() {
    }

    /** Called from the client initializer. */
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> tick(mc));
        lastOpen = false;
    }

    private static void tick(Minecraft mc) {
        if (mc.player == null) {
            lastOpen = false;
            return;
        }

        boolean open = mc.screen != null;
        if (open != lastOpen) {
            lastOpen = open;
            ClientPlayNetworking.send(new ScreenStatePayload(open));
        }
    }
}