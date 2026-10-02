package com.smartpickup.mixin;

import java.util.UUID;

import com.smartpickup.Config;
import com.smartpickup.PickupHandler;
import com.smartpickup.RejectToastThrottle;
import com.smartpickup.network.PickupToastPayload;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Intercepts {@link ItemEntity#playerTouch(Player)} at the very top to apply
 * the smart pickup order, blacklist and toast instead of the vanilla
 * hotbar-first behaviour.
 *
 * <p>Replaces the NeoForge {@code ItemEntityPickupEvent.Pre} hook. The injection
 * cancels the whole vanilla method so we fully control the outcome.
 */
@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin {

    /** Reserved-for/thrower target UUID (private field in the vanilla class). */
    @Shadow
    private UUID target;

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    private void smartpickup$onPlayerTouch(Player player, CallbackInfo ci) {
        if (!Config.masterEnabled()) {
            return;
        }
        // The vanilla method returns immediately on the client; do the same.
        if (((ItemEntity) (Object) this).level().isClientSide()) {
            return;
        }

        ItemEntity self = (ItemEntity) (Object) this;
        ItemStack stack = self.getItem();
        if (stack.isEmpty()) {
            return;
        }

        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();

        // Feature 3: blacklist -> refuse pickup entirely.
        if (Config.blacklistEnabled() && Config.isBlacklisted(itemId)) {
            ci.cancel();
            // playerTouch fires every tick while the player stands on the
            // refused item; only send one toast per item entity per cooldown.
            if (Config.toastEnabled() && Config.toastBlacklistRejected()) {
                UUID entityId = self.getUUID();
                long gameTime = ((ServerPlayer) player).level().getGameTime();
                if (RejectToastThrottle.trySend(entityId, gameTime)) {
                    ServerPlayNetworking.send((ServerPlayer) player,
                            new PickupToastPayload(itemId, 0)); // 0 = rejected marker
                }
            }
            return;
        }

        // Feature 1: smart pickup order.
        if (Config.pickupEnabled()) {
            // Respect vanilla eligibility rules (pickup delay and thrower/target
            // reservation) so freshly thrown items are NOT vacuumed back up and
            // items reserved for another player are left alone.
            if (self.hasPickUpDelay()) {
                return;
            }
            if (this.target != null && !this.target.equals(player.getUUID())) {
                return;
            }

            // Cancel the vanilla pickup and do our own.
            ci.cancel();
            int remaining = PickupHandler.smartPickup(player, stack);
            int picked = stack.getCount() - remaining;
            if (picked > 0) {
                if (remaining > 0) {
                    self.getItem().setCount(remaining);
                } else {
                    self.discard();
                }
                PickupHandler.playPickupSound(player);
                if (Config.toastEnabled()) {
                    ServerPlayNetworking.send((ServerPlayer) player,
                            new PickupToastPayload(itemId, picked));
                }
            }
        }
        // Feature 1 off: vanilla pickup proceeds (do nothing).
    }
}