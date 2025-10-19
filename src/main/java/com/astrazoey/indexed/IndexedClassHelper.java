package com.astrazoey.indexed;

import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class IndexedClassHelper implements IndexedInterfaceHelper {

    public static boolean boolValue;

    public static ThreadLocal<Boolean> booleanThreadLocal = ThreadLocal.withInitial(() -> false);

    @Override
    public void setBool(boolean value) {
        boolValue = value;
    }

    @Override
    public boolean getBool(IndexedClassHelper helper) {
        return boolValue;
    }
}
