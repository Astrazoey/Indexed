package com.astrazoey.indexed.neoforge.client;

import com.astrazoey.indexed.EnchantabilityConfig;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

import java.util.Map;


public final class NeoForgeEnchantingConfigHolder {
    private static Map<String, EnchantabilityConfig> CONFIG = Map.of();

    private NeoForgeEnchantingConfigHolder() {
    }

    public static void setConfig(Map<String, EnchantabilityConfig> map) {
        CONFIG = Map.copyOf(map);
    }


    public static int getMaxEnchantingSlots(Item item, int fallback) {
        EnchantabilityConfig config = CONFIG.get(BuiltInRegistries.ITEM.getKey(item).toString());
        return config != null ? config.getMaxEnchantingSlots() : fallback;
    }
}
