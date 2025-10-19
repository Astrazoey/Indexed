package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.ConfigMain;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(targets = "net.minecraft.loot.function.ApplyBonusLootFunction$OreDrops")
public class OreDropsMixin {
    @Inject(method = "getValue", at = @At(value="HEAD"), cancellable = true)
    public void mixinTest(Random random, int initialCount, int enchantmentLevel, CallbackInfoReturnable<Integer> cir) {
        if (ConfigMain.enableEnchantmentNerfs) {
            if (enchantmentLevel > 0) {
                int i = random.nextInt(enchantmentLevel+1)-1 + random.nextFloat() < 0.25f ? 1 : 0;
                if (i < 0)
                    i = 0;

                cir.setReturnValue(initialCount * (i + 1));
            } else {
                cir.setReturnValue(initialCount);
            }
        }
    }
}
