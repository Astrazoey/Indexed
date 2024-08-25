package com.astrazoey.indexed.archive;

import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
//import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
/*
@Mixin(AnvilScreen.class)
public class AnvilScreenMixin {

    ItemStack itemStack;

    //get item stack
    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target = "Lnet/minecraft/screen/AnvilScreenHandler;getSlot(I)Lnet/minecraft/screen/slot/Slot;"))
    public Slot getItemStack(AnvilScreenHandler anvilScreenHandler, int index) {
        Slot slot = anvilScreenHandler.getSlot(index);
        itemStack = slot.getStack();
        return slot;
    }

    //update text
    @Redirect(method = "drawForeground", at = @At(value="INVOKE", target = "Lnet/minecraft/client/font/TextRenderer;drawWithShadow(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/text/Text;FFI)I"))
    public int setText(TextRenderer textRenderer, MatrixStack matrices, Text text, float x, float y, int color) {

        if(MaxEnchantingSlots.getEnchantType(itemStack) != null) {
            if(MaxEnchantingSlots.getEnchantType(itemStack).getMaxEnchantingSlots() < MaxEnchantingSlots.getCurrent(itemStack)) {
                text = new TranslatableText("container.indexed.overcharged");
                String textString = text.getString();
                x = x + 105 - textRenderer.getWidth(textString);
            }
        }

        return textRenderer.drawWithShadow(matrices, (Text)text, (float)x, 69.0F, color);
    }

}
*/