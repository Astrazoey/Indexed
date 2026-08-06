package com.astrazoey.indexed.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class ColorSpecialMixin {
    @Inject(method = "getFullname", at = @At(value = "TAIL"), cancellable = true)
    private static void colorSpecialEnchantments(Holder<Enchantment> enchantment, int level, CallbackInfoReturnable<Component> cir) {


        if ((level == 1 && enchantment.value().getMaxLevel() == 1) && (!enchantment.is(EnchantmentTags.CURSE))) {
            MutableComponent mutableText = enchantment.value().description().copy();
            ComponentUtils.mergeStyles(mutableText, Style.EMPTY.withColor(ChatFormatting.DARK_GREEN));
            cir.setReturnValue(mutableText);
        }


    }
}
