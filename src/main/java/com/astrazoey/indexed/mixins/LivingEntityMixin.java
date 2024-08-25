package com.astrazoey.indexed.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.tag.DamageTypeTags;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
class DamageLivingEntityMixin {

    @ModifyConstant(method = "damage", constant = @Constant(intValue = 20, ordinal = 0))
    public int changeIFrames(int constant, @Local(ordinal = 0, argsOnly = true) final DamageSource source, @Local(ordinal = 0, argsOnly = true) final float amount) {

        int invulnerableTime = constant;

        if (source.isIn(DamageTypeTags.IS_PROJECTILE)) {
            invulnerableTime = 0;
        }

        return invulnerableTime;
    }

}
