
package com.astrazoey.indexed;

import com.astrazoey.indexed.blocks.CrystalGlobeBlock;
import com.astrazoey.indexed.criterion.*;
import com.astrazoey.indexed.mixins.EnchantmentMixin;
import com.astrazoey.indexed.mixins.CriteriaTriggersAccessor;
import com.astrazoey.indexed.network.ConfigS2CPayload;
import com.astrazoey.indexed.registry.IndexedItems;
import com.astrazoey.indexed.registry.IndexedParticles;
import com.astrazoey.indexed.status_effects.EnchantedStatusEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.fabric.mixin.item.EnchantmentBuilderAccessor;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.advancements.predicates.DamageSourcePredicate;
import net.minecraft.advancements.predicates.entity.EntityPredicate;
import net.minecraft.advancements.predicates.TagPredicate;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.*;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.core.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.ConditionalEffect;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.AddValue;
import net.minecraft.world.item.enchantment.effects.EnchantmentAttributeEffect;
import net.minecraft.world.item.enchantment.effects.EnchantmentValueEffect;
import net.minecraft.world.item.enchantment.effects.Ignite;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.NestedLootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.DamageSourceCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.*;
import java.util.function.UnaryOperator;


public class Indexed implements ModInitializer {

    public static final String MOD_ID = "indexed";

