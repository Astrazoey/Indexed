
package com.astrazoey.indexed;

import com.astrazoey.indexed.blocks.CrystalGlobeBlock;
import com.astrazoey.indexed.criterion.*;
//import com.astrazoey.indexed.mixins.CriterionRegistryAccessor;
import com.astrazoey.indexed.mixins.EnchantmentMixin;
import com.astrazoey.indexed.registry.IndexedItems;
import com.astrazoey.indexed.registry.IndexedParticles;
import com.astrazoey.indexed.status_effects.EnchantedStatusEffect;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.item.v1.DefaultItemComponentEvents;
import net.fabricmc.fabric.api.item.v1.EnchantmentEvents;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
//import net.fabricmc.fabric.api.object.builder.v1.advancement.CriterionRegistry;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.impl.tag.convention.v2.TagRegistration;
import net.fabricmc.fabric.mixin.item.EnchantmentBuilderAccessor;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.EnchantableComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.enchantment.effect.AttributeEnchantmentEffect;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.enchantment.effect.entity.IgniteEnchantmentEffect;
import net.minecraft.enchantment.effect.value.AddEnchantmentEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.item.*;
import net.minecraft.loot.*;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.entry.LootTableEntry;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.EntityTypeTags;
import net.minecraft.registry.tag.TagGroupLoader;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.registry.Registry;
import net.minecraft.world.World;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableFloat;

import java.util.*;
import java.util.function.UnaryOperator;


public class Indexed implements ModInitializer {

    public static final Identifier CONFIG_PACKET = Identifier.of("indexed", "config");

    public static final String MOD_ID = "indexed";

