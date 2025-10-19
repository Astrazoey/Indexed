package com.astrazoey.indexed.mixins;

import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class LinearXpMixin {
    @Inject(method = "getNextLevelExperience", at = @At("HEAD"), cancellable = true)
    private void makeXpLinear(CallbackInfoReturnable<Integer> cir) {
        int level = ((PlayerEntity)(Object)this).experienceLevel;
        cir.setReturnValue(15); // flat XP per level
    }
}