    public static int getEnchantmentValue(DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> type, ServerLevel world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyItemFilteredCount(type, world, level, stack, mutableFloat));
        return mutableFloat.intValue();
    }

    public static int getEnchantmentValue(DataComponentType<EnchantmentValueEffect> type, Level world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyUnfilteredValue(type, world.getRandom(), level, mutableFloat));
        return mutableFloat.intValue();
    }

    public static double getEnchantmentValueDouble(DataComponentType<EnchantmentValueEffect> type, Level world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.runIterationOnItem(stack, (enchantment, level) -> enchantment.value().modifyUnfilteredValue(type, world.getRandom(), level, mutableFloat));
        return mutableFloat.doubleValue();
    }

    private static void setMaxLevel(Enchantment.Builder builder, int maxLevel) {
        Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor) builder).getDefinition();
        ((EnchantmentMixin) builder).setDefinition(new Enchantment.EnchantmentDefinition(
                definition.supportedItems(),
                definition.primaryItems(),
                definition.weight(),
                maxLevel,
                definition.minCost(),
                definition.maxCost(),
                definition.anvilCost(),
                definition.slots()));
    }

    private static void clearExclusiveEnchantments(Enchantment.Builder builder) {
        builder.exclusiveWith(HolderSet.direct());
    }


    public static DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> REPLENISH_PROJECTILE = registerEnchantment("replenish_projectile", builder ->
            builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC).listOf()));
    public static DataComponentType<List<ConditionalEffect<EnchantmentValueEffect>>> ESSENCE = registerEnchantment("essence", builder ->
            builder.persistent(ConditionalEffect.codec(EnchantmentValueEffect.CODEC).listOf()));
    public static DataComponentType<EnchantmentValueEffect> REDUCE_REPAIR_COST = registerEnchantment("reduce_repair_cost", builder -> builder.persistent(EnchantmentValueEffect.CODEC));

    //Blocks
    public static final Block CRYSTAL_GLOBE = new CrystalGlobeBlock(BlockBehaviour.Properties.of().
            mapColor(DyeColor.MAGENTA).
            forceSolidOn().
            forceSolidOn().
            strength(1.5f).
            destroyTime(1.5f).
            lightLevel(CrystalGlobeBlock.STATE_TO_LUMINANCE).
            sound(SoundType.AMETHYST).
            noOcclusion()
            .setId(ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath("indexed", "crystal_globe")))
    );

    //Sounds
    public static final Identifier CRYSTAL_USE_SOUND = Identifier.fromNamespaceAndPath("indexed","use_crystal_globe");
    public static SoundEvent CRYSTAL_USE_SOUND_EVENT = SoundEvent.createVariableRangeEvent(CRYSTAL_USE_SOUND);
    public static final Identifier CRYSTAL_HARVEST_SOUND = Identifier.fromNamespaceAndPath("indexed","harvest_crystal_globe");
    public static SoundEvent CRYSTAL_HARVEST_SOUND_EVENT = SoundEvent.createVariableRangeEvent(CRYSTAL_HARVEST_SOUND);
    public static final Identifier CRYSTAL_AMBIENT_SOUND = Identifier.fromNamespaceAndPath("indexed","crystal_globe_ambient");
    public static SoundEvent CRYSTAL_AMBIENT_SOUND_EVENT = SoundEvent.createVariableRangeEvent(CRYSTAL_AMBIENT_SOUND);

    //Status Effects
    public static final Holder<MobEffect> ENCHANTED_STATUS_EFFECT = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(MOD_ID, "enchanted"), new EnchantedStatusEffect(MobEffectCategory.BENEFICIAL, 0xD400FF));

    //Loot Tables
    public static ResourceKey<LootTable> INDEXED_LOOT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_items"));
    public static ResourceKey<LootTable> INDEXED_NETHER_BRIDGE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_nether_bridge"));
    public static ResourceKey<LootTable> INDEXED_OUTPOST = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_outpost"));
    public static ResourceKey<LootTable> INDEXED_MANSION = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_mansion"));
    public static ResourceKey<LootTable> INDEXED_MINESHAFT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_mineshaft"));
    public static ResourceKey<LootTable> INDEXED_SHIPWRECK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_shipwreck"));
    public static ResourceKey<LootTable> INDEXED_TEMPLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_temple"));
    public static ResourceKey<LootTable> INDEXED_BURIED_TREASURE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_buried_treasure"));
    public static ResourceKey<LootTable> INDEXED_WATER_RUIN = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_water_ruin"));
    public static ResourceKey<LootTable> INDEXED_DUNGEON = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_dungeon"));
    public static ResourceKey<LootTable> INDEXED_IGLOO = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_igloo"));

    //Criterion
    public static OverchargeItemCriterion OVERCHARGE_ITEM = CriteriaTriggersAccessor.indexed$register("overcharge_item", new OverchargeItemCriterion());
    public static EnchantGoldBookCriterion ENCHANT_GOLD_BOOK = CriteriaTriggersAccessor.indexed$register("enchant_gold_book", new EnchantGoldBookCriterion());
    public static final RepairItemCriterion REPAIR_ITEM = CriteriaTriggersAccessor.indexed$register("repair_item", new RepairItemCriterion());
    public static MultishotCrossbowCriterion MULTISHOT_CROSSBOW = CriteriaTriggersAccessor.indexed$register("multishot_crossbow", new MultishotCrossbowCriterion());
    public static MaxGoldCriterion MAX_GOLD = CriteriaTriggersAccessor.indexed$register("max_gold", new MaxGoldCriterion());
    public static MaxKnockbackCriterion MAX_KNOCKBACK = CriteriaTriggersAccessor.indexed$register("max_knockback", new MaxKnockbackCriterion());
    public static final UseCrystalGlobeCriterion USE_CRYSTAL_GLOBE = CriteriaTriggersAccessor.indexed$register("use_crystal_globe", new UseCrystalGlobeCriterion());
    public static EnchantedCriterion ENCHANTED_ADVANCEMENT = CriteriaTriggersAccessor.indexed$register("enchanted_advancement", new EnchantedCriterion());



    @Override
    public void onInitialize() {

        IndexedItems.registerItems();
        var lookup = BuiltInRegistries.acquireBootstrapRegistrationLookup(BuiltInRegistries.ENTITY_TYPE);

        //Blocks
        Registry.register(BuiltInRegistries.BLOCK, ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(MOD_ID, "crystal_globe")), CRYSTAL_GLOBE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(MOD_ID, "crystal_globe"), new BlockItem(CRYSTAL_GLOBE, new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("indexed", "crystal_globe")))));

        //Sounds
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_USE_SOUND, CRYSTAL_USE_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_HARVEST_SOUND, CRYSTAL_HARVEST_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_AMBIENT_SOUND, CRYSTAL_AMBIENT_SOUND_EVENT);


        //Particles
        IndexedParticles.init();


        //Registers Config
        Identifier identifier = Identifier.parse(MOD_ID);
        ServerLifecycleEvents.SERVER_STARTING.register(identifier, callbacks -> {
            System.out.println("INDEXED: Server starting. Loading config.");
            initializeConfig();
        });

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(identifier, (server, serverResourceManager) -> {
            System.out.println("INDEXED: Server data pack reload. Loading config.");
            initializeConfig();
        });


        // Send Config to Players
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayer player = handler.player;

            System.out.println("INDEXED: Server player join. Sending config to player.");

            ConfigS2CPayload activePayload = new ConfigS2CPayload(Config.getConfigList());
            ServerPlayNetworking.send(player, activePayload);

        });

        PayloadTypeRegistry.clientboundPlay().register(ConfigS2CPayload.ID, ConfigS2CPayload.CODEC);

        //Ores Drop Experience
        SetOreExperience.set(Blocks.COPPER_ORE, UniformInt.of(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_COPPER_ORE, UniformInt.of(1,3));
        SetOreExperience.set(Blocks.IRON_ORE, UniformInt.of(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_IRON_ORE, UniformInt.of(1,3));
        SetOreExperience.set(Blocks.GOLD_ORE, UniformInt.of(2,4));
        SetOreExperience.set(Blocks.DEEPSLATE_GOLD_ORE, UniformInt.of(2,4));



        //Add Items to Chests
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
                        .add(NestedLootTable.lootTableReference(INDEXED_LOOT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.NETHER_BRIDGE.equals(key) || BuiltInLootTables.BASTION_OTHER.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_NETHER_BRIDGE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.PILLAGER_OUTPOST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_OUTPOST));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.IGLOO_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_IGLOO));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.WOODLAND_MANSION.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_MANSION));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.ABANDONED_MINESHAFT.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_MINESHAFT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.SHIPWRECK_TREASURE.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_SHIPWRECK));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.BURIED_TREASURE.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_BURIED_TREASURE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.UNDERWATER_RUIN_BIG.equals(key) ||
                    BuiltInLootTables.UNDERWATER_RUIN_SMALL.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_WATER_RUIN));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.JUNGLE_TEMPLE.equals(key) ||
                    BuiltInLootTables.DESERT_PYRAMID.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_TEMPLE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(BuiltInLootTables.SIMPLE_DUNGEON.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.lootPool()
                        .add(NestedLootTable.lootTableReference(INDEXED_DUNGEON));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        //Modify vanilla enchantments
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.AQUA_AFFINITY.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.CHANNELING.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SHARPNESS.equals(key)) {
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.DAMAGE).add(new ConditionalEffect<>(
                            new AddValue(LevelBasedValue.perLevel(1.0F, 0.4F)),
                            Optional.empty()));
                }

                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SMITE.equals(key)) {
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.BANE_OF_ARTHROPODS.equals(key)) {
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.DENSITY.equals(key)) {
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.BREACH.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.WIND_BURST.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.LUNGE.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.LOOTING.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FEATHER_FALLING.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.DEPTH_STRIDER.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.EFFICIENCY.equals(key)) {
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FIRE_ASPECT.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FLAME.equals(key)) {
                setMaxLevel(builder, 5);
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.PROJECTILE_SPAWNED).add(new ConditionalEffect<>(
                            new Ignite(LevelBasedValue.perLevel(160.0F, 40.0F)),
                            Optional.empty()));
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FROST_WALKER.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.IMPALING.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        5,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.INFINITY.equals(key)) {
                setMaxLevel(builder, 5);
                ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(Indexed.REPLENISH_PROJECTILE).add(new ConditionalEffect<>(
                        new AddValue(LevelBasedValue.perLevel(1F, 1F)),
                        Optional.empty()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.KNOCKBACK.equals(key)) {
                setMaxLevel(builder, 5);
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.KNOCKBACK).add(new ConditionalEffect<>(
                            new AddValue(LevelBasedValue.perLevel(0.7F, 0.7F)),
                            Optional.empty()));
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.LOYALTY.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.LUCK_OF_THE_SEA.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.LURE.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.MENDING.equals(key)) {
                setMaxLevel(builder, 5);
                ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.REPAIR_WITH_XP).add(new ConditionalEffect<>(
                        new AddValue(LevelBasedValue.perLevel(2.0F, 2.0F)),
                        Optional.empty()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.MULTISHOT.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        1,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.PIERCING.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        5,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.POWER.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.PROTECTION.equals(key)) {
                setMaxLevel(builder, 5);
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.DAMAGE).add(new ConditionalEffect<>(
                            new AddValue(LevelBasedValue.perLevel(0.5F, 0.5F)),
                            Optional.of(DamageSourceCondition.hasDamageSource(DamageSourcePredicate.Builder.damageType()
                                    .tag(TagPredicate.isNot(DamageTypeTags.BYPASSES_INVULNERABILITY))).build())
                    ));
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FIRE_PROTECTION.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.PROJECTILE_PROTECTION.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.BLAST_PROTECTION.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.PUNCH.equals(key)) {
                setMaxLevel(builder, 5);
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.KNOCKBACK).add(new ConditionalEffect<>(
                            new AddValue(LevelBasedValue.perLevel(0.75F)),
                            Optional.of(LootItemEntityPropertyCondition.hasProperties(
                                    LootContext.EntityTarget.DIRECT_ATTACKER, EntityPredicate.Builder.entity().of(registryLookup.lookup(Registries.ENTITY_TYPE).orElseThrow().getter(), EntityTypeTags.ARROWS).build()
                            ).build())
                    ));
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.QUICK_CHARGE.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentMixin) builder).effectMap().set(
                            EnchantmentEffectComponents.CROSSBOW_CHARGE_TIME, new AddValue(LevelBasedValue.perLevel(-0.2F))
                    );
                    ((EnchantmentMixin) builder).effectMap().set(
                            EnchantmentEffectComponents.CROSSBOW_CHARGING_SOUNDS,
                            List.of(
                                    new CrossbowItem.ChargingSounds(
                                            Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                    ),
                                    new CrossbowItem.ChargingSounds(
                                            Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                    ),
                                    new CrossbowItem.ChargingSounds(
                                            Optional.of(SoundEvents.CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.CROSSBOW_LOADING_END)
                                    )
                            )
                    );
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.RESPIRATION.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.RIPTIDE.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SILK_TOUCH.equals(key)) {
                Enchantment.EnchantmentDefinition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.EnchantmentDefinition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        1,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveWith(((EnchantmentBuilderAccessor) builder).getExclusiveSet());
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.FORTUNE.equals(key)) {
                setMaxLevel(builder, 5);
                builder.exclusiveWith(((EnchantmentBuilderAccessor) builder).getExclusiveSet());
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SOUL_SPEED.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SWEEPING_EDGE.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.SWIFT_SNEAK.equals(key)) {
                setMaxLevel(builder, 5);
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(DataComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponents.ATTRIBUTES).add(new EnchantmentAttributeEffect(
                                    Identifier.withDefaultNamespace("enchantment.swift_sneak"),
                                    Attributes.SNEAKING_SPEED,
                                    LevelBasedValue.perLevel(0.10F),
                                    AttributeModifier.Operation.ADD_VALUE
                            ));
                }
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.THORNS.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });
        EnchantmentEvents.MODIFY_WITH_LOOKUP.register((key, builder, source, registryLookup) -> {
            if (Enchantments.UNBREAKING.equals(key)) {
                setMaxLevel(builder, 5);
                clearExclusiveEnchantments(builder);
            }
        });

        // Make other tools enchantable
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.ELYTRA,
                    builder -> builder.set(
                            DataComponents.ENCHANTABLE,
                            new Enchantable(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.FLINT_AND_STEEL,
                    builder -> builder.set(
                            DataComponents.ENCHANTABLE,
                            new Enchantable(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.SHEARS,
                    builder -> builder.set(
                            DataComponents.ENCHANTABLE,
                            new Enchantable(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.SHIELD,
                    builder -> builder.set(
                            DataComponents.ENCHANTABLE,
                            new Enchantable(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.BRUSH,
                    builder -> builder.set(
                            DataComponents.ENCHANTABLE,
                            new Enchantable(1)
                    )
            );
        });





    }

    public static void initializeConfig() {
        ConfigMain.load(false);
        boolean modOutOfDate = ConfigMain.isOutOfDate();
        Config.loadConfig(modOutOfDate);
        ConfigMain.load(modOutOfDate);
    }

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    private static <T> DataComponentType<T> registerEnchantment(String id, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Indexed.id(id), (builderOperator.apply(DataComponentType.builder())).build());
    }
}
