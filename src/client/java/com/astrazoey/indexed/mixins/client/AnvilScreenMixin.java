package com.astrazoey.indexed.mixins.client;

import com.astrazoey.indexed.ClientEnchantingConfigHolder;
import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = AnvilScreen.class, priority = 999)
class AnvilScreenMixin {

    @Unique
    ItemStack itemStack;


    //get item stack
    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getSlot(I)Lnet/minecraft/screen/slot/Slot;"))
    public Slot getItemStack(AnvilScreenHandler anvilScreenHandler, int index) {
        Slot slot = anvilScreenHandler.getSlot(index);
        itemStack = slot.getStack();
        return slot;
    }


    //update text

    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getLevelCost()I"))
    public int checkOvercharge2(AnvilScreenHandler instance) {
        ItemStack item = instance.getSlot(2).getStack();

        EnchantabilityConfig config = ClientEnchantingConfigHolder.get(item.getItem());

        //If overcharged
        if(MaxEnchantingSlots.getEnchantType(item) != null) {
            if(config.getMaxEnchantingSlots() < MaxEnchantingSlots.getCurrent(item)) {
                return 1;
            }
        }

        return instance.getLevelCost();
    }

    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target="Lnet/minecraft/screen/slot/Slot;hasStack()Z"))
    public boolean checkOvercharge(Slot instance) {

        ItemStack item = instance.getStack();

        EnchantabilityConfig config = ClientEnchantingConfigHolder.get(item.getItem());

        //If overcharged
        if(MaxEnchantingSlots.getEnchantType(item) != null) {
            if(config.getMaxEnchantingSlots() < MaxEnchantingSlots.getCurrent(item)) {
                return true;
            }
        }
        return instance.hasStack();
    }


    //update text
    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"))
    public void setText(DrawContext context, TextRenderer textRenderer, Text text, int mouseX, int mouseY, int color) {

        EnchantabilityConfig config = ClientEnchantingConfigHolder.get(itemStack.getItem());

        if(MaxEnchantingSlots.getEnchantType(itemStack) != null) {
            if(config.getMaxEnchantingSlots() < MaxEnchantingSlots.getCurrent(itemStack)) {
                text = Text.translatable("container.indexed.overcharged");
                String textString = text.getString();
                mouseX = mouseX + 100 - textRenderer.getWidth(textString);
                color = Colors.LIGHT_RED;
            }
        }

        context.drawTextWithShadow(textRenderer, text, mouseX, 69, color);
    }

}