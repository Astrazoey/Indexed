package com.astrazoey.indexed.mixins;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class BowItemMixin {

    @Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/enchantment/EnchantmentHelper;getLevel(Lnet/minecraft/enchantment/Enchantment;Lnet/minecraft/item/ItemStack;)I", ordinal = 0))
    public int onStoppedUsing(Enchantment enchantment, ItemStack stack) {

        int infinityLevel = EnchantmentHelper.getLevel(Enchantments.INFINITY, stack);

        //System.out.println("infinity level is " + infinityLevel);

        double infinityChance = 0.5d + (infinityLevel * 0.1d);

        //System.out.println("infinity chance is + " + infinityChance);

        double randomNumber = Math.random();

        //System.out.println("random number is " + randomNumber);

        if(infinityChance > randomNumber && infinityLevel > 0) {
            //System.out.println("infinity triggered");
            return 1;
        } else {
            //System.out.println("infinity failed");
            return 0;
        }
    }

}
