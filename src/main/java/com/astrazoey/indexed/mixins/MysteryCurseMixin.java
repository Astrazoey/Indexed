package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Consumer;

@Mixin(value = ItemStack.class, priority = 1001)
public abstract class MysteryCurseMixin {


    @Inject(method="appendComponentTooltip", at = @At(value="HEAD"), cancellable = true)
    public <T> void curseOfMystery(ComponentType<T> componentType, Item.TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type, CallbackInfo ci) {
        if (EnchantmentHelper.hasAnyEnchantmentsWith((ItemStack) (Object) this, Indexed.HIDE_ENCHANTMENTS)) {
            ci.cancel();
        }
    }

}