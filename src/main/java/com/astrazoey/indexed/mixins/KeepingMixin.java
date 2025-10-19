package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.GameRules;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public class KeepingMixin {
}

@Mixin(ServerPlayerEntity.class)
class ServerPlayerEntityMixin {

    //This is here to make the keeping enchantment keep when clicking "respawn"
    @Inject(method="copyFrom", at = @At(value="RETURN"))
    public void keepItemsAfterDeath(ServerPlayerEntity oldPlayer, boolean alive, CallbackInfo ci) {
        if(!alive) {
            if(!oldPlayer.isSpectator() && !((ServerPlayerEntity) (Object) this).getEntityWorld().getGameRules().getBoolean(GameRules.KEEP_INVENTORY)) {
                ((ServerPlayerEntity) (Object) this).getInventory().clone(oldPlayer.getInventory());
            }
        }
    }
}

@Mixin(PlayerInventory.class)
class PlayerInventoryMixin {

    @Shadow
    @Final
    public PlayerEntity player;

    @Redirect(method="dropAll", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isEmpty()Z"))
    public boolean saveKeepingItems(ItemStack itemStack) {
        if(!itemStack.isEmpty()) {
            int keepingLevel = Indexed.getEnchantmentValue(Indexed.KEEP_ITEMS, (ServerWorld)player.getEntityWorld(), itemStack);
            System.out.println("Keeping level: " + keepingLevel);
            if(keepingLevel <= 0) {
                return false;
            } else {
                int randomValue = (int) (Math.random() * 100);
                int keepingFactor = 75 + (keepingLevel * 5);
                return (keepingFactor >= randomValue);
            }
        } else {
            return true;
        }
    }

}
