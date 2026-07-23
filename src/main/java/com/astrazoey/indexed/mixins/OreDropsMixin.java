package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.world.level.storage.loot.functions.ApplyBonusCount$OreDrops")
public class OreDropsMixin {
    @Inject(method = "calculateNewCount", at = @At(value="HEAD"), cancellable = true)
    public void mixinTest(RandomSource random, int initialCount, int enchantmentLevel, CallbackInfoReturnable<Integer> cir) {
        if (ConfigMain.enableEnchantmentNerfs) {
            if (enchantmentLevel > 0) {
                int i = random.nextInt(enchantmentLevel + 2) - 1;

                if(random.nextFloat() < 0.1) {
                    i = 0;
                }

                if (i < 0) {
                    i = 0;
                }

                cir.setReturnValue(initialCount * (i + 1));
            } else {
                cir.setReturnValue(initialCount);
            }
        }
    }
}
