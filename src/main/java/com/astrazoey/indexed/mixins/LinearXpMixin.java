package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public class LinearXpMixin {
    @Inject(method = "getXpNeededForNextLevel", at = @At("HEAD"), cancellable = true)
    private void makeXpLinear(CallbackInfoReturnable<Integer> cir) {
        if(ConfigMain.enableLinearXp) {
            int level = ((Player) (Object) this).experienceLevel;
            cir.setReturnValue(ConfigMain.linearXpAmount); // flat XP per level
        }
    }
}
