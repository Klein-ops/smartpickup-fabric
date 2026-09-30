package com.smartpickup.mixin;

import com.smartpickup.RefillHandler;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Handles the "broken tool" auto-refill trigger.
 *
 * <p>Replaces the NeoForge {@code PlayerDestroyItemEvent}. Vanilla calls
 * {@link ServerPlayer#onEquippedItemBroken(Item, EquipmentSlot)} exactly once a
 * held item breaks, giving us the precise equipment slot. We forward that to
 * {@link RefillHandler#onItemBroken}.
 */
@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "onEquippedItemBroken", at = @At("HEAD"))
    private void smartpickup$onItemBroken(Item item, EquipmentSlot slot, CallbackInfo ci) {
        ServerPlayer self = (ServerPlayer) (Object) this;

        // The broken stack may already be gone from the hand by the time this
        // callback runs; fall back to a default instance of the item so the
        // refill scan still matches by item type.
        ItemStack broken = self.getItemBySlot(slot);
        if (broken.isEmpty() || !broken.is(item)) {
            broken = item.getDefaultInstance();
        }
        RefillHandler.onItemBroken(self, slot, broken);
    }
}