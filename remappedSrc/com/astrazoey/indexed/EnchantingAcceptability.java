package com.astrazoey.indexed;

import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.*;

public class EnchantingAcceptability {

    public boolean checkAcceptability(Enchantment enchantment, ItemStack stack) {
        if(acceptableCheck(enchantment, stack)) {
            return enchantment.isAcceptableItem(stack);
        } else {
            return false;
        }
    }

    public boolean checkAcceptability(Enchantment enchantment, ThreadLocal<ItemStack> itemType) {
        if(acceptableCheck(enchantment, itemType.get())) {
            return enchantment.isAvailableForRandomSelection();
        } else {
            return false;
        }
    }

    public boolean acceptableCheck(Enchantment enchantment, ItemStack itemStack) {
        if((enchantment == Indexed.QUICK_FLIGHT && !ConfigMain.enableQuickFlight)) {
            return false;
        }

        if((itemStack.getItem() instanceof ElytraItem || itemStack.getItem() instanceof BookItem) && enchantment == Indexed.QUICK_FLIGHT) {
            return true;
        } else if(!(itemStack.getItem() instanceof ElytraItem || itemStack.getItem() instanceof BookItem) && enchantment == Indexed.SLOW_BURN) {
            return false;
        } else {
            return true;
        }
    }
}
