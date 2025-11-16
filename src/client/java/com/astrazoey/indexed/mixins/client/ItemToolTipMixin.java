package com.astrazoey.indexed.mixins.client;

import com.astrazoey.indexed.ClientEnchantingConfigHolder;
import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;

@Mixin(ItemStack.class)
public class ItemToolTipMixin {

    @Inject(method="getTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/item/Item$TooltipContext;Lnet/minecraft/component/type/TooltipDisplayComponent;Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/tooltip/TooltipType;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void appendTooltip(Item.TooltipContext context, @Nullable PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, TooltipDisplayComponent tooltipDisplayComponent, List list) {

        ItemStack stack = (ItemStack) (Object) this;

        //if not an enchanted book
        if(stack.getItem() != Items.ENCHANTED_BOOK) {
            //If enchantable, add text.

            EnchantabilityConfig config = ClientEnchantingConfigHolder.get(stack.getItem());

            EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType((stack));
            if(enchantingType != null) {
                MutableText mutableText;
                Formatting formatting;

                if(MaxEnchantingSlots.getCurrent(((ItemStack) (Object) this)) <= config.getMaxEnchantingSlots()) {
                    formatting = Formatting.BLUE;
                } else {
                    formatting = Formatting.RED;
                }

                mutableText = (Text.translatable("item.indexed.enchantment_tooltip", MaxEnchantingSlots.getCurrent((ItemStack) (Object) this), config.getMaxEnchantingSlots())).formatted(formatting);

                list.add(mutableText);
            }
        }
    }

}
