package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class IndexedItems {

    public static final Item GOLD_BOUND_BOOK = new Item( new Item.Settings()
            .enchantable(40)
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("indexed", "gold_bound_book"))));

    public static final Item VITALIS = new Item(new Item.Settings()
            .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("indexed", "vitalis"))));

    public static void registerItems() {
        Registry.register(Registries.ITEM, Identifier.of(Indexed.MOD_ID, "gold_bound_book"), GOLD_BOUND_BOOK);
        Registry.register(Registries.ITEM, Identifier.of(Indexed.MOD_ID, "vitalis"), VITALIS);

        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(GOLD_BOUND_BOOK));
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(VITALIS));
    }

}