    public static int getEnchantmentValue(ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> type, ServerWorld world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyValue(type, world, level, stack, mutableFloat));
        return mutableFloat.intValue();
    }

    public static int getEnchantmentValue(ComponentType<EnchantmentValueEffect> type, World world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyValue(type, world.random, level, mutableFloat));
        return mutableFloat.intValue();
    }

    public static double getEnchantmentValueDouble(ComponentType<EnchantmentValueEffect> type, World world, ItemStack stack) {
        MutableFloat mutableFloat = new MutableFloat(0);
        EnchantmentHelper.forEachEnchantment(stack, (enchantment, level) -> enchantment.value().modifyValue(type, world.random, level, mutableFloat));
        return mutableFloat.doubleValue();
    }


    public static final RegistryKey<Enchantment> SLOW_BURN = RegistryKey.of(RegistryKeys.ENCHANTMENT, id("slow_burn"));
    public static final RegistryKey<Enchantment> QUICK_FLIGHT = RegistryKey.of(RegistryKeys.ENCHANTMENT, id("quick_flight"));
    public static ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> DEBUFF_REDUCTION = registerEnchantment("debuff_reduction", builder ->
            builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_DAMAGE).listOf()));
    public static ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> REPLENISH_PROJECTILE = registerEnchantment("replenish_projectile", builder ->
            builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf()));
    public static ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> KEEP_ITEMS = registerEnchantment("keep_items", builder ->
            builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf()));
    public static ComponentType<List<EnchantmentEffectEntry<EnchantmentValueEffect>>> ESSENCE = registerEnchantment("essence", builder ->
            builder.codec(EnchantmentEffectEntry.createCodec(EnchantmentValueEffect.CODEC, LootContextTypes.ENCHANTED_ITEM).listOf()));
    public static ComponentType<EnchantmentValueEffect> REDUCE_REPAIR_COST = registerEnchantment("reduce_repair_cost", builder -> builder.codec(EnchantmentValueEffect.CODEC));
    public static ComponentType<Unit> HIDE_ARMOR = registerEnchantment("hide_armor", builder -> builder.codec(Unit.CODEC));
    public static ComponentType<Unit> HIDE_ENCHANTMENTS = registerEnchantment("hide_enchantments", builder -> builder.codec(Unit.CODEC));
    public static ComponentType<Unit> HIDE_DURABILITY = registerEnchantment("hide_durability", builder -> builder.codec(Unit.CODEC));

    //Blocks
    public static final Block CRYSTAL_GLOBE = new CrystalGlobeBlock(Block.Settings.create().
            mapColor(DyeColor.MAGENTA).
            solid().
            solid().
            strength(1.5f).
            hardness(1.5f).
            luminance(CrystalGlobeBlock.STATE_TO_LUMINANCE).
            sounds(BlockSoundGroup.AMETHYST_BLOCK).
            nonOpaque()
            .registryKey(RegistryKey.of(RegistryKeys.BLOCK, Identifier.of("indexed", "crystal_globe")))
    );

    //Sounds
    public static final Identifier CRYSTAL_USE_SOUND = Identifier.of("indexed","use_crystal_globe");
    public static SoundEvent CRYSTAL_USE_SOUND_EVENT = SoundEvent.of(CRYSTAL_USE_SOUND);
    public static final Identifier CRYSTAL_HARVEST_SOUND = Identifier.of("indexed","harvest_crystal_globe");
    public static SoundEvent CRYSTAL_HARVEST_SOUND_EVENT = SoundEvent.of(CRYSTAL_HARVEST_SOUND);
    public static final Identifier CRYSTAL_AMBIENT_SOUND = Identifier.of("indexed","crystal_globe_ambient");
    public static SoundEvent CRYSTAL_AMBIENT_SOUND_EVENT = SoundEvent.of(CRYSTAL_AMBIENT_SOUND);

    //Status Effects
    public static final RegistryEntry<StatusEffect> ENCHANTED_STATUS_EFFECT = Registry.registerReference(Registries.STATUS_EFFECT, Identifier.of(MOD_ID, "enchanted"), new EnchantedStatusEffect(StatusEffectCategory.BENEFICIAL, 0xD400FF));

    //Loot Tables
    public static RegistryKey<LootTable> INDEXED_LOOT = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_items"));
    public static RegistryKey<LootTable> INDEXED_NETHER_BRIDGE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_nether_bridge"));
    public static RegistryKey<LootTable> INDEXED_OUTPOST = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_outpost"));
    public static RegistryKey<LootTable> INDEXED_MANSION = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_mansion"));
    public static RegistryKey<LootTable> INDEXED_MINESHAFT = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_mineshaft"));
    public static RegistryKey<LootTable> INDEXED_SHIPWRECK = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_shipwreck"));
    public static RegistryKey<LootTable> INDEXED_TEMPLE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_temple"));
    public static RegistryKey<LootTable> INDEXED_BURIED_TREASURE = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_buried_treasure"));
    public static RegistryKey<LootTable> INDEXED_WATER_RUIN = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_water_ruin"));
    public static RegistryKey<LootTable> INDEXED_DUNGEON = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_dungeon"));
    public static RegistryKey<LootTable> INDEXED_IGLOO = RegistryKey.of(RegistryKeys.LOOT_TABLE, Identifier.of("indexed", "indexed_igloo"));

    //Criterion
    public static OverchargeItemCriterion OVERCHARGE_ITEM = Criteria.register("overcharge_item", new OverchargeItemCriterion());
    public static EnchantGoldBookCriterion ENCHANT_GOLD_BOOK = Criteria.register("enchant_gold_book", new EnchantGoldBookCriterion());
    public static final RepairItemCriterion REPAIR_ITEM = Criteria.register("repair_item", new RepairItemCriterion());
    public static GrindEssenceCriterion GRIND_ESSENCE = Criteria.register("grind_essence", new GrindEssenceCriterion());
    public static MultishotCrossbowCriterion MULTISHOT_CROSSBOW = Criteria.register("multishot_crossbow", new MultishotCrossbowCriterion());
    public static MaxGoldCriterion MAX_GOLD = Criteria.register("max_gold", new MaxGoldCriterion());
    public static MaxKnockbackCriterion MAX_KNOCKBACK = Criteria.register("max_knockback", new MaxKnockbackCriterion());
    public static final UseCrystalGlobeCriterion USE_CRYSTAL_GLOBE = Criteria.register("use_crystal_globe", new UseCrystalGlobeCriterion());
    //public static final FillCrystalGlobeCriterion FILL_CRYSTAL_GLOBE = Criteria.register("fill_crystal_globe", new FillCrystalGlobeCriterion());
    public static EnchantedCriterion ENCHANTED_ADVANCEMENT = Criteria.register("enchanted_advancement", new EnchantedCriterion());



    @Override
    public void onInitialize() {

        IndexedItems.registerItems();
        var lookup = Registries.createEntryLookup(Registries.ENTITY_TYPE);

        //Blocks
        Registry.register(Registries.BLOCK, RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(MOD_ID, "crystal_globe")), CRYSTAL_GLOBE);
        Registry.register(Registries.ITEM, Identifier.of(MOD_ID, "crystal_globe"), new BlockItem(CRYSTAL_GLOBE, new Item.Settings()
                .useBlockPrefixedTranslationKey()
                .registryKey(RegistryKey.of(RegistryKeys.ITEM, Identifier.of("indexed", "crystal_globe")))));

        //Sounds
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_USE_SOUND, CRYSTAL_USE_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_HARVEST_SOUND, CRYSTAL_HARVEST_SOUND_EVENT);
        Registry.register(Registries.SOUND_EVENT, CRYSTAL_AMBIENT_SOUND, CRYSTAL_AMBIENT_SOUND_EVENT);

        //Particles
        IndexedParticles.init();

        //Ores Drop Experience
        SetOreExperience.set(Blocks.COPPER_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_COPPER_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.IRON_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.DEEPSLATE_IRON_ORE, UniformIntProvider.create(1,3));
        SetOreExperience.set(Blocks.GOLD_ORE, UniformIntProvider.create(2,4));
        SetOreExperience.set(Blocks.DEEPSLATE_GOLD_ORE, UniformIntProvider.create(2,4));


        //Add Items to Chests
        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.END_CITY_TREASURE_CHEST.equals(key) ||
                    LootTables.ABANDONED_MINESHAFT_CHEST.equals(key) ||
                    LootTables.STRONGHOLD_LIBRARY_CHEST.equals(key) ||
                    LootTables.BASTION_TREASURE_CHEST.equals(key) ||
                    LootTables.WOODLAND_MANSION_CHEST.equals(key) ||
                    LootTables.NETHER_BRIDGE_CHEST.equals(key) ||
                    LootTables.PILLAGER_OUTPOST_CHEST.equals(key) ||
                    LootTables.SIMPLE_DUNGEON_CHEST.equals(key) ||
                    LootTables.RUINED_PORTAL_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_LOOT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.NETHER_BRIDGE_CHEST.equals(key) || LootTables.BASTION_OTHER_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_NETHER_BRIDGE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.PILLAGER_OUTPOST_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_OUTPOST));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.IGLOO_CHEST_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_IGLOO));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.WOODLAND_MANSION_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_MANSION));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.ABANDONED_MINESHAFT_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_MINESHAFT));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.SHIPWRECK_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_SHIPWRECK));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.BURIED_TREASURE_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_BURIED_TREASURE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.UNDERWATER_RUIN_BIG_CHEST.equals(key) ||
                    LootTables.UNDERWATER_RUIN_SMALL_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_WATER_RUIN));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.JUNGLE_TEMPLE_CHEST.equals(key) ||
                    LootTables.DESERT_PYRAMID_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_TEMPLE));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        LootTableEvents.MODIFY.register((key, tableBuilder, source, registries) -> {
            if(LootTables.SIMPLE_DUNGEON_CHEST.equals(key)) {
                LootPool.Builder poolBuilder = LootPool.builder()
                        .with(LootTableEntry.builder(INDEXED_DUNGEON));
                tableBuilder.pool(poolBuilder.build());
            }
        });

        //Modify vanilla enchantments
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.AQUA_AFFINITY.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor) builder).getDefinition();
                ((EnchantmentMixin) builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.CHANNELING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SHARPNESS.equals(key)) {
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.DAMAGE).add(new EnchantmentEffectEntry<>(
                            new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1.0F, 0.4F)),
                            Optional.empty()));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SMITE.equals(key)) {
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.BANE_OF_ARTHROPODS.equals(key)) {
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.DENSITY.equals(key)) {
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.BREACH.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.WIND_BURST.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.LOOTING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FEATHER_FALLING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.DEPTH_STRIDER.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.EFFICIENCY.equals(key)) {
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FIRE_ASPECT.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FLAME.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.PROJECTILE_SPAWNED).add(new EnchantmentEffectEntry<>(
                            new IgniteEnchantmentEffect(EnchantmentLevelBasedValue.linear(160.0F, 40.0F)),
                            Optional.empty()));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FROST_WALKER.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.IMPALING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        5,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.INFINITY.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(Indexed.REPLENISH_PROJECTILE).add(new EnchantmentEffectEntry<>(
                        new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(1F, 1F)),
                        Optional.empty()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.KNOCKBACK.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.KNOCKBACK).add(new EnchantmentEffectEntry<>(
                            new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.7F, 0.7F)),
                            Optional.empty()));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.LOYALTY.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.LUCK_OF_THE_SEA.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.LURE.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.MENDING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.REPAIR_WITH_XP).add(new EnchantmentEffectEntry<>(
                        new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(2.0F, 2.0F)),
                        Optional.empty()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.MULTISHOT.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        1,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.PIERCING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        5,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.POWER.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.PROTECTION.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.DAMAGE).add(new EnchantmentEffectEntry<>(
                            new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.5F, 0.5F)),
                            Optional.of(DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create()
                                    .tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY))).build())
                    ));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FIRE_PROTECTION.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.PROJECTILE_PROTECTION.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.BLAST_PROTECTION.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.PUNCH.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.KNOCKBACK).add(new EnchantmentEffectEntry<>(
                            new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(0.75F)),
                            Optional.of(EntityPropertiesLootCondition.builder(
                                    LootContext.EntityReference.DIRECT_ATTACKER, EntityPredicate.Builder.create().type(lookup, EntityTypeTags.ARROWS).build()
                            ).build())
                    ));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.QUICK_CHARGE.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentMixin) builder).effectMap().add(
                            EnchantmentEffectComponentTypes.CROSSBOW_CHARGE_TIME, new AddEnchantmentEffect(EnchantmentLevelBasedValue.linear(-0.2F))
                    );
                    ((EnchantmentMixin) builder).effectMap().add(
                            EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS,
                            List.of(
                                    new CrossbowItem.LoadingSounds(
                                            Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_1), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                    ),
                                    new CrossbowItem.LoadingSounds(
                                            Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_2), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                    ),
                                    new CrossbowItem.LoadingSounds(
                                            Optional.of(SoundEvents.ITEM_CROSSBOW_QUICK_CHARGE_3), Optional.empty(), Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END)
                                    )
                            )
                    );
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.RESPIRATION.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.RIPTIDE.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SILK_TOUCH.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        2,
                        1,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(((EnchantmentBuilderAccessor) builder).getExclusiveSet());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.FORTUNE.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(((EnchantmentBuilderAccessor) builder).getExclusiveSet());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SOUL_SPEED.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SWEEPING_EDGE.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.SWIFT_SNEAK.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                if (ConfigMain.enableEnchantmentNerfs) {
                    ((EnchantmentMixin) builder).setEffectMap(ComponentMap.builder());
                    ((EnchantmentMixin) builder).setEffectLists(new HashMap());
                    ((EnchantmentBuilderAccessor) builder).invokeGetEffectsList(EnchantmentEffectComponentTypes.ATTRIBUTES).add(new AttributeEnchantmentEffect(
                                    Identifier.ofVanilla("enchantment.swift_sneak"),
                                    EntityAttributes.SNEAKING_SPEED,
                                    EnchantmentLevelBasedValue.linear(0.10F),
                                    EntityAttributeModifier.Operation.ADD_VALUE
                            ));
                }
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.THORNS.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });
        EnchantmentEvents.MODIFY.register((key, builder, source) -> {
            if (Enchantments.UNBREAKING.equals(key)) {
                Enchantment.Definition definition = ((EnchantmentBuilderAccessor)builder).getDefinition();
                ((EnchantmentMixin)builder).setDefinition(new Enchantment.Definition(
                        definition.supportedItems(),
                        definition.primaryItems(),
                        definition.weight(),
                        5,
                        definition.minCost(),
                        definition.maxCost(),
                        definition.anvilCost(),
                        definition.slots()));
                builder.exclusiveSet(RegistryEntryList.of());
            }
        });

        // Make other tools enchantable
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.ELYTRA,
                    builder -> builder.add(
                            DataComponentTypes.ENCHANTABLE,
                            new EnchantableComponent(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.FLINT_AND_STEEL,
                    builder -> builder.add(
                            DataComponentTypes.ENCHANTABLE,
                            new EnchantableComponent(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.SHEARS,
                    builder -> builder.add(
                            DataComponentTypes.ENCHANTABLE,
                            new EnchantableComponent(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.SHIELD,
                    builder -> builder.add(
                            DataComponentTypes.ENCHANTABLE,
                            new EnchantableComponent(1)
                    )
            );
        });
        DefaultItemComponentEvents.MODIFY.register((context) -> {
            context.modify(
                    Items.BRUSH,
                    builder -> builder.add(
                            DataComponentTypes.ENCHANTABLE,
                            new EnchantableComponent(1)
                    )
            );
        });


        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addAfter(Items.DIAMOND ,IndexedItems.VITALIS));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> entries.addAfter(Items.BOOK,IndexedItems.GOLD_BOUND_BOOK));
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.FUNCTIONAL).register(entries -> entries.addAfter(Items.ENCHANTING_TABLE, Indexed.CRYSTAL_GLOBE));

        //Registers Config
        Identifier identifier = Identifier.of(MOD_ID);
        ServerLifecycleEvents.SERVER_STARTING.register(identifier, callbacks -> {
            System.out.println("INDEXED: Starting starting. Loading config.");
            initializeConfig();
        });

        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register(identifier, (server, serverResourceManager) -> {
            System.out.println("INDEXED: Server data pack reload. Loading config.");
            initializeConfig();
        });

    }

    public static void initializeConfig() {
        ConfigMain.load(false);
        boolean modOutOfDate = ConfigMain.isOutOfDate();
        Config.loadConfig(modOutOfDate);
        ConfigMain.load(modOutOfDate);
    }

    public static Identifier id(String path) {
        return Identifier.of(MOD_ID, path);
    }

    private static <T> ComponentType<T> registerEnchantment(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, Indexed.id(id), (builderOperator.apply(ComponentType.builder())).build());
    }
}
