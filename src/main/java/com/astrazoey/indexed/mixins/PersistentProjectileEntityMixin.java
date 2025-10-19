package com.astrazoey.indexed.mixins;

import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.AllOfEnchantmentEffects;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.registry.RegistryKeys;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(PersistentProjectileEntity.class)
public class PersistentProjectileEntityMixin {
    @ModifyConstant(method="onEntityHit", constant = @Constant(floatValue = 5.0F))
    public float buffDuration(float constant) {

        PersistentProjectileEntity projectile = ((PersistentProjectileEntity)(Object)this);
        if (projectile.getOwner() == null || projectile.getWeaponStack() == null)
            return constant;

        int flameLevel = EnchantmentHelper.getLevel(projectile.getOwner().getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(Enchantments.FLAME), projectile.getWeaponStack());

        if (flameLevel > 0) {

            constant = 5.0F + (flameLevel-1)*2;
        }

        return constant;
    }
}
