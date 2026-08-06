package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Constants;
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


    public static void registerItems() {
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "gold_bound_book"), GOLD_BOUND_BOOK);
    }
}
