package com.astrazoey.indexed.mixins.client;

import com.astrazoey.indexed.ClientEnchantingConfigHolder;
import com.astrazoey.indexed.EnchantabilityConfig;
import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.MaxEnchantingSlots;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

@Mixin(ItemStack.class)
public class ItemToolTipMixin {

    @Inject(method="getTooltipLines", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void appendTooltip(Item.TooltipContext context, @Nullable Player player, TooltipFlag type, CallbackInfoReturnable<List<Component>> cir, TooltipDisplay tooltipDisplayComponent, List list) {

        ItemStack stack = (ItemStack) (Object) this;

        //if not an enchanted book
        if(stack.getItem() != Items.ENCHANTED_BOOK) {
            //If enchantable, add text.

            EnchantabilityConfig config = ClientEnchantingConfigHolder.get(stack.getItem());

            EnchantingType enchantingType = MaxEnchantingSlots.getEnchantType((stack));
            if(enchantingType != null) {
                MutableComponent mutableText;
                ChatFormatting formatting;

                if(MaxEnchantingSlots.getCurrent(((ItemStack) (Object) this)) <= config.getMaxEnchantingSlots()) {
                    formatting = ChatFormatting.BLUE;
                } else {
                    formatting = ChatFormatting.RED;
                }

                mutableText = Component.translatable("item.indexed.enchantment_tooltip", MaxEnchantingSlots.getCurrent((ItemStack) (Object) this), config.getMaxEnchantingSlots()).withStyle(formatting);

                list.add(mutableText);
            }
        }
    }

}
