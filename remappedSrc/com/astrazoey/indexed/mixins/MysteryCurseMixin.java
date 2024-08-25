package com.astrazoey.indexed.mixins;

import com.astrazoey.indexed.Indexed;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = ItemStack.class, priority = 1001)
public abstract class MysteryCurseMixin {

    @Shadow
    protected static boolean isSectionVisible(int flags, ItemStack.TooltipSection tooltipSection) {
        return false;
    }

    @Redirect(method="getTooltip", at = @At(value="INVOKE", target = "Lnet/minecraft/item/ItemStack;isSectionVisible(ILnet/minecraft/item/ItemStack$TooltipSection;)Z", ordinal = 1))
    public boolean curseOfMystery(int flags, ItemStack.TooltipSection tooltipSection) {
        if(EnchantmentHelper.getLevel(Indexed.MYSTERY_CURSE, (ItemStack) (Object) this) > 0) {
            return false;
        } else {
            return isSectionVisible(flags, tooltipSection);
        }

    }


}