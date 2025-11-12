package com.astrazoey.indexed;

import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.ItemStack;

public interface MaxEnchantingSlots {



    @Deprecated
    static void setMax(Item item, int amount) {
        ((MaxEnchantingSlots) item).setMaxEnchantingSlots(amount);
    }

    static boolean setEnchantType(Item item, EnchantingType enchantingType) {

        if(item == Items.AIR) {
            return false;
        } else {
            ((MaxEnchantingSlots) item).setEnchantingType(enchantingType);
            return true;
        }


    }

    @Deprecated
    static int getMax(ItemStack itemStack) {
        if(itemStack != null) {
            return ((MaxEnchantingSlots) itemStack.getItem()).getMaxEnchantingSlots(itemStack);
        } else {
            return 0;
        }
    }

    static int getCurrent(ItemStack itemStack) {
        if(itemStack != null) {
            System.out.println("current enchanting slots is " + ((MaxEnchantingSlots) itemStack.getItem()).getEnchantingSlots(itemStack));
            return ((MaxEnchantingSlots) itemStack.getItem()).getEnchantingSlots(itemStack);
        } else {
            return 0;
        }
    }

    static EnchantingType getEnchantType(ItemStack itemStack) {
        if(itemStack != null) {
            return ((MaxEnchantingSlots) itemStack.getItem()).getEnchantingType(itemStack);
        } else {
            return EnchantingTypes.GENERIC;
        }
    }


    @Deprecated
    void setMaxEnchantingSlots(int amount);

    void setEnchantingType(EnchantingType enchantingType);

    @Deprecated
    int getMaxEnchantingSlots(ItemStack itemStack);

    int getEnchantingSlots(ItemStack itemStack);

    EnchantingType getEnchantingType(ItemStack itemStack);
}
