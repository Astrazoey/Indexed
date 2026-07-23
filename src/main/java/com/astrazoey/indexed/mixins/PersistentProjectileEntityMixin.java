package com.astrazoey.indexed.mixins;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.projectile.arrow.AbstractArrow;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(AbstractArrow.class)
public class PersistentProjectileEntityMixin {
    @ModifyConstant(method="onHitEntity", constant = @Constant(floatValue = 5.0F))
    public float buffDuration(float constant) {

        AbstractArrow projectile = ((AbstractArrow)(Object)this);
        if (projectile.getOwner() == null || projectile.getWeaponItem() == null)
            return constant;

        int flameLevel = EnchantmentHelper.getItemEnchantmentLevel(projectile.getOwner().registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.FLAME), projectile.getWeaponItem());

        if (flameLevel > 0) {

            constant = 5.0F + (flameLevel-1)*2;
        }

        return constant;
    }
}
