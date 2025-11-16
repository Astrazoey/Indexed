package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class LinearXpMixin {
    @Inject(method = "getNextLevelExperience", at = @At("HEAD"), cancellable = true)
    private void makeXpLinear(CallbackInfoReturnable<Integer> cir) {
        if(ConfigMain.enableLinearXp) {
            int level = ((PlayerEntity) (Object) this).experienceLevel;
            cir.setReturnValue(ConfigMain.linearXpAmount); // flat XP per level
        }
    }
}
