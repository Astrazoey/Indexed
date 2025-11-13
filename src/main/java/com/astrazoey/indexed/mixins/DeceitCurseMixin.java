package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = ItemStack.class, priority = 1001)
public abstract class DeceitCurseMixin {

    @Redirect(
        method = "appendTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/component/type/TooltipDisplayComponent;shouldDisplay(Lnet/minecraft/component/ComponentType;)Z"
        )
    )
    private boolean hideDurabilityForCurse(TooltipDisplayComponent instance, ComponentType<?> type) {
        // If checking for DAMAGE component and item has curse, return false to hide it
        if (type == DataComponentTypes.DAMAGE && EnchantmentHelper.hasAnyEnchantmentsWith((ItemStack) (Object) this, Indexed.HIDE_DURABILITY)) {
            return false;
        }
        // Otherwise, use original behavior
        return instance.shouldDisplay(type);
    }
}
