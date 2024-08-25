package com.astrazoey.indexed.mixins;


import net.minecraft.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(EnchantmentHelper.class)
public class SwiftSneakMixin {

    @ModifyConstant(method = "getSwiftSneakSpeedBoost", constant = @Constant(floatValue = 1.5f, ordinal = 0))
    private static float changeSwiftSneakAmount(float constant) {

        return 0.10f;
    }

}
