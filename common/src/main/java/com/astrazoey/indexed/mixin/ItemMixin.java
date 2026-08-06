package com.astrazoey.indexed.mixin;

import com.astrazoey.indexed.EnchantingType;
import com.astrazoey.indexed.MaxEnchantingSlots;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Item.class)
public abstract class ItemMixin implements MaxEnchantingSlots {
    @Shadow public abstract Item asItem();

    public int usedEnchantingSlots = 0;
    public EnchantingType enchantingType;


    @Override
    public EnchantingType getEnchantingType(ItemStack stack) {
        return enchantingType;
    }

    @Override
    public void setEnchantingType(EnchantingType enchantingType) {
        this.enchantingType = enchantingType;
    }


    @Override
    public int getEnchantingSlots(ItemStack itemStack) {
        ItemEnchantments itemEnchantments = EnchantmentHelper.getEnchantmentsForCrafting(itemStack);

        int totalLevels = 0;

        for(var enchantmentEntry : itemEnchantments.entrySet()) {
            if(enchantmentEntry.getKey().is(EnchantmentTags.CURSE)) {
                totalLevels -= enchantmentEntry.getIntValue();
            } else {
                totalLevels += enchantmentEntry.getIntValue();
            }
        }

        usedEnchantingSlots = totalLevels;

        return totalLevels;
    }

}
