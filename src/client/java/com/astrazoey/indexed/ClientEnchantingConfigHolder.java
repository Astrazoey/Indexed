package com.astrazoey.indexed;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.Map;

public final class ClientEnchantingConfigHolder {
    private static Map<String, EnchantabilityConfig> CONFIG = Collections.emptyMap();

    public static void setConfig(Map<String, EnchantabilityConfig> map) {
        CONFIG = Map.copyOf(map);
    }

    public static EnchantabilityConfig get(Item item) {
        String id = String.valueOf(Registries.ITEM.getId(item));
        return CONFIG.getOrDefault(id, new EnchantabilityConfig(0, 0));
    }
}