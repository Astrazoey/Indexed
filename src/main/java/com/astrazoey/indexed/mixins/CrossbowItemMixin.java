package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Inject(method="shootAll", at= @At(value="INVOKE", shift = At.Shift.AFTER, target="Lnet/minecraft/server/network/ServerPlayerEntity;incrementStat(Lnet/minecraft/stat/Stat;)V"))
    public void multishotAdvancement(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence, LivingEntity target, CallbackInfo ci) {
        if (EnchantmentHelper.getLevel(shooter.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.MULTISHOT), stack) >= 5) {
            Indexed.MULTISHOT_CROSSBOW.trigger((ServerPlayerEntity)shooter);
        }
    }
}
