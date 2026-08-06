package com.astrazoey.indexed;

import com.astrazoey.indexed.blocks.CrystalGlobeBlock;
import com.astrazoey.indexed.criterion.*;
import com.astrazoey.indexed.mixin.CriteriaTriggersAccessor;
import com.astrazoey.indexed.registry.IndexedItems;
import com.astrazoey.indexed.registry.IndexedParticles;
import com.astrazoey.indexed.status_effects.EnchantedStatusEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootTable;
import org.jetbrains.annotations.NotNull;

/**
 * Common initialization logic shared across all loaders.
 * Registers all game objects and holds the loaded config.
 */
public class CommonClass {

    private static IndexedConfig config;
    private static boolean initialized = false;

    /**
     * Runs the loader-agnostic initialization.
     * Each loader calls this from its own entrypoint.
     * Idempotent: NeoForge's RegisterEvent fires once per registry,
     * but registration must only happen once.
     */
    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;

        loadConfig();

        IndexedItems.registerItems();

        //Blocks
        Registry.register(BuiltInRegistries.BLOCK, ResourceKey.create(Registries.BLOCK, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "crystal_globe")), CRYSTAL_GLOBE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "crystal_globe"), new BlockItem(CRYSTAL_GLOBE, new Item.Properties()
                .useBlockDescriptionPrefix()
                .setId(ResourceKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("indexed", "crystal_globe")))));

        //Sounds
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_USE_SOUND, CRYSTAL_USE_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_HARVEST_SOUND, CRYSTAL_HARVEST_SOUND_EVENT);
        Registry.register(BuiltInRegistries.SOUND_EVENT, CRYSTAL_AMBIENT_SOUND, CRYSTAL_AMBIENT_SOUND_EVENT);

        //Particles
        IndexedParticles.init();
    }

    /**
     * Loads (or reloads) the config and applies it to items.
     * Contains no registry writes, so it is safe to call after
     * registries are frozen (e.g. client started / server reload events).
     */
    public static void loadConfig() {
        Constants.LOG.info("Loading Indexed configuration...");
        config = IndexedConfig.load();
        config.registerConfigListItems();
    }

    public static IndexedConfig getConfig() {
        return config;
    }

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
    public static final Holder<@NotNull MobEffect> ENCHANTED_STATUS_EFFECT = Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, Identifier.fromNamespaceAndPath(Constants.MOD_ID, "enchanted"), new EnchantedStatusEffect(MobEffectCategory.BENEFICIAL, 0xD400FF));

    //Loot Tables
    public static ResourceKey<@NotNull LootTable> INDEXED_LOOT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_items"));
    public static ResourceKey<@NotNull LootTable> INDEXED_NETHER_BRIDGE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_nether_bridge"));
    public static ResourceKey<@NotNull LootTable> INDEXED_OUTPOST = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_outpost"));
    public static ResourceKey<@NotNull LootTable> INDEXED_MANSION = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_mansion"));
    public static ResourceKey<@NotNull LootTable> INDEXED_MINESHAFT = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_mineshaft"));
    public static ResourceKey<@NotNull LootTable> INDEXED_SHIPWRECK = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_shipwreck"));
    public static ResourceKey<@NotNull LootTable> INDEXED_TEMPLE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_temple"));
    public static ResourceKey<@NotNull LootTable> INDEXED_BURIED_TREASURE = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_buried_treasure"));
    public static ResourceKey<@NotNull LootTable> INDEXED_WATER_RUIN = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_water_ruin"));
    public static ResourceKey<@NotNull LootTable> INDEXED_DUNGEON = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_dungeon"));
    public static ResourceKey<@NotNull LootTable> INDEXED_IGLOO = ResourceKey.create(Registries.LOOT_TABLE, Identifier.fromNamespaceAndPath("indexed", "indexed_igloo"));

    //Criterion
    public static OverchargeItemCriterion OVERCHARGE_ITEM = CriteriaTriggersAccessor.indexed$register("overcharge_item", new OverchargeItemCriterion());
    public static EnchantGoldBookCriterion ENCHANT_GOLD_BOOK = CriteriaTriggersAccessor.indexed$register("enchant_gold_book", new EnchantGoldBookCriterion());
    public static final RepairItemCriterion REPAIR_ITEM = CriteriaTriggersAccessor.indexed$register("repair_item", new RepairItemCriterion());
    public static final UseCrystalGlobeCriterion USE_CRYSTAL_GLOBE = CriteriaTriggersAccessor.indexed$register("use_crystal_globe", new UseCrystalGlobeCriterion());
    public static EnchantedCriterion ENCHANTED_ADVANCEMENT = CriteriaTriggersAccessor.indexed$register("enchanted_advancement", new EnchantedCriterion());

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(Constants.MOD_ID, path);
    }
}
