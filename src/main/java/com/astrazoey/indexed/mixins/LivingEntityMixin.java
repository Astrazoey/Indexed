package com.astrazoey.indexed.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(LivingEntity.class)
class DamageLivingEntityMixin {

    @ModifyConstant(method = "hurtServer", constant = @Constant(intValue = 20, ordinal = 0))
    public int changeIFrames(int constant, @Local(ordinal = 0, argsOnly = true) final DamageSource source, @Local(ordinal = 0, argsOnly = true) final float amount) {

        int invulnerableTime = constant;

        if (source.is(DamageTypeTags.IS_PROJECTILE)) {
            invulnerableTime = 0;
        }

        return invulnerableTime;
    }

}
