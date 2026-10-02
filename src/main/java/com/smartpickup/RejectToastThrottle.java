package com.smartpickup;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Server-side throttle for "blacklist rejected" pickup toasts.
 *
 * <p>{@code ItemEntity.playerTouch} fires every tick while a player stands on
 * a refused item, so without a throttle the client would receive one toast
 * per tick. We keep the last-sent game tick per item-entity UUID and only let
 * a new toast through after the cooldown has elapsed.
 */
public final class RejectToastThrottle {

    /**
     * Cooldown (ticks) before the same item entity triggers another
     * "blacklist rejected" toast.
     */
    private static final long COOLDOWN_TICKS = 20L;

    /** Last tick a reject toast was sent, keyed by item-entity UUID. */
    private static final Map<UUID, Long> LAST_SENT = new HashMap<>();

    private RejectToastThrottle() {
    }

    /**
     * Returns whether a reject toast may be sent now for the given entity,
     * and records the current tick if it may.
     *
     * @param entityId the refused {@code ItemEntity}'s UUID
     * @param gameTime current server game time (ticks)
     */
    public static boolean trySend(UUID entityId, long gameTime) {
        Long lastSent = LAST_SENT.get(entityId);
        if (lastSent != null && gameTime - lastSent < COOLDOWN_TICKS) {
            return false;
        }
        LAST_SENT.put(entityId, gameTime);
        return true;
    }

    /** Clears all timestamps (e.g. on player logout). */
    public static void clear() {
        LAST_SENT.clear();
    }
}