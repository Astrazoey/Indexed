package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(DrawContext.class)
public class DeceitCurseClientMixin {

    @Redirect(
        method = "drawItemBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isItemBarVisible()Z"
        )
    )
    private boolean hideDurabilityBarForCurse(ItemStack stack) {
        // If the item has the curse, return false to hide the durability bar
        if (EnchantmentHelper.hasAnyEnchantmentsWith(stack, Indexed.HIDE_DURABILITY)) {
            return false;
        }
        // Otherwise, use the original behavior
        return stack.isItemBarVisible();
    }
}
