package com.astrazoey.indexed.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class ColorSpecialMixin {
    @Inject(method = "getName", at = @At(value = "TAIL"), cancellable = true)
    private static void colorSpecialEnchantments(RegistryEntry<Enchantment> enchantment, int level, CallbackInfoReturnable<Text> cir) {


        if ((level == 1 && enchantment.value().getMaxLevel() == 1) && (!enchantment.isIn(EnchantmentTags.CURSE))) {
            MutableText mutableText = enchantment.value().description().copy();
            Texts.setStyleIfAbsent(mutableText, Style.EMPTY.withColor(Formatting.DARK_GREEN));
            cir.setReturnValue(mutableText);
        }


    }
}
