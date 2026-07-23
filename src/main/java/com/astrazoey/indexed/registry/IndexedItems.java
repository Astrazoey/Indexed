package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;

public class IndexedItems {

    public static final Item GOLD_BOUND_BOOK = new Item( new Item.Properties()
            .enchantable(40)
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("indexed", "gold_bound_book"))));

    public static final Item VITALIS = new Item(new Item.Properties()
            .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("indexed", "vitalis"))));

    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Indexed.MOD_ID, "gold_bound_book"), GOLD_BOUND_BOOK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Indexed.MOD_ID, "vitalis"), VITALIS);

        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(GOLD_BOUND_BOOK));
        //ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.add(VITALIS));
    }

}
