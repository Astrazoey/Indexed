package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(method = "performShooting(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/InteractionHand;Lnet/minecraft/world/item/ItemStack;FFLnet/minecraft/world/entity/LivingEntity;)V", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lnet/minecraft/server/level/ServerPlayer;awardStat(Lnet/minecraft/stats/Stat;)V"))
    public void multishotAdvancement(Level world, LivingEntity shooter, InteractionHand hand, ItemStack stack, float speed, float divergence, LivingEntity target, CallbackInfo ci) {
        if (EnchantmentHelper.getItemEnchantmentLevel(shooter.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.MULTISHOT), stack) >= 5) {
            Indexed.MULTISHOT_CROSSBOW.trigger((ServerPlayer)shooter);
        }
    }
}
