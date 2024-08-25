package com.astrazoey.indexed.registry;

import com.astrazoey.indexed.Indexed;
import com.astrazoey.indexed.items.GoldBoundBookItem;
import com.astrazoey.indexed.items.VitalisItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class IndexedItems {

    public static final GoldBoundBookItem GOLD_BOUND_BOOK = new GoldBoundBookItem(new Item.Settings()
            //.group(ItemGroup.MISC)
    );

    public static final Item VITALIS = new VitalisItem(new Item.Settings()
            //.group(ItemGroup.MISC)
    );

    public static void registerItems() {
        Registry.register(Registries.ITEM, new Identifier(Indexed.MOD_ID, "gold_bound_book"), GOLD_BOUND_BOOK);
        Registry.register(Registries.ITEM, new Identifier(Indexed.MOD_ID, "vitalis"), VITALIS);
    }

}
