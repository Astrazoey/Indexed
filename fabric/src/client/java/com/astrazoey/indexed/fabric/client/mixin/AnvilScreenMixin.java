package com.astrazoey.indexed.fabric.client.mixin;

import com.astrazoey.indexed.fabric.client.ClientEnchantingConfigHolder;
import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AnvilScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.util.CommonColors;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AnvilScreen.class, priority = 999)
class AnvilScreenMixin {
    @Unique private ItemStack indexed$itemStack;

    @Redirect(method = "extractLabels", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/AnvilMenu;getSlot(I)Lnet/minecraft/world/inventory/Slot;"))
    private Slot indexed$captureStack(AnvilMenu menu, int index) {
        Slot slot = menu.getSlot(index);
        indexed$itemStack = slot.getItem();
        return slot;
    }

    @Redirect(method = "extractLabels(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"))
    private void indexed$renderCost(GuiGraphicsExtractor graphics, Font font, Component text, int x, int y, int color) {
        EnchantabilityConfig config = ClientEnchantingConfigHolder.get(indexed$itemStack.getItem());
        if (MaxEnchantingSlots.getEnchantType(indexed$itemStack) != null && config.getMaxEnchantingSlots() < MaxEnchantingSlots.getCurrent(indexed$itemStack)) {
            text = Component.translatable("container.indexed.overcharged");
            x += 100 - font.width(text);
            color = CommonColors.SOFT_RED;
        }
        graphics.text(font, text, x, y, color);
    }
}
