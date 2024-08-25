package com.astrazoey.indexed.mixins;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;



@Mixin(ExperienceOrbEntity.class)
public class MendingMixin {

    @Shadow
    private int amount;


    @Inject(method = "repairPlayerGears", at = @At(value="HEAD"), cancellable = true)
    public void adjustRepairAmountForMending(PlayerEntity player, int amount, CallbackInfoReturnable<Integer> cir) {
        Map.Entry<EquipmentSlot, ItemStack> entry = EnchantmentHelper.chooseEquipmentWith(Enchantments.MENDING, player, ItemStack::isDamaged);
        if(entry != null) {
            ItemStack itemStack = entry.getValue();

            int enchantmentLevel = EnchantmentHelper.getLevel(Enchantments.MENDING, itemStack);

            //System.out.println("Start amount = " + this.amount);
            this.amount = amount * enchantmentLevel;
            //System.out.println("End amount = " + this.amount);
        }
    }

}
