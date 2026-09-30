package com.smartpickup.client.network;

import com.smartpickup.network.PickupToastPayload;
import com.smartpickup.toast.PickupToastManager;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

/**
 * Client-side network wiring.
 *
 * <p>Registers the client handler for {@link PickupToastPayload} (server -> client
 * action-bar messages). Handlers run on the client thread, so it is safe to touch
 * the Minecraft instance directly.
 */
public final class ClientNetwork {

    private ClientNetwork() {
    }

    /** Called from the client initializer. */
    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(PickupToastPayload.TYPE, (payload, context) ->
                PickupToastManager.onPickup(payload.itemId(), payload.count()));
    }
}