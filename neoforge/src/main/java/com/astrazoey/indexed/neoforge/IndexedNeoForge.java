package com.astrazoey.indexed.neoforge;

import com.astrazoey.indexed.CommonClass;
import com.astrazoey.indexed.Constants;
import com.astrazoey.indexed.neoforge.client.IndexedNeoForgeClient;
import com.astrazoey.indexed.neoforge.client.NeoForgeEnchantingConfigHolder;
import com.astrazoey.indexed.network.ConfigS2CPayload;
import com.astrazoey.indexed.registry.IndexedItems;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.LootTableLoadEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.RegisterEvent;

@Mod(Constants.MOD_ID)
public class IndexedNeoForge {

    public IndexedNeoForge(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegister);
        modEventBus.addListener(this::registerPayloads);
        modEventBus.addListener(this::buildCreativeTab);

        NeoForge.EVENT_BUS.addListener(this::onLootTableLoad);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        // Client-only handlers (tooltip/particles). Referenced class is never loaded on a server.
        if (FMLEnvironment.getDist() == Dist.CLIENT) {
            IndexedNeoForgeClient.register(modEventBus);
        }
    }

    private void onRegister(RegisterEvent event) {
        CommonClass.init();
    }

    private void registerPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(Constants.MOD_ID);
        registrar.playToClient(ConfigS2CPayload.ID, ConfigS2CPayload.CODEC, (payload, context) -> {
            // Client-side: cache the synced config for tooltip/anvil screen use
            NeoForgeEnchantingConfigHolder.setConfig(payload.configList());
        });
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            if (CommonClass.getConfig() != null) {
                ConfigS2CPayload payload = new ConfigS2CPayload(CommonClass.getConfig().getItemEnchanting());
                player.connection.send(payload);
            }
        }
    }

    private void onLootTableLoad(LootTableLoadEvent event) {
        var key = event.getKey();
        if (key == null) return;

        // Main indexed loot pool
        if (BuiltInLootTables.END_CITY_TREASURE.equals(key) ||
                BuiltInLootTables.ABANDONED_MINESHAFT.equals(key) ||
                BuiltInLootTables.STRONGHOLD_LIBRARY.equals(key) ||
                BuiltInLootTables.BASTION_TREASURE.equals(key) ||
                BuiltInLootTables.WOODLAND_MANSION.equals(key) ||
                BuiltInLootTables.NETHER_BRIDGE.equals(key) ||
                BuiltInLootTables.PILLAGER_OUTPOST.equals(key) ||
                BuiltInLootTables.SIMPLE_DUNGEON.equals(key) ||
                BuiltInLootTables.RUINED_PORTAL.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_LOOT))
                    .build());
        }

        if (BuiltInLootTables.NETHER_BRIDGE.equals(key) || BuiltInLootTables.BASTION_OTHER.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_NETHER_BRIDGE))
                    .build());
        }

        if (BuiltInLootTables.PILLAGER_OUTPOST.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_OUTPOST))
                    .build());
        }

        if (BuiltInLootTables.IGLOO_CHEST.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_IGLOO))
                    .build());
        }

        if (BuiltInLootTables.WOODLAND_MANSION.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_MANSION))
                    .build());
        }

        if (BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_MINESHAFT))
                    .build());
        }

        if (BuiltInLootTables.SHIPWRECK_TREASURE.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_SHIPWRECK))
                    .build());
        }

        if (BuiltInLootTables.BURIED_TREASURE.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_BURIED_TREASURE))
                    .build());
        }

        if (BuiltInLootTables.UNDERWATER_RUIN_BIG.equals(key) ||
                BuiltInLootTables.UNDERWATER_RUIN_SMALL.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_WATER_RUIN))
                    .build());
        }

        if (BuiltInLootTables.JUNGLE_TEMPLE.equals(key) ||
                BuiltInLootTables.DESERT_PYRAMID.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_TEMPLE))
                    .build());
        }

        if (BuiltInLootTables.SIMPLE_DUNGEON.equals(key)) {
            event.getTable().addPool(LootPool.lootPool()
                    .add(NestedLootTable.lootTableReference(CommonClass.INDEXED_DUNGEON))
                    .build());
        }
    }

    private void buildCreativeTab(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            event.insertAfter(new ItemStack(Items.ENCHANTING_TABLE), new ItemStack(CommonClass.CRYSTAL_GLOBE.asItem()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (event.getTabKey() == CreativeModeTabs.INGREDIENTS) {
            event.insertAfter(new ItemStack(Items.BOOK), new ItemStack(IndexedItems.GOLD_BOUND_BOOK), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
    }
}
