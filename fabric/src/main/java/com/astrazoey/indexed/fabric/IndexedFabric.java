package com.astrazoey.indexed.fabric;

import com.astrazoey.indexed.CommonClass;
import com.astrazoey.indexed.Constants;
import com.astrazoey.indexed.network.ConfigS2CPayload;
import com.astrazoey.indexed.registry.IndexedItems;
import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;

public class IndexedFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CommonClass.init();

        // Creative tab entries
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.FUNCTIONAL_BLOCKS).register(output -> {
            output.insertAfter(Items.ENCHANTING_TABLE, new ItemStack(CommonClass.CRYSTAL_GLOBE.asItem()));
        });
        CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.INGREDIENTS).register(output -> {
            output.insertAfter(Items.BOOK, new ItemStack(IndexedItems.GOLD_BOUND_BOOK));
        });

        // Loot table injection
        registerLootTableModifications();

        // Config reload on server events
        Identifier identifier = Identifier.parse(Constants.MOD_ID);
        ServerLifecycleEvents.SERVER_STARTING.register(identifier, callbacks -> {
            Constants.LOG.info("Server starting. Loading config.");
            initializeConfig();
        });

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(identifier, (server, serverResourceManager) -> {
            Constants.LOG.info("Server data pack reload. Loading config.");
            initializeConfig();
        });

        // Config sync networking
        PayloadTypeRegistry.clientboundPlay().register(ConfigS2CPayload.ID, ConfigS2CPayload.CODEC);

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;
            Constants.LOG.info("Server player join. Sending config to player.");
            ConfigS2CPayload activePayload = new ConfigS2CPayload(CommonClass.getConfig().getConfigList());
            ServerPlayNetworking.send(player, activePayload);
        });
    }

    private static void initializeConfig() {
        CommonClass.loadConfig();
    }

    private static void registerLootTableModifications() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.END_CITY_TREASURE.equals(key) ||
                    BuiltInLootTables.ABANDONED_MINESHAFT.equals(key) ||
                    BuiltInLootTables.STRONGHOLD_LIBRARY.equals(key) ||
                    BuiltInLootTables.BASTION_TREASURE.equals(key) ||
                    BuiltInLootTables.WOODLAND_MANSION.equals(key) ||
                    BuiltInLootTables.NETHER_BRIDGE.equals(key) ||
                    BuiltInLootTables.PILLAGER_OUTPOST.equals(key) ||
                    BuiltInLootTables.SIMPLE_DUNGEON.equals(key) ||
                    BuiltInLootTables.RUINED_PORTAL.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_LOOT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.NETHER_BRIDGE.equals(key) || BuiltInLootTables.BASTION_OTHER.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_NETHER_BRIDGE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.PILLAGER_OUTPOST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_OUTPOST));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.IGLOO_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_IGLOO));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.WOODLAND_MANSION.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_MANSION));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_MINESHAFT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.SHIPWRECK_TREASURE.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_SHIPWRECK));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.BURIED_TREASURE.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_BURIED_TREASURE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.UNDERWATER_RUIN_BIG.equals(key) ||
                    BuiltInLootTables.UNDERWATER_RUIN_SMALL.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_WATER_RUIN));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.JUNGLE_TEMPLE.equals(key) ||
                    BuiltInLootTables.DESERT_PYRAMID.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_TEMPLE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.SIMPLE_DUNGEON.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_DUNGEON));
                tableBuilder.pool(poolBuilder.build());
            }
        });
    }
}
