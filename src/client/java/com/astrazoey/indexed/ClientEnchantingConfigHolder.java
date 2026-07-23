package com.astrazoey.indexed;

import java.util.Collections;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public final class ClientEnchantingConfigHolder {
    private static Map<String, EnchantabilityConfig> CONFIG = Collections.emptyMap();

    public static void setConfig(Map<String, EnchantabilityConfig> map) {
        CONFIG = Map.copyOf(map);
    }

    public static EnchantabilityConfig get(Item item) {
        String id = String.valueOf(BuiltInRegistries.ITEM.getKey(item));
        return CONFIG.getOrDefault(id, new EnchantabilityConfig(0, 0));
    }
}
