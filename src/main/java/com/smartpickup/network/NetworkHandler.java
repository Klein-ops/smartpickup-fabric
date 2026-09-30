package com.smartpickup.network;

import com.smartpickup.ScreenStateTracker;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

/**
 * Common (server + client) network registration for the mod's payloads.
 *
 * <p>Payload <b>types</b> must be registered on both sides (they are part of
 * the shared protocol), so this runs from the main initializer. The
 * server-side <i>handler</i> for {@link ScreenStatePayload} is also registered
 * here; the client-side handler for {@link PickupToastPayload} lives in the
 * client source set ({@code com.smartpickup.client.network.ClientNetwork}),
 * because it references client-only classes.
 */
public final class NetworkHandler {

    private NetworkHandler() {
    }

    /** Called from the mod initializer on both sides. */
    public static void registerCommon() {
        // Server -> Client: pickup toast.
        PayloadTypeRegistry.playS2C().register(PickupToastPayload.TYPE, PickupToastPayload.STREAM_CODEC);
        // Client -> Server: screen open/close state (used to guard auto-refill).
        PayloadTypeRegistry.playC2S().register(ScreenStatePayload.TYPE, ScreenStatePayload.STREAM_CODEC);

        // Server side: client reports its screen state -> update the tracker.
        ServerPlayNetworking.registerGlobalReceiver(ScreenStatePayload.TYPE, (payload, context) -> {
            context.server().execute(() ->
                    ScreenStateTracker.set(context.player().getUUID(), payload.open()));
        });
    }
}